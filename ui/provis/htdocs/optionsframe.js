// _INSERT_SAO_COPYRIGHT_HERE_(2008)_
// _INSERT_GPL_LICENSE_HERE_

var disabledFlag = 0;
var objIds = new Array();


objIds[0]='viewpitch';
objIds[1]='viewroll';
objIds[2]='viewvisibility';
objIds[3]='viewbadpitch';
objIds[4]='viewmaxexposure';
objIds[5]='viewplotdata';
objIds[6]='pitchcolor';
objIds[7]='pitchlinestyle';
objIds[8]='rollcolor';
objIds[9]='rolllinestyle';
objIds[10]='rolltolerancelinestyle';
objIds[11]='visibilitycolor';
objIds[12]='visibilitylinestyle';
objIds[13]='badpitchlinestyle';
objIds[14]='badpitchfillstyle';
objIds[15]='maxexpcolor';
objIds[16]='maxexplinestyle';
objIds[17]='maxexpfillstyle';
objIds[18]='autofit';
objIds[19]='zoomfactor';
objIds[20]='movefactor';
objIds[21]='pswidth';
objIds[22]='psheight';
objIds[23]='fonttype';
objIds[24]='fontsize';
objIds[25]='axislinewidth';
objIds[26]='pitchlinewidth';
objIds[27]='rolllinewidth';
objIds[28]='visibilitylinewidth';
objIds[29]='badpitchlinewidth';
objIds[30]='maxexplinewidth';
objIds[31]='viewcrosshair';
objIds[32]='viewpitchcursor';
objIds[32]='viewrollcursor';
objIds[32]='viewvisibilitycursor';
objIds[33]='calfile';

function IsChanged()
{

  if ( document.getElementById('viewpitch').checked!=top.viewpitch
       || document.getElementById('viewroll').checked!=top.viewroll
       || document.getElementById('viewvisibility').checked!=top.viewvisibility
       || document.getElementById('viewbadpitch').checked!=top.viewbadpitch
       //       || document.getElementById('viewmaxexposure').checked!=top.viewmaxexposure
       || document.getElementById('viewplotdata').checked != top.viewplotdata
       || document.getElementById('viewcrosshair').checked != top.viewcrosshair
       || document.getElementById('viewpitchcursor').checked != top.viewpitchcursor
       || document.getElementById('viewrollcursor').checked != top.viewrollcursor
       || document.getElementById('viewvisibilitycursor').checked != top.viewvisibilitycursor
       || document.getElementById('autofit').checked!=top.autofit
       || document.getElementById('zoomfactor').value!=top.zoomfactor
       || document.getElementById('movefactor').value!=top.movefactor
       || document.getElementById('pswidth').value!=top.pswidth
       || document.getElementById('psheight').value!=top.psheight
       || document.getElementById('pitchcolor').options[document.getElementById('pitchcolor').selectedIndex].value != top.pitchcolor
       || document.getElementById('pitchlinestyle').options[document.getElementById('pitchlinestyle').selectedIndex].value != top.pitchlinestyle
       || document.getElementById('rollcolor').options[document.getElementById('rollcolor').selectedIndex].value != top.rollcolor
       || document.getElementById('rolllinestyle').options[document.getElementById('rolllinestyle').selectedIndex].value != top.rolllinestyle
       || document.getElementById('rolltolerancelinestyle').options[document.getElementById('rolltolerancelinestyle').selectedIndex].value != top.rolltolerancelinestyle
       || document.getElementById('visibilitycolor').options[document.getElementById('visibilitycolor').selectedIndex].value != top.visibilitycolor
       || document.getElementById('visibilitylinestyle').options[document.getElementById('visibilitylinestyle').selectedIndex].value != top.visibilitylinestyle
       
       || document.getElementById('badpitchlinestyle').options[document.getElementById('badpitchlinestyle').selectedIndex].value != top.badpitchlinestyle
       || document.getElementById('badpitchfillstyle').options[document.getElementById('badpitchfillstyle').selectedIndex].value != top.badpitchfillstyle
       //       || document.getElementById('maxexpcolor').options[document.getElementById('maxexpcolor').selectedIndex].value != top.maxexpcolor
       //       || document.getElementById('maxexplinestyle').options[document.getElementById('maxexplinestyle').selectedIndex].value != top.maxexplinestyle
       //       || document.getElementById('maxexpfillstyle').options[document.getElementById('maxexpfillstyle').selectedIndex].value != top.maxexpfillstyle
       || document.getElementById('fonttype').options[document.getElementById('fonttype').selectedIndex].value != top.fonttype
       || document.getElementById('fontsize').options[document.getElementById('fontsize').selectedIndex].value != top.fontsize
       || document.getElementById('axislinewidth').options[document.getElementById('axislinewidth').selectedIndex].value != top.axislinewidth
       || document.getElementById('pitchlinewidth').options[document.getElementById('pitchlinewidth').selectedIndex].value != top.pitchlinewidth
       || document.getElementById('rolllinewidth').options[document.getElementById('rolllinewidth').selectedIndex].value != top.rolllinewidth
       || document.getElementById('visibilitylinewidth').options[document.getElementById('visibilitylinewidth').selectedIndex].value != top.visibilitylinewidth
       || document.getElementById('badpitchlinewidth').options[document.getElementById('badpitchlinewidth').selectedIndex].value != top.badpitchlinewidth
       //       || document.getElementById('maxexplinewidth').options[document.getElementById('maxexplinewidth').selectedIndex].value != top.maxexplinewidth 
       )
    {
      return true;
    };

  return false;
}

function GetViewpitch()
{
  if ( document.getElementById('viewpitch').checked )
    {
      return 1;
    };

  return 0;
}

function GetPitchcolor()
{
  return document.getElementById('pitchcolor').options[document.getElementById('pitchcolor').selectedIndex].value;
}

function GetPitchlinestyle()
{
  return document.getElementById('pitchlinestyle').options[document.getElementById('pitchlinestyle').selectedIndex].value;
}


function GetViewroll()
{
  if ( document.getElementById('viewroll').checked )
    {
      return 1;
    };
  return 0;
}


function GetRollcolor()
{
  return document.getElementById('rollcolor').options[document.getElementById('rollcolor').selectedIndex].value;
}

function GetRolllinestyle()
{
  return document.getElementById('rolllinestyle').options[document.getElementById('rolllinestyle').selectedIndex].value;
}

function GetRollTolerancelinestyle()
{
  return document.getElementById('rolltolerancelinestyle').options[document.getElementById('rolltolerancelinestyle').selectedIndex].value;
}


function GetViewvisibility()
{
  if ( document.getElementById('viewvisibility').checked  )
    {
      return 1;
    };

  return 0;
}



function GetVisibilitycolor()
{
  return document.getElementById('visibilitycolor').options[document.getElementById('visibilitycolor').selectedIndex].value;
}

function GetVisibilitylinestyle()
{
  return document.getElementById('visibilitylinestyle').options[document.getElementById('visibilitylinestyle').selectedIndex].value;
}




function GetViewbadpitch()
{
  if ( document.getElementById('viewbadpitch').checked )
    {
      return 1;
    };
  return 0;
}


function GetBadpitchlinestyle()
{
  return document.getElementById('badpitchlinestyle').options[document.getElementById('badpitchlinestyle').selectedIndex].value;
}

function GetBadpitchfillstyle()
{
  return document.getElementById('badpitchfillstyle').options[document.getElementById('badpitchfillstyle').selectedIndex].value;
}


function GetViewmaxexposure()
{
  return 0;

  if ( document.getElementById('viewmaxexposure').checked )
    {
      return 1;
    }

  return 0;
};



function GetMaxexposurecolor()
{
  return top.maxexpcolor;
  return document.getElementById('maxexpcolor').options[document.getElementById('maxexpcolor').selectedIndex].value;
}



function GetMaxexposurelinestyle()
{
  return top.maxexplinestyle;
  return document.getElementById('maxexplinestyle').options[document.getElementById('maxexplinestyle').selectedIndex].value;
}

function GetMaxexposurefillstyle()
{
  return top.maxexpfillstyle;
  return document.getElementById('maxexpfillstyle').options[document.getElementById('maxexpfillstyle').selectedIndex].value;
}

function GetViewplotdata()
{
  return document.getElementById('viewplotdata').checked;
}


function GetExposureTime()
{
  return top.exposureTime;
}


function GetZoomFactor()
{
  if ( isNaN(document.getElementById('zoomfactor').value/1.0)
       || document.getElementById('zoomfactor').value == 0 )
    {
      document.getElementById('zoomfactor').value=top.zoomfactor
    };

  return document.getElementById('zoomfactor').value;
}

function GetMoveFactor()
{

  if ( isNaN(document.getElementById('movefactor').value/1.0)
       || document.getElementById('movefactor').value == 0 )
    {
      document.getElementById('movefactor').value=top.movefactor;

    };

  return document.getElementById('movefactor').value;
}

function GetViewcrosshair()
{
  return document.getElementById('viewcrosshair').checked;		
}

function GetViewpitchcursor()
{
  return document.getElementById('viewpitchcursor').checked;		
}


function GetViewrollcursor()
{
  return document.getElementById('viewrollcursor').checked;		
}

function GetViewvisibilitycursor()
{
  return document.getElementById('viewvisibilitycursor').checked;		
}



function GetAutofit()
{
  return document.getElementById('autofit').checked;
}


function GetPSwidth()
{

  if ( isNaN(document.getElementById('pswidth').value/1.0)
       || document.getElementById('pswidth').value == 0 )
    {
      return "";
      //      Document.getElementById('pswidth').value=top.pswidth;

    }
  else if (document.getElementById('pswidth').value <(top.minplotWidth/72.0) )
    {
      document.getElementById('pswidth').value = (top.minplotWidth/72.0);
    };

  return document.getElementById('pswidth').value;	

}


function GetPSheight()
{

  if ( isNaN(document.getElementById('psheight').value/1.0)
       || document.getElementById('psheight').value == 0 )
    {
      return "";
      //      document.getElementById('psheight').value=top.psheight;

    }
  else if (document.getElementById('psheight').value <(top.minplotHeight/72.0) )
    {
      document.getElementById('psheight').value = (top.minplotHeight/72.0);
    };


  return document.getElementById('psheight').value;	


}


function GetFonttype()
{
  return document.getElementById('fonttype').options[document.getElementById('fonttype').selectedIndex].value;
}


function GetFontsize()
{
  return document.getElementById('fontsize').options[document.getElementById('fontsize').selectedIndex].value;
}


function GetAxislinewidth()
{
  return document.getElementById('axislinewidth').options[document.getElementById('axislinewidth').selectedIndex].value;
}

function GetPitchlinewidth()
{
  return document.getElementById('pitchlinewidth').options[document.getElementById('pitchlinewidth').selectedIndex].value;
}


function GetRolllinewidth()
{
  return document.getElementById('rolllinewidth').options[document.getElementById('rolllinewidth').selectedIndex].value;
}

function GetVisibilitylinewidth()
{
  return document.getElementById('visibilitylinewidth').options[document.getElementById('visibilitylinewidth').selectedIndex].value;
}

function GetBadpitchlinewidth()
{
  return document.getElementById('badpitchlinewidth').options[document.getElementById('badpitchlinewidth').selectedIndex].value;
}

function GetMaxexposurelinewidth()
{
  return top.maxexplinewidth;
  return document.getElementById('maxexplinewidth').options[document.getElementById('maxexplinewidth').selectedIndex].value;
}




function Reset() 
{
  var oldviewplotdata=document.getElementById('viewplotdata').checked;


  document.getElementById('viewpitch').checked=top.viewpitch;
  document.getElementById('viewroll').checked=top.viewroll;
  document.getElementById('viewvisibility').checked=top.viewvisibility;
  document.getElementById('viewbadpitch').checked=top.viewbadpitch;
  //  document.getElementById('viewmaxexposure').checked=top.viewmaxexposure;
  document.getElementById('viewplotdata').checked=top.viewplotdata;
  document.getElementById('viewcrosshair').checked=top.viewcrosshair;	
  document.getElementById('viewpitchcursor').checked = top.viewpitchcursor;
  document.getElementById('viewrollcursor').checked = top.viewrollcursor;
  document.getElementById('viewvisibilitycursor').checked = top.viewvisibilitycursor;
  document.getElementById('autofit').checked=top.autofit;
  document.getElementById('zoomfactor').value=top.zoomfactor;
  document.getElementById('movefactor').value=top.movefactor;
  document.getElementById('pswidth').value=top.pswidth;
  document.getElementById('psheight').value=top.psheight;	


  SetListSelection(document.getElementById('pitchcolor'),top.pitchcolor);
  SetListSelection(document.getElementById('pitchlinestyle'),top.pitchlinestyle);
  SetListSelection(document.getElementById('rollcolor'),top.rollcolor);
  SetListSelection(document.getElementById('rolllinestyle'),top.rolllinestyle);
  SetListSelection(document.getElementById('rolltolerancelinestyle'),top.rolltolerancelinestyle);
  SetListSelection(document.getElementById('visibilitycolor'),top.visibilitycolor);
  SetListSelection(document.getElementById('visibilitylinestyle'),top.visibilitylinestyle);

  SetListSelection(document.getElementById('badpitchlinestyle'),top.badpitchlinestyle);
  SetListSelection(document.getElementById('badpitchfillstyle'),top.badpitchfillstyle);

  //  SetListSelection(document.getElementById('maxexpcolor'),top.maxexpcolor);
  //  SetListSelection(document.getElementById('maxexplinestyle'),top.maxexplinestyle);
  //  SetListSelection(document.getElementById('maxexpfillstyle'),top.maxexpfillstyle);
  SetListSelection(document.getElementById('fonttype'),top.fonttype);
  SetListSelection(document.getElementById('fontsize'),top.fontsize);
  SetListSelection(document.getElementById('axislinewidth'),top.axislinewidth);
  SetListSelection(document.getElementById('pitchlinewidth'),top.pitchlinewidth);
  SetListSelection(document.getElementById('rolllinewidth'),top.rolllinewidth);
  SetListSelection(document.getElementById('visibilitylinewidth'),top.visibilitylinewidth);
  SetListSelection(document.getElementById('badpitchlinewidth'),top.badpitchlinewidth);
  //  SetListSelection(document.getElementById('maxexplinewidth'),top.maxexplinewidth);





  if ( document.getElementById('viewplotdata').checked )
    {
      if ( top.PlotLoaded() == 1 )
	{
	  top.dataframe.Hide(0);
	};
    }
  else
    {
      top.dataframe.Hide(1);
    }
  

  
  if ( top.PlotLoaded() == 1 
       && document.getElementById('autofit').checked 
       && oldviewplotdata == document.getElementById('viewplotdata').checked )
    {
      var tmpPlotProps = top.plotframe.GetUpdatedPlotProps(0);
      top.plotframe.GeneratePlot(tmpPlotProps,0);
    };
  

}

function SetListSelection(inSelect,inText)
{

  for ( id= 0;id < inSelect.length;id++)
    {
      if ( inSelect.options[id].value == inText)
	{
	  inSelect.selectedIndex=id;
	}
    }
}


function Disable(inDisableFlag)
{
  disabledFlag = inDisableFlag;

   for (id in objIds)
    {
      
      try
	{
	  document.getElementById(objIds[id]).disabled=inDisableFlag;
	}
      catch(err)
	{
	}; 
    } 


   if ( document.getElementById('zoomfactor').value == ""  || isNaN(document.getElementById('zoomfactor').value/1.0) )
     {
       document.getElementById('zoomfactor').value=top.zoomfactor;
     };

   if ( document.getElementById('movefactor').value == "" || isNaN(document.getElementById('movefactor').value/1.0) )
     {
       document.getElementById('movefactor').value=top.movefactor;
     };
   if( inDisableFlag )
     {
       document.body.style.cursor=top.busyCursor;
     }
   else
     {
       document.body.style.cursor=top.notbusyCursor;
     };
}




function OnClick(inId)
{
  if ( disabledFlag == 1 )
    {
      return;
    }
  
  if ( inId == "viewplotdata")
    {
      if ( document.getElementById('viewplotdata').checked )
	{
	  if ( top.PlotLoaded() == 1 )
	    {
	      top.dataframe.Hide(0);
	    };
	}
      else
	{
	  top.dataframe.Hide(1);
	}

      if ( top.PlotLoaded() == 1 )
	{
	  var tmpPlotProps = top.plotframe.GetUpdatedPlotProps(0);
	  top.plotframe.GeneratePlot(tmpPlotProps,0);
	};
    }
  else if ( inId == "autofit" )
    {
      if ( document.getElementById('autofit').checked )
	{ 
	  if ( top.PlotLoaded() == 1 )
	    {
	      top.plotframe.OnResize();
	    };
	}
    }
  else if ( inId == "viewcrosshair" )
    {
      if ( top.PlotLoaded() == 1 )
	{
	  top.plotframe.DisplayCrosshair(document.getElementById('viewcrosshair').checked )
	}
    }
  else if (inId == "viewpitchcursor" || inId == "viewrollcursor" || inId == "viewvisibilitycursor")
    {     
      if ( top.PlotLoaded() == 1 )
	{
	  top.plotframe.DisplayCurrentCoords(0,0);
	};
    };


  top.buttonsframe.ConfigFrame();


}

function OnLoad(event) {

  Disable(0);
  top.OptionsLoaded(1);
  Reset();
  if ( top.GetFrameHeight('optionsframe') < 50 )
    {
      top.SetLayout("inputframesetcol","rows","50%,50%");
    };

  HideCalFileEntry(!top.enableCalFileFunctionality);

  return true;
}

function HideCalFileEntry(inHideFlag)
{
  if ( inHideFlag )
    { 
      if (top.GetFrameWidth('inputframe') == 250 )
	{
	  top.SetLayout('inputframeset',"cols","235,*");
	};
     // document.getElementById('optionstable').rows[21].cells[0].innerHTML='';

    }
  else
    {
      if (top.GetFrameWidth('inputframe') == 235 )
	{
	  top.SetLayout('inputframeset',"cols","250,*");
	};

      top.SetLayout('inputframesetcol',"rows","51%,49%");	
      
      //document.getElementById('optionstable').rows[21].cells[0].innerHTML='<div style="text-align: left;"><span style="font-weight: bold;">Calibration File</span><br> <span style="font-weight: bold;"></span></div> <div style="text-align: center;"><span style="font-weight: bold;"> <form id="calfileform" action="'+top.provisCGIScriptUpload+'" method="post" enctype="multipart/form-data" target="_blank">'
//	+'<input id="calfile" name="calfile" type="file" onkeyup="top.buttonsframe.ConfigFrame();" onchange="top.buttonsframe.ConfigFrame();" onfocus="top.buttonsframe.ConfigFrame();">'
//	+'<input id="sessionid" name="sessionid" type="hidden" ><input id="clientcount" name="clientcount" type="hidden" ></form></span>'
//	+'</div>';
      
      //	+'<div>'
      // +'<iframe  style="display:none" src="titleframe.html"  id="hiddeniframe" name="hiddeniframe" onload="CalFileUploaded(event);"></iframe>'
      //      +'</div>';

      //     document.getElementById('hiddeniframe').setAttribute('onload',"CalFileUploaded(event);");
      document.getElementById('calfileform').setAttribute('target', 'hiddeniframe');

    }

}


function UploadCalFile(inExpectedClientCount)
{ 
  if ( top.enableCalFileFunctionality )
    { 
      if (GetActualCalFile() != "" )
	{
	  document.getElementById('sessionid').value=top.sessionId+"."+top.sessionIndx;
	  document.getElementById('clientcount').value=inExpectedClientCount;
	  document.getElementById("calfile").disabled=0;
	  document.getElementById('calfileform').submit();	
	  document.getElementById("calfile").disabled=1;
	};
    };
}

function GetActualCalFile()
{
  if ( top.enableCalFileFunctionality )
    {
      var tmpStr=top.optionsframe.document.getElementById('calfile').value;
      tmpStr = tmpStr.replace(/ /g,"");
      return tmpStr;
    };
  return "";
}

function GetCalFile()
{
  if ( top.enableCalFileFunctionality )
    {
      var tmpStr=top.optionsframe.document.getElementById('calfile').value;
      tmpStr = tmpStr.replace(/ /g,"");
      
      if ( tmpStr != "" )
	{
	  return top.sessionId+"."+top.sessionIndx;
	};
    };

  return "";

}
function CleanupUpload()
{
  var calFile=top.optionsframe.GetCalFile();
  if ( calFile != "" )
    {
      var url = top.provisWebServer+top.provisCGIScript+'?type=cleanup'+'&cf='+calFile;
      httpRequest.open('GET', url , true);
      httpRequest.send(null);
    };
}

function CalFileUploaded(inEvent)
{

  var tmpStr;

  if(document.getElementById('hiddeniframe').contentDocument)
    {
      tmpStr = document.getElementById('hiddeniframe').contentDocument.body.innerHTML;
    }
  else if( document.getElementById('hiddeniframe').contentWindow)
    {
      tmpStr = document.getElementById('hiddeniframe').contentWindow.document.body.innerHTML;
    }

  var status = Number(tmpStr);
  // document.getElementById('hiddeniframe').contentDocument.body.innerHTML);

  if (status == 1)
    {
      //alert("SUCCESS");
      if ( top.PlotLoaded() == 1 || ( top.requests.length == 3 && top.requests[2] == "ps"))
	{
	  for ( indx= 0;indx < (top.requests.length-1);indx=indx+3)
	    {
	      top.requests[indx](top.requests[indx+1]);
	    }
	}
      else
	{
	  CleanupUpload();
	};
      top.sessionIndx += 1;

    }
  else if ( status == 0 )
    {
      alert("Error Uploading Calibration File '"+GetActualCalFile()+"'");
      if ( top.requestclearFlag )
	{
	  top.PlotLoaded(0);
	  top.dataframe.Hide(1);
	  top.dataframe.Clear();
	  top.LoadFrame(top.plotframe,top.welcomeLocation);
	  top.SetBusy(0);
	  top.Disable(0);
	};
    };

  if ( top.requestenableFlag )
    {
      top.SetBusy(0);
      top.Disable(0);
    };

  top.requests.length=0;
  top.requestsclearFlag=0;
  top.requestenableFlag=0;


}


