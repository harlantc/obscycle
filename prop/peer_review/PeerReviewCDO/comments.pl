#!/usr/bin/perl

#*****************************************************************************
# comments.pl
#
# This script prints out comments, grade comments
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

    printf OUT ("%-5.5s\t%-8.8s\t%s\n","Panel","Proposal","Comments");
    printf OUT ("%-5.5s\t%-8.8s\t%s\n","-----","--------","--------");

    my $stmt = qq(select panel_id , prop_id, comments,g_cmt,a_cmt
        from proposal where ( comments is not null 
	or g_cmt is not null or a_cmt is not null ));
    if ($param{panel}) {
      $stmt .= qq( and  panel_id = $param{panel} );
    }
    $stmt .= "order by panel_id,prop_id";

    my $get_props = $dbh1->prepare($stmt);
    $get_props->execute();
    my $print;
    my @row;
    while (@row = $get_props->fetchrow_array) {
        while ($row[2] =~ s/	/  /g) {;}
        while ($row[2] =~ s/\n/  /g) {;}
        while ($row[2] =~ s/\t/    /g) {;}
        while ($row[3] =~ s/	/  /g) {;}
        while ($row[3] =~ s/\n/  /g) {;}
        while ($row[3] =~ s/\t/    /g) {;}
        while ($row[4] =~ s/	/  /g) {;}
        while ($row[4] =~ s/\n/  /g) {;}
        while ($row[4] =~ s/\t/    /g) {;}
        #if ($row[1] == "") { $row[1] = " " };
        if (length($row[2]) > 1 || length($row[3]) > 1 || length($row[4]) > 1) {

        printf OUT ("%-5.5s\t%-8.8s\t",$row[0],$row[1]);
        if ($row[2] && length($row[2]) > 1) {
           printf OUT ("Comments: %s",$row[2]);
        }
        print OUT "\n";
        if ($row[3] && length($row[3]) > 1)  {
          while ($row[3] =~ s/	/  /g) {;}
          while ($row[3] =~ s/\n/  /g) {;}
          while ($row[3] =~ s/\t/    /g) {;}
          printf OUT ("%-5.5s\t%-8.8s\tGrade(final): %s\n","","",$row[3]);
        }
        if ($row[4] && length($row[4]) > 1)  {
          while ($row[4] =~ s/	/  /g) {;}
          while ($row[4] =~ s/\n/  /g) {;}
          while ($row[4] =~ s/\t/    /g) {;}
          printf OUT ("%-5.5s\t%-8.8s\tGrade(other): %s\n","","",$row[4]);
        }
        }
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
            verbose => 0
           );

  GetOptions( \%param,
	      "U=s",
	      "out=s",
	      "panel=s",
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

comments.pl [options]

=head1 OPTIONS

B<comments.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

database to read

=item B<-out> filename

output filename

=item B<-panel>

panel to dump 

=item B<-help>

displays documentation for B<comments.pl>

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
