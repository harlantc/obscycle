/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"

double areographic_to_centric( World world, double glat )
{
 double cosg;
 double sinc;
 double one_minus_f;
 double clat;
 /*  tan c = (1-f)^2 tan g */
 one_minus_f = world->polar_radius / world->radius;
 cosg = dcosd( glat );
 sinc = ( one_minus_f ) * ( one_minus_f ) * dsind( glat );
 clat = argd_d( cosg, sinc );
 return clat;
}

double areocentric_to_graphic( World world, double clat )
{
 double cosc;
 double sing;
 double one_minus_f;
 double glat;
 /*  tan c = (1-f)^2 tan g */
 one_minus_f = world->polar_radius / world->radius;
 cosc = dcosd( clat );
 sing = dsind( clat ) / (( one_minus_f ) * ( one_minus_f ));
 glat = argd_d( cosc, sing );
 return glat;
}
