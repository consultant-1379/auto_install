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
package com.ericsson.infrastructure.test.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.infrastructure.test.operators.MasterShOperator;
import com.google.inject.Inject;

public class MasterShTestSteps extends TorTestCaseHelper {

    private final static Logger logger = LoggerFactory.getLogger(MasterShTestSteps.class);

    @Inject
    private MasterShOperator masterShOperator;

    /**
     * Verify master.sh command line options.
     */

    @TestStep(id = "kickstartAutoinstall")
    public void testManageInstallService() throws InterruptedException {
        setTestStep("masterSh");

        // Check have we passed in an Input string
        String masterShInputs = masterShOperator.checkForMasterShInputs();

        // used for testing without jenkins can remove later.

        if (masterShInputs != null && !masterShInputs.isEmpty()) {
            logger.info("Using input Parameters " + masterShInputs);
        } else {
            logger.warn("No  input Parameters Supplied to autoinstall the client");
        }
        // masterShInputs =
        // "-c /export/scripts/CLOUD/configs/templates/oss_box_dm/manhattan_proj_variable_4939-1.txt:/export/scripts/CLOUD/configs/media_configs_dm/16.0.8/ieatloaner156-1.txt:/export/scripts/CLOUD/configs/netsim_configs/wran_lte_gran_16.2.2.txt:/export/scripts/CLOUD/configs/dm/ebas_only.txt -g `hostname` -o yes -l /export/scripts/CLOUD/logs/web/CI_EXEC_OSSRC/ -f rollout_config";
        // TC Add install service.
        boolean autoInstallStatus = masterShOperator.autoInstallClient(masterShInputs);
        assertTrue(autoInstallStatus);

    }

}
