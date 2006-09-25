<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content expires="0">
<% response.setContentType("text/xml"); %>
<%@page import = "java.lang.Integer" %>
<%@page import = "java.lang.String" %>
<%@page import = "java.util.Random" %>
<%@page import = "java.util.HashMap" %>
<%@page import = "java.util.ArrayList" %>
<%@page import = "java.util.Collection" %>
<%@page import = "java.util.Iterator" %>
<mm:cloud method="logout">
<mm:import externid="username" required="true" jspvar="username"/>
<mm:import externid="password" required="true" jspvar="password"/>
<mm:import externid="templatename" required="true" jspvar="templatename"/>

<mm:import id="subject" jspvar="jsp_subject" vartype="String"/>
<mm:import id="body" jspvar="jsp_body" vartype="String"/>
<mm:import id="from" jspvar="jsp_from" vartype="String"/>

<mm:import id="filteringjspname" jspvar="filteringjspname" >doFiltering.jsp</mm:import>
<!--
Collect all users which should be email using this template, XML output 
 -->

<% 
  HashMap emailUsers = new HashMap();
  ArrayList removeUsers = new ArrayList();
  long lastSent = System.currentTimeMillis()/1000;
%>

<%
    try{ 
%>
<!-- find last sent time -->
			<mm:cloud username="$username" password="$password" jspvar="cloud" method="pagelogon">
        <mm:listnodescontainer type="proactivemailbatches">
          <mm:constraint operator="LIKE" field="name" referid="templatename" />
          <mm:listnodes>
            <mm:last>
              <mm:import id="batchesFound" />
              <mm:import id="lastsent" jspvar="jsp_lastsent" vartype="Long"><mm:field name="end_time" /></mm:import>
              <% lastSent = jsp_lastsent.longValue(); %>
            </mm:last>
          </mm:listnodes>
        </mm:listnodescontainer>

        <%@include file="users.jsp" %>
        
        
<!-- make output, and do last filtering  -->        
        <template>            
          <from><%=jsp_from%></from>
          <subject><%=jsp_subject%></subject>
          <body><%=jsp_body%></body>
          <users>
              <% 
              Iterator it = emailUsers.values().iterator();
              while ( it.hasNext() ) {
                Object[] o = (Object[])it.next();
                long lastact = Long.parseLong((String)o[5]);
                long currtime = System.currentTimeMillis()/1000;
                long twomonthssec = 2*30*24*60*60;
                if ( lastact > 1 ) {
                    if ( !( (currtime - lastact > twomonthssec) && (lastSent - lastact < twomonthssec)) ) {
                    }
                }
                else {
                }
              }
              %>
              <% 
                Iterator it1 = ((Collection)emailUsers.values()).iterator();
                while ( it1.hasNext() ) {
                  Object[] o = (Object[])it1.next();
              %>
                <user>
                  <firstname><%=(String)o[1]%></firstname>
                  <lastname><%=(String)o[2]%></lastname>
                  <username><%=(String)o[3]%></username>
                  <email><%=(String)o[4]%></email>
                  <lastactivity><%=(String)o[5]%></lastactivity>
                </user>            
              <% 
                }
              %>
          </users>
        </template>            
        
			</mm:cloud> 
<%
    } catch (Exception ex) {
%>
        <error>
          <% ex.toString(); %>
        </error>
<%
    }
%>        
</mm:cloud> 
</mm:content>