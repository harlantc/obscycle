package captcha;

// This code is also in /vobs/ASC_DB_WEB/src/db/www/webutils/jutil/
// So when we modernize and use the ant build we could just take the package
// switch to log4j, etc
// For now, I'm trying to minimize the number of dependencies
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;

import com.google.common.base.Strings;

// based on code from http://stackoverflow.com/a/34874105/274677 (with slight mods)
public class ClientIPAddress {

    private ClientIPAddress() {}

    // http://stackoverflow.com/a/11327345/131929
    private static Pattern PRIVATE_ADDRESS_PATTERN = Pattern.compile("(^127\\.)         |"+
                                                                     "(^192\\.168\\.)   |"+
                                                                     "(^10\\.)          |"+
                                                                     "(^172\\.1[6-9]\\.)|"+
                                                                     "(^172\\.2[0-9]\\.)|"+
                                                                     "(^172\\.3[0-1]\\.)|"+
                                                                     "(^::1$)           |"+
                                                                     "(^[fF][cCdD])",
                                                                     Pattern.CANON_EQ);
    /**
     * Extracts the "real" client IP address from the request. It analyzes request headers
     * {@code REMOTE_ADDR}, {@code X-Forwarded-For} as well as {@code Client-IP}. Optionally
     * private/local addresses can be filtered in which case a supplied placeholder value is returned.
     *
     * @param request HTTP request
     * @param filterPrivateAddresses if true then in the case of
     * private/local addresses (see https://en.wikipedia.org/wiki/Private_network#Private_IPv4_address_spaces 
     * and https://en.wikipedia.org/wiki/Unique_local_address) return a supplied value
     * @param returnValueForFilteredAddresses return value to use in case of filtered private addresses
     * @return IP address or supplied string to use in case filtering of private addresses is requested
     */
    public static String getFrom(HttpServletRequest request, boolean filterPrivateAddresses, String returnValueForFilteredAddresses) {
        Assert.assertTrue( (   filterPrivateAddresses  && (returnValueForFilteredAddresses!=null)) ||
                           ( (!filterPrivateAddresses) && (returnValueForFilteredAddresses==null))  );
        String rv                  = request.getRemoteAddr();
        String headerClientIp      = request.getHeader("Client-IP");
        String headerXForwardedFor = request.getHeader("X-Forwarded-For");
        if (Strings.isNullOrEmpty(rv) && (!Strings.isNullOrEmpty(headerClientIp))) {
            rv = headerClientIp;
        } else if (!Strings.isNullOrEmpty(headerXForwardedFor)) {
            rv = headerXForwardedFor;
        }
        if ( (filterPrivateAddresses) && isPrivateOrLocalAddress(rv)) {
            return returnValueForFilteredAddresses;
        } else {
            return rv;
        }
    }

    public static String getFrom(HttpServletRequest request, boolean filterPrivateAddresses) {
        if (filterPrivateAddresses)
            return getFrom(request, true, "");
        else
            return getFrom(request, false, null);
    }

    public static String getFrom(HttpServletRequest request) {
        return getFrom(request, false);
    }

    private static boolean isPrivateOrLocalAddress(String address) {
        Matcher regexMatcher = PRIVATE_ADDRESS_PATTERN.matcher(address);
        return regexMatcher.matches();
    }


}
