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
import com.ericsson.infrastructure.test.operators.ManageInstallServiceOperator;
import com.google.inject.Inject;

public class ManageInstallServiceRemoveByNameTestSteps extends TorTestCaseHelper {

    private final static Logger logger = LoggerFactory.getLogger(ManageInstallServiceRemoveByNameTestSteps.class);

    @Inject
    private ManageInstallServiceOperator upgOperator;

    /**
     * Verify manage_install_service.bsh command line options.
     * will add exact options covered later.
     */

    @TestStep(id = "checkForInstallService")
    public void checkForInstallService() throws InterruptedException {
        setTestStep("checkForInstallService");

        // TC Remove install service.
        boolean exit1 = upgOperator.checkForInstallService();
        assertTrue(exit1);

    }

    /**
     * Verify manage_install_service.bsh command line options.
     * will add exact options covered later.
     */

    @TestStep(id = "RemoveByName")
    public void testManageInstallService() throws InterruptedException {
        setTestStep("RemoveByName");

        // Check have we passed in a service Name Input string
        String serviceName = upgOperator.checkForServiceInput();

        if (serviceName != null && !serviceName.isEmpty()) {
            logger.info("Using input Parameters " + serviceName);
        } else {
            logger.warn("No  input Parameters Supplied to test case using Hardcoded");
            serviceName = "19089-CXP9032968-B";
        }
        // TC Remove install service.
        boolean exit1 = upgOperator.callManageInstallServiceRemoveByName(serviceName);
        assertTrue(exit1);

    }

    /**
     * Verification Step.Verify install service have been removed using list option.
     */

    @TestStep(id = "verifyInstallServiceRemoval")
    public void verifyInstallServiceRemoval() throws InterruptedException {
        setTestStep("verifyInstallServiceRemoval");

        // Check have we passed in a service Name Input string
        String serviceName = upgOperator.checkForServiceInput();

        if (serviceName != null && !serviceName.isEmpty()) {
            logger.info("Using input Parameters " + serviceName);
        } else {
            logger.warn("No  input Parameters Supplied to test case using Hardcoded");
            serviceName = "19089-CXP9032968-B";
        }
        // TC Remove install service.
        boolean exit1 = upgOperator.checkForNamedService(serviceName);
        assertFalse(exit1);

    }

}
