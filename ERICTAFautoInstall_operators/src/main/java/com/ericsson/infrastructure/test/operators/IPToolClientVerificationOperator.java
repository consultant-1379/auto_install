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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.data.*;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;

public class IPToolClientVerificationOperator extends Auto_Install_CommonOperator {

    private final static Logger logger = LoggerFactory.getLogger(IPToolClientVerificationOperator.class);
    
    public boolean runTest(HostType hostType, String ipV6, String subnet) {
        Host host = DataHandler.getHostByType(hostType);
        if (host == null) {
            logger.info("No tests of type: " + hostType);
            return true;
        }
        List<Host> hostList = new ArrayList<Host>();
        hostList.add(host);
        String args = "-r 2001:1b70:82a1:0103::1 -n " + getHostname(host) + " -i " + ipV6 + " -s " + subnet;
        boolean success = this.copyExecute("TC_iptool.bsh", args, hostList);
        return success;
    }
    
    private String getHostname(Host host) {
        return host.getHostname() + "-v6";
    }

}
