<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>

<logic:messagesPresent>
 <table class="common_errors">
  <tr><td class="headers"><bean:message key="errors.header"/></td></tr>
  <tr>
   <td class="body">
    <ul>
     <html:messages id="error" message="false"><li><bean:write name="error"/></li></html:messages>
    </ul>
   </td>
  </tr>
  <tr><td class="footers"><bean:message key="errors.footer"/></td></tr>
 </table>
</logic:messagesPresent>
