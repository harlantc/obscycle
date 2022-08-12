/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011,2015)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/* 4/2015 - JCM ; fix bug #14125 ; */

#define UT_STATE_DEF 1
#define UT_DEFINE_GLOBALS 1
#include "utlib.h"
#include "utnan.h"

double utn_gnan_d( int pattern );

struct UtilNull_s {
 unsigned short s_null;
 unsigned short s_inf; 
 unsigned short s_ninf;
 long i_null;
 long i_inf;
 long i_ninf;
 float r_null;
 float r_inf;
 float r_ninf;
 double d_null;
 double d_inf;
 double d_ninf;
 float r_ieee_nan;
 float r_ieee_inf;
 float r_ieee_ninf;
 double d_ieee_nan;
 double d_ieee_inf;
 double d_ieee_ninf;
};

UtilNulls utn_null_data = NULL;

UtilNulls utn_null_global( void );
int utn_isnan( double x );


UtilNulls utn_null_global( void )
{
 if ( !utn_null_data ) utn_null_data = utn_null_alloc();
 return utn_null_data;
}

/* 
 * The Single Unix Specification requires isnan to be defined, but
 * the CXC use of -DPOSIX_C_SOURCE disables it. 
 */

int utn_isnan( double x )
{
#ifdef _POSIX_C_SOURCE
 #if _POSIX_C_SOURCE < 200112L
 return dmt_dNAN(x);
 #else
 return isnan(x);
 #endif
#else
 return isnan(x);
#endif


}


/* 
 * We support NAN-like values even for longs 
 */

/*
 *------------------------------
 * GLOBALS
 *------------------------------
 */

void utn_null_init( void )
{
 utn_null_state_init( NULL );
}

void utn_null_set_ieee( void )
{
 utn_null_state_ieee( NULL );
}

void utn_null_set( char typecode, char* opt, char* pattern )
{
 utn_null_state_set( NULL, typecode, opt, pattern );
}

/*
 * NULLS
 *
 * null:alloc
 * null:state_init
 * null:free
 *Set  
 * null:set_safe
 * null:state_ieee
 * null:state_set
 *Get
 * null:null_<sird>
 * null:nan_<rd>
 * null:infinity_<sird>
 *Test
 * null:qnull_<sird>
 * null:qinf_<sird>
 */

UtilNulls utn_null_alloc( void )
{
 UtilNulls nulls = calloc( 1, sizeof( struct UtilNull_s ));
 utn_null_state_init( nulls );
 return nulls;
}

void utn_null_state_init( UtilNulls nulls )
{
 integer p = 1;
 if ( !nulls ) nulls = utn_null_global();
/* This is a cheat: the reference to utn_constants forces the compiler to include the global symbol */
 if ( utn_constants.zero == 0.0 ) { p = 0; }
 if ( p ) {};
 utn_null_state_set( nulls, 'S', "NULL", "8000" );
 utn_null_state_set( nulls, 'S', "INF",  "8001" );
 utn_null_state_set( nulls, 'S', "NINF", "8002" );
 utn_null_state_set( nulls, 'I', "NULL", "7FFFFFFF" );
 utn_null_state_set( nulls, 'I', "INF",  "7F800000" );
 utn_null_state_set( nulls, 'I', "NINF", "FF800000" );
 
 nulls->d_ieee_nan = utn_nan_create_d( UT_NAN_D );
 nulls->d_ieee_inf = utn_nan_create_d( UT_INF_D );
 nulls->d_ieee_ninf= utn_nan_create_d( UT_NINF_D );
 nulls->r_ieee_nan = utn_nan_create_r( UT_NAN_R);
 nulls->r_ieee_inf = utn_nan_create_r( UT_INF_R );
 nulls->r_ieee_ninf= utn_nan_create_r( UT_NINF_R);
 utn_null_state_ieee( nulls );
}

void utn_null_free( UtilNulls nulls )
{
 free( nulls );
}
/*
 * The old patterns; these work if NaNs cause your code to die
 */
void utn_null_set_safe( void )
{
 utn_null_set( 'R', "NULL", "77FFFFFF" );
 utn_null_set( 'D', "NULL", "77FFFFFFFFFFFFFF" );
 utn_null_set( 'R', "INF",  "7EFFFFFF" );
 utn_null_set( 'R', "NINF", "FEFFFFFF" );
 utn_null_set( 'D', "INF",  "7EFFFFFFFFFFFFFF" );
 utn_null_set( 'D', "NINF", "FEFFFFFFFFFFFFFF" );
}

/*
 *  The IEEE patterns
 */

void utn_null_state_ieee( UtilNulls nulls )
{
 if ( !nulls ) nulls = utn_null_global();
 nulls->d_null = nulls->d_ieee_nan;
 nulls->d_inf = nulls->d_ieee_inf;
 nulls->d_ninf= nulls->d_ieee_ninf;
 nulls->r_inf = nulls->r_ieee_inf;
 nulls->r_ninf= nulls->r_ieee_ninf;
 nulls->r_null= nulls->r_ieee_nan;
}

void utn_null_state_set( UtilNulls nulls, char typecode, char* opt, char* pattern )
{

 if ( !nulls) nulls = utn_null_global();
 if ( utn_cs_uceq( opt, "NULL" ))
 {
  switch( typecode )
  {
    case 'S':
      nulls->s_null  = utn_cs_read_binary_s( pattern, 'Z' );
      break;
    case 'I':
      nulls->i_null  = utn_cs_read_binary_i( pattern, 'Z' );
      break;
    case 'R':
      nulls->r_null = utn_cs_read_binary_r( pattern, 'Z' );
      break;
    case 'D':
      nulls->d_null = utn_cs_read_binary_d( pattern, 'Z' );
      break;
    default:
      break;  
  }
 }


 else if ( utn_cs_uceq( opt, "INF" ))
 {
  switch( typecode )
  {
    case 'S':
      nulls->s_inf  = utn_cs_read_binary_s( pattern, 'Z' );
      break;
    case 'I':
      nulls->i_inf  = utn_cs_read_binary_i( pattern, 'Z' );
      break;
    case 'R':
      nulls->r_inf = utn_cs_read_binary_r( pattern, 'Z' );
      break;
    case 'D':
      nulls->d_inf = utn_cs_read_binary_d( pattern, 'Z' );
      break;
    default:
      break;  
  }
 }


 else if ( utn_cs_uceq( opt, "NINF" ))
 {
  switch( typecode )
  {
    case 'S':
      nulls->s_ninf  = utn_cs_read_binary_s( pattern, 'Z' );
      break;
    case 'I':
      nulls->i_ninf  = utn_cs_read_binary_i( pattern, 'Z' );
      break;
    case 'R':
      nulls->r_ninf = utn_cs_read_binary_r( pattern, 'Z' );
      break;
    case 'D':
      nulls->d_ninf = utn_cs_read_binary_d( pattern, 'Z' );
      break;
    default:
      break;  
  }
 }
 else if ( utn_cs_uceq( opt, "NAN" ))
 {
  switch( typecode )
  {
    case 'S':
      break;
    case 'I':
      break;
    case 'R':
      nulls->r_ieee_nan = utn_cs_read_binary_r( pattern, 'Z' );
      break;
    case 'D':
      nulls->d_ieee_nan = utn_cs_read_binary_d( pattern, 'Z' );
      break;
    default:
      break;  
  }
 }
}



long utn_null_i( void )
{
 UtilNulls nulls = utn_null_global();

 return( nulls->i_null );
}

short utn_null_s( void )
{
 UtilNulls nulls = utn_null_global();

 return( nulls->s_null );
}

float utn_null_r( void )
{
 UtilNulls nulls = utn_null_global();

 return( nulls->r_null );
}

double utn_null_d( void )
{
 UtilNulls nulls = utn_null_global();

 return( nulls->d_null );
}

double utn_nan_d( void )
{
 return utn_gnan_d( UT_NAN_D );
}

double utn_gnan_d( int pattern )
{
 union NaNPattern {
  double xval;
  UT_INT32T ival[2];
 } nan_pattern;
 short offset = UT_BIGEND;
 UT_INT32T* ptr = nan_pattern.ival;
 nan_pattern.xval = 0.0;
 ptr[offset]  = 0;
 ptr[1-offset]= pattern; 
 return nan_pattern.xval;
}


float utn_nan_r( void )
{
 UtilNulls nulls = utn_null_global();

 return( nulls->r_ieee_nan );
}


long utn_infinity_i( const long i )
{
 UtilNulls nulls = utn_null_global();
 return( i >= 0 ? nulls->i_inf : nulls->i_ninf );

}

short utn_infinity_s( const long i )
{
 UtilNulls nulls = utn_null_global();

 return( i >= 0 ? nulls->s_inf : nulls->s_ninf );
}


float utn_infinity_r( const long i )
{
 UtilNulls nulls = utn_null_global();

 return( i >= 0 ? nulls->r_inf : nulls->r_ninf );
}

double utn_infinity_d( const long i )
{
 int pattern =  ( i > 0 ) ? UT_INF_D : UT_NINF_D;
 return utn_gnan_d( pattern );
}


logical utn_qnull_i( const long i )
{
 UtilNulls nulls = utn_null_global();

 return( i == nulls->i_null );
}

logical utn_qnull_s( const short i )
{
 UtilNulls nulls = utn_null_global();

 logical q;
 unsigned short ui = (unsigned short)i;
 unsigned short us = (unsigned short)nulls->s_null;
 q = (us == ui);
 return q;
}

logical utn_qnan_r( const float x )
{
 UT_INT32T* ptr = (UT_INT32T*)&x;
 UT_INT32T  val;
 if ( *ptr == UT_INF_R || *ptr == UT_NINF_R )
  return 0; /* Value is INF */

 val = *ptr & UT_NAN_R_MASK; 
 return( val == UT_NAN_R_MASK );
}

logical utn_qnan_d( const double x )
{
/* Use SUS function for efficiency: isnan is required to be in math.h for POSIX_C_SOURCE > 200112L */
 return (logical)utn_isnan( x ); 
}

logical utn_qnull_d( const double x )
{
 UtilNulls nulls = utn_null_global();

 if ( utn_isnan( nulls->d_null ))
  return utn_isnan( x );
 else
  return ( x == nulls->d_null );
}



logical utn_qnull_r( const float x )
{
 UtilNulls nulls = utn_null_global();

 if ( utn_qnan_r( nulls->r_null ))
  return utn_qnan_r( x );
 else
  return ( x == nulls->r_null );
} 


logical utn_qinf_i( const long i )
{
 UtilNulls nulls = utn_null_global();

 return( i == nulls->i_inf || i == nulls->i_ninf );
}

logical utn_qinf_s( const short i )
{
 UtilNulls nulls = utn_null_global();

 unsigned short ui = (unsigned short)i;
 unsigned short us = (unsigned short)nulls->s_inf;
 unsigned short uns = (unsigned short)nulls->s_ninf;
 return( ui == us || ui == uns );
}

logical utn_qinf_r( const float x )
{
 UtilNulls nulls = utn_null_global();

 return( x == nulls->r_inf || x == nulls->r_ninf );
}
logical utn_qinf_d( const double x )
{
 UtilNulls nulls = utn_null_global();

 return( x == nulls->d_inf || x == nulls->d_ninf );
}

/* We create this once and store it for later use */

double utn_nan_create_d( UT_INT32T pattern )
{
 union NaNPatternD {
  double xval;
  UT_INT32T ival[2];
 } nan_pattern;

 short offset=UT_BIGEND;
 UT_INT32T* ptr= nan_pattern.ival;
 nan_pattern.xval = 0.0;     /* Clear buffer in case double > 2 int32 */
 ptr[offset]   = 0;
 ptr[1-offset] = pattern;
 return nan_pattern.xval;
}


float utn_nan_create_r( UT_INT32T pattern )
{
 union NaNPatternR {
  float xval;
  UT_INT32T ival[1];
 } nan_pattern;
 UT_INT32T* ptr = nan_pattern.ival;
 nan_pattern.xval = 0.0;
 *ptr = pattern;
 return nan_pattern.xval;
}

#if 0
FNANMASK 0x7F80 0x7F00  (VMS)  0x7F800000  val  0x7FC00000 (new)
DNANMASK 0x7FF0  0x7FE0 (VMS)  0x7FF00000  val  0x7FF80000 (new)
DINFMASK 0x7FF0
DNINFMASK 0xFFF0


BIG_END is opposite of BYTE_SWAPPED.
						sys/byteorder.h
               ALPHA UNIX                      BIG_END             NOT
              <nan.h>
                                                 A = 1 B=0 C=0     A=0 B=1 C=3
FNAN(X)
        unsigned int* ip = &x;	               ulong* sp = &x;
        unsigned int ival = *ip;                 sval = sp[0] & 0x7F00

        float if = (float)ival;                  if ( sval == 0x7F00 ) return 1
        if ( if == 0.0 )                         if ( sval == 0      ) return 2     /* Denorm underflow */
         return 0;
        else
         mask = ival & 0x7F800000
        if ( mask == 0x7F800000) return 1;
No      if  (mask == 0       ) return 2;
        else return 0;

          

DNAN(X)
        IsNANorINF(X)                          x=0? 0
					       ulong* sp = &x;

              			       sval = sp[B] & 0x7FF0
                                               if ( sval == 0x7F00 ) return 1
                                               if ( sval == 0      ) return 2
			
DINF(X)
        IsNANorINF(X) &&                       if ( sp[B] == 0x7FF0
        IsINF(X) &&
        IsPosNAN(X)
NINF(X)
        IsNANorINF(X) && 
        IsINF(X) &&
        IsNegNAN(X)
MAKE_DNAN(X)  
             dnan* np = (dnan*)&x;            ulong* up = (ulong*)&x;
             np->nan_parts.exponent = 0x7FF   up[B] = 0x7FF80000 up[A] = 0
             np->nan_parts.qnan_bit = 0x1
MAKE_FNAN(X)
             unsigned int* ip = &x;           up[0] = 0x7FF80000
             *ip = 0x7FC00000

             
#endif
