/*
 @Author: Diganth Aswath
 @Description: Script to change EndpointURLs for specific testSteps
 @Script Name: EndpointURLSetup
*/

def myscript = new soapUIScripts.utility(context, "Properties");
def oldEndpoint = myscript.readProperty("oldEndpoint");
def newEndpoint = myscript.readProperty("newEndpoint");

def testSteps = myscript.testStepsList();
testSteps.each{
	if (it.name == "CI_USPM_old"){
		it.properties['Endpoint'].value = oldEndpoint
	}
	else if (it.name =="CI_USPM_new"){
		it.properties['Endpoint'].value = newEndpoint
	}
}
