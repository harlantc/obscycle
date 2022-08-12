/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include <time.h>
#include "utlib.h"
#include <sys/types.h>
#include <unistd.h>

unsigned int utn_seed_static = 0;
unsigned int* utn_static_seed( void );

unsigned int* utn_static_seed( void )
{
 return &utn_seed_static;
}


/* Return rno dist uniform on [0,1] */
double utn_rand( void )
{
 unsigned int* seed = utn_static_seed(); 
 return utn_rand_eval( seed );
}

/* From stdlib.h */



double utn_rand_eval( unsigned int* seed )
{
 double r;
 if ( *seed == 0 ) *seed = time( NULL );
 r = (double)rand_r( seed ); 
 return r / RAND_MAX;
}

/* Use pid, as done by ImageMagick */
double utn_rand_pid( void )
{
 double r;
 unsigned int* seed = utn_static_seed(); 
 if ( *seed == 0 ) *seed = time( NULL ) + getpid();
 r = (double)rand_r( seed ); 
 return r / RAND_MAX;
}


