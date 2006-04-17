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
public class InsertEnrollmentSetServlet extends citibob.web.DbServlet{
    
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        String fname = request.getParameter( "fname" );
        String _entityid = request.getParameter( "id" );
        String _coursesetid = request.getParameter( "setid" );
        ResultSetArrayList currentEnrollments = 
                (ResultSetArrayList)sess.getAttribute("currentEnrollments");
        ResultSetArrayList eligibleEnrollments = 
                (ResultSetArrayList)sess.getAttribute("eligibleEnrollments");

        if ( _entityid != null && _coursesetid != null && 
             currentEnrollments != null && eligibleEnrollments != null ){
            Integer entityid = new Integer( _entityid );
            Integer coursesetid = new Integer( _coursesetid );
            
            // Create a list of courses to insert by...
            LinkedList courseset = new LinkedList();
System.out.println( "COURSESET IS: " + courseset );            
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
System.out.println( "COURSESET IS: " + courseset );            
            
            // Second remove any courses from courseset that were previously enrolled...
            Iterator ii = currentEnrollments.listIterator();
            while ( ii.hasNext() ){
                Map ccourse = (Map)ii.next();
                Integer ccourseid = (Integer)ccourse.get( "courseid" );
                if ( courseset.contains( ccourseid ) ){
                    courseset.remove( ccourseid );
                }
            }
System.out.println( "COURSESET IS: " + courseset );
            // Last insert all courses defined on courseset list
            try {
                // Insert enrollment given entityid and courseid...
                Iterator iii = courseset.listIterator();
                while ( iii.hasNext() ){
                    Integer courseid = (Integer)iii.next();
                    DB.insertStudentEnrollment( st, courseid, entityid );
                }
                sess.setAttribute( "hasEnrolledDuringSession", new Boolean(true) );
            } catch ( SQLException e ){
                System.out.println( e );
            }
            redirect( request, response, "/GetEnrollmentsServlet?" +
                    "id=" + entityid +
                    "&fname=" + fname
                    );
            
        } else redirect(request, response, "/FamilyStatus.jsp");
    }    
}
