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

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.infrastructure.test.operators.ManageDhcpNetworkAddOperator;
import com.google.common.io.Files;
import com.google.inject.Inject;

public class ManageDhcpNetworkAddTestSteps extends TorTestCaseHelper {

    private final static Logger logger = LoggerFactory.getLogger(ManageDhcpNetworkAddTestSteps.class);

    @Inject
    private ManageDhcpNetworkAddOperator addNetworkOperator;

    @TestStep(id = "copyNetworkInputFiles")
    public void copyNetworkInputFiles() {

        int i = 0;
        while (true) {
            String sourceLoc = (String) DataHandler.getAttribute("networkParamFile") + Integer.toString(i);
            if (sourceLoc != null && !sourceLoc.isEmpty()) {
                logger.info("networkParamFile Location is " + sourceLoc);
            }

            String destLoc = System.getProperty("user.dir") + "/networkParamFile" + Integer.toString(i);
            File sourceFile = new File(sourceLoc);
            File destFile = new File(destLoc);
            if (sourceFile.exists()) {
                try {
                    logger.info("Copying file " + sourceFile.toString() + " to " + destLoc);
                    Files.copy(sourceFile, destFile);
                } catch (IOException e) {
                    logger.error("Failed to copy files", e);
                    fail("Exception copying files");
                }
            } else {
                break;
            }
            i++;
        }
    }

    @TestStep(id = "CleanupNetworkFile")
    public void CleanupNetworkFile() {
        int i = 0;
        while (true) {
            String fileLoc = System.getProperty("user.dir") + "/networkParamFile" + Integer.toString(i);
            logger.info("File Location is " + fileLoc);
            File file = new File(fileLoc);
            if (file.exists()) {
                try {
                    java.nio.file.Files.delete(file.toPath());
                } catch (IOException e) {
                    logger.warn("Failed to tidy up files", e);
                }
                logger.info("Cleaning the repo, deleting existing file " + file.toString());
            } else {
                break;
            }
            i++;
        }

    }

    @TestStep(id = "manageDhcpNetworksAddParamFileNoConfirmTest")
    public void manageDhcpNetworksAddParamFileNoConfirmTest() {
        setTestStep("manageDhcpNetworksAddParamFileNoConfirmTest");
        int failureCount = 0;
        String paramFile = "";
        int i = 0;
        while (true) {
            paramFile = "networkParamFile" + Integer.toString(i);
            File f = new File(paramFile);
            if (f.exists()) {
                logger.info("Param file " + paramFile + " exists");
                boolean testResult = addNetworkOperator.addDhcpNetworkParamFileNoConfirmTest(paramFile);
                if (!testResult) {
                    failureCount++;
                }
            } else {
                logger.info("Param file " + paramFile + "does not exist");
                break;
            }
            i++;
        }

        assertEquals(0, failureCount);
    }

}
