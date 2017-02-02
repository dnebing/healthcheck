<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ include file="/init.jsp" %>

<%
	Map<String, String> statuses = sensorManager.getHealthStatus();
	List<String> names = new ArrayList<>(statuses.keySet());
	Collections.sort(names);
%>
<p>
	<b><liferay-ui:message key="healthcheck.status"/></b>
</p>

<table class="table table-striped">
	<thead><tr>
		<th><liferay-ui:message key="header.sensor" /></th>
		<th><liferay-ui:message key="header.status" /></th>
	</tr></thead>
	<tbody>
	<%
		for (String sensor : names) {
	%>
	<tr><td ><%= sensor %></td>
		<td ><%= statuses.get(sensor) %></td></tr>
	<%
		}
	%>
	</tbody>
</table>
