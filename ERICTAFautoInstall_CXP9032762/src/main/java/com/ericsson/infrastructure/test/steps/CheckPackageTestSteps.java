/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
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
import com.ericsson.infrastructure.test.operators.CheckPackageOperator;
import com.google.inject.Inject;

public class CheckPackageTestSteps extends TorTestCaseHelper {

    private final static Logger logger = LoggerFactory.getLogger(CheckPackageTestSteps.class);
    
    @Inject
    private CheckPackageOperator operator;

    @TestStep(id = "checkIPS")
    public void checkIPS() {
        setTestStep("checkIPS");
        String clients[] = DataHandler.getConfiguration().getStringArray("installedClient");
        for (String client: clients) {
            logger.info("Checking client: " + client);
            assertTrue("Failed checking IVR packages on client: " + client, operator.checkIPS(client));
        }
    }
    
    @TestStep(id = "checkSVR4")
    public void checkSVR4() {
        setTestStep("checkSVR4");
        String clients[] = DataHandler.getConfiguration().getStringArray("installedClient");
        for (String client: clients) {
            logger.info("Checking client: " + client);
            assertTrue("Failed checking SVR4 packages on client: " + client, operator.checkSVR4(client));
        }
    }

}
