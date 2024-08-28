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

import com.ericsson.cifwk.taf.TafTestContext;
import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.google.inject.Inject;
import com.ericsson.infrastructure.test.steps.*;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.cifwk.taf.scenario.api.ScenarioListener;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.cifwk.taf.TestContext;


public class ManageDhcpClientsScenario extends TorTestCaseHelper{
	
		@Inject
	    ManageDhcpClientsAddTestSteps addClient;
		@Inject
		ManageDhcpClientsListTestSteps listClient;
		@Inject
		ManageDhcpClientsRemoveTestSteps removeClient;
	   
	    @TestId(id = "infra_tst_p_autoinstall_manage_dhcp_client.bsh", title = "Verify manage_dhcp_client.bsh command options")
	    @Test
	    public void dhcpCientToMws() {
	    	TestStepFlow dhcpCientToMws;
	    	dhcpCientToMws = flow("DHCP Client on MWS") 
	    	.addTestStep(annotatedMethod(addClient, "CleanupTestFile")) 
	    	.addTestStep(annotatedMethod(addClient, "TestFile"))
	    	.addTestStep(annotatedMethod(addClient, "manageDhcpClientsAddParamFileNoConfirmTest"))
	    	.addTestStep(annotatedMethod(listClient, "listAllClients"))
	    	.addTestStep(annotatedMethod(listClient, "listIndividualClient"))
	    	.addTestStep(annotatedMethod(removeClient, "removeClientByChoosingTEST"))
	    	.addTestStep(annotatedMethod(addClient, "manageDhcpClientsAddParamFileConfirmTest"))
	    	.addTestStep(annotatedMethod(listClient, "listAllClients"))
	    	.addTestStep(annotatedMethod(listClient, "listIndividualClient"))
	    	.addTestStep(annotatedMethod(removeClient, "removeClientByChoosingTEST"))
	    	.addTestStep(annotatedMethod(addClient, "addDhcpClientTest"))
	    	.addTestStep(annotatedMethod(listClient, "listAllClients"))
	    	.addTestStep(annotatedMethod(listClient, "listIndividualClient"))
	    	.addTestStep(annotatedMethod(removeClient, "removeClientByHostnameTEST"))
	    	.addTestStep(annotatedMethod(addClient, "addDhcpClientHostnameTest"))
	    	.addTestStep(annotatedMethod(listClient, "listAllClients"))
	    	.addTestStep(annotatedMethod(listClient, "listIndividualClient"))
	    	.addTestStep(annotatedMethod(removeClient, "removeClientByHostnameTEST"))
	   		.build();
	    	
	    	TestScenario scenario = scenario("dhcpCientToMws").addFlow(sequence(dhcpCientToMws)).build();
	        ScenarioListener listener = new LoggingScenarioListener();
	        TestScenarioRunner runner = runner().withListener(listener).build();
	        runner.start(scenario);
}
}