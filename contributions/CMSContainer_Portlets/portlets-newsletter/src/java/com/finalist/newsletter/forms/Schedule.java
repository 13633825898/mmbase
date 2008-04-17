package com.finalist.newsletter.forms;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.newsletter.schedule.AbstractSchedule;
import com.finalist.newsletter.schedule.DailySchedule;
import com.finalist.newsletter.schedule.MonthSchedule;
import com.finalist.newsletter.schedule.ScheduleService;
import com.finalist.newsletter.schedule.SingleSchedule;
import com.finalist.newsletter.schedule.WeeklySchedule;

public class Schedule  extends MMBaseAction{
   
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form,
         HttpServletRequest request, HttpServletResponse response, Cloud cloud)
         throws Exception {
      Map<String,Object> requestParameters = new HashMap<String,Object>();
      AbstractSchedule schedule = null;
      //type calendar type , 1 once ,2 daily ,3 weekly ,4 monthly
      String type = request.getParameter("type");
      String hour = request.getParameter("hour");
      String minute = request.getParameter("minute");
      if(type != null) {
         requestParameters.put("hour", hour);
         requestParameters.put("minute", minute);
         if(type.equals("1")) {
            String date = request.getParameter("date");

            requestParameters.put("date", date);
            schedule = new SingleSchedule();
         }
         else if(type.equals("2")) {
            String date = request.getParameter("date");
            requestParameters.put("date", date);

            String strategy = request.getParameter("strategy");
            requestParameters.put("approach", strategy);
            
            if(strategy != null && strategy.equals("2")) {
               String interval = request.getParameter("interval");
               requestParameters.put("interval", interval);
            }
            schedule = new DailySchedule();
            
         }
         else if(type.equals("3")) {
            String interval = request.getParameter("interval");
            requestParameters.put("interval", interval);
            String[] weeks = request.getParameterValues("weeks");
            requestParameters.put("weeks", getWeeks(weeks));
            schedule = new WeeklySchedule();
         }
         else {
            String strategy = request.getParameter("strategy");
            requestParameters.put("strategy", strategy);
            
            if(strategy != null) {
               if(strategy.equals("0")) {
                  String day = request.getParameter("day");
                  requestParameters.put("day", day); 
               }
               else if (strategy.equals("1")) {
                  String whichweek = request.getParameter("whichweek");
                  requestParameters.put("whichweek", whichweek); 
                  
                  String week = request.getParameter("week");
                  requestParameters.put("week", week);
               }
            }
            String[] months = request.getParameterValues("month");
            requestParameters.put("month", getWeeks(months));
            schedule = new MonthSchedule();
         }
        
         ScheduleService Service = new ScheduleService(schedule);
         Service.setRequestParameters(requestParameters);
         String expression = Service.transform();
         response.setContentType("text/xml");
         
         response.getWriter().print("<expression>"+expression+"</expression>");
      }
      return null;
   }
   
   /**
    * from array to String
    * @param args
    * @return
    */
   private String getWeeks(String[] weeks) {
      StringBuilder sb = new StringBuilder();
      Arrays.sort(weeks);
      for(int i = 0 ; i < weeks.length ; i++) {
         sb.append(weeks[i]);
      }
      return sb.toString();
   }

}
