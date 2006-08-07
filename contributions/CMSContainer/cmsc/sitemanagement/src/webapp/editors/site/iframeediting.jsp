<%@taglib uri="http://finalist.com/csmc" prefix="cmsc"%>

window.onload = fillIframes;

function fillIframes() {
	elements = getElementsByClass("portlet-config-canvas", document, "div");
	for(x = 0; x < elements.length; x++) {
		var element = elements[x];
		fillIframe(element);
	}
}


function getElementsByClass(searchClass,node,tag) {
	var classElements = new Array();
	if ( node == null )
		node = document;
	if ( tag == null )
		tag = '*';
	var els = node.getElementsByTagName(tag);
	var elsLen = els.length;
	var pattern = new RegExp('(^|\\s)'+searchClass+'(\\s|$)');
	for (i = 0, j = 0; i < elsLen; i++) {
		if ( pattern.test(els[i].className) ) {
			classElements[j] = els[i];
			j++;
		}
	}
	return classElements;
}

function realLeftPosition(element) {
	if(element == undefined) {
		return 0;
	}
	else {
		return element.offsetLeft + realLeftPosition(element.offsetParent);
	}
}


function fillIframe(div) {
	var iframe = document.createElement("iframe");
	var parent = div.parentNode;
	var parentWidth = parent.offsetWidth
	
	iframe.frameBorder = 0;
	iframe.className = "portlet-config-iframe";
	parent.appendChild(iframe);
	parent.style.height="323px";

	if ( iframe.contentDocument ) {
		doc = iframe.contentDocument;        
	}
	else {
		doc = iframe.contentWindow.document;
	}  
	writeDocument(doc, div);

   var difference = parentWidth - iframe.clientWidth;
	if(difference < 0) {
		var clientWidth = document.body.clientWidth;
		
		if(realLeftPosition(div) < clientWidth/2) {
	  		iframe.style.marginRight=difference+'px';
	  	}
	  	else {
	  		iframe.style.marginLeft=difference+'px';
	  	}
	}

	for(count = 0; count < document.styleSheets.length; count++) {
		ss = document.styleSheets[count];
		createStylesheet( doc, ss.href);
	}
}

function writeDocument(doc, div) {
	var javascriptWindow = "<cmsc:staticurl page='/editors/utils/window.js' />";
	var cssPortaledit = "<cmsc:staticurl page='/editors/site/portaledit.css' />";
    html = "<html>\n";
    html += "<head>\n";
    html += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n";
	html += "<script type='text/javascript' src='" + javascriptWindow + "'></script>"
	html += "<link rel='stylesheet' type='text/css' href='" + cssPortaledit + "' />";
    html += "</head>\n";
    html += "<body>\n";
    html +=   div.innerHTML;
    html += "</body>\n";
    html += "</html>";

	doc.open();
	doc.write(html);
	doc.close();
}

function createStylesheet(doc, location) {
	var head = getHeadElement(doc);
	var stylesheet = doc.createElement("link");
	stylesheet.setAttribute("href", location);
	stylesheet.setAttribute("type", "text/css");
	stylesheet.setAttribute("rel", "stylesheet");
	head.appendChild(stylesheet);
}

function getHeadElement(doc) {
	return doc.getElementsByTagName("head")[0];
}
