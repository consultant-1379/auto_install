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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.data.*;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;

public class ManageInstallServiceOperator extends Auto_Install_CommonOperator {
    
    private Logger logger = LoggerFactory.getLogger(ManageInstallServiceOperator.class);

    /**
     * Calls manage install service with -a add -N command line options on Server
     * 
     * @return
     */
    public boolean callmanageInstallServiceAdd(String mountPath) {
        logger.info(" In Operator class  ");
        List<Host> sut = DataHandler.getAllHostsByType(HostType.MS);
        // test if MWS exists
        if (sut.isEmpty()) {
            logger.warn("No MWS Server  defined in host.properties so test has nothing to do");
            return true;
        }
        // Command to test
        String baseCmd = "/ericsson/autoinstall/bin/manage_install_service.bsh ";
        String cmdOpt = " -a add -p " + mountPath + " -N";
        // Command line options
        Boolean retVal = false;
        int runningTotal = 0;
        int exitCode = runSingleBlockingCommandOnMwsAsRoot(baseCmd + cmdOpt, false);
        logger.info("exitCode is  " + exitCode);
        if (exitCode == 0) {
            logger.info("running  " + baseCmd + " with option " + baseCmd + " on " + sut);
        } else {
            logger.error(getLastOutput());
            logger.error("Error Detected running  " + baseCmd + " with option " + cmdOpt + " on " + sut);
            runningTotal = runningTotal + 1;
        }

        // Work out return value based on running Total
        if (runningTotal == 0) {
            retVal = true;
        } else {
            retVal = false;
        }
        // Return True or false
        return retVal;
    }

 
    /**
     * Calls manage install service with -a remove -N command line options on Server
     * No user prompts
     * 
     * @return
     */
    public boolean callmanageInstallServiceRemoveN(String serviceName) {
        Boolean retVal = false;
        Host sut = getMsHost();
        String baseCmd = "yes | /ericsson/autoinstall/bin/manage_install_service.bsh ";
        String cmdOpt = " -a remove -s  " + serviceName + " -N";
        int exitCode = runSingleBlockingCommandOnMwsAsRoot(baseCmd + cmdOpt, false);
        logger.info("exitCode is  " + exitCode);
        if (exitCode == 0) {
            logger.info("running  " + baseCmd + " with option " + baseCmd + " on " + sut + " Sucessful");
            retVal = true;
        } else {
            logger.error("Error Detected running  " + baseCmd + " with option " + cmdOpt + " on " + sut);
            retVal = false;
        }
        return retVal;
    }

    /**
     * Calls manage install service with -a remove command line options on Server
     * Prompted removal
     * 
     * @return
     */
    public boolean callmanageInstallServiceRemove3(String serviceName) {
        Host sut = getMsHost();
        String baseCmd = "/ericsson/autoinstall/bin/manage_install_service.bsh ";
        String cmdOpt = " -a remove -s  " + serviceName;
        
        initializeHelper(sut, sut.getFirstAdminUser());
        runInteractiveScriptAndStop(baseCmd + cmdOpt);
        
        // Waits for question and answer yes
        boolean found = interactWithShell(60, "Are you sure you wish to remove the selected Install service?", "Yes");
        if (!found) {
            logger.error("Failed to remove");
            return false;                
        }
        
        String output = waitForInteractiveToEnd(200);
        int exitCode = getCommandExitValue();
        closeShell();

        logger.info("exitCode is  " + exitCode);
        
        if (!output.contains("<- Successful")) {
            logger.error("No successful in running  " + baseCmd + " with option " + cmdOpt + " on " + sut);
            return false;
        }

        if (exitCode == 0) {
            logger.info("running  " + baseCmd + " with option " + cmdOpt + " on " + sut + " Sucessful");
            return true;
        } else {
            logger.error("Error Detected running  " + baseCmd + " with option " + cmdOpt + " on " + sut);
            return false;
        }

    }

    /**
     * Calls manage install service with -a remove serviceName command line options on Server
     * 
     * @return
     */
    public boolean manageInstallServiceRemoveByName(String serviceName) {
        
        Host sut = getMsHost();
        String baseCmd = "/ericsson/autoinstall/bin/manage_install_service.bsh ";
        String cmdOpt = " -a remove -s  " + serviceName;
        logger.info("Running " + baseCmd + cmdOpt);
        
        initializeHelper(sut, sut.getFirstAdminUser());
        runInteractiveScriptAndStop(baseCmd + cmdOpt);
        
        // Waits for question and answer yes
        boolean found = interactWithShell(60, "Are you sure you wish to remove the selected Install service?", "Yes");
        if (!found) {
            logger.error("Failed to remove");
            return false;                
        }
        
        String output = waitForInteractiveToEnd(200);
        int exitCode = getCommandExitValue();
        closeShell();

        logger.info("exitCode is  " + exitCode);
        
        if (!output.contains("<- Successful")) {
            logger.error("No successful in output running  " + baseCmd + " with option " + cmdOpt + " on " + sut);
            logger.error(output);
            return false;
        }

        if (exitCode == 0) {
            logger.info("running  " + baseCmd + " with option " + cmdOpt + " on " + sut + " Sucessful");
            return true;
        } else {
            logger.error("Error Detected running  " + baseCmd + " with option " + cmdOpt + " on " + sut);
            return false;
        }
    }

    /**
     * Calls manage install service with -a list command line options on Server
     * 
     * @return
     */
    public boolean checkForAnyInstallService() {
        String cmdOutput;
        Boolean retVal = false;
        Boolean checkForService = true;
        Host sut = getMsHost();
        String baseCmd = "/ericsson/autoinstall/bin/manage_install_service.bsh ";
        String cmdOpt = " -a list";
        int exitCode = runSingleBlockingCommandOnMwsAsRoot(baseCmd + cmdOpt);
        logger.info("last output is ooo" + getLastOutput());
        cmdOutput = (getLastOutput());
        // Check text output to screen to see to we proceed
        String[] perlines = cmdOutput.split("\\n");
        for (int i = 0; i < perlines.length; i++) {

            if (perlines[i].contains("No Install Services found")) {
                logger.info("No Install Services found for Manage install services to Remove");
                checkForService = false;
                break;
            }

        }

        // if exitCode is 0 and there is a service there to remove we should proceed.
        logger.info("exitCode is  " + exitCode);
        ;
        if (exitCode == 0 && checkForService == true) {
            logger.info("running  " + baseCmd + " with option " + cmdOpt + " on " + sut + " Sucessful");
            retVal = true;
        } else {
            logger.error("Error Detected running  " + baseCmd + " with option " + cmdOpt + " on " + sut);
            retVal = false;
        }

        return retVal;
    }

    public boolean checkForInstallService() {
        String cmdOutput;
        Boolean retVal = false;
        Boolean checkForService = true;
        Host sut = getMsHost();
        String baseCmd = "/ericsson/autoinstall/bin/manage_install_service.bsh ";
        String cmdOpt = " -a list";
        int exitCode = runSingleBlockingCommandOnMwsAsRoot(baseCmd + cmdOpt);
        logger.info("last output is ooo" + getLastOutput());
        cmdOutput = (getLastOutput());
        // Check text output to screen to see to we proceed
        String[] perlines = cmdOutput.split("\\n");
        for (int i = 0; i < perlines.length; i++) {
            if (perlines[i].contains("No Install Services found")) {
                logger.info("No Install Services found for Manage install services to Remove");
                checkForService = false;
                break;
            }

        }

        // if exitCode is 0 and there is a service there to remove we should proceed.
        logger.info("exitCode is  " + exitCode);
        ;
        if (exitCode == 0 && checkForService == true) {
            logger.info("running  " + baseCmd + " with option " + cmdOpt + " on " + sut + " Sucessful");
            retVal = true;
        } else {
            logger.error("Error Detected running  " + baseCmd + " with option " + cmdOpt + " on " + sut);
            retVal = false;
        }

        return retVal;
    }

    public String getServicesList() {
        Host sut = getMsHost();
        String baseCmd = "/ericsson/autoinstall/bin/manage_install_service.bsh ";
        String cmdOpt = " -a list";
        int exitCode = runSingleBlockingCommandOnMwsAsRoot(baseCmd + cmdOpt, false);
        if (exitCode != 0) {

            logger.error("Error Detected running  " + baseCmd + " with option " + cmdOpt + " on " + sut);
            return null;
        }
        logger.info("exit Code printing " + exitCode);
        String[] eachLine = getLastOutput().split("\\n");
        int numline = eachLine.length;
        String list = "";
        for (int i = 0; i < numline; i++) {
            if (eachLine[i].contains("Service Name :")) {
                logger.info("Service Name Found " + eachLine[i]);
                String[] parts = eachLine[i].split(":");
                String part2 = parts[1];
                String service = part2.replaceAll("\\s", "");
                logger.info("service name is " + service);
                list += " " + service;

            }
        }
        logger.info("Complete service list is  " + list.toString().replaceAll("\\[\\]", ""));
        logger.info("Complete service list is  " + list);
        return list;
    }

    /**
     * Checks if a named service is there in the list
     * No user prompts
     * 
     * @return
     */
    public boolean checkForNamedService(String serviceName) {
        Boolean retVal = false;
        String completeList = getServicesList();
        if (completeList.matches(".*" + serviceName + ".*")) {
            logger.info(" Service   " + serviceName + " Found");
            String baseCmd = "/ericsson/autoinstall/bin/manage_install_service.bsh ";
            String cmdOpt = " -a list -s " + serviceName;
            int exitCode = runSingleBlockingCommandOnMwsAsRoot(baseCmd + cmdOpt, false);
            if (exitCode != 0) {
                logger.error("Error Detected running  " + baseCmd + " with option " + cmdOpt);
                retVal = false;
            } else {
                retVal = true;
            }

        } else {
            logger.error(" Service   " + serviceName + "  Not Found");
            retVal = false;
        }

        return retVal;
    }

    /**
     * Checks for a BMR Service , then checks if the -B switch works
     * 
     * @return
     */

    public boolean callManageInstallServiceRemoveByName(String serviceName) {
        List<String> myList = new ArrayList<String>();
        // We expect to have a service  to remove
        boolean serviceExists = checkForNamedService(serviceName);

        boolean retVal = true;
        if (serviceExists == true) {
            myList.add(serviceName);
        } else {
            logger.info("No " + serviceName + " to remove  ");
            retVal = false;
        }

        for (int i = 0; i < myList.size(); i++) {
            logger.debug(myList.get(i));
            serviceExists = checkForNamedService(myList.get(i));
            if (serviceExists == true) {
                logger.info("  call remove with  " + myList.get(i));
                boolean serviceDel = manageInstallServiceRemoveByName(myList.get(i));
                if (!serviceDel) {
                    retVal = false;
                }
            } else {
                logger.error("Expected Service Not Found  " + myList.get(i));
                retVal = false;
            }

        }

        return retVal;
    }

    /**
     * @param mountPath
     * @return
     */
    public boolean callmanageInstallServiceAddInteractive(String mountPath) {
        Host sut = getMsHost();
        String baseCmd = "/ericsson/autoinstall/bin/manage_install_service.bsh ";
        String cmdOpt = " -a add -p  " + mountPath;

        initializeHelper(sut, sut.getFirstAdminUser());
        runInteractiveScriptAndStop(baseCmd + cmdOpt);
        
        // Waits for question and answer yes
        boolean found = this.interactWithShell(20, "Are you sure you wish to add the specifed install area?", "Yes");
        if (!found) {
            logger.error("Failed to add");
            return false;                
        }
        String output = waitForInteractiveToEnd(200);
        int exitCode = getCommandExitValue();
        closeShell();

        logger.info("exitCode is  " + exitCode);
        
        if (!output.contains("<- Successful")) {
            logger.error("No successful in running  " + baseCmd + " with option " + cmdOpt + " on " + sut);
            return false;
        }
        
        if (exitCode == 0) {
            logger.info("running  " + baseCmd + " with option " + cmdOpt + " on " + sut + " Sucessful");
            return true;
        } else {
            logger.error("Error Detected running  " + baseCmd + " with option " + cmdOpt + " on " + sut);
            return false;
        }
    }

    /**
     * Method Checks if we have passed in a solmedia path parameter from jenkins.
     * *
     * 
     * @return String
     */
    public String checkForMountPathInput() {
        String useMountPointInputPath = (String) DataHandler.getAttribute("solmediaPath");
        if (useMountPointInputPath != null && !useMountPointInputPath.isEmpty()) {
            logger.info("Solaris Media Path" + useMountPointInputPath);
        }
        return (useMountPointInputPath);
    }

    /**
     * Method Checks if we have passed in a serviceNum parameter from jenkins.
     * *
     * 
     * @return String
     */
    public String checkForServiceInput() {
        String useServiceInput = (String) DataHandler.getAttribute("ServiceNum");
        if (useServiceInput != null && !useServiceInput.isEmpty()) {
            logger.info("Service Name is" + useServiceInput);
        }
        return (useServiceInput);
    }
    
    public String compareStrings() {
         return null;
    }

    
    /**
     * Verify we Display usage screen, if no params is passed in.
     */
    public boolean noParams() {
        boolean checkUsageMsg = false;
        Boolean retVal = false;
        Host sut = getMsHost();
        String baseCmd = "/ericsson/autoinstall/bin/manage_install_service.bsh ";
        
        
        initializeHelper(sut, sut.getFirstAdminUser());
        runInteractiveScriptAndStop(baseCmd);
        
        // Waits for question and answer yes
        String cmdOut = expect("Press return key to continue");
        interactWithShell("\\r");
        
        checkUsageMsg = checkForUsage(cmdOut);
        logger.info("Check usage returned    " + checkUsageMsg);

        if (checkUsageMsg == true) {
            logger.info("running Negative TC   " + baseCmd + " on " + sut + " We expected this to see Usage Screen");
            retVal = true;
        } else {
            logger.error("Running Negative TC , We did not See Usage Screen after command   " + baseCmd + "  on " + sut);
            retVal = false;
        }
        
        waitForInteractiveToEnd(200);
        int exitCode = getCommandExitValue();
        closeShell();
        
        if (exitCode != 1) {
            logger.error("Wrong exit code from getting usage");
            retVal = false;
        }

        return retVal;

    }

    /**
     * check for a manage-install-service with invalid -a option
     * -a listssss
     * 
     * @return
     */
    public boolean invalidOption() {
        boolean checkUsageMsg = false;
        Boolean retVal = false;
        Host sut = getMsHost();
        String baseCmd = "/ericsson/autoinstall/bin/manage_install_service.bsh ";
        String cmdOpt = "-a listssss";
        
        initializeHelper(sut, sut.getFirstAdminUser());
        runInteractiveScriptAndStop(baseCmd + cmdOpt);
        
        // Waits for question and answer yes
        String cmdOut = expect("Press return key to continue");
        interactWithShell("\\r");
        
        checkUsageMsg = checkForUsage(cmdOut);
        logger.info("Check usage returned    " + checkUsageMsg);

        if (checkUsageMsg == true) {
            logger.info("running Negative TC   " + baseCmd + " on " + sut + " We expected this to see Usage Screen");
            retVal = true;
        } else {
            logger.error("Running Negative TC , We did not See Usage Screen after command   " + baseCmd + "  on " + sut);
            retVal = false;
        }
        
        waitForInteractiveToEnd(200);
        int exitCode = getCommandExitValue();
        closeShell();
        
        if (exitCode != 1) {
            logger.error("Wrong exit code from getting usage");
            retVal = false;
        }


        return retVal;

    }

    /**
     * Verify a Usage screen is displayed when an incorrect message is entered
     * class takes get stdout stores it in a string and searches it for the word Usage:
     * 
     * @param cmdOutput
     * @return
     */
    private boolean checkForUsage(String cmdOutput) {
        boolean checkUsageMsg = false;
        // cmdOutput=(getLastOutput());
        // Check text output to screen to see to we proceed
        String[] perlines = cmdOutput.split("\\n");
        for (int i = 0; i < perlines.length; i++) {
            if (perlines[i].contains("Usage:")) {
                logger.info("Usage Screen Displayed OK");
                checkUsageMsg = true;
                break;
            }

        }
        return checkUsageMsg;

    }

    /**
     * check for a manage-install-service Fails with a non existing -z switch
     * 
     * @return
     */
    public boolean invalidSwitch() {
        boolean checkUsageMsg = false;
        Boolean retVal = false;
        Host sut = getMsHost();
        String baseCmd = "/ericsson/autoinstall/bin/manage_install_service.bsh ";
        String cmdOpt = "-z";
        
        initializeHelper(sut, sut.getFirstAdminUser());
        runInteractiveScriptAndStop(baseCmd + cmdOpt);
        
        // Waits for question and answer yes
        String cmdOut = expect("Press return key to continue");
        interactWithShell("\\r");
        
        checkUsageMsg = checkForUsage(cmdOut);
        logger.info("Check usage returned    " + checkUsageMsg);

        if (checkUsageMsg == true) {
            logger.info("running Negative TC   " + baseCmd + cmdOpt + " on " + sut + " We expected this to see Usage Screen");
            retVal = true;
        } else {
            logger.error("Running Negative TC , We did not See Usage Screen after command   " + baseCmd + cmdOpt + "  on " + sut);
            retVal = false;
        }
        
        waitForInteractiveToEnd(200);
        int exitCode = getCommandExitValue();
        closeShell();
        
        if (exitCode != 1) {
            logger.error("Wrong exit code from getting usage");
            retVal = false;
        }

        return retVal;
    }

    /**
     * check manage-install-service with an invalid mount path.
     * We exepec this to Fail
     * 
     * @param mountPath
     * @return
     */
    public boolean invalidMediaPath(String mountPath) {
        logger.info(" In Operator class  ");
        List<Host> sut = DataHandler.getAllHostsByType(HostType.MS);
        // test if MWS exists
        if (sut.isEmpty()) {
            logger.warn("No MWS Server  defined in host.properties so test has nothing to do");
            return true;
        }
        // Command to test
        String baseCmd = "/ericsson/autoinstall/bin/manage_install_service.bsh ";
        String cmdOpt = " -a add -p " + mountPath + " -N";
        Boolean retVal = false;
        int exitCode = runSingleBlockingCommandOnMwsAsRoot(baseCmd + cmdOpt, false);
        logger.info("exitCode is  " + exitCode);

        if (exitCode == 0) {
            logger.error("running  " + baseCmd + " with option " + cmdOpt + " on " + sut + " Expected it to fail");
            retVal = true;
        } else {
            logger.info("Negative TC Error Detected as Expected   " + baseCmd + " with option " + cmdOpt + " on " + sut);
            retVal = false;
        }
        return retVal;

    }



}
