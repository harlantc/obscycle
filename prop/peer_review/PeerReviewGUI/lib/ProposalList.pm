#
# ProposalList.pm - This object contains a list of proposal objects
#
# Purpose: Provides a single place to get at all of the proposals in a panel
#          
# Copyright (c) 2005 Sherry Winkelman <swinkelman@cfa.harvard.edu>.
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package ProposalList;
use strict;
use Carp;
use Proposal;
use base qw(Class::Accessor);
ProposalList->mk_accessors(qw(panel_id list verbose));

## Class Method ##
# Name: new
# Purpose: create a new ProposalList object
# Parameters: database handle
#             panel id
#             verbose
# Returns: ProposalList object
sub new {
    my $self = {};
    bless $self,shift;
    $self->_init(@_);
    print "ProposalList::new - creating new object\n" if $self->verbose > 2;
    return $self;
}

## Internal Utility ##
# Name: _init
# Purpose: initializes a new ProposalList object
# Parameters: database handle
#             panel id
#             verbose
# Returns: ProposalList object
sub _init {
    my ($self, $dbh, $panel_id, $if, $verbose) = @_;
    my (%init) = %$self;
    $init{'panel_id'} = $panel_id;
    $init{verbose} = $verbose;
    $init{open_edit} = $if;

    my $proposal_query = $dbh->prepare(qq(select prop_id from proposal
					  where panel_id = ?
					  ));
    $proposal_query->execute($panel_id);
    my %list;
    $proposal_query->execute($panel_id);
    while (my ($index) = $proposal_query->fetchrow_array) {
	$list{$index} = "";
    }
    $proposal_query->finish;

    foreach my $prop_id (keys %list) {
	my $proposal = new Proposal($dbh, $prop_id, $panel_id, $if, $verbose);
	$list{$prop_id} = $proposal;
    }

    $init{'list'} = \%list;
    %$self = %init;
}

## Class Method ##
# Name: proposal
# Purpose: accessor to a proposal
# Parameters: proposal id
# Returns: Proposal object
sub proposal {
    my ($self, $prop_id) = @_;
    my $list = $self->list();
    return $$list{$prop_id};
}

1;

__END__

=head1 NAME

ProposalList - This object contains a list of proposal objects

=head1 VERSION

$Revision: 1.6 $

=head1 SYNOPSIS

    use ProposalList;
    my $list = new ProposalList($dbh, $panel_id);

=head1 DESCRIPTION

Provides a single place to get at all of the proposals in a panel

=head1 PUBLIC METHODS

=head2 new($dbh, $panel_id)

Creates a new Panel object.

=over 

=item $dbh - database handle

=item $panel_id - panel id

=back

=head2 proposal($prop_id)

Returns the proposal object for a specified proposal id

=over 

=item $prop_id - proposal id

=back

=head1 PRIVATE METHODS

=head2 _init

Initializes new ProposalList object.

=head1 DEPENDENCIES

This module has no dependencies

=head1 BUGS AND LIMITATIONS

There are no known bugs in this module.
Please report problems to Sherry Winkelman swinkelman@cfa.harvard.edu
Patches are welcome.

=head1 AUTHOR

Sherry Winkelman swinkelman@cfa.harvard.edu

=head1 LICENCE AND COPYRIGHT

Copyright (c) 2005, Sherry Winkelman <swinkelman@cfa.harvard.edu>. All rights 
reserved.
