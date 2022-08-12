/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"

static void utn_sort_equals( const double* x, const long n, long* jsort )
{
/* Perform offset sorting of equal values in presorted array */
 logical loop;
 long index;
 long previous_index;
 long sorted_index;
 long sorted_previous_index;


 for ( index = 1; index <n; index++ )
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
 }
}

void utn_cheapsort_d( const double* x, const long n, ArrayIndex* jsort )
{
 double y;
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

void utn_cshellsort_d( const double* x, const long n, ArrayIndex * jsort )
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



void utn_ar_csort_d( const double* x, const long n, long* jsort )
{
 if ( n > UT_PREFER_HEAPSORT )
  utn_cheapsort_d( x, n, jsort );
 else if ( n <= 1 ) 
  jsort[0] = 0;
 else
  utn_cshellsort_d( x, n, jsort );
}


/* Cheapsort */
void utn_ar_csortp_d( double* x, const long n )
{
 double y;
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





