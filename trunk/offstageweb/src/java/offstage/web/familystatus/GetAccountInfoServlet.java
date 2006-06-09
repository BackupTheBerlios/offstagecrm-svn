/*
 * GetAccountInfoServlet.java
 * Created on April 1, 2006, 2:59 PM
 */

package offstage.web.familystatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.*;
import offstage.web.collections.ResultSetHashMap;
import offstage.web.*;
import offstage.web.collections.ResultSetArrayList;


/**
 * If adult and primary entity, then add account info (username, password) to
 * session and redirect to view Adult info, else if adult and not primary entity
 * then redirect to view Adult info, else redirect to view child info.
 * @author Michael Wahl
 */
public class GetAccountInfoServlet extends offstage.web.MyServlet {
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        sess.removeAttribute("account");
        sess.removeAttribute("person");
        sess.removeAttribute("genderlist");
        sess.removeAttribute("relprimarytypelist");

        Integer primaryentityid = (Integer)sess.getAttribute( "primaryentityid" );
        Integer entityid = this.getIntegerParameter(request, "id");
        
        // Test to see if parameter is null...if not null then process info
        if ( entityid != null && primaryentityid != null )
        {
            // Look for person associated with entityid in familylist
            ResultSetArrayList familyList = (ResultSetArrayList)sess.getAttribute("familyList");
            Map person = familyList.get( "entityid", entityid );
            
            // If person with entityid not found on family list then redirect
            if ( person == null ){
                redirect(request, response, "/GetFamilyStatusServlet" );
            } 
            else 
            {
                sess.setAttribute( "person", person );
                
                // Create values for drop down gender menu so that the correct
                // sex is initially selected...
                String gender = (String)person.get("gender");
                ArrayList genderlist = new ArrayList();
                HashMap hm1 = new HashMap();
                HashMap hm2 = new HashMap();
                genderlist.add(hm1);
                genderlist.add(hm2);
                hm1.put( "value", "m" );
                hm1.put( "label", "male" );
                hm2.put( "value", "f" );
                hm2.put( "label", "female" );
                
                sess.setAttribute( "genderlist", genderlist );

                Integer adultid = (Integer)person.get("adultid");
                // If person we are looking at is an adult then show Adult Info...
                if ( adultid.compareTo( entityid ) == 0 )
                {
                    try {
                        // Get username and password for editing
                        ResultSet rs = DB.getAccountInfo( st, primaryentityid );
                        ResultSetHashMap account = new ResultSetHashMap( rs );
                        sess.setAttribute( "account", account );
                    } catch ( SQLException e ){
                        System.out.println( e );   
                    }
                    // Redirect to view adult info
                    System.out.println("REDIRECTING TO VIEW ADULT");
                    redirect(request, response, "/familystatus/UpdateAdultInfo.jsp?id=" + entityid );
                // Else have to edit child info
                } else {
                    String relprimarytype = (String)person.get("name");
                    ArrayList relprimarytypelist = new ArrayList();
                    HashMap m1 = new HashMap();
                    HashMap m2 = new HashMap();
                    HashMap m3 = new HashMap();
                    HashMap m4 = new HashMap();
                    HashMap m5 = new HashMap();
                    HashMap m6 = new HashMap();
                    relprimarytypelist.add(m1);
                    relprimarytypelist.add(m2);
                    relprimarytypelist.add(m3);
                    relprimarytypelist.add(m4);
                    relprimarytypelist.add(m5);
                    relprimarytypelist.add(m6);
                    m1.put( "value", "child" );
                    m1.put( "label", "Son/Daughter" );
                    m2.put( "value", "grandchild" );
                    m2.put( "label", "Grandson/Granddaughter" );
                    m3.put( "value", "sibling" );
                    m3.put( "label", "Brother/Sister" );
                    m4.put( "value", "cousin" );
                    m4.put( "label", "Cousin" );
                    m5.put( "value", "niece" );
                    m5.put( "label", "Niece" );
                    m6.put( "value", "nephew" );
                    m6.put( "label", "Nephew" );
                    
                    sess.setAttribute("relprimarytypelist",  relprimarytypelist);
                    System.out.println("REDIRECTING TO VIEW CHILD");
                    redirect(request, response, "/familystatus/UpdateChildInfo.jsp?id=" + entityid );
                } 
            }
        } 
        else redirect(request, response, "/FamilyStatus.jsp");
    }
}
