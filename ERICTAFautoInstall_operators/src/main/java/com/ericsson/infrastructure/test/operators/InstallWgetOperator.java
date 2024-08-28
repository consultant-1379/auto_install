/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.infrastructure.test.operators;



import java.util.List;






//import com.ericsson.cifwk.taf.TestCase;



import com.ericsson.cifwk.taf.data.*;
import com.ericsson.infrastructure.test.operators.Auto_Install_CommonOperator;



public class InstallWgetOperator extends Auto_Install_CommonOperator {
	
	 /**
     * Copy and untar wget on the MWS.      
	 *  
     */
 
	 public  boolean copyUntarWget() {
	    	
	    	boolean cleanup = true ;
	    	Auto_Install_CommonOperator commonOperator;
	    	// identify box we want to copy file to by type
	    	List<Host> sut = DataHandler.getAllHostsByType(HostType.MS);
	    	String command = "cd /;tar -xvf wgetcompiled1.16.tar";
	    	int failureCount = 0;
	    	//test if box exists 
	    	if (sut.isEmpty()) {System.out.println("No Servers  defined in host.properties so test has nothing to do");	 		
	    		return cleanup;
	    	 }
	    	logger.info("call  copy & Execute to : " +sut);		
	    	commonOperator = new Auto_Install_CommonOperator();
	    	// Pass in script name , params and server 
	    	for(Host host : sut){
	    		User user=host.getUsers(UserType.ADMIN).get(0);	
	    	boolean a =  commonOperator.sendFileRemotely2(host, user, "wgetcompiled1.16.tar", "/");
	    	int b = commonOperator.runSingleBlockingCommandOnMwsAsRoot(command, true);
	    	logger.info("Untar output "+b);
	    	if (!a){
	    		failureCount+=1;
	    	}
	    	}
	    	if (failureCount !=0){
	    		return false;
	    	}
	    	else{
	    		return true;
	    	}
		}
	 
	
}
	   