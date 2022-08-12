#!/usr/bin/perl

#*****************************************************************************
# megalist.pl
#
# This script prints out peer review info in tab-delimited format 
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
	"Prop" . $param{delim} .
	"Status" . $param{delim} .
	"Rank" . $param{delim}.
	"FG-Avg" . $param{delim}.
	"FG-Norm" . $param{delim}.
	"PI" . $param{delim} .
	"Type" . $param{delim} .
	"Joint" . $param{delim} .
	"Multi Cycle" . $param{delim} .
	"Conflict" . $param{delim} .
	"ReqTime" . $param{delim} .
	"AppTime" . $param{delim} .
	"ReqTime+1" . $param{delim} .
	"AppTime+1" . $param{delim} .
	"ReqTime+2" . $param{delim} .
	"AppTime+2" . $param{delim} .
        "\n";

    my $bpp=" and panel_id < 90 ";
    if (defined $param{bpp}) {  $bpp=""; }
    my $get_props = $dbh1->prepare(qq(select 
	panel_id , 
	prop_id, 
	prop_status,rank,
	fg_avg,fg_norm,
        last_name,type,
	joint_flag,
	mcop, conflict,
	total_req_time,total_app_time,
	total_req_time_1,total_app_time_1,
	total_req_time_2,total_app_time_2
        from proposal
	where prop_status != 'B' ) . $bpp . 
	"order by panel_id,rank ASC,prop_id");

    $get_props->execute();
    my $print;
    my @row;
    while (@row = $get_props->fetchrow_array) {
        while ($row[1] =~ s/	/  /g) {;}
        while ($row[1] =~ s/\n/  /g) {;}
        while ($row[1] =~ s/\t/    /g) {;}
        $row[6] = qq("$row[6]");
	$print = join($param{delim},@row);
        while ($print =~ s/N\/A/ /) {;}
        printf OUT "$print\n";
    }
    $get_props->finish;

    close OUT;
}


#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
	    U => undef,
	    out => undef,
            delim => "	",
            verbose => 0
           );

  GetOptions( \%param,
	      "U=s",
	      "out=s",
	      "delim=s",
              "verbose",
              "version",
              "bpp",
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

megalist.pl [options]

=head1 OPTIONS

B<megalist.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

database to read

=item B<-out> filename

output filename

=item B<-delim> delimiter

output delimeter, default is tab

=item B<-bpp>

includes BPP panel 

=item B<-help>

displays documentation for B<megalist.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script reads the panel data from the peer review database
and writes tab-delimited format.

=head1 AUTHOR

CXCDS
