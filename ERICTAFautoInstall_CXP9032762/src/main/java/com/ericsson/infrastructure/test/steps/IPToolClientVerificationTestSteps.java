package com.ericsson.infrastructure.test.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.*;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.infrastructure.test.operators.IPToolClientVerificationOperator;
import com.google.inject.Inject;

public class IPToolClientVerificationTestSteps extends TorTestCaseHelper {

    final static Logger logger = LoggerFactory.getLogger(IPToolClientVerificationTestSteps.class);

    TestContext context = TafTestContext.getContext();
    @Inject
    private IPToolClientVerificationOperator ipOperator;

 
    /**
     * Check if the latest media on Mws
     */
    @TestStep(id = "runTest")
    public void runTest(@Input("hosttype") String hostType,
                                      @Input("ip") String ipv6,
                                      @Input("subnet") String subnet) {

        setTestStep("runTest");
        assertTrue("Failed to run iptool test for " + hostType, 
                    ipOperator.runTest(HostType.valueOf(hostType), ipv6, subnet));
    }

    

}
