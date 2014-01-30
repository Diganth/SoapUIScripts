def myTestCase = context.testCase
 
def propTestStep = myTestCase.getTestStepByName("Property - Looper") // get the Property TestStep

def packageType = propTestStep.getPropertyValue("packageType").toString() // Get value from the Property TestStep

endLoop = propTestStep.getPropertyValue("StopLoop").toString()
//Passing arg to another script
arg1 = packageType
//printURL(serviceType) // Print response URLs.
run(new File("C:/Users/daswath/Copy/Eclipse Workspace/SoapUIScripts/scripts/URLCollectorScript.groovy"), arg1)
 
if (endLoop.toString() == "T" || endLoop.toString()=="True" || endLoop.toString()=="true")
{
 	log.info ("Exit Groovy Looper")
 	assert true
}
else
{
 testRunner.gotoStepByName("Groovy Script - DataSource") // setStartStep
}
