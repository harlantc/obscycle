/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"


/*----------------------------------------------------------------------*/
/* 
 * fs_ends
 *
 * Return blank terminated length  - allows nulls if later non null chars 
 *  
 *
 */

integer utn_cs_fs_ends( string buf, integer size )
{
/* F type string */
 while ( size > 0 && ( buf[size-1] == ' ' ) )
  size--;
 return( size );
}

/*----------------------------------------------------------------------*/
/* fs_zero
 * 
 * Make a blank string of given length, without null termination.
 *
 */

void utn_cs_fs_zero( string line, integer size )
{
 /* Make a blank string to length size */
 while( size > 0 )  line[--size] = ' ';
}



