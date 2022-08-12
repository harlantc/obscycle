/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "utlib.h"
#include <string.h>

#define REALLOC_GAIN 5
/* Local routines */
void utn_ar_intersect_s( short* mins1, short* maxes1, long nvals1, 
                     short* mins2, short* maxes2, long nvals2,
                     short** minsp, short** maxesp, long* np );

void utn_ar_intersect_d( double* mins1, double* maxes1, long nvals1, 
                     double* mins2, double* maxes2, long nvals2,
                     double** minsp, double** maxesp, long* np );

void utn_ar_intersect_c( char** mins1, char** maxes1, long nvals1, 
                     char** mins2, char** maxes2, long nvals2,
                     char*** minsp, char*** maxesp, long* np );
/*
 *  Intersect two lists of mins and maxes.
 *  Return 0 for missing list
 *        -1 for no intersection
 *         1 for valid intersection
 */
long utn_ar_mix_intersect( void* mins1, void* maxes1, long n1, void* mins2, void* maxes2, long n2,
                 DataType type, void** minsp, void** maxesp, long* np )
{
  long nmax;
  long status = UT_INTERSECT_OK;
  *np = 0;
  
  if(!mins1 || !maxes1 || !mins2 || !maxes2 || !minsp || !maxesp )
  {
    status = UT_INTERSECT_ERR;
    return status;
  }
 *minsp = NULL;
 *maxesp= NULL;
 nmax = n1 + n2;
 if ( nmax == 0 )
     return status;

 switch( type )
 {
  case DT_REAL8:
   utn_ar_intersect_d( mins1, maxes1, n1, mins2, maxes2, n2, (double**)minsp, (double**)maxesp, np );
   break;
  case DT_INT2:
   utn_ar_intersect_s( mins1, maxes1, n1, mins2, maxes2, n2, (short**)minsp, (short**)maxesp, np );
   break;
  case DT_CHAR:
   utn_ar_intersect_c( mins1, maxes1, n1, mins2, maxes2, n2, (char***)minsp, (char***)maxesp, np );
  default:
   status = 0; /* Error */
   break;
 }
 if ( *np == 0 )
 {
  free( *minsp );
  free( *maxesp );
  *minsp  = NULL;
  *maxesp = NULL;
  status = UT_INTERSECT_NULL;   
 }
 return status;
}


void utn_ar_intersect_s( short* mins1, short* maxes1, long nvals1, 
                     short* mins2, short* maxes2, long nvals2,
                     short** minsp, short** maxesp, long* np )             
{
 long i, j;
 long count = 0;
 short min1, max1;
 long k;

 long size = UT_SZ_S;
 short* retval1;
 short* retval2;
 long nmax = nvals1 + nvals2;
 retval1 = calloc( nmax, size );
 retval2 = calloc( nmax, size );

 for ( i = 0; i < nvals1; i++ )
  {
   min1 = mins1[i];
   max1 = maxes1[i];

/* Handle case of contiguous ranges */
      k = i + 1;
      while ( k < nvals1 && mins1[k] == max1 )
      {
       max1 = maxes1[k];
       k++;
       i++;
      }

   for ( j = 0; j < nvals2; j++ )
   {
       if(min1 <= mins2[j])
        {
              if(max1 >= mins2[j])
              {
                  if(max1 < maxes2[j])
                  {
                      retval1[count] = mins2[j];
                      retval2[count] = max1;
                      count++;
                   }
                  else /* maxes2[j] =< maxes1[j] */
                  {
                      retval1[count] = mins2[j];
                      retval2[count] = maxes2[j];
                      count++;
                  }
                  if ( count >= nmax )
                  {
                   nmax *= REALLOC_GAIN;
                   retval1 = (short*)realloc(retval1, nmax * size );
                   retval2 = (short*)realloc(retval2, nmax * size );
                  }
              }
          }
          else /* mins1[i] > mins2[j] */
          {
              if(min1 <= maxes2[j])
              {
                  if(max1 < maxes2[j])
                  {
                      retval1[count] = min1;
                      retval2[count] = max1;
                      count++;
                  }
                  else /* maxes1[i] >= maxes2[j] */
                  {
                      retval1[count] = min1;   
                      retval2[count] = maxes2[j];
                      count++;
                  }
                  if ( count >= nmax )
                  {
                   nmax *= REALLOC_GAIN;
                   retval1 = realloc(retval1, nmax * size );
                   retval2 = realloc(retval2, nmax * size );
                  }
              }
          }
      } /* End loop 1 */
   
   }  /* End loop 2 */
 
 *np = count;
 *minsp = retval1;
 *maxesp= retval2;
}

void utn_ar_intersect_d( double* mins1, double* maxes1, long nvals1, 
                     double* mins2, double* maxes2, long nvals2,
                     double** minsp, double** maxesp, long* np )
{


 long i, j;
 long count = 0;
 double min1, max1;
 long k;
 long size = UT_SZ_D;
 double* retval1;
 double* retval2;
 long nmax = nvals1 + nvals2;
 retval1 = calloc( nmax, size );
 retval2 = calloc( nmax, size );

 for ( i = 0; i < nvals1; i++ )
  {
   min1 = mins1[i];
   max1 = maxes1[i];

/* Handle case of contiguous ranges */
      k = i + 1;
      while ( k < nvals1 && mins1[k] == max1 )
      {
       max1 = maxes1[k];
       k++;
       i++;
      }

   for ( j = 0; j < nvals2; j++ )
   {
       if(min1 <= mins2[j])
        {
              if(max1 >= mins2[j])
              {
                  if(max1 < maxes2[j])
                  {
                      retval1[count] = mins2[j];
                      retval2[count] = max1;
                      count++;
                   }
                  else /* maxes2[j] =< maxes1[j] */
                  {
                      retval1[count] = mins2[j];
                      retval2[count] = maxes2[j];
                      count++;
                  }
                  if ( count >= nmax )
                  {
                   nmax *= REALLOC_GAIN;
                   retval1 = realloc(retval1, nmax * size );
                   retval2 = realloc(retval2, nmax * size );
                  }
              }
          }
          else /* mins1[i] > mins2[j] */
          {
              if(min1 <= maxes2[j])
              {
                  if(max1 < maxes2[j])
                  {
                      retval1[count] = min1;
                      retval2[count] = max1;
                      count++;
                  }
                  else /* maxes1[i] >= maxes2[j] */
                  {
                      retval1[count] = min1;   
                      retval2[count] = maxes2[j];
                      count++;
                  }
                  if ( count >= nmax )
                  {
                   nmax *= REALLOC_GAIN;
                   retval1 = realloc(retval1, nmax * size );
                   retval2 = realloc(retval2, nmax * size );
                  }
              }
          }
      } /* End loop 1 */
   
   }  /* End loop 2 */
 
 *np = count;
 *minsp = retval1;
 *maxesp= retval2;
}

 

void utn_ar_intersect_c( char** mins1, char** maxes1, long nvals1, 
                     char** mins2, char** maxes2, long nvals2,
                     char*** minsp, char*** maxesp, long* np )
{


 long i, j;
 long count = 0;
 char* min1;
 char* max1;
 long k;

 long size = UT_SZ_D;
 char** retval1;
 char** retval2;
 long nmax = nvals1 + nvals2;
 retval1 = calloc( nmax, size );
 retval2 = calloc( nmax, size );

 for ( i = 0; i < nvals1; i++ )
  {
   min1 = mins1[i];
   max1 = maxes1[i];

/* Handle case of contiguous ranges */
      k = i + 1;
      while ( k < nvals1 && utn_cs_eq( mins1[k], max1 ) )
      {
       max1 = maxes1[k];
       k++;
       i++;
      }


   for ( j = 0; j < nvals2; j++ )
   {
       if( utn_cs_strcmp( min1, mins2[j]) <= 0 )
        {
              if( utn_cs_strcmp( max1, mins2[j] )>= 0)
              {
                  if( utn_cs_strcmp( max1, maxes2[j] ) < 0 )
                  {
                      retval1[count] = utn_cs_dup( mins2[j] );
                      retval2[count] = utn_cs_dup( max1 );
                      count++;
                   }
                  else /* maxes2[j] =< maxes1[j] */
                  {
                      retval1[count] = utn_cs_dup( mins2[j] );
                      retval2[count] = utn_cs_dup( maxes2[j] );
                      count++;
                  }
                  if ( count >= nmax )
                  {
                   nmax *= REALLOC_GAIN;
                   retval1 = realloc(retval1, nmax * size );
                   retval2 = realloc(retval2, nmax * size );
                  }
              }
          }
          else /* mins1[i] > mins2[j] */
          {
              if( utn_cs_strcmp( min1, maxes2[j] ) <= 0 )
              {
                  if( utn_cs_strcmp( max1, maxes2[j]) <0 )
                  {
                      retval1[count] = utn_cs_dup( min1 );
                      retval2[count] = utn_cs_dup( max1 );
                      count++;
                  }
                  else /* maxes1[i] >= maxes2[j] */
                  {
                      retval1[count] = utn_cs_dup( min1 );   
                      retval2[count] = utn_cs_dup( maxes2[j] );
                      count++;
                  }
                  if ( count >= nmax )
                  {
                   nmax *= REALLOC_GAIN;
                   retval1 = realloc(retval1, nmax * size );
                   retval2 = realloc(retval2, nmax * size );
                  }
              }
          }
      } /* End loop 1 */
   
   }  /* End loop 2 */
 
 *np = count;
 *minsp = retval1;
 *maxesp= retval2;
}

