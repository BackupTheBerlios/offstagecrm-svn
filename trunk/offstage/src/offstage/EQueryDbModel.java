/*
 * EQueryDbModel.java
 *
 * Created on September 23, 2006, 5:04 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage;

import citibob.jschema.*;
import offstage.schema.*;

/**
 *
 * @author citibob
 */
public class EQueryDbModel extends IntKeyedDbModel
{
	
public EQueryDbModel(OffstageSchemaSet dbSchemaSet) {
	super(new SchemaBuf(dbSchemaSet.equeries), "equeryid", false);
}


}
