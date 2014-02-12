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
import com.eviware.soapui.impl.wsdl.teststeps.*
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
    def context, filepath, util, log, testRunner;
    def captureURL;
    starter(def context, def testRunner, String filepath){
        this.context = context
        this.filepath = filepath
        this.testRunner = testRunner
        util = new utility(context, filepath); 
        log = new logger(util);
        captureURL = new captureURL (util, log)
        //testCaseIterator();
    }
    def testCaseIterator(){
        def testSteps = util.testStepsList();
        testSteps.each {
            // Checking if TestStep is of WSDLTestRequest type
            if (it instanceof com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep){
                // Reading Raw request to extract Namespace to use.
                def rawRequest = it.getProperty("Request").getValue()
                String[] nameSpaceURL= rawRequest.findAll('https?://[^\\s<>"]+|www\\.[^\\s<>"]+')
                if (rawRequest.contains("CreateIndicium")){
                    createIndicium(it.name,nameSpaceURL[1])
                    // Reading response content into an object
                    /*def url = context.expand( '${'+it.name+'#Response#declare namespace ns1=\''+nameSpaceURL[1]+'\';//ns1:CreateIndiciumResponse[1]/ns1:URL[1]}')
                    captureURL.printURL(url, it.name);
                    def stampsTxID = context.expand ('${'+it.name+'#Response#declare namespace ns1=\''+nameSpaceURL[1]+'\';//ns1:CreateIndiciumResponse[1]/ns1:StampsTxID[1]}')
                    log.info(it.name + "StampsTxTD : " + stampsTxID)
                    def tracking = context.expand ('${'+it.name+'#Response#declare namespace ns1=\''+nameSpaceURL[1]+'\';//ns1:CreateIndiciumResponse[1]/ns1:TrackingNumber[1]}')
                    log.info(it.name + "Tracking Number : " +tracking)*/
                }
                else if (rawRequest.contains("RegisterAccount")){
                    registration(it.name, nameSpaceURL[1])
                }
                else {
                    log.error("Unable to find any valid acceptable types in request.");
                }
            }
        }
    }
    def createIndicium(def testStepName, String nameSpace){
        def url = context.expand( '${'+testStepName+'#Response#declare namespace ns1=\''+nameSpaceURL[1]+'\';//ns1:CreateIndiciumResponse[1]/ns1:URL[1]}')
        def stampsTxID = context.expand ('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpaceURL[1]+'\';//ns1:CreateIndiciumResponse[1]/ns1:StampsTxID[1]}')
        def tracking = context.expand ('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpaceURL[1]+'\';//ns1:CreateIndiciumResponse[1]/ns1:TrackingNumber[1]}')
        captureURL.printURL(url, testStepName);
        log.info(testStepName + "StampsTxTD : " + stampsTxID)
        log.info(testStepName + "Tracking Number : " +tracking)
    }
    def registration(def testStepName, String nameSpace){
        def userName = context.expand('${'+testStepName+'#Request#declare namespace ns1=\''+nameSpaceURL[1]+'\';//ns1:RegisterAccount[1]/ns1:UserName[1]}')
        def userID = context.expand('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpaceURL[1]+'\';//ns1:RegisterAccountResponse[1]/ns1:UserId[1]}')
        def result = context.expand('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpaceURL[1]+'\';//ns1:RegisterAccountResponse[1]/ns1:RegistrationStatus[1]}')
        if (result == "Fail"){
            log.error ("FAIL :: Coud not create user : " + userName)
        }
        else if (result == "Pending"){
            log.info ("PENDING :: User " + userName)
        }
        else if (result == "Success"){
            log.info ("SUCCESS :: User " + userName + " and User ID "+ userID)
        }
    }
    def DataSource(def propertyName){
        def counter, next, size;
        def propTestStep = util.propertyTestStep(propertyName);
        counter = propTestStep.getPropertyValue("Count").toString()
        counter = counter.toInteger()
        size = propTestStep.getPropertValue("Total").toString()
        size = size.toInteger()
        next = (counter > size-2? 0: counter+1)
        propTestStep.setPropertyValue("Count", next.toString())
        next++;
        log.info ("Counter Value : " +counter)
        propTestStep.setPropertyValue("Next", next.toString())
        log.info ("Next Value : " +next)
        if (counter == size-1){
            propTestStep.setPropertyValue("StopLoop", "T")
            log.info ("Setting the stoploop property now ...")
        }
        else if (counter==0){
            def runner = new com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner(testRunner.testCase, null)
            log.info ("Starting the DataLoop now ...")
            propTestStep.setPropertyValue("StopLoop", "F")
        }
        else{
            propTestStep.setPropertyValue("StopLoop", "F")
        }
    }
    def DataLoop(def propertyName){
        def propTestStep = util.propertyTestStep(propertyName);
    }
}

