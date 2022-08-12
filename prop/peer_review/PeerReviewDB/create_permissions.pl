#!/usr/bin/perl

#*************************************************************************
# create_permissions.pl
#
# Generates an sql script which will grant the appropriate permissions to
# a user to be able to edit proposals with the GUI.
#
# Future development: Use the script to generate user permissions to be
# able to only read the proposals with the GUI.  That way post Peer Review
# requires only Belinda's database
#*************************************************************************

use strict;
use vars qw(%param $VERSION);
$VERSION = '$Id: create_permissions.pl,v 1.4 2012/02/14 14:00:44 wink Exp $';

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
  
  open(FILE, ">$param{file}") ||
    die "Sorry, can't open $param{file}: $!";
  
  print FILE "grant select on allotment to $param{U};\n";
  print FILE "grant all on alternate_target_group to $param{U};\n"; 
  print FILE "grant select on bpp_panel_grades to $param{U};\n";
  print FILE "grant select on columns to $param{U};\n";
  print FILE "grant select on conflicts to $param{U};\n";
  print FILE "grant all on final_comments to $param{U};\n"; 
  print FILE "grant all on groups to $param{U};\n"; 
  print FILE "grant select on observatory to $param{U};\n";
  print FILE "grant select on panel to $param{U};\n";
  print FILE "grant select on passwords to $param{U};\n";
  print FILE "grant select on phasereq to $param{U};\n";
  print FILE "grant all on proposal to $param{U};\n";
  print FILE "grant select on rollreq to $param{U};\n";
  print FILE "grant all on section_columns to $param{U};\n";
  print FILE "grant all on sections to $param{U};\n";
  print FILE "grant all on sorts to $param{U};\n";
  print FILE "grant select on table_columns to $param{U};\n";
  print FILE "grant all on target to $param{U};\n";
  print FILE "grant select on timereq to $param{U};\n";
  print FILE "grant all on too to $param{U};\n";
  print FILE "grant all on views to $param{U};\n";

  close FILE or die "Sorry, can't close $param{file}: $!\n";
}

#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
	    U => undef,
	    file => undef,
            verbose => 0
           );

  GetOptions( \%param,
	      "U=s",
	      "file=s",
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

pop_gui_tables.pl [options]

=head1 OPTIONS

B<pop_gui_tables.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

Username to receive permissions for Peer Review GUI

=item B<-file> <filename>

filename of output file

=item B<-help>

displays documentation for B<pop_gui_tables.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script creates the ddl to grant permissions to a Peer Review GUI user.

=head1 AUTHOR

Sherry L. Winkelman
