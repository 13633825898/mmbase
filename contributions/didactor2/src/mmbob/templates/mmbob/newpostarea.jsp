<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@ include file="thememanager/loadvars.jsp" %>
<%@ include file="settings.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
   <title>MMBob</title>
</head>
<mm:import externid="forumid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->
<center>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr><th colspan="3"><di:translate key="mmbob.createnewarea" /></th></tr>

  <form action="<mm:url page="start.jsp">
                    <mm:param name="forumid" value="$forumid" />
                </mm:url>" method="post">
    <tr><th><di:translate key="mmbob.name" /></th><td colspan="2">
    <input name="name" size="70" value="" style="width: 100%">
    </td></tr>
    <tr><th><di:translate key="mmbob.description" /></th><td colspan="2">
    <textarea name="description" rows="5" style="width: 100%"></textarea>
    </td></tr>
    <input type="hidden" name="admincheck" value="true">
    <input type="hidden" name="action" value="newpostarea">
    <tr><th>&nbsp;</th><td align="middle" >
    <input type="submit" value="<di:translate key="mmbob.commit" />">
    </form>
    </td>
    <td>
    <form action="<mm:url page="start.jsp">
    <mm:param name="forumid" value="$forumid" />
    </mm:url>"
    method="post">
    <p />
    <center>
    <input type="submit" value="<di:translate key="mmbob.cancel" />">
    </form>
    </td>
    </tr>

</table>
</center>
</html>
</mm:cloud>

