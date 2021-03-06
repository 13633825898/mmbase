/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.magicfile;

/**
 * DetectorProvider classes are meant to provide a list of Detectors,
 * which can be used by MagicFile.
 * @version $Id$
 * @author Michiel Meeuwissen
 */

public interface DetectorProvider {
    java.util.List<Detector> getDetectors();
}

