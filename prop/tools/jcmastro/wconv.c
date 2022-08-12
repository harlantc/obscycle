/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmastro.h"

double ast_kev_to_freq( double e )
{
 double u;
 if ( e > 0.0 )
  u = AST_LOG_KEV + log10( e );
 else
  u = utn_null_d();
 return u;
}

double ast_freq_to_kev( double u )
{
 double e;
 e = pow( 10.0, u - AST_LOG_KEV );
 return e;
}

double ast_freq_copy( double u )
{
 return u;
}

double ast_freq_to_hz( double u )
{
 return pow( 10.0, u );
}

double ast_hz_to_freq( double f )
{
 if ( f > 0.0 ) 
  return log10( f );
 else
  return utn_null_d();
}



double ast_freq_to_lam( double u )
{
 double mu;
 mu = pow( 10.0, AST_LOG_MU - u );
 return mu;
}

double ast_lam_to_freq( double mu )
{
 double u;
 if ( mu > 0.0 )
  u = AST_LOG_MU - log10( mu );
 else
  u = utn_null_d();
 return u;
}



double ast_freq_to_temp( double u )
{
 double T;
 T = pow( 10.0, u - AST_LOG_K );
 return T;
}

double ast_temp_to_freq( double T )
{
 double u;

 u = AST_LOG_K + log10( T );
 return u;
}

