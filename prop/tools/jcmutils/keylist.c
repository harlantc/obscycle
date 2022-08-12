/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"
#include <stdlib.h>

/* Key List
 *
 * Array with  string keyname and  arb type value
 *
 * Several modes:
 *  - array of constant type
 *  - array of mixed basic types, all less space than UT_SZ_D
 *    The data is in the value object
 *  - array of structure type ptrs unallocated: DT_PTR
 *  - array of structure type ptrs allocated: DT_VOID
 *    The value is a pointer to the data
 *
 */


void* utn_keylist_copy( KeyList list )
{
 integer i;
 KeyList nlist;
 nlist = utn_keylist_alloc_n( list->name, list->nmax, list->case_sensitive );
 nlist->n = list->n;
 nlist->size = list->size;
 for ( i = 0; i < list->n; i++ )
 {
  if( nlist->size > 0 ) 
  {
   nlist->data[i] = malloc( nlist->size );
   memcpy( nlist->data[i], list->data[i], nlist->size );
  } else if ( list->types[i] == DT_CHAR )  {
   nlist->data[i] = utn_cs_dup( list->data[i] );
  } else {
   nlist->data[i] = list->data[i];
  }
  nlist->keys[i] = utn_cs_dup( list->keys[i] );
  nlist->types[i] = list->types[i];
 }
 nlist->current = 0;
 nlist->size = list->size;
 return nlist;
}

logical utn_keylist_check( void* listp )
{
 KeyList list = (KeyList)listp;
 return ( list->list_type == LIST_KEY );
}

void utn_keylist_status( KeyList list )
{
 printf( "List %s: Size %ld Max size %ld  Memory %ld\n", list->name, list->n, list->nmax, list->memsize );
}

long utn_keylist_n( KeyList list )
{
 if ( list )
  return list->n;
 return 0;
}

void utn_keylist_free( KeyList list )
{
 integer i;
 if ( list ) {
  for ( i = 0; i < list->n; i++ )
  {
   free( list->keys[i] );
   if ( list->size > 0 )
    free( list->data[i] );
  }
  free( list->keys );
  free( list->data );
  free( list->types );
  free( list->name );
  free( list );
 }
}

KeyList utn_keylist_alloc( void )
{
 return utn_keylist_alloc_n( NULL, 100, UT_FALSE );  /* Non case sensitive unnamed list */
}

KeyList utn_keylist_alloc_size( integer size )
{
 KeyList list;
 list = utn_keylist_alloc_n( NULL, 100, UT_FALSE );  /* Non case sensitive unnamed list */
 utn_keylist_setv( list, size, NULL );  /* use standard free */
 return list;
}

KeyList utn_keylist_alloc_n( char* name, long nmax, logical case_s )
{
 KeyList list;

 list = (KeyList)calloc( 1, sizeof( struct KeyList_s ) );
 if ( list ) {
  list->list_type = LIST_KEY;
  list->nmax = nmax;
  list->n = 0;
  list->name = utn_cs_dup( name );
  list->data= (void**)calloc( nmax, UT_SZ_MAX ) ;
  list->keys= (char**)calloc( nmax, UT_SZ_P ) ;
  list->types = (integer*)calloc( nmax, UT_SZ_I );
  list->memsize = nmax * ( UT_SZ_MAX + UT_SZ_P + UT_SZ_I ) + sizeof( struct KeyList_s );
  list->case_sensitive = case_s;
  list->current = 0;
 }
 return( list );
}



/* If all data items are same size */
void utn_keylist_setv( KeyList list, integer size, void (*kfree)(void*) )
{
 list->size = size;
 if ( kfree )
  list->free = kfree;
 if ( !list->free )
  list->free = free;
}

logical utn_keylist_realloc( KeyList list, long nmax )
{
 logical ok = UT_FALSE;
 if ( list ) {
  list->nmax = nmax;
  list->data=(void**)realloc( (char*)list->data, nmax * UT_SZ_MAX ) ;
  list->keys=(char**)realloc( (char*)list->keys, nmax * UT_SZ_P ) ;
  list->types=(integer*)realloc( (char*)list->types, nmax * UT_SZ_I );
  list->memsize = nmax * ( UT_SZ_MAX + UT_SZ_P + UT_SZ_I ) + sizeof( struct KeyList_s );
  if ( list->data && list->keys && list->types )
   ok = UT_TRUE;
 } 
 return ok;
}

logical utn_keylist_extend( KeyList list )
{
 integer newnmax;
 logical ok = UT_TRUE;
  if ( list->n >= list->nmax ) 
  {
   newnmax = 100 + list->nmax * 2;
   ok = utn_keylist_realloc( list, newnmax );
  }
 list->n++;
 return ok;
}

integer utn_keylist_search_next( KeyList list, char* key )
{
 integer keyno = utn_keylist_search( list, key );
 if ( keyno <= 0 )
 {
  if (!utn_keylist_extend( list ))
   keyno = 0;
  else
   keyno = list->n;
 }
 return keyno;
}

/* Add a void object to a sized void list */
logical utn_keylist_addv( KeyList list, char* key, void* value )
{
 return utn_keylist_add( list, key, value, DT_VOID );
}

/* Overwrites or appends to list */
logical utn_keylist_add( KeyList list, char* key, void* value, integer type )
{
 logical ok = UT_TRUE;
 integer keyno;
 integer offset;
 logical do_copy = UT_TRUE;  /* Each char data type has memory that needs freeing */
 keyno = utn_keylist_search_next( list, key );
 if ( keyno <= 0 ) return UT_FALSE;
/* For string pointers, value contains pointer to allocated copy.
 * For other data types. copy value.
 */
 offset = keyno - 1;
 if ( type == DT_VOID && list->size > 0 )
 {
  list->data[offset] = malloc( list->size );
  memcpy( list->data[offset], value, list->size );  
 } else {
  utn_ar_mix_copy_value( value, type, list->data[offset], do_copy, 0 );
 }
 list->keys[offset] = utn_cs_dup( key );  
 list->types[offset] = type;
 list->current = keyno;
 return ok;
}

void utn_keylist_rewind( KeyList list )
{
 if ( list )
  list->current = 0;
}

char* utn_keylist_get_keyptr( KeyList list, integer keyno )
{
 if ( keyno > 0 )
  return list->keys[keyno-1];
 else
  return NULL;
}


/* Used for DT_VOID and DT_PTR case */
void* utn_keylist_get_ptr( KeyList list, integer next )
{

 void* value = NULL;
 if ( next > 0 )
  value = list->data[next-1];
 return value;
}

integer utn_keylist_next( KeyList list )
{
 if ( !list ) return -1;

 list->current++;
 if ( list->current > list->n )
  list->current = 0;
 return list->current;
 
}

integer utn_keylist_search( KeyList list, char* key )
{
 integer keyno;
 if ( !list )
  return -1;

 if ( list->case_sensitive )
  keyno = utn_ar_match_c( key, list->keys, list->n );
 else
  keyno = utn_ar_search_c( key, list->keys, list->n );
 return keyno;
}

integer utn_keylist_search_value( KeyList list, char* key, void* value, integer* type )
{
 integer keyno;
 keyno = utn_keylist_search( list, key );
 utn_keylist_get_value( list, keyno, value, type );
 return keyno;
}


logical utn_keylist_get_value( KeyList list, integer keyno, void* value, integer* typep )
{
 logical do_copy = UT_TRUE;
 integer type;
 if ( keyno > 0 )
 {
  type = list->types[keyno-1];
  utn_ar_mix_copy_value( list->data[keyno-1], type, value, do_copy, 0 );
  if ( typep ) *typep = type;
  return UT_TRUE;
 } else {
  *(char**)value = (char*)NULL;
  if ( typep ) *typep = DT_UNK;
  return UT_FALSE;
 }

}



integer utn_keylist_find_value( KeyList list, void* value )
{
 if ( list || value ) { };
 return 0;  /* To be implemented */
}

integer utn_keylist_search_value_c( KeyList list, char* key, void* value, integer maxlen )
{
 integer keyno;
 keyno = utn_keylist_search( list, key );
 utn_keylist_get_value_c( list, keyno, value, maxlen );
 return keyno;
}

logical utn_keylist_get_value_c( KeyList list, integer keyno, char* value, integer maxlen )
{
 integer type;
 if ( keyno > 0 )
 {
  type = list->types[keyno-1];
  utn_ar_mix_print_generic( list->data[keyno-1], type, value, maxlen );
  return UT_TRUE;
 } else {
  utn_cs_copy( "", value );
  return UT_FALSE;
 }
}



logical utn_keylist_delete_key( KeyList list, integer keyno )
{
 integer i;
 if ( keyno <= 0 ) return UT_FALSE;

 free( list->keys[keyno-1] );
 for ( i = keyno; i <= list->n; i++ )
 {
  list->data[i-1] = list->data[i];
  list->keys[i-1] = list->keys[i];
  list->types[i-1] = list->types[i];
 }
 list->n--;
 return UT_TRUE;
}
