/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"


/*----------------------------------------------------------------------*/
/*
 * sort_equals
 *
 * Perform sorting of equal values in presorted array 
 * Local routine
 *
 */

static void utn_sort_equals( char** x, const long n, long* jsort )
{
 logical loop;
 long index;
 long previous_index;
 long sorted_index;
 long sorted_previous_index;


 for ( index = 1; index <n; index++ )
 {
  sorted_index = jsort[index]-1;
  previous_index = index - 1;
  loop = UT_TRUE;
  while( previous_index >= 0 && loop )
  { 
   
   sorted_previous_index = jsort[previous_index]-1;
   loop = ( utn_cs_eq( x[sorted_index], x[sorted_previous_index] ) && sorted_index < sorted_previous_index );
   if ( loop ) {
    jsort[previous_index+1] = sorted_previous_index + 1;  
    jsort[previous_index] = sorted_index + 1;  
   }
   previous_index--;
  }
 }
}

/*----------------------------------------------------------------------*/
/*
 * heapsort_c
 *
 * Sort an array of char*s using the heap sort algorithm
 *
 */

void utn_heapsort_c( char** x, const long n, ArrayIndex* jsort )
{
 char* y;
 logical loop;
 long index;
 long sorted_index;
 long index1;
 long index2;
 long tindex1;
 long tindex2;
 long sorted_tindex2;
 long sorted_next;

 for ( index = 0; index < n; index++ )
  jsort[index] = index+1;

 index1 = n / 2;
 index2 = n-1;
 loop = UT_TRUE;
 while ( loop ) {
  if ( index1 > 0 ) {
   index1--;
   sorted_index = jsort[index1]-1;
   y = x[sorted_index];
  } else {
   sorted_index = jsort[index2] - 1;
   y=  x[sorted_index];
   jsort[index2]= jsort[0];
   index2--;
   if ( index2 == 0 ) {
    jsort[0] = sorted_index + 1;
    loop = UT_FALSE;
   }
  }
  if ( loop ) {
   tindex1 = index1;
   tindex2 = 2* index1 +1;
   while ( tindex2 <= index2  ) {
    if ( tindex2  < index2 ) {
     sorted_tindex2 = jsort[tindex2]-1;
     sorted_next    = jsort[tindex2+1] -1;
     if ( utn_cs_lt( x[sorted_tindex2], x[sorted_next] ) ) tindex2++;
    }
    if ( utn_cs_lt( y, x[jsort[tindex2]-1] )) {
     jsort[tindex1] = jsort[tindex2];
     tindex1 = tindex2;
     tindex2 = 2*tindex2 + 1;
    } else {
     tindex2 = index2 + 1;
    }
   }
  jsort[tindex1] = sorted_index + 1;
  }
 } /* end while loop */
 utn_sort_equals( x, n, jsort );

}

/*----------------------------------------------------------------------*/
/*
 * shellsort_c
 *
 * Sort an array of char*s using the shell sort algorithm
 *
 */

void utn_shellsort_c( char** x, const long n, ArrayIndex * jsort )
{
 long log2n;
 ArrayOffset i;
 logical loop;
 long index;

 long index1;
 long index2;
 long sorted_index1;
 long sorted_index2;
 long chunk;
 long segment;
 long segment_size;

 log2n = logbase_i( n, 2 );
 for ( i = 0; i < n; i++ )
 { jsort[i] = i+1; }

 segment_size = n ;
 
 for ( segment = 0; segment < log2n; segment++ )
 { /* outer loop */

  segment_size = segment_size / 2;
  chunk = n - segment_size;
  for ( index = 0; index < chunk; index++ )
  { /* second loop */
   index1 = index;
   loop = UT_TRUE;
   while( loop )
   {  /* inner loop */
    index2 = index1 + segment_size;
    sorted_index1 = jsort[index1]-1;
    sorted_index2 = jsort[index2]-1;
    if ( utn_cs_lt( x[sorted_index2], x[sorted_index1] ) ) {
     jsort[index1] = sorted_index2 + 1;
     jsort[index2] = sorted_index1 + 1;
     index1 = index1 - segment_size;
     loop = ( index1 >= 0 );
    } else {
     loop = UT_FALSE;
    }
   } /* end inner loop */
  } /* end second loop */
 } /* end outer loop */

/* Pass to check for equality */
 utn_sort_equals( x, n, jsort );

}


/*----------------------------------------------------------------------*/
/*
 * sort_c
 *
 * Sort an array of char*s using the shell sort algorithm or the heap sort algorithm
 *
 */

void utn_ar_sort_c( char** x, const long n, long* jsort )
{
 if ( n > UT_PREFER_HEAPSORT )
  utn_heapsort_c( x, n, jsort );
 else if ( n <= 1 ) 
  jsort[0] = 1;
 else
  utn_shellsort_c( x, n, jsort );
}






