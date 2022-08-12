#!/usr/bin/perl

#******************************************************************************
# pop_conflicts.pl
#
# This script creates the sql to populate the conflicts table using a bcp
# of Diane's conflict table and set the conflict flag in the proposal table.
# This script needs to be run against the GUI database with all panels loaded
# in order for the conflicts table to be fully populated.
#******************************************************************************

use strict;
use Data::Dumper;
use DBI;

use vars qw(%param $VERSION);
$VERSION = '$Id: pop_conflicts.pl,v 1.9 2012/02/14 15:52:53 wink Exp $';
{
  use Getopt::Long;
  parse_opts();
  
  if ($param{version}) {
    print $VERSION, "\n";
    exit( 0 );
  }
  
  if ($param{help}) { 
    usage(0);
  }
  # Database connection 1
  my $dsn = "dbi:Pg:dbname=$param{U}";
  my $dbh = DBI->connect($dsn, "", "", {
					PrintError => 1,
					RaiseError => 0});

  my $get_prop = $dbh->prepare(qq(select distinct targ_name, targ_num,last_name, detector, type, req_time from proposal p, target t where p.prop_id = t.prop_id and p.panel_id = t.panel_id and p.prop_id = ? and targ_id = ?));


  open IN, "$param{in}" ||
    die "Sorry, can't open $param{in}: $!\n";
  open OUT, ">$param{out}" ||
    die "Sorry, can't open $param{out}: $!\n";
  binmode(OUT, ":utf8");
  print OUT "delete from conflicts;\n";
  foreach my $line (<IN>) {
    chomp $line;
    my ($prop_id, $targ_id, $conflict_propid, $conflict_targ_id,
	$conflict_type, $conflict_sep
       ) = split /\|/, $line;
    $get_prop->execute($prop_id, $targ_id);
    my ($targ_name, $targ_num, $pi, $detector, $type, 
	$exptime) = $get_prop->fetchrow_array;
    $get_prop->finish;
    $get_prop->execute($conflict_propid, $conflict_targ_id);
    my ($conflict_targname, $conflict_targnum,$conflict_pi, 
	$conflict_detector, $conflict_proposal_type, 
	$conflict_exptime) = $get_prop->fetchrow_array;
    $get_prop->finish;

    $targ_name = enter_string($targ_name);
    $pi = enter_string($pi);
    $detector = enter_string($detector);
    $type = enter_string($type);
    $conflict_detector = enter_string($conflict_detector);
    $conflict_type = enter_string($conflict_type);
    $conflict_sep = enter_string($conflict_sep);

    if ($targ_num > 0 && $conflict_targnum > 0) {
        my $insert = qq/insert into conflicts values ($prop_id, $targ_id, $targ_num, $targ_name, $conflict_propid, $conflict_targ_id,$conflict_targnum, $conflict_exptime, $conflict_detector, $conflict_type, $conflict_sep, E$pi, $exptime, $detector, $type );\n/;
      
        print OUT $insert;
    }
  }
  
  print OUT qq(update proposal set conflict = 'Y' where prop_id in (select prop_id from conflicts);\n);
  close IN;
  close OUT;
}

sub enter_string {
  my $string = shift;
  $string =~ s/^\s+//;
  $string =~ s/\s+$//;
  
  $string =~ s/'/''/g;
  $string = qq('$string') if $string;
  $string = 'null' if !$string;
  return $string;
}

#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts {
  
  %param = (
	    U => undef,
	    in => undef,
            out => undef,
            verbose => 0
           );
  
  GetOptions( \%param,
	      "U:s",
              "in:s",
              "out:s",
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

#******************************************************************************
# Subroutine for usage statements
#******************************************************************************
sub usage {
  my ( $exit ) = @_;

  local $^W = 0;
  require Pod::Text;
  Pod::Text::pod2text( '-75', $0 );
  exit $exit;
}

__END__

=head1 USAGE

pop_conflicts.pl [options]

=head1 OPTIONS

B<pop_conflicts.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

User name

=item B<-in>

name of input file

=item B<-out>

name of output file

=item B<-help>

displays documentation for B<pop_conflicts.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script creates the sql to populate the conflicts table using a bcp
of Diane's conflict table.  This script needs to be run against the GUI 
database with all panels loaded in order for the conflicts table to be fully
populated.

=head1 AUTHOR

Sherry L. Winkelman
