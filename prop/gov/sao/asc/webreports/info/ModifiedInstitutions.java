package info;

import java.io.*;
import java.util.*;
import java.lang.*;

import ascds.LogMessage;


public class ModifiedInstitutions {

   private static String modifiedInstFilename;
   private static Vector<Institution> institutionList;
   private static long lastModified;


  public ModifiedInstitutions(String filename)
  {
     modifiedInstFilename = filename;
     institutionList = new Vector<Institution>();
     lastModified = 0;
     readInstitutions();
  }

  public static String getModifiedInstitution(String inputStr)
  {
     String returnVal = new String("");
     boolean foundit = false;

     
     readInstitutions();

     if (institutionList != null ) {
       Iterator ii = institutionList.iterator();
       while ( ii.hasNext() && !foundit) {
         Institution inst = (Institution)ii.next();
         if (inst.getInstitutionName().equalsIgnoreCase(inputStr)) {
           foundit = true;
           returnVal = inst.getModifiedInstitution();
         }
       }
     }
         
     return returnVal;
  }
         

  public Vector<Institution> getinstitutionList()
  {
        return institutionList;
  }
  private static void readInstitutions()
  {
    String nextLine;

    File inFile = new File(modifiedInstFilename);
    if (inFile.exists()) {
      if (inFile.lastModified() > lastModified) {

        BufferedReader in = null;
        LogMessage.println("reading institutions " + modifiedInstFilename);
  
        try {
          in = new BufferedReader(new FileReader(modifiedInstFilename));
   
          while ( (nextLine = in.readLine()) != null) {
            Institution ival = new Institution(); 
            String origName = new String("");
            String modName = new String("");
            StringTokenizer st = new StringTokenizer( nextLine,"\t",false);
            if (st.hasMoreTokens()) {
               origName = st.nextToken();
            }
            if (st.hasMoreTokens()) {
               modName = st.nextToken();
            }
            ival.setInstitutionNames(origName,modName);
            institutionList.add(ival);
          }
          in.close();
          lastModified = inFile.lastModified();
        }
        catch (Exception exc) {
          LogMessage.printException(exc);
        }
      }
    }

  }
}
