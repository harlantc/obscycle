/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: RPSMailTOO
 
* DEVELOPMENT: ObsCycle
 
* DESCRIPTION:


  start up perl script with arguments:
  
  prop_rps_too_mail.pl filename

  
 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
        Ref. No.        Date
        --------        ----
	%I%		%G%
 
*H****************************************************************************/
#include "Logger.hh"
#include "RPSMailTOO.hh"


// -----------------------------------------------------------------
// Constructor
// -----------------------------------------------------------------
RPSMailTOO::RPSMailTOO(Logger *rpslog) : PropMail(rpslog)
{
  
}

// -----------------------------------------------------------------
// Destructor
// -----------------------------------------------------------------
RPSMailTOO::~RPSMailTOO()
{
}

