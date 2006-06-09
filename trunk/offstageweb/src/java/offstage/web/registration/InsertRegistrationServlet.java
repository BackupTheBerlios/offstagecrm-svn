/*
 * InsertRegistrationServlet.java
 * Created on April 8, 2006, 12:52 PM
 */

package offstage.web.registration;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import javax.servlet.http.*;
import offstage.web.DB;

/**
 *
 * @author Michael Wahl
 */
public class InsertRegistrationServlet extends offstage.web.MyServlet {
    
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        // Remove Extraneous attributes
        sess.removeAttribute("registeredPrograms");
        sess.removeAttribute("eligiblePrograms");

        // Get parameters including checked boxes...
        String responsebutton = request.getParameter( "submit" );
        System.out.println("submit is: " + responsebutton );

        // Get all boxes that were checked...
        LinkedList programids = new LinkedList();
        Enumeration e = request.getParameterNames();
        while ( e.hasMoreElements() ) {
            String _parameter = (String)e.nextElement();
            // Test to see if parameter is a number
            Integer parameter = null;
            try {
                parameter = new Integer(_parameter);
            } catch(Throwable t){
                System.out.println(t);
            }
            
            // Get value if parameter is not null( i.e. it could be made into an integer )
            String value = null;
            if ( parameter != null ){
                value = (String)request.getParameter( parameter.toString() );
            }
            
            // If value gotten using parameter was on, then the box was checked
            // so add to programids list 
            if ( value != null && value.compareTo("on") == 0 ){
                programids.add( parameter );
            }
        }
        
        // Get entityid passed and the age of the entity
        Integer entityid = this.getIntegerParameter( request, "id" );
        
        // Verify entityid exists, that there are programids to register for,
        // and that the response button was to register...
        if ( entityid != null && programids.size() > 0 &&
                responsebutton != null && responsebutton.compareTo( "Register" ) == 0 ){
            try {
                Iterator i = programids.listIterator();
                while ( i.hasNext() ){
                    Integer programid = (Integer)i.next();
                    //DB.insertRegistration( st, programid, entityid );
System.out.println("INSERTED!");                
                }
//            } catch ( SQLException sqle ){
            } catch ( Throwable sqle ){
                System.out.println( sqle );
            }
            redirect( request, response, "/GetRegistrationsServlet?" +
                    "id=" + entityid );
        // If response button was to go back get previous url from urlstack
        } else if ( responsebutton != null && responsebutton.compareTo("Back") == 0 ){
            LinkedList urlstack = (LinkedList)sess.getAttribute("urlstack");
            String url = (String)urlstack.removeFirst();
            System.out.println("url is: " + url);
            url = (String)urlstack.removeFirst();
            System.out.println("url is: " + url);
            redirect( request, response, url );
        } else response.sendRedirect( request.getContextPath() + "/GetFamilyStatusServlet" );
    }
}
