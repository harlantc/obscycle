/* 
  MockRequest:
     Class to mimic HttpServletRequest for testing.
*/

/* ************************************************************************** */
import javax.servlet.http.Part;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.UnsupportedOperationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Enumeration;
import java.util.Collections;


public class MockRequest implements HttpServletRequest
{
    private HttpSession session;
    private Map<String, Object> elems = new HashMap<>();

    public boolean authenticate( HttpServletResponse response ){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getAuthType(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getContextPath(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public Cookie[] getCookies(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public long getDateHeader( java.lang.String name ){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getHeader( java.lang.String name ){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.util.Enumeration getHeaderNames(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.util.Enumeration getHeaders( java.lang.String name ){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public int getIntHeader( java.lang.String name ){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getMethod(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public Part getPart( java.lang.String name ){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.util.Collection getParts( ){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getPathInfo(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getPathTranslated(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getQueryString(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getRemoteUser(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getRequestedSessionId(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getRequestURI(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.StringBuffer getRequestURL(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getServletPath(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public HttpSession getSession(){
	return getSession(true);
    }
    public HttpSession getSession(boolean create){
	if ((session == null) && (create)){
	    session = new MockSession();
	}
	return session;
    }
    public java.security.Principal getUserPrincipal(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public boolean isRequestedSessionIdFromCookie(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    @Deprecated
    public boolean isRequestedSessionIdFromUrl(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public boolean isRequestedSessionIdFromURL(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public boolean isRequestedSessionIdValid(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public boolean isUserInRole(java.lang.String role){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public void login(java.lang.String username, java.lang.String password){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public void logout(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    /* inherited ServletRequest interface methods */
    public AsyncContext getAsyncContext(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.Object getAttribute( java.lang.String name ){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.util.Enumeration getAttributeNames(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getCharacterEncoding(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public int getContentLength(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getContentType(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public DispatcherType getDispatcherType(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public ServletInputStream getInputStream(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getLocalAddr(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.util.Locale getLocale(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.util.Enumeration getLocales(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getLocalName(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public int getLocalPort(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getParameter( java.lang.String name ){
	String result = (String) elems.get( name );
	return(result);
    }
    public java.util.Map getParameterMap(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.util.Enumeration getParameterNames(){
	Enumeration<String> result = Collections.enumeration( elems.keySet() );
	return( result );
    }
    public java.lang.String[] getParameterValues(java.lang.String name){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getProtocol(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.io.BufferedReader getReader(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    @Deprecated
    public java.lang.String getRealPath(java.lang.String path){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getRemoteAddr(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getRemoteHost(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public int getRemotePort(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public RequestDispatcher getRequestDispatcher(java.lang.String path){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getScheme(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public java.lang.String getServerName(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public int getServerPort(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public ServletContext getServletContext(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public boolean isAsyncStarted(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public boolean isAsyncSupported(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public boolean isSecure(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public void removeAttribute(java.lang.String name){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public void setAttribute(java.lang.String name, java.lang.Object o ){
	elems.put( name, o );
    }
    public void setCharacterEncoding(java.lang.String env ){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public AsyncContext startAsync(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse ){
	throw new UnsupportedOperationException("not yet implemented.");
    }
}
