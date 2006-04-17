/*
 * AddChildFilter.java
 * Created on April 14, 2006, 4:45 PM
 */

package offstage.web;
import citibob.web.DbFilter;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.*;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import offstage.web.collections.ResultSetArrayList;

/**
 * If adding a child during the enrollment process, then will have to filter
 * for specific URI requests which have been mapped to this Servlet
 * in the web.xml file.
 * @author  Michael Wahl
 */
public class AddChildFilter extends DbFilter {
    
    public AddChildFilter() {
		this.pool = new offstage.db.DBConnPool();
    }
    
    /**
     *
     * @param request The servlet request we are processing
     * @param result The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        // Call super's doFilter to get a Statement and Connection in ServletRequest
        // AND also to call chain.doFilter()
        //super.doFilter( request, response, chain );
        
        // IF ChildInfo.jsp requested and if there is a parameter 'message'
        // THEN ChildInfo.jsp has been called during the enrollment process ->
        // SO record the message
    	HttpServletRequest hr = (HttpServletRequest)request;
        HttpSession sess = hr.getSession();
        String uri = hr.getRequestURI();
        
        if( uri.indexOf("/familystatus/ChildInfo.jsp") != -1 ){
System.out.println("TESTING FOR MESSAGE IN CHILDINFO.JSP REQUEST PARAMETER");
            String message = hr.getParameter("message");
            if ( message != null && message.compareTo("enrollmentprocess") == 0 ){
System.out.println("SETTING EMESSAGE IN SESSION");
                sess.setAttribute( "emessage", message );
                sess.setAttribute( "eid", hr.getParameter("id") );
            }
        }
        
        // If asking for FamilyStatus then check to see if there is an 'emessage'
        // in the session.  This 'emessage' was set if '/familystatus/ChildInfo.jsp'
        // was requested and if there was a parameter called 'emessage' that
        // said 'enrollmentprocess'.  This means that a person choose to add a 
        // child during the enrollment process.
        else if ( uri.indexOf("FamilyStatus.jsp") != -1 )
        {
            String emessage = (String)sess.getAttribute("emessage");
            
            // IF asked for FamilyStatus.jsp AND there is an emessage,  
            // THEN that means that the user has choosen to cancel adding a new
            // child during the enrollment process so we should redirect back
            // to ViewEnrollments.jsp with out any changes.
            if( emessage != null && emessage.compareTo("enrollmentprocess") == 0 )
            {
System.out.println("CANCEL ADD CHILD SO...RETRIEVE EMESSAGE REDIRECT TO VIEW ENROLLMENTS");
                String id = (String)sess.getAttribute("eid");
                String fname = (String)sess.getAttribute("efname");
                sess.removeAttribute("emessage");
                sess.removeAttribute("eid");
                ((HttpServletResponse)response).sendRedirect( 
                        ((HttpServletRequest)request).getContextPath() + 
                        "/GetEnrollmentsServlet?" +
                        "id=" + id
                        );
            }
        } 
        else if ( uri.indexOf("GetFamilyStatusServlet") != -1 )
        {
            String emessage = (String)sess.getAttribute("emessage");
            
            // If asked for GetFamilyStatusServlet AND there is an emessage,  
            // THEN that means that the user has inserted a new
            // child during the enrollment process so we should 
            // update familyList and then redirect back
            // to GetEnrollmentsServlet to retrieve enrollment information of new
            // child.
            if( emessage != null && emessage.compareTo("enrollmentprocess") == 0 )
            {
System.out.println("CHILD ADDED SO...RESETTING FAMILYLIST...");
                Connection con = null;
                Statement st = null;
                // First update familyList...
                Integer primaryentityid = (Integer)sess.getAttribute("primaryentityid");
                try {
                    con = pool.checkout();
                    st = con.createStatement();
                    ResultSet rs = DB.getFamily( st, primaryentityid );
                    ResultSetArrayList newfamilyList = new ResultSetArrayList( rs );
                    
                    ResultSetArrayList familyList = (ResultSetArrayList)sess.getAttribute("familyList");

                    // Now compare old list with new list
                    Integer entityid = null;
                    String fname = null;
                    Iterator newi = newfamilyList.listIterator();
                    while ( newi.hasNext() ){
                        Map newperson = (Map)newi.next();
                        entityid = (Integer)newperson.get("entityid");
                        fname = (String)newperson.get("firstname");
                        Integer newentityid = (Integer)newperson.get("entityid");
                        
                        // Search through the entire old list for each identityid
                        // on the new list.
                        boolean entityfound = false;
                        Iterator oldi = familyList.listIterator();
                        while ( oldi.hasNext() && !entityfound ){
                            Map oldperson = (Map)oldi.next();
                            Integer oldentityid = (Integer)oldperson.get("entityid");
                            if( newentityid.compareTo(oldentityid) == 0 ){
                                entityfound = true;
                            }
                        }
                        
                        // If entityid was not found on old list, then this is the
                        // child that was added and we've found the entityid to 
                        // pass as a parameter to GetEnrollmentsServlet
                        if ( entityfound == false ){
                            break;
                        }
                    }
                    
                    // Put new list into session for later use
                    sess.setAttribute( "familyList", newfamilyList );
                    sess.removeAttribute("emessage");
                    sess.removeAttribute("eid");
System.out.println("CHILD ADDED SO...AND NOW REDIRECTING TO VIEW ENROLLMENTS");
                    // Then redirect to Get EnrollmentServlet
                    ((HttpServletResponse)response).sendRedirect( 
                            ((HttpServletRequest)request).getContextPath() + 
                            "/GetEnrollmentsServlet?" +
                            "id=" + entityid
                            );
                } catch ( SQLException e ){
                    System.out.println( e );
                    // If a problem redirect to GetFamilyStatusServlet
                    ((HttpServletResponse)response).sendRedirect( 
                            ((HttpServletRequest)request).getContextPath() + 
                            "/GetFamilyStatusServlet"
                            );
            	} finally {
                    try {
                        st.close();
                    } catch(Throwable e) {
                        System.out.println( e );
                    }
            		try {
            			pool.checkin(con);
            		} catch(Throwable e) {
                        System.out.println( e );
                    }
            	}
            }
        }
        super.doFilter( request, response, chain );
    }
}