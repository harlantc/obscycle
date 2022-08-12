/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/* Emulate F90 date and time */
#include <time.h>
#include <unistd.h>
#include "utlib.h"
/* This emulates the F90 routine date_and_time */

void utn_sys_date_and_time( char* c_date, char* c_time, char* c_zone )
{
 struct tm *t;
 time_t now;
 TextWord t_date;
 TextWord t_time;
 TextWord t_zone = " ";

 now = time( (time_t *)NULL );
 t = localtime( &now );
 strftime( t_date, WORD_SIZE, "%Y%m%d", t );
 strftime( t_time, WORD_SIZE, "%H%M%S.000", t );
#if 0
 strftime( t_zone, WORD_SIZE, "%z", t );
#endif
 utn_cs_copy( t_date, c_date );
 utn_cs_copy( t_time, c_time );
 utn_cs_copy( t_zone, c_zone );
}


double utn_sys_today_pack( void )
{
 TextCard buf;
 integer size = 80;
 struct tm *t;
 double dt, ft;
 time_t now;
 now = time( (time_t *)NULL );
 t = gmtime( &now );
 strftime( buf, size, "%H%M%S", t );
 ft = utn_cs_read_i( buf ) / 86400.0; 
 strftime( buf, size,"%Y%m%d", t );
 dt = utn_cs_read_i( buf ) + ft;
 return dt;
}

void utn_sys_today( char* buf, const long size )
{
 struct tm *t;
 time_t now;
 now = time( (time_t *)NULL );
 t = gmtime( &now );
 strftime( buf, size,"%Y %b %d %H:%M:%S", t );
}

void utn_sys_today_iso( char* buf, const long size )
{
 struct tm *t;
 time_t now;
 now = time( (time_t *)NULL );
 t = gmtime( &now );
 strftime( buf, size,"%Y-%m-%d", t );
}

void utn_sys_now_iso( char* buf, const long size )
{
 struct tm *t;
 time_t now;
 now = time( (time_t *)NULL );
 t = gmtime( &now );
 strftime( buf, size,"%Y-%m-%dT%H:%M:%S", t );
}

void utn_sys_time( char* buf, const long size )
{
 struct tm *t;
 time_t now;
 now = time( (time_t *)NULL );
 t = gmtime( &now );
 strftime( buf, size,"%H:%M:%S", t );
}


void utn_sys_date( char* buf, const long size )
{
 struct tm *t;
 time_t now;
 now = time( (time_t *)NULL );
 t = gmtime( &now );
 strftime( buf, size,"%d/%m/%y", t );
}

void utn_sys_wait( const long t )
{
 sleep( t );
}

void utn_sys_getenv( const char* name, char* value )
{
 utn_cs_copy( getenv( name ), value );
}

void utn_sys_delete( const char* name )
{
 unlink( name );
}

logical utn_sys_cmdline( char** args, long nargs, char* cmdline, long csiz )
{
 long i;
 char* ptr;
 long siz;
 logical q= UT_TRUE;
 ptr = cmdline;
 for ( i = 1; i < nargs; i++ ) 
 {
  siz = utn_cs_ends( args[i] );
  if ( siz + 1 >= csiz  )
  {
    siz = csiz - 1;
    q = UT_FALSE;
  }
  if ( siz > 0 )
  { 
     utn_cs_copy_siz( args[i], ptr, siz );
     ptr += siz;
     *ptr = ' '; ptr++;
     csiz -= siz+1;
  }
  if ( !q ) break;
 }
 *ptr = '\0';
 return q;
}

char** utn_sys_parse_cmdline( char* buf, integer* nargsp )
{
 integer nargs = 0;
 TextBuf item;
 integer nmax = 100;
 char* ptr = buf;
 char** args;

 args = calloc( nmax, UT_SZ_P );
 while ( ptr && *ptr )
 {
  utn_cs_copy_token_arg( &ptr, item, UT_TEXT_SIZE );
  if ( !utn_cs_is_blank( item ))
  {
   args[nargs] = utn_cs_dup( item );
   nargs++;
  }
 } 

 *nargsp = nargs;
 return args;
}


void utn_sys_type( const char* name )
{
 TextBuf cmd;
 snprintf( cmd, UT_TEXT_SIZE, "cat %s", name );
 system( cmd );
}

void utn_sys( const char* cmd )
{
 system( cmd );
}


double utn_sys_stamp( void )
{
 struct tm *t;
 time_t now;
 long j, k;
 double ddate;
 now = time( (time_t *)NULL );
 t = gmtime( &now );
 j = t->tm_sec + 100 * ( t->tm_min + 100 * t->tm_hour );
 k = t->tm_mday + 100 * ( t->tm_mon + 100 * t->tm_year );
 ddate = k + ( (double)j / 1.0E6 );
 return( ddate );
}


void utn_id_jcmlib( char* label, const char* ver, integer maxlen )
{ 
 TextCard cdate;
 utn_sys_today( cdate, CARD_SIZE );
 snprintf( label, maxlen, "%s (%s)", cdate, ver );
}
