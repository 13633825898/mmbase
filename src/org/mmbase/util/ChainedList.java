/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.util.*;

/**
 * Simple utility to chain several lists into a new one.
 *
 * @author	Michiel Meeuwissen
 * @since	MMBase-1.8
 * @version $Id: ChainedList.java,v 1.2 2006-10-25 20:41:52 michiel Exp $
 * @see ChainedIterator
 */
public class ChainedList<E> extends AbstractList<E> {

    private final List<List<? extends E>> lists = new ArrayList<List<? extends E>>();
    public ChainedList(List<? extends E>... ls) {
        for (List<? extends E> l : ls) {
            addList(l);
        }
    }

    public void addList(List<? extends E> l) {
        lists.add(l);
    }
    public int size() {
        int size = 0;
        for (List<? extends E> l : lists) {
            size += l.size();
        }
        return size;
    }
    public E get(int i) {
        for (List<? extends E> l : lists) {
            if (l.size() > i) {
                return l.get(i);
            }
            i -= l.size();
        }
        throw new IndexOutOfBoundsException();
    }


}
