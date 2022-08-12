/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/


#include "gcal.h"
extern integer gcal_verbose;

int main( int nargs, char* args[] )
{
 Calendrical* state;

 state = cal_conv_init( args, nargs );
 cal_conv_interpreter( state );
 cal_free( state );
 return 0;
}

