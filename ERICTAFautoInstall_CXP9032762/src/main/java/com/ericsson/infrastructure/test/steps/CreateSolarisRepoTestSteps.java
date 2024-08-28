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
import com.ericsson.infrastructure.test.operators.CreateSolarisRepoOperator;
import com.google.inject.Inject;

public class CreateSolarisRepoTestSteps extends TorTestCaseHelper {

    private final static Logger logger = LoggerFactory.getLogger(CreateSolarisRepoTestSteps.class);
    
    @Inject
    private CreateSolarisRepoOperator operator;

    @TestStep(id = "copyRepoFiles")
    public void copyRepoFiles() {
        setTestStep("copyRepoFiles");
        String sourceLoc = (String) DataHandler.getAttribute("repoSource");
        // Copy repo files from source to MWS
        // Can we assume that source is on location visible to gateway??
        if (sourceLoc == null || sourceLoc.isEmpty()) {
            logger.error("Source location needs to be defined in attribute repoSource");
            fail("No repoSource location defined");
        }
        assertTrue(operator.copyRepoFiles(sourceLoc));
    }
    
    @TestStep(id = "unpackAdminTar")
    public void unpackAdminTar() {
        setTestStep("unpackAdminTar");
        boolean testResult = operator.unpackAdminTar();
        assertTrue(testResult);
    }

    @TestStep(id = "verifyRepo")
    public void verifyRepo() {
        setTestStep("verifyRepo");
        boolean testResult = operator.verifyRepo();
        assertTrue(testResult);
    }
    
    @TestStep(id = "shareRepo")
    public void shareRepo() {
        setTestStep("shareRepo");
        boolean testResult = operator.shareRepo();
        assertTrue(testResult);
    }

    @TestStep(id = "installRepo")
    public void installRepo() {
        setTestStep("installRepo");
        boolean testResult = operator.installRepo();
        assertTrue(testResult);
    }
    
    @TestStep(id = "setPublisher")
    public void setPublisher() {
        setTestStep("setPublisher");
        boolean testResult = operator.setPublisher();
        assertTrue(testResult);
    }
    
    @TestStep(id = "cleanupTempFiles")
    public void cleanupTempFiles() {
        setTestStep("cleanupTempFiles");
        boolean testResult = operator.cleanupTempFiles();
        assertTrue(testResult);
    }

}
