/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging.packagehandlers;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.Versions;
import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.packagehandlers.*;
import org.mmbase.applications.packaging.providerhandlers.*;
import org.mmbase.applications.packaging.installhandlers.*;


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

import org.w3c.dom.*;

/**
 * DisplayHtmlPackage, Handler for html packages
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class DisplayHtmlPackage extends BasicPackage implements PackageInterface {


    private static Logger log = Logging.getLoggerInstance(DisplayHtmlPackage.class.getName());


    public DisplayHtmlPackage() {
    }

    public boolean install() {
	boolean result=true; // needs to be set to false on a error
	try {

		// step1
		installStep step=getNextInstallStep();
		step.setUserFeedBack("display/html installer started");

		// step 2
		step=getNextInstallStep();
		step.setUserFeedBack("receiving package ..");
		JarFile jf=getJarFile();
		step.setUserFeedBack("receiving package ... done ("+jf+")");


		// step 3
		step=getNextInstallStep();
		step.setUserFeedBack("checking dependencies ..");
		if (dependsInstalled(jf,step)) {

		step.setUserFeedBack("checking dependencies ... done");

		// step 4
		step=getNextInstallStep();
		step.setUserFeedBack("installing html pages ..");
		installPages(jf,step);
		step.setUserFeedBack("installing html pages ... done");


		// step 5
		step=getNextInstallStep();
		step.setUserFeedBack("updating mmbase registry ..");
		updateRegistryInstalled();
		step.setUserFeedBack("updating mmbase registry ... done");
		} else {
			step.setUserFeedBack("checking dependencies ... failed");
			setState("failed");
			result=false;
		}
		// step 6
		step=getNextInstallStep();
		step.setUserFeedBack("display/html installer ended");

	} catch (Exception e) {
		log.error("install crash on : "+this);
		result=false;
	}
	return result;
    }


    public boolean uninstall() {
	try {

		// step1
		installStep step=getNextInstallStep();
		step.setUserFeedBack("display/html uninstaller started");

		// step 3
		step=getNextInstallStep();
		step.setUserFeedBack("updating mmbase registry ..");
		updateRegistryUninstalled();
		step.setUserFeedBack("updating mmbase registry ... done");

		// step 4
		step=getNextInstallStep();
		step.setUserFeedBack("display/html installer ended");

	} catch (Exception e) {
		log.error("install crash on : "+this);
	}
	return true;
    }

    private boolean installPages(JarFile jf,installStep step) {
	Enumeration e=jf.entries();
	while (e.hasMoreElements()) {
		ZipEntry zippy=(ZipEntry)e.nextElement();
		
		// this is just a demo version, the html package people should figure
		// out the real format.
		String name=zippy.getName();
        	String htmldir = MMBaseContext.getHtmlRoot()+File.separator;
		
		// only unpack all thats in the html dir
		if (name.indexOf("html/")==0) {
			installStep substep=step.getNextInstallStep();
			// remove the "html/" to get the real install base
			name=name.substring(5);

		 	// check if its a dir or a file
			if (zippy.isDirectory()) {
			        File d = new File(htmldir+name);
          			if (!d.exists()) {
					substep.setUserFeedBack("creating dir : "+htmldir+name+".. ");
					d.mkdir();
					substep.setUserFeedBack("creating dir : "+htmldir+name+".. done");
				}
			} else {
				substep.setUserFeedBack("creating file : "+htmldir+name+".. ");
				try {
				InputStream in=jf.getInputStream(zippy);	
				BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(htmldir+name));
				int val;
                    		while ((val = in.read()) != -1) {
                      			out.write(val);
				}
				out.close();
				substep.setUserFeedBack("creating file : "+htmldir+name+".. done");
				} catch(IOException f) {
					substep.setUserFeedBack("creating file : "+htmldir+name+".. failed");
					f.printStackTrace();
				}
			}
		}
	}
	return true;
    }

}
