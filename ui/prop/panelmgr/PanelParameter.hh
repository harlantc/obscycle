/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: %M%
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:
 
        The parameter class provides the application with the
        set of specific parameters needed to execute the Proposal 
	Panel Manager GUI application .

 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%

*H****************************************************************************/

#ifndef PANELPARAMETER_HH
#define PANELPARAMETER_HH

#include "ProposalParameter.hh"

class PanelParameter : public ProposalParameter
{
public:
  // Constructor
  PanelParameter();
  PanelParameter(const int argc,const char **argv);
  

  // Accessor functions

protected:
  // Objects

  
};

extern PanelParameter *theParameters;

#endif
