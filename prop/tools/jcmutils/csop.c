/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/*======================================================================*/
/*
 *   cs op
 *
 *   C char* routines to manipulate char*s
 *
 *
 */
/*======================================================================*/
#include "utlib.h"

/*----------------------------------------------------------------------*/
/*
 *   utn_cs_lt
 *
 *   Test for char* A < char* B, up to trailing spaces.
 *   (Note that strcmp cares about trailing spaces and we don't.)
 *   1996 Aug 29 JCM
 *   Args:
 *	buf1		(i)	char* A
 * 	buf2		(i)	char* B
 */

logical utn_cs_lt( const char* ibuf1, const char* ibuf2 )
{
 char* buf1 = (char*)ibuf1;
 char* buf2 = (char*)ibuf2;
 while( *buf1 == *buf2 && *buf1 != '\0' )  {
  buf1++;
  buf2++;
 }
 if ( *buf1 == '\0' ) return( !utn_cs_is_blank( buf2 ) );
 if ( *buf2 == '\0' ) return( UT_FALSE );
 return( *buf1 < *buf2 );
}

/*----------------------------------------------------------------------*/
/*
 *   utn_cs_gt
 *
 *   Test for char* A > char* B, up to trailing spaces.
 *   (Note that strcmp cares about trailing spaces and we don't.)
 *   1996 Aug 29 JCM
 *   Args:
 *	buf1		(i)	char* A
 * 	buf2		(i)	char* B
 */

logical utn_cs_gt( const char* ibuf1, const char* ibuf2 )
{
 char* buf1 = (char*)ibuf1;
 char* buf2 = (char*)ibuf2;
 while( *buf1 == *buf2 && *buf1 != '\0' )  {
  buf1++;
  buf2++;
 }
 if ( *buf1 == '\0' ) return( UT_FALSE );
 if ( *buf2 == '\0' ) return( !utn_cs_is_blank( buf1 ) );
 return( *buf1 > *buf2 );
}


