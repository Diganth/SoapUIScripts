/*</pre>
@Author : Diganth Aswath
@Description : Data Source to create a loop and execute a request several times
@GroovyTestStepName : "DataSourceScript_counter"
*/
 
import com.eviware.soapui.support.XmlHolder

def myTestCase = context.testCase
def counter,next,previous,size
propTestStep = myTestCase.getTestStepByName("Property - Looper") // get the Property TestStep
counter = propTestStep.getPropertyValue("Count").toString()
counter= counter.toInteger()
size = propTestStep.getPropertyValue("Total").toString()
size= size.toInteger()
//log.info "Size of dataloop : ${size}"
next = (counter > size-2? 0: counter+1)
propTestStep.setPropertyValue("Count", next.toString())
next++
log.info "Counter value : ${counter}"
propTestStep.setPropertyValue("Next", next.toString())
log.info "Next value : ${next}"
if (counter == size-1)
{
	propTestStep.setPropertyValue("StopLoop", "T")
	log.info "Setting the stoploop property now..."
}
else if (counter==0)
{
	def runner = new com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner(testRunner.testCase, null)
	log.info "Starting the dataloop now..."
	propTestStep.setPropertyValue("StopLoop", "F")
}
else
{
	propTestStep.setPropertyValue("StopLoop", "F")
}