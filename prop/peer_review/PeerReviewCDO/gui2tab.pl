#!/usr/bin/perl

#*****************************************************************************
# gui2tab.pl
#
# This script prints out peer review info in tab-delimited format for use by 
# Antonella or anyone having 'speed' issues with the gui 
#*****************************************************************************

use strict;
use Data::Dumper;
use DBI;
use vars qw(%param $VERSION $dbh1);

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


    # Database connection 1
    my $dsn1 = "dbi:Pg:dbname=$param{U}";
    $dbh1 = DBI->connect($dsn1, "", "");


    open (OUT, ">$param{out}") ||
	die "Sorry can't open $param{out}: $!\n";
    binmode(OUT, ":utf8");

    printf OUT 
	"Panel" . $param{delim} .
	"Comments" .$param{delim}.
	"Prop" . $param{delim} .
	"PI" . $param{delim} .
	"Type" . $param{delim} .
	"LP/XVP" . $param{delim} .
	"Status" . $param{delim} .
	"Rank" . $param{delim}.
	"FG-Avg" . $param{delim}.
	"FG-Norm" . $param{delim}.
	"Joint" . $param{delim} .
	"Multi Cycle" . $param{delim} .
	"Conflict" . $param{delim} .
	"Time Crit " . $param{delim} .
	"Rev TOO " . $param{delim} .
	"Triage" . $param{delim} .
	"ReqTime" . $param{delim} .
	"AppTime" . $param{delim} .
	"ReqTime+1" . $param{delim} .
	"AppTime+1" . $param{delim} .
	"ReqTime+2" . $param{delim} .
	"AppTime+2" . $param{delim} .
	"ReqHEL" . $param{delim} .
	"AppHEL" . $param{delim} .
	"ReqHEL+1" . $param{delim} .
	"AppHEL+1" . $param{delim} .
	"ReqHEL+2" . $param{delim} .
	"AppHEL+2" . $param{delim} .
	"ReqTgt" . $param{delim} .
	"AppTgt" . $param{delim} .
	"ReqTgt+1" . $param{delim} .
	"AppTgt+1" . $param{delim} .
	"ReqTgt+2" . $param{delim} .
	"AppTgt+2" . $param{delim} .
	"RC" . $param{delim}.
	"FG-Med" . $param{delim}.
	"FG-Stdev" . $param{delim}.
	"PG-Avg" . $param{delim}.
	"PG-Med" . $param{delim}.
	"PG-Stdev" . $param{delim}.
	"Title" . $param{delim}.
        "\n";
    print OUT "$param{delim}$param{delim}$param{delim}".
	"Tno". "$param{delim}" . 
	"Target Name". "$param{delim}".
	"Detector". "$param{delim}".
	"Grating". "$param{delim}".
	"RC". "$param{delim}".
	"Time Crit". "$param{delim}".
	"Rev TOO". "$param{delim}".
	"Grid". "$param{delim}".
         " " . "$param{delim}" .	
	"Status". "$param{delim}".
	"Tax". "$param{delim}".
	"ReqTime". "$param{delim}".
	"AppTime". "$param{delim}".
	"ReqTime+1". "$param{delim}".
	"AppTime+1". "$param{delim}".
	"ReqTime+2". "$param{delim}".
	"AppTime+2". "$param{delim}".
	"ReqHEL". "$param{delim}".
	"AppHEL". "$param{delim}".
	"ReqHEL+1". "$param{delim}".
	"AppHEL+1". "$param{delim}".
	"ReqHEL+2". "$param{delim}".
	"AppHEL+2". "$param{delim}".
	"ObsReq". "$param{delim}".
	"ObsApp". "$param{delim}".
	"ObsReq+1". "$param{delim}".
	"ObsApp+1". "$param{delim}".
	"ObsReq+2". "$param{delim}".
	"ObsApp+2". "\n\n";

   my($bpp) = "and big_proj not like '%P'";
   if ($param{bpp} ) {
      $bpp = "";
   }

    my $get_props = $dbh1->prepare(qq(select 
	panel_id , 
	comments,
	prop_id, last_name,type,big_proj,
	prop_status,rank,
	fg_avg,fg_norm,
	joint_flag,
	mcop, conflict,
	tc_prop, rev_too, 
	triage,
	total_req_time,total_app_time,
	total_req_time_1,total_app_time_1,
	total_req_time_2,total_app_time_2,
	total_req_hel,total_app_hel,
	total_req_hel_1,total_app_hel_1,
	total_req_hel_2,total_app_hel_2,
	num_targ_req,num_targ_app,
	num_targ_req_1,num_targ_app_1,
	num_targ_req_2,num_targ_app_2,
	rc_score_app,
	fg_med,fg_stdev,
	pg_avg,pg_med,pg_stdev,
	title
        from proposal
	where panel_id = $param{panel}
	$bpp
	order by rank ASC,prop_id));

    $get_props->execute();
    my $print;
    my @row;
    while (@row = $get_props->fetchrow_array) {
        while ($row[1] =~ s/	/  /g) {;}
        while ($row[1] =~ s/\n/  /g) {;}
        while ($row[1] =~ s/\t/    /g) {;}
        $row[1] = qq("$row[1]");
        $row[3] = qq("$row[3]");
        $row[$#row] = qq("$row[$#row]");
        #if ($row[1] == "") { $row[1] = " " };
	$print = join($param{delim},@row);
        while ($print =~ s/N\/A/ /) {;}
        printf OUT "$print\n";
        process_targets($row[2]);
    }
    $get_props->finish;

    close OUT;
}

sub process_targets
{
  my($pid) = @_;

  # Database connection 2
  my $dsn2 = "dbi:Pg:dbname=$param{U}";
  my $dbh2 = DBI->connect($dsn2, "", "");

  my $get_tgts = $dbh1->prepare(qq(select 
	null,null,prop_id,
	targ_num,targ_name,
	detector,grating,
	rc_score,time_crit,
	rev_too,raster_scan,null,
	targ_status,
	tax,
	req_time,app_time,
	req_time_1,app_time_1,
	req_time_2,app_time_2,
	req_hel,app_hel,
	req_hel_1,app_hel_1,
	req_hel_2,app_hel_2,
	num_obs_req,num_obs_app,
	num_obs_req_1,num_obs_app_1,
	num_obs_req_2,num_obs_app_2
        from target where panel_id = $param{panel}
	and prop_id = $pid));

    $get_tgts->execute();
    my $print;
    my @row;
    while (@row = $get_tgts->fetchrow_array) {
        $row[4] = qq("$row[4]");
	$print = join($param{delim},@row);
        printf OUT "$print\n";
    }
    $get_tgts->finish;

}

#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
	    U => undef,
	    out => undef,
	    panel => undef,
            delim => ",",
            verbose => 0
           );

  GetOptions( \%param,
	      "U=s",
	      "out=s",
	      "panel=s",
	      "delim=s",
	      "bpp",
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

gui2tab.pl [options]

=head1 OPTIONS

B<gui2tab.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

database to read

=item B<-out> filename

output filename

=item B<-panel>

panel to dump 

=item B<-help>

displays documentation for B<gui2tab.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script reads the panel data from the peer review database
and writes tab-delimited format.

=head1 AUTHOR

Diane Hall
