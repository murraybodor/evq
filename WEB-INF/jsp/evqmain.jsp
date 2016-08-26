<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<table cellpadding="0" cellspacing="0" class="gwt-TabBar">
	<logic:equal name="EvqActionForm" property="showHistory" value="true">
	<tr valign="bottom">
		<td width="8%" NOWRAP class="gwt-TabBarItem-selected">Query History</td>
		<td width="8%" NOWRAP class="gwt-TabBarItem"><html:link action="/evq.do?method=Get%20Query">Query Detail</html:link></td>
		<td>&nbsp;</td>
	</tr>
	<tr valign="top">
		<td colspan="3" align="left" class="queryHistoryPanel">
			<tiles:insert page="/WEB-INF/jsp/evqhistory.jsp" flush="true"/>
		</td>
	</tr>
	</logic:equal>
	<logic:equal name="EvqActionForm" property="showHistory" value="false">
	<tr valign="bottom">
		<td width="8%" NOWRAP class="gwt-TabBarItem"><html:link action="/evq.do?method=Get%20History">Query History</html:link></td>
		<td width="8%" NOWRAP class="gwt-TabBarItem-selected">Query Detail</td>
		<td>&nbsp;</td>
	</tr>
	<tr valign="top">
		<td colspan="3" align="left" class="queryDetailPanel">
			<tiles:insert page="/WEB-INF/jsp/evqquery.jsp" flush="true"/>
		</td>
	</tr>
	</logic:equal>
</table>
