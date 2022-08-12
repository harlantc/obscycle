#!/usr/bin/perl

#*****************************************************************************
# pop_groups.pl
#
# This script creates the sql to populate the group assignments for a panel
# from the Reports Tool output.
#*****************************************************************************

use strict;
use Data::Dumper;

use vars qw(%param $VERSION);
$VERSION = '$Id: pop_groups.pl,v 1.8 2012/02/14 16:01:53 wink Exp $';
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

  my %groups = (_unassigned => 1);
  my $panel_id = $param{panel} if $param{panel};
  open (OUT, ">$param{out}") ||
    die "Sorry, can't open $param{out}: $!\n"; 
  if (!open (IN, "$param{in}"))
  {
    my $count = 0;
    foreach my $group (keys %groups) {
      $count++;
      print OUT qq(insert into groups values ($count, $panel_id, '$group');\n);
    }
    close OUT;
    die "Sorry, can't open $param{in}\n"; 
  }
  my $deleted = 0;

  foreach my $line (<IN>) {
    chomp $line;
    
    ($panel_id, my ($prop_id, $group)) = split /\t/, $line;
    $panel_id = $param{panel} if $param{panel};
    if (!$deleted) {
      print OUT qq(delete from groups where panel_id = $panel_id;\n);
      $deleted++;
    }
    $group =~ s/'|"//;
    while ($group =~ s/ $//g) {; }
    while ($group =~ s/^ //g) {; }
    $group = '_unassigned' if !$group;
    if ($group eq (int($group))) {
        print STDERR "reset $group  \n";
        $group = sprintf("%02d",$group);
    }
    $groups{$group} = 1;
    
    print OUT qq(update proposal set group_id = '$group' where panel_id = $panel_id and prop_id = $prop_id;\n);
  }

  my $count = 0;
  foreach my $group (keys %groups) {
    $count++;
    print OUT qq(insert into groups values ($count, $panel_id, '$group');\n);
  }
  close IN;
  close OUT;   
}


#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
            in => undef,
            out => undef,
            verbose => 0
           );

  GetOptions( \%param,
              "in:s",
              "out:s",
	      "panel:i",
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

#***************************************************************************
# Subroutine for usage statements
#***************************************************************************
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

pop_groups.pl [options]

=head1 OPTIONS

B<pop_groups.pl> uses long option names.  You can type as few characters 
as
are necessary to match the option name.

=over 4

=item B<-in>

name of input file

=item B<-out>

name of output file

=item B<-panel>

panel number to override panel number in file with

=item B<-help>

displays documentation for B<pop_groups.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script creates the sql to populate the group assignments for a panel
from the Reports Tool output.

=head1 AUTHOR

Sherry L. Winkelman
