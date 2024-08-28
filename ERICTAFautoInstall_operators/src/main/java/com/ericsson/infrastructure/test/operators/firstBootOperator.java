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

public class firstBootOperator extends Auto_Install_CommonOperator {

    /**
     * Returns list of client types required
     * @return String[]
     */
    private String[] getHostTypes() {
        String installedClients[] = DataHandler.getConfiguration().getStringArray("installedClient");
        if (installedClients == null || installedClients.length == 0) {
            installedClients = new String[] { "MS" };
        }
        return (installedClients);

    }
    
    /**
     * Copy and execute file from auto_install gitrepo and execute on SUT.
     * 
     * @param manageDhcpInputs
     */

    public boolean callCopyExecute() {

        boolean successful = true;
        Auto_Install_CommonOperator test2oper;
        // identify box we want to copy file to by type
        String hostTypes[] = getHostTypes();
        for (String hostType : hostTypes) {
            List<Host> sut = DataHandler.getAllHostsByType(HostType.valueOf(hostType));
            // test if box exists
            if (sut.isEmpty()) {
                logger.warn("No " + hostType + " servers defined in host.properties so test has nothing to do");
            } else {
                logger.info("call  copy & Execute to : " + sut);
                test2oper = new Auto_Install_CommonOperator();
                // Pass in script name , params and server
                if (!test2oper.copyExecute("TC_first_boot_test.bsh", "", sut)) {
                    logger.error("First boot test failed on type: " + hostType);
                    successful = false;
                }
            }
        }
        return successful;
    }

}
