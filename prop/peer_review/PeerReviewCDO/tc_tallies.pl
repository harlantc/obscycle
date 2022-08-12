#!/usr/bin/perl
# tc_tallies.pl - This script produces the tc_tally files.  The .proposal
#                 file shows the tc_summary including grades and status
#                 per proposal grouped by panel.  The .sum file gives the 
#                 stats for approved proposals.  If the -panels flag is used
#                 an initial constraint summary file is created for each panel
#                 and a file is created with summary for all the panels.

use strict;
use Data::Dumper;
use DBI;
use vars qw(%param $VERSION);
$VERSION = '$Id: tc_tallies.pl,v 1.4 2010/06/20 17:25:21 wink Exp $';
#'

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

  # Database connection
  my $dsn = "dbi:Pg:dbname=$param{U}";
  my $dbh = DBI->connect($dsn, "", "");
  
  my $get_panels = $dbh->prepare(qq(select distinct panel_id from proposal 
	where panel_id != 98
				    order by panel_id));

  my $tc_query_r = $dbh->prepare(qq(select sum(rc_score_req)
				     from proposal where prop_status != 'B'
				     and panel_id = ?));
  my $tc_query_a = $dbh->prepare(qq(select sum(rc_score_app)
				     from proposal where prop_status = 'Y'
				     and panel_id = ?));

  my $tc_prop_query = $dbh->prepare(qq(select prop_id,last_name,prop_status,
					fg_avg,rc_score_req,rc_score_app 
					from proposal where 
					prop_status in ('Y', 'G')
				        and panel_id = ? 
					order by fg_avg desc));

  my $num_props_query = $dbh->prepare(qq(select count(*) from proposal where
					 panel_id = ? and 
					 prop_status != 'B'));
  my $num_props_querya = $dbh->prepare(qq(select count(*) from proposal where
					   prop_status = 'Y' and 
					   panel_id = ?));

  my $graded_query = $dbh->prepare(qq(select count(*) from proposal where 
                                        (g1 > 0.0 or g2 > 0.0 or g3 > 0.0 or 
                                         g4 > 0.0 or g5 > 0.0 or g6 > 0.0 or 
                                         g7 > 0.0 or g8 > 0.0 or g9 > 0.0 or 
                                         g10 > 0.0 or g11 > 0.0 or g12 > 0.0 
                                         or g13 > 0.0 or g14 > 0.0 or 
                                         g15 > 0.0 or infeasible='Y') 
			and panel_id = ? and 
				      prop_status != 'B'));
  
 # Open files and print headers 
  open(SUM, ">$param{out}_sum.txt") || 
      die "Sorry can't open $param{out}_sum.txt: $!\n";
  binmode(SUM, ":utf8");
  
  open(PROP, ">$param{out}_proposals.txt") || 
      die "Sorry can't open $param{out}_proposals.txt: $!\n";
  binmode(PROP, ":utf8");
  print "Output file: $param{out}_sum.txt\n";
  print "             $param{out}_proposals.txt\n";

  print SUM "Pan\t  RC_Req\t  RC_App\tProps_R\tProps_A\tUngraded\n";
  print SUM "---\t  ------\t  ------\t-------\t-------\t--------\n";
  print PROP "Pan\tProposal\tPI             \tStatus\tFGr_Avg\tRC_Req\tRC_App\n";
  print PROP "---\t--------\t---------------\t-------\t------\t-------\t------\n";

  if ($param{panels}) {
      open(PANELALL, ">$param{out}_panelAll.txt") ||
	die "Sorry can't open $param{out}_panelAll.txt: $!\n";
      binmode(PANELALL, ":utf8");
      print "             $param{out}_panelAll.txt\n";
      print PANELALL "Pan\tProposal\tPI             \t RC_Req\n";
      print PANELALL "---\t--------\t---------------\t ------\n";
  }
  $get_panels->execute();
  my ($sum_props_req, $sum_props_app,$sum_ungraded,$sum_score_req,$sum_score_app) = 0;
  while (my($panel_id) = $get_panels->fetchrow_array) {
    if ($param{panels}) {
      open(PANEL, ">$param{out}_panel$panel_id.txt") ||
	die "Sorry can't open $param{out}_panel$panel_id.txt: $!\n";
      print "             $param{out}_panel$panel_id.txt\n";
      binmode(PANEL, ":utf8");
      print PANEL "Pan\tProposal\tPI            \t RC_Req\n";
      print PANEL "---\t--------\t--------------\t ------\n";
    }

    # Only include BPP if requested
    next if ($panel_id == 99 and !$param{lp});
    
    # Get the requested totals
    my $rc_score_req = 0;
    $tc_query_r->execute($panel_id);
    while (my($rc_score) = $tc_query_r->fetchrow_array) {
      $rc_score_req += $rc_score;
      $sum_score_req += $rc_score;
    }
    $tc_query_r->finish;
    
    # Get the approved totals
    $tc_query_a->execute($panel_id);
    my $rc_score_app = 0;
    while (my($rc_score) = $tc_query_a->fetchrow_array) {
      $rc_score_app += $rc_score;
      $sum_score_app+=$rc_score;
    }
    $tc_query_a->finish;
    
    # Get the number of proposals and number of graded proposals
    $num_props_query->execute($panel_id);
    my $num_props_req =  $num_props_query->fetchrow_array;
    $num_props_query->finish();
    $sum_props_req+=$num_props_req;
    
    $num_props_querya->execute($panel_id);
    my $num_props_app =  $num_props_querya->fetchrow_array;
    $num_props_querya->finish();
    $sum_props_app+=$num_props_app;
    
    $graded_query->execute($panel_id);
    my $graded = $graded_query->fetchrow_array;
    $graded_query->finish();
    my $ungraded = $num_props_req - $graded;
    $sum_ungraded += $ungraded;
    printf SUM "$panel_id\t%9.2f\t%9.2f\t$num_props_req\t$num_props_app\t$ungraded\n",$rc_score_req,$rc_score_app;

    # Print the approved and gray proposals for the panel
    #print PROP "Panel $panel_id\n";
    #print PROP "--------\n";
    $tc_prop_query->execute($panel_id);
    while (my ($prop,$pi,$status,$fg_avg,$score_req,$score_app) = $tc_prop_query->fetchrow_array) {
      printf PROP ("%-3.3s\t%-8.8s\t%-15.15s\t%-6.6s\t%4.2f   \t%7.2f\t%7.2f\n",
        $panel_id,$prop,$pi,$status,$fg_avg,$score_req,$score_app);
       
      # Print the constrained proposals to the panel file
      if ($param{panels} and ($score_req )) {
        printf PANEL ("%-3.3s\t%-8.8s\t%-15.15s\t%7.2f\n",
           $panel_id,$prop,$pi,$score_req);
        printf PANELALL ("%-3.3s\t%-8.8s\t%-15.15s\t%7.2f\n",
           $panel_id,$prop,$pi,$score_req);
      }
    }
    $tc_prop_query->finish();
    print PROP "\n";
    print PANEL "\n";
    print PANELALL "\n";
    close(PANEL);
  }
  print SUM "---\t---------\t---------\t-------\t-------\t--------\n";
  printf SUM "Sum\t%9.2f\t%9.2f\t$sum_props_req\t$sum_props_app\t$sum_ungraded\n",$sum_score_req,$sum_score_app;

  close(SUM);
  close(PROP);
  close(PANELALL) if ($param{panels})
}

#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
            U => undef,
            out => undef,
            verbose => 0
           );

  GetOptions( \%param,
              "U=s",
              "out=s",
	      "lp",
	      "panels",
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

tc_tallies.pl [options]

=head1 OPTIONS

B<tc_tallies.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

database to read from

=item B<-out> filename

Output filename.  The script will generate two files -- output.sum and output.proposals.

=item B<-lp>

Include large project panel.

=item B<-panels>

Create a summary file of requested constraints for each panel

=item B<-help>

displays documentation for B<tc_tallies.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script tallies the time constraints for each panel.

=head1 AUTHOR

Sarah Blecksmith
