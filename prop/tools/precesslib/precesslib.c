/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gprecess.h"


integer gprec_verbose = 0;

void cel_transform_free( CelTransform t )
{
 free( t );
}




CelTransform cel_transform_init( CelSystem* fsys, CelSystem* tsys )
{
 CelTransform t;
 t = calloc( 1, sizeof( struct CelTransform_s ));
 t->from.sys = fsys;
 t->to.sys = tsys;
 return t;
}


Celestial* precess_init( char* args[], integer nargs )
{
 logical opts = 0;
 Celestial* state;
 logical interact=0;
 TextCard argopt;
 char* aptr[1];

 logical fail_on_err = UT_TRUE;
 state = celestial_init(); 
 utn_esc_reset( "ARG", ':' );
 utn_tpio_cmdargs2( args, nargs, &interact, &opts, fail_on_err );
 if ( opts )
 {
  if ( interact )
   utn_cs_copy( "L,C", argopt );
  else
   utn_cs_copy( "C", argopt );
  aptr[0] = argopt;
  utn_tpio_setbuf( aptr, 1 );   
 }

 state->interact = interact;
 if ( state->interact && !opts )
 {
  utn_fio_tmsg( " ----------------- Precess ------------------ ");
  utn_fio_tmsg( " You are now in setup mode. ");
  utn_fio_tmsg( " Type \"c\" to enter conversion mode, " );
  utn_fio_tmsg( "      \"?\" to list setup mode commands,");
  utn_fio_tmsg( " or   \"q\" to quit the program. " );
  utn_fio_tmsg( "The default conversion is from B1950 to J2000." );
 }
 state->print_mode = state->interact ? 2 : 1;
 return state; 
}

void precess_interpreter( Celestial* state )
{

 CelSystem* fsys;
 CelSystem* tsys;
 logical eof = 0;

 fsys = cel_sys_alloc();
 tsys = cel_sys_alloc();
 state->verbose = gprec_verbose;


 prec_cel_set( state, fsys, "B1950" );
 prec_cel_set( state, tsys, "J2000" );
 if ( gprec_verbose )
 {
   printf( "Enter loop\n" ); 
 }

 while( !eof )
  eof = prec_interpret( state, fsys, tsys, NULL );

 cel_sys_free( fsys );
 cel_sys_free( tsys );
}


logical prec_interpret( Celestial* state, CelSystem* fsys, CelSystem* tsys, char* cmdbuf )
{
 TextCard opt;
 TextCard cmd;
 TextBuf ebuf;
 logical eof = 0;
 char** pptr = NULL;
 logical repeat = UT_TRUE;

 if ( cmdbuf ) pptr = &cmdbuf;

 utn_tpio_g_c( "Precess [Setup]>", pptr, opt, UT_CARD_SIZE );
 utn_cs_upper( opt );

 if ( *opt == 'Q' )
 {
  eof = 1;
  return eof;
 }
 if (utn_cs_is_blank( opt )) {
  return eof;
 } else if ( *opt == 'S' ) {
   integer mode;
/* Set */
/* Modes are:  1 = old B precess matrix */
   utn_tpio_g_c( "Set what", pptr, cmd, UT_CARD_SIZE );
   utn_cs_upper(cmd);
   if ( utn_cs_eq( cmd, "PRECMODE" ))
   {
    utn_tpio_g_c( "PRECMODE [0=old,1=new]", pptr, cmd, UT_CARD_SIZE );
    mode = utn_cs_read_i( cmd );
    if ( mode == 0 ) 
      state->mode |= AST_PREC_OLD_B;  /* old matrices  */
    else if ( mode == 1 )
      state->mode &= ~AST_PREC_OLD_B;  
   } else if ( utn_cs_eq( cmd, "PSEP" )) {
    utn_tpio_g_c( "PRECMODE [0=old,1=new]", pptr, cmd, UT_CARD_SIZE );
    state->sep = cmd[0];
   } else if ( utn_cs_eq( cmd, "-PSEP" )) {
    state->sep = ' ';
   }
   
 } else if ( *opt == 'H' || *opt == '?' ) {
  prec_help();
 } else if ( *opt == 'F' ) { 
  utn_tpio_g_c( "From", pptr, cmd, UT_CARD_SIZE );
  prec_cel_set( state, fsys, cmd );
 } else if ( *opt == 'T' ) { 
  utn_tpio_g_c( "To", pptr, cmd, UT_CARD_SIZE );
  prec_cel_set( state, tsys, cmd );
 } else if ( *opt == 'E' ) { 
  eof  =  prec_convert( state, fsys, tsys, pptr, UT_FALSE );
  eof = UT_TRUE;
 } else if ( *opt == 'C' ) { 
  eof  =  prec_convert( state, fsys, tsys, pptr, repeat );
 } else if ( *opt == 'P' ) { 
  if ( utn_cs_is_blank( opt ) )
  {
   utn_tpio_g_c( "Print level", pptr, cmd, UT_CARD_SIZE );
   state->print_mode = utn_cs_read_i( cmd );
  } else {
   state->print_mode = utn_cs_read_i( opt+1 );
  }
 } else if ( *opt == 'L' ) { 
   utn_fio_tmsgl( "From:" );
   prec_list_sys( fsys );
   utn_fio_tmsgl( "To  :" );
   prec_list_sys( tsys );
 } else if ( *opt == 'V' ) { 
  utn_dbg_set( 1 );
  gprec_verbose = 1;
 } else if ( *opt == ':' ) {
  sprintf( ebuf, "Error: colon syntax only works on command line" );
  utn_fio_tmsg( ebuf );  
 } else {
  sprintf( ebuf, "Sorry, don't understand command %s", opt );
  utn_fio_tmsg( ebuf );
 }

 return eof;
}

void prec_help( void )
{
 utn_fio_msg( "C[onvert]             Begin interactive conversion" );
 utn_fio_msg( "E[val]                Convert once, then exit" );
 utn_fio_msg( "F[rom] system         Select input system" );
 utn_fio_msg( "T[o] system           Select output system" );
 utn_fio_msg( "H[elp]                This info" );
 utn_fio_msg( "L[ist]                List current settings" );
 utn_fio_msg( "L[ist]/S              List available from/to systems" );
 utn_fio_msg( "Q[uit]                Exit program" );
 utn_fio_msg( "Pn                    Print mode (P0/P1/P2)" );
 utn_fio_msg( "V[erbose]             Debug mode" );
 utn_fio_msg( "---------------------------------" );
 utn_fio_msg( "List of available systems (F/T command options):" );
 utn_fio_msg( "B        Equatorial, Besselian epoch 1950" );
 utn_fio_msg( "CON      Constellations (TO only)" );
 utn_fio_msg( "J        Equatorial, Julian epoch 2000" );
 utn_fio_msg( "ICRS     Implemented as J2000" );
 utn_fio_msg( "Bxxxx    Equatorial, given Besselian epoch" );
 utn_fio_msg( "G        Galactic (l-II, b-II)" );
 utn_fio_msg( "SG       Supergalactic" );
 utn_fio_msg( "EC       Ecliptic, Besselian epoch 1950" );
 utn_fio_msg( "ECxxxx   Ecliptic, given Besselian epoch" );
 utn_fio_msg( "/DEG     Override format to degrees" );
 utn_fio_msg( "/HMS     Override format to hh mm ss.ss" );
 utn_fio_msg( "-----------------------------------" ); 
 utn_fio_msg("Example command:" );
 utn_fio_msg("Precess>: From B1950 to Galactic; convert" );
 utn_fio_msg("or equivalently" );
 utn_fio_msg("Precess>: f b t g c" );
 return;
}


logical prec_convert( Celestial* state, CelSystem* fsys, CelSystem* tsys, char** pptr, logical repeat )
{
 integer mode = state->print_mode;
 logical interact = state->interact;
 TextBuf pos1;
 TextBuf pos2="";
 TextCard prompt;
 logical loop = 1;
 logical eof = 0;
 CelTransform tfm;
 double tdata[2];
 double fdata[2];

 tfm  = cel_transform_init( fsys, tsys );
 coord_matrices( state, tfm );
 prec_header( fsys, tsys, mode );
 prec_get_prompt( fsys, prompt );
 while( loop  )
 {
  loop = prec_read( fsys, pptr, fdata );
  if ( !loop && !eof && !interact )
  {
   eof = utn_fio_stack_eof(1);
   loop =  !eof;
  }
  gprec_verbose = utn_dbg_level(1);
  if ( loop )
  {
   csys_format( state, fsys, fdata[0], fdata[1], pos1 );
   if ( gprec_verbose )
    printf( "PRECESS: Re-echoed input pos: %s\n", pos1 );
   coord_convert( state, tfm, fdata, tdata );
   csys_format( state, tsys, tdata[0], tdata[1], pos2 );
   prec_output( fsys, tsys, pos1, pos2, mode );
  }
  if ( loop ) loop = repeat;
 }

 cel_transform_free( tfm );
 return eof;
}



logical prec_read( CelSystem* fsys, char** pptr, double* pos )
{
 TextBuf buf = " ";
 char* xprompt = fsys->xprompt;
 char* yprompt = fsys->yprompt;
 logical deg = fsys->deg;
 char* ptr = NULL;

  if ( deg )
  utn_tpio_g_c( xprompt, pptr, buf, UT_TEXT_SIZE );
 else
  utn_tpio_g_cs( xprompt, pptr, buf, UT_TEXT_SIZE );

 if ( utn_cs_is_blank( buf )) 
 {
  pos[0]= 0.0;
  pos[1]= 0.0;
  return UT_FALSE;
 }
 if ( utn_cs_alpha_char( buf[0] ))  /* Quit */
  return UT_FALSE;
 ptr = buf;
 if ( deg )
  pos[0] = utn_cs_get_d( &ptr );
 else
 {
  pos[0] = sxg_parse_val( &ptr ) * 15.0 / 3600.0;
 }
 if ( !ptr || utn_cs_is_blank( ptr )) 
 {
  if ( deg )
   utn_tpio_g_c( yprompt, pptr, buf, UT_TEXT_SIZE );
  else
   utn_tpio_g_cs( yprompt, pptr, buf, UT_TEXT_SIZE );
  ptr = buf;
 }
 if ( deg )
  pos[1] = utn_cs_get_d( &ptr );
 else
  pos[1] = sxg_parse_val( &ptr ) / 3600.0;
 return UT_TRUE;

}

void prec_cel_set( Celestial* state, CelSystem* fsys, char* opt )
{
 TextCard uopt;
 TextCard xopt;
 TextCard buf;
 TextCard fmt;
 TextWord eword;
 integer p; 
 logical deg = UT_FALSE;
 logical have_fmt = UT_FALSE;
 char* ptr; 

 if ( utn_cs_is_blank( opt )) return;
 
 utn_cs_copy( opt, uopt );
 utn_cs_upper( uopt );


/*
 * Handle   "/deg" and "/hms" suffixes
 */ 

 p = utn_cs_index_char( uopt, '/' );
 if ( p > 0 ) 
 {
  utn_cs_copy_siz( uopt, xopt, p-1 );
  utn_cs_copy( uopt+p, fmt );
  have_fmt = UT_TRUE;
  if ( utn_cs_uceq( fmt, "deg" ))
   deg = UT_TRUE;
  else
   deg = UT_FALSE;
 } else {
  utn_cs_copy( uopt, xopt );
 }
 free( fsys->prompt );
 fsys->prompt = NULL;
 if ( utn_cs_eq( uopt, "CON" ))
 {
  fsys->prompt = utn_cs_dup( "Constellation" );
  utn_cs_copy( " ", fsys->xprompt );
  utn_cs_copy( " ", fsys->yprompt );
  fsys->equinox= 1875.0;
  fsys->type = 'C';
  if ( !state->constell_data )
  {
   state->constell_data = ast_constell_init( NULL, NULL );
   if ( !state->constell_data )
   {
    utn_fio_tmsg( "Failed to read constell data" );
   }
  }
 }
 else if ( *xopt == 'B' || *xopt == 'J' ) 
 {
  fsys->type = xopt[0];
  ptr = xopt+1;
  if ( utn_cs_eq( ptr, "STD" ) || utn_cs_is_blank( ptr ))
  {
   fsys->equinox = ( fsys->type == 'B' ) ? 1950.0 : 2000.0;   
  } else {
   fsys->equinox = utn_cs_get_d( &ptr );
  }
  sprintf( eword, "%c%6.1f", fsys->type, fsys->equinox );
  sprintf( buf, "RA,Dec %s", eword );
  fsys->prompt = utn_cs_dup( buf );
  sprintf( fsys->xprompt, "RA  %s", eword );
  sprintf( fsys->yprompt, "Dec %s", eword );
  fsys->deg = UT_FALSE;
 } 
 else if ( *xopt == 'G' )
 {
  fsys->prompt = utn_cs_dup( "Galactic l,b" );
  fsys->equinox = 2000.0;
  utn_cs_copy( "L", fsys->xprompt );
  utn_cs_copy( "B", fsys->yprompt );
  fsys->type = 'G';
  fsys->deg = UT_TRUE;
 }
 else if ( *xopt == 'S' )
 {
  fsys->prompt = utn_cs_dup( "Supergalactic l,b" );
  fsys->equinox = 2000.0;
  fsys->type= 'S';
  utn_cs_copy( "SGL", fsys->xprompt );
  utn_cs_copy( "SGB", fsys->yprompt );
  fsys->deg = UT_TRUE;
 }
 else if ( utn_cs_eq_siz( xopt, "EC", 2 ))
 {
  ptr = xopt+2;
  fsys->type= 'E';
  if ( utn_cs_is_blank( ptr )) 
  {
   fsys->equinox = 1950.0;
  } else {
   fsys->equinox = utn_cs_get_d(&ptr); 
  }
  sprintf( buf, "Ecliptic l,b epoch %7.2f", fsys->equinox );
  fsys->prompt = utn_cs_dup( buf );
  sprintf( fsys->xprompt, "EL  %s", eword );
  sprintf( fsys->yprompt, "EB  %s", eword );
  fsys->deg = UT_TRUE;
 }
/* Override */
 if ( have_fmt ) fsys->deg = deg;
}





CelSystem* cel_sys_alloc( void )
{
 return calloc( 1, sizeof( CelSystem ));
}

void cel_sys_free( CelSystem* sys )
{
 
  if (sys->prompt) free( sys->prompt );
  free( sys );
}



void prec_header( CelSystem* fsys, CelSystem* tsys, integer mode )
{
 TextBuf buf;
 if ( mode <= 0 )
  return;

 if ( mode == 1 )
 {
  sprintf( buf, "%-40s%-40s", fsys->prompt, tsys->prompt );
  utn_fio_msg( buf );
 } else if ( mode == 2 ) {
  utn_fio_tmsg( "Precess [Conversion mode]" );
  utn_fio_tmsg( "Enter \"q\" to return to setup mode" );
 }


}



