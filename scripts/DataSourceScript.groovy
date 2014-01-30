/*
@Author : Pradeep Bishnoi
@Modified : Diganth Aswath
@Description : Data Source to read .txt file and pass the value to corresponding property.
@GroovyTestStepName : "Groovy Script - DataSource"
*/
 
import com.eviware.soapui.support.XmlHolder


def DataSource(){
	File tickerEnumFile = new File("C:/Users/daswath/Copy/QA_Documents/SoapUI Projects/Input/PackageType.txt")
	// 	make sure input.txt file already exists and contains different set of values sepearted by new line (CR).
	List lines = tickerEnumFile.readLines()
	size = lines.size.toInteger()
	propTestStep = myTestCase.getTestStepByName("Property - Looper") // get the Property TestStep
	propTestStep.setPropertyValue("Total", size.toString())
	counter = propTestStep.getPropertyValue("Count").toString()
	counter= counter.toInteger()
	next = (counter > size-2? 0: counter+1)
	tempValue = lines[counter]
	propTestStep.setPropertyValue("packageType", tempValue)
	propTestStep.setPropertyValue("Count", next.toString())
	next++
	log.info "Reading line : ${(counter+1)} / $lines.size"
	propTestStep.setPropertyValue("Next", next.toString())
	log.info "Value '$tempValue' -- updated in $propTestStep.name"
	if (counter == size-1)
	{
		propTestStep.setPropertyValue("StopLoop", "T")
		log.info "Setting the stoploop property now..."
	}
	else if (counter==0)
	{
		def runner = new com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner(testRunner.testCase, null)
		propTestStep.setPropertyValue("StopLoop", "F")
	}
	else
	{
		propTestStep.setPropertyValue("StopLoop", "F")
	}
}

myTestCase = context.testCase
String counter,next,previous,size
run(new File("C:/Users/daswath/Copy/Eclipse Workspace/SoapUIScripts/scripts/Logger.groovy")
