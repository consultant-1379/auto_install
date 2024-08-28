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
import com.ericsson.infrastructure.test.steps.CacheLatestMediaOnMwsTestSteps;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.cifwk.taf.scenario.api.ScenarioListener;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.cifwk.taf.TestContext;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.*;

public class CacheLatestMediaOnMwsScenario extends TorTestCaseHelper {

    @Inject
    CacheLatestMediaOnMwsTestSteps steps;

    @TestId(id = "OSS-115013", title = "Cache Latest media available in shipment Sol11")
    @Test
    public void cacheLatestMediaFromNexus() {
        TestStepFlow cacheLatestMedia;
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
        cacheLatestMedia = flow("Checking cache media on mws")
                .addTestStep(annotatedMethod(steps, "CheckForAlternativeEricRepo"))
                .addTestStep(annotatedMethod(steps, "checkCachedMediaOnMws"))
                .addTestStep(annotatedMethod(steps, "downloadLatestMedia"))
                .addTestStep(annotatedMethod(steps, "mountMedia"))
                .addTestStep(annotatedMethod(steps, "installERICautoinstall"))
                .addTestStep(annotatedMethod(steps, "checkCachePath"))
                .addTestStep(annotatedMethod(steps, "cacheMediaOnMws"))
                .addTestStep(annotatedMethod(steps, "umountMediaOnMws"))
                .addTestStep(annotatedMethod(steps, "modifyOMSoftwareList"))
                .addTestStep(annotatedMethod(steps, "installAdditionalSoftware"))
                .addTestStep(annotatedMethod(steps, "UpdateERICrepoOnOM"))
                .addTestStep(annotatedMethod(steps, "overlaySRU"))
                .addTestStep(annotatedMethod(steps, "setPublishers"))
                .withDataSources(dataSource("Media")).build();

        TestScenario scenario = scenario("cacheLatestMedia").addFlow(sequence(cacheLatestMedia)).build();
        ScenarioListener listener = new LoggingScenarioListener();
        TestScenarioRunner runner = runner().withListener(listener).build();
        runner.start(scenario);

    }
}
