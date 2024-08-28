package com.ericsson.infrastructure.test.steps;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.infrastructure.test.operators.*;
import com.google.inject.Inject;

public class ManageDhcpTestSteps extends TorTestCaseHelper {

    final static Logger logger = LoggerFactory.getLogger(ManageDhcpTestSteps.class);

    boolean exceptionRaised = false;

    @Inject
    private ManageDhcpOperator upgOperator;

    /**
     * Verify manage_dhcp.bsh command line options.
     */

    @TestStep(id = "manageDhcp")
    public void testManageInstallService() throws InterruptedException {
        setTestStep("manageDhcp");
        // get admins defined in host.properties
        List<Host> hosts = DataHandler.getAllHostsByType(HostType.MS);
        for (Host host : hosts) {
            logger.info("Test with host: " + host);
        }

        // Check have we passed in an Input string
        String manageDhcpInputs[] = upgOperator.checkForManageDhcpInputs();

        // used for testing without jenkins can remove later.
        // String manageDhcpInputs="-i 10.224.30.0 -m 255.255.255.0 -r 10.224.30.1 -d 159.107.173.3 -n athtem.eei.ericsson.se -t none";

        if (manageDhcpInputs != null && manageDhcpInputs.length != 0) {
            for (String line : manageDhcpInputs) {
                logger.info("Using input Parameters " + line);
            }
        } else {
            logger.warn("No  input Parameters Supplied to test case");
        }

        // TC Add install service.
        boolean exit1 = upgOperator.callCopyExecute(manageDhcpInputs);
        assertTrue(exit1);

    }

}
