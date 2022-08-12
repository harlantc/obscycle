/*H****************************************************************************
* Copyright (c) 1995,2019 Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: PropRepParameter.hh
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:
 
        The parameter class provides the application with the
        set of specific parameters needed to execute the Proposal 
	background application that produces various report formats
	for specified proposal ids.
 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%

*H****************************************************************************/

#ifndef PROPREPPARAMETER_HH
#define PROPREPPARAMETER_HH

#include "ProposalParameter.hh"

class PropRepParameter : public ProposalParameter
{
public:
  // Constructor
  PropRepParameter();
  PropRepParameter(const int argc,const char **argv);
  

  // Accessor functions

  const char *GetAO()                 {  return ao.c_str(); }
  const char *GetType()               {  return type.c_str(); }
  const char *GetStatus()             {  return prop_status.c_str(); }
  const char *GetPropNo()             {  return propno.c_str(); }
  const char *GetPanelId()            {  return panelid.c_str(); }
  const char *GetFilename()           {  return filename.c_str(); }
  const char *GetOutDir()           {  return outdir.c_str(); }
  Boolean IsAll()               {  return all_flag; }

  Boolean TargetStatus()        { return tstat_flag; }
  Boolean IsAbstract()          { return a_flag; }
  Boolean IsTitle()             { return pt_flag; }
  Boolean IsPrimaryReviews()    { return p_flag; }
  Boolean IsSecondaryReviews()  { return s_flag; }
  Boolean IsTechnicalEval()     { return t_flag; }
  Boolean IsPeerReview()        { return peer_flag; }
  Boolean IsFinalReview()   	{ return final_flag; }
  Boolean IsLetter()   		{ return letter_flag; }
  Boolean IsFairshare()   	{ return fairshare_flag; }
  Boolean IsApproved()   	{ return app_flag; }
  Boolean IsConfirmation()   	{ return confirm_flag; }
  Boolean IsAppSummary()   	{ return appsumm_flag; }
  Boolean IsUserConflicts()   	{ return conflict_flag; }
  Boolean IsSupportingFiles()   { return supfile_flag; }


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

  string  ao;           // only produce report for this AO
  string  type;         // only produce report for this type of observer
  string  propno;       // only produce report for this proposal number
  string  prop_status;  // only produce report for this proposal status
  string  filename;     // only produce report for proposals in file
  string  panelid;      // only produce report for proposals in panel
  string  outdir;      // output directory for supporting files
  
  Boolean tstat_flag;
  Boolean a_flag;
  Boolean p_flag;
  Boolean s_flag;
  Boolean t_flag;
  Boolean pt_flag;
  Boolean all_flag;
  Boolean peer_flag;
  Boolean final_flag;
  Boolean letter_flag;
  Boolean fairshare_flag;
  Boolean app_flag;
  Boolean confirm_flag;
  Boolean appsumm_flag;
  Boolean conflict_flag;
  Boolean supfile_flag;

};

extern PropRepParameter *theParameters;

#endif
