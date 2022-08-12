/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

 #include "utlib.h"

/*
 *  Allocate an array of names and alias lists
 */
AliasList utn_ar_alias_alloc( const long n )
{
 AliasList array;

 array = (AliasList)malloc( sizeof( struct AliasList_s ) );
 if ( !array ) return NULL;
 array->nmax = n;
 array->n =    0;
 array->name = (char**)calloc( n, UT_SZ_P );
 array->dim = (long*)calloc( n,  UT_SZ_I );
 array->cpts = (char***)calloc( n, UT_SZ_P );
 if ( !array->name || !array->dim || 
      !array->cpts ) return NULL;

 return array;

}

long utn_ar_alias_get_dim( AliasList array, long offset )
{
 if ( offset >= 0 && offset < array->n )
  return array->dim[offset];
 else
  return 0;
}

void utn_ar_alias_get_name( AliasList array, long offset, char* name, long maxlen )
{
 if ( offset >= 0 && offset < array->n )
  utn_cs_copy_siz( array->name[offset], name, maxlen );
}

char** utn_ar_alias_get_cpt_list( AliasList array, long offset )
{
 if ( offset >= 0 && offset < array->n )
  return (array->cpts[offset]);
 else
  return NULL;
}

void utn_ar_alias_get_cpt( AliasList array, long offset, long cpt, char* name, long maxlen )
{
 long n;
 if ( offset >= 0 && offset < array->n )
 {
  n =array->dim[offset];
  if ( cpt >= 0 && cpt < n )  
  {
   utn_cs_copy_siz( array->cpts[offset][cpt], name, maxlen );
  }
 }
}


long utn_ar_alias_set_name( AliasList array, char* name )
{
 long nmax;
 long entry;
 entry = ++array->n;
 if ( entry >= array->nmax )
 {
  nmax = 2 * array->nmax + 10;
  utn_ar_alias_extend( array, nmax );
 }
 array->name[entry-1] = utn_cs_dup( name );
 return entry;
}

void utn_ar_alias_set_cpts( AliasList array, long entry, char** cpts, long dim )
{
 array->cpts[entry-1] = cpts;

 array->dim[entry-1] = dim;
}



/*
 *  Extend an ASArray to at least size n
 */
int utn_ar_alias_extend( AliasList array, const long n )
{
 if ( !array )   
  return UT_FALSE;

 if ( array->nmax <= n )
  return UT_TRUE;

 array->nmax = n;
 array->cpts= (char***)realloc( array->cpts, n * UT_SZ_P );
 array->name  = (char**)realloc( array->name, n * UT_SZ_P );
 array->dim   = (long*)realloc( array->dim, n * UT_SZ_I );
 return UT_TRUE;
}

/*
 *  Free an ASArray
 */
void utn_ar_alias_free( AliasList array )
{
 long offset;

 if ( array ) 
 {
  for ( offset = 0; offset < array->n; offset++ )
  {

   utn_ar_free_cp( array->cpts[offset], array->dim[offset] );
   free( array->name[offset] );
  }
  free( array->dim );
  free( array->cpts );
  free( array->name );
  free( array );
 }
}

/*
 *  Find out which list, if any, the cptname is in.
 *  Return the offset in the array.
 */
long utn_ar_alias_search( const AliasList array, const char* cptname )
{
 long offset;
 long cptno;
 for ( offset = 0; offset < array->n; offset++ )
 {
  for ( cptno = 0; cptno < array->dim[offset]; cptno++ )
  {
   if ( utn_cs_uceq( cptname, array->cpts[offset][cptno] ) )
    return offset;
  }
 }
 return -1;
}


void utn_ar_alias_print( const AliasList array )
{
 long offset;
 long cptno;
 char c = ' ';

 printf( "\n" );
 for ( offset = 0; offset < array->n; offset++ )
 {
  printf( "%-20s = ", array->name[offset] );
  c = ' ';
  for ( cptno = 0; cptno < array->dim[offset]; cptno++ )
  {
   printf( "%c%s", c, array->cpts[offset][cptno] );
   c = ',';
  }
  printf( "\n" );
 }
 printf( "\n" );
}

