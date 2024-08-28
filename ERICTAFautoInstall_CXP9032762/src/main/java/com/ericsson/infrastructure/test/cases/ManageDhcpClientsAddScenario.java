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


import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestContext;
import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.google.inject.Inject;
import com.ericsson.infrastructure.test.steps.ManageDhcpClientsAddTestSteps;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.cifwk.taf.scenario.api.ScenarioListener;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.cifwk.taf.TestContext;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.*;
public class ManageDhcpClientsAddScenario extends TorTestCaseHelper{

		
	    @Inject
	    ManageDhcpClientsAddTestSteps steps;
	   
	    @TestId(id = "OSS-121087", title = "SOL_11: AI manage_dhcp_clients.bsh [Test]")
	    @Test
	    public void addDhcpCientToMws() {
	    	TestStepFlow addDhcpCientToMws;
	    	// Creating flow
	    	addDhcpCientToMws = flow("Adding DHCP Client on MWS")
	    			.addTestStep(annotatedMethod(steps, "CleanupTestFile")) 
	    			.addTestStep(annotatedMethod(steps, "TestFile")) 
	    			.addTestStep(annotatedMethod(steps, "manageDhcpClientsAddParamFileNoConfirmTest"))
	    			//.addTestStep(annotatedMethod(steps, "manageDhcpClientsAddParamFileConfirmTest"))
	    			//.addTestStep(annotatedMethod(steps, "addDhcpClientTest"))
	    			//.addTestStep(annotatedMethod(steps, "manageDhcpClientsAddParamFileNoConfirm"))
	    			//.addTestStep(annotatedMethod(steps, "manageDhcpClientsAddParamFile"))
	    			//.addTestStep(annotatedMethod(steps, "addDhcpClientHostnameTest"))
	    			.build();
	    	
	    	TestScenario scenario = scenario("addDhcpCientToMws").addFlow(sequence(addDhcpCientToMws)).build();
	        ScenarioListener listener = new LoggingScenarioListener();
	        TestScenarioRunner runner = runner().withListener(listener).build();
	        runner.start(scenario);
	    	
	    }    
	}


