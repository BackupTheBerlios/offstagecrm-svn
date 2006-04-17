/*
 * InsertRegistrationServlet.java
 * Created on April 8, 2006, 12:52 PM
 */

package offstage.web.registration;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.http.*;
import offstage.web.DB;
import offstage.web.collections.ResultSetArrayList;

/**
 *
 * @author Michael Wahl
 */
public class InsertRegistrationServlet extends citibob.web.DbServlet {
    
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        String _entityid = request.getParameter( "id" );
        String _programid = request.getParameter( "programid" );
        String _age = request.getParameter( "age" );
        
        if ( _entityid != null & _programid != null && _age != null ){
            Integer entityid  = new Integer( _entityid );
            Integer programid = new Integer( _programid );
            Integer age       = new Integer( _age );
            boolean isEligible = false;
            
            try {
                ResultSet rs = DB.getEligibleRegistrations( st, entityid, age );
                while ( rs.next() ){
                    Integer pid = new Integer ( rs.getInt( "programid" ) );
                    if ( pid.compareTo(programid) == 0 ){
System.out.println( "PROGRAMIDS MATCH!" );
                        isEligible = true;
                        break;
                    }
                }
                if ( isEligible ) {
                    DB.insertRegistration( st, programid, entityid );
System.out.println("INSERTED!");                
                    redirect( request, response, "/GetRegistrationsServlet?" +
                            "id=" + entityid );
                }
            } catch ( SQLException e ){
                System.out.println( e );
            }
        }
        redirect( request, response, "/FamilyStatus.jsp" );
    }
}
