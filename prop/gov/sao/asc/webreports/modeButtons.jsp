<%@ page import="java.util.*, info.ReportsConstants" %>

<%
  Vector statusOptions = new Vector();  // status options values
  Vector statusDisplay = new Vector();  // status display strings for values
  String rstatus = ReportsConstants.SAVE;
  String preComp = rr.getPreComp();

  boolean reassignBtn = false;
  String  statusStr = theStatus;
  statusOptions.addElement(ReportsConstants.SAVE);
  statusDisplay.addElement("&nbsp;");
  //pundit only who is not a chair won't be resetting to type
  //if (!mode.equals(ReportsConstants.PUNDIT)) {
    statusOptions.addElement(ReportsConstants.COMPLETE);
    if (reportType.equals(ReportsConstants.LP)) {
      statusDisplay.addElement("Panel Chair");
      if (statusStr.equals(ReportsConstants.COMPLETE)) 
         statusStr="Panel Chair";
    } else {
      statusDisplay.addElement(ReportsConstants.COMPLETE);
    }
  //}

  statusOptions.addElement(ReportsConstants.CHECKOFF);
  if (reportType.equals(ReportsConstants.LP)) {
    statusDisplay.addElement("Pundit");
    if (statusStr.equals(ReportsConstants.CHECKOFF)) 
         statusStr="Pundit";
  } else {
    statusDisplay.addElement(ReportsConstants.CHECKOFF);
  }

  statusOptions.addElement(ReportsConstants.FINALIZE);
  statusDisplay.addElement(ReportsConstants.FINALIZE);
  
  if (beforePR && !mode.equals(ReportsConstants.ADMINEDIT) && !mode.equals(ReportsConstants.ADMIN) &&
	!mode.equals(ReportsConstants.DEVELOPER))
    // Keep code for reassign post GoogleDoc, but force to not show
    reassignBtn=false;

  String btnclass = "btn";
  if (disabled.length() > 1) {
      btnclass ="btnDisabled";
  }
       
  if  ( (mode.equals(ReportsConstants.CHAIR) && !reportType.equals(ReportsConstants.LP)) ||
       (memberType.indexOf(ReportsConstants.PUNDIT) >= 0 && reportType.equals(ReportsConstants.LP)) ||
       mode.equals(ReportsConstants.ADMINEDIT) ||
       mode.equals(ReportsConstants.ADMIN) ) {
  }
  String saveButton = ""; 
  if (disabled.length() <= 1) {
     saveButton = "<input type=\"button\" class=\"" + btnclass + "\" value=\"Save\" " +  disabled +  
    " onClick='theform.action=\"/reports/updateReport\"; theform.target=\"_self\"; theform.operation.value = \"" + 
     ReportsConstants.SAVE + "\"; theform.reportStatus.value=\"" +
     ReportsConstants.SAVE + "\"; if (validateReport()) {this.form.submit();}'> ";
  }

%>

<input type="hidden" name="preComp" value=<%=preComp%>>
<table width="99%">
<tr class="rptBtn" >
<td>
<% if (!beforePR) {

     if (disabled.length() > 1 || 
       (mode.equals(ReportsConstants.ADMIN) && isAllowedToEdit) ) {
       if (disabled.length() > 1 ) {
         String ts = statusStr;
         if (theStatus == null || theStatus.equals("")) {
            ts = "Incomplete";
         } 
%>
<%=ReportsConstants.APPLABEL%>: <%=ts%>
<% }else { %>
<%=ReportsConstants.APPLABEL%>: <Select name="reportStatusMenu" <%= disabled %> >
<%
  int ii=0;
  for(ii=0; ii < statusOptions.size(); ii++) {
  String currentChoice = (String)statusOptions.get(ii);
        if(currentChoice.equals(theStatus)) {
             rstatus = theStatus;
%>
<option value="<%= currentChoice %>" selected> <%= (String)statusDisplay.get(ii) %>

<% } else { %>

<option value="<%= currentChoice %>"> <%= (String)statusDisplay.get(ii) %>

<%  }
   }
%>
</select>
<% } %>
<td>
<%--  Secondary can view report but shouldn't be allowed to complete/uncomplete--%>
<% if (isAllowedUncomplete && !rr.isSecondaryPeer()) { %>
<input type="button" value="<%=ReportsConstants.UNAPPLABEL%>" class="doneBtn"
       onClick='theform.action="/reports/updateReport";theform.target="_self";theform.operation.value = "<%=ReportsConstants.UNCOMPLETE%>";  theform.reportStatus.value="<%=undoneStatus%>"; this.form.submit();'>
<br>
<span class="small">Click here if you need to edit your report.</span>
<% } %>

<%   } else if (!rr.isSecondaryPeer()){ %>
<input type="button" value="<%=ReportsConstants.APPBTNLABEL%>" <%= disabled %> class="doneBtn"
       onClick='theform.action="/reports/updateReport";theform.target="_self";theform.operation.value = "<%=ReportsConstants.SAVE%>";  theform.reportStatus.value="<%=doneStatus%>"; if (validateReport()) {this.form.submit();} else {theform.reportStatus.value="<%=theStatus%>";}'>
<br><span class="small">Click here when you have finished editing your report.</span>

<%   }  %>
<%  } else { //beforePR
      if (preComp.equalsIgnoreCase("true")){ %>
    <input type="button" value="<%=ReportsConstants.UNAPPLABEL%>" class="doneBtn"
           onClick='theform.action="/reports/updateReport";theform.target="_self";theform.operation.value = "<%=ReportsConstants.SAVE%>";  theform.preComp.value=""; this.form.submit();'>
    <br><span class="small">Click here if you need to edit your report.</span>
      <% } else { %>&nbsp;
    <input type="button" value="<%=ReportsConstants.APPBTNLABEL%>" class="doneBtn"
           onClick='theform.action="/reports/updateReport";theform.target="_self";theform.operation.value = "<%=ReportsConstants.SAVE%>";  theform.preComp.value="true"; this.form.submit();'>
    <br><span class="small">Click here when you have finished editing your report.</span>
    <% } %>
<% } %>
<input type="hidden" name="reportStatus" value=<%=ReportsConstants.SAVE%>>
</td>

<%-- Display general buttons and links. Only for adminedit with Goolgle Doc reports--%>
  <%  if (beforePR ||
                  (mode.equals(ReportsConstants.ADMINEDIT) || mode.equals(ReportsConstants.ADMIN) || mode.equals(ReportsConstants.DEVELOPER)) && !beforePR) { %>
  <td>
    <span class="btnDiv">
    <input type="button" class="<%=btnclass%>" value="Save" accessKey="S"  <%= disabled %>
           onClick='theform.action="/reports/updateReport";theform.target="_self";theform.operation.value = "<%=ReportsConstants.SAVE%>"; theform.reportStatus.value="<%=ReportsConstants.SAVE%>"; if (validateReport()) {this.form.submit();}'>
    <input type="button" class="<%=btnclass%>" value="Clear" <%=disabled%> onClick='clearfields()'>

    </span>
  </td>
  <% } %>
<% if (reassignBtn) { %>
<td>
  <input type="button" class="btnC" value="Request Reassignment due to conflicts" onClick='theform.action="/reports/updateReport";theform.target="_self";theform.operation.value="<%=ReportsConstants.REASSIGN%>"; this.form.submit()'>
<% } %>
<td align="right">
<input type="button" value="Printer-Friendly" class="linkBtn" onclick='theform.target="_blank";theform.action="/reports/updateReport";theform.operation.value="<%=ReportsConstants.PRINTVERSION%>";this.form.submit();'>
</td>
</tr>
</table>

