/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/* Internal unit routines */
#ifndef UNIT_REG_H
#define UNIT_REG_H



typedef struct {
 long n;
 long nmax;
 char** base;
 double* exp;
 long* fac;
} UnitRegister;

struct  UnitDomain_s {
 char** unit;
 char** def;
 long n;
 logical texmode;
 UnitRegister* ureg1;
 UnitRegister* ureg2;
};

void utn_unit_free( UnitRegister* unit );
UnitRegister* utn_unit_alloc( long n );
void utn_unit_regmult( UnitRegister* reg1, UnitRegister* reg2, long power2 );
void utn_unit_regdedup( UnitRegister* reg, UnitRegister* temp );
void utn_unit_regcompose( UnitRegister* reg, logical texmode, char* unit, long siz );
void utn_unit_regresolve( UnitDomain u );
void utn_unit_ddisp( UnitRegister* reg );
void utn_unit_regexp( UnitRegister* reg, double power );
void utn_cs_unit_si_resolve( char** unit, long n, char** d_unit, long dn, long* fac );
void utn_cs_unit_regparse( UnitDomain u, const char* unit, UnitRegister* reg );
void utn_cs_unit_parse( const char* unit, UnitRegister* reg );
void utn_cs_unit_reg_enlarge( UnitRegister* reg, long n );
#endif
