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


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;


//import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.configuration.TafConfiguration;
import com.ericsson.cifwk.taf.configuration.TafConfigurationProvider;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
//import com.ericsson.infrastructure.test.operators.CLIHelperOperator;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.ericsson.cifwk.taf.tools.cli.Shell;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import com.ericsson.infrastructure.test.operators.Auto_Install_CommonOperator;
import com.google.inject.Inject;


public class Test1Operator extends Auto_Install_CommonOperator {
 
	     public boolean adminPrep() {
	    	 
	     Logger logger = LoggerFactory.getLogger(CLIOperator.class);		 
	     logger.info ("Ollie Running Testcase  Number 11111111 ");
	     boolean cleanup = true ;
	     List<Host> hosts = DataHandler.getAllHostsByType(HostType.RC);
	     if (hosts.isEmpty()) {System.out.println("No Admins defined in host.properties so test has nothing to do");
	    	 		
	      		return cleanup;
	      }
	          
	     logger.info("Hosts found are "+ hosts);
	 	     
	     for (Host host : hosts){
	    	 User user = host.getUsers(UserType.ADMIN).get(0);
	    	 runSingleBlockingCommandOnHost(host,user,  "echo \"HOSTNAME is ******* `hostname` *********** \"; ls -lah /var/tmp/ ", false);
	    	 logger.info("ollie last output is   " + getLastOutput()); 
	     }
	     return (cleanup);
	     }
}
	   