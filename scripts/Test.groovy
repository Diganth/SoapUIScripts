import com.eviware.soapui.support.types.StringToStringMap 
def myTestCase = context.testCase
def testSteps = context.testCase.getTestStepList()
def groovyUtils = new com.eviware.soapui.support.GroovyUtils( context )
testSteps.each {
	def prop = it.getProperty("Request").getValue()
	String[] URL= prop.findAll('https?://[^\\s<>"]+|www\\.[^\\s<>"]+')
	log.info URL[1]
}