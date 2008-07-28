<%@include file="globals.jsp" 
%>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" 
%>
<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit"
%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"
%>
<fmt:setBundle basename="cmsc-community" scope="request" />
<cmscedit:head title="reactions.title">
	<script type="text/javascript">
	   function addToGroup(){
	      var checkboxs = document.forms[1].getElementsByTagName("input");
	      var objectnumbers = '';
	      for(i = 0; i < checkboxs.length; i++) {
	         if(checkboxs[i].type == 'checkbox' && checkboxs[i].name.indexOf('chk_') == 0 && checkboxs[i].checked) {
	            objectnumbers += checkboxs[i].value;
	         }
	      }
	      if(objectnumbers == ''){
	         alert('Please choose the users!');
	         return false;
	      }
	      return true;
	   }
	</script>
</cmscedit:head>

<edit:ui-tabs>
   <edit:ui-tab key="community.search.users" active="true"/>
   <edit:ui-tab key="community.search.groups">
      ${pageContext.request.contextPath }/editors/community/searchConditionalGroupAction.do
   </edit:ui-tab>
</edit:ui-tabs>

<div class="editor">
   <div style="padding-left:10px;">
      <p><a href="userAddInitAction.do" style=" padding-left:20px; background: url(<cmsc:staticurl page='/editors/gfx/icons/new.png'/>) left center no-repeat"><fmt:message key="view.newgroup"/></a><p>
      <p><a href="../community/index.jsp">TEST</a></p>
      <html:form action="/editors/community/SearchConditionalUser.do" method="post">
    
      <table border="0">
         <tbody >
            <tr> 
               <td style="width:150px"><fmt:message key="community.search.fullname"/></td>
               <td><html:text style="width: 250px" property="fullName"/></td>
            </tr>
            <tr>
               <td style="width:150px"><fmt:message key="community.search.username"/></td>
               <td><html:text style="width: 250px" property="userName" /></td>
            </tr>
            <tr>
               <td style="width:150px"><fmt:message key="community.search.emailAddress"/></td>
               <td><html:text style="width: 250px" property="emailAddr" /></td>
            </tr>
            <tr>
               <td style="width:150px"><fmt:message key="community.search.groups"/></td>
               <td><html:text style="width: 250px" property="groups" /></td>
            </tr>
            <tr>
               <td style="width:150px"></td>
               <td><input type="submit" value="Search" name="submitButton"/></td>
            </tr>
         </tbody>
      </table>
      </html:form>
   </div>
</div>

<div class="editor">
   <div class="ruler_green"><div>&nbsp;<fmt:message key="community.search.result"/>&nbsp;</div></div>
   <div class="body">
      <form action="../community/AddUserToGroupInit.do" method="post">
         <p><input type="submit" value="add To Group" name="submitButton" onclick="return addToGroup()"/></p>
         <edit:ui-table items="${personForShow}" var="person" size="${totalCount}" requestURI="/editors/community/SearchConditionalUser.do">
            <edit:ui-tcolumn title="">
               <input type="checkbox" name="chk_" value="${person.authId}"/>&nbsp;
               <a href="${pageContext.request.contextPath }/editors/community/userAddInitAction.do?authid=${person.authId}"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit.png'/>" width="16" height="16" title="edit"></a>
               <a href="${pageContext.request.contextPath }/editors/community/deleteUserAction.do?authid=${person.authId}"><img src="<cmsc:staticurl page='/editors/gfx/icons/delete.png'/>" width="16" height="16" title="delete"></a>
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="community.search.fullname" sort="fullname">
               <a href="${pageContext.request.contextPath }/editors/community/userAddInitAction.do?authid=${person.authId}">${person.fullname}</a>
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="community.search.username" sort="username">
               ${person.username }
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="community.search.emailAddress" sort="email">
               ${person.email}
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="community.search.memberOf">
               ${person.groups}
            </edit:ui-tcolumn>
         </edit:ui-table>
      </form>
   </div>
</div>