/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/*======================================================================*/
/*
 *   cs lib 2
 *
 *   C char* routines to manipulate char*s
 *
 */
/*======================================================================*/

#include "utlib.h"
static int utn_ar_match_compare_c( const void *key, const void *elem );

/*----------------------------------------------------------------------*/
/*   utn_cs_logic
 *
 *   Return a char Y for true and N for false.
 *
 */

char utn_cs_logic( const logical q )
{
 return( q ? 'Y' : 'N' );
}

/*----------------------------------------------------------------------*/
/*
 *   utn_cs_advptr
 *
 *   Advance pointer to next non blank char.
 *
 *   Note:   buf       is a pointer to pointer to char.
 *           *buf      is a pointer to char.
 *           **buf     is the value of the char.
 *           (*buf)++  increments the pointer to char, making it polong to the next char in the char*.
 *           (**buf)++ increments the ASCII value of the char.
 *
 *   1996 Aug 26 JCM
 *   Args:
 *	buf		Pointer to C char*
 */

char* utn_cs_advptr( char** buf )
{
 char* ptr = *buf;
 while( *ptr == ' ' || *ptr == '\t' ) ptr++;
 *buf = ptr;
 return ptr;
}

/* As advptr, but return null if at end of char* */
void utn_cs_advance( char** buf )
{
 char* ptr = *buf;
 while( *ptr == ' ' || *ptr == '\t' ) ptr++;
 if ( *ptr == '\0' ) 
  *buf = NULL;
 else
  *buf = ptr;
}


/*----------------------------------------------------------------------*/
/*
 *   utn_cs_is_blank
 *
 *   Test whether null terminated char* consists only of blank chars.
 *   1996 Aug 26 JCM
 *   Args:
 *	buf		(i) char*
 */

logical utn_cs_is_blank( const char* ibuf )
{
 char* buf = (char*)ibuf;
 if ( !buf ) return 1;
 while( *buf == ' ' ) buf++;
 return( *buf  == '\0' );
}

logical utn_cs_uceq_siz( const char* buf1, const char* buf2, long rsiz )
{
 TextBuf ubuf1;
 TextBuf ubuf2;
 utn_cs_copy( buf1, ubuf1 );
 utn_cs_copy( buf2, ubuf2 );
 utn_cs_upper( ubuf1 );
 utn_cs_upper( ubuf2 );
 return( utn_cs_eq_siz( ubuf1, ubuf2, rsiz ) );
}




/*----------------------------------------------------------------------*/
/*
 *   utn_cs_eq
 *
 *   Test for equality for null/blank terminated char*s.
 *   char*s are null terminated; return equal if char*s are the same
 *   with the exception of trailing blanks.
 *   1996 Aug 26 JCM
 *   Args:
 * 	buf1		(i)	char*
 *	buf2		(i)	char*
 */

logical utn_cs_eq( const char* ibuf1, const char* ibuf2 )
{
 char* buf1 = (char*)ibuf1;
 char* buf2 = (char*)ibuf2;
 if ( !buf1 )
  return ( buf2 == NULL );
 
 while( *buf1 == *buf2 && *buf1 != '\0' )  {
  buf1++;
  buf2++;
 }
 if ( *buf1 == '\0' ) return( utn_cs_is_blank( buf2 ) );
 if ( *buf2 == '\0' ) return( utn_cs_is_blank( buf1 ) );
 return( UT_FALSE );
}


/*----------------------------------------------------------------------*/
/*
 *   utn_cs_eq_siz
 *
 *   Test for equality for null terminated char*s.
 *   Return true of the first /siz/ chars of buf1 and buf2 are equal.
 *   Not the same as the strncmp routine as trailing blanks are not
 *   significant.
 *   1996 Aug 26 JCM
 *   Args:
 * 	buf1		(i)	char*
 *	buf2		(i)	char*
 *      siz		(i)	Size
 */

logical utn_cs_eq_siz( const char* ibuf1, const char* ibuf2, const long rsiz )
{
 char* buf1 = (char*)ibuf1;
 char* buf2 = (char*)ibuf2;
 long siz = rsiz;
 char c1;
 char c2;

 if ( !buf1 )
  return ( buf2 == NULL );

 while( siz-- ) {
  c1 = *buf1;
  c2 = *buf2;
  if ( c1 == '\0' )
  {
   if ( c2 == '\0' ) return UT_TRUE;
   if ( c2 != ' ' ) return UT_FALSE;
   buf2++;
  }
  else if ( c2 == '\0' )
  {
   if ( c1 != ' ' ) return UT_FALSE;
   buf1++;
  }
  else
  {
   if ( c1 != c2 ) return UT_FALSE;
   buf1++;
   buf2++;
  }
 }
 return( UT_TRUE );
}

logical utn_cs_eq_begin( const char* ibuf1, const char* ibuf2 )
{
 long siz = strlen( ibuf2 );
 return utn_cs_eq_siz( ibuf1, ibuf2, siz );
}

logical utn_cs_eq_ss( const char* buf, const long pos1, const long pos2, const char* comp )
{
 long siz;
 char* ptr;

 ptr = (char*)buf + pos1 - 1;
 siz = pos2 - pos1 + 1;
 return( utn_cs_eq_siz( ptr, comp, siz ) );
}



/*----------------------------------------------------------------------*/
/*
 *   utn_cs_ss
 *
 *  Test whether subchar* is present.
 *  Wraps the standard routine strstr.
 *
 */

logical utn_cs_ss( const char* buf, const char* sub )
{
 if ( buf && sub )
   return( strstr( buf, sub ) != NULL ); 
 else
   return UT_FALSE;
}

/*----------------------------------------------------------------------*/
/*
 *   utn_cs_ss_char
 *
 *  Test whether char is present.
 *  Wraps the standard routine strchr.
 *
 */

logical utn_cs_ss_char( const char* buf, const char sub )
{
   return( strchr( buf, sub ) != NULL ); 
}

/*----------------------------------------------------------------------*/
/*
 *   utn_cs_index
 *
 *   Return the char position of the first occurence of the given subchar*.
 *   Return zero if the subchar* is not present.
 *   1996 Aug 26 JCM
 *   Args:
 *	buf		(i)	char* to search
 *	sub		(i)	char* to search for
 */

long utn_cs_index( const char* buf, const char* sub )
{
 char* ptr;

 ptr = strstr( buf, sub );
 if ( ptr == NULL )
  return(0);
 else 
  return( ptr - buf + 1 );
}

/*
 *   We look for a subchar* in buf starting at position pos.
 *   If pos is zero or negative, return zero. If the subchar*
 *   is not found, return zero. Otherwise return the
 *   position of the start of the subchar*.
 */

long utn_cs_eindex( const char* buf, const long pos, const char* sub )
{
 char* ptr;
 if ( pos <= 0 ) {
  return( 0 );
 } else {
  ptr = strstr( buf+pos-1, sub );
  if ( ptr == NULL ) {
   return( 0 );
  } else { 
   return( ptr - buf + 1 );
  }
 }
}
/*----------------------------------------------------------------------*/
/*
 *   cmatch
 *
 *  Return array index of first equality match of char* in char* array
 *  Return 0 if no match
 *  1996 Aug 26 JCM
 *  Args:
 *	buf		(i) Search char*
 *	array		(i) Pointer array to char*s
 *	n		(i) Size of array
 *
 */

long utn_ar_match_c( const char* buf, char** array, const long n )
{
 long no;

 for ( no = 0; no < n; no++ )
  {
  if ( array[no] && utn_cs_eq( buf, array[no] ) ) return( no+1 );
   }
 return( 0 );
}

long utn_ar_search_c( const char* buf, char** array, const long n )
{
 long no;

 for ( no = 0; no < n; no++ )
  {
  if ( array[no] && utn_cs_uceq( buf, array[no] ) ) return( no+1 );
   }
 return( 0 );
}

long utn_ar_cmatch_c( const char* buf, char** array, const long n )
{
 long no;

 for ( no = 0; no < n; no++ )
  {
  if ( array[no] && utn_cs_eq( buf, array[no] ) ) return( no );
   }
 return( -1 );
}

/* Use stdlib.h bsearch for efficient binary search in sorted array */

static int utn_ar_match_compare_c( const void *key, const void *elem )
{ 
  if ( elem && *(char**)elem )
    return(strcmp((char*)key,*(char**)elem));
  else
    return 0;
}  

long utn_ar_match_sorted_c( const char* buf, char** array, const long n )
{
 char** ptr;
 char** start;
 long k;
 if ( !buf || !(*buf) )
  return 0;

 start = &array[0];
 ptr = (char**)bsearch( (void*)buf, (void*)start, n, UT_SZ_P, utn_ar_match_compare_c );
 if ( ptr )
  k = ptr - start + 1; 
 else
  k = 0;
 return k;
}


long utn_cs_match_minimum( const char* itarg, const char** list, const long n )
{ 
 char* targ = (char*)itarg;
 long entry = 0;
 long match_entry = 0;
 long best_match_len = 0;
 long n_match = 0;
 long partial = 0;
 long n_partial = 0;
 long targ_len;
 long match_len;
 char* ptr;
 long result;
/*
 * Look for the shortest char* in a list that matches the target.
 *
* We loop through each list entry, seeing if it is exactly equal to the
* target. If it is, we remember its (1-based) entry number and increment the
* number of matches found so far. If not, we see if it is equal to
* the target up to the length of the target (e.g. target is "FOO" and
* entry is "FOOBAR"); this counts as a partial match. We see if it
* is the best partial match so far (shortest char* that matches targ).
* If so, we remember it and its length.
* At the end, if we found an exact match we return its entry number;
* otherwise if we found exactly one partial match we return its
* entry number; else we return zero.
* At the moment, all of the info about number of matches and lengths
* is thrown away; a future version of this routine will generate return
* information about ambiguous matches.
*
*/

 targ_len = utn_cs_ends( targ );
 for ( entry = 1; entry <= n; entry ++ ) {
  ptr = (char*)(list[entry-1]);
  if( utn_cs_eq( targ, ptr ) ) {
   match_entry = entry;
   n_match++;
  } else if ( utn_cs_eq_siz( targ, ptr, targ_len ) ) {
   match_len = utn_cs_ends( ptr );
   if( partial  == 0 || match_len <= best_match_len ) {
    if ( match_len == best_match_len ) n_partial++;
    partial = entry;
    best_match_len = match_len;
   }
  }
 }

 if( n_match >= 1 ) {
  result = match_entry;
 } else if ( n_partial == 1 ) {
  result = partial;
 } else {
  result = 0;
 }
 return( result );
}


long utn_cs_index_char( const char* buf, const char c )
{
 char* ptr;
 ptr = strchr( buf, c );
 if ( ptr == NULL ) {
  return( 0 );
 } else {
  return(  ptr - buf + 1 );
 }
}

/* For option   PRINT/L   parse into PRINT and L, return true if / is present */

logical utn_cs_subcmd_split( const char* opt, char* cmd, char* sub, const char sep )
{
 long i;
 logical ok = 1;
 i = utn_cs_index_char( opt, sep );
 if ( i == 0 ) {
  utn_cs_copy( opt, cmd );
  utn_cs_copy( " ", sub );
  ok = 0;
 } else if ( i == 1 ) {
  utn_cs_copy( " ", cmd );
  utn_cs_copy( opt+1, sub );
 } else {
  utn_cs_copy_siz( opt, cmd, i-1 );
  utn_cs_copy( opt+i, sub );
 }
 return ok;
}


void utn_cs_remove_quotes_and_trail( const char* val, char* dest, const long maxlen )
{

 long j;
 char* ptr = (char*)val;
 if ( *ptr == '\'' ) ptr++;
  /* Strip trailing quote and spaces */
 j = strlen( ptr )-1;
 while ( j > 0 && (ptr[j] == '\'' || ptr[j] == ' '  )) j--;
 if ( j > maxlen ) j = maxlen;
 utn_cs_copy_siz( ptr, dest, j+1 );
}


/* 
 *  Copy input to output.
 *  Strip leading "-quotes if present.
 *  
 */

char* utn_cs_strip_double_quote( char* src, char* tokbuf, integer maxlen )
{
 char* ptr = src;
 integer m;
 if( *ptr == '"' )
 {
  ptr++;
  m = utn_cs_index_char( ptr, '"' );
  if ( m > 0 && m < maxlen )
   utn_cs_copy_siz( ptr, tokbuf, m -1 );
  else
   utn_cs_copy_siz( ptr, tokbuf, maxlen );
 } else {
  utn_cs_get_c( &ptr, tokbuf, maxlen );
 }
 return ptr;
}

char* utn_cs_strip_quotes( char* src, char* tokbuf, integer maxlen )
{
 char* ptr = src;
 integer m;
 char c;
 c = *ptr;
 if( c == '"' || c ==  '\'' )
 {
  ptr++;
  m = utn_cs_index_char( ptr, c );
  if ( m > 0 && m < maxlen )
   utn_cs_copy_siz( ptr, tokbuf, m -1 );
  else
   utn_cs_copy_siz( ptr, tokbuf, maxlen );
 } else {
  utn_cs_get_c( &ptr, tokbuf, maxlen );
 }
 return ptr;
}

/* A common construction */
void utn_cs_append_delim( char* buf, char* delim, char* text )
{
 if ( strlen(buf) > 0 )
  strcat( buf, delim );
 strcat( buf, text );
}

void utn_cs_append( char** pptr, char* sep, char* buf )
{
 char* ptr;
 char* iptr;
 if ( *pptr )
 {
  iptr = *pptr;
  ptr = malloc( strlen( iptr ) + strlen( buf ) + 2);
  strcpy( ptr, iptr );
  if ( sep ) strcat( ptr, sep );
  strcat( ptr, buf ); 
  free( iptr );
  free( buf );
  *pptr = ptr;
 } else {
  *pptr = buf;
 }
}

char** utn_cs_dup_array( char** data, long n )
{
 char** buf = NULL;
 long i;

 if ( n > 0 )
 {
  buf = (char**)malloc( n * UT_SZ_P );
  for ( i = 0; i < n; i++ )
   buf[i] = utn_cs_dup( data[i] );
 }
 return buf;
}



logical utn_cs_match_wild( char* buf, char* mask, char wild )
{
 char* ptr = buf;
 char* mptr = mask;
 logical loop = UT_TRUE;
 while( loop )
 {
  if ( *mptr == wild )
  {
   while ( *mptr == wild )
   {
    mptr++;
    if ( *mptr == '\0' ) return UT_TRUE;   /* Trailing wild card */
    while ( *ptr != *mptr )
     ptr++;
    if ( *ptr == '\0' ) return UT_FALSE;   /* Did not find next non wild char */
   /* Now, *ptr is equal to next char in mptr; advance */
    ptr++;
    mptr++;
   }
  } else if ( *ptr == '\0' ) {
   if ( *mptr == '\0' ) return UT_TRUE;
   return UT_FALSE;  /* Mask chars left at end */
  } else {  /* Still chars in ptr */
   if ( *mptr == '\0' ) return UT_FALSE;   /* Ran out of mask */
   if ( *ptr != *mptr ) return UT_FALSE;   /* Does not match mask */
   /* Does match mask */
   ptr++;
   mptr++;
  }
 }
 return UT_TRUE;
}
