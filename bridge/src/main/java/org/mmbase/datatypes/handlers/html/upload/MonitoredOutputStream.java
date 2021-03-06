/* License:
*   Use this however/wherever you like, just don't blame me if it breaks anything.
*
* Credit:
*   If you're nice, you'll leave this bit:
*
*   Class by Pierre-Alexandre Losson -- http://www.telio.be/blog
*   email : plosson@users.sourceforge.net
*/
package org.mmbase.datatypes.handlers.html.upload;

import java.io.OutputStream;
import java.io.IOException;

/**
 *
 * @author Pierre-Alexandre Losson
 * @author Michiel Meeuwissen (adapted for MMBase)
 * @version $Id$
 * @since MMBase-1.9.2
 */

public class MonitoredOutputStream extends OutputStream {
    private final OutputStream target;
    private final OutputStreamListener listener;
    public MonitoredOutputStream(OutputStream target, OutputStreamListener listener) {
        this.target = target;
        this.listener = listener;
        this.listener.start();
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
        target.write(b,off,len);
        listener.bytesRead(len - off);
    }

    @Override
    public void write(byte b[]) throws IOException {
        target.write(b);
        listener.bytesRead(b.length);
    }

    public void write(int b) throws IOException {
        target.write(b);
        listener.bytesRead(1);
    }

    @Override
    public void close() throws IOException {
        target.close();
        listener.done();
    }

    @Override
    public void flush() throws IOException {
        target.flush();
    }
}
