<%@ include file="settings.jsp"
%><mm:cloud name="mmbase" method="http" jspvar="cloud"><%@ page errorPage="exception.jsp"
%><%
    // start a popup wizard.
    String wizardname = request.getParameter("wizard");
    String objectnumber = request.getParameter("objectnumber");
    String inline = request.getParameter("inline");

    if (wizardname!=null && objectnumber!=null && !wizardname.equals("") && !objectnumber.equals("") && wizardname.indexOf("|")==-1) {
        if ("true".equals(inline)) {
            response.sendRedirect(response.encodeURL("wizard.jsp?proceed=true&wizard="+wizardname+"&sessionkey="+ewconfig.sessionKey+"&objectnumber="+objectnumber));
        } else {
            String sessionkey = wizardname + "|popup" + new java.util.Date().getTime();
            response.sendRedirect(response.encodeURL("wizard.jsp"+ewconfig.sessionId+"?wizard="+wizardname+"&sessionkey="+sessionkey+"&objectnumber="+objectnumber+"&referrer="+ewconfig.backPage));
        }
    } else {
        throw new WizardException("Could not start a popup wizard because no wizardname and objectnumber are applied."+
                                  " Please make sure you define a wizardname and objectnumber in the wizard schema.");
    }
%>
</mm:cloud>
