<%@ page session="true" import="java.util.*, java.io.*, java.text.SimpleDateFormat" %>
<%@ page import="info.User,info.ReviewReport,info.Proposal,info.ProposalFile,ascds.LogMessage,org.apache.commons.lang3.StringEscapeUtils" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1 %>

<%@ include file="reportsHead.html" %>

<body onunload="clearTimeout(timerId);clearTimeout(timerIdw)" >

<%@ include file="timeout.js" %>
<%@ include file="reportsPage.js" %>


<%
if (session == null || !request.isRequestedSessionIdValid()) {


} else {

try {


response.addCookie(new Cookie("JSESSIONID", session.getId()));
User currentUser = (User)session.getAttribute("user");
Boolean isAnonymous = (Boolean)session.getAttribute("isAnonymous");
if (isAnonymous == null) isAnonymous=true;
String mode = currentUser.getMode();
int userID = currentUser.getUserID();
String userName = currentUser.getUserName();
String userType = currentUser.getType();
String memberType = currentUser.getMemberType();
String ichanged = (String)session.getAttribute("ichanged");
String googleDoc = (String)session.getAttribute("googleDoc");
String guidelines = (String)session.getAttribute("guidelinesUrl");
ReviewReport rr = (ReviewReport)session.getAttribute("report");
int reviewerID = rr.getReviewerID();
String msg = (String)session.getAttribute("message");
boolean isAllowedToEdit = currentUser.isAllowedToEdit();
boolean preCompAndbeforePR = false;
String canEditTech = new String("readOnly class=\"noeditcmt\"");
boolean showTech =false;
String displayNotes = (String)session.getAttribute("displayNotes");
if (displayNotes == null)
  displayNotes = "";
if (displayNotes.equals("on"))
  displayNotes = "checked";
// always now
displayNotes = "checked";

String displayWarn="none";

String notes = (String)session.getAttribute("notes");
if (notes == null)
  notes = "";

String displayCmtEdits = (String)session.getAttribute("displayCmtEdits");
if (displayCmtEdits == null)
  displayCmtEdits = "";
if (displayCmtEdits.equals("on"))
  displayCmtEdits = "checked";
displayCmtEdits = "checked";

String cmtEditsHistory = (String)session.getAttribute("cmtEditsHistory");
if (cmtEditsHistory == null)
  cmtEditsHistory = "";
String cmtEditsURL = "";

String cmtEdits = (String)session.getAttribute("cmtEdits");
if (cmtEdits == null)
  cmtEdits = "";
String hrlink ="<a class=\"req\" href=\"reportViewHelp.jsp\" onClick='var mywin=window.open(\"reportViewHelp.jsp#";
String hlink ="<a  class=\"noreq\" href=\"reportViewHelp.jsp\" onClick='var mywin=window.open(\"reportViewHelp.jsp#";
String hlink3 ="<a  class=\"large\" href=\"" + guidelines + "\" onClick='var mywin=window.open(\"reportViewHelp.jsp#";
String hlink2= "\",\"webreportsHelp\",\"scrollbars=yes,menubar=no,toolbar=no,location=no,resizable=yes\");mywin.focus();return false;'>";

boolean completedReport = rr.isCompleted();
boolean checkedOffReport = rr.isCheckedOff();
boolean finalizedReport = rr.isFinalized();
boolean startedLPReport = rr.startedLPReport();

//LogMessage.println("mode: " + mode + " : user type = " + userType + "  : memberType= " + memberType );

//If during peer review, display the good proposals radio buttons
Boolean beforePRBool = (Boolean)session.getAttribute("beforePR");
boolean beforePR = beforePRBool.booleanValue();

//These variables will only be used in the links displayed on the
//peer review form
String priRevReport = null;
String secRevReport = null;
String peerRevReport = null;
String priPunditReport = null;
File peerRevReportFile = null;
File priRevReportFile = null;
File secRevReportFile = null;
File priPunditReportFile = null;

if(!beforePR) {
    priRevReport = rr.getPriRevReportName();
    secRevReport = rr.getSecRevReportName();
    peerRevReport = rr.getPeerRevReportName();
    priPunditReport = rr.getPriPunditReportName();
    priRevReportFile = new File(priRevReport);
    secRevReportFile = new File(secRevReport);
    peerRevReportFile = new File(peerRevReport);
    priPunditReportFile = new File(priPunditReport);
}


String reportType = rr.getType();
String proposalNum = rr.getProposalNumber();
String panelName = currentUser.getPanelName();
String secLast = rr.getSecondaryReviewerName();
String priLast = rr.getPrimaryReviewerName();
String constrainedTargets = rr.getConstrainedTargets();

String disabled = new String("");
String noedit = new String("class=\"txtcmt\"");
String noedit1 = new String("class=\"txtcmt1\"");
String noedit2 = new String("class=\"txtcmt2\"");
String currentEditor = new String("");
String saveWarning = new String("&nbsp;");
String instrMsg = new String("");
String doneStatus = null;
String undoneStatus = null;

boolean isAllowedUncomplete = false;
Boolean editLPReportsBool = (Boolean)session.getAttribute("editLPReports");
boolean editLPReports = false;
if(editLPReportsBool != null) {
  editLPReports = editLPReportsBool.booleanValue();
}

//Chair viewing LP reports, but shouldn't be able to edit these reports
if(rr.isLP() && !editLPReports) {
  isAllowedToEdit=false;
}


//If the peer review report is being viewed by a secondary reviewer,
//disable the fields
if(rr.isSecondaryPeer()) {
  isAllowedToEdit = true;
}

// CDO can edit the tech review for the Peer Review / Final reports only
if (mode.equals(ReportsConstants.ADMIN) &&  isAllowedToEdit) {
  if (rr.isLP() || (rr.isPeer() && (rr.getProposalType().indexOf("LP") < 0) && (rr.getProposalType().indexOf("XVP") < 0)) )
    canEditTech = new String("class=\"txtcmt2\"");

}
// We don't show the tech for the primary/secondary reports
if ((rr.isPeer() || rr.isSecondaryPeer() || rr.isLP()) &&
     rr.getTechnicalReview()!= null && rr.getTechnicalReview().length() > 2 )
  showTech=true;


//If finalized 'CDO', disable fields for all except AdminEdit
if(finalizedReport) {
  if (!mode.equals(ReportsConstants.ADMIN) ) {
    isAllowedToEdit = false;
  }
}

//If checked off 'Panel', disable fields for reviewers
//if LP, disable fields for chairs
else if(checkedOffReport) {
   if (isAllowedToEdit && rr.isLP() &&
       (memberType.indexOf(ReportsConstants.PUNDIT) >= 0)) {
        // LP report from BPP so PUNDITS can uncomplete
        isAllowedUncomplete = true;
        doneStatus=ReportsConstants.CHECKOFF;  //Panel->Panel
        undoneStatus=ReportsConstants.SAVE;    //Panel->date
   }
   else if (isAllowedToEdit &&
       (mode.equals(ReportsConstants.CHAIR) && !rr.isLP()) ) {
        // Not an LP report, so Chairs can reset it back to Reviewer
        isAllowedUncomplete = true;
        doneStatus=ReportsConstants.CHECKOFF;   //Panel->Panel
        undoneStatus=ReportsConstants.COMPLETE; //Panel->Reviewer
   }

   // can't edit until they reset status
   if(mode.equals(ReportsConstants.REVIEWER) ||
      mode.equals(ReportsConstants.CHAIR) ||
      memberType.indexOf(ReportsConstants.PUNDIT) >= 0) {
      isAllowedToEdit = false;
   }
}

else if(completedReport) {
   if(mode.equals(ReportsConstants.REVIEWER) ||
      (memberType.indexOf(ReportsConstants.PUNDIT) < 0 && mode.equals(ReportsConstants.CHAIR) && rr.isLP())) {
      if (isAllowedToEdit) {
        isAllowedUncomplete = true;
      }
      isAllowedToEdit = false;
      doneStatus=ReportsConstants.COMPLETE; //need to uncomplete first
      undoneStatus=ReportsConstants.SAVE;   //Reviewer->date
   }
   // is this true if editing own reports and not approving?
   else if (mode.equals(ReportsConstants.CHAIR) &&!rr.isLP() ||
      (memberType.indexOf(ReportsConstants.PUNDIT) >= 0 && rr.isLP())) {
      if (isAllowedToEdit) {
        isAllowedUncomplete = true;
      }
      doneStatus=ReportsConstants.CHECKOFF;  //Reviewer->Panel
      undoneStatus=ReportsConstants.SAVE;    //Reviewer->date
   }
}
else  {
   if (memberType.indexOf(ReportsConstants.PUNDIT) >= 0 ||
       (mode.equals(ReportsConstants.CHAIR) && !rr.isLP()) ) {
      doneStatus = ReportsConstants.CHECKOFF;  //Date->Panel
   }
   else {
      doneStatus = ReportsConstants.COMPLETE;  //Date->Reviewer
   }

}

// read only if prelim is completed
if (beforePR && rr.getPreComp().equalsIgnoreCase("true")){
    preCompAndbeforePR = true;
    isAllowedToEdit = false;
}

if(!isAllowedToEdit) {
  // Secondary should see report but not edit buttons + disabled edit buttons if preComp
  if (!rr.isSecondary() || ( rr.isSecondary() && preCompAndbeforePR ) ){
      disabled = new String("disabled");
  }
  noedit = new String("readOnly class=\"noeditcmt\"");
  noedit1 = new String("readOnly class=\"noeditcmt\"");
  noedit2 = new String("readOnly class=\"noeditcmt\"");
}

String glink = "<a class=\"xlargebc\" target=\"_blank\" onClick='var mywin=window.open(\"" +
        googleDoc +", \"googleDoc\",\"scrollbars=yes,menubar=no,toolbar=no,location=no,resizable=yes\");mywin.focus();return false;' href=";
String glinkComplete = glink +googleDoc +">Click to edit Report comments</a>";
String glinkUncomplete = glink + googleDoc + ">Click to view Report comments</a>";
String gMsg = "&nbsp;" + glinkComplete+ "<br>";

boolean allButtonsDisabled = false;
if(disabled.length() > 0) {
  allButtonsDisabled = true;
//admin and adminedit should see link, but admin shouldn't be allowed to complete/uncomplete LP report
  if (isAllowedUncomplete || (!beforePR && mode.equals(ReportsConstants.ADMIN))) {
      instrMsg = "&nbsp;" + glinkUncomplete+ "<br>" +
              "<ul>"+
              "<li><span class=\"large\">Report is currently not editable.</span></li>";
      if (!rr.isSecondaryPeer()) {
          instrMsg +=
                  "<li><span class=\"large\">If you need to edit this report, please click the <b>"
                          + ReportsConstants.UNAPPLABEL + "</b> button.</span></li>";
      }
      instrMsg += "</ul>";
  }
}
else {
  if (!beforePR) {
    if (isAllowedToEdit) {
        instrMsg = "&nbsp;" +
                 hlink3+ "is" + hlink2+"View guidelines for filling out report." +"</a>"+
                "<br><span class=\"large\"><b>Degree of effort</b>  field is required and will only be saved after clicking the <b>Completed</b> button.</span>"+
                "<br><span class=\"large\">Click the <b>" + ReportsConstants.APPBTNLABEL + "</b> button when you have finished editing your report.</span>";
    }
  }
  else {
    instrMsg = "<span class=\"large\">Click the <b>" + ReportsConstants.APPBTNLABEL + "</b> button when you have finished editing your report.</span>";
    instrMsg += "<br><span class=\"noticeme\">SAVE your report often!</span>" ;
  }
}


if(rr.isSecondaryPeer()) {
  reportType = ReportsConstants.PEER;
}


String fileLink = new String("");
Vector proposalFiles = rr.getProposalFiles();
int fidx=0;
if (proposalFiles != null) {
    Iterator pfi = proposalFiles.iterator();
    while ( pfi.hasNext()  ) {
       ProposalFile pf = (ProposalFile)pfi.next();
       String tfile ="displayFile.jsp?fileName=";
       tfile += pf.getFileName();
       tfile = response.encodeURL(tfile);
       String techLink = "&nbsp;<a href=\"" + tfile;
       String fType = pf.getFileType().replaceAll(" ","&nbsp;");

       techLink += "\" target=\"_blank\"  >" + fType + "</a>&nbsp;&nbsp;";

       fidx=  fidx + 1;
       if (fidx > 2) {
         fileLink += "<br>";
         fidx=1;
       }
       fileLink += techLink;
    }
}


String idPar = new String("userID="+ userID);
String namePar = new String("userName=" + userName);
String typePar = new String("userType=" + userType);

Vector lpReports = rr.getLPPeerReportNames();
Vector lpPanels = rr.getExtraPanels();
String reviewLink = new String("&nbsp;");
String secLink = new String("&nbsp;");
String thirdLink = new String("&nbsp;");
String punditLink = new String("");
String theStatus=rr.getReportStatus();
String propType = "";
String multicycle = "N";

propType = rr.getProposalType();

if (rr.getProposalMulticycle() != null && rr.getProposalMulticycle().equalsIgnoreCase("Y")) {
  propType += "&nbsp;&nbsp; Multicycle";
  multicycle = "Y";
}


if(!beforePR && !rr.isLP()) {
  if (priRevReportFile != null && priRevReportFile.exists()) {
     String pfile = priRevReport.substring(priRevReport.lastIndexOf('/'));
     String tmpStr = "displayPrintVersion.jsp?fileName=";
     tmpStr += pfile;
     tmpStr = response.encodeURL(tmpStr);
     reviewLink = "<a href=\"" + tmpStr;
     reviewLink += "\" target=\"_blank\"> Primary report <i>" + priLast +"</i></a>";

  }
  else {
     reviewLink ="No primary report <i>" + priLast + "</i>";
  }
  if ( secRevReportFile != null && secRevReportFile.exists()) {
     String pfile = secRevReport.substring(secRevReport.lastIndexOf('/'));
     String tmpStr = "displayPrintVersion.jsp?fileName=";
     tmpStr += pfile;
     tmpStr = response.encodeURL(tmpStr);
     secLink = "<a href=\"" + tmpStr;
     secLink += "\" target=\"_blank\"> Secondary report <i>" + secLast + "</i></a>";
  }
  else {
     secLink ="No secondary report <i>" + secLast + "</i>";

  }
}
if(!beforePR ) {
  if ( priPunditReportFile != null && priPunditReportFile.exists()) {
     String pfile = priPunditReport.substring(priPunditReport.lastIndexOf('/'));
     String tmpStr = "displayPrintVersion.jsp?fileName=";
     tmpStr += pfile;
     tmpStr = response.encodeURL(tmpStr);
     punditLink = "<tr><td class=\"nowrap\"><a href=\"" + tmpStr;
     punditLink += "\" target=\"_blank\"> Pundit Preliminary report</a> " +  "</td></tr>";
  }
}
if(!beforePR && rr.isLP() ) {
  if (lpReports != null && lpReports.size() > 0) {
    Date fileDate = null;
    String datePattern = new String("dd MMM HH:mm");
    SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
    String thisPanel = new String ("");

    peerRevReportFile = new File ((String)lpReports.get(0));
    thisPanel = (String)lpPanels.get(0);
    if (peerRevReportFile != null && peerRevReportFile.exists()) {
      String tmpStr = "displayPrintVersion.jsp?fileName=";
      String pf = (String)lpReports.get(0);
      String pfile = pf.substring(pf.lastIndexOf('/'));
      tmpStr +=  pfile;
      tmpStr = response.encodeURL(tmpStr);
      reviewLink = "<a href=\"" + tmpStr;
      reviewLink += "\" target=\"_blank\">View Panel " + thisPanel + " peer review report</a>";
      fileDate = new Date(peerRevReportFile.lastModified());
      String fclass = "lastMod" ;
      if (rr.isOlder(fileDate)) {
         fclass = "lastModRed";
      }
      reviewLink += "<br><span class=\"" + fclass + "\" >Last Modified: " + sdf.format(fileDate) + "</span>";
    }
    else {
      reviewLink = "No peer review report available for panel " + thisPanel;
    }

    if (lpReports.size() > 1) {
      thisPanel = (String)lpPanels.get(1);
      peerRevReportFile = new File ((String)lpReports.get(1));
      if (peerRevReportFile != null && peerRevReportFile.exists()) {
        String pf = (String)lpReports.get(1);
        String pfile = pf.substring(pf.lastIndexOf('/'));
        String tmpStr = "displayPrintVersion.jsp?fileName=";
        tmpStr += (String)pfile;
        tmpStr = response.encodeURL(tmpStr);
        secLink = "<a href=\"" + tmpStr;
        secLink += "\" target=\"_blank\">View Panel " + thisPanel + " peer review report</a>";
        fileDate = new Date(peerRevReportFile.lastModified());
        String fclass = "lastMod" ;
        if (rr.isOlder(fileDate)) {
           fclass = "lastModRed";
        }
        secLink += "<br><span class=\"" + fclass + "\" >Last Modified: " + sdf.format(fileDate) + "</span>";
      }
      else {
        secLink = "No peer review report available for panel " + thisPanel;
      }
    }
    if (lpReports.size() > 2) {
      thisPanel = (String)lpPanels.get(2);
      peerRevReportFile = new File ((String)lpReports.get(2));
      if (peerRevReportFile != null && peerRevReportFile.exists()) {
        String pf = (String)lpReports.get(2);
        String pfile = pf.substring(pf.lastIndexOf('/'));
        String tmpStr = "displayPrintVersion.jsp?fileName=";
        tmpStr += (String)pfile;
        tmpStr = response.encodeURL(tmpStr);
        thirdLink = "<a href=\"" + tmpStr;
        thirdLink += "\" target=\"_blank\">View Panel " + thisPanel + " peer review report</a>";
        fileDate = new Date(peerRevReportFile.lastModified());
        String fclass = "lastMod" ;
        if (rr.isOlder(fileDate)) {
           fclass = "lastModRed";
        }
        thirdLink += "<br><span class=\"" + fclass + "\" >Last Modified: " + sdf.format(fileDate) + "</span>";
      }
      else {
        thirdLink = "No peer review report available for panel " + thisPanel;
      }
    }
  }
}

//Display a link to the timed out report, if it exists
String timedOutLink = new String();
if(rr.timedOutFileExists()) {
  String tf = rr.getTimedOutFilename();
  String tmpStr = "displayPrintVersion.jsp?fileName=";
  String pfile = tf.substring(tf.lastIndexOf('/'));
  tmpStr += pfile;
  tmpStr = response.encodeURL(tmpStr);
  timedOutLink = "<a href=\"" + tmpStr;
  timedOutLink += "\" target=\"_blank\">View the timed out report</a>";
}

String rptHeader = "Chandra "  + reportType;
if (reportType.equals(ReportsConstants.LP)) {
  rptHeader = ReportsConstants.BPP;
}
%>
<form name="exitForm" method="POST" action="/reports/reportsLogout" target="_self">
<div id="cxcheaderplain">
<div class="menudiv">
<div class="hdrleft">
<a href="/" target="_top"><img src="cxcheaderlogo.png" alt="CXC Home Page" border="0"></a>
</div>
<div class="hdrcenter">
<img src="blank.gif" alt="" width="1" height="50">
<span class="mainhdr"><%= rptHeader %> Review Report</span>
<br>
<% if(rr.isLP()) { %>
<span class="mainhdr3">Proposal <%= proposalNum %> </span>
<% } else { %>
<span class="mainhdr3">Proposal:<%= proposalNum %> &nbsp;&nbsp; Panel:<%= panelName %> </span>
<% } %>
</div>
</div>
</div>
</form>


<form name="theform" method="POST" target="_self" action="/reports/updateReport">
<input type="hidden" name="operation" value = "NONE">
<input type="hidden" name="userID" value = "<%=userID%>" >
<input type="hidden" name="userMode" value = "<%=mode%>" >
<input type="hidden" name="userType" value = "<%=userType%>" >
<input type="hidden" name="userName" value = "<%=userName%>" >
<input type="hidden" name="reviewerID" value="<%=reviewerID%>" >

<input type="hidden" name="panelName" value = "<%=panelName%>" >
<input type="hidden" name="reportType" value = "<%=rr.getType()%>" >
<input type="hidden" name="propNum" value ="<%=proposalNum%>" >
<input type="hidden" name="category" value ="<%= rr.getCategory() %>" >
<% if (!isAnonymous) { %>
<input type="hidden" name="piName" value ="<%= rr.getPI() %>" >
<% } %>
<input type="hidden" name="title" value ="<%= rr.getTitle() %>" >
<input type="hidden" name="proposalType" value ="<%= rr.getProposalType() %>" >
<input type="hidden" name="constrainedTargs" value ="<%= constrainedTargets %>" >
<input type="hidden" name="multicycle" value ="<%= multicycle %>" >
<input type="hidden" name="joint" value ="<%= rr.getProposalJoint() %>" >
<input type="hidden" name="disabled" value ="<%= disabled %>" >
<input type="hidden" name="priLast" value ="<%= priLast %>" >
<input type="hidden" name="secLast" value ="<%= secLast %>" >
<input type="hidden" name="ichanged" value ="<%= ichanged %>" >

<table width="99%">
<tr>
<td align="left"><img src="arrow-left.png" alt=" " class='backimg' onmouseover="this.className='backactv';" onmouseout="this.className='backimg';" onclick='theform.target="_self";theform.action="/reports/updateReport";theform.operation.value="<%=ReportsConstants.LISTPROPOSALS%>";theform.submit();'>
<input class="backBtn" type="button" value="Back to list of proposals"
onclick='if (checkReportChange()) { theform.target="_self";theform.action="/reports/updateReport";theform.operation.value="<%=ReportsConstants.LISTPROPOSALS%>";this.form.submit();}'>
</td>
<td align="right">
<input type="button" class="linkBtn" onClick='var mywin=window.open("reportViewHelp.jsp","webreportsHelp","scrollbars=yes,menubar=no,toolbar=no,location=no,resizable=yes");mywin.focus();' value="Help">
</td>
</tr>
</table>

    <div class="infobar">
        <table border="0" cellspacing="0" cellpadding="0" width="98%">
            <tr>
                <td width="80%">
                    <table>
                        <tr>
                            <th class="left">Proposal Title:</th>
                            <td> <%= StringEscapeUtils.escapeHtml4(rr.getTitle()) %> </td>
                        </tr>

                        <tr>
                            <th class="left">Subject Category:</th>
                            <td> <%= rr.getCategory() %> </td>
                        </tr>

                        <% if (!isAnonymous) { %>
                        <tr>
                            <th class="left">P.I. Name:</th>
                            <td> <%= StringEscapeUtils.escapeHtml4(rr.getPI()) %> </td>
                        </tr>
                        <% } %>

                        <tr>
                            <th class="left">Type:</th>
                            <td> <%= propType %> </td>
                        </tr>
                        <tr>
                            <th class="left">Constrained&nbsp;Targets:&nbsp;&nbsp;</th>
                            <td> <%= constrainedTargets %></td>
                        </tr>
                        <tr>
                            <th class="left">Joint:</th>
                            <td><%=rr.getProposalJoint()%></td>
                        </tr>
                    </table>
                </td>
                <td valign="top">
                    <table border="0">
                        <tr valign="top">
                            <td valign="top" >
                                View: <%=fileLink%>
                            </td>
                        </tr>
                        <tr><td>&nbsp;</td></tr>
                        <tr>
                            <td class="nowrap"><%= reviewLink %></td>
                        </tr><tr>
                        <td class="nowrap"> <%= secLink %> </td>
                    </tr><tr>
                        <td ><%= thirdLink %> <%= timedOutLink %> </td>
                    </tr>
                        <%= punditLink %>
                    </table>
                </td>
            </tr>
        </table>
    </div>
<br>

    <%
        Vector dropMenuOptions = new Vector();
        dropMenuOptions.addElement(ReportsConstants.NONE);
        dropMenuOptions.addElement(ReportsConstants.TOPS);
        dropMenuOptions.addElement(ReportsConstants.GOOD);
        dropMenuOptions.addElement(ReportsConstants.AVERAGE);
        dropMenuOptions.addElement(ReportsConstants.BELOW);

        Vector dropMenuOptions2 = new Vector();
        dropMenuOptions2.addElement(ReportsConstants.NA);
        dropMenuOptions2.addElement(ReportsConstants.TOPS);
        dropMenuOptions2.addElement(ReportsConstants.GOOD);
        dropMenuOptions2.addElement(ReportsConstants.AVERAGE);
        dropMenuOptions2.addElement(ReportsConstants.BELOW);

        Vector fmtMenuOptions = new Vector();
        fmtMenuOptions.addElement(ReportsConstants.NONE);
        fmtMenuOptions.addElement("Easy to Read");
        fmtMenuOptions.addElement("Needs Improvement");
        fmtMenuOptions.addElement("Difficult to Read");
    %>


<% String buttonsLocation = new String("top"); %>
<% if (msg != null && msg.length() > 0) { %>
<i><%= msg %></i><br>
<% } %>
<% if (instrMsg != null && instrMsg.length() > 0) { %>
<div class="instructspanbar">
<br>
<%= instrMsg %>
<%
if (startedLPReport && !reportType.equals(ReportsConstants.LP)) {
%>
<p><span class="noticeme">Warning: </span>
The LP/VLP Report for this proposal has already been created.
Please notify the Chair/Deputy Chair if you are making changes to this report.
<% } %>
<%--Google Doc link box--%>

</div>
<% if (!beforePR && isAllowedToEdit) { %>
    <br>
    <div class="instructspanbar">
        <%= gMsg %>
    </div>
<% } %>
<%= fieldsetTimeout %>
<% } else { %>
<%= fieldsetTimeout %>
<% } %>

<%--Degree of effort box--%>
<% if(!rr.isSecondaryPeer()) { %>
    <table border="0">
        <tr class="report">

            <%
                int index=0;
                int menuSize = dropMenuOptions.size();
            %>

            <td class="nowrap" ><%=hrlink%>der<%=hlink2%><%=ReportsConstants.EFFORT%></a><br><%=ReportsConstants.EFFORT2%>:</td>
            <td class="report"><select name="effort"  onChange="this.form.ichanged.value='1';" <%=disabled%>>
                <%
                    Vector effortOptions = new Vector();
                    effortOptions.addElement(ReportsConstants.NONE);
                    effortOptions.addElement(ReportsConstants.EASY);
                    effortOptions.addElement(ReportsConstants.AVERAGE);
                    effortOptions.addElement(ReportsConstants.ABOVE);
                    String selectedChoice = "";
                    String rrEffort = rr.getEffort();
                    for(index=0; index < effortOptions.size(); index++) {
                        String currentChoice = (String)effortOptions.get(index);
                        if(currentChoice.equals(rrEffort)) {
                            selectedChoice = currentChoice;
                %>
                <option value="<%=currentChoice %>" selected> <%= currentChoice %>

                        <% } else { %>
                <option value="<%= currentChoice %>"> <%= currentChoice %>

                        <% }
    }
    %>
            </select>
            </td>
            <%-- To users, Prelim Complete/Uncomplete button looks like standard Complete/Uncomplete --%>
            <%-- but both really just submit SAVE operation. When effort is disabled, the value doesn't get submitted  --%>
            <%-- so it winds up getting saved in UpdateReportServlet as "". For Peer Uncomplete, --%>
            <%-- loadReport is called to populate effort, but don't want to do that when just saving. --%>
            <%-- Sooo, to get the correct choice to display AND submit even when disabled, add this hidden field with same name --%>
            <input type="hidden" name="effort" value ="<%= selectedChoice %>" >
            <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
        </tr></table>
    <% }%>
<%--Save/completed/uncompleted button--%>
<p>
<%@ include file = "modeButtons.jsp" %>
<%
  String fieldsetNotes;
  if (displayNotes.equals("checked") || displayNotes.equals("on"))
    fieldsetNotes = "<fieldset id=\"fieldset_Notes\" style=\"display:block;border:0\">";
  else
    fieldsetNotes = "<fieldset id=\"fieldset_Notes\" style=\"display:none;border:0\">";
  String fieldsetCmtEdits;
  if (displayCmtEdits.equals("checked") || displayCmtEdits.equals("on"))
    fieldsetCmtEdits = "<fieldset id=\"fieldset_CmtEdits\" style=\"display:block;border:0\">";
  else
    fieldsetCmtEdits = "<fieldset id=\"fieldset_CmtEdits\" style=\"display:none;border:0\">";

  if (!beforePR) {
    if (cmtEditsHistory != null && !cmtEditsHistory.equals(""))
       cmtEditsURL = "<i>Last Modified: " +  cmtEditsHistory + "</i>";
    // if (rr.isNewerCmt()) {
     // cmtEditsURL += "<img src='gold.gif' alt='new'>";
    //}

%>
<% } %>


<div style="margin-left:5px;">

<% if(beforePR) { %>
<%=hlink%>comments<%=hlink2%>Comments</a> (Brief description of strengths and weaknesses including reasons why the grade was not higher - if applicable):<br>
&nbsp;&nbsp;<textarea rows="15" cols="80" id="comments1" name="comments"  <%=noedit%> onChange="this.form.ichanged.value='1';" ><%= rr.getComments() %></textarea>

<p>
<%=hlink%>srct<%=hlink2%>If accepted, enter specific recommendations concerning targets, time, observing conditions</a>:<br>
&nbsp;&nbsp;<textarea rows="5" cols="80" id="specificRecs" name="specificRecs" <%=noedit1%> onChange="this.form.ichanged.value='1';" ><%= rr.getRecs() %></textarea>
<% } %>
<p>

<% if (showTech) { %>
Technical Review:<br>
&nbsp;&nbsp;<textarea rows="20" cols="80" id="technicalReview" name="technicalReview" <%=canEditTech%> ><%= rr.getTechnicalReview() %>
</textarea>
<% } %>
<!--
<%=hlink%>srgnh<%=hlink2%>Specify reason why the grade was not higher. Optional if proposal is accepted.</a>:<br>
&nbsp;&nbsp;<textarea rows="20" cols="80" id="whyGradeNotHigher" name="whyGradeNotHigher" <%=noedit2%> ><%= rr.getGradeReason() %>
</textarea>
-->
</div>

<p>
<br>
<% if (beforePR && !saveButton.equals("")) { %>
<span class="btnDiv">
<%= saveButton %>
</span>
<% } %>
</form>
<p>

<%@ include file = "footer.html" %>

<% } catch (Exception e) {};
 } %>

  </body>
</html>


