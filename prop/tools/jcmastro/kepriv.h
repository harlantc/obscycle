/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/



static const double Earth_GM = 398600.4418;
static const double Earth_Radius = 6378.165; 
static const double Earth_Day = 86164.1;
static const double Earth_J2   = 0.00108263;

double Sun_GM = 1.32712440018e11;
double ckms = 299792.458;
long Planet_n = 11;
char* Planet_names[] = { "Earth", "Moon", "Sun", "Venus", "Mars", "Jupiter",
                  "Saturn", "Io", "Europa", "Ganymede", "Callisto"  };
/* Planet inverse mass */
double Planet_GM[] = { 332946.05, 26739809.0, 1.0, 408523.71, 3098708.0, 1047.3486,
3497.898, 22302521.0, 40843336.0, 13351443.8, 18505606.1 };
double Planet_radius[] = { 6378.165, 1738.0, 695990.0, 6051.0, 3397.0, 71492.0, 60268.0,
 1815.0, 1569.0, 2631.0, 2400.0 };
/* Seconds */
double Planet_period[] = { 86164.1, 2360591.5, 2192832.0, 20996060.0, 88642.66, 35729.7, 38362.4,
 152841.0, 306806.0, 618192.0, 1442016.0 };
double Planet_J2[] = { 0.00108263, 0.0002027, 0.0, 6.0e-6, 0.001959, 0.014736, 0.016479,
 0.0, 0.0, 0.0, 0.0 };
double Planet_inc[] = { 23.45, 5.1454, 0.0, 177.4, 23.98, 3.08, 26.73, 0.0, 0.0, 0.0, 0.0 };

static struct World_s EarthData;
static World EarthPtr = NULL;



