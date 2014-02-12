/*</pre>
@Author : Diganth Aswath
@Description : Data Source Looper responsible for looping a registration testStep.
@GroovyTestStepName : "DataLoop"
*/

def myTestCase = context.testCase
 
def runner
propTestStep = myTestCase.getTestStepByName("Property - Looper") // get the Property TestStep
regTestStep = myTestCase.getTestStepByName("AMEX_Registration") // get the Registration TestStep
def rawRequest = regTestStep.getProperty("Request").getValue()
String[] nameSpaceURL = rawRequest.findAll('https?://[^\\s<>"]+|www\\.[^\\s<>"]+')
def userName = context.expand('${AMEX_Registration#Request#declare namespace ns1=\''+nameSpaceURL[1]+'\';//ns1:RegisterAccount[1]/ns1:UserName[1]}')
def userID = context.expand('${AMEX_Registration#Response#declare namespace ns1=\''+nameSpaceURL[1]+'\';//ns1:RegisterAccountResponse[1]/ns1:UserId[1]}')
def result = context.expand('${AMEX_Registration#Response#declare namespace ns1=\''+nameSpaceURL[1]+'\';//ns1:RegisterAccountResponse[1]/ns1:RegistrationStatus[1]}')
if (result == "Fail"){
	log.error ("Error :: Coud not create user : ${userName}")
}
else if (result == "Pending"){
	log.info ("Pending :: User ${userName}")
}
else if (result == "Success"){
	log.info ("Success :: User ${userName} and User ID ${userID}")
}
	
endLoop = propTestStep.getPropertyValue("StopLoop").toString()
 
if (endLoop.toString() == "T" || endLoop.toString()=="True" || endLoop.toString()=="true")
{
 log.info ("Exit Groovy Data Source Looper")
 assert true
}
else
{
 testRunner.gotoStepByName("DataSource") //setStartStep
}