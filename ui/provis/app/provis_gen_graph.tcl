# _INSERT_SAO_COPYRIGHT_HERE_(2008)_
# _INSERT_GPL_LICENSE_HERE_



foreach path [split $env(LD_LIBRARY_PATH) : ] {
    lappend auto_path $path
}

auto_reset

package require BLT

namespace import blt::*
namespace import -force blt::tile::*



#
# Defaults for plot layout
#
#


variable defaults { \
			{MJDBase 2400000.5} \
			{MJDLow auto} \
			{MJDHigh auto} \
			{MJDTitle "Date (days since JD 2400000.5)"} \
			{MJDMajorSpacing 100} \
			{MJDMinorSpacing 10} \
			{MJDLowPad 0}\
			{MJDHighPad 0}\
			{ROLLCurve 1} \
			{ROLLLow 0} \
			{ROLLHigh 360} \
			{ROLLColor green} \
			{ROLLLineType solid} \
			{ROLLLineWidth 1} \
			{ROLLLIMITSColor green} \
			{ROLLTOLERANCELineType dashed} \
			{ROLLTitle "\nNominal Roll Angle"} \
			{ROLLMajorSpacing 60} \
			{ROLLMinorSpacing 6} \
			{ROLLLowPad 0.1}\
			{ROLLHighPad 0.2}\
			{VISCurve 1} \
			{VISLow 0} \
			{VISHigh 1} \
			{VISColor blue} \
			{VISLineType solid} \
			{VISLineWidth 1} \
			{VISTitle "\nAverage Visibility per Orbit"} \
			{VISMajorSpacing 0.1} \
			{VISMinorSpacing 5} \
			{VISLowPad 0.1}\
			{VISHighPad 0.2}\
			{PITCHCurve 1} \
			{PITCHLow 0} \
			{PITCHHigh 180} \
			{PITCHColor red} \
			{PITCHLineType solid} \
			{PITCHLineWidth 1} \
			{PITCHTitle "Pitch Angle"} \
			{PITCHMajorSpacing 60} \
			{PITCHMinorSpacing 6} \
			{PITCHLowPad 0.1}\
			{PITCHHighPad 0.2}\
			{MAXEXPCurve 1} \
			{MAXEXPLow 0} \
			{MAXEXPHigh 160} \
			{MAXEXPColor black} \
			{MAXEXPLineType solid} \
			{MAXEXPLineWidth 1} \
			{MAXEXPFillType lefthatch} \
			{MAXEXPTitle "Maximum Exposure Time"} \
			{MAXEXPMajorSpacing 60} \
			{MAXEXPMinorSpacing 6} \
			{MAXEXPLowPad 0.1}\
			{MAXEXPHighPad 0.2}\
			{BADPITCHRegions 1} \
			{BADPITCHLabelOffset 10} \
			{BADPITCHLineType solid} \
			{BADPITCHLineWidth 1} \
			{BADPITCHFillType lefthatch } \
			{BADPITCHUpdateDate "" } \
			{font "Courier-Bold 10"} \
			{graphTitle "Graph Title\nSOME INFO"} \
			{titlefont "Courier-Bold 14"} \
			{errortitlefont "Courier-Bold 14"} \
			{errortitlecolor red } \
			{graphHeight 650} \
			{graphWidth 800} \
			{mingraphHeight 180} \
			{mingraphHeighti 1} \
			{mingraphWidth 312} \
			{mingraphWidthi 1} \
			{topMargin 20} \
			{bottomMargin 80} \
			{leftMargin 150} \
			{rightMargin 60} \
			{axisLinewidth 1} \
			{patternsLocation patterns} \
			{pspady 0 } \
			{pspadx 0} \
			{graphMode simple} \
			{selfTestMode 0} \
			{testDataPath "."} \
			{testOutputPath "/tmp"} \
			{errorData {}}
		    }

# "*-*-Bold-R-Normal-*-50-"} \
variable argv0
variable parameters 

variable visData
variable colData
variable badpitchData
variable maxexpData
variable graph





#
# Description: Conversion from Julian to Calendar
#
#
# Pre: inJulian - julian date
#
#
# Post: Returns calendar date in format YYYY-Mon-DD
#
#


proc JulianToCalendar { inJulian } {
    set julianBase(19700101) 2440587.50000

    set secVal [expr int ([clock scan "01/01/1970" ] + ($inJulian -$julianBase(19700101) )*86400)]

    return "[clock format $secVal -format "%Y-%b-%d" ]"

}



#
# Description: Converts linetype to actual blt parameters
#
#
# Pre: 
#
#
# Post: Returns list with line style parameters in blt format
#        to be used with -dashes option
#


proc GetLineStyle { inLineStyle } {

    switch [string tolower $inLineStyle] {

	"dotted" {
	    return {1 5};
	}
	"dashed" {
	    return {5 5};
	}
	"solid" -
	default {
	    return {}
	}
    }


}

#
# Description: Initializes defaults from command line options
#
#
# Pre: 
#
#
# Post: internal parameter structures are initialized to the values
#          passed on command line overriding the hardcoded ones
#


proc InitParameters { } {
    variable parameters
    variable defaults
    global env
    global argc
    global argv

    foreach entry $defaults {
	set parameters([lindex $entry 0]) [lindex $entry 1]

    }


    for {set indx 0} { $indx < $argc } {incr indx} {
	set entry [split [lindex $argv $indx] =]
	set parameters([lindex $entry 0]) [lindex $entry 1]
    }

    if { $parameters(graphMode) == "simple" } {
	set parameters(pspadx) 0
	set parameters(pspady) 0
    } else {
	set parameters(topMargin) 70
	if { [string index $parameters(graphHeight) [expr [string length $parameters(graphHeight)]-1] ] != "i" } {
	    set parameters(graphHeight) [expr $parameters(graphHeight) + $parameters(topMargin) ]
	}
    }
    
    if { [string index $parameters(graphHeight) [expr [string length $parameters(graphHeight)]-1] ] != "i" } {
	if { $parameters(graphHeight) < $parameters(mingraphHeight) } {
	    
	    set parameters(graphHeight) $parameters(mingraphHeight)
	}
    } 

   if { [string index $parameters(graphHeight) [expr [string length $parameters(graphHeight)]-1] ] != "i" } {
       if { $parameters(graphWidth) < $parameters(mingraphWidth) } {
	   
	   set parameters(graphWidth) $parameters(mingraphWidth)
       }
   }
       




}





#
# Description: Formats ticks labels.  Called by blt plot widget
#              for each major tick value.
#
# Pre: win - widget
#      inJuliandate - date to be used to calculate value of major tick
#                      label
#
# Post: returns label for a tick with value inJuliandate
#
#




proc format_xAxis_ticks {win inMJuliandate} {
    variable parameters

    set calendardate [JulianToCalendar [expr $inMJuliandate + $parameters(MJDBase) ] ]

    return "$inMJuliandate\n$calendardate"

}





#
# Description: Find lower and upper limits in the list
#
#
# Pre:  inValList - list of values
#
#
# Post: Returns a list of {low high} extracted from inValList
#
#



proc GetLimits { inValList } {
    if { [llength $inValList] == 0 } {
	return {}
    }

    set low [lindex $inValList 0]
    set high [lindex $inValList 0]
    for {set indx 1} { $indx < [llength $inValList] } {incr indx} {
	set val [lindex $inValList $indx]
	if { $low > $val } {
	    set low $val
	} elseif { $high < $val } {
	    set high $val
	}
    }

    return [list $low $high]
}







#
# Description: Sets default limits in preferences structure
#
#
# Pre: 
#
#
# Post: Default limits are set
#
#



proc SetDefaultLimits { } {
    variable colData
    variable parameters
    variable visData
    variable maxexpData


    foreach label { MJD VIS ROLL PITCH MAXEXP } {
	if {  $parameters(${label}High) != "auto" && $parameters(${label}Low) != "auto" } {
	    continue
	}

	set tmpList {}

	switch $label {
	    MJD -
	    VIS {
		if { [ info exists visData ] == 1 } {
		    if { $label == "VIS" } {
			set tmpList $visData(1)
		    } elseif { $label == "MJD" } {
			set tmpList $visData(0)
		    } 
		}	
	    }
	    ROLL -
	    PITCH {
		
		if { [ info exists colData(0,$label) ] == 1 } {
		    for {set segIndx 0} { $segIndx < $colData(segmentCount) } {incr segIndx} {
			set tmpList [concat $tmpList $colData($segIndx,$label)]
		    }
		}
	    }
	    MAXEXP {
		if { [ info exists maxexpData(0) ] == 1 } {
		    set tmpList [concat $tmpList $maxexpData(1) $maxexpData(3) ] 
		}
	    }
	}
	

	set tmpList [GetLimits $tmpList]


	if { [llength $tmpList] != 2 } {
	    set tmpList { 0 1}
	} 
	set dist [expr [lindex $tmpList 1] - [lindex $tmpList 0] ]
	set low [expr -$parameters(${label}LowPad) * $dist+ [lindex $tmpList 0]]
	set high [expr $parameters(${label}HighPad) * $dist+ [lindex $tmpList 1]]
	if {  $parameters(${label}High) == "auto"  } {
	    set parameters(${label}High) $high
	    
	}
	
	if { $parameters(${label}Low) == "auto" } {
	    set parameters(${label}Low) $low	
	}
	
    }
    
}








#
# Description: Reads plot data from standard input
#
#
# Pre: 
#
#
# Post: Data is read into internal structures and preferences are initialized
#           to reflect that data if necessary            
#



proc ReadData { {inChannel stdin} } {
    variable colData
    variable badpitchData
    variable maxexpData
    variable visData
    variable parameters

    if { [ info exists colData ] == 1 } {
	unset colData
    }

    if { [ info exists badpitchData ] == 1 } {
	unset badpitchData
    }

    if { [ info exists maxexpData ] == 1 } {
	unset maxexpData
    }

    if { [ info exists visData ] == 1 } {
	unset visData
    }
    

    set line [gets $inChannel]
    if { $line != "" } {
	while { $line != "" } {

	    set line [string trim $line ]
	    
	    regsub -all {[ ]+} $line { } line

	    switch $line {
		"START UPDATE" {
		    set line [gets $inChannel] 
		    set parameters(BADPITCHUpdateDate) ""
		    while { $line != "END UPDATE" } {
			if { $line != "" } {
			    lappend parameters(BADPITCHUpdateDate) "$line"
			}
			set line [gets $inChannel]
		    }

		    regsub -all {[ ]+} $parameters(BADPITCHUpdateDate) { } line

		    if { $line == "" } {
			set parameters(BADPITCHUpdateDate) "unknown"
		    }

		}
		"START ERROR" {
		    set line [gets $inChannel] 
		    while { $line != "END ERROR" } {
			lappend parameters(errorData) "$line\n"
			set line [gets $inChannel]
		    }

		}
		"START VIS" {
		    set line [gets $inChannel] 
		    set colCount [llength [split $line | ]]
		    while { $line != "END VIS" } {
			
			set tmpTuple [split $line | ]
			
			for {set indx 0} { $indx < $colCount } {incr indx} {
			    set val [lindex $tmpTuple $indx]
			    lappend visData($indx) $val
			}
			
			set line [gets $inChannel]
			
		    }
		    
		}
		"START DATA" {
		    set line [gets $inChannel]
		    set colLabels [split $line | ]
		    set colCount [llength $colLabels]
		    set segmentIndx 0
		    set line [gets $inChannel]
		    set oldline ""
		    
		    while { $line != "END DATA" } {
			
			if { $line != "" } {
			    
			    if { $oldline == "" } {
				incr segmentIndx
			    }
			    
			    set tmpTuple [split $line | ]
			    
			    for {set indx 0} { $indx < $colCount } {incr indx} {
				
				set label [lindex $colLabels $indx]
				set val [lindex $tmpTuple $indx]
				
				lappend colData([expr $segmentIndx-1],$label) $val
			    }
			}
			
			set oldline $line
			set line [gets $inChannel]
			
		    }

		    set colData(segmentCount) $segmentIndx

		}
		
		"START BPITCH" {
		    
		    set line [gets $inChannel] 
		    set colCount [llength [split $line | ]]
		    while { $line != "END BPITCH" } {
			
			set tmpTuple [split $line | ]
			
			for {set indx 0} { $indx < $colCount } {incr indx} {
			    set val [lindex $tmpTuple $indx]
			    lappend badpitchData($indx) $val
			}
			
			set line [gets $inChannel]
			
		    }
		    
		    
		}
	    
		"START MAXEXP" {
		    set line [gets $inChannel] 
		    set colCount [llength [split $line | ]]
		    while { $line != "END MAXEXP" } {
			set tmpTuple [split $line | ]
			
			
			for {set indx 0} { $indx < $colCount } {incr indx} {
			    set val [lindex $tmpTuple $indx]
			    lappend maxexpData($indx) $val
			}
			
			set line [gets $inChannel]
			
		    }
		    
		    
		}
		
		
	    }
	    
	    set line [gets $inChannel]	
	    
	}
    }

    if { [ info exists colData(0,ROLL) ] == 1 && [ info exists colData(0,ROLL_TOL) ] == 1 } { 
	for {set segIndx 0} { $segIndx < $colData(segmentCount) } {incr segIndx} {
	    for {set indx 0} { $indx < [llength $colData($segIndx,ROLL) ] } {incr indx} {
		set val [lindex $colData($segIndx,ROLL) $indx]
		set tol [lindex $colData($segIndx,ROLL_TOL) $indx]
		set tmpVal [expr  $val + $tol ]

#		if { $tmpVal > 360 } {
#		    set tmpVal [ expr $tmpVal - 360.0]
#		} elseif { $tmpVal < 0 } {
#		    set tmpVal [ expr $tmpVal + 360.0]
#		}

		lappend colData($segIndx,ROLLHIGH) $tmpVal
		set tmpVal [expr  $val - $tol ]

#		if { $tmpVal > 360 } {
#		    set tmpVal [ expr $tmpVal - 360.0]
#		} elseif { $tmpVal < 0 } {
#		    set tmpVal [ expr $tmpVal + 360.0]
#		}

		lappend colData($segIndx,ROLLLOW) $tmpVal

	    }
	}
    }
    
    
    
    SetDefaultLimits
    
    set tmpLow 0
    set tmpHigh 1
    if { [ info exists colData(0,MJD) ] == 1 } {

	if {  $parameters(MJDLow) == "auto"  } {
	    set tmpLow [lindex $colData(0,MJD) 0]
	}
	
	if {  $parameters(MJDHigh) == "auto"  } {
	    set tmpHigh [lindex $colData([expr $colData(segmentCount)-1],MJD) end]
	}
	
    }


    if {  $parameters(MJDLow) == "auto"  } {
	set parameters(MJDLow) $tmpLow
    }
    
    if {  $parameters(MJDHigh) == "auto"  } {
	set parameters(MJDHigh) $tmpHigh
    }
    
    
    if { [ info exists colData(0,ROLLHIGH) ] == 1 && [ info exists colData(0,ROLLLOW) ] == 1 } {
	    
	if {  $parameters(ROLLHigh) == "auto" || $parameters(ROLLLow) == "auto" } {
	    
	    set tmpList {}
	    for {set segIndx 0} { $segIndx < $colData(segmentCount) } {incr segIndx} {
		tmpList append [concat $colData($segIndx,ROLLHIGH) $colData($segIndx,ROLLLOW) $colData($segIndx,PITCH)]
	    }

	    set tmpList [GetLimits $tmpList]

	    if { [llength $tmpList] != 2 } {
		return 0
	    }  
	    set dist [expr [lindex $tmpList 1] - [lindex $tmpList 0] ]
	    set low [expr  -$parameters(padlowYLimit)* $dist+[lindex $tmpList 0]]
	    set high [expr $parameters(padhighYLimit)*$dist +[lindex $tmpList 1]]
	    
	    if {  $parameters(ROLLHigh) == "auto"  } {
		set parameters(ROLLHigh) $high
		
	    }
	    
	    if { $parameters(ROLLLow) == "auto" } {
		set parameters(ROLLLow) $low	
	    }
	}
    } else {

	if {  $parameters(ROLLHigh) == "auto"  } {
	    set parameters(ROLLHigh) $tmpHigh
	    
	}
	
	if { $parameters(ROLLLow) == "auto" } {
	    set parameters(ROLLLow) $tmpLow	
	}
	
    }
    
    return 1
}






#
# Description: Prints plot data
#
#
# Pre: 
#
#
# Post: Plot data is printed to stdout
#
#



proc PrintData { } {
    variable colData
    variable badpitchData
    variable maxexpData
    variable visData

    puts "CURVES DATA"
    set tmpString ""
    if { [ info exists colData(0,MJD) ] == 1 } {
	
	append tmpString "MJD"
    }
    if { [ info exists colData(0,ROLL) ] == 1 } {
	
	append tmpString  "|ROLL"
    }

    if { [ info exists colData(0,ROLL_TOL) ] == 1 } {
	
	append tmpString "|ROLL_TOL"
    }
    if { [ info exists colData(0,PITCH) ] == 1 } {
	
	append tmpString "|PITCH"
    }
    puts $tmpString
    

    
    for {set segIndx 0} { $segIndx < $colData(segmentCount)  } {incr segIndx} {

	set rowCount 0
	if { [ info exists colData(0,MJD) ] == 1 } {
	    set rowCount [llength $colData($segIndx,MJD) ]
	} elseif { [ info exists colData(0,ROLL) ] == 1 } {
	    set rowCount [llength $colData($segIndx,ROLL) ]
	} elseif { [ info exists colData(0,VIS) ] == 1 } {
	    set rowCount [llength $colData($segIndx,VIS) ]
	} elseif { [ info exists colData(0,ROLL_TOL) ] == 1 } {
	    set rowCount [llength $colData($segIndx,ROLL_TOL) ]
	} elseif { [ info exists colData(0,PITCH) ] == 1 } {
	    set rowCount [llength $colData($segIndx,PITCH) ]
	}

	
	for {set rowIndx 0} { $rowIndx < $rowCount } {incr rowIndx} {
	
	    set tmpString ""
	    if { [ info exists colData(0,MJD) ] == 1 } {
		append tmpString  "[lindex $colData($segIndx,MJD) $rowIndx]"
	    }
	    
	    if { [ info exists colData(0,ROLL) ] == 1 } {
		append tmpString  "|[lindex $colData($segIndx,ROLL) $rowIndx]"
	    }
	    
	    if { [ info exists colData(0,ROLL_TOL) ] == 1 } {
		append tmpString  "|[lindex $colData($segIndx,ROLL_TOL) $rowIndx]"
	    }
	    
	    if { [ info exists colData(0,PITCH) ] == 1 } {
		append tmpString  "|[lindex $colData($segIndx,PITCH) $rowIndx]"
	    }
	    
	    puts $tmpString
	}
	puts ""
    }

    if { [ info exists visData(0) ] == 1 } {
	puts "VISIBILITY"
	
	for {set rowIndx 0} { $rowIndx < [llength $visData(0)] } {incr rowIndx} {
	    set tmpString ""
	    for {set colIndx 0} { $colIndx < 2 } {incr colIndx} {
		append tmpString "[lindex $visData($colIndx) $rowIndx]"
		if { $colIndx != 1 } {
		    append tmpString "|"
		}
	    }
	    puts $tmpString
	}
	puts ""

    }

    if { [ info exists badpitchData(0) ] == 1 } {

	puts "BAD PITCH"

	for {set rowIndx 0} { $rowIndx < [llength $badpitchData(0)] } {incr rowIndx} {
	    set tmpString ""
	    for {set colIndx 0} { $colIndx < 7 } {incr colIndx} {
		append tmpString "[lindex $badpitchData($colIndx) $rowIndx]"
		if { $colIndx != 6 } {
		    append tmpString "|"
		}
	    }
	    puts $tmpString
	}
	puts ""

    }

    if { [ info exists maxexpData(0) ] == 1 } {
	
	puts "MAX EXPOSURE"
	
	for {set rowIndx 0} { $rowIndx < [llength $maxexpData(0)] } {incr rowIndx} {
	    set tmpString ""
	    for {set colIndx 0} { $colIndx < 4 } {incr colIndx} {
		append tmpString "[lindex $maxexpData($colIndx) $rowIndx]"
		if { $colIndx != 3 } {
		    append tmpString "|"
		}
	    }
	    puts $tmpString
	}
	puts ""

    }


}




#
# Description: Clears the graph of all curves
#
#
# Pre: 
#
#
# Post: All curves and regions are deleted from the graph
#       
#


proc DeleteGraph { } {
    variable graph
    variable visData
    variable colData
    variable badpitchData
    variable maxexpData

    destroy $graph


    if { [ info exists colData ] == 1 } {
	unset colData
    }

    if { [ info exists badpitchData ] == 1 } {
	unset badpitchData
    }

    if { [ info exists maxexpData ] == 1 } {
	unset maxexpData
    }

    if { [ info exists visData ] == 1 } {
	unset visData
    }
    


}


#
# Description: Generates empty graph with all the axis
#
#
# Pre: 
#
#
# Post: Graph with axis is created and initialized to values 
#         from preferences.
#



proc CreateGraph { } {
    variable parameters
    variable graph

    set graph [graph .g ]


#    set majorspacing [expr int(($parameters(MJDHigh) - $parameters(MJDLow))/ 3) ]
    set majorspacing $parameters(MJDMajorSpacing)
    set ticknum [expr ($parameters(MJDHigh) - $parameters(MJDLow))/$majorspacing]
    if { $ticknum >= 4 } {
	set majorspacing [expr ($parameters(MJDHigh) - $parameters(MJDLow))/3]
    } 

    $graph axis configure x -min $parameters(MJDLow)
    $graph axis configure x -max $parameters(MJDHigh)
    $graph axis configure x -linewidth $parameters(axisLinewidth)
    $graph axis configure x -tickfont $parameters(font)
    $graph axis configure x -titlefont $parameters(font)
    $graph axis configure x -command format_xAxis_ticks
    $graph axis configure x -title $parameters(MJDTitle)
    $graph axis configure x -stepsize $majorspacing
#$parameters(MJDMajorSpacing)
    $graph axis configure x -subdivisions $parameters(MJDMinorSpacing)
    
    $graph legend configure -hide yes
    #$graph axis configure x -hide false
    #$graph axis configure ROLLy -hide false
    #$graph axis configure VISy -hide false

    $graph configure -background "#CCCCCC"
    $graph configure -borderwidth 0 -plotborderwidth 0
    $graph configure -plotpadx 0
    $graph configure -plotpady 0
    $graph configure -leftmargin  $parameters(leftMargin)
    $graph configure -rightmargin $parameters(rightMargin)
    $graph configure -topmargin $parameters(topMargin)
    $graph configure -bottommargin $parameters(bottomMargin)
    $graph configure -height $parameters(graphHeight) -width $parameters(graphWidth)

    if { [llength $parameters(errorData)] != 0 } {
	
	#	$graph axis configure x -hide true
	#	$graph axis configure y -hide true
	
	$graph configure -font $parameters(errortitlefont)
	#	$graph configure -outline $parameters(errortitlecolor)
	set title "\n\n\nPROCESSING ERROR:\n"
	foreach line $parameters(errorData) {
	    append title "$line\n"
	}
	$graph configure -title $title	

    } elseif { $parameters(graphMode) != "simple" } {
	$graph configure -font $parameters(titlefont)
	set tmpTitle "$parameters(graphTitle)|Last Pitch Restriction Update: $parameters(BADPITCHUpdateDate)"
	set linecount [llength [split $tmpTitle | ]]
	regsub -all {[|]} $tmpTitle \n title
	$graph configure -title $title
	$graph configure -topmargin [expr $linecount*25]
    } 
    







    $graph axis create ROLLy
    $graph axis configure ROLLy -min $parameters(ROLLLow)
    $graph axis configure ROLLy -max $parameters(ROLLHigh)
    $graph axis configure ROLLy -linewidth $parameters(axisLinewidth)
    $graph axis configure ROLLy -tickfont $parameters(font)
    $graph axis configure ROLLy -titlefont $parameters(font)
    $graph axis configure ROLLy -title $parameters(ROLLTitle)
    $graph axis configure ROLLy -titlecolor $parameters(ROLLColor)
    $graph axis configure ROLLy -stepsize $parameters(ROLLMajorSpacing)
    $graph axis configure ROLLy -subdivisions $parameters(ROLLMinorSpacing)
	    


    $graph axis create PITCHy
    $graph axis configure PITCHy -min $parameters(PITCHLow)
    $graph axis configure PITCHy -max $parameters(PITCHHigh)
    $graph axis configure PITCHy -linewidth $parameters(axisLinewidth)
    $graph axis configure PITCHy -tickfont $parameters(font)
    $graph axis configure PITCHy -titlefont $parameters(font)
    $graph axis configure PITCHy -title $parameters(PITCHTitle)
    $graph axis configure PITCHy -titlecolor $parameters(PITCHColor)
    $graph axis configure PITCHy -stepsize $parameters(PITCHMajorSpacing)
    $graph axis configure PITCHy -subdivisions $parameters(PITCHMinorSpacing)


   

#    $graph axis create PITCHy
#    $graph axis configure PITCHy -showticks 0
#    $graph axis configure PITCHy -linewidth 0
#    $graph axis configure PITCHy -title $parameters(PITCHTitle)
#    $graph axis configure PITCHy -titlecolor $parameters(PITCHColor)
#    $graph axis configure PITCHy -titlefont $parameters(font)  
    
    
    
    $graph yaxis use {ROLLy PITCHy}
	
    

    $graph axis create VISy

    
    $graph axis configure VISy -min $parameters(VISLow)
    $graph axis configure VISy -max $parameters(VISHigh)
    $graph axis configure VISy -linewidth $parameters(axisLinewidth)
    $graph axis configure VISy -tickfont $parameters(font)
    $graph axis configure VISy -titlefont $parameters(font)
    $graph axis configure VISy -title $parameters(VISTitle)
    $graph axis configure VISy -titlecolor $parameters(VISColor)
    $graph axis configure VISy -stepsize $parameters(VISMajorSpacing)
    $graph axis configure VISy -subdivisions $parameters(VISMinorSpacing)



#    $graph axis create MAXEXPy
#    $graph axis configure MAXEXPy -min $parameters(MAXEXPLow)
#    $graph axis configure MAXEXPy -max $parameters(MAXEXPHigh)
#    $graph axis configure MAXEXPy -linewidth $parameters(axisLinewidth)
#    $graph axis configure MAXEXPy -tickfont $parameters(font)
#    $graph axis configure MAXEXPy -titlefont $parameters(font)
#    $graph axis configure MAXEXPy -title $parameters(MAXEXPTitle)
#    $graph axis configure MAXEXPy -titlecolor $parameters(MAXEXPColor)
#    $graph axis configure MAXEXPy -stepsize $parameters(MAXEXPMajorSpacing)
#    $graph axis configure MAXEXPy -subdivisions $parameters(MAXEXPMinorSpacing)
    
#    $graph y2axis use {VISy MAXEXPy}

    $graph y2axis use {VISy }

}






#
# Description: Generates roll curve in the plot
#
#
# Pre: Roll data is initialized
#      graph is initialized
#
# Post: Roll curve is created and attributes set to values from preferences
#
#


proc CreateRollCurve { } {
    variable parameters
    variable colData
    variable graph
    

    if { $parameters(ROLLCurve) == 1 } {
	if { [ info exists colData(0,ROLL) ] == 1 } {

	    
	    #    $graph element configure ROLL -areapattern solid
	    #    $graph axis configure ROLLy -titlealternate 0
	    
	    
	    
	    for {set segIndx 0} { $segIndx < $colData(segmentCount)  } {incr segIndx} {
		
		set splitIntervals {0}
		for {set indx 0} { $indx < [expr [llength $colData($segIndx,ROLL)]-1] } {incr indx} {
		    if { [lindex $colData($segIndx,ROLLHIGH) $indx] < [lindex $colData($segIndx,ROLLLOW) [expr $indx+1]]
			 || [lindex $colData($segIndx,ROLLLOW) $indx] > [lindex $colData($segIndx,ROLLHIGH) [expr $indx+1]] } {

			lappend splitIntervals $indx
			lappend splitIntervals [expr $indx+1]
		    }
		}
		lappend splitIntervals [expr [llength $colData($segIndx,ROLL)]-1]
		
		
		if { [llength $splitIntervals] > 2 } {
		
		    foreach {startIndx endIndx} $splitIntervals {
			set tmpMJDList {}
			set tmpROLLLOWList {}
			set tmpROLLHIGHList {}
			set tmpROLLList {}
			
			for {set indx $startIndx} { $indx <= $endIndx } {incr indx} {
			    lappend tmpMJDList [lindex $colData($segIndx,MJD) $indx]
			    lappend tmpROLLList [lindex $colData($segIndx,ROLL) $indx]
			    lappend tmpROLLLOWList [lindex $colData($segIndx,ROLLLOW) $indx]
			    lappend tmpROLLHIGHList [lindex $colData($segIndx,ROLLHIGH) $indx]
			}
			
			CreateRollCurveSegment ROLL${segIndx}$startIndx $tmpMJDList $tmpROLLLOWList $tmpROLLList $tmpROLLHIGHList
		    }
		    
		} else {
		    
		    CreateRollCurveSegment ROLL$$segIndx $colData($segIndx,MJD) $colData($segIndx,ROLLLOW) $colData($segIndx,ROLL) $colData($segIndx,ROLLHIGH)
		    
		    
		}
	    }
	}	
    }
}






#
# Description: Creates a curve segment using passed arrays of values
#
#
# Pre: inId - id to be used to name curve elements
#      inX - vector of x values
#      inY1,inY2,inY3 - vectors of y values representing low,roll,high
#                        for roll curve.
#      graph is created
#
# Post: Curves are created in the plot and attributes are set to values from 
#          preferences.
#


proc CreateRollCurveSegment { inId  inX inY1 inY2 inY3} {
    variable parameters
    variable graph


    $graph element create IDA$inId -x $inX -y  $inY1   -mapy ROLLy -dashes [GetLineStyle $parameters(ROLLTOLERANCELineType) ]
    $graph element create IDB$inId -x $inX -y  $inY2   -mapy ROLLy -dashes [GetLineStyle $parameters(ROLLLineType) ]
    $graph element create IDC$inId -x $inX -y  $inY3  -mapy ROLLy -dashes [GetLineStyle $parameters(ROLLTOLERANCELineType) ]

    set parameters(ROLLLIMITSColor) $parameters(ROLLColor)

    $graph element configure IDB$inId -color $parameters(ROLLColor)
    $graph element configure IDB$inId -symbol ""
    $graph element configure IDB$inId -linewidth $parameters(ROLLLineWidth)
    
    $graph element configure IDA$inId -color $parameters(ROLLLIMITSColor)
    $graph element configure IDA$inId -symbol ""
    $graph element configure IDA$inId -linewidth $parameters(ROLLLineWidth)
    #	$graph element configure $inId -areapattern @pattern.xbm -areaforeground $parameters(ROLLColor)
    $graph element configure IDC$inId -color $parameters(ROLLLIMITSColor)
    $graph element configure IDC$inId -symbol ""
    $graph element configure IDC$inId -linewidth $parameters(ROLLLineWidth)
#    $graph element configure ID$inId -areapattern solid
#    $graph element configure ID$inId -areapattern solid -areaforeground white
		
    
    
}






#
# Description: Generates pitch curve in the plot
#
#
# Pre: pitch data is initialized
#      graph is initialized
#
# Post: Pitch curve is created and attributes set to values from preferences
#
#


proc CreatePitchCurve { } {
    variable parameters
    variable colData
    variable graph
    


    if { $parameters(PITCHCurve) == 1 } {
	if { [ info exists colData(0,PITCH) ] == 1 } {

	
	    for {set segIndx 0} { $segIndx < $colData(segmentCount)  } {incr segIndx} {
		$graph element create PITCH$segIndx -x $colData($segIndx,MJD) -y $colData($segIndx,PITCH) -mapy PITCHy -dashes [GetLineStyle $parameters(PITCHLineType) ]
		#$graph element configure PITCH$segIndx -areapattern solid -areaforeground white
		$graph element configure PITCH$segIndx -color $parameters(PITCHColor)
		$graph element configure PITCH$segIndx -symbol ""
		$graph element configure PITCH$segIndx -linewidth $parameters(PITCHLineWidth)
	    }
	}
    }
    
    
}





#
# Description: Generates visibility curve in the plot
#
#
# Pre: Visibility data is initialized
#      graph is initialized
#
# Post: Visibility curve is created and attributes set to values from preferences
#
#


proc CreateVisCurve { } {
    variable parameters
    variable visData
    variable graph
    

    if { $parameters(VISCurve) == 1 } {
	if { [ info exists visData ] == 1 } {
	    $graph element create VIS -xdata $visData(0) -ydata  $visData(1) -mapy VISy -dashes [ GetLineStyle $parameters(VISLineType) ]
	    $graph element configure VIS -color $parameters(VISColor)
	    $graph element configure VIS -symbol ""
	    $graph element configure VIS -linewidth $parameters(VISLineWidth)
	}
    }
    

}












#
# Description: Generates bad pitch regions in the plot
#
#
# Pre: Bad Pitch data is initialized
#      graph is initialized
#
# Post: Bad pitch regions are created and attributes set to values from preferences
#
#



proc CreateBadPitchRegions { } {
    variable parameters
    variable badpitchData
    variable colData
    variable graph



    if { $parameters(BADPITCHRegions) == 1 } {
	if { [ info exists badpitchData ] == 1  } {
	    
	    if { [ info exists colData(0,PITCH) ] == 1 } {
		set maxPitch 0
		
		for {set segIndx 0} { $segIndx < $colData(segmentCount)  } {incr segIndx} {
		    for {set rowIndx 0} { $rowIndx < [llength $colData($segIndx,PITCH)] } {incr rowIndx} {
			if { [lindex $colData($segIndx,PITCH) $rowIndx] > $maxPitch } {
			    set maxPitch [lindex $colData($segIndx,PITCH) $rowIndx]
			}
		    }
		}
	    }
	    
	    
	    
	    for {set rowIndx 0} { $rowIndx < [llength $badpitchData(0)]  } {incr rowIndx} {
		set x1 [lindex $badpitchData(0) $rowIndx]
		set y1 [lindex $badpitchData(1) $rowIndx]
		set x2 [lindex $badpitchData(2) $rowIndx]
		set y2 [lindex $badpitchData(3) $rowIndx]


# do not draw polygons that are outside limits as this causes
# coredump in blt when postscript is generated

		if {  ($y1  < $parameters(PITCHLow) && $y1 <  $parameters(PITCHHigh)  &&  $y2 < $parameters(PITCHLow) && $y2 < $parameters(PITCHHigh) )
		      || ($y1  > $parameters(PITCHLow) && $y1 >  $parameters(PITCHHigh)  &&  $y2 > $parameters(PITCHLow) && $y2 > $parameters(PITCHHigh) )
		      ||  ($x1 < $parameters(MJDLow) && $x1 < $parameters(MJDHigh) && $x2 < $parameters(MJDLow) && $x2 < $parameters(MJDHigh)  )
		      ||  ($x1 > $parameters(MJDLow) && $x1 > $parameters(MJDHigh) && $x2  > $parameters(MJDLow) && $x2 > $parameters(MJDHigh) ) } {
		    continue;
		    
		}

		set label [lindex $badpitchData(4) $rowIndx]
		set color [lindex $badpitchData(5) $rowIndx]
#		set fill [string tolower [lindex $badpitchData(6) $rowIndx]]
		set fillcolor $color

		switch [string tolower $parameters(BADPITCHFillType)] {
		    "solid" {
			set fill ""
		    }
		    "" -
		    "none" {
			set fillcolor ""
			set fill ""
		    }
		    default {
			set fill @$parameters(patternsLocation)/[string tolower $parameters(BADPITCHFillType)][string tolower $parameters(BADPITCHLineType)].xbm
		    }
		    
		}
		

		$graph marker create polygon -name BADPITCHMARKERA$rowIndx -coords [list $x1 $y1 $x2 $y1 $x2 $y2 $x1 $y2 $x1 $y1] -mapy PITCHy -fill $fillcolor -stipple ${fill} -outline $color -linewidth $parameters(BADPITCHLineWidth) -dashes [GetLineStyle $parameters(BADPITCHLineType) ]

		
		





		if { [ info exists maxPitch ] == 1 } {
		    $graph marker creat line -name BADPITCHMARKERB$rowIndx -coords [list $x1 $maxPitch $x1 0] -outline $color -mapy PITCHy -dashes [GetLineStyle $parameters(BADPITCHLineType) ] -linewidth $parameters(BADPITCHLineWidth)
		    $graph marker creat line -name BADPITCHMARKERC$rowIndx -coords [list $x2 $maxPitch $x2 0] -outline $color -mapy PITCHy -dashes [GetLineStyle $parameters(BADPITCHLineType) ] -linewidth $parameters(BADPITCHLineWidth)
		}
		
		$graph marker create text -name BADPITCHLABEL$rowIndx -coords [list [expr ($x1+$x2)/2.0]  [expr $y2+$parameters(BADPITCHLabelOffset)]] -mapy PITCHy -outline $color -fill "" -text $label -font $parameters(font)
	    }
	}
    }

}






#
# Description: Generates max exposure regions in the plot
#
#
# Pre: MaxExposure data is initialized
#      graph is initialized
#
# Post: Max exposure regions are created and attributes set to values from preferences
#
#



proc CreateMaxExposureRegions { } {
    variable parameters
    variable maxexpData
    variable graph



    if { $parameters(MAXEXPCurve) == 1 } {
	if { [ info exists maxexpData ] == 1 } {
	    
	    set tmpList {}
	    for {set rowIndx 0} { $rowIndx < [llength $maxexpData(0)]  } {incr rowIndx} {
		lappend tmpList [list $rowIndx [lindex $maxexpData(0) $rowIndx]]
	    }
	    
	    set tmpPolyIdList {}
	    foreach data [ lsort -index 1 $tmpList] {
		lappend tmpPolyIdList [lindex $data 0]
	    }
	    
	    set startIndx 0
	    set endIndx 0
	    for {set polyIndx 1 } { $polyIndx <= [llength $tmpPolyIdList]  } {incr  polyIndx} {
		
		set x2 [lindex $maxexpData(2) [lindex $tmpPolyIdList [expr $polyIndx-1] ] ]
		
		if { $polyIndx < [llength $tmpPolyIdList] } {
		    set nx1 [lindex $maxexpData(0) [lindex $tmpPolyIdList $polyIndx ] ]
		} else {
		    set nx1 [expr ! $x2]
		}
		
		
		set tmpPointList {}
		if { $x2 != $nx1  } {
		    for {set indx $startIndx } { $indx <= $endIndx   } {incr  indx} {
			set x1 [lindex $maxexpData(0) [lindex $tmpPolyIdList $indx] ]
			set y1 [lindex $maxexpData(1) [lindex $tmpPolyIdList $indx] ] 
			set x2 [lindex $maxexpData(2) [lindex $tmpPolyIdList $indx] ]
			
			lappend tmpPointList $x1 $y1 $x2 $y1
		    }
		    
		    for {set indx $endIndx } { $indx >= $startIndx   } {incr  indx -1 } {
			set x1 [lindex $maxexpData(0) [lindex $tmpPolyIdList $indx] ]
			set x2 [lindex $maxexpData(2) [lindex $tmpPolyIdList $indx] ]
			set y2 [lindex $maxexpData(3) [lindex $tmpPolyIdList $indx] ] 
			
			lappend tmpPointList $x2 $y2 $x1 $y2
		    }
		    

		    set fillcolor $parameters(MAXEXPColor)

		    switch [string tolower $parameters(MAXEXPFillType)] {
			"solid" {
			    set fill ""
			}
			"" -
			"none" {
			    set fillcolor ""
			    set fill ""
			}
			default {
			    set fill @$parameters(patternsLocation)/[string tolower $parameters(MAXEXPFillType)][string tolower $parameters(MAXEXPLineType)].xbm
			}
			
		    }
		    
		    
		    $graph marker create polygon -name MAXEXPMARKERA$polyIndx -coords $tmpPointList -mapy MAXEXPy -fill $parameters(MAXEXPColor) -fill $fillcolor -stipple ${fill} -outline $parameters(MAXEXPColor) -linewidth $parameters(MAXEXPLineWidth) -dashes [GetLineStyle $parameters(MAXEXPLineType) ] 
		    
		    set startIndx $polyIndx
		    set endIndx $polyIndx
		    
		} else {
		    incr endIndx
		}
	    }
	}
    }
}






#
# Description: Generates postscript into stdout
#
#
# Pre: graph is initialized
#
#
# Post: Prints postscript dump of current plot into stdout
#
#



proc GeneratePS { {outChannelId stdout} } {
    variable parameters
    variable graph

    #table . \
    #    0,0 .g  -fill both -cspan 10 -rspan 10 

    set decorations "no"
    if { $parameters(graphMode) == "simple" } {
	set decorations "yes"
    }

    $graph postscript configure \
	-center no \
	-landscape no \
	-maxpect no \
	-preview no \
	-decorations $decorations \
	-padx $parameters(pspadx) \
	-pady $parameters(pspady) \
	-height  $parameters(graphHeight) \
	-width $parameters(graphWidth) \
	-paperheight $parameters(graphHeight) \
	-paperwidth $parameters(graphWidth) 
    
#    -maxpect yes 

#    -preview yes

#wm geometry . "800x650+0+0"
    puts $outChannelId [$graph postscript output ]

#[lindex $argv 7 ] 

#
#set image [image create photo]
#$graph snap $image
#$image write [lindex $argv 7 ] -format GIF


}


#
# Description: Runs unit tests on all the routines
#
#
# Pre: 
#
#
# Post: Prints status messages for each routine
#
#


proc SelfTest { } {
    variable parameters
    variable visData
    variable colData
    variable badpitchData
    variable maxexpData
    variable graph
    variable argv0
    
    set totalTests 0
    set passedTests 0

#    puts "Unit testing for $argv0"

    incr totalTests
    puts -nonewline "${totalTests}. Testing JulianToCalendar{ inJulian }..."
    
    if { [JulianToCalendar 2454832.5] != "2009-Jan-01" } {
	puts "FAILED";
    } else {
	incr passedTests
	puts "OK";
    }


    incr totalTests
    puts -nonewline "${totalTests}. Testing InitParameters{}..."
    
    InitParameters

    if { [info exists parameters(MJDBase)] !=1 || $parameters(MJDBase) != 2400000.5 } {
	puts "FAILED";
    } else {
	incr passedTests
	puts "OK";
    }




    incr totalTests
    puts -nonewline "${totalTests}. Testing format_xAxis_ticks {win inJuliandate}..."
    
    InitParameters
    set tmpStr [format_xAxis_ticks "" [expr 2454832.5 - $parameters(MJDBase) ]]

    if { $tmpStr != "54832.0\n2009-Jan-01" } {
	puts "FAILED";
    } else {
	incr passedTests
	puts "OK";
    }



    
    incr totalTests
    puts -nonewline "${totalTests}. Testing GetLimits{ inValList }..."
    
    set tmpList [GetLimits {1 2 4 9 0} ]

    if { $tmpList != {0 9} } {
	puts "FAILED";
    } else {
	incr passedTests
	puts "OK";
    }


    
    incr totalTests
    puts -nonewline "${totalTests}. Testing SetDefaultLimits{}..."
    
    InitParameters
    SetDefaultLimits

    if { $parameters(MJDLow) != 0 || $parameters(MJDHigh) != 1 } {
	puts "FAILED";
    } else {
	incr passedTests
	puts "OK";
    }



        
    incr totalTests
    puts -nonewline "${totalTests}. Testing ReadData{}..."

    InitParameters
    set tmpFileName "$parameters(testDataPath)/SelfTest_provis_gen_graph_ReadData.data"
    if {[catch {set file [open $tmpFileName r]}] > 0 } {
	puts "Could not open data file: '$tmpFileName'"
	puts "FAILED"
    } else {
	
	ReadData $file

	if {  [lindex $visData(0) 0] != 54830.92 || [lindex $visData(1) 0]  != 0.73
	      || [lindex $visData(0) end] != 55195.85 || [lindex $visData(1) end]  != 0.75
	      || [lindex $badpitchData(0) 0] != 54830.92 || [lindex $badpitchData(1) 0]  != 120.00
	      || [lindex $badpitchData(0) end] != 54888.85 || [lindex $badpitchData(1) end]  != 160
	      || [lindex $maxexpData(0) 0] != 54830.92 || [lindex $maxexpData(1) 0]  != 30.00
	      || [lindex $maxexpData(0) end] != 54869.47 || [lindex $maxexpData(1) end]  != 10.00
	      || [lindex $colData(0,MJD) 0] != 54830.92 || [lindex $colData(0,PITCH) 0]  != 130.97
	      || [lindex $colData(1,ROLL) end] != 68.04 || [lindex $colData(1,ROLL_TOL) end]  != 8.50
	  } {
	    puts "FAILED";
	} else {
	    incr passedTests
	    puts "OK";
	}
	close $file
    }



    incr totalTests
    puts -nonewline "${totalTests}. Testing CreateGraph{}..."

    InitParameters
    set tmpFileName "$parameters(testDataPath)/SelfTest_provis_gen_graph_ReadData.data"
    set tmpOutputFileName "$parameters(testOutputPath)/SelfTest_provis_gen_graph_CreateGraph.data"
    set tmpBaseFileName "$parameters(testDataPath)/SelfTest_provis_gen_graph_CreateGraph.data"
    if {[catch {set file [open $tmpFileName r]}] > 0 } {
	puts "Could not open data file: '$tmpFileName'"
	puts "FAILED"
    } elseif {[catch {set ofile [open ${tmpOutputFileName}.tmp w]}] > 0 } {
	puts "Could not open output file: '$tmpOutputFileName'"
	puts "FAILED"
	close $file
    } else {
	
	ReadData $file
	CreateGraph
	GeneratePS $ofile
	DeleteGraph

	close $file
	close $ofile
	
	exec sed -e /BoundingBox:/d -e /CreationDate:/d -e "/including file/d" ${tmpOutputFileName}.tmp  > ${tmpOutputFileName}
	file delete ${tmpOutputFileName}.tmp

	if { [catch {exec diff  $tmpOutputFileName $tmpBaseFileName } tmpVal ]  } {
	    catch { [exec diff  $tmpOutputFileName $tmpBaseFileName > "$parameters(testOutputPath)/SelfTest_provis_gen_graph_CreateGraph.diff"]}
	    puts "FAILED";
	} else {
	    incr passedTests
	    puts "OK";
	    file delete $tmpOutputFileName
	}

    }



    incr totalTests
    puts -nonewline "${totalTests}. Testing CreateRollCurveSegment{}..."
    
    InitParameters
    set tmpFileName "$parameters(testDataPath)/SelfTest_provis_gen_graph_ReadData.data"
    set tmpOutputFileName "$parameters(testOutputPath)/SelfTest_provis_gen_graph_CreateRollCurveSegment.data"
    set tmpBaseFileName "$parameters(testDataPath)/SelfTest_provis_gen_graph_CreateRollCurveSegment.data"
    if {[catch {set file [open $tmpFileName r]}] > 0 } {
	puts "Could not open data file: '$tmpFileName'"
	puts "FAILED"
    } elseif {[catch {set ofile [open ${tmpOutputFileName}.tmp w]}] > 0 } {
	puts "Could not open output file: '$tmpOutputFileName'"
	puts "FAILED"
	close $file
    } else {

	ReadData $file
	CreateGraph
	CreateRollCurveSegment "ID" {54900 55000 55100} {10 20 30} { 20 40 60} {30 60 90}
	GeneratePS $ofile
	DeleteGraph

	close $file
	close $ofile

	exec sed -e /BoundingBox:/d -e /CreationDate:/d -e "/including file/d" ${tmpOutputFileName}.tmp  > ${tmpOutputFileName}

	file delete ${tmpOutputFileName}.tmp

	if { [catch { exec diff  $tmpOutputFileName $tmpBaseFileName } tmpVal ] } {
	    catch { [exec diff  $tmpOutputFileName $tmpBaseFileName > "$parameters(testOutputPath)/SelfTest_provis_gen_graph_CreateRollCurveSegment.diff"]}
	    puts "FAILED";
	} else {
	    incr passedTests
	    puts "OK";
	    file delete $tmpOutputFileName
	}

    }


    
    incr totalTests
    puts -nonewline "${totalTests}. Testing CreateRollCurve{}..."
    
    InitParameters
    set tmpFileName "$parameters(testDataPath)/SelfTest_provis_gen_graph_ReadData.data"
    set tmpOutputFileName "$parameters(testOutputPath)/SelfTest_provis_gen_graph_CreateRollCurve.data"
    set tmpBaseFileName "$parameters(testDataPath)/SelfTest_provis_gen_graph_CreateRollCurve.data"
    if {[catch {set file [open $tmpFileName r]}] > 0 } {
	puts "Could not open data file: '$tmpFileName'"
	puts "FAILED"
    } elseif {[catch {set ofile [open ${tmpOutputFileName}.tmp w]}] > 0 } {
	puts "Could not open output file: '$tmpOutputFileName'"
	puts "FAILED"
	close $file
    } else {

	ReadData $file
	CreateGraph
	CreateRollCurve
	GeneratePS $ofile
	DeleteGraph
	close $file
	close $ofile


	exec sed -e /BoundingBox:/d -e /CreationDate:/d -e "/including file/d" ${tmpOutputFileName}.tmp  > ${tmpOutputFileName}

	file delete ${tmpOutputFileName}.tmp

	if { [ catch { exec diff  $tmpOutputFileName $tmpBaseFileName } tmpVal ] } {
	    catch { [exec diff  $tmpOutputFileName $tmpBaseFileName > "$parameters(testOutputPath)/SelfTest_provis_gen_graph_CreateRollCurve.diff"]}
	    puts "FAILED";
	} else {
	    incr passedTests
	    puts "OK";
	    file delete $tmpOutputFileName
	}

    }



    incr totalTests
    puts -nonewline "${totalTests}. Testing CreatePitchCurve{}..."
    
    InitParameters
    set tmpFileName "$parameters(testDataPath)/SelfTest_provis_gen_graph_ReadData.data"
    set tmpOutputFileName "$parameters(testOutputPath)/SelfTest_provis_gen_graph_CreatePitchCurve.data"
    set tmpBaseFileName "$parameters(testDataPath)/SelfTest_provis_gen_graph_CreatePitchCurve.data"
    if {[catch {set file [open $tmpFileName r]}] > 0 } {
	puts "Could not open data file: '$tmpFileName'"
	puts "FAILED"
    } elseif {[catch {set ofile [open ${tmpOutputFileName}.tmp w]}] > 0 } {
	puts "Could not open output file: '$tmpOutputFileName'"
	puts "FAILED"
	close $file
    } else {
	
	ReadData $file
	CreateGraph
	CreatePitchCurve
	GeneratePS $ofile
	DeleteGraph

	close $file
	close $ofile

	exec sed -e /BoundingBox:/d -e /CreationDate:/d -e "/including file/d" ${tmpOutputFileName}.tmp  > ${tmpOutputFileName}

	file delete ${tmpOutputFileName}.tmp

	catch { [exec diff  $tmpOutputFileName $tmpBaseFileName | wc -l ]} tmpVal

	if { [catch {exec diff  $tmpOutputFileName $tmpBaseFileName } tmpVal ] } {
	    catch { [exec diff  $tmpOutputFileName $tmpBaseFileName > "$parameters(testOutputPath)/SelfTest_provis_gen_graph_CreatePitchCurve.diff"]}
	    puts "FAILED";
	} else {
	    incr passedTests
	    puts "OK";
	    file delete $tmpOutputFileName
	}
	
    }






    
    incr totalTests
    puts -nonewline "${totalTests}. Testing CreateVisCurve{}..."
    
    InitParameters
    set tmpFileName "$parameters(testDataPath)/SelfTest_provis_gen_graph_ReadData.data"
    set tmpOutputFileName "$parameters(testOutputPath)/SelfTest_provis_gen_graph_CreateVisCurve.data"
    set tmpBaseFileName "$parameters(testDataPath)/SelfTest_provis_gen_graph_CreateVisCurve.data"
    if {[catch {set file [open $tmpFileName r]}] > 0 } {
	puts "Could not open data file: '$tmpFileName'"
	puts "FAILED"
    } elseif {[catch {set ofile [open ${tmpOutputFileName}.tmp w]}] > 0 } {
	puts "Could not open output file: '$tmpOutputFileName'"
	puts "FAILED"
	close $file
    } else {
	
	ReadData $file
	CreateGraph
	CreateVisCurve
	GeneratePS $ofile
	DeleteGraph

	close $file
	close $ofile


	exec sed -e /BoundingBox:/d -e /CreationDate:/d -e "/including file/d" ${tmpOutputFileName}.tmp  > ${tmpOutputFileName}

	file delete ${tmpOutputFileName}.tmp

	catch { [exec diff  $tmpOutputFileName $tmpBaseFileName | wc -l ]} tmpVal

	if { [catch {exec diff  $tmpOutputFileName $tmpBaseFileName } tmpVal ] } {
	    catch { [exec diff  $tmpOutputFileName $tmpBaseFileName > "$parameters(testOutputPath)/SelfTest_provis_gen_graph_CreateVisCurve.diff"]}
	    puts "FAILED";
	} else {
	    incr passedTests
	    puts "OK";
	    file delete $tmpOutputFileName
	}
	
    }





    incr totalTests
    puts -nonewline "${totalTests}. Testing CreateBadPitchRegions{}..."
    
    InitParameters
    set tmpFileName "$parameters(testDataPath)/SelfTest_provis_gen_graph_ReadData.data"
    set tmpOutputFileName "$parameters(testOutputPath)/SelfTest_provis_gen_graph_CreateBadPitchRegions.data"
    set tmpBaseFileName "$parameters(testDataPath)/SelfTest_provis_gen_graph_CreateBadPitchRegions.data"
    if {[catch {set file [open $tmpFileName r]}] > 0 } {
	puts "Could not open data file: '$tmpFileName'"
	puts "FAILED"
    } elseif {[catch {set ofile [open ${tmpOutputFileName}.tmp w]}] > 0 } {
	puts "Could not open output file: '$tmpOutputFileName'"
	puts "FAILED"
	close $file
    } else {
	
	ReadData $file
	CreateGraph
	CreateBadPitchRegions
	GeneratePS $ofile
	DeleteGraph

	close $file
	close $ofile

	exec sed -e /BoundingBox:/d -e /CreationDate:/d -e "/including file/d" ${tmpOutputFileName}.tmp  > ${tmpOutputFileName}

	file delete ${tmpOutputFileName}.tmp

	catch { [exec diff  $tmpOutputFileName $tmpBaseFileName | wc -l ]} tmpVal

	if { [catch {exec diff  $tmpOutputFileName $tmpBaseFileName } tmpVal ] } {
	    catch { [exec diff  $tmpOutputFileName $tmpBaseFileName > "$parameters(testOutputPath)/SelfTest_provis_gen_graph_CreateBadPitchRegions.diff"]}
	    puts "FAILED";
	} else {
	    incr passedTests
	    puts "OK";
	    file delete $tmpOutputFileName
	}
	
    }



#    incr totalTests
#    puts -nonewline "${totalTests}. Testing CreateMaxExposureRegions{}..."
    
#    InitParameters
#    set tmpFileName "$parameters(testDataPath)/SelfTest_provis_gen_graph_ReadData.data"
#    set tmpOutputFileName "$parameters(testOutputPath)/SelfTest_provis_gen_graph_CreateMaxExposureRegions.data.[pid]"
#    set tmpBaseFileName "$parameters(testDataPath)/SelfTest_provis_gen_graph_CreateMaxExposureRegions.data"
#    if {[catch {set file [open $tmpFileName r]}] > 0 } {
#	puts "Could not open data file: '$tmpFileName'"
#	puts "FAILED"
#    } elseif {[catch {set ofile [open $tmpOutputFileName w]}] > 0 } {
#	puts "Could not open output file: '$tmpOutputFileName'"
#	puts "FAILED"
#	close $file
#    } else {
	
#	ReadData $file
#	CreateGraph
#	CreateMaxExposureRegions
#	GeneratePS $ofile
#	DeleteGraph

#	close $file
#	close $ofile


#	catch { [exec diff  $tmpOutputFileName $tmpBaseFileName | wc -l ]} tmpVal

#	if { $tmpVal != "4\nchild process exited abnormally" } {
#	    catch { [exec diff  $tmpOutputFileName $tmpBaseFileName > "$parameters(testOutputPath)/SelfTest_provis_gen_graph_CreateMaxExposureRegions.diff"]}
#	    puts "FAILED";
#	} else {
#	    incr passedTests
#	    puts "OK";
#	    file delete $tmpOutputFileName
#	}
	
#    }













    incr totalTests
    puts -nonewline "${totalTests}. Testing DeleteGraph{}..."
    
    InitParameters
    set tmpFileName "$parameters(testDataPath)/SelfTest_provis_gen_graph_ReadData.data"

    if {[catch {set file [open $tmpFileName r]}] > 0 } {
	puts "Could not open data file: '$tmpFileName'"
	puts "FAILED"
    } else {
	
	ReadData $file

	if { [ info exists colData ] != 1 
	     || [ info exists badpitchData ] != 1 
	     || [ info exists maxexpData ] != 1
	     || [ info exists visData ] != 1 } {

	    puts "FAILED"
	} else {
	    
	    
	    CreateGraph
	    CreateVisCurve
	    CreatePitchCurve
	    CreateRollCurve
	    CreateBadPitchRegions
#	    CreateMaxExposureRegions
	    DeleteGraph
	    
	    if { [ info exists colData ] == 1 
		 || [ info exists badpitchData ] == 1 
		 || [ info exists maxexpData ] == 1
		 || [ info exists visData ] == 1 } {
		puts "FAILED";
	    } else {
		incr passedTests
		puts "OK";

	    }
	}
	
    }













    puts "SUMMARY: $passedTests out of $totalTests tests passed"

    if { $passedTests != $totalTests } {
	exit 1
    }
    exit 0
}



# Initialize the environment


InitParameters

if { $parameters(selfTestMode) == 1 } {
    exit [SelfTest]

} else {
    
    if { [ReadData] != 1 } {
    puts stderr "Error: $argv0 - ReadData failed." 
	exit 1
    }
    #PrintData
    
    
    # Initialize graph
    
    CreateGraph

    if { [llength $parameters(errorData) ] == 0 } {
	# Generate all the curves in the graph


    
	CreateVisCurve
	CreatePitchCurve
	CreateRollCurve

	CreateBadPitchRegions
#	CreateMaxExposureRegions
	

    }

    # Generate postscript into stdout

    GeneratePS
}

exit 0
