<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@page import="com.finalist.newsletter.services.NewsletterSubscriptionServices"%>
<%@page import="com.finalist.newsletter.services.NewsletterServiceFactory"%>
<%@page import="java.util.*"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="com.finalist.newsletter.services.CommunityModuleAdapter" %>
good!
<%
	NewsletterSubscriptionServices service = NewsletterServiceFactory.getNewsletterSubscriptionServices();
	int userId = CommunityModuleAdapter.getCurrentUser().getId().intValue();
	int newsletterId = 0;
	int tagId = 0;
	boolean hasSelect = false;
	String status = "unSubscription";
	String format = "html";
	String action = null;
	//Date pausedate = null;

	if(null!=request.getParameter("action"))
	{
	action = request.getParameter("action");
	}
	if(null!=request.getParameter("newsletterId"))
	{
	newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
	}
	if(null!=request.getParameter("tagId"))
	{
	tagId = Integer.parseInt(request.getParameter("tagId"));
	}
	if(null!=request.getParameter("select"))
	{
	hasSelect =  Boolean.parseBoolean(request.getParameter("select"));
	}
	if(null!=request.getParameter("format"))
	{
	 format = request.getParameter("format");
	}
	if(null!=request.getParameter("status"))
	{
	 status = request.getParameter("status");
	}
	//if(null!=request.getParameter("pausedate"))
	//{
	// String pausedateString = request.getParameter("pausedate");
	// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");    
	// pausedate = dateFormat.parse(pausedateString);
	//}
	//add newrecord
	if(service.noSubscriptionRecord(userId,newsletterId))
		{
			service.addNewRecord(userId,newsletterId);
		}else  
   //	modify status
	if("modifyStatus".equals(action))
	{
		System.out.println("modifyStatus="+status);
		if("ACTIVE".equals(status))
		{
			if(hasSelect){
				service.modifyStauts(userId,newsletterId,"ACTIVE");
			}else{
				service.modifyStauts(userId,newsletterId,"INACTIVE");
			}
		}
		if("PAUSED".equals(status))
		{
			if(hasSelect){
				service.modifyStauts(userId,newsletterId,"PAUSED");
			}else{
				service.modifyStauts(userId,newsletterId,"ACTIVE");
			}		
		}
	}
	
	
	//modify format
	if("modifyFormat".equals(action))
	{	
		System.out.println("modifyFormat");
		service.modifyFormat(userId,newsletterId,format);
	}
	
	//select tag
	if("modifyTag".equals(action))
	{	
		System.out.println("modifyTag");
		if(hasSelect){
				System.out.println("select");
				service.selectTagInLetter(userId,newsletterId,tagId);	
		}
			else{
			System.out.println("unselect");
				service.unSelectTagInLetter(userId,newsletterId,tagId);	
		}
	}
		
	
%>