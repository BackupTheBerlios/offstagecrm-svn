/*
 * UpdateAdultInfoServlet.java
 * Created on March 17, 2006, 10:14 PM
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
 * Update information associated with an adult identifiable by entityid
 * @author Michael Wahl
 */
public class UpdateAdultInfoServlet extends offstage.web.MyServlet  {
    
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        // Remove superfluous info from session
        sess.removeAttribute( "badInput" );
        sess.removeAttribute( "person" );
        sess.removeAttribute( "account" );
        sess.removeAttribute( "genderlist" );
        
        // Get parameters...
        String submit = request.getParameter("submit");
        Integer entityid = this.getIntegerParameter( request, "id" );
        System.out.println( "entityid is: " + entityid);

        // Check for illegal parameters
        if ( submit != null && submit.compareTo("Submit") == 0 && entityid != null ){
            Logic logic = new Logic();
            String firstname = request.getParameter("firstname");
            String middlename = request.getParameter("middlename");
            String lastname = request.getParameter("lastname");
            String gender = request.getParameter("gender");
            String dob = request.getParameter("dob");
            String email = request.getParameter("email");
            String occupation = request.getParameter("occupation");
            String title = request.getParameter("title");
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            
            // Format date and get age
            java.util.Date birthdate = null;
            Integer age = null;
            try {
                birthdate = logic.formatDate(dob);
                age = logic.getAge(birthdate);
            } catch ( ParseException pe ){
                System.out.println( pe );
            }
            
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
            sess.setAttribute( "genderlist", genderlist );

System.out.println("birthdate is: " + birthdate);
System.out.println("age is: " + age);
            // Append bad input info to buffer
            StringBuffer buf = new StringBuffer();
            if ( firstname == null || firstname.compareTo("") == 0  ) buf.append( "Firstname, " );
            if ( lastname == null || lastname.compareTo("") == 0 ) buf.append( "Lastname, " );
            if ( birthdate == null ) buf.append("Date Of Birth, ");
            if ( age == null || age.intValue() < 19 ) buf.append("Person's age must be over 18, ");
            if ( username == null || username.compareTo("") == 0 ) buf.append( "Username, " );
            if ( password == null || password.compareTo("") == 0 ) buf.append( "Password, " );
            
            // If buffer is not empty then redirect to correct input
            if ( buf.length() > 2 ){
                // Note: 'length - 2' removes last instance of ', ' in the String.
                String badInput = buf.substring(0, buf.length()-2);
                int length = buf.length();
                HashMap adult = new HashMap();
                HashMap account = new HashMap();
                adult.put( "entityid", entityid );
                adult.put( "firstname", (String)request.getParameter("firstname") );
                adult.put( "middlename", (String)request.getParameter("middlename") );
                adult.put( "lastname", (String)request.getParameter("lastname") );
                adult.put( "gender", (String)request.getParameter("gender") );
                adult.put( "dob", (String)request.getParameter("dob") );
                adult.put( "email",  (String)request.getParameter("email") );
                adult.put( "occupation", (String)request.getParameter("occupation") );
                adult.put( "title", (String)request.getParameter("title") );
                adult.put( "badInput", badInput );
                
                account.put( "username", (String)request.getParameter("username") );
                account.put( "password", (String)request.getParameter("password") );
                sess.setAttribute( "person", adult );
                sess.setAttribute( "account", account );
                redirect(request, response, "/familystatus/UpdateAdultInfoError.jsp?id=" + entityid );
            } else{
                try {
                    DB.updateAdult( st, 
                            entityid, firstname, middlename, lastname, gender,
                            birthdate, email, occupation, title
                            );
                    DB.updateAccountInfo( st,
                            entityid, username, password
                            );
                    
                    System.out.println("UPDATED!");
                } catch ( SQLException e ){
                    System.out.println(e);
                }
                redirect( request, response, "/GetFamilyStatusServlet" );
            }
        } else redirect(request, response, "/GetFamilyStatusServlet" );
    }
}