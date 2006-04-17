/*
 * ResultSetHashMap.java
 * Created on March 18, 2006, 4:03 PM
 */

package offstage.web.collections;

import java.sql.*;
import java.util.Collections;

/**
 * A HashMap with a constructor that has a java.sql.ResultSet as its only parameter.
 * This hashmap is suitable for ResultSets that contain one row of data.
 * @author Michael Wahl
 */
public class ResultSetHashMap extends java.util.HashMap implements RSCollection {
    private String[] columnNames;
    private int[] columnTypes;
    private String[] columnTypeNames;
    private String[] tableNames;
    
    /** Creates a new instance of ResultSetHashMap */
    public ResultSetHashMap( ResultSet rs ) 
    throws SQLException {
        ResultSetMetaData rsMeta = rs.getMetaData();
        MetaDataMapper convert = new MetaDataMapper();
        columnNames = convert.getResultColumnNames( rsMeta );
        columnTypes = convert.getResultColumnTypes( rsMeta );
        columnTypeNames = convert.getResultColumnTypeNames( rsMeta );
        tableNames = convert.getResultTableNames( rsMeta );
        while (rs.next()) {  
            for (int i = 0; i < columnNames.length; i++) {                
                this.put(columnNames[i], rs.getObject(i + 1));            
            }
        }
        Collections.unmodifiableMap(this);
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
}
