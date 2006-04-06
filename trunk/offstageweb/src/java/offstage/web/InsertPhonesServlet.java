/*
 * InsertPhonesServlet.java
 * Created on April 1, 2006, 12:56 PM
 */

package offstage.web;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.http.*;

/**
 *
 * @author Michael Wahl
 */
public class InsertPhonesServlet extends citibob.web.DbServlet {
    public void dbRequest(HttpServletRequest request, HttpServletResponse response,
        HttpSession sess, Statement st) throws Exception
    {
        String submit = request.getParameter("submit");
        if ( submit.compareTo("Submit") == 0 ){
            Logic logic = new Logic();
            Integer entityid = (Integer)sess.getAttribute( "primaryentityid" );
            String name = request.getParameter( "name" );
            String number = request.getParameter( "number" );
            String extension = request.getParameter( "extension" );
            
            boolean correctNumFormat = logic.isCorrectPhoneNumberFormat( number );
            boolean correctExtFormat = logic.isCorrectExtFormat( extension );
            
            StringBuffer message = new StringBuffer( "Input Error: ");
            
            // Need correct phone and extension....
            if ( !correctNumFormat && !correctExtFormat 
                 && extension != null && extension.compareTo("") != 0 ) 
            {
                message.append( "Phone number format is: (xxx)xxx-xxxx and " +
                        "extension must be all digits." );
            } 
            // ...else need just new extension...
            else if ( correctNumFormat && !correctExtFormat 
                      && extension != null && extension.compareTo("") != 0 ) 
            {
                message.append( "Extension must be all digits." );
            } 
            // ...else need just new phone if number either does not have an
            // extension or the extension is in the correct format
            else if ( !correctNumFormat && ( extension == null || correctExtFormat ) )
            {
                message.append( "Phone number format is: (xxx)xxx-xxxx." );
            } 
            // ...else both numbers are ok...so update DB
            else
            {
                try {
                    System.out.println("INSERTING PHONES NOW");
                    DB.insertPhones( st, entityid, name, number );
                    
                    // If not a work number, extension will be null
                    // If work number, it could not require an extension
                    // So if not null(not work) and requires extension
                    // then insert extension.
                    if ( extension != null && extension.compareTo("") != 0 ){
                        DB.insertPhones( st, entityid, "extension", extension );
                    }
                } catch ( SQLException e ){
                    System.out.println( e );
                }
                redirect(request, response, "/GetPhoneInfoServlet");
            }
            // Redirect if bad input
            redirect(request, response, "/InsertPhonesError.jsp?" +
                    "message=" + message.toString() +
                    "&name=" + name +
                    "&number=" + number +
                    "&extension=" + extension
                    );

        } else redirect(request, response, "/GetPhoneInfoServlet");
    }
}
