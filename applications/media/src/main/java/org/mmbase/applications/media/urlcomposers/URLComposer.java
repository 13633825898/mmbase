/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.applications.media.urlcomposers;

import org.mmbase.applications.media.builders.MediaProviders;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.HashCodeUtil;
import org.mmbase.util.MimeType;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.Codec;
import org.mmbase.applications.media.State;
import org.mmbase.util.images.Dimension;

import java.util.*;

/**
 * URLComposer is a wrapper/container class around an URL. It contains besides the
 * URL some extra meta information about it, like the original source
 * object of the resource it represents and if it is currently
 * available or not.  An URL can be unavailable because of two
 * reasons: because the provider is offline, or because the fragment
 * where it belongs to is not valid (e.g. because of publishtimes).
 *
 * It is used by the Media builders to pass around information (mainly
 * as an entry in Lists).
 *
 * @author Michiel Meeuwissen
 * @author Rob Vermeulen (VPRO)
 * @version $Id$
 */
public class URLComposer  {

    private static final Logger log = Logging.getLoggerInstance(URLComposer.class);

    protected MMObjectNode  source;
    protected MMObjectNode  provider;
    protected Map<String, Object>           info;

    protected int preference = 0;

    public void init(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map<String, Object> info, Set<MMObjectNode> cacheExpireObjects) {
        if(cacheExpireObjects!=null) {
            cacheExpireObjects.add(provider);
            cacheExpireObjects.add(source);

        }

        if (source   == null) throw new RuntimeException("Source may not be null in a URLComposer object");
        if (provider == null) throw new RuntimeException("Provider may not be null in a URLComposer object");
        this.provider = provider;
        this.source   = source;
        this.info     = info == null ? new java.util.HashMap<String, Object>() : info;

    }

    public MMObjectNode getSource()   {
        return source;
    }
    public MMObjectNode getProvider() {
        return provider;
    }
    /**
     * An auxiliary map. Filters can put stuff into it (a {@link org.mmbase.applications.media.filters.Labeler} does only that) for use by the subsequent filters in the chain.
     * The initial contents of this map is given given by the client. Using the functions on the builders, there are only a few predefined keys (the arguments of the function).
     * @return
     */
    public Map<String, Object> getInfo()     {
        return info;
    }

    /**
     * The format of the produced URL. This is not necessarily the format of the source.
     * (Though it normally would be)
     */
    public Format getFormat() {
        return Format.get(source.getIntValue("format"));
    }

    public Codec getCodec() {
        return Codec.get(source.getIntValue("codec"));
    }

    /**
     * Audio codec if its field is present, returns 'UNKNOWN' (-1) if no such field or codec is
     * unknown. Videosources have both codec and acodec (audio codec) fields, audiosources have
     * just one codec field.
     */
    public Codec getAcodec() {
        if (source.getBuilder().hasField("acodec")) {
            return Codec.get(source.getIntValue("acodec"));
        } else {
            return Codec.get(-1);
        }
    }

    public int getBitrate() {
        return source.getIntValue("bitrate");
    }
    
    /**
     * The mime-type of the produced URL. This is not necessarily the mimetype of the source.
     * (Though it normally would be)
     */
    public MimeType       getMimeType() {
        Format f = Format.get(source.getIntValue("format"));
        if (f == Format.UNKNOWN && source.getBuilder().hasField("mimetype")) {
            return new MimeType(source.getStringValue("mimetype"));
        }
        Codec c = Codec.get(source.getIntValue("codec"));
        return f.getMimeType(c == Codec.UNKNOWN ? null : c.toString().toLowerCase());
    }

    public String getGUIIndicator(Map<String,Locale> options) {
        Locale locale = options.get("locale");
        return getFormat().getGUIIndicator(locale);
    }

    public String getDescription(Map<String,Locale> options) {
        return null; // no informative description
    }

    public Dimension getDimension() {
        if (source.getBuilder().hasField("height")) {
            int w = source.getIntValue("width");
            int h = source.getIntValue("height");
            if (w > 0 || h > 0) {
                return new Dimension(w, h);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public int getFilesize() {
        return source.getIntValue("filesize");
    }

    /**
     * Returns true. This can be overridden if the URLComposer not
     * always can do it's job. It then returns false if it is (can be?)
     * taken from consideration.
     */
    public boolean canCompose() {
        return true;
    }

    /**
     * Extension will normally create URL's differently. They override this function.
     *
     */
    protected StringBuilder getURLBuffer() {
        String sourceUrl = source.getStringValue("url");
        StringBuilder buff = new StringBuilder();
        if (sourceUrl.length() > 0) {
            buff.append(provider.getFunctionValue("url", null).toString());
            buff.append(sourceUrl);
        } else {
            buff.append("#nourlyet");
        }
        return buff;
    }

    /**
     * Returns the URL as a String. To encourage efficient coding,
     * this method is final. Override getURLBuffer instead.
     */
    public final String getURL() {
        return getURLBuffer().toString();
    }

    /**
     * Returns the 'short' url (typically the filename) for display purposes,
     * just the source field value url.
     */
    public final String getFilename() {
        String sourceUrl = source.getStringValue("url");
        if (sourceUrl.length() > 0) {
            return sourceUrl;
        } else {
            return "#nourlyet";
        }
    }

    /**
     * Whether the URL which will be produced by this composer is actually already usable.
     * This means that the provider must be 'on', and the source must be either an original ({@link
     * State#SOURCE}), or its a generated source and its generation is done.
     */
    public boolean isAvailable() {
        boolean sourceAvailable = (source != null &&
                                      (source.getIntValue("state") == State.DONE.getValue() ||
                                       source.getIntValue("state") == State.SOURCE.getValue()));
        boolean providerAvailable  = (provider.getIntValue("state") == MediaProviders.STATE_ON);
        return providerAvailable && sourceAvailable;
    }

    /* The source */
    public boolean isMain() {
        if (source != null && (source.getIntValue("state") == State.SOURCE.getValue() || 
                source.getIntValue("state") == State.SOURCE_UNSUPPORTED.getValue()) ) {
            return true;
        } else {
            return false;
        }
    }

    /* State of this source */
    public State getState() {
        return source == null ? null : State.get(source.getIntValue("state"));
    }

    @Override
    public String toString() {
        // for verboseness:
        String className = getClass().getName().substring(getClass().getName().lastIndexOf(".") + 1);
        if (isAvailable()) {
            return className + "/" + getFormat() + ": " + getURL();
        } else {
            return "{" + className + "/" +  getFormat() + ": " + getURL() + "}";
        }
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null) return false;
        if (o.getClass().equals(getClass())) {
            URLComposer r = (URLComposer) o;
            return
            (source == null ? r.source == null : source.getNumber() == r.source.getNumber()) &&
            (provider == null ? r.provider == null : provider.getNumber() == r.provider.getNumber()) &&
            (info == null ? r.info == null : info.equals(r.info));
        }
        return false;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 0;
        result = HashCodeUtil.hashCode(result, source == null ? 0 : source.getNumber());
        result = HashCodeUtil.hashCode(result, provider == null ? 0 : provider.getNumber());
        result = HashCodeUtil.hashCode(result, info == null ? 0 : info.hashCode());
        return result;
    }

}
