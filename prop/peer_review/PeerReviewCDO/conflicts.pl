#!/usr/bin/perl
#******************************************************************************
# conflicts.pl
#
# This script prints conflicts for all Y/G proposals
# Files produced are the simple and cross conflict files used by WebReports
#
#******************************************************************************



use strict;
use Data::Dumper;
use DBI;
use vars qw(%param $VERSION $dlm);
$VERSION = '$Id: conflicts.pl,v 1.14 2011/06/19 16:16:10 wink Exp $';

{
  use Getopt::Long;
  parse_opts();
  $dlm = $param{f};

  if ($param{version}) {
    print $VERSION, "\n";
    exit( 0 );
  }

  if ($param{help}) {
    usage(0);
  }

  # Database connection 1
  my $dsn = "dbi:Pg:dbname=$param{U}";
  my $dbh = DBI->connect($dsn, "", "");

  my ($day, $month, $year) = (localtime)[3,4,5];
  $year += 1900;
  $month++;
  $month = sprintf("%02d", $month);
  $day = sprintf("%02d", $day);
  my $date = "$year$month$day";

  my $get_panels = $dbh->prepare(qq(select distinct panel_id from proposal));
  $get_panels->execute();
  my @panels;
  while (my($pan) = $get_panels->fetchrow_array) {
    push @panels, $pan;
  }
  $get_panels->finish;
  @panels = ($param{panel}) if $param{panel};

  # For each panel (including BPP): 
  # 	generate a cross-conflict proposal list
  # 	generate a simple conflict proposal list
  foreach my $panel (@panels) {
    my $conflicts = get_conflicts($dbh, $panel);
    my $pstr = sprintf("%02d",$panel);
    gen_simple_conflicts($dbh, $panel, $conflicts, 
			"${date}_simple_panel$pstr.txt");
    gen_cross_conflicts($dbh, $panel, $conflicts, 
		       "${date}_cross_panel$pstr.txt");
  }
}

sub get_conflicts {
  my $dbh = shift;
  my $panel = shift;
  my %conflicts;

  my $get_conflicts = $dbh->prepare(qq(select distinct 
	c.prop_id as pid, targ_num as tnum, exptime, detector, 
	conflict_propid, conflict_targnum, conflict_exptime, conflict_detector,
	 conflict_sep, conflict_type 
	from conflicts c, proposal p 
	where c.prop_id = p.prop_id and p.panel_id = ? 
	union 
	select distinct conflict_propid as pid, conflict_targnum as tnum, 
	conflict_exptime, conflict_detector, 
	c.prop_id, targ_num, exptime, detector, conflict_sep, conflict_type 
	from conflicts c, proposal p 
	where conflict_propid = p.prop_id 
	and p.panel_id = ?  
	order by pid,tnum));

  my $get_prop_status = $dbh->prepare(qq(select prop_status 
	from proposal where prop_id = ? and panel_id = ?));
  my $get_targ_status = $dbh->prepare(qq(select count(*) from target where
	prop_id = ? and targ_num = ? and panel_id = ? and targ_status = 'N'));

  my $get_conflict_pans = $dbh->prepare(qq(select panel_id from proposal 
	where prop_status in ('Y', 'G') and prop_id = ?));

  my $get_prop_info = $dbh->prepare(qq(select prop_status, big_proj, 
	fg_avg, fg_norm, type, last_name from proposal 
	where prop_id = ? and panel_id = ?));

  my $get_targ_name = $dbh->prepare(qq(select targ_name, alt_id,ra,dec,
	grating ,grid_name
	from target where prop_id = ? and panel_id = ? and targ_num = ?));


  # go get the conflicts for the current panel  
  $get_conflicts->execute($panel, $panel);
  while (my($propid, $targnum, $exptime, $detector, $conflict_propid, 
	    $conflict_targnum, $conflict_exptime, $conflict_detector, 
	    $conflict_sep, $conflict_type) = $get_conflicts->fetchrow_array) {
      next if skip($get_prop_status, $get_targ_status, $propid, $panel, 
		   $targnum,0);

    # Find panels where the conflict proposal has been approved
    $get_conflict_pans->execute($conflict_propid);
    while (my($conflict_panel) = $get_conflict_pans->fetchrow_array) {
      next if skip($get_prop_status, $get_targ_status, $conflict_propid, 
		   $conflict_panel, $conflict_targnum,1);

      my $prop = sprintf("$propid %02d %03d",$panel,$targnum);
      my $con = sprintf("$conflict_propid %02d %03d",$conflict_panel,$conflict_targnum);

      # Only add new conflicts
      if ( !$conflicts{"$prop|$con"} and !$conflicts{"$con|$prop"}) {

	# Collect info for proposal
	$get_prop_info->execute($propid, $panel);
	my ($prop_status, $big_proj, $fg_avg, 
	    $fg_norm, $prop_type, $pi) = $get_prop_info->fetchrow_array;
	$get_prop_info->finish;
	$get_targ_name->execute($propid, $panel, $targnum);
	my ($targname, $alt_id,$ra,$dec,$grating,$grid) = $get_targ_name->fetchrow_array;
	$get_targ_name->finish;
	$prop_type .= "/Alt" if $alt_id;
	$prop_type .= "/" . $big_proj if $big_proj =~ /LP|VLP|XVP/;
	my %prop1 = (prop_id => $propid,
		     panel => $panel,
		     prop_status => $prop_status,
		     prop_type => $prop_type,
		     targnum => $targnum,
		     exptime => $exptime,
		     detector => $detector,
		     grating => $grating,
		     ra => $ra,
		     dec => $dec,
		     Gavg => $fg_avg,
		     Gnorm => $fg_norm,
		     targname => $targname,
		     sep => $conflict_sep,
		     type => $conflict_type,
		     big_proj => $big_proj,
		     pi => $pi,
		     grid => $grid,
		    );

	# Collect info for conflict proposal
	$get_prop_info->execute($conflict_propid, $conflict_panel);
	($prop_status, $big_proj, $fg_avg, 
	 $fg_norm, $prop_type, $pi) = $get_prop_info->fetchrow_array;
	$get_prop_info->finish;
	$get_targ_name->execute($conflict_propid, $conflict_panel, 
				$conflict_targnum);
	($targname, $alt_id,$ra,$dec,$grating,$grid) = $get_targ_name->fetchrow_array;
	$get_targ_name->finish;
	#$prop_type = 'Alt TOO' if $alt_id;
	$prop_type .= "/Alt" if $alt_id;
	$prop_type .= "/" . $big_proj if $big_proj =~ /LP|VLP|XVP/;
	my %prop2 = (prop_id => $conflict_propid,
		     panel => $conflict_panel,
		     prop_status => $prop_status,
		     prop_type => $prop_type,
		     targnum => $conflict_targnum,
		     exptime => $conflict_exptime,
		     detector => $conflict_detector,
		     grating => $grating,
		     ra => $ra,
		     dec => $dec,
		     Gavg => $fg_avg,
		     Gnorm => $fg_norm,
		     targname => $targname,
		     sep => $conflict_sep,
		     type => $conflict_type,
		     big_proj => $big_proj,
		     pi => $pi,
		     grid => $grid,
		    );

	my @prop = (\%prop1, \%prop2);
	$conflicts{"$prop|$con"} = \@prop;
      }
    }
    $get_conflict_pans->finish;
  }
  return \%conflicts;
}

sub skip {
  my $get_prop_status = shift;
  my $get_targ_status = shift;
  my $propid = shift;
  my $panel = shift;
  my $targnum = shift;
  my $flg = shift;
  my $skip = 0;
  # Skip if prop_status in the panel is 'N' or 'P'
  $get_prop_status->execute($propid, $panel);
  my $status = $get_prop_status->fetchrow_array;
  $get_prop_status->finish;
  $skip++ if $status =~ /N|P|B/;

  $skip++ if $panel == 98;

  if (!$skip) {
      # Skip if targ_status in the panel is 'N'
      # This gives the wrong result when a panel turns off a target for 
      # accounting purposes
      # where the PI gets to choose n targets from the list
      $get_targ_status->execute($propid, $targnum, $panel);
      my $count = $get_targ_status->fetchrow_array;
      $get_targ_status->finish;
      $skip++ if $count;
  }
  return $skip;
}

sub gen_simple_conflicts {
  my $dbh = shift;
  my $panel = shift;
  my $conflicts = shift;
  my $file = shift;

  open my $fh, '>', $file || die "Sorry, can't open $file: $!\n";
  binmode($fh, ":utf8");
  if ($param{anonymous}) {
    printf $fh ("%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s\n",
    "Panel","Proposal","TargNum","Type","ExpTime","TargName","Detector","Grating","Conflict","Sep","Grid");
  } else {
    printf $fh ("%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s\n",
    "Panel","Proposal","P.I.","TargNum","Type","ExpTime","TargName","Detector","Grating","Conflict","Sep","Grid");
  }

  foreach my $conflict (sort keys %$conflicts) {
    next if 
      $$conflicts{$conflict}[0]{panel} != $$conflicts{$conflict}[1]{panel};
    next if 
      $$conflicts{$conflict}[0]{ra} == 0;

    # Skip reporting conflicts with BPPs unless the panel is 99
    #next if ($panel != 99 and 
	     #($$conflicts{$conflict}[0]{big_proj} =~ /LP/ or 
	      #$$conflicts{$conflict}[0]{big_proj} =~ /XVP/ or 
	      #$$conflicts{$conflict}[1]{big_proj} =~ /LP/ or
	      #$$conflicts{$conflict}[1]{big_proj} =~ /XVP/));

    print_record($fh, $conflict, $$conflicts{$conflict});
  }

  close $fh;
}

sub gen_cross_conflicts {
  my $dbh = shift;
  my $panel = shift;
  my $conflicts = shift;
  my $file = shift;

  open my $fh, '>', $file || die "Sorry, can't open $file: $!\n";
  binmode($fh, ":utf8");
  if ($param{anonymous}) {
    printf $fh ("%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s\n",
    "Panel","Proposal","TargNum","Type","ExpTime","TargName","Detector","Grating","Conflict","Sep","Grid");
  } else {
    printf $fh ("%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s\n",
    "Panel","Proposal","P.I.","TargNum","Type","ExpTime","TargName","Detector","Grating","Conflict","Sep","Grid");
  }

  foreach my $conflict (sort keys %$conflicts) {
    next if 
      $$conflicts{$conflict}[0]{panel} == $$conflicts{$conflict}[1]{panel};

    # Skip reporting conflicts with BPPs unless the panel is 99
    #next if ($panel != 99 and 
#	     ($$conflicts{$conflict}[0]{big_proj} =~ /LP/ or 
#	      $$conflicts{$conflict}[0]{big_proj} =~ /XVP/ or 
#	      $$conflicts{$conflict}[1]{big_proj} =~ /LP/ or
#	      $$conflicts{$conflict}[1]{big_proj} =~ /XVP/));

    #

    #skip all conflicts where ra=0,dec=0
    if ( $$conflicts{$conflict}[0]{ra} == 0 ||
         $$conflicts{$conflict}[0]{dec} == 0 ) {
      next;
    }


    print_record($fh, $conflict, $$conflicts{$conflict});
  }

  close $fh;
}

sub print_record {
  my $fh = shift;
  my $conflict_name = shift;
  my $conflict = shift;
  if ($param{anonymous}) {
    printf $fh ("%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm\"%s\"$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s\n",
      $$conflict[0]{panel},$$conflict[0]{prop_id},$$conflict[0]{targnum},$$conflict[0]{prop_type},$$conflict[0]{exptime},$$conflict[0]{targname},$$conflict[0]{detector},$$conflict[0]{grating},$$conflict[0]{type} ,$$conflict[0]{sep} , $$conflict[0]{grid});
    printf $fh ("%s$dlm%s$dlm%s$dlm%s$dlm%s$dlm\"%s\"$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s\n\n",
      $$conflict[1]{panel},$$conflict[1]{prop_id},$$conflict[1]{targnum},$$conflict[1]{prop_type},$$conflict[1]{exptime},$$conflict[1]{targname},$$conflict[1]{detector},$$conflict[1]{grating},"" ,"" , $$conflict[1]{grid});
  } 
  else {
    printf $fh ("%s$dlm%s$dlm\"%s\"$dlm%s$dlm%s$dlm%s$dlm\"%s\"$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s\n",
      $$conflict[0]{panel},$$conflict[0]{prop_id},$$conflict[0]{pi},$$conflict[0]{targnum},$$conflict[0]{prop_type},$$conflict[0]{exptime},$$conflict[0]{targname},$$conflict[0]{detector},$$conflict[0]{grating},$$conflict[0]{type} ,$$conflict[0]{sep} , $$conflict[0]{grid});
    printf $fh ("%s$dlm%s$dlm\"%s\"$dlm%s$dlm%s$dlm%s$dlm\"%s\"$dlm%s$dlm%s$dlm%s$dlm%s$dlm%s\n\n",
      $$conflict[1]{panel},$$conflict[1]{prop_id},$$conflict[1]{pi},$$conflict[1]{targnum},$$conflict[1]{prop_type},$$conflict[1]{exptime},$$conflict[1]{targname},$$conflict[1]{detector},$$conflict[1]{grating},"" ,"" , $$conflict[1]{grid});

  }
}

#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts{

  %param = (
	    U => undef,
	    f => ",",
            verbose => 0,
            anonymous => 0
           );

  GetOptions( \%param,
	      "U=s",
	      "f=s",
	      "panel=i",
              "verbose",
              "version",
              "anonymous",
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
sub usage{
  my ( $exit ) = @_;

  local $^W = 0;
  require Pod::Text;
  Pod::Text::pod2text( '-75', $0 );
  exit $exit;
}

__END__

=head1 USAGE

conflicts.pl [options]

=head1 OPTIONS

B<conflicts.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

Database username

=item B<-panel>

panel number to report only one panel

=item B<-anonymous>

anonymous review, hide the PI name

=item B<-help>

displays documentation for B<conflicts.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script prints target conflicts.  There are two types of lists: simple and
cross panel conflicts.  If the B<-panel> option is specified, only conflicts 
for that panel will be printed.  Otherwise each panel will be printed to 
separate files.

=head1 AUTHOR

Sherry L. Winkelman
