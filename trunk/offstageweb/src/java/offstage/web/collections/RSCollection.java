/*
 * RSCollection.java
 */

package offstage.web.collections;

/**
 * Each new data structure should implement these methods - we want to store
 * (and consequently retrieve) some of the metadata that was contained in the 
 * original ResultSet.  How the metadata is stored in the new data structure
 * is up to the programmer, but the metadata can be accessed only through these
 * abstract methods.
 * @author Michael Wahl
 */
public interface RSCollection {
    String[] getColumnNames();
    int[] getColumnTypes();
    String[] getColumnTypeNames();
    String[] getTableNames();
}