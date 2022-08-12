/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "unitlib.h"
#include "unit.h"

/* The unit register routines, internal to the library */

UnitRegister* utn_unit_alloc( const long n )
{
 UnitRegister* unit;
 unit = (UnitRegister *)calloc( 1, sizeof( UnitRegister ) );
 unit->nmax = n;
 unit->n = 0;

 unit->base = utn_ar_alloc_c( WORD_SIZE, n );
 unit->exp  = (double *)calloc( n, UT_SZ_D );
 unit->fac  = (long *)calloc( n, UT_SZ_I );

 return( unit );
}

void utn_unit_free( UnitRegister* unit )
{
 free( unit->fac );
 free( unit->exp );
 utn_ar_free_c( unit->base );
 free( unit );
}


void utn_unit_regresolve( UnitDomain u )
{
 long i;
 long j;
 UnitRegister* reg1 = u->ureg1;
 UnitRegister* reg2 = u->ureg2;
 for ( i = 0; i < reg1->n; i++ ) {
  j = utn_ar_cmatch_c( reg1->base[i], u->unit, u->n );
  if ( j >= 0 ) {
   if ( !utn_cs_is_blank( u->def[j] ) ) {
    utn_cs_unit_parse( u->def[j], reg2 );
    utn_cs_unit_si_resolve( reg2->base, reg2->n, u->unit, u->n, reg2->fac );
    utn_unit_regexp( reg2, reg1->exp[i] );
    reg2->exp[UT_UNIT_MANT_POS] += reg1->fac[i] * reg1->exp[i];
    reg1->exp[i] = 0.0;
    utn_unit_ddisp( reg1 );
    utn_unit_ddisp( reg2 );
    utn_unit_regmult( reg1, reg2, +1 );
    utn_unit_ddisp( reg1 );
   } else {
/* Resolve SI unit */
    reg1->exp[UT_UNIT_MANT_POS] += reg1->fac[i] * reg1->exp[i];
    reg1->fac[i] = 0.0;
   }
  }
 }

}

void utn_unit_regdedup( UnitRegister* reg, UnitRegister* temp )
{
 long i;
 long j;
 long fac;
 double exponent;
 for ( i = 2; i < reg->n; i++ ) {
  j = utn_ar_cmatch_c( reg->base[i], reg->base, i-1 );
  if ( j >= 0 ) {
   utn_si_tmult( reg->fac[i], reg->exp[i], temp->fac[j], temp->exp[j], &fac, &exponent );
   reg->fac[j] = fac;
   reg->exp[UT_UNIT_MANT_POS] += exponent;
   reg->exp[j] += reg->exp[i];
   reg->exp[i] = 0.0;
  }
 }
 utn_unit_ddisp( reg );
}

void utn_unit_regcompose( UnitRegister* reg, logical texmode, char* unit, const long siz )
{
 utn_unit_compose( reg->base, reg->exp, reg->fac, reg->n, texmode, unit, siz );
}

void utn_unit_ddisp( UnitRegister* reg )
{
 long i;
 TextWord tmp;
 TextCard buf;
#if 0
 if ( ut_dbg_level( UT_VERBOSE_UNIT ) ) 
#endif
 if (0 ) 
 {
  utn_fio_dashline( 60 );
  for ( i = 0; i < reg->n; i++ ) {
   snprintf( buf, UT_CARD_SIZE, "%6ld %6ld %-10s %s", (long)i, (long)reg->fac[i], reg->base[i], 
                                   utn_cs_write_d( reg->exp[i], tmp, WORD_SIZE ));
   utn_fio_tmsg( buf );
  }
  utn_fio_dashline( 60 );
 }
}

void utn_cs_unit_regparse( UnitDomain u, const char* unit, UnitRegister* reg )
{
 utn_cs_unit_parse( unit, reg );
 utn_cs_unit_si_resolve( reg->base, reg->n, u->unit, u->n, reg->fac );
}

void utn_unit_regexp( UnitRegister* reg, const double power )
{
 long i;
 for ( i = 0; i < reg->n; i++ ) {
  reg->exp[i] *= power;
 }
}


void utn_unit_regmult( UnitRegister* reg1, UnitRegister* reg2, const long power2 )
{
 utn_unit_tmult( &reg1->n, reg1->base, reg1->exp, reg1->fac,
             &reg2->n, reg2->base, reg2->exp, reg2->fac, power2 );
}


void utn_cs_unit_texmode( UnitDomain u, const logical q )
{
 u->texmode = q;
}




