package offstage.web;
/*
 * Logic.java
 *
 * Created on March 11, 2006, 2:01 PM
 */

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
     * Check to see if the date is of the format 'mm/dd/yyyy'
     */
    public boolean isCorrectDateFormat( String dob ){
        if ( dob.compareTo("mm/dd/yyyy") == 0) return false;
        StringTokenizer tokenizer = new StringTokenizer( dob, "/" );
        if ( tokenizer.countTokens() == 3 ) {
            String bmonth = tokenizer.nextToken();
            if ( bmonth.length() == 1 ) return false;
            
            String bdate = tokenizer.nextToken();
            if ( bdate.length() == 1 ) return false;
            
            String byear = tokenizer.nextToken();
            try {
                // If cant perform parseInt, then not a number - this will throw
                // an exception
                Integer.parseInt(bmonth);
                Integer.parseInt(bdate);
                int year = Integer.parseInt(byear);

                // Check format
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                dateFormat.setLenient(false);
                dateFormat.parse(dob);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else return false;
    }

}
