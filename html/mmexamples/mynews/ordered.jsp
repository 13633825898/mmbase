<%@page session="false" errorPage="error.jsp" language="java" contentType="text/html; charset=UTF-8" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content language="en" type="text/html"  escaper="inline">
<mm:import externid="magid">default.mags</mm:import>
<mm:cloud>
<mm:node number="$magid" id="mag">
<html>
 <head>
   <title><mm:field  name="title" /></title>
   <link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
 </head>
 <body class="basic">
   <h1><mm:field  name="title" /></h1>
   <mm:field  name="subtitle"><mm:isnotempty><h2><mm:write /></h2></mm:isnotempty></mm:field>
   <div class="intro">
     <mm:field  escape="p" name="intro"/>
   </div>
   <mm:field  escape="p" name="body"/>

   <table>
   <%-- we have to use the related tag if we want to order with pos --%>
   <mm:relatednodes id="newsid" role="posrel" type="news" orderby="posrel.pos">
     <mm:first>
       <table><tr><th>title</th><th class="navigate">&nbsp;</th></tr>
     </mm:first> 
     <tr>
       <td><mm:field name="title" /></td>
       <td  class="navigate link"><a href="<mm:url referids="mag,newsid" page="newsitem.jsp" />"><img src="<mm:url page="/mmbase/style/images/next.gif" />" alt="link"></a></td>
     </tr> 
     <mm:last></table></mm:last>
   </mm:relatednodes>
 </table>
 <hr />
  <div class="link">
    <a href="<mm:url referids="magid" page="." /> "><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" /> Simple news</a><br />
  </div>
  <hr /> 
  <a href="<mm:url page="../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
</body>
</html>
</mm:node>
</mm:cloud>
</mm:content>