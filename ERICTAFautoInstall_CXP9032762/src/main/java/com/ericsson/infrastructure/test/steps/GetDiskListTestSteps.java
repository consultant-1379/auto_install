package com.ericsson.infrastructure.test.steps;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.infrastructure.test.operators.GetDiskListOperator;
import com.google.inject.Inject;

public class GetDiskListTestSteps extends TorTestCaseHelper {

    final static Logger logger = LoggerFactory.getLogger(GetDiskListTestSteps.class);
    boolean exceptionRaised=false;
   
    @Inject
    private  GetDiskListOperator upgOperator;
    
    /**
     * Verify there is no dmr process still active on admin after initial install   
     * Verify there is no  /export/scripts mount left hanging around from master.sh  
     */
    
    @TestStep(id="GetDiskList")
    public void testAdminsFor() throws InterruptedException
    {
        setTestStep("GetDiskList");
        // get admins defined in host.properties      
        List<Host> hosts = DataHandler.getAllHostsByType(HostType.RC);                     
        for (Host host : hosts) {
        	logger.info("Test with host: "  +host);	     
        }

		// check each admin in turn. 	
		boolean exit1 = upgOperator.callCopyExecute();
		assertTrue(exit1);
				              
    }

    
    
    
      
}
  
    

