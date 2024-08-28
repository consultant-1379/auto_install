/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *yright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.infrastructure.test.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.infrastructure.test.operators.ManageInstallServiceOperator;
import com.google.inject.Inject;

public class ManageInstallServiceListTestSteps extends TorTestCaseHelper {

    private final static Logger logger = LoggerFactory.getLogger(ManageInstallServiceListTestSteps.class);

    @Inject
    private ManageInstallServiceOperator upgOperator;

    /**
     * Verify manage_install_service.bsh command line options.
     * will add exact options covered later.
     */

    @TestStep(id = "checkForInstallService")
    public void checkForInstallService() throws InterruptedException {
        setTestStep("checkForInstallService");

        // Check that services are there before we proceed .
        boolean exit1 = upgOperator.checkForInstallService();
        assertTrue(exit1);

    }

    /**
     * Verify manage_install_service.bsh command line options.
     * will add exact options covered later.
     */

    @TestStep(id = "checkForNamedService")
    public void testManageInstallService() throws InterruptedException {
        setTestStep("checkForNamedService");

        // Check have we passed in a service Name Input string
        String serviceName = upgOperator.checkForServiceInput();

        if (serviceName != null && !serviceName.isEmpty()) {
            logger.info("Using input Parameters " + serviceName);
        } else {
            logger.warn("No  input Parameters Supplied to test case using Hardcoded");
            serviceName = "19089-CXP9019993-A";
        }

        DataHandler.setAttribute("serviceName", serviceName);
        // Check if named service is in the list .
        boolean exit1 = upgOperator.checkForNamedService(serviceName);
        assertTrue(exit1);

    }

}
