package offstage.equery;

public class ColName
{
	private String stable;
	private String scol;
	public String getTable() { return stable; }
	public String getSCol() { return scol; }

	public ColName(String table, String col)
	{
		stable = table;
		scol = col;
	}
	public ColName(String fullName)
	{
		int dot = fullName.indexOf('.');
		if (dot < 0) return;
		stable = fullName.substring(0,dot);
		scol = fullName.substring(dot+1);
	}
	public String toString()
		{ return (stable + "." + scol); }
	
	public boolean equals(Object o) {
		if (!(o instanceof ColName)) return false;
		ColName cc = (ColName)o;
		boolean ret = (cc.stable.equals(stable) && cc.scol.equals(scol));
//System.out.println(this + " == " + o + ": " + ret);
		return ret;
	}
	public int hashCode()
	{
		return stable.hashCode() * 31 + scol.hashCode();
	}
}
