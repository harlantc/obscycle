/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmutils.h"
#include "jcmio.h"
#include <math.h>
#define UT_UNIT_MANT_POS 0
#define UT_SI_NPFX 8
#define UT_SI_MICRO "\\mu"
UnitDomain utn_cs_unit_init( const char* filename ); 
void utn_cs_unit_free( UnitDomain u ); 
void utn_cs_unit_texmode( UnitDomain u, const logical q ); 
void utn_cs_unit_mult( UnitDomain unit_domain, const char* u1, const char* u2, char* u, const long siz ); 
void utn_cs_unit_div( UnitDomain u, const char* u1, const char* u2, char* udiv, const long siz ); 
void utn_cs_unit_exp( UnitDomain u, const char* unit1, const double power, char* unit2, const long siz ); 
void utn_cs_unit_resolve( UnitDomain unit_domain, const char* u1, char* u2, const long siz ); 
void utn_cs_unit_dparse( UnitDomain unit_domain, const char* u1, char* u2, const long siz ); 
void utn_si_write_r( float x, char* cy );  
void utn_si_write_d( double x, char* cy ); 
float utn_si_read_r( char* cy );  
double utn_si_read_d( char* cy ); 
long utn_si_exp( char* pfx, logical* ok ); 
void utn_si_getpfx( long exponent, char* pfx, long* remainder ); 

void utn_unit_si_resolve( long n, char** unit, long* fac );
void utn_unit_compose( char** base_unit, double* exp_unit, long* si_fac, long n_uo, logical texmode, char* unit, long siz );
void utn_unit_compitem( char** ptr, char* item, double exp_unit, long si_fac, long* siz );
void utn_unit_parse_mantissa( char* base_unit, double* exp_unit );
void utn_unit_parse_exp( const char* utn_unit_item, char* base_unit, integer m, double* exp_unit );
void utn_unit_tmult( long* n1, char** base1, double* exponent1, long* si_fac1,
          long* n2, char** base2, double* exponent2, long* si_fac2, long power );

void utn_si_tmult( long fac1, double exponent1, long fac2, double exponent2, long* fac, double* mantissa );
long utn_si_eval_r( float x );
long utn_si_eval_d( double x );
void utn_si_calc_d( double x, double* y, char* pfx, long* exponent, char* mode );
void utn_si_calc_r( float x, float* y, char* pfx, long* exponent, char* mode );
void utn_si_check( char* unit, long* factor, long* pos );


