#!/usr/bin/perl

#******************************************************************************
# dump_gui_tables.pl
#
# This script dumps the view information for each panel from the database to
# files (one per panel).  It also dumps the sort information for each panel
# from the database to files (one per panel).  These files can be used by
# pop_gui_tables.pl to load views and sorts back into the database.
#******************************************************************************

use strict;
use Data::Dumper;
use DBI;
use vars qw(%param $VERSION);
$VERSION = '$Id: dump_gui_tables.pl,v 1.2 2012/02/14 16:21:58 wink Exp $';

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
    my $dsn1 = "dbi:Pg:dbname=$param{database}";
    my $dbh1 = DBI->connect($dsn1, "", "");

    # Database connection 2
    my $dsn2 = "dbi:Pg:dbname=$param{database}";
    my $dbh2 = DBI->connect($dsn2, "", "");

    # Database connection 3
    my $dsn3 = "dbi:Pg:dbname=$param{database}";
    my $dbh3 = DBI->connect($dsn3, "", "");

    # Database connection 4
    my $dsn4 = "dbi:Pg:dbname=$param{database}";
    my $dbh4 = DBI->connect($dsn4, "", "");

    # For each panel, dump the view and sort information from database tables
    # to a file
    my $get_panels = $dbh1->prepare(qq(select distinct panel_id from proposal));
    my $get_views = $dbh2->prepare(qq(select view_id, view_name, pub from views where 
				      panel_id = ? and view_id > 0 order by view_id));
    my $get_sections = $dbh3->prepare(qq(select section_id, section_name, section_order, 
					 section_type, section_width from sections where 
					 panel_id = ? and view_id = ? order by view_id, 
					 section_id, section_order));
    my $get_columns = $dbh4->prepare(qq(select col_name from columns, section_columns 
					where section_columns.col_id = columns.col_id and
					section_columns.panel_id = columns.panel_id and 
					columns.panel_id = ? and view_id = ? 
					and section_id = ? order by col_order));
    my $get_sorts = $dbh2->prepare(qq(select groupby, sortby, sort_name from sorts where 
				      panel_id = ? order by sort_id));

    $get_panels->execute();
    while (my ($panel_id) = $get_panels->fetchrow_array) {
	open VIEWS, ">./gui_views_panel$panel_id.dat" ||
	    die "Sorry, can't open gui_views_panel$panel_id.dat: $!\n";
	$get_views->execute($panel_id);
	while( my( $view_id, $view_name, $pub ) = $get_views->fetchrow_array) {
	    print VIEWS "VIEW\t$view_name\t$pub\n";

	    $get_sections->execute($panel_id, $view_id);
	    while( my($section_id, $section_name, $section_order, 
		      $section_type, $section_width) = $get_sections->fetchrow_array) {
		$section_width = 'null' if !$section_width;
		print VIEWS 
		    "SECTION\t$section_name\t$section_type\t$section_width\n";

		$get_columns->execute($panel_id, $view_id, $section_id);
		while( my($col_name) = $get_columns->fetchrow_array) {
		    print VIEWS "COLUMN\t$col_name\n";
		}
		$get_columns->finish;
	    }
	    $get_sections->finish;
	}
	$get_views->finish;
	close VIEWS;

	# For each panel, dump the sorts to a file
	open SORTS, ">./gui_sorts_panel$panel_id.dat" ||
	    die "Sorry, can't open gui_sorts_panel$panel_id.dat: $!\n";
	print SORTS "groupby\tsortby\tsort_name\n";
	$get_sorts->execute($panel_id);
	while( my($groupby, $sortby, $sort_name) = $get_sorts->fetchrow_array) {
	    $groupby = 'null' if !$groupby;
	    print SORTS "$groupby\t$sortby\t$sort_name\n";
	}
	$get_sorts->finish;
	close SORTS;
    }
    $get_panels->finish;
}

#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
	    database => undef,
            verbose => 0
           );

  GetOptions( \%param,
	      "database=s",
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

dump_gui_tables.pl [options]

=head1 OPTIONS

B<pop_gui_tables.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-help>

displays documentation for B<dump_gui_tables.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script dumps the view information for each panel from the database to files (one per panel).
It also dumps the sort information for each panel from the database to files (one per panel).
These files can be used by pop_gui_tables.pl to load views and sorts back into the database.

=head1 AUTHOR

Sherry L. Winkelman
