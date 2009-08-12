/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.virtual;

import org.mmbase.datatypes.DataType;
import org.mmbase.bridge.util.*;
import java.util.*;
import org.mmbase.bridge.*;

/**
 * Straight-forward implementation of NodeManager based on a Map with DataType's.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: MapNodeManager.java 36154 2009-06-18 22:04:40Z michiel $
 * @since   MMBase-1.9.2
 */

public class VirtualNodeManager extends AbstractNodeManager  {

    protected final Map<String, Field> map = new HashMap<String, Field>();
    protected final String name;
    protected final VirtualCloud vcloud;

    public VirtualNodeManager(VirtualCloud cloud, String name, Map<String, DataType> m) {
        super(cloud);
        this.vcloud = cloud;
        this.name = name;
        for (Map.Entry<String, DataType> entry : m.entrySet()) {
            map.put(entry.getKey(), new DataTypeField(cloud, entry.getValue()));
        }
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public Node createNode() {
        Map<String, Object> map = new HashMap<String, Object>();
        return vcloud.getNode(map, this);
    }



    @Override
    protected Map<String, Field> getFieldTypes() {
        return Collections.unmodifiableMap(map);
    }


    @Override
    public String toString() {
        return getClass().getName() + " " +  map;
    }

}