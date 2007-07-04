package nl.didactor.functions;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.*;
import java.util.*;

/**
 * Some didactor specific Node functions (implemented as 'bean')
 * @author Michiel Meeuwissen
 * @version $Id: Functions.java,v 1.4 2007-07-04 13:57:38 michiel Exp $
 */
public class Functions {
    protected final static Logger log = Logging.getLoggerInstance(Functions.class);
    
    private Node node;

    public void setNode(Node n) {
        node = n;
    }


    
    /**
     * Returns the locale assciated with this education.
     * Works on education nodes.
     */
    public Locale educationLocale() {
        NodeList providers = node.getRelatedNodes("providers");
        Node provider = providers.getNode(0);
        String providerPath = provider.getStringValue("path");
        String educationPath = node.getStringValue("path");
        Locale language = org.mmbase.util.LocalizedString.getLocale(provider.getStringValue("locale"));
        
        return new Locale(language.getLanguage(), language.getCountry(), 
                          providerPath + ("".equals(providerPath) || "".equals(educationPath) ? "" : "_") + educationPath);

    }

    /**
     * A node is active if it is related to an mmevent which is active.
     * Works on any node wich can have related mmevents.
     */
    public boolean active() {
        Date now = new Date();
        NodeList mmevents = node.getRelatedNodes("mmevents");
        NodeIterator ni = mmevents.nodeIterator();
        while (ni.hasNext()) {
            Node node = ni.nextNode();
            Date start = node.getDateValue("start");
            Date stop  = node.getDateValue("stop");
            if (start.before(now) && stop.after(now)) {
                return true;
            }
        }
        return false;
    }

    
    /**
     * Generate a 8-character base username, consisting of the first
     * character of the firstname, and the entire lastname. Strip out
     * all non-letter characters, and append a number if the account
     * already exists.
     * Works on people nodes only.
     */
    public String peopleGenerateUserName() {
        String firstName = node.getStringValue("firstname");
        if (firstName.length() > 0) {
            firstName = firstName.substring(0, 1);
        }
        String uname = firstName + node.getStringValue("lastname");
        if (uname.length() > 8) {
            uname = uname.substring(0, 8);
        }
        int seq = 0;
        String value = uname;
        try {
            while (true) {
                NodeQuery query = node.getNodeManager().createQuery();
                query.setConstraint(Queries.createConstraint(query, "username", Queries.getOperator("eq"), value));
                if (Queries.count(query) == 0) {
                    return value;
                }
                value = uname + seq;
                seq++;
            }
        } catch (Exception e) {
            log.warn(e);
            return uname + System.currentTimeMillis();
        }
    }

}
