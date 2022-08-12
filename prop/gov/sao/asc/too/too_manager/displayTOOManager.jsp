<%@ page session="true" import="java.text.* , java.util.* , info.*" %>
<% 
    Vector tooList = (Vector)session.getAttribute("tooList");
    String message = (String)session.getAttribute("message");
    String mgrMsg = (String)session.getAttribute("mgrMsg");
    String pocStr = (String)session.getAttribute("mpPOC");
    String ddtLink = (String)session.getAttribute("ddtLink");
    Vector aos = (Vector)session.getAttribute("maxAO");
    response.addCookie(new Cookie("JSESSIONID", session.getId()));
    Vector coordinatorList = (Vector)session.getAttribute("coordinatorList");
    String msgclass = (String)session.getAttribute("msgClass");
    if (message == null) {
      message = "";
    }
    if (mgrMsg == null) {
      mgrMsg = "";
    }
    if (msgclass == null) {
      msgclass = "msg";
    }
    String searchStr = (String)session.getAttribute("searchStr");
    String orderStr = (String)session.getAttribute("sortStr");

    String aoOptions = "";
    for (int ii=0;aos!= null && ii<aos.size(); ii++) {
        String aoStr = (String)aos.get(ii);
        String selStr = "";
        if (searchStr != null && searchStr.indexOf(aoStr) >= 0) {
          selStr = "selected";
        }
        aoOptions += "<option value='" + aoStr + "' " + selStr + " >" + aoStr;

    }

    String sortOptions = "";
    String[] sortArray = {"subdate","seqnum", "obsid","pi","status"};
    String[] sortLbls = {"Submission Date","SeqNum","ObsID","P.I.","Trigger Status"  };
    for (int ss=0; ss< sortArray.length;ss++) {
      String selStr = "";
      if (orderStr != null && orderStr.indexOf(sortArray[ss]) >= 0) {
        selStr = "selected";
      }
      sortOptions += "<option value='" + sortArray[ss] + "' " + selStr + " >" + sortLbls[ss];
    }

     
%>
<%@ include file = "tooManagerHead.jsp" %>
<body class="body" >
<script type="text/javascript" src="toomanager.js"></script>
<script type="text/javascript">
$(document).ready(function() {
  $("#tooTable").tablesorter( {
        widgets: ["saveSort","zebra"],
        emptyTo: "none",
        headers: {
          7: {sorter: 'select' }
        }
        });
});
</script>

<div class="hdrleft">
<img src="header_left.gif" alt="Chandra Science">
</div>
<div class="hdrcenter">
<font class="biggerLabel">
Chandra TOO Manager
</font>
<p>
<%@ include file = "tooManagerLinks.jsp" %>
</div>
<div class="hdrright">
<a href="/toomanager/logout">Logout</a>
<p>
<a href="/toomanager/ddtManager.jsp?operation=<%=ddtLink%>">DDT Manager</a>
<br>
<a href="https://icxc.harvard.edu/uspp/">Internal CDO Site</a>
<br>
<a href="https://icxc.harvard.edu/mta/CUS/Usint/obsid_usint_list.cgi">USINT</a>
</div>
<div style="clear:both;">
<p>
<%=mgrMsg%>
<form name="goto" method="post" action="/toomanager/tooManager.jsp">
<div class="searchbar">
<center>
<table cellpadding="0" cellspacing="5" >
<!--<caption><font class="bigLabel">Search Criteria</font></caption> -->
<tr>
<td class="rightb">Observing Cycle:</td>
<td class="left"><select name="cycle" size="4" multiple>
<%= aoOptions %>
</select>
</td>
<td width="20">&nbsp;</td>
<td class="rightb">Order by: </td>
<td class="left"><select size="3" name="ordr" >
<%= sortOptions %>
</select>
</td>
<td width="20">&nbsp;</td>
<td>
<table cellspacing="5" class="searchBtn">
<tr >
<td > <input type="submit" class="subbtn" name="Submit" value="Search" onclick="this.form.operation.value='Search';localStorage.removeItem('tablesorter-savesort');return true;" ></td>
<td > <input type="reset" class="subbtn"  name="Reset" value="Reset" ></td>
<td > <input type="submit" class="subbtn"  name="Clear" value="Clear" onclick="clearFields(this.form);return false;"></td>
</tr>
</table>
</td>
</tr>
</table>
</center>
</div>
<font class="<%= msgclass %>"> <%= message %></font>
<input type="hidden" name="operation" value="" >
<input type="hidden" name="triggerID" value="" >
<input type="hidden" name="hiddenCoordinator" value="" >
<table width="100%" border="1" id="tooTable" class="tablesorter"> 
<thead>
<tr>
<th ><%= TriggerTooConstants.SEQNBR%></th>
<th ><%= TriggerTooConstants.OBSID %></th>
<th >Obs. Cycle</th>
<th >Request</th> 
<th >Submission Date </th>
<th >P.I.</th>
<th >Trigger Status</th>
<th >Coordinator</th>
<th ><a class="hdrlink" href="https://icxc.harvard.edu/soft/UDFs/UDF/ObsCycle/Proposals/RPS/ddt.html#paging">Page Type</a></th>
<th >CXC Start</th>
<th >CXC Stop</th>
<th >LTS Date </th>
<th >Schedule Date </th>
</tr>
</thead>
<tbody>
<%  
   for (int ii=0; ii< tooList.size(); ii++) {
     TriggerTooEntry too = (TriggerTooEntry)tooList.get(ii);
     String status = too.getStatus();
     String coordinator = too.getCoordinator();
     String statClass = "list";
     String coordinatorStr;
     String ltsDate;
     String stsDate;
     boolean didit = false;
     if (status == null || status.length()==0) {
       status = "&nbsp;";
       statClass="error";
     }else if (status.indexOf("First") >= 0) {
       statClass="error";
     }
     ltsDate = too.getLTSDate();
     if (ltsDate == null || ltsDate.length() == 0) {
       ltsDate = "&nbsp;";
     }
     stsDate = too.getSTSDate();
     if (stsDate == null || stsDate.length() == 0) {
       stsDate = "&nbsp;";
     }
     if (coordinator == null) {
       coordinator = new String("");
     }
     coordinatorStr = "<select name='Coordinator' onchange=\"if (verifyChange(this)) {this.form.triggerID.value='";
     coordinatorStr += too.getTriggerID().toString();
     coordinatorStr += "';this.form.submit();} else { this.form.reset(); }\">";
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
<td class="list"><%= too.getSequenceNumber() %></td>
<td class="list"><a href="/toomanager/tooUpdate.jsp?triggerID=<%= too.getTriggerID() %>"><%= too.getObsid() %></a></td>
<td class="list"><%= too.getCycle() %></td>
<td class="list"><%= too.getVersion() %></td>
<td class="list"><%= too.getSubmissionDate() %></td>
<td class="list"><%= too.getPI() %></td>
<td class="<%=statClass%>"><%= status %></td>
<td class="list"><%= coordinatorStr %></td>
<td class="list"><%= too.getUrgency() %></td>
<td class="list"><%= too.getCXCStart() %></td>
<td class="list"><%= too.getCXCStop() %></td>
<td nowrap class="list"><%= ltsDate %></td>
<td nowrap class="list"><%= stsDate %></td>
</tr>
     
<% 
     }
%>
</tbody>
</table>
<p>
</form>
</div>
</body>
</html>
