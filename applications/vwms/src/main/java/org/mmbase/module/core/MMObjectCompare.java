/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import org.mmbase.util.*;

/**
 * MMObjectNodeCompare can me used as a compare function in Sortable
 * objects from org.mmbase.util (hitlisted)

 * @todo Should implement java.util.Comparator.
 * @see  org.mmbase.util.NodeComparator
 *
 */
public class MMObjectCompare implements CompareInterface {
	String compareField;

	public MMObjectCompare(String fieldName) {
		compareField = fieldName;
	}

	public int compare(Object thisOne, Object other) {
		Object object1;
		Object object2;
		int result = 0;

		object1 = ((MMObjectNode)thisOne).getValue(compareField);
		object2 = ((MMObjectNode)other).getValue(compareField);

		if(object1 instanceof String)
			result = internalStringCompare(object1, object2);
		else if(object1 instanceof Integer)
			result = internalIntCompare(object1, object2);

		return (result);
	}

	int internalIntCompare(Object thisOne, Object other) {
		return(((Integer)thisOne).intValue()-((Integer)other).intValue());
	}

	int internalStringCompare(Object thisOne, Object other) {
		return(((String)thisOne).compareTo((String)other));
	}
} 
