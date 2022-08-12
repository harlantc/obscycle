/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmstate.h"

#define TPIO_TMP_MAX 2048
char tpio_tmp[TPIO_TMP_MAX];


Tpio utn_tpio_get( void )
{
 return utn_state_tpio( NULL );
}

Tpio utn_tpio_data_init( void )
{
 long i;
 Tpio utn_tpio_data = calloc( 1, sizeof ( struct utn_tpio_s ));
 utn_tpio_data->buffer = calloc( UT_IO_DEPTH, UT_SZ_P);
 for ( i = 0; i < UT_IO_DEPTH; i++ )
  utn_tpio_data->buffer[i] = calloc( UT_TPIO_BUFLEN, UT_SZ_C );

 utn_tpio_data->bptr = calloc( UT_IO_DEPTH, UT_SZ_P);
 utn_tpio_data->cmd_mode = UT_FALSE;
 utn_tpio_data->cmd = utn_tpio_data->buffer[0];
 utn_tpio_data->ptr = NULL;
 utn_tpio_data->loop = calloc( UT_TPIO_MAX_LOOPS, sizeof( TpioLoop ));
 utn_tpio_data->nloop = 0;
 utn_tpio_data->maxloop = UT_TPIO_MAX_LOOPS;
 utn_tpio_data->depth = UT_IO_DEPTH;
 utn_tpio_tp_init( utn_tpio_data );
 return utn_tpio_data;
}

void utn_tpio_level_check( Tpio tpio, long level )
{
 long lev;
 long lmax;
 if ( level >= tpio->depth )
 {
   lmax = level + 1;
   tpio->buffer = realloc( tpio->buffer, lmax * UT_SZ_P);
   tpio->bptr  = realloc( tpio->bptr, lmax * UT_SZ_P);
   for ( lev = tpio->depth; lev < lmax; lev++ )
    tpio->buffer[lev] = calloc( UT_TPIO_BUFLEN, UT_SZ_C);
   tpio->depth = lmax;
 }
}

void utn_tpio_free( Tpio tpio )
{
 utn_ar_free_cp( tpio->buffer, tpio->depth );
 free(tpio->bptr );
 free( tpio->loop ); 
 free( tpio );
}

/*
 *--------------------------------------------------------------------------------
 * Internal routine to give buffer status
 */

void utn_tpio_loc( Tpio tpio, FioStack iofs, long* level, long* pos )
{
 if ( !tpio ) tpio = utn_tpio_get();
 *level = utn_fio_iostack_get_level( iofs, FIO_INPUT );
 utn_tpio_level_check( tpio, *level );
 if ( *tpio->ptr == NULL ) {
  *pos = -1;
 } else {
  *pos = *tpio->ptr - tpio->buffer[*level] + 1;
 }
}


/*
 *--------------------------------------------------------------------------------
 * Clear one level of the buffer
 */
void utn_tpio_cclear( Tpio tpio, long level )
{
 if ( !tpio ) tpio = utn_tpio_get();
 tpio->bptr[level] = tpio->buffer[level];
 utn_cs_zero( tpio->bptr[level] );
}


/*----------------------------------------------------------------------*/
/*
 * utn_tpio_dbg
 * Uses <stdio.h>
 */

void utn_tpio_dbg( void )
{
 utn_tpio_tp_dbg( NULL );
}

void utn_tpio_tp_dbg( Tpio tpio )
{
 if ( !tpio ) tpio = utn_tpio_get();
 printf("TPIO BUFFER POINTER %p\n", (void*)tpio->ptr );
 if ( tpio->ptr != NULL ) printf("TPIO BUFFER HEAD    %p\n", *tpio->ptr );
}


/*----------------------------------------------------------------------*/
/*
 * utn_tpio_init
 *
 * Zero the buffer and set the stack to bottom level
 * Uses csproto.h
 */

void utn_tpio_tp_init( Tpio tpio )
{
 long level;

 if ( !tpio ) tpio = utn_tpio_get();
 for ( level = 0; level < tpio->depth; level++ ) {
  tpio->bptr[level] = NULL;
  utn_cs_zero( tpio->buffer[level] );
 }
 tpio->ptr = tpio->bptr;
}

/*----------------------------------------------------------------------*/
/* Internal buffer manipulation */

char** utn_tpio_set( Tpio tpio, FioStack iofs )
{
 long level; 
 if ( !tpio ) tpio = utn_tpio_get();
 if ( !iofs ) iofs = utn_fio_get_stack();
 level = utn_fio_iostack_get_level( iofs, FIO_INPUT );
 utn_tpio_level_check( tpio, level );
 if ( tpio->bptr[level] == NULL ) tpio->bptr[level] = tpio->buffer[level];
 tpio->ptr = &(tpio->bptr[level]);
 return( tpio->ptr );
}
/*
 * utn_tpio_getbuf
 *
 * For interactive use, prompt for new buffer and read it from the terminal
 * using iofs_read.
 * Strip comments (tabs and continuations are handled in iofs_read).
 * In non-interactive use, read buffer from input stream; keep trying
 * if you get a blank line or a line starting with comment as the first character. Keeps going until
 * it gets either end of file, or a line with data. The data returned to
 * the internal buffer can be blank if either 1) end of file is found or 2)
 * the line consists of whitespace followed by comment.
 *
 * Uses csproto.h
 * Uses escproto.h
 * Uses esc_types.h

 */


/*
 *--------------------------------------------------------------------------------
 */

logical utn_tpio_getbuf( Tpio tpio, FioStack iofs, char* prompt )
{
  logical retry;
  character cmt_char, esc_char;
  logical status;
  FioFile file;
  char* buf;
  char** ptr;
  TextCard ebuf;

  if ( !iofs ) iofs = utn_fio_get_stack();
  if ( !tpio ) tpio = utn_tpio_get();
  ptr = utn_tpio_set( tpio, iofs );
  buf = *ptr;
  retry = UT_TRUE;
  esc_char = utn_esc_special_char( ESC_CHAR );
  cmt_char = utn_esc_special_char( CMT_CHAR );
  while( retry || *buf == cmt_char ) {
   status = utn_fio_iostack_read( iofs, buf, prompt, UT_TPIO_BUFLEN );
   if ( utn_dbg_level(UT_VERBOSE_FIO) ) {
    snprintf( ebuf, UT_CARD_SIZE, "TPIO new buffer:%s", buf );   
    utn_msg_d( UT_VERBOSE_FIO, ebuf );
   }
   retry = ( status && utn_cs_is_blank( buf ));
   if ( retry )
   {
    file = utn_fio_iostack_file( iofs, FIO_INPUT );
    retry  = !utn_fio_file_interactive( file );
   }
  }

  utn_cs_decmt( buf, esc_char, cmt_char );
  return( status );
}

/*----------------------------------------------------------------------*/
/*
 *
 * utn_tpio_loop_check
 *
 * Test for \END if in loop.
 * Uses csproto.h, esc_types.h, escproto.h
 */

static logical utn_tpio_loop_check( Tpio tpio, FioFileList list, char* buf )
{
 character end_string[5] = "\\END";

 if ( !tpio ) tpio = utn_tpio_get();

 if ( tpio->nloop > 0 ) {
  if ( !list ) list = utn_fio_get_list();
  if ( utn_fio_list_any_eof( list, FIO_CHECK ))
  {
   end_string[0] = utn_esc_special_char( ESC_CHAR );
   return ( !utn_cs_eq( end_string, buf ) );
  }
 } 
 return( UT_FALSE );
}

/*----------------------------------------------------------------------*/
/*
 * utn_tpio_next_name
 *
 * Get the next word in the buffer
 * Uses tokenproto.h
 */

static void utn_tpio_next_name( Tpio tpio, char* buf )
{
  long siz;

 if ( !tpio ) tpio = utn_tpio_get();
  siz = FN_SIZE;
  utn_cs_get_token( tpio->ptr, buf, TMODE_WORD, siz );
}

/*----------------------------------------------------------------------*/
/*
 * tpio->cmdfile
 *
 * Open file and use as input 
 * Uses csproto.h
 */

static void utn_tpio_cmdfile( Tpio tpio, FioFileList list, FioStack iofs, char* buf )
{
 Filename filename;
 if ( !tpio ) tpio = utn_tpio_get();
 if( *buf == '\0' ) {
  utn_tpio_next_name( tpio, filename );
 } else {
  utn_cs_copy( buf, filename );
 }
 utn_tpio_tp_in( list, iofs, filename );
}

/*----------------------------------------------------------------------*/
/*
 * utn_tpio_logfile
 *
 * Uses csproto.h
 * 
 */


static void utn_tpio_logfile( Tpio tpio, FioFileList list, FioStack iofs, char* buf )
{
 Filename filename;
 logical append, syserr;
 long lptr;

 char* ptr;
 lptr = 1;
 ptr = buf;
 append = (*ptr == '>' );
 if ( append ) ptr++;
 syserr = (*ptr == '&' );
 if ( syserr ) ptr++;
 utn_fio_iostack_opop( iofs, list, FIO_POP_CLOSE );
 utn_cs_copy( ptr, filename );
 if ( utn_cs_is_blank( filename ) ) utn_tpio_next_name( tpio, filename );
 utn_tpio_out( filename, append, syserr );
}

void utn_tpio_set_logfile( char* buf )
{
 utn_tpio_logfile( NULL, NULL, NULL, buf );
}

/*----------------------------------------------------------------------*/
/*
 * utn_tpio_esc
 * Uses csproto.h
 */

static logical utn_tpio_esc( Tpio tpio, FioFileList list, FioStack iofs, char* buf )
{
 logical flag;
 TextCard ebuf;
 TextCard ubuf;
 long ival;
 char* ptr;
 TextWord token;
 TextWord token1;
 TextCard embuf;
 FioPager pager;

 if ( !tpio ) tpio = utn_tpio_get();

 utn_cs_copy( " ", embuf );
 flag = UT_TRUE;
 utn_cs_copy( buf, ebuf );
 utn_cs_upper( ebuf );
 ptr = ebuf;
 if( buf[0] == '<' )
  utn_tpio_cmdfile( tpio, list, iofs, &buf[1] );
 else if ( buf[0] == '>' )
  utn_tpio_logfile( tpio, list, iofs, &buf[1] );
 else if ( utn_cs_eq_begin( buf, "[*" ) ) {
  ival = utn_cs_read_i( utn_cs_upto( &buf[2], ']', ubuf ) );
  utn_dbg_set( ival );
  snprintf( tpio_tmp, TPIO_TMP_MAX, "DEBUG SET %ld\n", (long)ival );
  utn_msg_d( ival, tpio_tmp );
 } else if ( utn_cs_eq( ptr, "SET" ) ) {
  utn_msg_d( UT_VERBOSE_FIO, "ESC <Reset special chars>" );
  utn_cs_get_token( tpio->ptr, token, TMODE_WORD, WORD_SIZE );
  snprintf( tpio_tmp, TPIO_TMP_MAX, "ARG = %s", token );
  utn_msg_d( UT_VERBOSE_FIO, tpio_tmp );
  if ( utn_cs_eq( token, "?" ) ) {
   utn_esc_status();
  } else {
   utn_cs_get_token( tpio->ptr, token1, TMODE_WORD, WORD_SIZE );
   utn_esc_reset( token, *token1 );
  }
 } else if ( utn_cs_eq( ptr, "VER" ) ) {
  utn_ver_msg();
 } else if ( utn_cs_eq( ptr, "VERBOSE" ) || utn_cs_eq( ptr, "DEBUG") ) {
  utn_cs_get_token( tpio->ptr, token, TMODE_WORD, WORD_SIZE );
  ival = utn_cs_read_i( token );
  utn_dbg_set( ival );
  snprintf( tpio_tmp, TPIO_TMP_MAX, "DEBUG SET %ld\n", (long)ival );
  utn_msg_d( ival, tpio_tmp );
 } else if ( utn_cs_eq( ptr, "CLEAR" ) ) {
  utn_state_clear_error( NULL );
  utn_msg_d( UT_VERBOSE, "Cleared error state" );
 } else if ( utn_cs_eq( ptr, "DO" ) ) {
  utn_loop_do( tpio, iofs );
 } else if ( utn_cs_eq( ptr, "END" ) ) {
  utn_loop_end( tpio, iofs );
 } else if ( utn_cs_eq( ptr, "ECHO" ) ) {
  utn_loop_echo( tpio, iofs );
 } else if ( utn_cs_eq( ptr, "EXIT" ) ) {
  utn_fio_iostack_ipop( iofs, list, FIO_POP_CLOSE );
 } else if ( *ptr == '?' ) { 
  if ( utn_cs_eq( ptr, "?IOFS" ) ) {
   utn_fio_iostack_print_state(iofs);
  } else if ( utn_cs_eq( ptr, "?FIO" ) ) {
   utn_fio_list_print(list);
  } else if ( utn_cs_eq( ptr, "?BIO" ) ){
   utn_fio_basic_print_state();
  } else if ( utn_cs_eq( ptr, "?PAGER" ) ){
   pager = utn_fio_pager( list );
   utn_fio_pager_print_state( pager );
  } else if ( utn_cs_eq( ptr, "?PATH" ) ){
   utn_fio_list_list_path( list );
  } else if ( utn_cs_eq( ptr, "?TPIO" ) ) {
   utn_tpio_print_state();
  } else if ( utn_cs_eq( ptr, "?ESC" ) ) {
   utn_esc_status();
  } else if ( utn_cs_eq( ptr, "?ERR" ) ) {
   utn_state_print_error( NULL );
  } else {
   utn_cs_copy( ptr, embuf );
  } 
 } else {
  utn_cs_copy( ptr, embuf );
 }
 flag = !( utn_cs_is_blank( buf ) || !utn_cs_is_blank( embuf ) );
 if ( !flag ) {
  snprintf( tpio_tmp, TPIO_TMP_MAX, " [ESC] Unknown escape command-:%-s", embuf );
  utn_state_error( NULL, tpio_tmp );
 }
 return( flag );
}

/*----------------------------------------------------------------------*/
/*
 * utn_tpio_test_esc
 * Uses csproto.h escproto.h esc_types.h
 */

static logical utn_tpio_test_esc( Tpio tpio, FioFileList list, FioStack iofs, char* buf )
{
 logical flag;
 character esc_char;
 esc_char = utn_esc_special_char( ESC_CHAR );
 flag = utn_tpio_loop_check( tpio, list, buf );
 if ( !flag ) {
  if ( buf[0] == esc_char ) {
   if ( buf[1] == esc_char ) { utn_cs_copy( &buf[1], buf ); return( flag ); }
   flag = utn_tpio_esc( tpio, list, iofs, &buf[1] );
  }
 }
 return( flag );
}


void utn_tpio_exit( void )
{
 FioFileList list = utn_fio_get_list();
 FioStack iofs = utn_fio_get_stack();
 utn_fio_iostack_ipop( iofs, list, FIO_POP_CLOSE );
}


/*----------------------------------------------------------------------*/
/*
 * utn_tpio_token
 *
 * Read new buffer if needed until token found. Handle escapes.
 * Pop all stacks if EOF found.
 * Uses tokenproto.h, ioproto.h
 */


void utn_tpio_tp_token( Tpio tpio, FioFileList list, FioStack iofs, char* prompt, char** pptr, long mode, char* token, long siz )
{

 logical loop;
 logical status;
 TextCard tbuf;
 long level, pos;

 if ( pptr )
 {
  utn_cs_get_token( pptr, token, mode, siz );
  return;
 }
 if ( !tpio ) tpio=utn_tpio_get();
 if ( !list ) list = utn_fio_get_list();
 if ( !iofs ) iofs = utn_fio_get_stack();
 loop = UT_TRUE;
 status = UT_FALSE;
 while ( loop ) {
  if ( utn_dbg_level(UT_VERBOSE_FIO) ) {
   utn_tpio_loc( tpio, iofs, &level, &pos );
   snprintf( tbuf, UT_CARD_SIZE, "TPIO TOKEN on Level %-6ld Pos %-6ld", (long)level, (long)pos );
   utn_msg_d( UT_VERBOSE_FIO, tbuf );
  }
  if ( *tpio->ptr == NULL ||  **tpio->ptr == '\0' ) 
  { 
   status = utn_tpio_getbuf( tpio, iofs, prompt );
   if ( utn_dbg_level(UT_VERBOSE_FIO) ) {
    utn_tpio_loc( tpio, iofs, &level, &pos );
    snprintf( tbuf, UT_CARD_SIZE, "TPIO new buffer on Level %-6ld Pos %-6ld", (long)level, 0L );
    utn_msg_d( UT_VERBOSE_FIO, tbuf );
   }
  }
  utn_cs_get_token( tpio->ptr, token, mode, siz );
  loop = utn_tpio_test_esc( tpio, list, iofs, token );
 }

 if ( utn_dbg_level(UT_VERBOSE_FIO) ) {
  snprintf( tbuf, UT_CARD_SIZE, "TPIO TOKEN =:%s: REM =:%s: %c", *tpio->ptr, token,
   status ? ' ' : '*' );
  utn_msg_d( UT_VERBOSE_FIO, tbuf );
 }
}

/*----------------------------------------------------------------------*/
/*
 * utn_tpio_in
 * Uses ioproto.h fileproto.h
 * Resets TPIO input to the file named in the argument
 */

void utn_tpio_in( char* buf )
{
 utn_tpio_tp_in( NULL, NULL, buf );
}


void utn_tpio_tp_in( FioFileList list, FioStack iofs, char* buf )
{

 FioFile file = NULL;
 long in = 0;
 TextCard tbuf;

 if ( !iofs ) iofs = utn_fio_get_stack(); 
 if ( !list ) list = utn_fio_get_list();
 if ( utn_cs_eq( buf, "*" ) ) 
 {
  utn_msg_d( UT_VERBOSE_FIO , "Opened interactive command level" );
 } else if ( utn_cs_eq( buf, "*RAW" )) {
  file = utn_fio_open_raw();

 } else {
  file = utn_fio_list_open_file( list, buf, "SEQ R" );
  if ( !file ) {
   snprintf( tpio_tmp, TPIO_TMP_MAX, "Failed to open command file %s", buf );
   utn_state_error( NULL, tpio_tmp );
   utn_fio_iostack_iclear( list, iofs );
  } else {
   in = utn_fio_file_fid( file );
   snprintf( tbuf, UT_CARD_SIZE, "OPEN COMMAND FILE %s CHANNEL %ld", buf, (long)in );
   utn_msg_d( UT_VERBOSE_FIO, tbuf );
  }
 }
 utn_fio_iostack_ipush( iofs, list, file );

}

void utn_tpio_open_raw( void (*resync)( void*, int ), void* driver )
{
 FioFile file;
 FioStack iofs;
 iofs = utn_fio_get_stack(); 

 utn_tpio_tp_in( NULL, iofs, "*RAW" );
 file = utn_fio_iostack_file( iofs, FIO_INPUT );
 if ( resync )
  utn_fio_raw_set_resync( file, resync, driver ); 
}



/*----------------------------------------------------------------------*/
/*
 * utn_tpio_out
 * Uses ioproto.h fileproto.h
 * Resets TPIO output to the file in the argument
 * If syserr is true, sends errors there too.
 * If append is true, appends output to an existing file.
 */

void utn_tpio_out( char* buf, logical append, logical syserr )
{
 utn_tpio_tp_out( NULL, NULL, buf, append, syserr );
}


void utn_tpio_tp_out( FioFileList list, FioStack iofs, char* buf, logical append, logical syserr )
{
 FioFile file = NULL;
  if ( !list ) list = utn_fio_get_list();
 if ( utn_cs_eq( buf, "*" ) ) {
  file = NULL;
 } else if ( append ) {
  file = utn_fio_list_open_file( list, buf, "SEQ A" );
 } else {
  file = utn_fio_list_open_file( list, buf, "SEQ W" );
 }

 utn_fio_iostack_opush( iofs, list, file );
 if ( syserr ) utn_fio_stack_set_err( file );
}

/*
 *--------------------------------------------------------------------------------
 * Handles TPIO loop control
 */


TpioLoop* utn_tpio_loop_push( Tpio tpio )
{
 TpioLoop* loop;
 if ( tpio->nloop >= tpio->maxloop ) 
 {
  tpio->maxloop *= 2;
  tpio->loop = realloc( tpio->loop, tpio->maxloop * sizeof ( TpioLoop ));
  snprintf( tpio_tmp, TPIO_TMP_MAX, "TPIO loop stack size increased to %ld", tpio->maxloop );
  utn_msg_d( UT_VERBOSE_FIO, tpio_tmp );
 }

 tpio->nloop++;
 loop = &tpio->loop[tpio->nloop-1];
 return loop;
}




void utn_loop_do( Tpio tpio, FioStack iofs )
{
 TextCard buf0;
 char* buf;
 FioFile file;
 TpioLoop* loop;
 logical truncated = UT_FALSE;
 buf = buf0;

 if ( !tpio ) tpio=utn_tpio_get();

 utn_cs_get_token( tpio->ptr, buf, TMODE_STRING, CARD_SIZE );
 loop = utn_tpio_loop_push( tpio );
 utn_cs_copy_token_esc( &buf, "=", " ", 0, loop->lvar, WORD_SIZE, &truncated );
 if ( utn_cs_eq( loop->lvar, "EOF" ) ) 
  {
   loop->value = 0;
   loop->step = 0;
   loop->maxval = 0;
  } else {
   loop->value = utn_cs_get_i( &buf );
   loop->maxval  = utn_cs_get_i( &buf );
   loop->step =  ( buf == NULL ) ? 1 : utn_cs_get_i( &buf );
  }
 file = utn_fio_iostack_file( iofs, FIO_INPUT );
 loop->addr = utn_fio_file_get_lineno( file );
}

/*
 *--------------------------------------------------------------------------------
 * Handles loop iteration and end of loop condition
 */


void utn_loop_end( Tpio tpio, FioStack iofs )
{
 logical finish;
 TpioLoop* loop;
 if ( !tpio ) tpio=utn_tpio_get();

 if ( tpio->nloop == 0 ) {
  utn_state_error( NULL, "TPIO: Loop END without DO" );
 } else {
  loop = &tpio->loop[tpio->nloop-1];
  if ( loop->step == 0 ) {
   finish = utn_fio_any_eof( 1 );
  } else {
   loop->value += loop->step;
   if ( loop->step >= 0 ) {
    finish = ( loop->value > loop->maxval );
   } else {
    finish = ( loop->value < loop->maxval );
   }
  }
  if ( finish ) {
   loop->value = 0;
   tpio->nloop--;
  } else {
   utn_loop_goto( iofs, loop->addr );
  }
 }
}

/*
 *--------------------------------------------------------------------------------
 * Go to a specified line in a sequential text file
 */


void utn_loop_goto( FioStack iofs, long addr )
{
 long i;
 TextCard buf;
 FioFile file;

 utn_fio_iostack_rewind_input( iofs );
 file = utn_fio_iostack_file( iofs, FIO_INPUT );
 for ( i = 0; i < addr; i++ ) 
 {
  utn_fio_read_line( file, buf, CARD_SIZE );
 }
}

/*
 *--------------------------------------------------------------------------------
 * Echo text substituting $I macros
 */

void utn_loop_echo( Tpio tpio, FioStack iofs )
{

 character macro_char;
 TextBuf buf;
 TextWord old, new;
 TpioLoop* loop;
 long i;
 if ( !tpio ) tpio=utn_tpio_get();

 utn_cs_get_token( tpio->ptr, buf, TMODE_STRING, CARD_SIZE );
 macro_char = utn_esc_special_char( MACRO_CHAR );
 if ( utn_cs_index_char( buf, macro_char ) > 0 ) {
  for ( i = 0; i < tpio->nloop; i++ ) {
   *old = macro_char;
   loop = &tpio->loop[i];
   utn_cs_copy( loop->lvar, old+1 );
   utn_cs_write_i( loop->value, new, WORD_SIZE );
   utn_cs_string_replace( buf, old, new );
  }
 }
 utn_fio_iostack_msg( iofs, NULL, buf, UT_TRUE, UT_FALSE );
}

/*
 *--------------------------------------------------------------------------------
 * Display TPIO internal state
 */
void utn_tpio_disp( long i )
{
 utn_tpio_tp_disp( NULL, NULL, NULL, i );
}


void utn_tpio_tp_disp( Tpio tpio, FioStack iofs, FioFileList list, long i )
{
 long idbg;

 character clev[8];

 if ( !iofs ) iofs = utn_fio_get_stack();
 idbg = utn_dbg_get();
 if ( idbg >= i ) {
  snprintf( clev, 8, "[*%ld]", (long)i );
  if ( idbg > UT_VERBOSE_FIO ) { 
   utn_fio_list_print( list );
   utn_fio_iostack_print_state( iofs );
  } else {
   utn_fio_iostack_print_top( iofs, "I", clev );
  }
  utn_tpio_tp_print_state( tpio, iofs );
 } 

}
/*
 *--------------------------------------------------------------------------------
 */

void utn_tpio_print_state( void )
{
 utn_tpio_tp_print_state( NULL, NULL );
}


void utn_tpio_tp_print_state( Tpio tpio, FioStack iofs )
{
 long level;
 TextBuf buf;
 TextWord spacer;
 long min_lev;
 long lev;
 char* gptr;
 long pos;
 TextWord eflag;
 TextBuf disp;
 TextWord stat;

 if ( !tpio ) tpio=utn_tpio_get();

 level = utn_fio_iostack_get_level( iofs, FIO_INPUT );
 utn_tpio_level_check( tpio, level );
 utn_fio_iostack_msg( iofs, NULL, " ", UT_TRUE, UT_FALSE );
 utn_fio_iostack_dashline( iofs, 40 );
 utn_fio_iostack_msg( iofs, NULL, " JCMLIB Command Processor (TPIO) Status ", UT_TRUE, UT_FALSE );
 utn_fio_iostack_dashline( iofs, 40 );
 snprintf( buf, UT_TEXT_SIZE, "  TPIO Stack Level: %ld", (long)level );
 utn_fio_iostack_msg( iofs, NULL, buf, UT_TRUE, UT_FALSE );
 utn_fio_iostack_msg( iofs, NULL, "  ------------------- ", UT_TRUE, UT_FALSE );
 utn_fio_iostack_msg( iofs, NULL, "  | Level Pos Flag  | ", UT_TRUE, UT_FALSE );
 utn_cs_copy( "  |                 | " , spacer );
 min_lev = tpio->cmd_mode ? 0 : 1;
 for ( lev = level; lev >= min_lev; lev-- ) {
  gptr = tpio->buffer[lev];
  if ( tpio->bptr[lev] == NULL ) {
   pos = -1;
   utn_cs_copy( "Empty", eflag );
  } else {
   pos = tpio->bptr[lev] - tpio->buffer[lev];
   utn_cs_copy( " ", eflag );
  }
  snprintf( stat, UT_WORD_SIZE, "  | %-6ld%-4ld%-6s| ", (long)lev, (long)pos, eflag );
  if ( pos == 0 ) {
   utn_cs_copy( ":^", disp );
  } else if ( pos < 0 ) {
   utn_cs_copy( "<", disp );
  } else if ( pos > 250 ) {
   utn_cs_copy( ":?", disp );
  } else {
   snprintf( disp, UT_TEXT_SIZE, ":%-250s", " " );
   disp[pos] = '^';
  }
  snprintf( buf, UT_TEXT_SIZE, "%-21s:%s", stat, tpio->buffer[lev] );
  utn_fio_iostack_msg( iofs, NULL, buf, UT_TRUE, UT_FALSE );
  snprintf( buf, UT_TEXT_SIZE, "%-21s%s", spacer, disp );
  utn_fio_iostack_msg( iofs, NULL, buf, UT_TRUE, UT_FALSE );
 } 
 utn_cs_copy( "  ------------------ ", buf );
 utn_fio_iostack_msg( iofs, NULL, buf, UT_TRUE, UT_FALSE );
 utn_fio_iostack_msg( iofs, NULL, " ", UT_TRUE, UT_FALSE );
}

/*
 *--------------------------------------------------------------------------------
 * Top level TPIO special command routine
 */


void utn_tpio_tp_cc( Tpio tpio, FioFileList list, FioStack iofs, char* prompt, char* buf, long siz )
{

 if ( !tpio ) tpio=utn_tpio_get();

 if ( tpio->cmd_mode ) {
  utn_tpio_tp_cc_cmd( tpio, list, iofs, prompt, &tpio->cmd_mode, buf, siz );
 }
 if ( !tpio->cmd_mode ) {
  utn_tpio_c( prompt, buf, siz  );
  if ( utn_dbg_get() >= UT_VERBOSE_FIO ) {
   snprintf( tpio_tmp, TPIO_TMP_MAX, "TPIO_CC READ WORD %s", buf );
   utn_msg_d( UT_VERBOSE_FIO, tpio_tmp );
  }
  utn_tpio_tp_cc_start_check( tpio, buf, &tpio->cmd_mode );
  if ( tpio->cmd_mode ) utn_tpio_tp_cc_cmd( tpio, list, iofs, prompt, &tpio->cmd_mode, buf, siz );
 }
}

/*
 *--------------------------------------------------------------------------------
 * Check if we are in a special mode
 */


void utn_tpio_tp_cc_start_check( Tpio tpio, char* buf, logical* mode )
{
 TextCard ubuf;

 if ( !tpio ) tpio=utn_tpio_get();

 if ( *buf == utn_esc_special_char( ESC_CHAR ) ) {
  utn_cs_copy( buf+1, ubuf );
  if ( utn_cs_is_blank( ubuf ) || utn_cs_eq( ubuf, "TABLE" ) ) {
   *mode = UT_TRUE;
   utn_msg_d( UT_VERBOSE_FIO, "TPIO_CC SET CMD TRUE" );
   utn_cs_get_token( tpio->ptr, tpio->cmd, TMODE_LINE, TEXT_SIZE );
   utn_tpio_cclear( tpio, 0 );
  }
 }
}

/*
 *--------------------------------------------------------------------------------
 */


void utn_tpio_tp_cc_cmd( Tpio tpio, FioFileList list, FioStack iofs, char* prompt, logical* mode, char* buf, long siz )
{
 logical eot;
 logical found_esc;

 logical verbose;

 if ( !tpio ) tpio=utn_tpio_get();
 verbose = ( utn_dbg_get() >= UT_VERBOSE_FIO );

 if ( utn_cs_is_blank( tpio->cmd ) ) {
  *mode = UT_FALSE;
  if( verbose ) utn_msg_d( UT_VERBOSE_FIO, "TPIO_CC SET CMD FALSE (EMPTY)" );
 } else {
  utn_tpio_tp_cc_check( tpio, list, iofs, prompt, &eot, &found_esc, buf, siz );
  if ( eot ) {
   *mode = UT_FALSE;
   if( verbose ) utn_msg_d( UT_VERBOSE_FIO, "TPIO_CC SET CMD FALSE" );
  }
  if ( *mode  && !found_esc ) {
   utn_cs_get_token( tpio->ptr, buf, TMODE_WORD, siz );
   if ( verbose )
   {
    snprintf( tpio_tmp, TPIO_TMP_MAX, "TPIO_CC READ CMD %s", buf );
    utn_msg_d( UT_VERBOSE_FIO, tpio_tmp );
   }
  }
 }
}

/*
 *--------------------------------------------------------------------------------
 */


void utn_tpio_tp_cc_check( Tpio tpio, FioFileList list, FioStack iofs,
 char* prompt, logical* eot, logical* found_esc, char* buf, long siz )
{

 TextWord etoken;
 character esc;
 char* save_ptr = NULL;

 esc = utn_esc_special_char( ESC_CHAR );
 *eot = UT_FALSE;
 *found_esc = UT_FALSE;
 if ( !tpio ) tpio=utn_tpio_get();

 if ( !eot ) {
  save_ptr = *tpio->ptr;
  utn_cs_get_token( tpio->ptr, etoken, TMODE_WORD, WORD_SIZE );
  if ( utn_dbg_level( UT_VERBOSE_FIO ))
  {
   snprintf( tpio_tmp, TPIO_TMP_MAX, "TPIO_CC CHECK %s", etoken );
   utn_msg_d( UT_VERBOSE_FIO, tpio_tmp );
  }
  if ( *etoken == esc && *(etoken+1) != esc ) {
   utn_cs_upper( etoken );
   utn_tpio_tp_cc_esc( tpio, list, iofs, etoken + 1, prompt, eot, found_esc, buf, siz );
  }
 }
 if ( ! (*found_esc) ) {
  *tpio->ptr = save_ptr;
 }
}

/*
 *--------------------------------------------------------------------------------
 */


void utn_tpio_tp_cc_esc( Tpio tpio, FioFileList list, FioStack iofs,
  char* ecmd, char* prompt, logical* eot, 
 logical* found_esc, char* buf, long siz )
{

 *(found_esc) = UT_TRUE;
 if ( !tpio ) tpio=utn_tpio_get();

 if ( utn_cs_is_blank( ecmd ) || utn_cs_eq( ecmd, "ENDTABLE" ) ) {
  *eot = UT_TRUE;
  utn_msg_d( UT_VERBOSE_FIO, "TPIO_CC EOT FOUND" );
 } else if ( utn_cs_eq( ecmd, "C" ) ) {
  utn_tpio_tp_token( tpio, list, iofs, prompt, NULL, TMODE_WORD, buf, siz );
 } else if ( utn_cs_eq( ecmd, "D" ) ) {
  utn_cs_get_token( tpio->ptr, buf, TMODE_WORD, siz );
  snprintf( tpio_tmp, TPIO_TMP_MAX, "TPIO_CC SKIP CMD %s", buf );
  utn_msg_d( UT_VERBOSE_FIO, tpio_tmp );
  utn_cs_copy( " ", buf );
 } else {
  *found_esc = UT_FALSE;
 }

}

/*
 *--------------------------------------------------------------------------------
 */


void utn_tpio_setbuf( char** arr, long n )
{
 utn_tpio_tp_setbuf( NULL, NULL, arr, n );
}

void utn_tpio_tp_setbuf( Tpio tpio, FioStack iofs, char** arr, long n )
{
 character sep;
 char* ptr;
 long i;
 long siz = UT_TPIO_BUFLEN;
 long le;
 if ( !tpio ) tpio = utn_tpio_get();
 if ( !iofs ) iofs = utn_fio_get_stack();
 sep = utn_esc_special_char( STRING_SEP );
 ptr = *utn_tpio_set( tpio, iofs );
 if (!utn_cs_is_blank(ptr) ) {
  le = utn_cs_ends( ptr );
  ptr += le;
  *ptr = sep;
  ptr++;
 }
 for ( i = 0; i < n; i++ ) { 
  utn_cs_put_token( arr[i], &ptr, sep, &siz );
 }
}


/*
 *  Specialized command argument processing for PRECESS family
 * This still needs some work to make it a clone of the Fortran version.
 * 1997 Nov 27 JCM
 */
/*
 *--------------------------------------------------------------------------------
 */

void utn_tpio_cmdargs( 
 char** args,         /* (i) Command line args */
 long nargs,       /* (i) Number of args */
 logical* interact,   /* (o) Interactive? */
 logical* opts        /* (o) Command line was not blank */
)
{
 logical fail_on_err = UT_FALSE;  /* Replicate old behaviour */
 utn_tpio_cmdargs2( args, nargs, interact, opts, fail_on_err );
}



void utn_tpio_cmdargs2( 
 char** args,         /* (i) Command line args */
 long nargs,       /* (i) Number of args */
 logical* interact,   /* (o) Interactive? */
 logical* opts,        /* (o) Command line was not blank */
 logical fail_on_err
)
{
 TextBuf argline;
 TextBuf emsg;
 TextBuf clargs;  
 logical truncated = UT_FALSE;
 FioFile in;
 FioFile out;


 TextBuf buf;
 char* ptr = NULL;
 Filename infile;
 Filename outfile;
 TextWord dargs;
 logical q;
 character sep[2];
 long id;
 FioFileList list;
 FioStack iofs;
 Tpio tpio;
 character esc;
 tpio = utn_tpio_get();
 iofs = utn_fio_get_stack();
 list = utn_fio_get_list();


/* Reconstitute the original command line */
 q = utn_sys_cmdline( args, nargs, argline, TEXT_SIZE );
 if ( !q )
 {
   snprintf( emsg, UT_TEXT_SIZE, "Warning: command line too long, truncated to %d chars", TEXT_SIZE );
   utn_msg_d( 0, emsg );
   utn_msg_d( 0, argline );
 }
 sep[0] = utn_esc_special_char( ARG_SEP );
 sep[1] = '\0';
 esc = utn_esc_special_char( ESC_CHAR );
 *interact = UT_TRUE;
 *opts = !utn_cs_is_blank( argline );
 if ( *opts ) {

/* There are some command line arguments */

  ptr = argline;
/* Read the first token and check if it is DEBUG or DEBUGn */
  utn_cs_copy_token_esc( &ptr, sep, " ", esc, clargs, TEXT_SIZE, &truncated );
  if ( truncated )
    utn_fio_tmsg( "Warning: first argument truncated" ); 
  utn_cs_copy_siz( clargs, dargs, 5 );
  utn_cs_upper( dargs );
  if ( utn_cs_eq( dargs, "DEBUG" ) ) {
   if ( !utn_cs_is_blank( clargs+5 ) ) {
    id = utn_cs_read_i( clargs+5 );
   } else {
    id = 10;
   }
   if ( ptr == NULL ) {
    utn_cs_copy( " ", clargs );
   } else {
    utn_cs_copy_token_esc( &ptr, sep, " ", esc, clargs, TEXT_SIZE, &truncated );
   }
  }
/* Get another token: this will be the input file, open it */
  *interact = ( ptr == NULL );
  if ( ptr != NULL ) {
   utn_cs_copy_token_esc( &ptr, sep, " ", esc, infile, FN_SIZE, &truncated );
   if ( truncated )
    utn_fio_tmsg( "Warning: infile argument truncated" ); 
   if ( !utn_cs_is_blank( infile ) ) {
    in = utn_fio_list_open_file( list, infile, "SEQ R" );
    utn_fio_iostack_ipush( iofs, list, in );
    if ( in ) 
    {
     snprintf( buf, UT_TEXT_SIZE, "Opened input file %s", infile ); 
     utn_fio_tmsg( buf );
    } else {
      snprintf( buf, UT_TEXT_SIZE, "Failed to open input file %s", infile );
     utn_fio_tmsg( buf );
      if ( fail_on_err ) exit(1);
    }


   }
  }
/* Get another token: this will be the output file,open it */
  if ( ptr != NULL ) {
   utn_cs_copy_token_esc( &ptr, sep, " ", esc, outfile, FN_SIZE, &truncated );
   if ( truncated )
    utn_fio_tmsg( "Warning: outfile argument truncated" ); 

   if ( !utn_cs_is_blank( outfile ) ) {
    out = utn_fio_open_aw( outfile );

    utn_fio_iostack_opush( iofs, list, out );
    if ( out ) { 
      snprintf( buf, UT_TEXT_SIZE, "Opened output file %s", outfile ); 
      utn_fio_tmsg( buf );
     } else {
      snprintf( buf, UT_TEXT_SIZE, "Failed to open output file %s", outfile ); 
      utn_fio_tmsg( buf );
      if ( fail_on_err ) exit(1);
    }
   } 
  }
/*  If you have arguments, put them in the TPIO buffer to be executed */
  if ( !utn_cs_is_blank( clargs ) ) {
   ptr = clargs;

   utn_tpio_tp_setbuf( tpio, iofs, &ptr, 1 );
  }
 }
}

