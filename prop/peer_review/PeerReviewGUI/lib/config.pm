# Config.pm - 
#

package config;
use strict;

use Exporter;
our @ISA='Exporter';
our @EXPORT = qw($AO $MIN_TRIAGE_PG $STAT_YES $STAT_NO $STAT_GRAY $STAT_BPP $num_grades $num_pgrades $isXVP $isVLP $xvp_vlp_lbl $MAX_TGTS);
our $AO = 24;
our $MIN_TRIAGE_PG = 4;
our $STAT_YES ='Y';
our $STAT_NO  ='N';
our $STAT_GRAY  ='G';
our $STAT_BPP  ='B';
our $isXVP = 0;
our $isVLP = 1;
our $xvp_vlp_lbl = "VLP";
our $MAX_TGTS=100;

our $num_pgrades = 11;
our $num_grades = 11;
