/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"
const double pi = M_PI;
const double deg_rad = 2 * M_PI / UT_CIRCLE;



/*----------------------------------------------------------------------*/
/*
 * utn_sx_sx5out_i
 *
 * Converts a sexg tuple to an long value (number of seconds).
 * 
 */

long utn_sx_sxa_to_val_i( const long sign, const long deg, const long min, const long sec )
{
 long val;
 val = (long) sign * ( ( deg * UT_SXG + min ) * UT_SXG + sec );
 return ( val );
}

/* Tuple to Value */

/*----------------------------------------------------------------------*/
/*
 *
 * Converts a sexg tuple with fraction to a double value (number of seconds).
 * Uses: sex.h
 */

double utn_sx_sxa_to_val_d( const long sign, const long deg, const long min, const long sec, const double frac )
{
 double val;
 val = sign *  ( (double) (( deg * UT_SXG + min ) * UT_SXG + sec ) + frac );
 return ( val );
}


/*----------------------------------------------------------------------*/
/*
 * ssexin_d
 *
 * Converts a value to sexagesimal
 * Uses: cmathproto.h
 * Uses: <math.h> fabs
 */

/* Value to Tuple */

void utn_sx_val_to_sxa_d( const double v, long* sign, long* deg, long* min, long* sec, double* frac )
{
 long tot_sec;

 *sign = ( v >= 0 ) ?  1 : -1 ;
 frac_d( fabs(v), &tot_sec, frac );
 *deg  = rmod_i( rmod_i( tot_sec, sec, UT_SXG ), min, UT_SXG );
}

void utn_sx_val_to_sxa_i( const long v, long* sign, long* deg, long* min, long* sec )
{
 long tot_sec;
 *sign = ( v >= 0 ) ?  1 : -1 ;
 tot_sec = ( v>=0)? v: -v;
 *deg  = rmod_i( rmod_i( tot_sec, sec, UT_SXG ), min, UT_SXG );
}


/*----------------------------------------------------------------------*/
/*
 *
 * Converts a sexg tuple with fraction to a float value (number of seconds).
 * Uses: sex.h
 */

float utn_sx_sxa_to_val_r( const long sign, const long deg, const long min, const long sec, const float frac )
{
 float val;
 val = sign *  ( (float) (( deg * UT_SXG + min ) * UT_SXG + sec ) + frac );
 return ( val );
}


/*----------------------------------------------------------------------*/
/*
 *
 * Converts a value to sexagesimal
 * Uses: cmathproto.h
 * Uses: <math.h> fabs
 */

/* Value to Tuple */

void utn_sx_val_to_sxa_r( const float v, long* sign, long* deg, long* min, long* sec, float* frac )
{
 long tot_sec;

 *sign = ( v >= 0 ) ?  1 : -1 ;
 frac_r( rabs(v), &tot_sec, frac );
 *deg  = rmod_i( rmod_i( tot_sec, sec, UT_SXG ), min, UT_SXG );
}


float utn_sx_wsxa_to_val_r( const logical sign, const long d, const long m, const long s, const float f, const long w )
{
 double r;
 r = utn_sx_sxa_to_val_d( utn_iplus( sign ), d, m, s, (double)f );
 return( (float) (r * (UT_CIRCLE/w) / (UT_SXG*UT_SXG) ));
}

void utn_sx_wval_to_sxa_r( const float r, const long w, logical* sign, long* d, long* m, long* s, float* f )
{
 double y;
 long signi;
 double g;
 y = r * UT_SXG * UT_SXG / ( UT_CIRCLE / w );
 utn_sx_val_to_sxa_d( y, &signi, d, m, s, &g );
 *f = g;
 *sign = signi >= 0;
}

double utn_sx_wsxa_to_val_d( const logical sign, const long d, const long m, const long s, const double f, const long w )
{
 double r;
 r = utn_sx_sxa_to_val_d( utn_iplus( sign ), d, m, s, f );
 return( r * (UT_CIRCLE/w) / (UT_SXG*UT_SXG) );
}

void utn_sx_wval_to_sxa_d( const double r, const long w, logical* sign, long* d, long* m, long* s, double* f )
{
 double y;
 long signi;
 y = r * UT_SXG * UT_SXG / ( UT_CIRCLE / w );
 utn_sx_val_to_sxa_d( y, &signi, d, m, s, f );
 *sign = signi >= 0;
}



void utn_sx_psx_to_sxa_d( const double r, logical* sg, long* d, long* m, long* s, double* f )
{
 long j;
 long k;
 const long sx = 100;
 *sg = r >= 0.0;
 frac_d( fabs(r), &j, f );
 k = rmod_i( j, s, sx );
 *d = rmod_i( k, m, sx );
}

void utn_sx_psx_to_sxa_i( const long r, logical* sg, long* d, long* m, long* s )
{
 long k;
 const long sx = 100;
 *sg = r >= 0;
 k = rmod_i( r, s, sx );
 *d = rmod_i( k, m, sx );
}

void utn_sx_psx_to_sxa_r( const float r, logical* sg, long* d, long* m, long* s, float* f )
{
 double g;
 double df;
 g = (double) r;
 utn_sx_psx_to_sxa_d( g, sg, d, m, s, &df );
 *f = (float) df;
}

double utn_sx_sxa_to_psx_d( const logical sg, const long d, const long m, const long s, const double f )
{
 const long sx = 100;
 return(  utn_iplus( sg ) * ( ( d * sx + m ) * sx + s + f ) );
}

float utn_sx_sxa_to_psx_r( const logical sg, const long d, const long m, const long s, const float f )
{
 double g;
 g = utn_sx_sxa_to_psx_d( sg, d, m, s, (double)f );
 return( (float)g );
}

