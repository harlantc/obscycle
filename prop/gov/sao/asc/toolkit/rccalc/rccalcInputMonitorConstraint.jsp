<table>
  <tr>    
    <td align="center" bgcolor="<%= session.getAttribute("monitorConstraintLabelBGColor" ) %>">
      <a href="prop_help_rccalc.jsp#MonitorConstraint"><b>Monitor Constraint:</b></a>

        &nbsp;&nbsp;&nbsp;&nbsp;

	<% if ( ((String) session.getAttribute( "groupConstraint")).equals( "Yes" ) ) { %>     
           <input disabled type="radio" name="monitorConstraint" value="Yes"
           	      onClick="this.form.submit()"
           <%= "Yes".equals( session.getAttribute( "monitorConstraint" ) ) ? "checked" : "" %>>
           <a href="prop_help_rccalc.jsp#MonitorConstraint">Yes</a>  

           <input disabled type="radio" name="monitorConstraint" value="No"
           	      onClick="this.form.submit()"
           <%= "No".equals( session.getAttribute( "monitorConstraint" ) ) ? "checked" : "" %>>
           <a href="prop_help_rccalc.jsp#MonitorConstraint">No</a>
	<% }else{ %>
           <input type="radio" name="monitorConstraint" value="Yes"
           	      onClick="this.form.submit()"
           <%= "Yes".equals( session.getAttribute( "monitorConstraint" ) ) ? "checked" : "" %>>
           <a href="prop_help_rccalc.jsp#MonitorConstraint">Yes</a>  

           <input type="radio" name="monitorConstraint" value="No"
           	      onClick="this.form.submit()"
           <%= "No".equals( session.getAttribute( "monitorConstraint" ) ) ? "checked" : "" %>>
           <a href="prop_help_rccalc.jsp#MonitorConstraint">No</a>
	<% } %>

    </td>
  </tr>

  <% if ( ((String) session.getAttribute( "monitorConstraint")).equals( "Yes" ) ) { %>

  <tr><td>
    <table border=1 width=80% align="center" id="monitorConstraintTable">
      <tr>
        <th align="left" bgcolor="<%= session.getAttribute("monitorConstraintLabelBGColor" ) %>">
          <b>Remove?</b>
        </th>

   	<th align="left" bgcolor="<%= session.getAttribute("monitorConstraintLabelBGColor" ) %>">
      	  <b>Order</b>
    	</th>

    	<th align="left" bgcolor="<%= session.getAttribute("monitorExposureTimeLabelBGColor" ) %>">
      	  <a href="prop_help_rccalc.jsp#MonitorExposure"><b>Exposure Time</b></a>&nbsp;&nbsp;(ks)
    	</th>

    	<th align="left" bgcolor="<%= session.getAttribute("monitorPreMinLabelBGColor" ) %>">
      	  <a href="prop_help_rccalc.jsp#MonitorPreMinLead"><b>Minimum Time Interval</b></a>&nbsp;&nbsp;(days)
    	</th>
                     
    	<th align="left" bgcolor="<%= session.getAttribute("monitorPreMaxLabelBGColor" ) %>"> 
      	  <a href="prop_help_rccalc.jsp#MonitorPreMaxLead"><b>Maximum Time Interval</b></a>&nbsp;&nbsp;(days)
    	</th>

    	<th align="left" bgcolor="<%= session.getAttribute("monitorIntervalLabelBGColor" ) %>">
      	  <a href="prop_help_rccalc.jsp#MonitorSplitInterval"><b>Split Interval</b></a>&nbsp;&nbsp;(days)
	</th>
      </tr>

        <% 
            Integer monitorCounter = (Integer)session.getAttribute("monitorNumRows");
            String monitorOp = (String)session.getAttribute("monitorOperation");
            int xx=0;

            if (( monitorCounter == null ) || (monitorCounter < 0)) {
                monitorCounter = 0;
            }

            String monitorExpTime="";
	    String monitorPreMinLead="";
	    String monitorPreMaxLead="";
	    String monitorSplitInterval="";
	    String monitorRemoveCheck="";

            if (monitorOp.equals("ADD MONITOR")) {
                monitorCounter = monitorCounter + 1;
		monitorExpTime = "monitorExpTime" + Integer.toString(monitorCounter-1); 
                session.setAttribute(monitorExpTime, "");
                monitorPreMinLead = "monitorPreMinLead" + Integer.toString(monitorCounter-1); 
                session.setAttribute(monitorPreMinLead, "");
                monitorPreMaxLead = "monitorPreMaxLead" + Integer.toString(monitorCounter-1); 
                session.setAttribute(monitorPreMaxLead, "");
                monitorSplitInterval = "monitorSplitInterval" + Integer.toString(monitorCounter-1); 
                session.setAttribute(monitorSplitInterval, "");
                monitorRemoveCheck = "monitorRemoveCheck" + Integer.toString(monitorCounter-1);
                session.setAttribute(monitorRemoveCheck, "");
            } 
            else if (monitorOp.equals("REMOVE MONITOR")) {
                String chkName="", currCheck="", nextCheck="", nextCheckVal="", currCol="", nextCol="", nextColVal="";    
                int removed = 0, yy=0;
                
                /* loop over rows in table */
                for (xx = 0; xx < monitorCounter; xx++) {

                    chkName = "monitorRemoveCheck" + Integer.toString(xx);

                    /* if this element is checked, remove the row from the table */
                    if ( "on".equals(session.getAttribute(chkName)) )
                    {

                        /* loop from current row (xx) to last row,
                           overwriting the deleted row and shifting
                           the rest of the rows down
                        */ 
                        for (yy=xx; yy < monitorCounter; yy++) {

                            /* if last row is to be deleted, just
                               reset to default values */
                            if (xx == monitorCounter-1) {
                                currCol = "monitorExpTime" + Integer.toString(yy);
                                session.setAttribute(currCol, "");

				currCol = "monitorPreMinLead" + Integer.toString(yy);
                                session.setAttribute(currCol, "");

				currCol = "monitorPreMaxLead" + Integer.toString(yy); 
                                session.setAttribute(currCol, "");

				currCol = "monitorSplitInterval" + Integer.toString(yy);
                                session.setAttribute(currCol, "");

                                session.setAttribute(currCheck, null);
                            } else {
                                /* move the row data values and checkbox
                                   values down in index */
                                currCol = "monitorExpTime" + Integer.toString(yy);
                                nextCol = "monitorExpTime" + Integer.toString(yy+1);
                                nextColVal = (String)session.getAttribute(nextCol);
                                session.setAttribute(currCol, nextColVal);

                                currCol = "monitorPreMinLead" + Integer.toString(yy);
                                nextCol = "monitorPreMinLead" + Integer.toString(yy+1);
                                nextColVal = (String)session.getAttribute(nextCol);
                                session.setAttribute(currCol, nextColVal);

                                currCol = "monitorPreMaxLead" + Integer.toString(yy);
                                nextCol = "monitorPreMaxLead" + Integer.toString(yy+1);
                                nextColVal = (String)session.getAttribute(nextCol);
                                session.setAttribute(currCol, nextColVal);

                                currCol = "monitorSplitInterval" + Integer.toString(yy);
                                nextCol = "monitorSplitInterval" + Integer.toString(yy+1);
                                nextColVal = (String)session.getAttribute(nextCol);
                                session.setAttribute(currCol, nextColVal);

                                currCheck = "monitorRemoveCheck" + Integer.toString(yy);
                                nextCheck = "monitorRemoveCheck" + Integer.toString(yy+1); 
                                if ("on".equals(session.getAttribute(nextCheck)) ) {
                                    session.setAttribute(currCheck, "on");
                                } else {
                                    session.setAttribute(currCheck, null);
                                }
                            }
                        }

                        xx--;

			/* update the number of rows */
                	monitorCounter--;
                    }
                }
            }

            session.setAttribute("monitorNumRows", monitorCounter);

            monitorExpTime="";
	    monitorPreMinLead="";
	    monitorPreMaxLead="";
	    monitorSplitInterval="";
	    monitorRemoveCheck="";

            // Display Rows
            xx=0;
            for (xx = 0; xx < monitorCounter; xx++) {
		monitorExpTime = "monitorExpTime" + Integer.toString(xx); 
                monitorPreMinLead = "monitorPreMinLead" + Integer.toString(xx);
                monitorPreMaxLead = "monitorPreMaxLead" + Integer.toString(xx);
                monitorSplitInterval = "monitorSplitInterval" + Integer.toString(xx);
                monitorRemoveCheck = "monitorRemoveCheck" + Integer.toString(xx);

		if ( xx == 0 ) {
		  session.setAttribute( monitorPreMinLead, "000:00:00:00.000" );
		  session.setAttribute( monitorPreMaxLead, "000:00:00:00.000" );
		}
        %> 
        
      <tr>
        <td>
          <input type="checkbox" name="<%= monitorRemoveCheck %>">
        </td>
        <td>
          <input size="5" disabled name="monitorTableIndex" value="<%= xx+1 %>" >
        </td>
        <td>
          <input size="16" name="<%= monitorExpTime %>" required value="<%=session.getAttribute( monitorExpTime ) %>" >
        </td>
        <td>
          <input size="16" <%= (xx==0) ? "disabled" : "" %> name="<%= monitorPreMinLead %>" required value="<%=session.getAttribute( monitorPreMinLead ) %>" >
        </td>
        <td>
          <input size="16" <%= (xx==0) ? "disabled" : "" %> name="<%= monitorPreMaxLead %>" required value="<%=session.getAttribute( monitorPreMaxLead ) %>" >
        </td>
        <td>
          <input size="16" name="<%= monitorSplitInterval %>" required value="<%=session.getAttribute( monitorSplitInterval ) %>" >
        </td>
      </tr>

        <% } %>


    </table>
  </td></tr>
  <tr>
    <td align="center">
      <input type="hidden" name="monitorOperation" value = "CLEAR">
      <input type="button" value="Add Observation" onclick='jspForm.monitorOperation.value = "ADD MONITOR";this.form.submit()' />
      <input type="button" value="Remove Selected Observations" onclick='jspForm.monitorOperation.value = "REMOVE MONITOR";this.form.submit()' />
    </td>
  </tr>


  <% } %> 

</table>
