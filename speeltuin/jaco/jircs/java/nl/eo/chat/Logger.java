/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Hashtable;

/**
 * The logger class can be used to log information to a file.
 *
 * @author Jaco de Groot
 */
public class Logger {
    private String propertiesFilename;
    private File propertiesFile;
    private long lastModified = Long.MIN_VALUE;
    private String logFilename;
    private boolean logPerChannel = false;
    private String propertiesPrefix;
    private BufferedWriter writer;
    private SimpleDateFormat df;
    private Hashtable channelLogFiles;
    private int logLevel = LOG_LEVEL_JOIN;
    public static final int LOG_LEVEL_SILENT = 0;
    public static final int LOG_LEVEL_ERROR = 1;
    public static final int LOG_LEVEL_EXCEPTION = 2;
    public static final int LOG_LEVEL_INFO = 3;
    public static final int LOG_LEVEL_CONNECT = 4;
    public static final int LOG_LEVEL_LOGIN = 5;
    public static final int LOG_LEVEL_JOIN = 6;
    public static final int LOG_LEVEL_MESSAGE = 7;
    public static final int LOG_LEVEL_DEBUG = 8;

    public Logger(String propertiesFilename, String propertiesPrefix) {
        this.propertiesPrefix = propertiesPrefix;
        propertiesFile = new File(propertiesFilename);
        df = new SimpleDateFormat("yyyyMMdd HH:mm:ss,SSS");
        checkProperties();
    }

    public void error(String message) {
        error(message, true);
    }

    private void error(String message, boolean checkProperties) {
        if (checkProperties) {
            checkProperties();
        }
        if (logLevel >= LOG_LEVEL_ERROR) {
            log("Error: " + message);
        }
    }

    public void exception(Exception e) {
        checkProperties();
        if (logLevel >= LOG_LEVEL_EXCEPTION) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            log("Exception :" + sw.toString());
        }
    }

    public void info(String message) {
        checkProperties();
        if (logLevel >= LOG_LEVEL_INFO) {
            log("Info: " + message);
        }
    }

    public void connect(String hostname) {
        checkProperties();
        if (logLevel >= LOG_LEVEL_CONNECT) {
            log("Connect " + hostname + ".");
        }
    }

    public void disconnect(String hostname, String reason) {
        checkProperties();
        if (logLevel >= LOG_LEVEL_CONNECT) {
            log("Disconnect " + hostname + " (" + reason + ").");
        }
    }

    public void disconnect(String hostname, String reason, String nick) {
        checkProperties();
        if (logLevel >= LOG_LEVEL_CONNECT) {
            log("Disconnect " + nick + "@" + hostname + " (" + reason + ").");
        }
    }

    public void login(String nick, String hostname) {
        checkProperties();
        if (logLevel >= LOG_LEVEL_LOGIN) {
            log("Login " + nick + " from " + hostname + ".");
        }
    }

    public void logout(String nick, String hostname) {
        checkProperties();
        if (logLevel >= LOG_LEVEL_LOGIN) {
            log("Logout " + nick + " from " + hostname + ".");
        }
    }

    public void join(String channel, String nick) {
        checkProperties();
        if (logLevel >= LOG_LEVEL_JOIN) {
            log("Join " + channel + " by " + nick + ".");
        }
    }

    public void part(String channel, String nick) {
        checkProperties();
        if (logLevel >= LOG_LEVEL_JOIN) {
            log("Part " + channel + " by " + nick + ".");
        }
    }

    public void message(String source, String destination, String message) {
        checkProperties();
        if (logLevel >= LOG_LEVEL_MESSAGE) {
            //log messages to channels in seperate files.
            if (logPerChannel) {
              log(source + ": " + message,destination);
            }
            log("Message from " + source + " to " + destination + " (" + message + ").");
        }
    }

    public void filteredMessage(String source, String destination, String message) {
        checkProperties();
        if (logLevel >= LOG_LEVEL_MESSAGE) {
            log("Filtered message from " + source + " to " + destination + " (" + message + ").");
        }
    }

    public void debug(String message) {
        checkProperties();
        if (logLevel >= LOG_LEVEL_DEBUG) {
            log("Debug info: " + message);
        }
    }

    protected void log(String message) {
        try {
            writer.write(df.format(new Date(System.currentTimeMillis())) + " " + message);
            writer.newLine();
            writer.flush();
        } catch(Exception e) {
            System.err.println("Can not write to file '" + logFilename + "' (" + e.getMessage() + ") continue on System.err." );
            System.err.println(df.format(new Date(System.currentTimeMillis())) + " " + message);
        }
    }
    
    /** method for logging per channel
     *
    */
    protected void log(String message,String destination) {
        //only log if it's a channel (ergo start with a #)
        if (destination.startsWith("#")) {
            try {
              //check if there's already a writer for this channel. if not, create one.
              if (channelLogFiles.containsKey(destination)) { 
                  BufferedWriter wr = (BufferedWriter)channelLogFiles.get(destination);
                  wr.write(df.format(new Date(System.currentTimeMillis())) + " " + message);
                  wr.newLine();
                  wr.flush();
              } else {
                  String lf = "logs/" + destination + ".log";
                  FileWriter fw = null;
                  try {
                      fw = new FileWriter(lf, true);
                      BufferedWriter wr = new BufferedWriter(fw);
                      wr.write(df.format(new Date(System.currentTimeMillis())) + " " + message);
                      wr.newLine();
                      wr.flush();              
                      channelLogFiles.put(destination,wr);                  
                  } catch(IOException e) {
                      error("Can not open file '" + lf + "' (" + e.getMessage() + ")", false);
                  }
              }
            } catch(Exception e) {
                System.err.println("Can not write to file '" + logFilename + "' (" + e.getMessage() + ") continue on System.err." );
                System.err.println(df.format(new Date(System.currentTimeMillis())) + " " + message);
            }
          }
    }

    
    
    protected void checkProperties() {
        long l = propertiesFile.lastModified();
        if (lastModified < l) {
            Properties properties = new Properties();
            boolean stop = false;
            try {
                properties.load(new FileInputStream(propertiesFile));
            } catch(FileNotFoundException e) {
                error("Could not find properties file '" + propertiesFilename + "'.", false);
                stop = true;
            } catch(IOException e) {
                error("Could not read properties file '" + propertiesFilename + "': " + e.getMessage(), false);
                stop = true;
            }
            if (!stop) {
                String lf = properties.getProperty(propertiesPrefix + "logfile");
                if (lf != null && (logFilename == null || !logFilename.equals(lf))) {
                    FileWriter fw = null;
                    try {
                        fw = new FileWriter(lf, true);
                    } catch(IOException e) {
                        error("Can not open file '" + lf + "' (" + e.getMessage() + ")", false);
                        stop = true;
                    }
                    if (!stop) {
                        logFilename = lf;
                        writer = new BufferedWriter(fw);
                    }
                }
                String ll = properties.getProperty(propertiesPrefix + "loglevel");
                if (ll != null) {
                    if (ll.equals("silent")) {
                        logLevel = LOG_LEVEL_SILENT;
                    } else if (ll.equals("error")) {
                        logLevel = LOG_LEVEL_ERROR;
                    } else if (ll.equals("info")) {
                        logLevel = LOG_LEVEL_INFO;
                    } else if (ll.equals("connect")) {
                        logLevel = LOG_LEVEL_CONNECT;
                    } else if (ll.equals("login")) {
                        logLevel = LOG_LEVEL_LOGIN;
                    } else if (ll.equals("join")) {
                        logLevel = LOG_LEVEL_JOIN;
                    } else if (ll.equals("message")) {
                        logLevel = LOG_LEVEL_MESSAGE;
                    } else if (ll.equals("debug")) {
                        logLevel = LOG_LEVEL_DEBUG;
                    }
                }
                //enable loggin per channel
                String lpc = properties.getProperty(propertiesPrefix + "logperchannel");
                if (lpc != null && lpc.equals("true")) {
                    channelLogFiles = new Hashtable();
                    logPerChannel = true;
                }                
            }
            lastModified = l;
        }
    }

}
