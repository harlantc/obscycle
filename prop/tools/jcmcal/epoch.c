/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmcal.h"

/* This gives julian centuries since b1900 */
double cal_jc_b1900( double t )
{
 return ( ( t - JD_B1900 ) /( 100 * YL_J2000 ));
}

double cal_jc( double t )
{
 return( ( t - JD_J2000  ) / ( 100 * YL_J2000 ) );
}


double cal_jepoch_to_jd( double epoch )
{
 double t;
 t = JD_J2000 + ( epoch - Y_J2000 ) * YL_J2000; 
 return t;
}

double cal_jd_to_jepoch( double t )
{
 return ( Y_J2000 + ( t - JD_J2000 ) / YL_J2000 );
}

double cal_jd_to_bepoch( double t )
{
 return ( Y_B1900 + ( t - JD_B1900 ) / YL_B1900 );
}

double cal_bepoch_to_jd( double be )
{
 return ( JD_B1900 + ( be - Y_B1900 ) * YL_B1900 );
}


double cal_bepoch_to_jepoch( double be )
{
 double je;

 je = be + ( DB1900 + ( be - Y_B1900 ) * DYL_B1900 ) / YL_J2000;
 return je;
}

double cal_jepoch_to_bepoch( double je )
{
 double be;

 be = je - ( DB1900 + ( je - Y_B1900 ) * DYL_B1900 ) / YL_B1900;
 return be;
}


