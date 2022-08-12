/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#ifndef JCMASTRO_H
#define JCMASTRO_H


#include <math.h>
#include "jcmcal.h"
/* AU = 1.49597870691 E11 m */

#include "unitlib.h"

#ifdef JCMASTRO_DEF
#define AST_GLOBAL
#else
#define AST_GLOBAL extern
#endif

#define WORLD_LUNA     0x1
#define WORLD_IS_EARTH 0x2
#define WORLD_PLANET   0x4
#define WORLD_ASTEROID 0x8
#define WORLD_STAR     0x10
#define WORLD_MOON     0x20
#define WORLD_COMET    0x40
#define WORLD_LP       0x80


#define AST_MAG_DEFAULT_FILE "mag.dat"

#define ANGSTROM_KEV 12.39854

/* Megatons per petaJoule */
#define MEGATON_PJ 4.18

#define AST_OBL_METHOD_STD 0
#define AST_OBL_METHOD_ET  1
#define AST_OBL_METHOD_EA  2
#define AST_OBL_METHOD_M   3

#define AST_CON_ABBR       0
#define AST_CON_NOMINATIVE 1
#define AST_CON_GENITIVE   2

#define AST_PREC_OLD_B 0x01

#define AST_FK5_SIMPLE 1
#define AST_FK5_FULL   0

#define AST_FREQ_PARSE_ERR  1
#define AST_NO_MAG_FILE_ERR 2
#define AST_BAD_FREQ_ERR    3
#define AST_BAD_FSYS_ERR    4

AST_GLOBAL double AST_LOG_K;
AST_GLOBAL double AST_LOG_MU;
AST_GLOBAL double AST_LOG_KEV;

#define AST_MAG_DEX 0.4
#define AST_VEGA_AZ   +0.03
#define AST_VEGA_AZIR +0.02
#define AST_VEGA_AB   +0.01625

/* Number of nutation coeffs */
#define AST_NUT_MAX 200
#define AST_MAX_ITEMS 10

/* GM per teraton */
#define GM_per_teraton 6.673E-5


/* How many IAU constellations are there? */

#define AST_NO_CONSTELL 88
#define AST_CON_MAX_BDY 1000

          /* Exponent in several formulae */
AST_GLOBAL double  TWO_THIRDS;
AST_GLOBAL double AST_KILO;
AST_GLOBAL double AST_MEGA;
AST_GLOBAL double AST_JY;
AST_GLOBAL double AST_SEC_PER_DEG;
AST_GLOBAL double AST_RAD_ASEC;
AST_GLOBAL double AST_ZERO_CELSIUS;
AST_GLOBAL double AST_PREC_APPROX1;
AST_GLOBAL double AST_PREC_APPROX2;
AST_GLOBAL double AST_C; /* c (m/s) */
AST_GLOBAL double AST_LY;
AST_GLOBAL double AST_AU_S; /* Light time for 1 AU in seconds */
AST_GLOBAL double AST_AU;
AST_GLOBAL double AST_AU_KM;
AST_GLOBAL double AST_AU_PER_YR;
AST_GLOBAL double AST_AU_PER_BYR;
AST_GLOBAL double AST_RG_SUN_AU;
AST_GLOBAL double AST_PC;
AST_GLOBAL double AST_LLUM;
AST_GLOBAL double AST_R_EARTH;
AST_GLOBAL double FRW_NOW;
AST_GLOBAL double FRW_MIN_DEVEL_ANGLE;
AST_GLOBAL double AST_R_SUN_DIRCOS;
AST_GLOBAL double AST_ABERR_J2000_ASEC;
AST_GLOBAL double AST_ABERR_J2000;
AST_GLOBAL double AST_TROPO_LAPSE;
AST_GLOBAL double AST_REFRACT_MAXZ;
AST_GLOBAL double AST_REFRACT_BIGZ;
AST_GLOBAL double AST_REFRACT_TOL;
AST_GLOBAL double AST_GAS_CONST;
AST_GLOBAL double AST_MWT_AIR;
AST_GLOBAL double AST_MWT_H2O;
AST_GLOBAL double AST_DPDT_EXP;
AST_GLOBAL double AST_TROPOPAUSE;
AST_GLOBAL double AST_MESOPAUSE;
AST_GLOBAL double AST_REFCO_ZERO;
AST_GLOBAL double AST_BOLTZMANN;
AST_GLOBAL double AST_M_AMU;
AST_GLOBAL double AST_M_PROTON;
AST_GLOBAL double AST_ATM_STD_TEMP;
AST_GLOBAL double AST_ATM_STD_PRESSURE;
AST_GLOBAL double AST_ATM_STD_SCALE;
AST_GLOBAL double AST_ATM_DH_DT;
AST_GLOBAL double AST_MU_FARIR;


#define AST_OBL_J2000_COS 0.9174820620691818
#define AST_OBL_J2000_SIN 0.3977771559319137


/* Structure to calculate K correction including line corrections
   based on Veron and Veron.
 */
typedef struct {
 double alpha;
 integer n;
 double z[200];
 double mv[200];
} K_Correction;

/* Structure to hold constellations */
#define AST_CON_ERROR    0
#define AST_CON_MERIDIAN 1
#define AST_CON_PARALLEL 2

typedef struct {
 integer con1;
 integer con2;
 double ra1;
 double dec1;
 double ra2;
 double dec2;
 integer type;
} ConstellationBoundary;

typedef struct ConstellationData_s {
 integer n;
 char** abbr;
 char** nominative;
 char** genitive;
 integer nmer;
 integer npar;
 ConstellationBoundary* meridians;
 ConstellationBoundary* parallels; 
} *ConstellationData;


typedef struct {
 integer d[AST_NUT_MAX];
 integer el[AST_NUT_MAX];
 integer elp[AST_NUT_MAX];
 integer om[AST_NUT_MAX];
 integer f[AST_NUT_MAX]; 
 double  p0[AST_NUT_MAX];
 double  p1[AST_NUT_MAX];
 double  e0[AST_NUT_MAX];
 double  e1[AST_NUT_MAX];
 integer n;
 Filename filename;
} NutCoeffs;

/* Apparent/mean place common block */

typedef struct {
 double t;
 double equinox;
 double epoch;  /*  Time interval (yr) for proper motion correction */
 double bary_state[6]; /* Barycenter pos */
 double helio_state[6]; /* Helio state */
 double gr2e;    /* Light deflection */
 double ab;        /* Aberration const */
 double pnm[9];  /* Precnut matrix */
 NutCoeffs* nut;
} EarthState;

#define COS_MODE_FRW 0
#define COS_MODE_GEN  3
#define COS_MODE_PEN1 1
#define COS_MODE_PEN  2

typedef struct {
 double h0;
 double omega;
 double lambda;
 double th0;
 double chi0;
 double t0;
 double eta0;
 integer k;
 TextWord unit;
 double omega_r;  /* Radiation density */
 double da; /* Integration step */
 integer mode;
} Cosmology;

typedef struct MagnitudeBand_s {
 TextWord name;
 double u;
 double u1,u2;
 double f0;
} *MagnitudeBand;

typedef struct MagnitudeBandList_s {
 integer nmax;
 integer n;
 MagnitudeBand* data;
 TextCard name;
} *MagnitudeBandList;


enum AST_WSYS_TYPE { AST_WSYS_FREQ, AST_WSYS_HZ, AST_WSYS_KEV, AST_WSYS_NM, AST_WSYS_K, AST_WSYS_A, AST_WSYS_MU, AST_WSYS_M, AST_WSYS_CM };

struct WaveSys_s {
 char tag[8];
 integer code;
 integer type;
 TextWord name;
 TextWord prompt;
 TextWord fmt;
 double scale;
 double (*from_freq)(double);
 double (*to_freq)(double);
};

typedef struct WaveSys_s WaveSysData;
typedef WaveSysData*     WaveSys;

struct WaveSysList_s {
 WaveSys data;
 WaveSys from;
 WaveSys to;
 MagnitudeBandList maglist;
 logical interact; /* Interact mode */
 integer mode;  /* Print mode */
 TextBuf errbuf;
};

typedef struct WaveSysList_s *WaveSysList;

typedef struct EarthAtmosphere_s {
 double lat;
 double lon;   /* deg */
 double height; /* km */
 double temp;
 double pressure;
 double humidity;
 double lapse;
 double g;      /* Estimated g */
 double wavelength;    /* Reference wavelength */
 double rates[4];    /* Lapse rates */
 double tcoeffs[6];  /* Troposphere coefficients */
 double dnt;          /* Tropopause refractive index */
 double rtol;         /* Tolerance for refraction */
} *EarthAtmosphere;



typedef struct AstState_s {
 TextBuf errbuf; 
 integer errmask; 
 CalState* cal_state;
} *AstState;


typedef struct World_s *World;
struct World_s {
 TextCard name;
 TextCard alt_name;
 TextCard primary_name; 
 double mass;
 double GM;    /* Gravitational coupling, km**3/s**2 */
 double rotation_period;   /* Rotation period, s */
 double surface_period;
 double stationary_radius;  /* Radius of stationary orbit, km */
 double radius;     /* Radius of primary surface, km */
 double polar_radius;
 double stationary_motion;
 double J2;
 double k3_power;   /* Power law for Kepler 3, usually 0.666 */
 double precess_const;   /* Precession constant */
 double precess_power;  /* Power law for precession, usually 7/3 */
 double a;
 double e;
 double i;
 double orbital_period;
 double L1;
 double sphere;
 integer mass_flag;
 integer radius_flag;
 TextWord eph_source;
 integer eph_id;
 integer idmask;
 logical ew_longitude;  /* Longitude measured -180/180 or 0/360? */
 integer id;
 char idname[10];
 double q;  /* Periapsis */
 double epoch;
 double node;
 double aop;
 double manom;
 double rates[7];
 void (*orient)( World world, double t, double* ex, double* ey, double* ez );  /* ICRS body axes vs time */
 void (*state)( World world, double t, integer framecode, double* pos, double* vel );  /* ICRS state versus time */ 
};



typedef struct JplState_s JplState;
typedef struct JplEph_s JplEph;



#ifdef JCMASTRO_DEF
AST_GLOBAL AstState ast_state = NULL;
#else
AST_GLOBAL AstState ast_state;
#endif

/* Useful enums for low number elements */
typedef enum  { Z_UNK = 0, 
                Z_H, Z_He, Z_Li, Z_Be, Z_B, Z_C, Z_N, Z_O, Z_F,
                Z_Ne, Z_Na, Z_Mg, Z_Al, Z_Si, Z_P, Z_S, Z_Cl, Z_Ar,
                Z_K, Z_Ca, Z_Sc, Z_Ti, Z_V, Z_Cr, Z_Mn, Z_Fe, Z_Co, Z_Ni, Z_Cu } ElementEnum;
#define Z_Au 79


AstState ast_init( void );
void ast_constants( void );

double ast_absmag( K_Correction* kcor, double v, double z, double omega, double h0 );
double frw_ldistf( double z, double omega, double h0 );
K_Correction* ast_absmag_init( char* infile, double alpha );
void ast_absmag_free( K_Correction* kcor );

void frw_set_Lambda( Cosmology* c, double Lambda );
void frw_set_Omega( Cosmology* c, double Omega );
void frw_set_H0( Cosmology* c, double H0 );
Cosmology* frw_init( double h0, double omega, double lambda );
void frw_free( Cosmology* c );
void frw_agecalc( Cosmology* c );
void frw_set( Cosmology* c, double h0, double omega, double lambda );
void frw_get_unit( Cosmology* c, char* opt );
double frw_hubble_scale( Cosmology* c, char* opt );
double frw_get_scale( Cosmology* c, char* opt );

double frw_proper_dist( Cosmology* c, double z );
double frw_a_to_coord_dist( Cosmology* c, double a );
double frw_t_to_z( Cosmology* c, double t );

double frw_hubble_dist( Cosmology* c, double z );
double frw_proper_to_coord( Cosmology* c, double chi );
double frw_a_to_z( double a );
double frw_z_to_a( double z );

double frw_z_to_omega( Cosmology* c, double z );
double frw_conformal_time( Cosmology* c, double z );
double frw_a_to_omega( Cosmology* c, double a );
double frw_a_to_dprop( Cosmology* c, double a );
double frw_coord_dist( Cosmology* c, double z );
double frw_a_to_htime( Cosmology* c, double a );
double frw_hubble_param( Cosmology* c, double z );
double frw_development_angle( Cosmology* c, double a );
double frw_a_to_cosmic_time( Cosmology* c, double a );
double frw_cosmic_time( Cosmology* c, double z );
double frw_t_to_a( Cosmology* c, double t );
double frw_a_to_arc( Cosmology* c, double a );
double frw_a_to_vol( Cosmology* c, double a );
double frw_volume( Cosmology* c, double z );
double frw_log_fnu( Cosmology* c, double L, double z );
double frw_log_flux( Cosmology* c, double L, double z );
double frw_log_lnu( Cosmology* c, double Snu, double z );
double frw_log_lum_area( Cosmology* c, double z );
double frw_angdist( Cosmology* c, double z );
double frw_ldist( Cosmology* c, double z );


EarthState* ast_earth_state_init_equinox( double equinox, double t ); 
EarthState* ast_earth_state_init( double t );
void ast_earth_state_free( EarthState* state );

void ast_obl_ecliptic_j2000_vector( double* obl );
double ast_obl_ecliptic_alt( double t, integer method );

void ast_light_deflect_invert( double* p, double* helio_pos, double gr2e, double* p1 );
void ast_light_deflect( double* p, double* helio_pos, double gr2e, double* p1 );
void ast_aberrate( double* p, double* bary_vel, double ab_const, double* p1 );
void ast_aberrate_invert( double* p2, double* bary_vel, double ab, double* p1 );
void ast_aberration_calc( EarthState* aber, double* p, double* pa );
void ast_correct_proper_motion( double* mean, double* proper_motion,  double parallax, double radial_vel, double dt_yr, double* corr );
void ast_apply_proper_motion( double* mean, double* proper_motion, double parallax, double radial_vel, double dt_yr, double* p );
void ast_correct_from_barycenter( EarthState* earth, double* mean, double parallax, double* apparent );
void ast_correct_vector_from_barycenter( double* p1, double parallax, double* bary_state, double* p );

void ast_mean_to_apparent( EarthState* aber, double* mean, double * result );
void ast_mean_to_apparent_pm( EarthState* aber,
               double* mean, double* proper_motion, double parallax,
               double radial_vel, double* result );
void ast_apparent_to_mean( EarthState* aber, double* apparent, double* mean );

void ast_precess_eval( double* celin, double* matrix, double* celout );
logical ast_prec_matrix_b( double epoch1, double epoch2, double* rmat, integer mode );
logical ast_prec_matrix_j( double epoch1, double epoch2, double* rmat );
void ast_prec_calc_angles_j( double t, double* angles );
void ast_prec_angles_j( double epoch1, double epoch2, double* zeta );
void ast_prec_angles_j_approx( double epoch1, double epoch2, double* euler );
void ast_prec_angles_j_simon( double epoch1, double epoch2, double* euler );
void ast_prec_angles_b( double epoch1, double epoch2, double* euler );

void ast_precnutmat( NutCoeffs* nut, double equinox, double t, double* matrix );
void ast_triad( double xlong, double xlat, double* ex, double* ey, double* ez );

void ast_ec_to_cel( double el, double eb, double epoch, double* ra, double* dec );
void ast_cel_to_ec( double ra, double dec, double epoch, double* el, double* eb );

double ast_obl_ecliptic( double t );
double ast_lunar_mean_node( double t );
double ast_lunar_mean_elongation( double t );
double ast_lunar_mean_longitude_from_node( double t );
double ast_lunar_mean_longitude_from_peri( double t );
double ast_solar_mean_longitude_from_peri( double t );
double ast_eqeqx( NutCoeffs* nut, double t );
void ast_nut_calc( NutCoeffs* nut, double t, double* angles );
NutCoeffs* ast_nut_read( char* filename );
void ast_nut_free( NutCoeffs* nut );
void ast_nut_matrix( NutCoeffs* nut, double t, double* rmatn );

double ast_solar_light_deflection( double r );
double ast_earth_aberration( double* bary_vel );


void ast_ec_mat( double epoch, double* rmat );
void ast_calc_eterms( double ecc, double peri, double obl, double* eterms );
void ast_earth_eterms( double t, double *eterms );
void ast_earth_eterms_b( double* eterms );
void ast_sub_eterms( double* p1, double* eterms, double* p2 );
double ast_earth_ecc( double t );
double ast_earth_perihelion( double t );
logical coord_read( double* ra, double* dec );

void ast_fk4_to_fk5_matrix( double* em );
void ast_fk4_to_fk5_matrix_old( double* em );
void ast_fk5_to_fk4_matrix_old( double* em );
void ast_fk5_to_fk4_matrix( double* em );
void ast_parallax_to_state( double* cel, double* pm, double rv, double parallax,  double* v1 );
void ast_state_to_parallax( double* v1, double p0, double* cel, double* pm, double* rv, double* parallax );
void ast_add_eterms6( double* v1, double* eterms, double* v2 );
void ast_sub_eterms6( double* p1, double* eterms, double* p2 );
void ast_b1950_to_j2000_full( double* cel_b, double* pm_b, double parallax_b, double vel_b,
 double epoch_b, integer mode, double* cel_j, double* pm_j, double* parallax_j, double* vel_j, logical old );
void ast_b1950_to_j2000( double* cel_b, double* cel_j );
void ast_j2000_to_b1950_full( double* cel_j, double* pm_j, double parallax_j, double vel_j,
  double epoch_b, integer mode, double* cel_b, double* pm_b, double* parallax_b, double* vel_b, logical old );
void ast_j2000_to_b1950( double* cel_j, double* cel_b );

void ast_cel_to_gal_b( double ra, double dec, double* ll, double* lb );
void ast_gal_to_cel_b( double ll, double lb, double* ra, double* dec );
void ast_cel_to_gal( double ra, double dec, double* ll, double* lb );
void ast_gal_to_cel( double ll, double lb, double* ra, double* dec );
void ast_gal_matrix_b( double* rmat );
void ast_gal_matrix_j( double* rmat );
void ast_sgal_matrix( double* rmat );
void ast_gal_to_sgal( double ll, double lb, double* sgl, double* sgb );
void ast_sgal_to_gal( double sl, double sb, double* gl, double* gb );

void ast_state_zero_vel( double* v1, double dt );
void ast_state_to_parallax_zero( double* v1, double* cel, double* pm, double* rv, double* parallax );

double ast_kev_to_freq( double e );
double ast_freq_to_kev( double u );
double ast_freq_copy( double u );
double ast_freq_to_hz( double u );
double ast_hz_to_freq( double f );
double ast_freq_to_lam( double u );
double ast_lam_to_freq( double mu );
double ast_freq_to_temp( double u );
double ast_temp_to_freq( double T );


WaveSysList ast_wsys_init( void );
void ast_wsys_list( WaveSysList list );
void ast_wsys_format( WaveSys w, double x, char* buf, integer siz );
logical ast_wsys_read( MagnitudeBandList list, WaveSys w, double* u );
void ast_wsys_print( WaveSys f, WaveSys t, char* buf1, char* buf2, integer mode );

WaveSys ast_wsys_type( WaveSysList list, char* opt );
double ast_wsys_convert( WaveSys wf, WaveSys wt, double x );
void ast_wsys_header( WaveSys wf, WaveSys wt, logical interact, integer mode );
integer ast_wave_interpreter( char* args[], integer nargs );
integer ast_wsys_command( WaveSysList list, char* opt );
void ast_wave_listopts( void );
WaveSysList ast_wave_init( char* args[], integer nargs );
void ast_wsys_loop( MagnitudeBandList list, WaveSys f, WaveSys t, logical interact, integer mode, logical repeat );
void ast_wsys_free( WaveSysList list );

double ast_mag_calc( MagnitudeBandList list, double mag, char* bandname );
MagnitudeBand ast_mag_band( MagnitudeBandList list, char* bandname );
void ast_mag_band_list_free( MagnitudeBandList list );
MagnitudeBandList ast_mag_band_read( char* filename );
double ast_mag_delta( char* type );
void ast_band_regime_name( integer i, char* name );
integer ast_band_find_regime( double u );
double ast_mag_freq( MagnitudeBandList list, char* bandname );
double ast_parse_freq( MagnitudeBandList list, char* buf );
double ast_parse_freq_unit( double nu, char* opt );
void ast_compose_freq( double u, char* buf, integer maxlen );

void ast_earth_atm_refindex( EarthAtmosphere atm, double r0, double r, 
                               double T0, double* Tp, double* dn, double* rdndr );


void ast_atm_free( EarthAtmosphere atm );
EarthAtmosphere ast_atm_init( void );
void ast_atm_set_location( EarthAtmosphere atm, double lon, double lat, double height );
void ast_atm_set_conditions( EarthAtmosphere atm, double temp, double pressure,
 double humidity );
void ast_atm_eval_rates( EarthAtmosphere atm );
void ast_atm_eval_coeffs( EarthAtmosphere atm, double mu );
double ast_earth_g( double lat, double height );
void ast_atm_set_lapse( EarthAtmosphere atm, double lapse );
void ast_atm_tropo_coeffs( double mu, double temp, double pressure, double humidity,
 double lapse,  double g, double* c );
void ast_earth_atm_refindex( EarthAtmosphere atm, double r0, double r, 
                               double T0, double* Tp, double* dn, double* rdndr );
double ast_earth_tropo_ref1( double mu );
double ast_earth_tropo_ref2( double mu );
double ast_earth_tropo_pwo( double T, double p, double humidity );
double ast_earth_tropo_psat( double temp, double pressure );
void ast_atm_set_tropo_index( EarthAtmosphere atm, double dnt );
void ast_atm_set_refract_tol( EarthAtmosphere atm, double tol );
double ast_refract( double zobs, double mu, double lat, double height, double temp, double pressure,
                  double humidity, double lapse );
double ast_refract_eval( EarthAtmosphere atm, double zobs );
void ast_refract_consts( EarthAtmosphere atm, double mu, double* a, double* b );
void ast_refract_consts_quick( double T, double p, double humidity, double mu, double* a, double* b );
void ast_refract_consts_quick_a( EarthAtmosphere atm, double mu, double* a, double* b );
double ast_atm_scale_height( double temp );
double ast_earth_tropo_ref3( double mu, double T );
void ast_refract_vector( double* v1, double a, double b, double* v2 );
double ast_refract_apply( double z, double a, double b );
double ast_refract_deapply( double z, double a, double b );

integer ast_constell_parse( ConstellationData data, char** ptr );
integer ast_constell_get_no( ConstellationData data, char* name );
void ast_constell_free( ConstellationData data );
void ast_constell_get_name( ConstellationData data, integer con, integer mode, char* name, integer maxlen );
integer ast_constell_get_n( ConstellationData data );

ConstellationData ast_constell_alloc( char* names_file );
ConstellationData ast_constell_init( char* names_file, char* boundary_file );
void ast_constell_read_boundaries( ConstellationData data, char* boundary_file );
void ast_iau_format_arg( double dec, integer nd, char* ybuf );
void ast_iau_name( double ra, double dec, char* mode, char* name, integer maxlen );
integer ast_constell_find( ConstellationData data, double ra, double dec, char* abbr );

double asteroid_diameter_estimate( double H, integer mode );
void solar_system_summary( GenList system );
GenList solar_system_default( void );
World solar_system_earth( GenList system );
World solar_system_world( GenList system, char* name );
integer solar_system_n( GenList system );
World solar_system_world_no( GenList system, integer no );
void solar_system_list( GenList system );

World world_earth( void );
void world_init( World primary );
integer world_init_default( World primary, char* name );
void world_set( World primary, char* name, double GM, double radius, double period, double J2 );
void world_list( World primary );
double kepler_precess_factor( World primary, double period, double e );
void kepler_orbit_heights( World primary, double a, double e, double* ph, double* ah );
double kepler_three( World primary, double period );
double kepler_height( World primary, double a, double e, double true_anom );
double kepler_ascent_angle( World primary, double height, double a, double e );
double kepler_angular_momentum( World primary, double a, double e );
double kepler_speed( World primary, double height, double a, double e );
double kepler_circular_speed( World primary, double height );
double kepler_period( World primary, double a );
double kepler_mean_motion( World primary, double a );
void kepler_eval_pos( World primary, double a, double e, double inc, double node, double arg_peri, double true_anom, double* height, double* lat, double* ra );
void kepler_orbit_type( World primary, double period, double a, double e, double i, char* type );
void kepler_orbit_ae( World primary, double ph, double ah, double* ap, double* ep );

double kepler_radius( double a, double e, double true_anom );
double kepler_solve( double mean_motion, double e, double t0, double t );
double kepler_mean_anom( double mean_motion, double e, double t0, double t );
double kepler_next_pass( double mean_motion, double t0, double t, double mean_anom );
integer kepler_rev( double mean_motion, double e, double arg_peri, double t0, double t );
double kepler_conic( double true, double e );
double kepler_equation( double M, double e );
double kepler_nodal_angle_to_latitude( double nodal_angle, double inc );
double kepler_latitude_to_nodal_angle( double latitude, double inc );
double kepler_nodal_angle_to_longitude( double nodal_angle, double inc );


JplEph* jpl_alloc( char* infile );
void jpl_free( JplEph* eph );
void jpl_print_state( JplEph* eph );
void jpl_print_planet( JplEph* eph, JplState* state, integer i );
void jpl_track( JplEph* eph, integer id, double t1, double t2, double dt, JplState* state );
JplEph* jpl_open( char* infile );
void jpl_close( JplEph* eph );
void jpl_ephem_eval( JplEph* eph, integer planet, edouble et, double* pv  );
void jpl_print( JplEph* eph );
void jpl_eval( JplEph* eph, edouble et );
void jpl_init_state( JplState* state );
void jpl_set( JplEph* eph, char* name, integer mode );
integer jpl_offset( char* name );
void jpl_eph_rec( JplEph* eph, integer nr );
void orrery_close( void );
void orrery_eval( World world, edouble et, double* pv );
double world_radius( World primary );
void world_get_name( World primary, char* name, long maxlen );
double kepler_eccentric_speed( World primary, double a, double e );
double world_sidereal_angle( World primary, double t );
void coord_write( double ra, double dec, char* buf );
logical coord_read_c( char* buf, double* ra, double* dec );
void coord_unpack( double* xp, double* yp );

double frw_get_H0( Cosmology* c );
double frw_get_Omega( Cosmology* c );
double frw_get_Lambda( Cosmology* c );
void frw_set_mode( Cosmology* c, char* opt );
void frw_get_mode( Cosmology* c, char* opt );

/* Local only */
double frw_eta_pen1( double omega_m, double z );
double frw_eta_pen( double omega_m, double z );
double frw_t_pen1( double omega_m, double z );
double frw_eta_integral( integer k, double omega_m, double omega_r, double omega_l, double x1, double x2, double da );
double frw_t_integral( integer k, double omega_m, double omega_r, double omega_l, double x1, double x2, double da );
double frw_get_Omega_r( Cosmology* c );
void frw_set_da( Cosmology* c, double da );
Cosmology* frw_init_std( char* id );
double frw_get_da( Cosmology* c );
void frw_set_Omega_r( Cosmology* c, double omega_r );

void ast_add_eterms6_old( double* v1, double* eterms, double* v2 );
double ast_earth_atm_temp_grc( double h );
double ast_earth_atm_density_grc( double T, double p );
double ast_earth_atm_pressure_grc( double h );
double ast_earth_atm_cs_grc( double h );
void ast_prec_unitmat( double* rmat );
double kepler_height_to_anom( World world, double a, double e, double h );

void world_idname( World world, char* buf );
integer world_idmask( World world );
integer world_id( World world );
double kepler_period_au( double a );
double kepler_speed_q( World primary, double height, double q, double e );
double kepler_eccentric_speed_q( World primary, double q, double e );
double kepler_height_q( World primary, double q, double e, double true_anom );
double kepler_radius_q( double q, double e, double true_anom );
double kepler_parabolic_mean_motion( World primary, double q );
double kepler_parabolic_period( World primary, double q );
void barker_demo( void );
double kepler_parabolic_three( World primary, double period );
void kepler_orbit_heights_q( World primary, double q, double e, double* ph, double* ah );
double kepler_height_to_anom_q( World world, double q, double e, double h );
double barker_solve( double M );
World solar_system_world_tag( GenList system, char* name );
void wcs_world_to_pixel( double* xw, char* ctype, double* crval,
 double* crpix, double* cdelt, double longpole, double* xp );
void wcs_pixel_to_world( double* xp, char* ctype, double* crval, 
 double* crpix, double* cdelt, double longpole, double* xw );
void wcs_pixel_to_physical( double* xp, double* cdelt, double* crpix, double* physical );
void wcs_physical_to_pixel( double* physical, double* cdelt, double* crpix, double* xp );
void wcs_world_to_native( double* xw, char* ctype, double* crval, double longpole,
 double* native );
void utn_wcs_make_lrotate( double* theta, double longpole, double* r );
void wcs_physical_to_native( double* physical, char* ctype, double* native );
void wcs_native_to_physical( double* native, char* ctype, double* physical );
void wcs_native_to_world( double* native, char* ctype, double* crval, double longpole, double* xw );
void gls_inverse( double* xw, double* native );
void gls_project( double* native, double* xw );
void euler_lrot( double phi, double theta, double psi, double* r );
void wcs_make_lrotate( double ra, double dec, double longpole, double* r );
double areographic_to_centric( World world, double glat );
double areocentric_to_graphic( World world, double clat );
void ast_free( AstState state );
logical world_state( World world, double t, integer frame, double* pos, double* vel );
logical world_orient( World world, double t, double* ex, double* ey, double* ez );
void orrery_print_header( void );
void luna_orient( World luna, double t, double* ex, double* ey, double* ez );
double geoc_to_geod_lat_iter( World world, double phi0, double r, double z );
double geod_to_geoc_height( World world, double h, double glat, double* latp );
double geoc_to_geod_height( World world, double height, double lat, double* glatp );
double geod_e( World world );
void geod_to_efg( World world, double h, double glat, double lon, double* efg );
double efg_to_geod( World world, double* efg, double* glatp, double* lonp );
double geod_flattening( World world );
double geodet_aux_C( double one_minus_f, double phi );
double geoc_to_geod_lat( World world, double lat, double height );
double geod_to_geoc_lat( World world, double h, double glat );
void geod_calc_re_z( World world, double h, double glat, double* re, double* z );
double geod_calc( World world, double height, double lat, double* glatp );
void world_list_els( World world, double t1, double t2, double dt );
void world_eval_els( World world, double t, double* els );
void system_read_els( GenList system, char* filename );
World solar_system_std_world( GenList system, integer no );
void orrery_eval_ec( World world, edouble et, double* matrix, double* pv );
#endif







