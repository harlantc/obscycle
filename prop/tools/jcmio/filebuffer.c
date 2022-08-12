/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/


#include "tpiolib.h"
#include <stdio.h>
#include <string.h>
static logical strip = TRUE;

#define utn_fio_FB_DEFAULT_CHUNK    10000
#define utn_fio_FB_DEFAULT_MAXLINES 1000
static FileBuffer utn_fio_file_buffer_current;  /* Current output buffer in use */

FileBuffer utn_fio_file_buffer_alloc( long size, long chunk_size, long maxlines )
{
 FileBuffer buffer;

 if ( chunk_size == 0 ) chunk_size = utn_fio_FB_DEFAULT_CHUNK;
 if ( size == 0 ) size = chunk_size;
 if ( maxlines == 0 ) maxlines = utn_fio_FB_DEFAULT_MAXLINES;

 buffer = (FileBuffer)malloc( sizeof( struct FileBuffer_s ) );
 buffer->nmax = maxlines;
 buffer->line = (long*)malloc( maxlines * UT_SZ_I );
 buffer->chunk_size = chunk_size;
 buffer->size = size;
 buffer->data = (char*)malloc( size * UT_SZ_C );
 if ( !buffer->data ) {
  free( buffer );
  utn_fio_tmsg( "Failed to allocate memory buffer" );
  return( NULL );
 }
/* Set first line to beginning of data */
 utn_fio_file_buffer_clear( buffer );
 return( buffer );
}

/* Free the file buffer */
void utn_fio_file_buffer_free( FileBuffer buffer )
{
 if ( !buffer ) return;
 free( buffer->data );
 free( buffer->line );
 free( buffer );
}

/* Clear the file buffer */
void utn_fio_file_buffer_clear( FileBuffer buffer )
{
 buffer->n = 1;
 buffer->top = 0;
 if( buffer->line)
  buffer->line[0] = 0;
 buffer->pos = 1;
}

logical utn_fio_file_buffer_print_line( FileBuffer buffer )
{
 TextBuf line;
 TextWord tmp;
 long pos;
 utn_cs_blank( line, WORD_SIZE );
 if ( utn_fio_file_buffer_read_line( buffer, line+6, TEXT_SIZE ) ) {
  pos = utn_fio_file_buffer_pos( buffer );
  utn_cs_write_i( pos, tmp, WORD_SIZE );
  utn_cs_put_ss( tmp, line, 1, 6 );
  utn_fio_msg( line );
  return( TRUE );
 } else {
  utn_fio_msg( "EOF" );
  return( FALSE );
 }
}


/* Skip blank lines */
logical utn_fio_file_buffer_skip_blanks( FileBuffer buffer )
{
 TextBuf line;
 logical loop = TRUE;
 logical blank = TRUE;
 while ( loop && blank ) {
  loop = utn_fio_file_buffer_read_line( buffer, line, TEXT_SIZE );
  if ( loop ) blank = utn_cs_is_blank( line );
 }
 utn_fio_file_buffer_move( buffer, -1 ); 
 return( loop );
}

/* Print file buffer status */
void utn_fio_file_buffer_status( FileBuffer buffer )
{
 TextCard buf;
 snprintf( buf, UT_CARD_SIZE, "Buffer size: %ld of %ld bytes %ld of %ld lines", buffer->top, buffer->size, buffer->n-1, buffer->nmax );
 utn_fio_msg( buf );
}

/* Find all the lines in the file buffer */

void utn_fio_file_buffer_find_lines( FileBuffer buffer )
{
 char* ptr;
 char* top;

 ptr = buffer->data;
 top = ptr + buffer->top;

 while ( ptr < top ) {
  if ( buffer->n >= buffer->nmax-1 ) 
  { 
   buffer->nmax = 2 * buffer->nmax + 10;
   buffer->line = realloc( buffer->line, buffer->nmax * UT_SZ_P ); 
  }
  if ( *ptr++ == '\n' ) {
   buffer->line[buffer->n++] = ptr - buffer->data;
  }
 }
 buffer->line[buffer->n] = buffer->top;
}

/* Return number of lines in file buffer */
long utn_fio_file_buffer_lines( FileBuffer buffer )
{
 return( buffer->n-1 );
}


/* copy bytes from buffer to line until line is full or all of line is taken.
 * Terminate line with null.
 * Increment buffer position.
 * Return false if already at last line.
 */
logical utn_fio_file_buffer_read_line( FileBuffer buffer, char* line, long maxlen )
{
 char* ptr;
 char* top;
 char* buf;
 long n;
 char c = '\0';
 if ( buffer->pos >= buffer->n ) {
  return( FALSE );
 } else {
  n = buffer->pos++;
  buf = line;
  ptr = buffer->data + buffer->line[n-1];
  top = buffer->data + buffer->line[n]-1;  /* Skip trailing newline */
   

  while( maxlen-- && ptr < top ) 
  { 
   *buf = *ptr++; 
    c= *buf;
    buf++;
  }
  *buf = '\0';
/* Strip trailing blanks */
  if ( strip && c == ' ' ) 
  {
   buf--;
   while( buf > line && *buf == ' ' ) buf--;
   buf++;
  }

  *buf = '\0';
  return( TRUE );
 }
}


/*
 *  Go to specified line
 */
logical utn_fio_file_buffer_seek( FileBuffer buffer, long n )
{
 logical ok;
 buffer->pos = n;
 ok = ( buffer->pos <= buffer->n );
 return ok;
}

logical utn_fio_file_buffer_move( FileBuffer buffer, long n )
{
 logical ok;
 buffer->pos += n;
 ok = ( buffer->pos <= buffer->n );
 return ok;
}

long utn_fio_file_buffer_pos( FileBuffer buffer )
{
 return( buffer->pos );
}

void utn_fio_file_buffer_write_line( FileBuffer buffer, char* line )
{
 utn_fio_file_buffer_write_general_line( buffer, line, TRUE );
}

void utn_fio_file_buffer_write_partial_line( FileBuffer buffer, char* line )
{
 utn_fio_file_buffer_write_general_line( buffer, line, FALSE );
}


void utn_fio_file_buffer_insert_lines( FileBuffer buffer, char** lines, long nlines )
{
 long i;
 long n = 0;
 char* start; 
 char* buf;
 long m;
 long spos = 0;
 for ( i = 0; i <  nlines; i++ )
  n += strlen( lines[i] ) + 1;
 if ( buffer->top + n > buffer->size )
 {
/* Need to enlarge buffer */
   buffer->size = ( buffer->size + buffer->top + n ) * 2 + 10;
   buffer->data = realloc( buffer->data, buffer->size * UT_SZ_C );
 }
 buffer->pos++;
 if ( buffer->pos < buffer->n )
  spos = buffer->line[buffer->pos-1];
 else  
  spos = buffer->top - 1;
 start = buffer->data + spos;
/* Move lines up by n */
 for ( i = buffer->top-1; i >= spos; i-- )
  buffer->data[i+n] = buffer->data[i];
 for ( i = buffer->pos; i <= buffer->n; i++ )
  buffer->line[i-1] += n;

/* Copy new data into vacated space */
 buf = start;
 for ( i = 0; i < nlines; i++ )
 {
  utn_cs_copy( lines[i], buf );
  m = strlen( lines[i] );
  buf[m] = '\n';
  buffer->line[buffer->pos+i] = buf - buffer->data;  
  buf += m + 1;
 }
 buffer->n += nlines;
}

/* Insert/overwrite */
void utn_fio_file_buffer_put_line( FileBuffer buffer, char* line )
{
 long n; 
 long i;
 long spos;
 long npos = 0;
 long on, dn;
 n = strlen( line );
 buffer->pos++;
 spos = buffer->line[buffer->pos-1];  
 if ( buffer->pos < buffer->n )
 {
  npos = buffer->line[buffer->pos];
/* The old line covered [spos:npos-1]  is npos-spos chars long
 * The new line covers  [spos:spos+n-1];  is n chars long
 * We need to shift the data to the right by   n - (npos-spos)
 */
  on = (npos - spos );
 } else {
  on = 0;
 }

 dn = n - on;
 if ( dn > 0 )
 {
   if ( buffer->top + dn > buffer->size )
   {
/* Need to enlarge buffer */
    buffer->size = ( buffer->size + buffer->top + dn ) * 2 + 10;
    buffer->data = realloc( buffer->data, buffer->size * UT_SZ_C );
   }
/* Move lines up by n */
 
  for ( i = buffer->top-1; i >= npos; i-- )
   buffer->data[i+dn] = buffer->data[i];
  for ( i = buffer->pos+1; i <= buffer->n; i++ )
   buffer->line[i-1] += dn;  
 }
/* Now overwrite n chars */

 for ( i = 0; i < n; i++ )
  buffer->data[i+spos] = line[i];

}


void utn_fio_file_buffer_write_general_line( FileBuffer buffer, char* line, logical newline )
{
 char* ptr;
 char* buf;
 char* start;
 long n;
 if ( line == NULL ) return;
 ptr = line;
 n = strlen( line );
 if ( buffer->top + n > buffer->size )
 {
/* Need to enlarge buffer */
   buffer->size = ( buffer->size + buffer->top + n ) * 2 + 10;
   buffer->data = realloc( buffer->data, buffer->size * UT_SZ_C );
 }
 start = buffer->data + buffer->top; 
 buf = start;
 while( *ptr != '\0' ) { *buf = *ptr++; buf++; }
 if( newline ) { 
  if ( strip ) {  /* Strip trailing blanks */
   buf--;
   while( *buf == ' ' && buf > start ) buf--;
   buf++;
  }
  *buf = '\n'; buf++;
  buffer->n++;
  buffer->line[buffer->n] = buf - buffer->data;
 }
 buffer->top = buf - buffer->data;
 if ( buffer->n >= buffer->nmax-1 ) 
 {
  buffer->nmax = buffer->n * 2 + 10;
  buffer->line = realloc( buffer->line, buffer->nmax * UT_SZ_P );
 }
 buffer->pos = buffer->n;
}


/*  
 * Read a file entirely into memory
 */ 
logical utn_fio_file_buffer_read( char* filename, FileBuffer buffer )
{
 FioFile in; 
 logical loop;
 long n;
 char* ptr;

/*
 *  Read data into buffer
 */
 utn_msg_d( 1, "Read file into memory" );
 in = utn_fio_open_sr( filename );
 if ( !in ) return( FALSE );
 loop = TRUE;
 while ( loop ) {
  if ( buffer->top + buffer->chunk_size > buffer->size ) 
  {
/* Need to enlarge buffer */
   buffer->size = ( buffer->size + buffer->top +  buffer->chunk_size ) * 2 + 10;
   buffer->data = realloc( buffer->data, buffer->size * UT_SZ_C );
  }
  ptr = buffer->data + buffer->top;
  loop = utn_fio_read_sio( in, ptr, buffer->chunk_size, &n, UT_SZ_C );
  buffer->top += n;
 }
 utn_fio_file_close( in );
 utn_msg_d( 1, "Find lines" );
 utn_fio_file_buffer_find_lines( buffer );
 utn_msg_d( 1, "Completed buffer read" );
 return( TRUE );
}

/*
 * Write a file from memory to disk
 */

int utn_fio_file_buffer_write( FileBuffer buffer, char* filename )
{
 FioFile file; 
 logical loop;
 long nw;
 long n;
 char* ptr;
 long nleft;
 TextBuf tbuf;

/*
 *   Read data to disk from buffer
 */
 snprintf( tbuf, UT_TEXT_SIZE, "Writing buffered file %s Records %ld", filename, buffer->top );
 utn_msg_d( 1, tbuf );

 file = utn_fio_open_sw( filename );
 if ( !file )
  return UT_FALSE;
 loop = UT_TRUE;
 ptr = buffer->data;
 nleft = buffer->top;
 while ( nleft > 0 && loop ) {
  n = min_i( nleft, buffer->chunk_size );
  loop = utn_fio_write_sio( file, ptr, n, &nw, UT_SZ_C );
  if ( !loop || n != nw ) 
   utn_fio_tmsg( "fio_file_buffer_ write failed" );
  ptr  += nw;
  nleft -= nw;
 }
 utn_fio_file_close( file );
 snprintf( tbuf, UT_TEXT_SIZE, "Closed file %s", filename );
 utn_msg_d( 1, tbuf );
 return UT_TRUE;
}




/* Configure msg to use the file buffer instead of the output stream */

void utn_fio_file_buffer_output( FileBuffer buffer )
{
 if ( buffer ) {
  utn_fio_file_buffer_clear( buffer );
  utn_fio_file_buffer_current = buffer;
  utn_fio_set_msg( utn_fio_file_buffer_msg );
 } else {
  utn_fio_set_msg( NULL );
 }
}

void utn_fio_file_buffer_msg( char* buf, logical newline )
{
 utn_fio_file_buffer_write_general_line( utn_fio_file_buffer_current, buf, newline );
}



