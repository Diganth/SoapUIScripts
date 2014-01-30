/*
 * @Author : Diganth Aswath 
 * @Description : Script to create the Result folder and Log File based on TestcaseName
 */
def Logger(){
	log.info "In logger function"
	myTestCase = context.testCase
	myTestCaseName = myTestCase.name
	myProjectName = myTestCase.testSuite.project.name
	filePath = "C:/Users/daswath/Copy/QA_Documents/SoapUI Projects/Result/"
	dirName = filePath+myProjectName+'_'+myTestCaseName+'_'+todayDate+'_'+todayTime+'/'
	File1 = new File(dirName).mkdirs()
	logFile = new File(dirName+"/Log.log")
}

