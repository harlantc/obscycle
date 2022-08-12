// _INSERT_SAO_COPYRIGHT_HERE_(2008)_
// _INSERT_GPL_LICENSE_HERE_

var busyFlag=0;
var disabledFlag=0;
var objIds = new Array();


objIds[0]='generateplot';
objIds[1]='getpsplot';
objIds[2]='cleartarget';
objIds[3]='resolvetarget';
objIds[4]='resetdates';
objIds[5]='cancel';
objIds[6]='help';
objIds[7]='resetoptions';

var maximizeFlag=0;

var httpRequest;
var timeoutId;

if ( window.XMLHttpRequest ) 
{
  httpRequest=new XMLHttpRequest();
} 
else if (window.ActiveXObject ) 
{
  httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
};



function ConfigFrame(inAllFlag)
{

  if ( disabledFlag == 1 ) 
    {
      return ;
    };

  if ( top.InputLoaded() != 1 || top.OptionsLoaded() != 1 || (typeof inAllFlag != 'undefined'))
    {
      document.getElementById('resolvetarget').disabled=1;
      document.getElementById('cleartarget').disabled=1;
      document.getElementById('resetdates').disabled=1; 
      document.getElementById('resetoptions').disabled=1; 
      document.getElementById('generateplot').disabled=1; 
      document.getElementById('getpsplot').disabled=1;
      document.getElementById('cancel').disabled=1;
      document.getElementById('help').disabled=0;
      return;
    };

  if ( top.inputframe.GetTargetName() == "" )
    {
      document.getElementById('resolvetarget').disabled=1;
    }
  else
    {
      document.getElementById('resolvetarget').disabled=0; 
    };
  

  if ( top.inputframe.GetTargetName() == "" 
       && top.inputframe.GetTargetCoords() == "" )
    {
      document.getElementById('cleartarget').disabled=1;
    }
  else
    {
      document.getElementById('cleartarget').disabled=0; 
    };
  
  
  if ( top.inputframe.DateChanged() == 1 )
    {
      document.getElementById('resetdates').disabled=0; 
    }
  else
    {
      document.getElementById('resetdates').disabled=1; 
    };
  
  if ( top.optionsframe.IsChanged() )
    {
      document.getElementById('resetoptions').disabled=0; 
    }
  else
    {
      document.getElementById('resetoptions').disabled=1; 
    };
  
  if ( top.inputframe.Validate() != 1 )
    {
      document.getElementById('generateplot').disabled=1; 

      if ( top.PlotLoaded() == 1 )
	{
	  if ( top.plotframe.IsChanged(0) ||  top.optionsframe.GetActualCalFile() != "")
	    {
	      document.getElementById('generateplot').disabled=0;
	    };

	  document.getElementById('getpsplot').disabled=0;  
	}
      else
	{
	  document.getElementById('getpsplot').disabled=1;
	};
    }
  else
    {

      document.getElementById('generateplot').disabled=0;  
      document.getElementById('getpsplot').disabled=0;  
      
      
      if ( top.PlotLoaded() == 1 )
	{
	  if ( top.plotframe.IsChanged(1) ||  top.optionsframe.GetActualCalFile() != "" )
	    {
	      document.getElementById('generateplot').disabled=0;
	    }
	  else
	    {
	      document.getElementById('generateplot').disabled=1;
	    };
	  
	};
    };


  if ( busyFlag == 1 )
    {
      document.getElementById('cancel').disabled=0;
      document.getElementById('help').disabled=1;
    }
  else
    {
      document.getElementById('cancel').disabled=1;
      document.getElementById('help').disabled=0;
    };
  
}




function GeneratePlot(e)
{
  
  if (typeof e != 'undefined' )
    {
      if (e.keyCode != 13)
	{
	  ConfigFrame();
	  return;
	}
    };
  
  
  top.inputframe.ClearInvalidCoords();
  if (top.inputframe.GetTargetCoords() == "" && top.inputframe.GetTargetName() != "")
    {
      ResolveTarget(ResolveAndGenerate,e);
    }
  else
    {
      if ( top.inputframe.Validate() )
	{
	  top.InitPlotParams();
	  
	  if (top.PlotLoaded() != 1 )
	    {
	      top.LoadFrame(top.plotframe,top.plotframeLocation);
	    }
	  else
	    {
	      top.plotframe.GeneratePlot(top.newPlotProps);
	    }
	}
      else if (top.PlotLoaded() == 1 )
	{
	  if ( top.plotframe.IsChanged(0)  )
	    {
	      top.newPlotProps.CopyObj(top.plotframe.GetPlotProps()); 
	      top.plotframe.GeneratePlot(top.newPlotProps);
	    }
	}
      
    };
  
}


function ResolveAndGenerate(inCoordinates)
{
  top.inputframe.SetTargetCoords(inCoordinates);
  GeneratePlot();

}


function GetPSPlot()
{

  if (top.PlotLoaded() == 1 || top.inputframe.GetTargetCoords() != "" )
    {

      if ( busyFlag == 1 || (! top.inputframe.Validate() && (top.PlotLoaded() != 1) ))
	{
	  return ;
	};

      busyFlag=1;
      top.Disable(1);
      top.inputframe.ClearInvalidCoords();


      if ( top.PlotLoaded() == 1 )
	{
	  top.newPlotProps.CopyObj(top.plotframe.GetPlotProps());
	}
      else
	{
	  top.InitPlotParams();
	};


      title ="PRoVis Plot for ";
      if ( top.newPlotProps.targetname != "" )
	{
	  title = title + "Target: '"+top.newPlotProps.targetname+"'|";
	};
      
      title = title + "Coords: '"+top.newPlotProps.targetcoords+"'|";
      
      title = title + "Time Interval: "+ top.FromJulian(top.newPlotProps.lX)+"-"+top.FromJulian(top.newPlotProps.hX);

      if ( top.optionsframe.GetCalFile() != "" )
	{
	  title = title + "|Calibration file: '"+top.optionsframe.GetActualCalFile()+"'";
	};

      if ( ( top.optionsframe.GetActualCalFile() != top.newPlotProps.calfile )
	   && (top.PlotLoaded() == 1 ))
	{
	  if (top.optionsframe.GetActualCalFile() == "")
	    {
	      alert("No calibration file was supplied.  Using defaults for postscript only");
	    }
	  else
	    {
	      alert("New calibration file was supplied.  Using new file for postscript only.");
	    };
	};


      var pswidth = top.optionsframe.GetPSwidth();
      var psheight= top.optionsframe.GetPSheight();
      var pswidthunit='i';
      var psheightunit='i';

      if ( pswidth == "" )
	{
	  pswidth = top.newPlotProps.width;
	  pswidthunit='p';
	};

      if ( psheight == "" )
	{
	  psheight = top.newPlotProps.height;
	  psheightunit='p';
	};

      var calFile=top.optionsframe.GetCalFile();
      var tmptargetcoords= top.newPlotProps.targetcoords.replace(/\+/g, "%2B");
      var url = top.provisWebServer+top.provisCGIScript+'?type=ps'
	+'&cf='+calFile
	+'&c='+tmptargetcoords
	+'&title='+title
	+'&jb='+top.newPlotProps.jbase
	+'&sjd='+top.newPlotProps.jlX
	+'&ejd='+top.newPlotProps.jhX
	+'&pf='+top.newPlotProps.viewpitch
	+'&plow='+top.newPlotProps.lP
	+'&phigh='+top.newPlotProps.hP
	+'&pcolor='+top.newPlotProps.pitchcolor
	+'&pline='+top.newPlotProps.pitchlinestyle
	+'&pwidth='+top.newPlotProps.pitchlinewidth
	+'&rf='+top.newPlotProps.viewroll
	+'&rlow='+top.newPlotProps.lR
	+'&rhigh='+top.newPlotProps.hR
	+'&rcolor='+top.newPlotProps.rollcolor
	+'&rline='+top.newPlotProps.rolllinestyle
	+'&rwidth='+top.newPlotProps.rolllinewidth
	+'&rtline='+top.newPlotProps.rolltolerancelinestyle
	+'&vf='+top.newPlotProps.viewvisibility
	+'&vlow='+top.newPlotProps.lV
	+'&vhigh='+top.newPlotProps.hV
	+'&vcolor='+top.newPlotProps.visibilitycolor
	+'&vline='+top.newPlotProps.visibilitylinestyle
	+'&vwidth='+top.newPlotProps.visibilitylinewidth
	+'&mf='+top.newPlotProps.viewmaxexposure
	+'&mlow='+top.newPlotProps.lME
	+'&mhigh='+top.newPlotProps.hME
	+'&mcolor='+top.newPlotProps.maxexpcolor
	+'&mline='+top.newPlotProps.maxexplinestyle
	+'&mwidth='+top.newPlotProps.maxexplinewidth
	+'&mfill='+top.newPlotProps.maxexpfillstyle
	+'&bf='+top.newPlotProps.viewbadpitch
	+'&bline='+top.newPlotProps.badpitchlinestyle
	+'&bwidth='+top.newPlotProps.badpitchlinewidth
	+'&bfill='+top.newPlotProps.badpitchfillstyle
	+'&mt='+top.newPlotProps.exptime
	+'&width='+pswidth
	+'&widthu='+pswidthunit
	+'&height='+psheight
	+'&heightu='+psheightunit
	+'&font='+top.newPlotProps.fonttype+' '+top.newPlotProps.fontsize
	+'&axisLinewidth='+top.newPlotProps.axislinewidth;


      if ( calFile != "" )
	{
	  top.requests.length=3;
	  top.requests[0]=LoadPS;
	  top.requests[1]=url;
	  top.requests[2]="ps";
	  top.requestclearFlag=0;
	  top.requestenableFlag=1;
	  top.optionsframe.UploadCalFile(1);
	}
      else
	{
	  top.LoadFrame(top.hiddenframe,url);
	  busyFlag=0;
	  top.Disable(0);
	};
    }
  else
    {

      if (top.inputframe.GetTargetCoords() == "" && top.inputframe.GetTargetName() != "")
	{
	  ResolveTarget(ResolveAndGeneratePS);
	};
    };
 

}


function LoadPS(inURL)
{
  top.LoadFrame(top.hiddenframe,inURL);
}


function ResolveAndGeneratePS(inCoordinates)
{

  top.inputframe.SetTargetCoords(inCoordinates);

  GetPSPlot();

}


function ClearTarget() 
{
  
  top.inputframe.ClearTarget();

  document.getElementById('resolvetarget').disabled=1;
  document.getElementById('cleartarget').disabled=1;

  ConfigFrame();
}

function ResetDates()
{
  top.inputframe.ResetDates();
  document.getElementById('resetdates').disabled=1;
  ConfigFrame();
}

function ResetOptions()
{
  top.optionsframe.Reset();
  document.getElementById('resetoptions').disabled=1;
  ConfigFrame();
}


function Cancel()
{
  clearTimeout(timeoutId);
  httpRequest.abort();
  top.dataframe.Cancel();
  if ( top.PlotLoaded() == 1 )
  {
  	top.plotframe.Unload();
  };

  busyFlag=0;
  top.Disable(0);

}


function Help()
{
  top.LoadFrame(top.plotframe,top.helpframeLocation);
  ConfigFrame();
}


function Resize()
{
  
  if ( disabledFlag == 1 ) 
    {
      return ;
    };

  maximizeFlag = ! maximizeFlag;

  if ( maximizeFlag == 1 )
    { 
      top.SetLayout('indexframeset',"rows","0,50,*,0");
      top.buttonsframe.document.getElementById('resize').src="images/minimize.bmp";
      top.buttonsframe.document.getElementById('resize').title="Reset plot area size";
    }
  else
    {
      top.SetLayout('indexframeset',"rows","110,50,*,0");
      top.buttonsframe.document.getElementById('resize').src="images/maximize.bmp";
      top.buttonsframe.document.getElementById('resize').title="Maximize plot area size";
    };

}

function ResolveTarget(inHandler,e)
{


  if (typeof e != 'undefined' )
    {
      if (e.keyCode != 13)
	{
	  ConfigFrame();
	  return;
	}
    };

  if ( busyFlag == 1 )
    {
      return ;
    };
  busyFlag=1;
  top.Disable(1);

  top.inputframe.ClearInvalidCoords();
  top.inputframe.SetTargetCoords("");
  var url = top.provisWebServer+top.provisCGIScript+'?type=res&name='+top.inputframe.GetTargetName(); 
  httpRequest.open('GET', url , true);
  httpRequest.onreadystatechange = function(){ HandleServerResponse(httpRequest,inHandler); };
  clearTimeout(timeoutId);
  timeoutId=setTimeout("HandleRequestTimeout();",top.timeout);
  httpRequest.send(null);
}

function HandleServerResponse(http, func)
{

  if (http.readyState == 4) {
    clearTimeout(timeoutId);
    busyFlag =0;
    if (http.responseText != "")
      {
	func(http.responseText);
      };
    ConfigFrame();
    top.Disable(0);
  }
}

function HandleRequestTimeout()
{
  if ( busyFlag == 1 )
    {
      httpRequest.abort();
      busyFlag =0;
      top.Disable(0);
      alert("Your Request Timed out. Please try again");
    };
}

function Disable(inDisableFlag)
{

  disabledFlag = inDisableFlag;

  if ( inDisableFlag )
    {
      document.body.style.cursor=top.busyCursor;
    }
  else
    {
      document.body.style.cursor=top.notbusyCursor;
    };

  for (id in objIds)
    {
      try
	{
	  if ( objIds[id] != "cancel")
	    {
	      document.getElementById(objIds[id]).disabled=inDisableFlag;
	    }
	  else
	    {
	      document.getElementById(objIds[id]).disabled=0;
	    };
	}
      catch(err)
	{
	}; 
    };

  if (! inDisableFlag )
    {
      ConfigFrame();
    }

}




function OnLoad(event) {
  ConfigFrame();
  return true;
}




