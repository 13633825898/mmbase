<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Servers</title>
<link rel="stylesheet" type="text/css" href="../css/mmbase.css" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="servers" width="93%" cellspacing="1" cellpadding="3" border="0">
<tr align="left">
<th class="header" colspan="7">Server Overview
</td>
</tr>
<tr>
  <td class="multidata" colspan="7"><p>This overview describes all MMBase servers running on this MMBase system.</p>
</tr>
<tr><td>&nbsp;</td></tr>
<tr align="left">
	<th class="header">Machine</td>
	<th class="header">State</td>
	<th class="header">Last Seen</td>
	<th class="header">Uptime</td>
	<th class="header">Host</td>
	<th class="header">OS</td>
	<th class="header">&nbsp;</td>
</tr>
<mm:list type="mmservers" fields="name,showstate,showatime,host,os,jdk,uptime">
<tr>
	<td class="data">
	<mm:field name="name" />
	</td>
	<td class="data">
	<mm:field name="showstate" />
	</td>
	<td class="data">
	<mm:field name="showatime" />
	</td>
	<td class="data">
	<mm:field name="uptime" />
	</td>
	<td class="data">
	<mm:field name="host" />
	</td>
	<td class="data">
	<mm:field name="os" />
	</td>
	<td class="navigate" width="14">
		<a href="server/actions.jsp?server=<%=name%>"><img src="../images/pijl.gif" alt="next" border="0" align="right"></a>
	</td>
</tr>
</mm:list>
<tr><td>&nbsp;</td></tr>

<tr>
<td class="navigate"><a href="../default.jsp" target="_top"><img src="../images/pijl2.gif" alt="back" border="0" align="left" /></td>
<td class="data" colspan="6">Return to home page</td>
</tr>
</table>
</body></html>
</mm:cloud>
