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

public class ManageInstallServiceAddTestSteps extends TorTestCaseHelper {

    private final static Logger logger = LoggerFactory.getLogger(ManageInstallServiceAddTestSteps.class);

    @Inject
    private ManageInstallServiceOperator upgOperator;

    /**
     * Verify manage_install_service.bsh command line options.
     * will add exact options covered later.
     */

    @TestStep(id = "manageInstallServiceAdd")
    public void testManageInstallService() throws InterruptedException {
        setTestStep("manageInstallServiceAdd");

        // Check have we passed in an Input string
        String mediaPath = upgOperator.checkForMountPathInput();

        if (mediaPath != null && !mediaPath.isEmpty()) {
            logger.info("Using input Parameters " + mediaPath);
        } else {
            logger.warn("No  input Parameters Supplied to test case using Hardcoded");
            mediaPath = "/JUMP/ISO/Consolidated_Solaris_11.3_Media.iso";

        }

        DataHandler.setAttribute("mediaPath", mediaPath);

        // TC Add install service.
        boolean exit1 = upgOperator.callmanageInstallServiceAdd(mediaPath);
        assertTrue(exit1);

    }

}
