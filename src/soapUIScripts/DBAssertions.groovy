/*
 * Copyright (C) 2016 Diganth Aswath <diganth2004@gmail.com>
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
import com.eviware.soapui.impl.wsdl.panels.support.MockTestRunContext
import com.eviware.soapui.model.project.ProjectFactoryRegistry
import com.eviware.soapui.model.support.ModelSupport
import com.eviware.soapui.support.UISupport
import com.eviware.soapui.LogMonitor.*
import com.eviware.soapui.SoapUI
import groovy.sql.Sql

/**
 *
 * @author Diganth Aswath <diganth2004@gmail.com>
 */
public class DBAssertions {
	
    private def context, testRunner, messageExchange;
    private def util, grUtils, log;
    
    private DBAssertions(def util, def context, def log){
        this.context = context;
        this.testRunner = testRunner;
        this.log = log;
        this.util = util;
    }
        
    private DBAssertions(def util, def context, def messageExchange, def log){
        this.util = util;
        this.context = context;
        this.messageExchange = messageExchange;
        this.testRunner = testRunner;
        this.log = log;
    } 
   
    /**
     * This is used to validate if response provided contains supported USPS/Consolidators/GP service types.
     * @param   response Soap response as a string.
     * @param   dbData Mail Class data from a prior executed SQL query as a string.
     * @return  Status of validation as a string. Following are the possible return statuses:
     *          <ul>
     *          <li>Matches. RequestData matches DB Data.
     *          <li>Mismatch. RequestData does not match DB Data.
     *          <li>Error. Request Data is empty.
     *          <li>Error. DB Data is empty.
     *          <li>Error. Request Data and DB Data is missing.
     *          </ul>
    */
    def assertMailClass(def response, def dbData){
        def assertionResult = null, responseMailClass;
        def testCaseName = util.readTestCaseProperty("CurrentTestName");
        log.debug("$testCaseName:: In DBAssertions.assertMailClass()...");
        
        if (response != null && dbData != null){
            if (response.contains("CreateIndiciumResponse")){
                responseMailClass = util.readXMLNodeValue(response, '//ns1:CreateIndiciumResponse[1]/ns1:Rate[1]/ns1:ServiceType[1]');
            }
            else if (response.contains("CreateEnvelopeIndiciumResponse")){
                responseMailClass = util.readXMLNodeValue(response, '//ns1:CreateEnvelopeIndiciumResponse[1]/ns1:Rate[1]/ns1:ServiceType[1]');
            }
            
            if( (responseMailClass == "US-FC" && dbData == "FIRST_CLASS") 
                || (responseMailClass == "US-PM" && dbData == "PRIORITY")
                || (responseMailClass == "US-XM" && dbData == "EXPRESS")
                || (responseMailClass == "US-PP" && dbData == "PARCEL_POST")
                || (responseMailClass == "US-MM" && dbData == "MEDIA_MAIL")
                || (responseMailClass == "US-BPM" && dbData == "BOUND_PRINTED_MATTER")
                || (responseMailClass == "US-LM" && dbData == "LIBRARY_MAIL")
                || (responseMailClass == "US-EMI" && dbData == "EXPRESS_MAIL_INTERNATIONAL")
                || (responseMailClass == "US-PMI" && dbData == "PRIORITY_MAIL_INTERNATIONAL") 
                || (responseMailClass == "US-FCI" && dbData == "FIRST_CLASS_MAIL_INTERNATIONAL")
                || (responseMailClass == "US-CM" && dbData == "CRITICAL")
                || (responseMailClass == "US-PS" && dbData == "Parcel_Select")
                || (responseMailClass == "SC-GPE" && dbData == "Global Post Economy")
                || (responseMailClass == "SC-GPP" && dbData == "Global Post Priority")
                || (responseMailClass == "SC-GPESS" && dbData == "Global Post Economy SmartSaver")
                || (responseMailClass == "SC-GPPSS" && dbData == "Global Post Priority SmartSaver")){
                assertionResult = "Matched. RequestData - [$responseMailClass] matches DB Data - [$dbData].";
            }
            else {
                assertionResult = "Mismatch. Request Data - [$responseMailClass] does not match DB Data - [$dbData].";
            }
        }
        else if (response == null && dbData != null){
            assertionResult = "Error. Request Data is empty.";
        }
        else if (response != null && dbData == null){
            assertionResult = "Error. DB Data is empty."
        }
        else {
            assertionResult = "Error. Request Data and DB Data is missing.";
        }
        return assertionResult;
    }
    
    /**
     * This is used to validate if the PostageAmount from the response matches the data stored in the database.
     * @param   request Soap request <CreateIndicium, CreateMailingLabelIndicia, CreateNetStampsIndicia, CreateEnvelopeIndicium> as a string.
     * @param   response Soap response as a string.
     * @dbData  dbData Postage Amount data from a prior executed SQL query as a string.
     * @return  Status of validation as a string. Following are the possible return statuses:
     *          <ul>
     *          <li>Matches. RequestData matches DB Data.
     *          <li>Mismatch. RequestData does not match DB Data.
     *          <li>Error. Request Data is empty.
     *          <li>Error. DB Data is empty.
     *          <li>Error. Request Data and DB Data is missing.
     *          </ul>
    */
    def assertPostageAmount(def request, def response, def dbData){
        def assertionResult = null, addOnValue = null, addOnTag = null, addOnNodeCount, responseAmount, requestAmount;
        def addOnNodeCountTag = null, addOnValueTag = null, addOnTypeTag = null, responseAmountTag = null, requestAmountTag = null, noPostageTag = null, noPostageValue = null;
        def addOnVersionText = util.readProjectProperty("AddOnVersionText");
        def testCaseName = util.readTestCaseProperty("CurrentTestName");
        Double totalPostage_Response, totalPostage_DB, totalPostage_Request;
         
        log.debug("$testCaseName:: In DBAssertions.assertPostageAmount()...");
        
        if (response != null && request != null && dbData != null){
            if (request.contains("CreateIndicium") && response.contains("CreateIndiciumResponse")){
                addOnNodeCountTag = "//ns1:CreateIndicium[1]/ns1:Rate[1]/ns1:AddOns[1]/ns1:$addOnVersionText";  
                noPostageTag = "//ns1:CreateIndicium[1]/ns1:PostageMode[1]";
                responseAmountTag = "//ns1:CreateIndiciumResponse[1]/ns1:Rate[1]/ns1:Amount[1]";
                addOnValueTag = "//ns1:CreateIndiciumResponse[1]/ns1:Rate[1]/ns1:AddOns[1]/ns1:$addOnVersionText[%d]/ns1:Amount[1]";
                addOnTypeTag = "//ns1:CreateIndiciumResponse[1]/ns1:Rate[1]/ns1:AddOns[1]/ns1:$addOnVersionText[%d]/ns1:AddOnType[1]";
                
            }
            else if (request.contains("CreateEnvelopeIndicium") && response.contains("CreateEnvelopeIndiciumResponse")){
                addOnNodeCountTag = "//ns1:CreateEnvelopeIndicium[1]/ns1:Rate[1]/ns1:AddOns[1]/ns1:$addOnVersionText";  
                noPostageTag = null;
                responseAmountTag = "//ns1:CreateEnvelopeIndiciumResponse[1]/ns1:Rate[1]/ns1:Amount[1]";
                addOnValueTag = "//ns1:CreateEnvelopeIndiciumResponse[1]/ns1:Rate[1]/ns1:AddOns[1]/ns1:$addOnVersionText[%d]/ns1:Amount[1]";
                addOnTypeTag = "//ns1:CreateIndiciumResponse[1]/ns1:Rate[1]/ns1:AddOns[1]/ns1:$addOnVersionText[%d]/ns1:AddOnType[1]";
            }
            else if (request.contains("GetAccountInfoResponse") && response.contains("CreateNetStampsIndiciaResponse")){
                requestAmountTag = "//ns1:GetAccountInfoResponse[1]/ns1:AccountInfo[1]/ns1:PostageBalance[1]/ns1:AvailablePostage[1]";
                noPostageTag = null;
                responseAmountTag = "//ns1:CreateNetStampsIndiciaResponse[1]/ns1:PostageBalance[1]/ns1:AvailablePostage[1]";
                addOnNodeCountTag = null;
                addOnValueTag = null;
                addOnTypeTag = null;
            }
            
            requestAmount = util.readXMLNodeValue(request, requestAmountTag);
            noPostageValue = util.readXMLNodeValue(request, noPostageTag);
            addOnNodeCount = util.countXMLNode(request, addOnNodeCountTag);     
            responseAmount = util.readXMLNodeValue(response, responseAmountTag);
            
            log.debug("$testCaseName:: RequestAmount  : $requestAmount");
            log.debug("$testCaseName:: NoPostage Value : $noPostageValue");
            log.debug("$testCaseName:: AddOnNode Count  : $addOnNodeCount");
            log.debug("$testCaseName:: ResponseAmount  : $responseAmount");
            
            totalPostage_Request = util.convertToDouble(requestAmount, "#.000");
            totalPostage_Response  = util.convertToDouble(responseAmount, "#.000");
            totalPostage_DB = util.convertToDouble(dbData.toString(), "#.000");
            
            log.debug("$testCaseName:: RequestAmount  : $totalPostage_Request");
            log.debug("$testCaseName:: ResponseAmount  : $totalPostage_Response");
            log.debug("$testCaseName:: DBAmount  : $totalPostage_DB");
            
            if (noPostageValue == null || noPostageValue == "Normal")
            {
                if (addOnNodeCount.toInteger() >  0) {
                    for (int i = 1; i <= addOnNodeCount; i++)
                    {
                        addOnTag = util.readXMLNodeValue(response, String.format(addOnTypeTag, i));
                        addOnValue = util.readXMLNodeValue(response, String.format(addOnValueTag, i));
                        log.debug("AddOnTag at $i position is : $addOnTag");
                        log.debug("AddOnValue at $i position is : $addOnValue");
                        if (addOnValue != null && addOnTag != "SC-A-INS")
                        totalPostage_Response = totalPostage_Response + util.convertToDouble(addOnValue, "#.000");
                    }
                }   
            }
            
            /* Used for CreateNetStampIndicia assertion only*/
            if (requestAmount != null) {
                totalPostage_Response = totalPostage_Request - totalPostage_Response;
            }
            
            totalPostage_Response = util.convertToDouble(totalPostage_Response.toString(), "#.000");
            
            if (totalPostage_Response == totalPostage_DB){
                assertionResult = "Matched.. Response Postage - [$totalPostage_Response] matches DB Data - [$totalPostage_DB].";
            }
            else{
                assertionResult = "Mismatch. Response Postage - [$totalPostage_Response] does not match DB Data - [$totalPostage_DB].";
            }
        }
        else if (response == null && (request != null || dbData != null)){
            assertionResult = "Error. Response data is empty.";
        }
        else if (request == null && (response != null || dbData != null)){
            assertionResult = "Error. Request data is empty.";
        }
        else if (dbData == null && (request != null || response != null)){
            assertionResult = "Error. Db Data is empty.";
        }
        else {
            assertionResult = "Error. Request, Response or DB Data is missing.";
        }
        return assertionResult;
    }
    
    /**
     * This is used to validate if the AddOnAmount from the response matches the data stored in the database.
     * @param   request Soap request <CreateIndicium, CreateMailingLabelIndicia, CreateNetStampsIndicia, CreateEnvelopeIndicium> as a string.
     * @param   response Soap response as a string.
     * @param   addOnToAssert  AddOn whose amount value needs to be validated.
     * @param   dbData AddOn Amount data from a prior executed SQL query as a string.
     * @return  Status of validation as a string. Following are the possible return statuses:
     *          <ul>
     *          <li>Matches. RequestData matches DB Data.
     *          <li>Mismatch. RequestData does not match DB Data.
     *          <li>Error. Request Data is empty.
     *          <li>Error. DB Data is empty.
     *          <li>Error. Request Data and DB Data is missing.
     *          </ul>
    */
    def assertAddOnAmount(def request, def response, def addOnToAssert, def dbData)
    {
        def assertionResult = null, addOnValue = null, addOnTag = null, addOnNodeCount, responseAmount, requestAmount;
        def addOnNodeCountTag = null, addOnValueTag = null, addOnTypeTag = null, responseAmountTag = null, requestAmountTag = null;
        def addOnVersionText = util.readProjectProperty("AddOnVersionText");
        def testCaseName = util.readTestCaseProperty("CurrentTestName");
        Double addOnAmount_DB, addOnAmount_Response;
        
        log.debug("$testCaseName:: In DBAssertions.assertAddOnAmount()...");
        log.debug("$testCaseName:: AddOn - Amount that is being asserted - $addOnToAssert");
        if (response != null && request != null && dbData != null && addOnToAssert != null){
            if (request.contains("CreateIndicium") && response.contains("CreateIndiciumResponse")){
                addOnNodeCountTag = "//ns1:CreateIndicium[1]/ns1:Rate[1]/ns1:AddOns[1]/ns1:$addOnVersionText";  
                log.debug("$testCaseName:: AddOnNodeCountTag : $addOnNodeCountTag");
                addOnValueTag = "//ns1:CreateIndiciumResponse[1]/ns1:Rate[1]/ns1:AddOns[1]/ns1:$addOnVersionText[%d]/ns1:Amount[1]";
                addOnTypeTag = "//ns1:CreateIndiciumResponse[1]/ns1:Rate[1]/ns1:AddOns[1]/ns1:$addOnVersionText[%d]/ns1:AddOnType[1]";
            }
        
            addOnNodeCount = util.countXMLNode(request, addOnNodeCountTag);
            addOnAmount_DB = util.convertToDouble(dbData.toString(), "#.000");
            log.debug("$testCaseName:: AddOnNode Count  : $addOnNodeCount");
         
            if (addOnNodeCount.toInteger() >  0) {
                for (int i = 1; i <= addOnNodeCount; i++)
                {
                    addOnTag = util.readXMLNodeValue(response, String.format(addOnTypeTag, i));
                    addOnValue = util.readXMLNodeValue(response, String.format(addOnValueTag, i));
                    log.debug("AddOnTag at $i position is : $addOnTag");
                    log.debug("AddOnValue at $i position is : $addOnValue");
                    if (addOnValue != null && addOnTag == addOnToAssert)
                    addOnAmount_Response = util.convertToDouble(addOnValue, "#.000");
                }
            }
            
            if (addOnAmount_DB == addOnAmount_Response){
                assertionResult = "Matched.. Response $addOnToAssert AddOnAmount - [$addOnAmount_Response] matches DB Data - [$addOnAmount_DB].";
            }
            else{
                assertionResult = "Mismatch. Response $addOnToAssert InsuranceAmount - [$addOnAmount_Response] matches DB Data - [$addOnAmount_DB].";
            }
        }
        else if (response == null && (request != null || dbData != null)){
            assertionResult = "Error. Response data is empty.";
        }
        else if (request == null && (response != null || dbData != null)){
            assertionResult = "Error. Request data is empty.";
        }
        else if (dbData == null && (request != null || response != null)){
            assertionResult = "Error. Db Data is empty.";
        }
        else {
            assertionResult = "Error. Request, Response or DB Data is missing.";
        }
        return assertionResult;
    }
       
    /**
     * This is used to validate if the ControlTotal from response matches the data stored in the database.
     * @param   request Soap request <CreateIndicium, CreateMailingLabelIndicia, CreateNetStampsIndicia, CreateEnvelopeIndicium> as a string.
     * @param   dbData_descReg DescReg data from a prior executed SQL query as a string.
     * @param   dbData_ascReg AscReg data from a prior executed SQL query as a string.
     * @return  Status of validation as a string. Following are the possible return statuses:
     *          <ul>
     *          <li>Matches. RequestData matches DB Data.
     *          <li>Mismatch. RequestData does not match DB Data.
     *          <li>Error. Request Data is empty.
     *          <li>Error. DB Data is empty.
     *          <li>Error. Request Data and DB Data is missing.
     *          </ul>
    */
    def assertControlTotal(def response, def dbData_descReg, def dbData_ascReg ){
        def assertionResult = null, responseControlTotal;
        def testCaseName = util.readTestCaseProperty("CurrentTestName");
        log.debug("$testCaseName:: In DBAssertions.assertControlTotal() ...");
        
        if (response != null && dbData_descReg != null && dbData_ascReg != null){
            if (response.contains("CreateIndiciumResponse")){
                responseControlTotal = util.readXMLNodeValue(response, "//ns1:CreateIndiciumResponse[1]/ns1:PostageBalance[1]/ns1:ControlTotal[1]");
            }
            else if (response.contains("CreateEnvelopeIndiciumResponse")){
                responseControlTotal = util.readXMLNodeValue(response, "//ns1:CreateEnvelopeIndiciumResponse[1]/ns1:PostageBalance[1]/ns1:ControlTotal[1]");
            }
            else if (response.contains("CreateMailingLabelIndiciaResponse")){
                responseControlTotal = util.readXMLNodeValue(response, "//ns1:CreateMailingLabelIndiciaResponse[1]/ns1:PostageBalance[1]/ns1:ControlTotal[1]");
            }
            else if (response.contains("CreateNetStampsIndiciaResponse")){
                responseControlTotal = util.readXMLNodeValue(response, "//ns1:CreateNetStampsIndiciaResponse[1]/ns1:PostageBalance[1]/ns1:ControlTotal[1]");
            }
            else{
                responseControlTotal = "0.000";
            }
            Double ControlTotal_Response = util.convertToDouble(responseControlTotal, "#.000");
            log.debug("ControlTotal_Response $ControlTotal_Response");
            Double ControlTotal_DB = util.convertToDouble(dbData_descReg.toString(), "#.000") + util.convertToDouble(dbData_ascReg.toString(), "#.000");
            ControlTotal_DB = util.convertToDouble(ControlTotal_DB.toString(), "#.000");
            log.debug("ControlTotal_DB after converting $ControlTotal_DB");
            if (ControlTotal_Response == ControlTotal_DB){
                assertionResult = "Matched.. Response Data - [$ControlTotal_Response] matches DB Data - [$ControlTotal_DB]";
            }
            else{
                assertionResult = "Mismatch. Response Data - [$ControlTotal_Response] does not match DB Data - [$ControlTotal_DB]";
            }
        }
        else if (response == null && (dbData_descReg != null || dbData_ascReg != null)){
            assertionResult = "Error. Response data is empty.";
        }
        else if (dbData_descReg == null && (response != null || dbData_ascReg != null)){
            assertionResult = "Error. Db data is empty.";
        }
        else if (dbData_ascReg == null && (dbData_descReg != null || response != null)){
            assertionResult = "Error. Db Data is empty.";
        }
        else {
            assertionResult = "Error. Request or DB Data is missing.";
        }
        return assertionResult;
    }
    
    /**
     * This is used to validate if the RateCategory stored in the datasource matches the data stored in the database.
     * @param   expectedData Expected RateCategory from DataSource.
     * @param   dbData RateCategory data from a prior executed SQL query as a string.
     * @return  Status of validation as a string. Following are the possible return statuses:
     *          <ul>
     *          <li>Matches. RequestData matches DB Data.
     *          <li>Mismatch. RequestData does not match DB Data.
     *          <li>Error. Request Data is empty.
     *          <li>Error. DB Data is empty.
     *          <li>Error. Request Data and DB Data is missing.
     *          </ul>
    */
    def assertRateCategory(def expectedData, def dbData){
        def assertionResult = null, responseRateCat;
        def testCaseName = util.readTestCaseProperty("CurrentTestName");
        log.debug("$testCaseName:: In DBAssertions.assertRateCategory() ....");
             
        if (expectedData != null && dbData != null){
            if (expectedData.contains("CreateIndiciumResponse")){
                responseRateCat = util.readXMLNodeValue(expectedData, "//ns1:CreateIndiciumResponse[1]/ns1:Rate[1]/ns1:RateCategory[1]");
            }
            else if (expectedData.contains("CreateEnvelopeIndiciumResponse")){
                responseRateCat = util.readXMLNodeValue(expectedData, "//ns1:CreateEnvelopeIndiciumResponse[1]/ns1:Rate[1]/ns1:RateCategory[1]");
            }
            else{
                responseRateCat = expectedData;
            }
            String dbRateCat = dbData.toString();
            String RateCat_DB = dbRateCat.substring(4,6) + dbRateCat.substring(2,4);
            String RateCat_Response = responseRateCat.toString();
            log.debug("$testCaseName:: Untamperred Rate Cat: $responseRateCat");
            log.debug("$testCaseName:: Untamperred DB Rate Cat: $dbRateCat");
            
            if (Integer.parseInt(RateCat_DB, 16).toString() == RateCat_Response){
                assertionResult = "Matched.. Response Data - [$RateCat_Response] matches DB Data - [$RateCat_DB]";
            }
            else{
                assertionResult = "Mismatch. Response Data - [$RateCat_Response] does not match DB Data - [$RateCat_DB]";
            }
        }
        else if (expectedData == null && dbData != null){
            assertionResult = "Error. Request Data is empty.";
        }
        else if (expectedData != null && dbData == null){
            assertionResult = "Error. DB Data is empty."
        }
        else {
            assertionResult = "Error. Request Data and DB Data is missing.";
        }
        return assertionResult;

    }
    
    /**
     * This is used to validate the PackageType requested against data stored in the database.
     * @param   request Soap request <CreateIndicium, CreateMailingLabelIndicia, CreateNetStampsIndicia, CreateEnvelopeIndicium> as a string.
     * @param   dbData  PackageType data from a prior executed SQL query as a string.
     * @return  Status of validation as a string. Following are the possible return statuses:
     *          <ul>
     *          <li>Matches. RequestData matches DB Data.
     *          <li>Mismatch. RequestData does not match DB Data.
     *          <li>Error. Request Data is empty.
     *          <li>Error. DB Data is empty.
     *          <li>Error. Request Data and DB Data is missing.
     *          </ul>
    */
    def assertPackageType(def request, def dbData){  
        def assertionResult = null;
        def testCaseName = util.readTestCaseProperty("CurrentTestName");
        log.debug("$testCaseName:: In DBAssertions.assertPackageType() ...");
        if (request.contains("CreateIndicium")){
            assertionResult = assertSingleValue(request, dbData, "//ns1:CreateIndicium[1]/ns1:Rate[1]/ns1:PackageType[1]" );
        }
        else if (request.contains("CreateEnvelopeIndicium")){
            assertionResult = assertSingleValue(request, dbData, "//ns1:CreateEnvelopeIndicium[1]/ns1:Rate[1]/ns1:PackageType[1]" );
        }
        return assertionResult;
    }
    
    /**
     * This is used to validate the StampsTxId in the response is recorded in the database.
     * @param   response Soap response <CreateIndicium, CreateMailingLabelIndicia, CreateNetStampsIndicia, CreateEnvelopeIndicium> as a string.
     * @param   dbData StampsTxID from a prior executed SQL query as a string.
     * @return  Status of validation as a string. Following are the possible return statuses:
     *          <ul>
     *          <li>Matches. RequestData matches DB Data.
     *          <li>Mismatch. RequestData does not match DB Data.
     *          <li>Error. Request Data is empty.
     *          <li>Error. DB Data is empty.
     *          <li>Error. Request Data and DB Data is missing.
     *          </ul>
    */
    def assertStampsTxID(def response, def dbData){
        def assertionResult = null;
        def testCaseName = util.readTestCaseProperty("CurrentTestName");
        log.debug("$testCaseName:: In DBAssertions.assertStampsTxID() ...");
        if (response.contains("CreateIndiciumResponse")){
            assertionResult = assertSingleValue(response, dbData, "//ns1:CreateIndiciumResponse[1]/ns1:StampsTxID[1]");
        }
        else if (response.contains("CreateEnvelopeIndiciumResponse")){
            assertionResult = assertSingleValue(response, dbData, "//ns1:CreateEnvelopeIndiciumResponse[1]/ns1:StampsTxID[1]");
        }
        else if (response.contains("CreateMailingLabelIndiciaResponse")){
            assertionResult = assertSingleValue(response, dbData, "//ns1:CreateMailingLabelIndiciaResponse[1]/ns1:StampsTxId[1]");
        }
        else if (response.contains("CreateNetStampsIndiciaResponse")){
            assertionResult = assertSingleValue(response, dbData, "//ns1:CreateNetStampsIndiciaResponse[1]/ns1:StampsTxId[1]");
        }
        return assertionResult;
    }
    
    /**
     * This is used to validate if TrackingNumber returned in the response matches the data stored in the database.
     * @param   response Soap response <CreateIndicium, CreateScanForm> as a string.
     * @param   dbData TrackingNumber from a prior executed SQL query as a string.
     * @return  Status of validation as a string. Following are the possible return statuses:
     *          <ul>
     *          <li>Matches. RequestData matches DB Data.
     *          <li>Mismatch. RequestData does not match DB Data.
     *          <li>Error. Request Data is empty.
     *          <li>Error. DB Data is empty.
     *          <li>Error. Request Data and DB Data is missing.
     *          </ul>
    */
    def assertTransactionNumber(def response, def dbData){
        def assertionResult = null;
        def testCaseName = util.readTestCaseProperty("CurrentTestName");
        log.debug("$testCaseName:: In DBAssertions.assertTransactionNumber() ...");
        if (response.contains("CreateIndiciumResponse")){
            assertionResult = assertSingleValue(response, dbData, "//ns1:CreateIndiciumResponse[1]/ns1:TrackingNumber[1]");
        }
        else if (response.contains("CreateScanFormResponse")){
            assertionResult = assertSingleValue(response, dbData, "//ns1:CreateScanFormResponse[1]/ns1:ScanFormId[1]");
        }
        return assertionResult;
    }
    
    /**
     * This is used to validate if CancelIndicium request was successful and the print is marked as Cancelled in the database.
     * @param   expectedData Expected Cancel Status as a string.
     * @param   dbData  CancelStatus from a prior executed SQL query as a string.
     * @return  Status of validation as a string. Following are the possible return statuses:
     *          <ul>
     *          <li>Matches. RequestData matches DB Data.
     *          <li>Mismatch. RequestData does not match DB Data.
     *          <li>Error. Request Data is empty.
     *          <li>Error. DB Data is empty.
     *          <li>Error. Request Data and DB Data is missing.
     *          </ul>
    */
    def assertCancelIndiciumStatus(def expectedData, def dbData){
        def assertionResult = null;
        def testCaseName = util.readTestCaseProperty("CurrentCancelTestName");
        log.debug("$testCaseName:: In DbAssertions.assertCancelIndiciumeStatus() ...");
        assertionResult = assertSingleValue(expectedData, dbData, null);
    }
    
    private def assertSingleValue(def soapData, def dbData, def nodeTag){
        def assertionResult = null, soapNodeValue;
        def testCaseName = util.readTestCaseProperty("CurrentTestName");
        log.debug("$testCaseName:: In DBAssertions.assertSingleValue() ...");
               
        if (soapData != null && dbData != null){
            if (nodeTag != null){
                soapNodeValue= util.readXMLNodeValue(soapData, nodeTag);
            }
            else{
                soapNodeValue = soapData;
            }
            if (soapNodeValue.toString().toLowerCase().trim() == dbData.toString().toLowerCase().trim()){
                assertionResult = "Matched. Request data = DB Data. ";
            }
            else{
                assertionResult = "Mismatch. Request [$soapNodeValue] does not match DB data [$dbData].";
            }
        }
        else if (soapData == null && dbData != null){
            assertionResult = "Error. Request Data is empty."
        }
        else if (soapData != null && dbData == null){
            assertionResult = "Error. DB Data is empty."
        }
        else{
            assertionResult = "Error. Request and DB Data are empty."
        }
        return assertionResult;
    }
    
    
}

