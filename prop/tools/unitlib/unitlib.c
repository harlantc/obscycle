/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "unitlib.h"
#include "unit.h"


void unit_init_domain( UnitDomain* unit_domain_p );

void utn_cs_unit_si_resolve( char** unit, long n, char** d_unit, long dn, long* fac )
{
 long i;
 long factor, pos;
 long j;
 TextWord temp;

 if ( n == 0 || dn == 0 ) return;
 for ( i = 0; i < n; i++ ) {
  utn_si_check( unit[i], &factor, &pos );
  if ( pos > 1 ) {
   utn_cs_copy( unit[i] + pos - 1, temp );
   j = utn_ar_cmatch_c( temp, d_unit, dn );
   if ( j >= 0 ) {
    utn_cs_copy( temp, unit[i] );
    fac[i] += factor;
   }
  }
 }

}

void utn_cs_unit_free( UnitDomain u )
{ 
 if ( !u ) return;
 utn_ar_free_c( u->unit );
 utn_ar_free_c( u->def );
 utn_unit_free( u->ureg1 );
 utn_unit_free( u->ureg2 );
 free( u );
}

UnitDomain utn_cs_unit_init( const char* filename )
{
 FioFile file;
 long n;
 TextBuf line;
 char* ptr;
 UnitDomain unit_domain;
 long nmax = UT_UDF_MAX;

 unit_domain = calloc( 1, sizeof(struct UnitDomain_s ));
 unit_domain->ureg1 = utn_unit_alloc( UT_UNIT_MAX );

 unit_domain->ureg2 = utn_unit_alloc( UT_UNIT_MAX ); 

 unit_domain->n = 0;
 if ( !filename || !(*filename) ) 
  return unit_domain;

 unit_domain->unit = utn_ar_alloc_c( WORD_SIZE, nmax );
 unit_domain->def  = utn_ar_alloc_c( WORD_SIZE, nmax );

 n = 0;
 file = utn_fio_open_ar( filename );
 if ( file ) {
/* Fixed bug 1997 Mar 12, fio read result is logical */
  while ( utn_fio_read_line( file, line, TEXT_SIZE )  ) {
   utn_cs_detab( line );
   if ( !utn_cs_is_blank( line ) ) {
    ptr = line;
    utn_cs_get_c( &ptr, unit_domain->unit[n], WORD_SIZE );
    if ( ptr != NULL ) {
     utn_cs_copy_siz( ptr, unit_domain->def[n], UT_WORD_SIZE );
    } else {
     utn_cs_copy( " ", unit_domain->def[n] );
    }
    n++;
    if ( n >= nmax )
    {
     nmax = 2 * n;
     unit_domain->unit = (char**)realloc( unit_domain->unit, nmax * UT_SZ_P );
     unit_domain->def  = (char**)realloc( unit_domain->def, nmax * UT_SZ_P );
    }
   }
  }
 }
 unit_domain->n = n;
 utn_fio_file_close( file );
 return unit_domain;
}




void utn_cs_unit_dparse( UnitDomain unit_domain, const char* u1, char* u2, const long siz )
{
 if ( !unit_domain ) return;
 utn_cs_unit_regparse( unit_domain, u1, unit_domain->ureg1 );
 utn_unit_ddisp( unit_domain->ureg1 );
 utn_unit_regcompose( unit_domain->ureg1, unit_domain->texmode, u2, siz );
}

void utn_cs_unit_resolve( UnitDomain unit_domain, const char* u1, char* u2, const long siz )
{
 if ( !unit_domain ) return;
 utn_cs_unit_regparse( unit_domain, u1, unit_domain->ureg1 );
 utn_unit_ddisp( unit_domain->ureg1 );
 utn_unit_regresolve( unit_domain );
 utn_unit_regdedup( unit_domain->ureg1, unit_domain->ureg2 );
 utn_unit_regcompose( unit_domain->ureg1, unit_domain->texmode, u2, siz );
}


void utn_cs_unit_mult( UnitDomain unit_domain, const char* u1, const char* u2, char* u, const long siz )
{
 if ( !unit_domain ) return;
 utn_cs_unit_regparse( unit_domain, u1, unit_domain->ureg1 );
 utn_cs_unit_regparse( unit_domain, u2, unit_domain->ureg2 );
 utn_unit_ddisp( unit_domain->ureg1 );
 utn_unit_ddisp( unit_domain->ureg2 );
 utn_unit_regmult( unit_domain->ureg1, unit_domain->ureg2, +1 );
 utn_unit_ddisp( unit_domain->ureg1 );
 utn_unit_regcompose( unit_domain->ureg1, unit_domain->texmode, u, siz );
}

void utn_cs_unit_div( UnitDomain u, const char* u1, const char* u2, char* udiv, const long siz )
{
 if ( !u ) return;
 utn_cs_unit_regparse( u, u1, u->ureg1 );
 utn_cs_unit_regparse( u, u2, u->ureg2 );
 utn_unit_ddisp( u->ureg1 );
 utn_unit_ddisp( u->ureg2 );
 utn_unit_regmult( u->ureg1, u->ureg2, -1 );
 utn_unit_ddisp( u->ureg1 );
 utn_unit_regcompose( u->ureg1, u->texmode, udiv, siz );
}



void utn_cs_unit_exp( UnitDomain u, const char* unit1, const double power, char* unit2, const long siz )
{
 if ( !u ) return;
 utn_cs_unit_regparse( u, unit1, u->ureg1 );
 utn_unit_regexp( u->ureg1, power );
 utn_unit_regcompose( u->ureg1, u->texmode, unit2, siz );

}


