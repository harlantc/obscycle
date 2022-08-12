/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2009)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gcal.h"

void cal_test_sxg( char* opt );

void cal_test_sxg( char* opt )
   {
 TextCard buf;
    Sexagesimal s;
    TextCard tmp1;
    TextCard tmp2;
    TextCard tmp3;
    char* ptr = opt;

    sxg_zero( &s );
    sxg_parse( &ptr, &s );
    sxg_fmt( &s, TPREC_UNDEF, SXG_MODE_STD, buf, TEXT_SIZE );
    sprintf( tmp3, "%d %d : %d : %d  %f %d", s.sign, s.hour, s.min, s.sec, s.frac, s.level );
    cal_precision_name( (integer)s.level, tmp2, CARD_SIZE );
    utn_cs_copy( " ", tmp1 );
  
    if ( ptr && *ptr ) utn_cs_copy( ptr, tmp1 );
    printf( "SXGP %-25s VALS %-30s LEV = %s REM = %s\n", buf, tmp3, tmp2, tmp1 );
   }


CalendarSystem* cal_sys_alloc( void )
{
  CalendarSystem* sys;
  sys = calloc( 1, sizeof( struct CalendarSystem_s ));
  return sys;
}

void cal_sys_free( CalendarSystem* sys )
{
 free( sys );
}


void cal_conv_interpreter( Calendrical* state )
{
 TextCard opt;
 logical eof=0;
 CalendarSystem* fsys;
 TextBuf cmdbuf = "";
 char* gptr;
 CalendarSystem* tsys;
 utn_cs_copy( " ", opt );
#if DEBUG
 utn_dbg_set(1);
 gcal_verbose=1;
#endif
 state->verbose = gcal_verbose;

 fsys = cal_sys_alloc();
 tsys = cal_sys_alloc();


 if ( gcal_verbose )
  printf( "-- SET FSYS to GREG\n" );
 cal_cts_set( state, fsys, "GREG/UTC" ); 
 if ( gcal_verbose )
  printf( "-- SET TSYS to JD\n" );
 cal_cts_set( state, tsys, "JD" );
 if ( gcal_verbose )
  printf( "-- Make prompts \n" );
 cal_conv_make_prompts( fsys, tsys );
 if ( gcal_verbose )
  printf( "-- Enter loop \n" );
 gptr = cmdbuf;
 while( !eof )
  eof = cal_conv_interpret( state, fsys, tsys, &gptr );

 cal_sys_free( fsys );
 cal_sys_free( tsys );

}

logical cal_conv_interpret( Calendrical* state, CalendarSystem* fsys, CalendarSystem* tsys, char** gptr )
{
 TextCard opt;
 TextCard cmd;
 TextCard subcmd;
 TextBuf mbuf;
 char** pptr = NULL; 
 char* popt;
 logical eof = 0;
 logical repeat =1;

/* Use command buf only if it is non empty */
 if ( gptr && *gptr && **gptr ) 
  pptr = gptr;

 utn_tpio_g_c( "Dates [Setup]>", pptr, opt, CARD_SIZE );
 utn_cs_upper( opt );  
  if ( *opt == 'Q' )
  {
   eof = 1;
   return eof;
  }
  gcal_verbose = utn_dbg_level(1);
  if ( gcal_verbose )
  {
   printf( "OPT = %s\n", opt );
  }

  if ( utn_cs_is_blank( opt ))
  {
   return eof;
  } else if ( utn_cs_eq( opt, "HELP" ) || utn_cs_eq( opt, "?") ) {
   cal_conv_help();
  } else if ( *opt == 'C' ) {
   eof = cal_conv_convert( state, fsys, tsys, pptr, repeat );
  } else if ( *opt == 'P' ) {
   if ( utn_cs_eq( opt, "P" ) || utn_cs_eq( opt, "PRINT" ) ) 
    utn_tpio_g_c( "Print mode", pptr, opt, CARD_SIZE );
   cal_conv_set_pmode( state, opt );
  } else if ( *opt == 'Z' ) {
   utn_tpio_g_cs( "Zero point for input system", pptr, opt, CARD_SIZE );
   popt = opt;
   cal_tzero_set( state, fsys, &popt );
   if ( popt )
   {
/* Copy remainder to command buf  - only works if cmdbuf is empty */
    utn_cs_copy( popt, *gptr );
   }
   cal_conv_make_prompts( fsys, tsys );
  } else if ( utn_cs_eq_begin( opt, "FZ" )) {
   utn_tpio_g_cs( "Zero point for input system", pptr, opt, CARD_SIZE );
   popt = opt;
   cal_tzero_set( state, fsys, &popt );
   cal_conv_make_prompts( fsys, tsys );
  } else if ( utn_cs_eq_begin( opt, "TZ" )) {
   utn_tpio_g_cs( "Zero point for output system", pptr, opt, CARD_SIZE );
   popt = opt;
   cal_tzero_set( state, tsys, &popt );
   cal_conv_make_prompts( fsys, tsys );
  } else if ( *opt == 'E' ) {
   state->interact = 0;
   cal_conv_convert( state, fsys, tsys, pptr, 0 );
   eof = 1;
  } else if ( *opt == 'F' ) {
   utn_tpio_g_c( "Dates[FROM system]",  pptr,  opt, CARD_SIZE );
   utn_cs_upper( opt );
   cal_cts_set( state, fsys, opt );
   cal_conv_make_prompts( fsys, tsys );
  } else if ( *opt == 'X' ) {
   utn_tpio_g_cs( "Time string", pptr, opt, CARD_SIZE );
   cal_test_sxg( opt );
  } else if ( *opt == 'T' ) {
   utn_tpio_g_c( "Dates[TO system]", pptr, opt, CARD_SIZE );
   utn_cs_upper( opt );
   if ( gcal_verbose )
     printf( "-- SET TSYS to %s\n", opt );
   cal_cts_set( state, tsys, opt );
   if ( gcal_verbose )
    printf( "Make prompts for %s\n", opt );
   cal_conv_make_prompts( fsys, tsys );
  } else if ( *opt == 'L' ) {
   utn_cs_subcmd_split( opt, cmd, subcmd, '/' );
   if ( utn_cs_is_blank( subcmd )) 
   {
    cal_conv_list( fsys, tsys );
   } else if ( utn_cs_eq( subcmd, "TT" )) {
    cal_tstype_list();
   } else if ( utn_cs_eq( subcmd, "TS" )) {
    cal_ts_list( state->timescales );
   } else if ( utn_cs_eq( subcmd, "CT" )) {
    cal_caltype_list();
   } else if ( utn_cs_eq( subcmd, "CONV" )) {
    utn_fio_msg( "From: " );
    cal_cts_print( fsys );
    utn_fio_msg( "To: " );
    cal_cts_print( tsys );
   } else if ( utn_cs_eq( subcmd, "CAL" )) {
    cal_calendar_list( state->calendars );
   }
  } else if ( *opt == 'V' ) {
   utn_dbg_set(1);
   gcal_verbose = 1;
  } else if ( *opt == 'D' ) { /* DO */
   cal_conv_exec_loop( state, fsys, tsys, pptr );
  } else {
   sprintf( mbuf, "Warning: Ignored unknown command %s", opt );
   utn_fio_tmsg( mbuf );
  }

 return eof;
}



logical cal_conv_convert( Calendrical* state, CalendarSystem* fsys_in, CalendarSystem* tsys, char** pptr, logical repeat )
{
 integer mode = state->print_mode;
 logical interact = cal_state_interact( state );
 TextBuf date1;
 TextBuf date2="";
 TextCard prompt;
 integer precision;
 logical loop = 1;
 edouble et;
 edouble et1;
 logical eof = 0;
 logical quit = 0;
 TextCard precbuf;
 GenCal gcal1;
 GenCal gcal2;
 CalendarSystem fsys1;  /* Temp system, as overridden by user */
 CalendarSystem* fsys;
 char* dptr;

 fsys = fsys_in;
 cal_conv_header( fsys, tsys, mode );
 cal_cts_get_prompt( fsys, prompt );
 while( loop  )
 {
  utn_tpio_g_cs( prompt, pptr, date1, TEXT_SIZE );  
  if ( !interact )
   eof = utn_fio_stack_eof(1);

  
/* Need EOF support here */   
  quit = utn_cs_uceq_siz( date1, "Q ", 2 ) || eof;
  loop =  !quit;
  gcal_verbose = utn_dbg_level(1);
  if ( loop && !utn_cs_is_blank( date1 ) )
  {
   if ( gcal_verbose )
    printf( "DATES: Calculate JD of %s\n", date1 );
   fsys1 = *fsys_in;
   dptr = date1;
   gcal1 = cal_cs_from_gen_date( state, &fsys1, &dptr );
   fsys = &fsys1;
   et1 = cal_cs_weekday_jde( &gcal1 );

   if ( gcal_verbose )
    printf( "DATES: JD (initial)   = %24.4f + %24.12f\n", et1.t, et1.dt );

   et = cal_conv_time( fsys, tsys, et1 );
   if ( gcal_verbose )
    printf( "DATES: JD (converted) = %24.4f + %24.12f\n", et.t, et.dt );

   cal_cs_print( &gcal1, date1 );
   if ( gcal_verbose )
    printf( "DATES: Re-echoed date = %s\n", date1 );
   cal_cs_init( &gcal2, tsys );
   precision = cal_get_precision( &gcal1 ); 
   if ( gcal_verbose )
   {
    cal_precision_name( precision, precbuf, CARD_SIZE );
    printf( "DATES: Precision is %s\n", precbuf );
   }
   cal_set_precision( &gcal2, precision );
   cal_cs_eval_jde( et, &gcal2 );
   cal_cs_print( &gcal2, date2 );
   if ( gcal_verbose )
    printf( "DATES: Converted date = %s\n", date2 );

   cal_conv_output( fsys, tsys, date1, date2, mode );
  }
  if ( loop ) loop = repeat;
 }
 return eof;
}

void cal_conv_exec_loop( Calendrical* state, CalendarSystem* fsys, CalendarSystem* tsys, char** pptr )
{
 TextBuf date1;
 TextBuf date2;
 TextCard prompt;
 TextCard step;
 integer mode = state->print_mode;

 cal_conv_header( fsys, tsys, mode );
 cal_cts_get_prompt( fsys, prompt );
 utn_tpio_g_cs( prompt, pptr, date1, TEXT_SIZE );
 utn_tpio_g_cs( prompt, pptr, date2, TEXT_SIZE );
 utn_tpio_g_cs( "Step", pptr, step, CARD_SIZE );
 cal_conv_loop( state, fsys, tsys, date1, date2, step, mode );
}


/*
 *  Control the print mode. Acceptable forms are
 *  p2
 *  p 2
 *  print 2
 *  print2
 *
 */
integer cal_conv_set_pmode( Calendrical* state, char* opt )
{
 integer p;

 if ( utn_cs_eq( opt, "P" ) || utn_cs_eq( opt, "PRINT" ))
  p = utn_tpio_d_i( "Print mode", 0 );
 else if ( utn_cs_eq_siz( opt, "PRINT", 5 ))
  p = utn_cs_read_i( opt+5 );
 else 
  p = utn_cs_read_i( opt+1 );

 state->print_mode =p;
 return p;
}

void cal_state_set_interact( Calendrical* state, logical interact )
{
 state->interact = interact;
}


logical cal_state_interact( Calendrical* state )
{
 return state->interact;
}

Calendrical* cal_conv_init( char* args[], integer nargs  )
{
 logical opts = 0;
 Calendrical* state;
 logical interact=0;
 TextCard argopt;
 char* aptr[1];
 utn_init_jcmlib();
 utn_esc_reset( "ARG", ':' );

 utn_tpio_cmdargs( args, nargs, &interact, &opts );
 if ( opts )
 {
  if ( interact )
   utn_cs_copy( "L,C", argopt );
  else
   utn_cs_copy( "C", argopt );
  aptr[0] = argopt;
  utn_tpio_setbuf( aptr, 1 );
 }
 state = cal_init();
 state->interact = interact;
 if ( state->interact && !opts )
 {
  utn_fio_tmsg( " ----------------- Dates ------------------ ");
  utn_fio_tmsg( " You are now in setup mode. ");
  utn_fio_tmsg( " Type \"c\" to enter conversion mode, " );
  utn_fio_tmsg( "      \"?\" to list setup mode commands,");
  utn_fio_tmsg( " or   \"q\" to quit the program. " );
  utn_fio_tmsg( "The default conversion is from Gregorian calendar date to JD." );
 }

 state->print_mode = state->interact ? 2 : 1;
 return state;
}

void cal_conv_help( void )
{
 utn_fio_msg( "C[onvert]             Begin interactive conversion" );
 utn_fio_msg( "DO date1;date2;step   Loop conversion" );
 utn_fio_msg( "E[val] date           Convert one date and exit " );
 utn_fio_msg( "F[rom] system         Define input calendar and timescale");
 utn_fio_msg( "L[ist]                List current conversion" );
 utn_fio_msg( "L[ist]/CONV           List current conversion (detail)" );
 utn_fio_msg( "L[ist]/TS             List timescales" );
 utn_fio_msg( "L[ist]/TT             List timescale types" );
 utn_fio_msg( "L[ist]/CT             List calendar types" );
 utn_fio_msg( "L[ist]/CAL            List calendars" );
 utn_fio_msg( "Pn                    Print mode (P0/P1/P2)" );
 utn_fio_msg( "T[o] system           Define output calendar and system" );
 utn_fio_msg( "V[erbose]             Debug mode" );
 return;
}

