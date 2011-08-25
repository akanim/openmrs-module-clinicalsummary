<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage Summaries" otherwise="/login.htm" redirect="/module/clinicalsummary/response/responseList.list"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="../localHeader.jsp" %>

<openmrs:htmlInclude file="/scripts/calendar/calendar.js"/>
<openmrs:htmlInclude file="/moduleResources/clinicalsummary/css/form.css"/>


<script>
	$j = jQuery.noConflict();

	$j(function() {

		$j("#search").click(function() {
			$j("#searchcontainer").show();
			$j("#searchcontainer tr").remove();
			var data = $j("#form").serialize();
			$j.ajax({
				url: "responseSearch.form",
				type: "POST",
				dataType: 'json',
				data: data,
				success: function(server) {
					jQuery.each(server, function(key, responses) {
						var header = null;
						jQuery.each(responses, function() {
							if (header == null) {
							    header = "<tr><td colspan='3'><span style='font-weight: bold'>Patient Name: " + this.patientName + " </span></td></tr>";
								$j("#result").append(header);
							}

							var data = "<tr><td>&nbsp;</td>";
							if (this.status == 1)
								data += "<td>Please remove " + this.medicationName + " from encounter on " + this.medicationDatetime + "</td>";
							else if (this.status == 0)
								data += "<td>Please add " + this.medicationName + " to encounter on " + this.medicationDatetime + "</td>";
							data += "<td> <a href='responseAccept.form?id=" + this.id + "'>Accept</a> | <a href='responseIgnore.form?id=" + this.id + "'>Ignore</a> </td></tr>";
							$j("#result").append(data);
						});
					});
				}
			});
		});

	});
</script>

<style type="text/css">

	td, th {
		color: #333333;
		padding: 5px;
	}

	td {
		font-weight: normal;
	}

</style>

<div id="container">
	<h3 id="header"><spring:message code="clinicalsummary.response.header"/></h3>

	<div id="main">
		<div id="leftcontent">
			<form method="post" id="form" action="">
				<fieldset>
					<ol>
						<li>
							<label for="locationId"><spring:message code="clinicalsummary.response.location"/></label>
							<openmrs:fieldGen type="org.openmrs.Location" formFieldName="locationId" val="" parameters="optionHeader=[blank]"/>
						</li>
						<li>
							<label for="displayType"><spring:message code="clinicalsummary.response.displayType"/></label>
							<select name="displayType" id="displayType">
								<c:forEach var="displayType" items="${displayTypes}" varStatus="varStatus">
									<option value="${displayType}">${displayType.value}</option>
								</c:forEach>
							</select>
						</li>
						<li />
						<li>
							<input type="button" id="search" value="<spring:message code="clinicalsummary.response.search"/>"/>
						</li>
					</ol>
				</fieldset>
			</form>
		</div>
		<div id="maincontent">
			<fieldset id="searchcontainer" style="display: none">
				<ol>
					<li>
						<label for="search"><span style="font-weight: bold; font-size=1em"><spring:message code="clinicalsummary.response.search"/></span></label>
						<table cellpadding="0" cellspacing="0" border="0" class="display">
							<tbody id="result"></tbody>
						</table>
					</li>
				</ol>
			</fieldset>
		</div>
		<div id="clear"></div>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
