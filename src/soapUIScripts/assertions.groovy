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

package soapUIScripts

import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep
import com.eviware.soapui.impl.wsdl.panels.support.MockTestRunContext
import com.eviware.soapui.model.project.ProjectFactoryRegistry
import com.eviware.soapui.model.support.ModelSupport

import com.eviware.soapui.support.UISupport
import com.eviware.soapui.LogMonitor.*
import com.eviware.soapui.SoapUI
import groovy.sql.Sql
import soapUIScripts.*

/**
 *
 * @author Diganth Aswath <diganth2004@gmail.com>
 */
class assertions {
	
    def context, testRunner, messageExchange;
    def util, grUtils, log;
    
    assertions(def util, def context, def log){
        this.context = context;
        this.testRunner = testRunner;
        this.log = log;
        this.util = util;    
    }
        
    assertions(def util, def context, def messageExchange, def log){
        this.util = util;
        this.context = context;
        this.messageExchange = messageExchange;
        this.testRunner = testRunner;
        this.log = log;
    } 
   
    def assertMailClass(def response, def dbData){
        def assertionResult = null;
        def respMailClass = util.readXMLNodeValue(response, '//ns1:CreateIndiciumResponse[1]/ns1:Rate[1]/ns1:ServiceType[1]');
        
        
        if (respMailClass != -999 && dbData != null){
            if( (respMailClass == "US-FC" && dbData == "FIRST_CLASS") 
                || (respMailClass == "US-PM" && dbData == "PRIORITY")
                || (respMailClass == "US-XM" && dbData == "EXPRESS")
                || (respMailClass == "US-PP" && dbData == "PARCEL_POST")
                || (respMailClass == "US-MM" && dbData == "MEDIA_MAIL")
                || (respMailClass == "US-BPM" && dbData == "BOUND_PRINTED_MATTER")
                || (respMailClass == "US-LM" && dbData == "LIBRARY_MAIL")
                || (respMailClass == "US-EMI" && dbData == "EXPRESS_MAIL_INTERNATIONAL")
                || (respMailClass == "US-PMI" && dbData == "PRIORITY_MAIL_INTERNATIONAL") 
                || (respMailClass == "US-FCI" && dbData == "FIRST_CLASS_MAIL_INTERNATIONAL")
                || (respMailClass == "US-CM" && dbData == "CRITICAL")
                || (respMailClass == "US-PS" && dbData == "Parcel_Select")){
                assertionResult = "passed";
            }
            else {
                assertionResult = "Mismatch. RequestData - $respMailClass does not match DB Data - $dbData";
            }
        }
        else {
            assertionResult = "Error. RequestData and DB Data is null.";
        }
        return assertionResult;
    }
    
    def assertPostageAmount(def request, def response, def dbData){
        
        def assertionResult = null, addOnValue = null;
        def addOnVersionText = util.readTestCaseProperty("AddOnVersionText");
        def addOnNodeCount = util.countXMLNode(request, "//ns1:CreateIndicium[1]/ns1:Rate[1]/ns1:AddOns[1]/ns1:$addOnVersionText");  
        def responseAmount = util.readXMLNodeValue(response, "//ns1:CreateIndiciumResponse[1]/ns1:Rate[1]/ns1:Amount[1]");
        log.debug("StartCount: $addOnNodeCount");
        log.debug("ResponseAmt: $responseAmount");
        log.debug("count: $addOnNodeCount")
        
        Double totalPostage_Response = util.convertToDouble(responseAmount, "#.00");
        Double totalPostage_DB = util.convertToDouble(dbData.toString(), "#.00");
        if (addOnNodeCount.toInteger() >  0) {
            log.debug("entered if");
            for (int i = 1; i <= addOnNodeCount; i++)
            {
                addOnValue = util.readXMLNodeValue(response, "//ns1:CreateIndiciumResponse[1]/ns1:Rate[1]/ns1:AddOns[1]/ns1:$addOnVersionText[$i]/ns1:Amount[1]");
                log.debug("addonValue $addOnValue")
                if (addOnValue != null)
                    totalPostage_Response = totalPostage_Response + util.convertToDouble(addOnValue, "#.00");
            }
        }
        
        log.debug("am here");
        if (totalPostage_Response == totalPostage_DB){
            assertionResult = "passed";
        }
        else{
            assertionResult = "Mismatch. Response Postage - $totalPostage_Response does not match DB Data - $totalPostage_DB";
        }
        return assertionResult;
    }
    
    
}

