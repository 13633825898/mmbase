<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
	<link href="../css/main.css" type="text/css" rel="stylesheet" />
	<script type="text/javascript" src="../utils/transparent_png.js" ></script>
<title><fmt:message key="userlist.title" /></title>
</head>
<body>
<mm:cloud loginpage="../login.jsp" rank="administrator" jspvar="cloud">
<div style="float: left">
	<div class="side_block_gray" >
		<!-- bovenste balkje -->
		<div class="header">
			<div class="title"><fmt:message key="userlist.groups" /></div>
			<div class="header_end"></div>
		</div>
		
		<ul class="shortcuts">
            <li class="usergroupnew">
				<a href="GroupInitAction.do"><fmt:message key="userlist.newgroup" /></a>
			</li>
		</ul>
		<div style="clear:both; height:10px;"></div>

		<table style="padding-left: 30px">
			<tr>
				<td><b><fmt:message key="group.name" /></b></td>
			</tr>
			<mm:listnodes type='mmbasegroups' orderby='name'>
				<tr>
					<td style="padding-right: 10px"><a href="GroupInitAction.do?id=<mm:field name='number'/>"><mm:field name="name" /></a></td>
					<mm:haspage page="/editors/usermanagement/site//">
						<td><a href="SiteRolesInitAction.do?nodeNumber=<mm:field name='number'/>">
							<img src="../gfx/icons/roles_site.png" border='0' title="<fmt:message key="userlist.siteroles" />" />
						</a></td>
					</mm:haspage>
					<mm:haspage page="/editors/usermanagement/repository/">
						<td><a href="ContentRolesInitAction.do?nodeNumber=<mm:field name='number'/>">
							<img src="../gfx/icons/roles_repository.png" border='0' title="<fmt:message key="userlist.contentroles" />" />
						</a></td>
					</mm:haspage>
	
					<td><mm:maydelete>
						<a href="DeleteGroupAction.do?id=<mm:field name='number'/>">
							<img src="../gfx/icons/delete.png" border='0' title="<fmt:message key="userlist.removegroup" />"
								onclick="return confirm('<fmt:message key="userlist.removegroupquestion" />')" />
						</a>
					</mm:maydelete></td>
				</tr>
			</mm:listnodes>
		</table>

		<!-- einde block -->
		<div class="side_block_end"></div>
	</div>
</div>

<div style="float: left;padding-left: 5px">
	<div class="side_block_green" style="float: left">
		<!-- bovenste balkje -->
		<div class="header">
			<div class="title"><fmt:message key="userlist.users" /></div>
			<div class="header_end"></div>
		</div>

		<ul class="shortcuts">
            <li class="usernew">
				<a href="UserInitAction.do"><fmt:message key="userlist.newuser" /></a>
			</li>
		</ul>
		<div style="clear:both; height:10px;"></div>

		<table style="padding-left: 30px">
			<tr>
				<td><b><fmt:message key="user.account" /></b></td>
				<td><b><fmt:message key="user.name" /></b></td>
			</tr>
			<mm:listnodes type='user' orderby='username'>
				<mm:field name="username" id="username" write="false" />
				<mm:compare referid="username" value="anonymous" inverse="true">
				<tr>
					<td style="padding-right: 10px"><a href="UserInitAction.do?id=<mm:field name='number'/>"><mm:field name="username" /></a></td>
					<td style="padding-right: 10px"><mm:field name="firstname" /> <mm:field name="prefix" /> <mm:field name="surname" /></td>
					<td><mm:maydelete>
						<a href="DeleteUserAction.do?id=<mm:field name='number'/>">
							<img src="../gfx/icons/delete.png" border='0' title="<fmt:message key="userlist.removeuser" />"
								onclick="return confirm('<fmt:message key="userlist.removeuserquestion" />')" />
						</a>
					</mm:maydelete></td>
				</tr>
				</mm:compare>
			</mm:listnodes>
		</table>
		<!-- einde block -->
		<div class="side_block_end"></div>
	</div>
</div>
</mm:cloud>
</body>
</html:html>
</mm:content>