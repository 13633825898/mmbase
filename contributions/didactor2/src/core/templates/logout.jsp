<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<mm:cloud loginpage="/login.jsp" jspvar="cloud"> 
<%-- get the $username --%>
<mm:import id="username" jspvar="username"><%=cloud.getUser().getIdentifier()%></mm:import>
<%-- get the $user --%>
<mm:listnodescontainer type="people">
  <mm:constraint operator="equal" field="username" referid="username" />
  <mm:listnodes>
    <mm:first>
        <mm:node>
            <mm:field id="user" name="number" write="false" />
        </mm:node>
    </mm:first>
  </mm:listnodes>
  <mm:notpresent referid="user">
    <mm:import id="user">0</mm:import>
  </mm:notpresent>
</mm:listnodescontainer>
<mm:present referid="user">
   <mm:node number="$user" notfound="skip">
      <mm:setfield name="islogged">0</mm:setfield>
   </mm:node>
</mm:present>
</mm:cloud>
<mm:cloud method="logout" />
<mm:redirect page="/" />
