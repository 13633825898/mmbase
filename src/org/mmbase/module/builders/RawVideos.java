/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import java.io.*;

import javax.servlet.http.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class RawVideos extends MMObjectBuilder {
 	public boolean replaceCache=true;

	public RawVideos() {
		// like rawaudios, has no contructor also
	}
/*
	public RawVideos(MMBase m) {
		this.mmb=m;
		this.tableName="rawvideos";
		this.description="Raw video parts, normally made by jwm";
		this.dutchSName="raw video";
		init();
		m.mmobjs.put(tableName,this);
		if (getMachineName().equals("station") || getMachineName().equals("noise") || getMachineName().equals("beep") || getMachineName().equals("sjouw")) {
			new RawVideosProbe(this);
		} else {
			System.out.println("RawVideos -> Probe NOT started for rawvideos");
		}
	}


	public boolean create() {
		// create the main object table
		// informix
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create row type "+mmb.baseName+"_"+tableName+"_t ("
				+"id integer not null"
				+", status integer not null"
				+", format integer not null"
				+", speed integer not null"
				+", channels integer not null"
				+", url varchar(255)"
				+", issurestream integer not null"
				+", cpu varchar(32)) under "+mmb.baseName+"_object_t");
			System.out.println("Created "+tableName);
			stmt.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("can't create type "+tableName);
			e.printStackTrace();
		}
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t ("
				+"primary key(id,format,speed,channels)) under "+mmb.baseName+"_object");
			stmt.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("can't create table "+tableName);
			e.printStackTrace();
		}
		return(false);
	}


	public int insert(String owner,MMObjectNode node) {
		int id=node.getIntValue("id");
		int status=node.getIntValue("status");
		int format=node.getIntValue("format");
		int speed=node.getIntValue("speed");
		int channels=node.getIntValue("channels");
		String url=node.getStringValue("url");
		int	issurestream=node.getIntValue("issurestream");
		String cpu=node.getStringValue("cpu");
		int number=super.insert(owner,""+id+","
						+status+","
						+format+","
						+speed+","
						+channels+",'"
						+url+"',"
						+issurestream+",'"
						+cpu+"'");

		// fast hack this should works because imports of wav are allways done
		if (status==1) {
			CDTracks bul=(CDTracks)mmb.getMMObject("cdtracks");
			bul.getTrack(id);
		}	
		return(number);
	}

*/

	public String getGUIIndicator(MMObjectNode node) {
		String str=node.getStringValue("number");
		if (str.length()>15) {
			return(str.substring(0,12)+"...");
		} else {
			return(str);
		}
	}

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("status")) {
			int val=node.getIntValue("status");
			switch(val) {
				case 1: return("Verzoek");
				case 2: return("Onderweg");
				case 3: return("Gedaan");
				case 4: return("Bron");
				default: return("Onbepaald");
			}
		} else if (field.equals("format")) {
			int val=node.getIntValue("format");
			switch(val) {
				case 1: return("mpg");
				case 2: return("rm");
				case 3: return("mov");
				// case 4: return("pcm");
				// case 5: return("mp2");
				default: return("Onbepaald");
			}
		} else if (field.equals("channels")) {
			int val=node.getIntValue("channels");
			switch(val) {
				case 1: return("mono");
				case 2: return("stereo");
				default: return("Onbepaald");
			}
		} else if (field.equals("issurestream")) {

			int val = node.getIntValue("issurestream");
			switch(val)
			{
				case 0	: return ("false");
				case 1	: return ("true");
				default	: return ("onbepaald");
			}
		}
		
		return(null);
	}

	/**
	* get new node
	*/
	public MMObjectNode getNewNode(String owner) {
		MMObjectNode node=super.getNewNode(owner);
		// readCDInfo();
		// if (diskid!=null) node.setValue("discId",diskid);
		// if (playtime!=-1) node.setValue("playtime",playtime);
		return(node);
	}


	public Object getValue(MMObjectNode node,String field) {
		if (field.equals("str(status)")) {
			int val=node.getIntValue("status");
			switch(val) {
				case 1: return("Verzoek");
				case 2: return("Onderweg");
				case 3: return("Gedaan");
				case 4: return("Bron");
				default: return("Onbepaald");
			}
		} else if (field.equals("str(channels)")) {
			int val=node.getIntValue("channels");
			switch(val) {
				case 1: return("Mono");
				case 2: return("Stereo");
				default: return("Onbepaald");
			}
		} else if (field.equals("str(format)")) {
			int val=node.getIntValue("format");
			switch(val) {
				case 1: return("mpg");
				case 2: return("rm");
				case 3: return("mov");
				default: return("Onbepaald");
			}
		} else if (field.equals("str(issurestream)")) {
			int val=node.getIntValue("issurestream");
			switch( val )
			{
				case 0	: return ("false");
				case 1	: return ("true");
				default	: return ("onbepaald");
			}
		}
		return(null);
	}


	public boolean removeVideo(int id) {
		Connection con;
		boolean rtn=false;
		MMObjectNode node;
		Enumeration videos;

		videos=search("WHERE id="+id);
		while(videos.hasMoreElements()) {
			node=(MMObjectNode)videos.nextElement();
			System.out.println("RawVideos -> Zapping "+node.getIntValue("number")+","+node.getStringValue("url"));
			removeRelations(node);
			removeNode(node);
			zapPhysical(node);
			rtn=true;
		}
		if (true) {
			// For every format check the directory
			// MP3 
			// Nothing yet
			// RA
			removeRM(id);
			// WAV
			// Nothing yet
		}
		return(rtn);
	}

	private void zapPhysical(MMObjectNode node) {
		int id,iformat;
		int speed,channels;
		String path;
		String name;

		id=node.getIntValue("id");
		iformat=node.getIntValue("format");
		speed=node.getIntValue("speed")/1000;
		channels=node.getIntValue("channels");
		switch(iformat) {
			case 1: // mp3
				// Nothing for now
				path="/data/video/mp3/"+id;
				break;
			case 2: // ra
				// Decode .ra file name
				path="/data/video/ra/"+id;
				name=speed+"_"+channels+".rm";
				removeFile(path,name);
				break;
			case 3: // wav
				// Nothing for now
				path="/data/video/mov/"+id;
				break;
			default: // Unknown
				break;
		}
	}

	public String getFullName(MMObjectNode node) {
		int id,iformat;
		int speed,channels;
		String path;
		String name;

		id=node.getIntValue("id");
		iformat=node.getIntValue("format");
		speed=node.getIntValue("speed")/1000;
		channels=node.getIntValue("channels");
		switch(iformat) {
			case 1: // mpg
				path="/data/video/mpg/"+id+"/"+speed+"_"+channels+".mpg";
				break;
			case 2: // ra
				path="/data/video/rm/"+id+"/"+speed+"_"+channels+".rm";
				break;
			case 3: // mov 
				path="/data/video/mov/"+id+".mov";
				break;
			default: // Unknown
				path=null;
				break;
		}
		return(path);
	}

	private void removeRM(int id) {
		String path="/data/video/rm";
		String name="real.txt";
		removeFile(path,id+"/"+name);
		removeFile(path,""+id);
	}

	private void removeFile(String path,String name) {
		File f;

		f=new File(path,name);
		if (f.isDirectory()) {
			System.out.println("Removing dir "+f.getPath());
			if (!f.delete()) {
				System.out.println("Can't delete directory "+f.getPath());
			}
		} else {
			System.out.println("Removing file "+f.getPath());
			if (!f.delete()) {
				System.out.println("Can't delete file "+f.getPath());
			}
		}
	}
}
