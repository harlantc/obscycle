/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: %M%
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:
 
        The parameter class provides the application with the
        set of specific parameters needed to execute the Proposal 
	Panel creation background application .

 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%

*H****************************************************************************/

#ifndef PANELCREATEPARAMETER_HH
#define PANELCREATEPARAMETER_HH

#include <string>
#include "ProposalParameter.hh"

class PanelCreateParameter : public ProposalParameter
{
public:
  // Constructor
  PanelCreateParameter();
  PanelCreateParameter(const int argc,const char **argv);
  

  // Accessor functions

  const char *GetFilename() {  return filename.c_str(); }
  const char *GetAO()       {  return use_ao.c_str(); }
  const char *GetOutfile()  {  return out_file.c_str(); }
  const char *GetListfile() {  return listfile.c_str(); }

  Boolean IsPrint()         {  return do_print; }
  Boolean AssignProposals() {  return do_proposals; }
  Boolean AssignSQLFiles()  {  return do_sql; }

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


  // Objects

  string  filename;
  string  out_file;
  string  use_ao;
  string  listfile;
  Boolean do_sql;
  Boolean do_proposals;
  Boolean do_print;
  Boolean use_default;

};

extern PanelCreateParameter *theParameters;

#endif
