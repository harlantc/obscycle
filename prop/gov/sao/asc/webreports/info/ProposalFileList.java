// Copyright (c) 2003, Smithsonian Astrophysical Observatory
// You may do anything you like with this file except remove this copyright.

//****************************************************************************
//                              CXC Data System
//****************************************************************************

//****************************************************************************
//                               WEBREPORTS
//                               INFO Package
//--------------------------------------------------------------------------
//                             ProposalFileList
//****************************************************************************
package info;

import java.io.*;
import java.util.*;
import java.lang.*;

import ascds.LogMessage;


public class ProposalFileList {

   private static String filename;
   private static Vector<ProposalFile> fileList;
   private static long lastModified;


  public ProposalFileList(String inFilename)
  {
     filename = inFilename;
     fileList = new Vector<ProposalFile>();
     lastModified = 0;
     readLinkedFiles();
  }

  public static String getRPSParam(String propNum)
  {
     return getFile("RPS",propNum);
  }
  public static String getMergedFile(String propNum)
  {
     return getFile("Proposal",propNum);
  }
  public static String getScienceJustFile(String propNum)
  {
     return getFile("Science",propNum);
  }
  public static String getTechnicalFile(String propNum)
  {
     return getFile("Technical",propNum);
  }
  public static String getProposerInputFile(String propNum)
  {
     return getFile("Proposer",propNum);
  }

  /**
    * return the vector of all the associated files
    *
    * @param propNum proposal number
    * @return Vector  associated files
    */
  public static Vector<ProposalFile> getProposalFiles(String propNum) 
  {
    Vector<ProposalFile> pfFiles = new Vector<ProposalFile>();

    readLinkedFiles();

    if (fileList != null ) {
      Iterator ii = fileList.iterator();
      while ( ii.hasNext() ) {
        ProposalFile pf = (ProposalFile)ii.next();
        if (pf.getProposalNumber().equalsIgnoreCase(propNum) ) {
           pfFiles.add(pf);
        }
      }
    }

    return pfFiles;

  }



  public static String getFile(String type, String propNum)
  {
     String returnVal = new String("");
     boolean foundit = false;

     
     readLinkedFiles();

     if (fileList != null ) {
       Iterator ii = fileList.iterator();
       while ( ii.hasNext() && !foundit) {
         ProposalFile pf = (ProposalFile)ii.next();
         if (pf.getProposalNumber().equalsIgnoreCase(propNum) &&
             pf.getFileType().indexOf(type) >= 0 ) {
           foundit = true;
           returnVal = pf.getFileName();
         }
       }
     }
         
     return returnVal;
  }
         


  private static void readLinkedFiles()
  {
    String nextLine;

    File inFile = new File(filename);
    if (inFile.exists()) {
      if (inFile.lastModified() > lastModified) {

        try {
            FileReader fileR = new FileReader(filename);
            BufferedReader linkedFilesBF = new BufferedReader(fileR);
            String inputLine;
            String[] inputArray;
            String[] inputTypes = null;
            int numFiles = 0;
            if (fileList != null) {
              fileList.clear();
            }


            if( (inputLine = linkedFilesBF.readLine()) != null) {
                    inputTypes = inputLine.split("\\t+");
            }

            while( (inputLine = linkedFilesBF.readLine()) != null) {

              //Found matching line, get the path for the files
              //Format of file: 
              // proposal#\tconflict_file\tscience_justfile\trpsform_file\n
              inputArray = inputLine.split("\\t+");

              for (int ii=1;ii< inputArray.length;ii++)  {
                if (inputArray[ii].length() > 2) {
                  ProposalFile pf = new ProposalFile();
                  if (inputTypes != null && ii <= inputTypes.length) {
                    pf.setFileType(inputTypes[ii]);
                  }
                  else {
                    pf.setFileType("Unknown");
                  }
                  pf.setFileName(inputArray[ii]);
                  pf.setProposalNumber(inputArray[0]);
                  fileList.add(pf);
                }
              }
            }
            linkedFilesBF.close();
            fileR.close();
            lastModified = inFile.lastModified();
        }
        catch (Exception exc) {
          LogMessage.printException(exc);
        }
      }
    }
  }
}
