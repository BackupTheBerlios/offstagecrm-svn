/*
 * InsertPaymentPlansServlet.java
 * Created on May 25, 2006, 9:48 PM
 */

package offstage.web.registration;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.servlet.http.*;
import offstage.web.DB;
import offstage.web.Logic;
import offstage.web.collections.ResultSetArrayList;
/**
 * Using inputs obtained from the user - paymenttypeid, cctype, ccnumber, 
 * cc expiration, name on credit card, and payment plan type - 
 * insert new payment plan and then update enrollments to show the correct 
 * pplanid.  If any of these inputs are not valid then redirect to error screen to get
 * correct input.
 * @author Michael Wahl
 */
public class InsertPaymentPlanServlet extends offstage.web.MyServlet {
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        // Get parameters including...
        String responsebutton = request.getParameter( "submit" );
        Integer primaryentityid = (Integer)sess.getAttribute("primaryentityid");
        ResultSetArrayList enrollmentsWOpplan = (ResultSetArrayList)sess.getAttribute( "enrollmentsWOpplan" );
        ResultSetArrayList paymenttypeidmenu = (ResultSetArrayList)sess.getAttribute( "paymenttypeidmenu" );
        ResultSetArrayList pplantypeidsmenu = (ResultSetArrayList)sess.getAttribute( "pplantypeidsmenu" );
        ArrayList cctypemenu = (ArrayList)sess.getAttribute("cctypemenu");
        
        if ( responsebutton != null && responsebutton.compareTo("Create New Payment Plan") == 0 
                && primaryentityid != null && enrollmentsWOpplan != null 
                && paymenttypeidmenu != null && pplantypeidsmenu != null 
                && cctypemenu != null ){
            Logic logic = new Logic();
            
            Integer paymenttypeid = this.getIntegerParameter( request, "paymenttypeid");
            String cctypeid = request.getParameter( "cctypeid" );
            String ccnumber = request.getParameter( "ccnumber");
            Integer month = this.getIntegerParameter( request, "month" );
            Integer year = this.getIntegerParameter( request, "year" );
            String name = request.getParameter("name");
            Integer pplantypeid = this.getIntegerParameter( request, "pplantypeid" );
            
            Date invaliddate = null;
            try {
                // Invaliddate must be the first day of the next month after
                // the expiration date on the credit card...
                invaliddate  = logic.formatDate( month + "/" + year, "MM/yyyy" );
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis( invaliddate.getTime() );
                c.roll( Calendar.MONTH, true );
                c.set( Calendar.DATE, 1 );
                invaliddate.setTime(c.getTimeInMillis());
            } catch (Throwable t){
                System.out.println(t);
            }

            // Need both the termid and the nextdate of the enrollment that is 
            // the farthest into the future.  This will be the date we have to 
            // be sure the credit card doesn't expire by if the payment plan is 
            // quarterly.
            Map farthestenrollment = null;
            Integer termid = null;
            Date fdate = null;
            Iterator i = enrollmentsWOpplan.listIterator();
            while ( i.hasNext() ){
                Map enrollment = (Map)i.next();
                System.out.println( "enrollment is: " + enrollment );
                if ( farthestenrollment == null ) farthestenrollment = enrollment;
                else {
                    fdate = (java.util.Date)farthestenrollment.get("nextdate");
                    Date _fdate = (java.util.Date)enrollment.get("nextdate");
                    if ( _fdate.compareTo(fdate) > 0 ) farthestenrollment = enrollment;
                }
            }
            termid = (Integer)farthestenrollment.get("termid");
            fdate = (java.util.Date)farthestenrollment.get("nextdate");
            
            // Append bad input info to buffer
            StringBuffer buf = new StringBuffer();
            
            // Get expected paymenttypeid of ccpayments...
            // Search for a map where 'table' == 'ccpayments'
            // If a map is returned, THEN get its value
            Integer epaymenttypeid = null;
            Map paymenttype = paymenttypeidmenu.get( "table", "ccpayments" );
            if ( paymenttype != null ) epaymenttypeid = (Integer)paymenttype.get("value");
            
            // Get expected payment plan type of ccpayments...
            // Search for a map where 'type' == 'infull'
            // IF a map is returned THEN get its pplanttypeid value
            Integer infullpplantypeid = null;
            Map pplantype = pplantypeidsmenu.get( "type", "infull" );
            if ( pplantype != null ) infullpplantypeid = (Integer)pplantype.get("value");
            
            // IF have expected paymenttypeid of ccpayments and the payment type
            // entered by user and IF they match, 
            // AND also IF infull plan type id is not null and the plantypeid is not null
            // AND also IF invaliddate is not null and fdate is not null
            // THEN validate the parameters needed for a credit card payment plan
            if ( paymenttypeid != null && epaymenttypeid != null && 
                    paymenttypeid.compareTo(epaymenttypeid) == 0 &&
                    infullpplantypeid != null && pplantypeid != null &&
                    invaliddate != null && fdate != null ){
                // Check ccnumber...
                if ( ccnumber == null ) buf.append( "Missing Credit Card Number, " );
                else if ( ccnumber.toString().length() != 16 ) buf.append( "Reenter Credit Card Number, " );
                
                // Check name on credit card
                if ( name == null || name.compareTo("") == 0 ) 
                    buf.append( "Missing Name On Credit Card, " );
                
                // If paying in full THEN the credit expiration date just can't be expired.
                if ( infullpplantypeid.compareTo(pplantypeid) == 0 ){
                    if ( invaliddate.compareTo(new java.util.Date(System.currentTimeMillis())) < 0 ){
                        buf.append( "Credit Card Expiration Date Is Expired, ");
                    }
                // ELSE IF paying in installments THEN the expiration
                // date has to be beyond the 'nextdate' of the farthest term                
                } else if ( invaliddate.compareTo(fdate) < 0 ) 
                    buf.append( "Credit Card Expiration Date Has To Be At Least " +
                            logic.getSimpleDate(fdate, "MM/yyyy") + ", ");
            }

            // If buffer is not empty then redirect to correct input
            if ( buf.length() > 2 ){
                // Note: 'length - 2' removes last instance of ', ' in the String.
                String badInput = buf.substring(0, buf.length()-2);
                
                HashMap paymentplan = new HashMap();
                paymentplan.put( "cctypeid", cctypeid );
                paymentplan.put( "pplantypeid", pplantypeid );
                paymentplan.put( "paymenttypeid", paymenttypeid );
                paymentplan.put( "month", month );
                paymentplan.put( "year", year );
                paymentplan.put( "ccnumber", ccnumber );
                paymentplan.put( "name", name );
                paymentplan.put( "badInput", badInput );
                sess.setAttribute( "paymentplan", paymentplan );
                response.sendRedirect( request.getContextPath() + 
                        "/ViewPaymentPlanError.jsp"
                        );
            } else {
                sess.removeAttribute( "currentPP" );
                sess.removeAttribute( "ppinvoices" );
                sess.removeAttribute( "enrollmentsWOpplan" );
                sess.removeAttribute( "paymenttypeidmenu" );
                sess.removeAttribute( "pplantypeidsmenu" );
                sess.removeAttribute( "cctypemenu" );
                try {
                    Integer pplanid = DB.insertPaymentPlan(st, primaryentityid, 
                            cctypeid, ccnumber, 
                            invaliddate, name, pplantypeid, termid, 
                            paymenttypeid );
                    System.out.println("pplanid is: " + pplanid );
                    
                    if ( pplanid != null ){
                        Iterator ii = enrollmentsWOpplan.listIterator();
                        while ( ii.hasNext() ){
                            Map enrollment = (Map)ii.next();
                            Integer entityid = (Integer)enrollment.get("entityid");
                            Integer courseid = (Integer)enrollment.get("courseid");
                            DB.updateEnrollment(st, courseid, entityid, pplanid);
                        }
                    }
                } catch ( SQLException e ){
                    System.out.println( e );
                }
            }
        } else if ( responsebutton != null && responsebutton.compareTo("Back") == 0 ){
            sess.removeAttribute( "currentPP" );
            sess.removeAttribute( "ppinvoices" );
            sess.removeAttribute( "enrollmentsWOpplan" );
            sess.removeAttribute( "paymenttypeidmenu" );
            sess.removeAttribute( "pplantypeidsmenu" );
            sess.removeAttribute( "cctypemenu" );

            LinkedList urlstack = (LinkedList)sess.getAttribute("urlstack");
            String url = (String)urlstack.removeFirst();
            url = (String)urlstack.removeFirst();
            redirect( request, response, url );
        } else response.sendRedirect( request.getContextPath() + "/GetFamilyStatusServlet" );
    }
}