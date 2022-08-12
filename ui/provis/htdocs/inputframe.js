// _INSERT_SAO_COPYRIGHT_HERE_(2008)_
// _INSERT_GPL_LICENSE_HERE_

var disabledFlag = 0;
var objIds = new Array();


objIds[0]='SYYYY';
objIds[1]='SMM';
objIds[2]='SDD';
objIds[3]='EYYYY';
objIds[4]='EMM';
objIds[5]='EDD';
objIds[6]='targetcoords';
objIds[7]='targetname';



var httpRequest;

if ( window.XMLHttpRequest ) 
{
  httpRequest=new XMLHttpRequest();
} 
else if (window.ActiveXObject ) 
{
  httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
};


function isBlank(s)
{
  var ii;
  for (ii=0; ii < s.length; ii++) {
     var ch = s.charAt(ii);
     if ((ch != '') && (ch != ' ') && (ch != '\n') && (ch != '\t')) {
        return false;
     }
  }
  return true;
}


function SetInvalidCoords() {
  var coordsColor='red';
  document.getElementById('targetcoords').style.backgroundColor=coordsColor;
  document.getElementById('coorderror').value = "1";
  return true;
}

function ClearInvalidCoords() {
  var coordsColor='white';
  document.getElementById('coorderror').value = "0";
  document.getElementById('targetcoords').style.backgroundColor=coordsColor;
  return true;
}

function InitFrame()
{
  Disable(0);
  document.getElementById('SYYYY').value=top.SYYYY;
  document.getElementById('SMM').value=top.SMM;
  document.getElementById('SDD').value=top.SDD;
  document.getElementById('EYYYY').value=top.EYYYY;
  document.getElementById('EMM').value=top.EMM;
  document.getElementById('EDD').value=top.EDD;

}


function Validate()
{
  var tmpCoords=document.getElementById('targetcoords').value;
  var tmpName =document.getElementById('targetname').value;
  var SJulian = top.ToJulian(top.SYYYY,top.SMM,top.SDD);
  var EJulian = top.ToJulian(top.EYYYY,top.EMM,top.EDD);
  var CSJulian = top.ToJulian(GetSYear(),GetSMonth(),GetSDay());
  var CEJulian = top.ToJulian(GetEYear(),GetEMonth(),GetEDay());

  var SYYYYColor='white';
  var SMMColor='white';
  var SDDColor='white';
  var EYYYYColor='white';
  var EMMColor='white';
  var EDDColor='white';
  var coordsColor='white';
  
  var validFlag=1;

				   
  
  if (isNaN(GetSYear()) || GetSYear()/2.0 == 0 || GetSYear() <=0)
    {
      SYYYYColor='red'; 
      validFlag=0;
    };

  if (isNaN(GetSMonth()) || GetSMonth()/2.0 == 0 || GetSMonth() > 12 || GetSMonth() <=0 )
    {
      SMMColor='red'; 
      validFlag=0;
    };

  if (isNaN(GetSDay()) || GetSDay()/2.0 == 0 || GetSDay() <= 0 
      || ((GetSMonth()==1 || GetSMonth()==3 || GetSMonth()==5 || GetSMonth()==7 || GetSMonth()==8 || GetSMonth()==10 || GetSMonth()==12) && (GetSDay() > 31)) 
      || ((GetSMonth()==4 || GetSMonth()==6 || GetSMonth()==9 || GetSMonth()==11) && (GetSDay() > 30))
      || (GetSMonth()==2 && GetSDay()>29 && IsLeapYear(GetSYear()))
      || (GetSMonth()==2 && GetSDay()>28 && !IsLeapYear(GetSYear())))
    {
      SDDColor='red'; 
      validFlag=0;
    };

  if (isNaN(GetEYear()) || GetEYear()/2.0 == 0 || GetEYear() <= 0)
    {
      EYYYYColor='red'; 
      validFlag=0;
    };

  if (isNaN(GetEMonth())|| GetEMonth()/2.0 == 0 || GetEMonth() > 12 || GetEMonth() <=0 )
    {
      EMMColor='red'; 
      validFlag=0;
    };

  if (isNaN(GetEDay()) || GetEDay()/2.0 == 0 || GetEDay() <= 0
      || ((GetEMonth()==1 || GetEMonth()==3 || GetEMonth()==5 || GetEMonth()==7 || GetEMonth()==8 || GetEMonth()==10 || GetEMonth()==12) && (GetEDay() > 31))
      || ((GetEMonth()==4 || GetEMonth()==6 || GetEMonth()==9 || GetEMonth()==11) && (GetEDay() > 30))
      || (GetEMonth()==2 && GetEDay()>29 && IsLeapYear(GetEYear()))
      || (GetEMonth()==2 && GetEDay()>28 && !IsLeapYear(GetEYear())))
    {
      EDDColor='red'; 
      validFlag=0;
    };

  if ( Number(GetSYear()) > Number(GetEYear()) )
    {
      SYYYYColor='red';
      EYYYYColor='red';
      validFlag=0;
    }
  

  if ( validFlag == 1 &&   CSJulian >= CEJulian  )
    {
      if ( Number(GetSYear()) > Number(GetEYear()) )
	{
	  SYYYYColor='red';
	  EYYYYColor='red';
	}
      else if ( Number(GetSMonth()) > Number(GetEMonth()) )
	{
	  SMMColor='red';
	  EMMColor='red';
	}
      else if (Number(GetSDay()) > Number(GetEDay()) )
	{
	  SDDColor='red';
	  EDDColor='red';
	};
      validFlag=0;
    };

  if (validFlag== 1){
     if ( CSJulian < top.ephemStart || CSJulian > top.ephemStop) {
       SYYYYColor='yellow';
       SMMColor='yellow';
       SDDColor='yellow';
     }
     if ( CEJulian < top.ephemStart || CEJulian > top.ephemStop) {
       EYYYYColor='yellow';
       EMMColor='yellow';
       EDDColor='yellow';
     }
  }

  if ( validFlag == 1 && (tmpCoords == "No results found" || tmpCoords == "Multiple results" ))
    {
      coordsColor='red'; 
      validFlag=0;
    }

  if (validFlag == 1 && (isBlank(tmpCoords) && isBlank(tmpName) ))
    {
      validFlag = 0;
    }

  if ( document.getElementById('coorderror').value == "1") {
     coordsColor='red'; 
  }

  document.getElementById('SYYYY').style.backgroundColor=SYYYYColor;
  document.getElementById('SMM').style.backgroundColor=SMMColor;
  document.getElementById('SDD').style.backgroundColor=SDDColor;
  
  document.getElementById('EYYYY').style.backgroundColor=EYYYYColor;
  document.getElementById('EMM').style.backgroundColor=EMMColor;
  document.getElementById('EDD').style.backgroundColor=EDDColor;
  document.getElementById('targetcoords').style.backgroundColor=coordsColor;


	

  

  return validFlag;

  /*
  if ( (tmpCoords == "" && tmpName=="") || tmpCoords == "No results found" || tmpCoords == "Multiple results"
       || CSJulian >= CEJulian 
       || isNaN(GetSYear()) || isNaN(GetSMonth()) || isNaN(GetSDay()) 
       || isNaN(GetEYear()) || isNaN(GetEMonth()) || isNaN(GetEDay())
       || GetSYear()/2.0 == 0 || GetSMonth()/2.0 == 0|| GetSDay()/2.0 == 0
       || GetEYear()/2.0 == 0 || GetEMonth()/2.0 == 0|| GetEDay()/2.0 == 0
       || GetSYear() <=0 || GetEYear() <= 0
       || GetSMonth() > 12 || GetSMonth() <=0 
       || GetEMonth() > 12 || GetEMonth() <=0
       || ((GetSMonth()==1 || GetSMonth()==3 || GetSMonth()==5 || GetSMonth()==7 || GetSMonth()==8 || GetSMonth()==10 || GetSMonth()==12) && (GetSDay() > 31))
       || ((GetEMonth()==1 || GetEMonth()==3 || GetEMonth()==5 || GetEMonth()==7 || GetEMonth()==8 || GetEMonth()==10 || GetEMonth()==12) && (GetEDay() > 31))
       || ((GetSMonth()==4 || GetSMonth()==6 || GetSMonth()==9 || GetSMonth()==11) && (GetSDay() > 30))
       || ((GetEMonth()==4 || GetEMonth()==6 || GetEMonth()==9 || GetEMonth()==11) && (GetEDay() > 30))
       || (GetSMonth()==2 && GetSDay()>29 && IsLeapYear(GetSYear()))
       || (GetEMonth()==2 && GetSDay()>29 && IsLeapYear(GetEYear()))
       || (GetSMonth()==2 && GetSDay()>28 && !IsLeapYear(GetSYear()))
       || (GetEMonth()==2 && GetSDay()>28 && !IsLeapYear(GetEYear()))
       )
    {
      return 0;
    }
  
  
  
  return 1;
  */
  
}

function IsLeapYear( inYear)
{
  return (Math.round(inYear/4.0) == inYear/4.0)
}

function ClearTarget() 
{
  document.getElementById('targetcoords').value = "";
  document.getElementById('targetname').value = "";
  document.getElementById('coorderror').value = "0";
  
}


function DateChanged()
{
  if (  document.getElementById('SYYYY').value != top.SYYYY
	|| document.getElementById('SMM').value != top.SMM
	|| document.getElementById('SDD').value != top.SDD
	|| document.getElementById('EYYYY').value != top.EYYYY
	|| document.getElementById('EMM').value != top.EMM
	|| document.getElementById('EDD').value != top.EDD)
    {
      return 1;
    };

  return 0;

}

function ResetDates()
{
  document.getElementById('SYYYY').value=top.SYYYY;
  document.getElementById('SMM').value=top.SMM;
  document.getElementById('SDD').value=top.SDD;
  document.getElementById('EYYYY').value=top.EYYYY;
  document.getElementById('EMM').value=top.EMM;
  document.getElementById('EDD').value=top.EDD;
}

function ResetTarget()
{
  document.getElementById('targetcoords').value = top.targetcoords;
  document.getElementById('targetname').value = top.targetname;

}


function GetSDate()
{
  return document.getElementById('SYYYY').value+"/"+document.getElementById('SMM').value+"/"+document.getElementById('SDD').value;
}


function GetEDate()
{
  return document.getElementById('EYYYY').value+"/"+document.getElementById('EMM').value+"/"+document.getElementById('EDD').value;
}

function GetSYear()
{
  return document.getElementById('SYYYY').value;
}


function SetSYear(inYear)
{
  document.getElementById('SYYYY').value=inYear;
}

function GetSMonth()
{
  return document.getElementById('SMM').value;
}


function SetSMonth(inMonth)
{
  document.getElementById('SMM').value=inMonth;
}


function GetSDay()
{
  return document.getElementById('SDD').value;
}


function SetSDay(inDay)
{
  document.getElementById('SDD').value=inDay;
}



function GetEYear()
{
  return document.getElementById('EYYYY').value;
}


function SetEYear(inYear)
{
  document.getElementById('EYYYY').value=inYear;
}

function GetEMonth()
{
  return document.getElementById('EMM').value;
}


function SetEMonth(inMonth)
{
  document.getElementById('EMM').value=inMonth;
}


function GetEDay()
{
  return document.getElementById('EDD').value;
}


function SetEDay(inDay)
{
  document.getElementById('EDD').value=inDay;
}



function GetTargetName()
{

  return document.getElementById('targetname').value;
}

function GetTargetCoords()
{

  return document.getElementById('targetcoords').value;
}


function SetTargetName(inTargetName)
{
  document.getElementById('targetname').value = inTargetName;
}


function SetTargetCoords(inTargetCoords)
{
  document.getElementById('targetcoords').value=inTargetCoords;

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
    };
  if ( inDisableFlag )
    {
      document.body.style.cursor=top.busyCursor;
    }
  else
    {
      document.body.style.cursor=top.notbusyCursor;
    }
}	


function OnLoad(inEvent) {

  InitFrame();
  top.InputLoaded(1);  
  return true;
}

