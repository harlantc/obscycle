<table>
  <tr>    
    <td align="center" bgcolor="<%= session.getAttribute("rollConstraintLabelBGColor" ) %>">
      <a href="prop_help_rccalc.jsp#RollConstraint"><b>Roll Constraint:</b></a>

        &nbsp;&nbsp;&nbsp;&nbsp;

        <input type="radio" name="rollConstraint" value="Yes"
         	      onClick="this.form.submit()"
        <%= "Yes".equals( session.getAttribute( "rollConstraint" ) ) ? "checked" : "" %>>
        <a href="prop_help_rccalc.jsp#RollConstraint">Yes</a>  

        <input type="radio" name="rollConstraint" value="No"
           	      onClick="this.form.submit()"
        <%= "No".equals( session.getAttribute( "rollConstraint" ) ) ? "checked" : "" %>>
        <a href="prop_help_rccalc.jsp#RollConstraint">No</a>

    </td>
  </tr>

  <% if ( ((String) session.getAttribute( "rollConstraint")).equals( "Yes" ) ) { %>

  <tr><td>
    <table border=1 width=80% align="center" id="rollConstraintTable">
      <tr>
        <th align="left" bgcolor="<%= session.getAttribute("rollConstraintLabelBGColor" ) %>">
          <b>Remove?</b>
        </th>

    	<th align="left" bgcolor="<%= session.getAttribute("rollAngleLabelBGColor" ) %>">
      	  <a href="prop_help_rccalc.jsp#RollAngleTolerance"><b>Roll Angle</b></a>&nbsp;&nbsp;
	</th>

    	<th align="left" bgcolor="<%= session.getAttribute("rollToleranceLabelBGColor" ) %>">
      	  <a href="prop_help_rccalc.jsp#RollAngleTolerance"><b>Roll Tolerance</b></a>&nbsp;&nbsp;
	</th>

    	<th align="left" bgcolor="<%= session.getAttribute("rollRotationLabelBGColor" ) %>">
      	  <a href="prop_help_rccalc.jsp#RollRotation"><b>180 Rotation OK?</b></a>&nbsp;&nbsp;
    	</th>
      </tr>


        <% 
            Integer rollCounter = (Integer)session.getAttribute("rollNumRows");
            String rollOp = (String)session.getAttribute("rollOperation");
            int mm=0;

            if (( rollCounter == null ) || (rollCounter < 0)) {
                rollCounter = 0;
            }

            String rollRotation="";
            String rollAngle="";
            String rollTolerance="";
            String rollRemoveCheck="";

            if (rollOp.equals("ADD ROLL")) {
                rollCounter = rollCounter + 1;
                rollRotation = "rollRotation" + Integer.toString(rollCounter-1); 
                session.setAttribute(rollRotation, null);
                rollAngle = "rollAngle" + Integer.toString(rollCounter-1); 
                session.setAttribute(rollAngle, "");
                rollTolerance = "rollTolerance" + Integer.toString(rollCounter-1); 
                session.setAttribute(rollTolerance, "");
                rollRemoveCheck = "rollRemoveCheck" + Integer.toString(rollCounter-1);
                session.setAttribute(rollRemoveCheck, "");
            } 
            else if (rollOp.equals("REMOVE ROLL")) {
                String chkName="", currCheck="", nextCheck="",
                nextCheckVal="", currCol="", nextCol="",
                nextColVal="";    
                int removed = 0, nn=0;
                
                /* loop over rows in table */
                for (mm = 0; mm < rollCounter; mm++) {
                    chkName = "rollRemoveCheck" + Integer.toString(mm);

                    /* if this element is checked, remove the row from the table */
                    if ( "on".equals(session.getAttribute(chkName)) )
                    {
                        /* loop from current row (mm) to last row,
                           overwriting the deleted row and shifting
                           the rest of the rows down
                        */ 
                        for (nn=mm; nn < rollCounter; nn++) {

                            /* if last row is to be deleted, just
                               reset to default values */
                            if (mm == rollCounter-1) {
                                currCol = "rollRotation" + Integer.toString(nn);
                                session.setAttribute(currCol, null);

                                currCol = "rollAngle" + Integer.toString(nn);
                                session.setAttribute(currCol, "");

                                currCol = "rollTolerance" + Integer.toString(nn);
                                session.setAttribute(currCol, "");

                                session.setAttribute(currCheck, null);
                            } else {
                                /* move the row data value and checkbox
                                   values down in index */
                                currCol = "rollRotation" + Integer.toString(nn);
                                nextCol = "rollRotation" + Integer.toString(nn+1);
                                if ("on".equals(session.getAttribute(nextCol)) ) {
                                    session.setAttribute(currCol, "on");
                                } else {
                                    session.setAttribute(currCol, null);
                                }

                                currCol = "rollAngle" + Integer.toString(nn);
                                nextCol = "rollAngle" + Integer.toString(nn+1);
                                nextColVal = (String)session.getAttribute(nextCol);
                                session.setAttribute(currCol, nextColVal);

                                currCol = "rollTolerance" + Integer.toString(nn);
                                nextCol = "rollTolerance" + Integer.toString(nn+1);
                                nextColVal = (String)session.getAttribute(nextCol);
                                session.setAttribute(currCol, nextColVal);

                                currCheck = "rollRemoveCheck" + Integer.toString(nn);
                                nextCheck = "rollRemoveCheck" + Integer.toString(nn+1); 
                                if ("on".equals(session.getAttribute(nextCheck)) ) {
                                    session.setAttribute(currCheck, "on");
                                } else {
                                    session.setAttribute(currCheck, null);
                                }
                            }
                        }

                        mm--;

                	/* update the number of rows */
                	rollCounter--;
                    }
                }
            }

            session.setAttribute("rollNumRows", rollCounter);

            rollRotation="";
            rollAngle="";
            rollTolerance="";
            rollRemoveCheck="";

            // Display Rows
            mm=0;
            for (mm = 0; mm < rollCounter; mm++) {
                rollRotation = "rollRotation" + Integer.toString(mm); 
                rollAngle = "rollAngle" + Integer.toString(mm);
                rollTolerance = "rollTolerance" + Integer.toString(mm);
                rollRemoveCheck = "rollRemoveCheck" + Integer.toString(mm);
        %> 
        
      <tr>
        <td>
          <input type="checkbox" name="<%= rollRemoveCheck %>">
        </td>
        <td>
          <input size="16" name="<%= rollAngle %>" required value="<%=session.getAttribute( rollAngle ) %>" >
        </td>
        <td>
          <input size="16" name="<%= rollTolerance %>" required value="<%=session.getAttribute( rollTolerance ) %>" >
        </td>
        <td>
          <input type="checkbox" name="<%= rollRotation %>" value="Yes"
	      <%= (session.getAttribute( rollRotation ) == null ) ? "" : "checked" %> >
        </td>
      </tr>

        <% } %>


    </table>
  </td></tr>
  <tr>
    <td align="center">
      <input type="hidden" name="rollOperation" value = "CLEAR">
      <input type="button" value="Add Roll Constraint" onclick='jspForm.rollOperation.value = "ADD ROLL";this.form.submit()' />
      <input type="button" value="Remove Selected Roll Constraints" onclick='jspForm.rollOperation.value = "REMOVE ROLL";this.form.submit()' />
    </td>
  </tr>


  <% } %> 

</table>
