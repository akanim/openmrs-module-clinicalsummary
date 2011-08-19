/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.clinicalsummary.task;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.clinicalsummary.Summary;
import org.openmrs.module.clinicalsummary.cache.CacheUtils;
import org.openmrs.module.clinicalsummary.evaluator.Evaluator;
import org.openmrs.module.clinicalsummary.evaluator.velocity.VelocityEvaluator;
import org.openmrs.module.clinicalsummary.rule.ResultCacheInstance;
import org.openmrs.module.clinicalsummary.service.CoreService;
import org.openmrs.module.clinicalsummary.service.IndexService;
import org.openmrs.module.clinicalsummary.service.SummaryService;

/**
 *
 */
public class ReturnDateEvaluatorProcessor {

	private static final Log log = LogFactory.getLog(DateCreatedEvaluatorTask.class);


	public void processSummary() {
		// remove the concept cache every night
		CacheUtils.clearConceptCache();
		CacheUtils.clearEncounterTypeCache();
		// location is clustered, clusters are separated by comma
		String clusterNames = Context.getAdministrationService().getGlobalProperty(TaskParameters.LOCATION_GROUP_LIST);
		if (clusterNames != null) {
			String[] clusterName = StringUtils.split(clusterNames, TaskParameters.CLUSTER_SEPARATOR);
			String processorCounter = Context.getAdministrationService().getGlobalProperty(TaskParameters.PROCESSOR_COUNTER);
			// start with the first cluster (offset = 0) when the counter is not a number
			Integer clusterOffset = NumberUtils.toInt(processorCounter, 0);
			if (clusterOffset >= 0 && clusterOffset < ArrayUtils.getLength(clusterName)) {
				String initProperty = Context.getAdministrationService().getGlobalProperty(TaskParameters.PROCESSOR_INITIALIZED);
				String currentCluster = clusterName[clusterOffset];
				// check whether all cluster have been initialized or not
				Boolean initialized = BooleanUtils.toBoolean(initProperty);

				Cohort cohort;
				String[] locationIds = StringUtils.split(currentCluster);
				for (int i = 0; i < ArrayUtils.getLength(locationIds); i++) {
					log.info("Processing location with id: " + locationIds[i]);
					// default return to -1 because no such location with id -1
					Location location = Context.getLocationService().getLocation(NumberUtils.toInt(locationIds[i], -1));
					if (!initialized) {
						cohort = Context.getService(CoreService.class).getReturnDateCohort(location, null, null);
					} else {
						// regenerate when there's new obs
						Calendar calendar = Calendar.getInstance();
						calendar.add(Calendar.DATE, clusterName.length * 2);
						Date date = calendar.getTime();
						cohort = Context.getService(CoreService.class).getReturnDateCohort(location, new Date(), date);
					}

					evaluate(cohort);
				}
			}
		}
	}

	private void evaluate(final Cohort cohort) {
		IndexService indexService = Context.getService(IndexService.class);
		Evaluator evaluator = new VelocityEvaluator();

		ResultCacheInstance cacheInstance = ResultCacheInstance.getInstance();

		Integer counter = 0;
		for (Integer patientId : cohort.getMemberIds()) {
			Patient patient = Context.getPatientService().getPatient(patientId);
			// for some weird case where we can get the cohort member id but can't get the patient
			if (patient != null) {
				SummaryService summaryService = Context.getService(SummaryService.class);
				for (Summary summary : summaryService.getSummaries(patient)) {
					if (summary.isAutoGenerated()) {
						double start = System.currentTimeMillis();

						evaluator.evaluate(summary, patient, Boolean.TRUE);
						indexService.saveIndex(indexService.generateIndex(patient, summary));

						double elapsed = System.currentTimeMillis() - start;
						log.info("Velocity evaluator running for " + elapsed + "ms (" + (elapsed / 1000) + "s)");
					}
				}

				cacheInstance.clearCache(patient);
				cleanSession(counter);
			}
			counter++;
		}
	}

	private void cleanSession(final Integer counter) {
		if (counter % 20 == 0) {
			Context.flushSession();
			Context.clearSession();
		}
	}
}
