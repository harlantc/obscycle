// _INSERT_SAO_COPYRIGHT_HERE_(2008-2021)_
// _INSERT_GPL_LICENSE_HERE_

var SYYYY="2023";
var SMM="01";
var SDD="01";
var EYYYY="2023";
var EMM="12";
var EDD="31";
var ephemStart = 59488;  // 2021 oct 01
var ephemStop  = 60340;  // 2024 jan 31
var lowPitch=0;
var highPitch=195;
var lowRoll=0;
var highRoll=390;
var lowVisibility=0;
var highVisibility=1;
var lowMaxExp=0;
var highMaxExp=180;
var viewpitch=true;
var viewroll=true;
var viewvisibility=true;
var viewbadpitch=true;
var viewmaxexposure=false;
var viewplotdata=false;
var viewcrosshair=true;
var viewpitchcursor=true;
var viewrollcursor=true;
var viewvisibilitycursor=true;
var pitchcolor="Red";
var pitchlinestyle="Solid";
var rollcolor="Magenta";
var rolllinestyle="Solid";
var rolltolerancelinestyle="Dashed";
var visibilitycolor="Blue";
var visibilitylinestyle="Solid";
var badpitchlinestyle="Solid";
var badpitchfillstyle="Righthatch";
var maxexpcolor="Black";
var maxexplinestyle="Solid";
var maxexpfillstyle="Lefthatch";
var exptime=30;
var plotWidth=600;
var plotHeight=600;
var pswidth="8";
var psheight="8";
var exposureTime=30;
var autofit=true;

var zoomfactor=2;
var movefactor=0.25

var minplotWidth=312;
var minplotHeight=180;
var maxplotWidth=1000000;
var maxplotHeight=1000000;

var fonttype="Helvetica";
var fontsize=10;

var axislinewidth=1;
var pitchlinewidth=1;
var rolllinewidth=1;
var visibilitylinewidth=1;
var badpitchlinewidth=1;
var maxexplinewidth=1;

var provisPageLocation="index.html";
// relative paths because this might be hosted on https 
//var provisWebServer="http://"+top.location.host;  
var provisWebServer="";
var provisCGIBin="/cgi-bin/provis/";
var provisCGIScript=provisCGIBin+"provis_gen.cgi";
var provisCGIScriptUpload=provisCGIBin+"provis_upload.cgi";
var provisCGIScriptLoad=provisCGIBin+"provis_load.cgi";
var busyCursor="wait";
var notbusyCursor="default";
var plotCursor="crosshair";
var plotframeLocation="plotframe.html";
var dataframeLocation="dataframe.html";
var welcomeLocation="welcome.html"
var helpframeLocation="help/helpframe.html";
//var titleframeLocation="titleframe.html";
var julianBase=2400000.5;
var julianBase19700101=2440587.50000;

var newPlotProps = new PlotProp();

var plotLoaded=0;
var dataLoaded=0;
var inputLoaded=0;
var optionsLoaded=0;
var tmpFrameLoc = new String();
var timeout=30000;


var uploadCounter=0;
var sessionIndx=0;

var jdateResolution=1;


var requests= new Array();
var requestclearFlag=0;
var requestenableFlag=0;

var loadProgressIndicatorStr="...";
var loadProgressRefreshTimeout=500;

var tableRowSelectColor="rgb(204, 204, 204)";
var tableRowHighlightColor="rgb(224, 224, 224)";
var tableRowNoColor="rgb(255, 255, 255)";

var availableColors = new Array();
availableColors[0]="Red";
availableColors[1]="Blue";
availableColors[2]="Green";
availableColors[3]="Yellow";
availableColors[4]="Black";
availableColors[5]="Magenta";
availableColors[5]="Cyan";







if ( (!window.XMLHttpRequest) &&   (!window.ActiveXObject ) )
  {
    alert("XMLHttpRequest objects not supported by browser."+'\n'+ "Application will not work.");
  };


function DisplayLoadProgress()
{
  if (top.loadProgressIndicatorStr == "&nbsp;&nbsp;&nbsp;")
    {
      top.loadProgressIndicatorStr=".&nbsp;&nbsp;";
    }
  else if (top.loadProgressIndicatorStr == ".&nbsp;&nbsp;")
    {
      top.loadProgressIndicatorStr = "..&nbsp;" ;
    }
  else if (top.loadProgressIndicatorStr == "..&nbsp;")
    {
      top.loadProgressIndicatorStr = "..." ;
    }
  else
    {
      top.loadProgressIndicatorStr="&nbsp;&nbsp;&nbsp;";
    };

  document.getElementById('loadinfocell').innerHTML='<big><big><span style="color: rgb(51, 51, 153);">LOADING PRoVis, PLEASE WAIT'+top.loadProgressIndicatorStr+'<small><small></small></small></span></big></big>';

  
  setTimeout('DisplayLoadProgress();',top.loadProgressRefreshTimeout)
}



function LoadTimeoutAlert()
{

  var tmpDoc = document.open("text/html","replace");


  var alertPage='<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">'
    +'<html>'
    +'<head>'
    +'<!-- _INSERT_SAO_COPYRIGHT_HERE_(2008)_ -->'
    +'<!-- _INSERT_GPL_LICENSE_HERE_ -->'
    +'<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">'
    +'<title>PRoVis</title>'
    +'</head>'
    +'<body " style="color: rgb(0, 0, 0); background-color: rgb(204, 204, 204);" alink="#ee0000" link="#0000ee" vlink="#551a8b">'
    +'<table style="text-align: left; background-color: rgb(204, 204, 204); width: 100%; height: 100%;" border="0" cellpadding="2" cellspacing="2">'
    +'<tbody>'
    +'<tr>'
    +'<td style="height: 100px;">'
    +'<table style="text-align: left; background-color: rgb(255, 255, 255); width: 100%; height: 100%;" border="1" cellpadding="2" cellspacing="2">'
    +'<tbody>'
    +'<tr>'
    +'<td style="height: 100%; text-align: center; vertical-align: middle; width: 20%;"><img alt="Chandra Science" src="images/obs1.gif" style="border: 0px solid ; width: 102px; height: 72px;"></td>'
    +'<td style="height: 100%; width: 60%; text-align: center; vertical-align: middle;">'
    +'<h1 style="color: rgb(51, 51, 153);"><small>Chandra'
    +'X-ray Center</small><br><small><small><span style="color: rgb(51, 51, 153);">Target'
    +'Visibility Interface</span></small></small></h1>'
    +'</td>'
    +'<td style="height: 100%; width: 20%; text-align: center; vertical-align: middle;"><a href="/" target="_top"><img style="border: 0px solid ; width: 54px; height: 59px;" src="images/obs2.gif" alt="Go To Chandra Home Page"></a><br>'
    +'<small><a href="/" target="_top">Go to Chandra Home Page</a></small></td>'
    +'</tr>'
    +'</tbody>'
    +'</table>'
    +'</td>'
    +'</tr>'
    +'<tr>'
    +'<td id="loadinfocell" style="text-align: center;"><big><big><span style="color: rgb(51, 51, 153);">LOADING OF PRoVis TIMED OUT<br>'
    +'No Response from the server.<br>We apologize for the inconvenience, please try again later.</span></big></big></td>'
    +'</tr>'
    +'</tbody>'
    +'</table>'
    +'</body>'
    +'</html>';

  



  tmpDoc.write(alertPage);
  tmpDoc.close();
}


function InitPlotParams()
{
  top.newPlotProps.targetname=top.inputframe.GetTargetName();
  top.newPlotProps.targetcoords=top.inputframe.GetTargetCoords();
  top.newPlotProps.lX=top.ToJulian(top.inputframe.GetSYear(),top.inputframe.GetSMonth(),top.inputframe.GetSDay());
  top.newPlotProps.hX=top.ToJulian(top.inputframe.GetEYear(),top.inputframe.GetEMonth(),top.inputframe.GetEDay());
  top.newPlotProps.jlX=1.0*top.julianBase+top.newPlotProps.lX;
  top.newPlotProps.jhX=1.0*top.julianBase+top.newPlotProps.hX;

  top.newPlotProps.jbase=top.julianBase;
  top.newPlotProps.lP=top.lowPitch;
  top.newPlotProps.hP=top.highPitch;
  top.newPlotProps.lR=top.lowRoll;
  top.newPlotProps.hR=top.highRoll;
  top.newPlotProps.lV=top.lowVisibility;
  top.newPlotProps.hV=top.highVisibility;
  top.newPlotProps.lME=top.lowMaxExp;
  top.newPlotProps.hME=top.highMaxExp;
  top.newPlotProps.viewpitch=top.optionsframe.GetViewpitch();
  top.newPlotProps.viewroll=top.optionsframe.GetViewroll();
  top.newPlotProps.viewvisibility=top.optionsframe.GetViewvisibility();
  top.newPlotProps.viewbadpitch=top.optionsframe.GetViewbadpitch();
  top.newPlotProps.viewmaxexposure=top.optionsframe.GetViewmaxexposure();
  top.newPlotProps.pitchcolor=top.optionsframe.GetPitchcolor();
  top.newPlotProps.pitchlinestyle=top.optionsframe.GetPitchlinestyle();
  top.newPlotProps.rollcolor=top.optionsframe.GetRollcolor();
  top.newPlotProps.rolllinestyle=top.optionsframe.GetRolllinestyle();
  top.newPlotProps.rolltolerancelinestyle=top.optionsframe.GetRollTolerancelinestyle();
  top.newPlotProps.visibilitycolor=top.optionsframe.GetVisibilitycolor();
  top.newPlotProps.visibilitylinestyle=top.optionsframe.GetVisibilitylinestyle();
  top.newPlotProps.badpitchlinestyle=top.optionsframe.GetBadpitchlinestyle();
  top.newPlotProps.badpitchfillstyle=top.optionsframe.GetBadpitchfillstyle();
  top.newPlotProps.maxexpcolor=top.optionsframe.GetMaxexposurecolor();
  top.newPlotProps.maxexplinestyle=top.optionsframe.GetMaxexposurelinestyle();
  top.newPlotProps.maxexpfillstyle=top.optionsframe.GetMaxexposurefillstyle();
  top.newPlotProps.exptime=top.optionsframe.GetExposureTime();
  if ( top.PlotLoaded() )
    {
      top.newPlotProps.width=top.plotframe.GetImageWidth();
      top.newPlotProps.height=top.plotframe.GetImageHeight();

      top.newPlotProps.framewith=top.GetFrameWidth('plotframe');
      top.newPlotProps.frameheight=top.GetFrameHeight('plotframe');
    }
  else
    {
      top.newPlotProps.width=0;
      top.newPlotProps.height=0;
      
      top.newPlotProps.framewith=0;
      top.newPlotProps.frameheight=0;
    };

  top.newPlotProps.fonttype=top.optionsframe.GetFonttype();
  top.newPlotProps.fontsize=top.optionsframe.GetFontsize();
  top.newPlotProps.axislinewidth=top.optionsframe.GetAxislinewidth();
  top.newPlotProps.pitchlinewidth=top.optionsframe.GetPitchlinewidth();
  top.newPlotProps.rolllinewidth=top.optionsframe.GetRolllinewidth();
  top.newPlotProps.visibilitylinewidth=top.optionsframe.GetVisibilitylinewidth();
  top.newPlotProps.badpitchlinewidth=top.optionsframe.GetBadpitchlinewidth();
  top.newPlotProps.maxexplinewidth=top.optionsframe.GetMaxexposurelinewidth();

  top.newPlotProps.calfile=top.optionsframe.GetActualCalFile();

}



function PlotProp()
{

  this.targetcoords="";
  this.targetname="";
  this.lX=top.ToJulian(top.SYYYY,top.SMM,top.SDD);
  this.hX=top.ToJulian(top.EYYYY,top.EMM,top.EDD);
  this.jlX=1.0*top.julianBase+this.lX;
  this.jhX=1.0*top.julianBase+this.hX;
  this.jbase=top.julianBase;
  this.lP=top.lowPitch;
  this.hP=top.highPitch;
  this.lR=top.lowRoll;
  this.hR=top.highRoll;
  this.lV=top.lowVisibility;
  this.hV=top.highVisibility;
  this.lME=top.lowMaxExp;
  this.hME=top.highMaxExp;
  this.viewpitch=0;
  this.viewroll=0;
  this.viewvisibility=0;
  this.viewbadpitch=0;
  this.viewmaxexposure=0;
  this.pitchcolor="";
  this.pitchlinestyle="";
  this.rollcolor="";
  this.rolllinestyle="";
  this.rolltolerancelinestyle="";
  this.visibilitycolor="";
  this.visibilitylinestyle="";
  this.badpitchlinestyle="";
  this.badpitchfillstyle="";
  this.maxexpcolor="";
  this.maxexplinestyle="";
  this.maxexpfillstyle="";
  this.exptime=top.exposureTime;
  this.width=top.plotWidth;
  this.height=top.plotHeight;
  this.framewidth=0;
  this.frameheight=0;
  this.fonttype=top.fonttype;
  this.fontsize=top.fontsize;
  this.axislinewidth=top.axislinewidth;
  this.pitchlinewidth=top.pitchlinewidth;
  this.rolllinewidth=top.rolllinewidth;
  this.visibilitylinewidth=top.visibilitylinewidth;
  this.badpitchlinewidth=top.badpitchlinewidth;
  this.maxexplinewidth=top.maxexplinewidth;
  this.calfile="";

  this.CopyObj=function(inObj) { 
    for (mem in inObj) {
      this[mem] = inObj[mem];
    } 
  };

  this.EqObj=function(inObj) { 
    for (mem in inObj) {
      if ( mem == 'CopyObj' || mem == 'EqObj' )
      {
	continue;
      };

      if (this[mem] != inObj[mem] )
      {
	if ( (( mem == 'pitchcolor' || mem == 'pitchlinestyle' || mem == 'pitchlinewidth') && this['viewpitch'] == inObj['viewpitch'] && this['viewpitch'] == 0 )
	     || (( mem == 'rollcolor' || mem == 'rolllinestyle' || mem == 'rolltolerancelinestyle'|| mem == 'rolllinewidth') && this['viewroll'] == inObj['viewroll'] && this['viewroll'] == 0 )
	     || (( mem == 'visibilitycolor' || mem == 'visibilitylinestyle' || mem == 'visibilitylinewidth') && this['viewvisibility'] == inObj['viewvisibility'] && this['viewvisibility'] == 0 )
	     || (( mem == 'visibilitycolor' || mem == 'visibilitylinestyle' || mem == 'visibilitylinewidth') && this['viewvisibility'] == inObj['viewvisibility'] && this['viewvisibility'] == 0 )
	     || (( mem == 'badpitchlinestyle' || mem == 'badpitchlinewidth' || mem == 'badpitchfillstyle') && this['viewbadpitch'] == inObj['viewbadpitch'] && this['viewbadpitch'] == 0 )
	     || (( mem == 'maxexpcolor' || mem == 'maxexplinestyle' || mem == 'maxexplinewidth' || mem == 'maxexpfillstyle') && this['viewmaxexposure'] == inObj['viewmaxexposure'] && this['viewmaxexposure'] == 0 )
	     )
	  
	  {
	    continue;
	  };
	return false;
      };
    };
    return true;
  };

}


function PlotLoaded(inVal)
{
  if ( typeof inVal == 'undefined')
    {
      return plotLoaded;
    }
  else
    {
      plotLoaded=inVal;
    };
}

function DataLoaded(inVal)
{

  if ( typeof inVal == 'undefined')
    {
      return dataLoaded;
    }
  else
    {
      dataLoaded=inVal;
    };
}

function InputLoaded(inVal)
{
  if ( typeof inVal == 'undefined')
    {
      return inputLoaded;
    }
  else
    {
      inputLoaded=inVal;
    };

}


function OptionsLoaded(inVal)
{
  if ( typeof inVal == 'undefined')
    {
      return optionsLoaded;
    }
  else
    {
      optionsLoaded=inVal;
    };

}




function ToJulian ( inYear,inMonth,inDay ) 
{
    return (julianBase19700101+(Date.UTC(inYear,inMonth-1,inDay))/86400000 - julianBase);

}

function FromJulian ( inJulianDate , inJulianBase)
{
  if ( typeof inJulianBase == 'undefined')
    {
      inJulianBase = julianBase;
    }

    msecs = (1.0*inJulianDate+inJulianBase-julianBase19700101)*86400000;


    var date = new Date();
    date.setTime(msecs);
    
    day = date.getUTCDate();
    if ( day < 10 )
      {
	day = "0"+day;
      }
    mon = date.getUTCMonth()+1;
    if ( mon < 10 )
      {
	mon = "0"+mon;
      }
    year= date.getUTCFullYear();
    
    return(""+year+"/"+mon+"/"+day);
    
}


function SetLayout(inFramesetId, inRowOrCol,inList)
{
  if ( inRowOrCol == "rows")
    {
      document.getElementById(inFramesetId).rows = inList;	
    }
  else
    {
      document.getElementById(inFramesetId).cols = inList;	
    }

}


function LoadFrame(inFrame,inUrl)
{
  if ( typeof inUrl == 'undefined')
    {
      inFrame.location.replace("blankframe.html");
    }
  else
    {
      inFrame.location.replace(inUrl);
    };
}


function GetFrameHeight(inFrameId)
{


  return  top.document.getElementById(inFrameId).scrollHeight;

};  

function GetFrameWidth(inFrameId)
{


  return  top.document.getElementById(inFrameId).scrollWidth;

};


function SetBusy(inBusyFlag)
{
  top.buttonsframe.busyFlag = inBusyFlag;
  top.inputframe.busyFlag = inBusyFlag;
  top.optionsframe.busyFlag = inBusyFlag;
  if (plotLoaded)
    {
      top.plotframe.busyFlag=inBusyFlag;
    };
  top.dataframe.busyFlag=inBusyFlag; 
}

function Disable(inDisableFlag) 
{
  
  var busyFlag=0;

  if ( top.buttonsframe.busyFlag == 1
       || top.dataframe.busyFlag == 1 )
    {
      busyFlag =1;
    };
  if (plotLoaded)
    {
      if ( top.plotframe.busyFlag == 1)
	{
	  busyFlag =1;
	}
    }	

  if ( busyFlag != inDisableFlag)
    {
      inDisableFlag = !inDisableFlag;
    };

  top.buttonsframe.Disable(inDisableFlag);
  //cache issues, so always keep this one editable
  //top.inputframe.Disable(inDisableFlag);
  top.optionsframe.Disable(inDisableFlag);
  if (plotLoaded)
    {
      top.plotframe.Disable(inDisableFlag);
    }
  else
    {
      if ( inDisableFlag )
	{
	  top.plotframe.document.body.style.cursor=top.busyCursor;
	}
      else
	{
	  top.plotframe.document.body.style.cursor=top.notbusyCursor;
	};
    };
      
  top.dataframe.Disable(inDisableFlag);
      


  return 1;
}


