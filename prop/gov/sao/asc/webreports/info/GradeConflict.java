// Copyright (c) 2003-2017,2022 Smithsonian Astrophysical Observatory
// You may do anything you like with this file except remove this copyright.

//****************************************************************************
//                              CXC Data System
//****************************************************************************

//****************************************************************************
//                               WEBREPORTS
//                               INFO Package
//--------------------------------------------------------------------------
//                               GradeConflict
//****************************************************************************
package info;

import ascds.LogMessage;

/** 
 * This class contains the original GradeConflict name and modified 
 * institution name that can be used for comparison.
 */
public class GradeConflict {
    private String  panelName;
    private String  proposalNumber;
    private Integer userID;
    private String reviewerName;
    private String reviewerPanel;

    public GradeConflict(String iPanel,String iProp,String iUser,String iName,String iRevPanel)
    {
       panelName = iPanel;
       proposalNumber = iProp;
       reviewerName = iName; // Last,F
       userID = new Integer(iUser);
       reviewerPanel = iRevPanel;
    }
    
    
   
    public String getPanelName()
    {
       return panelName;
    }
    public String getProposalNumber()
    {
       return proposalNumber;
    }
    public String getReviewerName()
    {
       return reviewerName;
    }
    public Integer getUserID()
    {
       return userID;
    }
    public String getReviewerPanel()
    {
       return reviewerPanel;
    }

    public boolean isConflict(String propNum, Integer member) 
    {
       boolean retval = false;
       if (proposalNumber.equals(propNum) &&
           member.toString().equals(userID.toString()) ) {
          retval = true;
       }

  
       return retval;
    }
    

}
