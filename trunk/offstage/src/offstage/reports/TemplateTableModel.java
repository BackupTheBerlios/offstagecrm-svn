/*
 * TemplateTableModel.java
 *
 * Created on August 11, 2007, 10:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import freemarker.template.*;
import javax.swing.table.TableModel;

/**
 * Converts a StringTableModel to a Freemarker template.
 * @author citibob
 */
public class TemplateTableModel implements TemplateSequenceModel
{
	
StringTableModel model;

/** Wraps a TableModel for JodReports. */
public TemplateTableModel(StringTableModel model)
{
	this.model = model;
}
	
public TemplateModel get(int index)
{
	return new RowModel(index);
}

public int size() 
{
	return model.getRowCount();
} 
// ============================================================
class RowModel implements TemplateHashModel {
	int row;
	public RowModel(int row) { this.row = row; }
	public TemplateModel get(java.lang.String key) {
		return new EleModel((String)model.getValueAt(row, model.findColumn(key)));
	}
	public boolean 	isEmpty() { return model.getColumnCount() == 0; }
}
// ============================================================
class EleModel implements TemplateScalarModel {
	String val;
	public EleModel(String val) { this.val = val; }
	public String getAsString() { return val; }
}
}
