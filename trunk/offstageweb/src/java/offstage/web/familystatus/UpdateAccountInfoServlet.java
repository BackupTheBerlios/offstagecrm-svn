/*
 * UpdateAccountInfoServlet.java
 * Created on April 1, 2006, 4:46 PM
 */

package offstage.web.familystatus;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.http.*;
import offstage.web.*;

/**
 * Update username and password for a given primaryentityid
 * @author Michael Wahl
 */
public class UpdateAccountInfoServlet extends citibob.web.DbServlet {
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        Integer primaryentityid = (Integer)sess.getAttribute( "primaryentityid" );
        if ( primaryentityid != null ){
            String username = request.getParameter( "username" );
            String password = request.getParameter( "password" );
            
            if ( username != null && username.compareTo("") != 0 
              && password != null && password.compareTo("") != 0  ){
                
                try {
                    DB.updateAccountInfo( st, primaryentityid, username, password );
                } catch ( SQLException e ){
                    System.out.println( e );
                }
                redirect(request, response, "/FamilyStatus.jsp");
            } else {
                String message = "Input Error: Please Fill In Both Fields!";
                sess.setAttribute( "message", message );
                redirect(request, response, "/familystatus/UpdateAccountError.jsp?" +
                        "username=" + username +
                        "&password=" + password
                        );
            }
        } else redirect(request, response, "/login.jsp");
    }
    
}
