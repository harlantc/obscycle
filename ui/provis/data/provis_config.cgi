# _INSERT_SAO_COPYRIGHT_HERE_(2008)_
# _INSERT_GPL_LICENSE_HERE_


use Sys::Hostname;
use File::Basename;

if ($ENV{"ASCDS_INSTALL"}) {
  $PROVIS_RELEASE_DIR=$ENV{"ASCDS_INSTALL"};
} else {
  $PROVIS_RELEASE_DIR="/home/ascds/DS.release/";
}

if ($ENV{"WEB_BIN"}) {
  $webdir= $ENV{"WEB_BIN"} . "/provis" ;
} else {
  $webdir= $PROVIS_RELEASE_DIR . "/bin" ;
}


%Global = (
	   'maxclients'       , '2',
	   'timeout'          , '1',
	   'retries'          , '30',
	   'xres'             , '90',
	   'yres'             , '90',
	   'fileexptime_s'    , '600',
	   'webbin'           , "$webdir",
	   'wget'             , "$PROVIS_RELEASE_DIR/ots/bin/wget",
	   'wish'             , "${PROVIS_RELEASE_DIR}/ots/bin/wize",
	   'simbadURL'        , 'http://simbad.harvard.edu/simbad/sim-script?submit=submit+script&script=output+console%3Doff+script%3Doff%0D%0Aformat+object+form1+%22%25COO%28s2%3BC%3BFK5%3BJ2000%29%22%0D%0Aquery+id+TARGETNAME%0D%0A',
	   'simbadURL_Failover', 'http://simbad.u-strasbg.fr/simbad/sim-script?submit=submit+script&script=output+console%3Doff+script%3Doff%0D%0Aformat+object+form1+%22%25COO%28s2%3BC%3BFK5%3BJ2000%29%22%0D%0Aquery+id+TARGETNAME%0D%0A',
	   'DISPLAY_HOST'     , '',
	   'DISPLAY'          , '',
	   'ASCDS_INSTALL'    , "${PROVIS_RELEASE_DIR}",
	   'EPHEMERIS_FILE'   , "${PROVIS_RELEASE_DIR}/data/provis.ephemeris.dat",
	   'EPHEMERIS_FILE2'   , "/data/rpc_dev/provis/provis.ephemeris.dat",
	   'LIBASTROCAL_FILE' , "${PROVIS_RELEASE_DIR}/data/astro.cal",
	   'LIBASTROCAL_FILE2' , "/data/rpc_dev/provis/astro.cal",
	   'LD_LIBRARY_PATH'  , '' ,
	   'FILL_PATTERNS_DIR', "${PROVIS_RELEASE_DIR}/data",
	   'gs'               , "${PROVIS_RELEASE_DIR}/ots/bin/gs" ,
	   'web_server_port'  , "$ENV{'SERVER_PORT'}",
	   'web_server'       , "http://$ENV{'SERVER_NAME'}",
           'htdocsdir'        , '/soft/provis/',
	   'cgibindir'        , '',
           'uploaddir'        , '/tmp/provis/www_zip/uploads/',
           'allowedips'       , '131.142.184.*a   131.142.198.*a 131.142.185.---a *a.*a.*a.*a'
	   );


# Linux definition
  $Global{'XVFB'}           = '/usr/bin/Xvfb';
  $Global{'XVFB_LIB'}       = '';
  $Global{'XVFB_OPTIONS'}   = '-screen 0 1280x1024x16 -dpi 90 -fbdir /tmp/.X11-provis -pn 2>/dev/null';
  $Global{'XVFB_PROC'}      = 'Xvfb';
  $Global{'DISPLAY_NUMBER'} = '1';
  $Global{'gs1'}            = '/usr/bin/gs';

# OLD SOLARIS
  #$Global{'XVFB'}           =  '/usr/openwin/bin/Xvfb';
  #$Global{'XVFB_LIB'}       = '';
  #$Global{'XVFB_OPTIONS'}   = '-dev vfb screen 0 1280x1024x16 dpix 90 dpiy 90 fbdir /tmp/.X11-provis -pn',
  #$Global{'XVFB_PROC'}      = 'Xsun';
  #$Global{'DISPLAY_NUMBER'} = '0';
  #$Global{'gs1'}            = '/usr/local/bin/gs';

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
#    $Global{'DISPLAY'}= $Global{'DISPLAY_HOST'}.":" . $Global{'DISPLAY_NUMBER'};
    $Global{'DISPLAY'}= ":" . $Global{'DISPLAY_NUMBER'};
};

if ( $Global{'web_server_port'} != 80 && $Global{'web_server_port'} ne ""  )
{
    $Global{'web_server'} .= ":" . $Global{'web_server_port'};
};

$ENV{'FONTCONFIG_FILE'} = "/etc/fonts/fonts.conf";
$Global{'LD_LIBRARY_PATH'} = "$Global{'ASCDS_INSTALL'}/lib";
$Global{'LD_LIBRARY_PATH'} .= ":" ."$Global{'ASCDS_INSTALL'}/ots/lib";
$Global{'PATH'} = "$Global{'ASCDS_INSTALL'}/ots/bin";

1;
