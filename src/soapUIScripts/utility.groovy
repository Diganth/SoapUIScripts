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
import com.eviware.soapui.impl.wsdl.panels.support.MockTestRunContext
import com.eviware.soapui.model.project.ProjectFactoryRegistry
import com.eviware.soapui.model.support.ModelSupport
import com.eviware.soapui.support.GroovyUtils
import com.eviware.soapui.support.UISupport
import com.eviware.soapui.LogMonitor.*
import com.eviware.soapui.SoapUI
import groovy.sql.Sql
import java.text.DecimalFormat
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document
import org.xml.sax.InputSource
/**
 *
 * @author Diganth Aswath <diganth2004@gmail.com>
 * @description Handles all the interaction with SoapUI and other helper functions that are used regularly in SoapUI.
 */
public class utility {
    private def today, todayDate, todayTime, propName, log;
    private def context, testRunner, grUtils;
    
    private utility(def context, def testRunner, String propName){
        this.context = context;
        this.propName = propName;
        this.testRunner = testRunner;
        this.grUtils = new GroovyUtils(context);
        today = new Date();  
    }
    
    private utility(def context, String propName){
        this.context = context;
        this.propName = propName;
        this.grUtils = new GroovyUtils(context);
        today = new Date();  
    }
    
    //Returns Today's date and time
    private def today(){
        return today.toString();
    }
    //Returns Today's Date in a readable format
    private def todayDate(){
        //today = new Date();
        return today.getDateString().split('/').join('_');
    }
    //Returns Today's Time in a readable format
    private def todayTime(){
        //today = new Date();
        return today.getTimeString().split(':').join('_');
    }
    /**
     * Get TestStepName in SoapUI TestCase.
     * @param testStepName Name of the testcase.
     * @return SoapUI TestStep Object.
    */
    def testStep(String testStepName){
        return context.testCase.getTestStepByName(testStepName);
    }
    /**
     * Get SoapUI TestCase in current context.
     * @return SoapUI TestCase Object.
    */
    def testCase(){
        return context.testCase;
    }
    /**
     * Get SoapUI TestSuite in current context.
     * @return SoapUI TestSuite Object.
    */
    def testSuite(){
        return context.testCase.testSuite;
    }
    /**
     * Get a list of all SoapUI TestSteps in current context.
     * @return Object List of SoapUI TestSteps.
    */
    def testStepsList(){
        return context.testCase.getTestStepList();
    }
    /**
     * Get SoapUI Project in current context.
     * @return SoapUI Project object.
    */
    def project(){
        return context.testCase.testSuite.project;
    }
    /**
     * Get data values via XPath
     * @param Xpath like <code>$&#123;GetAccountInfo#Response#//*:CustomerID[1]&#125;</code>.
     * @return Xpath value as a string.
    */
    def contextExpand(String values){
        if (values != null && values.length() != 0 && values.startsWith("\${") && values.endsWith("}"))
        return context.expand(values)
        else
        return values
    }
    /**
     * Get Soap request for a specific SoapUI test step in current context
     * @param TestStepName of the Soap request.
     * @return Soap request as a string.
    */
    def getRawRequest(def testStep){
        String rawRequest = null, request = null;
        int firstIndex = 0, lastIndex = 0;
        
        request = testStep.getProperty("RawRequest").getValue();
        
        if (request != null && request.toLowerCase().contains("<soapenv:envelope")){
            firstIndex = request.toLowerCase().indexOf("<soapenv:envelope");
            lastIndex = request.toLowerCase().indexOf("</soapenv:envelope");
            rawRequest = request.substring(firstIndex, lastIndex + "</soapenv:envelope>".length());
            SoapUI.log("SoapUIScript.jar::RawRequest -" + rawRequest);
        }
        else{
            rawRequest = testStep.getProperty("Request").getValue();
            SoapUI.log("SoapUIScript.jar::RawRequest -" + rawRequest);
        }
        
        return rawRequest;
    }
    //Returns the Directory name where results and logs of the current testcase are located.
    private def dirName(def dirLocation){
        if (dirLocation == "Project")
        return this.readProjectProperty("ResultFilePath") + testSuite().name + '_' + testCase().name + '_' + todayDate() + '_' + todayTime() + '/';
        else  
        return this.readProperty("ResultFilePath") + testSuite().name + '_' + testCase().name + '_' + todayDate() + '_' + todayTime() + '/';
    }
    //Returns the testStep with specified Property Name
    private def propertyTestStep(){
        if(context.testCase.getTestStepByName(propName) == null)
        SoapUI.log "SoapUIScript.jar::Check Property File Name. Supplied property name does not exist."
        else
        return context.testCase.getTestStepByName(propName);
    }
    /**
     * Get Property value from a SoapUI Property TestStep in current testCase context. This is used when there is only one Property TestStep in the test case.
     * @param property Property Name as a string.
     * @return Value of the property as a string.
    */
    def readProperty(def property){
        if (this.propertyTestStep().getPropertyValue(property) == null){
            SoapUI.log "SoapUIScript.jar::Check Property Name. Supplied property name does not match " + property;
            return "-1";
        }
        else
        return this.propertyTestStep().getPropertyValue(property);
    }
    /**
     * Write Value to a Property in a SoapUI Property TestStep in current testCase context. This is used when there is only one Property Teststep in the test case.
     * @param property Property Name as a string
     * @param value Property Value as a string
    */
    def writeProperty(def property, def value){
        this.propertyTestStep().setPropertyValue(property, value);
    }
    /**
     * Get Property value from a SoapUI Property TestStep in current testCase context. This is used when there are multiple Property TestSteps in the test case.
     * @param testStepName Name of the Property TestStep as a string.
     * @param property Property Name as a string.
     * @return Value of the property as a string.
    */
    def readTestStepProperty(def testStepName, def property){
        if (this.testStep(testStepName).getPropertyValue(property) == null){
            SoapUI.log "SoapUIScript.jar::Check TestStep Property Name. Supplied property name does not match " + property;
            return "-1";
        }
        else
        return this.testStep(testStepName).getPropertyValue(property);
    }
    /**
     * Write Value to a Property in SoapUI Property TestStep in current testCase context. This is used when there are multiple Property TestSteps in the test case.
     * @param testStepName Name of the Property TestStep as a string.
     * @param property Property Name as a string.
     * @param value Property Value as a string.
    */
    def writeTestStepProperty(def testStepName, def property, def value){
        this.testStep(testStepName).setPropertyValue(property, value);
    } 
    
    /**
     * Get Property Value from a SoapUI TestSuite in current context.
     * @param property Property Name as a string.
     * @return Value of the property as a string.
    */
    def readTestSuiteProperty(def property){
        if (this.testSuite().getPropertyValue(property) == null){
            SoapUI.log "SoapUIScript.jar::Check TestSuite Property Name. Supplied property name does not match " + property;
            return "-1";
        }
        else
        return this.testSuite().getPropertyValue(property);
    }
    /**
     * Write Value of a Property in SoapUI TestSuite in current context.
     * @param property Property Name as a string.
     * @param value Property Value as a string.
    */
    def writeTestSuiteProperty(def property, def value){
        this.testSuite().setPropertyValue(property, value);
    } 
    /**
     * Get Property Value from a SoapUI TestCase in current context.
     * @param property Property Name as a string.
     * @return Value of the property as a string.
    */
    def readTestCaseProperty(def property){
        if (this.testCase().getPropertyValue(property) == null){
            SoapUI.log "SoapUIScript.jar::Check TestCase Property Name. Supplied property name does not match " + property;
            return "-1";
        }
        else
        return this.testCase().getPropertyValue(property);
    }
    /**
     * Write Value of a Property in SoapUI TestCase in current context.
     * @param property Property Name as a string.
     * @param value Property Value as a string.
    */
    def writeTestCaseProperty(def property, def value){
        this.testCase().setPropertyValue(property, value);
    } 
    /**
     * Get Property Value from a SoapUI Project in current context.
     * @param property Property Name as a string.
     * @return Value of the property as a string.
    */
    def readProjectProperty(def property){
        if (this.project().getPropertyValue(property) == null){
            SoapUI.log "SoapUIScript.jar::Check Project Property Name. Supplied property name does not match $property";
            return "-1";
        }
        else 
        return this.project().getPropertyValue(property);
    }
    /**
     * Write Value of a Property in SoapUI Project in current context.
     * @param property Property Name as a string.
     * @param value Property Value as a string.
    */
    def writeProjectProperty(def property, def value){
        this.project().setPropertyValue(property, value);
    }
    
    /**
     * Retrieve Count of a datasource grid or data from a datasink subreport in current context.
     * @param sourceName Name of the DataSource or DataSink as a string.
     * @param info There are 2 possible values for this -
     *              <ul>
     *              <li>count - This indicates that a count of the datasource/datasink is requested.
     *              <li>info - This indicates that all the data from the datasink is requested.
     *              </ul>
     * @return  Count of Datasource or Data from DataSink is returned as a string.
    */
    def dsProperty(def sourceName, def info){
        def ds = context.testCase.testSteps[sourceName];
        def hashmap = ds.getProperties(); 
        def returnData, result;
        if (info.toLowerCase() == "count")
        {   
            returnData = hashmap.size();
        }
        if (info.toLowerCase() == "data")
        {
            int count = hashmap.size();
            if (count != 0){
                result = hashmap.values();
            }
            returnData = result;   
        }
        if (returnData == null && returnData.size() == 0)
            returnData = -1;
        return returnData;
    }
    /**
     * Checks if a specific column is present in a datasource grid.
     * @param dataSourceName Name of the DataSource as a string.
     * @param property Name of the column whose presence is to be determined.
     * @return Boolean value if property exists in datasource.
    */
    Boolean isAvailableInDataSource(def dataSourceName, def property){
        Boolean isPresent = false;
        def dataSourceData = dsProperty(dataSourceName, "data");
        if (dataSourceData != -1)
        {
            for (int i=0; i<dataSourceData.size(); i++)
            {
                if (dataSourceData[i].getName() == property)
                isPresent = true;
            }
        }
        return isPresent;
    }
    /**
     * Establishes a JDBC connection to a database.
     * @param dbServerName HostName of the database server to connect with.
     * @param dbName Name of the database to connect with.
     * @return SQLInstance Object of the connection to the database that can be used in other functions when querying the db.
    */
    def connectToDB(String dbServerName, String dbName){
        com.eviware.soapui.support.GroovyUtils.registerJdbcDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver")
        String connectionString = String.format("jdbc:sqlserver://%s:1433;databaseName=%s;integratedSecurity=true", dbServerName, dbName);
        def db = [url:connectionString, driver:'com.microsoft.sqlserver.jdbc.SQLServerDriver'];
        Sql connectionResult = null;
        try{
            connectionResult = Sql.newInstance(db.url, db.driver);
            SoapUI.log "SoapUIScript.jar::DB Connection Sucess...";
            return connectionResult;
        }
        catch(Exception e){
            SoapUI.log "SoapUIScript.jar::DB Connection unsuccessful. Error message : " + e.message;
            return connectionResult;
        }
    }
    /**
     * Executes a Select DB query
     * @param sqlInstance SQLInstance Object obtained as a return value from connectToDB().
     * @param SQLQuery The Query that needs to be executed against the database as a string.
     * @return Result(s) of the Query as a String Array.
    */
    def executeDBQuery(Sql sqlInstance, String SQLQuery){
        def rowCount;
        def queryResult;
        if (sqlInstance == null ) {
            queryResult = null;
        }
        else {
            try{
                rowCount = sqlInstance.rows(SQLQuery);
                if (rowCount.size() >= 1){
                    queryResult = sqlInstance.firstRow(SQLQuery);
                }
                closeDB(sqlInstance);
            }
            catch (Exception e){
                SoapUI.log "SoapUIScript.jar::Unable to get row count for SQL Query. Error message : " + e.message;
            } 
        }
        return queryResult;
    }
     /**
     * Executes an Insert DB query
     * @param sqlInstance SQLInstance Object obtained as a return value from connectToDB().
     * @param SQLQuery The Query that needs to be executed against the database as a string.
     * @return "[Success]" if insert query was successful else "[NULL]".
    */
    def insertDBQuery(Sql sqlInstance, String SQLQuery){
        def queryResult;
        if (sqlInstance == null) {
            queryResult = null;
        }
        else {
            try {
                sqlInstance.execute(SQLQuery);
                if (sqlInstance.updateCount == 1){
                    queryResult = "Success";
                }
                closeDB(sqlInstance);
            }
            catch (Exception e){
                SoapUI.log "SoapUIScript.jar::Unable to insert to DB. Error message : " + e.message;
            }
        }
        return queryResult;
    }
    /**
     * Executes an Update DB query
     * @param sqlInstance SQLInstance Object obtained as a return value from connectToDB().
     * @param SQLQuery The Query that needs to be executed against the database as a string.
     * @return "[Success]" if update query was successful else "[NULL]".
    */
    def updateDBQuery(Sql sqlInstance, String SQLQuery) {
        def queryResult;
        queryResult = insertDBQuery(sqlInstance, SQLQuery);
        return queryResult;
    }
    /**
     * Closes connection to the DB opened via connectToDB(). 
     * @param sqlInstance SQLInstance Object obtained as a return value from connectToDB().
    */
    void closeDB(Sql sqlInstance){
        sqlInstance.close();
        SoapUI.log "SoapUIScript.jar::Closing DB connection.";
    }
    //Create XML HOlder for all namespaces except "fautlstring"
    private def createXMLHolder(def data){
        def nameSpace = readProjectProperty("Namespace");
        def xmlHolder = null;
        
        if (data != null){
            xmlHolder = grUtils.getXmlHolder(data);
            xmlHolder.declareNamespace("ns1", nameSpace);
        }
        else{
            xmlHolder = null;
        }
        return xmlHolder;
    }
    // Overriding Create XML Holder for 'faultString' namespace
    private def createXMLHolder(def data, def requestType){
        def nameSpace = null;
        def xmlHolder = null;
        
        if (requestType == "fault")
        readProjectProperty("FaultStringNamespace");
        else 
        readProjectProperty("Namespace");
        if (data != null){
            xmlHolder = grUtils.getXmlHolder(data);
            xmlHolder.declareNamespace("ns1", nameSpace);
        }
        else{
            xmlHolder = null;
        }
        return xmlHolder;
    }
    /**
     * Quick way to access elements of an XML. Mostly used to read Soap Requests and Responses to get values of various elements.
     * @param data Soap request or response as a string.
     * @param nodeTag The element in the XML whose data is required.
     * @return Node value of the desired node as a string.
    */
    def readXMLNodeValue(def data, def nodeTag){ 
        def nodeVal = null;
        def holder = null;
        
        if (nodeTag == null)
        return nodeVal;
        else if (nodeTag.toString().toLowerCase().contains("faultstring"))
        holder = createXMLHolder(data, "fault");
        else
        holder = createXMLHolder(data);

        if (holder != null){
            if (nodeTag != null && nodeTag.contains("Authenticator")){
                nodeVal = holder.getNodeValue(nodeTag);
                nodeVal = "<![CDATA[" + nodeVal + "]]>";
            }
            else{
                nodeVal = holder.getNodeValue(nodeTag);
            }
        }
        else {
            nodeVal = null;
        }
        return nodeVal;
    }
    /**
     * Quick way to find the count of a node in an XML. Used to figure out number of AddOn Nodes in a request or response.
     * @param data Soap request or response as a string.
     * @param nodeTag The element name for whom we are requesting a count of.
     * @return Count of the desired node as a string.
    */
    def countXMLNode(def data, String nodeTag){ 
        def nodeCount = null;
        def holder = null;
        
        if (nodeTag.toString().toLowerCase().contains("faultstring"))
        holder = createXMLHolder(data, "fault");
        else
        holder = createXMLHolder(data);
        
        if (holder != null) {
            nodeCount = holder.getNodeValues(nodeTag);
        }
        else {
            nodeCount = 0;
        }
        return nodeCount.size();
    }
    /**
     * Converts String to a Double with a desired format.
     * @param text Value that needs to be converted to a double as a string.
     * @param format The format of double. Possible formats - "0.00", "0.000" etc.
     * @return Double value of the text in desired format.
    */
    Double convertToDouble(String text, def format){
        def dFormat;
        if (format == 'null')
        dFormat = "#.00";
        else
        dFormat = format;
            
        DecimalFormat df = new DecimalFormat(dFormat);
	if (text != null){
            if (text.length() > 0){
                Double tempVar = text.toDouble();
                String convVar = df.format(tempVar);
                return convVar.toDouble();
            }
            else{
                return 0.00;
            }
	}
	else{
            return 0.00;
	}	
    }
    /**
    *   Checks Authenticator to determine if 'SAS' user.
    *  @param authenticator Authenticator from Soap response.
    *  @return Boolean value if it's a SAS user ot not.
    */
    Boolean isSASUser(String authenticator){
        String[] authArray = new String[25];
        authArray = authenticator.split("&");
        boolean isSASUser = false;
        
        for (String s:authArray){
            if (s.contains("reseller")){
                String[] tempArray = new String[2];
                tempArray = s.split("=");
                if (tempArray[1] != "0")
                isSASUser = true;
            }
        }
        return isSASUser;
    }
    /**
    * Check Response to check if contains faultString
    * @param soapData Soap response as a string.
    * @return Boolean value if soapData is a faultstring or not.
    */
    Boolean isFaultString(String soapData){
        boolean isFaultString = false;
        if (soapData.contains("faultstring") == true){
            isFaultString = true;
        }
        return isFaultString;
    }
}

