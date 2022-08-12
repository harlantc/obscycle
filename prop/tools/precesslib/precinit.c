/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gprecess.h"

void celestial_free( Celestial* state )
{
 if ( state->constell_data )
  ast_constell_free( state->constell_data );
 ast_free( state->astate );
 free( state );  
}
 
Celestial* celestial_init( void )
{ 
 AstState astate;
 Celestial* state = NULL;
 astate = ast_init();
 if ( utn_ver_init( "JCMPREC", "V1.0" )) {
  printf( "JCMPREC reinitialized\n" );
 }
 state = calloc( 1, sizeof( Celestial ));   
 state->sep = ' ';
 state->mode |= AST_PREC_OLD_B;  /* Default old matrices for now */
 state->astate = astate;
 return state;
}


