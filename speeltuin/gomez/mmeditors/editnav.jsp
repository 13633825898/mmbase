<%@include file="header.jsp" %="">
<mm:cloud name="mmbase" method="http" jspvar="cloud">
  <head>
    <title>Editors</title>
    <link rel="stylesheet" href="css/mmeditors.css" type="text/css" />
    <style>
<%@include file="css/mmeditors.css" %>     
    </style>
<% if ("R".equals(session.getValue("mmeditors_reload"))) { %>
  <script type="text/javascript" language="javascript">
<!-- 
   window.setTimeout('document.location="editnav.jsp"',125000);
// -->
  </script>
<% } %>
  </head>
  <body class="header">
    <table>
      <tr>
        <td>
            <a href="<mm:url page="index.jsp" />" target="_top"><img src="gfx/arrow.gif" /></a>&nbsp;&nbsp;editors</font>
            <%  Stack states = (Stack)session.getValue("mmeditors_states");
                if (states != null) {
                  for (Iterator i = states.iterator(); i.hasNext(); ) {
                    Properties state = (Properties)i.next();
                    String managerName = (String)state.get("manager");
                    NodeManager manager = cloud.getNodeManager(managerName);
            %>&nbsp;<img alt="" src="gfx/arrow.gif" />&nbsp;<%=manager.getGUIName()%>
            <%
                    String role = (String)state.get("role");
                    if (role != null) {
                        RelationManager relman = cloud.getRelationManager(role);
            %> (<%=relman.getForwardGUIName()%>)
            <%
                    }
                  }
                }
            %>
        </td>
        <td class="status">
          (<%=cloud.getUser().getIdentifier() %>)&nbsp;&nbsp;<a href="http://www.vpro.nl/tools/reload.2/help.shtml" target="_new">Reload help</a>
          </font>
        </td>
      </tr>
    </table>
  </body>
</mm:cloud>
</html>

