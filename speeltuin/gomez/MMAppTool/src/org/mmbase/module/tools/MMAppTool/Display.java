/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.tools.MMAppTool;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import org.mmbase.util.*;

public class Display extends Frame implements WindowListener,ActionListener {

	private MenuBar bar;
	private AppCanvas can;
	private MMAppTool parent;

	public Display(MMAppTool parent) {
		this.parent=parent;
		setSize(640,480);
		bar=createMenus();
		setMenuBar(bar);
		setTitle("MMAppTool - Display");
		can=new AppCanvas(this);
		add(can);
	}


	public Display(MMAppTool parent,String filename) {
		this.parent=parent;
		setSize(640,480);
		bar=createMenus();
		setMenuBar(bar);
		setTitle("MMAppTool - Display");
		can=new AppCanvas(this);
		add(can);

		XMLApplicationReader app=new XMLApplicationReader(filename);
		if (can!=null) {
			can.setApplication(app);
			can.repaint();
		}

		XMLAppToolReader con=new XMLAppToolReader("/tmp/mmapptoolconfig.xml");
		if (con.ok) {
			can.setBackGroundColor(con.getColor("backgroundcolor"));
			can.setObjectColor(con.getColor("objectcolor"));
			can.setTextColor(con.getColor("textcolor"));
			can.setLineColor(con.getColor("linecolor"));
			can.setActiveColor(con.getColor("activecolor"));

			
			for (Enumeration e=can.getBuilderOvals().elements();e.hasMoreElements();) {
				BuilderOval b=(BuilderOval)e.nextElement();
				String name=b.getName();
				int x=con.getX(name);
				int y=con.getY(name);
				if (x!=-1 && y!=-1) {
					b.setX(x);
					b.setY(y);
				}
			}
		}
	}

	public void windowDeiconified(WindowEvent event) {
	}

	public void windowIconified(WindowEvent event) {
	}

	public void windowDeactivated(WindowEvent event) {
	}

	public void windowActivated(WindowEvent event) {
	}

	public void windowOpened(WindowEvent event) {
	}

	public void windowClosed(WindowEvent event) {
		System.out.println("Window Closed");
	}

	public void windowClosing(WindowEvent event) {
		System.out.println("Window Closing");
	}


	private MenuBar	createMenus() {
		MenuBar bar=new MenuBar();

		// create filemenu
		Menu m = new Menu("File");
		m.add(new MenuItem("Open",new MenuShortcut('O')));
		m.add(new MenuItem("Snapshot"));
		m.add(new MenuItem("Exit",new MenuShortcut('E')));

		m.addActionListener(this);
		bar.add(m);	
		
		return(bar);
	}

	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		if (cmd.equals("Exit")) {
			parent.doExit();
		} else if (cmd.equals("Open")) {
			doOpen();
		} else if (cmd.equals("Snapshot")) {
			doSnapshot();
		} else {
			System.out.println("Unknown="+cmd);
		}
	}

	private void doSnapshot() {
		if (can!=null) {
			XMLAppToolWriter.writeXMLFile(can,"/tmp/apptoolcondig.xml");	
		}
	}

	private void doOpen() {
		FileDialog df=new FileDialog(this,"Open Application xml",FileDialog.LOAD);
		df.show();
		String filename=df.getDirectory()+df.getFile();

		XMLApplicationReader app=new XMLApplicationReader(filename);
		if (can!=null) {
			can.setApplication(app);
			can.repaint();
		}
	}
}
