<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>

<di:hasrole role="teacher" inverse="true">
    <di:hasrole role="student">
        <jsp:forward page="student.jsp"/>
    </di:hasrole>
</di:hasrole>

<di:hasrole role="teacher">
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title>Voortgang</title>
  </mm:param>
</mm:treeinclude>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    Voortgang
  </div>		
</div>

<div class="folders">
  <div class="folderHeader">
    &nbsp;
  </div>
  <div class="folderBody">
    &nbsp;
  </div>
</div>

<div class="mainContent">
  <div class="contentHeader">
<%--    Some buttons working on this folder--%>
  </div>
  <div class="contentBodywit">


<mm:node number="$education">
<b><mm:field name="name" write="true"/></b>
<table class="font">
<tr>
<th></th>
    <mm:node number="progresstextbackground">
    <th>
        <img src="<mm:image template="font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(0,5,'Voortgang')+rotate(90)"/>">
    </th>
     <th>
        <img src="<mm:image template="font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(0,5,'Keer ingelogd')+rotate(90)"/>">
    </th>
      <th>
        <img src="<mm:image template="font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(0,5,'Tijd ingelogd')+rotate(90)"/>">
    </th>
    </mm:node>
    
  <mm:relatednodescontainer type="learnobjects" role="posrel">
    <mm:sortorder field="posrel.pos" direction="up"/>
    <mm:tree type="learnobjects" role="posrel" searchdir="destination" orderby="posrel.pos" direction="up">
      <mm:import id="nodetype" reset="true"><mm:nodeinfo type="type" /></mm:import>
      <mm:compare referid="nodetype" value="tests">
        <mm:field name="name" jspvar="name" vartype="String">
            <% name  = name.replaceAll("\\s+"," ").replaceAll("\"","''"); %>
         <mm:import id="template" reset="true">font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(10)+gravity(NorthEast)+text(0,5,"<%= name %>")+rotate(90)</mm:import>
         </mm:field>
         <mm:node number="progresstextbackground">
         <th><img src="<mm:image template="$template"/>"></th>
         </mm:node>
      </mm:compare>
    </mm:tree>
  </mm:relatednodescontainer>
</tr>
</mm:node>
<di:hasrole role="student">
 <mm:treeinclude page="/progress/progress_row.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="student"><mm:write referid="user"/></mm:param>
 </mm:treeinclude>
</di:hasrole>
<di:hasrole role="teacher">
    <mm:node referid="class">
        <mm:relatednodes type="people">
            <mm:import id="studentnumber" reset="true"><mm:field name="number"/></mm:import>
            <di:hasrole role="student" referid="studentnumber">
                <mm:treeinclude page="/progress/progress_row.jsp" objectlist="$includePath" referids="$referids">
                    <mm:param name="student"><mm:field name="number"/></mm:param>
                </mm:treeinclude>
            </di:hasrole>
        </mm:relatednodes>
    </mm:node>
</di:hasrole>


</table>


 </div>
</div>
</di:hasrole>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
