/*
 * ParaServlet.java
 * Created on May 1, 2006, 7:03 PM
 */

package offstage.web;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.servlet.http.*;



/**
 * These newly defined methods to retrieve parameters from HttpServletRequest 
 * request and convert them to these Data Types: br/
 * String, Boolean, Integer, Double, Float, Long, Integer, Short, Byte, Character
 * @author Michael Wahl
 */
public abstract class MyServlet extends citibob.web.DbServlet {
    
    /**
     * Remove all attributes from session variable
     */
    public void removeAttributes( HttpSession sess ){
        LinkedList alist = new LinkedList();       
        Enumeration en = sess.getAttributeNames();
        while( en.hasMoreElements() ){
            String aname = (String)en.nextElement();
            alist.add(aname);
        }
        Iterator i = alist.listIterator();
        while ( i.hasNext() ){
            String aname = (String)i.next();
            System.out.println("Removing: " + aname);
            sess.removeAttribute(aname);
        }
    }
    
    public Boolean getBooleanParameter( HttpServletRequest request, String key ){
        Boolean value = null;
        try {
            String _value = request.getParameter(key);
            if ( _value != null && _value.compareTo("on") == 0 )
                value = new Boolean(true);
            else value = new Boolean(false);
        } catch (Throwable t){}
        return value;
    }
    
    public Integer getIntegerParameter( HttpServletRequest request, String key ){
        Integer value = null;
        try {
            String _value = request.getParameter(key);
            value = new Integer(_value);
        } catch (Throwable t){}
        return value;
    }
    
    public Double getDoubleParameter( HttpServletRequest request, String key ){
        Double value = null;
        try {
            String _value = request.getParameter(key);
            value = new Double(_value);
        } catch (Throwable t){}
        return value;
    }

    public Float getFloatParameter( HttpServletRequest request, String key ){
        Float value = null;
        try {
            String _value = request.getParameter(key);
            value = new Float(_value);
        } catch (Throwable t){}
        return value;
    }

    public Long getLongParameter( HttpServletRequest request, String key ){
        Long value = null;
        try {
            String _value = request.getParameter(key);
            value = new Long(_value);
        } catch (Throwable t){}
        return value;
    }
    
    public Short getShortParameter( HttpServletRequest request, String key ){
        Short value = null;
        try {
            String _value = request.getParameter(key);
            value = new Short(_value);
        } catch (Throwable t){}
        return value;
    }

    public Byte getByteParameter( HttpServletRequest request, String key ){
        Byte value = null;
        try {
            String _value = request.getParameter(key);
            value = new Byte(_value);
        } catch (Throwable t){}
        return value;
    }
    
    public Character getCharacterParameter( HttpServletRequest request, String key ){
        Character value = null;
        try {
            String _value = request.getParameter(key);
            char __value = _value.charAt(0);
            value = new Character(__value);
        } catch (Throwable t){}
        return value;
    }
}
