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
import java.lang.reflect.Array;
import java.nio.file.*;
import java.util.*;

import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TafTestContext;
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.data.*;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;

public class ManageDhcpClientsAddOperator extends Auto_Install_CommonOperator {

    /** Operator for all PCJ **/
    private Logger logger = LoggerFactory.getLogger(CacheLatestMediaOperator.class);

    private static String AUTO_INSTALL_DIR = "/ericsson/autoinstall/";

    // private static String PARAM_DIR = "/tmp/Saravana/";
    private TestContext context = TafTestContext.getContext();

    // try adding the client again should fail
    public boolean addDhcpClientHostnameTest(
            Map<String, String> paramDetails,
            String ipv6,
            String appType,
            String installArea,
            String installService,
            String omMedia,
            String displayType) {
        int exitCode = -1;
        boolean testResult = false;
        // List<String> getListOfParamFiles = new ArrayList<String>();
        // this whole for loop can be put up in steps.
        String command = AUTO_INSTALL_DIR + "/bin/manage_dhcp_clients.bsh";
        Host sut = getMsHost();
        String cmdOpt = " -a add -c " + paramDetails.get("CLIENT_HOSTNAME");
        CLICommandHelper cmdHelper = new CLICommandHelper(sut); // Create instance on sc2 host
        cmdHelper.openShell(); // Open a shell instance
        cmdHelper.runInteractiveScript(command + cmdOpt); // Execute the script test.sh
        cmdHelper.expect("Enter the IP address of " + paramDetails.get("CLIENT_HOSTNAME"));
        cmdHelper.interactWithShell(paramDetails.get("CLIENT_IP_ADDR"));
        logger.info("Entered Client IP address as" + paramDetails.get("CLIENT_IP_ADDR"));
        cmdHelper.expect("Enter the IP Netmask of " + paramDetails.get("CLIENT_IP_ADDR"));
        cmdHelper.interactWithShell(paramDetails.get("CLIENT_NETMASK"));
        logger.info("Entered Client IP Netmask as" + paramDetails.get("CLIENT_NETMASK"));
        cmdHelper.expect("Please enter the MAC address for " + paramDetails.get("CLIENT_HOSTNAME"));
        cmdHelper.interactWithShell(paramDetails.get("CLIENT_MAC_ADDR"));
        logger.info("Entered Client MAC Address as" + paramDetails.get("CLIENT_MAC_ADDRESS"));
        cmdHelper.expect("Enter the IPV6 address of " + paramDetails.get("CLIENT_HOSTNAME") + ", example: 2001:1b70:82a1:000a:0000:4000:0034:0001/64");
        // if(paramDetails.get("IPV6_PARAMETER") == "NO"){return none; else return paramDetails.get("IPV6_PARAMETER");}
        cmdHelper.interactWithShell(ipv6);
        cmdHelper.expect("Select the application type you wish to install on " + paramDetails.get("CLIENT_HOSTNAME"));
        // write methods to return ipv6 and type of server to be installed & area & install service & O&M & display type
        cmdHelper.interactWithShell(appType);
        cmdHelper.expect("Select number of the area you wish to use");
        cmdHelper.interactWithShell(installArea);
        cmdHelper.expect("Select the Solaris install service you wish to use for " + paramDetails.get("CLIENT_HOSTNAME"));
        cmdHelper.interactWithShell(installService);
        cmdHelper.expect("Select number of the O&M media you wish to use");
        cmdHelper.interactWithShell(omMedia);
        cmdHelper.expect("Select the display type of " + paramDetails.get("CLIENT_HOSTNAME"));
        cmdHelper.interactWithShell(displayType);
        cmdHelper.expect("Enter the installation parameters for the client");
        cmdHelper.interactWithShell(paramDetails.get("CLIENT_INSTALL_PARAMS"));
        cmdHelper.expect("Are you sure you wish to add this auto install client? (Yes|No)", 60);
        cmdHelper.interactWithShell("Yes");
        // user confirmation Yes after the command is run
        // give a timer to wait for sometime on it
        exitCode = cmdHelper.getCommandExitValue();
        cmdHelper.disconnect();
        if (exitCode != 0) {
            testResult = false;
        } else {
            testResult = true;
        }
        return testResult;
    }

    public Map<String, String> readParamsIntoMap(ArrayList<String> param_details) {
        Map<String, String> paramDetails = new HashMap<String, String>();
        for (String param : param_details) {
            if (param != null) {
                String[] param_part = param.split("@");
                logger.debug("Adding Parameter information to Map as" + param_part[0] + " " + param_part[1]);
                if (param_part[0] != null && param_part[1] != null) {
                    paramDetails.put(param_part[0], param_part[1]);

                }
            }
        }
        return paramDetails;
    }

    /**
     * @param path
     * @return
     */
    public ArrayList<String> readContentsOfFile(File path) {
        logger.info("Path of the File is " + path);
        ArrayList<String> fileContentsList = new ArrayList<String>();
        try (InputStream in = new FileInputStream(path); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                fileContentsList.add(line);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return fileContentsList;
    }

    public boolean testParamFile(String paramFile) {
        String command = AUTO_INSTALL_DIR + "/bin/manage_dhcp_clients.bsh -a add -f " + paramFile + " -N";

        int exitCode = runSingleBlockingCommandOnMwsAsRoot(command);
        if (exitCode != 0) {
            return false;
        } else {
            return true;
        }

    }

    public boolean addDhcpClientParamFileNoConfirmTest(String paramFile) {
        int exitCode = -1;
        int failureCount = 0;
        int successCount = 0;
        boolean cleanup = true;
        Auto_Install_CommonOperator test2oper;
        // identify box we want to copy file to by type
        List<Host> sut = DataHandler.getAllHostsByType(HostType.MS);
        // test if box exists
        if (sut.isEmpty()) {
            System.out.println("No Servers defined in host.properties so test has nothing to do");
            return cleanup;
        }
        logger.info("call  copy & Execute to : " + sut);
        test2oper = new Auto_Install_CommonOperator();
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
                String command = AUTO_INSTALL_DIR + "/bin/manage_dhcp_clients.bsh -a add -f " + remoteFilePath + " -N";
                exitCode = runSingleBlockingCommandOnMwsAsRoot(command);
                if (exitCode != 0) {
                    logger.info("Failed adding the client with param file " + paramFile + "on server " + sut);
                    failureCount += 1;
                } else {
                    logger.info("Successfully added the client with param file " + paramFile + "on server " + sut);
                    successCount += 1;
                }
            }
        }
        if (failureCount != 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addDhcpClientParamFileConfirmTest(String paramFile) {
        int exitCode = -1;
        int failureCount = 0;
        int successCount = 0;
        boolean cleanup = true;
        Auto_Install_CommonOperator test2oper;
        // identify box we want to copy file to by type
        List<Host> sut = DataHandler.getAllHostsByType(HostType.MS);

        // test if box exists
        if (sut.isEmpty()) {
            System.out.println("No Servers defined in host.properties so test has nothing to do");
            return cleanup;
        }
        test2oper = new Auto_Install_CommonOperator();
        for (Host server : sut) {
            Host serverInfo = getMsHost();
            CLICommandHelper cmdHelper = new CLICommandHelper(server); // Create instance on sc2 host
            String localFile = paramFile;
            User user = server.getUsers(UserType.ADMIN).get(0);
            logger.info("Test with: " + server + " and user:  " + user);
            // hardcoded remote path where we copy file to
            String remoteFilePath = "/var/tmp/";
            boolean sent = sendFileRemotely2(server, user, localFile, remoteFilePath);
            // Only proceed to execute if copy has been Successful
            if (sent) {
                String command = AUTO_INSTALL_DIR + "/bin/manage_dhcp_clients.bsh -a add -f " + remoteFilePath + paramFile;
                cmdHelper.openShell(); // Open a shell instance
                cmdHelper.runInteractiveScript(command); // Execute the script test.sh
                cmdHelper.expect("Are you sure you wish to add this auto install client?", 30);
                cmdHelper.interactWithShell("Yes");
                try {
                    Thread.sleep(30000);                 // 1000 milliseconds is one second.
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                exitCode = cmdHelper.getCommandExitValue();
                cmdHelper.disconnect();
                if (exitCode != 0) {
                    logger.info("Failed adding the client with param file " + paramFile + "on server " + sut);
                    failureCount += 1;
                } else {
                    logger.info("Successfully added the client with param file " + paramFile + "on server " + sut);
                    successCount += 1;
                }
            }
        }
        if (failureCount != 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addDhcpClientTest(
            Map<String, String> paramDetails,
            String ipv6,
            String appType,
            String installArea,
            String installService,
            String omMedia,
            String displayType,
            boolean hostname) {
        int exitCode = -1;
        boolean testResult = false;
        boolean ldapServerConfigured = false;
        if (this.runSingleBlockingCommandOnMwsAsRoot("ls /JUMP/PKS_CERT/*/rootca.cer") == 0) {
            // DHCP client considers ldap there if finds root certificate
            ldapServerConfigured = true;
        }
        String command = AUTO_INSTALL_DIR + "/bin/manage_dhcp_clients.bsh";
        Host sut = getMsHost();
        String cmdOpt = " -a add";
        if (hostname) {
            cmdOpt = cmdOpt + " -c " + paramDetails.get("CLIENT_HOSTNAME");
        }
        CLICommandHelper cmdHelper = new CLICommandHelper(sut); // Create instance on sc2 host
        cmdHelper.openShell(); // Open a shell instance
        cmdHelper.runInteractiveScript(command + cmdOpt); // Execute the script test.sh
        if (!hostname) {
            cmdHelper.expect("Enter the client hostname"); // expect method which checks the standard output for prompt string
            cmdHelper.interactWithShell(paramDetails.get("CLIENT_HOSTNAME"));
            logger.info("Entered Client Hostname as" + paramDetails.get("CLIENT_HOSTNAME"));
        }
        cmdHelper.expect("Enter the IP address of " + paramDetails.get("CLIENT_HOSTNAME"), 10);
        cmdHelper.interactWithShell(paramDetails.get("CLIENT_IP_ADDR"));
        logger.info("Entered Client IP address as" + paramDetails.get("CLIENT_IP_ADDR"));
        cmdHelper.expect("Enter the IP Netmask of " + paramDetails.get("CLIENT_IP_ADDR"));
        cmdHelper.interactWithShell(paramDetails.get("CLIENT_NETMASK"));
        logger.info("Entered Client IP Netmask as" + paramDetails.get("CLIENT_NETMASK"));
        cmdHelper.expect("Please enter the MAC address for " + paramDetails.get("CLIENT_HOSTNAME"));
        cmdHelper.interactWithShell(paramDetails.get("CLIENT_MAC_ADDR"));
        logger.info("Entered Client MAC Address as" + paramDetails.get("CLIENT_MAC_ADDRESS"));
        cmdHelper.expect("Enter the IPV6 address of " + paramDetails.get("CLIENT_HOSTNAME") + ", example: 2001:1b70:82a1:000a:0000:4000:0034:0001/64");
        logger.info("Entered IPV6 as " + ipv6);
        cmdHelper.interactWithShell(ipv6);
        if (ldapServerConfigured) {
            cmdHelper.expect("Select the LDAP Server details you wish to use for " + paramDetails.get("CLIENT_HOSTNAME"));
            logger.info("Entered LDAP information as none");
            cmdHelper.interactWithShell("none");
        }
        cmdHelper.expect("Select the application type you wish to install on " + paramDetails.get("CLIENT_HOSTNAME"));
        logger.info("Selecting the app type as " + appType);
        cmdHelper.interactWithShell(appType);
        try {
            for (int i = 0; i < 15; i++) {
                cmdHelper.expect("Press return key to continue");

                logger.debug("Pressing return key with try block");
                logger.debug("for loop count " + i);
                cmdHelper.interactWithShell(" ");
            }
        } catch (TimeoutException e) {
            System.out.println("Cannot find string");

        }
        cmdHelper.interactWithShell(installArea);
        cmdHelper.expect("Select the Solaris install service you wish to use for " + paramDetails.get("CLIENT_HOSTNAME"));
        logger.info("Selecting solaris install service as " + installService);
        cmdHelper.interactWithShell(installService);
        cmdHelper.expect("Select number of the O&M media you wish to use");
        logger.info("Selecting O&M Media as " + omMedia);
        cmdHelper.interactWithShell(omMedia);
        cmdHelper.expect("Select the display type of " + paramDetails.get("CLIENT_HOSTNAME"));
        logger.info("Choosing display type as " + displayType);
        cmdHelper.interactWithShell(displayType);
        cmdHelper.expect("Enter the installation parameters for the client");
        logger.info("Entering install parameters as " + paramDetails.get("CLIENT_INSTALL_PARAMS"));
        cmdHelper.interactWithShell(paramDetails.get("CLIENT_INSTALL_PARAMS"));
        cmdHelper.expect("Are you sure you wish to add this auto install client? (Yes|No)");

        cmdHelper.interactWithShell("Yes");
        // user confirmation Yes after the command is run
        // give a timer to wait for sometime on it
        try {
            Thread.sleep(30000);                 // 1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        exitCode = cmdHelper.getCommandExitValue();
        logger.info("Exit Code is " + exitCode);
        cmdHelper.disconnect();
        if (exitCode != 0) {
            testResult = false;
        } else {
            testResult = true;
        }
        return testResult;
    }

    /**
     * @param clientInput
     * @param paramFileLoc
     * @return
     */
    public boolean createParamFile(String paramFileLoc) {
        // TODO Auto-generated method stub
        // boolean fileCreated = false;
        String paramFile = "/tmp/saravana/paramFile";
        String paramFileContent = getParamFileContentinString(paramFileLoc);
        // String clientInputs = clientInput.replaceAll(" ", "\\\\n");
        // String[] clientInputs = clientInput.split(";");
        try (OutputStream op = new FileOutputStream(paramFile); BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(op))) {

            op.write(paramFileContent.getBytes());

        } catch (IOException e) {
            System.err.println(e);
        }
        File f = new File(paramFile);
        if (f.exists() && !f.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param paramFileLoc
     * @return
     */
    private String getParamFileContentinString(String paramFileLoc) {
        // TODO Auto-generated method stub
        String command = "cat " + paramFileLoc;
        Host sut = getMsHost();
        final CLICommandHelper cmdHelper = new CLICommandHelper(sut);
        cmdHelper.execute(command);
        String tempParamlist = cmdHelper.getStdOut();
        logger.debug("Temp String" + tempParamlist);
        return tempParamlist;
    }

    /**
     * @param paramContentInMap
     * @return
     */
    public String ipv6Info(Map<String, String> paramContentInMap) {
        // TODO Auto-generated method stub
        String ipv6 = paramContentInMap.get("CLIENT_IP_ADDR_V6");

        return ipv6;
    }

    public String determineAppType(Map<String, String> paramContentInMap) {
        // TODO Auto-generated method stub
        String appType = paramContentInMap.get("CLIENT_APPL_TYPE");
        switch (appType) {
            case "cominf_install":
                appType = "1";
                break;
            case "eniq_es":
                appType = "2";
                break;
            case "eniq_events":
                appType = "3";
                break;
            case "eniq_stats":
                appType = "4";
                break;
            case "ombs":
                appType = "5";
                break;
            case "ossrc":
                appType = "6";
                break;
            case "solonly":
                appType = "7";
                break;
            case "bmr":
                appType = "8";
                break;
            default:
                logger.info("Cannot determine the apptype");
                break;
        }

        return appType;
    }

    /**
     * @param paramContentInMap
     * @return
     */
    public String determineInstallArea(Map<String, String> paramContentInMap) {
        // TODO Auto-generated method stub
        String installArea = "1";

        return installArea;
    }

    /**
     * @param paramContentInMap
     * @return
     */
    public String determineInstallService(Map<String, String> paramContentInMap) {
        // TODO Auto-generated method stub
        String installService = "1";
        return installService;
    }

    /**
     * @param paramContentInMap
     * @return
     */
    public String determineOmMedia(Map<String, String> paramContentInMap) {
        // TODO Auto-generated method stub
        String omMedia = "1";
        return omMedia;
    }

    /**
     * @param paramContentInMap
     * @return
     */
    public String determineDisplayType(Map<String, String> paramContentInMap) {
        // TODO Auto-generated method stub
        String displayType = null;
        if (paramContentInMap.get("CLIENT_DISP_TYPE").equalsIgnoreCase("vga")) {
            displayType = "2";
        } else {
            displayType = "1";
        }
        return displayType;
    }

}
