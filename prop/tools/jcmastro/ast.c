/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#define JCMASTRO_DEF 
#include "jcmastro.h"
#include <math.h>

AstState ast_init( void )
{
 AstState state;
 CalState* cal_state;
 state = ast_state;
 if ( !utn_ver_init( "JCMASTRO", "V1.0" ))
 {
  utn_init_jcmlib();
  cal_state = cal_state_init();
  state = calloc( 1, sizeof( struct AstState_s ));
  utn_cs_copy( " ", state->errbuf );
  state->cal_state = cal_state;
  ast_state = state;  /* Global */
  ast_constants();
 }
 return state;
}

void ast_free( AstState state )
{
 if ( !state ) state = ast_state;
 if ( state )
  cal_state_free( state->cal_state );
 free( state );
 ast_state = NULL;
}

void ast_constants( void )
{
 double AST_ASEC_RAD; /* Crosscheck */


 TWO_THIRDS =   2.0/3.0;

 FRW_NOW        = 1.0;              /* Expansion parameter now */

 AST_LOG_K = 10.3188;
 AST_LOG_MU =  14.476820703;
 AST_LOG_KEV =17.38345;
 AST_SEC_PER_DEG = UT_SXG * UT_SXG;
 AST_RAD_ASEC =    (UT_CIRCLE/2) * ( AST_SEC_PER_DEG / M_PI );
 AST_ASEC_RAD =    0.4848136811095359949e-05;  /* Arcsec to rad crosscheck */

/* Unit definitions */
 AST_KILO = 1000.0;
 AST_MEGA = AST_KILO * AST_KILO;
 AST_JY   = 1.0E-26;
 AST_C  =   299792458.0;            
 AST_ZERO_CELSIUS = 273.155;
 AST_BOLTZMANN   = 1.380658e-23;  /* J/K */
 AST_M_AMU       = 1.6605402e-27; /* kg; mass of atomic mass unit */
 AST_M_PROTON    = 1.6726231e-27; /* Kg; mass of proton */

/* Wavelengths*/
 AST_MU_FARIR = 100.0;       /* Used as boundary in refraction calcs */
/* Solar system scales */
 AST_AU_S =        499.00478370;
 AST_RG_SUN_AU =  9.87063E-9;

 AST_ABERR_J2000_ASEC = 20.49552;
 AST_ABERR_J2000 =  AST_ABERR_J2000_ASEC / AST_RAD_ASEC;

 AST_AU        =  AST_AU_S * AST_C;
 AST_AU_KM = AST_AU / AST_KILO;
 AST_AU_PER_YR =  YR_SEC / AST_AU_KM;  /* km/s to AU/yr = 0.211 */
 AST_AU_PER_BYR=  TROPYR_SEC / AST_AU_KM;
 AST_LY = YR_SEC * AST_C;
 AST_PC   =    AST_AU * AST_RAD_ASEC;   /* parsec in m */

 AST_R_EARTH   = 6378.120;  /* Earth radius in km */

/* Earth's atmosphere */
 AST_GAS_CONST   = AST_BOLTZMANN / AST_M_AMU;  /* Universal gas constant J/K/kg (8314.51 here; was 8314.32 in SLA) */
 AST_TROPO_LAPSE = 6.5; /* K/km  canonical value */
 AST_MWT_AIR     = 28.9644;    /* Molecular weight of dry air */
 AST_MWT_H2O     = 18.0152;    /* Molecular weight of water vapor */
 AST_DPDT_EXP    = 18.360;     /* Exponent of temp dependence of water vapor pressure */

 AST_ATM_STD_TEMP =  AST_ZERO_CELSIUS;    /* K */
 AST_ATM_STD_PRESSURE = 1013.250;  /* mbar */
 AST_REFCO_ZERO = 287.941;  

 AST_TROPOPAUSE = 11.0;        /* Tropopause height */
 AST_MESOPAUSE  = 80.0;        /* Nominal mesopause height; used in refraction calc */
 AST_ATM_STD_SCALE = 7.74833;  /* Scale height at Re in km */
 AST_ATM_DH_DT  = ( AST_ATM_STD_SCALE / AST_R_EARTH ) * ( 1.0 / AST_ATM_STD_TEMP );
 

/* Log values */
 AST_LLUM =    31.03155236;
 /*                                = log10( 4 * M_PI * AST_JY ) + 2 * log10( AST_C * AST_PC * AST_KILO )      */

/* Parameters */
 FRW_MIN_DEVEL_ANGLE = 0.005;       /* Transition value for approximation */
 AST_PREC_APPROX1 = 1600.0;
 AST_PREC_APPROX2 = 2400.0;
 AST_R_SUN_DIRCOS = 1.0E-5;         /* Minimum value of 1 + p.e  = R_Sun */
 AST_REFRACT_MAXZ = 93.0;
 AST_REFRACT_BIGZ = 83.0;
 AST_REFRACT_TOL  = 5.0E-7;        /* in deg; about 1.E-8 rad */
}


