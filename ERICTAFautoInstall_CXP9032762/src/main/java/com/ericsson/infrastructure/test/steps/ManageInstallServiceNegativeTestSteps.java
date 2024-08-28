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

public class ManageInstallServiceNegativeTestSteps extends TorTestCaseHelper {

    private final static Logger logger = LoggerFactory.getLogger(ManageInstallServiceNegativeTestSteps.class);

    @Inject
    private ManageInstallServiceOperator upgOperator;

    /**
     * Verify manage_install_service.bsh returns a non zero and displays usage Sceen for Incorrect commands.
     */

    @TestStep(id = "invalidMediaPath")
    public void invalidMediaPAth() throws InterruptedException {
        setTestStep("invalidMediaPath");

        // Hardcode media path to be negative
        String mountPath = " /var/tmp/HA/Oracle_Solaris_AI_X86666.iso";

        // TC Add install service.
        boolean exit1 = upgOperator.invalidMediaPath(mountPath);
        assertFalse(exit1);

    }

    /**
     * Verify manage_install_service.bsh display usage screen when
     */
    @TestStep(id = "invalidSwitch")
    public void testManageInvalidSwitch() throws InterruptedException {
        setTestStep("invalidSwitch");

        // Check if named service is in the list .
        boolean exit1 = upgOperator.invalidSwitch();
        assertTrue(exit1);

    }

    /**
     * Check that -a listssss fails
     */

    @TestStep(id = "aInvalidOption")
    public void checkForInvalidOption() throws InterruptedException {
        setTestStep("aInvalidOption");

        // Check if named service is in the list .
        boolean exit1 = upgOperator.invalidOption();
        assertTrue(exit1);

    }

    @TestStep(id = "noParams")
    public void checkForNoParms() throws InterruptedException {
        setTestStep("noParams");

        // Check that services are there before we proceed .
        boolean exit1 = upgOperator.noParams();
        assertTrue(exit1);

    }

}
