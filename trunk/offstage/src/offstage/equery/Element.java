package offstage.equery;

public class Element
{
	public ColName colName;
	public String comparator;
	public Object value;

	public Element(ColName colName, String comparator, Object value)
	{
		this.colName = colName;
		this.comparator = comparator;
		this.value = value;
	}
	public Element()
	{ }
}
