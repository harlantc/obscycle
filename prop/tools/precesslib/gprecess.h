/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#ifndef GPREC_H
#define GPREC_H

#include "jcmastro.h"

#include <stdio.h>

typedef struct Celestial_s Celestial;

extern integer gprec_verbose;

typedef struct CelSystem_s {
 char* prompt;
 char xprompt[40];
 char yprompt[40];
 logical deg;
 double equinox;
 char type;
} CelSystem;

typedef struct CelTransformElt_s {
 CelSystem* sys;
 void (*convert)(Celestial*,double*,double*); 
 logical domat;
 logical do_eterms;
 double matrix[9];
 double eterms[3]; 
} CelTransformElt;

typedef struct CelTransform_s {
 CelTransformElt from;
 CelTransformElt to;
} *CelTransform;

#if 0
 CelSystem* fsys;
 CelSystem* tsys;
 void (*invert)(Celestial*,double*,double*);
 logical fdomat;
 logical tdomat;
 double fmatrix[9];
 double tmatrix[9]; 
#endif


struct Celestial_s {
 integer print_mode;
 logical interact;
 integer verbose;
 ConstellationData constell_data;
 integer mode;
 char sep;
 AstState astate;  /* For thread safe */
};



void prec_list_sys( CelSystem* fsys );
void prec_make_prompts( CelSystem* fsys, CelSystem* tsys );
void prec_cel_set( Celestial* state, CelSystem* fsys, char* opt );
logical prec_convert( Celestial* state, CelSystem* fsys_in, CelSystem* tsys, char** pptr, logical repeat );
void coord_matrices( Celestial* state, CelTransform tfm );
Celestial* precess_init( char* args[], integer nargs );
void prec_help( void );
logical prec_interpret( Celestial* state, CelSystem* fsys, CelSystem* tsys, char* cmd );
void precess_interpreter( Celestial* state );
Celestial* precess_init( char* args[], integer nargs );

logical prec_read( CelSystem* fsys, char** pptr, double* pos );
void celestial_free( Celestial* state );
Celestial* celestial_init( void );
void prec_get_prompt( CelSystem* fsys, char* prompt );

void prec_header( CelSystem* fsys, CelSystem* tsys, integer mode );
void  prec_output( CelSystem* fsys, CelSystem* tsys, char* pos1, char* pos2, integer mode );

void coord_convert( Celestial* state, CelTransform tfm, double* fdata, double* tdata );

void csys_format( Celestial* state,CelSystem* sys, double x, double y, char* buf );
void cel_sys_free( CelSystem* sys );
CelSystem* cel_sys_alloc( void );

void cel_transform_free( CelTransform t );
CelTransform cel_transform_init( CelSystem* fsys, CelSystem* tsys );


#endif
