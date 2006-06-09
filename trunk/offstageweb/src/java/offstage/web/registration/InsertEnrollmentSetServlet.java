/*
 * InsertEnrollmentSetServlet.java
 *
 * Created on April 13, 2006, 5:29 PM
 */

package offstage.web.registration;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.servlet.http.*;
import offstage.web.DB;
import offstage.web.collections.ResultSetArrayList;

/**
 *
 * @author Michael Wahl
 */
public class InsertEnrollmentSetServlet extends offstage.web.MyServlet{
    
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        ResultSetArrayList currentEnrollments = 
                (ResultSetArrayList)sess.getAttribute("currentEnrollments");
        ResultSetArrayList eligibleEnrollments = 
                (ResultSetArrayList)sess.getAttribute("eligibleEnrollments");
        sess.removeAttribute( "currentEnrollments" );
        sess.removeAttribute( "famDropDown" );
        sess.removeAttribute( "eligibleEnrollments" );
        
        // Get parameters including checked boxes...
        String responsebutton = request.getParameter( "submit" );
        System.out.println("submit is: " + responsebutton );
        
        Integer entityid = this.getIntegerParameter( request, "id" );
        
        if ( responsebutton != null ){
            if ( responsebutton.compareTo("View Enrollment Status") == 0 && entityid != null ){
                redirect( request, response, "/GetEnrollmentsServlet?" +
                        "id=" + entityid
                        );
            } else if ( responsebutton.compareTo("Back") == 0 ){ 
                LinkedList urlstack = (LinkedList)sess.getAttribute("urlstack");
                String url = (String)urlstack.removeFirst();
                System.out.println("url is: " + url);
                url = (String)urlstack.removeFirst();
                System.out.println("url is: " + url);
                redirect( request, response, url );
            } else {
                redirect( request, response, "/GetFamilyStatusServlet" );
            }
        } else {
            Integer coursesetid = this.getIntegerParameter( request, "setid" );

            if ( entityid != null && coursesetid != null && 
                    currentEnrollments != null && eligibleEnrollments != null ){

                // Create a list of courses to insert by...
                LinkedList courseset = new LinkedList();

                // First search for all eligible courses with the given couresetid
                // and add to list
                Iterator i = eligibleEnrollments.listIterator();
                while ( i.hasNext() ){
                    Map ecourse = (Map)i.next();
                    Integer ecoursesetid = (Integer)ecourse.get("coursesetid");
                    if ( ecoursesetid.compareTo( coursesetid ) == 0 ){
                        courseset.addLast( (Integer)ecourse.get("courseid") );
                    }
                }

                // Second remove any courses from courseset that were previously enrolled...
                Iterator ii = currentEnrollments.listIterator();
                while ( ii.hasNext() ){
                    Map ccourse = (Map)ii.next();
                    Integer ccourseid = (Integer)ccourse.get( "courseid" );
                    if ( courseset.contains( ccourseid ) ){
                        courseset.remove( ccourseid );
                    }
                }

                // Last insert all courses defined on courseset list
                try {
                    // Insert enrollment given entityid and courseid...
                    Iterator iii = courseset.listIterator();
                    while ( iii.hasNext() ){
                        Integer courseid = (Integer)iii.next();
                        DB.insertStudentEnrollment( st, courseid, entityid );
                    }
                } catch ( SQLException e ){
                    System.out.println( e );
                }
                redirect( request, response, "/GetEnrollmentsServlet?" +
                        "id=" + entityid
                        );
            } else response.sendRedirect( request.getContextPath() + "/GetFamilyStatusServlet" );
        }
    }    
}
