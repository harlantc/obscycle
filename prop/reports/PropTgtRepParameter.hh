/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: PropTgtRepParameter.hh
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:
 
        The parameter class provides the application with the
        set of specific parameters needed to execute the Proposal 
	background application that produces an RA sorted list of
	targets. 

 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%

*H****************************************************************************/

#ifndef PROPTGTREPPARAMETER_HH
#define PROPTGTREPPARAMETER_HH

#include "ProposalParameter.hh"

class PropTgtRepParameter : public ProposalParameter
{
public:
  // Constructor
  PropTgtRepParameter();
  PropTgtRepParameter(const int argc,const char **argv);
  

  // Accessor functions

  const char    *GetAO()        {  return prop_ao.c_str(); }
  const char    *GetType()      {  return type.c_str(); }
  const char    *GetStatus()    {  return prop_status.c_str(); }
  const char    *GetTgtStatus() {  return tgt_status.c_str(); }

  const char    *GetPropNo()  {  return propno.c_str(); }
  const char    *GetPanelId() {  return panelid.c_str(); }
  const char    *GetFile()    {  return filename.c_str(); }
  const char    *GetTgtFile() {  return tgtfilename.c_str(); }

  Boolean IsAll()       {  return all_flag; }

  Boolean IsPI()        {  return pi; }
  Boolean IsPropNo()    {  return pno; }

  Boolean IsConflicts() {  return conflicts; }
  Boolean IsMP()        {  return mp_flag; }
  Boolean IsApproved()  {  return app_flag; }

protected:

  void Initialize();

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

  string  type;        // only produce report for this type of observer
  string  prop_status; // only produce report for this proposal status
  string  tgt_status;  // only produce report for this target status
  string  prop_ao;     // only produce report for this AO

  string  propno;      // use Proposal Number 
  string  panelid;     // use panel name
  string  filename;
  string  tgtfilename;

  Boolean pi;          // include PI in output format
  Boolean pno;         // include Proposal Number in output format

  Boolean conflicts;   // include conflicts in output format
  Boolean mp_flag;     // special target list for mission planning
  Boolean app_flag;    // print approved target format

  Boolean all_flag;    // print approved target format
  
};

extern PropTgtRepParameter *theParameters;

#endif
