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

/**
 * @author Diganth Aswath <diganth2004@gmail.com>
 * @description This class is used to log request, response and other miscellaneous data into a log file or  log the datasink into a results file.
 */
public class logger {
    private def dirName, destDirName;
    private def util;
    private File logFile, resultFile;
    
    /** Initializes the logger class with the uitl object.
     * @param util soapUIScripts.utility object.
     */
    logger (def util)
    {
        this.util = util;
    }
    /** Main function to be called to create a log file "log.log" at a specified location.
     * @param propertyLocation Directory location for the log file.
     */
    private def createLogFile(def propertyLocation){
        dirName = util.dirName(propertyLocation);
        logFile = fileCreator(propertyLocation, dirName, "log.log");
    }
    /** Main function to be called to creates a result file at a specified location for a specified DataSink.
     * @param propertyLocation Directory location for the results file.
     * @param dataSinkName Name of the data sink that needs to be saved.
     */
    private def createResultFile(def propertyLocation, def dataSinkName){
        def fileName = dataSinkName;
        dirName = util.dirName(propertyLocation);
        resultFile = fileCreator(propertyLocation, dirName, fileName);
    }
    /** Helper function used by both createLogFile() and createResultFile() that returns a File Obj to the prior mentioned methods.
     * @param propertyLocation Directory location for the files to be created at.
     * @param fileName Name of the file to be created. Determined by values passed in from createLogFile() or createResultFile()
     * @return Java.IO.File object
     */
    private File fileCreator(def propertyLocation, def directoryName, def fileName){
        File directoryObj, fileObj;   
        directoryObj = new File(dirName);

        if(!directoryObj.isDirectory() && propertyLocation != "Project" && (util.readProperty("LogFileLocation").toString()== "0" || util.readProperty("LogFileLocation").toString()== "-1"))//checking if folder exists
        {
            SoapUI.log "SoapUIScript.jar::Creating Directory :" + directoryName + "as it does not exist"
            directoryObj.mkdirs()
            fileObj = new File( directoryObj, fileName)
            util.writeProperty("LogFileLocation", directoryName)
        }
        else if (!directoryObj.isDirectory() && propertyLocation == "Project" && (util.readTestCaseProperty("LogFileLocation").toString() == "0" || util.readTestCaseProperty("LogFileLocation").toString() == "-1"))
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
    /** Logs info statements with "Info:" tag in the log.log file created using createLogFile()
     * @param comment Information string that needs to be saved in the log file.
     */
    def info(String comment){
        logFile << "${util.today()}:INFO:${comment}" << "\r\n"
        //captureLogs ("soapUI log");
    }
    /** Logs error statements with "Error:" tag in the log.log file created using createLogFile()
     * @param error Error string that needs to be saved in the log file.
     */
    def error(String error){
        logFile << "${util.today()}:ERROR:${error}" << "\r\n"
        //captureLogs ("error log");
    }
    /** Logs debug statements with "Debug:" tag in the log.log file created using createLogFile()
     * @param debug Debug string that needs to be saved in the log file.
     */
    def debug(String debug){
        logFile << "${util.today()}:DEBUG:${debug}" <<"\r\n"
        //captureLogs ("http log");
    }
    /** This function can be used to capture logs that are created by SoapUI.
     * @param logType This parameter can have any of the following strings: SoapUI, error, http
     */
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
    /** This is an internal function used to write any string value to a result file created by createResultFile(). 
     * @param data String that needs to be appended to the file.
     */
    def results (def data){
        resultFile << data << "\r\n";
    }
}
