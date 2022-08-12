/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

void geoc( double phi, double h, double* rp, double* zp );

void geoc( double phi, double h, double* rp, double* zp )
{
 
 double re = 6378.140
 double e = 0.00335281;
 double b;
 double cos_alpha;
 double sin_alpha;

 b = ( 1.0 - e ) * ( 1.0 - e );

 cos_phi = dcosd( phi );
 sin_phi = dsind( phi );

 norm = cos_phi * cos_phi + sin_phi * sin_phi;
 sin_alpha = b * cos_alpha;
 s2   = cos_alpha * cos_alpha + sin_alpha * sin_alpha;
 c2   = cos_alpha * cos_alpha - sin_alpha * sin_alpha;
 rho = re * sqrt( ( s2 + dcosd( 2 * phi ) * c2 ) / 2.0 ) );
 *rp = ( rho * cos_alpha + h ) * cos_phi;
 *zp = ( rho * sin_alpha + h ) * sin_phi;
}
