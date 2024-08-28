/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.infrastructure.test.steps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import junit.framework.TestResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.*;
import com.ericsson.infrastructure.test.operators.*;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.sun.source.tree.AssertTree;


public class ManageDhcpClientsAddTestSteps extends TorTestCaseHelper {

    final static Logger logger = LoggerFactory.getLogger(ManageDhcpClientsAddTestSteps.class);
    boolean exceptionRaised=false;

    @Inject
    private  ManageDhcpClientsAddOperator addClientOperator;
    
    @TestStep(id="TestFile")
    public void copyClientInputFiles(){

    	int i =0;
    	while(true){
    	String sourceLoc = (String) DataHandler.getAttribute("clientParamFile")+Integer.toString(i);
    	 if ( sourceLoc != null && ! sourceLoc.isEmpty()  ){ 
   			logger.info("clientParamFile Location is " +sourceLoc);		
   		}

    	String destLoc = System.getProperty("user.dir")+"/paramFile"+Integer.toString(i);
    	File sourceFile = new File(sourceLoc);
    	File destFile = new File(destLoc);
    	if (sourceFile.exists()){
	    	try {
	    		logger.info("Copying file "+sourceFile.toString()+ " to "+destLoc);
				Files.copy(sourceFile, destFile);
			} catch (IOException e) {
				logger.debug("Something's wrong");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
    	else {
    		break;
    	}
    	i++;
    	}
    }
    
    @TestStep(id="CleanupTestFile")
    public void cleanupOldClientInputFiles(){
    	int i =0;
    	while(true){
    		String fileLoc = System.getProperty("user.dir")+"/paramFile"+Integer.toString(i);
    		logger.info("File Location is "+fileLoc);
    		File file = new File(fileLoc);
    		if (file.exists()){
    			try {
					java.nio.file.Files.delete(file.toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			logger.info("Cleaning the repo, deleting existing file "+file.toString());
    		}
    		else{
    			break;
    		}
    	i++;
    	}
    	
    }
    	@TestStep(id="manageDhcpClientsAddParamFileNoConfirmTest")
        public void addDhcpClientParamFileNoConfirmTest(){
        	setTestStep("manageDhcpClientsAddParamFileNoConfirmTest");
        	List<Host> hosts = DataHandler.getAllHostsByType(HostType.MS);
        	int failureCount = 0;
      		String paramFile = "";
       		int i =0;
           	while(true){
           	paramFile = "paramFile"+Integer.toString(i);
           	File f = new File(paramFile);
           	if(f.exists()){
           		logger.info("Param file "+paramFile+ " exists");
           		for (Host host : hosts) {
           			logger.info("Test with host: "  +host);	     
            		}
        		boolean testResult = addClientOperator.addDhcpClientParamFileNoConfirmTest(paramFile);
        		if (!testResult){
        			failureCount+=1;
        		}
            }
            else{
            		logger.info("Param file "+paramFile+ "does not exist");
            		break;
            }
            i++;
            }
        	
        	if(failureCount != 0){
     			assertTrue(false);
     		}
     		else{
     			assertTrue(true); 
     		}
        }
	

    @TestStep(id="manageDhcpClientsAddParamFileConfirmTest")
    public void addDhcpClientParamFileConfirmTest(){
    	setTestStep("manageDhcpClientsAddParamFileConfirmTest");
    	List<Host> hosts = DataHandler.getAllHostsByType(HostType.MS);
    	int failureCount = 0;
    	String paramFile = "";
		int i =0;
    	while(true){
    	paramFile = "paramFile"+Integer.toString(i);
    	File f = new File(paramFile);
    	if(f.exists()){
    		logger.info("Param file "+paramFile+ " exists");
    		for (Host host : hosts) {
    			logger.info("Test with host: "  +host);	     
    		}
        
    		boolean testResult = addClientOperator.addDhcpClientParamFileConfirmTest(paramFile);
    		if (!testResult){
    			failureCount+=1;
    		}
    	}
    	else{
    		break;
    	}
    	i++;
    	}
    	if(failureCount != 0){
 			assertTrue(false);
 		}
 		else{
 			assertTrue(true); 
 		}
    }
    
    @TestStep(id="addDhcpClientTest")
    public void addDhcpClientTest(){
    	setTestStep("addDhcpClientTest");
    	List<Host> hosts = DataHandler.getAllHostsByType(HostType.MS);
    	int failureCount = 0;
    	
    	String paramFile = "";
		int i =0;
    	while(true){
    	paramFile = "paramFile"+Integer.toString(i);
    	File f = new File(paramFile);
    	if(f.exists()){
    		logger.info("Param file "+paramFile+ " exists");
    		for (Host host : hosts) {
            	logger.info("Test with host: "  +host);	     
            }
    		ArrayList<String> fileContent=addClientOperator.readContentsOfFile(f);
    		Map<String, String> paramContentInMap=addClientOperator.readParamsIntoMap(fileContent);
    		String ipv6 = addClientOperator.ipv6Info(paramContentInMap);
    		String appType = addClientOperator.determineAppType(paramContentInMap);
    		String installArea = addClientOperator.determineInstallArea(paramContentInMap);
    		String installService = addClientOperator.determineInstallService(paramContentInMap);
    		String omMedia = addClientOperator.determineOmMedia(paramContentInMap);
    		String displayType = addClientOperator.determineDisplayType(paramContentInMap);
    		boolean testResult=addClientOperator.addDhcpClientTest(paramContentInMap, ipv6, appType, installArea, installService, omMedia, displayType,false);
    		if (!testResult){
    			failureCount+=1;
    		}
    		
    	}
    	else{
    		break;
    	}
    	i++;
    	}
    	if(failureCount != 0){
 			assertTrue(false);
 		}
 		else{
 			assertTrue(true); 
 		}

    	
    }
    
    @TestStep(id="addDhcpClientHostnameTest")
    public void addDhcpClientHostnameTest(){
    	setTestStep("addDhcpClientTest");
    	int failureCount = 0;
    	int successCount = 0;
    	List<Host> hosts = DataHandler.getAllHostsByType(HostType.MS);
       	String paramFile = "";
    		int i =0;
        	while(true){
        	paramFile = "paramFile"+Integer.toString(i);
        	File f = new File(paramFile);
        	if(f.exists()){
        		logger.info("Param file "+paramFile+ " exists");
        		for (Host host : hosts) {
                	logger.info("Test with host: "  +host);	     
                }
    		ArrayList<String> fileContent=addClientOperator.readContentsOfFile(f);
    		Map<String, String> paramContentInMap=addClientOperator.readParamsIntoMap(fileContent);
    		String ipv6 = addClientOperator.ipv6Info(paramContentInMap);
    		String appType = addClientOperator.determineAppType(paramContentInMap);
    		String installArea = addClientOperator.determineInstallArea(paramContentInMap);
    		String installService = addClientOperator.determineInstallService(paramContentInMap);
    		String omMedia = addClientOperator.determineOmMedia(paramContentInMap);
    		String displayType = addClientOperator.determineDisplayType(paramContentInMap);
    		boolean testResult=addClientOperator.addDhcpClientTest(paramContentInMap, ipv6, appType, installArea, installService, omMedia, displayType,true);
    		if (!testResult){
    			failureCount+=1;
    		}
        	}
        	else{
        		break;
        	}
    		i++;
        	}
    	if(failureCount != 0){
 			assertTrue(false);
 		}
 		else{
 			assertTrue(true); 
 		}

    	
    }
    
    
}
