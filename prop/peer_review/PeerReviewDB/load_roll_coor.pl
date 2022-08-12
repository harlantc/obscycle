#!/usr/bin/perl

#*****************************************************************************
# load_roll_coor.pl
#
# This script loads the roll,coor, or constraint in remarks data into the
# target table.
#
# The coor type should not be needed any longer since this is populated from
# the proposal database as of cycle10.
#
# The remarks type is no longer used in this script.  An sql script is created
# for the panel as updates are specified by CDO
#*****************************************************************************


use strict;                      
use DBI;

use vars qw($password %param $VERSION);

$VERSION = '$Id: load_roll_coor.pl,v 1.7 2012/02/14 14:36:18 wink Exp $';
#'

# Get the flags/values from the commandline and store in a hash
use Getopt::Long;           
parse_opts();

# Handle some of the commandline flags
if ($param{version}) {
  print $VERSION, "\n";
  exit( 0 );
}
    
if ($param{help}) { usage(0);}

# Database connection 1
my $dsn1 = "dbi:Pg:dbname=$param{database}";
my $dbh1 = DBI->connect($dsn1, "", "");

my $panel_id = $param{panel};
  
open(IN, "$param{input}") || die "Can't open $param{input}: $!\n";

if ($param{tctype} eq 'roll') {
  while (defined(my $line = <IN>)){
    my ($targ_id, $ordr, $roll_grade) = split /\t/, $line;
    my $update_roll_grade = $dbh1->prepare(qq(update rollreq set tc_roll = ? 
                                              where targ_id = ? 
					      and ordr = ? and panel_id = ?));
    $update_roll_grade->execute($roll_grade, $targ_id, $ordr, $panel_id);
    $update_roll_grade->finish;
  }
} 
elsif ($param{tctype} eq 'coor'){
  while (defined(my $line = <IN>)){
    my ($prop_id, $targ_id, $coor_grade) = split /\t/, $line;
    my $update_coor_grade = $dbh1->prepare(qq(update target set tc_coor = ? 
                                              where targ_id = ? 
					      and panel_id = ?));
    $update_coor_grade->execute($coor_grade, $targ_id, $panel_id);
    $update_coor_grade->finish;
  }
}
elsif ($param{tctype} eq 'remarks'){
  while (defined(my $line = <IN>)){
    my ($prop_id, $targ_id, $const_grade) = split /\t/, $line;
    my $update_const_grade = $dbh1->prepare(qq(update target set 
                                               tc_const_rem = ? 
                                               where targ_id = ? 
					       and panel_id = ?));
    $update_const_grade->execute($const_grade, $targ_id, $panel_id);
    $update_const_grade->finish;
  }
}
else {
  print "Dont' know how to handle that constraint type\n";
}


#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (database=>undef,
	    panel=>undef,
            tctype=>undef,
	    input=>undef,
            verbose => 0
           );

  GetOptions( \%param,
              "database=s",
	      "panel=s",
              "tctype=s",
	      "input=s",
              "verbose",
              "version",
              "help"
            ) or exit(1);

  return if $param{help} or $param{version};


  my $err = 0;
  while ( my ( $par, $val ) = each ( %param ) )
  {
    next if defined $val;
    warn("parameter `$par' not set\n");
    $err++;
  }

  exit(1) if $err;

}
#****************************************************************************
# Subroutine for usage statements
#****************************************************************************
sub usage
{
  my ( $exit ) = @_;

  local $^W = 0;
  require Pod::Text;
  Pod::Text::pod2text( '-75', $0 );
  exit $exit;
}

__END__

=head1 USAGE

load_roll_coor.pl [options]

=head1 OPTIONS

B<load_roll_coor.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-database>

database name

=item B<-panel>

The number of the panel you are using

=item B<-tctype>

Which type of constraint you are loading. (roll, coor, remarks)

=item B<-input>

Name of file containing roll or coor data

=item B<-help>

displays documentation for B<load_roll_coor.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script loads the roll,coor, or constraint in remarks data into the target
table.  

The coor options should not be needed any longer since this is populated from 
the proposal database as of cycle10.

=head1 AUTHOR

Sarah Blecksmith
