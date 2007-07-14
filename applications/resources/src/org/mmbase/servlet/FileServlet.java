/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import org.mmbase.module.core.*;

import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.util.regex.*;

import java.io.*;
import java.text.*;

import org.mmbase.util.logging.*;


/**
 * Straight-forward filter which can serve files from one directory (the directory 'files' in the
 * mmbase 'datadir') outside the web application root.
 *
 * @version $Id: FileServlet.java,v 1.5 2007-07-14 14:03:40 michiel Exp $
 * @author Michiel Meeuwissen
 * @since  MMBase-1.9
 * @see    AttachmentServlet
 */
public class FileServlet extends BridgeServlet {
    private static Logger log;

    private static File files = null;
    
    private Pattern ignore = Pattern.compile("");

    //private static final Properties properties= new Properties();

    public void init() throws ServletException {
        super.init();
        String ig = getInitParameter("ignore");
        if (ig != null && ig.length() > 0) {
            ignore = Pattern.compile(ig);
        }
        log = Logging.getLoggerInstance(FileServlet.class);
    }
    public static void init(HttpServletResponse res) {
        if (files == null) {
            if (log == null) {
                log = Logging.getLoggerInstance(FileServlet.class);
            }
            if (mmbase == null) throw new RuntimeException("MMBase is null");
            String dataDir = mmbase.getInitParameter("datadir");
            if (dataDir != null && ! "".equals(dataDir)) {
                File data;
                if (dataDir.startsWith(".")) {
                    data = new File(new File(MMBaseContext.getServletContext().getRealPath("WEB-INF/data/")), dataDir);
                } else if (dataDir.startsWith("/")) {
                    data = new File(dataDir);
                } else {
                    data = new File(MMBaseContext.getServletContext().getRealPath(dataDir));
                }
                files = new File(data, "files");
            } else {
                files = new File(MMBaseContext.getServletContext().getRealPath("WEB-INF/data/files"));
            }

            
            if (! files.exists()) {
                if (files.mkdirs()) {
                    log.info("Directory " + files + " was created");
                } else {
                    log.warn("Directory " + files + " could not be created");
                }
            }
            log.info("Using datadir " + files);

        }
        
    }


    protected boolean checkInited(HttpServletResponse res) throws ServletException, IOException  {
        boolean inited = super.checkInited(res);
        if (inited) {
            init(res);
            /*
            try {
                properties.load(new FileInputStream(new File(files, ".mmbaseowners")));
            } catch (Exception e) {
                log.warn(e);
            }
            */
        }
        return inited;
    }
    public String getServletInfo()  {
        return "Serves files from <MMBase data directory>/files";
    }

    protected Map<String, Integer> getAssociations() {
        Map<String, Integer> a = super.getAssociations();
        a.put("files",      50);  
        return a;
    }

    public static File getDirectory() {
        return files;
    }    

    public static File getFile(String pathInfo, ServletResponse res) {
        if (res != null) init((HttpServletResponse) res);
        if (pathInfo == null) {
            return files;
        } else {
            return new File(files, pathInfo.replace("/", File.separator));
        }
    }

    protected boolean canRead(HttpServletRequest req, File f) {
        // something with mmbase security ?
        return f.canRead();
    }

    protected boolean ignores(String pi) {
        return pi != null && (ignore.matcher(pi).matches());
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pi = req.getPathInfo();
        if (ignores(pi)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "The file '" + pi + "' is not served");
            return;
        }

        File file = getFile(pi, resp);
        if (! file.exists()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "The file '" + pi + "' does not exist");
            log.debug("" + file + " does not exist");
            return;
        }
        if (! canRead(req, file)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "The file '" + pi + "' may not be read");
            log.debug("" + file + " may not be read");
            return;
        }
        if (file.isDirectory()) {
            listing(req, resp, file);
            return;
        }
        resp.setContentType(getServletContext().getMimeType(file.getName()));
        resp.setContentLength((int) file.length());
        BufferedOutputStream out = new BufferedOutputStream(resp.getOutputStream());
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        byte[] buf = new byte[1024];
        int b = 0;
        while ((b = in.read(buf)) != -1) {
            out.write(buf, 0, b);
        }
        out.flush();
        in.close();
        out.close();
    }


    protected void listing(HttpServletRequest req, HttpServletResponse resp, File directory) throws IOException {
        if ("true".equals(getInitParameter("listings"))) {
            resp.setContentType("application/xhtml+xml"); // We hate IE anyways.
            StringBuilder result = new StringBuilder();
            result.append("<?xml version='1.0'?>\n");
            result.append("<html xmlns='http://www.w3.org/1999/xhtml'>");
            result.append("<head>");
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || ! pathInfo.endsWith("/")) {
                resp.sendRedirect(req.getContextPath() + req.getServletPath() + (pathInfo == null ? "" : pathInfo) + "/");
                return;
            }
            result.append("<title>Directory Listing For " + pathInfo + "</title>");            
            result.append("<link rel='stylesheet' href='" + req.getContextPath() + "/mmbase/style/css/mmbase.css' type='text/css' />");
            result.append("</head>");
            result.append("<body class='filelisting'>");
            result.append("<h1>Directory Listing For " + pathInfo + "</h1>");
            result.append("<table>");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:m");
            result.append("<tr><td class='lastmodified'>" + df.format(new Date(directory.lastModified())) + "</td><td class='filename'><a href='.'>./</a></td></tr>");
            if (! pathInfo.equals("/")) {
                result.append("<tr><td class='lastmodified'>" + df.format(new Date(directory.getParentFile().lastModified()))  +"</td><td class='filename'><a href='..'>../</a></td></tr>");
            }
            for (File file : directory.listFiles()) {
                String name = file.getName() + (file.isDirectory() ? "/" : "");
                if (ignores(pathInfo + name)) continue;
                
                result.append("<tr><td class='lastmodified'>" + df.format(new Date(file.lastModified())) + "</td><td class='filename'>");
                if (canRead(req, file)) {
                    result.append("<a href='" + name + "'>" + name + "</a>");
                } else {
                    result.append(name);
                }
                result.append("</td></tr>");
            }
            result.append("</table>");
            result.append("<h3>" + org.mmbase.Version.get() + "</h3>");
            result.append("</body>");
            result.append("</html>");
            try {
                byte [] bytes = result.toString().getBytes("UTF-8");
                resp.setContentLength(bytes.length);
                BufferedOutputStream out = new BufferedOutputStream(resp.getOutputStream());
                out.write(bytes);
                out.flush();
            } catch (java.io.UnsupportedEncodingException uue) {
                // cannot happen UTF-8 is supported.
                log.fatal(uue);
            }
            return;
        } else {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Directory listings are not enabled. (see web.xml)");
            return;            
        }
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        File file = getFile(req.getPathInfo(), resp);
        if (file.exists()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "The file '" + req.getPathInfo() + "' already exists");
            return;
        }
        /*
        Cloud cloud = getCloud(readQuery(req.getQueryString()));
        if (cloud.getUser().getRank() == Rank.ANONYMOUS) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "The file '" + req.getPathInfo() + "' already exists");
            return;
        }
        */
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        BufferedInputStream in = new BufferedInputStream(req.getInputStream());
        byte[] buf = new byte[1024];
        int b = 0;
        while ((b = in.read(buf)) != -1) {
            out.write(buf, 0, b);
        }
        out.flush();
        in.close();
        out.close();
    }

    protected long getLastModified(HttpServletRequest req) {
        return getFile(req.getPathInfo(), null).lastModified();
    }
}


