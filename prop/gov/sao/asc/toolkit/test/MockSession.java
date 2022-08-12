/**
  MockSession:
     Class to mimic HttpSession for testing.
*/

/* ************************************************************************** */
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Enumeration;


public class MockSession implements HttpSession
{
    /* Storage for session attributes */
    private Map<String, Object> attributes;

    public MockSession(){
	this.attributes = new HashMap<>();
    }

    // Interface methods

    public Object getAttribute( String name ){
        Object result = null;
        try {
            result = attributes.get(name);
        } catch( Exception ex ){
            result = null;
        }
        return result;
    }
    public Enumeration<String> getAttributeNames(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public long getCreationTime(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public String getId(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public long getLastAccessedTime(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public int getMaxInactiveInterval(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public ServletContext getServletContext(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    @Deprecated
    public HttpSessionContext getSessionContext(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    @Deprecated
    public Object getValue( String name ){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    @Deprecated
    public String[] getValueNames(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public void invalidate(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public boolean isNew(){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    @Deprecated
    public void putValue( String name, Object value ){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public void removeAttribute( String name ){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    @Deprecated
    public void removeValue( String name ){
	throw new UnsupportedOperationException("not yet implemented.");
    }
    public void setAttribute( String name, Object value ){
	attributes.put( name, value );
    }
    public void setMaxInactiveInterval(int interval){
    }
}

