/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/*======================================================================*/
/*
 *    cs
 *
 *    Null terminated char* ("C char*") handling routines
 *
 * cchar*.h:  strlen 		called by utn_cs_ends
 * ctype.h:    toupper          called by utn_cs_upper
 */
/*======================================================================*/
#include <ctype.h>
#include "utlib.h"



/*----------------------------------------------------------------------*/
/*    utn_cs_copy_siz  
 *    
 *    Copy at most a given number of chars from one C char* to another.
 *    Resulting char* will be null terminated.
 *    1996 Aug 26 JCM
 *   Inputs:
 *     	source			Source char*
 *	siz			Max number of chars
 *   Outputs:
 *     	dest			Destination char*
 */

void utn_cs_copy_siz( const char* isource, char* dest, const long maxlen )
{
 char* source = (char*)isource;
 long siz = maxlen;
 if ( !dest ) return;
 if ( !siz )
 {
  utn_cs_copy( source, dest );
 } else {
  if ( source == NULL ) { *dest= '\0'; return; }
  while ( *source != '\0' && siz )
  {
   *dest = *source;
   source++;
   dest++; 
   siz--;
  }
  *dest = '\0';
 }
}

/*-----------------------------------------------------------------------*/
/*   utn_cs_copy
 * 
 *   Copy a char* until null termination reached. 
 *   WARNING: May overflow bounds of destination char*. Only use this routine
 *   if you are sure the source char* is null terminated with fewer chars
 *   than are available for the destination char*.
 *   1996 Aug 26 JCM
 *   Inputs:
 *     	source			Source char*
 *   Outputs:
 *     	dest			Destination char*
 */

void utn_cs_copy( const char* isource, char* dest )
{
 char* source = (char*)isource;
 if ( !dest ) return;
 if ( source == NULL ) { *dest= '\0'; return; }
 while ( ( *dest = *source++ ) != '\0' ) dest++;
}


/*----------------------------------------------------------------------*/
/*   utn_cs_get_ss
 *   
 *   Copy a subchar* of the source char*, place in destination char* and null terminate.
 *   Return a pointer to the destination char*.
 *   1996 Aug 26 JCM
 *   Inputs:
 *     	source			Source char*
 *     	pos1			Start position, counting from 1
 *     	pos2		 	Stop position, counting from 1
 *   Outputs:
 *     	dest			Destination char*
 */

char* utn_cs_get_ss( const char* isource, char* dest, const long pos1, const long pos2 )
{
 char* source = (char*)isource;
 char* result;  /* Pointer to start of destination char* */
 long siz;       /* Number of chars to copy */
 long m;

 m = strlen( source );
 result = dest;
 siz = pos2 - pos1 + 1;
 source += pos1 -1;
 if ( siz <= 0 || pos1 > m ) { 
  *dest = '\0';
 } else {
  while( *source && siz )
  {
   *dest = *source;
   dest++;
   source++;
   siz--;
  }
  *dest = '\0';
 }
 return( result );
}

/*----------------------------------------------------------------------*/
/*   utn_cs_put_ss
 *
 *   Copy a char* to a subchar* of the destination char*, adding 
 *   trailing spaces. No null termination is added.
 *   Warning: if the subchar* overwrites the null termination,
 *   you may get unexpected results. The intention is that this routine be
 *   used with char*s that have been set to all zero or all blank in advance.
 *   1996 Aug 26 JCM
 *   Inputs:
 *     	source			Source char*
 *     	pos1			Start position, counting from 1
 *     	pos2		 	Stop position, counting from 1
 *   Outputs:
 *     	dest			Destination char*
 */

void utn_cs_put_ss( const char* isource, char* dest, const long pos1, const long pos2 )
{
 char* source = (char*)isource;
 long siz;
 siz = pos2 - pos1 + 1;
 dest += pos1 - 1;
 while ( siz-- ) {
  if ( *source != '\0' ) {
   *dest = *source++;
  } else {
   *dest = ' ';
  }
  dest++;
 }
}

/*----------------------------------------------------------------------*/
/*   utn_cs_ends
 * 
 *   Return length of char* up to last non-blank char prior to first null.
 *   Can be zero.
 *   1996 Aug 26
 *
 *   Args:
 *	buf		(i)	char* to check
 */

long utn_cs_ends( const char* ibuf )
{
 char* buf = (char*)ibuf;
 long size;
 if ( !buf ) return 0;
 size = strlen( buf );
 buf += size - 1 ;
 while ( size > 0 && *buf-- == ' '  )  size-- ;

 return( size );
}

/*----------------------------------------------------------------------*/
/*   utn_cs_begs
 * 
 *   Return char position of first non blank char.
 *   1996 Aug 26
 *
 *   Args:
 *	buf		(i)	char* to check
 */

long utn_cs_begs( const char* ibuf )
{
 char* buf = (char*)ibuf;
 long size;
 long pos;

 size = strlen( buf );
 pos = 1;
 while ( pos < size && *buf == ' ' ) { buf++; pos++; }

 return( pos );
}



/*----------------------------------------------------------------------*/
/*
 *   utn_cs_zero
 *
 *   Set a char* to null
 *   1996 Aug 26
 *   Args:
 *	line		(i/o)	char*
 */

void utn_cs_zero( char* line )
{
 line[0] = '\0';
}


void utn_cs_blank( char* line, long maxlen )
{
 while( maxlen-- ) { *line = ' '; line++; }
 *line = '\0';
}


/*----------------------------------------------------------------------*/
/*
 *   utn_cs_denull
 *
 *   Change nulls to blanks
 *   1996 Aug 26
 *   Args:
 *	line		(i/o)	char*
 */

void utn_cs_denull( char* line, const long maxlen )
{
 long siz = maxlen;
 while ( siz-- ) {
  if ( *line == '\0' ) *line = ' ';
  line++;
 }
}


/*----------------------------------------------------------------------*/
/*
 *   utn_cs_null
 *
 *   Set a char* to all nulls
 *   1996 Aug 26
 *   Args:
 *	line		(i/o)	char*
 *      size		(i)     Size
 */

void utn_cs_null( char* line, const long maxlen )
{
 long size = maxlen;
 if ( !line ) return;
 while( size > 0 )  line[--size] = 0;
}

/*----------------------------------------------------------------------*/
/*
 *   utn_cs_upper
 *  
 *   Uppercase char*
 *   1996 Aug 26
 *   Args:
 *	buf		(i/o) 	char*
 *
 */

void utn_cs_upper( char* buf )
{
 if ( !buf ) return;
 while( *buf != '\0' ) 
 {
  *buf = toupper( *buf );
  buf++;
 }
}
void utn_cs_lower( char* buf )
{
 if ( !buf ) return;
 while( *buf != '\0' )
 {
  *buf = tolower( *buf );
  buf++;
 }
}

/* Reverse in place */
void utn_cs_reverse( char* buf )
{
 char c;
 long m = strlen( buf );
 char* endptr;
 char* ptr = buf;
 endptr = buf + m -1;
 while ( endptr > ptr ) 
 {
  c = *endptr;
  *endptr = *ptr;
  *ptr = c;
  ptr++;
  endptr--;
 }
}

/*--------------------------------------------------------------------------------*/
/*
 *  utn_cs_conc
 *
 */

char* utn_cs_conc( const char* buf1, const char* buf2, char* buf )
{
 long end;
 utn_cs_copy( buf1, buf );
 end = utn_cs_ends( buf1 );
 utn_cs_copy( buf2, buf+end );
 return( buf );
}

char* utn_cs_conc1( const char* buf1, const char* buf2, char* buf )
{
 long end;
 utn_cs_copy( buf1, buf );
 end = utn_cs_ends( buf1 );
 buf[end] = ' ';
 utn_cs_copy( buf2, buf+end+1 );
 return( buf );
}

void utn_cs_mixcase( char* buf )
{
 int after_space = 1;
 int space = 0;

 while( *buf != '\0' ) 
 {
  if ( isspace( *buf ))
   space = 1;
  else if ( after_space )
   *buf = toupper( *buf ); 
  else
   *buf = tolower( *buf ); 

  buf++;
  after_space = space;
  space = 0;
 }
}


logical utn_cs_uceq( const char* ibuf1, const char* ibuf2 )
{
 char* buf1 = (char*)ibuf1;
 char* buf2 = (char*)ibuf2;

 if ( !buf1 )
  return ( buf2 == NULL );
 
 while( ( toupper( *buf1 ) == toupper( *buf2 )) && *buf1 != '\0' )  {
  buf1++;
  buf2++;
 }

 if ( *buf1 == '\0' ) 
 {
  if( *buf2 == '\0' ) return UT_TRUE;
  return( utn_cs_is_blank( buf2 ) );
 }
 if ( *buf2 == '\0' ) return( utn_cs_is_blank( buf1 ) );
 return( UT_FALSE );
}



char* utn_cs_dup( const char* buf )
{
 char* ptr = NULL;
 if ( buf)
 {
  ptr = (char*)malloc( strlen(buf)+1 );
  strcpy( ptr, buf );
 }
 return ptr;
}

char* utn_cs_dup_siz( const char* isource, const long maxlen )
{
 char* source = (char*)isource;
 long siz = maxlen;
 char* retval;
 if ( source == NULL || maxlen == 0 ) 
  return NULL;
 
 retval = calloc( maxlen+1, UT_SZ_C);
 utn_cs_copy_siz( source, retval, siz );
 return retval;
}


void utn_cs_trim( char* buf )
{
 char* ptr;
 ptr = buf + strlen( buf ) - 1;
 while( ptr >= buf && ( *ptr == ' ' || *ptr == '\t' ) )
  ptr--;
 *(ptr+1) = '\0';
}
