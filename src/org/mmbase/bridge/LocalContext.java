/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import org.mmbase.bridge.implementation.*;
import org.mmbase.module.core.*;
import java.util.*;

/**
 * The collection of clouds, and modules within a Java Virtual Machine.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id: LocalContext.java,v 1.2 2002-01-31 10:05:07 pierre Exp $
 */
public final class LocalContext extends BasicCloudContext {

    // singleton CloudContext
    private static final CloudContext thiscontext = new LocalContext();

    /**
     * Constructor to call from the MMBase class
     * (private, so cannot be reached from a script)
     */
    private LocalContext() {
        super();
    }

    /**
     * Called from the script to retrieve the current CloudContext
     */
    public static CloudContext getCloudContext() {
        return thiscontext;
    }

}
