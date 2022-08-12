/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include <stddef.h>
#include "utlib.h"


/*
 * New simple alloc routines for vectors
 */

/*
 *--------------------------------------------------------------------------------
 */
long** utn_ar_alloc_i( const long dim, const long n )
{
 long** p;
 long* array;
 long i;
 if ( n == 0 ) return NULL;

 array = (long *)malloc( n * dim * UT_SZ_I );
 p = (long** )malloc( n * UT_SZ_P );

 for ( i = 0; i < n; i++ ) {
  p[i] = array + dim * i;
 }
 return( p );
}

/*
 *--------------------------------------------------------------------------------
 */
void utn_ar_free_i( long** p )
{
 free( p[0] );
 free( p );
}

/*
 *--------------------------------------------------------------------------------
 */

short** utn_ar_alloc_s( const long dim, const long n )
{
 short** p;
 short* array;
 long i;

 if ( n == 0 ) return NULL;
 array = (short *)malloc( n * dim * UT_SZ_S );
 p = (short** )malloc( n * UT_SZ_P );
 for ( i = 0; i < n; i++ ) {
  p[i] = array + dim * i;
 }
 return( p );
}

/*
 *--------------------------------------------------------------------------------
 */
void utn_ar_free_s( short** p )
{
 free( p[0] );
 free( p );
}

/*
 *--------------------------------------------------------------------------------
 */

double** utn_ar_alloc_d( const long dim, const long n )
{
 double** p;
 double* array;
 long i;
 if ( n == 0 ) return NULL;
 array = (double *)malloc( n * dim * UT_SZ_D );
 p = (double** )malloc( n * UT_SZ_P );
 for ( i = 0; i < n; i++ ) {
  p[i] = array + dim * i;
 }
 return( p );
}
/*
 *--------------------------------------------------------------------------------
 */

void utn_ar_free_d( double** p )
{
 if ( p )
 {
  free( p[0] );
  free( p );
 }
}

/*
 *--------------------------------------------------------------------------------
 */

float** utn_ar_alloc_r( const long dim, const long n )
{
 float** p;
 float* array;
 long i;
 if ( n == 0 ) return NULL;
 array = (float *)malloc( n * dim * UT_SZ_R );
 p = (float** )malloc( n * UT_SZ_P );
 for ( i = 0; i < n; i++ ) {
  p[i] = array + dim * i;
 }
 return( p );
}
/*
 *--------------------------------------------------------------------------------
 */

void utn_ar_free_r( float** p )
{
 free( p[0] );
 free( p );
}

/*
 *--------------------------------------------------------------------------------
 */

char** utn_ar_alloc_c( const long size, const long n )
{
 char** word;
 char* buf;
 long i;

 /* Reserve n+1 bytes for each char* */
 if ( n == 0 ) return NULL;
 buf  = (char*)calloc(  n * (size + 1),  UT_SZ_C);
 word = (char**) malloc( (size_t) ( n * UT_SZ_P ) );
 word[0] = buf;
 /* Now assign the pointers to each char* */
 for ( i = 1; i < n; i++ ) {
  word[i] = word[i-1] + size + 1;
 }
 return( word );
}

/*
 *--------------------------------------------------------------------------------
 * Free Type FC array with fixed size char*s
 */
void utn_ar_free_c( char** word )
{
 free( word[0] );
 free( word    );
}

/*
 *  Free array of char* pointers.
 */
void utn_ar_free_cp( char** word, const long n )
{
 long i;
 if ( word )
 {
  for ( i = 0; i < n; i++ )
  {
   if ( word[i] ) free( word[i] );
  }
  free( word );
 }
}

/* 
 * Following Press, we use a pointer to an array of pointers to matrix rows. 
 */
/*
 *--------------------------------------------------------------------------------
 */
float** utn_ar_alloc_matrix_r( 
  const long xmin,
  const long xmax,
  const long ymin,
  const long ymax
)
{
 long nx = xmax - xmin + 1;
 long ny = ymax - ymin + 1;
 float*  ptr;
 float** row_ptr;
 long row;

 /* Allocate ny pointers to float */
 if ( nx <= 0 || ny <= 0 ) return NULL;

 row_ptr = (float **) malloc( (size_t)( nx * UT_SZ_P ) );
 row_ptr -= xmin;
 /* Allocate nx * ny floats */
 ptr = (float *) malloc( (size_t) ( ny * nx * UT_SZ_R ) );
 ptr -= ymin;

 row_ptr[xmin] = ptr;
 for ( row = xmin + 1; row <= xmax; row++ ) {
  row_ptr[row] = row_ptr[row-1] + ny;
 }
 return( row_ptr );
}

/*
 *--------------------------------------------------------------------------------
 */

double** utn_ar_alloc_matrix_d( 
  const long xmin,
  const long xmax,
  const long ymin,
  const long ymax
)
{
 long nx = xmax - xmin + 1;
 long ny = ymax - ymin + 1;
 double*  ptr;
 double** row_ptr;
 long row;

 if ( nx <= 0 || ny <= 0 ) return NULL;
 /* Allocate ny pointers to float */
 row_ptr = (double **) malloc( (size_t)( nx * UT_SZ_P ) );
 row_ptr -= xmin;
 /* Allocate nx * ny floats */
 ptr = (double *) malloc( (size_t) ( ny * nx * UT_SZ_D ) );
 ptr -= ymin;

 row_ptr[xmin] = ptr;
 for ( row = xmin + 1; row <= xmax; row++ ) {
  row_ptr[row] = row_ptr[row-1] + ny;
 }
 return( row_ptr );
}

/*
 *--------------------------------------------------------------------------------
 */
long** utn_ar_alloc_matrix_i( 
  const long xmin,
  const long xmax,
  const long ymin,
  const long ymax
)
{
 long nx = xmax - xmin + 1;
 long ny = ymax - ymin + 1;
 long*  ptr;
 long** row_ptr;
 long row;

 if ( nx <= 0 || ny <= 0 ) return NULL;
 /* Allocate ny pointers to float */
 row_ptr = (long**) malloc( (size_t)( nx * UT_SZ_P ) );
 row_ptr -= xmin;
 /* Allocate nx * ny floats */
 ptr = (long *) malloc( (size_t) ( ny * nx * UT_SZ_I ) );
 ptr -= ymin;

 row_ptr[xmin] = ptr;
 for ( row = xmin + 1; row <= xmax; row++ ) {
  row_ptr[row] = row_ptr[row-1] + ny;
 }
 return( row_ptr );
}

/*
 *--------------------------------------------------------------------------------
 */

short** utn_ar_alloc_matrix_s( 
  const long xmin,
  const long xmax,
  const long ymin,
  const long ymax
)
{
 long nx = xmax - xmin + 1;
 long ny = ymax - ymin + 1;
 short*  ptr;
 short** row_ptr;
 long row;

 if ( nx <= 0 || ny <= 0 ) return NULL;
 /* Allocate ny pointers to float */
 row_ptr = (short**) malloc( (size_t)( nx * UT_SZ_P ) );
 row_ptr -= xmin;
 /* Allocate nx * ny floats */
 ptr = (short *) malloc( (size_t) ( ny * nx * UT_SZ_S ) );
 ptr -= ymin;

 row_ptr[xmin] = ptr;
 for ( row = xmin + 1; row <= xmax; row++ ) {
  row_ptr[row] = row_ptr[row-1] + ny;
 }
 return( row_ptr );
}

/*
 *--------------------------------------------------------------------------------
 */
void utn_ar_free_matrix_s(
 short** matrix,
 const long xmin,
 const long ymin
)
{
 free( (char*) (matrix[xmin]+ymin) );  /* Free columns*/
 free( (char*) (matrix+xmin) );		/* Free rows*/
}
/*
 *--------------------------------------------------------------------------------
 */
void utn_ar_free_matrix_i(
 long** matrix,
 const long xmin,
 const long ymin
)
{
 free( (char*) (matrix[xmin]+ymin) );  /* Free columns*/
 free( (char*) (matrix+xmin) );		/* Free rows*/
}
/*
 *--------------------------------------------------------------------------------
 */
void utn_ar_free_matrix_r(
 float** matrix,
 const long xmin,
 const long ymin
)
{
 free( (char*) (matrix[xmin]+ymin) );  /* Free columns*/
 free( (char*) (matrix+xmin) );		/* Free rows*/
}
/*
 *--------------------------------------------------------------------------------
 */
void utn_ar_free_matrix_d(
 double** matrix,
 const long xmin,
 const long ymin
)
{
 free( (char*) (matrix[xmin]+ymin) );  /* Free columns*/
 free( (char*) (matrix+xmin) );		/* Free rows*/
}



char** utn_ar_realloc_c( char** word, const long size, const long n )
{
 char* buf = word[0];
 long i;
 buf = (char*)realloc( buf, n * (size+1) * UT_SZ_C );
 word = (char**)realloc( word, n * UT_SZ_P );
 word[0] = buf;
 for ( i = 1; i < n; i++ )
  word[i] = word[i-1] + size + 1;
 return word;
}

