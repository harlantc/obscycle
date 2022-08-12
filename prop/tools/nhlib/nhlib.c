/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2009,2012)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/* 7/19/2012 - add "(B1950)" to make ra,dec info. more clearer to users.
* 11/8/2012 - comment out the msg 'NHBASE directory =..' 
*/

#define LIB_MAIN 1
#include "nhlib.h"

void colden_interpreter( Colden state )
{
 CelSystem* fsys;

 logical eof = 0;

 fsys = cel_sys_alloc();
 prec_cel_set( state->celestial, fsys, "J2000" );
 while ( !eof )
 {
  eof = colden_interpret( state, fsys, NULL );
 }
 cel_sys_free( fsys );

}

logical colden_interpret( Colden state, CelSystem* fsys, char* cmdbuf )
{
 char** pptr = NULL;
 TextCard opt;
 logical eof = 0;
 double vmin,vmax;
 TextCard cmd;
 TextCard buf;
 Celestial* cel = state->celestial;
 logical repeat = UT_TRUE;
 utn_cs_copy( "NONE", opt );
 if ( cmdbuf ) pptr = &cmdbuf;

 utn_tpio_g_c( "Colden[Setup]>", pptr, opt, UT_CARD_SIZE );
 utn_cs_upper( opt );
 if ( *opt == 'Q' )
 {
  eof = UT_TRUE;
  return eof;
 } 
 if ( utn_cs_is_blank( opt ))
 {
  return eof;
 } else  if ( utn_cs_eq( opt,"?" ) || opt[0] == 'H' ) {
  nh_listopts();
 } else  if ( utn_cs_eq( opt,"VLIMS" )) {
  utn_tpio_c( "Velocity limit min", opt, UT_CARD_SIZE );
  if ( utn_cs_eq( opt, "*" ) || utn_cs_eq( opt, "-") )
   nh_unset_vlims( state );
  else
  {
   vmin = utn_cs_read_d( opt );
   vmax = utn_tpio_d( "Velocity limit max" );
   nh_set_vlims( state, vmin, vmax );
  }
 } else if ( *opt == 'P' ) {
  if ( utn_cs_ends( opt ) == 1 )
   cel->print_mode = 0;
  else
   cel->print_mode = utn_cs_read_i( opt+1 );
 } else if ( *opt == 'F' ) {
  utn_tpio_c( "Colden[FROM system]", cmd, UT_CARD_SIZE );
  utn_cs_upper( cmd );
  prec_cel_set( state->celestial, fsys, cmd );
 } else if ( *opt == 'L' ) {
  nh_status( state );
 } else if ( *opt == 'D' ) {
  utn_tpio_c( "Data Source [Bell|NRAO]", cmd, UT_CARD_SIZE );
  utn_cs_upper(cmd );
  utn_cs_copy( cmd, state->source );
 } else if ( *opt == 'E' ) {
  eof = nh_convert( state, fsys, pptr, !repeat );
 } else if ( *opt == 'C' ) {
  eof = nh_convert( state, fsys, pptr, repeat );
 } else if ( *opt != 'Q' ) {
  sprintf( buf, "Sorry, don't understand command %s", opt );
  utn_fio_tmsg( buf );
 }
 return eof;
}


void colden_free( Colden state )
{
 integer ifid;
 nh_data_free( state );
 for ( ifid = 0; ifid < state->interface_n; ifid++ )
 {
  nh_interface_free( state->interface[ifid] );
 } 
 free( state->interface );
 celestial_free( state->celestial );   /* Also frees jcmastro */
 free( state );
 utn_free_jcmlib();
}


Colden colden_init( char* args[], int nargs )
{

 logical opts = 0;
 Colden state;
 logical interact=0;
 TextCard argopt;
 char* aptr[1];
 AstState astate;
 Celestial* cel;
 TextBuf nh_dir = " "; 
 logical fail_err = UT_TRUE;
 astate = ast_init();
  
 utn_esc_reset( "ARG", ':' );
 utn_tpio_cmdargs2( args, nargs, &interact, &opts, fail_err );
 if ( opts )
 {
  if ( interact )
   utn_cs_copy( "L,C", argopt );
  else
   utn_cs_copy( "C", argopt );
  aptr[0] = argopt;
  utn_tpio_setbuf( aptr, 1 );   
 }
 state = calloc( 1, sizeof( struct Colden_s ));
 state->celestial = celestial_init();
 state->VMIN_FULL = -550.0;
 state->VMAX_FULL =  550.0;
 nh_unset_vlims( state );
 utn_cs_copy( "BELL", state->source );
 cel = state->celestial;
 cel->interact = interact;
 cel->print_mode = cel->interact ? 2 : 1;

 utn_sys_getenv( "NHBASE", nh_dir );
 if ( utn_cs_is_blank( nh_dir ))
  utn_sys_getenv( "JCMLIBDATA", nh_dir );
 if ( utn_cs_is_blank( nh_dir ))
 {
  utn_fio_tmsg( "Warning: COLDEN found no environment variable NHBASE or JCMLIBDATA" );
  utn_fio_tmsg( "Fallback to hard coded data directory /data/colden" );
  utn_cs_copy( "/data/colden", nh_dir );
 }
 nh_set_dir( state, nh_dir );
 state->interface = nh_interface_alloc( &state->interface_n );
 if ( cel->interact && !opts )
 {
  utn_fio_tmsg( " ----------------- Colden ------------------ ");
  utn_fio_tmsg( " You are now in setup mode. ");
  utn_fio_tmsg( " Type \"c\" to enter conversion mode, " );
  utn_fio_tmsg( "      \"?\" to list setup mode commands,");
  utn_fio_tmsg( " or   \"q\" to quit the program. " );
  utn_fio_tmsg( "The default conversion is from J2000." );     /*11/2009: was B1950*/
 }

 

 return state; 

}

logical nh_interact( Colden state )
{
 Celestial* cel = state->celestial;
 return cel->interact;
}


void nh_listopts( void )
{
 utn_fio_dashline(60);
 utn_fio_tmsg( "Setup mode commands: " );
 utn_fio_tmsg( "H       Help     This info" );
 utn_fio_tmsg( "Q       Quit     Leave program" );
 utn_fio_tmsg( "F sys   From     Select input system" );
 utn_fio_tmsg( "L       List     List current settings");
 utn_fio_tmsg( "P       Printmode Set Pmode=0");
 utn_fio_tmsg( "VLIMS vmin vmax  Velocity limits (km/s)" );
 utn_fio_tmsg( "VLIMS *          No Velocity limits" );
 utn_fio_tmsg( "D       name     Select source data" );
 utn_fio_tmsg( "C       Convert  Run conversion program" );
 utn_fio_dashline( 60 );
 utn_fio_tmsg( "Example command:" );
 utn_fio_tmsg( "Colden>: From B1950; convert");
 utn_fio_tmsg( "or equivalently" );
 utn_fio_tmsg( "Colden>: f b c");
 utn_fio_dashline(60);  

}

void nh_unset_vlims( Colden state )
{

 state->vmin = state->VMIN_FULL;
 state->vmax = state->VMAX_FULL;
 state->vstate = UT_FALSE;
}

void nh_set_vlims( Colden state, double vmin, double vmax )
{
 state->vmin = vmin;
 state->vmax = vmax;
 state->vstate = ( vmin > state->VMIN_FULL || vmax < state->VMAX_FULL );
}

/*11/2009: note: use 'data bell (default) or data nrao' to set the data source*/ 
void nh_status( Colden state) /*routine to list the data source for cmd="list"*/
{
 TextBuf buf;
 TextWord tag = " ";


 sprintf( buf, "Data Source:    DATA  %s", state->source );
 utn_fio_tmsg( buf );
 if ( state->vstate )
  utn_cs_copy( "(full range)", tag );
 sprintf( buf, "Velocity range: VLIMS %10.2f%10.2f %s", state->vmin,
   state->vmax, tag );
 utn_fio_tmsg( buf );
/* utn_fio_tmsg( "Data source options: " );*/  /*11/2009*/

 utn_cs_copy( " ", buf );
  
}

logical nh_convert( Colden state, CelSystem* fsys, char** pptr, logical repeat )
{

 logical loop;
 logical eof = 0;
 Celestial* celestial = state->celestial;
 logical ok;
 CelTransform btfm;
 CelTransform gtfm;
 CelSystem* tsys;
 CelSystem* gsys;
 integer ifid;
 Filename buf;
 TextBuf buf1;
 TextBuf buf0;
 TextBuf buf2;
 TextBuf buf3;
 TextCard codebuf;
 ColdenInterface interface;
 integer printmode;
 double bpos[2] = { 0.0, 0.0 };
 double gpos[2] = { 0.0, 0.0 };
 double nh = 0.0;
 double inpos[2];
 TextBuf pos1;
 logical interact = nh_interact( state );

 printmode = celestial->print_mode;
 /* 11/8/2012:    if ( printmode > 0 )   
  *                   printf( "NHBASE directory = %s\n", state->nh_dir );
  */
 ifid = nh_set_source( state, state->source );

 if ( interact && ifid < 0 )
 {
  utn_fio_tmsg( "Error: could not find data files" );
  utn_fio_tmsg( "Please enter the directory where the data files are located" );
  utn_fio_tmsg( "or enter q to quit" );
  utn_tpio_c( "Data directory", buf, UT_TEXT_SIZE );
  if ( utn_cs_uceq( buf, "Q" ))
   exit(1);
  nh_set_dir( state, buf );
  ifid = nh_set_source( state, state->source );  
 }
 if ( ifid < 0 )
 {
  eof = 1;
  utn_fio_tmsg( "Failed to open data file" );
 }
 tsys = cel_sys_alloc();
 gsys = cel_sys_alloc();
 interface = state->interface[ifid];
 prec_cel_set( celestial, tsys, "B1950" );
 prec_cel_set( celestial, gsys, "G" ); 
 btfm = cel_transform_init( fsys, tsys );
 gtfm = cel_transform_init( fsys, gsys );
 coord_matrices( celestial, btfm );
 coord_matrices( celestial, gtfm );

 loop = UT_TRUE;
 gpos[0] = 0.0;
 gpos[1] = 0.0;
 while( loop ) 
 {
  ok = prec_read( fsys, pptr, inpos );
  if ( ok )
  {
   if ( loop ) loop = repeat;
   csys_format( celestial, fsys, inpos[0], inpos[1], pos1 );
   coord_convert( celestial, btfm, inpos, bpos );
   coord_convert( celestial, gtfm, inpos, gpos );      
   csys_format( celestial, fsys, inpos[0], inpos[1], buf0 );
   sprintf( buf1, "%-32s%-40s", fsys->prompt, buf0 );
   csys_format( celestial, tsys, bpos[0], bpos[1], buf0 );
   sprintf( buf2, "%-32s%-40s", tsys->prompt, buf0 );
   csys_format( celestial, gsys, gpos[0], gpos[1], buf0 );
   sprintf( buf3, "%-32s%-40s", gsys->prompt, buf0 );   
#if 0
   utn_fio_msg( buf1 );
   utn_fio_msg( buf2 );
   utn_fio_msg( buf3 );
#endif
/* calc NH here */
   if ( state->vstate && !interface->vel )
   {
    printf( "Error: no velocity slices in %s data\n", interface->name );
   }
   nh = interface->eval( state->obs, state->data, bpos[0], bpos[1], gpos[0], gpos[1], state->vmin, state->vmax, codebuf );
   nhbase_result( state->celestial, gsys, pos1, bpos, gpos,
     state->vmin, state->vmax, nh, codebuf, printmode );

  } else {
   if ( !interact )
     eof = utn_fio_stack_eof( 1 );
   loop = UT_FALSE;
  }
 }
 cel_sys_free( tsys );
 cel_sys_free( gsys );
 cel_transform_free( btfm );
 cel_transform_free( gtfm ); 
 if ( !repeat ) eof = 1;
 return eof;
}

void nhbase_result( Celestial* state, CelSystem* tsys, char* pos1, double* bpos, double* gpos, double vmin,
 double vmax, double nh, char* codebuf, integer printmode )
{
 TextCard buf;
 TextCard buf2;
 TextCard buf4;
 TextBuf mbuf;
 TextWord buf3; 

 long siz1=12;
 long siz2=13;

 utn_cs_copy( codebuf, buf4 );
#if 0
 coord_write( bpos[0], bpos[1], buf );
#endif
 utn_cs_sexwrite_hr_d( bpos[0], ' ', buf, siz1 );
 strcat( buf, " " );
 utn_cs_sexwrite_d( bpos[1], ' ', buf+siz1+1, siz2 );

 csys_format( state, tsys, gpos[0], gpos[1], buf2 );
 if ( utn_qnan_d( nh ))
  utn_cs_copy( " - ", buf3 );
 else
  sprintf( buf3, "%6.2f", nh );
 if ( printmode == 2 ) 
 {
  utn_fio_dashline( 90 );
  sprintf( mbuf, "Input coords:  %s", pos1 );
  utn_fio_msg( mbuf );
  sprintf( mbuf, "Target RA,Dec (B1950): %-28s (l,b):%s", buf, buf2 ); /*7/19/2012*/
  utn_fio_msg( mbuf );
  sprintf( mbuf, "Density integrated from %10.3f to %10.3f km/s", vmin, vmax );
  utn_fio_msg( mbuf );
  sprintf( mbuf, "Hydrogen density (10^20 cm**(-2)): %s %s", buf3, buf4 );
  utn_fio_msg( mbuf );    
  utn_fio_dashline( 90 );
 } else if ( printmode == 1 ) {
  utn_cs_copy_siz( pos1, buf, 28 );
  sprintf( mbuf, "%-28s%-24s%-8s%-20s", buf, buf2, buf3, buf4 );
  utn_fio_msg( mbuf );
 } else if ( printmode == 0 ) {
  /* even terser result */
  utn_fio_msg( buf3 );
 }
}

void nh_set_dir( Colden state, char* dir )
{
 utn_cs_copy( dir, state->nh_dir );
}

logical nh_get_vlims( Colden state, double* vmin, double* vmax )
{
 *vmin = state->vmin;
 *vmax = state->vmax;
 return state->vstate;
}

void nh_interface_free( ColdenInterface interface )
{
 free( interface->filename );
 free( interface->opt ); 
 free( interface->name );
 free( interface );
}

ColdenInterface bell_alloc( void )
{
 ColdenInterface interface;
 interface = calloc( 1, sizeof( struct ColdenInterface_s ));
 interface->name = utn_cs_dup( "BELL" );
 interface->open = nh_read_bell;
 interface->free = nh_bell_free;
 interface->eval = colden_eval_bell;
 interface->filename = utn_cs_dup( "bell.idx" );
 interface->opt = utn_cs_dup( "bell.dat" );
 interface->vel = UT_TRUE;
 return interface;
}


ColdenInterface nrao_alloc( void )
{
 ColdenInterface interface;
 interface = calloc( 1, sizeof( struct ColdenInterface_s ));
 interface->name = utn_cs_dup( "NRAO" );
 interface->open = nh_read_nrao;
 interface->free = nh_close_nrao;
 interface->eval = colden_eval_nrao;
 interface->filename = utn_cs_dup( "nrao.fits" );
 interface->opt = NULL;
 interface->vel = UT_FALSE;
 return interface;
}

ColdenInterface* nh_interface_alloc( integer* np )
{
 integer n = 2;
 ColdenInterface* interfaces;

 interfaces = calloc( 2, UT_SZ_P );

 interfaces[0] = bell_alloc();
 interfaces[1] = nrao_alloc();
 *np = n;
 return interfaces;
}

integer nh_find_interface( Colden state, char* opt )
{
 ColdenInterface interface;
 TextCard uopt;
 integer ifid;

 utn_cs_copy( opt, uopt );
 utn_cs_upper( uopt );

 for ( ifid = 0; ifid < state->interface_n; ifid++ )
 {
  interface = state->interface[ifid];
  if ( utn_cs_eq( interface->name, uopt ))
   return ifid;
 }
 return -1;
}

void nh_data_free( Colden state )
{
 ColdenInterface interface;
 integer ifid;
 ifid = state->ifid;
 interface = state->interface[ifid];
 if ( interface && state->data )
 {
  interface->free( state->data );
  state->data = NULL;
 }
}

integer nh_set_source( Colden state, char* opt )
{
 ColdenInterface interface;
 integer ifid;

 Filename filename;
 Filename filename2;
 ifid = nh_find_interface( state, opt );
 if ( ifid >= 0 )
 {
  state->ifid = ifid;
  interface = state->interface[ifid];
  utn_sys_filepath( state->nh_dir, interface->filename, "", filename, UT_FN_SIZE );
  if ( !utn_cs_is_blank( interface->opt ))
  {
   utn_sys_filepath( state->nh_dir, interface->opt, "", filename2, UT_FN_SIZE );
  } else {
   utn_cs_copy( "", filename2 );
  }
  state->data = (void*)interface->open( filename, filename2 );
  if ( state->data )
   return ifid;
 }
 return -1;
}
