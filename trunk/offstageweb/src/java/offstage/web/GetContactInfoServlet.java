/*
 * GetContactInfoServlet.java
 * Created on March 18, 2006, 3:55 PM
 */

package offstage.web;
import javax.servlet.http.*;

import java.sql.*;
import java.util.*;
import offstage.web.collections.ResultSetArrayList;
import offstage.web.collections.ResultSetHashMap;

/**
 * Get both address info and phone info then redirect to ViewContactInfo.jsp
 * Extend GetPhoneInfoServlet to use its getExtension() method
 * @author Michael Wahl
 */
public class GetContactInfoServlet extends GetPhoneInfoServlet  {
    
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        Integer primaryentityid = (Integer)sess.getAttribute("primaryentityid");
        
        if ( primaryentityid != null ) {
            try {
                // Get address info, email
                ResultSet rs = DB.getContactInfo( st, primaryentityid );
                Map contactInfo = new ResultSetHashMap( rs );
                sess.setAttribute( "contactInfo", contactInfo );
                
                // Get phone numbers
                ResultSet _rs = DB.getPhones( st, primaryentityid );
                ResultSetArrayList phones = new ResultSetArrayList( _rs );
                sess.setAttribute( "phones", phones );
                
                // Get phone extension if it exists in phone numbers
                String extension = getExtension( phones );
                if ( extension != null ) sess.setAttribute( "extension", extension );
                
                redirect(request, response, "/ViewContactInfo.jsp");
                
            } catch ( SQLException e ){
                System.out.println( e );
                redirect(request, response, "/FamilyStatus.jsp");
            }
        } else redirect(request, response, "/FamilyStatus.jsp");
    }
}
