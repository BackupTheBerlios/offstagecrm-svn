/*
 * DB.java
 * Created on March 10, 2006, 4:43 PM
 */

package offstage.web;

import java.sql.*;
import citibob.jschema.pgsql.*;
/**
 * 
 * @author Michael Wahl
 */
public class DB {
    
    /** Creates a new instance of DB */
    public DB() {
    }
    
    /**
     * Get family information given the unique identifier primaryentityid
     * @param st 
     * @param primaryentityid unique identifier
     * @throws java.sql.SQLException if a database access error occurs
     * @return rows of entityid, firstname, middlename, lastname, gender, dob, email,
     * relprimarytype.name, adultid, occupation, title
     */
    public static ResultSet getFamily( Statement st, int primaryentityid )
    throws SQLException
    {
        return st.executeQuery(
                "SELECT entityid, firstname, middlename, lastname, gender, dob, " +
                " email, relprimarytypes.name, adultid, occupation, title " +
                " FROM persons, relprimarytypes " +
                " WHERE primaryentityid = " + primaryentityid +
                " AND obsolete = " + SqlBool.sql(false) +
                " AND persons.relprimarytypeid = relprimarytypes.relprimarytypeid" +
                " ORDER BY dob"
                );
    }
    
    /**
     * Retrieve contact info associated with given primaryentityid
     * @param st 
     * @param primaryentityid unique identifier of contact info
     * @throws java.sql.SQLException if a database access error occurs
     * @return rows containing customaddressto, address1, address2, city, state,
     * country, zip, email
     */
    public static ResultSet getContactInfo( Statement st, Integer primaryentityid )
    throws SQLException
    {
        return st.executeQuery(
                "SELECT customaddressto, address1, address2, city, state, " +
                " zip, country, email " +
                " FROM persons" +
                " WHERE primaryentityid = entityid" +
                " AND primaryentityid = " + SqlInteger.sql(primaryentityid) +
                " AND obsolete = " + SqlBool.sql(false)
                );
    }
    
    /**
     * Update contact info given the entities primaryentityid
     * @param st statement used query the database
     * @param primaryentityid unique identifier of the entity
     * @param customaddressto info to be updated
     * @param address1 info to be updated
     * @param address2 info to be updated
     * @param city info to be updated
     * @param state info to be updated
     * @param zip info to be updated
     * @param country info to be updated
     * @param email info to be updated
     * @throws java.sql.SQLException if a database access error occurs
     * @return number of rows effected by the update
     */
    public static int updateContactInfo( Statement st, Integer primaryentityid, 
            String customaddressto, String address1, String address2, String city,
            String state, String zip, String country, String email )
    throws SQLException
    {
        return st.executeUpdate(
                "UPDATE persons " +
                " SET " +
                " customaddressto = " + SqlString.sql(customaddressto) + ", " +
                " address1 = " + SqlString.sql(address1) + ", " +
                " address2 = " + SqlString.sql(address2) + ", " +
                " city = " + SqlString.sql(city) + ", " +
                " state = " + SqlString.sql(state) + ", " +
                " zip = " + SqlString.sql(zip) + ", " +
                " country = " + SqlString.sql(country) + ", " +
                " email = " + SqlString.sql(email) + " " +
                " WHERE entityid = " + SqlInteger.sql(primaryentityid)
                );
    }
    
    /**
     * Get all phones associated with the unique identifier, primaryentityid
     * @param st 
     * @param primaryentityid unique identifier
     * @throws java.sql.SQLException if a database access error occurs
     * @return rows containing type of phone number, phonenumber
     */
    public static ResultSet getPhones( Statement st, Integer primaryentityid )
    throws SQLException
    {
        return st.executeQuery(
                "SELECT phoneids.name, phones.phone" +
                " FROM phones, phoneids" +
                " WHERE entityid = " + SqlInteger.sql(primaryentityid) +
                " AND phones.groupid = phoneids.groupid"
                );
    }
    
    /**
     * Get card balance for the associated primaryentityid
     * @param st 
     * @param primaryentityid unique identifier
     * @throws java.sql.SQLException if a database access error occurs
     * @return card balance
     */
    public static double getCardBalance( Statement st, int primaryentityid )
    throws SQLException
    {
        
        double balance = 0.0;
        double transactions = 0.0;

        ResultSet rs = st.executeQuery(
                "SELECT ccardbalance.balance " +
                " FROM ccardbalance " +
                " WHERE ccardbalance.dtime = " +
                "   ( SELECT max(ccardbalance.dtime) " +
                "     FROM ccardbalance " +
                "     WHERE ccardbalance.personid = " + primaryentityid + ") "
                );
        if ( rs.next() ){
            balance = rs.getDouble("balance");
            
            // Get all transactions commited after the most current balance was inserted
            ResultSet rs2 = st.executeQuery(
                    "SELECT sum(amount) " +
                    " FROM ccardtrans " +
                    " WHERE ccardtrans.dtime >" +
                    "   ( SELECT max(ccardbalance.dtime) " +
                    "     FROM ccardbalance " +
                    "     WHERE ccardbalance.personid = " + primaryentityid + ") "
                    );
            if ( rs2.next() ){
                transactions = rs2.getDouble("sum");
            }
        }
        return balance + transactions;
    }
    
    /**
     * Query db to see if there is a cardbalance given the unique identifier 
     * primaryentityid
     * @param st 
     * @param primaryentityid unique identifier
     * @throws java.sql.SQLException if a database access error occurs
     * @return number of rows effected
     */
    public static boolean hasCardBalance( Statement st, int primaryentityid )
    throws SQLException
    {
        // Get most current balance
        ResultSet rs = st.executeQuery(
                "SELECT ccardbalance.balance " +
                " FROM ccardbalance " +
                " WHERE ccardbalance.personid = " + primaryentityid
                );
        if ( rs.next() ){ return true;}
        else return false;
    }

    /**
     * Update a child in the db given the unique identifier, entityid.
     * @param st 
     * @param entityid - unique identifier
     * @param firstname - info to be updated
     * @param middlename - info to be updated
     * @param lastname - info to be updated
     * @param gender - info to be updated
     * @param dob - info to be updated
     * @param email - info to be updated
     * @param relprimarytype - info to be updated
     * @throws java.sql.SQLException if a database access error occurs
     * @return number of rows effected
     */
    public static int updateChild( Statement st, Integer entityid, String firstname, 
            String middlename, String lastname, String gender, String dob, 
            String email, String relprimarytype )
    throws SQLException
    {
        int relprimarytypeid = 0;
        ResultSet rs = st.executeQuery(
                " SELECT relprimarytypeid " +
                " FROM relprimarytypes " +
                " WHERE name = " + SqlString.sql(relprimarytype)
                );
        if ( rs.next() ) relprimarytypeid = rs.getInt(1);
System.out.println( "relprimarytypeid is: " + relprimarytypeid );
        
        return st.executeUpdate(
                "UPDATE persons " +
                " SET " +
                " firstname = " + SqlString.sql(firstname) + ", " +
                " middlename = " + SqlString.sql(middlename) + ", " +
                " lastname = " + SqlString.sql(lastname) + ", " +
                " gender = " + SqlString.sql(gender) + ", " +
                " dob = to_date(" + SqlString.sql(dob) + ", 'MM/DD/YYYY'), " +
                " email = " + SqlString.sql(email) + ", " +
                " relprimarytypeid = " + SqlInteger.sql( relprimarytypeid )  + ", " +
                " lastupdated = " + SqlTimestamp.sql( new Timestamp( System.currentTimeMillis() ) ) + " " +
                " WHERE entityid = " + SqlInteger.sql(entityid)
                );
    }
    
    /**
     * Update adult in db given the unique identifier entityid
     * @param st 
     * @param entityid - unique identifier
     * @param firstname - info to be updated
     * @param middlename - info to be updated 
     * @param lastname - info to be updated 
     * @param gender - info to be updated 
     * @param dob - info to be updated 
     * @param email - info to be updated 
     * @param occupation - info to be updated 
     * @param title - info to be updated 
     * @throws java.sql.SQLException if a database access error occurs
     * @return number of rows effected
     */
    public static int updateAdult( Statement st, Integer entityid, String firstname, 
            String middlename, String lastname, String gender, String dob, 
            String email, String occupation, String title )
    throws SQLException
    {
        System.out.println( "occupation is " + occupation );
        return st.executeUpdate(
                "UPDATE persons " +
                " SET " +
                " firstname = " + SqlString.sql(firstname) + ", " +
                " middlename = " + SqlString.sql(middlename) + ", " +
                " lastname = " + SqlString.sql(lastname) + ", " +
                " gender = " + SqlString.sql(gender) + ", " +
                " dob = to_date(" + SqlString.sql(dob) + ", 'MM/DD/YYYY'), " +
                " email = " + SqlString.sql(email) + ", " +
                " lastupdated = " + SqlTimestamp.sql( new Timestamp( System.currentTimeMillis() ) ) + ", " +
                " occupation = " + SqlString.sql(occupation) + ", " +
                " title = " + SqlString.sql(title) + " " +
                " WHERE entityid = " + SqlInteger.sql(entityid)
                );
    }
    
    /**
     * Insert new child
     * @param st 
     * @param firstname info to be inserted
     * @param middlename info to be inserted
     * @param lastname info to be inserted
     * @param gender info to be inserted
     * @param dob info to be inserted
     * @param email info to be inserted
     * @param relprimarytype info to be inserted
     * @param primaryentityid info to be inserted
     * @throws java.sql.SQLException if a database access error occurs
     * @return number of rows effected
     */
    public static int insertChild( Statement st, String firstname, 
            String middlename, String lastname, String gender, String dob, 
            String email, String relprimarytype, Integer primaryentityid )
    throws SQLException
    {
        int entityid = 0;
        ResultSet rs = st.executeQuery("SELECT nextval('entities_entityid_seq')");
        if ( rs.next() ) entityid = rs.getInt(1);
System.out.println("new entityid is: " + entityid );
        int relprimarytypeid = 0;
        rs = st.executeQuery(
                " SELECT relprimarytypeid " +
                " FROM relprimarytypes " +
                " WHERE name = " + SqlString.sql(relprimarytype)
                );
        if ( rs.next() ) relprimarytypeid = rs.getInt(1);
System.out.println( "relprimarytypeid is: " + relprimarytypeid );
        
        return st.executeUpdate(
                "INSERT INTO persons" +
                " VALUES (" +
                " " + SqlInteger.sql(entityid) + ", " +
                " " + SqlInteger.sql(0) + ", " +
                " " + SqlInteger.sql(primaryentityid) + ", " +
                " " + SqlString.sql("") + ", " +
                " " + SqlString.sql("") + ", " +
                " " + SqlString.sql("") + ", " +
                " " + SqlString.sql("") + ", " +
                " " + SqlString.sql("") + ", " +
                " " + SqlString.sql("") + ", " +
                " " + SqlString.sql("") + ", " +
                " " + SqlInteger.sql(0) + ", " +
                " " + SqlTimestamp.sql( new Timestamp( System.currentTimeMillis() ) ) + ", " +
                " " + SqlInteger.sql( relprimarytypeid )  + ", " +
                " " + Boolean.valueOf(true) + ", " +
//this was obsolete - a boolean that was added to make crm work
                " " + Boolean.valueOf(false) + ", " +
                " now()," +
//this was adultid - an int added to make crm work
                " " + SqlInteger.sql( primaryentityid ) + ", " +
                " " + SqlString.sql("") + ", " +
                " " + SqlString.sql("") + ", " +
                " " + SqlString.sql("") + ", " +
                " " + SqlString.sql(firstname) + ", " +
                " " + SqlString.sql(middlename) + ", " +
                " " + SqlString.sql(lastname) + ", " +
                " " + SqlString.sql("") + ", " +
                
                " " + SqlString.sql(gender) + ", " +
                " to_date(" + SqlString.sql(dob) + ", 'MM/DD/YYYY'), " +
                " " + SqlString.sql(email) + " " +
                " )" 
                );
    }
}