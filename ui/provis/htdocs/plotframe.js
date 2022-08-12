// _INSERT_SAO_COPYRIGHT_HERE_(2008)_
// _INSERT_GPL_LICENSE_HERE_

var busyFlag=0;
var disabledFlag=0;
var verticalPlotPixelMarginTop=20;
var verticalPlotPixelMarginBottom=78;
var horizontalPlotPixelMarginLeft=149;
var horizontalPlotPixelMarginRight=63

var plotPixelX1=0;
var plotPixelX2=0;
var plotPixelY1=0;
var plotPixelY2=0;
var plotPixelWidth=0;
var plotPixelHeight=0;

var objIds = new Array();

var origPlotProps = new top.PlotProp();
var curPlotProps = new top.PlotProp();
var oldPlotProps = new top.PlotProp();
var newPlotProps = new top.PlotProp();
var resetOrigProps=0;
var overwriteInputFlag=0;

var curjDate;
var curPitch;
var curPitchType;
var curRoll;
var curRollHigh;
var curRollLow;
var curVis;
var curMaxExp;

var selectionIds = new Array();
var selectionArray = new Array();

var timeoutId;


var lastMouseX=0;
var lastMouseY=0;



var browserImageHeightCorrection=0;
var browserImageWidthCorrection=0;

var browserImageLocationXCorrection=0;
var browserImageLocationYCorrection=0;


var browser = navigator.appName;
var userAgent = navigator.userAgent;
if ( browser == "Netscape" )
{
  if ( userAgent.indexOf("Netscape") != -1 )
    {
      
      browserImageHeightCorrection=20;
      browserImageWidthCorrection=20;  
      
      browserImageLocationXCorrection=0;
      browserImageLocationYCorrection=1;
      
    }
  else if ( userAgent.indexOf("Firefox") != -1 )
    {

      if (userAgent.indexOf("Windows") != -1)
	{
	  browserImageLocationXCorrection=0;
	  browserImageLocationYCorrection=1;
	}
      else
	{
	  browserImageLocationXCorrection=0;
	  browserImageLocationYCorrection=0;
	};
    }
  else if ( userAgent.indexOf("SeaMonkey") != -1)
    {
      browserImageHeightCorrection=1;
      browserImageWidthCorrection=1; 
      browserImageLocationXCorrection=0;
      browserImageLocationYCorrection=-1;
    }
  else if ( userAgent.indexOf("Safari") != -1)
    {
	browserImageHeightCorrection=0;
	browserImageWidthCorrection=0;

       browserImageLocationXCorrection=2;
       browserImageLocationYCorrection=2;

    };

}
else if ( browser=="Microsoft Internet Explorer")
{

  browserImageHeightCorrection=10;
  browserImageWidthCorrection=10;

  browserImageLocationXCorrection=1;
  browserImageLocationYCorrection=1;

}


var movelimitsxdownOn = new Image();
movelimitsxdownOn.src='images/leftarrow.bmp';
var movelimitsxdownOff = new Image();
movelimitsxdownOff.src='images/leftarrow1.bmp';


var zoominlimitsxOn = new Image();
zoominlimitsxOn.src='images/zoomin.bmp';
var zoominlimitsxOff = new Image();
zoominlimitsxOff.src='images/zoomin1.bmp';


var zoomresetlimitsxOn  = new Image();
zoomresetlimitsxOn.src='images/zoomreset.bmp';
var zoomresetlimitsxOff  = new Image();
zoomresetlimitsxOff.src='images/zoomreset1.bmp';


var zoomoutlimitsxOn = new Image();
zoomoutlimitsxOn.src='images/zoomout.bmp';
var zoomoutlimitsxOff = new Image();
zoomoutlimitsxOff.src='images/zoomout1.bmp';


var movelimitsxupOn = new Image();
movelimitsxupOn.src='images/rightarrow.bmp';
var movelimitsxupOff = new Image();
movelimitsxupOff.src='images/rightarrow1.bmp';


var blank = new Image();
blank.src='images/blank.png';

var cross_Red = new Image();
cross_Red.src='images/cross_Red.png';
var cross_Blue = new Image();
cross_Blue.src='images/cross_Blue.png';
var cross_Green = new Image();
cross_Green.src='images/cross_Green.png';
var cross_Yellow = new Image();
cross_Yellow.src='images/cross_Yellow.png';
var cross_Black = new Image();
cross_Black.src='images/cross_Black.png';
var cross_Magenta = new Image();
cross_Magenta.src='images/cross_Magenta.png';
var cross_Cyan = new Image();
cross_Cyan.src='images/cross_Cyan.png';


var lowlimit_Red = new Image();
lowlimit_Red.src='images/lowlimit_Red.png';
var lowlimit_Blue = new Image();
lowlimit_Blue.src='images/lowlimit_Blue.png';
var lowlimit_Green = new Image();
lowlimit_Green.src='images/lowlimit_Green.png';
var lowlimit_Yellow = new Image();
lowlimit_Yellow.src='images/lowlimit_Yellow.png';
var lowlimit_Black = new Image();
lowlimit_Black.src='images/lowlimit_Black.png';
var lowlimit_Magenta = new Image();
lowlimit_Magenta.src='images/lowlimit_Magenta.png';
var lowlimit_Cyan = new Image();
lowlimit_Cyan.src='images/lowlimit_Cyan.png';



var highlimit_Red = new Image();
highlimit_Red.src='images/highlimit_Red.png';
var highlimit_Blue = new Image();
highlimit_Blue.src='images/highlimit_Blue.png';
var highlimit_Green = new Image();
highlimit_Green.src='images/highlimit_Green.png';
var highlimit_Yellow = new Image();
highlimit_Yellow.src='images/highlimit_Yellow.png';
var highlimit_Black = new Image();
highlimit_Black.src='images/highlimit_Black.png';
var highlimit_Magenta = new Image();
highlimit_Magenta.src='images/highlimit_Magenta.png';
var highlimit_Cyan = new Image();
highlimit_Cyan.src='images/highlimit_Cyan.png';

var selectionArea = new Image();
selectionArea.src = "images/selection.png"




objIds[0]='plot';
objIds[1]='movelimitsxdown';
objIds[2]='zoominlimitsx';
objIds[3]='zoomresetlimitsx';
objIds[4]='zoomoutlimitsx';
objIds[5]='movelimitsxup';


function UpdateInput()
{
  var tmpArray = new Array();
  var tmpString = new String();
  
  tmpString = top.FromJulian(curPlotProps.lX);
  
  tmpArray=tmpString.split("/");
  top.inputframe.SetSYear(tmpArray[0]);
  top.inputframe.SetSMonth(tmpArray[1]);
  top.inputframe.SetSDay(tmpArray[2]);
  
  tmpString = top.FromJulian(curPlotProps.hX);
  tmpArray=tmpString.split("/");
  top.inputframe.SetEYear(tmpArray[0]);
  top.inputframe.SetEMonth(tmpArray[1]);
  top.inputframe.SetEDay(tmpArray[2]);
  
  top.inputframe.SetTargetCoords(curPlotProps.targetcoords);
  top.inputframe.SetTargetName(curPlotProps.targetname);
}




function IsChanged(inNewInputParamsFlag)
{
 
  var tmpProps = GetUpdatedPlotProps(inNewInputParamsFlag);

  
  return (! curPlotProps.EqObj(tmpProps));

}

function GetPlotProps()
{
  return curPlotProps;
}


function GetUpdatedPlotProps(inNewInputParamsFlag)
{
  var tmpProps = new top.PlotProp();

  if ( inNewInputParamsFlag )
    {
      tmpProps.targetname=top.inputframe.GetTargetName();
      tmpProps.targetcoords=top.inputframe.GetTargetCoords();
      tmpProps.lX=top.ToJulian(top.inputframe.GetSYear(),top.inputframe.GetSMonth(),top.inputframe.GetSDay());
      tmpProps.hX=top.ToJulian(top.inputframe.GetEYear(),top.inputframe.GetEMonth(),top.inputframe.GetEDay());

    }
  else
    {
      tmpProps.targetname=curPlotProps.targetname;
      tmpProps.targetcoords=curPlotProps.targetcoords;
      tmpProps.lX=curPlotProps.lX;
      tmpProps.hX=curPlotProps.hX;
    }
  
  
  tmpProps.calfile=top.optionsframe.GetActualCalFile();



  tmpProps.jlX=1.0*top.julianBase+tmpProps.lX;
  tmpProps.jhX=1.0*top.julianBase+tmpProps.hX;

  tmpProps.jbase=curPlotProps.jbase;
  tmpProps.lP=curPlotProps.lP;
  tmpProps.hP=curPlotProps.hP;
  tmpProps.lR=curPlotProps.lR;
  tmpProps.hR=curPlotProps.hR;
  tmpProps.lV=curPlotProps.lV;
  tmpProps.hV=curPlotProps.hV;
  tmpProps.lME=curPlotProps.lME;
  tmpProps.hME=curPlotProps.hME;
  tmpProps.viewpitch=top.optionsframe.GetViewpitch();
  tmpProps.viewroll=top.optionsframe.GetViewroll();
  tmpProps.viewvisibility=top.optionsframe.GetViewvisibility();
  tmpProps.viewbadpitch=top.optionsframe.GetViewbadpitch();
  tmpProps.viewmaxexposure=top.optionsframe.GetViewmaxexposure();
  tmpProps.pitchcolor=top.optionsframe.GetPitchcolor();
  tmpProps.pitchlinestyle=top.optionsframe.GetPitchlinestyle();
  tmpProps.rollcolor=top.optionsframe.GetRollcolor();
  tmpProps.rolllinestyle=top.optionsframe.GetRolllinestyle();
  tmpProps.rolltolerancelinestyle=top.optionsframe.GetRollTolerancelinestyle();
  tmpProps.visibilitycolor=top.optionsframe.GetVisibilitycolor();
  tmpProps.visibilitylinestyle=top.optionsframe.GetVisibilitylinestyle();
  tmpProps.badpitchlinestyle=top.optionsframe.GetBadpitchlinestyle();
  tmpProps.badpitchfillstyle=top.optionsframe.GetBadpitchfillstyle();
  tmpProps.maxexpcolor=top.optionsframe.GetMaxexposurecolor();
  tmpProps.maxexplinestyle=top.optionsframe.GetMaxexposurelinestyle();
  tmpProps.maxexpfillstyle=top.optionsframe.GetMaxexposurefillstyle();
  tmpProps.exptime=top.optionsframe.GetExposureTime();


  if ( top.optionsframe.GetAutofit() )
    {
      tmpProps.width=top.plotframe.GetImageWidth();
      tmpProps.height=top.plotframe.GetImageHeight();
    }
  else
    {
      tmpProps.width=curPlotProps.width;
      tmpProps.height=curPlotProps.height;
    };

  tmpProps.framewith=top.GetFrameWidth('plotframe');
  tmpProps.frameheight=top.GetFrameHeight('plotframe');
  

  tmpProps.fonttype=top.optionsframe.GetFonttype();
  tmpProps.fontsize=top.optionsframe.GetFontsize();
  tmpProps.axislinewidth=top.optionsframe.GetAxislinewidth();
  tmpProps.pitchlinewidth=top.optionsframe.GetPitchlinewidth();
  tmpProps.rolllinewidth=top.optionsframe.GetRolllinewidth();
  tmpProps.visibilitylinewidth=top.optionsframe.GetVisibilitylinewidth();
  tmpProps.badpitchlinewidth=top.optionsframe.GetBadpitchlinewidth();
  tmpProps.maxexplinewidth=top.optionsframe.GetMaxexposurelinewidth();


  if ( tmpProps.width < top.minplotWidth )
    {
      tmpProps.width = top.minplotWidth;
    };
  
  if ( tmpProps.height < top.minplotHeight )
    {
      tmpProps.height = top.minplotHeight;
    }


  if ( tmpProps.width > top.maxplotWidth )
    {
      tmpProps.width = top.maxplotWidth;
    };
  
  if ( tmpProps.height > top.maxplotHeight )
    {
      tmpProps.height = top.maxplotHeight;
    } 
    



  return tmpProps;

}


function ValidatePlotProps(inPlotProps)
{
  
  if ( inPlotProps.targetcoords == "" || isNaN(inPlotProps.lX) || isNaN(inPlotProps.hX))
    {
      return false;
    }

  return true;
}

function SetPitchUpdateDate(inDate)
{
  var curplottitle=top.plotframe.document.getElementById('plottable').rows[0].cells[0].innerHTML;

  if ( inDate == "")
    {
      inDate="unknown";
    };

  document.getElementById('plottable').rows[0].cells[0].innerHTML=curplottitle.replace(/Last Pitch Restriction Update:.*[Ss][Pp][Aa][Nn]>/,"Last Pitch Restriction Update: "+inDate+"</span>");



}

function SetPlotTitle(inPlotProps)
{ 

  var title;

  title ="Plot for ";
  
  if (typeof inPlotProps == 'undefined' ) 
    {
      
      if ( curPlotProps.targetname != "" )
	{
	  title = title + "Target: '"+curPlotProps.targetname+"'  ";
	};
      
      title = title + "Coords: '"+curPlotProps.targetcoords+"'  ";
      
      title = title + "Time Interval0: "+ top.FromJulian(curPlotProps.lX)+"-"+top.FromJulian(curPlotProps.hX);

      if ( curPlotProps.calfile != "" )
	{
	  title = title + "<br>Calibration file: '"+curPlotProps.calfile+"'";
	};

    }
  else
    {

      if ( inPlotProps.targetname != "" )
	{
	  title = title + "Target: '"+inPlotProps.targetname+"'  ";
	};
      
      title = title + "Coords: '"+inPlotProps.targetcoords+"'  ";
      
      title = title + "Time Interval1: "+ top.FromJulian(inPlotProps.lX)+"-"+top.FromJulian(inPlotProps.hX);

      if (  top.optionsframe.GetCalFile() != "" )
	{
	  title = title + "<br>Calibration file: '"+inPlotProps.calfile+"'";
	}


    }
  
  //title = title + "<br>Last Pitch Restriction Update:            ";

  document.getElementById('plottable').rows[0].cells[0].innerHTML='<small><span style="font-weight: bold;">'+title+'</span></small>';

  //  document.getElementById('plot').title=title;


  if ( top.DataLoaded())
    {
      //top.plotframe.SetPitchUpdateDate(top.dataframe.pitch_restriction_update_date);
    };

  

  
}


function GetImageHeight()
{
  var imageHeight;



  if (top.document.getElementById('plotframe').scrollHeight != document.getElementById('plottable').offsetHeight )
    {
      imageHeight = top.document.getElementById('plotframe').scrollHeight;
    }
  else
    {
      imageHeight = document.getElementById('plottable').offsetHeight
    }


  imageHeight = imageHeight - document.getElementById('plottabler0').offsetHeight
    -document.getElementById('plottabler1').offsetHeight
    -document.getElementById('plottabler2').offsetHeight
    -document.getElementById('plottabler4').offsetHeight-6-browserImageHeightCorrection;




  if ( imageHeight < top.minplotHeight )
    {
      imageHeight = top.minplotHeight;
    }

  if ( imageHeight > top.maxplotHeight )
    {
      imageHeight = top.maxplotHeight;
    } 
    

  return imageHeight;
}


function GetImageWidth()
{
  var imageWidth;


  if (top.document.getElementById('plotframe').scrollWidth != document.getElementById('plottable').offsetWidth )
    {
      imageWidth = top.document.getElementById('plotframe').scrollWidth;
    }
  else
    {
      imageWidth = document.getElementById('plottable').offsetWidth
    }

  imageWidth = imageWidth-4-browserImageWidthCorrection;

  if ( imageWidth < top.minplotWidth )
    {
      imageWidth = top.minplotWidth;
    };
  
  if ( imageWidth > top.maxplotWidth )
    {
      imageWidth = top.maxplotWidth;
    };

  return imageWidth;
}



function GenerateImageSrc(inPlotProps)
{

  var tmpCoords= inPlotProps.targetcoords.replace(/\+/g, "%2B");

  var calFile=top.optionsframe.GetCalFile();
  var dummy="";

  if ( calFile != "" )
    {
      top.uploadCounter= top.uploadCounter+1;
      dummy = top.uploadCounter;
    };
  
  
  var tmpSrc = top.provisWebServer+top.provisCGIScript+'?type=png'
    +'&cf='+calFile+'&dummy='+dummy
    +'&c='+tmpCoords
    +'&title= '
    +'&jb='+inPlotProps.jbase
    +'&sjd='+inPlotProps.jlX
    +'&ejd='+inPlotProps.jhX
    +'&pf='+inPlotProps.viewpitch
    +'&plow='+inPlotProps.lP
    +'&phigh='+inPlotProps.hP
    +'&pcolor='+inPlotProps.pitchcolor
    +'&pline='+inPlotProps.pitchlinestyle
    +'&pwidth='+inPlotProps.pitchlinewidth
    +'&rf='+inPlotProps.viewroll
    +'&rlow='+inPlotProps.lR
    +'&rhigh='+inPlotProps.hR
    +'&rcolor='+inPlotProps.rollcolor
    +'&rline='+inPlotProps.rolllinestyle
    +'&rwidth='+inPlotProps.rolllinewidth
    +'&rtline='+inPlotProps.rolltolerancelinestyle
    +'&vf='+inPlotProps.viewvisibility
    +'&vlow='+inPlotProps.lV
    +'&vhigh='+inPlotProps.hV
    +'&vcolor='+inPlotProps.visibilitycolor
    +'&vline='+inPlotProps.visibilitylinestyle
    +'&vwidth='+inPlotProps.visibilitylinewidth
    +'&mf='+inPlotProps.viewmaxexposure
    +'&mlow='+inPlotProps.lME
    +'&mhigh='+inPlotProps.hME
    +'&mcolor='+inPlotProps.maxexpcolor
    +'&mline='+inPlotProps.maxexplinestyle
    +'&mwidth='+inPlotProps.maxexplinewidth
    +'&mfill='+inPlotProps.maxexpfillstyle
    +'&bf='+inPlotProps.viewbadpitch
    +'&bline='+inPlotProps.badpitchlinestyle
    +'&bwidth='+inPlotProps.badpitchlinewidth
    +'&bfill='+inPlotProps.badpitchfillstyle
    +'&mt='+inPlotProps.exptime
    +'&width='+inPlotProps.width
    +'&height='+inPlotProps.height
    +'&font='+inPlotProps.fonttype+' '+inPlotProps.fontsize
    +'&axisLinewidth='+inPlotProps.axislinewidth;

  return tmpSrc;
}

function Unload()
{
  top.PlotLoaded(0);
  top.dataframe.Hide(1);
  top.dataframe.Clear();
  top.LoadFrame(top.plotframe,top.welcomeLocation);
  //  top.LoadFrame(top.title,top.titleframeLocation);
  busyFlag = 0;
  top.Disable(0);
}

function GetPlot( inPlotProps,inResetOrigProps)
{
  
  if ( typeof inResetOrigProps == 'undefined')
    {
      resetOrigProps=0;
    }
  else
    {
      resetOrigProps=inResetOrigProps;
    };
  
  if ( inPlotProps.targetcoords == "" )
    {
      Unload();
      return;
    }


  SetPlotTitle(inPlotProps);

  var reqCount=0;
  top.requests.length=0;
  top.requestclearFlag=1;
  top.requestenableFlag=0;		

  

  if ( top.optionsframe.GetCalFile() != "" )
    {  

      if ( !top.dataframe.Compare(inPlotProps))
	{
	  reqCount++;
	  top.requests[0]=top.dataframe.GetPlotData;
	  top.requests[1]=inPlotProps;
	  top.requests[2]="data";
	};
      
      if ( top.optionsframe.GetViewplotdata() )
	{
	  top.dataframe.Hide(0);	  
	};
    }
  else
    {
      top.dataframe.GetPlotData(inPlotProps);
    };

  

  if ( top.optionsframe.GetAutofit() )
    {
      inPlotProps.width=GetImageWidth();
      inPlotProps.height=GetImageHeight();
    };
    
  inPlotProps.framewith=top.GetFrameWidth('plotframe');
  inPlotProps.frameheight=top.GetFrameHeight('plotframe');


  if ( inPlotProps.width < top.minplotWidth )
    {
      inPlotProps.width = top.minplotWidth;
    };

   if ( inPlotProps.height < top.minplotHeight )
    {
      inPlotProps.height = top.minplotHeight;
    }

  if ( inPlotProps.width > top.maxplotWidth )
    {
      inPlotProps.width = top.maxplotWidth;
    };
  
  if ( inPlotProps.height > top.maxplotHeight )
    {
      inPlotProps.height = top.maxplotHeight;
    } 


  var tmpSrc =  GenerateImageSrc(inPlotProps);
  var curSrc = document.getElementById('plot').src;
  curSrc = curSrc.replace(/%20/g," ");



  if ( curSrc != tmpSrc )
    {
      DisplaySelection();
      newPlotProps.CopyObj(inPlotProps);

      if ( top.optionsframe.GetCalFile() != "" )
	{  
	  top.requests[top.requests.length]=LoadImage;
	  top.requests[top.requests.length]=tmpSrc;
	  top.requests[top.requests.length]="plot";
	  reqCount++;
	}
      else
	{
	  clearTimeout(timeoutId);
	  timeoutId=setTimeout("HandleRequestTimeout();",top.timeout);
	  document.getElementById('plot').setAttribute('src',tmpSrc);
	};
    }
  else
    {
      busyFlag=0;
      if ( !reqCount )
	{
	  top.Disable(0);
	};
    };

  if ( reqCount)
    {
      top.optionsframe.UploadCalFile(reqCount);
    };

}

function LoadImage(inSrc)
{
  top.plotframe.document.getElementById('plot').src = inSrc;
}


function HandleRequestTimeout()
{
  Unload();
  alert("Your request timed out. Please try again");
}

function GeneratePlot(inPlotProps,inResetOrigPropFlag )
{


  if ( busyFlag == 1 ) 
    {
      return ;
    };
  busyFlag =1;
  top.Disable(1);


  if ( typeof inResetOrigPropFlag == 'undefined' )
    {
      GetPlot(inPlotProps,1);
    }
  else
    {
      GetPlot(inPlotProps,inResetOrigPropFlag);
    }
  
}

function ResetPlot()
{
  if ( busyFlag == 1 )
    {
      return ;
    };
  busyFlag=1;
  top.Disable(1);
  GetPlot(origPlotProps,1);
}

function GetAxisActionButtonImage(inId,inPosition)
{
  var tmpImage="";

  if ( busyFlag == 1 || inPosition == 'down')
    {
      if ( inId == 'movelimitsxdown')
	{
	  tmpImage= 'images/leftarrow1.bmp';
	}; 

      if ( inId == 'zoominlimitsx' )
	{
	  tmpImage= 'images/zoomin1.bmp';
	}; 


      if ( inId ==  'zoomresetlimitsx')
	{
	  tmpImage= 'images/zoomreset1.bmp';
	}; 

      if ( inId == 'zoomoutlimitsx' )
	{
	  tmpImage= 'images/zoomout1.bmp';
	}; 
      
      if ( inId == 'movelimitsxup' )
	{
	  tmpImage= 'images/rightarrow1.bmp';
	}; 

    }
  else
    {
    
      if ( inId == 'movelimitsxdown')
	{
	  tmpImage= 'images/leftarrow.bmp';
	}; 

      if ( inId == 'zoominlimitsx' )
	{
	  tmpImage= 'images/zoomin.bmp';
	}; 
      
      if ( inId ==  'zoomresetlimitsx')
	{
	  tmpImage='images/zoomreset.bmp';
	}; 

      if ( inId == 'zoomoutlimitsx' )
	{
	  tmpImage= 'images/zoomout.bmp';
	}; 
      
      if ( inId == 'movelimitsxup' )
	{
	  tmpImage= 'images/rightarrow.bmp';
	}; 
    };

  return tmpImage;

}


function Disable(inDisableFlag)
{

  disabledFlag = inDisableFlag;
  
  if ( inDisableFlag )
    {
  



      document.getElementById('plot').onclick = "";
      //      document.getElementById('plot').onmousemove = "";
      document.getElementById('plot').onmouseover = "";
      top.plotframe.onresize = "";


      document.body.style.cursor=top.busyCursor;
      SetCursor(top.busyCursor);


      for (id in objIds)
	{
	  
	  try
	    {

	      /* Thist causes problems in IE by disabling onload event 
	      if ( id < 0 )
		{
		  document.getElementById(objIds[id]).disabled=1;
		}
	      else
		{
	      */

	      if ( objIds[id] == 'movelimitsxdown' )
		{
		  document.getElementById('movelimitsxdown').src='images/leftarrow1.bmp';
		};
	      
	      if ( objIds[id] == 'zoominlimitsx' )
		{
		  document.getElementById('zoominlimitsx').src='images/zoomin1.bmp';
		};
	      
	      if ( objIds[id] == 'zoomresetlimitsx' )
		{
		  document.getElementById('zoomresetlimitsx').src='images/zoomreset1.bmp';
		};
	      
	      if ( objIds[id] == 'zoomoutlimitsx' )
		{
		  document.getElementById('zoomoutlimitsx').src='images/zoomout1.bmp';
		};
		  
	      if ( objIds[id] == 'movelimitsxup' )
		{
		  document.getElementById('movelimitsxup').src='images/rightarrow1.bmp';
		}
	      
	      /*   };
	       */
	    }
	  catch(err)
	    {
	      alert(err);
	    }; 
	}

    }
  else
    {

      document.getElementById('plot').onclick = OnClick;
      document.getElementById('verticalbar').onclick = OnClick;
      document.getElementById('horizontalbar').onclick = OnClick;
      document.getElementById('visibilitymarker').onclick = OnClick;
      document.getElementById('pitchmarker').onclick = OnClick;
      document.getElementById('rollmarker').onclick = OnClick;
      document.getElementById('rollhighmarker').onclick = OnClick;
      document.getElementById('rolllowmarker').onclick = OnClick;

      //      document.getElementById('plot').onmousemove = OnMouseMove;
      document.getElementById('plot').onmouseover = OnMouseOver;
      top.plotframe.onresize = OnResize;


      document.body.style.cursor=top.notbusyCursor;
      SetCursor(top.plotCursor);


      for (id in objIds)
	{
	  try
	    { 
	      /* 
	      if ( id < 1 )
		{
		  document.getElementById(objIds[id]).disabled=0;
		}
	      else
		{
	      */
 
	      if ( objIds[id] == 'movelimitsxdown' )
		{
		  document.getElementById('movelimitsxdown').src='images/leftarrow.bmp';
		};
	      
	      if ( objIds[id] == 'zoominlimitsx' )
		{
		  document.getElementById('zoominlimitsx').src='images/zoomin.bmp';
		};
	      
	      if ( objIds[id] == 'zoomresetlimitsx' )
		{
		  document.getElementById('zoomresetlimitsx').src='images/zoomreset.bmp';
		};
	      
	      
	      if ( objIds[id] == 'zoomoutlimitsx' )
		{
		  document.getElementById('zoomoutlimitsx').src='images/zoomout.bmp';
		};
	      
	      
	      if ( objIds[id] == 'movelimitsxup' )
		{
		  document.getElementById('movelimitsxup').src='images/rightarrow.bmp';
		};

	      /*  };
	       */
	    }
	  catch(err)
	    {
	    }; 
	  
	};
      
      
    };
  
  
}

function PadString(inString,inLength)
{

  var pad=inLength;

  if (typeof inString != 'undefined' )
    {
      pad = pad - inString.length;
    };
  
  while ( pad > 0 )
    {
      inString = "&nbsp;"+inString; 
      pad = pad -1;
    }

  return inString;
}


function InterpolateY ( inXArray, inYArray, inExtraDataArray,inX,inAccuracy,inType)
{

  if ( inXArray.length == 0 || inYArray.length == 0 
       || inXArray.length != inYArray.length 
       || inX > inXArray[inXArray.length-1] || inX < inXArray[0] )
    {
      return '';
    }

  var startIndx=0;
  var endIndx=inXArray.length+1;
  var curIndx=Math.floor((endIndx-startIndx)/2);
  var returnData=new Array();

  while ( curIndx > 0 )
    {
      curIndx=startIndx+curIndx;

      if ( inXArray[curIndx] < inX )
	{
	  startIndx=curIndx;
	}
      else if ( inXArray[curIndx] > inX )
	{
	  endIndx=curIndx; 
	}
      else
	{
	  startIndx=curIndx;
	  endIndx=curIndx;
	  break;
	};

      curIndx=Math.floor((endIndx-startIndx)/2);

    };

  
  var ratio;

  if ( startIndx == endIndx )
    {
      returnData[0]=inYArray[endIndx];
      ratio=0;
    }
  else
    {

      if ( inType == "roll" && 
	   (((inYArray[startIndx]+inExtraDataArray[startIndx]) < (inYArray[endIndx]-inExtraDataArray[endIndx]))
	    ||
	    ((inYArray[startIndx]-inExtraDataArray[startIndx]) > (inYArray[endIndx]+inExtraDataArray[endIndx])))
	   )
	{
	  return returnData;
	};


      ratio = (inYArray[endIndx]-inYArray[startIndx])/(inXArray[endIndx]-inXArray[startIndx]);
    };


  returnData[0] = Number(ratio*(inX-inXArray[startIndx]) + inYArray[startIndx]);
  returnData[0] = returnData[0].toFixed(inAccuracy);



  if ( inType == "pitch" )
    {

      var type="";

      for ( indx= 0;indx < inExtraDataArray.length;indx=indx+3)
	{
	  if ( inX >= inExtraDataArray[indx] && inX <= inExtraDataArray[indx+1]) 
	    {
	      if ( type != "" )
		{
		  type = type +"/";
		};
	      type= type+inExtraDataArray[indx+2];
	    };
	};

      returnData[1]=type;

    };
  
  
  if ( inType == "rollNotUsedasOf2014" )
    {
      if ( startIndx == endIndx )
	{
	  ratio=0;
	}
      else
	{
	  ratio = (inExtraDataArray[endIndx]+inYArray[endIndx]-(inExtraDataArray[startIndx]+inYArray[startIndx]))/(inXArray[endIndx]-inXArray[startIndx]);
	};
        

      var tmpHighY = Number(ratio*(inX-inXArray[startIndx]) + inExtraDataArray[startIndx]+inYArray[startIndx]);
      tmpHighY = tmpHighY.toFixed(inAccuracy);
      
      if ( tmpHighY > 360)
	{
	  tmpHighY = 360;
	};
      
      returnData[1]=tmpHighY;

      if ( startIndx == endIndx )
	{
	  ratio=0;
	}
      else
	{
	  ratio = (-inExtraDataArray[endIndx]+inYArray[endIndx]-(-inExtraDataArray[startIndx]+inYArray[startIndx]))/(inXArray[endIndx]-inXArray[startIndx]);
	};

      var tmpLowY = Number(ratio*(inX-inXArray[startIndx]) - inExtraDataArray[startIndx]+inYArray[startIndx]);
      tmpLowY = tmpLowY.toFixed(inAccuracy);
     
      if ( tmpLowY < 0)
	{
	  tmpLowY = 0;
	};
      returnData[2]=tmpLowY;
      
    };

  return returnData;

}


function DisplayCurrentCoords(inMouseX,inMouseY,injDate)
{
  var tmpData;
  var displayFlag;

  var plotMousePosXPixel;
  var xratio;
  
  var scrollOffsetX=0;
  var scrollOffsetY=0;


  if( typeof( window.pageYOffset ) == 'number' ) 
    {
      scrollOffsetY = window.pageYOffset;
      scrollOffsetX = window.pageXOffset;
    } 
  else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) 
    {
      scrollOffsetY = document.body.scrollTop;
      scrollOffsetX = document.body.scrollLeft;
    } 
  else if( document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) 
    {
      scrollOffsetY = document.documentElement.scrollTop;
      scrollOffsetX = document.documentElement.scrollLeft;
    };



  inMouseX += scrollOffsetX;
  inMouseY += scrollOffsetY;



  if ( typeof injDate  != 'undefined' )
    {
      curjDate=injDate;
      inMouseX = TimeToPixelX(curjDate);
      plotMousePosXPixel=inMouseX-plotPixelX1;
      xratio = (0.5+plotMousePosXPixel)/plotPixelWidth;
    }
  else
    {
      plotMousePosXPixel=inMouseX-plotPixelX1;
      xratio = (0.5+plotMousePosXPixel)/plotPixelWidth;
      curjDate=curPlotProps.lX+(curPlotProps.hX-curPlotProps.lX)*xratio;
    };
  


  var plotMousePosYPixel=plotPixelY2-inMouseY;
  var yratio=(0.5+plotMousePosYPixel)/plotPixelHeight;


  if ( (plotMousePosXPixel< 0) || (plotMousePosXPixel >=plotPixelWidth)
       || (plotMousePosYPixel< 0) || (plotMousePosYPixel >=plotPixelHeight) )
    {

      SetCursor(top.notbusyCursor);

      top.plotframe.document.getElementById('datedisplaytable').rows[0].cells[3].innerHTML='<small><span style="font-weight: bold;">       </span></small>';

      top.plotframe.document.getElementById('datedisplaytable').rows[0].cells[1].innerHTML='<small><span style="font-weight: bold;">        </span></small>';
  
      top.plotframe.document.getElementById('currentcoordstable').rows[0].cells[1].innerHTML='<small><span style="font-weight: bold;">      </span></small>';

      top.plotframe.document.getElementById('currentcoordstable').rows[0].cells[3].innerHTML='<small><span style="font-weight: bold;">      </span></small>';

      top.plotframe.document.getElementById('currentcoordstable').rows[0].cells[5].innerHTML='<small><span style="font-weight: bold;">    </span></small>';


      //      top.plotframe.document.getElementById('currentcoordstable').rows[0].cells[7].innerHTML='<small><span style="font-weight: bold;">      </span></small>';

      
      top.plotframe.document.getElementById('plot').title=""
      DisplayCrosshair(0,inMouseX,inMouseY);
      DisplayCurveMarker(0,'visibilitymarker');
      DisplayCurveMarker(0,'pitchmarker');
      DisplayCurveMarker(0,'rollmarker');
      return;
    };

  top.plotframe.document.getElementById('plot').title="Left bttn: center,Shft-left bttn: zoom in,Ctrl-left bttn: zoom out,Shft-Ctrl-left bttn: reset";
  DisplayCrosshair(top.optionsframe.GetViewcrosshair(),inMouseX,inMouseY);

  if ( disabledFlag != 1 )
    {
      SetCursor(top.plotCursor); 
    };



  var cDate= top.FromJulian(curjDate);


  tmpData = InterpolateY ( top.dataframe.timeData, top.dataframe.visibilityData,"",curjDate,2);
  curVis=tmpData[0];

  var curVisS="";
  displayFlag = curPlotProps.viewvisibility && top.optionsframe.GetViewvisibilitycursor() ;

  if (curPlotProps.viewvisibility ) 
    {
      if (typeof curVis != 'undefined')
	{
	  curVisS =curVis;  
	}
      else
	{
	  displayFlag=0;
	}
    }
  else
    {
      displayFlag = 0;
    };
  curVisS = PadString(curVisS,5);
  top.plotframe.document.getElementById('currentcoordstable').rows[0].cells[5].innerHTML='<small><span style="font-weight: bold;">'+curVisS+'</span></small>';

  DisplayCurveMarker(displayFlag,'visibilitymarker',inMouseX,curVis);




  var curPitchS="";
  displayFlag = curPlotProps.viewpitch && top.optionsframe.GetViewpitchcursor() && (curVis != 0.0);
  if  (curVis != 0.0) 
    {
      tmpData = InterpolateY ( top.dataframe.timeData, top.dataframe.pitchData, top.dataframe.badpitchData,curjDate,2,"pitch");
      curPitch = tmpData[0];
      curPitchType= tmpData[1];

      if (typeof curPitch =='undefined')
	{
	  displayFlag = 0;
	}
      else
	{
	  curPitchS = curPitch;
	  if ( curPitchType != "")
	    {
	      curPitchS = curPitchS+"("+curPitchType+")";
	    };
	};
    };

  curPitchS = PadString(curPitchS,7);


  top.plotframe.document.getElementById('currentcoordstable').rows[0].cells[1].innerHTML='<small><span style="font-weight: bold;">'+curPitchS+'</span></small>';
  DisplayCurveMarker(displayFlag,'pitchmarker',inMouseX,curPitch);




  var curRollS="";
  displayFlag = curPlotProps.viewroll && top.optionsframe.GetViewrollcursor() && (curVis != 0.0);
  if ( curVis != 0.0 )
    {


      tmpData= InterpolateY ( top.dataframe.timeData, top.dataframe.rollData,top.dataframe.rolltolData,curjDate,2,"roll");
      curRoll =  tmpData[0];
      curRollHigh = 0;
      curRollLow = 0;
      
      if (typeof curRoll =='undefined')
	{
	  displayFlag = 0;
	}
      else
	{
        var xx = getRollTol(curPitch);
        curRollHigh = parseFloat(curRoll) + xx;
        if (curRollHigh > 360){ curRollHigh=360; }
        curRollHigh = curRollHigh.toFixed(2);
        curRollLow = parseFloat(curRoll) - xx;
        if (curRollLow < 0) {curRollLow=0; }
        curRollLow = curRollLow.toFixed(2);

        curRollS = PadString(curRoll+"(Min:"+curRollLow+",Max:"+curRollHigh+")",7);
	}
    };     
  curRollS=PadString(curRollS,7);  
  top.plotframe.document.getElementById('currentcoordstable').rows[0].cells[3].innerHTML='<small><span style="font-weight: bold;">'+curRollS+'</span></small>';
  

  DisplayCurveMarker(displayFlag,'rollmarker',inMouseX,curRoll,curRollHigh,curRollLow);




  
  //  curMaxExp=curPlotProps.lME+(curPlotProps.hME-curPlotProps.lME)*yratio;



      
  /*  curMaxExp = curMaxExp.toFixed(2);
      var curMaxExpS= PadString(curMaxExp,7);
      top.plotframe.document.getElementById('currentcoordstable').rows[0].cells[7].innerHTML='<small><span style="font-weight: bold;">'+curMaxExpS+'</span></small>';
  */
      
  top.plotframe.document.getElementById('datedisplaytable').rows[0].cells[3].innerHTML='<small><span style="font-weight: bold;">'+cDate+'</span></small>';

  curjDate=curjDate.toFixed(2);

  top.plotframe.document.getElementById('datedisplaytable').rows[0].cells[1].innerHTML='<small><span style="font-weight: bold;">'+curjDate+'</span></small>';
  

}

function OnMouseMove(inEvent) 
{



  if ( typeof  inEvent == 'undefined'  )
    {
      lastMouseX=event.clientX-2;
      lastMouseY=event.clientY-2;
    }
  else
    {
      lastMouseX=inEvent.clientX;
      lastMouseY=inEvent.clientY;
    };

  if ( disabledFlag != 1 )
    {
      DisplayCurrentCoords(lastMouseX,lastMouseY);

    };
  return true;
}


function OnMouseOver() 
{
  
  if ( disabledFlag == 1 )
    {
      SetCursor(top.busyCursor);
    }
  else
    {
      SetCursor(top.plotCursor);
    };
  return true;
}


function OnLoad(inEvent) 
{

  document.getElementById('movelimitsxdown').src='images/leftarrow.bmp';
  document.getElementById('zoominlimitsx').src='images/zoomin.bmp';
  document.getElementById('zoomresetlimitsx').src='images/zoomreset.bmp';
  document.getElementById('zoomoutlimitsx').src='images/zoomout.bmp';
  document.getElementById('movelimitsxup').src='images/rightarrow.bmp';

  document.getElementById('verticalbar').src='images/bar.bmp';
  document.getElementById('horizontalbar').src='images/bar.bmp';

  
  document.getElementById('plot').onclick = OnClick;
  document.getElementById('verticalbar').onclick = OnClick;
  document.getElementById('horizontalbar').onclick = OnClick;
  document.getElementById('plot').onerror = OnError;
  document.getElementById('plot').onmousemove = OnMouseMove;
  document.onmousemove=OnMouseMove;
  document.getElementById('plot').onload = OnLoadImage;
  document.getElementById('plot').onmouseover = OnMouseOver;
  top.plotframe.onresize = OnResize;

  
  

  top.PlotLoaded(1);



  if ( top.optionsframe.GetViewplotdata() )
    {
      top.dataframe.Hide(0);
    };

  if ( ! top.optionsframe.GetAutofit())
  {
    SetPlotTitle(top.newPlotProps);
    top.newPlotProps.width=GetImageWidth();
    top.newPlotProps.height=GetImageHeight();
  };


  GeneratePlot(top.newPlotProps);

  return true;
}

function OnUnLoad(inEvent) 
{ 
  //top.newPlotProps.CopyObj(top.plotframe.GetPlotProps()); 
  top.PlotLoaded(0);
  top.dataframe.Clear();
  top.dataframe.Hide(1);
  top.buttonsframe.ConfigFrame();
}

function OnResize(inEvent)
{

  if ( disabledFlag != 1 )
    {
      CalculateImagePixelCoords();

      SetupCrosshair();

      if ( top.optionsframe.GetAutofit() )
	{
	  var tmpPlotProps = GetUpdatedPlotProps(0);
	  GeneratePlot(tmpPlotProps,0);
	}
      else
	{
	  DisplayCrosshair(top.optionsframe.GetViewcrosshair(),lastMouseX,lastMouseY);
	  DisplaySelection(selectionArray);
	  top.buttonsframe.ConfigFrame();
	};
    };
}


function DisplayCrosshair(inFlag,inPixelX,inPixelY)
{

  if ( inFlag && inPixelX >= plotPixelX1 && inPixelX <= plotPixelX2 
       && inPixelY >= plotPixelY1 && inPixelY <= plotPixelY2  )
    {
      document.getElementById('verticalbar').width=1;
      document.getElementById('horizontalbar').height=1;
      document.getElementById('verticalbar').style.left=inPixelX;
      document.getElementById('horizontalbar').style.top=inPixelY;
    }
  else
    {
      document.getElementById('verticalbar').width=0;
      document.getElementById('horizontalbar').height=0;
    };
}


function SetupCrosshair()
{


  document.getElementById('verticalbar').height=plotPixelY2-plotPixelY1+1;
  document.getElementById('verticalbar').width=0;
  document.getElementById('verticalbar').style.left=plotPixelX1;
  document.getElementById('verticalbar').style.top=plotPixelY1;


  document.getElementById('horizontalbar').width=plotPixelX2-plotPixelX1+1;
  document.getElementById('horizontalbar').height=0;
  document.getElementById('horizontalbar').style.left=plotPixelX1;
  document.getElementById('horizontalbar').style.top=plotPixelY1;


}


function DisplayCurveMarker(inVisibilityFlag, inMarker, inMouseX,inY,inYHigh,inYLow)
{

  if ( inMarker == 'visibilitymarker' )
    {
      if ( inVisibilityFlag && (typeof inY != 'undefined') )
	{
	  document.getElementById('visibilitymarker').width=11;
	  document.getElementById('visibilitymarker').style.left=inMouseX-5;
	  document.getElementById('visibilitymarker').style.top=plotPixelY1-6+plotPixelHeight*(curPlotProps.hV-inY)/(curPlotProps.hV-curPlotProps.lV);
	  
	}
      else
	{
	  document.getElementById('visibilitymarker').width=0;
	};
    }
  else if (inMarker == 'pitchmarker' )
    {
      if ( inVisibilityFlag && (typeof inY != 'undefined'))
	{
	  document.getElementById('pitchmarker').width=11;
	  document.getElementById('pitchmarker').style.left=inMouseX-5;
	  document.getElementById('pitchmarker').style.top=plotPixelY1-6+plotPixelHeight*(curPlotProps.hP-inY)/(curPlotProps.hP-curPlotProps.lP);
	}
      else
	{
	  document.getElementById('pitchmarker').width=0;
	}
    }
  else if (inMarker == 'rollmarker' )
    { 
      
      if ( inVisibilityFlag && (typeof inY != 'undefined') && (typeof inYHigh != 'undefined') && (typeof inYLow != 'undefined') )
	{
	  document.getElementById('rollmarker').width=11;
	  document.getElementById('rollmarker').style.left=inMouseX-5;
	  document.getElementById('rollmarker').style.top=plotPixelY1-6+plotPixelHeight*(curPlotProps.hR-inY)/(curPlotProps.hR-curPlotProps.lR);
	  
	  document.getElementById('rollhighmarker').width=11;
	  document.getElementById('rollhighmarker').style.left=inMouseX-5;
	  document.getElementById('rollhighmarker').style.top=plotPixelY1-8+(plotPixelHeight*(curPlotProps.hR-inYHigh))/(curPlotProps.hR-curPlotProps.lR);
	  
	  document.getElementById('rolllowmarker').width=11;
	  document.getElementById('rolllowmarker').style.left=inMouseX-5;
	  document.getElementById('rolllowmarker').style.top=plotPixelY1-1+(plotPixelHeight*(curPlotProps.hR-inYLow))/(curPlotProps.hR-curPlotProps.lR);
	}
      else
	{
	  document.getElementById('rollmarker').width=0;
	  document.getElementById('rollhighmarker').width=0; 
	  document.getElementById('rolllowmarker').width=0;
	};


    };

}


function SetCursor(inCursorType)
{
  document.getElementById('plot').style.cursor=inCursorType;
  document.getElementById('verticalbar').style.cursor=inCursorType;
  document.getElementById('horizontalbar').style.cursor=inCursorType;
  document.getElementById('visibilitymarker').style.cursor=inCursorType;
  document.getElementById('pitchmarker').style.cursor=inCursorType;
  document.getElementById('rollmarker').style.cursor=inCursorType;
  document.getElementById('rollhighmarker').style.cursor=inCursorType;
  document.getElementById('rolllowmarker').style.cursor=inCursorType;
}


function TimeToPixelX(inTime)
{
  return plotPixelX1+plotPixelWidth*(inTime-curPlotProps.jlX+curPlotProps.jbase)/(curPlotProps.jhX-curPlotProps.jlX);
}


function DisplayDataCursor(inX)
{  
  var scrollOffsetY=0;


  if ( typeof inX != 'undefined')
    {
  
  
      if( typeof( window.pageYOffset ) == 'number' ) 
	{
	  scrollOffsetY = window.pageYOffset;
	} 
      else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) 
	{
	  scrollOffsetY = document.body.scrollTop;
	} 
      else if( document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) 
	{
	  scrollOffsetY = document.documentElement.scrollTop;
	};

      DisplayCurrentCoords(0,plotPixelY2-scrollOffsetY,inX);
    }
  else
    {
      DisplayCurrentCoords(0,0);
    }
}

function DisplaySelection(inSelectionArray)
{

  

  for (indx in selectionIds)
    {
      document.getElementById('plottabler4').removeChild(top.plotframe.document.getElementById(selectionIds[indx]));
    };

  selectionIds.length=0;

  if ( typeof inSelectionArray == 'undefined')
    {
      return;
    };

  if ( selectionArray != inSelectionArray)
    {
      selectionArray=inSelectionArray;
    };


  if ( selectionArray.length != 0  )
    {  

      var tmpId;
      var tmpLowX;
      var tmpWidth;

      for (indx=0;indx<(selectionArray.length-1);indx=indx+2)
	{
	  tmpId="selectarea"+indx;
	  selectionIds[selectionIds.length]=tmpId;
	  document.getElementById('plottabler4').innerHTML =  top.plotframe.document.getElementById('plottabler4').innerHTML+'<img id="'+tmpId+'" src="'+selectionArea.src+'" style="position:absolute; height: '+plotPixelHeight+';">';


	  tmpLowX=TimeToPixelX(selectionArray[indx]);
	  tmpWidth = TimeToPixelX(selectionArray[indx+1])-tmpLowX;
	  if ( tmpWidth == 0 )
	    {
	      tmpWidth=1;
	    };
	  document.getElementById(tmpId).width=tmpWidth;
	  document.getElementById(tmpId).style.left=tmpLowX;
	  document.getElementById(tmpId).style.top=plotPixelY1;
	  
	};
    };


}


function CalculateImagePixelCoords()
{
  var deltaX = deltaY = 0;
  var obj = document.getElementById('plot');
  
  if (obj.offsetParent) {
    do {
      deltaX += obj.offsetLeft;
      deltaY += obj.offsetTop;
    } while (obj = obj.offsetParent);
  };
  
  deltaX += browserImageLocationXCorrection;
  deltaY += browserImageLocationYCorrection;
  
  plotPixelX1=deltaX+horizontalPlotPixelMarginLeft;
  plotPixelX2=deltaX+document.getElementById('plot').width-horizontalPlotPixelMarginRight;
  plotPixelY1=deltaY+verticalPlotPixelMarginTop;
  plotPixelY2=deltaY+document.getElementById('plot').height-verticalPlotPixelMarginBottom;

  plotPixelHeight=plotPixelY2-plotPixelY1+1;
  plotPixelWidth=plotPixelX2-plotPixelX1+1;

}



function OnLoadImage(inEvent) 
{
  timeoutCount=0;
  clearTimeout(timeoutId);

  document.getElementById('plot').style.width=newPlotProps.width;
  document.getElementById('plot').style.height=newPlotProps.height;


  document.getElementById('visibilitymarker').src='images/cross_'+top.newPlotProps.visibilitycolor+'.png';
  document.getElementById('pitchmarker').src='images/cross_'+top.newPlotProps.pitchcolor+'.png';
  document.getElementById('rollmarker').src='images/cross_'+top.newPlotProps.rollcolor+'.png';
  document.getElementById('rollhighmarker').src='images/highlimit_'+top.newPlotProps.rollcolor+'.png';
  document.getElementById('rolllowmarker').src='images/lowlimit_'+top.newPlotProps.rollcolor+'.png';




  CalculateImagePixelCoords();


  SetupCrosshair();
  if (curPlotProps.jlX == newPlotProps.jlX && curPlotProps.jhX == newPlotProps.jhX)
    {
      DisplaySelection(selectionArray);
    }
  else
    {
      selectionArray.length=0;
    };
  
  if ( resetOrigProps==1 )
    {
      origPlotProps.CopyObj(newPlotProps);
    };
  oldPlotProps.CopyObj(newPlotProps);
  curPlotProps.CopyObj(newPlotProps);  
  

  //  SetPlotTitle();

  DisplayCurrentCoords(lastMouseX,lastMouseY);

  if ( overwriteInputFlag == 1 )
    {
      UpdateInput();
      overwriteInputFlag = 0;
    };
  busyFlag =0;
  top.Disable(0);


  if ( top.optionsframe.GetAutofit() && IsChanged(0) )
    {
      var tmpPlotProps = GetUpdatedPlotProps(0);
      GeneratePlot(tmpPlotProps,resetOrigProps);
    };


  
  return true;
}

function OnMouseDown(inEvent) 
{
  //alert('Coords:'+inEvent.clientX+','+inEvent.clientY);
  //document.getElementById('plot').title='Coords:'+inEvent.clientX+','+inEvent.clientY;
}


function OnError(inEvent)
{
  Unload();
  alert("Error occured while generating plot. Please try again");
}


function OnClick(inEvent)
{
  if ( disabledFlag == 1 )
    {
      return ;
    };


  var ctrlKey=0,shiftKey=0;

  if ( typeof  inEvent == 'undefined'  )
    {
      lastMouseX=event.clientX-2;
      lastMouseY=event.clientY-2;
      ctrlKey = event.ctrlKey;
      shiftKey = event.shiftKey;
    }
  else
    {
      lastMouseX=inEvent.clientX;
      lastMouseY=inEvent.clientY;
      ctrlKey = inEvent.ctrlKey;
      shiftKey = inEvent.shiftKey;
    };

  DisplayCurrentCoords(lastMouseX,lastMouseY);
  
  if (document.getElementById('plot').style.cursor==top.plotCursor  &&  curjDate!="" )
    //&& curPitch != "" && curRoll != "" && curVis != "" &&  curMaxExp != "" )
    {
      
      busyFlag =1;
      top.Disable(1);
      var delta;
      var tmpPlotProps  = GetUpdatedPlotProps(0);

      if ( ctrlKey && shiftKey )
	{
	  tmpPlotProps.lX= origPlotProps.lX;
	  tmpPlotProps.hX= origPlotProps.hX;
	  
	}
      else
	{
	  delta=(curPlotProps.hX -curPlotProps.lX)/2.0;

	  if ( ctrlKey )
	    {
	      delta = delta*top.optionsframe.GetZoomFactor();
	    }
	  else if ( shiftKey )
	    {
	      delta = delta/top.optionsframe.GetZoomFactor();
	    };

	  if ((2*delta) <= top.jdateResolution)
	    {
	      delta=top.jdateResolution/2.0;
	    };


	  tmpPlotProps.lX= Number(curjDate)-delta;
	  tmpPlotProps.hX= Number(curjDate)+delta;

	}
      
      tmpPlotProps.jlX=1.0*tmpPlotProps.jbase+tmpPlotProps.lX;
      tmpPlotProps.jhX=1.0*tmpPlotProps.jbase+tmpPlotProps.hX;
      
      





      /*      
      
      if ( document.getElementById('pitchaxis').checked )
	{
	  
	  
	  if ( inEvent.ctrlKey && inEvent.shiftKey )
	    {
	      tmpPlotProps.lP= origPlotProps.lP;
	      tmpPlotProps.hP= origPlotProps.hP;
	    }
	  else
	    {
	      delta = (curPlotProps.hP -curPlotProps.lP)/2.0;
	      
	      if ( inEvent.ctrlKey )
		{
		  delta = delta*top.optionsframe.GetZoomFactor();
		}
	      else if ( inEvent.shiftKey )
		{
		  delta = delta/top.optionsframe.GetZoomFactor();
		};
	      
	      tmpPlotProps.lP= Number(curPitch)-delta;
	      tmpPlotProps.hP= Number(curPitch)+delta;
	    };
	};
      
      if ( document.getElementById('rollaxis').checked )
	{
	  if ( inEvent.ctrlKey && inEvent.shiftKey )
	    {
	      tmpPlotProps.lR= origPlotProps.lR;
	      tmpPlotProps.hR= origPlotProps.hR;
	    }
	  else
	    {
	      delta = (curPlotProps.hR -curPlotProps.lR)/2.0;
	      
	      if ( inEvent.ctrlKey )
		{
		  delta = delta*top.optionsframe.GetZoomFactor();
		}
	      else if ( inEvent.shiftKey )
		{
		  delta = delta/top.optionsframe.GetZoomFactor();
		};
	      
	      tmpPlotProps.lR= Number(curRoll)-delta;
	      tmpPlotProps.hR= Number(curRoll)+delta;
	    };
	}
      
      if ( document.getElementById('visibilityaxis').checked )
	{
	  
	  if ( inEvent.ctrlKey && inEvent.shiftKey )
	    {
	      tmpPlotProps.lV= origPlotProps.lV;
	      tmpPlotProps.hV= origPlotProps.hV;
	    }
	  else
	    {
	      delta = (curPlotProps.hV -curPlotProps.lV)/2.0;
	      
	      
	      if ( inEvent.ctrlKey )
		{
		  delta = delta*top.optionsframe.GetZoomFactor();
		}
	      else if ( inEvent.shiftKey )
		{
		  delta = delta/top.optionsframe.GetZoomFactor();
		};
	      
	      tmpPlotProps.lV= Number(curVis)-delta;
	      tmpPlotProps.hV= Number(curVis)+delta;
	      
	    };
	};
      
      if ( document.getElementById('maxexpaxis').checked )
	{ 
	  
	  if ( inEvent.ctrlKey && inEvent.shiftKey )
	    {
	      tmpPlotProps.lME= origPlotProps.lME;
	      tmpPlotProps.hME= origPlotProps.hME;
	    }
	  else
	    {
	      delta = (curPlotProps.hME -curPlotProps.lME)/2.0;
	      
	      
	      if ( inEvent.ctrlKey )
		{
		  delta = delta*top.optionsframe.GetZoomFactor();
		}
	      else if ( inEvent.shiftKey )
		{
		  delta = delta/top.optionsframe.GetZoomFactor();
		};
	      
	      tmpPlotProps.lME= Number(curMaxExp)-delta;
	      tmpPlotProps.hME= Number(curMaxExp)+delta;
	    }
	};

	*/

      overwriteInputFlag=1;
      GetPlot(tmpPlotProps);
    };

//  inEvent.preventDefault();

}





function MoveDownLimitsTime()
{
  if ( disabledFlag == 1 )
    {
      return ;
    };
  busyFlag=1;
  top.Disable(1);
  var delta = (curPlotProps.hX -curPlotProps.lX)*top.optionsframe.GetMoveFactor();  

  var tmpPlotProps  = GetUpdatedPlotProps(0);
  
  tmpPlotProps.lX= tmpPlotProps.lX-delta;
  tmpPlotProps.hX= tmpPlotProps.hX-delta;
  tmpPlotProps.jlX=1.0*tmpPlotProps.jbase+tmpPlotProps.lX;
  tmpPlotProps.jhX=1.0*tmpPlotProps.jbase+tmpPlotProps.hX;

  overwriteInputFlag=1;
  GetPlot(tmpPlotProps);  

}

function MoveUpLimitsTime()
{
  if ( disabledFlag == 1 )
    {
      return ;
    }; 
  busyFlag=1;
  top.Disable(1);

  var delta = (curPlotProps.hX -curPlotProps.lX)*top.optionsframe.GetMoveFactor();  

  var tmpPlotProps  = GetUpdatedPlotProps(0);

  tmpPlotProps.lX= tmpPlotProps.lX+delta;
  tmpPlotProps.hX= tmpPlotProps.hX+delta;
  tmpPlotProps.jlX=1.0*tmpPlotProps.jbase+tmpPlotProps.lX;
  tmpPlotProps.jhX=1.0*tmpPlotProps.jbase+tmpPlotProps.hX;

  overwriteInputFlag=1;
  GetPlot(tmpPlotProps);  

}


function ZoomInLimitsTime()
{
  if ( disabledFlag == 1 )
    {
      return ;
    }; 
  busyFlag=1;
  top.Disable(1);


  var delta = ((curPlotProps.hX -curPlotProps.lX)/top.optionsframe.GetZoomFactor())/2.0	;  
  var center = (curPlotProps.hX +curPlotProps.lX)/2.0;

  if ((2*delta) <= top.jdateResolution)
    {
      delta=top.jdateResolution/2.0;
    }

  
  var tmpPlotProps  = GetUpdatedPlotProps(0);
  tmpPlotProps.lX= center-delta;
  tmpPlotProps.hX= center+delta;
  tmpPlotProps.jlX=1.0*top.julianBase+tmpPlotProps.lX;
  tmpPlotProps.jhX=1.0*top.julianBase+tmpPlotProps.hX;

  overwriteInputFlag=1;
  GetPlot(tmpPlotProps);  

}

function ZoomOutLimitsTime()
{
  if ( disabledFlag == 1 )
    {
      return ;
    }; 
  busyFlag=1;
  top.Disable(1);

  var delta = ((curPlotProps.hX -curPlotProps.lX)*top.optionsframe.GetZoomFactor())/2.0;
  var center = (curPlotProps.hX +curPlotProps.lX)/2.0;

  var tmpPlotProps  = GetUpdatedPlotProps(0);

  tmpPlotProps.lX= center-delta;
  tmpPlotProps.hX= center+delta;
  tmpPlotProps.jlX=1.0*top.julianBase+tmpPlotProps.lX;
  tmpPlotProps.jhX=1.0*top.julianBase+tmpPlotProps.hX;

  overwriteInputFlag=1;
  GetPlot(tmpPlotProps);  

}


function ResetLimitsTime()
{
  if ( disabledFlag == 1 )
    {
      return ;
    }; 
  busyFlag=1;
  top.Disable(1);

  var tmpPlotProps  = GetUpdatedPlotProps(0);
  tmpPlotProps.lX= origPlotProps.lX;
  tmpPlotProps.hX= origPlotProps.hX;
  tmpPlotProps.jlX=1.0*top.julianBase+tmpPlotProps.lX;
  tmpPlotProps.jhX=1.0*top.julianBase+tmpPlotProps.hX;

  overwriteInputFlag=1;
  GetPlot(tmpPlotProps);  

}






/*
function MoveDownLimits(inSet)
{

  if ( disabledFlag == 1 )
    {
      return ;
    }; 
  busyFlag=1;
  top.Disable(1);

  var delta;
  var tmpPlotProps  = GetUpdatedPlotProps(0);

  if ( inSet == 1)
    {
      if ( document.getElementById('pitchaxis').checked )
	{

	  delta = (curPlotProps.hP -curPlotProps.lP)*top.optionsframe.GetMoveFactor();  
	  
	  tmpPlotProps.lP= tmpPlotProps.lP-delta;
	  tmpPlotProps.hP= tmpPlotProps.hP-delta;
	}
      if ( document.getElementById('rollaxis').checked )
	{
	  delta = (curPlotProps.hR -curPlotProps.lR)*top.optionsframe.GetMoveFactor();  
	  
	  tmpPlotProps.lR= tmpPlotProps.lR-delta;
	  tmpPlotProps.hR= tmpPlotProps.hR-delta;
	};
      
    }
  else
    {
      
      if ( document.getElementById('visibilityaxis').checked )
	{
	  
	  var delta = (curPlotProps.hV -curPlotProps.lV)*top.optionsframe.GetMoveFactor();  
	  
	  tmpPlotProps.lV= tmpPlotProps.lV-delta;
	  tmpPlotProps.hV= tmpPlotProps.hV-delta;
	  

	}
      if ( document.getElementById('maxexpaxis').checked )
	{
	  var delta = (curPlotProps.hME -curPlotProps.lME)*top.optionsframe.GetMoveFactor();  
	  
	  tmpPlotProps.lME= tmpPlotProps.lME-delta;
	  tmpPlotProps.hME= tmpPlotProps.hME-delta;
	}
      
    };

  overwriteInputFlag=1;
  GetPlot(tmpPlotProps);  

}





function MoveUpLimits(inSet)
{
  if ( disabledFlag == 1 )
    {
      return ;
    }; 
  busyFlag=1;
  top.Disable(1);

  var delta;
  var tmpPlotProps  = GetUpdatedPlotProps(0);

  if ( inSet == 1)
    {
      if ( document.getElementById('pitchaxis').checked )
	{
	  delta = (curPlotProps.hP -curPlotProps.lP)*top.optionsframe.GetMoveFactor();  
	  
	  tmpPlotProps.lP= tmpPlotProps.lP+delta;
	  tmpPlotProps.hP= tmpPlotProps.hP+delta;

	}
      if ( document.getElementById('rollaxis').checked )
	{
	  delta = (curPlotProps.hR -curPlotProps.lR)*top.optionsframe.GetMoveFactor();  
	  
	  tmpPlotProps.lR= tmpPlotProps.lR+delta;
	  tmpPlotProps.hR= tmpPlotProps.hR+delta;

	}
      
    }
  else
    {
      
      if ( document.getElementById('visibilityaxis').checked )
	{
	  delta = (curPlotProps.hV -curPlotProps.lV)*top.optionsframe.GetMoveFactor();  
	  
	  tmpPlotProps.lV= tmpPlotProps.lV+delta;
	  tmpPlotProps.hV= tmpPlotProps.hV+delta;
	  
	}
      if ( document.getElementById('maxexpaxis').checked )
	{

	  delta = (curPlotProps.hME -curPlotProps.lME)*top.optionsframe.GetMoveFactor();  
	  
	  tmpPlotProps.lME= tmpPlotProps.lME+delta;
	  tmpPlotProps.hME= tmpPlotProps.hME+delta;

	}
      
    };



  overwriteInputFlag=1;
  GetPlot(tmpPlotProps);  

}




function ZoomInLimits(inSet)
{
  if ( disabledFlag == 1 )
    {
      return ;
    }; 
  busyFlag=1;
  top.Disable(1);


  var delta;
  var center;
  var tmpPlotProps  = GetUpdatedPlotProps(0);

  if ( inSet == 1)
    {
      if ( document.getElementById('pitchaxis').checked )
	{
	  delta = ((curPlotProps.hP -curPlotProps.lP)/top.optionsframe.GetZoomFactor())/2.0;  
	  center = (curPlotProps.hP +curPlotProps.lP)/2.0;
	  tmpPlotProps.lP= center-delta;
	  tmpPlotProps.hP= center+delta;
	}

      if ( document.getElementById('rollaxis').checked )
	{

	  delta = ((curPlotProps.hR -curPlotProps.lR)/top.optionsframe.GetZoomFactor())/2.0;  
	  center = (curPlotProps.hR +curPlotProps.lR)/2.0;
	  tmpPlotProps.lR= center-delta;
	  tmpPlotProps.hR= center+delta;
 
	}
      
    }
  else
    {
      
      if ( document.getElementById('visibilityaxis').checked )
	{
	  delta = ((curPlotProps.hV -curPlotProps.lV)/top.optionsframe.GetZoomFactor())/2.0;  
	  center=(curPlotProps.hV +curPlotProps.lV)/2.0;

	  tmpPlotProps.lV= center -delta;
	  tmpPlotProps.hV= center +delta;
	}
      if ( document.getElementById('maxexpaxis').checked )
	{
	  delta = ((curPlotProps.hME -curPlotProps.lME)/top.optionsframe.GetZoomFactor())/2.0;  
	  center = (curPlotProps.hME +curPlotProps.lME)/2.0;
	  tmpPlotProps.lME= center-delta;
	  tmpPlotProps.hME= center+delta;
	}
      
    };

  overwriteInputFlag=1;
  GetPlot(tmpPlotProps);  

}



function ZoomOutLimits(inSet)
{
  if ( disabledFlag == 1 )
    {
      return ;
    }; 
  busyFlag=1;
  top.Disable(1);


  var delta;
  var center;
  var tmpPlotProps  = GetUpdatedPlotProps(0);


  if ( inSet == 1)
    {
      if ( document.getElementById('pitchaxis').checked )
	{
	  delta = ((curPlotProps.hP -curPlotProps.lP)*top.optionsframe.GetZoomFactor())/2.0;
	  center = (curPlotProps.hP +curPlotProps.lP)/2.0;
	  tmpPlotProps.lP= center-delta;
	  tmpPlotProps.hP= center+delta;
	}
      if ( document.getElementById('rollaxis').checked )
	{
	  delta = ((curPlotProps.hR -curPlotProps.lR)*top.optionsframe.GetZoomFactor())/2.0;
	  center = (curPlotProps.hR +curPlotProps.lR)/2.0;
	  
	  tmpPlotProps.lR= center-delta;
	  tmpPlotProps.hR= center+delta;

	}
    }
  else
    {
      if ( document.getElementById('visibilityaxis').checked )
	{
	  delta = ((curPlotProps.hV -curPlotProps.lV)*top.optionsframe.GetZoomFactor())/2.0;
	  center = (curPlotProps.hV +curPlotProps.lV)/2.0;
	  
	  tmpPlotProps.lV= center-delta;
	  tmpPlotProps.hV= center+delta; 
	}
      if ( document.getElementById('maxexpaxis').checked )
	{
	  delta = ((curPlotProps.hME -curPlotProps.lME)*top.optionsframe.GetZoomFactor())/2.0;
	  center=(curPlotProps.hME +curPlotProps.lME)/2.0;
	  
	  tmpPlotProps.lME= center-delta;
	  tmpPlotProps.hME= center+delta;
	}
    };




  overwriteInputFlag=1;
  GetPlot(tmpPlotProps);  

}





function ResetLimits(inSet)
{
  if ( disabledFlag == 1 )
    {
      return ;
    }; 
  busyFlag=1;
  top.Disable(1);

  var tmpPlotProps  = GetUpdatedPlotProps(0);


  if ( inSet == 1)
    {
      if ( document.getElementById('pitchaxis').checked )
	{
	  tmpPlotProps.lP= origPlotProps.lP;
	  tmpPlotProps.hP= origPlotProps.hP;
	}
      if ( document.getElementById('rollaxis').checked )
	{
	  tmpPlotProps.lR= origPlotProps.lR;
	  tmpPlotProps.hR= origPlotProps.hR;
	}
    }
  else
    {
      if ( document.getElementById('visibilityaxis').checked )
	{
	  tmpPlotProps.lV= origPlotProps.lV;
	  tmpPlotProps.hV= origPlotProps.hV;
	}
      if ( document.getElementById('maxexpaxis').checked )
	{
	  tmpPlotProps.lME= origPlotProps.lME;
	  tmpPlotProps.hME= origPlotProps.hME;
	}
    };



  overwriteInputFlag=1;
  GetPlot(tmpPlotProps);  

}
*/

// this determines roll tolerance based on pitch
// values are cut&paste from astro.cal and must be updated manually
// whenever  there is a new astro.cal (usually for December release)
function getRollTol(thePitch)
{
  var ii;
  var retval = 0.0;

  var myarr = new Array();
  myarr  = top.dataframe.calRollTol;

  if (thePitch >= myarr[myarr.length -2]) {
    retval = myarr[myarr.length-1];
  }
  else {
    for (ii=0;ii<(myarr.length-2);ii++) {
      if (thePitch>= myarr[ii] && thePitch < myarr[ii+2]) {
        retval= myarr[ii+1];
        break;
      }
      ii++;
    }
  }
  //alert("pitch=" + thePitch + "  tol=" + retval);
  return retval;

}

