#
# AltGrp.pm - This object contains data associated with an alternate target
#             group
#
# Purpose: Provides a single place to access elements of an alternate target
#          group
#          
# Copyright (c) 2006 Sherry Winkelman <swinkelman@cfa.harvard.edu>.
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package AltGrp;
use strict;
use Carp;
use Data::Dumper;

use base qw(Class::Accessor::Ref);
AltGrp->mk_accessors(qw(dbh prop_id panel_id alt_id alt_grp_name changed
			req_cnt app_cnt ALT_GRP_PROPERTIES verbose));
AltGrp->mk_refaccessors(qw(app_cnt));

## Class Method ##
# Name: new
# Purpose: create a new AltGrp object
# Parameters: database handle
#             proposal id
#             alternate group id
#             reference to AltGrp properties
#             panel id
#             verbosity
# Returns: AltGrp object
sub new {
    my $self = {};
    bless $self,shift;
    $self->_init(@_);
    print "AltGrp::new - creating new object\n" if $self->verbose > 2;
    return $self;
}


## Internal Utility ##
# Name: _init
# Purpose: initializes a new AltGrp object
# Parameters: database handle
#             proposal id
#             alternate group id
#             reference to AltGrp properties
#             panel_id
#             verbosity
# Returns: AltGrp object
sub _init {
    my ($self, $dbh, $prop_id, $alt_id, $AltGrpProp, $panel_id, $verbose) = @_;
    my %init = %$self;
    my %changed = ();

    $init{changed} = \%changed;
    $init{dbh} = $dbh;
    $init{panel_id} = $panel_id;
    $init{prop_id} = $prop_id;
    $init{ALT_GRP_PROPERTIES} = $AltGrpProp;
    $init{verbose} = $verbose;
    my @AltGrpProp = @$AltGrpProp;

    my $AltGrp_query = $dbh->prepare(qq(select * from alternate_target_group
                                        where prop_id = ? and alt_id = ? and
                                        panel_id = ?));

    $AltGrp_query->execute($prop_id, $alt_id, $panel_id);
    my $AltGrp_ref = $AltGrp_query->fetchrow_hashref('NAME_lc');
    $init{$_} = $AltGrp_ref->{$_} foreach @AltGrpProp;
    $AltGrp_query->finish;
    %$self = %init;
}

## Class Method ##
# Name: dump
# Purpose: does a data dump of the alternate target group object
# Parameters: none
# Returns: nothing
sub dump {
    my $self = shift;
    print Dumper($self);
}

## Class Method ##
# Name: set
# Purpose: overloads the set accessor from Class::Accessor to update %changed
#          to reflect that there are unsaved changes in the object
# Parameters: field to access
#             array of values
# Returns: nothing
sub set {
    my($self, $key, @values) = @_;
    
    if ($key !~ /changed/) {
	my $changed = $self->changed;
	my %changed = %$changed;
	$changed{$key} = 1;
	$self->changed(\%changed);
    }

    $self->SUPER::set($key, @values);
}
 
## Class Method ##
# Name: save_member
# Purpose: saves the new value of a field to the alternate target group object
# Parameters: field
#             value
# Returns: nothing
sub save_member {
    my ($self, $member, $value) = @_;
    $self->$member($value);
    print "AltGrp::save_member - Saving $value to $member\n" if $self->verbose;
}

## Class Method ##
# Name: save2database
# Purpose: saves the alternate target group object to the database
# Parameters:none
# Returns: nothing
sub save2database {
    my ($self) = @_;
    my $changed = $self->changed;
    my %changed = %$changed;
    my $dbFields = $self->ALT_GRP_PROPERTIES;

    foreach my $member (keys %changed) {
	my @matching = grep {$_ =~ /$member/} @$dbFields;
	if (scalar @matching > 0) {
	    my $update = $self->dbh->prepare(qq(update alternate_target_group
                                                set $member = ? 
						where panel_id = ? and 
						prop_id = ? and alt_id = ?));
	    $update->execute($self->get($member), $self->panel_id, 
			     $self->prop_id, $self->alt_id);
	    print "Saving $member in alternate_target_group ", 
	      $self->get($member),"\n" if $self->verbose;
	}
    }
    # Reset the changed hash
    %changed = ();
    $self->changed(\%changed);
}

1;

__END__

=head1 NAME

AltGrp - This object contains data associated with an alternate target group.

=head1 VERSION

$Revision: 1.3 $

=head1 SYNOPSIS

    use AltGrp;
    my $altgrp = 
       new AltGrp($dbh, $prop_id, $alt_id, $AltGrpProps, $panel_id $verbosity);

=head1 DESCRIPTION

Provides a single place to access elements of an alternate target group

=head1 PUBLIC METHODS

=head2 new($dbh, $prop_id, $alt_id, $AltGrpProps, $panel_id, $verbosity)

Creates a new Alternate Target Group object

=over 

=item $dbh - database handle

=item $prop_id - proposal id

=item $alt_id - alternate target group id

=item $AltGrpProps - reference to array of alternate target group properties

=item $panel_id - panel id

=back 

=head2 set($field, $value)

Overloads the set accessor from Class::Accessor to update %changed to
reflect that there are unsaved changes in the object.

=head2 dump

Prints a data dump of the alternate target group object

=head2 save_member($field, $value)

Saves the new value of a field to the alternate target group object

=head2 save2database

Saves the alternate target group object to the database.

=head1 PRIVATE METHODS

=head2 _init

Initializes new Alternate Target Group object.

=head1 DEPENDENCIES

This module has no dependencies

=head1 BUGS AND LIMITATIONS

There are no known bugs in this module.
Please report problems to Sherry Winkelman swinkelman@cfa.harvard.edu
Patches are welcome.

=head1 AUTHOR

Sherry Winkelman swinkelman@cfa.harvard.edu

=head1 LICENCE AND COPYRIGHT

Copyright (c) 2006, Sherry Winkelman <swinkelman@cfa.harvard.edu>. All rights 
reserved.

