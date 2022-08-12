package info;
/*
  Copyrights:
 
  Copyright (c) 2000 Smithsonian Astrophysical Observatory
 
  Permission to use, copy, modify, distribute, and  sell  this
  software  and  its  documentation  for any purpose is hereby
  granted without  fee,  provided  that  the  above  copyright
  notice  appear  in  all  copies and that both that copyright
  notice and this permission notice appear in supporting docu-
  mentation,  and  that  the  name  of the  Smithsonian Astro-
  physical Observatory not be used in advertising or publicity
  pertaining to distribution of the software without specific,
  written  prior  permission.   The Smithsonian  Astrophysical
  Observatory makes no representations about  the  suitability
  of  this  software for any purpose.  It is provided  "as is"
  without express or implied warranty.
  THE  SMITHSONIAN  INSTITUTION  AND  THE  SMITHSONIAN  ASTRO-
  PHYSICAL OBSERVATORY DISCLAIM ALL WARRANTIES  WITH REGARD TO
  THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANT-
  ABILITY AND FITNESS,  IN  NO  EVENT  SHALL  THE  SMITHSONIAN
  INSTITUTION AND/OR THE SMITHSONIAN ASTROPHYSICAL OBSERVATORY
  BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES
  OR ANY DAMAGES  WHATSOEVER  RESULTING FROM LOSS OF USE, DATA
  OR PROFITS,  WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
  OTHER TORTIOUS ACTION,  ARISING OUT OF OR IN CONNECTION WITH
  THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/


import java.lang.String;
import java.lang.Integer;
import java.lang.Double;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.text.NumberFormat;
import ascds.LogMessage;
import org.apache.commons.lang3.*;


/**
  * This class contains all the information related to an observation.
  * needed for triggering/managing a TOO
  */

public class AcisParam {
    private Integer targid;
    private String  exp_mode;
    private String  bep_pack;
    private String  ccdi0 ;
    private String  ccdi1 ;
    private String  ccdi2 ;
    private String  ccdi3 ;
    private String  ccds0 ;
    private String  ccds1 ;
    private String  ccds2 ;
    private String  ccds3 ;
    private String  ccds4 ;
    private String  ccds5 ;
    private String  multiple_spectral_lines ;
    private Double  spectra_max_count ;

    private String  most_efficient ;
    private Double  frame_time;

    private String  subarray ;
    private Integer subarray_start;
    private Integer subarray_no;

    private String  alt_exp ;
    private Integer secondary_exp;
    private Double  primary_exptime;

    private String  energyfilt ;
    private Double  energyfilt_lower;
    private Double  energyfilt_range;

    private String  spwindow;
    private String  spadditional;

    

    /**
      * Constructor
      */
    public AcisParam() {
	init();
    }

    private void init() {
        targid = new Integer(0);
        exp_mode = new String("");
        bep_pack = new String("");
        multiple_spectral_lines = new String("");
        ccdi0 = new String("");
        ccdi1 = new String("");
        ccdi2 = new String("");
        ccdi3 = new String("");
        ccds0 = new String("");
        ccds1 = new String("");
        ccds2 = new String("");
        ccds3 = new String("");
        ccds4 = new String("");
        ccds5 = new String("");
        subarray = new String("");
        alt_exp = new String("");
        energyfilt = new String("");
        spwindow = new String("");
        spadditional = new String("");

        spectra_max_count = TriggerTooConstants.EMPTY_VALUE;
        frame_time = TriggerTooConstants.EMPTY_VALUE;
        subarray_start = TriggerTooConstants.EMPTY_INT;
        subarray_no = TriggerTooConstants.EMPTY_INT;
        energyfilt_lower = TriggerTooConstants.EMPTY_VALUE;
        energyfilt_range = TriggerTooConstants.EMPTY_VALUE;
        secondary_exp = TriggerTooConstants.EMPTY_INT;
        primary_exptime = TriggerTooConstants.EMPTY_VALUE;

    }

    /**
      * copy  - copy the AcisParam record
      * @param inputObs  input AcisParam
      */
    public void copy(AcisParam inputObs) {
        targid = inputObs.getTargID();
        exp_mode = inputObs.getExpMode();
        bep_pack = inputObs.getBEPPack();
        multiple_spectral_lines = inputObs.getMultipleSpectralLines();
        ccdi0 = inputObs.getCCDI0();
        ccdi1 = inputObs.getCCDI1();
        ccdi2 = inputObs.getCCDI2();
        ccdi3 = inputObs.getCCDI3();
        ccds0 = inputObs.getCCDS0();
        ccds1 = inputObs.getCCDS1();
        ccds2 = inputObs.getCCDS2();
        ccds3 = inputObs.getCCDS3();
        ccds4 = inputObs.getCCDS4();
        ccds5 = inputObs.getCCDS5();
        subarray = inputObs.getSubarray();
        alt_exp = inputObs.getAltExp();
        energyfilt = inputObs.getEnergyFilter();
        spwindow = inputObs.getSPWindow();
        spadditional = inputObs.getSPAdditional();

        spectra_max_count = inputObs.getSpectraMaxCount();
        frame_time = inputObs.getFrameTime();
        subarray_start = inputObs.getSubarrayStart();
        subarray_no = inputObs.getSubarrayNo();
        energyfilt_lower = inputObs.getEnergyFilterLower();
        energyfilt_range = inputObs.getEnergyFilterRange();
        secondary_exp = inputObs.getSecondaryExp();
        primary_exptime = inputObs.getPrimaryExpTime();
     
    
    }


    // Set routines
    public void setTargID(Integer inputValue) {
	targid = inputValue;
    }
    public void setTargID(int inputValue) {
       targid = new Integer(inputValue);
    }
    public void setExpMode(String inputValue) {
      if (inputValue != null) {
	exp_mode = inputValue.trim();
      } else {
	exp_mode = inputValue;
      }
    }
    public void setBEPPack(String inputValue) {
      if (inputValue != null) {
	bep_pack = inputValue.trim();
      } else {
	bep_pack = inputValue;
      }
    }
    public void setMostEfficient(String inputValue) {
      if (inputValue != null) {
	most_efficient = inputValue.trim();
      } else {
	most_efficient = inputValue;
      }
    }

    public void setMultipleSpectralLines(String inputValue) {
      if (inputValue != null) {
	multiple_spectral_lines = inputValue.trim();
      } else {
	multiple_spectral_lines = inputValue;
      }
    }
    public void setCCDI0(String inputValue) {
      if (inputValue != null) {
	ccdi0 = inputValue.trim();
      } else {
	ccdi0 = inputValue;
      }
    }
    public void setCCDI1(String inputValue) {
      if (inputValue != null) {
	ccdi1 = inputValue.trim();
      } else {
	ccdi1 = inputValue;
      }
    }
    public void setCCDI2(String inputValue) {
      if (inputValue != null) {
	ccdi2 = inputValue.trim();
      } else {
	ccdi2 = inputValue;
      }
    }
    public void setCCDI3(String inputValue) {
      if (inputValue != null) {
	ccdi3 = inputValue.trim();
      } else {
	ccdi3 = inputValue;
      }
    }
    public void setCCDS0(String inputValue) {
      if (inputValue != null) {
	ccds0 = inputValue.trim();
      } else {
	ccds0 = inputValue;
      }
    }
    public void setCCDS1(String inputValue) {
      if (inputValue != null) {
	ccds1 = inputValue.trim();
      } else {
	ccds1 = inputValue;
      }
    }
    public void setCCDS2(String inputValue) {
      if (inputValue != null) {
	ccds2 = inputValue.trim();
      } else {
	ccds2 = inputValue;
      }
    }
    public void setCCDS3(String inputValue) {
      if (inputValue != null) {
	ccds3 = inputValue.trim();
      } else {
	ccds3 = inputValue;
      }
    }
    public void setCCDS4(String inputValue) {
      if (inputValue != null) {
	ccds4 = inputValue.trim();
      } else {
	ccds4 = inputValue;
      }
    }
    public void setCCDS5(String inputValue) {
      if (inputValue != null) {
	ccds5 = inputValue.trim();
      } else {
	ccds5 = inputValue;
      }
    }
    public void setSubarray(String inputValue) {
      if (inputValue != null) {
	subarray = inputValue.trim();
      } else {
	subarray = inputValue;
      }
    }
    public void setAltExp(String inputValue) {
      if (inputValue != null) {
	alt_exp = inputValue.trim();
      } else {
	alt_exp = inputValue;
      }
    }
    public void setEnergyFilter(String inputValue) {
      if (inputValue != null) {
	energyfilt = inputValue.trim();
      } else {
	energyfilt = inputValue;
      }
    }
    public void setSPWindow(String inputValue) {
      if (inputValue != null) {
	spwindow = inputValue.trim();
      } else {
	spwindow = inputValue;
      }
    }
    public void setSPAdditional(String inputValue) {
      if (inputValue != null) {
	spadditional = inputValue.trim();
      } else {
	spadditional = inputValue;
      }
    }


    public void setSpectraMaxCount(Double inputValue) {
	spectra_max_count = inputValue;
    }
    public void setSpectraMaxCount(double inputValue) {
	spectra_max_count = new Double(inputValue);
    }
    public void setFrameTime(Double inputValue) {
	frame_time = inputValue;
    }
    public void setFrameTime(double inputValue) {
	frame_time = new Double(inputValue);
    }
    public void setSubarrayStart(Integer inputValue) {
	subarray_start = inputValue;
    }
    public void setSubarrayStart(int inputValue) {
	subarray_start = new Integer(inputValue);
    }
    public void setSubarrayNo(Integer inputValue) {
	subarray_no = inputValue;
    }
    public void setSubarrayNo(int inputValue) {
	subarray_no = new Integer(inputValue);
    }
    public void setEnergyFilterLower(Double inputValue) {
	energyfilt_lower = inputValue;
    }
    public void setEnergyFilterLower(double inputValue) {
	energyfilt_lower = new Double(inputValue);
    }
    public void setEnergyFilterRange(Double inputValue) {
	energyfilt_range = inputValue;
    }
    public void setEnergyFilterRange(double inputValue) {
	energyfilt_range = new Double(inputValue);
    }
    public void setSecondaryExp(Integer inputValue) {
	secondary_exp = inputValue;
    }
    public void setSecondaryExp(int inputValue) {
	secondary_exp = new Integer(inputValue);
    }
    public void setPrimaryExpTime(Double inputValue) {
	primary_exptime = inputValue;
    }
    public void setPrimaryExpTime(double inputValue) {
	primary_exptime = new Double(inputValue);
    }


    //  Get routines
    public Integer getTargID() {
	return targid;
    }
    public String getExpMode() {
       return exp_mode;
    }
    public String getBEPPack() {
       return bep_pack;
    }
    public String getMostEfficient() {
       return most_efficient;
    }
    public String getMultipleSpectralLines() {
       return multiple_spectral_lines;
    }
    public String getCCDI0() {
       return ccdi0;
    }
    public String getCCDI1() {
       return ccdi1;
    }
    public String getCCDI2() {
       return ccdi2;
    }
    public String getCCDI3() {
       return ccdi3;
    }
    public String getCCDS0() {
       return ccds0;
    }
    public String getCCDS1() {
       return ccds1;
    }
    public String getCCDS2() {
       return ccds2;
    }
    public String getCCDS3() {
       return ccds3;
    }
    public String getCCDS4() {
       return ccds4;
    }
    public String getCCDS5() {
       return ccds5;
    }
    public String getSubarray() {
       return subarray;
    }
    public String getAltExp() {
       return alt_exp;
    }
    public String getEnergyFilter() {
       return energyfilt;
    }
    public String getSPWindow() {
       return spwindow;
    }
    public String getSPAdditional() {
       return spadditional;
    }
    public Integer getSubarrayStart() {
	return subarray_start;
    }
    public Integer getSubarrayNo() {
	return subarray_no;
    }
    public Integer getSecondaryExp() {
	return secondary_exp;
    }

    public Double getSpectraMaxCount() {
	return spectra_max_count;
    }
    public Double getFrameTime() {
	return frame_time;
    }
    public Double getEnergyFilterLower() {
	return energyfilt_lower;
    }
    public Double getEnergyFilterRange() {
	return energyfilt_range;
    }
    public Double getPrimaryExpTime() {
	return primary_exptime;
    }


}
