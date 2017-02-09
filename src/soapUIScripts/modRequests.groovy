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

package soapUIScripts;

import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep
import com.eviware.soapui.impl.wsdl.teststeps.*
import com.eviware.soapui.impl.wsdl.panels.support.MockTestRunContext
import com.eviware.soapui.model.project.ProjectFactoryRegistry
import com.eviware.soapui.model.support.ModelSupport
import com.eviware.soapui.support.UISupport
import com.eviware.soapui.LogMonitor.*
import com.eviware.soapui.SoapUI
/**
 *
 * @author Diganth Aswath <diganth2004@gmail.com>
 */
protected class modRequests {
    
    private def context, util, log;
    private modRequests(def util, def context, def log){
        this.util = util
        this.context = context
        this.log = log
    }
        
    private def testCaseIterator(){
        def testSteps = util.testStepsList();
        testSteps.each {
            // Checking if TestStep is of WSDLTestRequest type
            if (it instanceof com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep){
                // Reading Raw request to extract Namespace to use.
                def rawRequest = it.getProperty("Request").getValue()
                String[] nameSpaceURL= rawRequest.findAll('https?://[^\\s<>"]+|www\\.[^\\s<>"]+')
                if (rawRequest.contains("CreateIndicium")){
                    createIndicium(it.name,nameSpaceURL[1])
                }
                else if (rawRequest.contains("RegisterAccount")){
                    registration(it.name, nameSpaceURL[1])
                }
                else if (rawRquest.contains("PurchasePostage")){
                    
                }
                else if (rawRequest.contains("GetAccountInfo")){
                    
                }
                else if (rawRequest.contains("CreateUnfundedIndicium")){
                    
                }
                else {
                    log.error("Unable to find any valid acceptable types in request.");
                }
            }
        }
    }
    
    private def createIndicium(def testStepName, String nameSpace){
        
    }

    private def registration(def testStepName, String nameSpace){
        
    }

    private def purchasePostage(def testStepName, String nameSpace){
        
    }
    
    private def getAccountInfo(def testStepName, String nameSpace){
        
    }
    
    private def createUnfundedIndicium(def testStepName, String nameSpace){
        
    }

}

