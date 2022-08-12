/*_C_INSERT_SAO_COPYRIGHT_HERE_(1997,2007)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/* FILE NAME:	pileup.c
 *
 * DEVELOPMENT:	OC Group
 *
 * DESCRIPTION: Estimate and report the percentage of arriving photons that 
 *	will "pile up" (and therefore not be counted) in the ACIS detector, 
 *	given the photon arrival rate (computed by PIMMS, e.g.); also report 
 *	the resulting (reduced) count rate.
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


/* Approximate pileup test values (accurate to 10-20%):
 *
 *     input rate  ct_rate   detected rate    pileup fraction
 *       0.03        0.10      0.095            0.038  (3.8%)
 *       0.09        0.30      0.25             0.12   (12%)
 *       0.24        0.80      0.45             0.28   (28%)
 *       0.61        2.0       0.52             0.57   (57%)
 *
 *      Note:  model starts to break above 0.5 for an input rate.
 */


double
pileup( double cts_per_sec_given, double frame_time, 
	double *cts_per_frame_piled, double *cts_per_sec_piled, 
	BOOL *pileup_too_high )
{
  /* Computation function called by prop_pileup main() */

  /* input:	cts_per_sec_given is the given count rate (counts/sec) */
  /* input:	frame_time is the time to obtain one frame (sec) */
  /* output:	cts_per_frame_piled is the detected (piled) count rate 
			(counts/frame) */
  /* output:	cts_per_sec_piled is the detected (piled) count rate 
			(counts/sec) */
  /* output:	pileup_too_high is TRUE if pileup fraction > 
                        specifed fraction */
  /* return:	pileup fraction */

  double	area_i_to_o_ratio = 1.0 / 8.0;
					/* ratio of area of inner detect cell 
					   to area of outer detect cells */
  double	encircled_energy_frac_expected = 0.8860;
					/* expected encircled energy fraction 
					   in inner detect cell */
  double	outer_energy_weighting;	/* weighting factor for encircled 
					   energy in outer detect cells */
  double	cts_per_frame;		/* counts per frame */
  double	cts_per_frame_expected_inner;	/* expected count rate in 
						   inner detect cell */
  double	cts_per_frame_expected_outer;	/* expected count rate in 
						   outer detect cells */
  double	cts_per_frame_piled_inner;	/* single photon detection rate
						   in inner detect cell */
  double	cts_per_frame_piled_outer;	/* single photon detection rate
						   in outer detect cells */
  double	zero_rate_piled_inner;	/* zero photon detection rate in 
					   inner detect cell */
  double	zero_rate_piled_outer;	/* zero photon detection rate in 
					   outer detect cells */
  double	pileup_fraction_inner;	/* pileup fraction in inner cell */
  double	pileup_fraction_outer;	/* pileup fraction in outer cells */
  double	pileup_fraction;	/* computed pileup fraction (return) */

  /* Compute weighting factor for encircled energy in outer detect cell */

  outer_energy_weighting = ( 1.0 - encircled_energy_frac_expected ) * 
				area_i_to_o_ratio;

  /* Desired quantity is counts/frame.  Assume all photons land
   * in one pixel (this is the problem of pileup, after all).  Then
   * the input count rate must be multiplied by the frame time, so
   *
   *	counts/frame = counts/sec * sec/frame
   */
  cts_per_frame = cts_per_sec_given * frame_time;

  /* Calculate expected count rates in each detect cell. */
  cts_per_frame_expected_inner = cts_per_frame * encircled_energy_frac_expected;
  cts_per_frame_expected_outer = cts_per_frame * outer_energy_weighting;

  /* Calculate the single photon detection rate in each detect cell. */
  cts_per_frame_piled_inner = cts_per_frame_expected_inner * 
				exp( -cts_per_frame_expected_inner );
  cts_per_frame_piled_outer = cts_per_frame_expected_outer * 
				exp( -cts_per_frame_expected_outer );

  /* Calculate the detected (piled) count rate. */
  *cts_per_frame_piled = cts_per_frame_piled_inner + 
			cts_per_frame_piled_outer / area_i_to_o_ratio;
  *cts_per_sec_piled = *cts_per_frame_piled / frame_time;

  /* Calculate pileup fraction. */

  /* Calculate the zero photon detection rate in each detect cell. */
  zero_rate_piled_inner = exp( -cts_per_frame_expected_inner );
  zero_rate_piled_outer = exp( -cts_per_frame_expected_outer );

  /* Calculate pileup fraction in each detect cell.  Weight by the 
   * encircled energy fractions.
   */
  if (zero_rate_piled_inner == 1.0) {
    pileup_fraction_inner = 0.0;
  } else {
    pileup_fraction_inner = 
	( 1.0 - ( zero_rate_piled_inner + cts_per_frame_piled_inner ) ) / 
					( 1.0 - zero_rate_piled_inner );
  }
  pileup_fraction_inner = pileup_fraction_inner * 
				encircled_energy_frac_expected;
  if (zero_rate_piled_outer == 1.0) {
    pileup_fraction_outer = 0.0;
  } else {
    pileup_fraction_outer = 
	( 1.0 - ( zero_rate_piled_outer + cts_per_frame_piled_outer ) ) / 
					( 1.0 - zero_rate_piled_outer );
  }
  pileup_fraction_outer = pileup_fraction_outer * 
				( 1.0 - encircled_energy_frac_expected );

  pileup_fraction = pileup_fraction_inner + pileup_fraction_outer;

  /* Set flag if pileup_fraction is too high */
  *pileup_too_high = ( pileup_fraction > 0.75 );

  return pileup_fraction;
}


main( int argc, char **argv )
{
  /* Compute and report pileup percentage for given input count rate. */
  /* Also determine piled count rate. */

  int		clarg;		/* command line argument index */
  BOOL		clerr;		/* true if command line error */
  BOOL		quiet;		/* if true, print only the percent, no text */
  int		n_num_args_found;	/* number of numerical args found */
  double	cts_per_sec_given;	/* given input count rate (counts/sec) */
  double	frame_time;	/* Frame Time (sec) */
  BOOL		pileup_too_high;	/* TRUE if counts/frame is beyond 
					   model's valid range */
  double	cts_per_frame_piled;	/* piled count rate (counts/frame) */
  double	cts_per_sec_piled;	/* piled count rate (counts/sec) */
  double	pileup_percent;	/* computed pileup percent (printed out) */

  /* process command line args */

  for( clerr = FALSE, quiet = FALSE, n_num_args_found = 0, 
       cts_per_sec_given = 0.0, frame_time = 3.20, 
       clarg = 1;  clarg < argc;  ++clarg )  {

    if( strcmp( argv[clarg], "-q" ) == 0 )
      quiet = TRUE;

    else  {
      switch( n_num_args_found++ ) {

      case 0:
	cts_per_sec_given = atof( argv[clarg] );
	if( ( errno != 0 )  ||  ( cts_per_sec_given <= 0.0 ) )
	  clerr = TRUE;		/* it has to be a positive number */
	break;

      case 1:
	frame_time = atof( argv[clarg] );
	if( ( errno != 0 ) || ( frame_time < 0.2 ) || ( frame_time > 10.0 ) )
	  clerr = TRUE;
	break;

      default:
	clerr = TRUE;
	break;
      }
    }
  }
  if( ( argc < 2 ) || ( argc > 4 ) || ( n_num_args_found < 1 ) || ( clerr ) )  {
    printf( "Usage:  prop_pileup [-q] cts_per_sec_given [frame_time]\n" );
    printf( "          cts_per_sec_given is a count rate (from PIMMS),\n" );
    printf( "          frame_time is the frame time in seconds (default = 3.2), and\n" );
    printf( "          \"-q\" is an optional flag requesting \"quiet\" output\n" );
    printf( "              (less text explaining the numbers)\n" );
    exit(0);
  }


  /* the computation */
  pileup_percent = pileup( cts_per_sec_given, frame_time, 
			   &cts_per_frame_piled, &cts_per_sec_piled, 
			   &pileup_too_high ) * 100.0;

  /* the report */

  if( quiet ) {
    printf( "counts/frame=" );
  } else {
    printf( " The approximate count rate after pileup will be " );
  }
  printf( "%.4f", cts_per_frame_piled );
  if( quiet ) {
    printf( "\n" );
  } else {
    printf( " counts/frame, or\n" );
  }

  if( quiet ) {
    printf( "counts/sec=" );
  } else {
    printf( "                                                 " );
  }
  printf( "%.4f", cts_per_sec_piled );
  if( quiet ) {
    printf( "\n" );
  } else {
    printf( " counts/sec.\n" );
  }

  if( quiet ) {
    printf( "pileup_pct=" );
  } else {
    printf( " The ACIS spectrum will be piled up by " );
  }
  printf( "%d", (int) ( pileup_percent + 0.5 ) );
  if( quiet ) {
    printf( "\n" );
  } else {
    printf( "%%.\n" );
  }

  if( pileup_too_high ) {

    /* Note: The toolkit GUI is expecting only 1 warning, and for this single 
       warning to be at the end of the pileup output.  If this pileup routine
       is changed so that this is not the case, then the Toolkit GUI code will
       need to be changed as well. */

    printf( "Warning:  Above approximately 3 counts/frame or a pileup\n" );
    printf( "fraction greater than about 75%%, the pileup model begins to\n" );
    printf( "break down and becomes increasingly invalid as a predictor.\n" );
  }
}
