package com.ericsson.infrastructure.test.cases;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestContext;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.google.inject.Inject;
import com.ericsson.infrastructure.test.steps.OverlayPackagesTestSteps;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.cifwk.taf.scenario.api.ScenarioListener;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.cifwk.taf.TestContext;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.*;

public class OverlayPackagesScenario extends TorTestCaseHelper {

    @Inject
    OverlayPackagesTestSteps steps;

    @TestId(id = "OSS-147192", title = "Overlay OM and OSSRC packages to Media Area")
    @Test
    public void cacheLatestMediaFromNexus() {
        TestStepFlow OverlayPkg;
        //Creating DataSource 
        TestContext context = TafTestContext.getContext();
        String media = (String) DataHandler.getAttribute("media");
        if (media != null && !media.isEmpty()) {
            String[] mediaList = media.toString().split("\\ ");
            for (int i = 0; i < mediaList.length; i++) {
                context.dataSource("Media").addRecord().setField("media", mediaList[i].toUpperCase());
            }
        }
        // Creating flow with above created DataSource as input.
        OverlayPkg = flow("Overlay OM and OSSRC Packages")
        		// in this step we check if we have any new changes for Ericrepo tar file then it picks that only from the configuration
           
                 .addTestStep(annotatedMethod(steps, "overlayPackages"))
                  .addTestStep(annotatedMethod(steps, "overlayTarFiles"))
                .withDataSources(dataSource("Media")).build();

        TestScenario scenario = scenario("OverlayPkg").addFlow(sequence(OverlayPkg)).build();
        ScenarioListener listener = new LoggingScenarioListener();
        TestScenarioRunner runner = runner().withListener(listener).build();
        runner.start(scenario);

    }
}
