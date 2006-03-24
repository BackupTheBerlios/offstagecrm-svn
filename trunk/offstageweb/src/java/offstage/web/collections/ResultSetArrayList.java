/*
 * ResultSetArrayList.java
 * Created on March 17, 2006, 5:43 PM
 */
package offstage.web.collections;

import java.util.*;
import java.sql.*;
/**
 * An ArrayList with a constructor that has a java.sql.ResultSet as its only parameter.
 * Each row in the ResultSet is represented by a HashMap and corresponds to a
 * single slot in the ArrayList.
 * This hashmap is suitable for ResultSets that contain multiple rows of data.
 * @author Michael Wahl
 */
public class ResultSetArrayList extends ArrayList implements RSCollection {
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
//            list.add(Collections.unmodifiableMap(map));        
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
}