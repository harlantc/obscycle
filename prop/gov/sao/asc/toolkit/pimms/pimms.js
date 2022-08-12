function setDefaultInputEnergyLevels( form )
{
  var choices = form.inputInstrument;
  var low = form.inputEnergyLow;
  var high = form.inputEnergyHigh;
  var missionSelector = form.inputMissionSelector;

  //setDefaultEnergyLevels( choices, low, high, missionSelector );
}

function setDefaultOutputEnergyLevels( form )
{
  var choices = form.outputInstrument;
  var low = form.outputEnergyLow;
  var high = form.outputEnergyHigh;
  var missionSelector = form.outputMissionSelector;

  //setDefaultEnergyLevels( choices, low, high, missionSelector );
}

function setDefaultEnergyLevels( choices, low, high, missionSelector )
{
  var index = choices.selectedIndex;
  var tag = choices.options[index].text;
  switch ( tag )
  {
    case "ACIS-I/None/None":       
    case "ACIS-S/None/None":     
    case "ACIS-S-BI/None/None":     
    case "ACIS-S/HETG/ORDER0":     
    case "ACIS-S/HETG/HEG1":       
    case "ACIS-S/HETG/MEG1":       
    case "ACIS-S/HETG/HEG1MEG1":     
    case "ACIS-S/LETG/ORDER0":     
    case "ACIS-S/LETG/LETG1":      
      low.value = "0.2";
      high.value = "10.0";
      break;

    case "ASM/None/None":       
      low.value = "2.0";
      high.value = "10.0";
      break;

    case "ACIS-I/HETG/ORDER0":
    case "ACIS-I/LETG/ORDER0":
    case "HRC-I/None/None":
    case "HRC-I/LETG/ORDER0":
    case "HRC-S/None/None":
    case "HRC-S/LETG/ORDER0":
    case "HRC-S/LETG/LETGHI":
    case "HRC-S/LETG/LETG1":     
    case "HRC-S-LESF/LETG/LETGHI":
    case "HRC-S-LESF/LETG/LETG1": 
      low.value = "0.08";
      high.value = "10.0";
      break;

    case "GIS/None/None":       
      low.value = "0.4";
      high.value = "11.9";
      break;

    case "HPGSPC/None/None":    
      low.value = "3.0";
      high.value = "120.0";
      break;

    case "HRC-S-HESF/LETG/LETG1": 
      low.value = "0.08";
      high.value = "0.4";
      break;

    case "LECS/None/None":
      low.value = "0.1";
      high.value = "10.0";
      break;

    case "MECS/None/None":      
      low.value = "1.0";
      high.value = "10.0";
      break;

    case "HEXTE/None/DEFAULT":  
    case "HEXTE/None/LLD10":    
    case "HEXTE/None/LLD15":    
    case "HEXTE/None/LLD20":    
    case "HEXTE/None/LLD25":    
    case "HEXTE/None/LLD30":    
      low.value = "15.0";
      high.value = "250.0";
      break;

    case "HRI/None/None":
      var index = missionSelector.selectedIndex;
      var mission = missionSelector.options[index].text;
      if ( mission.indexOf( "ROSAT" ) == 0 )
      {
        low.value = "0.12";
        high.value = "2.48";
      }
      else if ( mission.indexOf( "EINSTEIN" ) == 0 )
      {
        low.value = "0.2";
        high.value = "4.5";
      }
      else
      {
	low.value = "Error";
	high.value = "Error";
      }
      break;

    case "IPC/None/None":
      low.value = "0.2";
      high.value = "4.5";
      break;

    case "BAT/None/Single":
      low.value = "15.0";
      high.value = "150.0";
      break;

    case "XRT/None/PC":
    case "XRT/None/WT":
    case "XRT/None/PD":
      low.value = "0.2";
      high.value = "10.0";
      break;

    case "HXD/None/GSO":       
      low.value = "50.0";
      high.value = "200.0";
      break;
    case "HXD/None/PIN":       
      low.value = "15.0";
      high.value = "40.0";
      break;
    case "XIS/None/BI":       
    case "XIS/None/FI":       
      low.value = "0.3";
      high.value = "10.0";
      break;

    case "LAC/None/TOP":
    case "LAC/None/BOTH":
      low.value = "1.5";
      high.value = "30.0";
      break;

    case "RGS1/None/O1":     
    case "RGS2/None/O1":     
      low.value = "0.35";
      high.value = "2.5";
      break;

    case "RGS1/None/O2":     
    case "RGS2/None/O2":     
      low.value = "0.62";
      high.value = "2.5";
      break;

    case "RGS1/None/O3":    
    case "RGS2/None/O3":    
      low.value = "1.20";
      high.value = "2.5";
      break;

    case "ME/None/None":
      low.value = "0.45";
      high.value = "36.0";
      break;

    case "MOS/None/THIN":       
    case "MOS/None/MEDIUM":        
    case "MOS/None/THICK":      
    case "PN/None/THIN":        
    case "PN/None/MEDIUM":         
    case "PN/None/THICK":
      low.value = "0.2";
      high.value = "15.0";
      break;

    case "PCA/None/None":       
      low.value = "2.0";
      high.value = "60.0";
      break;

    case "PDS/None/None":       
      low.value = "15.0";
      high.value = "300.0";
      break;

    case "PSPC/None/OPEN":
      low.value = "0.12";
      high.value = "2.48";
      break;

    case "SIS/None/None":       
      low.value = "0.3";
      high.value = "11.9";
      break;

    case "WFC/None/None":       
      low.value = "2.0";
      high.value = "30.0";
      break;

    default:
      low.value = "error";
      high.value = "error";
      break;
  }
}


