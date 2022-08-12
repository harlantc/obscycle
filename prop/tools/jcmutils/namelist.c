/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"
/*
 * Simple allocated name list
 * Simplified version of fixnamelist 
 */


NameList utn_cs_namelist_alloc( void )
{
 NameList list;
 list = calloc( 1, sizeof( struct NameList_s ));
 list->n = 0;
 list->nmax = 0; 
 list->data = NULL;
 return list;
}

void utn_cs_namelist_free( NameList list )
{
 integer i;
 if ( !list ) return;
 if ( list->data )
 {
  for ( i = 0; i < list->n; i++ )
   free( list->data[i] );
  free( list->data );
 }
 free( list );
}


void utn_cs_namelist_add( NameList list, const char* name )
{
 if (  list->n >= list->nmax )
 {
  list->nmax = 2 * list->nmax + 10;
  list->data = realloc( list->data, list->nmax * UT_SZ_P );
 }
 list->data[list->n] = utn_cs_dup( name );
 list->n++;
}

long utn_cs_namelist_size( NameList list )
{
 return list->n;
}

char* utn_cs_namelist_entry( NameList list, long i )
{
 char* ptr = NULL;
 if ( list && i > 0 && i <= list->n )
  ptr =  list->data[i-1];
 return ptr;
}

long utn_cs_namelist_match( NameList list, const char* name )
{
 return utn_ar_match_c( name, list->data, list->n );
}

/* Case insensitive match */
long utn_cs_namelist_search( NameList list, const char* name )
{
 return utn_ar_search_c( name, list->data, list->n );
}


/* V2.05 Added 2009 Sep 13 */
NameList utn_cs_namelist_assign( char** names, integer n )
{
 NameList list = utn_cs_namelist_alloc();
 list->data = names;
 list->n = n;
 list->nmax = n;
 return list;
}


logical utn_cs_namelist_set( NameList list, const long entry, const char* name )
{
 logical q = UT_FALSE;
 if ( !list )
  return q;
 if ( entry <= 0 || entry > list->n + 1 )
  return q;

 q = UT_TRUE; 
 if( entry > list->n )
 {
  utn_cs_namelist_add( list, name );
 } else {
  free( list->data[entry-1] );
  list->data[entry-1] = utn_cs_dup( name );
 }


 return q;

}



