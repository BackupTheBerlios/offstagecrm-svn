/*
 * GetFamilyStatusServlet.java
 * Created on March 17, 2006, 2:41 PM
 */

package offstage.web;
import javax.servlet.http.*;

import java.sql.*;
import offstage.web.collections.ResultSetArrayList;
/**
 * 
 * @author Michael Wahl
 */
public class GetFamilyStatusServlet extends citibob.web.DbServlet {
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        
// Change this when add in log in process
// Normally log in process will set primaryentityid in the session
// For now either pass id in using parameters or use one harded coded below
//    int primaryentityid = 12633;
        Integer id = new Integer( request.getParameter("id") );
        if ( id == null ){
            Integer _primaryentityid = new Integer(6056);
            sess.setAttribute( "primaryentityid", _primaryentityid );
        } else sess.setAttribute( "primaryentityid", id );

// Keep the code from this point on...
        Integer primaryentityid = (Integer)sess.getAttribute( "primaryentityid" );
        if ( primaryentityid != null ){
            try {
                boolean hasCardBalance = DB.hasCardBalance( st, primaryentityid );
                if ( hasCardBalance ){
                    double cardBalance = DB.getCardBalance( st, primaryentityid );
                    sess.setAttribute("cardBalance", new Double( cardBalance ) );
                } else sess.setAttribute( "cardBalance", null );

                ResultSet rs = DB.getFamily( st, primaryentityid );
                ResultSetArrayList familyList = new ResultSetArrayList( rs );
                sess.setAttribute( "familyList", familyList );
            } catch ( SQLException e ){
                System.out.println( e );
            }
            redirect( request, response, "/FamilyStatus.jsp" );
        } else redirect(request, response, "/login.jsp");
    }
}
