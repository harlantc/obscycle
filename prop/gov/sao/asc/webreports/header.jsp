<% String startPage = (String)session.getAttribute("startPageURL"); %>
<form name="exitForm" method="POST" action="/reports/reportsLogout" target="_self">
<input type="hidden" name="operation" value = "NONE"> 

<div id="cxcheaderplain">
<div class="menudiv">
<div class="hdrleft">
<a href="<%=startPage%>" target="_top"><img src="cxcheaderlogo.png" alt="CXC Home Page" border="0"></a>
</div>
<div class="hdrcenter">
<img src="blank.gif" alt="" width="1" height="50"> 
<span class="mainhdr"> Cycle <%= currentAO %> Panel Access Site: <%= userName %> </span>
<br>
<span class="mainhdr3"><%= subHeading %> </span>
</div>
</div>
</div>
<% 
String topMenuBtn = "&nbsp;";
String disableTopMenuButton = new String("disabled");
String helpPageLink;
if(helpPage == null || helpPage.length() == 0) {
  if(topMenuPage) {
     helpPageLink = new String("/reports/topMenuViewHelp.jsp");
  } else {
    helpPageLink = new String("/reports/generalHelp.jsp");
  }
} else {
  helpPageLink = new String(helpPage);
}

if(!topMenuPage) { 
  if (topMenuBtnCB != null ) {
     topMenuBtn = topMenuBtnCB;
  } else {
    topMenuBtn = "<img src='arrow-left.png' alt=' ' class='backimg' onmouseover=\"this.className='backactv';\" onmouseout=\"this.className='backimg';\" onclick='exitForm.target=\"_self\";exitForm.action=\"/reports/login.jsp?file=NoFile\";exitForm.submit()' ><input type=\"button\" value=\"Main Menu\"  class=\"mainBtn\" onClick='exitForm.target=\"_self\";exitForm.action=\"/reports/login.jsp?file=NoFile\";exitForm.submit()' >";
  }
  disableTopMenuButton = "";
} 
%>

<div style="display:inline-block; width:99%; position:relative; margin-top:3px;">
<div style="display:inline-block; float:left; width:50%;">
<%= topMenuBtn %>
</div>
<div style="display:inline-block; float:right; text-align:right; width:50%;">
<input type="button" value="Logout"  class="linkBtn"
        onClick='exitForm.target="_self";exitForm.action="/reports/reportsLogout";exitForm.operation.value="<%=ReportsConstants.EXIT%>";this.form.submit()'>
<input type="button" value="Help" class="linkBtn" onClick='var mywin=window.open("<%=helpPageLink%>","webreportsHelp","scrollbars=yes,menubar=no,toolbar=no,location=no,resizable=yes");mywin.focus();'>
</div>
</div>
</form>
