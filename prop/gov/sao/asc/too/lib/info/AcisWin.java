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

public class AcisWin {
    private Integer targid;
    private Integer ordr;
    private String  chip;
    private Integer sample;
    private Integer startCol;
    private Integer colWidth ;
    private Integer startRow ;
    private Integer rowHeight;
    private Double  lowerThreshold;
    private Double  energyRange;

    /**
      * Constructor
      */
    public AcisWin() {
	init();
    }

    private void init() {
        targid = new Integer(0);
        ordr = new Integer(0);
        chip = new String("");
        sample = TriggerTooConstants.EMPTY_INT;
        startCol = TriggerTooConstants.EMPTY_INT;
        startRow = TriggerTooConstants.EMPTY_INT;
        colWidth = TriggerTooConstants.EMPTY_INT;
        rowHeight = TriggerTooConstants.EMPTY_INT;
        lowerThreshold = TriggerTooConstants.EMPTY_VALUE;
        energyRange = TriggerTooConstants.EMPTY_VALUE;

    }

    /**
      * copy  - copy the AcisWin record
      * @param inputObs  input AcisWin
      */
    public void copy(AcisWin inputObs) {
        targid = inputObs.getTargID();
        ordr = inputObs.getOrdr();
        chip = inputObs.getChip();
        sample = inputObs.getSample();
        startCol = inputObs.getStartCol();
        startRow = inputObs.getStartRow();
        colWidth = inputObs.getColWidth();
        rowHeight = inputObs.getRowHeight();
        lowerThreshold = inputObs.getLowerThreshold();
        energyRange = inputObs.getEnergyRange();
    }


    //  Set routines

    public void setTargID(Integer inputValue) {
	targid = inputValue;
    }
    public void setTargID(int inputValue) {
       targid = new Integer(inputValue);
    }
    public void setOrdr(Integer inputValue) {
	ordr = inputValue;
    }
    public void setOrdr(int inputValue) {
       ordr = new Integer(inputValue);
    }
    public void setChip(String inputValue) {
      if (inputValue != null) {
	chip = inputValue.trim();
      } else {
	chip = inputValue;
      }
    }
    public void setSample(Integer inputValue) {
	sample = inputValue;
    }
    public void setSample(int inputValue) {
	sample = new Integer(inputValue);
    }
    public void setStartCol(Integer inputValue) {
	startCol = inputValue;
    }
    public void setStartCol(int inputValue) {
	startCol = new Integer(inputValue);
    }
    public void setStartRow(Integer inputValue) {
	startRow = inputValue;
    }
    public void setStartRow(int inputValue) {
	startRow = new Integer(inputValue);
    }
    public void setColWidth(Integer inputValue) {
	colWidth = inputValue;
    }
    public void setColWidth(int inputValue) {
	colWidth = new Integer(inputValue);
    }
    public void setRowHeight(Integer inputValue) {
	rowHeight = inputValue;
    }
    public void setRowHeight(int inputValue) {
	rowHeight = new Integer(inputValue);
    }
    public void setLowerThreshold(Double inputValue) {
	lowerThreshold = inputValue;
    }
    public void setLowerThreshold(double inputValue) {
	lowerThreshold = new Double(inputValue);
    }
    public void setEnergyRange(Double inputValue) {
	energyRange = inputValue;
    }
    public void setEnergyRange(double inputValue) {
	energyRange = new Double(inputValue);
    }


    //  Get routines
    public Integer getTargID() {
	return targid;
    }
    public Integer getOrdr() {
	return ordr;
    }
    public String getChip() {
       return chip;
    }
    public Integer getSample() {
	return sample;
    }
    public Integer getStartRow() {
	return startRow;
    }
    public Integer getStartCol() {
	return startCol;
    }
    public Integer getColWidth() {
	return colWidth;
    }
    public Integer getRowHeight() {
	return rowHeight;
    }
    public Double getLowerThreshold() {
	return lowerThreshold;
    }
    public Double getEnergyRange() {
	return energyRange;
    }



  /**
   * write roll information in printer friendly format 
   * @param outputPW output buffer
   * @return boolean  true if no errors 
   */
  public boolean writeOutput(PrintWriter outputPW)
  {
    boolean retval=true;
    String[] strArray;

    try {
       NumberFormat nf = NumberFormat.getInstance();
       nf.setMaximumFractionDigits(6);
       nf.setMinimumFractionDigits(6);

       outputPW.print(StringUtils.leftPad(ordr.toString(),6));
       outputPW.print("  ");
       outputPW.print(StringUtils.leftPad(chip,6));
       outputPW.print("  ");
       outputPW.print(StringUtils.leftPad(sample.toString(),6));
       outputPW.print("  ");
       outputPW.print(StringUtils.leftPad(startCol.toString(),6));
       outputPW.print("  ");
       outputPW.print(StringUtils.leftPad(colWidth.toString(),6));
       outputPW.print("  ");
       outputPW.print(StringUtils.leftPad(startRow.toString(),6));
       outputPW.print("  ");
       outputPW.print(StringUtils.leftPad(rowHeight.toString(),6));
       outputPW.print("  ");
       outputPW.print(nf.format(lowerThreshold.doubleValue()));
       outputPW.print("  ");
       outputPW.print(nf.format(energyRange.doubleValue()));
       outputPW.println("");

    } 
    catch(Exception exc) {
       LogMessage.println("AcisWin: Caught exception in writeOutput function");
       LogMessage.println(exc.getMessage());
       retval  = false;
    }


    return retval;
  }

}
