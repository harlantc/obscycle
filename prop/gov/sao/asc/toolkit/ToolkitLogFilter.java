/************************************************************************/
/*     Copyrights:                                                      */
/*                                                                      */
/*     Copyright (c) 2019 Smithsonian Astrophysical Observatory */
/*                                                                      */
/*    Permission to use, copy, modify, distribute,  and  sell  this     */
/*    software  and  its  documentation  for  any purpose is hereby     */
/*    granted  without  fee,  provided  that  the  above  copyright     */
/*    notice  appear  in  all  copies  and that both that copyright     */
/*    notice and this permission notice appear in supporting  docu-     */
/*    mentation,  and  that  the  name  of  the  Smithsonian Astro-     */
/*    physical Observatory not be used in advertising or  publicity     */
/*    pertaining  to distribution of the software without specific,     */
/*    written  prior  permission.   The  Smithsonian  Astrophysical     */
/*    Observatory  makes  no  representations about the suitability     */
/*    of this software for any purpose.  It  is  provided  "as  is"     */
/*    without express or implied warranty.                              */
/*    THE  SMITHSONIAN  ASTROPHYSICAL  OBSERVATORY  DISCLAIMS   ALL     */
/*    WARRANTIES  WITH  REGARD  TO  THIS  SOFTWARE,  INCLUDING  ALL     */
/*    IMPLIED  WARRANTIES  OF  MERCHANTABILITY  AND FITNESS, IN  NO     */
/*    EVENT  SHALL  THE  SMITHSONIAN  ASTROPHYSICAL  OBSERVATORY BE     */
/*    LIABLE FOR ANY SPECIAL,  INDIRECT  OR  CONSEQUENTIAL  DAMAGES     */
/*    OR  ANY  DAMAGES  WHATSOEVER RESULTING FROM LOSS OF USE, DATA     */
/*    OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,  NEGLIGENCE  OR     */
/*    OTHER  TORTIOUS  ACTION, ARISING OUT OF OR IN CONNECTION WITH     */
/*    THE USE OR PERFORMANCE OF THIS SOFTWARE.                          */
/*                                                                      */
/************************************************************************/

import java.io.IOException;
import java.util.Date;
import java.net.URL;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.MDC;

public class ToolkitLogFilter implements Filter {

  @Override
  public void init(FilterConfig config) throws ServletException {
      //
  }


  @Override
  public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException
  {

    try {
      /*
       * This code puts the value "x-forwarded-for" to the Mapped Diagnostic
       * context. Since MDC is a static class, we can directly access it
       * with out creating a new object from it. 
       */
       HttpServletRequest request = (HttpServletRequest) req;
       HttpServletResponse response = (HttpServletResponse) res;
       String hdr = request.getHeader("x-forwarded-for");
       hdr += ":" + request.getHeader("user-agent");
       MDC.put("x-forwarded-for",hdr);

       chain.doFilter(request, response);

    } finally {
       MDC.remove("x-forwarded-for");
    }

  }

  @Override
  public void destroy() {
        //
  }

}
