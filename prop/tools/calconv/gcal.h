/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#ifndef GCAL_H
#define GCAL_H

#include "jcmcal.h"

#include <stdio.h>

/* UTC = TAI at UTC 1958 Jan 1.0 */

#define GC_EPT  0
#define JD_SIGN 0
#define JD_DAY  1
#define GC_YR   0
#define GC_MON  1
#define GC_DAY  2
#define GC_HR   3
#define GC_MIN  4
#define GC_SEC  5
#define GC_DOW  7
#define RC_SECT 2
#define RC_DAY  3
#define GC_MAYA_DEG 0
#define GC_MAYA_TZOL 1
#define GC_MAYA_HAAB 3
#define GC_MAYA_LC  5

#define CALDEPTH 16

/* Generic calendars */

#define CALTYPE_N 10
#define TSTYPE_N 7

#ifndef LIB_MAIN
#define EXTERN extern
#else 
#ifndef EXTERN
#define EXTERN
#endif
#endif

EXTERN enum { UNK_CAL_, TGREG_, TJD_, JULIAN_CAL_, TROMAN_, TFRENCH_, TGST_, TELAPSE_, TEPOCH_, TMAYA_ } CalendarType;

EXTERN enum { UNK_TS_, TT_, UTC_, TDB_, TAI_, UT1_, ST_, LST_ } TimescaleType;

#define CAL_ANNUAL 1
#define CAL_NOT_ANNUAL 0

#ifndef CTS_2
extern integer gcal_verbose;
#endif

#define TSYS_NSYS 20
#define TS_NMAX 100
#define TS_LEN 40

typedef struct GenCal_s GenCal;
typedef struct {
 char* name;
 char* label;
 integer type;
 double zone;
 integer no;
} Timescale;

typedef struct {
 char* name;
 char* label;
 integer type;
 logical annual;
 void (*print)(GenCal*, char*, integer); /* Depends on Calendar */
 char* (*parse)(char*, GenCal*); /* Depends on calendar type */
 void (*eval)(edouble, GenCal* );
 edouble (*convert)(GenCal*);  
} Calendar;

typedef struct {
 Timescale* data;
 integer n;    /* Number of timescales */
 integer nmax;
} TimescaleList;

typedef struct {
 Calendar* data;
 integer n;    
 integer nmax;
} CalendarList;



typedef struct CalendarSystem_s {
 Calendar* cal;
 Timescale* ts;
 edouble tzero;
 TextCard prompt;
} CalendarSystem;

typedef struct {
 CalendarList* calendars;
 TimescaleList* timescales;
 integer print_mode;
 logical interact;
 integer verbose;
 CalState* calglobal;
} Calendrical;

struct GenCal_s {
 CalendarSystem* sys;
 integer data[CALDEPTH];
 edouble et;
 integer precision;
};

void cal_conv_help( void );
integer cal_conv_set_pmode( Calendrical* state, char* opt );
logical cal_conv_convert( Calendrical* state, CalendarSystem* fsys, CalendarSystem* tsys, char** pptr, logical repeat );
void cal_conv_exec_loop( Calendrical* state, CalendarSystem* fsys, CalendarSystem* tsys, char** pptr );
void cal_conv_interpreter( Calendrical* state );
void cal_free( Calendrical* state );
logical cal_state_interact( Calendrical* state );
void cal_state_set_interact( Calendrical* state, logical interact );

CalendarSystem* cal_sys_alloc( void );
void cal_sys_free( CalendarSystem* sys );

Calendrical* cal_conv_init( char* args[], integer nargs  );
Calendrical* cal_init( void );
void cal_conv_loop( Calendrical* state, CalendarSystem* fsys, CalendarSystem* tsys, 
  char* date1, char* date2, char* step, integer mode );
void cal_conv_header( CalendarSystem* cts1, CalendarSystem* cts2, integer mode );
void cal_conv_output( CalendarSystem* cts1, CalendarSystem* cts2, char* buf1, char* buf2, integer mode );

void cal_parse_spec( char* opt, char* cal, char* ts );
void cal_cts_set( Calendrical* state, CalendarSystem* sys, char* opt );
integer cal_cts_reset( Calendrical* state, CalendarSystem* sys, char* opt );

GenCal cal_cs_from_gen_date( Calendrical* calendrical, CalendarSystem* cts, char** buf );
void cal_cs_print( GenCal* date, char* buf );

edouble cal_cts_date_to_jde( Calendrical* calendrical, CalendarSystem* cts, char** date );
void cal_cts_jde_to_date( CalendarSystem* cts, edouble et, char* buf );

/* Pairs of systems */
void cal_conv_list( CalendarSystem* cts1, CalendarSystem* cts2 );
void cal_conv_make_prompts( CalendarSystem* cts1, CalendarSystem* cts2 );
edouble cal_conv_time( CalendarSystem* fsys, CalendarSystem* tsys, edouble et1 );


/* Calendar Systems*/
GenCal cal_cs_from_date( CalendarSystem* cts, char** buf );
void cal_cts_print( CalendarSystem* cts );
void cal_cts_get_prompt( CalendarSystem* cts, char* prompt );
logical cal_cts_has_years( CalendarSystem* cts );
logical cal_cts_leap( CalendarSystem* cts );
CalendarSystem* cal_cts_copy( CalendarSystem* sys );

/* Zero points*/
edouble cal_tzero( CalendarSystem* sys );
void cal_tzero_set( Calendrical* state, CalendarSystem* sys, char** tzero );

/* Ts Lists */
void cal_ts_list_free( TimescaleList* list );
TimescaleList* cal_ts_list_alloc( integer n );
Timescale* cal_ts_parse_spec( TimescaleList* list, char* opt );

void cal_ts_list( TimescaleList* list );
void cal_ts_list_hdr( void );
void cal_ts_print( Timescale* ts );

void cal_ts_default( TimescaleList* list );
integer cal_ts_create( TimescaleList* list, char* name, char* label, char* type,
 char* zonecode );
Timescale* cal_ts_alloc( TimescaleList* list );
TimescaleList* cal_ts_init( char* filename );
/* internal */
Timescale* cal_ts_match( TimescaleList* list, char* opt );



/* Calendar Lists*/

CalendarList* cal_init_calendars( void );
CalendarList* cal_cal_list_alloc( integer n );
void cal_cal_list_free( CalendarList* list );
void cal_calendar_list( CalendarList* list );
Calendar* cal_calendar_parse_spec( CalendarList* list, char* opt );
integer cal_calendar_get_id( CalendarList* list, char* opt );

/* Ts Types */
void cal_tstype_get_label( integer id, char* name );
void cal_tstype_get_name( integer id, char* name );
integer cal_tstype_get_id( char* type );
void cal_tstype_list( void );

/* Timescales*/
void cal_ts_get_name( Timescale* ts, char* name );
logical cal_ts_leap( Timescale* ts );
edouble cal_ts_convert( Timescale* ts1, Timescale* ts2, edouble et1 );
edouble cal_ts_converter( edouble et1, integer type1, integer type2, double zone1, double zone2 );
edouble cal_ts_jde_add( Timescale* ts, edouble et, double dt );

/* Calendars */
void cal_calendar_get_label( Calendar* t, char* label );
integer cal_calendar_get_type( Calendar* t );
void cal_calendar_get_name( Calendar* t, char* label );
void cal_calendar_print( integer id, Calendar* cal );

/* Calendar types*/
integer cal_caltype_get_id( char* code );
void cal_caltype_get_name( integer type, char* name );
void cal_caltype_list( void );


/* GenCal functions */

void cal_cs_init( GenCal* date, CalendarSystem* cts );
integer cal_cs_type( GenCal* date );
integer cal_ts_type( GenCal* date );
Timescale* cal_cs_get_ts( GenCal* date );
void cal_set_precision( GenCal* date, integer prn );
integer cal_get_precision( GenCal* date );

edouble cal_cs_julian_jde( GenCal* date );
edouble cal_cs_weekday_jde( GenCal* date );
edouble cal_cs_utd_to_jde( GenCal* cal );
edouble cal_cs_jd_to_jde( GenCal* date );
logical cal_is_utc( GenCal* date );
double cal_cs_utd( GenCal* cal );
double cal_cs_elapse( GenCal* date );



/* Called by cts_jde_to_date */
void cal_cs_eval_jde( edouble et, GenCal* date );
void cal_cs_eval_jd( edouble et, GenCal* date );
void cal_cs_eval_greg( edouble et, GenCal* date );
void cal_cs_eval_thermidor( edouble et, GenCal* date );
void cal_cs_eval_fail( edouble et, GenCal* date );

edouble cal_cs_fail_to_jde( GenCal* date );
void cal_cs_from_gdate( GregDate* gdate, GenCal* date );

void cal_cs_print_gen( GenCal* gcal, char* cdate, logical use_hms, char* obuf, long maxlen );


void cal_sys_set_print( CalendarSystem* sys );
void cal_cs_print_epoch( GenCal* date, char* buf, integer maxlen );

void cal_cs_print_os( GenCal* gcal, char* buf, long maxlen );
void cal_cs_print_simple_greg( GenCal* gcal, char* buf, long maxlen );
void cal_cs_print_mjd( GenCal* date, char* buf, integer siz );
void cal_cs_print_greg( GenCal* gcal, char* buf, long maxlen );
void cal_cs_print_doy( GenCal* gcal, char* buf, integer maxlen );
void cal_cs_print_days( GenCal* date, char* buf, integer maxlen );
void cal_cs_print_jd( GenCal* date, char* buf, integer siz );
void cal_cs_print_elapse( GenCal* date, char* buf, integer maxlen );
void cal_cs_print_gsd( GenCal* date, char* buf, integer maxlen );
void cal_cs_print_gst( GenCal* date, char* buf, integer maxlen );
void cal_cs_print_pack( GenCal* date, char* buf, integer maxlen );
void cal_cs_print_auc( GenCal* date, char* buf, integer maxlen );
void cal_cs_print_thermidor( GenCal* date, char* buf, integer maxlen );

char* cal_cs_parse_fail( char* ptr, GenCal* gcal );
char* cal_cs_parse_greg( char* ptr, GenCal* date );
char* cal_parse_jd_date( char* buf, GenCal* date );
char* cal_cs_parse_doy( char* ptr, GenCal* date );
char* cal_cs_parse_pack( char* ptr, GenCal* date );
char* cal_cs_parse_os( char* ptr, GenCal* date );
char* cal_parse_elapse( char* ptr, GenCal* date );
char* cal_parse_epoch( char* buf, GenCal* date );

char* cal_cs_date_to_gen_cal( CalendarSystem* cts, char* ptr, GenCal* gcal );



edouble cal_cs_elapse_to_jde( GenCal* date );

void cal_cs_set_parse( CalendarSystem* cts );

void cal_cs_eval_elapse( edouble et, GenCal* date );
edouble cal_cs_set_convert( CalendarSystem* cts );
void cal_cs_eval_auc( edouble et, GenCal* date );
void cal_cs_set_eval( CalendarSystem* sys );
void cal_cts_set_annual( CalendarSystem* cts );


edouble cal_cs_epoch_to_jde( GenCal* date );
void cal_cs_eval_epoch( edouble et, GenCal* date );


logical cal_conv_interpret( Calendrical* state, CalendarSystem* fsys, CalendarSystem* tsys, char** opt );
edouble cal_cs_maya_to_jde( GenCal* date );
void cal_cs_print_aztec( GenCal* date, char* buf, integer maxlen );
void cal_cs_print_maya( GenCal* date, char* buf, integer maxlen );
void cal_cs_eval_maya( edouble et, GenCal* date );
#endif

/* edouble cal_conv_copy_ed( edouble et, integer zone1, integer zone2 ); */
/* edouble cal_conv_tt_to_tdb( edouble et, integer zone1, integer zone2 );  */

