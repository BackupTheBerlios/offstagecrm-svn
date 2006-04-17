/*
 * GetAccountInfoServlet.java
 * Created on April 1, 2006, 2:59 PM
 */

package offstage.web.familystatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.http.*;
import offstage.web.collections.ResultSetHashMap;
import offstage.web.*;


/**
 * If adult and primary entity, then add account info (username, password) to
 * session and redirect to view Adult info, else if adult and not primary entity
 * then redirect to view Adult info, else redirect to view child info.
 * @author Michael Wahl
 */
public class GetAccountInfoServlet extends GetPhoneInfoServlet {
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        Integer primaryentityid = (Integer)sess.getAttribute( "primaryentityid" );
        // If signed in...
        if ( primaryentityid != null )
        {
            String id = request.getParameter("id");
            String aid = request.getParameter( "adultid" );
            
            // Need id of person being edited and its adult id
            if ( id != null && aid != null )
            {
                Integer entityid = new Integer( id );
                Integer adultid = new Integer( aid );
                
                // If is adult...
                if ( adultid.compareTo( entityid ) == 0 ){
                {
                    // If is primary adult..
                    if ( entityid.compareTo( primaryentityid ) == 0 )
                        try {
                            // Get username and password for editing
                            ResultSet rs = DB.getAccountInfo( st, primaryentityid );
                            ResultSetHashMap account = new ResultSetHashMap( rs );
                            sess.setAttribute( "account", account );
                        } catch ( SQLException e ){
                            System.out.println( e );   
                        }
                    }
                    // Redirect to view adult info
                    System.out.println("REDIRECTING TO VIEW ADULT");
                    redirect(request, response, "/familystatus/ViewAdultInfo.jsp?id=" + id );
                } 
                
                // Else got to edit child info
                else {
                    System.out.println("REDIRECTING TO VIEW CHILD");
                    redirect(request, response, "/familystatus/ViewChildInfo.jsp?id=" + id );
                }
            } 
            else redirect(request, response, "/FamilyStatus.jsp" );
        } 
        else redirect(request, response, "/login.jsp");
    }
}
