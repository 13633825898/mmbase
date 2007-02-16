/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.io.Serializable;
import java.util.*;
import org.mmbase.bridge.*;

/**
 * Analogon of {@link java.util.Collections}. Methods speak for themselves.
 *
 *
 * @author  Michiel Meeuwissen
 * @version $Id: BridgeCollections.java,v 1.8 2007-02-16 20:06:41 michiel Exp $
 * @since   MMBase-1.8
 */

public abstract class BridgeCollections {

    /**
     * Makes a BridgeList unmodifiable.
     */
    public static final <E> BridgeList<E> unmodifiableBridgeList(BridgeList<E> bridgeList) {
        return new UnmodifiableBridgeList<E>(bridgeList);
    }

    /**
     * Makes a NodeList unmodifiable.
     */
    public static final NodeList unmodifiableNodeList(NodeList nodeList) {
        return new UnmodifiableNodeList(nodeList);
    }

    /**
     * Makes a NodeManagerList unmodifiable.
     */
    public static final NodeManagerList unmodifiableNodeManagerList(NodeManagerList nodeList) {
        return new UnmodifiableNodeManagerList(nodeList);
    }

    /**
     * Makes a RelationManagerList unmodifiable.
     */
    public static final RelationManagerList unmodifiableRelationManagerList(RelationManagerList nodeList) {
        return new UnmodifiableRelationManagerList(nodeList);
    }

    /**
     * Makes a RelationList unmodifiable.
     */
    public static final RelationList unmodifiableRelationList(RelationList relationList) {
        return new UnmodifiableRelationList(relationList);
    }

    /**
     * Makes a StringList unmodifiable.
     */
    public static final StringList unmodifiableStringList(StringList stringList) {
        return new UnmodifiableStringList(stringList);
    }

    public static final NodeList            EMPTY_NODELIST            = new EmptyNodeList();
    public static final NodeManagerList     EMPTY_NODEMANAGERLIST     = new EmptyNodeManagerList();
    public static final RelationManagerList EMPTY_RELATIONMANAGERLIST = new EmptyRelationManagerList();
    public static final RelationList        EMPTY_RELATIONLIST        = new EmptyRelationList();
    public static final StringList          EMPTY_STRINGLIST          = new EmptyStringList();

    // IMPLEMENTATIONS follow below.

    /* --------------------------------------------------------------------------------
     * Unmodifiable iterators
     */
    static class UnmodifiableListIterator<E> implements ListIterator<E> {
        final protected ListIterator<E> i;
        UnmodifiableListIterator(ListIterator<E> i) {
            this.i = i;
        }
        public boolean hasNext() { return i.hasNext(); }
        public boolean hasPrevious() { return i.hasPrevious(); }
        public E next() { return i.next(); }
        public E previous() { return i.previous(); }
        public int nextIndex() { return i.nextIndex(); }
        public int previousIndex() { return i.previousIndex(); }
        public void remove() { throw new UnsupportedOperationException(); }
        public void add(Object o) { throw new UnsupportedOperationException(); }
        public void set(Object o) { throw new UnsupportedOperationException(); }
    }

    static class UnmodifiableNodeIterator extends UnmodifiableListIterator<Node> implements NodeIterator {
        UnmodifiableNodeIterator(NodeIterator i) {
            super(i);
        }
        public Node nextNode() { return ((NodeIterator) i).nextNode(); }
        public Node previousNode() { return ((NodeIterator) i).previousNode(); }
    }

    static class UnmodifiableNodeManagerIterator extends UnmodifiableListIterator<NodeManager> implements NodeManagerIterator {
        UnmodifiableNodeManagerIterator(NodeManagerIterator i) {
            super(i);
        }
        public NodeManager nextNodeManager() { return ((NodeManagerIterator) i).nextNodeManager(); }
        public NodeManager previousNodeManager() { return ((NodeManagerIterator) i).previousNodeManager(); }
    }

    static class UnmodifiableRelationManagerIterator extends UnmodifiableListIterator<RelationManager> implements RelationManagerIterator {
        UnmodifiableRelationManagerIterator(RelationManagerIterator i) {
            super(i);
        }
        public RelationManager nextRelationManager() { return ((RelationManagerIterator) i).nextRelationManager(); }
        public RelationManager previousRelationManager() { return ((RelationManagerIterator) i).previousRelationManager(); }
    }

    static class UnmodifiableRelationIterator extends UnmodifiableListIterator<Relation> implements RelationIterator {
        UnmodifiableRelationIterator(RelationIterator i) {
            super(i);
        }
        public Relation nextRelation() { return ((RelationIterator) i).nextRelation(); }
        public Relation previousRelation() { return ((RelationIterator) i).previousRelation(); }
    }

    static class UnmodifiableStringIterator extends UnmodifiableListIterator<String> implements StringIterator {
        UnmodifiableStringIterator(StringIterator i) {
            super(i);
        }
        public String nextString() { return ((StringIterator) i).nextString(); }
        public String previousString() { return ((StringIterator) i).previousString(); }

    }

    /* --------------------------------------------------------------------------------
     * Unmodifiable Lists.
     */
    static class UnmodifiableBridgeList<E> implements BridgeList<E> {
        final List<E> c;
        final BridgeList<E> parent ; // just to expose properties to sublists.

        UnmodifiableBridgeList() {
            c = new EmptyBridgeList<E>();
            parent = null;
        }

        UnmodifiableBridgeList(BridgeList<E> c) {
            if (c == null) { throw new NullPointerException(); }
            this.c = c;
            this.parent = null;
        }

        UnmodifiableBridgeList(List<E> c, BridgeList<E> parent) {
            if (c == null) { throw new NullPointerException(); }
            this.c = c;
            this.parent = parent;
        }

        public int size() 		    { return c.size(); }
        public boolean isEmpty() 	    { return c.isEmpty(); }
        public boolean contains(Object o)   { return c.contains(o); }
        public Object[] toArray()           { return c.toArray(); }
        public <T> T[] toArray(T[] a) { return c.toArray(a); }
        @Override
        public String toString()            { return c.toString(); }
        public ListIterator<E> listIterator(final int s) { return new UnmodifiableListIterator<E>(c.listIterator(s)); }
        public ListIterator<E> listIterator() { return listIterator(0); }
        public Iterator<E> iterator() { return listIterator(); }
        public boolean add(Object o){ throw new UnsupportedOperationException(); }
        public void add(int i, Object o) { throw new UnsupportedOperationException(); }
        public E set(int i, Object o) { throw new UnsupportedOperationException(); }
        public boolean remove(Object o) { throw new UnsupportedOperationException(); }
        public E remove(int i) { throw new UnsupportedOperationException(); }
        public boolean containsAll(Collection<?> coll) { return c.containsAll(coll); }
        public boolean addAll(Collection<? extends E> coll) { throw new UnsupportedOperationException(); }
        public boolean addAll(int i, Collection<? extends E> coll) { throw new UnsupportedOperationException(); }
        public boolean removeAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
        public boolean retainAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
        public void clear() { throw new UnsupportedOperationException(); }
        public E get(int i) { return c.get(i); }

        public Object getProperty(Object key) {
            if (parent != null) return parent.getProperty(key);
            return ((BridgeList<E>) c).getProperty(key);
        }

        public void setProperty(Object key, Object value) { throw new UnsupportedOperationException(); }
        public void sort() { throw new UnsupportedOperationException(); }
        public void sort(Comparator<? super E> comparator) { throw new UnsupportedOperationException(); }

        public BridgeList<E> subList(int fromIndex, int toIndex) {
            return new UnmodifiableBridgeList<E>(c.subList(fromIndex, toIndex), parent != null ? parent : (BridgeList<E>) c);
        }

        public int lastIndexOf(Object o) { return c.lastIndexOf(o); }
        public int indexOf(Object o) { return c.indexOf(o); }
        @Override
        public boolean equals(Object o) { return c.equals(o); }
        @Override
        public int hashCode() { return c.hashCode(); }
    }

    static class UnmodifiableNodeList extends UnmodifiableBridgeList<Node> implements NodeList {

        UnmodifiableNodeList(NodeList nodeList) {
            super(nodeList);
        }

        public Node getNode(int index) {
            return ((NodeList) c).getNode(index);
        }

        public NodeIterator nodeIterator() {
            return new UnmodifiableNodeIterator(((NodeList)c).nodeIterator());
        }

        public NodeList subNodeList(int fromIndex, int toIndex) {
            return new UnmodifiableNodeList(((NodeList) c).subNodeList(fromIndex, toIndex));
        }
    }

    static class UnmodifiableNodeManagerList extends UnmodifiableBridgeList<NodeManager> implements NodeManagerList {

        UnmodifiableNodeManagerList(NodeManagerList nodeManagerList) {
            super(nodeManagerList);
        }

        public NodeManager getNodeManager(int index) {
            return ((NodeManagerList) c).getNodeManager(index);
        }

        public NodeManagerIterator nodeManagerIterator() {
            return new UnmodifiableNodeManagerIterator(((NodeManagerList)c).nodeManagerIterator());
        }
    }

    static class UnmodifiableRelationManagerList extends UnmodifiableBridgeList<RelationManager> implements RelationManagerList {

        UnmodifiableRelationManagerList(RelationManagerList relationManagerList) {
            super(relationManagerList);
        }

        public RelationManager getRelationManager(int index) {
            return ((RelationManagerList) c).getRelationManager(index);
        }

        public RelationManagerIterator relationManagerIterator() {
            return new UnmodifiableRelationManagerIterator(((RelationManagerList)c).relationManagerIterator());
        }
    }

    static class UnmodifiableRelationList extends UnmodifiableBridgeList<Relation> implements RelationList {

        UnmodifiableRelationList(RelationList relationList) {
            super(relationList);
        }

        public Relation getRelation(int index) {
            return ((RelationList) c).getRelation(index);
        }

        public RelationIterator relationIterator() {
            return new UnmodifiableRelationIterator(((RelationList)c).relationIterator());
        }

        public RelationList subRelationList(int fromIndex, int toIndex) {
            return new UnmodifiableRelationList(((RelationList) c).subRelationList(fromIndex, toIndex));
        }
    }

    static class UnmodifiableStringList extends UnmodifiableBridgeList<String> implements StringList {

        UnmodifiableStringList(StringList stringList) {
            super(stringList);
        }

        public String getString(int index) {
            return ((StringList) c).getString(index);
        }

        public StringIterator stringIterator() {
            return new UnmodifiableStringIterator(((StringList)c).stringIterator());
        }
    }

    /* --------------------------------------------------------------------------------
     * Empty (and unmodifiable) Lists.
     */
    static class EmptyBridgeList<E> extends UnmodifiableBridgeList<E> implements Serializable  {
        EmptyBridgeList() { }

        private static final Object[] EMPTY = new Object[] {};
        @Override
        public final int size() { return 0; }
        @Override
        public final boolean isEmpty() { return true; }
        @Override
        public final boolean contains(Object o) { return false; }
        @Override
        public final boolean containsAll(Collection<?> col) { return col.isEmpty(); }
        @Override
        public final Object[] toArray() { return EMPTY; }
        @Override
        public String toString() { return "[]"; }

        @SuppressWarnings("unchecked")
        @Override
        public final ListIterator listIterator(int c) { return Collections.EMPTY_LIST.listIterator(c); }
        @Override
        public E get(int i) { throw new IndexOutOfBoundsException(); }
        @Override
        public Object getProperty(Object key) { return null; }
        @Override
        public BridgeList<E> subList(int fromIndex, int toIndex) { throw new IndexOutOfBoundsException(); }
        @Override
        public int lastIndexOf(Object o) { return -1; }
        @Override
        public int indexOf(Object o) { return -1; }
        @Override
        public boolean equals(Object o) { return Collections.EMPTY_LIST.equals(o); }
        @Override
        public int hashCode() { return Collections.EMPTY_LIST.hashCode(); }
    }

    static class EmptyNodeList extends EmptyBridgeList<Node> implements NodeList {

        public final Node getNode(int index) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }

        public final NodeIterator nodeIterator() {
            return new UnmodifiableNodeIterator(null) {
                @Override
                public boolean hasNext() { return false; }
                @Override
                public boolean hasPrevious() { return false; }
                @Override
                public Node nextNode() { throw new NoSuchElementException(); }
                @Override
                public Node previousNode() { throw new NoSuchElementException(); }
                @Override
                public Node next() { throw new NoSuchElementException(); }
                @Override
                public Node previous() { throw new NoSuchElementException(); }
            };
        }

        public NodeList subNodeList(int fromIndex, int toIndex) {
            if (fromIndex == 0 && toIndex == 0) return this;
            throw new IndexOutOfBoundsException();
        }
        // Preserves singleton property
        protected Object readResolve() { return EMPTY_NODELIST; }
    }

    static class EmptyRelationList extends EmptyBridgeList<Relation> implements RelationList {

        public Relation getRelation(int index) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }

        public RelationIterator relationIterator() {
            return new UnmodifiableRelationIterator(null) {
                @Override
                public boolean hasNext() { return false; }
                @Override
                public boolean hasPrevious() { return false; }
                @Override
                public Relation nextRelation() { throw new NoSuchElementException(); }
                @Override
                public Relation previousRelation() { throw new NoSuchElementException(); }
                @Override
                public Relation next() { throw new NoSuchElementException(); }
                @Override
                public Relation previous() { throw new NoSuchElementException(); }
            };
        }

        public RelationList subRelationList(int fromIndex, int toIndex) {
            if (fromIndex == 0 && toIndex == 0) return this;
            throw new IndexOutOfBoundsException();
        }
        protected Object readResolve() { return EMPTY_RELATIONLIST; }
    }

    static class EmptyNodeManagerList extends EmptyBridgeList<NodeManager> implements NodeManagerList {

        public NodeManager getNodeManager(int index) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }

        public NodeManagerIterator nodeManagerIterator() {
            return new UnmodifiableNodeManagerIterator(null) {
                @Override
                public boolean hasNext() { return false; }
                @Override
                public boolean hasPrevious() { return false; }
                @Override
                public NodeManager nextNodeManager() { throw new NoSuchElementException(); }
                @Override
                public NodeManager previousNodeManager() { throw new NoSuchElementException(); }
                @Override
                public NodeManager next() { throw new NoSuchElementException(); }
                @Override
                public NodeManager previous() { throw new NoSuchElementException(); }
            };
        }
        protected Object readResolve() { return EMPTY_NODEMANAGERLIST; }
    }

    static class EmptyRelationManagerList extends EmptyBridgeList<RelationManager> implements RelationManagerList {

        public RelationManager getRelationManager(int index) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }

        public RelationManagerIterator relationManagerIterator() {
            return new UnmodifiableRelationManagerIterator(null) {
                @Override
                public boolean hasNext() { return false; }
                @Override
                public boolean hasPrevious() { return false; }
                @Override
                public RelationManager nextRelationManager() { throw new NoSuchElementException(); }
                @Override
                public RelationManager previousRelationManager() { throw new NoSuchElementException(); }
                @Override
                public RelationManager next() { throw new NoSuchElementException(); }
                @Override
                public RelationManager previous() { throw new NoSuchElementException(); }
            };
        }
        protected Object readResolve() { return EMPTY_RELATIONMANAGERLIST; }
    }

    static class EmptyStringList extends EmptyBridgeList<String> implements StringList {
        public String getString(int index) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }

        public StringIterator stringIterator() {
            return new UnmodifiableStringIterator(null) {
                @Override
                public boolean hasNext() { return false; }
                @Override
                public boolean hasPrevious() { return false; }
                @Override
                public String nextString() { throw new NoSuchElementException(); }
                @Override
                public String previousString() { throw new NoSuchElementException(); }
            };
        }
        protected Object readResolve() { return EMPTY_STRINGLIST; }
    }

}
