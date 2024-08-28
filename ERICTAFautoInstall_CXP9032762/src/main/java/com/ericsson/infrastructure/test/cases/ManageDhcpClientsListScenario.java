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
package com.ericsson.infrastructure.test.cases;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.*;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.scenario.*;
import com.ericsson.cifwk.taf.scenario.api.ScenarioListener;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.infrastructure.test.steps.ManageDhcpClientsAddTestSteps;
import com.ericsson.infrastructure.test.steps.ManageDhcpClientsListTestSteps;
import com.google.inject.Inject;

public class ManageDhcpClientsListScenario extends TorTestCaseHelper{
	
		@Inject
	    ManageDhcpClientsListTestSteps steps;
	   
	    @TestId(id = "OSS-121087", title = "SOL_11: AI manage_dhcp_clients.bsh [Test]")
	    @Test
	    public void listDhcpClientsOnMws() {
	    	TestStepFlow listDhcpClientOnMws;

	    	// Creating flow 
	    	listDhcpClientOnMws = flow("List DHCP Client on MWS")
	    			.addTestStep(annotatedMethod(steps, "listAllClients"))
	    			.addTestStep(annotatedMethod(steps, "listIndividualClient"))
	    			.build();
	    	
	    	TestScenario scenario = scenario("listDhcpCientOnMws").addFlow(sequence(listDhcpClientOnMws)).build();
	        ScenarioListener listener = new LoggingScenarioListener();
	        TestScenarioRunner runner = runner().withListener(listener).build();
	        runner.start(scenario);
	    	
	    }    

}
