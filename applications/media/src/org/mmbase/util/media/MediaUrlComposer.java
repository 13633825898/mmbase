/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.util.media;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.builders.media.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.XMLBasicReader;
import org.mmbase.util.FileWatcher;

import java.util.*;
import java.io.File;

import javax.servlet.http.*;
import org.w3c.dom.Element;

/**
 * The urlcomposer contains functionality for creating the url, the uri, and the content type.
 * 
 * @author Rob Vermeulen (VPRO)
 */
public class MediaUrlComposer {
    
    private static Logger log = Logging.getLoggerInstance(MediaSourceFilter.class.getName());
    
    // MediaFragment builder
    private static MediaFragments mediaFragmentBuilder = null;
    
    // MediaSource builder
    private static MediaSources mediaSourceBuilder = null;
    
    // Mime mapping
    private static Hashtable mimeMapping = null;
    
    private FileWatcher configWatcher = new FileWatcher(true) {
        protected void onChange(File file) {
            readConfiguration(file);
        }
    };
    
    /**
     * construct the MediaProviderFilter
     */
    public MediaUrlComposer(MediaFragments mf, MediaSources ms) {
        log.debug("Starting urlcomposer");
        mediaFragmentBuilder = mf;
        mediaSourceBuilder = ms;
        
        File configFile = new File(org.mmbase.module.core.MMBaseContext.getConfigPath(), "media" + File.separator + "urlcomposer.xml");
        if (! configFile.exists()) {
            log.error("Configuration file for urlcomposer " + configFile + " does not exist");
            return;
        }
        readConfiguration(configFile);
        configWatcher.add(configFile);
        configWatcher.setDelay(10 * 1000); // check every 10 secs if config changed
        configWatcher.start();
    }
    /**
     * read the MediaSourceFilter configuration
     */
    private synchronized void readConfiguration(File configFile) {
        
        XMLBasicReader reader = new XMLBasicReader(configFile.toString(), getClass());
        mimeMapping = new Hashtable();
        
        // reading filterchain information
        for(Enumeration e = reader.getChildElements("urlcomposer.mimemapping","map");e.hasMoreElements();) {
            Element map=(Element)e.nextElement();
            String format = reader.getElementAttributeValue(map,"format");
            String codec = reader.getElementAttributeValue(map,"codec");
            String mime = reader.getElementValue(map);
            
            mimeMapping.put(format+"/"+codec,mime);
            log.debug("Adding mime mapping "+format+"/"+codec+" -> "+mime);
        }
    }
    
    /**
     * create the uri for a certain media source
     */
    public String getURI(MMObjectNode mediafragment, MMObjectNode mediasource, Map info) {
        
        String url = null;
        int format = mediasource.getIntValue("format");
        if(format==MediaSources.RM_FORMAT || format==MediaSources.RA_FORMAT) {
            url = createRealURL(mediafragment, mediasource, info);
        } else {
            log.debug("Only readaudio/realmedia is supported");
        }
        return url;
    }
    
    /**
     * Set the content type in the response
     *
     * @param mediasource the mediasource
     * @param response the HttpSevletResponse
     */
    public void setContentType(MMObjectNode mediasource, HttpServletResponse response) {
        response.setContentType(getContentType(mediasource));
    }
    
    /**
     * resolve the url of the mediasource (e.g. pnm://www.mmbase.org/test/test.ra)
     *
     * @param mediasouce the media source
     * @param info extra info (i.e. HttpRequestIno, bitrate, etc.)
     * @return the url of the media source
     */
    public String getUrl(MMObjectNode mediasource, Map info) {
        return mediasource.getStringValue("url");
    }
    
    /**
     * Resolve the content type for a certain media source
     *
     * @param mediasource the media source
     * @return the content type
     */    
    public String getContentType(MMObjectNode mediasource) {
        String format = mediasource.getStringValue("format");
        if(format==null || format.equals("")) {
            format="*";
        }
        String codec = mediasource.getStringValue("codec");
        if(codec==null || codec.equals("")) {
            codec="*";
        }
        String mimetype = "";
        if(mimeMapping.containsKey(format+"/"+codec)) {
            mimetype += mimeMapping.get(format+"/"+codec);
        } else if (mimeMapping.containsKey(format+"/*")) {
            mimetype += mimeMapping.get(format+"/*");
        } else if (mimeMapping.containsKey("*/"+codec)) {
            mimetype += mimeMapping.get("/"+codec);
        } else if (mimeMapping.containsKey("*/*")) {
            mimetype += mimeMapping.get("*/*");
        }   
        log.debug("Mimetype for mediasource "+mediasource.getStringValue("number")+" is "+mimetype);
        return mimetype;
    }
    
    /**
     * Just an implementation of a real url. Maybe we should make this configuratble in the config file.
     * but i don't know if that makes much sence.
     */
    public String createRealURL(MMObjectNode mediafragment, MMObjectNode mediasource, Map info) {
        List params = new Vector();
        String urlpart = "";
        
        // Evaluate title
        String title = makeRealCompatible(mediafragment.getStringValue("title"));
        if(title!=null && !title.equals("")) {
            params.add("title="+title);
        }
        
        // Evaluate author
        String author=null;
        MMObjectNode rootFragment = null;
        if(mediaFragmentBuilder.isSubFragment(mediafragment)) {
            rootFragment = mediaFragmentBuilder.getParentFragment(mediafragment);
        } else {
            rootFragment = mediafragment;
        }
        Enumeration e = rootFragment.getRelations("groups");
        if (e.hasMoreElements()) {
            MMObjectNode group = (MMObjectNode)e.nextElement();
            String name = makeRealCompatible(group.getStringValue("name"));
            if( name!=null && !name.equals("")) {
                params.add("author="+name);
            }
        }
        
        // Evaluate start & end
        if(mediaFragmentBuilder.isSubFragment(mediafragment)) {
            String start = makeRealTime(mediafragment.getLongValue("start"));
            if(start!=null && !start.equals("")) {
                params.add("start="+start);
            }
            String stop = makeRealTime(mediafragment.getLongValue("stop"));
            if(stop!=null && !stop.equals("")) {
                params.add("end="+stop);
            }
        }
        
        if(params.size()!=0) {
            urlpart+="?";
            for (Iterator i = params.iterator(); i.hasNext();) {
                urlpart+=(String) i.next();
                if(i.hasNext()) {
                    urlpart+="&";
                }
            }
        }
        return urlpart;
    }
    
    /**
     * Replaces all plus characters to procent 20
     * @param s String in which chars will be replaced.
     * @return replaced String
     */
    public static String plusToProcent20(String s) {
        String result = "";
        for(int i=0; i<s.length(); i++) {
            if (s.charAt(i) != '+') {
                result += s.charAt(i);
            } else {
                result += "%20";
            }
        }
        return result;
    }
    
    /**
     * Removes RealPlayer incompatible characters from the string.
     * '#' characters are replaced by space characters.
     * Characters that are allowed are every letter or digit and ' ', '.', '-' and '_' chars.
     * @param s the String that needs to be fixed.
     * @return a realPlayer compatible String.
     */
    public static String makeRealCompatible(String s) {
        if (s != null) {
            char[] sArray = s.replace('#',' ').toCharArray();
            char[] dArray = new char[sArray.length];
            
            int j = 0;
            for (int i=0;i<sArray.length;i++) {
                if (Character.isLetterOrDigit(sArray[i]) ||(sArray[i]==' ')||(sArray[i]=='.')||(sArray[i]=='-')||(sArray[i]=='_')) {
                    dArray[j] = sArray[i];
                    j++;
                }
            }
            //Only use the characters until the first character with value=0. This is from index 0 to j-1.
            return (new String(dArray)).substring(0,j);
        }
        return null;
        
    }
    
    /**
     * Real is using times that look like hh:mm:ss:t, where t is tenths of seconds.
     * @param time the time in milliseconds
     * @return the time in real format
     */
    public static String makeRealTime(long time) {
        String ret="";
        time = time/100;
        long t=0;
        
        // tenths of seconds
        if(time!=0) {
            t = time%10;
            time = time/10;
            ret = "0."+t;
        }
        
        // seconds
        if(time!=0) {
            long sec = time%60;
            time = time/60;
            ret = ""+sec+"."+t;
        }
        
        // minutes
        if(time!=0) {
            long min = time%60;
            time = time/60;
            ret = ""+min+":"+ret;
        }
        
        if(time!=0) {
            long hour = time%24;
            time = time/24;
            ret = ""+hour+":"+ret;
        }
        return ret;
    }
}
