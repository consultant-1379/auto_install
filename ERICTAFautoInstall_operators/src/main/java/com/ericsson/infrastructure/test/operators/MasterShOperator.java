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

import com.ericsson.cifwk.taf.data.*;
import com.ericsson.infrastructure.test.operators.Auto_Install_CommonOperator;

public class MasterShOperator extends Auto_Install_CommonOperator {

    /**
     * @return String
     */
    public String checkForMasterShInputs() {
        String masterShInputs = (String) DataHandler.getAttribute("Master_sh_inputs");
        if (masterShInputs != null && !masterShInputs.isEmpty()) {
            logger.info("Master_sh_inputs " + masterShInputs);
        }
        return (masterShInputs);
    }

    /**
     * @param masterShInputs
     * @return boolean
     */
    public boolean autoInstallClient(String masterShInputs) {
        int exitCode = -1;
        int failureCount = 0;
        String masterShScript = "/export/scripts/CLOUD/bin/cdb_master.sh";
        String command = masterShScript + " " + masterShInputs;
        logger.info("Executing command " + command);
        logger.info("Running master.sh on gateway to install the client");
        List<Host> sut = DataHandler.getAllHostsByType(HostType.GATEWAY); // to check the host type
        if (sut.isEmpty()) {
            logger.error("No Servers  defined in host.properties");
            return false;
        }
        for (Host server : sut) {
            User user = server.getUsers(UserType.ADMIN).get(0);
            exitCode = runSingleBlockingCommandOnHost(server, user, command, true);
            if (exitCode != 0) {
                logger.info("Master.sh failed on " + server.getIp());
                logger.info("Output was: ");
                logger.info(getLastOutput());
                failureCount += 1;
            }
        }
        if (failureCount != 0) {
            logger.error("master.sh exited with a failure value, Please check the logs to see the reason for installation failure ---- the client may not be installed properly");
            return false;
        } else {
            logger.info("Client installation through master.sh is successful");
            return true;
        }

    }

}