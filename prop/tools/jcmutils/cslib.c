/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/*======================================================================*/
/*
 *   cs lib 
 *
 *   C char* routines to manipulate char*s
 *
 *   char*.h: strlen 		used in utn_cs_remove_nl
 *
 */
/*======================================================================*/

#include "utlib.h"



/*----------------------------------------------------------------------*/
/*
 *   utn_cs_remove_nl
 *
 *   Remove trailing \r and \n chars from a line.
 *
 *   1996 Aug 26 JCM
 *   Args:
 *	ptr		(i/o)  char* to remove trailing newlines from
 */

logical utn_cs_remove_nl( char* ptr )
{
 long length;
 logical q = UT_FALSE;
 length = strlen( ptr );
 if ( length ) {
  ptr += length - 1;
  while ( length && ( *ptr == '\n' || *ptr == '\r' ) ) { q = UT_TRUE; *ptr-- = '\0'; length--; }
 }
 return q;
}


/*----------------------------------------------------------------------*/
/*
 *   utn_cs_detab
 *
 *   Change tabs to spaces in a C char*
 *   1996 Aug 26 JCM
 *   Args:
 *	buf		(i/o)	char* to remove tabs from
 *
 */

void utn_cs_detab( char* buf )
{
 while( *buf != '\0' ) {
  if ( *buf == '\t' ) *buf = ' ';
  buf++;
 }
}


/*----------------------------------------------------------------------*/
/*
 *   utn_cs_decmt
 *
 *   Remove comments from a char* by placing a null at the first comment char.
 *   Goes through the char* changing occurrences of esc-cmt to cmt until
 *   a null is encountered or until a cmt without an esc is encountered.
 * 
 *   1996 Aug 26 JCM
 *   
 *   Args:
 *	buf		(i/o) 	char* to remove comments from
 *	esc		(i)	Escape char
 *	cmt		(i)	Comment char
 *
 */


void utn_cs_decmt( char* buf, const char esc, const char cmt )
{
 while ( *buf != '\0' ) {
  if ( buf[0] == esc && buf[1] == cmt ) {
   utn_cs_copy( buf+1, buf );   /* Move everything back one byte */
  }  
  else if ( *buf == cmt ) {
   *buf = '\0';
   return;
  }
  buf++;
 }
}


/*----------------------------------------------------------------------*/
/*   utn_cs_compress_spaces
 *  
 *   Copy source char* to destination, while
 *   reducing multiple spaces to a single space.
 *   1996 Aug 26
 *
 *   Args:
 *	source		(i)	Source char*
 *	dest		(o)	Output char*
 */
void utn_cs_compress_spaces( const char* isource, char* dest )
{
 char* source = (char*)isource;
 while ( ( *dest = *source++ ) != '\0' ) 
 {
  if ( *dest++ == ' ' ) 
  {
   while (*source == ' ' ) source++; 
  }
 }
}


/*----------------------------------------------------------------------*/
/*
 *+cs_upto
 * Copy source to dest up to first occurrence of char targ
 */
char* utn_cs_upto( const char* isource, const char targ, char* utn_cs_upto_buf )
{
 char* source = (char*)isource;
 char* ptr;
 ptr = utn_cs_upto_buf;
 while ( *source != '\0' && *source != targ ) {
  *ptr = *source++;
  ptr++;
 }
 *ptr = '\0';
 return( utn_cs_upto_buf );
}




void utn_cs_string_replace( char* cbuf, const char* old, const char* new )
{
 long lold;
 long lnew;
 long lbuf;
 TextBuf buf;
 long i;
 char* iptr;
 char* optr;
 char* iend;
 lold = strlen( old );
 lnew = strlen( new );
 lbuf = strlen( cbuf );
 iend = cbuf + lbuf - 1;
 i = 1;
 iptr = cbuf;
 optr = buf;
 while ( i > 0 ) { 
  i = utn_cs_index( iptr, old );  /* Location of OLD */
  if ( i > 0 ) {
   utn_cs_get_ss( iptr, optr, 1, i-1 );
   iptr += i-1;
   optr += i-1;
   utn_cs_put_ss( new, optr, 1, lnew );
   iptr += lold;
   optr += lnew;
   if ( iptr > iend ) i = 0;
  }
 }
 if ( iptr < iend ) {
  utn_cs_copy( iptr, optr );
 } else {
  *optr = '\0';
 }
 utn_cs_copy( buf, cbuf );
}


/*
 *   utn_cs_match
 *
 *  Return array index of first equality match of char* in Fortran-style char* array
 *  The size argument does not include the terminating null.
 *  Return 0 if no match
 *  1996 Aug 26 JCM
 *  Args:
 *	buf		(i) Search char*
 *	array		(i) Pointer to first char in array
 *	n		(i) Size of array
 *      size		(i) Size of each array char*; Number of bytes to advance pointer less one
 */

long utn_cs_fs_match( const char* buf, const char* iarray, const long n, const long size1 )
{
 char* array = (char*)iarray;
 long no;
 long size = size1;
 size++;  /* Allow for terminating null */

 for ( no = 1; no <= n; no++ )
  {
  if ( utn_cs_eq( buf, array ) ) return( no );
  array += size;
   }
 return( 0 );
}

/* Version of strcmp supporting null char*s */
logical utn_cs_strcmp( char* a, char* b )
{
 if ( !a )
 {
  if ( !b ) return 0;
  return 1;
 } else if ( !b ) {
  return -1;
 } else {
  return strcmp( a, b );
 } 
}
      
      

/*
 * Concatenate with separator
 */
char* utn_cs_dup_cat( char* a, char* b, char* sep )
{
 long n;
 char* p;
 if ( !a )
  return utn_cs_dup( b );
 if ( !b )
  return utn_cs_dup( a );

 n = strlen(a)+strlen(b)+strlen(sep) +1;
 p = malloc(n);
 snprintf( p, n, "%s%s%s", a, sep, b );
 return p;
}

