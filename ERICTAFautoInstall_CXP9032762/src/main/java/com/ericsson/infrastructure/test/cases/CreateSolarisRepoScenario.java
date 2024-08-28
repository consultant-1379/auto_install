/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
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
import com.ericsson.infrastructure.test.steps.CreateSolarisRepoTestSteps;
import com.google.inject.Inject;

public class CreateSolarisRepoScenario extends TorTestCaseHelper {

    @Inject
    CreateSolarisRepoTestSteps steps;

    @TestId(id = "OSS-124390_1", title = "SOL_11: Create Solaris Repo")
    @Test
    public void createSolarisRepo() {
        TestStepFlow createSolarisRepo;
        // Creating flow
        createSolarisRepo = flow("Create Solaris Repo on MWS").addTestStep(annotatedMethod(steps, "copyRepoFiles"))
                .addTestStep(annotatedMethod(steps, "unpackAdminTar")).addTestStep(annotatedMethod(steps, "shareRepo"))
                .addTestStep(annotatedMethod(steps, "installRepo")).addTestStep(annotatedMethod(steps, "verifyRepo"))
                .addTestStep(annotatedMethod(steps, "setPublisher")).addTestStep(annotatedMethod(steps, "cleanupTempFiles")).build();

        TestScenario scenario = scenario("createSolarisRepo").addFlow(createSolarisRepo).build();
        ScenarioListener listener = new LoggingScenarioListener();
        TestScenarioRunner runner = runner().withListener(listener).build();
        runner.start(scenario);

    }
}
