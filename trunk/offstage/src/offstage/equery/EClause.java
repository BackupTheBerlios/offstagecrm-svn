package offstage.equery;

import java.util.*;


public class EClause
{
// Clause types
public final static int ADD = 1;
public final static int SUBTRACT = -1;

	public int type = ADD;			// ADD or SUBTRACT
	public ArrayList elements;
	public String name;

	public EClause(String name)
	{
		this.name = name;
		this.elements = new ArrayList();
	}
	public EClause()
		{ this("New Clause"); }
	public Element getElement(int i)
		{ return (Element)elements.get(i); }
public ArrayList getElements() { return elements; }
// ============================================
/** Inserts clause before clause #ix */
public void insertElement(int ix, Element c)
	{ elements.add(ix, c); }
public void removeElement(int ix)
{ elements.remove(ix); }
//public void appendElement(Element c)
//	{ elements.add(c); }

}
