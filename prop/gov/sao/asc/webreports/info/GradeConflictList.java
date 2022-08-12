// Copyright (c) 2003-2017, Smithsonian Astrophysical Observatory
// You may do anything you like with this file except remove this copyright.

//****************************************************************************
//                              CXC Data System
//****************************************************************************

//****************************************************************************
//                               WEBREPORTS 
//                               INFO Package
//--------------------------------------------------------------------------
//                             GradeConflictList
//****************************************************************************
package info;

import java.io.*;
import java.util.*;
import java.lang.*;
import ascds.LogMessage;
import ascds.FileUtils;
import info.*;


/** 
  * This class supports the proposal conflicts from the preliminary grades files
 */
@SuppressWarnings("unchecked")
public class GradeConflictList
{
  private Vector<GradeConflict>  gradesList;
  
  

  /**
   * Constructor
   *
  */
  public GradeConflictList ()
  {

    gradesList = new Vector<GradeConflict>();
  }


  /**
    * return number of proposals in this class
    *
    * @return int number of proposals
   */
  public int size()
  {
    return gradesList.size();
  }
  public void add(GradeConflict gc)
  {
     gradesList.add(gc);
  }

  public String getConflict(String propNum, Integer id)
  {
    return getConflict(propNum,id,"NULL");
  }

  public String getConflict(String propNum, Integer id,String pname)
  {
    String retval = "";
    Iterator ii = gradesList.iterator();
    while ( ii.hasNext()  ) {
      GradeConflict gc = (GradeConflict)ii.next();
      if (gc.isConflict(propNum,id)) {
        retval += gc.getReviewerName();
        if (pname.indexOf(gc.getReviewerPanel()) > 0)
          retval += "*";
        retval += "---";
      }    
    }

    return retval;
  }


  /**
    * Return GradeConflict for the given index
    *
    * @param idx  index of proposal in vector
    * @return Proposal proposal for given index or null
   */
  public GradeConflict get(int idx)
  {
    GradeConflict retval = null;
    if (idx >= 0 && idx < gradesList.size()) {
      retval = gradesList.get(idx);
    }
    return retval;
  }


  
}  // end of class

