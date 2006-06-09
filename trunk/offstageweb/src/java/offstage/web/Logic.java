package offstage.web;
/*
 * Logic.java
 *
 * Created on March 11, 2006, 2:01 PM
 */

import java.sql.Time;
import java.util.*;
import java.text.*;
/**
 *
 * @author Michael Wahl
 */
public class Logic {
    
    /** Creates a new instance of Logic */
    public Logic() {
    }

    /**
     * Return whether the date of birth given is 19 or older - this is assuming
     * that the dob string given is of the format 'mm/dd/yyyy'
     */
    public boolean isAdult( String dob ){
        StringTokenizer tokenizer = new StringTokenizer( dob, "/" );
        String bmonth = tokenizer.nextToken();            
        String bdate = tokenizer.nextToken();
        String byear = tokenizer.nextToken();
        
        int month = Integer.parseInt(bmonth);
        int date = Integer.parseInt(bdate);
        int year = Integer.parseInt(byear);

        // Test for age limit of 19 or older
        Calendar c = Calendar.getInstance();
        int curYear = c.get( Calendar.YEAR );
        
        // Get cut off date
        c.set( Calendar.YEAR, curYear - 19 );
        Date cutOffDate = c.getTime();
        
        // Get birth date
        c.set( year, month-1, date );
        Date birthDate =  c.getTime();
        
        if ( birthDate.compareTo(cutOffDate) <= 0 ) return true;
        else return false;
    }
    
    /**
     * Return Date with 'mm/dd/yyyy' format
     */
    public Date formatDate( String dob )
    throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(false);
        return dateFormat.parse(dob);
    }

    /**
     * Return Date with the specified format
     */
    public Date formatDate( String date, String format )
    throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setLenient(false);
        return dateFormat.parse(date);
    }
    
    /**
     * Test for correct format: '(xxx)xxx-xxxx'
     * Should be split into six tokens:
     * token 1 - '('
     * token 2 - 'xxx' - area code
     * token 3 - ')'
     * token 4 - 'xxx' - first 3 digits
     * token 5 - '-'
     * token 6 - 'xxxx' - last 4 digits
     * @param number string to be tested
     * @return if given parameter is in correct phone number format
     */
    public boolean isCorrectPhoneNumberFormat( String number ){
        try {
            // Test for '('
            StringTokenizer tokenizer = new StringTokenizer( number, "()-", true );
            String token1 = tokenizer.nextToken();
            if ( token1.compareTo("(") != 0 ) return false;
            
            // Test for three digit area code
            String token2 = tokenizer.nextToken();
            if ( token2.length() != 3 ) return false;
            int areacode = Integer.parseInt(token2);
            
            // Test for ')'
            String token3 = tokenizer.nextToken();
            if ( token3.compareTo(")") != 0 ) return false;
            
            // Test for three digit number
            String token4 = tokenizer.nextToken();
            if ( token4.length() != 3 ) return false;
            int threedigits = Integer.parseInt(token4);
            
            // Test for '-'
            String token5 = tokenizer.nextToken();
            if ( token5.compareTo("-") != 0 ) return false;
            
            // Test for four digit number
            String token6 = tokenizer.nextToken();
            if ( token6.length() != 4 ) return false;
            int fourdigits = Integer.parseInt(token6);
            
        } catch ( Exception e ){
            return false;
        }
        return true;
    }

    /**
     * Test for correct extension format - 'xxx..' where all x are numerals
     * @param extension string to check for correct extension
     * @return true if correct extension format or false if wrong format
     */
    public boolean isCorrectExtFormat( String extension ) {
        try {
            int ext = Integer.parseInt( extension );
        } catch ( Exception e ){
            return false;
        }
        return true;
    }

    /**
     * Given dob Date return age.
     */
    public Integer getAge( Date dob ) 
    throws ParseException {
        if (dob == null) throw new NullPointerException("dob cannot be null");
        
        Calendar c = Calendar.getInstance();
        int cmonth = c.get(Calendar.MONTH);
        int cday = c.get(Calendar.DATE);
        int cyear = c.get(Calendar.YEAR);
        
        c.setTime(dob);
        int bmonth = c.get(Calendar.MONTH);
        int bday = c.get(Calendar.DATE);
        int byear = c.get(Calendar.YEAR);

        if ( bmonth < cmonth || ( bmonth == cmonth && bday <= cday ) ){
            return new Integer( cyear - byear );
        } else return new Integer( cyear - byear - 1 );
    }
    
    public String getDayOfWeek(Integer dayofweek) {
        if ( dayofweek == null ) return null;
        else {
            switch( dayofweek.intValue() ){
                case Calendar.SUNDAY : return "Sunday";
                case Calendar.MONDAY : return "Monday";
                case Calendar.TUESDAY : return "Tuesday";
                case Calendar.WEDNESDAY : return "Wednesday";
                case Calendar.THURSDAY : return "Thursday";
                case Calendar.FRIDAY : return "Friday";
                case Calendar.SATURDAY : return "Saturday";
            }
        }
        return null;
    }
    
    /**
     * Given specified pattern for SimpleDateFormat, format the given Date.  If
     * Date paramter is null or pattern is null, then return null.
     */
    public String getSimpleDate( java.util.Date date, String pattern ){
        if ( date == null || pattern == null ) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
    
    /**
     * Given the specified DateFormat type and the given Time, format.  If the
     * Time parameter is null, then return null.
     */
    public String getSimpleTime( java.sql.Time time, int dateformat ){
        if ( time == null ) return null;
        DateFormat timeFormatter = DateFormat.getTimeInstance( dateformat );
        return timeFormatter.format(time);
    }
}
