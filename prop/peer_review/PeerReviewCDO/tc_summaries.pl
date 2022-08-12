#!/usr/bin/perl

use strict;
use Data::Dumper;
use DBI;
use vars qw(%param $VERSION);
$VERSION = '$Id: tc_summaries.pl';
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
				    order by panel_id));
  my $tc_sum_query = $dbh->prepare(qq(select t.panel_id, t.prop_id, last_name, targ_num, targ_name, t.num_obs_req,t.num_pnt_req, rc_score from target t, proposal p where t.prop_id = p.prop_id and t.panel_id = p.panel_id and time_crit = 'Y' and t.panel_id = ? order by t.prop_id, targ_id));
  

  # Open files and print headers
  open(ALL, ">$param{out}_all.txt") || 
    die "Sorry can't open $param{out}_all.txt: $!\n";
  binmode(ALL, ":utf8");

  if ($param{anonymous}) {
    print ALL "Panel\tProposal\tTargNum\tTarget\tNumObsReq\tRC\n";
  } else {
    print ALL "Panel\tProposal\tPI\tTargNum\tTarget\tNumObsReq\tRC\n";
  }

  $get_panels->execute();
  while (my($panel_id) = $get_panels->fetchrow_array) {
    my($pid) = sprintf("%02d",$panel_id);
    open(PAN, ">$param{out}_panel$pid.txt") || 
      die "Sorry can't open $param{out}_panel$pid.txt: $!\n";
    binmode(PAN, ":utf8");
    if ($param{anonymous}) {
      print PAN "Panel\tProposal\tTargNum\tTarget\tNumObsReq\tRC\n";
    } else {
      print PAN "Panel\tProposal\tPI\tTargNum\tTarget\tNumObsReq\tRC\n";
    }

    # Get the requested totals
    $tc_sum_query->execute($panel_id);
    while (my ($panid,$propid,$last,$targid,$targname,$numobs,$numpnt,$rc_score) =
	$tc_sum_query->fetchrow_array) {
       $last = qq("$last");
       $targname = qq("$targname");

       if ($param{anonymous}) {
         printf ALL ("%s\t%s\t%s\t%s\t%s\t%s\n",
           $panid,$propid,$targid,$targname,$numobs,$rc_score);
         printf PAN ("%s\t%s\t%s\t%s\t%s\t%s\n",
           $panid,$propid,$targid,$targname,$numobs,$rc_score);
        } else {
         printf ALL ("%s\t%s\t%s\t%s\t%s\t%s\t%s\n",
           $panid,$propid,$last,$targid,$targname,$numobs,$rc_score);
         printf PAN ("%s\t%s\t%s\t%s\t%s\t%s\t%s\n",
           $panid,$propid,$last,$targid,$targname,$numobs,$rc_score);
        }
      }
      close PAN;
      $tc_sum_query->finish;
  }
   
  close(ALL);

}

#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
            U => undef,
            out => undef,
            verbose => 0,
            anonymous => 0
           );

  GetOptions( \%param,
              "U=s",
              "out=s",
              "verbose",
              "version",
              "anonymous",
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

tc_summaries.pl [options]

=head1 OPTIONS

B<tc_summaries.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

database to read from

=item B<-out> filename

Output filename.  The script generates output.sum and output.proposals.

=item B<-anonymous>

anonymous review, hide the PI name

=item B<-help>

displays documentation for B<tc_summaries.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script retrieves all time critical proposals .

=head1 AUTHOR

me
