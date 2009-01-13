/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;


import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.*;


import org.mmbase.util.functions.*;
import org.mmbase.util.*;
import org.mmbase.module.core.MMBase;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Currently renders documentation directly from CVS. The idea is that a fall-back can be added to
 * render the documenation from the xml's in a/the jar.
 *
 * @author Michiel Meeuwissen
 * @version $Id: DocumentationRenderer.java,v 1.1 2009-01-13 08:24:21 michiel Exp $
 * @since MMBase-1.9.1

 */
public class DocumentationRenderer extends CachedRenderer {
    private static final Logger log = Logging.getLoggerInstance(DocumentationRenderer.class);

    private String repository  = "http://cvs.mmbase.org/viewcvs/*checkout*/";
    private String module      = "documentation";

    private String docbook     = null;

    public void setDocbook(String s) {
        docbook = s;
    }
    public Parameter[] getParameters() {
        return new Parameter[] {new Parameter<String>("docbook", String.class)};
    }


    public DocumentationRenderer(String t, Block parent) {
        super(t, parent);
    }
    @Override public Renderer getWraps() {
        if (wrapped == null) {
            try {
                ConnectionRenderer connection = new ConnectionRenderer(getType().toString(), getBlock()) {
                        @Override public URI getUri(Parameters blockParameters, RenderHints hints) {
                            try {
                                String db = blockParameters != null ? blockParameters.getString("docbook") : null;
                                if (db == null || "".equals(db)) {
                                    db = DocumentationRenderer.this.docbook;
                                    if (db == null) throw new IllegalArgumentException("docbook parameter not set on parameters, nor as renderer property");
                                }
                                return new URL(repository + module + "/" + db).toURI();
                            } catch (MalformedURLException mfe) {
                                throw new RuntimeException(mfe.getMessage(), mfe);
                            } catch (URISyntaxException use) {
                                throw new RuntimeException(use.getMessage(), use);
                            }
                        }
                    };
                connection.setXslt("xslt/docbook2block.xslt");
                wrapped = connection;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return wrapped;
    }


}