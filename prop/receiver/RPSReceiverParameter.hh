/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: %M%
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:
 
        The RPSReceiverParameter class provides the application with the
        set of specific parameters needed to execute the Proposal 
	application RPS Receiver function.

 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%
 
*H****************************************************************************/

#ifndef RPSRECEIVERPARAMETER_HH
#define RPSRECEIVERPARAMETER_HH

#include "FW_Parameter.hh"

class RPSReceiverParameter : public FW_Parameter
{
public:
  // Constructor
  RPSReceiverParameter();
  RPSReceiverParameter(const int argc,const char **argv);
  

  // Accessor functions

  int GetDuration()       { return duration; }
  Boolean IsTOO()  { return rps_too; }

protected:
  // Objects
  void Initialize();

  // Virtual function for Printing the command line argument format for the
  // derived class
  virtual Strings MyFormat();
  
  // Virtual member function to process the derived parameters
  virtual FW_Param_Error ProcessMyParameters
    (int &ii,const int argc,const char **argv);

  // Virtual member function to validate required parameters
  virtual FW_Param_Error  ValidateRequiredParameters();



  // Print to the output stream
  void Print(ostream&);

  int duration;
  Boolean printit;
  Boolean rps_too;
};

extern RPSReceiverParameter *theParameters;

#endif
