/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"


/*----------------------------------------------------------------------*/
/*
 *+cs_list_add
 *
 *
 *  Add an entry 'item' to a list of char*s and return its number in the list.
 *  If item is already in the list, return its number, i.
 *  If it is not already in the list, add it to the end of the list,
 *  increment list_size, and return its number i = list_size.
 *  The number in the list is counted from 1, so item = list[i-1].
 */
long utn_cs_list_add( char** list, long* list_size, const long max_size, const char* item )
{
 long item_no;

 item_no = utn_ar_match_c( item, list, *list_size );
 if ( item_no == 0 ) {
  if ( *list_size < max_size ) {
   (*list_size)++;
   item_no = *list_size;
   utn_cs_copy( item, list[item_no-1] );
  }
 }
 return( item_no );
}



