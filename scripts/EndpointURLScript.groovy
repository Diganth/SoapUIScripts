/*
 @Author : Pradeep Bishnoi
 @Modified : Diganth Aswath
 @Description : Setting my customized endpoint URL
@Usage : Place the script in the Project Load/Save script section
*/
def UpdateEndpoint(Endpoint){
	testCaseList = testSuite.getTestCases()
	log.info " ${text*5} TestSuite :: $testSuite.name"
	testCaseList.each
	 {
	 testCase = testSuite.getTestCaseByName(it.key)
	 log.info " ${text*5} Testcase :: $testCase.name"

	 wsdlTestSteps = testCase.getTestStepsOfType( com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep.class )  //only WsdlTestRequest steps
	 wsdlTestSteps.each
	  {
	  //log.info it.properties['Endpoint'].value
	  it.properties['Endpoint'].value = Endpoint
	  }
	 }
	 log.info "All the endpoints are now : $Endpoint"
}
def myEndpoint1 = project.getPropertyValue("myEndpoint1").toString()
def myEndpoint2 = project.getPropertyValue("myEndpoint2").toString()
def myEndpoint3 = project.getPropertyValue("myEndpoint3").toString()
 testSuiteList = project.getTestSuites()
 text = "~"
  testSuiteList.each
 {
 testSuite = project.getTestSuiteByName(it.key)
 log.info testSuite.name
 switch ( testSuite.name) {
 	case "AmazonV2 TestSuite":
 		UpdateEndpoint(myEndpoint1)
	case "AmazonV3 TestSuite":
		UpdateEndpoint(myEndpoint2)
	case "AmazonV3 ProdTests":
		UpdateEndpoint(myEndpoint3)
	default:
		log.info "Error"
 }
 }