package com.ericsson.infrastructure.test.steps;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.infrastructure.test.operators.*;
import com.google.inject.Inject;

public class InstallWgetTestSteps extends TorTestCaseHelper {

    final static Logger logger = LoggerFactory.getLogger(InstallWgetTestSteps.class);
    boolean exceptionRaised=false;
   
    @Inject
    private  InstallWgetOperator wgetOperator;

    
    
    /**
     * Install wget on server
     *   
     */
    
    @TestStep(id="copyUntarWget")
    public void testManageInstallService() throws InterruptedException
    {
        setTestStep("copyUntarWget");
        // get admins defined in host.properties      
        List<Host> hosts = DataHandler.getAllHostsByType(HostType.MS);                     
        for (Host host : hosts) {
        	logger.info("Test with host: "  +host);	     
        }		 

		// TC Add install service. 	
		boolean exitCode = wgetOperator.copyUntarWget();
		assertTrue(exitCode);
				              
    }

    
    
    
      
}
  
    

