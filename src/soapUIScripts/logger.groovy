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
 */
class logger {
    def dirName, destDirName;
    def util;
    //def util, myTestCase, myTestCaseName, myTestSuiteName, dirName;
    File logFile, resultFile;

    //Initialize Log file
    logger (utility util)
    {
        this.util = util;
    }
    def createLogFile(def propertyLocation){
        dirName = util.dirName(propertyLocation);
        logFile = fileCreator(propertyLocation, dirName, "log.log");
    }
    def createResultFile(def propertyLocation){
        dirName = util.dirName(propertyLocation);
        resultFile = fileCreator(propertyLocation, dirName, "DataSink.txt");
    }
          
    File fileCreator(def propertyLocation, def directoryName, def fileName){
        File directoryObj, fileObj;   
        directoryObj = new File(dirName);

        if(!directoryObj.isDirectory() && propertyLocation != "Project" && util.readProperty("LogFileLocation").toString()== "0")//checking if folder exists
        {
            SoapUI.log "SoapUIScript.jar::Creating Directory :" + directoryName + "as it does not exist"
            directory.mkdirs()
            fileObj = new File( logDir, fileName)
            util.writeProperty("LogFileLocation", directoryName)
        }
        else if (!directoryObj.isDirectory() && propertyLocation == "Project" && util.readTestCaseProperty("LogFileLocation").toString() == "0")
        {
            SoapUI.log "SoapUIScript.jar::Creating Directory :" + directoryName + "as it does not exist"
            directoryObj.mkdirs()
            fileObj = new File( directoryObj, fileName)
            util.writeTestCaseProperty("LogFileLocation", directoryName)
        }
        else if (propertyLocation != "Project"){
            SoapUI.log "SoapUIScript.jar::Log Directory and File already exist at :" + directoryName
            directoryObj = new File(util.readProperty("LogFileLocation"))
            fileObj = new File( directoryObj, fileName)  
        }
        else {
           SoapUI.log "SoapUIScript.jar::Log Directory and File already exist at :" + directoryName
           directoryObj = new File(util.readTestCaseProperty("LogFileLocation"))
           fileObj = new File( directoryObj, fileName) 
        }
        
        return fileObj;
    }
    def info(String comment){
        logFile << "${util.today()}:INFO:${comment}" << "\r\n"
        //captureLogs ("soapUI log");
    }
    def error(String error){
        logFile << "${util.today()}:ERROR:${error}" << "\r\n"
        //captureLogs ("error log");
    }
    def debug(String debug){
        logFile << "${util.today()}:DEBUG:${debug}" <<"\r\n"
        //captureLogs ("http log");
    }
    //Captures SOAPUI logs.
    def captureLogs (String logType){
        def logArea = com.eviware.soapui.SoapUI.logMonitor.getLogArea( logType );
        if( logArea !=null )
        {
            def model = logArea.model
            SoapUI.log model.size
            if( model.size > 0 ){
                for( c in 0..(model.size-1) ){
                    logFile.append(model.getElementAt( c ))
                    logFile <<"\r\n"
                }
                logArea.clear();
            }
        }
    }
    //Save Data in DataSink file
    def results (def data){
        resultFile << data << "\r\n";
    }
}
