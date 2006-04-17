/*
 * GetEligibleProgramsServlet.java
 * Created on April 6, 2006, 6:16 PM
 */

package offstage.web.registration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.http.*;
import offstage.web.DB;
import offstage.web.Logic;
import offstage.web.collections.ResultSetArrayList;

/**
 * Retrieve all programs person is eligible for given entityid given date of
 * birth.  If person is anonymous then retrieve all programs an anonymous person
 * is eligible for given date of birth.
 * @author Michael Wahl
 */
public class GetRegistrationsServlet extends citibob.web.DbServlet {
    
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        String _entityid = request.getParameter( "id" );
        String dob = request.getParameter( "dob" );
        if ( _entityid != null && dob != null ){
            Logic logic = new Logic();
            Integer age = logic.getAge(dob);
            Integer entityid = new Integer( _entityid );
            try {
                
                ResultSet rs = DB.getCurrentRegistrations( st, entityid );
                ResultSetArrayList registeredPrograms = new ResultSetArrayList( rs );
                sess.setAttribute( "registeredPrograms", registeredPrograms );
System.out.println(registeredPrograms);
                rs = DB.getEligibleRegistrations( st, entityid, age );
                ResultSetArrayList eligiblePrograms = new ResultSetArrayList( rs );
                sess.setAttribute( "eligiblePrograms", eligiblePrograms );
System.out.println(eligiblePrograms);
                
            } catch( SQLException e ){
                System.out.println( e );
            }
            redirect( request, response, "/ViewRegistrations.jsp?id=" + entityid + 
                    "&fname=" + request.getParameter( "fname" ) +
                    "&age=" + age
                    );

        } else redirect( request, response, "/FamilyStatus.jsp" );
    }
}
