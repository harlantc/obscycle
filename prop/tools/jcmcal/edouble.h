/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/


#ifndef EDOUBLE_H
#define EDOUBLE_H

#include "jcmutils.h"
typedef struct {
 double t;
 double dt;
} edouble;
double cal_ed_minus( edouble e1, edouble e2 );
logical cal_ed_le( edouble e1, edouble e2 );
edouble cal_ed_add( edouble t1, double dt );
edouble cal_ed_compose( double t, double dt );
edouble cal_ed_make( double t, double dt );
edouble cal_ed_resolve( edouble e );
double cal_ed_double( edouble et );
logical cal_ed_eq( edouble e1, edouble e2 );
edouble cal_ed_norm( edouble e );
#endif

