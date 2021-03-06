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

package org.openmrs.module.clinicalsummary.util.response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReminderResponse extends Response {

	private static final Log log = LogFactory.getLog(ReminderResponse.class);

	private String token;

	private Integer response;

	private String comment;

	/**
	 * @return
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token
	 */
	public void setToken(final String token) {
		this.token = token;
	}

	/**
	 * @return
	 */
	public Integer getResponse() {
		return response;
	}

	/**
	 * @param response
	 */
	public void setResponse(final Integer response) {
		this.response = response;
	}

	/**
	 * @return
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 */
	public void setComment(final String comment) {
		this.comment = comment;
	}

}
