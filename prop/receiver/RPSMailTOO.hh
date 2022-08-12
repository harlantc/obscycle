
/*H****************************************************************************
* Copyright (c) 1995, Smithsonian Astrophysical Observatory
  You may do anything you like with this file except remove this
  copyright.
 
* FILE NAME: RPSMailTOO.hh
 
* DEVELOPEMENT: ObsCycle
 
* DESCRIPTION:
 	This class controls the mailing of all out-of-cycle TOO 
	requests.

 
* NOTES:
 
        <None>
 
* REVISION HISTORY:
 
 
*H****************************************************************************/

#ifndef RPSMAILTOO_HH
#define RPSMAILTOO_HH

#include "PropMail.hh"

class RPSMailTOO : public PropMail
{
public:
  // Constructor
  RPSMailTOO(Logger *log);

  // Destructor
  ~RPSMailTOO();


protected:

};

#endif
