<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="comp"%>
<table cellpadding="0" cellspacing="0" class="headerPanel">
  <tr valign="center"> 
    <td align="left" class="logo">
		<img src="logo.gif">
    </td>
    <td NOWRAP align="left" class="title">
		Electrical Volumes Query
    </td>
    <td NOWRAP align="left" valign="bottom" class="label">User: <bean:write name="EvqActionForm" property="user" />&nbsp;&nbsp;<html:link action="/evq.do?method=Logout">Logout</html:link></td>
  </tr>
</table>
<hr class="separator">