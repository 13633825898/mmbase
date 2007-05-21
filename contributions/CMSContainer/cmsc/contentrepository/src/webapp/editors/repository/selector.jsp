<%@page language="java" contentType="text/html;charset=utf-8"%>

<%@include file="globals.jsp"%>
<%@page import="com.finalist.cmsc.repository.RepositoryUtil" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<cmscedit:head title="selector.title" ajax="true">
	<script type="text/javascript" src="ccp.js"></script>
	<script type="text/javascript" src="../utils/cookies.js"></script>

	<link href="../utils/ajaxtree/ajaxtree.css" type="text/css" rel="stylesheet" />
	<link href="../utils/ajaxtree/addressbar.css" type="text/css" rel="stylesheet" />

	<script type="text/javascript" src="../utils/ajaxtree/ajaxtree.js"></script>
	<script type="text/javascript" src="../utils/ajaxtree/addressbar.js"></script>

	<script type="text/javascript">
		ajaxTreeConfig.resources = '../utils/ajaxtree/images/';
		ajaxTreeConfig.url = '<mm:url page="Navigator.do" />';
		ajaxTreeConfig.addressbarId = 'addressbar';
   </script>
   <script type="text/javascript">
      function resizeTreeDiv() {
         var treeElement = document.getElementById('tree');
         if (treeElement) {
            var x = treeElement.offsetHeight;
            var leftElement = document.getElementById('left');
            x = leftElement.offsetHeight - x;
            var y = getFrameHeight(document.window);
            treeElement.style.height = y - x + 'px';
         }
      }
      
      function clearDefaultSearchText(defaultText) {
      	var searchField = document.forms["searchForm"]["title"];
      	if(searchField.value == defaultText) {
	      	searchField.value = "";
      	}
      }
      
      window.onresize = resizeTreeDiv;
      
      function loadFunction() {
		ajaxTreeLoader.initTree('', 'tree_div');
		resizeTreeDiv();
		alphaImages();
      }
   </script>

	<style type="text/css">
		.tooltip {
			position: absolute;
			display: none;
			z-index: 1000;
			left: 0px;
			top: 0px;
		}
		.width80 {
			width: 80%
		}
	</style>
</cmscedit:head>
<body style="overflow: auto" onload="loadFunction();">
   <mm:cloud jspvar="cloud" loginpage="../login.jsp">

	<div id="left">
		<cmscedit:sideblock title="selector.search.header" titleStyle="width: 241px;" bodyClass="body_table">
			<div class="search_form">
	      		<form action="SearchAction.do" name="searchForm" method="post" target="content">
				<input type="text" name="title" value="<fmt:message key="selector.search.term" />" onfocus="clearDefaultSearchText('<fmt:message key="selector.search.term" />');"/>
				</form>
			</div>
		
			<div class="search_form_options">
				<a href="javascript:document.forms['searchForm'].submit()" class="button"><fmt:message key="selector.search.search" /></a>
			</div>
				
			<ul class="shortcuts">
				<mm:hasrank minvalue="administrator">
				<li class="trashbin">
					<a href="<mm:url page="../recyclebin/index.jsp"/>" target="content">
						<fmt:message key="selector.recyclebin" />
					</a>
					<mm:node number="<%= RepositoryUtil.ALIAS_TRASH %>">
						(<mm:countrelations type="contentelement" searchdir="destination" role="contentrel"/>)
					</mm:node>
				</li>
				</mm:hasrank>
        			<li class="images"><a href="<mm:url page="../resources/ImageInitAction.do"/>" target="content"><fmt:message key="selector.images" /></a></li>
				<li class="attachements"><a href="<mm:url page="../resources/AttachmentInitAction.do"/>" target="content"><fmt:message key="selector.attachments" /></a></li>
				<li class="urls"><a href="<mm:url page="../resources/UrlInitAction.do"/>" target="content"><fmt:message key="selector.urls" /></a></li>
			</ul>
		</cmscedit:sideblock>
		<cmscedit:sideblock title="selector.title" titleClass="side_block_gray" bodyClass="body_table"
			titleStyle="width: 241px;">
			<div class="search_form">
				<c:if test="${not empty param.channel}">
	 				<mm:node number="${param.channel}">
						<mm:field name="path" jspvar="channelPath" write="false" />
					</mm:node>
				</c:if>
				<html:form action="/editors/repository/QuickSearchAction" target="bottompane" styleId="addressBarForm">
					<html:text property="path" value="${channelPath}" styleId="addressbar"/>
				</html:form>
			</div>
			<div class="search_form_options">
				<a href="#" class="button" onclick="getElementById('addressBarForm').submit()"> <fmt:message key="selector.search" /> </a>
			</div>

			<div id="addressbar_choices" class="addressbar"></div>
			<script type="text/javascript">
				new AddressBar("addressbar",
					"addressbar_choices", 
					ajaxTreeConfig.url + "?action=autocomplete",
					{paramName: "path" });
			</script>
			<br />
			<div id="tree" style="float: left;width: 239px; height: 100px; overflow:auto">
				<div style="float: left" id="tree_div"><fmt:message key="selector.loading" /></div>
				
				<jsp:include page="../usermanagement/role_legend.jsp"/>
			</div>

			<html:form action="/editors/repository/PasteAction">
				<html:hidden property="action" />
				<html:hidden property="sourcePasteChannel" />
				<html:hidden property="destPasteChannel" />
			</html:form>
		</cmscedit:sideblock>
	</div>
</mm:cloud>
</body>
</html:html>
</mm:content>