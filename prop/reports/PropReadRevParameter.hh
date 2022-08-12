/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: PropReadRevParameter.hh
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:
 
        The parameter class provides the application with the
        set of specific parameters needed to execute the Proposal 
	background application that reads the primary,secondary and
	technical reviews and updates the database.
 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%

*H****************************************************************************/

#ifndef PROPREADREVPARAMETER_HH
#define PROPREADREVPARAMETER_HH

#include "ProposalParameter.hh"

class PropReadRevParameter : public ProposalParameter
{
public:
  // Constructor
  PropReadRevParameter();
  PropReadRevParameter(const int argc,const char **argv);
  

  // Accessor functions

  const char *GetDirectory() {  return dirname.c_str(); }
  const Boolean isOverride() { return is_override; }


protected:

  // Virtual function for Printing the command line argument format for the
  // derived class
  virtual void MyAdditionalFormat(string &);

  // Virtual member function to process the derived parameters
  virtual FW_Param_Error ProcessAdditionalParameters
      (int &i,const int argc,const char **argv);

  // Virtual member function to validate required parameters 
  virtual FW_Param_Error  ValidateAdditionalParameters();

  // Print to the output stream
  void PrintAdditional(ostream&);


  void Initialize();
  // Objects

  string  dirname;  // process files in specified directory
  Boolean is_override;
  

};

extern PropReadRevParameter *theParameters;

#endif
