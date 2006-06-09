/*
 * HTMLValue.java
 * Created on April 27, 2006, 7:56 PM
 */

package offstage.web.collections;

import java.util.Map;
/**
 * If you are using 
 * @author Michael Wahl
 */
public class HTMLValue {
    
    /** Creates a new instance of HTMLValue */
    public HTMLValue() {}
    
    /**
     * IF Map, m, is null or the Object gotten from Map, m, using String, key, is null,
     * THEN return an empty String( i.e. value == "" )
     */
    public static String toValue( Map m, String  key ){
        // Set default return value to ""
        String value = "";
        
        // Try to remove an object from the map given the key
        // Try to get a String value from that object
        // If either of these throw an exception, then return default value
        try {
            Object o = m.get(key);
            value = o.toString();
        } catch (Throwable t){}
        return value;
    }
    
    
    /**
     * Replace a null Object, o, with an empty String ( i.e. value == "" )
     */
    public static String toValue( Object o ){
        // Set default value to ""
        String value = "";
        try {
            value = o.toString();
        } catch( Throwable t ){}
        return value;
    }
}