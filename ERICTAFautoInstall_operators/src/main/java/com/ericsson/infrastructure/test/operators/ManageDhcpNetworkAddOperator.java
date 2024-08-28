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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.data.*;

public class ManageDhcpNetworkAddOperator extends Auto_Install_CommonOperator {

    /** Operator for all PCJ **/
    private Logger logger = LoggerFactory.getLogger(ManageDhcpNetworkAddOperator.class);

    private static String AUTO_INSTALL_DIR = "/ericsson/autoinstall/";

    public boolean addDhcpNetworkParamFileNoConfirmTest(String paramFile) {
        int failureCount = 0;
        boolean cleanup = true;
        // identify box we want to copy file to by type
        List<Host> sut = DataHandler.getAllHostsByType(HostType.MS);
        // test if box exists
        if (sut.isEmpty()) {
            logger.info("No Servers defined in host.properties so test has nothing to do");
            return cleanup;
        }
        logger.info("Running on : " + sut);
        for (Host server : sut) {
            String localFile = paramFile;
            User user = server.getUsers(UserType.ADMIN).get(0);
            logger.info("Test with: " + server + " and user:  " + user);
            // hardcoded remote path where we copy file to
            String remoteFilePath = "/var/tmp/" + localFile;
            logger.info("Host is " + server + " local file is " + localFile + " remote file is " + remoteFilePath);
            boolean sent = sendFileRemotely2(server, user, localFile, remoteFilePath);
            // boolean sent=getRemoteFile(server, user, remoteFilePath, localFile);
            // Only proceed to execute if copy has been Successful
            if (sent) {
                String command = AUTO_INSTALL_DIR + "/bin/manage_dhcp.bsh -a add -s network -f " + remoteFilePath + " -N";
                int exitCode = runSingleBlockingCommandOnMwsAsRoot(command);
                if (exitCode != 0) {
                    logger.error("Failed adding the network with param file " + paramFile + "on server " + sut);
                    logger.error(getLastOutput());
                    failureCount++;
                } else {
                    logger.info("Successfully added the network with param file " + paramFile + "on server " + sut);            
                }
            }
        }
        return (failureCount == 0);
    }

}
