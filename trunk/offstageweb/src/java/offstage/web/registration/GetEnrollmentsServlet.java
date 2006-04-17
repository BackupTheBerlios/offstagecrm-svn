/*
 * GetEnrollmentsServlet.java
 * Created on April 8, 2006, 2:20 PM
 */

package offstage.web.registration;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
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
public class GetEnrollmentsServlet extends citibob.web.DbServlet{
    
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        String _entityid = request.getParameter( "id" );
System.out.println("ENTITY ID IS: " + _entityid);
        String fname = null;
        if ( _entityid != null ){
            Integer entityid = new Integer( _entityid );
            
            // Get firstname to send as a parameter during the redirect...
            ResultSetArrayList familyList = (ResultSetArrayList)sess.getAttribute("familyList");
            Iterator iterator = familyList.listIterator();
            while( iterator.hasNext() ){
                Map person = (Map)iterator.next();
                Integer pid = (Integer)person.get("entityid");
                if ( pid.compareTo(entityid) == 0 ){
                    fname = (String)person.get("firstname");
                    break;
                }
            }
            
            // Now get all enrollments associated with entityid
            try {
                ResultSet rs = DB.getCurrentEnrollments( st, entityid );
                ResultSetArrayList currentEnrollments = new ResultSetArrayList( rs );
                sess.setAttribute( "currentEnrollments", currentEnrollments );

                rs = DB.getEligibleEnrollments( st, entityid );
                ResultSetArrayList eligibleEnrollments = new ResultSetArrayList( rs );
                sess.setAttribute( "eligibleEnrollments", eligibleEnrollments );

                // Create a distinct list of coursesetids from eligibleEnrollments
                LinkedList coursesetids = new LinkedList();
                Iterator i = eligibleEnrollments.listIterator();
                while ( i.hasNext() ){
                    Map course = (Map)i.next();
                    Integer coursesetid = (Integer)course.get("coursesetid");
                    if ( !coursesetids.contains( coursesetid ) ){
                        coursesetids.addLast( coursesetid );
                    }
                }
                
                // Iterate through the coursesetids list...
                Iterator ii = coursesetids.listIterator();
                while ( ii.hasNext() ){
                    Integer coursesetid = (Integer)ii.next();
                    // ...& get courseids associated with a coursesetid
                    LinkedList courseset = new LinkedList();
                    rs = DB.getCourseset( st, coursesetid );
                    while( rs.next() ){
                        courseset.addLast( new Integer( rs.getInt( "courseid") ) ); 
                    }
                    
                    // If current enrollments contains all courses in a courseset
                    // Then remove all courses in that courseset from eligible enrollments
                    if ( this.isFullyEnrolled( currentEnrollments, courseset ) ){
                        Iterator iii = eligibleEnrollments.listIterator();
                        while ( iii.hasNext() ){
                            Map course = (Map)iii.next();
                            if ( coursesetid.compareTo( course.get( "coursesetid" ) ) == 0 ){
                                iii.remove();
                            }
                        }
                    }
                }
            } catch ( SQLException e ){
                System.out.println( e );
            }
            redirect( request, response, "/ViewEnrollments.jsp?" +
                    "id=" + entityid +
                    "&fname=" + fname 
                    );
        } else redirect(request, response, "/FamilyStatus.jsp");
    }
    
    /**
     * Return whether each course in the courseset in on the list of currently
     * enrolled classes - if so return true
     */
    private boolean isFullyEnrolled( ResultSetArrayList currentEnrollments, 
            LinkedList courseset ){
        // Iterate through entire courseset...
        Iterator i = courseset.listIterator();
        while ( i.hasNext() ){
            // Get its courseid...
            Integer courseid = (Integer)i.next();
            boolean found = false;
            
            // Iterate through current enrollments
            Iterator ii = currentEnrollments.listIterator();
            while ( ii.hasNext() ){
                
                // Get each currently enrolled course's id
                Map currentCourse = (Map)ii.next();
                Integer currentCourseid = (Integer)currentCourse.get( "courseid" );
                
                // Compare to see if the current course id equals the one in the courseset
                if ( courseid.compareTo( currentCourseid ) == 0 ){
                    // If so mark found as true...
                    found = true;
                    // And go to the next courseid in the courseset
                    continue;
                }
            }
            
            // If courseid from courseset is not found in the list of currently
            // enrolled courses then return false - the student is not fully 
            // enrolled in all courses on the courseset
            if ( found == false ){
                return false;
            }
        }
        return true;
    }
}
