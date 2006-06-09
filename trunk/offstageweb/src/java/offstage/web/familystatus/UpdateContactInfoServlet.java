package offstage.web.familystatus;
import javax.servlet.http.*;

import java.sql.*;
import java.util.*;
import offstage.web.*;
import offstage.web.PhoneNumber;
import offstage.web.PhoneNumberFormat;

/**
 * Update contact information associated with a unique primary entityid.
 * Contact information includes address and home, work, and fax number.
 * @author Michael Wahl
 */
public class UpdateContactInfoServlet extends offstage.web.MyServlet   {
    
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        // Remove extraneous information from session...
        sess.removeAttribute("contactInfo");
        sess.removeAttribute("phones");
        sess.removeAttribute("message");
        
        // Get mandatory parameters
        Integer primaryentityid = (Integer)sess.getAttribute("primaryentityid");
        String buttonResponse = request.getParameter( "submit" );
        
        // Test parameters
        if ( primaryentityid != null && buttonResponse != null && 
                buttonResponse.compareTo("Submit") == 0 ) {
            // Now get rest of user input....
            String customaddressto = request.getParameter("customaddressto");
            String address1 = request.getParameter("address1");
            String address2 = request.getParameter("address2");
            String city = request.getParameter("city");
            String state = request.getParameter("state");
            String zip = request.getParameter("zip");
            String country = request.getParameter("country");
            String email = request.getParameter("email");
            
            PhoneNumberFormat pnf = new PhoneNumberFormat();
            // Get phone numbers
            String _wphone = request.getParameter("wphone");
            String _hphone = request.getParameter("hphone");
            String _fphone = request.getParameter("fphone");
            
            // Get work phone ...
            PhoneNumber wphone = null;
            try {
                wphone = pnf.parse( _wphone );
            }catch (Throwable t){
                System.out.println(t);
            }
            
            // ...Get home phone...
            PhoneNumber hphone = null;
            try {
                hphone = pnf.parse( _hphone );
            }catch (Throwable t){
                System.out.println(t);
            }
            
            // ...Get fax phone.
            PhoneNumber fphone = null;
            try {
                fphone = pnf.parse( _fphone );
            }catch (Throwable t){
                System.out.println(t);
            }
            
            // Get work, home and fax extension
            Integer wext = this.getIntegerParameter(request, "wext");
            Integer hext = this.getIntegerParameter(request, "hext");            
            Integer fext = this.getIntegerParameter(request, "fext");
            
            // Get whether or not the work, home and fax numbers are domestic
            Boolean wdomestic = this.getBooleanParameter(request, "wdomestic");
System.out.println("wdomestic" + wdomestic);
            // If checked, then phone is international...so must
            // switch the value of boolean...this might seem a little odd but
            // we are mapping user input of whether a phone is international 
            // to what will be stored in the db which is whether it is domestic
            if ( wdomestic.booleanValue() ) wdomestic = new Boolean(false);
            else wdomestic = new Boolean( true );
            
            Boolean hdomestic = this.getBooleanParameter(request, "hdomestic");
System.out.println("hdomestic" + hdomestic);
            // If checked, then phone is international...so must
            // switch the value of boolean...this might seem a little odd but
            // we are mapping user input of whether a phone is international 
            // to what will be stored in the db which is whether it is domestic
            if ( hdomestic.booleanValue() ) hdomestic = new Boolean(false);
            else hdomestic = new Boolean( true );
            
            Boolean fdomestic = this.getBooleanParameter(request, "fdomestic");
            // If checked, then phone is international...so must
            // switch the value of boolean...this might seem a little odd but
            // we are mapping user input of whether a phone is international 
            // to what will be stored in the db which is whether it is domestic
            if ( fdomestic.booleanValue() ) fdomestic = new Boolean(false);
            else fdomestic = new Boolean( true );
System.out.println("fdomestic" + fdomestic);

            // Get whether or not work, home and fax number have been verified by user
            Boolean wVerify = this.getBooleanParameter(request, "wVerify");
            Boolean hVerify = this.getBooleanParameter(request, "hVerify");
            Boolean fVerify = this.getBooleanParameter(request, "fVerify");
            
            // Done getting user input...now test for bad input...
            // Append bad input info to buffer...
            StringBuffer buf = new StringBuffer();
            if ( customaddressto == null || customaddressto.compareTo("") == 0 )
                buf.append( "Address To, " );
            if ( address1 == null || address1.compareTo("") == 0 )
                buf.append( "Address 1, " );
            if ( city == null || city.compareTo("") == 0 )
                buf.append( "City, " );
            if ( state == null || state.compareTo("") == 0 )
                buf.append( "State, " );
            if ( zip == null || zip.compareTo("") == 0 )
                buf.append( "Zip, " );
            if ( country == null || country.compareTo("") == 0 )
                buf.append( "Country, " );
            
            // We want the user to have final say for the format of the phones...
            // So IF the PhoneNumber is null, and IF the original parameter of 
            // the phone is not null and not an empty string, and IF the number
            // has not already been verified, THEN have the user verify the phone.
            if ( fphone == null && 
                    _fphone != null && _fphone.compareTo("") != 0 &&
                    ( fVerify == null || fVerify.booleanValue() == false ) )
                buf.append( "Verify Fax Number, ");
            if ( wphone == null && 
                    _wphone != null && _wphone.compareTo("") != 0 && 
                    ( wVerify == null || wVerify.booleanValue() == false ) )
                buf.append( "Verify Work Number, ");
            if ( hphone == null && 
                    _hphone != null && _hphone.compareTo("") != 0 && 
                    ( hVerify == null || hVerify.booleanValue() == false ) )
                buf.append( "Verify Home Number, ");
            
            // We also want extensions that are just digits...So IF the input
            // for an extensions is not just digits, then append message to buffer
            String _wext = request.getParameter("wext");
            if ( wext == null && _wext != null && _wext.compareTo("") != 0 )
                buf.append("Work Ext Must Be All Digits");
            
            String _hext = request.getParameter("hext");
            if ( hext == null && _hext != null && _hext.compareTo("") != 0 )
                buf.append("Home Ext Must Be All Digits");
            
            String _fext = request.getParameter("fext");
            if ( fext == null && _fext != null && _fext.compareTo("") != 0 )
                buf.append("Fax Ext Must Be All Digits");
            
            // If there are bad input...then create contactinfo and phones hashmaps
            // to store relevant information in the session key for use by
            // UpdateContactInfoError.jsp
            if ( buf.length() > 2 ){
                // Note: 'length - 2' removes last instance of ', ' in the String.
                String badInput = buf.substring(0, buf.length()-2);
                
                HashMap contactInfo = new HashMap();
                contactInfo.put( "customaddressto", customaddressto );
                contactInfo.put( "address1", address1 );
                contactInfo.put( "address2", address2 );
                contactInfo.put( "city", city );
                contactInfo.put( "state", state );
                contactInfo.put( "zip", zip );
                contactInfo.put( "country", country );
                contactInfo.put( "email", email );
                
                HashMap phones = new HashMap();
                phones.put( "wphone", wphone );
                phones.put( "wext", wext );
                phones.put( "wdomestic", wdomestic );
                
                phones.put( "hphone", wphone );
                phones.put( "hext", hext );
                phones.put( "hdomestic", hdomestic );
                
                phones.put( "fphone", fphone );
                phones.put( "fext", fext );
                phones.put( "fdomestic", fdomestic );
                
                // IF the original parameters for a phone were not empty
                // but a PhoneNumber could not be formatted correctly,
                // THEN have the user validate that the original number is 
                // what he/she had intended (ie the user has ultimate say in phone format)
                if ( wphone == null && _wphone != null && _wphone.compareTo("") != 0 ){
                    // Replace phone with the original parameter
                    phones.put( "wphone", _wphone );
                    phones.put( "wVerify", new Boolean(true) );
                } else phones.put("wVerify", new Boolean(false));
                
                if ( hphone == null && _hphone != null && _hphone.compareTo("") != 0 ){
                    // Replace phone with the original parameter
                    phones.put("hphone", _hphone);
                    phones.put("hVerify", new Boolean(true));
                } else phones.put("hVerify", new Boolean(false));
                
                if ( fphone == null && _fphone != null && _fphone.compareTo("") != 0 ){
                    // Replace phone with the original parameter
                    phones.put("fphone", _fphone);
                    phones.put("fVerify", new Boolean(true) );
                } else phones.put("fVerify", new Boolean(false));
                
                sess.setAttribute( "message", badInput );
                sess.setAttribute( "contactInfo", contactInfo );
                sess.setAttribute( "phones", phones );
                redirect(request, response, "/familystatus/UpdateContactInfoError.jsp");
            } else {
                HashMap phones = new HashMap();
                try {
                    // First Update mailing address...
                    DB.updateContactInfo( st, primaryentityid, customaddressto, 
                            address1, address2, city, state, zip, country, email );
                    
                    // IF either hphone has been filled 
                    // OR IF the original parameter, _hphone, is not null AND
                    // is not empty, AND _hphone has been verified by the customer
                    // THEN try to delete the old phone in the db (if an old
                    // phone number exists) and then insert the new phone into
                    // the database.
                    if ( hphone != null || 
                            ( _hphone != null && _hphone.compareTo("") != 0
                              && hVerify != null && hVerify.booleanValue() == true ) ){
                        DB.deletePhones( st, primaryentityid, "home" );
                        
                        // IF phone has a value then insert into db
                        if ( hphone != null ){
                            DB.insertPhones( st, primaryentityid, "home", hphone.toString(), hext, hdomestic );
                            
                        // ELSE the original parameter has a value that couldn't
                        // be converted to a PhoneNumber but the user has verified.
                        } else {
                            DB.insertPhones( st, primaryentityid, "home", _hphone, hext, hdomestic );
                        }
                    }
                    
                    // IF either wphone has been filled 
                    // OR IF the original parameter, _wphone, is not null AND
                    // is not empty, AND _wphone has been verified by the customer
                    // THEN try to delete the old phone in the db (if an old
                    // phone number exists) and then insert the new phone into
                    // the database.
                    if ( wphone != null || 
                            ( _wphone != null && _wphone.compareTo("") != 0
                              && wVerify != null && wVerify.booleanValue() == true ) ){
                        DB.deletePhones( st, primaryentityid, "work" );
                        
                        // IF phone has a value then insert into db
                        if ( wphone != null ){
                            DB.insertPhones( st, primaryentityid, "work", wphone.toString(), wext, wdomestic );
                            
                        // ELSE the original parameter has a value that couldn't
                        // be converted to a PhoneNumber but the user has verified.
                        } else {
                            DB.insertPhones( st, primaryentityid, "work", _wphone, wext, wdomestic );
                        }
                    }
                    
                    // IF either fphone has been filled 
                    // OR IF the original parameter, _fphone, is not null AND
                    // is not empty, AND _fphone has been verified by the customer
                    // THEN try to delete the old phone in the db (if an old
                    // phone number exists) and then insert the new phone into
                    // the database.
                    if ( fphone != null || 
                            ( _fphone != null && _fphone.compareTo("") != 0
                              && fVerify != null && fVerify.booleanValue() == true ) ){
                        DB.deletePhones( st, primaryentityid, "fax" );
                        
                        // IF phone has a value then insert into db
                        if ( fphone != null ){
                            DB.insertPhones( st, primaryentityid, "fax", fphone.toString(), fext, fdomestic );
                            
                        // ELSE the original parameter has a value that couldn't
                        // be converted to a PhoneNumber but the user has verified.
                        } else {
                            DB.insertPhones( st, primaryentityid, "fax", _fphone, fext, fdomestic );
                        }
                    }
                } catch (SQLException e){
                    System.out.println( e );
                }
                redirect(request, response, "/FamilyStatus.jsp" );
            }
        } else redirect(request, response, "/FamilyStatus.jsp" );
    }
}
