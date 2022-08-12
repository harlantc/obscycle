#!/usr/bin/perl 

#******************************************************************************
# manual_triage.pl
#
# This script calls the initial triage for the given panel
#******************************************************************************

use strict;
use DBI;
use FindBin qw ($Bin);
use lib "$Bin/../PeerReviewGUI/lib";
use Panel;
use config;
use Data::Dumper;
use vars qw($pwdProp %param $VERSION $panel );

$VERSION = '$Id: manual_triage.pl,v 1.34 2016/06/23  Exp $';

{
  use Getopt::Long;
  parse_opts();
  
  if ($param{version})
    {
      print $VERSION, "\n";
      exit( 0 );
    }
  
  if ($param{help}) 
    { 
      usage(0);
    }
  
#  DBI->trace(1);
  # Database connection 1
  my $dsn1 = "dbi:Pg:dbname=$param{U}";
  my $dbh1 = DBI->connect($dsn1, "", "", {
					  PrintError => 1,
					  RaiseError => 0});
  
  # Load the panel
  $panel = new Panel($dbh1, $param{panel}, 'N', $param{verbose});
  
  triagePanel();

}
sub triagePanel {
  $panel->set_sort('lcd', '', 'pg_avg', 'PGradeAvg');
  my %gslist = $panel->get_group_list($panel->lcd_groupby, $panel->lcd_sortby,
                                      $panel->hide_triage,
                                      $panel->hide_arc_the,
                                      $panel->hide_lp_vlp);

  # The number of targets to turn off is not simply num_targs because of
  # the possibility of alternate targets

  foreach my $group (sort keys %gslist) {
    my $sorted_list = $gslist{$group};
    my %sorted_list = %$sorted_list;

    my $numProps = scalar keys %sorted_list;
    my $cut_offNum = int(.25 * $numProps);
    my $cut_offProp = $sorted_list{$cut_offNum};
    my $cut_offGrade = $panel->proposals->proposal($cut_offProp)->pg_avg;

    foreach my $prop_order (sort {$a <=> $b} keys %$sorted_list) {
      my $prop_id = $sorted_list{$prop_order};
      my $numGrades = $panel->proposals->proposal($prop_id)->num_pg();
      if ($panel->proposals->proposal($prop_id)->pg_avg <= $cut_offGrade &&
          $panel->proposals->proposal($prop_id)->prop_status !~ /$STAT_BPP/) {
        if ($numGrades >= $MIN_TRIAGE_PG) {
          $panel->proposals->proposal($prop_id)->save_member('prop_status',
                                                            'N');

          $panel->proposals->proposal($prop_id)->triage('Y');
          $panel->proposals->proposal($prop_id)->save_member('triage_sort', 2);

          $panel->proposals->proposal($prop_id)->save_member('g1',
              $panel->proposals->proposal($prop_id)->pg_avg);
          $panel->proposals->proposal($prop_id)->save_member('fg_avg',
              $panel->proposals->proposal($prop_id)->pg_avg);
          $panel->proposals->proposal($prop_id)->save2database();
        }  else {
          print STDERR "$prop_id falls below triage line but only $numGrades preliminary grades submitted\n";
        }
      }
    }
  }
  $panel->set_grades();
  $panel->update_stats();
  $panel->set_running_totals();
}



#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
	    U => undef,
	    panel => undef,
            verbose => 0
           );

  GetOptions( \%param,
	      "U=s",
	      "panel=i",
              "verbose=i",
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

manual_triage.pl [options]

=head1 OPTIONS

B<manual_triage.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

User name on postgresql server

=item B<-panel>

Panel to run triage on

=item B<-help>

displays documentation for B<manual_triage.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script manually triages a panel. 

=head1 AUTHOR

Diane Hall
