/*
 * Copyright (C) 2014 Diganth Aswath <diganth2004@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package soapUIScripts

import com.eviware.soapui.impl.wsdl.panels.support.MockTestRunContext
import com.eviware.soapui.model.project.ProjectFactoryRegistry
import com.eviware.soapui.model.support.ModelSupport
import com.eviware.soapui.support.UISupport
import com.eviware.soapui.LogMonitor.*
import com.eviware.soapui.SoapUI
import soapUIScripts.*
import org.apache.commons.codec.binary.Base64

/**
 *
 * @author Diganth Aswath <diganth2004@gmail.com>
 */
class captureImageData {
    
    def util, log, testStepName, serviceType, layout, imageType;
    String[] imageData;
    
    captureImageData(utility util, logger log){
        this.util = util;
        this.log = log;
    }
    
    def file_create (String decodedImageData, imagetype){
       def fileName = util.readProperty("LogFileLocation")+testStepName+"_"+serviceType+imagetype
       log.debug("File Name :: " +fileName)
       //Write output to file created
       writetoFile (fileName, decodedImageData)
    }
    
    def file_create2 (byte[] decodedImageData, imagetype){
       def fileName = util.readProperty("LogFileLocation")+testStepName+"_"+serviceType+imagetype
       log.debug("File Name :: " +fileName)
       //Write output to file created
       writetoFile2 (fileName, decodedImageData)
    }
	
    // Function to write decoded imageData to the file/
    def writetoFile (fileName, String decodedImageData){
        def ImgDatafilestream;
        try{
            ImgDatafilestream = new FileOutputStream(fileName)
            ImgDatafilestream.write(decodedImageData.getBytes(), 0, decodedImageData.length())
        }catch (IOException e){
            log.error ("Unable to write to file :: " + e.printStackTrace())
        }
        ImgDatafilestream.close();
    }
    
    def writetoFile2 (fileName, byte[] decodedImageData){
        def ImgDatafilestream;
        try{
            ImgDatafilestream = new FileOutputStream(fileName)
            ImgDatafilestream.write(decodedImageData)
        }catch (IOException e){
            log.error ("Unable to write to file :: " + e.printStackTrace())
        }
        ImgDatafilestream.close();
    }
    
   // Function to decode base64 encoded data.
   public String base64decoder (String[] imageData, def testStepName, def serviceType, def layout, def imageType){
       //log.debug("Image Data :: " +imageData)
       String errorFlag = null;
       //this.imageData = imageData;
       this.testStepName = testStepName;
       this.serviceType = serviceType;
       this.layout = layout;
       this.imageType = imageType;
       log.debug("In the base64 decoder function -> "+ testStepName)
       for (int i = 0; i < imageData.length; i++){
            if (imageData[i]?.trim()){
                Base64 b64 = new Base64();
                byte[] decoded = b64.decodeBase64(imageData[i].getBytes())
                if ((decoded == null) || (decoded.length == 0 ) ){
                    log.error ("DECODING Return Image data FAILED :: TestStep -> " + testStepName)
                }
                /*byte[] decodedImageData = new byte[decoded.length];
                for ( int i = 0; i < decoded.length; i++ ){
                decodedImageData[i] = (byte)decoded[i];
                }*/
                //log.debug("Decoded Data :: " +new String(decoded, "Cp1252"))
                if(imageType.contains("Pdf")) {
                    file_create2(decoded, "_" +i+ ".pdf")
                    log.info("Captured ReturnImageData label for ->" + testStepName);
                    errorFlag = "No Error";
                }
                else if (imageType.contains("Gif")){
                    file_create2(decoded, "_" +i+ ".gif")
                    log.info("Captured ReturnImageData label for ->" + testStepName);
                    errorFlag = "No Error";
                }
                else if (imageType.contains("Png")){
                    file_create2(decoded, "_" +i+ ".png")
                    log.info("Captured ReturnImageData label for ->" + testStepName);
                    errorFlag = "No Error";
                }
                else if (imageType.contains("Jpg")){
                    file_create2(decoded, "_" +i+ ".jpg")
                    log.info("Captured ReturnImageData label for ->" + testStepName);
                    errorFlag = "No Error";
                }
                else if (imageType.contains("Zpl")){
                    file_create(new String(decoded, "Cp1252"), "_" +i+ ".zpl")
                    log.info("Captured ReturnImageData label for ->" + testStepName);
                    errorFlag = "No Error";
                }
                else if (imageType.contains("AZpl")){
                    file_create(new String(decoded, "Cp1252"), "_" +i+ ".azpl")
                    log.info("Captured ReturnImageData label for ->" + testStepName);
                    errorFlag = "No Error";
                }
                else if (imageType.contains("BZpl")){
                    file_create(new String(decoded, "Cp1252"), "_" +i+ ".bzpl")
                    log.info("Captured ReturnImageData label for ->" + testStepName);
                    errorFlag = "No Error";
                }
                else if (imageType.contains("Epl")){
                    file_create(new String(decoded, "Cp1252"), "_" +i+ ".epl")
                    log.info("Captured ReturnImageData label for ->" + testStepName);
                    errorFlag = "No Error";
                }
                else{
                    log.info("Unable to capture ReturnImageData label for -> "+ testStepName +" as it doesnot match supported formats.");
                    errorFlag = "Unable to capture ReturnImageData label for -> "+ testStepName +" as it doesnot match supported formats.";
                }
                
            }
            else {
                log.error("Unable to capture ReturnImageData label for -> "+ testStepName)
                errorFlag = "Unable to capture ReturnImageData label for -> "+ testStepName;
            }
        }
       return errorFlag;
   }
}

