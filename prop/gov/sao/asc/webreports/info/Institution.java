// Copyright (c) 2003, Smithsonian Astrophysical Observatory
// You may do anything you like with this file except remove this copyright.

//****************************************************************************
//                              CXC Data System
//****************************************************************************

//****************************************************************************
//                               WEBREPORTS
//                               INFO Package
//--------------------------------------------------------------------------
//                               Institution
//****************************************************************************
package info;

import ascds.LogMessage;
/** 
 * This class contains the original Instutitution name and modified 
 * institution name that can be used for comparison.
 */
public class Institution {
    private String institutionName;
    private String modifiedInstitution;

    public Institution() {
	init();
    }
    public Institution(String inputStr) {
	init();
        setInstitutionName(inputStr);
    }

    
    private void init() {
	institutionName = new String("");
	modifiedInstitution = new String("");
    }

    
    /**
     * Copy from an existing Institution object
     *
     * @param inputInstitution   institution name
     */
    public void copy(Institution inputInstitution) {
	institutionName = inputInstitution.institutionName;
	modifiedInstitution = inputInstitution.modifiedInstitution;
        
    }

    // Set routines
    public void setInstitutionName(String inputStr)
    {
      if (inputStr != null)  {
        institutionName = inputStr.trim();
      }
      else {
        institutionName = "";
      }
      setModifiedInstitution(institutionName);
  
    }

    public void setInstitutionNames(String inputStr,String modStr)
    {
      if (inputStr != null)  {
        institutionName = inputStr.trim();
      } else {
        institutionName = "";
      }
      if (modStr != null)  {
        modifiedInstitution = modStr.trim();
      } else {
        modifiedInstitution = "";
      }
    }


    /**
     * This method sets the modified institution name to be used for
     * comparisons.  It will either use the ModifiedInstitutions class
     * which reads a file that was created separately or if no match is
     * found it will just try to modify the institution name by removing
     * the types of strings that usually cause institutions not to match.
     * @param inputStr input institution name
     */
    private void setModifiedInstitution(String inputStr) 
    {
       
      if (inputStr.length() > 0) {
        modifiedInstitution = ModifiedInstitutions.getModifiedInstitution(inputStr);
        if (modifiedInstitution.length() < 1) { 
            LogMessage.println("Modified institution using inline code for " + inputStr + "---");
            modifiedInstitution = inputStr.toLowerCase();
            modifiedInstitution = modifiedInstitution.replace(" of "," ");
	    modifiedInstitution = modifiedInstitution.replace(" and "," ");
  	    modifiedInstitution = modifiedInstitution.replace(" the "," ");
  	    modifiedInstitution = modifiedInstitution.replace(" de "," ");
  	    modifiedInstitution = modifiedInstitution.replace(" di "," ");
  	    modifiedInstitution = modifiedInstitution.replace(" et "," ");
  	    modifiedInstitution = modifiedInstitution.replace("  "," ");
  	    modifiedInstitution = modifiedInstitution.replace("\""," ");
  	    modifiedInstitution = modifiedInstitution.replace("&"," ");
  	    modifiedInstitution = modifiedInstitution.replace("'"," ");
  	    modifiedInstitution = modifiedInstitution.replace("-"," ");
  	    modifiedInstitution = modifiedInstitution.replace("/"," ");
  	    modifiedInstitution = modifiedInstitution.replace("."," ");
  	    modifiedInstitution = modifiedInstitution.replace(","," ");
    
            String[] instArray = modifiedInstitution.split(" ");
            modifiedInstitution = new String("");
            for (int ii=0; ii<instArray.length; ii++) { 
              if (instArray[ii].indexOf("univ",0) >= 0) {
                modifiedInstitution += "university ";
              }
              else if (instArray[ii].indexOf("inst",0) >= 0 ) {
                modifiedInstitution += "institute ";
              }
              else {
                modifiedInstitution += instArray[ii];
              }
            }
  
            if ((modifiedInstitution.indexOf("harvard") >= 0) ||
               (modifiedInstitution.indexOf("smithsonian") >= 0) ||
               ((modifiedInstitution.indexOf("sao") >= 0) &&
               (modifiedInstitution.indexOf("paolo") < 0) &&
               (modifiedInstitution.indexOf("special") < 0)  ) ||
               (modifiedInstitution.indexOf("cxc") >= 0) ||
               (modifiedInstitution.indexOf("cfa") >= 0) ||
               (modifiedInstitution.indexOf("center for astrophysic") >= 0) ) {
              modifiedInstitution = "sao";
            }
  
            if ((modifiedInstitution.indexOf("spitzer") >= 0) ||
               (modifiedInstitution.indexOf("ipac") >= 0) ) {
              modifiedInstitution = "spitzer";
            }
  
            if ((modifiedInstitution.indexOf("planck") >= 0) &&
                (modifiedInstitution.indexOf("extraterr") >= 0) ) {
              modifiedInstitution = "max-planck extraterr";
            }
  
            if ((modifiedInstitution.indexOf("sron") >= 0) ) {
              modifiedInstitution = "sron";
            }
        }
      }
      else {
        modifiedInstitution="";
      }
    }
      
   
    public String getInstitutionName()
    {
       return institutionName;
    }
    public String getModifiedInstitution()
    {
       return modifiedInstitution;
    }

}
