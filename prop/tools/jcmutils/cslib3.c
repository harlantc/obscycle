/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"
#include <ctype.h>

/*
 *   utn_cs_split_char*
 * 
 *   Split a char* into two parts. 
 *   Takes a char* of the form aaasbbb, where s is a separator character,
 *   and returns two char*s aaa and bbb, each null terminated.
 *   If the separator is not present, the first output char* contains the whole
 *   input and the second char* is null. The function returns true if the separator
 *   is found.
 *
 *   1996 Aug 26 JCM
 *   Args:
 *	buf		(i)	Input char*
 *	sep		(i)	Separator char
 *	buf1		(o)	char* before separator
 *	buf2		(o)	char* after separator.
 */

logical utn_cs_split_string( const char* buf, const char sep, char* buf1, char* buf2 )
{
 char* ptr;
 logical status ;

 status = UT_FALSE;
 ptr = (char*)buf;
 while ( *ptr != sep && *ptr != '\0' ) 
 {
  *buf1 = *ptr++;
  buf1++;
 }
 if ( *ptr++ != '\0' ) {
  status = UT_TRUE;
  while ( *ptr != '\0' )
  {
   *buf2 = *ptr++;
   buf2++;
  }
 }
 *buf1 = '\0';
 *buf2 = '\0';
 return( status );
}



/*
 *  Copy a char* converting all digits to the "#" char
 *  Used to match FITS keywords of the type  CnTYPj
 */
void utn_cs_strip_digits( const char* name, char* pattern, const long maxlen )
{
 long i, j;
 long k = 0;
   
 j = strlen( name );

 for ( i = 0; i < j && i < maxlen; i++ )
 {
    if ( !isdigit( name[i] ) )
    {
        pattern[k++] = name[i];
    } else if ( k == 0 || pattern[k-1] != '#' ) {
        pattern[k++] = '#';
    }
 }
 pattern[k] = '\0';
}
