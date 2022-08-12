/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmfits.h"

int main( int nargs, char* args[] )
{
 FioFits file;
 file = utn_fits_open_r( args[1] );
 utn_fits_close( file );
 return 0;
}
