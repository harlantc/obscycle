/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008-2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"

#define AST_WSYS_NSYS 9
static WaveSysData AST_WSYS_LIST[] =  {
    { "lognu",    AST_WSYS_FREQ, AST_WSYS_FREQ, "log(nu)", "log(nu/Hz)",   "%16.6f", 1.0,  ast_freq_copy, ast_freq_copy },
    { "Hz",   AST_WSYS_HZ,   AST_WSYS_HZ,   "Hz",   "nu(Hz)",          "%16.6g", 1.0,  ast_freq_to_hz, ast_hz_to_freq },
    { "keV",  AST_WSYS_KEV,  AST_WSYS_KEV,  "keV",  "Energy (keV)",    "%16.6f", 1.0,  ast_freq_to_kev, ast_kev_to_freq },
    { "nm",   AST_WSYS_NM,   AST_WSYS_MU,   "nm",   "Wavelength (nm)", "%16.6f", 1.0e3, ast_freq_to_lam, ast_lam_to_freq },
    { "m",    AST_WSYS_M,    AST_WSYS_MU,   "m",   "Wavelength (m)", "%16.6f",   1.E-6, ast_freq_to_lam, ast_lam_to_freq },
    { "cm",   AST_WSYS_CM,   AST_WSYS_MU,   "cm",   "Wavelength (cm)", "%16.6f", 1.E-4, ast_freq_to_lam, ast_lam_to_freq },
    { "A",    AST_WSYS_A,    AST_WSYS_MU,   "A",    "Wavelength (A)", "%16.6f",  1.0e4,  ast_freq_to_lam, ast_lam_to_freq },
    { "K",    AST_WSYS_K,    AST_WSYS_K,    "K",    "Temperature (K)", "%16.6f", 1.0,  ast_freq_to_temp, ast_temp_to_freq },
    { "mu",   AST_WSYS_MU,   AST_WSYS_MU,   "mu",   "Wavelength (mu)", "%16.6f", 1.0,  ast_freq_to_lam, ast_lam_to_freq } };

void ast_wsys_free( WaveSysList list )
{
 ast_mag_band_list_free( list->maglist );
 free( list );
}

WaveSysList ast_wsys_init( void )
{
 WaveSysList list;
 list = calloc( 1, sizeof( struct WaveSysList_s ));
 list->data = &(AST_WSYS_LIST[0]);
 list->from = ast_wsys_type( list,"*" );
 list->to   = ast_wsys_type( list,"lognu" ); 
 list->maglist = ast_mag_band_read( NULL );

 return list;
}

void ast_wsys_list( WaveSysList list )
{
 WaveSys wf;
 WaveSys wt;

 wf = list->from;
 wt = list->to;
 utn_fio_tmsgl( "From:" );
 utn_fio_tmsg( wf->name );
 utn_fio_tmsgl( "To  :" );
 utn_fio_tmsg( wt->name );
}


void ast_wsys_format( WaveSys w, double x, char* buf, integer siz )
{
 utn_cs_writef_d( x, w->fmt, buf, siz );
}


logical ast_wsys_read( MagnitudeBandList list, WaveSys w, double* x )
{
 TextCard xbuf;
 logical ok;
 double u = 0.0;
 char* ptr = xbuf;
 utn_tpio_cs( w->prompt, xbuf, TEXT_SIZE );
 ok = utn_fio_stack_ok(0) &&  !utn_cs_uceq( xbuf, "Q" );

 if ( ok ) {
  if ( w->code == AST_WSYS_FREQ )
  {
   u = ast_parse_freq( list, xbuf );
  } 
  else if ( utn_cs_is_blank( xbuf ))
   u = utn_null_d();
  else
   u = utn_cs_get_d( &ptr );

 }
 *x = u;
 if ( ok )
 {
  if ( utn_qnull_d( u ) )
  {
   ok = 0;
   sprintf( ast_state->errbuf, "Bad frequency %s", xbuf );
  }
  if ( ok )
   ok = utn_fio_stack_ok(1) && !utn_fio_stack_eof(0);
 }
 return ok;
}

void ast_wsys_print( WaveSys f, WaveSys t, char* buf1, char* buf2, integer mode )
{
 TextBuf buf;
 TextCard inpline;
 TextCard outline;
 if ( mode <= 0 ) return;

 utn_cs_copy_siz( buf1, inpline, 40 ); 
 utn_cs_copy_siz( buf2, outline, 40 ); 
 if ( mode == 0 )
  utn_fio_msg( outline );
 else if ( mode == 1 )
 {
  sprintf( buf, "%-40s%-40s", inpline, outline );
  utn_fio_msg( buf );
 }
 else if ( mode == 2 )
 {
  utn_fio_dashline(80);
  sprintf( buf, "%-20s%s", f->name, inpline );
  utn_fio_msg( buf );
  sprintf( buf, "%-20s%s", t->name, outline );
  utn_fio_msg( buf );
  utn_fio_dashline(80);
 }
}


 
WaveSys ast_wsys_type( WaveSysList list, char* xopt )
{
 WaveSys w;
 integer nsys = AST_WSYS_NSYS;
 TextCard opt;
 integer i;

 utn_cs_copy( xopt, opt );
  if ( utn_cs_is_blank( opt ) || utn_cs_eq( opt, "*" ) || utn_cs_uceq( opt, "LOGNU" ) 
 || utn_cs_uceq( opt, "U" ))
   utn_cs_copy( "lognu", opt );


  for ( i = 0; i < nsys; i++ )
  {
   w = &list->data[i];
   if ( utn_cs_eq( w->tag, opt ) )
    return w;
  }

  sprintf( ast_state->errbuf, "No match for freq system %s", opt );
  return NULL;
}

double ast_wsys_convert( WaveSys wf, WaveSys wt, double x )
{
 double u;
 double y;
 if ( wf->type == wt->type )
 {
  y = x * ( wt->scale / wf->scale );
 } else {
  u = wf->to_freq( x / wf->scale );
  y = wt->scale * wt->from_freq( u );
 }
 return y;
}

void ast_wsys_header( WaveSys wf, WaveSys wt, logical interact, integer mode )
{
 char buf1[16];
 char buf2[16];
 TextCard buf;

 if (interact) utn_fio_tmsg ("WAVE[Conversion mode]");
 if ( mode <= 0 )
  return;
 else if ( mode == 1 )
 {

  utn_cs_copy_siz( wf->prompt, buf1, 12 );
  utn_cs_copy_siz( wt->prompt, buf2, 12 );
  sprintf( buf, "%-12s%-12s", buf1, buf2 );
  utn_fio_msg( buf );
 } else if ( mode == 2 ) {
  utn_fio_tmsg( "Enter value (q to quit)" );
 }
 if (interact) utn_fio_tmsg ("Enter 'q' to return to setup mode");
}





integer ast_wsys_command( WaveSysList list, char* opt )
{
 TextCard nopt;
 logical repeat =1;
 logical norepeat=0;
 integer retval = 1;
 
 if ( utn_cs_is_blank( opt ))
  retval = 1;
 else if ( *opt == 'Q' )
  retval = 0;
 else if ( utn_cs_eq( opt, "?" ) || *opt == 'H' )
  ast_wave_listopts();
 else if ( *opt == 'P' ) 
 {

  if ( utn_cs_eq( opt, "P" ) )
   list->mode =0;
  else
   list->mode = utn_cs_read_i( opt+1 );

 }
 else if ( *opt == 'D' ) 
 {
  utn_dbg_set(1);
 }
 else if ( *opt == 'F' ) 
 {
  utn_tpio_c( "Wave[FROM system]", nopt, CARD_SIZE );
  list->from = ast_wsys_type( list, nopt );
 } 
 else if ( *opt == 'T' )
 {
  utn_tpio_c( "Wave[TO system]", nopt, CARD_SIZE );
  list->to = ast_wsys_type( list, nopt );
 } 
 else if ( *opt == 'L' )
 {
  ast_wsys_list( list );
 }
 else if ( utn_cs_eq( opt, "EVAL" ))
 {
  ast_wsys_loop( list->maglist, list->from, list->to, list->interact, list->mode, norepeat );
  retval = 0;
 }
 else if ( *opt == 'C' ) 
 {
   ast_wsys_loop( list->maglist, list->from, list->to, list->interact, list->mode, repeat );
   if ( !list->interact ) retval = 0;
 } 
 else if ( *opt != 'Q' )
 {
  sprintf( list->errbuf, "Sorry, don't understand command %s", opt );

  utn_fio_tmsg( list->errbuf );
 }
 return retval;
}


void ast_wave_listopts( void )
{
 utn_fio_dashline( 60 );
 utn_fio_tmsg( "Setup mode commands" );
      utn_fio_tmsg ("H       Help    This info");
      utn_fio_tmsg ("Q       Quit    Leave program");
      utn_fio_tmsg ("F sys   From    Select input system");
      utn_fio_tmsg ("T sys   To      Select output system");
      utn_fio_tmsg ("L       List    List current settings");
      utn_fio_tmsg ("P       Printmode Set Pmode=0");
      utn_fio_tmsg ("C       Convert Run conversion program");
      utn_fio_tmsg ("EVAL    Evaluate Run convert once only");
 utn_fio_dashline( 60) ;
      utn_fio_tmsg ("List of available input/output systems:");
      utn_fio_tmsg("*         log nu/Hz (default)");
      utn_fio_tmsg("keV       Energy (keV)");
      utn_fio_tmsg("A         Angstroms");
      utn_fio_tmsg("mu        microns");
      utn_fio_tmsg("nm        nanometers");
      utn_fio_tmsg("K         Kelvin (h nu/k)");
 utn_fio_dashline( 60) ;
      utn_fio_tmsg ("Example command:");
      utn_fio_tmsg ("wave>: to keV; convert");
      utn_fio_tmsg ("or equivalently");
      utn_fio_tmsg ("wave>: t keV c");
 utn_fio_dashline( 60) ;
}




WaveSysList ast_wave_init( char* args[], integer nargs )
{
  WaveSysList list;
  logical opts;
  TextCard argopt;
  AstState state;
  char* ptr;
  state = ast_init();
  list = ast_wsys_init();
  utn_esc_reset("ARG", ':' );
  utn_tpio_cmdargs( args, nargs, &list->interact, &opts );
  if ( opts )
  {
   if ( list->interact )
    utn_cs_copy( "L,C", argopt );
   else
    utn_cs_copy( "C", argopt );
   ptr = argopt;
   utn_tpio_setbuf( &ptr, 1 );
  }
  if ( list->interact && !opts )
  {
	 utn_fio_tmsg(" -------------------- Waves -------------------- ");
	 utn_fio_tmsg(" You are now in setup mode.");
	 utn_fio_tmsg(" Type 'c' to enter conversion mode, ");
         utn_fio_tmsg("      '?' to list setup mode commands,");
	 utn_fio_tmsg(" or   'q' to quit the program.");
	 utn_fio_tmsg("The default conversion is from [generic] to log nu" );
  }
  list->mode = list->interact ? 2 : 1;
  return list;
}


void ast_wsys_loop(  MagnitudeBandList maglist, WaveSys f, WaveSys t, logical interact, integer mode, logical repeat )
{
 logical loop = 1;
 double x, y;
 TextCard buf1;
 TextCard buf2;

 if ( !f || !t ) return;

 if ( repeat )
  ast_wsys_header( f, t, interact, mode );

 while ( loop )
 {
  
  loop = ast_wsys_read( maglist, f, &x );
  if ( loop )
  {
    y = ast_wsys_convert( f, t, x );
    ast_wsys_format( f, x, buf1, CARD_SIZE );
    ast_wsys_format( t, y, buf2, CARD_SIZE );
    ast_wsys_print( f, t, buf1, buf2, mode );
  }
  loop = loop && repeat;


 }
}

integer ast_wave_interpreter( char* args[], integer nargs )
{
  WaveSysList list;
  TextCard opt;
  logical loop = 1;    

  list = ast_wave_init( args, nargs );
  utn_cs_copy( " ", opt );

  while( loop )
  {
   utn_tpio_c( "Waves[Setup]>", opt, CARD_SIZE );
   utn_cs_upper( opt );
   if ( !list->interact && utn_fio_stack_eof(0))
    loop = 0;
   else
    loop = ast_wsys_command( list, opt );
  }

 ast_wsys_free( list );
 utn_error_status();
 return 0;
}


