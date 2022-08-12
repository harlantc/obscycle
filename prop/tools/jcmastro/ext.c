/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/*
 *
 * Object model for astronomy;
 *
 */

typedef struct Universe_s {
 ExtinctionLaw extlaw;
} *Universe;

typedef struct extinction_law_s {
 double* x;
 double* y;
 long n;
} *ExtinctionLaw;


Universe universe_global( void )
{
 if ( !universe_state )
  universe_state = calloc( 1, sizeof( struct Universe_s ));
 return universe_state;
}

ExtinctionLaw universe_ism_extlaw( Universe universe )
{
 ExtinctionLaw extlaw;
 FioFile file;
 char* filename = "ext.dat";
 TextCard line;
 long nmax = 0;
 if ( !universe )
  universe = universe_global();
 if ( !universe->extlaw )
 {
  extlaw = calloc( 1, sizeof( struct extinction_law_s ));
  universe->extlaw = extlaw;
  file = fio_open_ar( filename );
  while( fio_read_line( file, line, CSIZE ))
  {
   if ( !cs_is_blank( line ))
   {
    ptr = line;
    if ( n >= nmax )
    {
     nmax = nmax * 2 + 20;
     x = realloc( x, nmax * UT_SZ_D );
     y = realloc( y, nmax * UT_SZ_D );
    }
    x[n] = cs_get_d( &ptr );
    y[n] = cs_get_d( &ptr );
    n++;   
   }
  }
  fio_file_close( file );  
  extlaw->x = x;
  extlaw->y = y;
  extlaw->n = n;

 }
 return universe->extlaw;
}


double ast_ism_extinct( Universe universe, double u )
{
 ExtinctionLaw extlaw;
 extlaw = universe_ism_extlaw( universe );
 e = ar_linterp_d( extlaw->x, extlaw->y, extlaw->n, u );
 if ( e < 0 ) e = 0.0;
 return e;
}
