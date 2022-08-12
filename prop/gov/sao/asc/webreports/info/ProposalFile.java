// Copyright (c) 2003, Smithsonian Astrophysical Observatory
// You may do anything you like with this file except remove this copyright.

//****************************************************************************
//                              CXC Data System
//****************************************************************************

//****************************************************************************
//                               WEBREPORTS
//                               INFO Package
//--------------------------------------------------------------------------
//                               ProposalFile
//****************************************************************************
package info;

/** 
 * This class contains the label and filename for each associated file
 * for a proposal
 */
public class ProposalFile {
    private String proposalNumber;
    private String fileType;
    private String fileName;

    public ProposalFile() {
	init();
    }

    
    private void init() {
	proposalNumber = new String("");
	fileType = new String("");
	fileName = new String("");
    }

    
    /**
     * Copy from an existing ProposalFile object
     *
     * @param inputPropFile   input ProposalFile object
     */
    public void copy(ProposalFile inputPropFile) {
	proposalNumber = inputPropFile.getProposalNumber();
	fileType = inputPropFile.getFileType();
	fileName = inputPropFile.getFileName();
    }

    /**
      * Set the proposal number .  
      * @param inputStr proposal number
     */
    public void setProposalNumber(String inputStr)
    {
        proposalNumber = inputStr.trim();
    }

    /**
      * Set the associated file type.  This is the name that will
      * be displayed in the gui. It was read from the header of the
      * proposal_files.list .
      * @param inputStr  file type (Proposal, Technical Review, etc)
     */
    public void setFileType(String inputStr)
    {
        fileType = inputStr.trim();
  
    }
   
    /**
     *  Set the full directory path filename for this associated file.
     *
     * @param inputStr  Full filename for the associated file
     */
    public void setFileName(String inputStr)
    {
        fileName = inputStr.trim();
    }

    /**
     * Return the proposal number
     *
     * @return String proposal number
     */
    public String getProposalNumber()
    {
        return proposalNumber;
    }
    /**
     * Return the file name for the associated file.  
    /**
     * Return the file type for the associated file.  
     *
     * @return String file type
     */
    public String getFileType()
    {
        return fileType;
    }
    /**
     * Return the file name for the associated file.  
     *
     * @return String  Full filename for the associated file
     */
    public String getFileName()
    {
        return fileName;
    }
}
