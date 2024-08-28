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

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;

import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.cli.*;

public class ManageDhcpClientsRemoveOperator extends Auto_Install_CommonOperator {

    protected Shell shell;

    private static String AUTO_INSTALL_DIR = "/ericsson/autoinstall/";

    public boolean removeClientByChoosingTEST(String hostname) {

        int exitCode = -1;
        String command = AUTO_INSTALL_DIR + "/bin/manage_dhcp_clients.bsh";
        Host sut = getMsHost();
        String cmdOpt = " -a remove";
        final CLICommandHelper cmdHelper = new CLICommandHelper(sut); // Create instance on sc2 host
        cmdHelper.openShell(); // Open a shell instance
        cmdHelper.runInteractiveScript(command + cmdOpt);
        try {
            for (int i = 0; i < 15; i++) {
                cmdHelper.expect("Press return key to continue");

                logger.debug("Pressing return key with try block");
                logger.debug("for loop count" + i);
                cmdHelper.interactWithShell(" ");
                // cmdHelper.simpleExec("send");

            }
        } catch (TimeoutException e) {
            System.out.println("Cannot find string");
        }

        cmdHelper.interactWithShell(hostname);
        cmdHelper.expect("Are you sure you wish to delete the selected auto install client? (Yes|No)");
        cmdHelper.interactWithShell("Yes");
        exitCode = cmdHelper.getCommandExitValue();
        cmdHelper.disconnect();
        if (exitCode != 0) {

            return false;
        } else {
            return true;
        }
    }

    public String getListofClientsIntoString() {
        // int exitCode = -1;
        String tempList = "/tmp/templist";
        String command = "rm " + tempList + ";" + AUTO_INSTALL_DIR + "/bin/manage_dhcp_clients.bsh -a list >> " + tempList;
        Host sut = getMsHost();
        final CLICommandHelper cmdHelper = new CLICommandHelper(sut);
        cmdHelper.execute(command);
        cmdHelper.execute("cat /tmp/templist");
        String tempClientlist = cmdHelper.getStdOut();
        logger.debug("Temp Client List is " + tempClientlist);
        return tempClientlist;

    }

    /**
     * @param tempClientList
     * @param hostname
     * @return
     */
    public int getHostnameChoice(String tempClientList, String hostname) {
        // TODO Auto-generated method stub
        String[] tmpArray = tempClientList.split("\\[");
        logger.debug("Temporary String array is" + tmpArray);
        logger.info("Size of Temp array is " + tmpArray.length);
        int choice = 0;
        for (int j = 1; j < tmpArray.length; j++) {
            if (tmpArray[j].contains(hostname)) {
                choice = j;
                logger.info("The hostname choice to be removed is " + choice);
                break;
            } else {
                continue;
            }

        }
        return choice;
    }

    public ArrayList<String> readHostnamesFromFile(File path, ArrayList<String> fileContent) {
        // ArrayList<String> fileContentsList = new ArrayList<String>();
        try (InputStream in = new FileInputStream(path); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            while ((line = reader.readLine()) != null) {

                if (line.contains("CLIENT_HOSTNAME")) {
                    fileContent.add(line);
                    logger.info("Adding line from file" + line);
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return fileContent;
    }

    /**
     * @param hostname
     * @return
     */
    public boolean removeClientHostnameTEST(String hostname) {

        int exitCode = -1;
        String command = AUTO_INSTALL_DIR + "/bin/manage_dhcp_clients.bsh";
        Host sut = getMsHost();
        String cmdOpt = " -a remove -c " + hostname;
        final CLICommandHelper cmdHelper = new CLICommandHelper(sut); // Create instance on sc2 host
        cmdHelper.openShell(); // Open a shell instance
        cmdHelper.runInteractiveScript(command + cmdOpt);
        cmdHelper.expect("Are you sure you wish to delete the selected auto install client? (Yes|No)");
        cmdHelper.interactWithShell("Yes");
        exitCode = cmdHelper.getCommandExitValue();
        logger.info("Command Exit value is " + exitCode);
        if (exitCode != 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean verifyClientDirectoryDeleted(String hostname) {
        String command = "ls /JUMP/autoinstall/oss-clients/" + hostname;
        int exitCode = this.runSingleBlockingCommandOnMwsAsRoot(command);
        logger.info("Command Exit value is " + exitCode);
        if (exitCode == 2) {
            // We expect directory to be missing - so expecting return code of 2
            return true;
        } else {
            logger.error("Oss-clients still exists for client " + hostname);
            return false;
        }
    }

}
