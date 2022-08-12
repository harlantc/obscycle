package edu.harvard.asc.cps.xo;

public class GUIMessage{

    public String type;
    private String page;
    private String targnum;
    private String targid;
    private String msg;


    public GUIMessage(String itype,String istr) {
       type=itype;
       page="";
       targnum="";
       targid="";
       msg=istr;
    }
    
    public GUIMessage(String itype,String guiPage,String istr) {
       type=itype;
       page=guiPage;
       targnum="";
       targid="";
       msg=istr;
    }

    public GUIMessage(String itype,String guiPage,int iTargnum, int iTargid,String istr) {
       type=itype;
       page=guiPage;
       msg=istr;
       try {
         Integer ival = Integer.valueOf(iTargnum);
         targnum = ival.toString();
         ival = Integer.valueOf(iTargid);
         targid = ival.toString();
       } catch (Exception exc) { 
         targnum="";
         targid="";
       }
    }
    public boolean isError() {
      boolean retval = false;
      if (type.equals(CPSConstants.ERROR_TYPE))
          retval = true;
      return retval;
    }
    public boolean isWarn() {
      boolean retval = false;
      if (type.equals(CPSConstants.WARN_TYPE))
          retval = true;
      return retval;
    }
    public boolean isNote() {
      boolean retval = false;
      if (type.equals(CPSConstants.NOTE_TYPE))
          retval = true;
      return retval;
    }
    public boolean isSuccess() {
      boolean retval = false;
      if (type.equals(CPSConstants.SUCCESS_TYPE))
          retval = true;
      return retval;
    }
    public String getGrid(){
       String str = "data:[\"";
       String tclass = "valnote";
       if (isError()) tclass ="valerr";
       else if (isWarn()) tclass ="valwarn";
       else if (isNote()) tclass ="valnote";
       else if (isSuccess()) tclass ="valsuccess";
       str += "<span class='" + tclass + "'>" + type + "</span>\",";
       str += "\"" + page + "\",";
       str += "\"" + targnum + "\",";
       str += "\"" + targid + "\",";
       str += "\"" + msg + "\"]";
       return str;
    }

        
      
    public String getMsg(boolean isHtml)
    {
      String str = "";
      String tclass = "";
      String pclass= "class ='valpage'";
      if (type.equals(CPSConstants.ERROR_TYPE))
        tclass = "class='valerr' ";
      else if (type.equals(CPSConstants.WARN_TYPE))
        tclass = "class='valwarn' ";

      if (page != null && !page.equals(""))  {
        if (!isHtml ) 
          str += page + ":";
         else 
          str += "<span " + pclass + ">" +  page + "</span>:";
      }
      if (targnum != null && !targnum.equals("")) 
        if (!isHtml ) 
          str += "Target #" + targnum + ":";
        else
          str += "<span " + tclass + ">Target #" + targnum + "</span>:";
      if (msg != null)
        str += msg;
      
      return str;
    }
}
