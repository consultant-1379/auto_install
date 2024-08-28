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
import com.ericsson.infrastructure.test.steps.RemoveMediaOnMwsTestSteps;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.cifwk.taf.scenario.api.ScenarioListener;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.cifwk.taf.TestContext;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.*;


public class RemoveMediaOnMwsScenario extends TorTestCaseHelper implements TestCase {
	
	
    @Inject
    RemoveMediaOnMwsTestSteps steps;
   
    @TestId(id = "OSS-115013", title = "Remove Latest media on MWS")
    @Test
    public void removeLatestMediaFromNexus() {
    	TestStepFlow removeLatestMedia;
    	//Creating DataSource 
    	TestContext context = TafTestContext.getContext();
    	String media=(String)DataHandler.getAttribute("media");
    	if ( media != null && ! media.isEmpty() ){
    		String[] mediaList = media.toString().split("\\ ");
        	for (int i=0; i<mediaList.length; i++) {
        		context.dataSource("Media").addRecord()
        		.setField("media", mediaList[i].toUpperCase());
        	}	
    	}
    	// Creating flow with above created DataSource as input.
    	removeLatestMedia = flow("Listing cache media on mws")
    			.addTestStep(annotatedMethod(steps, "getListedMediaOnMws"))
    			.addTestStep(annotatedMethod(steps, "removeMediaOnMws"))
    			.addTestStep(annotatedMethod(steps, "removeNoMediaOnMws"))
    			.addTestStep(annotatedMethod(steps, "listNoMediaOnMws"))
    			.withDataSources(dataSource("Media")).build();
    	
    	TestScenario scenario = scenario("removeLatestMedia").addFlow(sequence(removeLatestMedia)).build();
        ScenarioListener listener = new LoggingScenarioListener();
        TestScenarioRunner runner = runner().withListener(listener).build();
        runner.start(scenario);
    	
    }    
}
