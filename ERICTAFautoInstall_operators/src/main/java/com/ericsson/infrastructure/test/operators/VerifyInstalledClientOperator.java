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
package com.ericsson.infrastructure.test.operators;

import java.util.List;

import com.ericsson.cifwk.taf.data.*;
import com.ericsson.cifwk.taf.handlers.netsim.Cmd;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;

public class VerifyInstalledClientOperator extends Auto_Install_CommonOperator {

    /**
     * Returns list of client types required
     * @return String[]
     */
    public String[] getInstalledClient() {
        String installedClients[] = DataHandler.getConfiguration().getStringArray("installedClient");
        return (installedClients);

    }

    /**
     * @return
     */
    public boolean checkSSH(String installedClient) {
        
        int exitCode = -1;
        int failureCount = 0;
        boolean cleanup = true;
        Auto_Install_CommonOperator commonOperator = new Auto_Install_CommonOperator();
        String command = "ls";
        List<Host> sut = DataHandler.getAllHostsByType(HostType.valueOf(installedClient)); // to check the host type

        if (sut.isEmpty()) {
            System.out.println("No Servers  defined in host.properties so test has nothing to do: " + installedClient);
            return cleanup;
        }
        for (Host server : sut) {
            User user = server.getUsers(UserType.ADMIN).get(0);
            exitCode = commonOperator.runSingleBlockingCommandOnHost(server, user, command, true);
            if (exitCode != 0) {
                failureCount += 1;
            }
        }
        if (failureCount != 0) {
            logger.error("Unable to ssh to client");
            return false;
        } else {
            logger.info("Able to successfully ssh to client");
            return true;

        }
    }

    /**
     * @return
     */
    public boolean checkSolarisVersion(String installedClient) {
        // TODO Auto-generated method stub
        int failureCount = 0;
        boolean cleanup = true;
        Auto_Install_CommonOperator commonOperator = new Auto_Install_CommonOperator();
        String command = "uname -r";
        String command1 = "uname -v";
        List<Host> sut = DataHandler.getAllHostsByType(HostType.valueOf(installedClient)); // to check the host type
        if (sut.isEmpty()) {
            System.out.println("No Servers  defined in host.properties so test has nothing to do: " + installedClient);
            return cleanup;
        }
        for (Host server : sut) {
            User user = server.getUsers(UserType.ADMIN).get(0);
            final CLICommandHelper cmdHelper = new CLICommandHelper(server);
            cmdHelper.execute(command);
            String release = cmdHelper.getStdOut();
            logger.info("The release is " + release);
            cmdHelper.execute(command1);
            String version = cmdHelper.getStdOut();
            logger.info("The version is " + version);
            if (!release.contains("5.11") && !version.contains("11.3")) {
                failureCount += 1;
            }
        }
        if (failureCount != 0) {
            logger.error("Solaris Release and version are not correct");
            return false;
        } else {
            logger.info("Solaris Release and Version is correct");
            return true;
        }
    }

}
