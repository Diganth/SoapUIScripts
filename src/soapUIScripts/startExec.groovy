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
import com.eviware.soapui.LogMonitor.*
import com.eviware.soapui.SoapUI
import soapUIScripts.utility
/**
 *
 * @author Diganth Aswath <diganth2004@gmail.com>
 * @description This is the main class of the project. This essentially
 *              creates an execution loop in SoapUI that executes any request placed
 *              within two groovy scripts called DataSource and DataLoop. The number
 *              of times the loop executes is determined by values "LoopCount" and 
 *              "LoopTotal" which are obtained from the property file.
 */
class startExec {
        
    def context, util, log, testRunner, evaluator;
    
    startExec(def context, def testRunner, def propertyName){
        this.context = context
        this.testRunner = testRunner
        util = new utility(context, propertyName); 
        log = new logger(util);
        log.createLogFile();
        evaluator = new evalRequests(util, context, log);
    }
        
     def startLoop(){
        def counter, next, size;
        counter = util.readProperty("LoopCount").toString()
        counter = counter.toInteger() 
        size = util.readProperty("LoopTotal").toString()
        size = size.toInteger()
        next = (counter > size-2? 0: counter+1)
        util.writeProperty("LoopCount", next.toString())
        next++;
        log.info ("Counter Value : " +counter)
        util.writeProperty("LoopNextValue", next.toString())
        log.info ("Next Value : " +next)
        if (counter == size-1){
            util.writeProperty("StopLoop", "T")
            log.info ("Setting the stoploop property now ...")
        }
        else if (counter==0){
            def runner = new com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner(testRunner.testCase, null)
            log.info ("Starting the DataLoop now ...")
            util.writeProperty("StopLoop", "F")
        }
        else{
            util.writeProperty("StopLoop", "F")
        }
    }
    
    def executeLoopRequests(){
        String error = null;
        evaluator.testCaseIterator();
        def endLoop = util.readProperty("StopLoop").toString()
        if (endLoop.toString()=="T" || endLoop.toString()=="True" || endLoop.toString()=="true"){
            log.info ("Exiting the Data Source Looper")
            util.writeProperty("LogFileLocation", "0") //Erasing log file location in the property file.
            assert true
        }
        else{
            testRunner.gotoStepByName("DataSource") // Starting TestStep -- Note that "DataSource" needs to be name of starting groovy script in SOAPUI.
        }
    }
    
    def executeOnce(){
        String error = null;
        SoapUI.log ("SoapUIScript.jar::In Execute Once()")
        error = evaluator.testCaseIterator();
        return error;
    }

}

