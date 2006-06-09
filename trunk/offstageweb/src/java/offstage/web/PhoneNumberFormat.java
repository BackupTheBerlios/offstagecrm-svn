/*
 * PhoneNumberFormat.java
 * Created on April 28, 2006, 7:15 PM
 */

package offstage.web;

import java.text.FieldPosition;
import java.text.ParsePosition;

/**
 * Formats phone number into (xxx)xxx-xxxx format
 * @author Michael Wahl
 */
public class PhoneNumberFormat extends java.text.Format {
    // Before areacode
    private String preareacode;
    // After areacode and before trunk
    private String pretrunk;
    // After trunk and before areacode
    private String prelastfour;
    
    /** 
     * Creates a new instance of PhoneNumberFormat 
     */
    public PhoneNumberFormat() {
        preareacode = "(";
        pretrunk = ")";
        prelastfour = "-";
    }
    
    public PhoneNumberFormat( String format ){
        this.setFormat( format );
    }
    
    /**
     * Takes the given object and reformats into a correctly formatted phone number
     * in the given StringBuffer result parameter - default format is '(ddd)ddd-dddd'
     * @param o object to be formatted
     * @param result buffer used to put the formated phone into
     * @param fieldPosition keep track of parsing of the Object
     * @return a buffer containing a correctly formatted phone number
     */
    public StringBuffer format( Object o, StringBuffer result, FieldPosition fieldPosition )
    {
        // Only convert Strings, Longs, and Integers to a PhoneNumber
        if ( o instanceof String )
            return format( (String)o, result, fieldPosition );
        
        // This has to be a very large number
        else if ( o instanceof Long )
            return format( (Long)o, result, fieldPosition );
        else 
            throw new IllegalArgumentException("Cannot format given Object as a Phone Number");
    }
    
    /**
     * Takes the given Long number returns a correctly formatted phone number
     * in the given StringBuffer result parameter
     * @param phone to be formatted
     * @param result buffer used to put the formated phone into
     * @param fieldPosition keep track of parsing of the Object
     * @return a buffer containing a correctly formatted phone number
     * @exception NullPointerException if the given Long is null
     */
    public StringBuffer format( Long phone, StringBuffer result, FieldPosition fieldPosition )
    {
        if ( phone == null ) throw new NullPointerException("Long cannot be null");
        
        // If phone has 10 digits
        if ( phone.longValue() > 999999999L && phone.longValue() < 10000000000L ){
            
            // First 3 digits
            int areacode = (int)((phone.longValue() - phone.longValue()%10000000L)/10000000L);
            System.out.println("areacode IS " + areacode);
            
            // Second 3 digits
            int trunk = (int)(((phone.longValue() - areacode*10000000L) - (phone.longValue()%10000))/10000);
            System.out.println("trunk IS " + trunk);
            
            // Last 4 digits
            int lastfour = (int)( phone.longValue()%10000);
            System.out.println("lastfour IS " + lastfour);
            
            result.append( preareacode + areacode + pretrunk + trunk + prelastfour + lastfour );
            
            fieldPosition.setBeginIndex(0);
            fieldPosition.setEndIndex(result.length());
            
            return result;
        } else 
            throw new IllegalArgumentException( "Phone number must be 10 digits long");
    }
    
    /**
     * Formats the given <code>String</code> into a phone string and appends
     * the result to the given <code>StringBuffer</code>.
     *
     * @param phone the phone value to be formatted into a date-time string.
     * @param result where the new phone text is to be appended.
     * @param fieldPosition the formatting position. On input: an alignment field,
     * if desired. On output: the offsets of the alignment field.
     * @return the formatted phone string.
     * @exception NullPointerException if the given string is null
     */
    public StringBuffer format( String phone, StringBuffer result, FieldPosition fieldPosition )
    {
        if ( phone == null ) throw new NullPointerException("String cannot be null");
        
        StringBuffer numBin = new StringBuffer();
        char[] phoneAry = phone.toCharArray();
        for( int i = 0; i < phone.length(); ++i ){
            if( phoneAry[i] >= '0' && phoneAry[i] <= '9' ){
                numBin.append(phoneAry[i]);
            }
        }
        if ( numBin.length() > 0 ){
            Long phonenumber = new Long( numBin.toString() );
            return this.format( phonenumber, result, fieldPosition );
        } else 
            throw new IllegalArgumentException("Phone must contain numbers!");
    }
    
    
    /**
     * Return a correctly parsed <code>PhoneNumber</code>
     * @param source phonenumber used to create the new Object
     * @param pos to keep track of how the source parameter has been parsed
     * @return new <code>PhoneNumber</code>
     */
    public Object parseObject( String source, ParsePosition pos )
    {
        return parse( source, pos );
    }
    
    /**
     * Return a correctly parsed <code>PhoneNumber</code>
     * @param phone number used to create the PhoneNumber
     * @param pos to keep track of how the source parameter has been parsed
     * @return new <code>PhoneNumber</code>
     */
    public PhoneNumber parse( String phone, ParsePosition pos )
    {
        StringBuffer numBin = new StringBuffer();
        int i = 0;
        char[] phoneAry = phone.toCharArray();
        for( ; i < phone.length(); ++i ){
            pos.setIndex(i);
            if( phoneAry[i] >= '0' && phoneAry[i] <= '9' ){
                numBin.append(phoneAry[i]);
            }
        }
        if ( numBin.length() > 0 ){
            Long phonenumber = new Long( numBin.toString() );
            return this.parse( phonenumber );
        } else 
            throw new IllegalArgumentException("Phone must contain numbers!");
    }
    
    /**
     * Return a correctly parsed <code>PhoneNumber</code>
     * @param phone number used to create the PhoneNumber
     * @return new <code>PhoneNumber</code>
     */
    public PhoneNumber parse( Long phone ){
        StringBuffer phonebuf = this.format( phone, new StringBuffer(), new FieldPosition(-1) );
        return new PhoneNumber( phonebuf.toString() );
    }
    
    /**
     * Return a correctly parsed <code>PhoneNumber</code>
     * @param phone number used to create the PhoneNumber
     * @return new <code>PhoneNumber</code>
     */
    public PhoneNumber parse( String phone ){
        ParsePosition pp = new ParsePosition( 0 );
        return parse( phone, pp );
    }
    
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
    public void setFormat( String format ){
        if ( format == null ) throw new NullPointerException("PARAMETER TO SET FORMAT CANNOT BE NULL");
        
        char[] aryformat = format.toCharArray();
        
        // Make sure parameter has 10 digits each represented by a 'd' in the String
        int dcount = 0;
        for( int i = 0; i < aryformat.length; ++i ){
            if ( aryformat[i] == 'd' )++dcount;
        }
        if ( dcount != 10 )
            throw new IllegalArgumentException("Format must contain 10 digits");

        int slot = 0;
        // Get preareacode if first letter is not 'd'
        if ( aryformat[0] != 'd' ){
            StringBuffer temp = new StringBuffer();
            for ( ; slot < aryformat.length; ++slot ){
                if ( aryformat[slot] == 'd' ) break;
                else {
                    temp.append(aryformat[slot]);
                }
            }
            preareacode = temp.toString();
        // If first letter is a 'd' then preareacode is blank
        } else preareacode = "";
        
        // Iterate through the area code - search until you find a non-'d' character
        dcount = 0;
        for ( ; slot < aryformat.length; ++slot ){
            if ( aryformat[slot] != 'd') break;
            ++dcount;
        }
        if ( dcount != 3 )
            throw new IllegalArgumentException("Lastfour digits must be four digits long!");
        
        // Now get pretrunk
        if ( slot < aryformat.length ){
            // Get pretrunk number
            StringBuffer temp = new StringBuffer();
            for ( ; slot < aryformat.length; ++slot ){
                if ( aryformat[slot] == 'd' ) break;
                else {
                    temp.append(aryformat[slot]);
                }
            }
            pretrunk = temp.toString();
        } else pretrunk = "";
        
        // Iterate through the trunk...Now search until you find a non-'d' character
        dcount = 0;
        for ( ; slot < aryformat.length; ++slot ){
            if ( aryformat[slot] != 'd') break;
            ++dcount;
        }
        if ( dcount != 3 )
            throw new IllegalArgumentException("Trunk digits must be four digits long!");
        
        // Get prelastfour
        if ( slot < aryformat.length ){
            // Get pretrunk number
            StringBuffer temp = new StringBuffer();
            for ( ; slot < aryformat.length; ++slot ){
                if ( aryformat[slot] == 'd' ) break;
                else {
                    temp.append(aryformat[slot]);
                }
            }
            prelastfour = temp.toString();
        } else prelastfour = "";
        
        // Iterate through the last four digits - search until you find a non-'d' character
        dcount = 0;
        for ( ; slot < aryformat.length; ++slot ){
            if ( aryformat[slot] != 'd') break;
            ++dcount;
        }
        if ( dcount != 4 )
            throw new IllegalArgumentException("Lastfour digits must be four digits long!");
    }
}
