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

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.infrastructure.test.operators.ManageDhcpClientsAddOperator;
import com.ericsson.infrastructure.test.operators.ManageDhcpClientsRemoveOperator;
import com.google.inject.Inject;

public class ManageDhcpClientsRemoveTestSteps extends TorTestCaseHelper {

    final static Logger logger = LoggerFactory.getLogger(ManageDhcpClientsRemoveTestSteps.class);

    @Inject
    private ManageDhcpClientsRemoveOperator removeClientOperator;

    private final File folder = new File("../ERICTAFautoInstall_CXP9032762/src/main/resources/paramData");

    private final File[] listOfFiles = folder.listFiles();

    @TestStep(id = "removeClientByChoosingTEST")
    public void removeDhcpClientByChoosingTEST() {
        ArrayList<String> fileContent = new ArrayList<String>();
        int failureCount = 0;
        String paramFile = "";
        int i = 0;
        while (true) {
            paramFile = "paramFile" + Integer.toString(i);
            File f = new File(paramFile);
            if (f.exists()) {
                logger.info("Param file " + paramFile + " exists");
                fileContent = removeClientOperator.readHostnamesFromFile(f, fileContent);
            } else {
                break;
            }
            i++;
        }
        for (String hostname : fileContent) {
            String[] tempHostname = hostname.split("@");
            String tempClientList = removeClientOperator.getListofClientsIntoString();
            int hostnameChoice = removeClientOperator.getHostnameChoice(tempClientList, tempHostname[1]);
            logger.info("Hostname choice to be removed is" + hostnameChoice);
            boolean testResult = false;

            if (hostnameChoice != 0) {
                testResult = removeClientOperator.removeClientByChoosingTEST(Integer.toString(hostnameChoice));
            } else {
                logger.info("Hostname " + tempHostname[1] + " not found");
            }

            if (!testResult) {

                failureCount += 1;
            } else {
                testResult = removeClientOperator.verifyClientDirectoryDeleted(tempHostname[1]);
                if (!testResult) {

                    failureCount += 1;
                }
            }

        }
        if (failureCount == 0) {
            assertTrue(true);

        } else {
            assertTrue(false);

        }
    }

    @TestStep(id = "removeClientByHostnameTEST")
    public void removeDhcpClientByHostnameTEST() {

        int failureCount = 0;
        boolean testResult = false;
        ArrayList<String> fileContent = new ArrayList<String>();
        String paramFile = "";
        int i = 0;
        while (true) {
            paramFile = "paramFile" + Integer.toString(i);
            File f = new File(paramFile);
            if (f.exists()) {
                logger.info("Param file " + paramFile + " exists");
                fileContent = removeClientOperator.readHostnamesFromFile(f, fileContent);
            } else {
                break;
            }
            i++;
        }
        for (String hostname : fileContent) {

            String[] tempHostname = hostname.split("@");
            testResult = removeClientOperator.removeClientHostnameTEST(tempHostname[1]);

            if (!testResult) {

                failureCount += 1;
            } else {
                testResult = removeClientOperator.verifyClientDirectoryDeleted(tempHostname[1]);
                if (!testResult) {

                    failureCount += 1;
                }
            }

        }
        if (failureCount != 0) {
            assertTrue(false);
        } else {
            assertTrue(true);
        }
    }
}
