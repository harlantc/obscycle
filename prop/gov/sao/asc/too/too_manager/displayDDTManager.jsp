<%@ page session="true" import="java.text.* , java.util.* , info.*" %>
<% 
    Vector ddtList = (Vector)session.getAttribute("ddtList");
    String message = (String)session.getAttribute("ddtmessage");
    String mgrMsg = (String)session.getAttribute("mgrMsg");
    String pocStr = (String)session.getAttribute("mpPOC");
    String tooLink = (String)session.getAttribute("tooLink");
    String searchStr = (String)session.getAttribute("ddtsearchStr");
    String statStr = (String)session.getAttribute("ddtstatStr");
    String calFrom = (String)session.getAttribute("ddtcalFrom");
    String calTo = (String)session.getAttribute("ddtcalTo");
    Vector aos= (Vector)session.getAttribute("ddtAO");
    Vector ao_totals = (Vector)session.getAttribute("ddtAOTotals");
    response.addCookie(new Cookie("JSESSIONID", session.getId()));
    Vector coordinatorList = (Vector)session.getAttribute("coordinatorList");
    String msgclass = (String)session.getAttribute("ddtmsgClass");

    if (calFrom == null) calFrom="";
    if (calTo == null) calTo="";
    if (msgclass == null) msgclass = "msg";
    if (mgrMsg == null) mgrMsg = "";
    if (aos == null) aos = new Vector();

    String aoOptions = "";
   
    for (int ii=0; ii < aos.size();ii++) {
        String aoStr = (String)aos.get(ii);
        String selStr = "";
        if (searchStr != null && searchStr.indexOf(aoStr) >= 0) {
          selStr = "selected";
        }
        aoOptions += "<option value='" + aoStr + "' " + selStr + " >" + aoStr + "</option>";
          
    }
   
    String statOptions = "";
    String[] statArray = { "First Notification","Acknowledged","Approved","Not Approved","Request Withdrawn" };
    String[] statLbls = {"First Notification","Acknowledged","Approved","Not Approved","Withdrawn"};
    for (int ss=0; ss< statArray.length;ss++) {
      String selStr = "";
      if (statStr != null && statStr.indexOf("'" + statArray[ss] + "'") >= 0) {
        selStr = "selected";
      }
      statOptions += "<option value='" + statArray[ss] + "' " + selStr + " >" + statLbls[ss];
    }

    // show all cycles and totals for the current display option
    String totals_stats= "<tr><td>Retrieved</td>";
    String reqCol="<td>unknown</td>";
    String appCol="<td>unknown</td>";
    try {
      Double curReqTime = new Double(0.0);  
      Double curAppTime = new Double(0.0);
      for (int ii=0; ii< ddtList.size(); ii++) {
        DDTEntry ddt = (DDTEntry)ddtList.get(ii);
        curReqTime += ddt.getRequestedTime();
        curAppTime += ddt.getApprovedTime();
      }
      NumberFormat nf = NumberFormat.getInstance();
      nf.setMaximumFractionDigits(2);
      nf.setMinimumFractionDigits(2);
      nf.setGroupingUsed(false);
      reqCol = "<td class='right'>" + nf.format(curReqTime) + "</td>";
      appCol = "<td class='right'>" + nf.format(curAppTime) + "</td></tr>";

    } catch (Exception exc) {
    }
    totals_stats += reqCol + appCol;
    for (int ii=0; ii< ao_totals.size(); ii++) {
       AOCycle ele= (AOCycle)ao_totals.get(ii);
       totals_stats += "<tr><td>" + ele.getAOCycle() + "</td>";
       totals_stats += "<td class='right'>" + ele.getTotalRequestedTimeStr() + "</td>";
       totals_stats += "<td class='right'>" + ele.getTotalApprovedTimeStr() + "</td></tr>";
    }
%>
<%@ include file = "ddtManagerHead.jsp" %>
<body class="body" onload="doOnload();" >
	<style>
		#calendar,
		#calendar2 {
			border: 1px solid #dfdfdf;
			font-size: 14px;
			color: #404040;
		}
	</style>
<script type="text/javascript">
var calFrom,calTo;

$(document).ready(function() {
  $("#ddtTable").tablesorter( {
        widgets: ["saveSort","zebra"],
        emptyTo: "none",
	headers: {
	  5: {sorter: 'select' }
        }
        });
});

function doOnload()
{
  var d1 = "<%=calFrom%>";
  var d2 = "<%=calTo%>";
  var useFormat = "%M %d %Y";
  calFrom = new dhtmlXCalendarObject("cal_from");
  calTo = new dhtmlXCalendarObject("cal_to");
  calFrom.setDateFormat(useFormat);
  calTo.setDateFormat(useFormat);
  calFrom.hideTime();
  calTo.hideTime();
  if (d1 != "") {
    calFrom.setDate(d1);
    document.getElementById("cal_from").value =d1;
  }
  if (d2 != "") {
    calTo.setDate(d2);
    document.getElementById("cal_to").value =d2;
  }

}

</script>
<script type="text/javascript" src="toomanager.js"></script>

<div class="hdrleft">
<img src="header_left.gif" alt="Chandra Science">
</div>
<div class="hdrcenter">
<font class="biggerLabel">
Chandra DDT Manager
</font>
<p>
<%@ include file = "tooManagerLinks.jsp" %>
</div>
<div class="hdrright">
<a href="/toomanager/logout">Logout</a>
<p>
<a href="/toomanager/tooManager.jsp?operation=<%=tooLink%>">TOO Manager</a>
<br>
<a href="https://icxc.harvard.edu/uspp/">Internal CDO Site</a>
<br>
<a href="https://icxc.harvard.edu/mta/CUS/Usint/obsid_usint_list.cgi">USINT</a>
</div>
<div style="clear:both;">
<%=mgrMsg%>
<form name="goto" method="post" action="/toomanager/ddtManager.jsp">

<div class="searchdiv">
<div class="searchstat">
<table id="statstable" width="95%" border="1">
<tr>
<th>Cycle</th><th>ReqTime</th><th>AppTime</th>
</tr>
<%=totals_stats%>
</table>
</div>
<div class="searchbar">
<input type="hidden" name="operation" value="">
<input type="hidden" name="ordr" value="subdate">
<table cellpadding="0" cellspacing="5" >
<tr>
<td><span style="font-weight:bold">Cycle:</span>
<br>
<select id="cycle" name="cycle" size="4" multiple>
<%= aoOptions %>
</select>
</td>
<td width="10">&nbsp;</td>
<td><span style="font-weight:bold">Status:</span>
<br><select size="4" name="searchstat" multiple>
<%= statOptions %>
</select>
</td>
<td width="10">&nbsp;</td>
<td><span style="font-weight:bold">Submission Date:</span>
<br>
<div style="position:relative;height:auto;overflow:visible;">
<table border="0">
<tr><td>From:</td>
<td><input type="text" id="cal_from" name="cal_from" size="15"> </td>
</tr>
<tr><td>To:</td>
<td> <input type="text" id="cal_to" name="cal_to" size="15"> </td>
</tr></table>
</div>
</td>
<td width="25">&nbsp;</td>
<td>
<table cellspacing="5" class="searchBtn">
<tr >
<td> <input type="submit" class="subbtn" name="Submit" value="Search" onclick="this.form.operation.value='Search';localStorage.removeItem('tablesorter-savesort');return true; " ></td>
<td> <input type="reset"  class="subbtn" name="Reset" value="Reset" ></td>
<td> <input type="submit"  class="subbtn" name="Clear" value="Clear" onclick='clearFields(this.form);return false;'></td>
</tr> </table>
</td> </tr>
</table>
</div>
</div>
<br>
<div style="position:relative;clear:both;width:95%; margin-left:5px;">
<div style="width:95%">
<span style="float:left;">
<font class="<%= msgclass %>"> <%= message %></font>
</span>
</div>
</div>
<input type="hidden" name="proposalID" value="" >
<input type="hidden" name="hiddenCoordinator" value="" >
<table  class="tablesorter" id="ddtTable" width="95%" border="1" cellpadding="0">
<caption style="width:95%;" >
<span style="float:right;text-align:right;">
<img src="blank.gif" alt="  " style="width:10px;height:10px;background-color:#ff0000;">Need Response&nbsp;&nbsp;
<img src="blank.gif" alt="   " style="width:10px;height:10px;background-color:#99ffff;">Migrate to Obscat&nbsp;&nbsp;
<img src="blank.gif" alt="   " style="width:10px;height:10px;background-color:#ffff00;">In-progress
</span>
</caption>
<thead>
<tr>
<th >Proposal#</th>
<th >Submission Date </th>
<th >PI </th>
<th >Proposal Status</th>
<th >DDT Status</th>
<th >Coordinator</th>
<th ><a class="hdrlink" href="https://icxc.harvard.edu/soft/UDFs/UDF/ObsCycle/Proposals/RPS/ddt.html#paging">Page Type</a></th>
<th >ReqTime</th>
<th >AppTime</th>
</tr>
</thead>
<tbody>
<%  
   for (int ii=0; ii< ddtList.size(); ii++) {
     DDTEntry ddt = (DDTEntry)ddtList.get(ii);
     String status = ddt.getStatus();
     String propstatus = ddt.getProposalStatus();
     String coordinator = ddt.getCoordinator();
     String coordinatorStr;
     String statClass = "list";
     String pstatClass = "list";
     boolean didit = false;
     if (propstatus != null && propstatus.equals("APPROVED") && 
	ddt.getOCatID().intValue() > 0) {
        propstatus="<a href=\"ddtOCat.jsp?prop=" + ddt.getProposalNumber();
        propstatus += "\">APPROVED</a>";
     }
     
     if (status == null || status.length()==0) {
       status = "&nbsp;";
     }
     if (status.indexOf(TriggerTooConstants.PENDING) >= 0) {
         statClass = "error";
     }
     if (status.equals(TriggerTooConstants.APPROVED) &&
         !ddt.inObsCat()) {
         pstatClass = "actreq";
     }
     else if( propstatus.indexOf("PROPOSED") >= 0) {
         pstatClass = "warn";
     }
%>
<%
     if (coordinator == null) {
       coordinator = new String("");
     }
     coordinatorStr = "<select name='Coordinator' onchange=\"if (verifyChange(this)) {this.form.proposalID.value='";
     coordinatorStr += ddt.getProposalID().toString();
     coordinatorStr += "';this.form.submit();} else { this.form.reset(); } \">";
     coordinatorStr += "<option value=''>&nbsp;";
     for (int cidx=0;cidx<coordinatorList.size();cidx++) {
       coordinatorStr += "<option ";
       if (coordinator.equals((String)coordinatorList.get(cidx))) {
         coordinatorStr += "selected";
         didit = true;
       }
       coordinatorStr += ">";
       coordinatorStr += (String)coordinatorList.get(cidx);
     }
     if (!didit && coordinator.length() > 0) {
       coordinatorStr += "<option selected>";
       coordinatorStr += coordinator;
     }
     coordinatorStr += "</select>";
    
%>
<tr >
<td class="list"><a href="/toomanager/ddtUpdate.jsp?proposalID=<%= ddt.getProposalID() %>"><%= ddt.getProposalNumber() %></a></td>
<td class="list"><%= ddt.getSubmissionDate() %></td>
<td class="list"><%= ddt.getPI() %></td>
<td class="<%=pstatClass%>" ><%= propstatus %></td>
<td class="<%=statClass%>" ><%= status %></td>
<td class="list"><%= coordinatorStr %></td>
<td class="list"><%= ddt.getUrgency() %></td>
<td class="list"><%= ddt.getRequestedTimeStr() %></td>
<% if (ddt.getApprovedTime() > 0.0) { %>
<td class="list"><%= ddt.getApprovedTimeStr()%></td>
<% } else { %>
<td class="list">&nbsp;</td>
<% } %>
</tr>
<%
  } 
%>
</tbody>
</table>
<p>&nbsp;
</form>
</div>
</body>
</html>
