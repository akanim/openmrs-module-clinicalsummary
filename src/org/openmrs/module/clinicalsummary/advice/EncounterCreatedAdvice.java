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
package org.openmrs.module.clinicalsummary.advice;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.aop.AfterReturningAdvice;

/**
 * 
 */
public class EncounterCreatedAdvice implements AfterReturningAdvice {
	
	private static final Log log = LogFactory.getLog(EncounterCreatedAdvice.class);
	
	private static final Location MTRH_MODULE_4 = Context.getLocationService().getLocation("MTRH Module 4");
	
	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
		if (method.getName().equals("saveEncounter")) {
			Encounter encounter = (Encounter) args[0];
			
			if (log.isDebugEnabled())
				log.debug("Interception save encounter for type: " + encounter.getEncounterType().getName() + " ...");
			
			if (OpenmrsUtil.nullSafeEquals(encounter.getLocation(), MTRH_MODULE_4)) {
				// randomize the patient
			}
		}
	}
	
}
