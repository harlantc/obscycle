/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#ifndef NHLIB_H
#define NHLIB_H

#include "jcmfits.h"
#include "jcmastro.h"
#include "gprecess.h"
#include <string.h>

#define NH_BELL 0
#define NH_NRAO 1
#define BELL_NTEMP 124
#define BELL_NBINS 36
#define COLDEN_NGRID 5

typedef struct MapData_s {
 integer naxes;
 integer axes[2];
 char* axname[2];
 char* projtype[2];
 double cdelt[2];
 double crval[2];
 double crpix[2];
 double longpole;
 double** map;
} *MapData;

typedef struct ColdenRecord_s {
 integer seq;
 integer ptr;
 double ra;
 double dec;
 double t;
 double nh;
 double dist;
} ColdenRecord;


/* 5/2011: avoid compiler warning */
#undef  EXTERN

#ifndef LIB_MAIN
#define EXTERN extern
#else
#define EXTERN
#endif

EXTERN enum { NH_CODE_INTERP=0, NH_CODE_TARG, NH_CODE_NEAR, NH_CODE_EQ, NH_CODE_SOUTH } ColdenCodes;
EXTERN enum { DIRECT_OO, DIRECT_UL, DIRECT_UR, DIRECT_LL, DIRECT_LR } SphDirection;


typedef struct BellRecord_s {
 short seq;
 float gl;
 float gb;
 short cchan;             /* Central channel */
 float lsr;               /* LSR velocity */
 float temp[BELL_NTEMP];  /* Channel temperatures */
} *BellRecord;


typedef struct BellIndex_s {
 integer ptr[BELL_NBINS+1];
 integer top[BELL_NBINS+1];
 integer nbins;
} *BellIndex;

typedef struct BellData_s {
 double* ra;
 double* dec;
 double* tint;
 double* nh;
 integer* seq;
 integer* ptr;
 integer n;
 BellIndex index;
 double north_cap;
 double search_radius;
 integer north_cap_bin;
 double ra_bin_size;
 double south_boundary;
 BellRecord record;
 FioFits cube;
} *BellData;



typedef struct ColdenInterface_s {
  char* name;
  void* (*open)( char* filename, char* opt );
  double (*eval)( ColdenRecord* grid, void* data, double ra, double dec, double gl, double gb,
        double vmin, double vmax, char* msgbuf );
  void (*free)( void* data );
  logical vel;
  char* filename;
  char* opt;
} *ColdenInterface;

typedef struct ColdenInterface_s ColdenInterfaceData;

typedef struct Colden_s {
 Celestial* celestial;
 TextCard source;
 double vmin;
 double vmax;
 logical vstate;
 double VMIN_FULL;
 double VMAX_FULL;
 TextBuf nh_dir;
 integer nopts;
 ColdenInterface* interface;
 integer interface_n;
 integer ifid;
 ColdenRecord obs[COLDEN_NGRID];
 void* data;
} *Colden;

logical nh_convert( Colden state, CelSystem* fsys, char** pptr, logical repeat );
logical colden_interpret( Colden state, CelSystem* fsys, char* cmdbuf );
void colden_interpreter( Colden state );
logical nh_interact( Colden state );
void colden_free( Colden state );
void* nh_read_bell( char* idx_file, char* cube_file );
Colden colden_init( char* args[], int nargs );

void nh_listopts(void);
void nh_set_vlims( Colden state, double vmin, double vmax );
void nh_unset_vlims( Colden state );
void nh_status( Colden state );
void nh_set_dir( Colden state, char* dir );

ColdenInterface* nh_interface_alloc( integer* np );
logical nh_get_vlims( Colden state, double* vmin, double* vmax );
integer nh_set_source( Colden state, char* opt );
void nh_interface_free( ColdenInterface interface );
void nh_bell_get_code( integer code, char* code_message );
BellData nh_bell_alloc( integer n );
void nh_bell_free( void* datap );
logical nh_bell_get_rec( BellData bell, integer irec, BellRecord bellrec );
void nh_bell_get_code( integer code, char* code_message );

void nh_bell_find_nearest( ColdenRecord* grid, BellData bell, double targ_ra, double targ_dec,
 double ra_scale, integer binno );
double colden_eval_bell( ColdenRecord* grid, void* data, double ra, double dec, double gl, double gb,
 double vmin, double vmax, char* codebuf );


double nh_eval_bell( ColdenRecord* grid, BellData bell, double ra, double dec, double vmin, double vmax, char* codebuf );
double colden_eval_nrao( ColdenRecord* grid, void* data, double ra, double dec, double gl, double gb,
 double vmin, double vmax, char* codebuf );
double nh_eval_nrao( double gl, double gb, MapData data );
void nh_bell_search_index( ColdenRecord* grid, BellData bell, double targ_ra, double targ_dec );
logical sph_close( double x1, double x2, double y1, double y2, double xtol, double ytol );
integer nh_bell_set_code( ColdenRecord* record, integer code );
double sph_nbr( double x1, double y1, double x2, double y2, integer* direct );
double nhbase_xinterp( double ra, double dec, double lra, double ldec,
 double rra, double rdec, double* decp );
double nhbase_interp( ColdenRecord* grid, double tra, double tdec, integer* codep );
double nh_bell_integrate( BellData bell, integer ptr, double vmin, double vmax );
void nhbase_result( Celestial* state, CelSystem* tsys, char* pos1, double* bpos, double* gpos, double vmin,
 double vmax, double nh,  char* codebuf, integer printmode );
 
MapData map_data_alloc( integer nx, integer ny );
integer nh_find_interface( Colden state, char* opt );

double nrao_map_interp( double x, double y, integer nx, integer ny, double** map );
void* nh_read_nrao( char* filename, char* opt );
void nh_close_nrao( void* datap );
void nh_data_free( Colden state );
ColdenInterface nrao_alloc( void );
ColdenInterface bell_alloc( void );
void nrao_get_data( double** map, integer* axes, double scale, FioFits file );
#endif
