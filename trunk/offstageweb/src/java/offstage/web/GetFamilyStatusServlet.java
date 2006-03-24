/*
 * GetFamilyStatusServlet.java
 * Created on March 17, 2006, 2:41 PM
 */

package offstage.web;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.*;
import citibob.sql.*;
import citibob.jschema.pgsql.*;
/**
 * 
 * @author Michael Wahl
 */
public class GetFamilyStatusServlet extends citibob.web.DbServlet {
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        
    }
}
