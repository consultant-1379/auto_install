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
import com.ericsson.infrastructure.test.steps.CheckPackageTestSteps;
import com.google.inject.Inject;

public class CheckPackageScenario extends TorTestCaseHelper {

    @Inject
    CheckPackageTestSteps steps;

    @TestId(id = "OSS-141836_1", title = "SOL_11: Check installed packages")
    @Test
    public void checkPackages() {
        TestStepFlow checkPackages;
        // Creating flow
        checkPackages = flow("Check packages").addTestStep(annotatedMethod(steps, "checkIPS"))
                .addTestStep(annotatedMethod(steps, "checkSVR4")).build();

        TestScenario scenario = scenario("checkPackages").addFlow(checkPackages).build();
        ScenarioListener listener = new LoggingScenarioListener();
        TestScenarioRunner runner = runner().withListener(listener).build();
        runner.start(scenario);

    }
}
