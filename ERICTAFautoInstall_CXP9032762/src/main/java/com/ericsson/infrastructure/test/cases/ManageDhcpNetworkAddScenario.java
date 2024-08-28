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
import com.ericsson.infrastructure.test.steps.ManageDhcpNetworkAddTestSteps;
import com.google.inject.Inject;

public class ManageDhcpNetworkAddScenario extends TorTestCaseHelper {

    @Inject
    ManageDhcpNetworkAddTestSteps steps;

    @TestId(id = "OSS-132542_1", title = "SOL_11: AI manage_dhcp.bsh -a add -s network [Test]")
    @Test
    public void addDhcpNetworkToMws() {
        TestStepFlow addDhcpNetworkToMws;
        // Creating flow
        addDhcpNetworkToMws = flow("Adding DHCP Network on MWS").addTestStep(annotatedMethod(steps, "CleanupNetworkFile"))
                .addTestStep(annotatedMethod(steps, "copyNetworkInputFiles")).addTestStep(annotatedMethod(steps, "manageDhcpNetworksAddParamFileNoConfirmTest")).build();

        TestScenario scenario = scenario("addDhcpNetworkToMws").addFlow(addDhcpNetworkToMws).build();
        ScenarioListener listener = new LoggingScenarioListener();
        TestScenarioRunner runner = runner().withListener(listener).build();
        runner.start(scenario);

    }
}
