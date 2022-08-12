/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

logical utn_clip_r( const float x, const float y, const float x1, const float x2, const float yy1, const float yy2 )
{
 return(  x >= x1 && x <= x2 && y >= yy1 && y <= yy2 );
}
logical utn_clip_d( const double x, const double y, const double x1, const double x2, const double yy1, const double yy2 )
{
 return(  x >= x1 && x <= x2 && y >= yy1 && y <= yy2 );
}

double utn_rlap_check_d( const double x, const double yy1, const double yy2, logical* left, logical* right )
{
 double u;
 *left = x < yy1;
 *right = x > yy2;
 u = *left  ? yy1 : ( *right ? yy2 : x );
 return( u );
}
float utn_rlap_check_r( const float x, const float yy1, const float yy2, logical* left, logical* right )
{
 float u;
 *left = x < yy1;
 *right = x > yy2;
 u = *left  ? yy1 : ( *right ? yy2 : x );
 return( u );
}

logical utn_overlap_r( const float x1, const float x2, const float yy1, const float yy2,
    float* u1, float* u2 )
{
 logical left1, left2, right1, right2;

 *u1 = rlap_check_r( x1, yy1, yy2, &left1, &right1 );
 *u2 = rlap_check_r( x2, yy1, yy2, &left2, &right2 );
 return( !( right1 || left2 ) );
}

logical utn_overlap_d( const double x1, const double x2, const double yy1, const double yy2,
    double* u1, double* u2 )
{
 logical left1, left2, right1, right2;

 *u1 = rlap_check_d( x1, yy1, yy2, &left1, &right1 );
 *u2 = rlap_check_d( x2, yy1, yy2, &left2, &right2 );
 return( !( right1 || left2 ) );
}


