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
import com.ericsson.infrastructure.test.operators.ManageInstallServiceOperator;
import com.ericsson.infrastructure.test.operators.Test1Operator;
import com.google.inject.Inject;

public class Test1TestSteps extends TorTestCaseHelper {

    final static Logger logger = LoggerFactory.getLogger(Test1TestSteps.class);
    boolean exceptionRaised=false;
   
    @Inject
    //private  Test1Operator upgOperator;
    private ManageInstallServiceOperator upgOperator;
    
    /**
     * Verify there is no dmr process still active on admin after initial install   
     * Verify there is no  /export/scripts mount left hanging around from master.sh  
     * @return 
     */
    
    @TestStep(id="verifyAdminPrep")
    public boolean testAdminsFor() throws InterruptedException
    {
        setTestStep("verifyAdminPrep");
        // get admins defined in host.properties      
        List<Host> hosts = DataHandler.getAllHostsByType(HostType.MS);                     
        for (Host host : hosts) {
        	logger.info("Test with: "  +host);	     
        }

		// check each admin in turn. 	
		String servicelist = upgOperator.compareStrings();
		//logger.info("service list is :" +servicelist);
		if (servicelist != null) {
            return true;
        } 
		else {
            return false;
        }
    }			                 
    
      
}
  
    

