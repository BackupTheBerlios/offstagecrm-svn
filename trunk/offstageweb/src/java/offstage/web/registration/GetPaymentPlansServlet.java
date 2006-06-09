/*
 * GetPaymentPlansServlet.java
 *
 * Created on May 18, 2006, 4:25 PM
 */

package offstage.web.registration;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
 * @version
 */
public class GetPaymentPlansServlet extends offstage.web.MyServlet {
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        sess.removeAttribute( "currentPP" );
        sess.removeAttribute( "ppinvoices" );
        sess.removeAttribute( "enrollmentsWOpplan" );
        sess.removeAttribute( "paymenttypeidmenu" );
        sess.removeAttribute( "pplantypeidsmenu" );
        sess.removeAttribute( "cctypemenu" );
        
        Integer primaryentityid = (Integer)sess.getAttribute("primaryentityid");
        try {
            // Get current payment plans
            ResultSet rs = DB.getCurrentPaymentPlans(st, primaryentityid);
            ResultSetArrayList currentPP = new ResultSetArrayList(rs);
            
            // Get invoices associated with each payment plan
            HashMap ppinvoices = new HashMap();
            Iterator i = currentPP.listIterator();
            while ( i.hasNext() ){
                Map pp = (Map)i.next();
                Integer pplanid = (Integer)pp.get("pplanid");
                rs = DB.getPPInvoices(st, pplanid);
                ResultSetArrayList invoices = new ResultSetArrayList(rs);

                // put invoices on hashmap using pplanid as the key
                ppinvoices.put(pplanid, invoices);
            }
            System.out.println( currentPP );
            System.out.println( ppinvoices );
            
            rs = DB.getCurrentEnrollments(st, primaryentityid);
            ResultSetArrayList enrollmentsWOpplan = new ResultSetArrayList(rs);
            
            // Need to remove enrollments that already have a payment plan in effect
            i = enrollmentsWOpplan.listIterator();
            while ( i.hasNext() ){
                Map enrollment = (Map)i.next();
                Integer pplanid = (Integer)enrollment.get("pplanid");
                if ( pplanid != null ) i.remove();
            }
            System.out.println( "enrollments are: " + enrollmentsWOpplan );
            
            rs = DB.getPPlanTypeidsMenu(st);
            ResultSetArrayList pplantypeidsmenu = new ResultSetArrayList(rs);
            
            rs = DB.getPaymentTypeidsMenu(st);
            ResultSetArrayList paymenttypeidmenu = new ResultSetArrayList(rs);
            
            ArrayList cctypemenu = new ArrayList();
            HashMap item1 = new HashMap();
            item1.put( "value", "m" );
            item1.put( "label", "Mastercard" );
            HashMap item2 = new HashMap();
            item2.put( "value", "v" );
            item2.put( "label", "Visa" );
            cctypemenu.add(item1);
            cctypemenu.add(item2);
            
            sess.setAttribute( "currentPP", currentPP );
            sess.setAttribute( "ppinvoices", ppinvoices );
            sess.setAttribute( "enrollmentsWOpplan", enrollmentsWOpplan );
            sess.setAttribute( "paymenttypeidmenu", paymenttypeidmenu );
            sess.setAttribute( "pplantypeidsmenu", pplantypeidsmenu );
            sess.setAttribute( "cctypemenu", cctypemenu );
        } catch(SQLException e) {
            System.out.println(e);
        }
        LinkedList urlstack = (LinkedList)sess.getAttribute("urlstack");
        urlstack.addFirst( "/GetPaymentPlansServlet" );
        
        response.sendRedirect( request.getContextPath() + "/ViewPaymentPlans.jsp" );
    }
}
