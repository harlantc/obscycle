/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "gprecess.h"

void prec_ast_ec_mat( double equinox1, double equinox2, double* matrix );
void ast_sgal_matrix_j( double* matrix, char type );
void ast_sgal_matrix_b( double* matrix, char type );
void prec_ast_b1950_to_j2000( Celestial* state, double* cel_b, double* cel_j );
void prec_ast_j2000_to_b1950( Celestial* state, double* j, double* b );

void prec_ast_j2000_to_b1950( Celestial* state, double* cel_j, double* cel_b )
{

 double pm_b[2] = { 0.0, 0.0 };
 double parallax_b = 0.0;
 double vel_b = 0.0;
 double pm_j[2] = { 0.0, 0.0 };
 double parallax_j = 0.0;
 double vel_j = 0.0;
 logical old = ( state->mode & AST_PREC_OLD_B );  
 ast_j2000_to_b1950_full( cel_j, pm_j, parallax_j, vel_j, Y_B1950, AST_FK5_SIMPLE,
                          cel_b, pm_b, &parallax_b, &vel_b, old );
}


void prec_ast_b1950_to_j2000( Celestial* state, double* cel_b, double* cel_j )
{
 double pm_b[2] = { 0.0, 0.0 };
 double parallax_b = 0.0;
 double vel_b = 0.0;
 double pm_j[2] = { 0.0, 0.0 };
 double parallax_j = 0.0;
 double vel_j = 0.0;
 logical old = ( state->mode & AST_PREC_OLD_B );  
 ast_b1950_to_j2000_full( cel_b, pm_b, parallax_b, vel_b, Y_B1950, AST_FK5_SIMPLE,
                          cel_j, pm_j, &parallax_j, &vel_j,old );
}



void ast_sgal_matrix_j( double* matrix, char type )
{
 double smatrix[9];
 double rmatrix[9];

 if( type == 'S' )
 {
   ast_sgal_matrix( rmatrix );
   ast_gal_matrix_j( smatrix );
   utn_ar_lmat_mult_d( rmatrix, smatrix, matrix );
 } else {
   ast_gal_matrix_j( matrix );
 }
}

void ast_sgal_matrix_b( double* matrix, char type )
{
 double smatrix[9];
 double rmatrix[9];
 if( type == 'S' )
 {
   ast_sgal_matrix( rmatrix );
   ast_gal_matrix_b( smatrix );
   utn_ar_lmat_mult_d( rmatrix, smatrix, matrix );
 } else {
   ast_gal_matrix_b( matrix );
 }
}


/*
 *  Path from B1983 to EC1985:
 *   B1983-> B1950  (via fsys matrix )
 *   B1950-> J2000  (via fsys convert )
 *   J2000-> J1985  (via tsys matrix part 1)
 *   J1985-> EC1985  (via tsys matrix part 2)
 * We need to combine the two parts in tsys matrix to allow the B1950->J2000 part
 * So, even if we do J1983 to EC1985, we could in principle use
 *   J1983 ->        (no fsys matrix )
 *                   (no fsys convert)
 *   J1983 -> J1985  (tsys matrix part 1)
 *   J1985 -> EC1985 (tsys matrix part 2)
 * rather than separating the J1983-J1985 bit in fsys matrix, which we in fact do, for (?) clarity.
 */

void prec_ast_ec_mat( double equinox1, double equinox2, double* matrix )
{
  double t;
  double smatrix[9];
  double rmatrix[9];
  logical both;
  both = ast_prec_matrix_j( equinox1, equinox2, smatrix );
  t = cal_jepoch_to_jd( equinox2 );
  if ( both )
  {
   ast_ec_mat( t, rmatrix );
   utn_ar_lmat_mult_d( rmatrix, smatrix, matrix );
  } else {
   ast_ec_mat( t, matrix );
  }
}

/*
 *  Setup transform matrices.
 *  Go via B1950 or J2000 
 */
void coord_convert( Celestial* state, CelTransform tfm, double* fdata, double* tdata )
{

 double v0[3];
 double v1[3];
 double v2[3];
 double v3[3];
 double* p1;
 double* p2;
 double* p3;
 double pdata1[2];
 double pdata2[2];
 CelSystem* fsys;
 CelSystem* tsys;
 CelTransformElt* from = &tfm->from;
 CelTransformElt* to = &tfm->to;
/*  conversion table 


 TO        B     Bx     J    Jx    G    E   Ex

FROM   B   -     fmat  <t>   <t>tm tpost

       Bx fmat   fmat  fm<t> tmat

       J  <t>    tmat   -    fmat  tpost

       Jx fm<t>  tmat   fmat fmat

       G         fpre  fpre  tmat 

       E         tmat        tmat
 
       Ex        tmat        tmat



 */

 logical docvt;


 fsys = from->sys;
 tsys = to->sys;
 docvt = ( from->convert != NULL );

/* First perform any B-J conversions. These are done in spherical coords */

 if ( docvt )
 {
  if ( from->do_eterms  || to->do_eterms ) printf( "INTERNAL ERROR IN CONVERT: ETERMS\n" );

  if ( from->domat )
  {
   utn_ar_unitll_d( fdata[0], fdata[1], v0 );
   utn_ar_lmat_prex_d( from->matrix, v0, v1 );
   utn_ar_polarll_d( v1, &pdata1[0], &pdata1[1] );
   p1 = pdata1;
  } else {
   p1 = fdata;
  }
  if ( to->domat )
   p2 = pdata2;
  else
   p2 = tdata;

  from->convert( state, p1, p2 );

  if ( to->domat )
  {
   utn_ar_unitll_d( p2[0], p2[1], v0 );
   utn_ar_lmat_postx_d( to->matrix, v0, v1 );
   utn_ar_polarll_d( v1, &tdata[0], &tdata[1] );
  }

 } else {

/* No conversion, just matrices: work in cartesians */
  utn_ar_unitll_d( fdata[0], fdata[1], v0 );
  if ( from->domat )
  {
   utn_ar_lmat_prex_d( from->matrix, v0, v1 );
   p1 = v1;
  } else {
   p1 = v0;
  }

/* For G->B add eterms; B -> G subtract eterms */

  if ( from->do_eterms )
  {
   p2 = v2;
   utn_ar_vadd_d( p1, from->eterms, p2 );
  }
  else if ( to->do_eterms )
  {
   p2 = v2;
   ast_sub_eterms( p1, to->eterms, p2 );
  } else {
   p2 = p1;
  }

  if ( to->domat )
  {
   utn_ar_lmat_postx_d( to->matrix, p2, v3 );
   p3 = v3;
  } else {
   p3 = p2;
  }
  utn_ar_polarll_d( p3, &tdata[0], &tdata[1] );

 }
}

void coord_matrices( Celestial* state, CelTransform tfm )
{
 CelTransformElt* from = &tfm->from;
 CelTransformElt* to = &tfm->to;
 CelSystem* fsys;
 CelSystem* tsys;
 integer bprecmode;
 double bepoch = 1950.0;
 double jepoch = 2000.0;
 double target_epoch;
 double t;
 char ttype;


 fsys = from->sys;
 tsys = to->sys;
 ast_prec_unitmat( from->matrix );
 ast_prec_unitmat( to->matrix );
 from->domat = UT_FALSE;
 to->domat = UT_FALSE;
 from->do_eterms = UT_FALSE;
 to->do_eterms = UT_FALSE;
 from->convert = NULL;
 to->convert = NULL;
 ttype = tsys->type;

 bprecmode = ( state->mode & AST_PREC_OLD_B );

 if ( tsys->type == 'C' )
  ttype = 'B';

 if( fsys->type == 'B' )
 {

/* If Bx -> By, then do it in one matrix; otherwise do Bx -> B1950 for this half. */
   if ( ttype == 'B' )
     target_epoch = tsys->equinox;
   else
     target_epoch = bepoch;

   from->domat = ast_prec_matrix_b( target_epoch, fsys->equinox, from->matrix, bprecmode );
/* If Bx -> Jy, do Bx->B1950, B1950-J2000, J2000 -> Jy */
   if ( ttype == 'J' || ttype == 'E' )
   {
    from->convert = prec_ast_b1950_to_j2000;
    to->convert = prec_ast_j2000_to_b1950;
   } else if ( ttype == 'G' || ttype == 'S' ) {
    to->do_eterms = UT_TRUE;
    to->domat = UT_TRUE;
    t = cal_bepoch_to_jd( Y_B1950 );
    ast_earth_eterms( t, to->eterms );
    ast_sgal_matrix_b( to->matrix, ttype );
   }
   
  
 } else if ( ttype == 'B' ) {
/* If  ? -> By, set up B1950 -> By */
   to->domat = ast_prec_matrix_b( bepoch, tsys->equinox, to->matrix, bprecmode );

   if ( fsys->type == 'G' || fsys->type == 'S' ) 
   {
    from->do_eterms = UT_TRUE;
    from->domat = UT_TRUE;
    t = cal_bepoch_to_jd( Y_B1950 );
    ast_earth_eterms( t, from->eterms );
    ast_sgal_matrix_b( from->matrix, fsys->type );     
   }
 }  

 if ( fsys->type == 'J' ) {
   if ( ttype == 'J' || ttype == 'E' )
     target_epoch = tsys->equinox;
   else 
     target_epoch = jepoch;
   from->domat = ast_prec_matrix_j( target_epoch, fsys->equinox, from->matrix );

   if( ttype == 'B' )  /* We already set the matrix */
   {
    from->convert = prec_ast_j2000_to_b1950;
    to->convert = prec_ast_b1950_to_j2000;
   }

 } else if ( fsys->type == 'E' ) {
  from->domat = UT_TRUE;
  if ( ttype == 'B' )
   {
    from->convert = prec_ast_j2000_to_b1950;
    to->convert = prec_ast_b1950_to_j2000;
    target_epoch = jepoch;
   } else if ( ttype == 'J' ) {
    to->domat = ast_prec_matrix_j( fsys->equinox, tsys->equinox, to->matrix );  
    target_epoch = fsys->equinox;
   } else {   /* S or G or E */
    target_epoch = jepoch;
   }
   prec_ast_ec_mat( target_epoch, fsys->equinox, from->matrix );
 } else if ( ttype == 'J' ) {
  to->domat = ast_prec_matrix_j( jepoch, tsys->equinox, to->matrix );  
 }

 if ( (fsys->type == 'G' || fsys->type=='S') && !from->do_eterms )
 {
  if ( fsys->type == 'G'  && ttype == 'S' )
  {
   to->domat = UT_TRUE;
   ast_sgal_matrix( to->matrix );
  }
  else if ( fsys->type == 'S' && ttype == 'G' )
  {
   from->domat = UT_TRUE;
   ast_sgal_matrix( from->matrix );
  }
  else if ( fsys->type != ttype )    /*4/2011: for galactic sys*/
  {
   from->domat = UT_TRUE;
   ast_sgal_matrix_j( from->matrix, fsys->type );
  }
 } else if ( (ttype == 'G' || ttype =='S') && !to->do_eterms ) {
  to->domat = UT_TRUE;
  ast_sgal_matrix_j( to->matrix, ttype );
 }

 if ( ttype == 'E' ) {
  to->domat = UT_TRUE;
/* If from G or SG, go via J2000 */
  if ( fsys->type == 'J' )
   target_epoch = tsys->equinox;  /* We did the precession in fsys */
  else
   target_epoch = jepoch;
  prec_ast_ec_mat( target_epoch, tsys->equinox, to->matrix );
 } 

}


