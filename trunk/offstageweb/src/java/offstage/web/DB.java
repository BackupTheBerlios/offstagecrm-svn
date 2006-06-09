/*
 * DB.java
 * Created on March 10, 2006, 4:43 PM
 */

package offstage.web;

import java.sql.*;
import citibob.jschema.pgsql.*;
import java.util.Calendar;
//import org.apache.xml.dtm.ref.sax2dtm.SAX2DTM;
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
    public static ResultSet getFamily( Statement st, Integer primaryentityid )
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
     * Update phone number given the unique entityid( owner of phone number ) and
     * the type of phone (name)
     * @param st statement used to update
     * @param entityid unique id of owner of phone number
     * @param name type of phone number (i.e. work, home, fax etc.)
     * @param number phone number
     * @throws java.sql.SQLException if a database access error occurs
     * @return number of rows effected
     */
    public static int updatePhones( Statement st, Integer entityid, 
            String name, String number )
    throws SQLException
    {
        ResultSet rs = st.executeQuery(
                " SELECT groupid " +
                " FROM phoneids " +
                " WHERE name = " + SqlString.sql(name)
                );
        
        int groupid = Integer.MIN_VALUE;
        if ( rs.next() ) groupid = rs.getInt("groupid");
System.out.println("groupid is: " + groupid);
        return st.executeUpdate(
                "UPDATE phones " +
                " SET phone = " + SqlString.sql(number) +
                " WHERE groupid = " + SqlInteger.sql(groupid) +
                " AND entityid = " + SqlInteger.sql(entityid)
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
                "SELECT phoneids.name, phone, domestic, ext " +
                " FROM phones, phoneids" +
                " WHERE entityid = " + SqlInteger.sql(primaryentityid) +
                " AND phones.groupid = phoneids.groupid"
                );
    }

    /**
     * Remove phone given entityid and name of type of phone
     * @param st statement used to interact with db
     * @param entityid unique id of entity associated with phone
     * @param name type of phone being removed
     * @throws java.sql.SQLException if a database access error occurs
     * @return whether phone number has been removed from db
     */
    public static boolean deletePhones( Statement st, Integer entityid, String name )
    throws SQLException
    {
        ResultSet rs = st.executeQuery(
                " SELECT groupid " +
                " FROM phoneids " +
                " WHERE name = " + SqlString.sql(name)
                );
        
        int groupid = Integer.MIN_VALUE;
        if ( rs.next() ) groupid = rs.getInt("groupid");
        
        return st.execute(
                "DELETE FROM " +
                " phones " +
                " WHERE entityid =" + SqlInteger.sql(entityid) +
                " AND groupid =" + SqlInteger.sql(groupid)
                );
    }

    /**
     * Insert phone number into db given entityid and name
     * @param st statement used to interact with db
     * @param entityid unique id of entity the phone number belongs to
     * @param name type of phone number
     * @param number phone number
     * @param ext the extension
     * @param domestic whether or not the number is domestic
     * @throws java.sql.SQLException if a database access error occurs
     * @return number of rows effected by insert
     */
    public static int insertPhones( Statement st, Integer entityid, String name, 
            String number, Integer ext, Boolean domestic )
    throws SQLException
    {
        ResultSet rs = st.executeQuery(
                " SELECT groupid " +
                " FROM phoneids " +
                " WHERE name = " + SqlString.sql(name)
                );
        
        int groupid = Integer.MIN_VALUE;
        if ( rs.next() ) groupid = rs.getInt("groupid");
        
        return st.executeUpdate(
                "INSERT INTO phones" +
                " VALUES (" +
                " " + SqlInteger.sql(groupid) + ", " +
                " " + SqlInteger.sql(entityid) + ", " +
                " " + SqlString.sql(number) + ", " +
                " " + SqlInteger.sql(ext) + ", " +
                " " + SqlBool.sql(domestic) + 
                " ) " 
                );
        
    }
    /**
     * Get card balance for the associated primaryentityid
     * @param st statement used to interact with db
     * @param primaryentityid unique identifier
     * @throws java.sql.SQLException if a database access error occurs
     * @return card balance
     */
    public static double getCardBalance( Statement st, Integer primaryentityid )
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
    public static boolean hasCardBalance( Statement st, Integer primaryentityid )
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
            String middlename, String lastname, String gender, java.util.Date dob, 
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
                " dob = " + SqlDate.sql(dob) + ", " +
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
            String middlename, String lastname, String gender, java.util.Date dob, 
            String email, String occupation, String title )
    throws SQLException
    {
        return st.executeUpdate(
                "UPDATE persons " +
                " SET " +
                " firstname = " + SqlString.sql(firstname) + ", " +
                " middlename = " + SqlString.sql(middlename) + ", " +
                " lastname = " + SqlString.sql(lastname) + ", " +
                " gender = " + SqlString.sql(gender) + ", " +
                " dob = " + SqlDate.sql(dob) + ", " +
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
            String middlename, String lastname, String gender, java.util.Date dob, 
            String email, String relprimarytype, Integer primaryentityid )
    throws SQLException
    {
        // Get new entityid
        int entityid = 0;
        ResultSet rs = st.executeQuery("SELECT nextval('entities_entityid_seq')");
        if ( rs.next() ) entityid = rs.getInt(1);

        // Find relprimarytypeid
        int relprimarytypeid = 0;
        rs = st.executeQuery(
                " SELECT relprimarytypeid " +
                " FROM relprimarytypes " +
                " WHERE name = " + SqlString.sql(relprimarytype)
                );
        if ( rs.next() ) relprimarytypeid = rs.getInt(1);
        
        // Insert new person
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
                " " + SqlInteger.sql( primaryentityid ) + ", " +
                " " + SqlString.sql("") + ", " +
                " " + SqlString.sql("") + ", " +
                " " + SqlString.sql("") + ", " +
                " " + SqlString.sql(firstname) + ", " +
                " " + SqlString.sql(middlename) + ", " +
                " " + SqlString.sql(lastname) + ", " +
                " " + SqlString.sql("") + ", " +
                
                " " + SqlString.sql(gender) + ", " +
                " " + SqlDate.sql(dob) + ", " +
                " " + SqlString.sql(email) + " " +
                " )" 
                );
    }

    /**
     * Retrieve password, username associated with given entityid
     * @param st statement used to get info from db
     * @param entityid unique id
     * @throws java.sql.SQLException if a database access error occurs
     * @return single username, password pair
     */
    public static ResultSet getAccountInfo( Statement st, Integer entityid )
    throws SQLException
    {
        return st.executeQuery(
                "SELECT * " +
                " FROM accounts" +
                " WHERE entityid = " + SqlInteger.sql(entityid)
                );
    }
    
    /**
     * Update username, password of the given entityid
     * @param st statement used to update
     * @param entityid unique id
     * @param username name for entityid
     * @param password password for entityid's account
     * @throws java.sql.SQLException if a database access error occurs
     * @return number of rows effected in update
     */
    public static int updateAccountInfo( Statement st, Integer entityid, 
            String username, String password )
    throws SQLException
    {
        return st.executeUpdate(
                "UPDATE accounts " +
                " SET " +
                " username = " + SqlString.sql(username) + ", " +
                " password = " + SqlString.sql(password) + " " +
                " WHERE entityid = " + SqlInteger.sql(entityid)
                );
    }

    /**
     * Get programs in which the given entityid has already registered for that
     * are current.
     * @param st statement used to query db
     * @param entityid unique id associated with programid
     * @throws java.sql.SQLException if a database access error occurs
     * @return set of programids and name of programs that given entityid has 
     * registered for
     */
    public static ResultSet getCurrentRegistrations(Statement st, Integer entityid) 
    throws SQLException {
        return st.executeQuery(
                "SELECT programids.name, programids.programid" +
                " FROM registrations, programids" +
                " WHERE registrations.entityid = " + SqlInteger.sql(entityid) + 
                " AND registrations.programid = programids.programid" +
                
                // exclude registrations that have expired
                " AND registrations.expiredate >= now()" +
                
                // EITHER exclude programs that are not current OR...
                " AND ( programids.termid NOT IN ( SELECT termids.termid" +
                "                                  FROM termids" +
                "                                  WHERE termids.iscurrent = " + SqlBool.sql(false) + ") " +
                
                // ...OR include programs that do not have a termid
                "       OR ( programids.programid NOT IN ( SELECT programids.programid " +
                "                                          FROM programids " +
                "                                          WHERE programids.termid > " + SqlInteger.sql(-1) + ") ) )"
                );
    }

    /**
     * Get all programs that the given entityid is eligible for given the age
     * and whether or not any needed eligibility has been met.  Exclude programs
     * that the entityid has already registered for and programs that aren't 
     * current.
     * @param st statement used for the query
     * @param entityid unique id associated with the eligible programs
     * @param age age of entity
     * @throws java.sql.SQLException if a database access error occurs
     * @return 
     */
    public static ResultSet getEligibleRegistrations(Statement st, Integer entityid, Integer age ) 
    throws SQLException {
        return st.executeQuery(
                " SELECT programids.name, programids.programid " +
                " FROM programids" +
                
                // Get programs where no eligibilty needed
                " WHERE ( programids.needselig = 'false' " +
                
                // If need eligibility then get programs that are on
                // the registration eligibility list that have the
                // associated entityid
                "         OR ( programids.needselig = 'true' " +
                "              AND programids.programid IN ( SELECT programid" +
                "                                            FROM regelig " +
                "                                            WHERE regelig.entityid = " + SqlInteger.sql(entityid) + " )))" +
                
                // Also EITHER include programs where minage requirement is met OR...
                " AND ( " + SqlInteger.sql(age) + " >= programids.minage OR" +
                
                // ...include programs where no minage specified
                "       programids.programid NOT IN ( SELECT programid " +
                "                                     FROM programids" +
                                                      // This returns all programs where minage specified
                "                                     WHERE programids.minage > " + SqlInteger.sql(-1) + " ) )" +
                
                // Next exclude programs which person has already registered for
                " AND ( programids.programid NOT IN ( SELECT registrations.programid" + 
                "                                     FROM registrations" +
                "                                     WHERE registrations.entityid = " + SqlInteger.sql(entityid) + " ) )" +
                
                // Last EITHER exclude programs that are not current OR...
                " AND ( ( programids.termid NOT IN (SELECT termids.termid" +
                "                                   FROM termids" +
                "                                   WHERE termids.iscurrent = " + SqlBool.sql(false) + ") )" +
                
                // ...OR include programs that do not have a termid
                "         OR ( programids.programid NOT IN ( SELECT programids.programid " +
                "                                            FROM programids " +
                "                                            WHERE programids.termid > " + SqlInteger.sql(-1) + ") ) )"
                );
    }

    /**
     * Given programid and entityid insert a new registration
     * @param st statement used to query and update db
     * @param programid unique id of the program
     * @param entityid unique id of the entity
     * @throws java.sql.SQLException if a database access error occurs
     * @return number of rows effected by insert
     */
    public static int insertRegistration(Statement st, Integer programid, 
            Integer entityid) 
    throws SQLException {
        
        // First need to get the expiration date:
        Calendar c = Calendar.getInstance();
        java.util.Date expiredate = null;
        
        ResultSet rs = st.executeQuery(
                "SELECT termids.nextdate " +
                " FROM termids, programids" +
                " WHERE termids.termid = programids.termid" +
                " AND programids.programid = " + SqlInteger.sql( programid )
                );
        
        // Get expire time of term from termids if available...
        if ( rs.next() ) {
            java.sql.Date d = rs.getDate("nextdate");
            c.setTimeInMillis( d.getTime() );
        }
        
        // ...Else if no termid associated with program then set expiration date to 1 year
        else {
            int year = c.get( Calendar.YEAR );
            c.set( Calendar.YEAR, year + 1 );
        }
        expiredate = new java.sql.Date( c.getTimeInMillis() );
        
        return st.executeUpdate(
                "INSERT INTO registrations" +
                " VALUES (" +
                " " + SqlInteger.sql( programid ) + ", " +
                " " + SqlInteger.sql( entityid ) + ", " +
                " " + SqlTimestamp.sql( new Timestamp( System.currentTimeMillis() ) ) + ", " +
                " " + SqlDate.sql( expiredate ) + ") "
                );
    }
    
    /**
     * Get all enrollments that a given entityid is eligible for - including any
     * that the entityid is currently enrolled in.
     * @param st statement used to query and update db
     * @throws java.sql.SQLException if a database access error occurs
     */
    public static ResultSet getEligibleEnrollments( Statement st, Integer entityid ) 
    throws SQLException {
        return st.executeQuery(
                "SELECT coursesetids.coursesetid, programid, " +
                " coursesetids.name as coursesetids_name, " +
                " courseids.courseid, courseids.name as courseids_name," +
                " courseids.dayofweek, courseids.tstart, courseids.tnext," +
                " termids.name as term_name, termids.firstdate, termids.nextdate" +
                " FROM coursesetids, coursesets, courseids, termids" +
                " WHERE coursesetids.coursesetid = coursesets.coursesetid" +
                " AND coursesets.courseid = courseids.courseid" +
                
                // Cross termids, courseids
                " AND termids.termid = courseids.termid" +
                
                // Entity has to be registered for program
                " AND coursesetids.programid IN" +
                "(" +
                "            SELECT programids.programid" +
                "            FROM registrations, programids" +
                "            WHERE registrations.entityid = " + SqlInteger.sql(entityid) + 
                "            AND registrations.programid = programids.programid" +

                             // Registration can't be expired
                "            AND registrations.expiredate >= now()" +
                             // EITHER term has to be current... 
                "            AND ( programids.termid NOT IN ( SELECT termids.termid" +
                "                                             FROM termids" +
                "                                             WHERE termids.iscurrent = false )" +
                
                                   // ...OR there is no term associated with the program.
                "                  OR ( programids.programid NOT IN ( SELECT programids.programid" +
                "                                                     FROM programids" +
                "                                                     WHERE programids.termid > -1 ) ) )" +
                ")" +
                "ORDER BY coursesets.coursesetid, courseids.dayofweek, courseids.tstart"
                );
    }
    
    /**
     * Get all courses for which an entityid is currently enrolled in
     * @param st statement used to query and update db
     * @throws java.sql.SQLException if a database access error occurs
     */
    public static ResultSet getCurrentEnrollments(Statement st, Integer entityid) 
    throws SQLException {
        return st.executeQuery(
                // Get all course information of enrolled course associated with entityid...
                "SELECT courseids.courseid, courseids.name as course_name, " +
                " courseids.dayofweek, courseids.tstart, courseids.tnext," +
                " termids.name as term_name, termids.firstdate, termids.nextdate, " +
                " enrollments.pplanid, enrollments.entityid, termids.termid" +
                " FROM enrollments, courseids, termids " +
                " WHERE enrollments.entityid = " + SqlInteger.sql(entityid) + 
                
                // Include EITHER current enrollments...
                " AND ( enrollments.dend <= now() or" +

                // OR enrollments where enrollments.dend is blank
                "       enrollments.dend NOT IN ( SELECT enrollments.dend " +
                "                                 FROM enrollments " +
                "                                 WHERE enrollments.dend > 'epoch' ) )" +
                
                // Cross courseids, enrollments
                " AND courseids.courseid = enrollments.courseid" +
                
                // Cross termids, courseids
                " AND termids.termid = courseids.termid" +
                " ORDER BY courseids.dayofweek"
                );
    }

    public static ResultSet getCourseset(Statement st, Integer coursesetid) 
    throws SQLException {
        return st.executeQuery(
                "SELECT coursesets.courseid" +
                " FROM coursesets" +
                " WHERE coursesets.coursesetid = " + SqlInteger.sql( coursesetid )
                );
    }

    /**
     * Insert enrollment row with courserole as student and date enrolled as
     * current time given unique courseid and entityid
     * @param st statement used to query and update db
     * @throws java.sql.SQLException if a database access error occurs
     */
    public static int insertStudentEnrollment(Statement st, Integer courseid, 
            Integer entityid ) 
    throws SQLException {
        // Get courserole id...
        ResultSet rs = st.executeQuery(
                "SELECT courseroleid " +
                " FROM courseroles" +
                " WHERE name = 'Student'"
                );
        Integer courseroleid = null;
        if ( rs.next() ){
            courseroleid = new Integer( rs.getInt("courseroleid") );
            
            // ...then insert new enrollment
            return st.executeUpdate(
                    "INSERT INTO enrollments " +
                    " ( courseid, entityid, courserole, dtenrolled )" +
                    " VALUES (" +
                    SqlInteger.sql( courseid ) + ", " +
                    SqlInteger.sql( entityid ) + ", " +
                    SqlInteger.sql( courseroleid ) + ", " +
                    SqlTimestamp.sql( new Timestamp( System.currentTimeMillis() ) ) + 
                    " ) "
                    );
        } else return 0;
    }

    /**
     * Get all current payment plans associated with the given entityid
     * @param st statement used to query and update db
     * @param entityid 
     * @throws java.sql.SQLException if a database access error occurs
     * @return 
     */
    public static ResultSet getCurrentPaymentPlans(Statement st, Integer entityid) 
    throws SQLException {
            // ...then insert new enrollment
        return st.executeQuery(
                "SELECT pplanids.pplanid, pplanids.remain as remainpp, " +
                " pplanids.amount as amountpp, substring(pplanids.ccnumber from '....$') as ccnumber," +
                " paymenttypeids.name as paymentname, pplantypeids.name as planname, " +
                " termids.name as termname" +
                " FROM pplanids, paymenttypeids, pplantypeids, termids" +
                " WHERE entityid = " + SqlInteger.sql(entityid) +
                " AND pplanids.paymenttypeid = paymenttypeids.paymenttypeid" +
                " AND pplanids.paymentplantypeid = pplantypeids.pplantypeid" +
                " AND pplanids.termid = termids.termid"
                );
    }
    
    /**
     * Get all invoices associated with a particular payment plan id
     * @param st statement used to query and update db
     * @param pplanid 
     * @throws java.sql.SQLException if a database access error occurs
     * @return 
     */
    public static ResultSet getPPInvoices(Statement st, Integer pplanid) 
    throws SQLException {
        return st.executeQuery(
                "SELECT * " +
                " FROM pplaninvoiceids, invoiceids" +
                " WHERE pplaninvoiceids.invoiceid = invoiceids.invoiceid" +
                " AND pplaninvoiceids.pplanid = " + SqlInteger.sql(pplanid)
                );
    }


    public static ResultSet getPPlanTypeidsMenu(Statement st) throws SQLException {
        return st.executeQuery(
                " SELECT pplantypeid as value, name as label, pplantypeids.type" +
                " FROM pplantypeids"
                );
    }

    public static ResultSet getPaymentTypeidsMenu(Statement st) throws SQLException {
        return st.executeQuery(
                " SELECT paymenttypeid as value, name as label, paymenttypeids.table" +
                " FROM paymenttypeids"
                );
    }

    public static Integer insertPaymentPlan(Statement st, Integer primaryentityid, 
            String cctypeid, String ccnumber, java.util.Date invaliddate, String name, 
            Integer pplantypeid, Integer termid, Integer paymenttypeid) 
    throws SQLException {
        // Get new pplanid
        int pplanid = 0;
        ResultSet rs = st.executeQuery("SELECT nextval('pplanids_pplanid_seq')");
        if ( rs.next() ) pplanid = rs.getInt(1);
        
        boolean executed = st.execute(
                "INSERT INTO pplanids" +
                " VALUES (" +
                " " + SqlInteger.sql( pplanid ) + ", " +
                " " + SqlInteger.sql( primaryentityid ) + ", " +
                " " + SqlInteger.sql( paymenttypeid ) + ", " +
                " " + SqlString.sql( cctypeid ) + ", " +
                " " + SqlString.sql( ccnumber ) + ", " + 
                " " + SqlDate.sql( invaliddate ) + ", " + 
                " " + SqlString.sql( name ) + ", " + 
                // dtime
                " " + SqlTimestamp.sql( new Timestamp(System.currentTimeMillis()) ) + ", " + 
                // dtapproved
                " null, " + 
                " " + SqlInteger.sql( pplantypeid ) + ", " +
                // Remain
                " null, " + 
                // Amount
                " null, " + 
                " " + SqlInteger.sql( termid ) + ") "
                );
        return new Integer(pplanid);
    }

    public static int updateEnrollment(Statement st, Integer courseid, 
            Integer entityid, Integer pplanid) 
    throws SQLException {
        return st.executeUpdate(
                " UPDATE enrollments " +
                " SET pplanid = " + SqlInteger.sql(pplanid) +  
                " WHERE courseid = " + SqlInteger.sql(courseid) + 
                " and entityid = " + SqlInteger.sql(entityid)
                );
    }
}