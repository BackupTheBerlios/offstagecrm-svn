/*
 * MetaDataMapper.java
 */

package offstage.web.collections;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Utility class that maps data from ResultSetMetaData to different Data Structures
 * @author Michael Wahl
 */
public class MetaDataMapper {
    
    /**
     * Creates a new instance of MetaDataMapper 
     */
    public MetaDataMapper() {
    }

    /**     
     * Retrieves the column names for a result.     
     * @param rsMeta The ResultSet metadata to retrieve names from     
     * @throws SQLException If there was a problem retrieving results     
     */
    public String[] getResultColumnNames( ResultSetMetaData rsMeta )
    throws SQLException {
        String[] columnNames = new String[rsMeta.getColumnCount()];        
        for (int i = 0; i < columnNames.length; i++) {            
            columnNames[i] = rsMeta.getColumnName(i + 1);            
            columnNames[i] = columnNames[i].toLowerCase();            
        }
        return columnNames;
    }

    /**     
     * Retrieves the column types of the result set as defined by java.sql.Types
     * @param rsMeta The ResultSet metadata to retrieve names from     
     * @throws SQLException If there was a problem retrieving results     
     */
    public int[] getResultColumnTypes( ResultSetMetaData rsMeta )
    throws SQLException {
        int[] columnTypes = new int[rsMeta.getColumnCount()];        
        for (int i = 0; i < rsMeta.getColumnCount(); i++) {            
            columnTypes[i] = rsMeta.getColumnType(i + 1);
        }
        return columnTypes;
    }

    /**     
     * Retrieves the column types of the result set
     * @param rsMeta The ResultSet metadata to retrieve names from     
     * @throws SQLException If there was a problem retrieving results     
     */
    public String[] getResultColumnTypeNames( ResultSetMetaData rsMeta )
    throws SQLException {
        String[] columnTypeNames = new String[rsMeta.getColumnCount()];
        for (int i = 0; i < rsMeta.getColumnCount(); i++) {            
            columnTypeNames[i] = rsMeta.getColumnTypeName(i + 1);
        }
        return columnTypeNames;
    }

    /**
     * Retrieves the column types of the result set
     * @param rsMeta The ResultSet metadata to retrieve names from     
     * @throws SQLException If there was a problem retrieving results     
     */
    public String[] getResultTableNames( ResultSetMetaData rsMeta )
    throws SQLException {
        String[] tableNames = new String[rsMeta.getColumnCount()];
        for (int i = 0; i < rsMeta.getColumnCount(); i++) {            
            tableNames[i] = rsMeta.getColumnTypeName(i + 1);
        }
        return tableNames;
    }

}
