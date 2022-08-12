/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#ifndef JCMCAL_H
#define JCMCAL_H
#include "jcmutils.h"
#include <stdio.h>
#include "edouble.h"
#include <math.h>

static const double DB1900    = 0.31352;             /* Rate of change of tropical year */
static const double DYL_B1900 = -0.007801219;

static const integer DL_SEC    = 86400;    /* Day length in seconds */
static const double  DL_SEC_D  = 86400.0;

static const double  CAL_NOON      = 0.5;
static const integer DOY_J2000     = 1;  /* Day of year at JD_J2000 */
static const integer WKDAY_J2000   = 7;  /* Day of week: Sat 2000 Jan 1 */

static const integer WK_DAYS  = 7;
static const integer YL_DAYS  = 365;            /* Nominal days per year */
static const double  YL_B1900 = 365.242198781;  /* Tropical year, Days in B1900  = YL_J2000 + DYL_B1900 */
static const double  YL_GREG  = 365.2425;       /* Gregorian year */
static const double  YL_J2000 = 365.2500;       /* Julian year, JC_DAYS / 100 */
static const integer JC_DAYS  = 36525;          /* Days in a Julian century */
static const double  YL_ORB   = 365.2568982098; /* Orbital period at 1 AU */

#define              CENTURY   100
#define             _YL_J2000  365.2500
#define             _YL_B1900  365.242198781
#define             _DL_SEC_D  86400.0
#define              YR_SEC   ( _YL_J2000 * _DL_SEC_D )
#define              TROPYR_SEC ( _YL_B1900 * _DL_SEC_D )


static const double  MJD_ZERO       = 2400000.5; /* Origin of MJD */
static const double  JD_B1900       = 2415020.31352; /* B1900 origin */
static const double  JD_B1950       = 2433282.42345905; /* B1950 origin */
static const integer UTC_START_JD   = 2436204;   /* Leap seconds: UTC rate = TAI rate, 1972 Jan 1.0 UTC */
static const integer UTC_LEAP_START = 2441317;
static const double  JD_ET_TT       = 2445700.5;  /* Switch from ET to TT */
static const double  JD_C2000       = 2451543.5; /* Day 0.0 of year 2000 */
static const integer IJD_J2000      = 2451544;   /* J2000 integer day */
static const double  JD_J2000       = 2451545.0; /* J2000 date */

/* Dates from 1940 to 2040 can be abbreviated in y2k mode */
#define Y_2DIGIT_START  40
static const double  Y_B1900   = 1900.0;
static const double  Y_B1950   = 1950.0;
static const integer IY_J2000  = 2000;
static const double  Y_J2000   = 2000.0;

#define SXG_MODE_STD   0
#define SXG_MODE_SDB   1
#define SXG_MODE_DEG   2
#define CAL_MODE_NO_DAYNAME 4
#define SXG_MODE_SDB1  8
#define SXG_MODE_CENTI  16
#define SXG_EPSILON   1.0E-9
#define CAL_LARGE_DT  1.0E6

typedef struct {
 char errbuf[UT_TEXT_SIZE];
} CalState;

#ifdef CAL_GLOBAL
#define EXTERN
EXTERN CalState* cal_global_state= NULL;
#else
#define EXTERN extern
EXTERN CalState* cal_global_state;
#endif

EXTERN enum { CAL_UT1_ERR, CAL_UT1_FNF, CAL_DOY_RANGE_ERR, CAL_AUC_ERR } CalErrorCode;

EXTERN enum { TPREC_UNDEF = -1, TPREC_UNKNOWN = 0, TPREC_NA = 0, TPREC_MILLENIUM, TPREC_CENTURIES,
       TPREC_CENTURY, TPREC_DECADES, TPREC_DECADE, TPREC_YEARS, TPREC_YEAR,
       TPREC_QTR, TPREC_MONTHS, TPREC_MONTH, TPREC_DAYS, TPREC_DAY, TPREC_AUC = TPREC_DAY, 
       TPREC_DECIDAY, TPREC_HOUR, TPREC_CENTIDAY, 
       TPREC_MINUTE, TPREC_SECOND, TPREC_MILLISEC } TimescalePrecision;

typedef struct SEXG {
 double frac;
 unsigned short int hour;
 char sign;
 char min;
 char sec;
 char level;
} Sexagesimal;       /* 14 bytes promoted to 16 */

typedef struct  {
 integer year;
 char month;
 integer day;   /* Promoted in case DOY used */
 Sexagesimal time;
 integer weekday;
 char level;  /* Accuracy level */
} GregDate;

typedef struct {
 integer year;
 integer month;
 integer day;
 double sec;
} CalDate;


typedef struct {
 double tai_minus_utc;
 integer day;
 integer adjust;
 integer add;
} LeapInfo;

/* CDATE: */
double cal_date_to_jd( char* cdate );
void cal_fmt_date( double jd, char* buf, integer maxlen );
void cal_fmt_std_date( double jd, char* buf, integer maxlen );

integer cal_date_get_precision( char* cdate );
double cal_date_round( char* cdate, integer level, integer maxlen );

double cal_precision_val( integer precision );
void cal_precision_name( integer precision, char* name, integer maxlen );

/* JD and JDE */
double   cal_jd( integer y, integer m, integer d, double sec );
edouble cal_jde( integer y, integer m, integer d, double sec );
void cal_doy( double t, integer* y, integer* doy, double* dsec );
void cal_greg( double t, integer* y, integer* m, integer* d, double* utd );


/* SEXLIB */
void sxg_zero( Sexagesimal* x );
double sxg_out_d( Sexagesimal* x );
void sxg_in_d( double v, Sexagesimal* x );
Sexagesimal sxg_init( integer sign, integer hour, integer m, integer sec, double frac, integer precision );
double sxg_parse_val( char** pptr );
void sxg_parse( char** pptr, Sexagesimal* x );
void sxg_fmt( Sexagesimal* ptr, integer level,  integer mode, char* buf, integer siz );
logical read_sxg_separator( char** buf, integer step );
void sxg_round( Sexagesimal* ptr, integer dp, integer max_sec );

void cal_sec_to_hms_d( double sec, integer* hr, integer* mn, integer* s, double* fs );
integer cal_hms_to_sec_i( integer h, integer mn, integer s );
double cal_hms_to_sec_d( integer h, integer mn, integer s, double fs );

/* GDATE */
void cal_zero_gdate( GregDate* gdate );

logical cal_parse_ymd_to_gdate( char** pptr, GregDate* gdate );
char* cal_parse_greg_date( char* buf, GregDate* gdate );

void cal_greg_gdate( double t, GregDate* gdate );
double cal_jd_gdate( GregDate gdate );
void cal_fmt_gdate( GregDate date, integer mode, char* buf, integer siz );
GregDate cal_jde_to_gdate( edouble t );
edouble cal_gdate_to_jde( GregDate* gdate );

/* YMD */
CalDate cal_ymd_create( integer y, integer m, integer d, double utd );
CalDate  cal_gdate_to_ymd( GregDate gdate );
GregDate cal_ymd_to_gdate( CalDate u );
CalDate cal_jde_to_ymd( edouble et );           
edouble cal_ymd_to_jde( CalDate date );
CalDate cal_jde_to_doy( edouble et );

void cal_doy_to_ymd( CalDate* u );
void cal_ymd_to_greg( CalDate u, integer* y, integer* m, integer* d, double* s );

void cal_prec( integer precode, double* precl, double* precu );

/* PACK */
CalDate cal_pdoy_to_doy( double pdoy );
double cal_doy_to_pdoy( CalDate u );

double cal_jde_to_pdoy( edouble t );
logical cal_doy_y2k( void );
void cal_doy_set_y2k( logical q );

/* Internal */
integer cal_greg_offset( integer dy );
GregDate cal_gdate_set( integer y, integer m, integer d, Sexagesimal* time, integer weekday, integer level );
integer cal_unpack_y2k_year( integer year );
integer cal_pack_y2k_year( integer y );
integer cal_lt_find_leap( edouble et, integer zone );
/*
 *    From:        To:
 *                t   et       CalDate           CalDoy        String
 *    et              -        cal_jde_to_greg   cal_jde_doy
 *    t                        cal_greg          cal_doy       cal_fmt_date
 *    CalDate  cal_jd  cal_jde  

 * Also:
 *   et -> weekday      cal_jde_weekday_no
 *   
 */



/*  From t  
 *  To:  JC         cal_jc
 *       GMST       gmst
 */

/* Epochs */
double cal_jc( double t );
double cal_jepoch_to_jd( double epoch );
double cal_bepoch_to_jd( double be );
double cal_bepoch_to_jepoch( double be );
double cal_jepoch_to_bepoch( double je );
double cal_jd_to_jepoch( double t );
double cal_jd_to_bepoch( double t );
double cal_jc_b1900( double t );

double gmst( double jd );

/* Timescales */
edouble cal_ut1_to_tt( edouble ut1 );
edouble cal_ut1_to_gsd( edouble ut1 );
#define jde_gmst cal_ut1_to_gsd
edouble cal_tai_to_lt( edouble tai, integer zone );
edouble cal_lt_to_tai( edouble lt, integer zone );
edouble cal_lt_convert( edouble et, integer zone1, integer zone2 );
edouble cal_tai_to_tt( edouble tai );
edouble cal_tt_to_tai( edouble tt );
edouble cal_tt_to_ut1( edouble tt );
edouble cal_tt_to_tdb( edouble tt );
edouble cal_tdb_to_tt( edouble tdb );
edouble cal_lt_to_tt( edouble lt, integer zone );
edouble cal_lt_to_gsd( edouble lt, integer zone );
edouble cal_tt_to_lt( edouble tt, integer zone );

/* Special aux */
double cal_earth_anom( double Centuries );
double cal_tdb( edouble et );

/* Local aux */
double cal_tai_minus_lt( edouble lt, integer zone );
double cal_lt_minus_tai( edouble tai, integer zone );
Sexagesimal cal_lt_sec_to_hms( edouble et, integer zone );
double cal_lt_hms_to_sec( integer day, Sexagesimal* time, integer zone );
LeapInfo cal_lt_leap( logical utc, edouble et, integer zone );
edouble cal_lt_to_tai_leap( edouble utc, integer zone );
edouble cal_tai_to_lt_leap( edouble tai, integer zone );
edouble cal_lt_to_tai_interp( edouble utc, integer zone );
edouble cal_tai_to_lt_interp( edouble tai, integer zone );
double cal_ut1_minus_tt( edouble et );

/* Generic routines */
double cal_mjd( integer y, integer m, integer d, double t );
char* cal_mon( integer month, char* buf );
integer cal_imon( char* buf );
integer cal_month_length( integer month );
integer cal_month_offset( integer m );
integer cal_greg_lyr( integer year );
integer cal_daynum( integer y, integer m, integer d );
void cal_daymon( integer y, integer doy, integer*m, integer* d );
integer cal_jd_yr( integer year );

void cal_jd_to_doy_i( integer jd, integer* year, integer* doy );

/* Formatting */
void cal_fmt_day( double jd, char* buf );
void cal_fmt_centiday( double te, char* buf, integer maxl );
void cal_fmt_ymd( integer y, integer m, integer d, char* buf, integer siz );
void cal_fmt_month_day( integer m, integer d, integer prn, char* date );
void cal_fmt_hms( integer h, integer m, integer s, double fs, integer prn, char* buf, integer siz );
void cal_fmt_fgreg( 
 integer y, integer d, integer m, Sexagesimal* time,
 integer prn, integer tprec, integer mode, char* dayname, char* timescale, char* calendar, char* buf, integer maxlen );
void cal_fmt_days( double mjd, char* buf, integer maxlen );
void cal_fmt_jd( integer sign, integer day, double dt, char* tsname, char* buf, integer siz );
void cal_fmt_mjd( integer sign, integer day, double dt, char* tsname, char* buf, integer siz );


/* CalDate to pack */
/* PDOY */
edouble cal_pdoy_to_jde( double pdoy );
GregDate cal_pdoy_to_gdate( double pdoy );
double cal_pdoy_to_jd( double pdoy );
double cal_unpack( double tp );
double cal_greg_pack( integer year, integer m, integer d, double utd );
void cal_greg_unpack( double g, integer* y, integer* m, integer* d, double* utd );
double cal_doy_pack( integer y, integer d, double utd );
void cal_doy_unpack( double pdoy, integer* y, integer* doy, double* utd );
/*
 *  From:  To:
 *         gpack          date              pdoy            doy              t
 *  date   cal_greg_pack  -                 -               -                -
 *  doy                   -                 cal_doy_pack    -                -
 *  gpack                 cal_greg_unpack   -               -                -
 *  pdoy                  -                 -               cal_doy_unpack   doy_jd
 *  t                                                       doy_epack        -
 *
 */

/* DAYSEC */
edouble cal_daysec_to_jde( integer day, double sec );
void cal_jde_to_daysec( edouble et, integer* day, double* sec );




/* THERMIDOR */
void cal_jde_to_thermidor( edouble et, integer* an, integer* mois, integer* jour );
void cal_thermidor_date( edouble et, char* buf, integer maxlen );
void cal_thermidor_format( integer FR_Year, integer FR_Month, integer FR_Day, char* date, integer maxlen );



/* OS */
integer cal_os_daynum( integer y, integer m, integer d );
integer cal_os_lyr( integer y );
edouble cal_os_to_jde( integer y, integer m, integer d, double sec );
void cal_jde_to_os_doy( edouble et, integer* y, integer* doy, double* sec );
void cal_jde_to_os( edouble et, integer* y, integer* m, integer* d, double* sec );

/* AUC */
void cal_auc_format( integer y, integer m, integer sect, integer sday, char* date, integer siz );
void cal_auc_date( double t, char* date, integer siz );
void cal_auc_calc( double t, integer* y, integer *m, integer* sect, integer* sday );
void cal_rome_get_cos( integer y, char* buf, integer siz );
void cal_rome_read_cos( char* filename );

/* WEEKDAY */

integer cal_weekday_parse( char* buf );
void cal_weekday_name( integer weekday_no, char* buf );
integer cal_weekday_no( double t );
integer cal_jde_weekday_no( edouble et );       

/*ZONES */
integer cal_zone_get_code( char* zonename );
void cal_zone_set_code( integer zone, char* label );

double cal_greg_offset_d( double dy );
double gmst_simple( double t );
CalState* cal_state_init( void );

edouble cal_lst_to_tt( edouble lst, double zone );
edouble cal_lst_to_utc( edouble ut1, double zone );
void cal_fmt_date_mode( double t, char* buf, integer maxlen, integer mode );
void cal_state_free( CalState* state );
edouble cal_ed_resolve_scale( edouble e, double scale, double epsilon, double step );

integer cal_precision_eval_code( double dt );
void cal_fmt_prec_date( double t, integer prec, char* buf, integer maxlen );
void cal_fmt_prec_dt_date( double t, double dt, char* buf, integer maxlen );
void cal_fmt_prec_centidate( double t, double dt, char* buf, integer maxlen );

void jd_to_maya_long( double t, integer* lc, integer degree );
double maya_long_to_jd( integer* lc, integer degree );
void maya_long_fmt2( integer* lc, integer degree, char* buf, integer maxlen );
void maya_long_fmt( integer* lc, integer degree, char* buf, integer maxlen );
void maya_msg( double t, integer degree );
void maya_jd_to_tzolkin( double t, integer* number, integer* day );
void maya_jd_to_haab( double t, integer* number, integer* haab_month );
void tzolkin_fmt( integer n, integer m, char* buf, integer maxlen );
void haab_fmt( integer n, integer m, char* buf, integer maxlen );
void tzolkin_afmt( integer n, integer m, char* buf, integer maxlen );
void haab_afmt( integer n, integer m, char* buf, integer maxlen );
void maya_full_fmt( integer* lc, integer degree, integer tn, integer td, integer hn, integer hd, 
    char* buf, integer maxlen );
void aztec_full_fmt( integer* lc, integer degree, integer tn, integer td, integer hn, integer hd, 
    char* buf, integer maxlen );
#endif
