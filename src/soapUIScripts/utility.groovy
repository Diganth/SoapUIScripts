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
import com.eviware.soapui.support.GroovyUtils
import com.eviware.soapui.support.UISupport
import com.eviware.soapui.LogMonitor.*
import com.eviware.soapui.SoapUI
import groovy.sql.Sql
import java.text.DecimalFormat
import soapUIScripts.*
/**
 *
 * @author Diganth Aswath <diganth2004@gmail.com>
 * @description: wrapper class to use SOAPUI groovy commands.
 */
protected class utility {
    def today, todayDate, todayTime, propName;
    def context, testRunner, grUtils;
    
    protected utility(def context, def testRunner, String propName){
        this.context = context;
        this.propName = propName;
        this.testRunner = testRunner;
        this.grUtils = new GroovyUtils(context);
        today = new Date();  
    }
  
    //Returns Today's date and time
    def today(){
        return today.toString();
    }
    //Returns Today's Date in a readable format
    def todayDate(){
        //today = new Date();
        return today.getDateString().split('/').join('_');
    }
    //Returns Today's Time in a readable format
    def todayTime(){
        //today = new Date();
        return today.getTimeString().split(':').join('_');
    }
    //Returns testStep by name
    def testStep(String testStepName){
        return context.testCase.getTestStepByName(testStepName);
    }
    //Returns current testcase
    def testCase(){
        return context.testCase;
    }
    //Returns TestSuite to which the testcase belongs to.
    def testSuite(){
        return context.testCase.testSuite;
    }
    //Returns all the teststeps in the Test Suite
    def testStepsList(){
        return context.testCase.getTestStepList();
    }
    //Returns the project in which the Test Suite is located in
    def project(){
        return context.testCase.testSuite.project;
    }
    //Returns the Directory name where results and logs of the current testcase are located.
    def dirName(def dirLocation){
        if (dirLocation == "Project")
            return this.readProjectProperty("ResultFilePath") + testSuite().name + '_' + testCase().name + '_' + todayDate() + '_' + todayTime() + '/';
        else  
            return this.readProperty("ResultFilePath") + testSuite().name + '_' + testCase().name + '_' + todayDate() + '_' + todayTime() + '/';
    }
    //Returns the testStep with specified Property Name
    def propertyTestStep(){
        if(context.testCase.getTestStepByName(propName) == null)
            SoapUI.log "SoapUIScript.jar::Check Property File Name. Supplied property name does not exist."
        else
            return context.testCase.getTestStepByName(propName);
    }
    //Reads from Property file
    def readProperty(def property){
        if (this.propertyTestStep().getPropertyValue(property) == null){
            SoapUI.log "SoapUIScript.jar::Check Property Name. Supplied property name does not match " + property;
            return -1;
        }
        else
            return this.propertyTestStep().getPropertyValue(property);
    }
    //Writes to Property file
    def writeProperty(def property, def value){
        this.propertyTestStep().setPropertyValue(property, value);
    }
     //Reads from TestStep property
    def readTestStepProperty(def testStepName, def property){
        if (this.testStep(testStepName).getPropertyValue(property) == null){
            SoapUI.log "SoapUIScript.jar::Check TestSuite Property Name. Supplied property name does not match " + property;
            return -1;
        }
        else
            return this.testStep(testStepName).getPropertyValue(property);
    }
    //Write to TestStep property
    def writeTestStepProperty(def testStepName, def property, def value){
        this.testStep(testStepName).setPropertyValue(property, value);
    } 
    //Reads from TestSuite property
    def readTestSuiteProperty(def property){
        if (this.testSuite().getPropertyValue(property) == null){
            SoapUI.log "SoapUIScript.jar::Check TestSuite Property Name. Supplied property name does not match " + property;
            return -1;
        }
        else
            return this.testSuite().getPropertyValue(property);
    }
    //Write to TestSuite property
    def writeTestSuiteProperty(def property, def value){
        this.testSuite().setPropertyValue(property, value);
    } 
    //Reads from TestCase property
    def readTestCaseProperty(def property){
        if (this.testCase().getPropertyValue(property) == null){
            SoapUI.log "SoapUIScript.jar::Check TestSuite Property Name. Supplied property name does not match " + property;
            return -1;
        }
        else
            return this.testCase().getPropertyValue(property);
    }
    //Write to TestCase property
    def writeTestCaseProperty(def property, def value){
        this.testCase().setPropertyValue(property, value);
    } 
    //Reads from Project Property
    def readProjectProperty(def property){
        if (this.project().getPropertyValue(property) == null){
            SoapUI.log "SoapUIScript.jar::Check Project Property Name. Supplied property name does not match $property";
            return -1;
        }
        else 
            return this.project().getPropertyValue(property);
    }
    //Write to Project Property
    def writeProjectProperty(def property, def value){
        this.project().setPropertyValue(property, value);
    }
    //Check if DataSource Element exists
    Boolean isAvailableInDataSource(def DataSourceName, def property){
        def ds = testRunner.testCase.testSteps[DataSourceName];
        def hashmap = ds.getProperties();
        Boolean isPresent = false;
        Iterator it = hashmap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry element = (Map.Entry)it.next();
            if (property  == element.getKey()){
                isPresent = true;
            }
        }
        return isPresent;
    }
    //Connect to SQL DB
    def connectToDB(String dbServerName, String dbName){
        com.eviware.soapui.support.GroovyUtils.registerJdbcDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver")
        String connectionString = String.format("jdbc:sqlserver://%s:1433;databaseName=%s;integratedSecurity=true", dbServerName, dbName);
        def db = [url:connectionString, driver:'com.microsoft.sqlserver.jdbc.SQLServerDriver'];
        def connectionResult
        try{
            connectionResult = Sql.newInstance(db.url, db.driver);
            SoapUI.log "SoapUIScript.jar::DB Connection Sucess...";
            return connectionResult;
        }
        catch(Exception e){
            connectionResult = "DBError";
            SoapUI.log "SoapUIScript.jar::DB Connection unsuccessful. Error message : " + e.message;
            return connectionResult;
        }
    }
    //Return Results for SQL queries.
    def executeDBQuery(Sql sqlInstance, String SQLQuery){
        def rowCount;
        def queryResult;
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
        return queryResult;
    } 
    //Close Opened DB Connection
    void closeDB(Sql sqlInstance){
        sqlInstance.close();
        SoapUI.log "SoapUIScript.jar::Closing DB connection.";
    }
    //Create XML Holder
    def createXMLHolder(def data){
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
    //Read Single Node Value
    def readXMLNodeValue(def data, def nodeTag){ 
        def nodeVal = null;
        def holder = createXMLHolder(data);

        if (holder != null){
            nodeVal = holder.getNodeValue(nodeTag);
        }
        else {
            nodeVal = null;
        }
        return nodeVal;
    }
    //Node Count
    def countXMLNode(def data, String nodeTag){ 
        def nodeCount = null;
        def holder = createXMLHolder(data);
        
        if (holder != null) {
            nodeCount = holder.getNodeValues(nodeTag);
        }
        else {
            nodeCount = 0;
        }
        return nodeCount.size();
    }
    //Convert Text to Double
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
    //Check Authenticator to determine if 'SAS' user.
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
    //Check Response to check if contains faultString
    Boolean isFaultString(String soapData){
        boolean isFaultString = false;
        if (soapData.contains("faultstring") == true){
            isFaultString = true;
        }
        return isFaultString;
    }
}

