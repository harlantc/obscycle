# used by CGI routines that need to read/print meta/footer files
1;

sub getMeta()
{
  my($docroot) = $ENV{'DOCUMENT_ROOT'};
  my($metafile) = $docroot . "/soft/RELEASE/include/cxcds_meta.html";
  if ($docroot =~ /icxc/) {
    $metafile = $docroot . "/soft/include/cxcds_meta.html";
  }

  my($meta) = "";

  my($str) = "";
  if (-e $metafile) {
    if (open FFILE,"< $metafile" ) {
      while ($str= <FFILE>) {
        $meta  .= $str;
      }
      close FFILE;
    }
  }
  return $meta;
}


sub getFooter()
{
  my($xtra) = @_;
  my($Footer);
  my($docroot) = $ENV{'DOCUMENT_ROOT'};
  my($version) = $docroot . "/soft/RELEASE/include/VERSION";
  my($footerplain) = $docroot . "/soft/RELEASE/include/cxcfooter.html";
  if ($docroot =~ /icxc/) {
    $version = $docroot . "/soft/include/VERSION";
    $footerplain = $docroot . "/soft/include/cxcfooter.html";
  }


  my($vstr) = "";
  if (-e $version) {
    if (open FFILE,"< $version" ) {
      $vstr .= "Release ";
      while ($str= <FFILE>) {
        $vstr  .= $str;
      }
      $vstr .= "<br>\n";
      close FFILE;
    }
  }

  $Footer = qq( <div class="footerdiv">);
  
  if (-e $footerplain) {
       if (open FFILE,"< $footerplain" ) {
         while ($str= <FFILE>) {
           if ($str =~ /VERSION/) {
             $Footer .= $vstr;
             $Footer .= $xtra;
           } else  {
             $Footer  .= $str;
           }
         }
         close FFILE;
      }
   }

   $Footer .= "</div>";
 
    return $Footer;
}
   
