/*</pre>
@Author : Pradeep Bishnoi
@Description : Data Source Looper responsible for looping a specific teststep.
@GroovyTestStepName : "Groovy Script - Data Loop"
*/

def myTestCase = context.testCase
 
def runner
propTestStep = myTestCase.getTestStepByName("Property - Looper") // get the Property TestStep
endLoop = propTestStep.getPropertyValue("StopLoop").toString()
 
if (endLoop.toString() == "T" || endLoop.toString()=="True" || endLoop.toString()=="true")
{
 log.info ("Exit Groovy Data Source Looper")
 assert true
}
else
{
 testRunner.gotoStepByName("Groovy Script - DataSource") //setStartStep
}