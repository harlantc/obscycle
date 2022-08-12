/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "nhlib.h"
#include <math.h>

/* where does ll->nh get evaluated? */

double sph_nbr( double x1, double y1, double x2, double y2, integer* direct )
{
 double dx,dy;
 double dist;
 dx = x2-x1;
 dy = y2-y1;
 if ( dx == 0.0 && dy == 0.0 )
 {
  dist = 0.0;
  *direct = DIRECT_OO;
 } else {
  dist = utn_spharc_d( x1, y1, x2, y2 );
  if ( dx >= 0.0 && dy < 180.0 )
  {
   *direct = ( dy >= 0.0 ) ? DIRECT_UR: DIRECT_LR;
  } else {
   *direct = ( dy >= 0.0 ) ? DIRECT_UL: DIRECT_LL;
  }
 }
 return dist;
}


logical sph_close( double x1, double y1, double x2, double y2, double xtol, double ytol )
{
 logical near;

 near = ( fabs( x1 - x2 ) < xtol ) && ( fabs( y1-y2 ) < ytol );
 return near;
}



double nhbase_xinterp( double ra, double dec, double lra, double ldec,
 double rra, double rdec, double* decp )
{
 double wt;
 double wt1;
 double idec;
 double rdist, rdist1;
 if ( lra > ra ) lra -= 360.0;
 if ( rra < ra ) rra += 360.0;
 wt1 = fabs( ra - lra ) / fabs ( rra - lra );
 idec = ldec * ( 1 - wt1 ) + rdec * wt1;
 rdist = utn_spharc_d( ra, idec, lra, ldec );
 rdist1 =utn_spharc_d( rra, rdec, lra, ldec );
 wt = rdist / rdist1;
#if 0
printf( "LINT %f %f %f %f\n", ra, rra, lra, wt1 );
printf( "XINT %f %f %f %f\n", rdist, rdist1, wt1, idec );
#endif
 *decp = idec;
 return wt;
}

double nhbase_interp( ColdenRecord* grid, double tra, double tdec, integer* codep )
{
 double upperdec,lowerdec;
 ColdenRecord* record;
 double rdist1;
 double ulwt, urwt, llwt, lrwt;
 double wt;
 integer code = 0;
 double rdist;
 double big_dist= 500.0;
 double nhval = 0;
 integer direct;
 integer minpt = 0;
 double lwt, uwt;
 logical fix = UT_FALSE;
 ColdenRecord* ul = &grid[DIRECT_UL];
 ColdenRecord* ur = &grid[DIRECT_UR];
 ColdenRecord* ll = &grid[DIRECT_LL];
 ColdenRecord* lr = &grid[DIRECT_LR];

 fix = ( ur->seq == 0 ) || ( ul->seq == 0 ) ||
       ( ll->seq == 0 ) || ( lr->seq == 0 );
 if( fix )
 {
  code = NH_CODE_NEAR;
  rdist = big_dist;
  for ( direct = 1; direct <= 4; direct++ )
  {
   record = &grid[direct];
   if( record->dist < rdist && record->seq > 0 )
   {
    minpt = direct;
    rdist = record->dist;
   }
  }
  if ( minpt > 0 )
  {
   record = &grid[minpt];
   nhval = record->nh;
  }
 } else {
/* Interpolate in RA above and below */
  uwt = nhbase_xinterp( tra, tdec, ul->ra, ul->dec, ur->ra, ur->dec, &upperdec );
  lwt = nhbase_xinterp( tra, tdec, ll->ra, ll->dec, lr->ra, lr->dec, &lowerdec );
  rdist = utn_spharc_d( tra, lowerdec, tra, tdec );
  rdist1= utn_spharc_d( tra, upperdec, tra, lowerdec ); 
  wt = rdist / rdist1;
  llwt = ( 1.0 - wt ) * ( 1.0 - lwt );
  ulwt =  wt  * ( 1.0 - uwt );
  lrwt = ( 1.0 - wt ) * lwt;
  urwt = wt  * uwt;
  nhval = llwt * ll->nh + lrwt * lr->nh + ulwt * ul->nh + urwt * ur->nh;
if ( utn_dbg_level(1))
{
 TextBuf ebuf;
 sprintf( ebuf, "Target Position: (%12.6f %12.6f)  Low, upper dist: %12.6f %12.6f Decs: %12.6f %12.6f", tra, tdec, rdist, rdist1, lowerdec, upperdec );
 utn_fio_tmsg( ebuf );
 sprintf( ebuf, "Positions: (%12.6f %12.6f) (%12.6f %12.6f) (%12.6f %12.6f) (%12.6f %12.6f)",
            ll->ra, ll->dec, lr->ra, lr->dec, ul->ra, ul->dec, ur->ra, ur->dec );
 utn_fio_tmsg( ebuf );
 sprintf( ebuf, "Main weights:   %6.3f %6.3f %6.3f", wt, lwt, uwt );
 utn_fio_tmsg( ebuf );
 sprintf( ebuf, "Weight coeffs:  %6.3f %6.3f %6.3f %6.3f", llwt, lrwt, ulwt, urwt );
 utn_fio_tmsg( ebuf );
 sprintf( ebuf, "NH Values:      %6.3f %6.3f %6.3f %6.3f", ll->nh, lr->nh, ul->nh, ur->nh );
 utn_fio_tmsg( ebuf );
 sprintf( ebuf, "Weighted result: %6.3f", nhval );
 utn_fio_tmsg( ebuf );
}
/* Weighted average of the four points */
 }
 *codep = code;
 return nhval;
}

