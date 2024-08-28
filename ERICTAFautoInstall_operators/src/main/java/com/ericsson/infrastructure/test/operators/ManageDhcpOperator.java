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

//import com.ericsson.cifwk.taf.TestCase;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.infrastructure.test.operators.Auto_Install_CommonOperator;

public class ManageDhcpOperator extends Auto_Install_CommonOperator {

    /**
     * Copy and execute file from auto_install gitrepo and execute on SUT.
     */

    public boolean callCopyExecute(String[] manageDhcpInputs) {

        boolean cleanup = true;
        Auto_Install_CommonOperator test2oper;
        // identify box we want to copy file to by type
        List<Host> sut = DataHandler.getAllHostsByType(HostType.MS);
        // test if box exists
        if (sut.isEmpty()) {
            System.out.println("No Servers  defined in host.properties so test has nothing to do");
            return cleanup;
        }

        logger.info("call  copy & Execute to : " + sut);
        test2oper = new Auto_Install_CommonOperator();
        // Pass in script name , params and server
        
        boolean result = true;
        for (String line : manageDhcpInputs) {
            System.out.println("Doing: " + line);
        }
        for (String line : manageDhcpInputs) {
            boolean success = test2oper.copyExecute("TC_managed_dhcp.bsh", line, sut);
            if (!success) {
                result = false;
            }
        }
        return result;
    }

    /**
     * Method Checks if we have passed in a parameter string to TC_manage_dhcp.
     * Using one String for initial testing
     * We can break out to several values stored in variables if we need to later *
     * 
     * @return String
     */
    public String[] checkForManageDhcpInputs() {
        String[] useManageDhcpInputsPath = DataHandler.getConfiguration().getStringArray("TC_manage_dhcp_inputs");
        if (useManageDhcpInputsPath != null && useManageDhcpInputsPath.length != 0) {
            logger.info("TC_manage_dhcp_inputs " + useManageDhcpInputsPath);
        }
        return (useManageDhcpInputsPath);

    }

}
