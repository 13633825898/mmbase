<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:import id="dac"><mm:function set="mmbob" name="getDefaultAccount" /></mm:import>
<mm:import id="dpw"><mm:function set="mmbob" name="getDefaultPassword" /></mm:import>
<mm:cloud sessionname="forum" username="$dac" password="$dpw">
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="adminmode">false</mm:import>
<mm:import externid="forumid" />
<mm:import externid="boxname">Inbox</mm:import>
<mm:import externid="mailboxid" />
<mm:import externid="messageid" />
<mm:import externid="pathtype">privatemessages</mm:import>
<mm:import externid="posterid" id="profileid" />

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
   <title>MMBob</title>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
</head>

<body>

<div class="header">
</div>

<div class="bodypart">

<mm:include page="path.jsp?type=$pathtype" />
<table cellpadding="0" cellspacing="0" style="margin-top : 20px;" width="95%">
 <tr>
   <td width="160" valign="top">
	<table cellpadding="0" width="150">
	<tr><td>
	<table cellpadding="0" class="list" cellspacing="0" width="150">
	<tr><th><mm:write referid="mlg_Folder" /></th></tr>
	<mm:node referid="posterid">
	<mm:related path="posrel,forummessagebox">
		<mm:node element="forummessagebox">
			<mm:field name="name">
			<mm:notpresent referid="mailboxid">
			<mm:compare referid2="boxname">
				<mm:remove referid="mailboxid" />
				<mm:import id="mailboxid"><mm:field name="number" /></mm:import>
			</mm:compare> 
			</mm:notpresent> 
			</mm:field>
			<tr><td><a href="<mm:url page="privatemessages.jsp" referids="forumid,mailboxid" />"><mm:field name="name" /></a> (<mm:relatednodes type="forumprivatemessage"><mm:last><mm:size /></mm:last></mm:relatednodes>)</td></tr>
		</mm:node>
	</mm:related>
	</mm:node>
	</table>
	</td></tr>
	<tr><td>
	<form action="<mm:url page="privatemessage.jsp" referids="forumid,mailboxid,messageid" />" METHOD="POST">
	<table cellpadding="0" class="list" style="margin-top : 20px;" cellspacing="0" width="150">
	<tr><th><mm:write referid="mlg_Add_folder" /></th></tr>
	<input name="action" type="hidden" value="newfolder">
	<tr><td><input name="newfolder" style="width: 98%" /></td></tr>
	</table>
	</form>
	</td></tr>
	<tr><td>
        <table cellpadding="0" class="list" style="margin-top : 20px;" cellspacing="0" width="150">
        <mm:import id="barsize">150</mm:import>
        <mm:nodefunction set="mmbob" name="getQuotaInfo" referids="forumid,posterid,barsize">
        <tr><th colspan="3"><mm:write referid="mlg_PM_Quota" /></th></tr>
        <tr><td colspan="3"><mm:write referid="mlg_You_are_using" /> <mm:field name="quotausedpercentage" />% <mm:write referid="mlg_of_your_quota" /></td></tr>
        <tr><td colspan="3"><img src="images/<mm:field name="quotawarning" />.gif" height="7" width="<mm:field name="quotausedbar" />"></td></tr>
        <tr><td align="left" width="33%">0%</td><td align="middle" width="34%">50%</td><td align="right" width="33%">100%</td></tr>
        </mm:nodefunction>
        </table>

	</td></tr>
	</table>
   </td>
   <form action="<mm:url page="privatemessageconfirmaction.jsp" referids="forumid,mailboxid,messageid" />" method="post">
   <td valign="top">
	<table cellpadding="0" class="list" style="margin-top : 2px;" cellspacing="0" width="100%" border="1">
	<tr><th><mm:write referid="mlg_Message" /></th><th><mm:write referid="mlg_Sender" /></th></tr>
	<mm:present referid="mailboxid">
	<mm:node referid="messageid">
	<tr>
	<td width="50%">
		<br />
		<mm:write referid="mlg_Subject" /> : <mm:field name="subject" /><br />
		<mm:write referid="mlg_Date" /> : <mm:field name="createtime"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field><br />
		<mm:write referid="mlg_Sender" /> : <mm:field name="poster" /> (<mm:field name="fullname" />)<br />
		<mm:write referid="mlg_Mailbox" /> : <mm:node referid="mailboxid"><mm:field name="name" /></mm:node><br />
		<br />
	</td>
	  <td width="30%">
	  <mm:functioncontainer>
                  <mm:field name="poster"><mm:param name="posterid" value="$_" /></mm:field>
	  	  <mm:nodefunction set="mmbob" name="getPosterInfo" referids="forumid">
                        <mm:field name="avatar"><mm:compare value="" inverse="true">
                         <img align="left" width="50" border="0" src="../img.db?<mm:field name="avatar" />" style="margin-top: 5px; margin-right: 10px;">
                        </mm:compare></mm:field>
			<mm:field name="account" /> (<mm:field name="firstname" /> <mm:field name="lastname" />)<br />
			<mm:write referid="mlg_Level"/> : <mm:field name="level" /> <br />
			<mm:write referid="mlg_Posts"/> : <mm:field name="accountpostcount" /><br />
			<mm:write referid="mlg_Gender"/> : <mm:field name="gender" /><br />
			<mm:write referid="mlg_Location"/> : <mm:field name="location" /><br /><br />
                        <mm:write referid="mlg_Member_since"/> : <mm:field name="firstlogin"><mm:time format="d/M/yy, HH:mm:ss" /></mm:field><br />
                        <mm:write referid="mlg_Last_visit"/> : <mm:field name="lastseen"><mm:time format="d/M/yy, HH:mm:ss" /></mm:field><br />
		  </mm:nodefunction>
	  </mm:functioncontainer>
	</td>
	</tr>

	<tr>
		<td colspan="2" height="350" valign="top">
			<h4><mm:field name="subject" /></h4>
			<p>
			<mm:field name="html(body)" />
			</p>
		</td>
	</tr>
	<tr>
		<th colspan="2" height="25" valign="top">
		<mm:write referid="mlg_Actions"/> : <input type="submit" name="folderaction" value="<mm:write referid="mlg_delete"/>"> - 
		<input type="submit" name="folderaction" value="<mm:write referid="mlg_forward"/>"> -
		<input type="submit" name="folderaction" value="<mm:write referid="mlg_email"/>"> -
		<input type="submit" name="folderaction" value="<mm:write referid="mlg_move"/>">
		</th>
	</tr>
	<mm:import id="viewstate"><mm:field name="viewstate" /></mm:import>
		<mm:compare referid="viewstate" value="0">
		<mm:setfield name="viewstate">1</mm:setfield>
		<mm:import id="mbn"><mm:node referid="mailboxid"><mm:field name="name" /></mm:node></mm:import>
		</mm:compare>
	</mm:node>
	<mm:compare referid="viewstate" value="0">
        <mm:function set="mmbob" name="signalMailboxChange" referids="forumid,posterid,mbn@mailboxid" />
	</mm:compare>
	</mm:present>
	</table>
	</form>
   </td>
 </tr>
</table>

</div>
<div class="footer">
</div>

</body>
</html>

</mm:locale>
</mm:cloud>
