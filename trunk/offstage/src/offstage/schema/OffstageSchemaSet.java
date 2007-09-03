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
public OffstageSchemaSet(citibob.sql.SqlRunner str, DbChangeModel change, java.util.TimeZone tz)
throws SQLException
{
	map.put("courseids", courseids = new CourseidsSchema());
	map.put("donations", donations = new DonationsSchema(str, change, tz));
	map.put("flags", new FlagsSchema(str, change));
	map.put("entities", entities = new EntitiesSchema(str, change));
	map.put("events", events = new EventsSchema(str, change));
	map.put("groupids", groupids = new GroupidsSchema());
	map.put("mailingids", mailingids = new MailingidsSchema());
	map.put("mailings", mailings = new MailingsSchema(str, change));
	map.put("notes", notes = new NotesSchema(str, change, tz));
	map.put("tickets", tickets = new TicketeventsSchema(str, change));
	map.put("org", org = new OrgSchema(str, change));
	map.put("persons", persons = new PersonsSchema(str, change, tz));
	map.put("phones", phones = new PhonesSchema(str, change));
	map.put("termids", termids = new TermidsSchema(str, change, tz));
	map.put("termtypes", termtypes = new TermtypesSchema());
	map.put("classes", classes = new ClassesSchema(str, change));
	map.put("interests", interests = new InterestsSchema(str, change));
	map.put("equeries", equeries = new EQueriesSchema());
	map.put("donationids", new DonationidsSchema());
	map.put("phoneids", new PhoneidsSchema());
	map.put("meetings", new MeetingsSchema());
	map.put("enrollments", new EnrollmentsSchema(str, change, tz));
	map.put("entities_school", new EntitiesSchoolSchema(str, change));
	map.put("actrans", new ActransSchema(str, change, tz));
	map.put("cashpayments", new CashpaymentsSchema(str, change,tz));
	map.put("checkpayments", new CheckpaymentsSchema(str, change,tz));
	map.put("ccpayments", new CcpaymentsSchema(str, change,tz));
	map.put("adjpayments", new CcpaymentsSchema(str, change,tz));
	map.put("termregs", new TermregsSchema(str, change,tz));
}
	
}
