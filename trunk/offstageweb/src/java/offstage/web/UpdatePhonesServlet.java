/*
 * UpdatePhonesServlet.java
 *
 * Created on March 31, 2006, 6:52 PM
 */

package offstage.web;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.*;
import offstage.web.collections.ResultSetArrayList;

/**
 *
 * @author Michael Wahl
 * @version
 */
public class UpdatePhonesServlet extends citibob.web.DbServlet {
    
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
                    System.out.println("UPDATING PHONES NOW");
                    DB.updatePhones( st, entityid, name, number );
                    
                    // If updating a work number check to see if there originally
                    // was an extension.  If there was one then you can one of
                    // two things: First if the new work number no longer has
                    // an extension then delete the extension from the db.  OR
                    // update new extension.
                    if ( name.compareTo("work") == 0 ){
                        boolean needToInsertPhoneExtension = true;
                        
                        // Get original phone list associated with entityid
                        ResultSetArrayList phones = (ResultSetArrayList)sess.getAttribute( "phones" );
                        Iterator i = phones.iterator();
                        while ( i.hasNext() ){
                            Map phone = (Map)i.next();
                            String _name = (String)phone.get("name");
                            String _number = (String)phone.get("phone");
                            // If originally had an extension and now dont have one
                            // then remove old extension from db
                            if ( _name.compareTo("extension") == 0 ){
                                if ( extension.compareTo("") == 0 ){
                                    System.out.println("DELETING EXTENSION");
                                    DB.deletePhones( st, entityid, "extension" );
                                    needToInsertPhoneExtension = false;
                                } else {
                                    System.out.println("UPDATING PHONES");
                                    DB.updatePhones( st, entityid, "extension", extension );
                                    needToInsertPhoneExtension = false;
                                }
                            }
                        }
                        // If there wasn't originally an extension and there is one now...
                        if ( needToInsertPhoneExtension && extension.compareTo("") != 0 ){
                            DB.insertPhones( st, entityid, "extension", extension ); 
                        }
                    }
                } catch ( SQLException e ){
                    System.out.println( e );
                }
                redirect(request, response, "/GetPhoneInfoServlet");
            }
            // Redirect if bad input
            redirect(request, response, "/EditPhonesError.jsp?" +
                    "message=" + message.toString() +
                    "&name=" + name +
                    "&number=" + number +
                    "&extension=" + extension
                    );
        } else redirect(request, response, "/UpdatePhones.jsp");
        
    }
    
}
