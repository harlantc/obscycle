package CDA::PropPaperLookup;

use strict;
use warnings;

#$Id: PropPaperLookup.pm 5710 2016-06-15 16:57:38Z wink $
#Modified slightly for Data Systems

#*****************************************************************************
# CDA::PropPaperLookup returns tablular output of the CSPs associated with a
# proposals.  The lookup can be done by proposal number or grant,
# and can be for a single value or a list of values.
#*****************************************************************************

use warnings;
use strict;
use version; our $VERSION = qv('1.0');
use Data::Dumper;
require Carp;
use Carp;


sub new {
  my $self = {};
  bless $self;
  return $self;
}

##############################################################################
# Function....: compile_data
# Purpose.....: Collect all of the data bits and put them into a data hash
# Parameters..: self
#               $input: input string
# Returns.....: hash of data
##############################################################################
sub compile_data {
  my ($self, $db, $input) = @_;
  my $data;

  # Initialize data hash
  my $keys = $self->clean_input($input);
  $data = $self->init_data($keys);

  # Construct the elements for the 'in' list for queries to db
  my $in_list = $self->stringify($keys);

  # Update the status for each proposal and add some other basic info
  my $prop_status_query = $self->get_query('prop_status', $in_list);
  $data = $self->update_status($db,$data, $prop_status_query);

  # Get the data linked papers
  my $prop_paper_query = $self->get_query('prop_list', $in_list);
  ($data, $keys) = $self->get_papers($db,$data, $prop_paper_query);

  return $data, $keys;
}

# End subroutine: compile_data

##############################################################################
# Function....: init_data
# Purpose.....: Initialize the data hash with the proposal of interest
# Parameters..: self
#               $keys: array ref of proposals
# Returns.....: hash of data
##############################################################################
sub init_data {
  my ($self, $keys) = @_;
  my %data;

  foreach my $key (@$keys) {
    my %initial_status = (status => 'Not valid Chandra proposal');
    $data{$key} = \%initial_status;
  }

  return \%data;
}

# End subroutine init_data

##############################################################################
# Function....: update_status
# Purpose.....: Update the status for each proposal in the data hash
# Parameters..: self
#               $data: reference to data hash
#               $status_query: proposal status query string
# Returns.....: $data
##############################################################################
sub update_status {
  my ($self, $db, $data, $status_query) = @_;

  my $sth = $db->prepare($status_query);
  $sth->execute;
  while (my($prop_num,$prop_type,$ao_str) = $sth->fetchrow_array()) {
    $$data{$prop_num}{status} = 'Valid Chandra proposal';
    $$data{$prop_num}{prop_type} = $prop_type;
    $$data{$prop_num}{ao_str} = $ao_str;
  }

  return $data;
}

# End subroutine: update_status

##############################################################################
# Function....: get_papers
# Purpose.....: Get the papers and return hashes for papers related to proposal
#               and grant
# Parameters..: self
#               $data: reference to data hash
#               $status_query: proposal status query string
# Returns.....: $data
##############################################################################
sub get_papers {
  my ( $self, $db, $data, $prop_list_query ) = @_;


  my $sth = $db->prepare($prop_list_query);
  $sth->execute();

  while (my ($prop_num, $bibcode, $bib_source, $title, $author, $journal, $title_txt, $author_txt, $journal_txt) = $sth->fetchrow_array()) {
    $$data{$prop_num}{status} = 'Papers found';
    #example is 02900076 02900378
    $bibcode =~ s/\%26/&/g;
    if ($bib_source eq 'obsid') {
      my %bib_info = (title => $title,
		      author => $author,
		      journal => $journal,
		      title_txt => $title_txt,
		      author_txt => $author_txt,
		      journal_txt => $journal_txt);
      $$data{$prop_num}{prop_papers}{$bibcode} = \%bib_info;
    }
    elsif ($bib_source eq 'grant' and
	   !$$data{$prop_num}{prop_papers}{$bibcode}) {
      # Only include grant papers which are not also obsid papers
      my %bib_info = (title => $title,
		      author => $author,
		      journal => $journal,
		      title_txt => $title_txt,
		      author_txt => $author_txt,
		      journal_txt => $journal_txt);
      $$data{$prop_num}{grant_papers}{$bibcode} = \%bib_info;
    }
  }
  my @keys = keys %$data;

  return $data, \@keys;
}

# End subroutine: get_papers

##############################################################################
# Function....: get_query
# Purpose.....: Construct the query needed to access the contact information
# Parameters..: self
#               $type - type of lookup being performed
#               $clean - the cleaned input
# Returns.....: $query
##############################################################################
sub get_query {
    my ( $self, $type, $clean ) = @_;

    my %queries = (
        prop_list => q(select b2p.proposal, b2p.bibcode, bib_source, )
          . q(title_html, author_html, journal_html, title, author, journal from cdabibliography..bib2prop b2p, )
          . q(cdabibliography..bibtex tex where )
          . q(b2p.bibcode *= tex.bibcode and )
          . q(proposal in (XXX) )
          . q(order by b2p.proposal, bib_source DESC, b2p.bibcode),
	prop_status => q(select prop_num, proposal_type, ao_str from )
          . q(axafocat..prop_info where )
          . q(prop_num in (XXX)),
    );

    my $query = $queries{$type};
    $query =~ s/XXX/$clean/msg;

    return $query;
}

# End subroutine: get_query

##############################################################################
# Function....: clean_input
# Purpose.....: Checks that the input is acceptable and puts the proposals
#               into a an array
# Parameters..: self
#               $input: string to clean
# Returns.....: \@list: array of proposal numbers
##############################################################################
sub clean_input {
    my ( $self, $input ) = @_;
    my $status = 0;
    my $clean  = $input;
    my @list;

    # Remove spaces
    $clean =~ s/\s//msg if $clean;

    # Remove trailing comma
    $clean =~ s/,$//ms if $clean;

    if ( !$clean ) {
        carp "No usable search input";
    }


    if ( $clean !~ /^\d+(,\d+)*$/ms ) {
      carp "$input is not a valid list";
    }
    else {
      @list = split /,/, $clean;
    }

    return \@list;
}

# End subroutine: clean_input

##############################################################################
# Function....: stringify
# Purpose.....: Takes an array and creates a comma-separated list with quotes
#               around elements
# Parameters..: self
#               $list: array of proposals
# Returns.....: $string: quoted string for putting into a query
##############################################################################
sub stringify {
    my ( $self, $list ) = @_;
    my $string;

    foreach my $item (@{$list}) {
        $string .= "'$item',";
    }
    $string =~ s/,$//ms;

    return $string;
}

# End subroutine: stringify

1;

__END__


=head1 NAME

CDA::PropPaperLookup - Module for getting CSPs linked to proposals

=head1 VERSION

Version 1.0

=head1 SYNOPSIS

CDA::PropPaperLookup returns xml output of a list of Chandra Science Papers
linked to proposals.  The lookup can be by for a single proposal or a list of
proposals.

     use CDA::PropPaperLookup

     my $foo = CDA::PropPaperLookup->new();
     $foo->get_papers($searchby, $data);

=head1 FUNCTIONS

=head2 get_papers($searchby, $data)

Returns XML output for Chandra Science Papers linked to proposals.  The first
argument is the 'search by' parameter and will be treated as 'proposal'
regardless of what is submitted.  The second argument is a string containing
the proposal(s) for the search to be performed over.

The second argument can be a single proposal or a comma-separated list of
proposals.  Spaces are allowed in the string and will be stripped prior to
processing.

=head1 AUTHOR

Sherry Winkelman, C<< <wink at head.cfa.harvard.edu> >>

=head1 BUGS

=head1 BUGS

Please report any bugs or feature requests to C<arcops at head.cfa.harvard.edu>.

=head1 LICENSE AND COPYRIGHT

Copyright 2013 Sherry Winkelman.
