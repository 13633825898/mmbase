package org.mmbase.util;

import java.lang.reflect.*; // necessary for SizeOf
import java.util.*;

import org.mmbase.util.logging.*;
/**
 * Implementation of sizeof.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */

public class SizeOf {
    private static Logger log = Logging.getLoggerInstance(SizeOf.class.getName());
    
    private static final int SZ_REF = 4;               
    private static int size_prim(Class t) { 
        if      (t == Boolean.TYPE)   return 1;
        else if (t == Byte.TYPE)      return 1;
        else if (t == Character.TYPE) return 2;
        else if (t == Short.TYPE)     return 2;
        else if (t == Integer.TYPE)   return 4;
        else if (t == Long.TYPE)      return 8;
        else if (t == Float.TYPE)     return 4;
        else if (t == Double.TYPE)    return 8;
        else if (t == Void.TYPE)      return 0;
        else return SZ_REF;
    }
        
    public static int sizeof(boolean b) { return 1; }        
    public static int sizeof(byte b)    { return 1; }        
    public static int sizeof(char c)    { return 2; }
    public static int sizeof(short s)   { return 2; }        
    public static int sizeof(int i)     { return 4; }
    public static int sizeof(long l)    { return 8; }        
    public static int sizeof(float f)   { return 4; }        
    public static int sizeof(double d)  { return 8; }

    private Set countedObjects = new HashSet();
    
    public static int getByteSize(Object obj) {
        return new SizeOf().sizeof(obj);
    }

    public int sizeof(Object obj) {
        if (obj == null) {
            return 0;
        }
        log.trace("sizeof object " + obj.getClass());

        if (countedObjects.contains(obj)) {
            log.trace("already counted");
            return 0;
        } else {
            log.trace("adding to countedObject");
            countedObjects.add(obj);
        }
            
        Class c = obj.getClass();
            
        if (c.isArray()) {
            log.trace("an array");
            return size_arr(obj, c);
        } else {
            log.trace("an object " + obj);
            if (Sizeable.class.isAssignableFrom(c)) return sizeof((Sizeable) obj);
            if (Map.class.isAssignableFrom(c))      return sizeof((Map) obj);
            if (String.class.isAssignableFrom(c))   return sizeof((String) obj);
            // more insteresting stuff can be added here.
            return size_inst(obj, c);
        }
    }
        
    private int sizeof(Map m) {
        log.trace("sizeof Map");
        int len = size_inst(m, m.getClass());
        Iterator i = m.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            log.trace("key");
            len += sizeof(entry.getKey());
            log.trace("value");
            len += sizeof(entry.getValue());
        }
        return len;
    }

    private int sizeof(String m) {
        log.trace("sizeof String " + m);
        int len = size_inst(m, m.getClass());            
        return len + m.getBytes().length;
    }

    private int sizeof(Sizeable m) {
        log.trace("sizeof Sizeable " + m);
        int len = size_inst(m, m.getClass());            
        return len + m.getByteSize(this);
    }
        

    private int size_inst(Object obj, Class c) {
        Field flds[] = c.getDeclaredFields();
        int sz = 0;
            
        for (int i = 0; i < flds.length; i++) {
            Field f = flds[i];
            if (!c.isInterface() &&  (f.getModifiers() & Modifier.STATIC) != 0) {
                continue;
            }
            sz += size_prim(f.getType());
            try {
                log.trace("found a field " + f);
        
                    sz += sizeof(f.get(obj)); // recursion
        
            } catch (java.lang.IllegalAccessException e) {
                // well...
                log.trace(e);

            }
        }
            
        if (c.getSuperclass() != null) {
            sz += size_inst(obj, c.getSuperclass());
        }
            
        Class cv[] = c.getInterfaces();
        for (int i = 0; i < cv.length; i++) {
            sz += size_inst(obj, cv[i]);
        }
            
        return sz;
    }
        
    private int size_arr(Object obj, Class c) {
        Class ct = c.getComponentType();
        int len = Array.getLength(obj);
            
        if (ct.isPrimitive()) {
            return len * size_prim(ct);
        }
        else {
            int sz = 0;
            for (int i = 0; i < len; i++) {
                sz += SZ_REF;
                Object obj2 = Array.get(obj, i);
                if (obj2 == null)
                    continue;
                Class c2 = obj2.getClass();
                if (!c2.isArray())
                    continue;
                sz += size_arr(obj2, c2);
            }
            return sz;
        }
    }
}

