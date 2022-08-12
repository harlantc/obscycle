/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gprecess.h"
#include "jcmutils.h"
#include "jcmastro.h"
int main( int nargs, char* args[] )
{

 Celestial* state;
 utn_init_jcmlib();
 state = precess_init( args, nargs );
 precess_interpreter( state );
 celestial_free( state );
 utn_free_jcmlib();
 return 0;
}
