/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
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
import com.ericsson.infrastructure.test.operators.VerifyInstalledClientOperator;
import com.google.inject.Inject;

public class VerifyInstalledClientTestSteps extends TorTestCaseHelper {
    final static Logger logger = LoggerFactory.getLogger(VerifyInstalledClientTestSteps.class);

    @Inject
    private VerifyInstalledClientOperator clientOperator;

    /**
     * Check ssh functionality of the client installed.
     */

    @TestStep(id = "checkSSH")
    public void checkSSHToClient() {
        String installedClients[] = clientOperator.getInstalledClient();
        logger.info("Checking SSH to the Client");
        int timeout = 0;
        String strTimeout = (String)DataHandler.getAttribute("verify.timeout");
        if (strTimeout != null) {
            timeout = Integer.parseInt(strTimeout);
        }
        logger.info("Sleeping for " + timeout + "s before running testcases on the client");
        try {
            Thread.sleep(timeout * 1000);   
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        logger.info("Done Sleeping, trying to SSH now");
        for (String installedClient : installedClients) {
            assertTrue(clientOperator.checkSSH(installedClient.toUpperCase()));
        }
    }

    @TestStep(id = "checkSolarisVersion")
    public void checkSolarisVersion() {
        String installedClients[] = clientOperator.getInstalledClient();
        logger.info("Checking solaris version on client");
        for (String installedClient : installedClients) {
            assertTrue(clientOperator.checkSolarisVersion(installedClient.toUpperCase()));
        }
    }

}
