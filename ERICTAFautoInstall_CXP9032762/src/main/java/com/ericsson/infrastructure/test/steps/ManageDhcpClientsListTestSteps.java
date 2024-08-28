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
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.infrastructure.test.operators.ManageDhcpClientsAddOperator;
import com.ericsson.infrastructure.test.operators.ManageDhcpClientsListOperator;
import com.google.inject.Inject;

public class ManageDhcpClientsListTestSteps extends TorTestCaseHelper {

    final static Logger logger = LoggerFactory.getLogger(ManageDhcpClientsListTestSteps.class);
    boolean exceptionRaised=false;
    private static String PARAM_DIR = "/tmp/Saravana";
	private final File folder = new File("../ERICTAFautoInstall_CXP9032762/src/main/resources/test_scripts/env/testenv");
	private final File[] listOfFiles = folder.listFiles();
    @Inject
    private  ManageDhcpClientsListOperator listClientOperator;
    
    @TestStep(id="listAllClients")
    public void listAllClients(){
    	
    	//ArrayList<String> paramFiles = listClientOperator.getListOfParamFiles();
    	String listContent = listClientOperator.getListofClientsIntoString(null);
    	logger.info("Client list is "+listContent);
    	
		ArrayList<String> fileContent = new ArrayList<String>();
		String paramFile = "";
		int i =0;
    	while(true){
    		paramFile = "paramFile"+Integer.toString(i);
    		File f = new File(paramFile);
    		if(f.exists()){
    			logger.info("Param file "+paramFile+ " exists");
	
    			fileContent=listClientOperator.readHostnamesFromFile(f,fileContent);
    			logger.debug("Adding hostname infor from" +f.toString());
    		
    		}
    		else{
    			break;
    		}
    		i++;
    	}
    	
    	assertTrue(listClientOperator.compareListedInfo(listContent, fileContent));
    	
      }
    
    @TestStep(id="listIndividualClient")
    public void listIndividualClient(){
    	
    	Map<String, String> detailsInFIle = new HashMap<String, String>();
    	Map<String, String> clientInfoInMap = new HashMap<String, String>();
    	ArrayList<String> fileContent = new ArrayList<String>();
    	String clientInfo = null;
    	int failureCount =0;
    	String paramFile = "";
		int i =0;
    	while(true){
    		paramFile = "paramFile"+Integer.toString(i);
    		File f = new File(paramFile);
    		if(f.exists()){
    			logger.info("Param file "+paramFile+ " exists");
    			fileContent = listClientOperator.readParamsIntoList(f);
    			detailsInFIle = listClientOperator.readParamsIntoMap(fileContent,null,"@");
    			clientInfo = listClientOperator.getClientInfo(detailsInFIle);
    			clientInfoInMap = listClientOperator.readParamsIntoMap(new ArrayList<String>(),clientInfo, " \\:");
    			boolean validClient = (listClientOperator.compareIndividualHostInfo(detailsInFIle,clientInfoInMap));
    			if (!validClient){
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
    	else {
    		assertTrue(true);
    	}
    	
    }

}
