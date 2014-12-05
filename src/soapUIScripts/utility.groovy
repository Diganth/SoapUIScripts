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
import com.eviware.soapui.support.UISupport
import com.eviware.soapui.LogMonitor.*
import com.eviware.soapui.SoapUI
import soapUIScripts.*
/**
 *
 * @author Diganth Aswath <diganth2004@gmail.com>
 */
class utility {
    def today, todayDate, todayTime, propName;
    def context;
    
    utility(def context, String propName){
        this.context = context;
        this.propName = propName;
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
    def dirName(){
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
            return 0;
        }
        else
            return this.propertyTestStep().getPropertyValue(property);
    }
    //Writes to Property file
    def writeProperty(def property, def value){
        this.propertyTestStep().setPropertyValue(property, value);
    }
    //Reads from TestSuite property
    def readTestSuiteProperty(def property){
        if (this.testSuite().getPropertyValue(property) == null){
            SoapUI.log "SoapUIScript.jar::Check TestSuite Property Name. Supplied property name does not match " + property;
            return 0;
        }
        else
            return this.testSuite().getPropertyValue(property);
    }
    //Write to TestSuite property
    def writeTestSuiteProperty(def property, def value){
        this.testSuite().setPropertyValue(property, value);
    }
}

