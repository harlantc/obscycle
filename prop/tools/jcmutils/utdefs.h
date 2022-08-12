/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

 



typedef struct {
 long zero;        /* Dummy */
 struct ut_double_circ {
  double polar_lat;
  double twopi;
  double circle;
  double deg_rad;
 } d;
 struct ut_float_circ {
  float   polar_lat;
  float   twopi;
  float   circle;
  float   deg_rad;
 } r;
} UtilConstants;

/* UT_BIGEND is defined in ../../inc/config.h */
#if UT_BIGEND
#define BYTE_SWAPPED 0
#define UT_R8_WORD1 2
#define UT_R8_WORD2 1
#else
#define BYTE_SWAPPED 1
#define UT_R8_WORD1 1
#define UT_R8_WORD2 2
#endif


#define UT_VERBOSE       1
#define UT_VERBOSE_UNIT  5
#define UT_VERBOSE_FIO   10
#define UT_VERBOSE_PARSE 12

/* To convert from signed to unsigned */
#define UT_BIG_INT 65536
#define UT_BYTE_BITS 8

#define UT_NGREEK 24
#define UT_PARSE_NTYPES 28
#define UT_NCHARS 128


#define UT_APPROX_ARG_1  1.0E-6
#define UT_FUNC_TOL      1.0E-5
#define UT_MAX_EXP_ARG 700.0
#define UT_MIN_EXP_ARG 1.0E-10
#define UT_MIN_SINH_ARG 1.0E-10
#define UT_MAX_SINH_ARG 1.0E+40
#define UT_TINY_D 1.0E-100

#define UT_PREFER_HEAPSORT 1000

/*  An IDX is a C array index counting from 0 */
/*  An ID  is a Fortran style index from 1 */

typedef long ArrayOffset;
typedef long ArrayIndex;

/* The error codes are 1024 * class_no + class_entry */
#define UT_ERR_CLASS 1024
#define UT_ERR_CLASS_INIT_SIZE 20




