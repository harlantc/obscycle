#!/usr/bin/perl 

#******************************************************************************
# move_proposal.pl
#
# This script creates the script  to move a  proposal to one panel 
# and then delete from the existing panel
#******************************************************************************

use strict;
use DBI;
use Data::Dumper;
use vars qw($pwdProp %param $VERSION);

$VERSION = '$Id: move_proposal.pl,v 1.0 2012/07/26  dmh Exp $';

{
  use Getopt::Long;
  my($row,$str);
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
  

#  DBI->trace(1);
  # Database connection 1
  my $dsn1 = "dbi:Pg:dbname=$param{U}";
  my $dbh1 = DBI->connect($dsn1, "", "", {
					  PrintError => 1,
					  RaiseError => 0});
  print STDERR "\nHEY -- Don't forget to run backup_PR_tables_for_mods first!!!!\n\n";

  my $atg = $dbh1->prepare("select * from alternate_target_group 
	where prop_id = $param{proposal} and panel_id = $param{from}");
  my $tr = $dbh1->prepare("select timereq.* from timereq ,target
	where prop_id = $param{proposal} 
	and target.panel_id = $param{from}
	and timereq.panel_id = $param{from}
	and target.targ_id = timereq.targ_id");
  my $rr = $dbh1->prepare("select rollreq.* from rollreq ,target
	where prop_id = $param{proposal} 
	and target.panel_id = $param{from}
	and rollreq.panel_id = $param{from}
	and target.targ_id = rollreq.targ_id");
  my $ph = $dbh1->prepare("select phasereq.* from phasereq ,target
	where prop_id = $param{proposal} 
	and target.panel_id = $param{from}
	and phasereq.panel_id = $param{from}
	and target.targ_id = phasereq.targ_id");
  my $too = $dbh1->prepare("select * from too
	where prop_id = $param{proposal} and panel_id = $param{from}");
  my $tgt = $dbh1->prepare("select * from target 
	where prop_id = $param{proposal} and panel_id = $param{from}");
  my $prop = $dbh1->prepare("select * from proposal 
	where prop_id = $param{proposal} and panel_id = $param{from}");

  my($dname) = $param{dir};
  $dname .= "/" . $param{proposal} . ".move_";
  my($fname);
  $fname = $dname . "insert";
  open IFILE,"> $fname";
  $fname = $dname . "delete";
  open DFILE,"> $fname";
  
  $prop->execute();
  doit($prop,"proposal");
  $tgt->execute();
  doit($tgt,"target");
  $too->execute();
  doit($too,"too");
  $atg->execute();
  doit($atg,"alternate_target_group");

  $tr->execute();
  doit($tr,"timereq");
  $rr->execute();
  doit($rr,"rollreq");
  $ph->execute();
  doit($ph,"phasereq");

  print DFILE "\n\n--Run this after you have inserted entries in the new panel\n\n";
  print DFILE qq(delete from alternate_target_group 	
	where prop_id = $param{proposal} and panel_id = $param{from};\n);
  print DFILE qq(delete from phasereq 	
	where targ_id in (select targ_id from target where  prop_id = $param{proposal} and panel_id = $param{from}) 
	and panel_id = $param{from};\n);
  print DFILE qq(delete from timereq 	
	where targ_id in (select targ_id from target where  prop_id = $param{proposal} and panel_id = $param{from}) 
	and panel_id = $param{from};\n);
  print DFILE qq(delete from rollreq 	
	where targ_id in (select targ_id from target where  prop_id = $param{proposal} and panel_id = $param{from}) 
	and panel_id = $param{from};\n);
  print DFILE qq(delete from too 	
	where prop_id = $param{proposal} and panel_id = $param{from};\n);
  print DFILE qq(delete from target 	
	where prop_id = $param{proposal} and panel_id = $param{from};\n);
  print DFILE qq(delete from proposal 	
	where prop_id = $param{proposal} and panel_id = $param{from};\n);



  close IFILE;
  close DFILE;
}


sub doit
{
  my($sth,$tbl) = @_;
  my($row,$str);
  my($dropcols) = ",g1,g2,g3,g4,g5,g6,g7,g8,g9,g10,g11,g12,g13,g14,g15,g16,g17,g18,g19,g20,g21,g22,g23,g24,g25,fg_avg,fg_med,fg_stdev,fg_norm,";

  my $fields = $sth->{NAME};
  my $type = $sth->{TYPE};
  my $numfields = $sth->{NUM_OF_FIELDS};
  while ($row = $sth->fetchrow_hashref) {
     my($ii) = 0;
     my($cols,$flds) = "";
     for ($ii=0;$ii< $numfields;$ii++) {
        $str = $sth->{NAME}->[$ii];
        if ($dropcols =~ /,$str,/) {
          next;
        }
        $type = $sth->{TYPE}->[$ii];
        #print STDERR "$type for $str\n";
        $cols .= $str . ",";
        my($x) = $$row{$str};
        if ($type > 10 || $type == -1) {
          if (!defined $x ) {
            $flds .= "null,";
          } else {
            $x =~ s/'/''/g;
            $x =~ s/"/\\"/g;
            $flds .= qq('$x',);
          }
        } elsif ($str =~ /panel_id/) {
          $flds .= $param{to} . ",";
        } else  {
          if (!defined $x) {
            $x = "null"
          }
          $flds .= $x . ",";
        }
     }
     chop($cols);
     chop($flds);
     print IFILE "insert into $tbl(";
     
     print IFILE $cols . ")\nvalues(" . $flds .");\n\n";
  }
}
    



#***************************************************************************
# Subroutine for parse opts
#***************************************************************************
sub parse_opts
{

  %param = (
	    U => undef,
	    from=> undef,
	    to=> undef,
	    proposal=> undef,
	    dir=> ".",
            verbose => 0
           );

  GetOptions( \%param,
	      "U=s",
	      "from=i",
	      "to=i",
	      "dir=s",
	      "proposal=i",
              "verbose=i",
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

move_proposal.pl [options]

=head1 OPTIONS

B<move_proposal.pl> uses long option names.  You can type as few characters as
are necessary to match the option name.

=over 4

=item B<-U>

User name on postgresql server

=item B<-from>

Panel proposal is being moved FROM

=item B<-to>

Panel proposal is being moved TO

=item B<-proposal>

The proposal number

=item B<-dir>

The output directory.  Default is current directory.

=item B<-help>

displays documentation for B<move_proposal.pl>

=item B<-version>

displays the version

=item B<-verbose>

displays required options

=back

=head1 DESCRIPTION

This script creates the SQL to move a proposal from one panel to another
at the peer review.

=head1 AUTHOR

Diane Hall
