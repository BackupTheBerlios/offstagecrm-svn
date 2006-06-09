/*
 * GetEnrollmentsServlet.java
 * Created on April 8, 2006, 2:20 PM
 */

package offstage.web.registration;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.servlet.http.*;
import offstage.web.DB;
import offstage.web.collections.ResultSetArrayList;

/**
 *
 * @author Michael Wahl
 */
public class GetEnrollmentsServlet extends offstage.web.MyServlet{
    
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        sess.removeAttribute( "currentEnrollments" );
        sess.removeAttribute( "famDropDown" );
        sess.removeAttribute( "eligibleEnrollments" );
        
        // Get entityid from parameter
        Integer entityid = this.getIntegerParameter(request, "id");
        
        if ( entityid != null ){
            
            // Create drop down list for ViewEnrollments.jsp to view 
            // enrollments of another person in the family
            ResultSetArrayList familyList = (ResultSetArrayList)sess.getAttribute("familyList");
            int famsize = familyList.size();
            ArrayList famDropDown = new ArrayList();
            Iterator it = familyList.listIterator();
            while ( it.hasNext() ){
                Map person = (Map)it.next();
                Integer eid = (Integer)person.get("entityid");
                String fname = (String)person.get("firstname");
                HashMap temp = new HashMap();
                temp.put( "value", eid );
                temp.put( "label", fname );
                famDropDown.add(temp);
            }
            sess.setAttribute( "famDropDown", famDropDown );
            
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
                
                if ( eligibleEnrollments.size() == 0 && currentEnrollments.size() == 0 ){
                    sess.removeAttribute( "currentEnrollments" );
                    sess.removeAttribute( "famDropDown" );
                    sess.removeAttribute( "eligibleEnrollments" );
                    redirect( request, response, "/GetRegistrationsServlet?" +
                            "id=" + entityid
                            );
                } else {
                    LinkedList urlstack = (LinkedList)sess.getAttribute("urlstack");
                    urlstack.addFirst( "/GetEnrollmentsServlet?id=" + entityid );
                    redirect( request, response, "/ViewEnrollments.jsp?" +
                            "id=" + entityid
                            );
                }
            } catch ( SQLException e ){
                System.out.println( e );
            }
        } else redirect(request, response, "/GetFamilyStatusServlet");
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
