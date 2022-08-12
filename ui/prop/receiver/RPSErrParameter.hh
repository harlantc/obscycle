/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: RPSErrParameter.hh
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:
 
        The RPSErrParameter class provides the application with the
        set of specific parameters needed to execute the Proposal 
	application RPS Error processing GUI function.

 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%

*H****************************************************************************/

#ifndef RPSERRPARAMETER_HH
#define RPSERRPARAMETER_HH

#include <string>
#include "FW_Parameter.hh"

class RPSErrParameter : public FW_Parameter
{
public:
  // Constructor
  RPSErrParameter();
  RPSErrParameter(const int argc,const char **argv);
  

  // Accessor functions

  const char *GetUser()       {return user.c_str(); }
  const char *GetPassword()   {return password.c_str(); }
  const char *GetServer()     {return server.c_str(); }
  const char *GetSQLServer()  {return sqlserver.c_str(); }
  
  const char *GetAO()         {return ao.c_str(); }
  const char *GetStatus()     {return status.c_str(); }
  Boolean IsAllProposals()    { return all_proposals; }

  const char *GetLogEnv()       { return logenv.c_str(); }
  const char *GetErrEnv()       { return errenv.c_str(); }
  const char *GetArchEnv()      { return archenv.c_str(); }

protected:
  // Objects
  void Initialize();

  // Virtual function for Printing the command line argument format for the
  // derived class
  virtual Strings MyFormat();
  
  // Virtual member function to process the derived parameters
  virtual FW_Param_Error ProcessMyParameters 
      (int &i,const int argc,const char **argv);

  // Virtual member function to validate required parameters 
  virtual FW_Param_Error  ValidateRequiredParameters();


  // Print to the output stream
  void Print(ostream&);


  string  ao;
  string  status;

  string  user;
  string  password;
  string  server;
  string  sqlserver;

  string  logenv;
  string  errenv;
  string  archenv;

  Boolean all_proposals;
  Boolean printit;
};

extern RPSErrParameter *theParameters;

#endif
