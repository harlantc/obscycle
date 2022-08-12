package info;

public class DDTInfo {
    public String  msgclass;
    public String  coordMessage;
    public boolean newDDTStatus ;
    public boolean newUrgency;
    public boolean newDataRights;
    public String  ccemail;
    public String  displayComments;

    public DDTInfo() {
       msgclass="error";
       coordMessage = "";
       newDDTStatus=false;
       newUrgency=false;
       newDataRights=false;
       ccemail="";
       displayComments="on";
   }
    
}
