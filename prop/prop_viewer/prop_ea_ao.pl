use vars qw ( $def_cycle $def_lineWidth $def_scale
		$def_minX $def_maxX $def_minY $def_maxY
		$def_minXP $def_maxXP $def_minYP $def_maxYP
		$def_canvasX $def_canvasY
		@energyLabel @colorArray @colorStyle @colorRGB
);
# define the current cycle and previous cycle for the EA Viewer comparison.

$def_cycle = 24;
$def_lineWidth = 2;
$def_scale = 1;

$def_minX = 0.1;
$def_maxX = 12.0;
$def_minY = 0.01;
$def_maxY = 800;

$def_minXP = 0.1;
$def_maxXP = 20.0;
$def_minYP = 0.1;
$def_maxYP = 100;
$def_maxXY = 100000;

$def_canvasX = 1000;
$def_canvasY = 600;

@energyLabel = (".25",".75","1.5","3.0","4.5","6.5","8.5");

@colorRGB = ("#ffffff", 
	"#ff0000","#00ff00","#ff00ff","#0000cc","#00ffff",
	"#6600ff","#ffff00","#ff9900","#000000","#cc9900");

@colorArray = ("red","green","magenta","blue","cyan","purple","yellow","orange","black","brown");
@colorStyle = ("1","2","3","4","5","6","7","8","9","10");


sub verify_number {
   my($val,$def,$min,$max) = @_;
   if ($val !~ /^[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?$/ ) {
      $val = $def;
   }
   if ($val < $min || ($max && $val > $max)) {
     $val = $def;
   }
   return $val;
}

