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
package com.ericsson.infrastructure.test.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.data.*;

public class CreateSolarisRepoOperator extends Auto_Install_CommonOperator {

    /** Operator for all PCJ **/
    private Logger logger = LoggerFactory.getLogger(CreateSolarisRepoOperator.class);

    private static final String PACKAGE_DIR = "/var/share/pkg/repositories/solaris";

    private static final String REPO_SRC = "/var/share/solaris_repo_source";

    private static final String INSTALL_REPO_CMD = REPO_SRC + "/install-repo.ksh";

    private static final String ADMIN_TAR = "sol-11_3-repo_admin.tar";

    public boolean unpackAdminTar() {

        // Unpack admin tar
        String command = "cd " + REPO_SRC + "; tar xvf " + ADMIN_TAR;
        int exitCode = runSingleBlockingCommandOnMwsAsRoot(command);
        if (exitCode != 0) {
            logger.error("Failed created the package dir on server ");
            logger.error(getLastOutput());
            return false;
        }
        return true;

    }

    public boolean shareRepo() {
        // Create path
        String command = "mkdir -p " + PACKAGE_DIR;
        int exitCode = runSingleBlockingCommandOnMwsAsRoot(command);
        if (exitCode != 0) {
            logger.error("Failed created the package dir on server ");
            return false;
        }

        command = "/usr/sbin/zfs set share.nfs=on rpool/VARSHARE/pkg/repositories";
        exitCode = runSingleBlockingCommandOnMwsAsRoot(command);
        if (exitCode != 0) {
            logger.error("Failed sharing pkg repo");
            return false;
        }

        command = "/usr/sbin/zfs set share.nfs.ro=* rpool/VARSHARE/pkg/repositories";
        exitCode = runSingleBlockingCommandOnMwsAsRoot(command);
        if (exitCode != 0) {
            logger.error("Failed sharing readonly pkg repo");
            return false;
        }
        return true;
    }

    public boolean installRepo() {

        // Run the install repo
        // String omMedia = (String) DataHandler.getAttribute("om" + CacheLatestMediaOperator.ISO_CACHE_SUFFIX);
        // command = omMedia + INSTALL_REPO_CMD + "-s " + TEMP_REPO_SRC + " -d " + PACKAGE_DIR + " -v";
        String filename = "installRepo.out." + System.currentTimeMillis();
        String command = INSTALL_REPO_CMD + " -s " + REPO_SRC + " -d " + PACKAGE_DIR + " -v > /tmp/" + filename + "  2>&1";
        Host host = getMsHost();
        User user = host.getUsers(UserType.ADMIN).get(0);
        int exitCode = runSingleCommandOnHost(host, user, command);
        if (exitCode != 0) {
            logger.error("Failed created the repo on server");
            logger.error(getLastOutput());
            runSingleBlockingCommandOnMwsAsRoot("uptime");
            logger.error("Uptime: " + getLastOutput());
            return false;
        } else {
            logger.info("Successfully created the repo on server");
            return true;
        }
    }

    public boolean copyRepoFiles(String sourceLoc) {
        // Create repo src
        if (runSingleBlockingCommandOnMwsAsRoot("mkdir -p " + REPO_SRC) != 0) {
            logger.error("Failed to create " + REPO_SRC);
            return false;
        }
        int failureCount = 0;
        String[] filenames = { sourceLoc + "/" + "sol*.zip",
                               sourceLoc + "/" + ADMIN_TAR };
        for (String filename : filenames) {
            int exit = runSingleBlockingCommandOnMwsAsRoot("cp " + filename + " " + REPO_SRC);
            try {
                // Have a pause else next command will fail
                Thread.sleep(60000);
            } catch (InterruptedException e) {
            }
            if (exit != 0) {
                logger.error("Failed to copy " + filename);
                failureCount++;
            }
        }
        return (failureCount == 0);
    }

    public boolean verifyRepo() {
        // Verify path exists
        String command = "ls " + PACKAGE_DIR + "/publisher/solaris";
        int exitCode = runSingleBlockingCommandOnMwsAsRoot(command);
        if (exitCode != 0) {
            logger.error("Failed listing publisher dir on server ");
            return false;
        } else {
            logger.info("Successfully listed publisher dir on server ");
            return true;
        }
    }

    public boolean cleanupTempFiles() {
        String command = "rm -rf " + REPO_SRC;
        int exitCode = runSingleBlockingCommandOnMwsAsRoot(command);
        if (exitCode != 0) {
            logger.error("Failed deleting temp repo files");
            return false;
        } else {
            logger.info("Successfully deleting temp repo files");
            return true;
        }

    }

    /**
     * Checks Solaris publisher is not set
     * @return
     */
    public boolean setPublisher() {
        String command = "pkg publisher | grep solaris";
        int exitCode = runSingleBlockingCommandOnMwsAsRoot(command);
        if (exitCode == 0) {
            logger.error("solaris publisher unexpectedly set");
            return false;
        } else {
            logger.info("publisher correctly unset");
            return true;
        }
    }

}
