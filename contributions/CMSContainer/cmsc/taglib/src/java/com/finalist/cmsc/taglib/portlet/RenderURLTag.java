package com.finalist.cmsc.taglib.portlet;

import javax.portlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import com.finalist.pluto.PortletURLImpl;

/**
 ** Supporting class for the <CODE>renderURL</CODE> tag.
 ** Creates a url that points to the current Portlet and triggers an render request
 ** with the supplied parameters. 
 **
 **/
public class RenderURLTag extends BasicURLTag
{

    protected PortletURL getRenderUrl() {
        PortletURL renderUrl = null;
        if (!StringUtil.isEmpty(page) && !StringUtil.isEmpty(window)) {
            String link = getLink();
            renderUrl = new PortletURLImpl(link, window, (HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), false);
        }
        else {
            RenderResponse renderResponse = (RenderResponse)pageContext.getRequest().getAttribute("javax.portlet.response");
            if (renderResponse != null)
            {
                renderUrl = renderResponse.createRenderURL();
            }
        }
        return renderUrl;
    }
    
}

