/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering.multicast;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.mmbase.util.ThreadPools;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

/**
 * ChangesReceiver is a thread object that builds a MultiCast Thread
 * to receive changes from other MMBase Servers.
 *
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @author Nico Klasens
 * @version $Id$
 */
public class ChangesReceiver implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(ChangesReceiver.class);

    private Thread kicker = null;

    /** Queue with messages received from other MMBase instances */
    private final BlockingQueue<byte[]> nodesToSpawn;

    /** address to send the messages to */
    private final InetAddress ia;

    /** Socket to send the multicast packets */
    private MulticastSocket ms;

    /** Port for sending datapackets send by Multicast */
    private final int mport;

    /** Datapacket receive size */
    private final int dpsize;

    private Predicate<byte[]> predicate = Predicates.alwaysTrue();

    /**
     * Construct the MultiCast Receiver
     * @param multicastHost 'channel' of the multicast
     * @param mport port of the multicast
     * @param dpsize datapacket receive size
     * @param nodesToSpawn Queue of received messages
     * @throws UnknownHostException when multicastHost is not found
     */
    public ChangesReceiver(String multicastHost, int mport, int dpsize, BlockingQueue<byte[]> nodesToSpawn)  throws UnknownHostException {
        this.mport = mport;
        this.dpsize = dpsize;
        this.nodesToSpawn = nodesToSpawn;
        this.ia = InetAddress.getByName(multicastHost);
    }

    public void start() {
        if (kicker== null && ia != null) {
            try {
                ms = new MulticastSocket(mport);
                ms.joinGroup(ia);
            } catch(Exception e) {
                log.error(e.getMessage(), e);
            }
            if (ms != null) {
                kicker = new Thread(ThreadPools.threadGroup, this, "MulticastReceiver");
                kicker.setDaemon(true);
                kicker.start();
                log.trace("MulticastReceiver started");
            }
        }
    }

    void stop() {
        MulticastSocket closingMS = ms; // for closing the socket while the thread stops
        ms = null; // the criteria for stopping thread
        try {
            closingMS.leaveGroup(ia);
            closingMS.close();
        } catch (Exception e) {
            // nothing
        }
        if (kicker != null) {
            try {
                kicker.interrupt();
                kicker.setPriority(Thread.MIN_PRIORITY);
                kicker = null;
            } catch (Throwable t) {
            }
        } else {
            log.service("Cannot stop thread, because it is null");
        }
    }

    @Override
    public void run() {
        log.info("MultiCast receiving on " + ms +  " " + ia + ":" + mport);
        // create a datapackage to receive all messages
        byte[] buffer = new byte[dpsize];
        DatagramPacket dp = new DatagramPacket(buffer, dpsize);
        while (ms != null) {
            try {
                // reset datapackage buffer size for re-use
                dp.setLength(dpsize);
                ms.receive(dp);
                byte[] message = new byte[dp.getLength()];

                // the dp.getData array always has dpsize length.
                // That's not what we want. Especially when falling back to legacy, this is translated to a String.
                // which otherwise gets dpsize length (64k!)
                System.arraycopy(dp.getData(), 0, message, 0, dp.getLength());
                if (predicate.apply(message)) {
                    nodesToSpawn.offer(message);
                    if (log.isDebugEnabled()) {
                        log.debug("multicast RECEIVED=> " + dp.getLength() + " bytes from " + ia + ":" + mport + " " + dp.getAddress());
                    }
                }
            } catch (java.net.SocketException se) {
                // generally happens on shutdown (ms==null)
                // if not log it as an error
                if (ms != null) log.error(se.getMessage());
            } catch (Exception f) {
                log.error(f.getMessage(), f);
            }
        }
    }

    public void setPredicate(Predicate<byte[]> predicate) {
        this.predicate = predicate;
    }
}
