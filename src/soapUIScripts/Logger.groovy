package soapUIScripts

import com.eviware.soapui.impl.wsdl.panels.support.MockTestRunContext
import com.eviware.soapui.model.project.ProjectFactoryRegistry
import com.eviware.soapui.model.support.ModelSupport
import com.eviware.soapui.support.UISupport
import com.eviware.soapui.LogMonitor.*

class Logger {
	def filepath, temp;
	def context;
	def today, todayDate, todayTime, myTestCase, myTestCaseName, myTestSuiteName, dirName;
	File logDir, logFile;

	//Initialize Log file
	Logger (def context, String filepath)
	{
		this.filepath = filepath;
		this.context = context;
		today = new Date();
		todayDate = today.getDateString().split('/').join('_');
		todayTime = today.getTimeString().split(':').join('_');
		myTestCase = context.testCase;
		myTestCaseName = myTestCase.name;
		myTestSuiteName = myTestCase.testSuite.name;
		dirName = filepath + myTestSuiteName + '_' + myTestCaseName + '_' + todayDate + '_' + todayTime + '/';
		logDir = new File(dirName)
		if(!logDir.exists()) //checking if folder exists
		{
			logDir.mkdirs()
			//log.info "Creating the Directory as it does not exist"
		}
		logFile = new File( logDir, "log.log")
	}
	def info(String comment)
	{
		logFile << "${today}:INFO:${comment}" << "\r\n"
		captureLogs ("script log");
	}
	def error(String error)
	{
		logFile << "${today}:ERROR:${error}" << "\r\n"
		captureLogs ("error log");
	}
	def debug(String debug)
	{
		logFile << "${today}:DEBUG:${debug}" <<"\r\n"
		captureLogs ("http log");
	}
	def captureLogs (String logType){
		def logArea = com.eviware.soapui.SoapUI.logMonitor.getLogArea( logType );
		if( logArea !=null )
		{
		   def model = logArea.model
		   if( model.size > 0 )
			  for( c in 0..(model.size-1) )
				logFile.append(model.getElementAt( c ))
				logFile <<"\r\n"
				logArea.clear();
		}
	}
}
