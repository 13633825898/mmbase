/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

/**
 * Interface for the automatic generation of passwords.
 * <br />
 * The passwords generated by classes implementing this interface are based on a template.
 * A template can exists of a number of characters, which are replaced by
 * the generator.
 * The meaning of the characters in a template is left to the implementing class.
 *
 * @author Rico Jansen
 * @author Pierre van Rooden (javadocs)
 * @version 9 Jan 1997
 */

public interface PasswordGeneratorInterface {
    /**
     * Generate a password, based on a default template.
     * @return the generated password.
     */
    public String getPassword();

    /**
     * Generate a password, based on the given template.
     * @param template the template the password should be based on.
     * @return the generated password.
     */
    public String getPassword(String template);
}
