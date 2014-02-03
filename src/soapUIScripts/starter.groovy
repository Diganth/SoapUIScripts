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

import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep
import com.eviware.soapui.impl.wsdl.panels.support.MockTestRunContext
import com.eviware.soapui.model.project.ProjectFactoryRegistry
import com.eviware.soapui.model.support.ModelSupport
import com.eviware.soapui.support.UISupport
import com.eviware.soapui.LogMonitor.*
import com.eviware.soapui.SoapUI
import soapUIScripts.*
/**
 *
 * @author Diganth Aswath <diganth2004@gmail.com>
 */
class starter {
    def context, filepath, util, logger;
    def captureURL;
    starter(def context, String filepath){
        this.context = context
        this.filepath = filepath
        //SoapUI.log("Creating class for util");
        util = new utility(context, filepath); 
        //SoapUI.log("Creating class for logger");
        logger = new logger(util);
        //SoapUI.log("Creating class for captureURL");
        captureURL = new captureURL (util, logger)
        testCaseIterator();
    }
    def testCaseIterator(){
        def testSteps = util.testStepsList();
        testSteps.each {
            // Checking if TestStep is of WSDLTestRequest type
            if (it instanceof com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep){
                // Reading Raw request to extract Namespace to use.
                def rawRequest = it.getProperty("Request").getValue()
                if (rawRequest.contains("CreateIndicium")){
                    String[] nameSpaceURL= rawRequest.findAll('https?://[^\\s<>"]+|www\\.[^\\s<>"]+')
                    // Reading response content into an object
                    def url = context.expand( '${'+it.name+'#Response#declare namespace ns1=\''+nameSpaceURL[1]+'\';//ns1:CreateIndiciumResponse[1]/ns1:URL[1]}')
                    captureURL.printURL(url, it.name);
                }
                else {
                    log.error("Unable to find Create Indicium in request.");
                }
            }
        }
    }
}

