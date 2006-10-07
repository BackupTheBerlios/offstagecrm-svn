package offstage.equery;

import java.util.*;


public class WizClause
{
// Clause types
public final static int ADD = 1;
public final static int SUBTRACT = -1;

	public int type = ADD;			// ADD or SUBTRACT
	public ColName colName;
	public Object value;
	public java.util.Date firstDt, nextDt;

	public WizClause(int type, ColName colName, Object value,
	java.util.Date firstDt, java.util.Date nextDt)
	{
		this.type = type;
		this.colName = colName;
		this.value = value;
		this.firstDt = firstDt;
		this.nextDt = nextDt;
	}
}
