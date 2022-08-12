/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"
/*
 *
 * Case of nsize = 0;
 *  No alloc of list->data[0];
 */


FixNameList utn_cs_fnamelist_alloc( long n, long size )
{
 FixNameList list;
 list = calloc( 1, sizeof( struct FixNameList_s ));
 list->nmax = n; 
 if ( n > 0 )
  list->data = calloc( n, UT_SZ_P );
 list->n = 0;
 list->size = size;
 if ( size && n )
  list->data[0] = calloc( n * ( size + 1 ), UT_SZ_C );
 return list;
}

void utn_cs_fnamelist_free( FixNameList list )
{
 long i;
 long nfree = 1;
 if ( !list ) return;
 if ( !list->size )
  nfree = list->n;

 for ( i = 0; i < nfree; i++ )
   free( list->data[i] );

 free( list->data );
 free( list );
}


void utn_cs_fnamelist_add( FixNameList list, const char* name )
{
 char* ptr;
 long i;
 if (  list->n >= list->nmax )
 {
  list->nmax = 2 * list->nmax + 10;
  list->data = realloc( list->data, list->nmax * UT_SZ_P );
  if ( list->size ) 
  {
   list->data[0] = realloc( list->data[0], list->nmax * (list->size+1) * UT_SZ_C ); 
   for ( i = 1; i < list->n; i++ )
    list->data[i] = list->data[0] + (i-1) * (list->size+1);
  }
 }
 if ( list->size )
 {
  ptr = list->data[0] + list->n * (list->size+1);
  utn_cs_copy_siz( name, ptr, list->size );
  list->data[list->n] = ptr;
 } else {
  list->data[list->n] = utn_cs_dup( name );
 }
 list->n++;

}

long utn_cs_fnamelist_size( FixNameList list )
{
 return list->n;
}

long utn_cs_fnamelist_entry_size( FixNameList list )
{
 return list->size;
}


char* utn_cs_fnamelist_entry( FixNameList list, long i )
{
 char* ptr = NULL;
 if ( list && i > 0 && i <= list->n )
  ptr =  list->data[i-1];

 return ptr;
}

long utn_cs_fnamelist_match( FixNameList list, const char* name )
{
 return utn_ar_match_c( name, list->data, list->n );
}

/* Case insensitive match */
long utn_cs_fnamelist_search( FixNameList list, const char* name )
{
 return utn_ar_search_c( name, list->data, list->n );
}


/* V2.05 Added 2009 Sep 13 */
FixNameList utn_cs_fnamelist_assign( char** names, integer n )
{
 FixNameList list = utn_cs_fnamelist_alloc( 0, 0 );
 list->data = names;
 list->n = n;
 list->nmax = n;
 list->size = 0;
 return list;
}


logical utn_cs_fnamelist_set( FixNameList list, const long entry, const char* name )
{
 logical q = UT_FALSE;
 if ( !list )
  return q;
 if ( entry <= 0 || entry > list->n + 1 )
  return q;

 q = UT_TRUE; 
 if( entry > list->n )
 {
  utn_cs_fnamelist_add( list, name );
 } else if ( list->size ) {
  utn_cs_copy_siz( name, list->data[entry-1], list->size );
 } else {
  free( list->data[entry-1] );
  list->data[entry-1] = utn_cs_dup( name );
 }


 return q;

}



