/*
 * GetEligibleProgramsServlet.java
 * Created on April 6, 2006, 6:16 PM
 */

package offstage.web.registration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Map;
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
public class GetRegistrationsServlet extends offstage.web.MyServlet {
    
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        // Remove Extraneous attributes
        sess.removeAttribute("registeredPrograms");
        sess.removeAttribute("eligiblePrograms");
        
        Integer entityid = this.getIntegerParameter( request, "id" );
        ResultSetArrayList familyList = (ResultSetArrayList)sess.getAttribute( "familyList" );
System.out.println("entityid is: " + entityid);
System.out.println("familyList is: " + familyList);

        Integer age = null;
        if ( entityid != null && familyList != null ) {
            Map person = familyList.get( "entityid", entityid );
        
            // Validate dob and get age
            Logic logic = new Logic();
            java.util.Date birthdate = null;
            if ( person != null ){
                birthdate = (java.util.Date)person.get("dob");
                age = logic.getAge(birthdate);
            }
        }
        
        if ( entityid != null && age != null ){
            try {
                ResultSet rs = DB.getCurrentRegistrations( st, entityid );
                ResultSetArrayList registeredPrograms = new ResultSetArrayList( rs );
System.out.println(registeredPrograms);

                rs = DB.getEligibleRegistrations( st, entityid, age );
                ResultSetArrayList eligiblePrograms = new ResultSetArrayList( rs );
System.out.println(eligiblePrograms);

                LinkedList urlstack = (LinkedList)sess.getAttribute("urlstack");
                urlstack.addFirst( "/GetRegistrationsServlet?id=" + entityid );

                sess.setAttribute( "registeredPrograms", registeredPrograms );
                sess.setAttribute( "eligiblePrograms", eligiblePrograms );
                redirect( request, response, "/ViewRegistrations.jsp?" +
                        "id=" + entityid
                        );
            } catch( SQLException e ){
                System.out.println( e );
            }
        } else redirect( request, response, "/GetFamilyStatusServlet" );
    }
}
