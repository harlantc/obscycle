<table>
  <tr>    
    <td align="center" bgcolor="<%= session.getAttribute("windowConstraintLabelBGColor" ) %>">
      <a href="prop_help_rccalc.jsp#WindowConstraint"><b>Window Constraint:</b></a>

        &nbsp;&nbsp;&nbsp;&nbsp;

        <input type="radio" name="windowConstraint" value="Yes"
         	      onClick="this.form.submit()"
        <%= "Yes".equals( session.getAttribute( "windowConstraint" ) ) ? "checked" : "" %>>
        <a href="prop_help_rccalc.jsp#WindowConstraint">Yes</a>  

        <input type="radio" name="windowConstraint" value="No"
           	      onClick="this.form.submit()"
        <%= "No".equals( session.getAttribute( "windowConstraint" ) ) ? "checked" : "" %>>
        <a href="prop_help_rccalc.jsp#WindowConstraint">No</a>

    </td>
  </tr>

  <% if ( ((String) session.getAttribute( "windowConstraint")).equals( "Yes" ) ) { %>

  <tr><td align="center">
    <table border=1 align="center" id="windowConstraintTable">
      <tr>
        <td align="left" bgcolor="<%= session.getAttribute("windowConstraintLabelBGColor" ) %>">
          <b>Remove?</b>
        </td>

    	<td align="left" bgcolor="<%= session.getAttribute("windowStartLabelBGColor" ) %>">
      	  <a href="prop_help_rccalc.jsp#WindowStartTime"><b>Start Time</b></a>
    	</td>

    	<td align="left" bgcolor="<%= session.getAttribute("windowStopLabelBGColor" ) %>">
      	  <a href="prop_help_rccalc.jsp#WindowStopTime"><b>Stop Time</b></a>
	</td>
      </tr>

        <% 
            Integer windowCounter = (Integer)session.getAttribute("windowNumRows");
            String windowOp = (String)session.getAttribute("windowOperation");
            int ii=0;

            if (( windowCounter == null ) || (windowCounter < 0)) {
                windowCounter = 0;
            }

            String windowStartTime="";
            String windowStopTime="";
            String windowRemoveCheck="";

            if (windowOp.equals("ADD WINDOW")) {
                windowCounter = windowCounter + 1;
                windowStartTime = "windowStartTime" + Integer.toString(windowCounter-1); 
                session.setAttribute(windowStartTime, "");
                windowStopTime = "windowStopTime" + Integer.toString(windowCounter-1); 
                session.setAttribute(windowStopTime, "");
                windowRemoveCheck = "windowRemoveCheck" + Integer.toString(windowCounter-1);
                session.setAttribute(windowRemoveCheck, "");
            } 
            else if (windowOp.equals("REMOVE WINDOW")) {
                String chkName="", currCheck="", nextCheck="",
                nextCheckVal="", currCol="", nextCol="",
                nextColVal="";    
                int removed = 0, jj=0;
                
                /* loop over rows in table */
                for (ii = 0; ii < windowCounter; ii++) {

                    chkName = "windowRemoveCheck" + Integer.toString(ii);

                    /* if this element is checked, remove the row from the table */
                    if ( "on".equals(session.getAttribute(chkName)) )
                    {
                        /* loop from current row (ii) to last row,
                           overwriting the deleted row and shifting
                           the rest of the rows down
                        */ 
                        for (jj=ii; jj < windowCounter; jj++) {

                            /* if last row is to be deleted, just
                               reset to default values */
                            if (ii == windowCounter-1) {
			        currCol = "windowStartTime" + Integer.toString(jj);
                                session.setAttribute(currCol, "");

			        currCol = "windowStopTime" + Integer.toString(jj);
                                session.setAttribute(currCol, "");

                                session.setAttribute(currCheck, null);
                            } else {
                                /* move the row data values and checkbox
                                   values down in index */
                                currCol = "windowStartTime" + Integer.toString(jj);
                                nextCol = "windowStartTime" + Integer.toString(jj+1);
                                nextColVal = (String)session.getAttribute(nextCol);
                                session.setAttribute(currCol, nextColVal);

                                currCol = "windowStopTime" + Integer.toString(jj);
                                nextCol = "windowStopTime" + Integer.toString(jj+1);
                                nextColVal = (String)session.getAttribute(nextCol);
                                session.setAttribute(currCol, nextColVal);

                                currCheck = "windowRemoveCheck" + Integer.toString(jj);
                                nextCheck = "windowRemoveCheck" + Integer.toString(jj+1); 
                                if ("on".equals(session.getAttribute(nextCheck))
                                ) {
                                    session.setAttribute(currCheck, "on");
                                } else {
                                    session.setAttribute(currCheck, null);
                                }
                            }
                        }

                        ii--;

			/* update the number of rows */
			windowCounter--;
                    }
                }
	    }

            session.setAttribute("windowNumRows", windowCounter);

            windowStartTime="";
            windowStopTime="";
            windowRemoveCheck="";

            // Display Rows
            ii=0;
            for (ii = 0; ii < windowCounter; ii++) {
                windowStartTime = "windowStartTime" + Integer.toString(ii); 
                windowStopTime = "windowStopTime" + Integer.toString(ii);
                windowRemoveCheck = "windowRemoveCheck" + Integer.toString(ii);
        %> 
        
      <tr>
        <td>
          <input type="checkbox" name="<%= windowRemoveCheck %>">
        </td>
        <td>
          <input type="datetime-local" step=0.01 pattern="[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}[A-Z]{2}" size="50" name="<%= windowStartTime %>" required value="<%=session.getAttribute( windowStartTime ) %>" >
        </td>
        <td>
          <input type="datetime-local" step=0.01 pattern="[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}[A-Z]{2}" size="50" name="<%= windowStopTime %>" required value="<%=session.getAttribute( windowStopTime ) %>" >
        </td>
      </tr>

        <% } %>

    </table>
  </td></tr>
  <tr>
    <td align="center">
      <input type="hidden" name="windowOperation" value = "CLEAR">
      <input type="button" value="Add Window Constraint" onclick='jspForm.windowOperation.value = "ADD WINDOW";this.form.submit()' />
      <input type="button" value="Remove Selected Windows" onclick='jspForm.windowOperation.value = "REMOVE WINDOW";this.form.submit()' />
    </td>
  </tr>


  <% } %> 

</table>
