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
import com.eviware.soapui.support.editor.xml.*
import com.eviware.soapui.support.XmlHolder
import com.eviware.soapui.support.GroovyUtils
import com.eviware.soapui.LogMonitor.*
import com.eviware.soapui.SoapUI
import soapUIScripts.*
/**
 *
 * @author Diganth Aswath <diganth2004@gmail.com>
 * @description This class contains code that parses executed requests and 
 *              extracts data, logs them, prints URLs. if adding new functionality,
 *              only this class needs to be changed.
 */
class evalRequests {
    def context, util, log;
    def captureURL, captureImageData, responseHolder, requestHolder, captureResponses;

    evalRequests(def util, def context, def log){
        this.util = util
        this.context = context
        this.log = log
        captureURL = new captureURL (util, log)
        captureImageData = new captureImageData (util, log)
        captureResponses = new captureResponses (util, log)
    }
    String testCaseIterator(){
        def testSteps = util.testStepsList();
        String[] nameSpaceURL = null;
        String error = null;
        testSteps.each {
            // Checking if TestStep is of WSDLTestRequest type
            if (it instanceof com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep){
                
                def rawRequest = it.getProperty("RawRequest").getValue()
                log.debug(it.name + ":: *********** Raw Request **************** ")
                log.debug("\r\n")
                log.debug(it.name + ":: " + rawRequest)               
                def response = it.getProperty("Response").getValue()
                             
                // Reading Raw request to extract Namespace to use.
                if ((rawRequest != null) && (response.contains("faultstring") == false)){
                   nameSpaceURL = rawRequest.findAll('https?://[^\\s<>"]+|www\\.[^\\s<>"]+')
                
                    if (rawRequest.contains("CreateIndicium")){
                        error = createIndicium(it,nameSpaceURL[1])
                        log.info(it.name + ":: Response returned -- " + error)
                    }
                    else if (rawRequest.contains("CreateScanForm")){
                        error = createScanForm(it,nameSpaceURL[1])
                        log.info(it.name + ":: Response returned -- " + error)
                    }
                    else if (rawRequest.contains("RegisterAccount")){
                        error = registration(it.name, nameSpaceURL[1])
                    }
                    else if (rawRequest.contains("PurchasePostage")){
                        error = purchasePostage(it.name,nameSpaceURL[1])
                    }
                    else if (rawRequest.contains("GetAccountInfo")){
                        error = getAccountInfo(it,nameSpaceURL[1])
                        log.info(it.name + ":: Response returned -- " + error)
                    }
                    else if (rawRequest.contains("CreateUnfundedIndicium")){
                        error = createUnfundedIndicium(it,nameSpaceURL[1])
                        log.info(it.name + ":: Response returned -- " + error)
                    }
                    else if (rawRequest.contains("CreateNetStampsIndicia")){
                        error = createNetStampsIndicia(it, nameSpaceURL[1])
                        log.info(it.name + ":: Response returned -- " + error)
                    }
                    else if (rawRequest.contains("CreateEnvelopeIndicium")){
                        error = createEnvelopeIndicium(it, nameSpaceURL[1])
                        log.info(it.name + ":: Response returned -- " + error)
                    }
                    else if (rawRequest.contains("CreateMailingLabelIndicia")){
                        error = createMailingLabelIndicia(it, nameSpaceURL[1])
                        log.info(it.name + ":: Response returned -- " + error)
                    }
                    else{
                        error = "Unable to find any valid acceptable types in request.";
                        log.error("Unable to find any valid acceptable types in request.");
                    }
                }
                else{
                    error = context.expand ('${'+it.name+'#Response#//faultstring[1]}');
                }
            }
        }
        return error;
    }
    String createIndicium(def testStep, String nameSpace){
        
        def testStepName = testStep.name
        def grUtils = new GroovyUtils(context);
        
        def errorData;
        
        requestHolder = grUtils.getXmlHolder(testStep.getProperty("RawRequest").getValue());
        requestHolder.declareNamespace("ns1", nameSpace);
        
        responseHolder = grUtils.getXmlHolder(testStep.getProperty("Response").getValue())
        
        def imageType = requestHolder.getNodeValue('//ns1:CreateIndicium/ns1:ImageType');
        def returnImageData = requestHolder.getNodeValue('//ns1:CreateIndicium/ns1:ReturnImageData');
        
        def url = responseHolder.getNodeValue('//ns1:CreateIndiciumResponse[1]/ns1:URL[1]')
        def stampsTxID = responseHolder.getNodeValue('//ns1:CreateIndiciumResponse[1]/ns1:StampsTxID[1]')
        def tracking = responseHolder.getNodeValue('//ns1:CreateIndiciumResponse[1]/ns1:TrackingNumber[1]')
        def serviceType = responseHolder.getNodeValue('//ns1:CreateIndiciumResponse[1]/ns1:Rate[1]/ns1:ServiceType[1]')
        def layout = responseHolder.getNodeValue('//ns1:CreateIndiciumResponse[1]/ns1:Rate[1]/ns1:PrintLayout[1]')
        String[] base64Data = responseHolder.getNodeValues('//ns1:CreateIndiciumResponse[1]/ns1:ImageData[1]/ns1:base64Binary')
              
        log.info(testStepName + ":: Request -- Return Image Data : " + returnImageData)
        log.info(testStepName + ":: Request -- Image Type : " + imageType)
        
        log.info(testStepName + ":: Response -- StampsTxTD : " + stampsTxID)
        log.info(testStepName + ":: Response -- Tracking Number : " +tracking)
        log.info(testStepName + ":: Response -- Service Type : " +serviceType)
        log.info(testStepName + ":: Response -- PrintLayout : " + layout)
        
        responseHolder.removeDomNodes('//ns1:CreateIndiciumResponse[1]/ns1:ImageData[1]/ns1:base64Binary')
        responseHolder.updateProperty()
        log.debug(testStepName + ":: *********** Response **************** :")
        log.debug(testStepName + ":: " + responseHolder.getPrettyXml())
        
        log.debug(testStepName + "Trying to capture response in a different file")
        errorData = captureResponses.saveResponse(responseHolder.getPrettyXml(), testStepName, serviceType, layout);
        
        if (returnImageData == "true"){
            errorData += captureImageData.base64decoder(base64Data, testStepName, serviceType, layout, imageType);
            
        }
        else
            errorData += captureURL.printURL(url, testStepName, serviceType, layout);
        
        return errorData;
    }
    String createScanForm(def testStep, String nameSpace){
        
        def testStepName = testStep.name
        def grUtils = new GroovyUtils(context);
        def errorData;
       
        responseHolder = grUtils.getXmlHolder(testStep.getProperty("Response").getValue())
        
        def url = responseHolder.getNodeValue('//ns1:CreateScanFormResponse[1]/ns1:Url[1]')
        def scanFormID = responseHolder.getNodeValue('//ns1:CreateScanFormResponse[1]/ns1:ScanFormId[1]')
              
        log.info(testStepName + ":: Response -- ScanFormID : " + scanFormID)
        log.info(testStepName + ":: Response -- ScanForm URL : " + url)

        log.debug(testStepName + ":: *********** Response **************** :")
        log.debug(testStepName + ":: " + responseHolder.getPrettyXml())
        
        log.debug(testStepName + "Trying to capture response in a different file")
        errorData = captureResponses.saveResponse(responseHolder.getPrettyXml(), testStepName, null, null);
        
        errorData += captureURL.printURL(url, testStepName, null, null);
        
        return errorData;
         
    }
    String registration(def testStepName, String nameSpace){
        
        def userName = context.expand('${'+testStepName+'#Request#declare namespace ns1=\''+nameSpace+'\';//ns1:RegisterAccount[1]/ns1:UserName[1]}')
        def userID = context.expand('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:RegisterAccountResponse[1]/ns1:UserId[1]}')
        def result = context.expand('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:RegisterAccountResponse[1]/ns1:RegistrationStatus[1]}')
        if (result == "Fail"){
            log.error ("Registration FAIL :: Coud not create user : " + userName)
        }
        else if (result == "Pending"){
            log.info ("Registration PENDING :: User " + userName)
        }
        else if (result == "Success"){
            log.info ("Registration SUCCESS :: User " + userName + " and User ID "+ userID)
        }
        
        return NULL;
    }
    String purchasePostage(def testStepName, String nameSpace){
        def purchaseStatus = context.expand('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:PurchasePostageResponse[1]/ns1:PurchaseStatus[1]}')
        def transactionID = context.expand('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:PurchasePostageResponse[1]/ns1:TransactionID[1]}')
        def availablePostage = context.expand('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:PurchasePostageResponse[1]/ns1:PostageBalance[1]/ns1:AvailablePostage[1]}')
        def controlTotal = context.expand('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:PurchasePostageResponse[1]/ns1:PostageBalance[1]/ns1:ControlTotal[1]}')
        def rejectionReason = context..expand('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:PurchasePostageResponse[1]/ns1:RejectionReason[1]}')
        if (purchaseStatus == "Success"){
            log.info ("Purchase Postage SUCCESS :: Transaction ID " + transactionID + " :: Available Postage " + availablePostage + ":: Control Total " + controlTotal)
        }
        else if (purchaseStatus == "Pending"){
            log.info ("Purchase Postage PENDING :: Transaction ID " + transactionID + " :: Available Postage " + availablePostage + ":: Control Total " + controlTotal)
        }
        else{
            log.error ("Purchase Postage REJECTED :: Transaction ID " + transactionID + " :: Available Postage " + availablePostage + ":: Control Total " + controlTotal)
        }
        
        return NULL;
    }
        
    String getAccountInfo(def testStep, String nameSpace){
        
        def testStepName = testStep.name
        def grUtils = new GroovyUtils(context);
        def errorData;
        responseHolder = grUtils.getXmlHolder(testStep.getProperty("Response").getValue())
        
        def custID = context.expand( '${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:GetAccountInfoResponse[1]/ns1:AccountInfo[1]/ns1:CustomerID[1]}')
        def meterno = context.expand( '${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:GetAccountInfoResponse[1]/ns1:AccountInfo[1]/ns1:MeterNumber[1]}')
        def userID = context.expand( '${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:GetAccountInfoResponse[1]/ns1:AccountInfo[1]/ns1:UserID[1]}')
        def availablePostage = context.expand( '${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:GetAccountInfoResponse[1]/ns1:AccountInfo[1]/ns1:PostageBalance[1]/ns1:AvailablePostage[1]}')
        def controlTotal = context.expand( '${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:GetAccountInfoResponse[1]/ns1:AccountInfo[1]/ns1:PostageBalance[1]/ns1:ControlTotal[1]}')
        def maxPostageBal = context.expand( '${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:GetAccountInfoResponse[1]/ns1:AccountInfo[1]/ns1:MaxPostageBalance[1]}')
        log.info ("AcountInfo :: PurchasePostage request details")
        log.info ("AcountInfo :: Customer ID == " + custID)
        log.info ("AcountInfo :: Meter Number == " + meterno)
        log.info ("AcountInfo :: User ID == " + userID)
        log.info ("AcountInfo :: Available Postage  == " + availablePostage)
        log.info ("AcountInfo :: Control Total == " + controlTotal)
        log.info ("AcountInfo :: Max. Postage Balance == " + maxPostageBal)
        
        log.debug(testStepName + "Trying to capture response in a different file")
        errorData = captureResponses.saveResponse(responseHolder.getPrettyXml(), testStepName, null, null);
        
        return errorData;
    }
    
    String  createUnfundedIndicium( def testStep, String nameSpace){
        
        def testStepName = testStep.name
        def grUtils = new GroovyUtils(context);
        def errorData;
        responseHolder = grUtils.getXmlHolder(testStep.getProperty("Response").getValue())
        
        def url = context.expand( '${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:CreateUnfundedIndiciumResponse[1]/ns1:URL[1]}')
        def stampsTxID = context.expand ('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:CreateUnfundedIndiciumResponse[1]/ns1:StampsTxID[1]}')
        def tracking = context.expand ('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:CreateUnfundedIndiciumResponse[1]/ns1:TrackingNumber[1]}')
        log.info (testStepName + ":: StampsTxTD : " + stampsTxID)
        log.info (testStepName + ":: Tracking Number : " +tracking)
        
        log.debug(testStepName + "Trying to capture response in a different file")
        errorData = captureResponses.saveResponse(responseHolder.getPrettyXml(), testStepName, null, null);
        errorData += captureURL.printURL(url, testStepName, null, null);
        
        return errorData;
    }

    String createNetStampsIndicia(def testStep, String nameSpace){
        
        def grUtils = new GroovyUtils(context);
        def errorData;
        responseHolder = grUtils.getXmlHolder(testStep.getProperty("Response").getValue())
        
        def testStepName = testStep.name
        def rawResponse = testStep.getProperty("Response").getValue()
        def imageType = context.expand ('${'+testStepName+'#Request#declare namespace ns1=\''+nameSpace+'\';//ns1:CreateNetStampsIndicia[1]/ns1:ImageType}')
            
        def url = context.expand( '${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:CreateNetStampsIndiciaResponse[1]/ns1:URL[1]}')
        def stampsTxID = context.expand ('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:CreateNetStampsIndiciaResponse[1]/ns1:StampsTxID[1]}')
        def netstampsStatus = context.expand ('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:CreateNetStampsIndiciaResponse[1]/ns1:NetstampsStatus[1]}')
        
        log.info(testStepName + ":: Image Type : " + imageType)
        log.info(testStepName + ":: StampsTxTD : " + stampsTxID)
        log.info(testStepName + ":: NetStampsStatus : " + netstampsStatus)
        log.debug(testStepName + ":: *********** Raw Response **************** ")
        log.debug("\r\n")
        log.debug(testStepName + ":: " + rawResponse)
        
        log.debug(testStepName + "Trying to capture response in a different file")
        errorData = captureResponses.saveResponse(responseHolder.getPrettyXml(), testStepName, null, null);
        errorData += captureURL.printURL(url, testStepName, null, null);
        
        return errorData;
    }
    
    String createEnvelopeIndicium(def testStep, String nameSpace){
        
        def grUtils = new GroovyUtils(context);
        def errorData;
        responseHolder = grUtils.getXmlHolder(testStep.getProperty("Response").getValue())
        
        def testStepName = testStep.name
        def rawResponse = testStep.getProperty("Response").getValue()
        //def imageType = context.expand ('${'+testStepName+'#Request#declare namespace ns1=\''+nameSpace+'\';//ns1:CreateNetStampsIndicia[1]/ns1:ImageType}')
            
        def url = context.expand( '${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:CreateEnvelopeIndiciumResponse[1]/ns1:URL[1]}')
        def stampsTxID = context.expand ('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:CreateEnvelopeIndiciumResponse[1]/ns1:StampsTxID[1]}')
        def trackingNumber = context.expand ('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:CreateEnvelopeIndiciumResponse[1]/ns1:TrackingNumber[1]}')
        
        //log.info(testStepName + ":: Image Type : " + imageType)
        
        log.info(testStepName + ":: StampsTxTD : " + stampsTxID)
        log.info(testStepName + ":: TrackingNumber : " + trackingNumber)
        log.debug(testStepName + ":: *********** Raw Response **************** ")
        log.debug("\r\n")
        log.debug(testStepName + ":: " + rawResponse)
        
        log.debug(testStepName + "Trying to capture response in a different file")
        errorData = captureResponses.saveResponse(responseHolder.getPrettyXml(), testStepName, null, null);
        errorData += captureURL.printURL(url, testStepName, null, null);
        
        return errorData;
    }
    
     String createMailingLabelIndicia(def testStep, String nameSpace){
        
        def grUtils = new GroovyUtils(context);
        def errorData;
        responseHolder = grUtils.getXmlHolder(testStep.getProperty("Response").getValue())
        
        def testStepName = testStep.name
        def rawResponse = testStep.getProperty("Response").getValue()
        //def imageType = context.expand ('${'+testStepName+'#Request#declare namespace ns1=\''+nameSpace+'\';//ns1:CreateNetStampsIndicia[1]/ns1:ImageType}')
            
        def url = context.expand( '${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:CreateMailingLabelIndiciaResponse[1]/ns1:Url[1]}')
        def stampsTxID = context.expand ('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:CreateMailingLabelIndiciaResponse[1]/ns1:StampsTxID[1]}')
        def confirmationNumbers = context.expand ('${'+testStepName+'#Response#declare namespace ns1=\''+nameSpace+'\';//ns1:CreateMailingLabelIndiciaResponse[1]/ns1:ConfirmationNumbers[1]/ns1:string[1]}')
        
        //log.info(testStepName + ":: Image Type : " + imageType)
        
        log.info(testStepName + ":: StampsTxTD : " + stampsTxID)
        log.info(testStepName + ":: TrackingNumber : " + confirmationNumbers)
        log.debug(testStepName + ":: *********** Raw Response **************** ")
        log.debug("\r\n")
        log.debug(testStepName + ":: " + rawResponse)
        
        log.debug(testStepName + "Trying to capture response in a different file")
        errorData = captureResponses.saveResponse(responseHolder.getPrettyXml(), testStepName, null, null);
        errorData += captureURL.printURL(url, testStepName, null, null);
        
        return errorData;
    }
}

