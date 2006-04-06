/*
 * UpdateAdultInfoServlet.java
 * Created on March 17, 2006, 10:14 PM
 */

package offstage.web;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.*;
import citibob.sql.*;
import citibob.jschema.pgsql.*;
import java.util.*;
import java.text.*;
/**
 * Update information associated with an adult identifiable by entityid
 * @author Michael Wahl
 */
public class UpdateAdultInfoServlet extends citibob.web.DbServlet  {
    
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        String submit = request.getParameter("submit");
        if ( submit.compareTo("Submit") == 0 ){
            Logic logic = new Logic();
            Hashtable adult = new Hashtable();
            adult.put( "entityid", new Integer( (String)request.getParameter("id") ) );
            adult.put( "firstname", (String)request.getParameter("firstname") );
            adult.put( "middlename", (String)request.getParameter("middlename") );
            adult.put( "lastname", (String)request.getParameter("lastname") );
            adult.put( "gender", (String)request.getParameter("gender") );
            adult.put( "dob", (String)request.getParameter("dob") );
            adult.put( "email",  (String)request.getParameter("email") );
            
            adult.put( "occupation", (String)request.getParameter("occupation") );
            adult.put( "title", (String)request.getParameter("title") );
            
            StringBuffer buf = new StringBuffer();
                
            // Append bad input info to buffer
            if ( adult.get("firstname") == null ) buf.append( "Firstname, " );
            if ( adult.get("lastname") == null ) buf.append( "Lastname, " );
            if ( adult.get("occupation") == null ) buf.append( "Occupation, " );
            if ( adult.get("title") == null ) buf.append( "Job Title, " );
            if ( adult.get("dob") == null || 
                 !logic.isCorrectDateFormat( (String)adult.get("dob") ) ||
                 !logic.isAdult( (String)adult.get("dob") ) )
            {
                buf.append("Date Of Birth, ");
            }
            if ( adult.get("email") == null ) buf.append("Email, ");

            // If put bad input in buffer then redirect to fix the input
            if ( buf.length() > 2 ){
                // Note: 'length - 2' removes last instance of ', ' in the String.
                int length = buf.length();
                String badInput = buf.substring(0, length-2);
                sess.setAttribute( "badInput", badInput );
                sess.setAttribute( "adult", adult );
                redirect(request, response, "/familystatus/UpdateAdultInfoError.jsp?id=" + (Integer)adult.get( "entityid" ) );
            } else{
                try {
                    DB.updateAdult( st, 
                            (Integer)adult.get("entityid" ),
                            (String)adult.get( "firstname" ),
                            (String)adult.get( "middlename" ),
                            (String)adult.get( "lastname" ),
                            (String)adult.get( "gender" ),
                            (String)adult.get( "dob" ),
                            (String)adult.get( "email" ), 
                            (String)adult.get( "occupation" ), 
                            (String)adult.get( "title" )
                            );
                    System.out.println("UPDATED!");
                } catch ( SQLException e ){
                    System.out.println(e);
                }
                redirect( request, response, "/FamilyStatus.jsp" );
            }
        } else redirect(request, response, "/FamilyStatus.jsp");
    }
}