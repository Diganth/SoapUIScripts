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
    def dirName;
    def util;
    //def util, myTestCase, myTestCaseName, myTestSuiteName, dirName;
    File logDir, logFile;

    //Initialize Log file
    logger (utility util)
    {
        this.util = util;
    }
    def createLogFile(){
        dirName = util.dirName();
        logDir = new File(dirName)
        if(!logDir.isDirectory() && util.readProperty("LogFileLocation").toString()== "0") //checking if folder exists
        {
            SoapUI.log "Creating Directory :" + dirName + "as it does not exist"
            logDir.mkdirs()
            logFile = new File( logDir, "log.log")
            util.writeProperty("LogFileLocation", dirName)
        }
        else {
            SoapUI.log "Log Directory and File already exist at :" + dirName
            logDir = new File(util.readProperty("LogFileLocation"))
            logFile = new File( logDir, "log.log")
            
        }
    }
    def info(String comment)
    {
        logFile << "${util.today()}::INFO::${comment}" << "\r\n"
        //captureLogs ("soapUI log");
    }
    def error(String error)
    {
        logFile << "${util.today()}::ERROR::${error}" << "\r\n"
        //captureLogs ("error log");
    }
    def debug(String debug)
    {
        logFile << "${util.today()}::DEBUG::${debug}" <<"\r\n"
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
}
