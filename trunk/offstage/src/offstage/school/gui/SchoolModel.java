/*
 * SchoolModel.java
 *
 * Created on December 9, 2007, 5:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.school.gui;

import citibob.app.App;
import citibob.jschema.IntKeyedDbModel;
import citibob.jschema.IntsKeyedDbModel;
import citibob.jschema.SchemaBufRowModel;

/**
 *
 * @author citibob
 */
public class SchoolModel extends SchoolModelMVC
{

int termID;

public StudentDbModel studentDm;
	public SchemaBufRowModel studentRm;
	public SchemaBufRowModel schoolRm;
public IntsKeyedDbModel termregsDm;
	public SchemaBufRowModel termregsRm;
public PayerDbModel payerDm;
//public HouseholdDbModel householdDm;
public ParentDbModel parentDm;
public ParentDbModel parent2Dm;

// -------------------------------------------------------------

public SchoolModel(App fapp)
{
	studentDm = new StudentDbModel(fapp);
	payerDm = new PayerDbModel(fapp);
//	householdDm = new HouseholdDbModel(fapp);
	parentDm = new ParentDbModel(fapp);
	parent2Dm = new ParentDbModel(fapp);
	termregsDm = new IntsKeyedDbModel(fapp.getSchema("termregs"), new String[] {"groupid", "entityid"}, true);

}

public void setTermID(Integer TermID)
	{ setTermID(TermID == null ? -1 : TermID.intValue()); }
public void setTermID(int newTermID)
{
	int oldTermID = termID;
	this.termID = newTermID;
	if (oldTermID != termID) fireTermIDChanged(oldTermID, termID);
}
public int getTermID()
	{ return termID; }

}
