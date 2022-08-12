// _INSERT_SAO_COPYRIGHT_HERE_(2008)_
// _INSERT_GPL_LICENSE_HERE_

#include <iostream>
#include "DVObsWinCalc.hh"
#include "scconv.h"
#include <unistd.h>

using namespace std;

int main(int argc, char **argv)
{
  int		i, rollFlag=1,visFlag=1,pitchFlag=1,dateFlag=1,badpitchFlag=1,maxexpFlag=1;
  int           niceFormatFlag=0;
  int           niceFormatExtraDataFlag=0;
  char		*ephemerisFileName=0;
  char          *outputFileName=0;
  char          *coords=0;
  double	juliandatebase=-1, decdegrees, radegrees;
  double	startjulian=-1, endjulian=-1, rra, rdec;
  char*        calibrationFileName=0;
  int          maxexposureTime=30;
  ostream* outputStream;


  for(i=1; i<argc; i++)
    {
      if(!strcmp(argv[i], "-ef"))
	ephemerisFileName = argv[++i];
      else if(!strcmp(argv[i], "-jb"))
	juliandatebase = atof(argv[++i]);
      else if(!strcmp(argv[i], "-c"))
	coords= argv[++i];
      else if(!strcmp(argv[i], "-sjd"))
	startjulian = atoi(argv[++i]);
      else if(!strcmp(argv[i], "-ejd"))
	endjulian = atoi(argv[++i]);
      else if(!strcmp(argv[i], "-of"))
	outputFileName = argv[++i];
      else if(!strcmp(argv[i], "-rf"))
	rollFlag= atoi(argv[++i]);
      else if(!strcmp(argv[i], "-vf"))
	visFlag= atoi(argv[++i]);
      else if(!strcmp(argv[i], "-pf"))
	pitchFlag= atoi(argv[++i]);
      else if(!strcmp(argv[i], "-df"))
	dateFlag= atoi(argv[++i]);
      else if (!strcmp(argv[i], "-cf"))
	calibrationFileName = argv[++i];
      else if(!strcmp(argv[i], "-bf"))
	badpitchFlag= atoi(argv[++i]);
      else if(!strcmp(argv[i], "-mf"))
	maxexpFlag= atoi(argv[++i]);
      else if(!strcmp(argv[i], "-nf"))
	niceFormatFlag= atoi(argv[++i]);
      else if(!strcmp(argv[i], "-ed"))
	niceFormatExtraDataFlag= atoi(argv[++i]);
      else if(!strcmp(argv[i], "-mt"))
	maxexposureTime= atoi(argv[++i]);
      else if (!strcmp(argv[i], "-help"))
	{
	  cout<<argv[0]<<" -c coords -ef ephemerisfile -jb julianbase -sjd startjulian -ejd startjulian [options]"<<endl<<endl;
	  cout<<"   options     -cf filename   : calibrationfile"<<endl;
	  cout<<"               -of filename   : name of output file"<<endl;
	  cout<<"               -rf val        : roll flag"<<endl;
	  cout<<"               -vf val        : vis flag"<<endl;
	  cout<<"               -pf val        : pitch flag"<<endl;
	  cout<<"               -df val        : date flag"<<endl;
	  cout<<"               -bf val        : bad pitch flag"<<endl;
	  cout<<"               -mf val        : max exposure flag"<<endl;
	  cout<<"               -nf val        : nice format flag"<<endl;
	  cout<<"               -ed val        : extra data in nice format flag"<<endl;
	  cout<<"               -mt val        : max exposure time"<<endl;
	  cout<<"               -help          : display this help"<<endl<<endl;
	}
    }
  
  
  if  ( startjulian == -1 || endjulian == -1 
	|| ephemerisFileName == 0 || juliandatebase == -1
	|| coords == 0) 
    {
      cout <<"START ERROR"<<endl;
      cout <<"Invalid Parameters"<<endl;
      cout <<"END ERROR"<<endl;
      return 1;
    }
  
  
  if ( ParseCoords(coords,&radegrees,&decdegrees) != 1 )
    {
      cout <<"START ERROR"<<endl;
      cout <<"Invalid Target Coordinates"<<endl;
      cout <<"END ERROR"<<endl;
      return 2;
    };
  rra = radegrees * (M_PI / 180);
  rdec = decdegrees * (M_PI / 180);
  
  DVObsWinCalc	peph;

  
  if ( access(ephemerisFileName, R_OK) == -1 )
    return 3;
  
  if (calibrationFileName == 0 )
    {
      
      calibrationFileName=getenv("LIBASTRO_CAL_FILE");
    }
      
  if ( access(calibrationFileName, R_OK) == -1 )
    {   
      cout <<"START ERROR"<<endl;
      cout <<"Can not access calibration file"<<endl;
      cout <<"END ERROR"<<endl;
      return 5;
    }

  if (outputFileName != 0 )
    {
      outputStream = new ofstream(outputFileName);
    }
  else
    {
      outputStream= &cout;
    }

  if ( ! outputStream)
    {    
      cout <<"START ERROR"<<endl;
      cout <<"Failed to open output stream"<<endl;
      cout <<"END ERROR"<<endl;
      return 6;
    };
  
  if ( peph.DoCalc(ephemerisFileName,calibrationFileName,rra, rdec, startjulian, endjulian,maxexposureTime) == 0 )
    {
      return 7;
    };

  if ( niceFormatFlag )
    {
      if( peph.PrintData(*outputStream,juliandatebase,rollFlag,visFlag,pitchFlag,dateFlag,badpitchFlag,maxexpFlag,niceFormatExtraDataFlag) == 0)
	{   
	  cout <<"START ERROR"<<endl;
	  cout <<"Failed to generate data"<<endl;
	  cout <<"END ERROR"<<endl;
	  return 8;
	}
    }
  else
    {
      if( peph.PrintPlotData(*outputStream,juliandatebase,rollFlag,visFlag,pitchFlag,badpitchFlag,maxexpFlag) == 0)
	{  
	  cout <<"START ERROR"<<endl;
	  cout <<"Failed to generate plot data"<<endl;
	  cout <<"END ERROR"<<endl;
	  return 9;
	}
    }
  
  return 0;

}



