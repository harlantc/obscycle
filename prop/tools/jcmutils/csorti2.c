/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

static void utn_sort_equals( const long* x, const long n, long* jsort )
{
/* Perform offset sorting of equal values in presorted array */
 logical loop;
 long index;
 long previous_index;
 long sorted_index;
 long sorted_previous_index;


/*
 *
           x  0 0 0 0 0 0 0 20 10 20 30
           js 6 5 4 3 2 1 0  8  7  9 10
              5 6           
           
 index 1
 si =  5  x[si] = 0
 pi    0
  spi = 6  x[spi] = 0
  si < spi  YES
   js[1] = spi = 6; js[0] = si = 5
 *
 */
 index = 1;
 while ( index < n )
 {
  sorted_index = jsort[index];
  previous_index = index-1;
  loop = UT_TRUE;
  while( previous_index >= 0 && loop )
  {    
   sorted_previous_index = jsort[previous_index];
   loop = ( x[sorted_index] == x[sorted_previous_index] && sorted_index < sorted_previous_index );
   if ( loop ) {
    jsort[previous_index+1] = sorted_previous_index;  
    jsort[previous_index] = sorted_index;  
   }
   previous_index--;
  }
  index++;
 }
}

void utn_cheapsort_i( const long* x, const long n, ArrayIndex* jsort )
{
 long y;
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
  jsort[index] = index;

 index1 = n / 2;
 index2 = n-1;
 loop = UT_TRUE;
 while ( loop ) {
  if ( index1 > 0 ) {
   index1--;
   sorted_index = jsort[index1];
   y = x[sorted_index];
  } else {
   sorted_index = jsort[index2];
   y = x[sorted_index];
   jsort[index2]= jsort[0];
   index2--;
   if ( index2 == 0 ) {
    jsort[0] = sorted_index;
    loop = UT_FALSE;
   }
  }
  if ( loop ) {
   tindex1 = index1;
   tindex2 = 2* index1 +1;
   while ( tindex2 <= index2  ) {
    if ( tindex2  < index2 ) {
     sorted_tindex2 = jsort[tindex2];
     sorted_next    = jsort[tindex2+1];
     if ( x[sorted_tindex2] < x[sorted_next] ) tindex2++;
    }
    if ( y < x[jsort[tindex2]] ) {
     jsort[tindex1] = jsort[tindex2];
     tindex1 = tindex2;
     tindex2 = 2*tindex2 + 1;
    } else {
     tindex2 = index2 + 1;
    }
   }
   jsort[tindex1] = sorted_index;
  }
 } /* end while loop */
 utn_sort_equals( x, n, jsort );

}

void utn_cshellsort_i( const long* x, const long n, ArrayIndex * jsort )
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
 { jsort[i] = i; }

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
    sorted_index1 = jsort[index1];
    sorted_index2 = jsort[index2];
    if ( x[sorted_index2] < x[sorted_index1] ) {
     jsort[index1] = sorted_index2;
     jsort[index2] = sorted_index1;
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



void utn_ar_csort_i( const long* x, const long n, long* jsort )
{
 if ( n > UT_PREFER_HEAPSORT )
  utn_cheapsort_i( x, n, jsort );
 else if ( n <= 1 ) 
  jsort[0] = 0;
 else
  utn_cshellsort_i( x, n, jsort );
}




void utn_ar_creindex_i( const long* x, const long n, long* y )
{
 /* x is the output of jsort.    x[3]=8 says in the 4th position, use entry No. 8 */
 /* y is the inverse index.      y[7]=4 says put entry no. 8 in the 4th position */
 long i;
 for ( i = 0; i < n; i++ ) {
  y[x[i]] = i;
 }
}


