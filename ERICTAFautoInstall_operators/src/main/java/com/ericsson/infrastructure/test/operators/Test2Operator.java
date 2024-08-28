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
package com.ericsson.infrastructure.test.operators;



import java.util.List;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.infrastructure.test.operators.Auto_Install_CommonOperator;


@SuppressWarnings("deprecation")
public class Test2Operator extends Auto_Install_CommonOperator {
	
		Logger logger = LoggerFactory.getLogger(CLIOperator.class);	
 
	     public boolean copyExecute2(String file, String args,List<Host> sut) {
	   
	     logger.info("Ollie Running Testcase Number 22222222  ");
	     boolean cleanup = true ;
	     //List<Host> hosts = DataHandler.getAllHostsByType(HostType.RC);
	     
	     
	     
	     // Copy file from Repo 
	     //Host mws = getMsHost();
	     
	     String localFile=file;
	     String argss=args;
	     List<Host> servers=sut;
	    
	     for (Host server : servers) {
	    	 logger.info("Test with: "  +server);	     
	       
	    	 User user = server.getUsers(UserType.ADMIN).get(0);
	    	 //String localFile = "somefile.bsh";
	    	 String remoteFilePath = "/var/tmp/";
	    	 sendFileRemotely(server,user,localFile,remoteFilePath);
	    	 logger.info("Ollie copy from repo  " + getLastOutput());
	     	     
	    	 // Execute File on SUT
	    	 String cmd=remoteFilePath+localFile;
	    	 String cmdwithargs=remoteFilePath+localFile+" "+argss;
	    	 int runExit =runSingleBlockingCommandOnHost(server,user, cmdwithargs, cleanup);
	    	 logger.info("runExit has a value of " +runExit);
	    	 if (runExit == 0 ) {
	    		 logger.info(cmd +" executed sucessfully on SUT " +server);
	    		// Delete file copied from repo after sucessful run 
	             runSingleBlockingCommandOnHost(server,user,"rm -rf "+cmd,cleanup);
	             return true;
	    	 } else if (runExit != 0 ) {
	    		 logger.error(cmd +" executed with non zero return code on SUT " +server);
	    		 return false;
         }    	 
	     
	     }
	     return cleanup;
	     }
	     

	     public boolean adminPrep() {
	    	 System.out.println(" adminPrep Run Rabbit  Run    " + getLastOutput());
	    	 return true;
		} 
	     
	     public  boolean callCopyExecute( ) {
	    	// identify box we want to copy file to 
	    	boolean cleanup = true ;
	    	List<Host> sut = DataHandler.getAllHostsByType(HostType.RC);
	    	//test if box exists 
	    	if (sut.isEmpty()) {logger.error("No Servers  defined in host.properties so test has nothing to do");	 		
	    		return cleanup;
	    	 }
	    	logger.info("Ollie call  copyExecute2 ");
			copyExecute("somefile.bsh","" ,sut);
	    	 return true;
		}  
	
}
	   