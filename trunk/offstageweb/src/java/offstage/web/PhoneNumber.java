/*
 * PhoneNumber.java
 * Created on April 28, 2006, 7:57 PM
 */

package offstage.web;

import java.text.FieldPosition;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Holds the value for a phone number.
 * @author Michael Wahl
 */
public class PhoneNumber {
    // All digits
    private Long phonedigits;
    
    // Format of phone number - 'd' represents a digit in the String
    private String format;
    
    /**
     * Create new instance of Phone number given a format of a 10 digit phone 
     * number.  By default the format is "(ddd)ddd-dddd"
     * where d represents a digit.  
     * @param phonenumber Any string that has 10 digits - which will be 
     * reformatted according to the format set.
     */
    public PhoneNumber( String phonenumber ) {
        if ( phonenumber == null )
            throw new NullPointerException("PhoneNumber: parameter in the constructor cannot be null");
        format = "(ddd)ddd-dddd";
        this.setPhoneNumber( phonenumber );
    }
    
    /**
     * Create new instance of Phone number given a String and a format of a 10 digit phone 
     * number. 
     * @param phonenumber must have 10 digits total
     * @param format The format must follow this convention:
     * search for any non-'d' characters before the areacode, then non-'d' 
     * character between the areacode and the trunk, then non-'d' characters 
     * between the trunk and the last four digits. For example the default
     * format is: "(ddd)ddd-dddd".  If you want to change the format to have only
     * hyphens send the parameter: "ddd-ddd-dddd". NOTE that the format must set
     * the area code to be 3 digits, the trunk to be 3 digits and the last four 
     * digits to be four digits long.
     */
    public PhoneNumber( String phonenumber, String format ) {
        if ( phonenumber == null )
            throw new NullPointerException("PhoneNumber: parameter in the constructor cannot be null");
        this.format = format;
        this.setPhoneNumber( phonenumber );
    }
    
    /*
     * Create new instance of Phone number given a format of a 10 digit phone 
     * number.  By default the format is "(ddd)ddd-dddd"
     * where d represents a digit.  
     */
    public PhoneNumber(Long phonenumber){
        if ( phonenumber == null )
            throw new NullPointerException("PhoneNumber: parameter in the constructor cannot be null");
        format = "(ddd)ddd-dddd";
        this.setPhoneNumber( phonenumber.toString() );
    }
    
    /*
     * Create new instance of Phone number given a String and a format of a 10 digit phone 
     * number. 
     * @param phonenumber must have 10 digits total
     * @param format The format must follow this convention:
     * search for any non-'d' characters before the areacode, then non-'d' 
     * character between the areacode and the trunk, then non-'d' characters 
     * between the trunk and the last four digits. For example the default
     * format is: "(ddd)ddd-dddd".  If you want to change the format to have only
     * hyphens send the parameter: "ddd-ddd-dddd". NOTE that the format must set
     * the area code to be 3 digits, the trunk to be 3 digits and the last four 
     * digits to be four digits long.
     */
    public PhoneNumber(Long phonenumber, String format){
        if ( phonenumber == null )
            throw new NullPointerException("PhoneNumber: parameter in the constructor cannot be null");
        this.format = format;
        phonedigits = phonenumber;
        this.setPhoneNumber( phonenumber.toString() );
    }    
    
    // Set phone as a Long number...must be 10 digits long
    private void setPhoneNumber( String phonenumber ){
        StringBuffer sb = new StringBuffer();
        char[] phoneAry = phonenumber.toCharArray();
        for ( int i = 0; i < phoneAry.length; ++i ){
            if ( phoneAry[i] >= '0' && phoneAry[i] <= '9' ){
                sb.append( phoneAry[i] );
            }
        }
        // Must be 10 digits long
        if ( sb.length() != 10 ) throw new IllegalArgumentException( "Phone number needs 10 digits!" ); 
        else phonedigits = new Long( sb.toString() );
    }
    
    /**
     * Return String representation of the PhoneNumber
     * @return String representation of the PhoneNumber
     */
    public String toString(){
        PhoneNumberFormat pnf = new PhoneNumberFormat(format);
        return pnf.format(phonedigits);
    }
    
    /**
     * Return digit representation of the PhoneNumber
     * @return digit representation of the PhoneNumber
     */
    public Long toDigits(){return phonedigits;}
    
    /**
     * Set format of a 10 digit phone number.
     * @param format The format must follow this convention:
     * search for any non-'d' characters before the areacode, then non-'d' 
     * character between the areacode and the trunk, then non-'d' characters 
     * between the trunk and the last four digits. For example the default
     * format is: "(ddd)ddd-dddd".  If you want to change the format to have only
     * hyphens send the parameter: "ddd-ddd-dddd". NOTE that the format must set
     * the area code to be 3 digits, the trunk to be 3 digits and the last four 
     * digits to be four digits long.
     */
    public void setFormat(String format){this.format = format;}
    
    /**
     * Returns current format for PhoneNumber
     * @return format for PhoneNumber
     */
    public String getFormat(){return format;}
}
