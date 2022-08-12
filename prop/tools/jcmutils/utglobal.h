/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#ifndef UT_GLOBAL_H
#define UT_GLOBAL_H
/*
 *  We define global variables as extern except in the
 *  source file where they are instantiated.
 */
#ifdef UT_DEFINE_GLOBALS
#define UT_GLOBAL
#else
#define UT_GLOBAL extern
#endif
#define UT_TMP_MAX 2048
UT_GLOBAL character ut_tmp[UT_TMP_MAX];  /* global Scratch space */



UT_GLOBAL const UtilConstants utn_constants 
#ifdef UT_DEFINE_GLOBALS
= { 
  0,
                    {
                      UT_CIRCLE/4.0, 
                      2.0 * M_PI,
                      UT_CIRCLE, 
                      2 * M_PI / UT_CIRCLE, 
                     },
                     {
                      (float)UT_CIRCLE/4.0,
                      (float)(2.0 * M_PI),
                      (float)UT_CIRCLE,
                      (float)(2 * M_PI / UT_CIRCLE),
                     },
 }
#endif
;
#endif
