<%--
  This template creates a new folder.
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="portfolio.createfolder" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="currentfolder"/>
<mm:import externid="callerpage"/>
<mm:import externid="typeof"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>
<mm:import externid="detectclicks" from="parameters"/>
<mm:import externid="oldclicks" from="session"/>

<mm:present referid="detectclicks">
    <mm:compare referid="detectclicks" value="$oldclicks">
	<mm:redirect referids="$referids,currentfolder,typeof" page="$callerpage"/>
    </mm:compare>
    <mm:write session="oldclicks" referid="detectclicks"/>
</mm:present>

<mm:import externid="contact">-1</mm:import>
<mm:compare referid="contact" value="-1" inverse="true">
  <mm:import id="user" reset="true"><mm:write referid="contact"/></mm:import>
</mm:compare>

<%-- Check if the back button is not pressed --%>
<mm:notpresent referid="action2">
    <%-- check if a foldername is given --%>
    <mm:import id="foldername" externid="_name"/>
    <mm:compare referid="foldername" value="" inverse="true">
    <mm:node number="$user">
      <mm:relatednodes type="portfolios" constraints="m_type=$typeof" id="myportfolios">
        <mm:import id="pos" jspvar="pos" vartype="Integer">-1</mm:import>
        <mm:related path="posrel,folders">
          <mm:field name="posrel.pos" jspvar="posrelPos" vartype="Integer">
            <% if (posrelPos.intValue() > pos.intValue()) { %>
              <% pos = posrelPos; %>
            <% } %>
          </mm:field>
        </mm:related>
        <% int nextpos = pos.intValue(); %>
        <% nextpos++; %>
        <mm:createnode type="folders" id="myfolders">
          <mm:fieldlist type="all" fields="name,type">
            <mm:fieldinfo type="useinput" />
          </mm:fieldlist>
        </mm:createnode>
        <mm:createrelation role="posrel" source="myportfolios" destination="myfolders">
          <mm:setfield name="pos"><%= nextpos %></mm:setfield>
        </mm:createrelation>
      </mm:relatednodes>
    </mm:node>
    <mm:redirect referids="$referids,currentfolder,typeof,contact?" page="$callerpage"/>
    </mm:compare>
    <mm:compare referid="foldername" value="">
	  <mm:import id="error">1</mm:import>
	</mm:compare>


</mm:notpresent>


<%-- Check if the back button is pressed --%>
<mm:present referid="action2">
  <mm:import id="action2text"><di:translate key="portfolio.back" /></mm:import>
  <mm:compare referid="action2" referid2="action2text">
    <mm:redirect referids="$referids,currentfolder,typeof,contact?" page="$callerpage"/>
  </mm:compare>
</mm:present>

<div class="rows">


<div class="navigationbar">
<div class="titlebar">
<img src="<mm:treefile write="true" page="/gfx/icon_shareddocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" title="<di:translate key="portfolio.portfolio" />" alt="<di:translate key="portfolio.portfolio" />"/>
<di:translate key="portfolio.portfolio" />
</div>
</div>

<div class="folders">

<div class="folderHeader">
<di:translate key="portfolio.portfolio" />
</div>
<div class="folderBody"></div>
</div>


<div class="mainContent">

  <div class="contentHeader">
    <di:translate key="portfolio.createfolder" />
  </div>

  <div class="contentBodywit">
    <br><br><br>
    <%-- Show the form --%>
    <form name="createfolder" method="post" action="<mm:treefile page="/portfolio/createfolder.jsp" objectlist="$includePath" referids="$referids,contact?"/>">

      <table class="Font">
      <mm:fieldlist nodetype="folders" fields="name">
        <tr>
        <td><mm:fieldinfo type="guiname"/></td>
        <td><mm:fieldinfo type="input"/></td>
        </tr>
      </mm:fieldlist>
      </table>
    <script>
	document.forms['createfolder'].elements['_name'].focus();
    </script>
      <input type="hidden" name="detectclicks" value="<%= System.currentTimeMillis() %>">
      <input type="hidden" name="currentfolder" value="<mm:write referid="currentfolder"/>"/>
      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input type="hidden" name="typeof" value="<mm:write referid="typeof"/>"/>
      <input class="formbutton" type="submit" name="action1" value="<di:translate key="portfolio.create" />" />
      <input class="formbutton" type="submit" name="action2" value="<di:translate key="portfolio.back" />" />
      <mm:present referid="error">
	    <p/>
	    <h1><di:translate key="portfolio.foldernamenotempty" /></h1>
	  </mm:present>
    </form>
  </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
