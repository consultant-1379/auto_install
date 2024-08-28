package com.ericsson.infrastructure.test.steps;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import com.ericsson.infrastructure.test.operators.Test1Operator;
import com.ericsson.infrastructure.test.operators.Test2Operator;
import com.google.inject.Inject;

public class Test2TestSteps extends TorTestCaseHelper {

    final static Logger logger = LoggerFactory.getLogger(Test1TestSteps.class);
    boolean exceptionRaised=false;
   
    @Inject
    private  Test2Operator upgOperator;
    
    /**
     * Verify there is no dmr process still active on admin after initial install   
     * Verify there is no  /export/scripts mount left hanging around from master.sh  
     */
    
    @TestStep(id="verifyAdminPrep")
    public void testAdminsFor() throws InterruptedException
    {
        setTestStep("verifyAdminPrep");
        // get admins defined in host.properties      
        List<Host> hosts = DataHandler.getAllHostsByType(HostType.RC);                     
        for (Host host : hosts) {
        	logger.info("Test with: "  +host);	     
        }

		// check each admin in turn. 	
		boolean exit1 = upgOperator.callCopyExecute();
		assertTrue(exit1);
				              
    }
    
}

  
    

