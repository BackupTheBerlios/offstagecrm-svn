package offstage.web.familystatus;
/*
 * UpdateContactInfoServlet.java
 * Created on March 18, 2006, 6:11 PM
 */

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.*;
import citibob.sql.*;
import citibob.jschema.pgsql.*;
import java.util.*;
import java.text.*;
import offstage.web.*;
/**
 * Update contact information associated with a unique primary entityid
 * @author Michael Wahl
 */
public class UpdateContactInfoServlet extends citibob.web.DbServlet   {
    
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        Integer primaryentityid = (Integer)sess.getAttribute("primaryentityid");
        String buttonResponse = (String)request.getParameter("submit");
        
        if ( primaryentityid != null && buttonResponse.compareTo("Submit") == 0 ) {
            String customaddressto = (String)request.getParameter("customaddressto");
System.out.println(customaddressto);
            String address1 = (String)request.getParameter("address1");
            String address2 = (String)request.getParameter("address2");
            String city = (String)request.getParameter("city");
            String state = (String)request.getParameter("state");
            String zip = (String)request.getParameter("zip");
            String country = (String)request.getParameter("country");
            String email = (String)request.getParameter("email");
            if ( customaddressto == null || address1 == null ||
                 city == null || state.compareTo("") == 0 || 
                 zip == null || country == null ){
                redirect(request, response, "/familystatus/UpdateContactInfoError.jsp?" +
                        "customaddressto=" + URLEncoder.encode(customaddressto, "UTF-8" ) + 
                        "&address1=" + URLEncoder.encode( address1, "UTF-8" ) +
                        "&address2=" + URLEncoder.encode( address2, "UTF-8" ) + 
                        "&city=" + URLEncoder.encode( city, "UTF-8" ) + 
                        "&state=" + URLEncoder.encode( state, "UTF-8" ) +
                        "&zip=" + URLEncoder.encode( zip, "UTF-8" ) + 
                        "&country=" + URLEncoder.encode( country, "UTF-8" ) + 
                        "&email=" + URLEncoder.encode( email, "UTF-8" )
                        );
            } else {
                try {
                    DB.updateContactInfo( st, primaryentityid, customaddressto, 
                            address1, address2, city, state, zip, country, email );
                } catch (SQLException e){
                    System.out.println( e );
                }
                redirect(request, response, "/FamilyStatus.jsp");
            }
        } else redirect(request, response, "/FamilyStatus.jsp");
    }
    
}
