/*_C_INSERT_SAO_COPYRIGHT_HERE_(2008,2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/


#define WK_DAYS 7
#define YL_MONTHS 12
#define YL_DAYS   365
static const integer JC_DAYS = 36525;
static const integer    DL_SEC    = 86400;
static const double DL_SEC_D  = 86400.0;
static const double NOON      = 0.5;
static const integer    IY_J2000  = 2000;
static const double Y_J2000   = 2000.0;
static const integer    IJD_J2000 = 2451544;
static const double JD_J2000 = 2451544.5;
static const double MJD_ZERO = 2400000.5;

static const double YL_J2000  = 36525 / 100.0;   /* JC_DAYS / 100 */
static const integer DOY_J2000 = 1;  /* Day of year at JD_J2000 */
static const integer WKDAY_J2000  = 7;  /* Day of week: Sat 2000 Jan 1 */
static const double DB1900    = 0.31352;
static const double DYL_B1900 = -0.007801219;
static const double YL_B1900 = 365.242198781;  /* YL_J2000 + DYL_B1900 */
static const double JD_B1900 = 2415109.81352;

