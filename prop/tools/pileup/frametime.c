/*_C_INSERT_SAO_COPYRIGHT_HERE_(2000,2007)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/* FILE NAME:	frametime.c
 *
 * DEVELOPMENT:	OC Group
 *
 * DESCRIPTION: Determine the best choice for ACIS frame time depending
 *	upon the input number of CCDs, ACIS instrument, and ACIS subarray
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>
#include <errno.h>

#ifndef TRUE
#define	TRUE	(1)
#endif
#ifndef FALSE
#define FALSE	(0)
#endif

typedef int BOOL;

double
frametime( int num_ccds, int instrument, double subarray )

{
  /* Function called by prop_frametime main() */

  /* input:	num_ccds is the number of CCDs (1-6) */
  /* input:	instrument is the choice of ACIS-I or ACIS-S (1 or 5) */
  /* input:	subarray is the selection of subarray (1.0, 0.5, 0.25, 0.125)*/
  /*               1.0 means no subarray  */

  /* return:	acis frame time */

  int           full_array=1024;             /* size of full array */
  int           n_num_rows_subarray;         /* number of rows in subarray */
  int           q_num_rows_separation;       /* number of rows separation */

  double        acis_frametime;              /* acis frametime (sec) */

  /* Calculate number of rows in the subarray (n) */

  n_num_rows_subarray = subarray * full_array;

  /* Calculate separating number of rows (q) */

  /* ACIS-I */
  if (instrument == 1) {
    q_num_rows_separation = full_array - n_num_rows_subarray;
  } 

  /* ACIS-S */
  if (instrument == 5) {
    q_num_rows_separation = 0.5 * ( full_array - n_num_rows_subarray );
  } 

  /* Calculate acis frame time.  Formula from page 95 of                */
  /*    Chandra Proposer's Observatory Guide, Rev 15.0, December 2012   */

   acis_frametime  =  (41.12 * num_ccds +  
                  0.040 * (num_ccds * q_num_rows_separation) + 
                  2.85 * n_num_rows_subarray  - 32.99 ) / 1000;
 
  return acis_frametime;
}


main( int argc, char **argv )
{
  /* Determine ACIS frame time */

  int		clarg;		/* command line argument index */
  BOOL		clerr;		/* true if command line error */
  BOOL		quiet;		/* if true, print only the percent, no text */
  int		n_num_args_found;	/* number of numerical args found */

  int           num_ccds;       /* number of ccds 1-6 */
  int           instrument;     /* instrument ACIS-I or ACIS-S (1 or 5) */
  double        subarray;       /* subarray (1.0 for none, 0.5, 0.25, 0.125) */

  double	output_frametime;    /* computed frame time */

  /* process command line args */

  for( clerr = FALSE, quiet = FALSE, n_num_args_found = 0, 
       num_ccds = 6, instrument = 1, subarray = 1.0, 
       clarg = 1;  clarg < argc;  ++clarg )  {

    if( strcmp( argv[clarg], "-q" ) == 0 )
      quiet = TRUE;

    else  {

      switch( n_num_args_found++ ) {

      case 0:
	num_ccds = atof( argv[clarg] );
	if( ( errno != 0 )  ||  ( num_ccds < 1 ) || ( num_ccds > 6 ) )
	  clerr = TRUE;		/* it has to be 1-6 */
	break;

      case 1:
	instrument = atof( argv[clarg] );
	if( ( errno != 0 ) || !( instrument == 1 || instrument == 5 ) )
	  clerr = TRUE;        /* it has to be 1 or 5 */        
	break;

      case 2:
	subarray = atof( argv[clarg] );
	if( ( errno != 0 ) || !( subarray == 1.0 || subarray == 0.5 || subarray == 0.25 || subarray == 0.125 ) )
	  clerr = TRUE;        /* it has to be 1.0, 0.5, 0.25, or 0.125 */
	break;

      default:
	clerr = TRUE;
	break;
      }
    }
  }

  if( ( argc < 3 ) || ( argc > 5 ) || ( n_num_args_found < 1 ) || ( clerr ) )  {
    printf( "Usage:  prop_frametime [-q] num_ccds instrument subarray \n" );
    printf( "\n");
    printf( "          \"-q\" is an optional flag requesting \"quiet\" output\n" );
    printf( "              (less text explaining the numbers)\n" );
    printf( "\n");
    printf( "          num_ccds is Number of CCDs. Options are 1-6 \n" );
    printf( "\n");
    printf( "          instrument is ACIS-I or ACIS-S. Options are 1 (for I) or 5 (for S) \n" );
    printf( "\n");
    printf( "          subarray is the Subarray. Options are 1.0 (full array; no subarray),\n" );
    printf( "            0.5 (for 1/2), 0.25 (for 1/4), 0.125 (for 1/8)\n" );
    exit(0);
  }

  /* calculate the frame time */

  output_frametime =frametime(num_ccds, instrument, subarray);

  /* report the frame time */

  if( quiet ) {
    printf( "output_frametime = " );
  } else {
    printf( " The recommended ACIS frametime (sec) is " );
  }

  /* force the output frametime to round up the next tenth */
  if (output_frametime != floor(output_frametime)) {
    output_frametime += .05;
  }
  printf( "%.1f", output_frametime );
  printf( "\n" );

}




