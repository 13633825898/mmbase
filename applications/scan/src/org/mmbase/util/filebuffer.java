package org.mmbase.util;

import java.util.Date;

public class filebuffer {

	public Date lastmod;
	public byte data[]=null;
	public Object obj;
	public int filesize=0;
	public String mimesuper;
	public String mimesub;
	public String mimetype;

	public filebuffer(Object o) {
		obj=o;
	}

	public filebuffer(byte[] data) {
		this.data=data;
	}

	public filebuffer(int len) {
		data = new byte[len];
		filesize=len;
	}
}

