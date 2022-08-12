# _INSERT_SAO_COPYRIGHT_HERE_(2008)_
# _INSERT_GPL_LICENSE_HERE_


use Sys::Hostname;
use File::Basename;

$PROVIS_RELEASE_DIR="/proj/cm/Release/ds.provis/";
#"/proj/ascds/DS.daily/";

#"/proj/xena3/rmilas/SOLARIS/webvis_test_release_zip/";

%Global = (
	   'maxclients'       , '2',
	   'timeout'          , '1',
	   'retries'          , '30',
	   'xres'             , '90',
	   'yres'             , '90',
	   'XVFB'             , '/usr/openwin/bin/Xvfb',
	   'XVFB_LIB'         , '',
	   'XVFB_OPTIONS'     , '-dev vfb screen 0 1280x1024x16 dpix 90 dpiy 90 fbdir /tmp/.X11-provis -ac -pn',
	   'XVFB_PROC'        , 'Xsun',
	   'fileexptime_s'    , '600',
	   'wget'             , '/usr/local/bin/wget',
	   'wish'             , "${PROVIS_RELEASE_DIR}/ots/bin/bltwish",
	   'simbadURL'        , 'http://simbad.harvard.edu/simbad/sim-script?submit=submit+script&script=output+console%3Doff+script%3Doff%0D%0Aformat+object+form1+%22%25COO%28s2%3BC%3BFK5%3BJ2000%29%22%0D%0Aquery+id+TARGETNAME%0D%0A',
	   'DISPLAY_NUMBER'   , '0',
	   'DISPLAY_HOST'     , '',
	   'DISPLAY'          , '',
	   'ASCDS_INSTALL'    , "${PROVIS_RELEASE_DIR}",
	   'EPHEMERIS_FILE'   , "${PROVIS_RELEASE_DIR}/data/provis.ephemeris.dat",
	   'LIBASTROCAL_FILE' , "${PROVIS_RELEASE_DIR}/data/astro.cal",
	   'LD_LIBRARY_PATH'  , '' ,
	   'FILL_PATTERNS_DIR', "${PROVIS_RELEASE_DIR}/data",
	   'gs'               , "${PROVIS_RELEASE_DIR}/ots/bin/gs" ,
	   'gs1'               , '/usr/local/bin/gs',
	   'web_server_port'  , "$ENV{'SERVER_PORT'}",
	   'web_server'       , "http://$ENV{'SERVER_NAME'}",
           'htdocsdir'        , '/soft/provis/',
	   'cgibindir'        , '',
           'uploaddir'        , '/proj/xena/rmilas/SOLARIS/www_zip/uploads/',
           'allowedips'       , '131.142.184.*a   131.142.185.---a *a.*a.*a.*a'
	   );


if ( $Global{'cgibindir'} eq '' && $ENV{'SCRIPT_NAME'} ne '' )
{
    $Global{'cgibindir'}=dirname($ENV{'SCRIPT_NAME'}),
}

if ( $Global{'DISPLAY_HOST'} eq '' )
{
    $Global{'DISPLAY_HOST'}= hostname();
};

if ( $Global{'DISPLAY'} eq '' )
{
    $Global{'DISPLAY'}= $Global{'DISPLAY_HOST'}.":" . $Global{'DISPLAY_NUMBER'};
};

if ( $Global{'web_server_port'} != 80 && $Global{'web_server_port'} ne ""  )
{
    $Global{'web_server'} .= ":" . $Global{'web_server_port'};
};

$Global{'LD_LIBRARY_PATH'} = "$Global{'ASCDS_INSTALL'}/lib" . ":" . "$ENV{'LD_LIBRARY_PATH'}" ;

