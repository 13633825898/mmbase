<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="forumid" />
<mm:import externid="postareaid" />
<mm:import externid="postingid" />
<mm:import externid="postthreadid" />

<!-- login part -->
<%@ include file="getposterid.jsp" %>
<!-- end login part -->

<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
   <script language="JavaScript1.1" type="text/javascript" src="js/smilies.js"></script>
</head>
<body>

<div class="header">
  <mm:import id="headerpath" jspvar="headerpath"><mm:write referid="theme_header" /></mm:import>
  <jsp:include page="<%=headerpath%>"/>
</div>
                                                                                              
<div class="bodypart">

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%" align="center">
  <mm:node referid="postingid">
  <tr><th colspan="3"><mm:write referid="mlg_Edit_message" /></th></tr>
  <form action="<mm:url page="thread.jsp">
	<mm:param name="forumid" value="$forumid" />
	<mm:param name="postareaid" value="$postareaid" />
	<mm:param name="postthreadid" value="$postthreadid" />
	<mm:param name="postingid" value="$postingid" />
	</mm:url>" method="post" name="posting">
	<tr><th><mm:write referid="mlg_Name"/></th><td colspan="2">
		<mm:compare referid="posterid" value="-1" inverse="true">
		<mm:node number="$posterid">
		<mm:field name="account" /> (<mm:field name="firstname" /> <mm:field name="lastname" />)
		<input name="poster" type="hidden" value="<mm:field name="account" />" >
		</mm:node>
		</mm:compare>
		<mm:compare referid="posterid" value="-1">
		<input name="poster" size="32" value="gast" >
		</mm:compare>
	</td></tr>
	<tr><th width="150"><mm:write referid="mlg_Topic"/></th><td colspan="2"><input name="subject" style="width: 100%" value="<mm:field name="subject" />" ></td></th>
	<tr><th valign="top"><mm:write referid="mlg_Message"/><center><table width="100"><tr><th><%@ include file="includes/smilies.jsp" %></th></tr></table></center></th><td colspan="2"><textarea name="body" rows="20" style="width: 100%"><mm:formatter xslt="xslt/posting2textarea.xslt"><mm:field name="body" /></mm:formatter></textarea>
</td></tr>
	<tr><th>&nbsp;</th><td>
	<input type="hidden" name="action" value="editpost">
	<center><input type="submit" value="<mm:write referid="mlg_Save"/>">
  	</form>
	</td>
	<td>
	</mm:node>
  	<form action="<mm:url page="thread.jsp">
	<mm:param name="forumid" value="$forumid" />
	<mm:param name="postareaid" value="$postareaid" />
	<mm:param name="postthreadid" value="$postthreadid" />
	</mm:url>"
 	method="post">
	<p />
	<center>
	<input type="submit" value="<mm:write referid="mlg_Cancel"/>">
  	</form>
	</td>
	</tr>
</table>
</div>

<div class="footer">
  <mm:import id="footerpath" jspvar="footerpath"><mm:write referid="theme_footer" /></mm:import>
  <jsp:include page="<%=footerpath%>"/>
</div>
                                                                                              
</body>
</html>
</mm:locale>
</mm:content>
</mm:cloud>
