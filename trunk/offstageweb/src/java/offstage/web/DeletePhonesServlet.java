/*
 * DeletePhonesServlet.java
 * Created on April 1, 2006, 12:39 PM
 */

package offstage.web;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.http.*;

/**
 *
 * @author Michael Wahl
 * @version
 */
public class DeletePhonesServlet extends citibob.web.DbServlet {
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        String phonetype = request.getParameter( "phonetype" );
        String hasExtension = request.getParameter( "hasExtension" );
        
        if ( phonetype != null && phonetype.compareTo("") != 0 ){
            Integer entityid = (Integer)sess.getAttribute( "primaryentityid" );
            try {
                DB.deletePhones( st, entityid, phonetype );
                if ( hasExtension != null && hasExtension.compareTo("yes") == 0 ){
                    DB.deletePhones( st, entityid, "extension" );
                }
            } catch ( SQLException e ) {
                System.out.println( e );
            }
            redirect(request, response, "/GetPhoneInfoServlet");
        } else redirect(request, response, "/GetPhoneInfoServlet");
    }
}
