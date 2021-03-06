/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.util;

import java.util.ArrayList;

import org.mmbase.bridge.Field;
import org.mmbase.module.core.MMObjectBuilder;

/**
 * @javadoc
 *
 * @since  MMBase-1.8
 * @author Pierre van Rooden
 * @version $Id$
 */
public class Index extends ArrayList<Field> {

    /**
     * Name of the 'main' index of a builder (the 'nameless' index of all fields whose 'key' attribute is true)
     */
    static final public String MAIN = "main";

    private final MMObjectBuilder builder;
    private final String name;
    private boolean unique = false;

    public Index(MMObjectBuilder builder, String name) {
        super();
        this.builder = builder;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public MMObjectBuilder getParent() {
        return builder;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    @Override
    public synchronized boolean add(Field field) {
        if (!contains(field)) {
            return super.add(field);
        } else {
            return false;
        }
    }

    public synchronized boolean remove(Field field) {
        return super.remove(field);
    }

    @Override
    public String toString() {
        return (isUnique() ? "UNIQUE INDEX" : "INDEX") + " '" + getName() + "' on " + getParent().getTableName() + " " + super.toString();
    }

}
