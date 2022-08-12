/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "nhlib.h"


int main( int nargs, char* args[] )
{
 Colden state;

 state = colden_init( args, nargs );
 colden_interpreter( state );
 colden_free( state );
 return 0;
}

