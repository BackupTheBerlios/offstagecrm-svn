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
public class OffstageSchemaSet extends BaseSchemaSet
{

public Schema courseids;
public Schema donations;
//public Schema flags;
public Schema entities;
public Schema events;
public Schema groupids;
public Schema mailingids;
public Schema mailings;
public Schema notes;
public Schema tickets;
public Schema org;
public Schema persons;
public Schema phones;
public Schema termids;
public Schema termtypes;
public Schema classes, interests;
public Schema equeries;

/** Creates a new instance of OffstageSchema */
public OffstageSchemaSet(Statement st, DbChangeModel change)
throws SQLException
{
	map.put("courseids", courseids = new CourseidsSchema());
	map.put("donations", donations = new DonationsSchema(st, change));
	map.put("flags", new FlagsSchema(st, change));
	map.put("entities", entities = new EntitiesSchema(st, change));
	map.put("events", events = new EventsSchema(st, change));
	map.put("groupids", groupids = new GroupidsSchema());
	map.put("mailingids", mailingids = new MailingidsSchema());
	map.put("mailings", mailings = new MailingsSchema(st, change));
	map.put("notes", notes = new NotesSchema(st, change));
	map.put("tickets", tickets = new TicketeventsSchema(st, change));
	map.put("org", org = new OrgSchema(st, change));
	map.put("persons", persons = new PersonsSchema(st, change));
	map.put("phones", phones = new PhonesSchema(st, change));
	map.put("termids", termids = new TermidsSchema(st, change));
	map.put("termtypes", termtypes = new TermtypesSchema());
	map.put("classes", classes = new ClassesSchema(st, change));
	map.put("interests", interests = new InterestsSchema(st, change));
	map.put("equeries", equeries = new EQueriesSchema());
	map.put("donationids", new DonationidsSchema());
	map.put("phoneids", new PhoneidsSchema());
}
	
}
