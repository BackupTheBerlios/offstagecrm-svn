/*
 * InsertChildServlet.java
 * Created on March 10, 2006, 6:37 PM
 */

package offstage.web.familystatus;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import citibob.sql.*;
import citibob.jschema.pgsql.*;
import java.util.*;
import java.text.*;
import offstage.web.*;

/**
 * Add child and its associated information to the persistent storage
 * @author Michael Wahl
 */
public class InsertChildServlet extends offstage.web.MyServlet  {
    
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        // Remove extraneous info from session
        sess.removeAttribute( "child" );
        sess.removeAttribute( "relprimarytypelist" );
        sess.removeAttribute( "genderlist" );
        
        // Get parameter and check parameter...
        String submit = request.getParameter("submit");
        if ( submit.compareTo("Submit") == 0 ){
            Logic logic = new Logic();
            String firstname = request.getParameter("firstname");
            String middlename = request.getParameter("middlename");
            String lastname = request.getParameter("lastname");
            String gender = request.getParameter("gender");
            String dob = request.getParameter("dob");
            String email = request.getParameter("email");
            String relprimarytype = request.getParameter("relprimarytype");
            
            // Format date
            java.util.Date birthdate = null;
            Integer age = null;
            try {
                birthdate = logic.formatDate(dob);
                age = logic.getAge(birthdate);
            } catch ( ParseException pe ){
                System.out.println( pe );
            }
            
            // Append bad input info to buffer
            StringBuffer buf = new StringBuffer();
            if ( firstname == null || firstname.compareTo("") == 0 ) buf.append( "Firstname, " );
            if ( lastname == null || lastname.compareTo("") == 0 ) buf.append( "Lastname, " );
            if ( gender == null || gender.compareTo("") == 0 ) buf.append("Gender, ");
            if ( birthdate == null ) buf.append("Date Of Birth, ");
            // Age must be between 19 and 0 (exclusive)
            if ( age == null || ( age.intValue() > 18 || age.intValue() < 0 ) ) buf.append( "Child's age must be less then 18, ");
//            if ( email != null && email.compareTo("") == 0 ) buf.append("Email, ");
            if ( relprimarytype == null || relprimarytype.compareTo("") == 0 ) buf.append("Child's Relation to Adult, ");
            
            if ( buf.length() > 2 ){
                // Note: 'length - 2' removes last instance of ', ' in the String.
                String badInput = buf.substring(0, buf.length()-2);
                HashMap child = new HashMap();
                child.put( "firstname", firstname );
                child.put( "middlename", middlename );
                child.put( "lastname", lastname );
                child.put( "gender", gender );
                child.put( "dob", dob );
                child.put( "email", email );
                child.put( "relprimarytype", relprimarytype );
                child.put( "badInput", badInput );
                
                // Create values for drop down gender menu so that the correct
                // sex is initially selected...
                ArrayList genderlist = new ArrayList();
                HashMap hm1 = new HashMap();
                HashMap hm2 = new HashMap();
                genderlist.add(hm1);
                genderlist.add(hm2);
                hm1.put( "value", "m" );
                hm1.put( "label", "male" );
                hm2.put( "value", "f" );
                hm2.put( "label", "female" );

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
                
                sess.setAttribute( "child", child );
                sess.setAttribute( "relprimarytypelist",  relprimarytypelist );
                sess.setAttribute( "genderlist", genderlist );
                redirect(request, response, "/familystatus/InsertChildInfoError.jsp");
            } else{
                Integer primaryentityid = (Integer)sess.getAttribute("primaryentityid");
                try {
                    DB.insertChild( st, firstname, middlename, lastname, gender,
                            birthdate, email, relprimarytype, primaryentityid );
                } catch ( SQLException e ){
                    System.out.println(e);
                }
                redirect( request, response, "/GetFamilyStatusServlet" );
            }
        } else redirect(request, response, "/GetFamilyStatusServlet");
    }
    
}
