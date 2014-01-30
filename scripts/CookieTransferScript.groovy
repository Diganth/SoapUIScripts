/*
 *  Author: Diganth
 *  Description: Copies Header value "Set-Cookie" from one request to another
 */
import com.eviware.soapui.support.types.StringToStringsMap
import com.eviware.soapui.impl.wsdl.teststeps.*

def myEndpoint
//def TestStep1, TestStep2
def myTestCase = testRunner.testCase

propertyValue = myTestCase.getTestStepByName("Endpoint_properties") // Get Property teststep
myEndpoint = propertyValue.getPropertyValue("myEndpoint").toString()
int TestStep1 = Integer.parseInt (propertyValue.getPropertyValue("TestStep1Location"))
int TestStep2 = Integer.parseInt (propertyValue.getPropertyValue("TestStep2Location"))

def headerValue = myTestCase.getTestStepAt(TestStep1.value).httpRequest.response.responseHeaders["Set-Cookie"]
if (!headerValue.isEmpty()){
	def headers = new StringToStringsMap()
	headers.put ("Cookie", headerValue)
	myTestCase.getTestStepAt(TestStep2).testRequest.setEndpoint(myEndpoint)
	myTestCase.getTestStepAt(TestStep2).testRequest.setRequestHeaders(headers)
}
else{
	log.info "Set-Cookie does not exist which an indication of request failure."
}

/*
def headers = myTestCase.testSteps["CreateIndiciumV3 US-PMI"].getHttpRequest().getRequestHeaders()
		headers.each{
		key, val -> 
		log.info "Key="+key+" ; value="+val
		}
*/
