<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<html:html locale="true">
<head>
 <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
 <meta http-equiv="expires" content="0">
 <meta http-equiv="pragma" content="no-cache">
 <base href="<%=request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/"%>" >

 <script language="JavaScript" type="text/javascript">
  <!--
  // initialize variables
  var appcontextpath = "<html:rewrite page='/'/>";
  // -->
 </script>
 <script language="JavaScript" type="text/javascript" src="<html:rewrite page='/evq.js'/>"></script>
 <style type="text/css">@import url("<html:rewrite page='/evq.css'/>");</style>
 <title><tiles:getAsString name="title"/></title>
</head>
<body>
 <table class="masterlayout" border="0">
  <tr>
   <td class="header">
    <div class="header">
     <tiles:insert attribute="header"/>
    </div>
   </td>
   </tr>
   <tr>
    <td class="contentlayout">
     <div class="contentlayout">
      <table class="contentlayout" border="0" name="content" id="content">
       <tr>
        <td class="bodycontent">
         <div class="bodycontent">
          <tiles:insert attribute="body-content"/>
         </div>
        </td>
       </tr>
      </table>
     </div>
    </td>
   </tr>
  <tr>
   <td class="footer">
    <div class="footer">
     <tiles:insert attribute="footer"/>
    </div>
   </td>
   </tr>
  </table>
</body>
</html:html>

