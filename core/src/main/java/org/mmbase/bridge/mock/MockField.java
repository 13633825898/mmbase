/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.mock;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.datatypes.DataType;

/**

 * @author  Michiel Meeuwissen
 * @version $Id: MapNode.java 36154 2009-06-18 22:04:40Z michiel $
 * @since   MMBase-1.9.2
 * @todo    EXPERIMENTAL
 */

public class MockField extends DataTypeField  {

    private int searchPosition = -1;
    private int listPosition = -1;
    private int editPosition = 1;
    private int storagePosition = 1;

    MockField(NodeManager nm, Field f) {
        super(nm, f);
        storagePosition = f.isVirtual() ? -1 : 1;
    }

    MockField(Field f, DataType dt) {
        super(f, dt);
        storagePosition = f.isVirtual() ? -1 : 1;
    }

    MockField(String n, NodeManager nm, DataType dt) {
        super(n, nm, dt);
        setState(STATE_PERSISTENT);
        storagePosition = n.startsWith("_") ? -1 : 1;
    }


    @Override
    public int getSearchPosition() {
        return searchPosition;
    }

    @Override
    public int getListPosition() {
        return listPosition;
    }

    @Override
    public int getEditPosition() {
        return editPosition;
    }

    @Override
    public int getStoragePosition() {
        return storagePosition;
    }

}