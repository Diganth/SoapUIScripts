/*
 * Copyright (C) 2014 Diganth Aswath <diganth2004@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package soapUIScripts

import com.eviware.soapui.impl.wsdl.panels.support.MockTestRunContext
import com.eviware.soapui.model.project.ProjectFactoryRegistry
import com.eviware.soapui.model.support.ModelSupport
import com.eviware.soapui.support.UISupport
import javax.net.ssl.SSLHandshakeException
import com.eviware.soapui.LogMonitor.*
import com.eviware.soapui.SoapUI
import soapUIScripts.*

/**
 *
 * @author Diganth Aswath <diganth2004@gmail.com>
 */
class captureURL {
	
    def util, log, url, modded_url, testStepName;
    captureURL(utility util, logger log){
        //SoapUI.log("In constructor of captureURL");
        this.util = util;
        this.log  = log;
    }
    def modURLString(url){
        url.replaceFirst(/^https/, "http")
    }
	
    //Function to create file with TestStep name depending on imagetype
    def file_create (imageType, url){
        def fileName = util.readProperty("LogFileLocation")+testStepName+util.readProperty("LoopCount")+imageType
        // Write output to file created
        writetoFile (fileName, url)
    }
        
    // Function to write responseURL to the file.
    def writetoFile (fileName,  url){
        //def file = new File(fileName)
        //file.write(url, "UTF-8") //Writing response into the file created
        def URLimgfile = new FileOutputStream(fileName)
        def input;
        def out = new BufferedOutputStream(URLimgfile)
        try{
            input = new URL(url).openStream()
        }
        catch (SSLHandshakeException e){
            log.error ("SSL Exception in testStep ->" +testStepName)
        }
        if (input == null){
            modded_url = modURLString(url)
            log.info ("Using Modded URL")
            out << new URL(modded_url).openStream()
        } 
        else{
            log.info ("Used URL as is.")
            out << input
        }
        out.close()
    }
	
    // Function that controls the logic of iterating through the testSteps to obtain
    // URL from the response.
    def printURL (def url, def testStepName) {
        this.url = url;
        this.testStepName = testStepName;
        log.info("URL -> " +url);
        // Checking if URL string is empty
        if(url?.trim()) {
            if(url.contains(".pdf")) {
                file_create (".pdf", url)
                log.info("Captured label for ->" + testStepName);
            }
            else if (url.contains(".png")){
                file_create (".png", url)
                log.info("Captured label for ->" + testStepName);
            }
            else{
                log.info("Unable to capture label for ->"+ testStepName +"as it doesnot match .pdf or .png");
            }
        }
        else {
            log.error("Unable to capture label for ->"+ testStepName)
        }
    }
}
