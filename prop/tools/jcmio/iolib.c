/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011,2012)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmstate.h"

/* 5/2012 - bug 13254: fix segV. */

/* IO stack for C JCMLIB */


FioStack utn_fio_iostack_init( void )
{
 FioStack iofs;
 iofs = calloc( 1, sizeof( FioStackData ));
 iofs->input_stack = calloc( UT_IO_DEPTH, sizeof( FioFile ));
 iofs->output_stack = calloc( UT_IO_DEPTH, sizeof( FioFile ));
 iofs->depth = UT_IO_DEPTH;
 iofs->input = iofs->input_stack;
 iofs->output= iofs->output_stack;
 iofs->err_chan = NULL;
 iofs->ilevel = 1;
 iofs->olevel = 1;
 iofs->msgfunc = utn_fio_std_msg;
 iofs->detab_flag = UT_TRUE;
 return iofs;
}

void utn_fio_iostack_free( FioStack iofs )
{
 free( iofs->input_stack );
 free( iofs->output_stack );
 free( iofs );
}

/* Thread-safe versions */

FioFile utn_fio_iostack_file( FioStack iofs, long mode )
{
 if ( !iofs) iofs = utn_fio_get_stack();
 switch( mode )
 {
  case FIO_ERR:
   return iofs->err_chan;
  case FIO_INPUT:
   return *iofs->input;
  case FIO_OUTPUT:
   return *iofs->output;
  default:
   return NULL;
 }
}


void utn_fio_iostack_realloc( FioStack iofs, long n )
{
  iofs->depth = n;
  iofs->input_stack  = realloc( iofs->input_stack, iofs->depth * sizeof( FioFile ));
  iofs->output_stack = realloc( iofs->input_stack, iofs->depth * sizeof( FioFile ));
  iofs->input = &iofs->input_stack[iofs->ilevel-1];
  iofs->output= &iofs->output_stack[iofs->olevel-1];
}


void utn_fio_iostack_ipush( FioStack iofs, FioFileList list, FioFile file )
{
 if ( !iofs ) iofs = utn_fio_get_stack();
 if ( iofs->ilevel >= iofs->depth ) 
  utn_fio_iostack_realloc( iofs, 2 * iofs->depth );

 iofs->ilevel++;
 iofs->input++;
 if ( file ) 
  utn_fio_iostack_set( iofs, list, file, FIO_INPUT );

}

void utn_fio_iostack_opush( FioStack iofs, FioFileList list, FioFile file )
{
 if ( !iofs ) iofs = utn_fio_get_stack();
 if ( iofs->olevel >= iofs->depth )
  utn_fio_iostack_realloc( iofs, 2 * iofs->depth );

 iofs->olevel++;
 iofs->output++;

 if ( file ) 
  utn_fio_iostack_set( iofs, list, file, FIO_OUTPUT );

}


void utn_fio_iostack_ipop( FioStack iofs, FioFileList list, long mode )
{ 
 if ( !iofs ) iofs = utn_fio_get_stack();
 if ( mode >= 0 ) {
  FioFile file = utn_fio_iostack_file( iofs, FIO_INPUT );
  if ( !utn_fio_file_interactive( file ) ) utn_fio_file_close( file );

 }
 iofs->ilevel--;
 iofs->input--;
 if ( iofs->ilevel <= 0 ) {
  utn_fio_iostack_iclear( list, iofs );
 }
}


void utn_fio_iostack_opop( FioStack iofs, FioFileList list, long mode )
{
 FioFile file;

 if ( !iofs ) iofs = utn_fio_get_stack();

 if ( mode >= 0 ) {
  file = utn_fio_iostack_file( iofs, FIO_OUTPUT );
  if ( !utn_fio_file_interactive( file )) utn_fio_file_close( file );
 }
 iofs->olevel--;
 iofs->output--;
 if ( iofs->olevel <= 0 ) {
  iofs->olevel = 1; 
  iofs->output = iofs->output_stack;
  utn_fio_iostack_set( iofs, list, NULL, FIO_OUTPUT );
  utn_msg_d( UT_VERBOSE, "Output to *" );
 }
}


long utn_fio_iostack_get_level( FioStack iofs, long mode )
{
 if ( !iofs ) iofs = utn_fio_get_stack();
 if ( mode == FIO_INPUT )
  return iofs->ilevel;
 else
  return iofs->olevel;
}

void utn_fio_iostack_iclear( FioFileList list, FioStack iofs )
{
 if ( !iofs ) iofs = utn_fio_get_stack();
 if ( !list ) list = utn_fio_get_list();
 iofs->ilevel = 1;
 iofs->input = iofs->input_stack;
 utn_fio_iostack_set( iofs, list, NULL, FIO_INPUT );
 utn_msg_d( UT_VERBOSE, "Input to *" ); 
}
/*----------------------------------------------------------------------*/
/*
 * io_init
 *
 * Reinitialize IO stacks
 *
 */

void utn_fio_iostack_reinit( FioFileList list, FioStack iofs )
{
 if ( !iofs ) iofs = utn_fio_get_stack();
 iofs->input = iofs->input_stack;
 iofs->output = iofs->output_stack;
 utn_fio_iostack_set( iofs, list, NULL, FIO_INPUT );
 utn_fio_iostack_set( iofs, list, NULL, FIO_OUTPUT );
 utn_fio_iostack_set( iofs, list, NULL, FIO_ERR );
}


void utn_fio_stack_reinit( void )
{ 
 utn_fio_iostack_reinit( NULL, NULL );
}


void utn_fio_iostack_set( FioStack iofs, FioFileList list, FioFile file, long mode )
{

 if ( !iofs ) iofs = utn_fio_get_stack();
 if ( !list && !file ) list = utn_fio_get_list();
 switch( mode )
 {
  case FIO_ERR:
    if ( !file )
     file = utn_fio_list_tty_err( list );
    iofs->err_chan = file;
    break;
  case FIO_OUTPUT:
    if ( !file )
     file = utn_fio_list_tty_out( list );

    *iofs->output = file;
    break;
  case FIO_INPUT:
    if ( !file )
     file = utn_fio_list_tty_in( list );
    *iofs->input = file;
    break;
  default:
    break;
 }
}



void utn_fio_basic_print_state( void )
{
 FioStack iofs = utn_fio_get_stack();
 TextCard buf;
 FioFile file;
 long fid;
 utn_fio_msg( " " );
 utn_fio_dashline( 40 );
 utn_fio_msg( " JCMLIB Basic IO (BIO) Status " );
 utn_fio_dashline( 40 );
 file = *iofs->input;
 fid = file? file->fid : 0;
 snprintf( buf, UT_CARD_SIZE, "  Input Channel:  %ld", fid ); utn_fio_msg( buf );
 file = *iofs->output;
 fid = file? file->fid : 0;
 snprintf( buf, UT_CARD_SIZE, "  Output Channel: %ld", fid ); utn_fio_msg( buf );
 file = iofs->err_chan;
 fid = file? file->fid : 0;
 snprintf( buf, UT_CARD_SIZE, "  Error Channel:  %ld", fid ); utn_fio_msg( buf );
}

/*----------------------------------------------------------------------*/
/*
 * input_test_eof
 * Uses fileproto.h utn_fio_test_eof
 */

logical utn_fio_input_test_eof( void )
{
 FioFile file = utn_fio_iostack_file( NULL, FIO_INPUT );
 return ( utn_fio_file_test_eof( file ));
}

/*----------------------------------------------------------------------*/
/*
 * msg
 *
 */

void utn_fio_std_msg( char* buf, logical newline )
{
 FioFile file = utn_fio_iostack_file( NULL, FIO_OUTPUT );
 if ( newline ) {
  utn_fio_write_line( file, buf );
 } else {
  utn_fio_write_text( file, buf );
 }
}

void utn_fio_msg_endline( void )
{
 utn_fio_msg( NULL );
}



/*----------------------------------------------------------------------*/
/*
 * tty_input
 *
 */

logical utn_fio_tty_input( char* buf, long siz )
{
 return( utn_fio_read_line( NULL, buf, siz ) );
} 


void utn_fio_dashline( long n )
{
 utn_fio_iostack_dashline( NULL, n );
}

void utn_fio_iostack_dashline( FioStack iofs, long n )
{
 char chunk[81];
 long m;
 long k;
 char* ptr;
 m = n;
 while ( m > CARD_SIZE ) {
  k = CARD_SIZE; 
  ptr = chunk; 
  while( k-- )
  { *ptr = '-'; ptr++; }
  *ptr = '\0';
  utn_fio_iostack_msg( iofs, NULL, chunk, UT_FALSE, UT_FALSE );
  m -= CARD_SIZE;
 } 
 k = m; 
 ptr = chunk; 
 while( k-- ) 
  { *ptr = '-'; ptr++; }
 *ptr = '\0';
 utn_fio_iostack_msg( iofs, NULL, chunk, UT_TRUE, UT_FALSE );
}

FioFile utn_fio_iostack_get_entry( FioStack iofs, char* io_type, long level, char* buf, char* status )
{

 logical inmode;
 long stack_top;
 long istat;
 FioFile file = NULL;
 FioDriver fio;
 if ( !iofs ) iofs = utn_fio_get_stack(); 
 inmode = *io_type == 'I';
 utn_cs_copy( " ", status ); 
 stack_top = inmode ? iofs->ilevel : iofs->olevel;
 if ( level <= 0 ) {
  utn_cs_copy( ">> Invalid IO stack level <<", buf );
 } else if ( level > stack_top ) {
  utn_cs_copy( ">> Beyond top of stack    <<", buf );
 } else {
  file = inmode?  iofs->input_stack[level-1] : iofs->output_stack[level-1];
  fio = file? file->driver : NULL;
  if ( utn_cs_eq( fio->name, "TTY IN" )) {
   utn_cs_copy( "(Interactive stream)", buf );
   utn_cs_copy( "[TTY IN]", status );
  } else if (  utn_cs_eq( fio->name, "TTY OUT" )) {
   utn_cs_copy( "(Interactive stream)", buf );
   utn_cs_copy( "[TTY OUT]", status );
  } else if ( !fio ) { 
   utn_cs_copy( "(No file)", buf );
  } else {
   utn_fio_file_get_name( file, buf, FN_SIZE );
   istat = utn_fio_file_status( file, FIO_CHECK );
   if ( istat == UT_FILE_EOF ) {
    utn_cs_copy( "[EOF]", status );
   }  else if ( istat == UT_FILE_OPEN ) { 
    utn_cs_copy( "[Open]", status );
   } else if ( istat == UT_FILE_CLOSED ) {
    utn_cs_copy( "(No file)", status );
   } else {
    utn_cs_copy( "(Bad status)", status ); 
   }

  }
 }
 return file;
}


void utn_fio_iostack_print_top( FioStack iofs, char* type, char* pfx )
{
 long stack_top;
 Filename filename;
 TextCard status;
 TextCard mbuf;
 TextBuf buf;
 long fid;
 FioFile file;

 if ( !iofs ) iofs = utn_fio_get_stack();

 stack_top = utn_fio_iostack_get_level( iofs, FIO_INPUT );
 file = utn_fio_iostack_get_entry( iofs, type, stack_top, filename, status );
 if ( file )
 {
  fid = utn_fio_file_fid( file );
  snprintf( mbuf, UT_CARD_SIZE, " Unit %ld", (long)fid );
 } else {
  utn_cs_copy( " ", mbuf );
 }
 snprintf( buf, UT_CARD_SIZE, "%s IO stream: %s%s%s", pfx, filename, status, mbuf );
 utn_fio_msg( buf );
}


void utn_fio_stack_print_state( void )
{
 FioStack iofs = utn_fio_get_stack();
 utn_fio_iostack_print_state( iofs );
}

void utn_fio_iostack_print_state( FioStack iofs )
{

 long top;
 TextWord tmp;
 long iofs_in_ios;
 TextBuf buf;
 if ( !iofs ) iofs = utn_fio_get_stack();
 top = utn_fio_iostack_get_level( iofs, FIO_INPUT );
 if ( utn_fio_stack_status(FIO_CHECK) == UT_FILE_EOF )  {
  iofs_in_ios = -1;
 } else {
  iofs_in_ios = 0;
 }
 if ( utn_fio_stack_eof(FIO_CHECK) ) {
  utn_cs_copy( "set", tmp );
 } else {
  utn_cs_copy( "clear", tmp );
 }
 utn_fio_msg( " " );
 utn_fio_dashline( 40 );
 utn_fio_msg( " JCMLIB IO File Stack (IOFS) Status" );
 utn_fio_dashline( 40 );
 snprintf( buf, UT_TEXT_SIZE, "  IOFS Input Stack depth: %-6ld IOS Code: %-6ld EOF Flag: %s", (long)top, (long)iofs_in_ios, tmp );
 utn_fio_msg( buf );
 if ( top > 0 ) {
  utn_fio_iostack_print_stack( iofs, "I", top );
 }
 top = iofs->olevel;
 snprintf( buf, UT_TEXT_SIZE, "  IOFS Output Stack depth: %-6ld", (long)top );
 utn_fio_msg( buf );
 if ( top > 0 ) {
  utn_fio_iostack_print_stack( iofs, "O", (long)top );
 }
 utn_fio_msg( " " );
}


void utn_fio_iostack_print_stack( FioStack iofs, char* type, long top )
{
 long level;
 long fid;
 Filename filename;
 TextCard status;
 TextBuf buf;
 FioFile file;
 utn_fio_msg( "    ------------------------------" );
 utn_fio_msg( "    | Level    FID   Status      | File ");
 for ( level = top; level > 0; level-- ) {
  file = utn_fio_iostack_get_entry( iofs, type, level, filename, status );
  fid = utn_fio_file_fid( file );
  snprintf( buf, UT_TEXT_SIZE, "    | %-6ld   %-6ld%-12s| %s", (long)level, (long)fid, status, filename );
  utn_fio_msg( buf );
 }
 utn_fio_msg( "    ------------------------------" );
 
}

void utn_fio_iostack_set_detab( FioStack f, logical q )
{
 f->detab_flag = q;
}

/*
 *  Internal routine

 */
void utn_fio_iostack_set_input( FioStack iofs, FioFileList list )
{
 long level;
 long fid;
 Filename tbuf;
 FioFile file;
 TextCard ebuf;
 long lineno;
 
 if ( !iofs ) iofs = utn_fio_get_stack();

 level = utn_fio_iostack_get_level( iofs, FIO_INPUT );
 file = utn_fio_iostack_input_entry( iofs, 0 );
 while ( level > 0 && utn_fio_file_test_eof( file ) ) {
  utn_msg_d( UT_VERBOSE_FIO, "IOFS Input Pop" );
  utn_fio_iostack_ipop( iofs, list, FIO_POP_CLOSE );
  level = utn_fio_iostack_get_level( iofs, FIO_INPUT );
  file = utn_fio_iostack_input_entry( iofs, 0 );
 }
 if ( utn_dbg_level(UT_VERBOSE_FIO) ) {
  utn_fio_file_get_name( file, tbuf, FN_SIZE );
  lineno = utn_fio_file_get_lineno( file );
  fid = file? file->fid : 0;
  snprintf( ebuf, UT_CARD_SIZE, "IOFS INPUT Level %-6ldFID %-6ldLine %-6ldFile %s", (long)level, (long)fid, (long)lineno, tbuf );
  utn_msg_d( UT_VERBOSE_FIO, ebuf );
 }
}

FioFile utn_fio_iostack_input_entry( FioStack iofs, long level )
{
 long stklevel;
 if ( !iofs ) iofs = utn_fio_get_stack();

 if ( level == 0 ) {
  stklevel = iofs->ilevel;
 } else {
  stklevel = level;
 }
 return( iofs->input_stack[stklevel-1] );
}


long utn_fio_stack_status( long mode )
{
 FioFile file = utn_fio_iostack_file( NULL, FIO_INPUT );
 return( utn_fio_file_status( file, mode ) );
}

logical utn_fio_stack_ok( long mode )
{
 FioFile file = utn_fio_iostack_file( NULL, FIO_INPUT );
 return( utn_fio_file_status( file, mode ) == UT_FILE_OPEN );
}

logical utn_fio_stack_eof( long mode )
{
 FioFile file = utn_fio_iostack_file( NULL, FIO_INPUT );
 return( utn_fio_file_mode_eof( file, mode ) );
}


void utn_fio_iostack_rewind_input( FioStack iofs )
{
 FioFile file;
 if ( !iofs ) iofs = utn_fio_get_stack();
 file = utn_fio_iostack_file( iofs, FIO_INPUT );
 utn_fio_file_rewind( file );
}
