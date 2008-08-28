/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import java.io.*;
import java.net.URI;
import org.mmbase.util.functions.*;

/**
 * A Renderer renders a certain aspect of a {@link Block}. Currently every block has two renderers,
 * which are identified by the renderer 'type' (see {@link #getType }). Every block also has a
 * {@link Processor}, which is similar to a Renderer, but a processor never generates content, only
 * handles interaction.
 *
 * A Renderer is stateless.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Renderer.java,v 1.23 2008-08-28 11:43:56 michiel Exp $
 * @since MMBase-1.9
 */
public interface Renderer {

    enum Type {
        /**
         * Not yet rendering
         */
        NOT,
        /**
         * Rendering for 'HEAD' typically happens in the &lt;head&gt; block of HTML, and can
         * e.g. produce links to javascript.
         */
        HEAD,
       /**
        * A body typed renderer renders the actual content of a block. It should produce, at least
        * for text/html, a &lt;div&gt; with class 'mm_c c_&lt;name of component&gt; b_&lt; name of
        * block&gt;
        */
        BODY;

        /**
         * Returns a renderer that does nothing.
         */
        public Renderer getEmpty(final Block block) {
            return new Renderer() {
                public Type getType() { return Type.this; }
                public void render(Parameters parameters, Writer w, RenderHints hints) { };
                public Parameter[] getParameters() { return Parameter.emptyArray(); };
                public Block getBlock() { return block ; };
                public String toString() { return "EMPTY Renderer"; }
                public URI getUri() { try {return new URI("mmbase:/renderer/" + Type.this + "/empty");} catch (Exception e) { return null;} }
                public boolean equals(Object o) {
                    if (o instanceof Renderer) {
                        Renderer r = (Renderer) o;
                        return getUri().equals(r.getUri()) && getBlock().equals(r.getBlock());
                    } else {
                        return false;
                    }
                }
            };
        }
    }

    /**
     * Describes what kind of renderer this is
     */
    Type getType();

    /**
     * Every renderer renders for a certain block.
     */
    Block getBlock();

    /**
     * A renderer may need certain parameters. These are added to the block-parameters. This method
     * is called on instantation of the renderer.
     */
    Parameter[] getParameters();

    /**
     * Renders to a writer. In case of e.g. a JSPView, the parameters must also contain
     * the Http Servlet response and request, besided specific parameters for this component.
     */
    void render(Parameters blockParameters, Writer w, RenderHints hints) throws FrameworkException;


    /**
     * An URI which may identify the implementation of this Renderer.
     */

    URI getUri();
}
