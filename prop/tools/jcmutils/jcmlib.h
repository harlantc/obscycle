/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/


#ifndef DMNUTILS_H
#define DMNUTILS_H

#define JCMLIB "JCMLIB UTILS"
#define JCMLIBVER "CV3.0"

/* V0.1 1985 Nov    Initial version */
/* V1.1 1997 Feb 19 Made consistent use of long int */
/* V1.2 1997 Mar  9 Moved all prototypes into jcmproto.h */
/* V1.3 1997 Mar 10 Fixed SIO and TFR bugs, unitlib bug */
/* V1.4 1997 May  2 Added new allocate routines, vsub_d */
/* V1.5 1997 Nov 27 Cleaned up, added const declarations, moved stuff back to jcmlib.h */
/* V2.0 2001 Jun  2 Rewrite */
/* V2.1 2002 Jul  1 Revision for DM use */
/* V2.2 2005 Jul  9 Update */
/*  2008 Jun 27  Update */
/* V2.4 2009 Oct  3 Update, cleanup */
/* V3.0 2010 Apr 13 Split into jcmutils, jcmio, oerrlib, unitlib, and change IO to FioDriver model */

#define UT_ERR_START 1000
#include "utconfig.h"
#include <stdlib.h>
#include <sys/types.h>    /* mode_t required */
#include <stdio.h>
/* 
 * JCMLIB global symbols
 */
#ifndef M_PI
#define M_PI   3.1415926535897932384626433832795
#endif

#undef TRUE
#undef FALSE
#define TRUE  1
#define FALSE 0

#define UT_BITS_MAX 128
#define UT_UDF_MAX 100
#define UT_UNIT_MAX 20

#define UT_TRUE  1
#define UT_FALSE 0

#define UT_NULL_CHAR '\0'

#define CARD_SIZE 80
#define FN_SIZE  512
#define TEXT_SIZE 1024
#define WORD_SIZE   31


#define UT_CARD_SIZE 80
#define UT_FN_SIZE  512
#define UT_TEXT_SIZE 1024
#define UT_WORD_SIZE   31
#define UT_LIST_SIZE 4096

enum TMODE_OPTIONS { TMODE_WORD, TMODE_STRING, TMODE_LINE };
#define UT_INTERSECT_ERR 0
#define UT_INTERSECT_NULL -1
#define UT_INTERSECT_OK 1

/*
 *  File symbols
 */
#define FIO_TTY_IN  1
#define FIO_TTY_OUT 2
#define FIO_TTY_ERR 3


#define FIO_NOPERM   -1
#define FIO_NOFILE    0
#define FIO_READONLY  1
#define FIO_READWRITE 2

#define FIO_FILE_ERROR -1
#define FIO_FILE_STD  1
#define FIO_FILE_DIR  2
#define FIO_FILE_CDEV 3
#define FIO_FILE_BDEV 4
#define FIO_FILE_FIFO 5
#define FIO_FILE_SOCKET 6
#define FIO_FILE_EXE 7
#define FIO_FILE_UNKNOWN 8
#define FIO_FILE_NFT 8

#define UT_FILE_CLOSED 0
#define UT_FILE_OPEN   1
#define UT_FILE_EOF   -1
#define UT_FILE_ERR    2

#define FIO_CLEAR     0
#define FIO_CHECK     1
#define FIO_INPUT  0
#define FIO_OUTPUT 1
#define FIO_ERR    2

#define UT_CHAR_UMASK 255
#define UT_CHAR_MASK  127
#define UT_CHAR_ERR  -1
#define UT_CHAR_NULL  0
#define UT_CHAR_CTRLA 1
#define UT_CHAR_CTRLB 2
#define UT_CHAR_CTRLC 3
#define UT_CHAR_CTRLD 4
#define UT_CHAR_CTRLE 5
#define UT_CHAR_CTRLF 6
#define UT_CHAR_BEEP  7
#define UT_CHAR_BS    8
#define UT_CHAR_HT    9
#define UT_CHAR_LF   10
#define UT_CHAR_VT   11
#define UT_CHAR_FF   12
#define UT_CHAR_CR   13
#define UT_CHAR_SO   14
#define UT_CHAR_ESC  27
#define UT_CHAR_SPACE 32


#define UT_SZ_B 1
#define UT_SZ_R UT_SZ_FLOAT
#define UT_SZ_I UT_SZ_LONG
#define UT_SZ_C UT_SZ_CHAR
#define UT_SZ_S UT_SZ_SHORT
#define UT_SZ_D UT_SZ_DOUBLE
#define UT_SZ_Q UT_SZ_INT

/* Largest type */
#define UT_SZ_MAX UT_SZ_D

/* Fixed size (external) bytes */
#define DT_SZ_S 2
#define DT_SZ_I 4
#define DT_SZ_R 4
#define DT_SZ_D 8
#define DT_SZ_Q 4

/* Mins and maxes of the DM datatypes */
#define DT_INT2_MAX  32767
#define DT_INT2_MIN  -32768
#define DT_UINT2_MAX 65535
#define DT_UINT2_MIN 0
#define DT_INT4_MAX  2147483647L
#define DT_INT4_MIN  -2147483648L
#define DT_UINT4_MAX 4294967295UL
#define DT_UINT4_MIN 0
#define DT_REAL4_MAX 3.402823466E+38F
#define DT_REAL4_MIN -DT_REAL4_MAX
#define DT_REAL8_MAX 1.7976931348623157E+308
#define DT_REAL8_MIN -DT_REAL8_MAX
#define DT_UINT1_MAX 255
#define DT_UINT1_MIN 0


/* Max no of digits in an long in read_val_i */
#define UT_MAX_DIGITS 10
#define UT_SXG 60
#define UT_CIRCLE 360.0
#define UT_DEG_RAD  ( 2 * M_PI / UT_CIRCLE )

#define FIO_POP_CLOSE  1
#define FIO_POP_ONLY  -1

/* Global zero */
extern const double VZERO_D[3];

/* Special chars */




/* Special matrix macros */
#define utn_ar_lmat22_d(m,i,j)   (m[2*(i-1)+(j-1)])
#define utn_ar_lmat33_d(m,i,j)   (m[3*(i-1)+(j-1)])
#define utn_ar_lmatnn_d(m,n,i,j)   (m[n*(i-1)+(j-1)])

#define integer  long
#define real float

/*
 *   1,1  0
 *   1,2  1
 *   1,3  2
 *   2,1  3
 *   2,2  4
 *   2,3  5
 *   3,1  6
 *   3,2  7
 *   3,3  8
 */

/* JCM special types */

/*
 *
 *  JCMLIB types
 *  In general I have avoided the use of special types, which tend to
 *  make a general purpose library less reusable.
 *  For example, 3-vector arguments are just double* and you must read the
 *  documentation to know that you need a double[3].

 *  The 'logical' type is a typedef for int and is used for 
 *  boolean values.

 *
 *  There are also some special string types:TextWord for short strings,
 *  TextCard for 80 byte ASCII cards, TextBuf for long strings, and Filename
 *  for pathnames. These are never used in the external prototypes,
 *  but you may want to use FSIZE to check if your pathnames will exceed
 *  JCMLIB's capacity.
 *
 *  Note that all string tests in the library ignore trailing blanks;
 *  and a string of blanks is considered the same as a null string.
 *  This improves compatibility with Fortran programs.
 */



typedef int logical;    /* C standard specifies result of logical expr is int */

#define MAKE_CHARS_UNSIGNED 0
#if MAKE_CHARS_UNSIGNED == 1
#define character unsigned char
typedef unsigned char* string;
#else
#define character char
typedef char* string; 
#endif

/* These are always explicitly unsigned */
#define uchar unsigned char
typedef unsigned char* ustring;

/* New convention: types are capitalized, sizes are prefixed */
typedef char TextCard[CARD_SIZE+1];
typedef char Filename[FN_SIZE+1];
typedef char TextWord[WORD_SIZE+1];
typedef char TextBuf[TEXT_SIZE+1];
typedef struct UtilNull_s *UtilNulls;

typedef struct { float x;   float y; }   complex;
typedef struct { double x; double y; } dcomplex;

/* The UnitDomain object */
typedef struct UnitDomain_s     *UnitDomain;
/* The ErrorClasses object */
typedef struct ErrorClasses_s  *ErrorClasses;

typedef struct ErrorGlobal_s   *ErrorGlobal;
#if 0
typedef void* Tpio;  /* Dummy out TPIO system for this version */
#endif
/* Generic type support */
/* Would be nice if order was in casting precedence */
/* Suppress defs if given in DM already (but may conflict!) */
#ifndef DMDEF_H
#if OLD_DM
typedef enum { DT_VOID, DT_INT2, DT_PTR, DT_INT4, DT_REAL4, DT_REAL8, DT_CHAR, 
               DT_UINT1, 
               DT_LOGICAL, DT_UNK, DT_UINT2, DT_UINT4, DT_BIT } DataType;
#else
/* New precedence order; add support for uint8/int8 */
typedef enum { 
               DT_UNK=0, DT_LOGICAL, DT_CHAR, DT_UINT1, DT_UINT2, DT_INT2, DT_UINT4, DT_INT4, DT_UINT8, DT_INT8, DT_REAL4, DT_REAL8, 
               DT_BIT, DT_PTR, DT_VOID, DT_NTYPES
             } DataType;
#endif
#else
#define DataType dmDataType
#endif
typedef double GenericType;  /* GenericType can hold any scalar type or pointer */

/* Parser support */

typedef enum p_ctype { PTT_Null, PTT_Control, PTT_Special, PTT_Space, 
       PTT_LPar, PTT_RPar,
       PTT_Op, PTT_Sign, PTT_Comma, PTT_Point, PTT_Rel, PTT_SLPar, PTT_SRPar,
       PTT_Underbar, PTT_Digit, PTT_UC_Let, PTT_LC_Let,
       PTT_Letter, PTT_Letters, PTT_Alphanumeric, 
       PTT_Integer, PTT_Real, PTT_Real_E, PTT_Double, PTT_Logical,
       PTT_Word, PTT_Number, PTT_Symbol } ParseTokenType;

/* Dynamically expanding vector of names.
   The names array of pointers is reallocated in large chunks for efficiency */


typedef struct FixNameList_s {
 char** data;
 long n;
 long nmax;
 long size;
} *FixNameList;

typedef struct NameList_s {
 char** data;
 long n;
 long nmax;
} *NameList;

typedef struct PathList_s {
 NameList local;
 NameList global;
} *PathList;

typedef struct AliasList_s {
 char** name;
 char*** cpts;
 long* dim;
 long n;
 long nmax;
} *AliasList;


#define LIST_GEN 101
#define LIST_KEY 103
#define LIST_VOID 104

typedef struct GenList_s {
 long list_type;  /* This is a genlist */
 void* data;
 long n;
 long nmax;
 long size;
 long memsize;
 char* name;
} *GenList;

typedef struct VList_s {
 long list_type;  /* This is a void pointer list */
 void** data;
 long n;
 long nmax;
 long size;
 long memsize;
 char* name;
} *VList;

/* We manage the KeyList memory inefficiently (separate mallocs)
 * so that the returned pointers are persistent.
 * KeyList is not appropriate for high perfomance large arrays
 */

typedef struct KeyList_s {
 long list_type;  /* This is a keylist */
 void** data;
 char** keys;
 integer* types;
 integer* sizes;  /* Size of each allocated value, if relevant; array may be null */
 long n;
 long nmax;
 long memsize;
 char* name;
 logical case_sensitive;  /* Are names case sensitive? */
 integer current;
 integer type;  /* DT_UNK if not all types same */
 integer size;  /* If all types same, number of bytes: used for void types */
 void (*free)( void* );  /* Free memory */
} *KeyList;

typedef struct FileDirectory_s *FileDirectory;


typedef struct MixArray_s {
 void* data;
 DataType type;
 DataType* types;
 long size;
 long n;
 long nmax;
} *MixArray;



/*
 *   JCMLIB routine prototypes
 *  --  GENERAL: --
 *   1. Initialization
 *   2. System related routines
 *   3. Bit related functions
 *  --  STRINGS: --
 *   4. Converting between strings and numbers.
 *   5. Basic string manipulation
 *   6. Substrings
 *   7. String cleanup routines
 *   8. F90 style string functions
 *   9. Miscellaneous string functions
 *  10. Token parsing
 *  --  NUMBERS: --
 *  11. Sorting
 *  12. Arithmetic and Math
 *  13. Sexagesimal functions
 *  14. Special functions
 *  15. Complex number routines
 *  16. Extreme value handling
 *  17. Random numbers.
 *  18. Circular and trig functions
 *  19. 3-vector routines
 *  --   ARRAYS:  ---
 *  20. Array memory management
 *  21. Basic array operations
 *  22. F90 style array operations
 *  23. Logical arrays
 *  --   INPUT AND OUTPUT --
 *  24. General file I/O (FIO)
 *  25. Byte streams (SIO)
 *  26. Raw char streams (CIO)
 *  27. Text files (FIO)
 *  28. Message streams (IOFS)
 *  29. Command line interface (TPIO)
 *  30. Escape commands (ESC)
 *  31. Debug routines (DEBUG)
 *  32. Error handling (ERR)
 *  --   MISCELLANEOUS --
 *  33. Units
 *  34. Another string parser
 *  35. Fortran strings
 */

#define spharc_r utn_spharc_r
#define spharc_d utn_spharc_d
#define rcosd utn_rcosd
#define rsind utn_rsind
#define dcosd utn_dcosd
#define dsind utn_dsind
#define dacosd utn_dacosd
#define dasind utn_dasind
#define racosd utn_racosd
#define rasind utn_rasind
#define iplus utn_iplus
#define rotate2_d utn_rotate2_d
#define argd_d utn_argd_d
#define argd_r utn_argd_r
#define radin_d utn_radin_d
#define radin_r utn_radin_r
#define radout_d utn_radout_d
#define radout_r utn_radout_r
#define circ_d utn_circ_d
#define circ_r utn_circ_r
#define circ_ew_r utn_circ_ew_r
#define circ_ew_d utn_circ_ew_d
#define circ_rad_r utn_circ_rad_r
#define circ_rad_d utn_circ_rad_d
#define circ_ew_rad_r utn_circ_ew_rad_r
#define circ_ew_rad_d utn_circ_ew_rad_d
#define nint_d utn_nint_d
#define nint_r utn_nint_r
#define rpow utn_rpow
#define rlog10 utn_rlog10
#define rabs utn_rabs
#define rsqrt utn_rsqrt
#define lint_r utn_lint_r
#define lint_d utn_lint_d
#define order_r utn_order_r
#define order_d utn_order_d
#define round_r utn_round_r
#define round_d utn_round_d
#define frac_r utn_frac_r
#define frac_d utn_frac_d
#define logbase_i utn_logbase_i
#define logbase_r utn_logbase_r
#define logbase_d utn_logbase_d
#define rmod_i utn_rmod_i
#define rmod_s utn_rmod_s
#define rmod_d utn_rmod_d
#define rmod_r utn_rmod_r
#define divisible utn_divisible
#define min_i utn_min_i
#define max_i utn_max_i
#define modulo_i utn_modulo_i
#define modulo_s utn_modulo_s
#define modulo_r utn_modulo_r
#define modulo_d utn_modulo_d
#define floor_r utn_floor_r
#define floor_d utn_floor_d
#define ceiling_r utn_ceiling_r
#define ceiling_d utn_ceiling_d
#define min_d utn_min_d
#define max_d utn_max_d

#define hexagam utn_hexagam
#define acych utn_acych
#define acyc  utn_acyc
#define E1_func utn_E1_func
#define dplanckn utn_dplanckn
#define ipow_r utn_ipow_r
#define ipow_d utn_ipow_d
#define dgauss utn_dgauss
#define tpow_r utn_tpow_r
#define tpow_d utn_tpow_d
#define acosh utn_acosh
#define asinh utn_asinh
#define atanh utn_atanh
#define esinh utn_esinh
#define eexp utn_eexp

#define clip_d utn_clip_d
#define clip_r utn_clip_r
#define overlap_d utn_overlap_d
#define overlap_r utn_overlap_r
#define rlap_check_d utn_rlap_check_d
#define rlap_check_r utn_rlap_check_r
#define poly_d utn_poly_d
#define poly_r utn_poly_r
#define bose_einstein utn_bose_einstein
#define ecosh utn_ecosh
void utn_init_jcmlib( void ); 
void utn_free_jcmlib( void ); 
void utn_ver_jcmlib( char* ver, const long maxlen ); 
logical utn_ver_init( const char* prog, const char* ver ); 
void utn_id_jcmlib( char* label, const char* ver, integer maxlen ); 
void utn_sys_filepath( const char* dir, const char* name, const char* ext, char* buf, long siz ); 
void utn_sys_pathstrip( const char* path, char* dir, char* name, const long maxlen ); 
void utn_sys_os( char*  ); 
void utn_sys_getenv( const char* name, char* value ); 
void utn_sys_delete( const char* name ); 
void utn_sys( const char* cmd ); 
void utn_sys_type( const char* filename ); 
void utn_sys_date_and_time( char* date, char* ctime, char* zone ); 
void utn_sys_date( char* buf, const long size ); 
void utn_sys_time( char* buf, const long size ); 
void utn_sys_today( char* buf, const long size ); 
void utn_sys_wait( const long t ); 
double utn_sys_stamp( void ); 
void utn_bytes_split_i( const long value, long* bytes ); 
void utn_bytes_split_r( const float value, long* bytes ); 
void utn_bytes_split_d( const double value, long* bytes ); 
void utn_bytes_split_s( const short value, long* bytes ); 
void utn_bytes_split_c( const char* value, long* bytes, const long n_bytes ); 
long utn_bytes_combine_i( const long* bytes ); 
short utn_bytes_combine_s( const long* bytes ); 
float utn_bytes_combine_r( const long* bytes ); 
double utn_bytes_combine_d( const long* bytes ); 
void utn_bytes_combine_c( const long* bytes, char* value, const long n_bytes ); 
void utn_byte_set_s( short* number, const long value, const long byte ); 
void utn_byte_set_i( long* number, const long value, const long byte ); 
void utn_byte_set_r( float* number, const long value, const long byte ); 
void utn_byte_set_d( double* number, const long value, const long byte ); 
void utn_byte_set_c( char* data, const long value, const long byte ); 
long utn_byte_get_d( const double value, const long byte ); 
long utn_byte_get_s( const short value, const long byte ); 
long utn_byte_get_r( const float value, const long byte ); 
long utn_byte_get_i( const long value, const long byte ); 
long utn_byte_get_c( const char* value, long byte ); 
unsigned short utn_bit_single_s( const long utn_bit_no ); 
unsigned long utn_bit_single_i( const long utn_bit_no ); 
void utn_bit_set_i( long* value, const long utn_bit_no ); 
void utn_bit_set_s( short* value, const long utn_bit_no ); 
void utn_bit_clr_i(  long* value, const long utn_bit_no ); 
void utn_bit_clr_s(  short* value, const long utn_bit_no ); 
short utn_bit_shft_s( const short value, const long bits ); 
long utn_bit_shft_i( const long value, const long bits ); 
logical utn_bit_test_i( const long value, const long bit ); 
logical utn_bit_test_s( const short value, const long bit ); 
long utn_bit_unpack_i( const long value, const long start_bit, const long n_bits ); 
long utn_bit_unpack_s( const short value, const long start_bit, const long n_bits ); 
short utn_bit_ar_set_s( const long* bits ); 
long utn_bit_ar_set_i( const long* bits ); 
float utn_bit_ar_set_r( const long* bits ); 
double utn_bit_ar_set_d( const long* bits ); 
long utn_bit_ar_get_r( const float value, long* bits ); 
long utn_bit_ar_get_d( const double value, long* bits ); 
long utn_bit_ar_get_s( const short value, long* bits ); 
long utn_bit_ar_get_i( const long value, long* bits ); 
long utn_bits_unsigned( const short value ); 
long utn_bits_val( const long* bits, const long n ); 
void utn_bit_write( const long* bits, const long nbits, const char mode, char* buf ); 
double utn_cs_read_binary_d( const char* buf, const char mode ); 
float utn_cs_read_binary_r( const char* buf, const char mode ); 
long utn_cs_read_binary_i( const char* buf, const char mode ); 
short utn_cs_read_binary_s( const char* buf, const char mode ); 
void utn_cs_write_binary_d( const double value, char* buf, const char mode ); 
void utn_cs_write_binary_r( const float value, char* buf, const char mode ); 
void utn_cs_write_binary_s( const short value, char* buf, const char mode ); 
void utn_cs_write_binary_i( const long value, char* buf, const char mode ); 
void utn_ieee_get_i( const char* buf, long* data, const long n ); 
void utn_ieee_get_s( const char* buf, short* data, const long n ); 
void utn_ieee_get_d( const char* buf, double* data, const long n ); 
void utn_ieee_get_r( const char* buf, float* data, const long n ); 
void utn_ieee_put_r( const float* data, char* buf, const long n ); 
void utn_ieee_put_d( const double* data, char* buf, const long n ); 
void utn_ieee_put_s( const short* data, char* buf, const long n ); 
void utn_ieee_put_i( const long* data, char* buf, const long n ); 
void utn_dbg_set( const long level ); 
logical utn_dbg_level( const long i );	 
long utn_dbg_get( void ); 

void utn_dbg_msg_tag( const long level, char* buf ); 
void utn_error_init( char* name ); 

void utn_error_status(void); 
void utn_null_init( void ); 
float utn_nan_r( void );  
double utn_nan_d( void );  
float utn_null_r( void );  
double utn_null_d( void );  
short utn_null_s( void );  
long utn_null_i( void ); 
double utn_infinity_d( const long i );  
float utn_infinity_r( const long i );  
short utn_infinity_s( const long i );  
long utn_infinity_i( const long i ); 
void utn_null_set( char typecode, char* opt, char* pattern ); 
void utn_null_set_ieee( void );  
void utn_null_set_safe( void ); 
logical utn_qnull_i( const long i ); 
logical utn_qnull_s( const short i ); 
logical utn_qnan_r( const float x ); 
logical utn_qnan_d( const double x ); 
logical utn_qnull_d( const double x ); 
logical utn_qnull_r( const float x ); 
logical utn_qinf_i( const long i );  
logical utn_qinf_s( const short i );  
logical utn_qinf_r( const float x );  
logical utn_qinf_d( const double x ); 
double utn_rand( void ); 
double utn_rand_eval(unsigned int* seed ); 
logical utn_eflag_syntax( char* opt, char* param, char* value ); 
logical utn_eflag_parse( char** args, long nargs, logical local_flag_set(char*, char*, logical)); 
logical utn_eflag_set( char* param, char* value, char** paramlist,  char** valuelist, long nparams, logical* flags, logical eswitch );  
void utn_eflag_list( char** paramlist, logical* flags, char** valuelist, long nparams, char* cmt, long tab );  
char* utn_cs_dup( const char* buf ); 
char* utn_cs_dup_siz( const char* buf, const long siz ); 
void utn_cs_copy_siz( const char* source, char* dest, long siz );	 
void utn_cs_copy( const char* source, char* dest ); 
long utn_cs_ends( const char* buf ); 
long utn_cs_begs( const char* buf ); 
void utn_cs_upper( char* buf );	 
void utn_cs_lower( char* buf );	 
void utn_cs_mixcase( char* buf ); 
logical utn_cs_is_blank( const char* buf );					 
logical utn_cs_eq( const char* buf1, const char* buf2 );			 
logical utn_cs_eq_siz( const char* buf1, const char* buf2, const long siz ); 
logical utn_cs_uceq_siz( const char* buf1, const char* buf2, const long siz ); 
logical utn_cs_uceq( const char* buf1, const char* buf2 ); 
long utn_cs_match_minimum( const char* buf, const char** array, const long n ); 
void utn_cs_zero( char* line );	 
void utn_cs_blank( char* line, long maxlen ); 
void utn_cs_null( char* line, const long siz );	 
logical utn_cs_nullstr( const char* source ); 
logical utn_cs_alpha_char( const char c ); 
logical utn_cs_digit_char( const char c ); 
logical utn_cs_numeric_char( const char c ); 

logical utn_cs_subcmd_split( const char* opt, char* cmd, char* sub, const char sep );
void utn_cs_strip_digits( const char* name, char* pattern, const long maxlen );
char* utn_cs_conc( const char* buf1, const char* buf2, char* buf ); 
char* utn_cs_conc1( const char* buf1, const char* buf2, char* buf ); 
logical utn_cs_gt( const char* x, const char* y ); 
logical utn_cs_lt( const char* x, const char* y ); 
long utn_cs_list_add( char** list, long* list_size, const long max_size, const char* item ); 
char* utn_cs_get_ss( const char* source, char* dest, const long pos1, const long pos2 );	 
void utn_cs_put_ss( const char* source, char* dest, const long pos1, const long pos2 );		 
logical utn_cs_eq_ss( const char* buf, const long pos1, const long pos2, const char* comp ); 
logical utn_cs_ss( const char* buf, const char* sub ); 
long utn_cs_index( const char* buf, const char* sub ); 
long utn_cs_eindex( const char* buf, const long pos, const char* sub ); 
long utn_cs_index_char( const char* buf, const char c ); 
logical utn_cs_ss_char( const char* buf, const char sub ); 
logical utn_cs_split_string( const char* buf, const char sep, char* buf1, char* buf2 );  
char* utn_cs_upto( const char* source, const char targ, char* dest ); 
void utn_cs_denull( char* buf, const long siz ); 
void utn_cs_detab( char* buf ); 
void utn_cs_decmt( char* buf, const char esc, const char cmt ); 
logical utn_cs_remove_nl( char* line );	 
void utn_cs_ubar( char* source ); 
void utn_cs_deubar( char* source ); 
void utn_cs_compress_spaces( const char* source, char* dest );	 
void utn_cs_strpak( const char* source, char* dest ); 
void utn_cs_string_replace( char* cbuf, const char* old, const char* new ); 
void utn_cs_append_delim( char* buf, char* delim, char* text );
char* utn_cs_sparen( const char* source, char* dest ); 
char* utn_cs_paren( const char* source, char* dest ); 	
void utn_cs_gparen( const char* source, const char lpar, const char rpar, char* dest ); 
void utn_cs_deparen( char** buf, char* dest ); 
void utn_cs_parse_assignment( char** pptr, char* name, long maxlen, char* op );
void utn_cs_remove_quotes_and_trail( const char* val, char* dest, const long maxlen );
long utn_cs_count_char(const char* buf, const char c ); 
logical utn_cs_fs_format_parse( const char*  Fortran_Format, char* C_Format, integer n, char* DataType, integer* Field_Length ); 
long utn_cs_read_i( const char* buf ); 
double utn_cs_read_d( const char* buf ); 
float utn_cs_read_r( const char* buf ); 
short utn_cs_read_s( const char* buf ); 
logical utn_cs_read_q( const char* buf ); 
long utn_cs_read_digit( const char c ); 
long utn_cs_read_hex_digit( const char c ); 
long utn_cs_read_i1( const char* buf ); 
long utn_cs_read_val_int( char** buf, long* digits ); 
double utn_cs_read_val_frac( char** buf, long* digits ); 
long utn_cs_read_val_sign( char** buf ); 
void utn_cs_read_val_d( char** buf, long* sign_val, long* int_val, double* frac_val ); 
void utn_cs_read_val_de( char** buf, long* sign_val, long* int_val, double* frac_val ); 
void utn_cs_read_val_ee( char** buf, long* sign_val, long* int_val, double* frac_val, long* exp_val ); 
char* utn_cs_write_i( const long val, char* buf, const long siz ); 
char* utn_cs_write_d( const double val, char* buf, const long siz ); 
char* utn_cs_write_r( const  float val, char* buf, const long siz ); 
char* utn_cs_write_s( const short val, char* buf, const long siz ); 
char utn_cs_write_q( const logical q ); 
char* utn_cs_writef_i( const long val, const char* fmt, char* buf, const long siz ); 
char* utn_cs_writef_r( const float val, const char* fmt, char* buf, const long siz ); 
char* utn_cs_writef_s( const short val, const char* fmt, char* buf, const long siz ); 
char* utn_cs_writef_d( const double val, const char* fmt, char* buf, const long siz ); 
char* utn_cs_vwritef_d( const double* p, const char* fmt, char* buf, const long siz ); 
char* utn_cs_vwritef_r( const float* p, const char* fmt, char* buf, const long siz ); 
char* utn_cs_write_opt_s( const char* prompt, const short val, char* buf, const long siz ); 
char* utn_cs_write_opt_i( const char* prompt, const long val, char* buf, const long siz ); 
char* utn_cs_write_opt_r( const char* prompt, const float val, char* buf, const long siz ); 
char* utn_cs_write_opt_d( const char* prompt, const double val, char* buf, const long siz ); 
char* utn_cs_write_opt_c( const char* prompt, const char* val, char* buf, const long siz ); 
char* utn_cs_write_e_r( const float x, const float off, char* buf, const long siz ); 
char* utn_cs_write_e_d( const double x, const double off, char* buf, const  long siz ); 
char* utn_cs_write_tp_i( const long i, char* buf, const long siz ); 
char* utn_cs_write_rj_i( const long i, char* buf, const long siz ); 
void utn_cs_numberclean( char* tbuf, long sig, char* buf, long maxlen ); 
char utn_cs_logic( const logical q ); 
logical utn_cs_nancode( const char* buf ); 
void utn_cs_greek_letter( const long n, char* buf ); 
long utn_cs_greek_no( const char* buf ); 
long utn_cs_roman_parse( const char* buf ); 
void utn_cs_roman_numeral( const long n, char* buf ); 
char* utn_cs_advptr( char** buf ); 
void utn_cs_get_c( char** buf, char* token, const long siz ); 				
void utn_cs_get_cs( char** buf, char* token, const long siz ); 
void utn_cs_get_cl( char** buf, char* token, const long siz ); 			
double utn_cs_get_d( char** buf ); 
float utn_cs_get_r( char** buf ); 
long utn_cs_get_i( char** buf ); 
short utn_cs_get_s( char** buf ); 
logical utn_cs_get_q( char** buf ); 
double utn_cs_getd_d( char** buf, const double xdefault ); 
float utn_cs_getd_r( char** buf, const float xdefault ); 
short utn_cs_getd_s( char** buf, const short xdefault ); 
long utn_cs_getd_i( char** buf, const long xdefault ); 
void utn_cs_put_c( const char* token, char** buf, long* siz ); 
void utn_cs_put_cs( const char* token, char** buf, long* siz ); 	
void utn_cs_put_cn( const char* token, char** buf, long* siz ); 
void utn_cs_put_d( const double token, char** buf, long* siz ); 
void utn_cs_put_r( const float token, char** buf, long* siz ); 
void utn_cs_put_s( const short token, char** buf, long* siz ); 
void utn_cs_put_i( const long token, char** buf, long* siz ); 
long utn_cs_list_parse( char* buf, char** list, const long maxitems ); 
char** utn_cs_parse_paren_list( const char* buf, long* n ); 
char** utn_cs_parse_parcom_list( const char* buffer, long* np, logical mode );
char** utn_cs_parse_comma_list( const char* buffer, long* np );
char utn_cs_copy_token_esc( char** buf, const char* seps, const char* ppars, const char esc, char* dest, const long gsiz, logical* truncated );
char*  utn_cs_parse_paren_token( char** pptr, char lsep, char rsep, char* result, long maxlen );
char*  utn_cs_parse_paren_token_validate( char** pptr, char lsep, char rsep, char* result, long maxlen, logical *status );
char*  utn_cs_parse_term_token( char** pptr, char rsep );
void utn_cs_append( char** pptr, char* sep, char* buf );

void utn_cs_put_token( const char* buf, char** dest, const char sep, long* siz ); 
void utn_cs_get_token( char** buf, char* token, const long mode, const long siz ); 
void utn_cs_advance( char** buf ); 
long utn_cs_fs_ends( char* buf, long size ); 
void utn_cs_fs_zero( char* line, long size ); 
void utn_cs_copy_cf( char* cname, char* fname,  long fsiz );  
void utn_cs_copy_fc( char* fname, char* cname, long fsiz, long maxlen ); 
char* utn_cs_fs_dup( char* fstring, long maxlen ); 
void utn_cs_fs_free( char* cstring, char* fstring, long fsiz ); 
char** utn_cs_fs_alloc_array( char* array, long fsiz, long n );  
void utn_cs_fs_free_array( char** fc_StringArray, char* value, long fsiz, long n ); 
void utn_cs_fs_copy_item( char* cstring, char* fstring, long maxlen, long i );  
void utn_cs_fs_put_item( char* fstring, long maxlen, long i, char* cstring ); 
long utn_cs_fs_match( const char* buf, const char* array, const long n, const long size ); 
void utn_cs_char_name( char c, char* name ); 
void utn_cs_parser( char* buf, long* itemcode, long* itempos, long* nitems, char* cmode, logical debug); 
char utn_esc_get_default_char( const long type );
void utn_cs_parse_disp( char* buf, long* itemcode, long* itempos, long nitems );  
void utn_cs_parse_tdisp( char* buf, long* itemcode, long* itempos, long nitems, char* mb ); 
void utn_cs_parse_read( long i, char* buf, long* itempos, char* token, long* siz ); 
void utn_cs_parse_fmt( char* buf, long* itemcode, long* itempos, long nitems, char* mbuf ); 
long utn_rmod_d( const double x, double* y, const double r ); 
long utn_rmod_i( const long x, long* y, const long r ); 
long utn_rmod_s( const short value, short* remainder, const short divisor ); 
long utn_rmod_r( const float value, float* remainder, const float divisor ); 
long utn_logbase_i( const long x, const long n ); 
double utn_logbase_d( const double x, const long n ); 
float utn_logbase_r( const float x, const long n ); 
void utn_frac_r( const float value, long* int_val, float* fraction );  
void utn_frac_d( const double value, long* int_val, double* fraction ); 
long utn_min_i( const long i, const long j ); 
long utn_max_i( const long i, const long j ); 
double utn_min_d( double x, double y ); 
double utn_max_d( double x, double y ); 
double utn_range_d( double x, double xmin, double xmax );
logical utn_divisible( const long a, const long b ); 
long utn_ceiling_d( const double x );  
long utn_ceiling_r( const float x ); 
long utn_floor_d( const double x );  
long utn_floor_r( const float x ); 
long utn_nint_d( const double x );  
long utn_nint_r( const float x ); 
long utn_modulo_i( const long x, const long base );  
short utn_modulo_s( const short x, const short base );  
float utn_modulo_r( const float x, const float base );  
double utn_modulo_d( const double x, const double base ); 
double utn_round_d( const double x, const long f );  
float utn_round_r( const float x, const long f ); 
void utn_order_r( float* x, float* y);  
void utn_order_d( double* x, double* y ); 
logical utn_clip_r( const float x, const float y, const float x1, const float x2, const float yy1, const float yy2 );  
logical utn_clip_d( const double x, const double y, const double x1, const double x2, const double yy1, const double yy2 );  
logical utn_cx_clip_r( const complex z, const complex z1, const complex z2 );  
logical utn_cx_clip_d( const dcomplex z, const dcomplex z1, const dcomplex z2 ); 
double utn_rlap_check_d( const double x, const double x1, const double x2, logical* left, logical* right );  
float utn_rlap_check_r( const float x, const float x1, const float x2, logical* left, logical* right ); 
logical utn_overlap_r( const float xx1, const float xx2, const float yy1, const float yy2,    float* u1, float* u2 );  
logical utn_overlap_d( const double xx1, const double xx2, const double yy1, const double yy2,    double* u1, double* u2 ); 
double utn_lint_d( const double x1, const double x2, const double yy1, const double yy2, const double x0 );  
float utn_lint_r( const float x1, const float x2, const float yy1, const float yy2, const float x0 ); 
float utn_rabs( const float x ); 
float utn_rlog10( const float x ); 
float utn_rsqrt( const float x ); 
float utn_rpow( const float x, const float n ); 
float utn_ipow_r( const float x, const long n );  
double utn_ipow_d( const double x, const long n ); 
float utn_tpow_r( const long n );  
double utn_tpow_d( const long n ); 
double utn_ar_linterp_d( const double* x, const double* y, const long n, const double x0 );  
float utn_ar_linterp_r( const float* x, const float* y, const long n, const float x0 ); 
void utn_ar_movag_d( const double* yy0, double* y, const long n, const long k );  
void utn_ar_movag_r( const float* yy0, float* y, const long n, const long k ); 
float utn_poly_r( const float x, const float* coeffs, const long degree );  
double utn_poly_d( const double x, const double* coeffs, const long degree ); 
void utn_sx_val_to_sxa_i( const long v, long* sign, long* deg, long* min, long* sec );  
void utn_sx_val_to_sxa_r( const float v, long* sign, long* deg, long* min, long* sec, float* frac );  
void utn_sx_val_to_sxa_d( const double v, long* sign, long* deg, long* min, long* sec, double* frac ); 
long utn_sx_sxa_to_val_i( const long sign, const long deg, const long min, const long sec );  
float utn_sx_sxa_to_val_r( const long sign, const long deg, const long min, const long sec, const float frac );  
double utn_sx_sxa_to_val_d( const long sign, const long deg, const long min, const long sec, const  double frac ); 
void utn_sx_wval_to_sxa_r( const float r, const long w, logical* sign, long* d, long* m, long* s, float* f );  
void utn_sx_wval_to_sxa_d( const double r, const long w, logical* sign, long* d, long* m, long* s, double* f ); 
float utn_sx_wsxa_to_val_r( const logical sign, const long d, const long m, const long s, const float f, const long w );  
double utn_sx_wsxa_to_val_d( const logical sign, const long d, const long m, const long s, const double f, const long w ); 
void utn_sx_psx_to_sxa_i( const long r, logical* sg, long* d, long* m, long* s );  
void utn_sx_psx_to_sxa_d( const double r, logical* sg, long* d, long* m, long* s, double* f );  
void utn_sx_psx_to_sxa_r( const float r, logical* sg, long* d, long* m, long* s, float* f ); 
double utn_sx_sxa_to_psx_d( const logical sg, const long d, const long m, const long s, const double f );  
float utn_sx_sxa_to_psx_r( const logical sg, const long d, const long m, const long s, const float f ); 
void utn_cs_sex_fmt( const logical sg, const long d, const long m, const long s, const double fs, char sep,char* buf, const long siz ); 
char* utn_cs_sexwrite_hr_d( const double x, char sep, char* buf, const long siz );  
char* utn_cs_sexwrite_d( const double x, char sep, char* buf, const long siz ); 
double utn_eexp( const double x ); 
double utn_esinh( const double x );  
double utn_ecosh( const double x ); 
double utn_acosh( const double x );  
double utn_asinh( const double x );  
double utn_atanh( const double x ); 
double utn_dgauss( const double x, const double x0, const double sigma ); 
double utn_bose_einstein( const double x ); 
double utn_dplanckn( const double x, const long n ); 
double utn_hexagam( const double x );  
double utn_acych( const double x );  
double utn_acyc( const double x );  
double utn_E1_func( const double x ); 
complex utn_cx_set_r( const float x, const float y );  
dcomplex utn_cx_set_d( const double x, const double y ); 
float utn_cx_float_r( const complex z );  
double utn_cx_float_d( const dcomplex z );  
float utn_cx_imag_r( const complex z );  
double utn_cx_imag_d( const dcomplex z ); 
complex utn_cx_conj_r( const complex z1 );  
dcomplex utn_cx_conj_d( const dcomplex z1 ); 
complex utn_cx_mult_r( const complex z1, const complex z2 );  
dcomplex utn_cx_mult_d( const dcomplex z1, const dcomplex z2 ); 
complex utn_cx_add_r( const complex z1, const complex z2 );  
dcomplex utn_cx_add_d( const dcomplex z1, const dcomplex z2 ); 
void utn_cx_inc_r( const complex z1, complex* z2 );  
void utn_cx_inc_d( const dcomplex z1, dcomplex* z2 ); 
dcomplex utn_cx_prod_d( const dcomplex z1, const dcomplex z2 );  
complex utn_cx_prod_r( const complex z1, const complex z2 ); 
logical utn_cx_gt_r( const complex z1, const complex z2 );  
logical utn_cx_ge_r( const complex z1, const complex z2 );  
logical utn_cx_lt_r( const complex z1, const complex z2 );  
logical utn_cx_le_r( const complex z1, const complex z2 );  
logical utn_cx_gt_d( const dcomplex z1, const dcomplex z2 );  
logical utn_cx_ge_d( const dcomplex z1, const dcomplex z2 );  
logical utn_cx_lt_d( const dcomplex z1, const dcomplex z2 );  
logical utn_cx_le_d( const dcomplex z1, const dcomplex z2 ); 
float utn_radin_r( const float x ) ;  
double utn_radin_d( const double x ) ; 
float utn_radout_r( const float x );  
double utn_radout_d( const double x ); 
float utn_circ_r( const float x );  
double utn_circ_d( const double x ); 
float utn_circ_ew_r( const float x );  
double utn_circ_ew_d( const double x ); 
float utn_circ_rad_r( const float x );  
double utn_circ_rad_d( const double x ); 
float utn_circ_ew_rad_r( const float x );  
double utn_circ_ew_rad_d( const double x ); 
double utn_argd_d( const double x, const double y );  
float utn_argd_r( const float x, const float y );  
float utn_cx_argd_r( const complex z );  
double utn_cx_argd_d( const dcomplex dz ); 
float utn_rsind( const float x );  
double utn_dsind( const double x );  
float utn_rcosd( const float x );  
double utn_dcosd( const double x ); 
float utn_rasind( const float x );  
double utn_dasind( const double x );  
float utn_racosd( const float x );  
double utn_dacosd( const double x ); 
void utn_rotate2_d( const double x, const double y, const double roll, double* xr1, double* yr1 ); 
long utn_iplus( const logical q ); 
float utn_spharc_r( const float a1, const float b1, const float a2, const float b2 );  
double utn_spharc_d( const double a1, const double b1, const double a2, const double b2 ); 
void utn_ar_zero_i( long* a, const long siz );  
void utn_ar_zero_r( float* a, const long siz );  
void utn_ar_zero_s( short* a, const long siz );  
void utn_ar_zero_d( double* a, const long siz );  
void utn_ar_zero_c( char** x, const long n );  
void utn_ar_zero_dz( dcomplex* z, const long n );  
void utn_ar_zero_z( complex* z, const long n ); 
void utn_ar_copy_d( const double* a, double* b, const long siz );  
void utn_ar_copy_i( const long* a, long* b, const long siz );  
void utn_ar_copy_s( const short* a, short* b, const long siz );  
void utn_ar_copy_r( const float* a, float* b, const long siz );  
void utn_ar_copy_q( const logical* a, logical* b, const long siz );  
void utn_ar_copy_c( const char** x, char** y, const long n );  
void utn_ar_copy_z( const complex* z1, complex* z2, const long size );  
void utn_ar_copy_dz( const dcomplex* z1, dcomplex* z2, const long size ); 
double utn_ar_dot_d( const double* x, const double* y, const long siz );  
float utn_ar_dot_r( const float* x, const float* y, const long n );  
short utn_ar_dot_s( const short* x, const short* y, const long n );  
long utn_ar_dot_i( const long* x, const long* y, const long siz );  
dcomplex utn_ar_dot_dz( const dcomplex* x, const dcomplex* y, const long size );  
complex utn_ar_dot_z( const complex* x, const complex* y, const long size ); 
void utn_ar_bounds_r( const float* x, const long n, float* xmin, float* xmax );  
void utn_ar_bounds_s( const short* x, const long n, short* xmin, short* xmax );  
void utn_ar_bounds_i( const long* x, const long n, long* xmin, long* xmax );  
void utn_ar_bounds_d( const double* x, const long n, double* xmin, double* xmax ); 
void utn_ar_stats_s( const short* x, const long n, float* mean, float* var );  
void utn_ar_stats_r( const float* x, const long n, float* mean, float* var );  
void utn_ar_stats_i( const long* x, const long n , double* mean, double* var );  
void utn_ar_stats_d( const double* x, const long n , double* mean, double* var ); 
void utn_ar_fill_c( char** x, const long n, const char* x0 );  
void utn_ar_fill_i( long* x, const long n, const long x0 );  
void utn_ar_fill_s( short* x, const long n, const short x0 );  
void utn_ar_fill_d( double* x, const long n, const double x0 );  
void utn_ar_fill_r( float* x, const long n, const float x0 );  
void utn_ar_fill_z( complex* z, const long n, const complex z0 );  
void utn_ar_fill_dz( dcomplex* z, const long n, const dcomplex z0 ); 
void utn_ar_cmult_s( short* x, const long n, const short x0 );  
void utn_ar_cmult_i( long* x, const long n, const long x0 );  
void utn_ar_cmult_r( float* x, const long n, const float x0 );  
void utn_ar_cmult_d( double* x, const long n, const double x0 );  
void utn_ar_cmult_z( complex* z, const long n, const complex z0 );  
void utn_ar_cmult_dz( dcomplex* z, const long n, const dcomplex z0 ); 
void utn_ar_cadd_s( short* x, const long n, const short x0 );  
void utn_ar_cadd_i( long* x, const long n, const long x0 );  
void utn_ar_cadd_r( float* x, const long n, const float x0 );  
void utn_ar_cadd_d( double* x, const long n, const double x0 );  
void utn_ar_cadd_z( complex* z, const long n, const complex z0 );  
void utn_ar_cadd_dz( dcomplex* z, const long n, const dcomplex z0 ); 
void utn_ar_add_d( const double* x, double* y, const long n );  
void utn_ar_add_s( const short* x, short* y, const long n );  
void utn_ar_add_r( const float* x, float* y, const long n );  
void utn_ar_add_i( const long* x, long* y, const long n );  
void utn_ar_add_z( const complex* z1, complex* z2, const long n );  
void utn_ar_add_dz( const dcomplex* z1, dcomplex* z2, const long n ); 
void utn_ar_sub_s( const short* x, short* y, const long n );  
void utn_ar_sub_i( const long* x, long* y, const long n );  
void utn_ar_sub_r( const float* x, float* y, const long n );  
void utn_ar_sub_d( const double* x, double* y, const long n );  
void utn_ar_sub_dz( const  dcomplex* z1, dcomplex* z2, const long n );  
void utn_ar_sub_z( const complex* z1, complex* z2, const long n ); 
void utn_ar_wadd_s( const short* x1, const short* x2, const short w1, const short w2,short* y, const long n );  
void utn_ar_wadd_i( const long* x1, const long* x2, const long w1, const long w2,long* y, const long n );  
void utn_ar_wadd_r( const float* x1, const float* x2, const float w1, const float w2,float* y, const long n );  
void utn_ar_wadd_d( const double* x1, const double* x2, const double w1, const double w2,double* y, const long n );  
void utn_ar_wadd_z( const complex* z1, const complex* z2, const complex w1, const complex w2,  complex* z, const long n );  
void utn_ar_wadd_dz( const dcomplex* z1, const dcomplex* z2, const dcomplex w1, const dcomplex w2,  dcomplex* z, const long n ); 
void utn_ar_bin_s( short* x, const long n, const short x0, const short dx );  
void utn_ar_bin_r( float* x, const long n, const float x0, const float dx );  
void utn_ar_bin_i( long* x, const long n, const long x0, const long dx );  
void utn_ar_bin_d( double* x, const long n, const double x0, const double dx );  
void utn_ar_bin_dz( dcomplex* z, const long n, const dcomplex z0, const dcomplex dz );  
void utn_ar_bin_z( complex* z, const long n, const complex z0, const complex dz ); 
long utn_ar_cbisect_d( const double* x, const long n, const double x0 );  
long utn_ar_cbisect_r( const float* x, const long n, const float x0 );  
long utn_ar_cbisect_s( const short* x, const long n, const short x0 );  
long utn_ar_cbisect_i( const long* x, const long n, const long x0 ); 
long utn_ar_bisect_d( const double* x, const long n, const double x0 );  
long utn_ar_bisect_r( const float* x, const long n, const float x0 );  
long utn_ar_bisect_s( const short* x, const long n, const short x0 );  
long utn_ar_bisect_i( const long* x, const long n, const long x0 ); 
void utn_ar_copy_si( const   short* x,  long* y,  const long n);  
void utn_ar_copy_is( const  long* x, short* y, const long n);  
void utn_ar_copy_rd( const   float* x,  double* y,  const long n);  
void utn_ar_copy_dr( const double* x, float* y, const long n);  
void utn_ar_copy_dzz( const dcomplex* x, complex* y, const long n);  
void utn_ar_copy_zdz( const complex* x, dcomplex* y, const long n); 
long utn_ar_match_r(   const float value,  const float* list,  const long n );  
long utn_ar_match_i(   const long value,  const long* list,  const long n );  
long utn_ar_match_d(   const double value,  const double* list,  const long n );  
long utn_ar_match_s(   const short value,  const short* list,  const long n );  
long utn_ar_match_c( const char* buf, char** array, const long n ); 
long utn_ar_match_sorted_c( const char* buf, char** array, const long n );
long utn_ar_cmatch_r(   const float value,  const float* list,  const long n );  
long utn_ar_cmatch_i(   const long value,  const long* list,  const long n );  
long utn_ar_cmatch_d(   const double value,  const double* list,  const long n );  
long utn_ar_cmatch_s(   const short value,  const short* list,  const long n );  
long utn_ar_cmatch_c( const char* buf, char** array, const long n ); 
void utn_ar_csort_i( const long* x, const long n, long* jsort );  
void utn_ar_csort_d( const double* x, const long n, long* jsort ); 
void utn_ar_csort_r( const float* x, const long n, long* jsort ); 
void utn_ar_csort_s( const short* x, const long n, long* jsort );  
void utn_ar_csort_c( char** x, const long n, long* jsort ); 
void utn_ar_creindex_i( const long* jsort, const long n, long* isort ); 
void utn_ar_cresort_s( const short* x, const long n, short* y, long* jsort ); 
void utn_ar_cresort_d( const double* x, const long n, double* y, long* jsort ); 
void utn_ar_cresort_r( const float* x, const long n, float* y, long* jsort ); 
void utn_ar_cresort_i( const long* x, const long n, long* y, long* jsort ); 
void utn_ar_sort_r( const float* x, const long n, long* jsort ); 
void utn_ar_sort_s( const short* x, const long n, long* jsort );  
void utn_ar_sort_d( const double* x, const long n, long* jsort ); 
void utn_ar_sort_i( const long* x, const long n, long* jsort );  
void utn_ar_sort_c( char** x, const long n, long* jsort );  
void utn_cheapsortp_i( long* x, const long n );
void utn_csort_equals_i( const long* x, const long n, long* jsort );
void utn_ar_reindex_i( const long* x, const long n, long* y ); 
void utn_ar_resort_i( const long* x, long n, long* y, long* jsort );  
void utn_ar_resort_s( const short* x, const long n, short* y, long* jsort ); 
void utn_ar_resort_d( const double* x, const long n, double* y, long* jsort ); 
void utn_ar_resort_r( const float* x, const long n, float* y, long* jsort ); 
void utn_ar_poleval_r( const float* x, float* y, const long n, const float* coeffs, const long degree );  
void utn_ar_poleval_d( const double* x, double* y, const long n, const double* coeffs, const long degree ); 
void utn_ar_lsfit_d( const double* x, const double* y, const long n, double* a, double* b );  
void utn_ar_lsfit_r( const float* x, const float* y, const long n, float* a, float* b ); 
float utn_ar_trap_r( const float* x, const float* y, const long n );  
double utn_ar_trap_d( const double* x, const double* y, const long n ); 
float utn_ar_trap_log_r( const float* x, const float* y, const long n );  
double utn_ar_trap_log_d( const double* x, const double* y, const long n ); 
float utn_ar_trap_dx_r( const float* y, const long n, const float dx );  
double utn_ar_trap_dx_d( const double* y, const long n, const double dx ); 
double** utn_ar_valloc_d( const long n ); 
void utn_ar_vfree_d( double** v ); 
float utn_ar_vdot_r( const float* a, const float* b );  
double utn_ar_vdot_d( const double* a, const double* b ); 
float utn_ar_vnorm_r( const float* a );  
double utn_ar_vnorm_d( const double* a ); 
void utn_ar_vcross_r( const float* a, const float* b, float* c );  
void utn_ar_vcross_d( const double* a, const double* b, double* c ); 
float utn_ar_vmaxmod_r( const float* r );  
double utn_ar_vmaxmod_d( const double* r ); 
float utn_ar_vunit_r( float* r );  
double utn_ar_vunit_d( double* r ); 
void utn_ar_vcmult_r( float* p, const float c );  
void utn_ar_vdelta_d( const double* p, const double delta, const double* dp, double* r );
void utn_ar_vcmult_d( double* p, const double c ); 
void utn_ar_vadd_r( const float* p, const float* q, float* r );  
void utn_ar_vadd_d( const double* p, const double* q, double* r ); 
void utn_ar_vsub_r( const float* p, const float* q, float* r );  
void utn_ar_vsub_d( const double* p, const double* q, double* r ); 


void utn_ar_vzero_r( float* p );  
void utn_ar_vzero_d( double* p ); 
logical utn_ar_vnull_r( const float* v );  
logical utn_ar_vnull_d( const double* v ); 
void utn_ar_vcopy_r( const float* p, float* r );  
void utn_ar_vcopy_d( const double* p, double* r ); 
double utn_ar_vcopy_unit_d( double* r, double* unit ); 
void utn_ar_vcdiv_d( double* p, const double c ); 
void utn_ar_lmat_prex_d( double* aa, const double* y, double* x );  
void utn_ar_lmat_postx_d( double* aa, const double* y, double* x ); 
void utn_ar_mat_prex_r( float** aa, const float* y, float* x );  
void utn_ar_mat_prex_d( double** aa, const double* y, double* x ); 
void utn_ar_mat_postx_r( float** aa, const float* y, float* x );  
void utn_ar_mat_postx_d( double** aa, const double* y, double* x ); 
void utn_ar_mat_mult_r( float** aa, float** bb, float** cc );  
void utn_ar_mat_mult_d( double** aa, double** bb, double** cc ); 

void utn_ar_lmatnn_prex( double* v1, double* r, double* v2, long n );
void utn_ar_lmatnn_postx( double* v1, double* r, double* v2, long n );
void utn_ar_lmat_mult_d( double* a, double* b, double* c );

double utn_quat_unit( double* p );
double utn_quat_dot( double* p, double* q );
void utn_quat_mult( double* p, double* q, double* r );
void utn_quat_rot( double theta, double* n, double* p );
void utn_quat_from_matrix( double* rotate, double* q );
void utn_quat_to_matrix( double* q, double* rotate );

void utn_ar_euler_rotate_alt( double* euler, double* rotate );
void utn_ar_euler_calc_gen( double* r, long* axes, double* euler );
void utn_ar_euler_rotate_gen( double* euler, long* axes, double* rotate );
void utn_ar_euler_rotate( double* euler, double* rotate );
void utn_ar_euler_calc( double* rotate, double* euler );
void utn_ar_transform_backward( const double* in, double* rotate, double* translate, double* out );
void utn_ar_transform_forward( const double* in, double* rotate, double* translate, double* out );

void utn_ar_vec_r( const float r, const float theta, const float phi, float* p );  
void utn_ar_vec_d( const double r, const double theta, const double phi, double* p ); 
void utn_ar_polar_r( const float* p, float* r, float* theta, float* phi );  
void utn_ar_polar_d( const double* p, double* r, double* theta, double* phi ); 
float utn_ar_polarll_r( const float* p, float* llong, float* lat );  
double utn_ar_polarll_d( const double* p, double* llong, double* lat ); 
void utn_ar_unitll_d( const double llong, const double lat, double* p );  
void utn_ar_unitll_r( const float llong, const float lat, float* p ); 
void utn_ar_vec6_r( const float r, const float theta, const float phi, const float rrdot, const float tdot, const float phidot, float* p );  
void utn_ar_vec6_d( const double r, const double theta, const double phi, const double rrdot, const double tdot, const double phidot, double* p ); 
void utn_ar_polar6_r( const float* p, float* r, float* theta, float* phi, float* rrdot, float* tdot, float* phidot );  
void utn_ar_polar6_d( const double* p, double* r, double* theta, double* phi, double* rrdot, double* tdot, double* phidot ); 
void utn_free( void* p ); 
char** utn_ar_alloc_c( const long size, const long n ); 
void utn_ar_free_c( char** word ); 
void utn_ar_free_cp( char** word, const long n ); 
short** utn_ar_alloc_s( const long dim, const long n );  
long** utn_ar_alloc_i( const long dim, const long n );  
float** utn_ar_alloc_r( const long dim, const long n );  
double** utn_ar_alloc_d( const long dim, const long n ); 
void utn_ar_free_s( short** p );  
void utn_ar_free_i( long** p );  
void utn_ar_free_r( float** p );  
void utn_ar_free_d( double** p ); 
float** utn_ar_alloc_matrix_r(   const long xmin,  const long xmax,  const long ymin,  const long ymax);  
double** utn_ar_alloc_matrix_d(   const long xmin,  const long xmax,  const long ymin,  const long ymax);  
long** utn_ar_alloc_matrix_i(   const long xmin,  const long xmax,  const long ymin,  const long ymax);  
short** utn_ar_alloc_matrix_s(   const long xmin,  const long xmax,  const long ymin,  const long ymax);  
complex** utn_ar_alloc_matrix_z(   const long xmin,  const long xmax,  const long ymin,  const long ymax);  
dcomplex** utn_ar_alloc_matrix_dz(   const long xmin,  const long xmax,  const long ymin,  const long ymax); 
void utn_ar_free_matrix_s( short** matrix, const long xmin, const long ymin);  
void utn_ar_free_matrix_i( long** matrix, const long xmin, const long ymin);  
void utn_ar_free_matrix_r( float** matrix, const long xmin, const long ymin);  
void utn_ar_free_matrix_d( double** matrix, const long xmin, const long ymin);  
void utn_ar_free_matrix_z( complex** matrix, const long xmin, const long ymin);  
void utn_ar_free_matrix_dz( dcomplex** matrix, const long xmin, const long ymin); 
void utn_ar_transpose_s(   const short** x,  short** y,  const long xmin,  const long xmax,  const long ymin,  const long ymax);  
void utn_ar_transpose_i(   const long** x,  long** y,  const long xmin,  const long xmax,  const long ymin,  const long ymax);  
void utn_ar_transpose_r(   const float** x,  float** y,  const long xmin,  const long xmax,  const long ymin,  const long ymax);  
void utn_ar_transpose_d(   const double** x,  double** y,  const long xmin,  const long xmax,  const long ymin,  const long ymax);  
void utn_ar_transpose_q(   const logical** x,  logical** y,  const long xmin,  const long xmax,  const long ymin,  const long ymax);  
void utn_ar_transpose_dz(   const dcomplex** x,  dcomplex** y,  const long xmin,  const long xmax, const long ymin,  const long ymax);  
void utn_ar_transpose_z(   const complex** x,  complex** y,  const long xmin,  const long xmax,  const long ymin,  const long ymax); 
void utn_ar_minmax_d(  const double* x,	const  long  n,	 double* xmin,	 double* xmax,	 long*  imin, long*  imax	);  
void utn_ar_minmax_r(  const float* x,	const  long  n,	 float* xmin,	 float* xmax,	 long*  imin, long*  imax	);  
void utn_ar_minmax_i(  const long* x,	const  long  n,	 long* xmin,	 long* xmax,	 long*  imin, long*  imax	);  
void utn_ar_minmax_s(  const short* x,	const  long  n,	 short* xmin,	 short* xmax,	 long*  imin, long*  imax	); 
long utn_ar_product_i(  const long* a,	 const long n 	 );  
short utn_ar_product_s(  const short* a,	const  long n 	 );  
float utn_ar_product_r(  const float* a,	 const long n 	 );  
double utn_ar_product_d(  const double* a,const 	 long n 	 );  
dcomplex utn_ar_product_dz(  const dcomplex* a,const 	 long n );  
complex utn_ar_product_z(  const complex* a,	 const long n 	 ); 
long utn_ar_sum_i(  const long* a,	 const long n 	 );  
short utn_ar_sum_s(  const short* a, const long n  );  
float utn_ar_sum_r(  const float* a,	const  long n 	 );  
double utn_ar_sum_d( const double* a,	const  long n 	 );  
dcomplex utn_ar_sum_dz(  const dcomplex* a,	const  long n 	 );  
complex utn_ar_sum_z(  const complex* a,	const  long n 	 ); 
void utn_ar_merge_s(  const short* true, const short* false, short* merge, const logical* mask, const long size);  
void utn_ar_merge_i(  const long* true, const long* false, long* merge, const logical* mask, const long size);  
void utn_ar_merge_d(  const double* true, const double* false, double* merge, const logical* mask, const long size);  
void utn_ar_merge_r(  const float* true, const float* false, float* merge, const logical* mask, const long size);  
void utn_ar_merge_c(  const char** true, const char** false, char** merge, const logical* mask, const long size);  
void utn_ar_merge_cp(  char** true, char** false, char** merge, const logical* mask, const long size);  
void utn_ar_merge_z( const complex* z1, const complex* z2, complex* z, const logical* mask, const long size );  
void utn_ar_merge_dz( const dcomplex* z1, const dcomplex* z2, dcomplex* z, const logical* mask, const long size ); 
long utn_ar_pack_s( const short* x,	 const logical* mask,	 short* y,	 const long n	);  
long utn_ar_pack_i( const long* x,	const 	 logical* mask, long* y, const long n);  
long utn_ar_pack_r( const float* x,	 const logical* mask,	 float* y,	 const long n	);  
long utn_ar_pack_d( const double* x,	 const logical* mask,	 double* y,	 const long n	);  
long utn_ar_pack_c( const char** x,	 const logical* mask,	 char** y,	 const long n	);  
long utn_ar_pack_cp( char** x,	 const logical* mask,	 char** y,	 const long n	);  
long utn_ar_pack_dz( const dcomplex* z1, const logical* mask, dcomplex* z2, const long n );  
long utn_ar_pack_z( const complex* z1, const logical* mask, complex* z2, const long n ); 
long utn_ar_unpack_s( const short* x,	 const logical* mask,	 short* y,	 const short  x0, 	 const long n	);  
long utn_ar_unpack_i( const long* x,	 const logical* mask,	 long* y,	 const long  x0, 	 const long n	);  
long utn_ar_unpack_r( const float* x,	 const logical* mask,	 float* y,	 const float  x0, 	 const long n	);  
long utn_ar_unpack_d( const double* x,	 const logical* mask,	 double* y,	 const double  x0, 	 const long n	);  
long utn_ar_unpack_cp( char** x,	 const logical* mask,	 char** y,	 char*  x0, 	 const long n	);  
long utn_ar_unpack_c( const char** x,	 const logical* mask,	 char** y,	 const char*  x0, 	 const long n	 );  
long utn_ar_unpack_dz( const dcomplex* z1, const logical* mask, dcomplex* z2, const dcomplex z0, const long n );  
long utn_ar_unpack_z( const complex* z1, const logical* mask, complex* z2,  const complex z0, const long n ); 
void utn_ar_cshift_s(  const short* x, short* y, const long n, const long shift);  
void utn_ar_cshift_i(  const long* x, long* y, const long n, const long shift);  
void utn_ar_cshift_r(  const float* x, float* y, const long n, const long shift);  
void utn_ar_cshift_d(  const double* x, double* y, const long n, const long shift);  
void utn_ar_cshift_cp(  char** x, char** y, const long n, const long shift);  
void utn_ar_cshift_c(  const char** x, char** y, const long n, const long shift);  
void utn_ar_cshift_dz(  const dcomplex* x, dcomplex* y, const long n, const long shift);  
void utn_ar_cshift_z(  const complex* x, complex* y, const long n, const long shift); 
void utn_ar_eoshift_s(  const short* x, short* y, const long n, const long shift, const short fill);  
void utn_ar_eoshift_i(  const long* x, long* y, const long n, const long shift, const long fill);  
void utn_ar_eoshift_r(  const float* x, float* y, const long n, const long shift, const float fill);  
void utn_ar_eoshift_d(  const double* x, double* y, const long n, const long shift, const double fill);  
void utn_ar_eoshift_cp( char** x, char** y, const long n, const long shift, char* fill);  
void utn_ar_eoshift_c(  const char** x, char** y, const long n, const long shift, const char* fill);  
void utn_ar_eoshift_dz(  const dcomplex* x, dcomplex* y, const long n, const long shift, const dcomplex fill);  
void utn_ar_eoshift_z(  const complex* x, complex* y, const long n, const long shift, const complex fill); 
void utn_ar_not_q( logical* mask, const long n ); 
void utn_ar_true_q( logical* mask, const long n );  
void utn_ar_false_q( logical* mask, const long n ); 
logical utn_ar_all_q( const logical* mask, const long n ); 
logical utn_ar_any_q( const logical* mask, const long n ); 
long utn_ar_count_q( const logical* mask, const long n ); 
void utn_ar_and_q( const logical* x, logical* y, const long n );  
void utn_ar_or_q( const logical* x, logical* y, const long n ); 


void utn_esc_init( void ); 
void utn_esc_status( void );	 
char utn_esc_get( const char* name ); 
char utn_esc_get_default( const char* name ); 


ParseTokenType utn_char_code( char c );
ParseTokenType utn_cs_parse_namecode( char* name );

logical utn_fio_posix_access( const char* name, const char* mode );

long utn_ar_search_c( const char* buf, char** array, const long n );



NameList utn_fio_pathlist_expand( char* path );
void utn_fio_pathlist_free( PathList plist );

void utn_ar_mix_print_generic( void* ptr, DataType type, char* buf, long size );
int utn_ar_mix_set_elt( MixArray array, const long offset, const DataType type, const void* value );
int utn_ar_mix_get_elt( const MixArray array, const long offset, DataType* type, void* value );
void utn_ar_mix_copy_value( const void* ptr, const DataType type, void* value, logical copy, long size );

void utn_ar_mix_delete_elt( MixArray array, const long offset );
void utn_ar_mix_copy_elt( const MixArray src, const long srcoffset, MixArray dest, const long destoffset );
void utn_ar_mix_move_elt( MixArray src, const long srcoffset, MixArray dest, const long destoffset );
void* utn_ar_mix_ptr( const MixArray array, const long offset );
void utn_ar_mix_numeric_cast( const void* in, const DataType intype, const DataType outtype, void* out );
void utn_ar_mix_cast_array( const void* in, const DataType intype, const DataType outtype, void* out, const long nvals );
void utn_ar_mix_numeric_castArray( const void* in, const DataType intype, const DataType outtype, void* out, const long nvals );
MixArray utn_ar_mix_alloc_type( const DataType type, const long size, const long n );
MixArray utn_ar_mix_alloc( const long n );
int utn_ar_mix_extend( MixArray array, const long n );
void utn_ar_mix_free( MixArray array );
void utn_ar_mix_cast_elt( MixArray array, const long offset, const DataType type, void* value );
int utn_ar_mix_cast_elt_cp( MixArray array, const long offset, char* value );
char* utn_ar_mix_print_elt( const MixArray array, const long offset, char* buf, long size );

/* Unsupported?*/
void utn_ar_mix_numeric_cast_array( const void* in, const DataType intype, const DataType outtype, void* out, const long nvals );

long utn_ar_alias_search( const AliasList array, const char* cptname );
void utn_ar_alias_print( const AliasList array );
void utn_ar_alias_free( AliasList array );
int utn_ar_alias_extend( AliasList array, const long n );
AliasList utn_ar_alias_alloc( const long n );
long utn_ar_alias_set_name( AliasList array, char* name );
long utn_ar_alias_get_dim( AliasList array, long offset );
void utn_ar_alias_get_name( AliasList array, long offset, char* name, long maxlen );
void utn_ar_alias_get_cpt( AliasList array, long offset, long cpt,  char* name, long maxlen );
void utn_ar_alias_set_cpts( AliasList array, long entry, char** cpts, long dim );
double utn_cs_read_numeric_val(  char** pptr );

DataType* utn_ar_mix_array_types( MixArray array );
long utn_ar_mix_type_size( const DataType type );
long utn_ar_mix_array_size( const MixArray array );
void utn_ar_mix_type_name( const DataType type, char* buf, long maxlen );

void* utn_realloc( void* data, long const old, long const new, const long size );
void* utn_ar_mix_elt( const MixArray array, const long offset );
long utn_ar_mix_n( MixArray array );
long utn_ar_mix_nmax( MixArray array );
void utn_ar_mix_type_min( const DataType type, void* value );
void utn_ar_mix_type_max( const DataType type, void *value );
void utn_ar_mix_type_zero( const DataType type, void* value );
void utn_cs_compose_paren_list( char** items, const long n, char* buf, const long maxlen );

void utn_cs_writef_zeros( double x, long ni, long nf, char* xbuf, long maxlen );


void utn_cs_reverse( char* buf );
void utn_cs_trim( char* buf );

long utn_fio_dir_next_normal_file( FileDirectory dir, char* name, long maxlen );
FileDirectory utn_fio_dir_open( const char* idirname );
void utn_fio_dir_close( FileDirectory dir );
long utn_fio_dir_next_filepath( FileDirectory dir, char* fn, long maxlen );

logical utn_ar_mix_is_max( const DataType type, void* value );
logical utn_ar_mix_is_min( const DataType type, void* value );
char** utn_ar_alias_get_cpt_list( AliasList array, long offset );
void* utn_ar_gen_alloc( const DataType type, const long n );
logical utn_ar_gen_get_cast_elt( const void* array, const long offset, const long n, const DataType array_type, const DataType type, void* value );
logical utn_ar_gen_get_elt( const void* array, const long offset, const DataType type, void* value );
logical utn_ar_gen_set_elt( void* array, const long offset, const DataType type, void* value );
void* utn_ar_gen_ptr( const void* data, const DataType type, const long size, const long offset );
logical utn_ar_mix_is_numeric( const DataType type );
long utn_ar_mix_intersect( void* mins1, void* maxes1, long n1, void* mins2, void* maxes2, long n2,
                 DataType type, void** minsp, void** maxesp, long* np );
logical utn_cs_strcmp( char* a, char* b );
char* utn_cs_writef_clean_d( const double val, const char* fmt, const logical clean, const long sig, char* buf, const long siz );
char* utn_cs_dup_cat( char* a, char* b, char* sep );

int utn_ar_mix_eq( const DataType type, void* value1, void* value2 );
char** utn_cs_dup_array( char** data, long n );
char** utn_cs_parse_parsep_list( const char* buffer, char* pars, char sep, long* np, logical mode );
long utn_sys_align( long total, long size, long old_size );

void utn_genlist_status( GenList list );
void utn_genlist_free( GenList list );
void* utn_genlist_entry( GenList list, integer entry );
GenList utn_genlist_alloc( char* name, integer nmax, integer size );
void utn_genlist_realloc( GenList list, integer nmax );
void* utn_genlist_add( GenList list );
long utn_genlist_n( GenList list );
char** utn_ar_realloc_c( char** word, const long size, const long n );

double utn_bi_tpio_d( char* prompt, char** ptr );
void utn_bi_tpio_c( char* prompt, char* buf, long size, char** ptr );
logical utn_sys_big_endian( void );
void utn_sys_get_bytes( const char* utn_byte_buffer, char* result_buffer, const long result_nvals, long memory_bytes, long disk_bytes, logical disk_big_endian );
void utn_sys_get_bytes_i( const char* buf, integer* data, const long n, logical disk_endian );
void utn_sys_get_bytes_d( const char* buf, double* data, const long n, logical disk_endian );

void* utn_genlist_add_t( GenList list, logical* ok );
double utn_erfinv( const double p );
double utn_erf( const double x );
double utn_erfinv2(double p);
double utn_erftot( const double x );
void utn_quat_to_asp( double* q, double* ra, double* dec, double* roll );
double utn_hypot( double x, double y );
void utn_ar_csortp_d( double* x, const long n );
void utn_ar_fill_q( logical* x, const long m, const logical x0 );

void utn_quat_from_asp( double ra, double dec, double roll, double* q );


double utn_rand_pid( void );
char* utn_cs_write_q_string( logical val );
logical utn_cs_is_number( char* s );
char* utn_cs_fix_null(char* s);	/* Don't return NULL pointers! */
char* utn_cs_strip_double_quote( char* src, char* tokbuf, integer maxlen );
logical utn_cs_white_char( char c );
void utn_cs_translate_string(char* source, char* lookup, char* trans, char* result);
void utn_cs_pad(char* s, integer len);	/* pad a string to indicated length */
integer utn_cs_match_complete( char** names, integer n, char* name, integer cpos );
void utn_cs_translate_escapes( char* src, char* dest, char esc );
void utn_tpio_set_logfile( char* buf );
void utn_ieee_get( const char* buf, char* ptr, const long n, const long size, const long fsize);
logical utn_cs_eq_begin( const char* ibuf1, const char* ibuf2 );

void utn_cshellsort_c( char** x, const long n, long * jsort );
void utn_cshellsort_d( const double* x, const long n, long * jsort );
void utn_cshellsort_i( const long* x, const long n, long * jsort );
void utn_cshellsort_r( const float* x, const long n, long * jsort );
void utn_cshellsort_s( const short* x, const long n, long * jsort );
void utn_cheapsort_c( char** x, const long n, long* jsort );
void utn_cheapsort_d( const double* x, const long n, long* jsort );
void utn_cheapsort_i( const long* x, const long n, long* jsort );
void utn_cheapsortp_i( long* x, const long n );
void utn_cheapsort_r( const float* x, const long n, long* jsort );
void utn_cheapsort_s( const short* x, const long n, long* jsort );


long var_sprintf( char* buf, integer length, char* format, ... );
logical utn_genlist_check( void* listp );



integer utn_keylist_find_value( KeyList list, void* value );
logical utn_keylist_get_value( KeyList list, integer offset, void* value, integer* type );
logical utn_keylist_get_value_c( KeyList list, integer keyno, char* value, integer maxlen );
integer utn_keylist_search_value_c( KeyList list, char* key, void* value, integer maxlen );
integer utn_keylist_search_value( KeyList list, char* key, void* value, integer* type );
integer utn_keylist_search( KeyList list, char* key );
logical utn_keylist_realloc( KeyList list, long nmax );
logical utn_keylist_add( KeyList list, char* key, void* value, integer type );
logical utn_keylist_check( void* listp );
void utn_keylist_status( KeyList list );
long utn_keylist_n( KeyList list );
void utn_keylist_free( KeyList list );
integer utn_keylist_next( KeyList list );
void utn_keylist_rewind( KeyList list );
void* utn_keylist_get_ptr( KeyList list, integer next );
void utn_keylist_set_size( KeyList list, integer size );
void* utn_keylist_copy( KeyList list );
char* utn_keylist_get_keyptr( KeyList list, integer keyno );
logical utn_keylist_delete_key( KeyList list, integer keyno );
KeyList utn_keylist_alloc_n( char* name, long nmax, logical case_s );
KeyList utn_keylist_alloc( void );
KeyList utn_keylist_alloc_size( integer size );
void utn_keylist_setv( KeyList list, integer size, void (*kfree)(void*) );
logical utn_keylist_extend( KeyList list );
integer utn_keylist_search_next( KeyList list, char* key );
logical utn_keylist_addv( KeyList list, char* key, void* value );
void utn_cs_texubar( char* source );
logical utn_cs_match_wild( char* buf, char* mask, char wild );
char* utn_fio_dir_next_file( FileDirectory dir );

double utn_sys_today_pack( void );
void utn_sys_get_bytes_r( const char* buf, float* data, const long n, logical disk_endian );
void utn_sys_cwd( char* buf, integer maxlen, logical trail );
char** utn_sys_parse_cmdline( char* buf, integer* nargsp );
char utn_cs_copy_token_arg( char** buf, char* dest, const long gsiz );
void utn_sys_get_bytes_ii( const char* buf, int* data, const long n, logical disk_endian );

integer utn_file_bytes( char* name );
long utn_file_stat_size(  char* name );
logical utn_fio_open_fifo( char* path, mode_t mode );
long utn_file_set_mask( void );



integer utn_cs_read_hex( char* buf );

long utn_vlist_n( VList list );
void utn_vlist_free( VList list );
void utn_vlist_status( VList list );
integer utn_vlist_add_t( VList list, void* ptr, logical* reset );
logical utn_vlist_check( void* listp );
integer utn_vlist_add( VList list, void* ptr );
void utn_vlist_realloc( VList list, long nmax );
void* utn_vlist_entry( VList list, long entry );
VList utn_vlist_alloc( char* name, long nmax );
void utn_vlist_free( VList list );


typedef struct Spline_s {
 double* x;
 double* y;
 double* y2;
 double* u;
 integer n;
} *Spline;

Spline utn_spline_init_i( integer* x, integer* y, integer n );
void utn_spline_free( Spline spline );
double utn_spline_eval( Spline spline, double x, logical* status );
Spline utn_spline_init_d( double* x, double* y, integer n );
void utn_sys_today_iso( char* buf, const long size );
void utn_sys_now_iso( char* buf, const long size );
void utn_cs_simple_token( char** buf, const char sep, char* dest, const long gsiz );
double utn_atan_d( const double x, const double y );

typedef struct GenError_s {
 logical fatal;
 integer code;
 TextBuf buf;
} *GenError;
  
void utn_error_free( GenError error );
GenError utn_error( char* buf );
GenError utn_warn_code( char* buf, integer code );
GenError utn_warn( char* buf );
GenError utn_fatal_code( char* buf, integer code );



typedef struct GenStack_s {
 void** data;
 integer n;
 integer nmax;
} *GenStack;


integer utn_stack_push( GenStack stack, void* entry );
GenStack utn_stack_alloc( void );
void utn_stack_set( GenStack stack, integer i, void* entry );
void utn_stack_free( GenStack stack );
void utn_stack_clear( GenStack stack );
void* utn_stack_pop( GenStack stack );
void* utn_stack_top( GenStack stack );
integer utn_stack_size( GenStack stack );
void* utn_stack_entry( GenStack stack, integer i );

void utn_error_set( GenStack old_error, char* text, long code, long auxcode ); 
long utn_error_code( GenStack error ); 
void utn_error_disp( GenStack error, ErrorClasses error_context );  
void utn_error_clear( GenStack error );  
void utn_error_class_decode( ErrorClasses list, long code, char* text, integer maxlen ); 
long utn_error_class_register( ErrorClasses list, char* name ); 
ErrorClasses utn_error_class_init( void ); 
ErrorClasses utn_error_class(void); 
GenStack utn_error_state(void); 
void utn_error_message( GenStack errorp, char* buf, long maxlen );

GenStack utn_fio_std_drivers( void );

FixNameList utn_cs_fnamelist_alloc( long n, long size );
void utn_cs_fnamelist_free( FixNameList list );
void utn_cs_fnamelist_add( FixNameList list, const char* name );
long utn_cs_fnamelist_size( FixNameList list );
char* utn_cs_fnamelist_entry( FixNameList list, long i );
long utn_cs_fnamelist_match( FixNameList list, const char* name );
long utn_cs_fnamelist_search( FixNameList list, const char* name );
logical utn_cs_fnamelist_set( FixNameList list, const long entry, const char* name );
long utn_cs_fnamelist_entry_size( FixNameList list );
FixNameList utn_cs_fnamelist_assign( char** names, integer n );

NameList utn_cs_namelist_alloc( void );
void utn_cs_namelist_free( NameList list );
void utn_cs_namelist_add( NameList list, const char* name );
long utn_cs_namelist_size( NameList list );
char* utn_cs_namelist_entry( NameList list, long i );
long utn_cs_namelist_match( NameList list, const char* name );
long utn_cs_namelist_search( NameList list, const char* name );
logical utn_cs_namelist_set( NameList list, const long entry, const char* name );
long utn_cs_namelist_entry_size( NameList list );


void utn_err_util( char* buf, long code );


enum { 
      UT_ERR_MALLOC = UT_ERR_START,
      UT_ERR_UNKNOWN_FFMT, 
      UT_ERR_READ_STRING,
      UT_ERR_FIO_BAD_MODE,
      UT_ERR_FIO_OPEN_FAIL,
      UT_ERR_TPIO_OPEN_FAIL,
      UT_ERR_FLAG_PARSE,
      UT_ERR_VER_INIT,
      UT_ERR_UNIT_PARSE,
      UT_ERR_ESC,
      UT_ERR_TPIO_LOOP,
      UT_ERR_TPIO_ESC
     };
logical utn_sys_cmdline( char** args, long nargs, char* cmdline, long siz );
UtilNulls utn_null_alloc( void );


typedef struct UtilEscapeData_s *UtilEscData;
typedef struct {
 char echar;
 char edefault;
 char name[8];
} UtilEscapes;
 
struct UtilEscapeData_s {
 UtilEscapes* data;
 long n;
};
#define ESC_NTYPES 10

enum ESC_TYPES {
 ESC_CHAR, LEFT_PAREN, RIGHT_PAREN, 
 WORD_SEP, STRING_SEP, 
 SYS_CHAR, CONT_CHAR, CMT_CHAR, MACRO_CHAR, ARG_SEP
 };


char utn_esc_special_char( const long type );
void utn_esc_init( void );
UtilEscData utn_esc_alloc( void );
void utn_esc_init_state( UtilEscData edata );
void utn_esc_free( UtilEscData edata );
void utn_esc_get_name( const long type, char* name );
char utn_esc_special_char( const long type );  
GenError utn_esc_reset( const char* name, const char val );
char utn_esc_get( const char* name );
char utn_esc_get_default( const char* name );
void utn_esc_status( void );

void utn_error_free_state( ErrorGlobal error_global );
void utn_null_free( UtilNulls nulls );
ErrorGlobal utn_error_init_state( void );
UtilEscData utn_state_escapes( void );
void utn_cs_compose_comma_list( char** names, long n, char* buf, long maxlen );
double utn_asym_sinc( double x, double a );
char* utn_cs_strip_quotes( char* src, char* tokbuf, integer maxlen );
void ar_minmax_d( 
 double* x,		/* (i) Array */
 integer  n,		/* (i) Array size */
 double* range,		/* (o) Min max value */
 integer* loc );
double utn_ar_max_d( double* x, integer n );
void utn_sys_put_bytes_r(   char* buf, float* data,   long n, logical disk_endian );
void utn_sys_put_bytes_i(   char* buf, integer* data,   long n, logical disk_endian );
void utn_sys_put_bytes_d(   char* buf, double* data,   long n, logical disk_endian );
void utn_sys_put_bytes( char* mem_buffer, char* output_buffer,   long result_nvals, long memory_bytes, long disk_bytes, logical disk_big_endian );
float utn_ar_max_r( float* x, integer n );
#endif



