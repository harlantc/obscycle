/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

void utn_csort_equals_i( const long* x, const long n, long* jsort )
{
/* Perform offset sorting of equal values in presorted array */
 integer left;
 integer m;
 integer b;
 integer sorted_m;
 integer sorted_last;
 logical loop;
 long index;

 long sorted_index;

 integer nn = 0;
 index = 0;
 while ( index < n-1 )
 {
  sorted_index = jsort[index];

  loop = UT_TRUE;
  left = n - index - 1;
  m = 1;
  b = 0;
/* Let's see how many equal values we have in a row */
  sorted_last = sorted_index;
  while ( m <= left && x[jsort[index+m]] == x[sorted_index] )
  {
   sorted_m = jsort[index+m];
   if ( sorted_m < sorted_last )
   {
    b++;
   }
   m++;   
  }
  if ( m > 1 && b > 0  )
  {
   nn += m;
   utn_cheapsortp_i( jsort+index, m );
   index += m;
  }
  index++;
 }

}

void utn_cheapsort_i( const long* x, const long n, ArrayIndex* jsort )
{
 long y;
 integer x2, xn;
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
    sorted_tindex2 = jsort[tindex2];
    if ( tindex2  < index2 ) {
     sorted_next    = jsort[tindex2+1];
     x2 = x[sorted_tindex2];
     xn = x[sorted_next];
     if ( x2 < xn || ( x2 == xn && sorted_tindex2 < sorted_next ) )
     {
      tindex2++;
      sorted_tindex2 = sorted_next;
     }
    }
    x2 = x[sorted_tindex2];
    if ( y < x2 || ( y == x2 && jsort[tindex1]  < sorted_tindex2 )) {
     jsort[tindex1] = sorted_tindex2;
     tindex1 = tindex2;
     tindex2 = 2*tindex2 + 1;
    } else {
     tindex2 = index2 + 1;
    }
   }
   jsort[tindex1] = sorted_index;

  }
 } /* end while loop */


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


}



void utn_ar_csort_i( const long* x, const long n, long* jsort )
{
 if ( n > UT_PREFER_HEAPSORT )
  utn_cheapsort_i( x, n, jsort );
 else if ( n <= 1 ) 
  jsort[0] = 0;
 else
  utn_cshellsort_i( x, n, jsort );
 utn_csort_equals_i( x, n, jsort );
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



void utn_cheapsortp_i( long* x, const long n )
{
 long y;
 logical loop;

 long index1;
 long index2;
 long tindex1;
 long tindex2;

 index1 = n / 2;
 index2 = n-1;
 loop = UT_TRUE;

 while ( loop ) {
  if ( index1 > 0 ) {
   index1--;
   y = x[index1];

  } else {
   y = x[index2];
   x[index2] = x[0];


   index2--;
   if ( index2 == 0 ) {

    x[0] = y;
    loop = UT_FALSE;
   }
  }
  if ( loop ) {
   tindex1 = index1;
   tindex2 = 2* index1 +1;
   while ( tindex2 <= index2  ) {
    if ( tindex2  < index2 ) {
     if ( x[tindex2] < x[tindex2+1] ) tindex2++;
    }
    if ( y < x[tindex2] ) {

     x[tindex1] = x[tindex2];
     tindex1 = tindex2;
     tindex2 = 2*tindex2 + 1;
    } else {
     tindex2 = index2 + 1;
    }
   }

   x[tindex1] = y;
  }
 } /* end while loop */


}




