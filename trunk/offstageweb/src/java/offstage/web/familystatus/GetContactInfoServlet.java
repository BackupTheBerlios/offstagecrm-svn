/*
 * GetContactInfoServlet.java
 * Created on March 18, 2006, 3:55 PM
 */

package offstage.web.familystatus;
import javax.servlet.http.*;

import java.sql.*;
import java.util.*;
import offstage.web.collections.ResultSetArrayList;
import offstage.web.collections.ResultSetHashMap;
import offstage.web.*;

/**
 * Get both address info then redirect to ViewContactInfo.jsp
 * Extend GetPhoneInfoServlet to use its getExtension() method
 * @author Michael Wahl
 */
public class GetContactInfoServlet extends citibob.web.DbServlet {
    
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        // Remove extraneous information from session...
        sess.removeAttribute("contactInfo");
        sess.removeAttribute("phones");
        
        // Get primaryentityid from session
        Integer primaryentityid = (Integer)sess.getAttribute("primaryentityid");
        if ( primaryentityid != null ) {
            try {
                // Get address info, email
                ResultSet rs = DB.getContactInfo( st, primaryentityid );
                Map contactInfo = new ResultSetHashMap( rs );
                sess.setAttribute( "contactInfo", contactInfo );
                
                // Get phone numbers
                rs = DB.getPhones( st, primaryentityid );
                ResultSetArrayList phonelist = new ResultSetArrayList( rs );
                HashMap phones = new HashMap();
                
                // Iterate to search for phones that are work, home or fax
                // Remove all phones that aren't work, home, or fax
                Iterator i = phonelist.listIterator();
                while ( i.hasNext() ){
                    Map phone = (Map)i.next();
                    String phonename = (String)phone.get( "name" );
                    if ( phonename.compareTo("work") == 0 ||
                         phonename.compareTo("home") == 0 ||
                         phonename.compareTo("fax") == 0 )
                    {
                        phones.put( phonename,  phone );
                    }
                }
                sess.setAttribute( "phones", phones );
                
                redirect(request, response, "/familystatus/UpdateContactInfo.jsp");
            } catch ( SQLException e ){
                System.out.println( e );
                redirect( request, response, "/FamilyStatus.jsp" );
            }
        } else redirect( request, response, "/FamilyStatus.jsp" );
    }
}
