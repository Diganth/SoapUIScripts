/*</pre>
@Author : Diganth Aswath
@Description : Data Source Looper responsible for looping a specific teststep.
@GroovyTestStepName : "Groovy Script - Print URL to file"
*/
//Function to replace https with http in URL to enable capture of label images.
def modURLString(url){
	url.replaceFirst(/^https/, "http")
}
//Function to create a Text File with TestStep name
def file_txt (testCasename, testStepname, weight){
	def fileName_txt = "C:/Users/daswath/Copy/QA_Documents/SoapUI Projects/Result/"+testCasename+"/"+testStepname+".txt"
}
// Function to create an Img file with TestStep name
def file_img (testCasename, testStepname, weight){
	def fileName_img = "C:/Users/daswath/Copy/QA_Documents/SoapUI Projects/Result/"+testCasename+"/"+testStepname+".pdf"
}
// Function to write responseURL to the file.
def accessFile (fileName_txt, fileName_img,  url){
	def file = new File(fileName_txt)
	file.write(url, "UTF-8") //Writing response into the file created
	modded_url = modURLString(url)
	//log.info modded_url
	def URLimgfile = new FileOutputStream(fileName_img)
	def out = new BufferedOutputStream(URLimgfile)
	out << new URL(modded_url).openStream()
	out.close()
}
// Function that controls the logic of iterating through the testSteps to obtain
// URL from the response.
def printURL (weightLB) {
	def myTestCase = context.testCase
	def testSteps = context.testCase.getTestStepList()
	def File1 = new File('C:/Users/daswath/Copy/QA_Documents/SoapUI Projects/Result/'+myTestCase.name+'/').mkdirs()
	testSteps.each {
	  if (it.name.contains("CreateIndicium")){
		// Reading response content into an object
		def url = context.expand( '${'+it.name+'#Response#declare namespace ns1=\'http://stamps.com/xml/namespace/2014/01/swsim/swsimv34\';//ns1:CreateIndiciumResponse[1]/ns1:URL[1]}' )
		if(url?.trim()) {// Checking if URL string is empty
			// Calling create file function
			def fileName_txt = file_txt (myTestCase.name, it.name, weightLB)
			def fileName_img = file_img(myTestCase.name, it.name, "1")
			// Accesing file created
			accessFile (fileName_txt, fileName_img, url)
		}
		else {
			log.info ("Unable to capture URL for ${it.name}")
		}
	  }
	}
}

printURL("0") // Print response URLs.

 