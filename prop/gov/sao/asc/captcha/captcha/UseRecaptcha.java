package captcha;
import java.util.Properties;

/**
 * Simple class to retrieve use.captcha from properties
 */
public class UseRecaptcha  {


    /**
      * Verify if Recaptcha should be used
      *
      * @param  appProp  application properties for use.captcha
      * @return boolean  true if recaptcha should be used
     */
    public static boolean doRecaptcha(Properties appProp) {

        boolean retval = true;
        try  {
	  String str = appProp.getProperty("use.captcha");
          if (str != null && str.indexOf("false") >= 0)
             retval = false;
        }
        catch (Exception exc) {
        }

        return retval;
    }
}


