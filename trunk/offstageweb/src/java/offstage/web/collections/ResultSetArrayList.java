/*
 * ResultSetArrayList.java
 * Created on March 17, 2006, 5:43 PM
 */
package offstage.web.collections;

import java.io.Serializable;
import java.util.*;
import java.sql.*;
/**
 * An ArrayList with a constructor that has a java.sql.ResultSet as its only parameter.
 * Each row in the ResultSet is represented by a HashMap and corresponds to a
 * single slot in the ArrayList.
 * This hashmap is suitable for ResultSets that contain multiple rows of data.
 * @author Michael Wahl
 */
public class ResultSetArrayList extends ArrayList implements RSCollection, Serializable {
    private String[] columnNames;
    private int[] columnTypes;
    private String[] columnTypeNames;
    private String[] tableNames;
    
    /** Creates a new instance of ResultSetArrayList */
    public ResultSetArrayList( ResultSet rs )
    throws SQLException {
        ResultSetMetaData rsMeta = rs.getMetaData();
        MetaDataMapper convert = new MetaDataMapper();
        columnNames = convert.getResultColumnNames( rsMeta );
        columnTypes = convert.getResultColumnTypes( rsMeta );
        columnTypeNames = convert.getResultColumnTypeNames( rsMeta );
        tableNames = convert.getResultTableNames( rsMeta );
        while (rs.next()) {  
            Map map = new HashMap();            
            for (int i = 0; i < columnNames.length; i++) {                
                map.put(columnNames[i], rs.getObject(i + 1));            
            }
            map = Collections.unmodifiableMap(map);
            this.add(map);        
        }
    }

    /**
     * MetaData for this structure
     */
    public String[] getColumnNames(){return columnNames;}
    /**
     * MetaData for this structure
     */
    public int[] getColumnTypes(){return columnTypes;}
    /**
     * MetaData for this structure
     */
    public String[] getColumnTypeNames(){return columnTypeNames;}
    /**
     * MetaData for this structure
     */
    public String[] getTableNames(){return tableNames;}
    
    /**
     * Search entire list for Maps that have the specified value given the 
     * specified key.
     * @param key what we are searching with each Map in the ArrayList
     * @param value what we want to find using the key
     * @return a list of all Maps in this array that have the specified value
     */
    public Map get( String key, Object value ){
        if ( key == null || value == null )
            throw new NullPointerException("KEY AND/OR VALUE PARAMETERS CANNOT BE NULL");
        
        // First check to see if key is found on maps...
        boolean correctkey = false;
        for ( int i = 0; i < columnNames.length; ++i ){
            if ( columnNames[i].compareTo(key) == 0 ){
                correctkey = true;
                break;
            }
        }
        // If key not found on map then throw illegal argument exception
        if( !correctkey ) throw new IllegalArgumentException("KEY NOT FOUND ON MAP");
        
        // Now search for the first map that has the key/value pair
        Iterator i = this.listIterator();
        while ( i.hasNext() ){
            Map row = (Map)i.next();
            Object _value = (Object)row.get(key);
            if ( value instanceof Boolean ) 
            {
                if ( ((Boolean)_value).equals((Boolean)value) )
                    return row;
            }
            else if ( value instanceof Byte )
            {
                if ( ((Byte)_value).compareTo((Byte)value) == 0 )
                    return row;
            }
            else if ( value instanceof Character )
            {
                if ( ((Character)_value).compareTo((Character)value)== 0 )
                    return row;
            } 
            else if ( value instanceof Double )
            {
                if ( ((Double)_value).compareTo((Double)value)== 0 )
                    return row;
            } 
            else if ( value instanceof Float )
            {
                if ( ((Float)_value).compareTo((Float)value)== 0 )
                    return row;
            } 
            else if ( value instanceof Integer )
            {
                if ( ((Integer)_value).compareTo((Integer)value)== 0 )
                    return row;
            } 
            else if ( value instanceof Long )
            {
                if ( ((Long)_value).compareTo((Long)value)== 0 )
                    return row;
            } 
            else if ( value instanceof Short )
            {
                if ( ((Short)_value).compareTo((Short)value)== 0 )
                    return row;
            } 
            else if ( value instanceof String )
            {
                if ( ((String)_value).compareTo((String)value)== 0 )
                    return row;
            } else throw new IllegalArgumentException("Value must be Boolean" +
                        "Byte, Character, Double, Float, Integer, Long, Short or String");
        }
        return null;
    }
/*
    public String toString(){
        StringTokenizer tokenizer = new StringTokenizer( super.toString(), "}" );
        StringBuffer buf = new StringBuffer();
        while ( tokenizer.hasMoreTokens() ){
            buf.append( "\n" + tokenizer.nextToken() + "}" );
        }
        return buf.toString();
    }
 */

}