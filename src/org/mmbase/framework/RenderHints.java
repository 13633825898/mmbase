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
 * A Renderer is used by {@link Renderer#render}, with a RenderHints object, which gives to Renderer
 * implementation some instructions about how to render excactly, since it may vary a bit in that.
 *
 * @author Michiel Meeuwissen
 * @version $Id: RenderHints.java,v 1.3 2009-03-25 21:55:25 michiel Exp $
 * @since MMBase-1.9
 */
public class RenderHints {

    public final static String KEY = "org.mmbase.framework.hints";

    private final WindowState state;
    private final String id;
    private final String clazz;
    private final Renderer renderer;

    public RenderHints(Renderer renderer, WindowState state, String id, String clazz) {
        this.renderer = renderer;
        this.state = state;
        this.id = id;
        this.clazz = clazz;
    }

    /**
     * The renderer where these hints are hinting for.
     */
    public Renderer getRenderer() {
        return renderer;
    }
    /**
     * The window state is a hint on how big a part the rendering can take of the available
     * area. Thins like {@link WindowState#MAXIMIZED} or {@link WindowState#NORMAL}.
     */
    public WindowState getWindowState() {
        return state;
    }

    /**
     * A string which can identify the returned rendition in the current request. This can e.g. be
     * used to fill an 'id' attribute of the outermost produced XML tag.
     */
    public String getId() {
        return id;
    }

    /**
     * The framework may request to add a class for styling to the outermost produced XML tag.
     */
    public String getStyleClass() {
        return clazz;
    }


}
