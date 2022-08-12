#
# Too.pm - This object contains data associated with a TOO
#
# Purpose: Provides a single place to access elements of a TOO
#          
# Copyright (c) 2005 Sherry Winkelman <swinkelman@cfa.harvard.edu>.
# All Rights Reserved. Std. disclaimer applies.
# Artificial License, same as perl itself. Have fun.
#
# namespace
package Too;
use strict;
use Carp;
use Data::Dumper;

use base qw(Class::Accessor::Ref);
Too->mk_accessors(qw(dbh prop_id targ_id ordr req_obs_time app_obs_time ao
                     pre_min_lead pre_max_lead fractol
		     obs_status trigflag TOO_PROPERTIES panel_id changed
		     obs_status_bck num_too_pt verbose segtime));
Too->mk_refaccessors(qw(app_obs_time obs_status num_too_pt));

## Class Method ##
# Name: new
# Purpose: create a new Too object
# Parameters: database handle
#             length of observation segments
#             proposal id
#             target id
#             too order
#             reference to TOO properties
#             panel id
#             verbose
# Returns: Too object
sub new {
    my $self = {};
    bless $self,shift;
    $self->_init(@_);
    print "Too::new - creating new object\n" if $self->verbose > 2;
    return $self;
}


## Internal Utility ##
# Name: _init
# Purpose: initializes a new Target object
# Parameters: database handle
#             length of observation segments
#             proposal id
#             target id
#             too order
#             reference to TOO properties
#             panel_id
#             verbose
# Returns: Too object
sub _init {
    my ($self, $dbh, $segtime, $prop_id, $targ_id, $i, $TooProp, 
	$panel_id, $verbose) = @_;
    my %init = %$self;
    my %changed = ();

    $init{changed} = \%changed;
    $init{segtime} = $segtime;
    $init{dbh} = $dbh;
    $init{panel_id} = $panel_id;
    $init{TOO_PROPERTIES} = $TooProp;
    $init{verbose} = $verbose;
    my @TooProp = @$TooProp;
    my $too_query = $dbh->prepare(qq(select * from too where prop_id = ? 
				     and targ_id = ? and ordr = ? and 
				     panel_id = ?));

    $too_query->execute($prop_id, $targ_id, $i, $panel_id);
    my $too_ref = $too_query->fetchrow_hashref('NAME_lc');
    foreach my $xx (@TooProp) {
       my $val = $too_ref->{$xx} ;
       if (defined $val && $xx =~ /time/) {
          $val = sprintf("%7.2f",$val);
       }
       $init{$xx} = $val;
    }
    $too_query->finish;
    %$self = %init;
}

## Class Method ##
# Name: dump
# Purpose: does a data dump of the too object
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
# Name: num_pnt
# Purpose: calculate the number of pointings for the observation
# Parameters: none
# Returns: integer
sub num_pnt {
  my $self = shift;
  my $segments = int($self->app_obs_time / $self->segtime);
  $segments++ if ($self->app_obs_time % $self->segtime) > 0.0;
  return $segments;
}
 
## Class Method ##
# Name: save_member
# Purpose: saves the new value of a field to the too object
# Parameters: field
#             value
# Returns: nothing
sub save_member {
    my ($self, $member, $value) = @_;
    $self->$member($value);
}

## Class Method ##
# Name: save2database
# Purpose: saves the too object to the database
# Parameters:none
# Returns: nothing
sub save2database {
    my ($self) = @_;
    my $changed = $self->changed;
    my %changed = %$changed;
    my $dbFields = $self->TOO_PROPERTIES;

    foreach my $member (keys %changed) {
	my @matching = grep {$_ =~ /$member/} @$dbFields;
	if (scalar @matching > 0) {
	    my $update = $self->dbh->prepare(qq(update too set $member = ? 
						where panel_id = ? and 
						prop_id = ? and 
						targ_id = ? and ordr = ?));
	    $update->execute($self->get($member), $self->panel_id, 
			     $self->prop_id, $self->targ_id, $self->ordr);
	    print "TOO::save2database\n\tSaving $member in too ", 
	      $self->get($member), "\n" if $self->verbose;
	}
    }
    # Reset the changed hash
    %changed = ();
    $self->changed(\%changed);
}

1;

__END__

=head1 NAME

Too - This object contains data associated with a too.

=head1 VERSION

$Revision: 1.9 $

=head1 SYNOPSIS

    use Too;
    my $too =  new Too($dbh, $prop_id, $targ_id, $i, $TooProps, $panel_id, $verbosity);

=head1 DESCRIPTION

Provides a single place to access elements of a TOO

=head1 PUBLIC METHODS

=head2 new($dbh, $prop_id, $targ_id, $i, $TooProps, $panel_id, $verbosity)

Creates a new Too object

=over 

=item $dbh - database handle

=item $prop_id - proposal id

=item $targ_id - target id

=item $i - TOO order

=item $TooProps - reference to array of TOO properties

=item $panel_id - panel id

=item $verbosity - level of verbosity

=back


=head2 set($field, $value)

Overloads the set accessor from Class::Accessor to update %changed to
reflect that there are unsaved changes in the object.

=head2 dump

Prints a data dump of the too object

=head2 save_member($field, $value)

Saves the new value of a field to the too object

=head2 num_pnt

Returns the number of pointings for the observation

=head2 save2database

Saves the too object to the database.

=head1 PRIVATE METHODS

=head2 _init

Initializes new Too object.

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
