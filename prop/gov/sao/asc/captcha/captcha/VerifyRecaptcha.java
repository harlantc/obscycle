package captcha;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Properties;
import javax.net.ssl.*;
import java.net.*;
import java.net.*;

import com.google.gson.stream.JsonReader;


/**
 * Provide method to verify recaptcha response 
 */

public class VerifyRecaptcha  {

    /**
     * Verify the response using the site key
     *
     * @param appProp  properties containing the site,proxy,key info to use for verification
     * @param gRecaptchaResponse  client google response
     * @param remoteIP            remote IP address (optional)
     * 
     * @return boolean            True if success response, else False
     */

    public static boolean verify(Properties appProp,String gRecaptchaResponse,String remoteIP) throws IOException {

	String url = "";
	String secret = "";
        String proxySite = appProp.getProperty("proxy.site");
        String proxyPortStr = appProp.getProperty("proxy.port");
        Integer proxyPort = new Integer(0);

        url = appProp.getProperty("google.site");
        if (url == null || url.equals(""))
	  url = "https://www.google.com/recaptcha/api/siteverify";

        try  {
           proxyPort = new Integer(proxyPortStr);
        } catch(Exception ex) {
           proxyPort = 0;
        }
       
        // now get the key for this application
        String fkey = appProp.getProperty("key.path") ;
        try {
          FileReader fileR = new FileReader(fkey);
          BufferedReader rFileBR = new BufferedReader(fileR);
          secret = rFileBR.readLine(); 
          rFileBR.close();
          fileR.close();
        }
        catch (Exception exc) {
	  exc.printStackTrace();
	  return false;
        }

	if (gRecaptchaResponse == null || "".equals(gRecaptchaResponse)) {
          return false;
	}
		
	try {
	  URL obj = new URL(url);
	  HttpsURLConnection conn = null;
          if ( proxyPort.intValue() <= 0) {
	    conn = (HttpsURLConnection) obj.openConnection();
          } else {
            //see https://docs.oracle.com/javase/8/docs/technotes/guides/net/proxies.html
            SocketAddress addr = new InetSocketAddress(proxySite, proxyPort.intValue());
            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
            conn = (HttpsURLConnection)obj.openConnection(proxy);
          }

          // add request header
          conn.setRequestMethod("POST");
          //conn.setRequestProperty("User-Agent", USER_AGENT);
          conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
          conn.setConnectTimeout(15 * 1000);
          conn.setReadTimeout(15 * 1000);

          String postParams = "secret=" + secret + "&response="
                    + gRecaptchaResponse + "&remoteIP=" + remoteIP;
          // Send post request
          conn.setDoOutput(true);
          DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
          wr.writeBytes(postParams);
          wr.flush();
          wr.close();

	  int responseCode = conn.getResponseCode();
          //System.out.println("\nSending 'POST' request to URL : " + url);
	  //System.out.println("Post parameters : " + postParams);
	  //System.out.println("Response Code : " + responseCode);

          BufferedReader in = new BufferedReader(new InputStreamReader(
		conn.getInputStream()));
          String inputLine;
          StringBuffer response = new StringBuffer();

          while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
          }
          in.close();

          // print result
          //System.out.println(remoteIP + " " + response.toString());

          JsonReader jsonReader = new JsonReader(new StringReader(response.toString()));
          Boolean retval = false;
          try {
            jsonReader.beginObject();
            while(jsonReader.hasNext()){
              String name = jsonReader.nextName();
              if (name.equals("success")) {
                retval  =  jsonReader.nextBoolean();
              } else {
                jsonReader.skipValue();
              }
            }
          } catch (IOException e) {
             e.printStackTrace();
             retval=false;
          }
          jsonReader.close();

          return retval.booleanValue();
        } catch(Exception e){
           e.printStackTrace();
           return false;
        }
   }
}


