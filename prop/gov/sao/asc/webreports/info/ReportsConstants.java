package info;

//ReportsConstants class
//This class contains all the constants needed in the web reports
//application

public class ReportsConstants {
    public static String CURRENTUSERSSEP = "/"; //current users file field seperator
    public static String PHPSESSIONID = "PHPSESSID";
    public static String ACCESSDATE = "accessDate";
    public static String PRDATE = "peerReviewDate";
    public static String ADMINDATE = "adminDate";
    public static String CHAIRLPDATE = "chairLPDate";
    public static String PRWEEK = "peerReviewWeek";
    public static String PRELIMDATE = "prelimGradesDate";
    public static String GROUPSDATE = "propGroupsDate";
    public static String PRELIMDEADLINE = "prelimGradesDeadline";
    public static String GROUPSDEADLINE = "propGroupsDeadline";
    public static String ENDREVIEWER = "endReviewerAccess";
    public static String ENDCHAIR = "endChairAccess";
    public static String ENDPUNDIT = "endPunditAccess";
    public static String ENDFACILITATOR = "endFacilitatorAccess";

    public static String REVIEWER = "Reviewer";
    public static String CHAIR = "Chair";
    public static String DEPUTYCHAIR = "Deputy Chair";
    public static String ADMIN = "Admin";
    public static String ADMINEDIT = "AdminEdit";
    public static String FACILITATOR = "Facilitator";
    public static String PUNDIT = "Pundit";
    public static String PUNDITCHAIR = "Pundit Chair";
    public static String PUNDITDEPUTY = "Pundit Deputy";
    public static String NOTYPE = "No User Type";
    public static String DEVELOPER = "Developer";
    public static int DEVELOPERID = 0;
    public static int ADMINID = -1111;
    public static int FACILITATORID = -2222;
    public static int PUNDITID = -3333;

    public static String BPP = "Big Project Panel";
    //public static String BPPTYPE = "LP/XVP";
    //public static String BPPLBL = "Large Project/X-ray Visionary Proposals";
    public static String BPPTYPE = "LP";
    public static String BPPLBL = "Large Project Proposals";
    public static String BPP_PANEL = "bpp";


    public static String FINAL = "Final";
    public static String PRIMARY = "Primary";
    public static String SECONDARY = "Secondary";
    public static String SECONDARYPEER = "SecondaryPeer";
    public static String PEER = "Peer";
    public static String PRELIM = "Prelim";
    public static String PERSONALCONF = "PersonalConflict";
    public static String LP = "LP"; //for large project/very large projects

    public static String EXIT = "EXIT";

    public static String FINALIZE = "CDO";
    public static String CHECKOFF = "Panel";
    public static String COMPLETE = "Reviewer";
    public static String APPLABEL = "Completed By";
    public static String UNAPPLABEL = "Un-Complete";
    public static String APPBTNLABEL = "Complete";

    public static String CLEAR = "CLEAR";
    public static String SAVE = "SAVE";
    public static String DONE = "DONE";
    public static String RESET = "RESET";
    public static String UNCHECKOFF = "UNCHECKOFF";
    public static String UNFINALIZE = "UNFINALIZE";
    public static String UNSAVED = "UNSAVED";
    public static String UNCOMPLETE = "UNCOMPLETE";
    public static String UNLOCK = "UNLOCK";
    public static String NAMEUNLOCK = "NUNLOCK";
    public static String REASSIGN = "REASSIGN";
    public static String REASSIGNb = "REASSIGNb";
    public static String TIMEDOUTREP = "TIMEDOUT"; //Timed out report
    public static String LISTPROPOSALS = "PROPOSALLIST";
    public static String SAVENOTES = "SAVE NOTES";
    public static String SAVECMTEDITS = "ADD EDIT INPUT";

    public static String PRINTVERSION = "Printer-friendly version";
    public static String CSVVERSION = "Comma Separated List";
    public static String VIEWHELP = "View help";
    public static int VALIDENTRY = 0;
    public static int TIMEDOUT = 1;
    public static int INVALIDENTRY = 2;

    //drop-down menu choices in the report itself
    public static String NONE = "----";
    public static String NA = "N/A";
    public static String TOPS = "Tops";
    public static String EASY = "Easy";
    public static String GOOD = "Good";
    public static String AVERAGE = "Average";
    public static String ABOVE = "Above-average";
    public static String BELOW = "Below-average";

    public static String PRIMARYEXT = ".pri";
    public static String SECONDARYEXT = ".sec";
    public static String PEEREXT = ".peer";
    public static String OLDEXT = ".OLD";
    public static String LPEXT = ".LP";
    public static String LOCKEXT = ".lock";
    public static String TMPDIR = "tmp";
    public static String REASSIGNDIR = "reassign";
    public static String REASSIGNEXT = ".txt";
    public static String GRADEDIR = "grades";
    public static String GRADEEXT = ".pgrade";
    public static String PCONFEXT = ".pconf";
    public static String GROUPDIR = "groups";
    public static String GROUPEXT = ".groups";
    public static String CONFLICTDIR = "conflicts";
    public static String CONFLICTEXT = ".conflicts";

    public static String INFO = "Info:";
    public static String REVIEWFORM = "Review Form";
    public static String PROPNUMBER = "Proposal Number";
    public static String PANEL = "Panel";
    public static String CATEGORY = "Subject Category";
    public static String PINAME = "P.I. Name";
    public static String TITLE = "Proposal Title";
    public static String PROPTYPE = "Type";
    public static String MULTICYCLE = "MultiCycle";
    public static String CONSTTARGET = "Constrained targets";
    public static String JOINT = "Joint";

    public static String SCIENCEIMPORTANCE = "Importance of Science";
    public static String SCIENCEJUSTIFICATION = "Proposal Science Justification";
    public static String FMTCLARITY = "Clarity of formatting";
    public static String FEASIBILITY = "Feasibility";
    public static String FEASIBILITYCONSTRAINT = "Feasibility of Science if constraint preferences not met";
    public static String CAPABILITY = "Use of Chandra capability";
    public static String CLARITY = "Clarity of proposal";
    public static String HIGHERRANKED = "Good proposal, but all targets were allocated to higher-ranked proposals";
    public static String TOO = "Good proposal, but TOO observations are limited";
    public static String CONSTRAINED = "Good proposal, but constrained observations are limited";
    public static String COMMENTS = "Comments";
    public static String ENDCOMMENTS = "End of Comments";
    public static String RECOMMENDATIONS = "If accepted, enter specific recommendations concerning targets, time, observing conditions";
    public static String ENDRECS = "End of Recommendations";
    public static String REASON = "Specify reason why the grade was not higher";
    public static String ENDREASON = "End of Reason";
    public static String TECHRPT = "Technical Review";
    public static String ENDTECHRPT = "End of Technical Review";
    public static String EFFORT = "Degree of effort required to achieve analysis goals";
    public static String EFFORT2 = "(flag used to adjust funding if proposal is approved)";
    public static String PRELIMCOMPLETE = "Preliminary Report Completed";

    // Contact Conflict file column order
    public static Integer CONTACTFIRST = 0;
    public static Integer CONTACTLAST = 1;
    public static Integer CONTACTINSTITUTE = 2;
    public static Integer CONTACTPROPSAL = 3;
    public static boolean isLP(String reportType) {
	boolean isLP = false;
	if(reportType.equals(LP) ||
	   reportType.equals("VLP") ||
	   reportType.equals("XVP") ||
	   reportType.equals("ARCHIVE")) {
	    isLP = true;
	}
	return isLP;
    }
    
    /**
     * get the file extension for the specified type of report
     *
     * @param reportType   type of report (primary,secondary,peer,lp)
     * @return String extension for specified report type
     */
    public static String getExtension(String reportType) {
	if(reportType.equals(PRIMARY)) {
	    return PRIMARYEXT;
	} else if(reportType.equals(SECONDARY)) {
	    return SECONDARYEXT;
	} else if(reportType.equals(SECONDARYPEER)) {
	    //Seconday reviewers need to view the peer review report
	    //at peer review.  The report will be uneditable.
	    return PEEREXT;
	} else if(reportType.equals(PEER)) {
	    return PEEREXT;
	} else if(reportType.equals(PRELIM)) {
        return GRADEEXT;
    } else if (reportType.equals(PERSONALCONF)) {
        return PCONFEXT;
	} else if(reportType.equals(LP) ||
		  reportType.equals("VLP") ||
		  reportType.equals("ARCHIVE")) {
	    return LPEXT;
	} else {
	    //Throw an exception?
	    return "No such type";
	}
    }


}
