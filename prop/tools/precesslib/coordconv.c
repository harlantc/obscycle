/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gprecess.h"

#include <string.h>

void prec_coord_write( double ra, double dec, char sep, char* buf );

void csys_format( Celestial* state, CelSystem* sys, double x, double y, char* buf )
{
 char sep = state->sep;
 if ( sys->type == 'C' )
  ast_constell_find( state->constell_data, x, y, buf );
 else if ( sys->deg ) 
  sprintf( buf, "%10.6f   %10.6f", x, y );
 else
#if 0
  coord_write( x, y, buf );
#endif
  prec_coord_write( x, y, sep, buf );

}

/* For compatibility with the old Fortran precess */
void prec_coord_write( double ra, double dec, char sep, char* buf )
{
 long siz1 = 11;
 long siz2 = 12;
 utn_cs_sexwrite_hr_d( ra, sep, buf, siz1 );
 strcat( buf, " " );
 utn_cs_sexwrite_d( dec, sep,  buf+siz1+1, siz2 );
}


void prec_get_prompt( CelSystem* fsys, char* prompt )
{
 if ( fsys->prompt )
  utn_cs_copy( fsys->prompt, prompt );
 else
  utn_cs_copy( "UNKNOWN", prompt );
}

void prec_output( CelSystem* fsys, CelSystem* tsys, char* pos1, char* pos2, integer mode )
{
 char inbuf[42];
 char outline[42];
 TextBuf buf;
 if ( mode < 0 ) return;

 if ( mode == 0 )
  utn_fio_msg( pos2 );
 else if ( mode == 1 )
 {
  utn_cs_copy_siz( pos1, inbuf, 40 );
  utn_cs_copy_siz( pos2, outline, 40 );
  sprintf( buf, "%-40s%-40s", inbuf, outline );
  utn_fio_msg( buf );
 } else if ( mode == 2 ) { 
  utn_cs_copy_siz( pos1, inbuf, 40 );
  utn_cs_copy_siz( pos2, outline, 40 );
  utn_fio_dashline( 80 );
  sprintf( buf, "%-32s%-40s", fsys->prompt, inbuf );
  utn_fio_msg( buf );
  sprintf( buf, "%-32s%-40s", tsys->prompt, outline );
  utn_fio_msg( buf );
  utn_fio_dashline( 80 );
 }
}


void prec_list_sys( CelSystem* fsys )
{
 TextCard buf;
 
 if( fsys->type == 'C' )
  sprintf( buf, "%-40s", fsys->prompt );
 else if ( fsys->deg )
  sprintf( buf, "%-40s Format:  Degrees", fsys->prompt );
 else
  sprintf( buf, "%-40s Format:  hh mm ss.ss  dd mm ss.ss", fsys->prompt );
 utn_fio_tmsg( buf );
}


