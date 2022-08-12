/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"
#include <stdlib.h>

/* A list of void pointers with a slightly different interface than GenStack */

logical utn_vlist_check( void* listp )
{
 VList list = (VList)listp;
 return ( list->list_type == LIST_VOID );
}

void utn_vlist_status( VList list )
{
 printf( "List %s: Size %ld Max size %ld  Memory %ld\n", list->name, list->n, list->nmax, list->memsize );
}

long utn_vlist_n( VList list )
{
 if ( list )
  return list->n;
 return 0;
}

void utn_vlist_free( VList list )
{
 if ( list ) {
  free( list->data );
  free( list->name );
  free( list );
 }
}

VList utn_vlist_alloc( char* name, long nmax )
{
 VList list;
 integer size = UT_SZ_P;
 list = (VList)calloc( 1, sizeof( struct VList_s ) );
 if ( list ) {
  list->list_type = LIST_VOID;
  list->nmax = nmax;
  list->n = 0;
  list->name = utn_cs_dup( name );
  list->data= (void**)calloc( nmax, size );
  list->size = size;
  list->memsize = nmax * size + sizeof( struct VList_s );
 }
 return( list );
}

void* utn_vlist_entry( VList list, long entry )
{
 void* ptr;
 if ( entry >= 0 && entry < list->n )
 {
  ptr =  list->data[entry];
 } else {
  ptr = NULL;
 }
 return ptr;
}


void utn_vlist_realloc( VList list, long nmax )
{
 if ( list ) {
  list->nmax = nmax;
  list->data=(void**)realloc( list->data, (nmax+1) * list->size ) ;
  list->memsize = nmax * list->size + sizeof( struct VList_s );
 }
}

integer utn_vlist_add( VList list, void* ptr )
{ 
 logical reset; /* Discard */
 return utn_vlist_add_t( list, ptr, &reset );
}

/* Alert if reallocation has happened */
integer utn_vlist_add_t( VList list, void* ptr, logical* reset )
{

 integer newnmax; 
 *reset = UT_FALSE;
 if ( list->n >= list->nmax )
 {
  newnmax = 100 + list->nmax * 2;
  utn_vlist_realloc( list, newnmax );
  *reset = UT_TRUE;
 }

 if ( list->n < list->nmax ) {
  list->data[list->n] = ptr;
  list->n++;
 }
 return list->n; 
}


