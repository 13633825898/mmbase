package nl.didactor.taglib;

import java.io.IOException;
import java.util.*;
import java.text.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import javax.servlet.Servlet;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Translate tag: it will figure out a translation for a given
 * abstract locale.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class TranslateTag extends BodyTagSupport { 
    private static Logger log = Logging.getLoggerInstance(TranslateTag.class.getName());

    // These parameters are set with the different setXyz() methods
    // they may not be manipulated by this class, because that will
    // mess up in case we have tagpooling enabled.
    private String locale;
    private String debug;
    private String key;

    public void setKey(String key) {
        log.debug("set key to [" + key + "]");
        this.key = key;
    }

    public void setLocale(String locale) {
        log.debug("set locale to [" + locale + "]");
        this.locale = locale;
    }

    public void setDebug(String value) {
        this.debug = value;
    }

    public int doEndTag() throws JspTagException {
        String translateLocale = "";
        String translateDebug = "";

        String translationpath = ((Servlet)pageContext.getPage()).getServletConfig().getServletContext().getRealPath("/WEB-INF/config/translations");
        TranslateTable.init(translationpath);

        if (locale == null) {
            // If no locale is given in the tag, then we look it up in the page context
            translateLocale = (String)pageContext.getAttribute("t_locale");
            if (translateLocale == null) {
                translateLocale = "";
            }
        } else {
            // If a locale is given in the tag, then we put it in the page context
            pageContext.setAttribute("t_locale", locale);
            translateLocale = locale;
        }
        if (debug == null) {
            // if no debug is given in the tag, then we look it up in the page context
            translateDebug = (String)pageContext.getAttribute("t_debug");
            if (translateDebug == null) {
                translateDebug = "";
            }
        } else {
            // if debug is given in the tag, then we put it in the page context
            pageContext.setAttribute("t_debug", debug);
            translateDebug = debug;
        }
        
        log.debug("Getting translation table for locale '" + translateLocale + "'");
        TranslateTable tt = new TranslateTable(translateLocale);
        String translation = "";

        if (key != null) {
            translation = tt.translate(key);
            log.debug("Translating '" + key + "' to '" + translation + "'");
        } else {
            return EVAL_PAGE;
        }

        if (translation == null || "".equals(translation)) {
            if ("true".equals(translateDebug)) {
                translation = "???[" + key + "]???";
            }
        }

        try {
            pageContext.getOut().print(translation);
        } catch (java.io.IOException e) {
        }
        return EVAL_PAGE;
    }

    public void release() {
        locale = null;
        key = null;
        debug = null;
    }
}
