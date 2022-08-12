#!/usr/bin/perl

use strict;
use Data::Dumper;
use DBI;
use vars qw(%param $VERSION);
$VERSION = '$Id: joint.pl,v 1.6 2011/04/12 13:51:37 wink Exp $';
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
  
  my $joint_query_r = $dbh->prepare(qq(select sum(hst_req), 
                                      sum(xmm_req),
                                      sum(noao_req),
				      sum(nrao_req),
				      sum(spitzer_req),
				      sum(swift_req),
				      sum(nustar_req)
				      from proposal where prop_status != 'B' 
                                      and panel_id = ?));

  my $joint_query_a = $dbh->prepare(qq(select sum(hst_app), sum(xmm_app),
					sum(noao_app),
					sum(nrao_app),
					sum(spitzer_app),
					sum(swift_app),
					sum(nustar_app)
					from proposal where prop_status = 'Y' 
					and panel_id = ?));
  
  my $joint_props_query = $dbh->prepare(qq(select prop_id, last_name, 
					      prop_status, fg_avg, hst_req, 
					      hst_app, xmm_req, xmm_app,
					      noao_req,
					      noao_app, nrao_req, nrao_app,
					      spitzer_req, spitzer_app,
					      swift_req, swift_app,
					      nustar_req, nustar_app,
                                              total_req_time, total_app_time
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
  open(SUM, ">$param{out}.sum.txt") || 
    die "Sorry can't open $param{out}.sum.txt: $!\n";
  open(PROP, ">$param{out}.proposals.txt") || 
    die "Sorry can't open $param{out}.proposals.txt: $!\n";
  binmode(PROP, ":utf8");

  print  "Output file: $param{out}.sum.txt\n";
  print  "             $param{out}.proposals.txt\n";

  print SUM "Pan\t";
  print PROP "Pan\tProposal\tPI              \tstat\tfg_avg\ttime_r\ttime_a\t";
  my($jcnt) = 0;
  if ($param{all} || $param{hst}) {
    print SUM "hst_r\thst_a\t";
    print PROP "hst_r\thst_a\t";
    $jcnt++;
  }
  if ($param{all} || $param{xmm}) {
    print SUM "xmm_r\txmm_a\t";
    print PROP "xmm_r\txmm_a\t";
    $jcnt++;
  }
  if ($param{all} || $param{noao}) {
    print SUM "noao_r\tnoao_a\t";
    print PROP "noao_r\tnoao_a\t";
    $jcnt++;
  }
  if ($param{all} || $param{nrao}) {
    print SUM "nrao_r\tnrao_a\t";
    print PROP "nrao_r\tnrao_a\t";
    $jcnt++;
  }
  if ($param{all} || $param{spitzer}) {
    print SUM "sptz_r\tsptz_a\t";
    print PROP "sptz_r\tsptz_a\t";
    $jcnt++;
  }
  if ($param{all} || $param{swift}) {
    print SUM "swft_r\tswft_a\t";
    print PROP "swft_r\tswft_a\t";
    $jcnt++;
  }
  if ($param{all} || $param{nustar}) {
    print SUM "nustar_r\tnustar_a\t";
    print PROP "nustar_r\tnustar_a\t";
    $jcnt++;
  }
  print SUM "props_r\tprops_a\tUngraded\n";
  print PROP "\n";

  print SUM "---\t";
  print PROP "---\t--------\t----------------\t----\t------\t------\t------\t";
  for (my $xx=0;$xx<$jcnt;$xx++) {
    print SUM "------\t------\t";
    print PROP "------\t------\t";
  }
  print SUM "-------\t-------\t--------\n";
  print PROP "\n";


  $get_panels->execute();
  my ($sum_hst_r, $sum_hst_a, $sum_xmm_r, $sum_xmm_a, 
      $sum_noao_r, $sum_noao_a,
      $sum_nrao_r, $sum_nrao_a, 
      $sum_spitzer_r, $sum_spitzer_a, 
      $sum_swift_r, $sum_swift_a, 
      $sum_nustar_r, $sum_nustar_a, 
      $sum_props_r, $sum_props_a,
      $sum_ungraded) = 0;
  while (my($panel_id) = $get_panels->fetchrow_array) {
      # Only include BPP if requested
      next if ($panel_id == 99 && !$param{lp});

      # Get the requested totals
      my $s_hst_r = 0;
      my $s_xmm_r = 0;
      my $s_noao_r = 0;
      my $s_nrao_r = 0;
      my $s_spitzer_r = 0;
      my $s_swift_r = 0;
      my $s_nustar_r = 0;
      $joint_query_r->execute($panel_id);
      while (my ($s_hst, $s_xmm, $s_noao, $s_nrao,$s_spitzer,$s_swift,$s_nustar) = 
	$joint_query_r->fetchrow_array) {
	  $s_hst_r += $s_hst;
	  $s_xmm_r += $s_xmm;
	  $s_noao_r += $s_noao;
	  $s_nrao_r += $s_nrao;
	  $s_spitzer_r += $s_spitzer;
	  $s_swift_r += $s_swift;
	  $s_nustar_r += $s_nustar;
	  $sum_hst_r += $s_hst;
	  $sum_xmm_r += $s_xmm;
	  $sum_noao_r += $s_noao;
	  $sum_nrao_r += $s_nrao;
	  $sum_spitzer_r += $s_spitzer;
	  $sum_swift_r += $s_swift;
	  $sum_nustar_r += $s_nustar;
      }
      $joint_query_r->finish;
   
      # Get the approved totals
      my $s_hst_a = 0;
      my $s_xmm_a = 0;
      my $s_noao_a = 0;
      my $s_nrao_a = 0;
      my $s_spitzer_a = 0;
      my $s_swift_a = 0;
      my $s_nustar_a = 0;
      $joint_query_a->execute($panel_id);
      while (my ($s_hst, $s_xmm, $s_noao, $s_nrao, $s_spitzer,
		 $s_swift,$s_nustar) = $joint_query_a->fetchrow_array) {
	  $s_hst_a += $s_hst;
	  $s_xmm_a += $s_xmm;
	  $s_noao_a += $s_noao;
	  $s_nrao_a += $s_nrao;
	  $s_spitzer_a += $s_spitzer;
	  $s_swift_a += $s_swift;
	  $s_nustar_a += $s_nustar;
	  $sum_hst_a += $s_hst;
	  $sum_xmm_a += $s_xmm;
	  $sum_noao_a += $s_noao;
	  $sum_nrao_a += $s_nrao;
	  $sum_spitzer_a += $s_spitzer;
	  $sum_swift_a += $s_swift;
	  $sum_nustar_a += $s_nustar;
      }
      $joint_query_a->finish;
   
      # Get the number of proposals and number of graded proposals
      $num_props_query->execute($panel_id);
      my $num_props_req =  $num_props_query->fetchrow_array;
      $num_props_query->finish();
      $sum_props_r+=$num_props_req;
     
      $num_props_querya->execute($panel_id);
      my $num_props_app =  $num_props_querya->fetchrow_array;
      $num_props_querya->finish();
      $sum_props_a+=$num_props_app;

     $graded_query->execute($panel_id);
      my $graded = $graded_query->fetchrow_array;
      $graded_query->finish();
      my $ungraded = $num_props_req - $graded;
      $sum_ungraded+=$ungraded;


    print SUM "$panel_id\t";
    if ($param{all} || $param{hst}) {
      print SUM "$s_hst_r\t$s_hst_a\t";
    }
    if ($param{all} || $param{xmm}) {
      print SUM "$s_xmm_r\t$s_xmm_a\t";
    }
    if ($param{all} || $param{noao}) {
      print SUM "$s_noao_r\t$s_noao_a\t";
    }
    if ($param{all} || $param{nrao}) {
      print SUM "$s_nrao_r\t$s_nrao_a\t";
    }
    if ($param{all} || $param{spitzer}) {
      print SUM "$s_spitzer_r\t$s_spitzer_a\t";
    }
    if ($param{all} || $param{swift}) {
      print SUM "$s_swift_r\t$s_swift_a\t";
    }
    if ($param{all} || $param{nustar}) {
      print SUM "$s_nustar_r\t$s_nustar_a\t";
    }
    print SUM "$num_props_req\t$num_props_app\t$ungraded\n";

      # Print the approved and gray proposals for the panel
      #print PROP "Panel $panel_id\n";
      #print PROP "========\n";
      $joint_props_query->execute($panel_id);
      while (my ($prop, $pi, $status, $fg_avg, $hst_r, $hst_a,
		 $xmm_r, $xmm_a, $noao_r, $noao_a,
		 $nrao_r,$nrao_a, $spitzer_r, $spitzer_a, 
		 $swift_r, $swift_a, 
		 $nustar_r, $nustar_a, 
		 $time_req, $time_app) = $joint_props_query->fetchrow_array){
	  
	  my @fixed_vals;
          my @values;
	  my @duhvalues = ($hst_r, $hst_a, $xmm_r, $xmm_a ,
			$noao_r, $noao_a, $nrao_r, $nrao_a, 
			$spitzer_r, $spitzer_a,
			$swift_r, $swift_a,
			$nustar_r, $nustar_a);
    if ($param{all} || $param{hst}) {
      push(@values,$hst_r);
      push(@values,$hst_a);
    }
    if ($param{all} || $param{xmm}) {
      push(@values,$xmm_r);
      push(@values,$xmm_a);
    }
    if ($param{all} || $param{noao}) {
      push(@values,$noao_r);
      push(@values,$noao_a);
    }
    if ($param{all} || $param{nrao}) {
      push(@values,$nrao_r);
      push(@values,$nrao_a);
    }
    if ($param{all} || $param{spitzer}) {
      push(@values,$spitzer_r);
      push(@values,$spitzer_a);
    }
    if ($param{all} || $param{swift}) {
      push(@values,$swift_r);
      push(@values,$swift_a);
    }
    if ($param{all} || $param{nustar}) {
      push(@values,$nustar_r);
      push(@values,$nustar_a);
    }
	  foreach my $val (@values){
	      if ($val eq ""){
		  $val = "*";
	      }
	      push @fixed_vals, $val;
	  }

         print PROP "$panel_id\t$prop\t$pi\t$status\t$fg_avg\t$time_req\t$time_app\t"; 
         foreach my $st (@fixed_vals) {
              print PROP "$st\t";
         }
         print PROP "\n";
 
    }
      $joint_props_query->finish();
      print PROP "\n";  
  }

  print SUM "---\t";
  for (my $xx=0;$xx<$jcnt;$xx++) {
    print SUM "------\t------\t"
  }
  print SUM "-------\t-------\t--------\n";
  print SUM "Sum\t";
    if ($param{all} || $param{hst}) {
      print SUM "$sum_hst_r\t$sum_hst_a\t";
    }
    if ($param{all} || $param{xmm}) {
      print SUM "$sum_xmm_r\t$sum_xmm_a\t";
    }
    if ($param{all} || $param{noao}) {
      print SUM "$sum_noao_r\t$sum_noao_a\t";
    }
    if ($param{all} || $param{nrao}) {
      print SUM "$sum_nrao_r\t$sum_nrao_a\t";
    }
    if ($param{all} || $param{spitzer}) {
      print SUM "$sum_spitzer_r\t$sum_spitzer_a\t";
    }
    if ($param{all} || $param{swift}) {
      print SUM "$sum_swift_r\t$sum_swift_a\t";
    }
    if ($param{all} || $param{nustar}) {
      print SUM "$sum_nustar_r\t$sum_nustar_a\t";
    }
    print SUM "$sum_props_r\t$sum_props_a\t$sum_ungraded\n";

    close(SUM);
    close(PROP);

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
              "all",
              "hst",
              "noao",
              "xmm",
              "nrao",
              "spitzer",
              "swift",
              "nustar",
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
  my @jmsn = ("hst","xmm","nrao","noao","spitzer","swift","nustar");
  my $jcnt = 0;
  foreach my $str (@jmsn) {
    if (defined $param{$str} ) {
       $jcnt++;
    }
  }
  if ($jcnt == 0) {
    $param{all} = 1;
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

joint.pl [options]

=head1 OPTIONS

B<joint.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

database to read from

=item B<-out> filename

Output filename.  The script generates output.sum and output.proposals.

=item B<-lp>

Include the large project panel.

=item B<-all>

flag indicating ALL joints should be included in stats

=item B<-hst>

flag indicating hst should be included in stats

=item B<-noao>

flag indicating noao should be included in stats

=item B<-xmm>

flag indicating xmm should be included in stats

=item B<-nrao>

flag indicating nrao should be included in stats

=item B<-spitzer>

flag indicating spitzer should be included in stats

=item B<-swift>

flag indicating swift should be included in stats

=item B<-nustar>

flag indicating nustar should be included in stats

=item B<-help>

displays documentation for B<joint.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script tallies the joint totals for each panel.

=head1 AUTHOR

Sarah Blecksmith
