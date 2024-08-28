package com.ericsson.infrastructure.test.cases;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.*;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.scenario.*;
import com.ericsson.cifwk.taf.scenario.api.ScenarioListener;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.infrastructure.test.steps.IPToolClientVerificationTestSteps;
import com.google.inject.Inject;

public class IPToolClientVerificationScenario extends TorTestCaseHelper {

    @Inject
    IPToolClientVerificationTestSteps steps;

    @TestId(id = "OSS-146601_2", title = "IPTool Verification Test")
    @Test
    public void ipToolClientVerification() {
        TestStepFlow ipToolVerification;
        // Creating flow with above created DataSource as input.
        ipToolVerification = flow("IPTool testing")
                .addTestStep(annotatedMethod(steps, "runTest"))
                .withDataSources(dataSource("IPToolHosts")).build();

        TestScenario scenario = scenario("ipToolClientVerification").addFlow(sequence(ipToolVerification)).build();
        ScenarioListener listener = new LoggingScenarioListener();
        TestScenarioRunner runner = runner().withListener(listener).build();
        runner.start(scenario);

    }
}
