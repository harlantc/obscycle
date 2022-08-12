/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"
#include <stdlib.h>

logical utn_genlist_check( void* listp )
{
 GenList list = (GenList)listp;
 return ( list->list_type == LIST_GEN );
}

void utn_genlist_status( GenList list )
{
 printf( "List %s: Size %ld Max size %ld  Memory %ld\n", list->name, list->n, list->nmax, list->memsize );
}

long utn_genlist_n( GenList list )
{
 if ( list )
  return list->n;
 return 0;
}

void utn_genlist_free( GenList list )
{
 if ( list ) {
  free( list->data );
  free( list->name );
  free( list );
 }
}

GenList utn_genlist_alloc( char* name, long nmax, long size )
{
 GenList list;

 list = (GenList)calloc( 1, sizeof( struct GenList_s ) );
 if ( list ) {
  list->list_type = LIST_GEN;
  list->nmax = nmax;
  list->n = 0;
  list->name = utn_cs_dup( name );
  list->data= (void*)calloc( nmax, size ) ;
  list->size = size;
  list->memsize = nmax * size + sizeof( struct GenList_s );
 }
 return( list );
}

void* utn_genlist_entry( GenList list, long entry )
{
 char* ptr;
 ptr = ((char*)list->data) + entry * list->size;
 return (void*)ptr;
}


void utn_genlist_realloc( GenList list, long nmax )
{
 if ( list ) {
  list->nmax = nmax;
  list->data=(void*)realloc( (char*)list->data, nmax * list->size ) ;
  list->memsize = nmax * list->size + sizeof( struct GenList_s );

 }
}

void* utn_genlist_add( GenList list )
{
 void* object;
 integer newnmax; 
 if ( list->n >= list->nmax )
 {
  newnmax = 100 + list->nmax * 2;
  utn_genlist_realloc( list, newnmax );
 }

 if ( list->n < list->nmax ) {
  object = utn_genlist_entry( list, list->n );
  list->n++;
 } else {
  object = NULL;
 }
 return( object );
}

/* Alert if reallocation has happened */
void* utn_genlist_add_t( GenList list, logical* ok )
{
 void* object;
 integer newnmax; 
 *ok = UT_TRUE;
 if ( list->n >= list->nmax )
 {
  newnmax = 100 + list->nmax * 2;

  utn_genlist_realloc( list, newnmax );
  *ok = UT_FALSE;
 }

 if ( list->n < list->nmax ) {
  object = utn_genlist_entry( list, list->n );
  list->n++;
 } else {
  object = NULL;
 }
 return( object );
}


