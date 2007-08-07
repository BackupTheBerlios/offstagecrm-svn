/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2007 by Robert Fischer

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
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
public OffstageSchemaSet(Statement st, DbChangeModel change, java.util.TimeZone tz)
throws SQLException
{
	map.put("courseids", courseids = new CourseidsSchema());
	map.put("donations", donations = new DonationsSchema(st, change, tz));
	map.put("flags", new FlagsSchema(st, change));
	map.put("entities", entities = new EntitiesSchema(st, change));
	map.put("events", events = new EventsSchema(st, change));
	map.put("groupids", groupids = new GroupidsSchema());
	map.put("mailingids", mailingids = new MailingidsSchema());
	map.put("mailings", mailings = new MailingsSchema(st, change));
	map.put("notes", notes = new NotesSchema(st, change, tz));
	map.put("tickets", tickets = new TicketeventsSchema(st, change));
	map.put("org", org = new OrgSchema(st, change));
	map.put("persons", persons = new PersonsSchema(st, change, tz));
	map.put("phones", phones = new PhonesSchema(st, change));
	map.put("termids", termids = new TermidsSchema(st, change, tz));
	map.put("termtypes", termtypes = new TermtypesSchema());
	map.put("classes", classes = new ClassesSchema(st, change));
	map.put("interests", interests = new InterestsSchema(st, change));
	map.put("equeries", equeries = new EQueriesSchema());
	map.put("donationids", new DonationidsSchema());
	map.put("phoneids", new PhoneidsSchema());
	map.put("meetings", new MeetingsSchema());
	map.put("enrollments", new EnrollmentsSchema(st, change, tz));
	map.put("entities_school", new EntitiesSchoolSchema(st, change));
	map.put("actrans", new ActransSchema(st, change));
	map.put("cashpayments", new CashpaymentsSchema(st, change));
	map.put("checkpayments", new CheckpaymentsSchema(st, change));
	map.put("ccpayments", new CcpaymentsSchema(st, change));
	map.put("adjpayments", new CcpaymentsSchema(st, change));
}
	
}
