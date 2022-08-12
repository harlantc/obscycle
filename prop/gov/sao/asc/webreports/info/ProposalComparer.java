// Copyright (c) 2003, Smithsonian Astrophysical Observatory
// You may do anything you like with this file except remove this copyright.

//****************************************************************************
//                              CXC Data System
//****************************************************************************

//****************************************************************************
//                               WEBREPORTS
//                               INFO Package
//--------------------------------------------------------------------------
//                             ProposalComparator
//****************************************************************************
package info;

import java.util.Comparator;
import java.lang.*;

/** 
 * This file contains the comparison classes used for the Proposal object
 */


// sort by proposal number
class PNumberComparer implements Comparator {
    public int compare(Object obj1, Object obj2)
    {
       String t1 = ((Proposal)obj1).getProposalNumber();
       String t2 = ((Proposal)obj2).getProposalNumber();
       return (t1.compareTo(t2));
    }
}
// sort by proposal title
class PTitleComparer implements Comparator {
    public int compare(Object obj1, Object obj2)
    {
       String t1 = ((Proposal)obj1).getTitle().toLowerCase();
       String t2 = ((Proposal)obj2).getTitle().toLowerCase();
       return (t1.compareTo(t2));
    }
}
// sort by proposal PI
class PPIComparer implements Comparator {
    public int compare(Object obj1, Object obj2)
    {
       String t1 = ((Proposal)obj1).getPI().toLowerCase();
       String t2 = ((Proposal)obj2).getPI().toLowerCase();
       return (t1.compareTo(t2));
    }
}
// sort by proposal conflict
class PConflictComparer implements Comparator {
    public int compare(Object obj1, Object obj2)
    {
       String t1 = ((Proposal)obj1).getPrelimGradeConflict().toLowerCase();
       String t2 = ((Proposal)obj2).getPrelimGradeConflict().toLowerCase();
       if (t1.length() < 1) {
          t1="zzz";
       }
       if (t2.length() < 1) {
          t2="zzz";
       }
       return (t1.compareTo(t2));
    }
}

// sort by grade name
class PGradeComparer implements Comparator {
    public int compare(Object obj1, Object obj2)
    {
       Double t1 = ((Proposal)obj1).getPrelimGrade();
       Double t2 = ((Proposal)obj2).getPrelimGrade();
       int retval = 0;

       if (t1.floatValue() < 0 && t2.floatValue() < 0) {
         String s1 = ((Proposal)obj1).getPrelimGradeConflict();
         String s2 = ((Proposal)obj2).getPrelimGradeConflict();

         return (s1.compareTo(s2));
       }
       else {
         return (t2.compareTo(t1));
      }
    }
}
// sort by group name
class PGroupComparer implements Comparator {
    public int compare(Object obj1, Object obj2)
    {
       String t1 = ((Proposal)obj1).getGroupName();
       if (t1 == null || t1.length() < 1) {
         t1 = "zzzzzzzzzzzzzzzzzzzzzzzzzz";
       }
       String t2 = ((Proposal)obj2).getGroupName();
       if (t2 == null || t2.length() < 1) {
         t2 = "zzzzzzzzzzzzzzzzzzzzzzzzzz";
       }
       return (t1.toLowerCase().compareTo(t2.toLowerCase()));
    }
}
// sort by primary reviewer name
class PPrimaryComparer implements Comparator {
    public int compare(Object obj1, Object obj2)
    {
       String t1 = ((Proposal)obj1).getPrimaryReviewer();
       if (t1 == null || t1.length() < 1) {
         t1 = "zzzzzzzzzzzzzzzzzzzzzzzzzz";
       }
       String t2 = ((Proposal)obj2).getPrimaryReviewer();
       if (t2 == null || t2.length() < 1) {
         t2 = "zzzzzzzzzzzzzzzzzzzzzzzzzz";
       }
       return (t1.toLowerCase().compareTo(t2.toLowerCase()));
    }
}
// sort by secondary reviewer name
class PSecondaryComparer implements Comparator {
    public int compare(Object obj1, Object obj2)
    {
       String t1 = ((Proposal)obj1).getSecondaryReviewer();
       if (t1 == null || t1.length() < 1) {
         t1 = "zzzzzzzzzzzzzzzzzzzzzzzzzz";
       }
       String t2 = ((Proposal)obj2).getSecondaryReviewer();
       if (t2 == null || t2.length() < 1) {
         t2 = "zzzzzzzzzzzzzzzzzzzzzzzzzz";
       }
       return (t1.toLowerCase().compareTo(t2.toLowerCase()));
    }
}

// sort by panel name
class PPanelComparer implements Comparator {
    public int compare(Object obj1, Object obj2)
    {
       String t1 = ((Proposal)obj1).getPanelName();
       if (t1 == null || t1.length() < 1) {
         t1 = "zzzzzzzzzzzzzzzzzzzzzzzzzz";
       }
       String t2 = ((Proposal)obj2).getPanelName();
       if (t2 == null || t2.length() < 1) {
         t2 = "zzzzzzzzzzzzzzzzzzzzzzzzzz";
       }
       return (t1.toLowerCase().compareTo(t2.toLowerCase()));
    }
}
