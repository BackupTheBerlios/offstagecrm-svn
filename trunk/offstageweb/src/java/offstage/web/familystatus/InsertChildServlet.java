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
public class InsertChildServlet extends citibob.web.DbServlet  {
    
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
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
            
            // If bad input then redirect to ChildInfoError.jsp for correction
            if ( firstname.compareTo("") == 0 || lastname.compareTo("") == 0 || 
                 gender.compareTo("") == 0 || dob.compareTo("") == 0 || email.compareTo("") == 0 || 
                 relprimarytype.compareTo("") == 0 || !logic.isCorrectDateFormat(dob) ){
                StringBuffer buf = new StringBuffer();
                
                // Append bad input info to buffer
                if ( firstname.compareTo("") == 0 ) buf.append( "Firstname, " );
                if ( lastname.compareTo("") == 0 ) buf.append( "Lastname, " );
                if ( gender.compareTo("") == 0 ) buf.append("Gender, ");
                if ( dob.compareTo("") == 0 || !logic.isCorrectDateFormat(dob) ) buf.append("Date Of Birth, ");
                if ( email.compareTo("") == 0 ) buf.append("Email, ");
                if ( relprimarytype.compareTo("") == 0 ) buf.append("Child's Relation to Adult, ");

                // Note: 'length - 2' removes last instance of ', ' in the String.
                int length = buf.length();
                String badInput = buf.substring(0, length-2);
                
                sess.setAttribute( "firstname", firstname );
                sess.setAttribute( "middlename", middlename );
                sess.setAttribute( "lastname", lastname );
                sess.setAttribute( "gender", gender );
                sess.setAttribute( "dob", dob );
                sess.setAttribute( "email", email );
                sess.setAttribute( "relprimarytype", relprimarytype );
                sess.setAttribute( "badInput", badInput );
                redirect(request, response, "/familystatus/ChildInfoError.jsp");
            } else{
                // If age indicates under 19 then insert into db...
                if ( !logic.isAdult(dob) ){
                    Integer primaryentityid = (Integer)sess.getAttribute("primaryentityid");
                    System.out.println("Info Entered: ");
                    System.out.println("firstname " + firstname + "\nmiddlename " 
                            + middlename + "\nlastname " + lastname + "\ngender " 
                            + gender + "\ndob " + dob + "\nemail " + email +
                            "\nrelprimarytype " + relprimarytype);
                    try {
                        DB.insertChild( st, firstname, middlename, lastname, gender,
                                dob, email, relprimarytype, primaryentityid );
                    } catch ( SQLException e ){
                        System.out.println(e);
                    }
                    redirect( request, response, "/GetFamilyStatusServlet" );
                    
                // ...else age indicates over 18 so redirect to ChildIsAdult.jsp
                } else {
                    sess.setAttribute( "firstname", firstname );
                    sess.setAttribute( "middlename", middlename );
                    sess.setAttribute( "lastname", lastname );
                    sess.setAttribute( "gender", gender );
                    sess.setAttribute( "dob", dob );
                    sess.setAttribute( "email", email );
                    sess.setAttribute( "relprimarytype", relprimarytype );
                    redirect(request, response, "/familystatus/ChildIsAdult.jsp");
                }
            }
        } else redirect(request, response, "/FamilyStatus.jsp");

    }
    
}
