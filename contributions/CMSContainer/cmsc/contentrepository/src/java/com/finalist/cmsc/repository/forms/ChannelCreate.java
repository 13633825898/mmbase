/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.repository.forms;

import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class ChannelCreate extends MMBaseFormlessAction {

   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String parentchannel = getParameter(request, "parentchannel", true);
      String action = getParameter(request, "action");
      String channeltype = getParameter(request, "channeltype", RepositoryUtil.CONTENTCHANNEL);

      if (StringUtil.isEmptyOrWhitespace(action)) {
         request.getSession().setAttribute("parentchannel", parentchannel);

         ActionForward ret = new ActionForward(mapping.findForward("openwizard").getPath() + "?action=create"
               + "&contenttype=" + channeltype + "&returnurl=" + mapping.findForward("returnurl").getPath());
         ret.setRedirect(true);
         return ret;
      }
      else {
         if ("save".equals(action)) {
            String ewnodelastedited = getParameter(request, "ewnodelastedited");
            RepositoryUtil.appendChild(cloud, parentchannel, ewnodelastedited);
         }
         request.getSession().removeAttribute("parentchannel");
         ActionForward ret = mapping.findForward(SUCCESS);
         return ret;
      }
   }

}
