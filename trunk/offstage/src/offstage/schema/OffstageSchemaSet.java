/*
 * OffstageSchema.java
 *
 * Created on March 19, 2006, 6:09 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.schema;

import citibob.sql.*;
import citibob.jschema.*;
import java.sql.*;

/**
 *
 * @author citibob
 */
public class OffstageSchemaSet {

public Schema courseids;
public Schema donations;
public Schema entities;
public Schema events;
public Schema groupids;
public Schema mailingids;
public Schema mailings;
public Schema notes;
public Schema org;
public Schema persons;
public Schema phones;
public Schema termids;
public Schema termtypes;


/** Creates a new instance of OffstageSchema */
public OffstageSchemaSet(Statement st, DbChangeModel change)
throws SQLException
{
	courseids = new CourseidsSchema();
	donations = new DonationsSchema(st, change);
	entities = new EntitiesSchema();
	events = new EventsSchema(st, change);
	groupids = new GroupidsSchema();
	mailingids = new MailingidsSchema();
	mailings = new MailingsSchema(st, change);
	notes = new NotesSchema(st, change);
	org = new OrgSchema();
	persons = new PersonsSchema();
	phones = new PhonesSchema(st, change);
	termids = new TermidsSchema(st, change);
	termtypes = new TermtypesSchema();
}
	
}
