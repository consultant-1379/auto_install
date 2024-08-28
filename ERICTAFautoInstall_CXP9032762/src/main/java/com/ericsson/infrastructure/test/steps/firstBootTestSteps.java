package com.ericsson.infrastructure.test.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.infrastructure.test.operators.firstBootOperator;
import com.google.inject.Inject;

public class firstBootTestSteps extends TorTestCaseHelper {

    private final static Logger logger = LoggerFactory.getLogger(firstBootTestSteps.class);

    @Inject
    private firstBootOperator upgOperator;

    /**
     * Verify eric_first_boot package.
     */

    @TestStep(id = "ericFirstBoot")
    public void testManageInstallService() throws InterruptedException {
        setTestStep("ericFirstBoot");

        // TC First boot
        boolean exit1 = upgOperator.callCopyExecute();
        assertTrue(exit1);

    }

}
