<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<table cellpadding="0" cellspacing="0" class="queryHistoryWidget">
	  <tr class="queryHistoryWidgetHeader">
	  	<td NOWRAP>Query Date</td>
	  	<td NOWRAP>Status</td>
	  	<td NOWRAP>Duration</td>
	  	<td NOWRAP>Name</td>
	  	<td NOWRAP>Date Parameters</td>
	  	<td NOWRAP>Geog</td>
	  	<td NOWRAP>Volumes</td>
	  	<td NOWRAP>Interest Point</td>
	  </tr>
	<logic:iterate id="queries" name="EvqActionForm" property="queryHistory">
	  <tr>
		<td NOWRAP><html:link styleClass="link" paramId="selectedQueryId" paramName="queries"
			paramProperty="queryId" action="/evq.do?method=Get%20Query">
			<bean:write name="queries" property="queryDateStr" />
		</html:link></td>
		<td NOWRAP>
			<logic:equal name="queries" property="queryStatus" value="C" >
			<html:link styleClass="link" paramId="queryId" paramName="queries" paramProperty="queryId" href="/evq/evq.download">Complete</html:link>
			</logic:equal>
			<logic:equal name="queries" property="queryStatus" value="F" >Failed</logic:equal>
			<logic:equal name="queries" property="queryStatus" value="E" >Executing</logic:equal>
			<logic:equal name="queries" property="queryStatus" value="R" >Removed</logic:equal>
		</td>
		<td NOWRAP><bean:write name="queries" property="runtimeStr" /></td>
		<td NOWRAP><bean:write name="queries" property="nameStr" /></td>
		<td NOWRAP><bean:write name="queries" property="dateParametersStr" /></td>
		<td NOWRAP><bean:write name="queries" property="geogParametersStr" /></td>
		<td NOWRAP><bean:write name="queries" property="volumeParametersStr" /></td>
		<td NOWRAP><bean:write name="queries" property="interestPointStr" /></td>
	  </tr>
	</logic:iterate>
</table>
