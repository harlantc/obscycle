// _INSERT_SAO_COPYRIGHT_HERE_(2008)_
// _INSERT_GPL_LICENSE_HERE_

var busyFlag=0;
var sjDate=0;
var ejDate=0;
var jDatebase=0;
var targetcoords="";
var targetname="";
var pitch=0;
var roll=0;
var vis=0;
var badpitch=0;
var maxexp=0;
var exptime=0;
var calfile="";
var httpRequest;
var timeoutId;
var timeData = new Array();
var rollData = new Array();
var rolltolData = new Array();
var visibilityData = new Array();
var pitchData = new Array();
var pitchtypeData = new Array();
var badpitchData = new Array();
var maxexpData = new Array();
var maxexplowData = new Array();
var maxexphighData = new Array();
var yearLabel="YYYY";
var monthLabel="MM";
var dayLabel="DD";
var rollLabel="ROLL";
var rolltolLabel="ROLL_TOL";
var visibilityLabel="VIS";
var pitchLabel="PITCH";
var pitchtypeLabel="PITCH_TYPE";
var maxexplowLabel="MAX_EXP_LOW";
var maxexphighLabel="MAX_EXP_HIGH";
var timeLabel="MJD";
var starterrorLabel="START ERROR";
var enderrorLabel="END ERROR";
var startbadpitchLabel="START BPITCH";
var endbadpitchLabel="END BPITCH";
var startmaxexpLabel="START MAXEXP";
var endmaxexpLabel="END MAXEXP";
var datatableColCount=0;
var selectedRows = new Array();
var pitch_restriction_update_date="";
var calRollTol = new Array();


var displayedFlag=0;

if ( window.XMLHttpRequest ) 
{
  httpRequest=new XMLHttpRequest();
} 
else if (window.ActiveXObject ) 
{
  httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
};


function Compare(inPlotProps)
{ 

  if ( inPlotProps.jlX == sjDate
       && inPlotProps.jhX == ejDate
       && inPlotProps.jbase == jDatebase
       && inPlotProps.targetcoords == targetcoords 
       && inPlotProps.targetname == targetname 
       && inPlotProps.viewpitch == pitch
       && inPlotProps.viewroll == roll
       && inPlotProps.viewvisibility == vis
       && inPlotProps.viewbadpitch == badpitch
       && inPlotProps.viewmaxexposure == maxexp
       && inPlotProps.exptime == exptime
       && inPlotProps.calfile == calfile )
    {

      if ( top.enableCalFileFunctionality)
	{
	  if ( (inPlotProps.calfile == calfile) && (calfile != "" ))
	    {
	      return 0;
	    }
	};

      return 1;
    };

  return 0;
}


function GetPlotData(inPlotProps)
{

  if ( busyFlag == 1 )
    {
      return;
    }


  if ( Compare(inPlotProps) )
    {
      if ( displayedFlag == 0 )
	{  
	  if ( top.optionsframe.GetViewplotdata() )
	    {
	      busyFlag=1;
	      top.Disable(1);
	      DisplayData();
	      busyFlag=0;
	      top.Disable(0);
	    };
	};
      return;
    };


  busyFlag=1;
  top.Disable(1);


  Clear();


  sjDate =  inPlotProps.jlX;
  ejDate =  inPlotProps.jhX;
  jDatebase =  inPlotProps.jbase;
  targetcoords = inPlotProps.targetcoords;
  targetname =  inPlotProps.targetname;
  pitch = inPlotProps.viewpitch;
  roll =  inPlotProps.viewroll;
  vis =  inPlotProps.viewvisibility;
  badpitch=  inPlotProps.viewbadpitch;
  maxexp =  inPlotProps.viewmaxexposure;
  exptime = inPlotProps.exptime;
  calfile = inPlotProps.calfile;
  
  var calFile=top.optionsframe.GetCalFile();
      
  
  var tmptargetcoords1= targetcoords.replace(/\+/g, "%2B");
  var url = top.provisWebServer+top.provisCGIScript;
  
  url = url+ '?type=data'
    +'&cf='+calFile
    +'&sjd='+sjDate
    +'&ejd='+ejDate
    +'&jb='+jDatebase
    +'&c='+tmptargetcoords1
    +'&pf='+pitch
    +'&rf='+roll
    +'&vf=1'
    +'&mf='+maxexp
    +'&bf='+badpitch
    +'&mt='+exptime;
  
  httpRequest.open('GET', url , true);
  httpRequest.onreadystatechange = function(){ HandleServerResponse(httpRequest,PlotDataHandler); };
  clearTimeout(timeoutId);
  pitch_restriction_update_date="";
  timeoutId=setTimeout("HandleRequestTimeout();",top.timeout);
  httpRequest.send(null);

}

function HandleServerResponse(http, func)
{

  if (http.readyState == 4) {
    clearTimeout(timeoutId);

    if (http.responseText != "")
      {
	func(http.responseText);
      };
    busyFlag=0;
    top.Disable(0);
  }
}

function HandleRequestTimeout()
{
  Cancel();
  alert("Your Request for Plot Data Timed out. Please try again");
}

function Cancel()
{
  clearTimeout(timeoutId);		  
  httpRequest.abort(); 
  Hide(1);
  Clear();
  busyFlag=0;
  top.Disable(0);
}

function ReadData(inText)
{
  var tmpValArray;
  var timeIndx=-1;
  var rollIndx=-1;
  var rolltolIndx=-1;
  var visibilityIndx=-1;
  var pitchIndx=-1;
  var pitchtypeIndx=-1;
  var maxexplowIndx=-1;
  var maxexphighIndx=-1;
  var curplottitle="";

 
  pitch_restriction_update_date=inText.substring(0,inText.indexOf("\n"));


  if ( top.PlotLoaded())
    {
      top.plotframe.SetPitchUpdateDate(pitch_restriction_update_date);
    };

  inText = inText.substring(inText.indexOf("\n")+1);
  var tmpArray = inText.split('\n');


  id =0;
  while(id < tmpArray.length)
    {


      if ( tmpArray[id] == starterrorLabel )
	{
	  var errorString="";
	  id++;
	  while ( tmpArray[id] != enderrorLabel && id < tmpArray.length )
	    {
	      tmpValArray = tmpArray[id].split('|');
	      for (id1 in tmpValArray )
		{
		  if (tmpValArray[id1] != "" ) 
		    {
		      errorString +=tmpValArray[id1];
		    }
		}
	      id++;
	    };



	  alert(errorString);
	  top.PlotLoaded(0);
	  top.dataframe.Hide(1);
	  top.dataframe.Clear();
	  top.LoadFrame(top.plotframe,top.welcomeLocation);
	  top.SetBusy(0);
	  top.Disable(0);
          if (errorString.indexOf("Coordinates are invalid"  ,0) > 0) {
               top.inputframe.SetInvalidCoords(); 
          }
	  break;
	};

      
      if ( tmpArray[id] == startbadpitchLabel )
	{
	  id++;
	  while ( tmpArray[id] != endbadpitchLabel && id < tmpArray.length )
	    {
	      tmpValArray = tmpArray[id].split('|');

	      for (id1 in tmpValArray )
		{
		  if (tmpValArray[id1] != "" ) 
		    {
		      badpitchData[badpitchData.length]=tmpValArray[id1];
		    }
		}
	      id++;
	    };
	  continue;
	};
      
      
      var tmpValArray = tmpArray[id].split('|');


      for (id1 in tmpValArray )
	{
	  if (tmpValArray[id1] != "" ) 
	    {
	      
	      if ( id == 0 )
		{
		  
		  if (tmpValArray[id1] == timeLabel )
		    {
		      timeIndx=id1;
		    }
		  else if (tmpValArray[id1] == rollLabel )
		    {
		      rollIndx=id1;
		    }
		  else if (tmpValArray[id1] == rolltolLabel )
		    {
		      rolltolIndx=id1;
		    }
		  else if (tmpValArray[id1] == visibilityLabel )
		    {
		      visibilityIndx=id1;
		    }
		  else if (tmpValArray[id1] == pitchLabel )
		    {
		      pitchIndx=id1;
		    }
		  else if (tmpValArray[id1] == pitchtypeLabel )
		    {
		      pitchtypeIndx=id1;
		    }
		   else if (tmpValArray[id1] == maxexplowLabel )
		    {
		      maxexplowIndx=id1;
		    }
		  else if (tmpValArray[id1] == maxexphighLabel )
		    {
		      maxexphighIndx=id1;
		    };

		}
	      else
		{
		  if ( timeIndx != -1 && id1 == timeIndx)
		    {
		      timeData[id-1]=Number(tmpValArray[id1]);
		    };
		  
		  if ( rollIndx != -1 && id1 == rollIndx)
		    {
		      rollData[id-1]=Number(tmpValArray[id1]);
		    };

		   if ( rolltolIndx != -1 && id1 == rolltolIndx)
		    {
		      rolltolData[id-1]=Number(tmpValArray[id1]);
		    };

		  if ( visibilityIndx != -1 && id1 == visibilityIndx)
		    {
		      visibilityData[id-1]=Number(tmpValArray[id1]);
		    };

		  if ( pitchIndx != -1 && id1 == pitchIndx)
		    {
		      pitchData[id-1]=Number(tmpValArray[id1]);
		    };
		  
		  if ( pitchtypeIndx != -1 && id1 == pitchtypeIndx)
		    {
		      pitchtypeData[id-1]=tmpValArray[id1];
		    };

		   if ( maxexplowIndx != -1 && id1 == maxexplowIndx)
		    {
		      maxexplowData[id-1]=tmpValArray[id1];
		    };

		   if ( maxexphighIndx != -1 && id1 == maxexphighIndx)
		    {
		      maxexphighData[id-1]=tmpValArray[id1];
		    };
		};
	    }
	}

      id++;
    }

  ToggleSelectedRow();


}


function PlotDataHandler(inText)
{
  ReadData(inText);
  
  if ( top.optionsframe.GetViewplotdata() )
    {
      DisplayData();
    }
  else
    {
      Hide(1);
      ClearDisplay();
    };

}

function HighlightRow(inRow,inColor)
{
  if (inRow == 0 )
    {
      return;
    };

  var tmpId;

  if ( inColor == top.tableRowNoColor )
    {
      
      top.plotframe.DisplayDataCursor();
      if ( selectedRows[inRow] )
	{
	  inColor=top.tableRowSelectColor;
	};
    }
  else
    {
      top.plotframe.DisplayDataCursor(timeData[inRow-1]);
    };

  for ( col= 0;col < datatableColCount;col++)
    {
      tmpId= "("+inRow+","+col+")";
      document.getElementById(tmpId).style.backgroundColor=inColor;
    };
  

  
}


function ToggleSelectedRow(inRow)
{
  if ( typeof inRow != 'undefined')
    {
      if ( inRow == 0 )
	{
	  return ;
	};
      
      var tmpId;
      var tmpColor;
      selectedRows[inRow]=!selectedRows[inRow];
      
      
      if ( selectedRows[inRow] )
	{
	  tmpColor=top.tableRowSelectColor;
	}
      else
	{
	  tmpColor=top.tableRowNoColor;
	};
      
      HighlightRow(inRow,tmpColor)
	
    };

  var tmpSelectionArray = new Array();

  var startIndx=0;
  var endIndx=1;
  var row=1;
  while(row<selectedRows.length)
    {
      while(row<selectedRows.length && !selectedRows[row])
	{
	  row++;
	};

      if (row<selectedRows.length)
	{
	  tmpSelectionArray[startIndx]=timeData[row-1];
	  tmpSelectionArray[endIndx]=timeData[row-1];
	  while(row<selectedRows.length && selectedRows[row])
	    {
	      row++;
	    };
	  
	  tmpSelectionArray[endIndx]=timeData[row-2];

	  startIndx+=2;
	  endIndx+=2;

	};
    };
  
  top.plotframe.DisplaySelection(tmpSelectionArray);


}

function GenHTMLTableCell(inRow,inCol,inText)
{

  var tmpId= "("+inRow+","+inCol+")";
  return '<td id='+tmpId+' style="text-align: center;" onclick="ToggleSelectedRow('+inRow+');" onmouseover="HighlightRow('+inRow+",'"+top.tableRowHighlightColor+"');"+'" onmouseout="HighlightRow('+inRow+','+"'"+top.tableRowNoColor+"'"+');" ><small style="font-weight: bold;">'+inText+'</small></td>';
}


function DisplayData()
{
  var tmpBody="";

  var outputFrame=top.dataframe;


  if ( 1 == 0 )
    {
      outputFrame=window.open('','plotdataframe','toolbar=yes,menubar=yes,status=yes,scrollbars=yes,width=700');
    }

  var title ="PRoVis PLOT DATA<br>";
  var sDate= top.FromJulian(sjDate,0);
  var eDate= top.FromJulian(ejDate,0);

  if ( targetname != "" )
    {
      title = title + "Target Name: '"+targetname+"'<br>";
    }
  
  title = title + "Target Coordinates: '"+targetcoords+"'<br>Time Interval: "+ sDate+"-"+eDate+"<br>";
  
  if ( top.optionsframe.GetActualCalFile() != "" )
    {
      title = title + "Calibration file: '"+top.optionsframe.GetActualCalFile()+"'<br>";
    }
  //title=title + "Last Pitch Restriction Update: ";

  if ( pitch_restriction_update_date == "")
    {
      //title = title+"unknown";
    }
  else
    {
      //title = title + pitch_restriction_update_date;
    };

  tmpBody = '<table id="datatable" style="text-align: center; width: 100%;" border="0" cellpadding="0" cellspacing="0"> <caption><small style="font-weight: bold;">'+title+'</small></caption><tbody>';
  
  /*  
      for ( indx= 0;indx < badpitchData.length;indx=indx+3)
      {
      tmpBody = tmpBody + '<tr>' +GenHTMLTableCell(badpitchData[indx])+'</tr>'; 
      tmpBody = tmpBody + '<tr>' +GenHTMLTableCell(badpitchData[indx+1])+'</tr>'; 
      tmpBody = tmpBody + '<tr>' +GenHTMLTableCell(badpitchData[indx+2])+'</tr>'; 
      }
  */

  datatableColCount=0;

  selectedRows.length=timeData.length;
  
  tmpBody = tmpBody + "<tr>";
  
  
  tmpBody=tmpBody + GenHTMLTableCell(0,datatableColCount++,yearLabel);
  
  tmpBody=tmpBody + GenHTMLTableCell(0,datatableColCount++,monthLabel);
  
  tmpBody=tmpBody + GenHTMLTableCell(0,datatableColCount++,dayLabel);
  
  tmpBody=tmpBody + GenHTMLTableCell(0,datatableColCount++,timeLabel);
  
  
  
  if ( rollData.length != 0 )
    {
      tmpBody=tmpBody + GenHTMLTableCell(0,datatableColCount++,rollLabel);
    };
  
  if ( visibilityData.length != 0 )
    {
      tmpBody=tmpBody + GenHTMLTableCell(0,datatableColCount++,visibilityLabel);
    };
  
  
  if ( rolltolData.length != 0 )
    {
      tmpBody=tmpBody + GenHTMLTableCell(0,datatableColCount++,rolltolLabel);
    };
  
  
  if ( pitchData.length != 0 )
    {
      tmpBody=tmpBody + GenHTMLTableCell(0,datatableColCount++,pitchLabel);
    };
  
  
  if ( pitchtypeData.length != 0 )
    {
      tmpBody=tmpBody + GenHTMLTableCell(0,datatableColCount++,pitchtypeLabel);
    };
  
  if ( maxexplowData.length != 0 )
    {
      tmpBody=tmpBody + GenHTMLTableCell(0,datatableColCount++,maxexplowLabel);
    };
  
  if ( maxexphighData.length != 0 )
    {
      tmpBody=tmpBody + GenHTMLTableCell(0,datatableColCount,maxexphighLabel);
    };
  
  tmpBody = tmpBody +"</tr>";
  
  var tmpCol;
  
  for ( indx= 0;indx < timeData.length;indx++)
    {
      tmpCol=0;
      tmpBody = tmpBody + "<tr>";
      
      
      
      var tmpDate=top.FromJulian(timeData[indx]);
      var tmpDateArray = tmpDate.split('/');
      
      tmpBody=tmpBody + GenHTMLTableCell(indx+1,tmpCol++,tmpDateArray[0]);
      
      tmpBody=tmpBody + GenHTMLTableCell(indx+1,tmpCol++,tmpDateArray[1]);
      
      tmpBody=tmpBody + GenHTMLTableCell(indx+1,tmpCol++,tmpDateArray[2]);
      
      tmpBody=tmpBody + GenHTMLTableCell(indx+1,tmpCol++,timeData[indx].toFixed(1));
      
      if ( rollData.length != 0 )
	{
	  tmpBody=tmpBody + GenHTMLTableCell(indx+1,tmpCol++,rollData[indx].toFixed(1));
	};
      
      if ( visibilityData.length != 0 )
	{
	  tmpBody=tmpBody + GenHTMLTableCell(indx+1,tmpCol++,visibilityData[indx].toFixed(2));
	};
      
      
      if ( rolltolData.length != 0 )
	{
	  tmpBody=tmpBody + GenHTMLTableCell(indx+1,tmpCol++,rolltolData[indx].toFixed(2));
	};
      
      
      if ( pitchData.length != 0 )
	{
	  tmpBody=tmpBody + GenHTMLTableCell(indx+1,tmpCol++,pitchData[indx].toFixed(1));
	};
      
      
      if ( pitchtypeData.length != 0 && pitchtypeData[indx].indexOf("nominal") < 0 )
	{
	  tmpBody=tmpBody + GenHTMLTableCell(indx+1,tmpCol++,pitchtypeData[indx]);
	};
      
      if ( maxexplowData.length != 0 )
	{
	      tmpBody=tmpBody + GenHTMLTableCell(indx+1,tmpCol++,maxexplowData[indx].toFixed(2));
	};
      
      if ( maxexphighData.length != 0 )
	{
	  tmpBody=tmpBody + GenHTMLTableCell(indx+1,tmpCol++,maxexphighData[indx].toFixed(2));
	};
      
      
      
      
      tmpBody = tmpBody +"</tr>";
    };
  
  
  tmpBody = tmpBody + "</tbody></table>";
  outputFrame.document.getElementById('maintable').rows[0].cells[0].innerHTML=tmpBody;
  
  Hide(0);

  displayedFlag=1;
}


function ClearDisplay()
{

  document.getElementById('maintable').rows[0].cells[0].innerHTML="";
  displayedFlag=0;
}

function Clear()
{
  sjDate=0;
  ejDate=0;
  jDatebase=0;
  targetcoords="";
  targetname="";
  pitch=0;
  roll=0;
  vis=0;
  badpitch=0;
  maxexp=0;
  exptime=0;
  calfile="";
  timeData.length = 0;
  rollData.length = 0;
  rolltolData.length = 0;
  visibilityData.length = 0;
  pitchData.length = 0;
  pitchtypeData.length = 0;
  badpitchData.length = 0;
  maxexpData.length = 0;
  maxexplowData.length = 0;
  maxexphighData.length = 0;
  document.getElementById('maintable').rows[0].cells[0].innerHTML="";
  displayedFlag=0;
  datatableColCount=0;
  selectedRows.length=0;
  
}


function Hide(inHideFlag)
{

  if ( inHideFlag == 1 )
    {
      top.SetLayout('plotframeset',"rows","100%,0");
    }
  else
    {
      if ( top.GetFrameHeight('dataframe') < 50 )
	{
	  top.SetLayout("plotframeset","rows","50%,50%");
	};
    };
  

}


function Disable(inDisableFlag)
{
  if ( inDisableFlag )
    {
      document.body.style.cursor=top.busyCursor;
    }
  else
    {
      document.body.style.cursor=top.notbusyCursor;
    };

}

function ReadCAL(inText)
{
  if (inText.toLowerCase().indexOf("error") >= 0) {
     alert("ERROR: Unable to read calibration file for roll tolerance values.");
  }
  else {
    var tmpArray = inText.split('\n');
    for (var id=0;id < tmpArray.length; id++) {
      if (tmpArray[id] > 0) {
        calRollTol[id] = Number(tmpArray[id]); 
        id++;
        calRollTol[id] = Number(tmpArray[id]); 
      }
    }
  }
}

function OnLoad(inEvent)
{

  Clear();
  top.DataLoaded(1);


  var calurl= "/cgi-bin/provis/provis_readcal.cgi?mt=" + top.exposureTime;
  var calHttpRequest;
  if ( window.XMLHttpRequest ) 
    calHttpRequest=new XMLHttpRequest();
  else if (window.ActiveXObject ) 
    calHttpRequest = new ActiveXObject("Microsoft.XMLHTTP");

  calHttpRequest.open('GET', calurl , true);
  calHttpRequest.onreadystatechange = function(){ HandleServerResponse(calHttpRequest,ReadCAL); };
  calHttpRequest.send(null);

}
