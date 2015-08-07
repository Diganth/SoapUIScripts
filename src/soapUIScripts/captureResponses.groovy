/*
 * Copyright (C) 2015 Diganth Aswath <diganth2004@gmail.com>
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
class captureResponses {
    def util, log, testStepName, serviceType, layout, response;
    captureResponses(utility util, logger log){
        SoapUI.log("SoapUIScript.jar::In constructor of captureResponses");
        this.util = util;
        this.log  = log;
    }
    
    //Function to create file with TestStep name depending on imagetype
    def file_create (response){
        def fileName;
        if (serviceType == null)
            fileName = util.readProperty("LogFileLocation")+testStepName+"_"+layout+".txt";
        else if (serviceType == null && layout == null)
            fileName = util.readProperty("LogFileLocation")+testStepName+"_"+util.todayTime()+".txt";
        else if (layout == null)
            fileName = util.readProperty("LogFileLocation")+testStepName+"_"+serviceType+".txt";
        else
            fileName = util.readProperty("LogFileLocation")+testStepName+".txt";
        // Write output to file created
        writetoFile (fileName, response)
    }
    
    // Function to write response to the file.
    def writetoFile (fileName,  response){
        def ResponseFile = new FileOutputStream(fileName)
        def out = new BufferedOutputStream(ResponseFile)
        log.info ("Writing response to file.");
        out << response;
        out.close()
    }
    
    public String saveResponse (def response, def testStepName, def serviceType, def layout) {
        
        log.info("In SaveResponse() for ->" + testStepName);
        String errorFlag = null;
        this.response = response;
        this.testStepName = testStepName;
        this.serviceType = serviceType;
        this.layout = layout;
        
        if (response != null){
            file_create(response);
            log.info("Captured Response for ->" + testStepName);
            errorFlag = "No Error";
            return errorFlag;
        }
        else{
             log.error("Unable to capture response for ->"+ testStepName);
            return "Unable to capture response for ->"+ testStepName;
        }
    }
}

