#!/usr/bin/perl

#****************************************************************************
# pop_gui_tables.pl
#
# Popluates the tables related to running the GUI (not the tables related to
# the data).  The data to populate the tables are found in the data directory
# in this branch.  Inserts are made for each panel.  There are flags to
# populated the columns, views, and sorts in the GUI.
#****************************************************************************
use strict;
use Data::Dumper;
use DBI;
use vars qw(%param $VERSION);
$VERSION = '$Id: pop_gui_tables.pl,v 1.6 2012/02/14 14:05:43 wink Exp $';

{
  use Getopt::Long;
  parse_opts();
  my $data = $param{datadir};

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
  
  # Prepared sql statements
  my $get_col = $dbh1->prepare(qq(select col_id from columns where 
                                  col_name = ? and panel_id = ?));
  
  my $insert_columns = $dbh1->prepare(qq(insert into columns values 
					 (?,?,?,?,?,?,?,?,?,?,?,?,?)));
  my $insert_table_columns = $dbh1->prepare(qq(insert into table_columns 
                                               values (?, ?, ?, ?, ?, ?)));
  my $insert_views = $dbh1->prepare(qq(insert into views values (?, ?, ?, ?)));
  my $insert_sections = $dbh1->prepare(qq(insert into sections values 
                                          (?, ?, ?, ?, ?, ?, ?)));
  my $insert_section_columns = $dbh1->prepare(qq(insert into section_columns 
                                                 values (?, ?, ?, ?, ?)));
  my $insert_sorts = $dbh1->prepare(qq(insert into sorts values (?,?,?,?,?)));
  
  my $del_columns = $dbh1->prepare(qq(delete from columns where panel_id = ?));
  my $del_table_columns = $dbh1->prepare(qq(delete from table_columns where 
                                            panel_id = ?));
  my $del_views = $dbh1->prepare(qq(delete from views where panel_id = ?));
  my $del_sections = $dbh1->prepare(qq(delete from sections where 
                                       panel_id = ?));
  my $del_section_columns = $dbh1->prepare(qq(delete from section_columns 
                                              where panel_id = ?));
  my $del_sorts = $dbh1->prepare(qq(delete from sorts where panel_id = ?));
  
  if ($param{columns}) {
    # Delete existing columns for the panel
    $del_columns->execute($param{panel});
    $del_columns->finish;
    $del_table_columns->execute($param{panel});
    $del_table_columns->finish;
    
    # Populate columns and table_columns for the panel
    open COLS, "$data/gui_columns.dat" ||
      die "Sorry, can't open $data/gui_columns.dat: $!\n";
    my $count = -1;
    foreach my $line (<COLS>) {
      chomp $line;
      $count++;
      next if $count < 1;   # skips header line
      my ($object, $attribute, $name, $width, $edit, 
	  $type, $table_id, $sortby, $groupby, 
	  $justify, $pub) = split /\t/, $line;
      
      # Calculate the maximum column width for labels based on the longest 
      #string in the dataset (add 2 for caps)
      if ($width =~ /null/ && 
	  $object =~ /proposal|target/ && 
	  $type =~ /label/) {
	my $query = qq(select max(char_length($attribute) + 2) from $object 
		       where panel_id = ?);
	my $get_width = $dbh2->prepare($query);
	$get_width->execute($param{panel});
	($width) = $get_width->fetchrow_array;
	$get_width->finish;
      }
      
      $width = undef if $width =~ /null/;
      $table_id = undef if $table_id =~ /null/;
      $insert_columns->execute($count, $param{panel}, $object, $attribute, 
			       $name, $width, $edit, $type, $table_id, 
			       $sortby, $groupby, $justify, $pub);
      $insert_columns->finish;
    }
    close COLS;
    
    open TABCOLS, "$data/gui_table_columns.dat" ||
      die "Sorry, can't open $data/gui_table_columns.dat: $!\n";
    $count = -1;
    foreach my $line (<TABCOLS>) {
      chomp $line;
      $count++;
      next if $count < 1;   # skips header line
      my ($table_id, $table_name, $col_name, 
	  $col_order, $col_width) = split /\t/, $line;
      $col_width = undef if $col_width =~ /null/;
      $insert_table_columns->execute($table_id, $param{panel}, 
				     $table_name, $col_name, 
				     $col_order, $col_width);
      $insert_table_columns->finish;
    }
    close TABCOLS;
  }
  
  if ($param{views} ) {
    # Delete existing views for the panel
    $del_views->execute($param{panel});
    $del_views->finish;
    $del_sections->execute($param{panel});
    $del_sections->finish;
    $del_section_columns->execute($param{panel});
    $del_section_columns->finish;
    
    # Populate views, sections, and section_columns for the panel
    open VIEWS, "$data/gui_views.dat" ||
      die "Sorry, can't open gui_views.dat: $!\n";
    my $view_id = -1;
    my ($section_id, $col_order);
    my $skip = 'y';
    foreach my $line (<VIEWS>) {
      # Skip lines until we get the start of the first view
      next if ($skip =~ 'y' and $line !~ /VIEW/);
      $skip = 'n';
      
      chomp $line;
      if ($line =~ /VIEW/) {
	# add Preview Views before first view
	if ($view_id < 0) {
	  $insert_views->execute(-1, $param{panel}, 'Preview', 'N');
	  $insert_views->finish;
	  $view_id = 0;
	  $view_id++;
	  $section_id = 0;
	  my ($label, $view_name, $pub) = split /\t/, $line;
	  $pub = 'Y' if !$pub;
	  $insert_views->execute($view_id, $param{panel}, 
				 $view_name, $pub);
	  $insert_views->finish;
	}
	else {
	  $view_id++;
	  $section_id = 0;
	  my ($label, $view_name, $pub) = split /\t/, $line;
	  $pub = 'Y' if !$pub;
	  $insert_views->execute($view_id, $param{panel}, 
				 $view_name, $pub);
	  $insert_views->finish;
	}
      }
      elsif ($line =~ /SECTION/) {
	$col_order = 0;
	$section_id++;
	my ($label, $section_name, $section_type, $section_width) = 
	  split /\t/, $line;
	$section_width = undef if $section_width =~ /null/;
	$insert_sections->execute($section_id, $view_id, 
				  $param{panel}, $section_name, 
				  $section_id, $section_type, 
				  $section_width);
	$insert_sections->finish;
      }
      elsif ($line =~ /COLUMN/) {
	chomp $line;
	my ($label, $col_name) = split /\t/, $line;
	$col_order++;
	$get_col->execute($col_name, $param{panel});
	my ($col_id) = $get_col->fetchrow_array;
	if ($col_id) {
	  $insert_section_columns->execute($section_id, $view_id, 
					   $param{panel},
					   $col_id, $col_order);
	  $insert_section_columns->finish;
	}
	else {
	  print "$col_name missing from columns table.  Deleting views from database for panel $param{panel}\n";
	  $del_views->execute($param{panel});
	  $del_views->finish;
	  $del_sections->execute($param{panel});
	  $del_sections->finish;
	  $del_section_columns->execute($param{panel});
	  $del_section_columns->finish;
	  exit(1);
	}
      }
    }
    close VIEWS;
  }
  
  if ($param{sorts}) {
    # Delete existing sorts from database
    $del_sorts->execute($param{panel});
    $del_sorts->finish;
    
    # Populates sorts for panel
    open SORTS, "$data/gui_sorts.dat" ||
      die "Sorry, can't open gui_sorts.dat: $!\n";
    my $count = -1;
    foreach my $line (<SORTS>) {
      chomp $line;
      $count++;
      next if $count < 1;   # skips header line
      my ($groupby, $sortby, $sort_name) = split /\t/, $line;
      $groupby = undef if $groupby =~ /null/;
      
      $insert_sorts->execute($count, $param{panel}, $groupby, 
			     $sortby, $sort_name);
      $insert_sorts->finish;
    }
    close SORTS;
  }
}

#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts {
  
  %param = (
	    datadir => undef,
	    database => undef,
	    panel => undef,
	    verbose => 0
	   );
  
  GetOptions( \%param,
	      "datadir=s",
	      "database=s",
	      "panel=i",
	      "columns",
	      "views",
	      "sorts",
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
sub usage {
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

=item B<-datadir>

directory where data files reside

=item B<-database>

database to populate

=item B<-panel>

panel number you wish to load

=item B<-columns>

load columns into database

=item B<-views>

load views into database

=item B<-sorts> 

load sorts into database

=item B<-help>

displays documentation for B<pop_gui_tables.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script populates the tables used by the gui for displaying the data.  
Several files
containing information on columns for display, views, and sort are used to 
populate the
gui tables for each peer review panel in the database.

=head1 AUTHOR

Sherry L. Winkelman
