/*
Offstage CRM: Enterprise Database for Arts Organizations
This file Copyright (c) 2006 by Robert Fischer

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
/*
 * EQueryXStream.java
 *
 * Created on July 4, 2005, 9:39 AM
 */

package offstage.equery;

import com.thoughtworks.xstream.*;

/**
 * XStream class, customized for reading and writing all kinds
 * of Query subclasses in XML.
 * @author citibob
 */
public class QueryXStream extends XStream {

    /** Creates a new instance of EQueryXStream */
    public QueryXStream() {
    }

}
