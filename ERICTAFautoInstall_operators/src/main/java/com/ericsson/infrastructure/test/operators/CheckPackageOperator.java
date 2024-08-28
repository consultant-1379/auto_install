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

public class CheckPackageOperator extends Auto_Install_CommonOperator {

    /** Operator for all PCJ **/
    private Logger logger = LoggerFactory.getLogger(CheckPackageOperator.class);

    
    /**
     * Checks if listed IPS packages are on hosts of specified type
     * @return
     */
    public boolean checkIPS(String hostType) {        
        String svrPkgs[] = getPackageList("IPSPKGLIST");
        return checkPackages("pkg info -q ", svrPkgs, hostType);

    }

    /**
     * Checks if listed SVR4 packages are on hosts of specified type
     * @return
     */
    public boolean checkSVR4(String hostType) {
        String svrPkgs[] = getPackageList("SVR4PKGLIST");
        return checkPackages("pkginfo -q ", svrPkgs, hostType);

    }
    
    private boolean checkPackages(String packageCmd, String packages[], String hostType) {
        int failureCount = 0;
        List<Host> sut = DataHandler.getAllHostsByType(HostType.valueOf(hostType)); // to check the host type
        if (sut.isEmpty()) {
            logger.warn("No Servers  defined in host.properties so test has nothing to do: " + hostType);
            return true;
        }
        StringBuffer errors = new StringBuffer("Failed to find: \n");
        for (Host server : sut) {
            errors.append(server + ": ");
            User user = server.getUsers(UserType.ADMIN).get(0);
            for (String pkg : packages) {
                String command = packageCmd + pkg;
                int exitCode = runSingleBlockingCommandOnHost(server, user, command, true);
                if (exitCode != 0) {
                    failureCount += 1;
                    logger.error("SVR4 pkg " + pkg + " missing on " + server);
                    errors.append(pkg + ",");
                }
            }
            errors.append("\n");
        }
        if (failureCount != 0) {
            logger.error(errors.toString());
        }
        return (failureCount == 0);
    }
    
    private String[] getPackageList(String attributeName) {
        Object obsPkg = DataHandler.getAttribute(attributeName);
        String pkgarr[];
        if (obsPkg instanceof List) {
            int len = ((List)obsPkg).size();
            pkgarr = new String [len];
            for (int i = 0; i < len; i++) {
                pkgarr[i] = (String)((List)obsPkg).get(i);
            }
        } else {
            pkgarr = new String[1];
            pkgarr[0] = (String)obsPkg;
        }
        return pkgarr;
    }
}
