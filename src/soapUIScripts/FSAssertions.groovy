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

/**
 *
 * @author Diganth Aswath <diganth2004@gmail.com>
 */
class FSAssertions {
	
    def context, testRunner, messageExchange;
    def util, grUtils, log;
    
    FSAssertions(def util, def context, def log){
        this.context = context;
        this.testRunner = testRunner;
        this.log = log;
        this.util = util;    
    }
        
    FSAssertions(def util, def context, def messageExchange, def log){
        this.util = util;
        this.context = context;
        this.messageExchange = messageExchange;
        this.testRunner = testRunner;
        this.log = log;
    } 
    
    def assertFaultString(def response, def dsData){
        def assertionResult = null;
        def testCaseName = util.readTestCaseProperty("CurrentTestName");
        
        if (response != null){
            String faultString_Response = util.readXMLNodeValue(response, "//faultstring[1]").toString();
            String faultString_DataSource = dsData.toString();
            log.debug("$testCaseName:: FaultString - $faultString_Response");
            log.debug("$testCaseName:: DS Data - $dsData");
            if (faultString_Response.length() > 1 && faultString_DataSource.length() > 1){
                if (dsData.toString().contains(faultString_Response)){
                    assertionResult = "Match. Response FS = DataSource FS.";
                }
                else{
                    assertionResult = "Mismatch. Response FS - [$faultString_Response] does not match DS FS - [$dsData]";
                }
            }
            else if(faultString_Response.length() > 1 && faultString_DataSource.length() < 1){
                assertionResult = "Mismatch. FS - [$faultString_Response] not expected";
            }
            else if(faultString_Response == null && faultString_DataSource.length() > 1){
                assertionResult = "Mismatch. FS expected but got none. Expected FS - [$dsData]";
            }
            else{
                assertionResult = "Match. No faultstring";
            }
        }
        else{
            assertionResult = "Fail. Response data missing";
        }
        return assertionResult;
    }
    
    def assertGetUSPSRates(def request, def response, def dataSource){
        
    }
}

