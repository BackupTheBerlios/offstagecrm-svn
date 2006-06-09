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
import java.util.LinkedList;
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
System.out.println("IN ADDCHILDFILTER, uri is: " + uri );
        
        if( uri.indexOf("/familystatus/InsertChildInfo.jsp") != -1 ){
System.out.println("TESTING FOR MESSAGE IN CHILDINFO.JSP REQUEST PARAMETER");
            String message = hr.getParameter("message");
            if ( message != null && message.compareTo("enrollmentprocess") == 0 ){
System.out.println("SETTING EMESSAGE IN SESSION");
                sess.setAttribute( "emessage", message );
                sess.setAttribute( "eid", hr.getParameter("id") );
            }
        }
        
        if ( uri.indexOf("GetFamilyStatusServlet") != -1 )
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
                    boolean entityfound = false;
                    Integer entityid = null;
                    Iterator newi = newfamilyList.listIterator();
                    while ( newi.hasNext() ){
                        entityfound = false;
                        Map newperson = (Map)newi.next();
                        entityid = (Integer)newperson.get("entityid");
                        Integer newentityid = (Integer)newperson.get("entityid");
                        
                        // Search through the entire old list for each identityid
                        // on the new list.
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
                    
                    // If the two lists are the same then assume that adding child
                    // process was cancelled so we want to return to original 
                    // entity enrollment screen
                    if (entityfound == true){
                        String _entityid = (String)sess.getAttribute("eid"); 
                        try {
                            entityid = new Integer(_entityid);
                        } catch (Throwable t){ 
                            System.out.println(t);
                        }
                    }
                    
                    // Put new list into session for later use
                    sess.setAttribute( "familyList", newfamilyList );
                    // Remove extraneous attributes
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