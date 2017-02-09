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

import com.eviware.soapui.impl.wsdl.panels.support.MockTestRunContext
import com.eviware.soapui.model.project.ProjectFactoryRegistry
import com.eviware.soapui.model.support.ModelSupport
import com.eviware.soapui.support.UISupport
import com.eviware.soapui.LogMonitor.*
import com.eviware.soapui.SoapUI
import java.lang.*
import helperScripts.*
/**
 *
 * @author Diganth Aswath <diganth2004@gmail.com>
 * @Description     This is the main class of the project and one of the below constructors need to be invoked to be able to use this jar file functionality.
 *                  Functionality that can be invoked from this class is as follows.
 *                  <ul>
 *                  <li>Capture URLs from responses, generate label images, and save them.
 *                  <li>Capture Responses from SoapUI TestRequests and save them.
 *                  <li>Capture DataSinks from SoapUI and save their contents to a CSV or Pipe Delimited file.
 *                  </ul>
 *              
 */
public class startExec {
        
    private def context, util, log, testRunner, evaluator, dbassertor, fsassertor, captureType, propertyName;
    
    /**
     * Constructor 1
     * @param context   This is the SoapUI Context parameter that needs to be passed in for the jar file to access SoapUI components.
     * <p>
     * @param propertyName  There are 2 possible values for this string variable
     *                      <ul>
     *                      <li>"Project" - This string directs the jar file to look for "ResultFilePath" property in SoapUI Project properties.
     *                      <li>TestStepPropertyName - This directs the jar file to look for "ResultFilePath" property in SoapUI property test step mentioned.
     *                      <ul/>
     */
    startExec(def context, def propertyName){
        this.captureType = captureType;
        initializeClassObjects(context, propertyName);
        evaluator = new evalRequests(util, context, log);
        dbassertor = new DBAssertions(util, context, log);
        fsassertor = new FSAssertions(util, context, log);
    }
    /**
     * Constructor 2
     * @param context   This is the SoapUI Context parameter required for the jar file to access SoapUI components.
     * @param testRunner    This is the SoapUI TestRunner paramater required for the jar file to access SoapUI components.
     * <p>
     * @param propertyName  There are 2 possible values for this string variable
     *                      <ul>
     *                      <li>"Project" - This string directs the jar file to look for "ResultFilePath" property in SoapUI Project properties.
     *                      <li>TestStepPropertyName - This directs the jar file to look for "ResultFilePath" property in SoapUI property test step mentioned.
     *                      <ul/>
     */
    startExec(def context, def testRunner, def propertyName){
        initializeClassObjects(context, testRunner, propertyName);
        evaluator = new evalRequests(util, context, log);
        dbassertor = new DBAssertions(util, context, log);
        fsassertor = new FSAssertions(util, context, log);
    }
    /**
     * Constructor 3
     * @param context   This is the SoapUI Context parameter required for the jar file to access SoapUI components.
     * @param testRunner    This is the SoapUI TestRunner paramater required for the jar file to access SoapUI components.
     * <p>
     * @param propertyName  There are 2 possible values for this string variable
     *                      <ul>
     *                      <li>"Project" - This string directs the jar file to look for "ResultFilePath" property in SoapUI Project properties.
     *                      <li>TestStepPropertyName - This directs the jar file to look for "ResultFilePath" property in SoapUI property test step mentioned.
     *                      <ul/>
     * <p>
     * @param captureType   There are 2 possible values for this string variable
     *                      <ul>
     *                      <li>"Response" - This string directs the jar file to only capture Responses from the Test Requests.
     *                      <li>"URL" - This string directs the jar file to only capture URL's from the responses of Test Requests and generate label images.
     *                      <ul/>
     */
    startExec(def context, def testRunner, def propertyName, def captureType){
        this.captureType = captureType;
        initializeClassObjects(context, testRunner, propertyName);
        evaluator = new evalRequests(util, context, log, captureType);
        dbassertor = new DBAssertions(util, context, log);
        fsassertor = new FSAssertions(util, context, log);
    }
    
    private void initializeClassObjects(def context, def testRunner, def propertyName){
        this.context = context
        this.testRunner = testRunner
        this.propertyName = propertyName
        util = new utility(context, testRunner, propertyName); 
        log = new logger(util);
        log.createLogFile(propertyName);
    }
    
    private void initializeClassObjects(def context, def propertyName){
        this.context = context
        this.propertyName = propertyName
        util = new utility(context, propertyName); 
        log = new logger(util);
        log.createLogFile(propertyName);
    }
    
    /**
     * @deprecated This functionality is deprecated. 
     */
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
    
    /**
     * @deprecated This functionality is deprecated. 
     */
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
    
    /**
     * Based on the type of constructor invoked, this function performs the following functionalities
     * <p>
     * <ul>
     * <li>Capture URLs from responses, generate label images, and saves them.
     * <li>Capture Responses from SoapUI TestRequests and saves them.
     * </ul>
     * @return String value with errors during data capture process is returned. If there are no errors, then the value returned is NULL.
     */
    def saveData(){
        String error = null;
        SoapUI.log ("SoapUIScript.jar::In SaveData()")
        error = evaluator.testCaseIterator();
        return error;
    }
    
    /**
     * Function is used to capture SoapUI DataSink and store them in .csv files.
     * This function requires the presence of a SoapUI TestCase property "CSVHeader" with value 0 to capture the DataSink Column Headers.
     * If the TestCase property is missing, the .csv file created will not contain column headers.
     * @param dataSinkName This is the name of the SoapUI data sink testStep to be captured.
     */
    def captureDataSink(def dataSinkName){
        log.createResultFile(propertyName, dataSinkName+".csv");
        String finalData = null;
        StringBuilder propertyData = new StringBuilder();
        def dataSinkData = util.dsProperty(dataSinkName, "data");
        if (dataSinkData != -1)
        {
            
            for (int i = 0; i < dataSinkData.size(); i++){
                if (util.readTestCaseProperty("CSVHeader").toString() != "1"){
                    propertyData.append(dataSinkData[i].getName());
                    propertyData.append(',');
                }
                else{
                    propertyData.append(util.contextExpand(dataSinkData[i].getValue()));
                    propertyData.append(',');
                }
            }
            finalData = propertyData.substring(0,propertyData.length()-1);
            log.results(finalData);
            util.writeTestCaseProperty("CSVHeader", "1");
        }
        else {
            SoapUI.log "Specified DataSink either does not exist or has no properties defined. Check DataSink: " + dataSinkName; 
        }
    }
    
    /**
     * Function is used to capture SoapUI DataSink and store them in "|" delimited '.dat' files.
     * This function requires the presence of a SoapUI TestCase property "PIPEHeader" with value 0 to capture the DataSink Column Headers.
     * If the TestCase property is missing, the .dat file created will not contain column headers.
     * @param dataSinkName This is the name of the SoapUI data sink testStep to be captured.
     */
    def captureDataSinkPipe(def dataSinkName){
        log.createResultFile(propertyName, dataSinkName+".dat");
        String finalData = null;
        StringBuilder propertyData = new StringBuilder();
        def dataSinkData = util.dsProperty(dataSinkName, "data");
        if (dataSinkData != -1)
        {
            for (int i = 0; i < dataSinkData.size(); i++){
                if (util.readTestCaseProperty("PIPEHeader").toString() != "1"){
                    propertyData.append(dataSinkData[i].getName());
                    propertyData.append('|');
                }
                else{
                    propertyData.append(util.contextExpand(dataSinkData[i].getValue()));
                    propertyData.append('|');
                }
            }
            finalData = propertyData.substring(0,propertyData.length()-1);
            log.results(finalData);
            util.writeTestCaseProperty("PIPEHeader", "1");
        }
        else {
            SoapUI.log "Specified DataSink either does not exist or has no properties defined. Check DataSink: " + dataSinkName; 
        }
        
    }
}

