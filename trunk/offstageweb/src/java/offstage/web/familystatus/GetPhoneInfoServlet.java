/*
 * GetPhoneInfoServlet.java
 *
 * Created on March 24, 2006, 4:44 PM
 */

package offstage.web.familystatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.*;
import offstage.web.collections.ResultSetArrayList;
import offstage.web.*;

/**
 * Get phone information for the given entity redirect to 
 * @author Michael Wahl
 */
public class GetPhoneInfoServlet extends citibob.web.DbServlet {

    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        Integer primaryentityid = (Integer)sess.getAttribute("primaryentityid");
        
        if ( primaryentityid != null ) {
            try {
                System.out.println("GETTING PHONES");
                ResultSet rs = DB.getPhones( st, primaryentityid );
                ResultSetArrayList phones = new ResultSetArrayList( rs );
                sess.setAttribute( "phones", phones );
                
                String extension = this.getExtension( phones );
                if ( extension != null ) sess.setAttribute( "extension", extension );
                else sess.setAttribute( "extension", null );
                
                redirect(request, response, "/familystatus/UpdatePhones.jsp");
                
            } catch ( SQLException e ){
                System.out.println( e );
                redirect(request, response, "/FamilyStatus.jsp");
            }
        } else redirect(request, response, "/login.jsp");
    }

    /**
     * Find extension if it exists in the set of phone numbers
     */
    protected String getExtension( ResultSetArrayList phones ){
        String extension = null;
        Iterator i = phones.iterator();
        while ( i.hasNext() ){
            Map phone = (Map)i.next();
            if ( phone.containsValue("extension") ){
                return (String)phone.get("phone");
            }
        }
        return null;
    }

}
