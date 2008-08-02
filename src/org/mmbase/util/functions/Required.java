/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;
import java.lang.annotation.*;

/**
 * This annotation can be used on methods, to make the parameter of
 * the corresponding {@link BeanFunction} required.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Required.java,v 1.3 2008-08-02 15:14:40 michiel Exp $
 * @since MMBase-1.9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Required {


}
