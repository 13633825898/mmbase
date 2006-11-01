<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.util.HashMap"%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title>MMBob</title>
    <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
    <link rel="stylesheet" type="text/css" href="<mm:treefile page="/mmbob/css/navigation.css" objectlist="$includePath" referids="$referids" />" />
  </mm:param>
</mm:treeinclude>

<div class="rows">
  <div class="relativenavigationbar">
      <img src="<mm:treefile write="true" page="/gfx/icon_forum.gif" objectlist="$includePath" />" width="25" height="13" border="0" title="forum" alt="forum" /> Forum
  </div>
  <mm:import externid="forumid" jspvar="forumid">unknown</mm:import>
  <di:hasrole role="teacher">
    <mm:remove referid="posterid"/>
    <mm:remove referid="lang"/>
    <mm:listnodes type="posters" constraints="account='admin' AND password='admin2k'">
        <mm:import id="adminposter" reset="true"><mm:field name="number"/></mm:import>
    </mm:listnodes>

    <%@ include file="getposterid.jsp" %>
    <mm:import id="dummy" reset="true"><mm:write referid="posterid"/></mm:import>
    <mm:present referid="adminposter">
      <mm:import id="posterid" reset="true"><mm:write referid="adminposter"/></mm:import>
    </mm:present>
    <mm:import id="newadministrator" reset="true"><mm:write referid="dummy"/></mm:import>
    <mm:booleanfunction set="mmbob" name="newAdministrator" referids="forumid,posterid,newadministrator"/>
    <mm:import id="posterid" reset="true"><mm:write referid="dummy"/></mm:import>
  </di:hasrole>

  <iframe width="100%" height="100%" name="content" frameborder="0" src="<mm:treefile page="/mmbob/start.jsp" objectlist="$includePath" referids="$referids" escapeamps="false"/>&forumid=<mm:write referid="forumid"/>"></iframe>

  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids "/>
</div>
</mm:cloud>

</mm:content>
