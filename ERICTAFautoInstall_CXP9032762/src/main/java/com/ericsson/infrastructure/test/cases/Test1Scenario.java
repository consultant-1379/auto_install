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
import static com.ericsson.cifwk.taf.scenario.TestScenarios.sequence;
import org.testng.annotations.Test;
import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.api.ScenarioListener;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.infrastructure.test.steps.*;
import com.google.inject.Inject;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;


import com.ericsson.cifwk.taf.annotations.Context;

public class Test1Scenario extends TorTestCaseHelper {

   
	@Inject
	Test1TestSteps steps;
	
    @TestId(id = "test1_autoinstall", title = "Need todo a quick test")
    @Context(context = {Context.CLI})
    @Test
public void createTestFlow() {

      TestStepFlow testFlow;

      
      testFlow = flow("AdminPreChecks")
            
             .addTestStep(annotatedMethod(steps, "verifyAdminPrep"))
             .build();
      
      TestScenario scenario = scenario("test1").addFlow(testFlow).build();

      ScenarioListener listener = new LoggingScenarioListener();
      TestScenarioRunner runner = runner().withListener(listener).build();
      runner.start(scenario);
  }
        
        
}
   