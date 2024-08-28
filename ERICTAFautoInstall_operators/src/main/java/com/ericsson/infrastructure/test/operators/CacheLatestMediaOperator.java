package com.ericsson.infrastructure.test.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TafTestContext;
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.configuration.TafConfiguration;
import com.ericsson.cifwk.taf.configuration.TafConfigurationProvider;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.infrastructure.test.operators.Auto_Install_CommonOperator;
import com.google.inject.Singleton;

@Operator(context = Context.CLI)
@Singleton
public class CacheLatestMediaOperator extends Auto_Install_CommonOperator {

    /** Operator for all PCJ **/
    private Logger logger = LoggerFactory.getLogger(CacheLatestMediaOperator.class);
    private final static String WGET = "/usr/local/bin/wget";
    private final static String CI_LINK = "https://cifwk-oss.lmera.ericsson.se";
    private final static String CI_PROXY_LINK = "https://arm901-eiffel004.athtem.eei.ericsson.se:8443";
    private static final String USE_TO_SHIPMENT = "testware.cacheto_to_shipment";
    private final static String TEST_PKG = "/var/tmp/testArea";
    private final static String TEST_DROP_AREA = "/net/10.45.227.26/export/TestDropArea/";
    public final static String  CACHE_SUFFIX = "CachePath";

    private TestContext context = TafTestContext.getContext();
    private String mode;
    private String mediaFile;
    private String downloadPath = (String) TafConfigurationProvider.provide().getString("downloadPath", "/JUMP/PSV/");

    /**
     * Returns the mode(KGB/CDB) from the property file
     * 
     * @return
     */
    public String getMode() {
        logger.info("Getting the mode from the job");
        mode = (String) DataHandler.getAttribute("mode");
        return (mode);
    }

    /**
     * Gets the psv from propertyfile if mode is CDB or runs rest call for shipment
     * 
     * @param shipment
     *            ex:15.2.1
     * @param mode
     *            ex CDB/ KGB
     * @return Product set version of the given shipment
     */

    public String getPsv(String shipment, String mode) {
        int exitCode;
        logger.info("Getting PSV for the shipment " + shipment + " for the mode " + mode);
        if (mode.toLowerCase().equals("cdb")) {
            String psv = (String) DataHandler.getAttribute("psv");
            if (psv.toLowerCase().equals("latest")) {
                String url = CI_LINK + "/getProductSetVersions/?drop=" + shipment + "&productSet=OSS-RC";
                String command = WGET + " -q -O - --no-check-certificate --ignore-length '" + url + "'"
                        + " | nawk -F '</*version>' '{print $2}' | head -1";
                exitCode = runSingleBlockingCommandOnMwsAsRoot(command);
                if (exitCode == 0) {
                    if (getLastOutput() == null || (getLastOutput().length() == 0)) {
                        logger.error("wget failed to download the latest psv");
                        return (null);
                    } else {
                        return (getLastOutput());
                    }
                }
                return (null);
            } else {
                return (psv);
            }
        } else {

            String url = CI_LINK + "/getLastGoodProductSetVersion/?drop=" + shipment + "&productSet=OSS-RC";
            String command = WGET + " -q -O - --no-check-certificate --ignore-length '" + url + "'";
            logger.info("URL IS" + url);
            logger.info("URL IS" + command);
            if (runSingleBlockingCommandOnMwsAsRoot(command) == 0) {
                return (getLastOutput());
            }
            return (null);
        }

    }

    /**
     * Runs the rest call to get the json file and greps the media to get the respective version
     * 
     * @param psv
     * @param iso
     *            - ex : OM, COMINF, Security, OSS-RC
     * @param shipment
     * @return version of iso in product set
     * 
     */
    public String getISOver(String psv, String iso, String shipment) {
        logger.info("Getting ISO verison of " + iso + " for " + shipment + " of psv " + psv);
        if (iso.equals("OSSRC")) {
            iso = "OSS-RC";
        }
        if (iso.equals("OMSAS")) {
            iso = "Security";
        }
        if (iso.equals("SOLARIS")) {
            iso = "solaris";
        }
        String command = WGET + " -q -O - --no-check-certificate --ignore-length '" + CI_LINK + "/getProductSetVersionContents/?drop=" + shipment
                + "&productSet=OSS-RC&version=" + psv + "&pretty=true' | grep athloneUrl | awk -F: '{print $4}' | grep ERIC" + iso
                + " | awk -F/ '{print $(NF-1)}' | head -1";
        if (runSingleBlockingCommandOnMwsAsRoot(command) == 0) {
            return (getLastOutput());
        }
        return (null);
    }

    /**
     * Runs lofiadm mediaPath command on mws
     * 
     * @param mediaFile
     * @param mediaVer
     * @return lofidevice ex- /dev/lofi/1ls
     */
    public String getLofiadmDevice(String mediaFile, String mediaVer) {

        logger.info("Getting lofiadm device of " + mediaFile);
        String command = "lofiadm " + downloadPath + mediaFile;
        if (runSingleBlockingCommandOnMwsAsRoot(command) == 0) {
            return (getLastOutput());
        }
        command = "lofiadm -a " + downloadPath + mediaFile;
        if (runSingleBlockingCommandOnMwsAsRoot(command) == 0) {
            return (getLastOutput());
        }
        return (null);
    }

    /**
     * Runs mount -F command on MWS
     * 
     * @param lofiDev
     * @param mountPath
     * @return exit value of the command
     */

    public int mountMedia(String lofiDev, String mountPath) {
        if (runSingleBlockingCommandOnMwsAsRoot("mount | grep " + lofiDev) == 0) {
            runSingleBlockingCommandOnMwsAsRoot("mount | grep " + lofiDev + " | awk '{print $1}'");
            umountMedia(getLastOutput().trim());
        } else if (runSingleBlockingCommandOnMwsAsRoot("mount | grep " + mountPath) == 0) {
            umountMedia(mountPath);
        }
        makeDirOnMws(mountPath);
        logger.info("Mounting device " + lofiDev + " to " + mountPath);
        String command = "mount -F hsfs " + lofiDev + " " + mountPath;
        return (runSingleBlockingCommandOnMwsAsRoot(command));

    }

    /**
     * Greps the media in download path on MWS
     * 
     * @param media
     * @param isoVer
     * @return exit value of the command
     */

    public int existenceOfMedia(String media, String isoVer) {
        // There is no single convention for media in names in all the Data
        // bases(like nexus and media info files, depending upon the DB naming
        // conventions are changing as below.
        if (media.toUpperCase().equals("OSSRC")) {
            media = "OSS-RC";
        }
        if (media.toUpperCase().equals("OMSAS")) {
            media = "Security";
        }
        if (media.toUpperCase().equals("SOLARIS")) {
            media = "solaris";
        }
        logger.debug("Checking the Existence of " + media + " media of version " + isoVer + " in /JUMP/ISO");
        String command = "ls " + downloadPath + " | grep ERIC" + media + " | grep " + isoVer + ".iso$";
        int exitCode = runSingleBlockingCommandOnMwsAsRoot(command);
        mediaFile = getLastOutput();
        context.setAttribute("mediaFile", mediaFile);
        return (exitCode);
    }

    /**
     * Runs upgrade_om.bsh script to upgrde the ERICjump package.
     * 
     * @param directory
     *            - mount directory of om media
     * @return returns exit value of the script
     */
    public int upgradeERICjump(String directory) {
        logger.info("Upgrading ERICjump package on mws");
        String command = directory + "/omtools/upgrade_om.bsh  -p " + directory + " -a mws -x ERICjump";
        return (runSingleBlockingCommandOnMwsAsRoot(command, false));
    }

    /**
     * Runs wget command of the web page to grep the built media
     * 
     * @param psv
     * @param shipment
     * @param media
     * @return Built media
     */
    public String getShipmentOfPsvMedia(String psv, String shipment, String media) {
        logger.info("Getting the built shipment of the media");

        if (media.toUpperCase().equals("OSSRC")) {
            media = "OSS-RC";
        }
        if (media.toUpperCase().equals("OMSAS")) {
            media = "Security";
        }
        if (media.toUpperCase().equals("SOLARIS")) {
            media = "solaris";
        }
        
        String command = WGET + " -q -O - --no-check-certificate --ignore-length  '" + CI_LINK + "/getProductSetVersionContents/?drop=" + shipment
                + "&productSet=OSS-RC&version=" + psv + "&pretty=false' | tr '{' '\n' | grep ERIC" + media + "  | head -1 | tr ',' '\n' | grep builtFor | cut -d : -f 2 | sed 's/[ \"]//g' ";
        


        if (runSingleBlockingCommandOnMwsAsRoot(command) == 0) {
            return (getLastOutput());
        }
        return (null);
    }

    /**
     * Gets the expected path from the template files present in /ericsson/autoinstall/etc/nfs_media_config/
     * 
     * @param media
     *            -Ex OM,OSSRC,OMSAS,COMINF
     * @param shipment
     * @return
     */
    public String getMediaExpectedCachePath(String media, String shipment) {
        if (media.toUpperCase().equals("COMINF")) {
            media = "COMINF_INSTALL";
        }
        if (media.toUpperCase().equals("SOLARIS")) {
            String statement = "/JUMP/AUTO_INSTALL";
            return (statement);
        }
        String configFile = "/ericsson/autoinstall/etc/nfs_media_config/" + media.toLowerCase();
        if (verifyPathAvailabilityOnMws(configFile) != 0) {
            configFile = "/ericsson/autoinstall/template/media_config_template/" + media.toLowerCase() + "_template";
        }

        runSingleBlockingCommandOnMwsAsRoot("cat " + configFile);

        logger.info("Getting the cached media path w.r.t " + media + " media for " + shipment);
        String mediaAreaCommand = "grep MEDIA_AREA= " + configFile + " | awk -F= '{print $2}'";
        runSingleBlockingCommandOnMwsAsRoot(mediaAreaCommand);
        String mediaArea = getLastOutput();
        logger.info("MediaArea is          :" + mediaArea);
        if (getLastOutput() == null || getLastOutput().isEmpty()) {
            return (null);
        }
        String mediaDirCommand = "grep MEDIA_DIRECTORY= " + configFile + " | awk -F= '{print $2}'";
        runSingleBlockingCommandOnMwsAsRoot(mediaDirCommand);
        String mediaDir = getLastOutput();
        logger.info("MediaArea is          :" + mediaDir);
        if (getLastOutput() == null || getLastOutput().isEmpty()) {
            return (null);
        }
        String command = "echo " + shipment + " | awk -F. '{print $1,$2}' | sed 's/ /\\_/g'";
        runSingleBlockingCommandOnMwsAsRoot(command);
        String track = getLastOutput();
        logger.info("MediaArea is          :" + track);
        if (getLastOutput() == null || getLastOutput().isEmpty()) {
            return (null);
        }

        if (media.toUpperCase().equals("OM")) {
            media = "OSSRC";
        }
        if (media.toUpperCase().equals("COMINF_INSTALL")) {
            media = "COMINF";
        }

        String mediaCachedPath = mediaArea + "/" + mediaDir + "/" + media + "_O" + track + "/" + shipment;

        logger.info("MEDIA CACHE IS HERE----->" + mediaCachedPath);
        return (mediaCachedPath);
    }
    
    

    public String getDestinationCachePath(String media, String isoCachePath, String destShipment) {
        logger.info("Inside getDestinationCachePath method" );
        logger.info("media is " + media );
        logger.info("isoCachePath is " + isoCachePath );
        logger.info("destShipment is " + destShipment );
        
        String cachePath = isoCachePath;
        logger.info("cachePath = isoCachePath is " + cachePath );
        String useToShipment = (String) TafConfigurationProvider.provide().getString(USE_TO_SHIPMENT, "no");
        if ("yes".equalsIgnoreCase(useToShipment)) {
            logger.info("Use to shipment for cache location");
            cachePath = getMediaExpectedCachePath(media, destShipment);
        }
        return cachePath;
    }

    /**
     * Returns Solaris expected Cache Path
     * 
     * @param shipment
     * @return Returns Solaris expected Cache Path
     */
    public String getSolCachePath(String shipment) {

        runSingleBlockingCommandOnMwsAsRoot("cat  /ericsson/autoinstall/release/i386/media_identity/[0-9]*");

        logger.info("Getting the cached media path w.r.t SOLARIS media for " + shipment);
        runSingleBlockingCommandOnMwsAsRoot("grep media_label /ericsson/autoinstall/release/i386/media_identity/* | awk -F: '{print $1}'", false);
        String[] solMediaInfoFileList = getLastOutput().split("\\n");
        String solMediaInfoFile = null;
        int solVer = 0, solUpdate = 0;

        for (int i = 0; i < solMediaInfoFileList.length; i++) {
            runSingleBlockingCommandOnMwsAsRoot("grep media_label " + solMediaInfoFileList[i].replace("\n", "").replace("\r", "")
                    + " | awk -F= '{print $2}' | awk -F_ '{print $3}'");
            int solVerTemp = Integer.parseInt(getLastOutput());
            if (solVerTemp > solVer) {
                solVer = solVerTemp;
            }
            runSingleBlockingCommandOnMwsAsRoot("grep ERIC_SOL_" + solVer + " " + solMediaInfoFileList[i].replace("\n", "").replace("\r", "")
                    + " | awk -F= '{print $2}' | awk -F_ '{print $4}' | sed 's/U//g''");
            int solUpdateTemp = 0;
            if (getLastExitCode() == 0) {
                solUpdateTemp = Integer.parseInt(getLastOutput());
            }
            if (solUpdateTemp > solUpdate) {
                solUpdate = solUpdateTemp;
                solMediaInfoFile = solMediaInfoFileList[i];
            }
        }
        String solMediaNum = null, solMediaRev = null, solMediaPrefix = null;

        if (runSingleBlockingCommandOnMwsAsRoot("grep media_number " + solMediaInfoFile.replace("\n", "").replace("\r", "")
                + " | awk -F= '{print $2}'") == 0) {
            solMediaNum = getLastOutput();
        }
        if (runSingleBlockingCommandOnMwsAsRoot("grep media_rev " + solMediaInfoFile.replace("\n", "").replace("\r", "") + " | awk -F= '{print $2}'") == 0) {
            solMediaRev = getLastOutput();
        }
        if (runSingleBlockingCommandOnMwsAsRoot("grep media_prefix " + solMediaInfoFile.replace("\n", "").replace("\r", "")
                + " | awk -F= '{print $2}'") == 0) {
            solMediaPrefix = getLastOutput();
        }
        if (solMediaNum.equals(null) && solMediaRev.equals(null) && solMediaPrefix.endsWith(null)) {
            return null;
        }
        runSingleBlockingCommandOnMwsAsRoot("/ericsson/autoinstall/bin/manage_install_service.bsh -a list -j " + solMediaPrefix + "-" + solMediaNum
                + "-" + solMediaRev);
        if (getLastOutput().contains("Path :")) {
            String output = getLastOutput();
            return (output.substring(output.indexOf("Path :") + 15, output.indexOf("Architecture :")).trim());
        } else {
            runSingleBlockingCommandOnMwsAsRoot("cd /JUMP/SOL_MEDIA; ls -d *");
            String[] directoryList = getLastOutput().split("");
            int newDir = 1;
            if (directoryList.length != 0) {
                for (int i = 0; i < directoryList.length; i++) {
                    if (directoryList[i].contains(Integer.toString(newDir))) {
                        newDir++;
                    }
                }
            }
            return ("/JUMP/SOL_MEDIA/" + newDir);
        }
    }

    /**
     * Gets the r-state of the cache path from the hidden file present in it
     * 
     * @param cachePath
     * @param media
     * @return version of Cached ISO
     */
    public String getVerOfCachePath(String cachePath, String media) {
        String configFile;
        logger.debug("Getting the version of path" + cachePath);
        if (media.toUpperCase().equals("COMINF")) {
            media = "COMINF_INSTALL";
        }
        if (media.toUpperCase().equals("SOLARIS")) {
            configFile = "/ericsson/autoinstall/etc/ericai_config";
            runSingleBlockingCommandOnMwsAsRoot("cat " + configFile);

            String mediaInfoFileCommand = "grep SOL_MEDIA_INFO_FILE= " + configFile + " | awk -F= '{print $2}' ";
            runSingleBlockingCommandOnMwsAsRoot(mediaInfoFileCommand);
            if (getLastOutput() == null || getLastOutput().isEmpty()) {
                return (null);
            }
            String mediaInfoFile = getLastOutput();
            // Now checking revision as the r-state is not present, in future
            // this might change to media rstate.
            String revision = "grep media_rev= " + cachePath + "/" + mediaInfoFile;
            return (convertRstateToVersion("R1" + revision + "01"));
        }
        configFile = "/ericsson/autoinstall/etc/nfs_media_config/" + media.toLowerCase();
        if (verifyPathAvailabilityOnMws(configFile) != 0) {
            configFile = "/ericsson/autoinstall/template/media_config_template/" + media.toLowerCase() + "_template";
        }
        String mediaFileCommand = null;
        if (media.toUpperCase().equals("OSSRC")) {
            mediaFileCommand = "grep MEDIA_FILE= " + configFile + " | awk -F= '{print $2}' | awk -F\\| '{print $2}'";
        } else {
            mediaFileCommand = "grep MEDIA_FILE= " + configFile + " | awk -F= '{print $2}' ";
        }

        runSingleBlockingCommandOnMwsAsRoot("cat  " + configFile);
        runSingleBlockingCommandOnMwsAsRoot(mediaFileCommand);
        if (getLastOutput() == null || getLastOutput().isEmpty()) {
            return (null);
        }
        mediaFile = getLastOutput();

        String mediaLabelCommand = "grep MEDIA_LABEL= " + configFile + " |  awk -F= '{print $2}'";
        runSingleBlockingCommandOnMwsAsRoot(mediaLabelCommand);
        if (getLastOutput() == null || getLastOutput().isEmpty()) {
            return (null);
        }
        String mediaLabel = getLastOutput();

        String rstateCommand = "grep media_rstate= " + cachePath + "/" + mediaLabel + "/" + mediaFile + " | awk -F= '{print $2}'";
        runSingleBlockingCommandOnMwsAsRoot(rstateCommand);
        if (getLastOutput() == null || getLastOutput().isEmpty()) {
            return (null);
        }

        return (convertRstateToVersion(getLastOutput()));
    }

    /**
     * Runs a rest call to convert r-state to a version
     * 
     * @param rState
     * @return version of rState
     */

    public String convertRstateToVersion(String rState) {
        logger.debug("Converting R-state" + rState + " to version ");
        String url = CI_LINK + "/getVersionFromRstate/?version=" + rState;
        String command = WGET + " -q -O - --no-check-certificate --ignore-length '" + url + "'";
        if (runSingleBlockingCommandOnMwsAsRoot(command) == 0) {
            return (getLastOutput());
        }
        return (null);
    }

    /**
     * Checks the r-state of cache path and latest version of media
     * 
     * @param cachePath
     * @param media
     * @param isoVer
     * @return boolean to download media or not
     */
    public boolean checkMediaToDownload(String cachePath, String media, String isoVer) {
        String alwaysCacheOssrc = (String) DataHandler.getAttribute("always_cache_ossrc");
        String alwaysCacheOm = (String) DataHandler.getAttribute("always_cache_om");

        if (verifyPathAvailabilityOnMws(cachePath) == 0) {
            if (media.toUpperCase().equals("SOLARIS")) {
                logger.info("$$$*** SKIPPED Downloading of " + media + " MEDIA  as it is already cached in mws with LATEST ERICJUMP Package ***$$$");
                return (true);
            }
            String rState = getVerOfCachePath(cachePath, media);
            if (rState == null) {
                logger.error("Unable to get rState of Cache path " + cachePath);
                return (false);
            }
            int rStateVer = convertVerToNum(rState);
            int latestVer = convertVerToNum(isoVer);
            if (rStateVer == latestVer) {
                if (media.toUpperCase().equals("OSSRC") && alwaysCacheOssrc != null && !alwaysCacheOssrc.isEmpty()
                        && alwaysCacheOssrc.toLowerCase().equals("true")) {
                    return (false);
                }

                if (media.toUpperCase().equals("OM") && alwaysCacheOm != null && !alwaysCacheOm.isEmpty()
                        && alwaysCacheOm.toLowerCase().equals("true")) {
                    return (false);
                }

                logger.info("$$$*** SKIPPED Downloading of " + media + " MEDIA since LATEST Verison " + isoVer + " is already cached in mws ***$$$");
                return (true);
            } else if (rStateVer > latestVer) {
                logger.info("$$$*** SKIPPED Downloading of  " + media + " MEDIA since CACHED R-STATE " + rState + " is  HIGHER than  PSV  R-STATE "
                        + isoVer + " in mws ***$$$");
                return (true);
            } else {
                logger.info("LATEST Cache path of " + media + " MEDIA exists but DOESNOT contain latest media Version " + isoVer + " it has "
                        + rState);
                return (false);
            }
        } else {
            logger.info("LATEST Cache path of " + media + " does not exists");
            return (false);
        }
    }

    /**
     * Eliminates the dots from a version
     * 
     * @param ver
     * @return integer by eliminating dots(.)
     */
    public int convertVerToNum(String ver) {
        if (!ver.equals("None")) {
            String command = "echo " + ver + " | sed 's/\\.//g'";
            runSingleBlockingCommandOnMwsAsRoot(command);
            return (Integer.parseInt(getLastOutput()));
        }
        return (0);
    }

    /**
     * Removes the latest cache path if the r-state is lesser than the latest
     * 
     * @param cachePath
     * @param media
     * @param isoVer
     * @return
     */

    public int checkAndRemoveCachePathOnMws(String cachePath, String media, String isoVer) {
        String alwaysCacheOssrc = (String) DataHandler.getAttribute("always_cache_ossrc");
        String alwaysCacheOm = (String) DataHandler.getAttribute("always_cache_om");

        // If not set , setting to false to prevent exception later.
        if (alwaysCacheOssrc == null)
            alwaysCacheOssrc = "false";
        if (alwaysCacheOm == null)
            alwaysCacheOm = "false";

        if (media.toUpperCase().equals("SOLARIS")) {
            return (0);
        }
        if (verifyPathAvailabilityOnMws(cachePath) == 0) {
            String rState = getVerOfCachePath(cachePath, media);
            if (rState == null) {
                logger.error("Unable to get rState of Cache path " + cachePath);
                return (1);
            }
            int rStateVer = convertVerToNum(rState);
            int latestVer = convertVerToNum(isoVer);
            logger.info("***  rStateVer is " + rStateVer + " ,   media id is " + media);

            // In some cases we want to force a re-cache of the OSSRC MEDIA
            if (media.toUpperCase().equals("OSSRC") && alwaysCacheOssrc.toLowerCase().equals("true")) {
                rStateVer = 100;
                logger.info("Always Cache OSSRC Media is set to TRUE");
            }

            // In some cases we want to force a re-cache of the OM MEDIA
            if (media.toUpperCase().equals("OM") && alwaysCacheOm.equals("true")) {
                rStateVer = 100;
                logger.info("Always Cache OM Media is set to TRUE");
            }

            if (rStateVer < latestVer) {
                logger.info("Removing the Cache path " + cachePath + " from mws");
                return (removePathOnMws(cachePath));
            }
        }
        logger.debug("Nothing to remove as expected Cache path is not present");
        return (0);
    }

    /**
     * @return boolean value
     */
    public boolean storageCheckOnMws() {
        if (makeDirOnMws(downloadPath) == 0) {
            String command = "df -k " + downloadPath + " | awk '{print $4}' | tail -1 | sed 's/G//g'";
            runSingleBlockingCommandOnMwsAsRoot(command);
            try {
                if (Double.parseDouble(getLastOutput()) >= 5242880) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
            return false;
        }
        return false;
    }

    /**
     * Run a rest call to download the media from proxy nexus.
     * 
     * @param shipment
     * @param psv
     * @param iso
     * @return exit value of wget command
     */
    public int downloadMediaFromNexus(String shipment, String psv, String iso) {
        int exitCode;
        TafConfiguration configuration = TafConfigurationProvider.provide();
        String downloadLoopDelay = (String) configuration.getProperty("testware.downloadLoopDelay");
        if (downloadLoopDelay == null) {
            downloadLoopDelay = "600000";
        } else {
            logger.info("Value of download loop delay is " + downloadLoopDelay);
        }
        int delaytime = Integer.parseInt(downloadLoopDelay);
        String url = getMediaUrl(shipment, psv, iso);
        if (iso.toUpperCase().equals("SOLARIS")) {
            url = url.replaceAll("Solaris", "solaris");
        }
        logger.info("Downloading the " + iso + " media from Nexus to " + downloadPath + " of mws");
        String command = WGET + " -q -P " + downloadPath + " --no-check-certificate --ignore-length " + url;
        exitCode = runSingleBlockingCommandOnMwsAsRoot(command);
        if (exitCode == 0) {
            return (exitCode);
        } else if (exitCode != 8) {
            logger.error("wget failed to download the " + iso + " media from Nexus to " + downloadPath + " of mws");
            return (exitCode);
        } else {
            for (int i = 1; i <= 18; i++) {
                exitCode = runSingleBlockingCommandOnMwsAsRoot(command);
                if (exitCode != 8) {
                    return (exitCode);
                } else {
                    putDelay(delaytime);
                }
            }
            return (exitCode);
        }
    }

    /**
     * Greps the link from the json file which we get from a latest psv restcall
     * 
     * @param shipment
     * @param psv
     * @param iso
     * @return URL of the media
     */
    public String getMediaUrl(String shipment, String psv, String iso) {
        logger.debug("Getting Media URL ");
        if (iso.toUpperCase().equals("OSSRC")) {
            iso = "OSS-RC | sed 's/oss/ossrc/'";
        }
        if (iso.toUpperCase().equals("COMINF")) {
            iso = "COMINF";
        }
        if (iso.toUpperCase().equals("OMSAS")) {
            iso = "Security";
        }
        if (iso.toUpperCase().equals("SOLARIS")) {
            iso = "solaris";
        }

        String command = WGET + " -q -O - --no-check-certificate --ignore-length  '" + CI_LINK + "/getProductSetVersionContents/?drop=" + shipment
                + "&productSet=OSS-RC&version=" + psv + "&pretty=true' | grep athloneUrl | grep ERIC" + iso + "  | sed 's/ //g' | cut -d\\\" -f4 | head -1";

        if (runSingleBlockingCommandOnMwsAsRoot(command) == 0) {
            String commandOutput = getLastOutput();
            return (commandOutput.trim());
        }
        return (null);

    }

    /**
     * Downloads the sha1 checksum of the respective media and gets the content
     * 
     * @param shipment
     * @param psv
     * @param iso
     * @return returns the content of sha1 checksum
     */
    public String getMediaSha1FromNexus(String shipment, String psv, String iso) {
        int exitCode;
        TafConfiguration configuration = TafConfigurationProvider.provide();
        String downloadLoopDelay = (String) configuration.getProperty("testware.downloadLoopDelay");
        if (downloadLoopDelay == null) {
            downloadLoopDelay = "600000";
        } else {
            logger.info("Value of download loop delay is " + downloadLoopDelay);
        }
        int delaytime = Integer.parseInt(downloadLoopDelay);
        logger.debug("Getting sha1 key from the nexus");
        String url = getMediaUrl(shipment, psv, iso) + ".sha1";
        if (iso.toUpperCase().equals("SOLARIS")) {
            url = url.replaceAll("Solaris", "solaris");
        }
        String command = WGET + " -q -O - --no-check-certificate --ignore-length " + url;
        exitCode = runSingleBlockingCommandOnMwsAsRoot(command);
        if (exitCode == 0) {
            return (getLastOutput());
        } else if (exitCode != 8) {
            logger.error("Unable to download SHA1 checksum from NEXUS");
            return ("");
        } else {
            for (int i = 1; i <= 18; i++) {
                exitCode = runSingleBlockingCommandOnMwsAsRoot(command);
                if (exitCode == 0) {
                    return (getLastOutput());
                } else if (exitCode != 8) {
                    return ("");
                } else {
                    putDelay(delaytime);
                }
            }
            return ("");
        }
    }

    /**
     * Runs digest command to get the checksum of media on mws.
     * 
     * @param mediaFile
     * @return returns checksum generated
     */

    public String getMediaSha1OnMws(String mediaFile) {
        logger.debug("Getting sha1 digest from the downloaded media under " + mediaFile);
        String command = "/usr/bin/digest -a sha1 " + downloadPath + mediaFile;
        if (runSingleBlockingCommandOnMwsAsRoot(command) != 0) {
            logger.error("Unable to digest sha1 checksum of " + mediaFile);
            return ("");
        }
        return (getLastOutput());
    }

    /**
     * Checks the media already exists on Downloadpath given
     * 
     * @param media
     * @param isoVer
     * @param shipment
     * @param psv
     * @return returns boolean wheather to download media or not
     */

    public boolean checkMediaAlreadyExists(String media, String isoVer, String shipment, String psv) {
        logger.info("Checking Latest " + media + " Media existance in download path before Downloading ");
        if (existenceOfMedia(media, isoVer) == 0) {
            logger.info("Found Latest " + media + " Media under download path");
            mediaFile = context.getAttribute("mediaFile");
            logger.info("Checking checksum of existing media");
            if (getMediaSha1FromNexus(shipment, psv, media).equals(getMediaSha1OnMws(mediaFile))) {
                logger.info("SKIPPING DOWNLOAD of " + media + " MEDIA as it is Already Dowloaded in download path " + downloadPath);
                return (true);
            } else {
                logger.info("Removing the " + media + " media as checksum didnot match");
                removePathOnMws(downloadPath + mediaFile);
                return (false);
            }
        }
        logger.info("Media does not exists in download path " + downloadPath);
        return (false);
    }

    /**
     * Gets the ERICjump version on latest mount path
     * 
     * @param directory
     * @return version of ERICjump
     */
    public String getERICjumpVer(String directory) {
        logger.info("Getting Version of ERICjump present in " + directory + "path");
        String command = "pkginfo -ld " + directory + "/omtools/eric_jumpstart/ERICjump.pkg | grep VERSION | awk '{print $2}'";
        if (runSingleBlockingCommandOnMwsAsRoot(command) == 0) {
            return (getLastOutput());
        }
        return (null);
    }

    /**
     * Gets the ERICjump version on mws
     * 
     * @return
     */

    public String getERICjumpVerOnMws() {
        logger.info("Getting version of deployed ERICjump pkg on MWS");
        return (getPkgInfoCmdVersion("ERICjump"));
    }

    /**
     * Runs manage_nfs_media.bsh script to cache the media
     * 
     * @param media
     * @param mountPath
     * @return returns exit code of the script
     */
    public int cacheMedia(String media, String mountPath, String mediaFile) {
        logger.info("Caching " + media + " media on MWs");
        if (media.toUpperCase().equals("SOLARIS")) {
            User rootUser = getMsHost().getUsers(UserType.ADMIN).get(0);
            String command = "/ericsson/autoinstall/bin/manage_install_service.bsh -a add -p " + downloadPath + mediaFile + " -N";
            int exit = runSingleBlockingCommandOnMwsAsRoot(command, false);
            if (exit != 0) {
                logger.error("Failed to cache media");
                logger.error(getLastOutput());
            }
            return exit;
        }
        if (media.toUpperCase().equals("COMINF")) {
            media = "COMINF_INSTALL";
        }
        String command = "/ericsson/autoinstall/bin/manage_nfs_media.bsh -a add -m " + media.toLowerCase() + " -N -p " + mountPath;
        int exit = runSingleBlockingCommandOnMwsAsRoot(command, false);
        if (exit != 0) {
            logger.error("Failed to cache media");
            logger.error(getLastOutput());
        }
        return exit;
    }

    /**
     * Add a link from the mediaCachePath to the cachePath
     * 
     * @param cachePath
     *            - where want to appear media is cached to
     * @param mediaCachePath
     *            - real location of cached media
     * @return
     */
    public int addMediaLink(String cachePath, String mediaCachePath) {
        String command = "ln -s " + mediaCachePath + " " + cachePath;
        return (runSingleBlockingCommandOnMwsAsRoot(command, false));
    }

    /**
     * 
     * @param host
     * @param user
     * @param engTarBall
     * @return
     */
    public boolean copySysIdFileToMWS(final Host host, final User user) {
        return sendFileRemotely(host, user, "sysid.cfg", "/tmp/");
    }

    /**
     * Runs umount command to the given path
     * 
     * @param mountPath
     * @return exit code of the command
     */
    public int umountMedia(String mountPath) {
        logger.info("Unmounting the path " + mountPath + " on Mws");
        runSingleBlockingCommandOnMwsAsRoot("umount " + mountPath);
        int umountExitCode = getLastExitCode();
        // if (getLastExitCode() != 0){
        // runSingleBlockingCommandOnMwsAsRoot("fuser -c " + mountPath);
        // logger.warn("SKIPPING umount of " + mountPath +
        // " :fuser command output is " + getLastOutput());
        // }
        return (umountExitCode);
    }

    /**
     * Deletes the lofi device on MWS
     * 
     * @param lofiDevice
     * @return exit value of the command
     */
    public int deleteLofiDevice(String lofiDevice) {
        logger.info("Removing lofi device " + lofiDevice + " from MWS");
        return (runSingleBlockingCommandOnMwsAsRoot("lofiadm -d " + lofiDevice));
    }

    /**
     * Deletes the download Path on MWS
     * 
     * @param downloadPath
     * @return exit value of the command
     */
    public int removeDownloadPath() {
        logger.info("Removing downloaded Path from MWS ");
        return (runSingleBlockingCommandOnMwsAsRoot("rm -rf " + downloadPath));
    }

    /**
     * Runs manage_nfs_media.bsh script to List the media
     * 
     * @param media
     * @param cachePath
     * @return returns exit code of the script
     */
    public int listMedia(String media, String cachePath) {
        logger.info("Listing  " + media + " media on MWs");
        if (media.toUpperCase().equals("SOLARIS")) {
            String command = "/ericsson/autoinstall/bin/manage_install_service.bsh -a list -p " + cachePath;
            return (runSingleBlockingCommandOnMwsAsRoot(command, false));
        }
        if (media.toUpperCase().equals("COMINF")) {
            media = "COMINF_INSTALL";
        }
        String command = "/ericsson/autoinstall/bin/manage_nfs_media.bsh -a list -m " + media.toLowerCase() + " -N -p " + cachePath;
        return (runSingleBlockingCommandOnMwsAsRoot(command, false));
    }

    /**
     * Runs manage_nfs_media.bsh script to remove the media
     * 
     * @param media
     * @param cachePath
     * @return returns exit code of the script
     */
    public int removeMedia(String media, String cachePath) {
        logger.info("Removing  " + media + " media on MWs");
        if (media.toUpperCase().equals("SOLARIS")) {
            String command = "/ericsson/autoinstall/bin/manage_install_service.bsh -a remove -p " + cachePath + " -N";
            return (runSingleBlockingCommandOnMwsAsRoot(command, false));
        }
        if (media.toUpperCase().equals("COMINF")) {
            media = "COMINF_INSTALL";
        }
        String command = "/ericsson/autoinstall/bin/manage_nfs_media.bsh -a remove -m " + media.toLowerCase() + " -N -p " + cachePath;
        return (runSingleBlockingCommandOnMwsAsRoot(command, false));
    }

    /**
     * Checks hp & Ericsson publishers on mws are not set
     * 
     * @param directory - mount directory of om media
     * @return returns exit value of the script
     */
    public int setPublishers(String cachePath, String mwsIP) {
        // Check publishers are unset
        String command = "pkg publisher ericsson";
        int exit = runSingleBlockingCommandOnMwsAsRoot(command);
        if (exit == 0) {
            logger.error("ericsson publisher unexpectedly set");
            logger.error(getLastOutput());
            return -1;
        } 
        // Check hp.com completely gone
        command = "pkg publisher hp.com";
        exit = runSingleBlockingCommandOnMwsAsRoot(command);
        if (exit == 0) {
            logger.error("hp.com publisher unexpectedly set");
            logger.error(getLastOutput());
            return -1;
        }
        return 0;
    }

    /**
     * Method Checks if an ERICrepo Tar file Path has been passed in as part of jenkins Param alternate_ERICrepo_tar_file.
     * 
     * @return String
     */
    public String checkForAlternateEricRepo() {
        String useAlternateEricRepoPath = (String) DataHandler.getAttribute("alternate_ERICrepo_tar_file");
        if (useAlternateEricRepoPath != null && !useAlternateEricRepoPath.isEmpty()) {
            logger.info("ERICrepo Test package supplied " + useAlternateEricRepoPath);
        }
        return (useAlternateEricRepoPath);

    }

    private int makeTestRepo() {
        logger.info("Making test location " + TEST_PKG);
        String Command = "mkdir " + TEST_PKG;
        return (runSingleBlockingCommandOnMwsAsRoot(Command));

    }

    /**
     * Untar Test repo on MWS in newly created test directory
     * 
     * @param String installPath
     * @return int ,exit value of the cmd
     */
    public int untarRepo(String installPath, String altRepoFile) {
        logger.info("Untar ERICrepo    ");

        makeTestRepo();
        String Command = "cd " + TEST_PKG +";tar zxvf " + installPath + "/" + altRepoFile;
        return (runSingleBlockingCommandOnMwsAsRoot(Command));

    }

    /**
     * Install ERICautoinstall package from OM media.
     * 
     * @param directory- mount directory of om media
     * @return returns exit value of the script
     */
    public int installERICautoinstall(String mountPath) {
        logger.info("Installing OM's ERICautoinstall package on mws");
        logger.info("DIRECTORY IS   " + mountPath);
        String command = mountPath + "/omtools/upgrade_om.bsh -a mws -p " + mountPath + " -x ERICautoinstall";

        int exit = runSingleBlockingCommandOnMwsAsRoot(command, false);
        logger.info(getLastOutput());
        if (exit != 0) {
            logger.error("Failed to install ERICautoinstall");
        }
        return exit;
    }

    /**
     * Install EricAutoinstall on MWS using test pkg of ERICrepo
     * 
     * @param String installPath
     * @return int ,exit value of the cmd
     */
    public int installEricAutoinstall() {

        logger.info("ERICREPO_TEST_PKG is: " + TEST_PKG);
        String cmd = "pkg info ERICautoinstall";
        int exit = runSingleBlockingCommandOnMwsAsRoot(cmd);
        if (exit == 0) {
            logger.info("Uninstall ERICautoinstall");
            exit = runSingleBlockingCommandOnMwsAsRoot("pkg uninstall ERICautoinstall");
            if (exit != 0) {
                logger.error("Failed to uninstall ERICautoinstall");
                logger.error(getLastOutput());
            }
        } else {
            logger.info("No ERICautoinstall to uninstall");
        }

        cmd = "pkg install -g " + TEST_PKG + "/ERICrepo/ ERICautoinstall && pkg unset-publisher ericsson ";
        logger.info("Installing ERICautoinstall pkg on MWS" + cmd);
        exit = runSingleBlockingCommandOnMwsAsRoot(cmd);
        if (exit != 0) {
            logger.error("Failed to install ERICautoinstall");
            logger.error(getLastOutput());
        }
        return exit;
    }

    /**
     * Method Lists EricAi tar file from filepath on MWS ieatloaner156-1 
     * 
     * @param altEricRepoFile fullpath EricRepo Tar file to list
     * @return int
     */
    public int listAlternateTarFile(String altEricRepoFile) {
        logger.info("Test PACKAGE IS:  " + altEricRepoFile);
        String cmd = "ls " + TEST_DROP_AREA + altEricRepoFile;
        return runSingleBlockingCommandOnMwsAsRoot(cmd);
    }
    
    /**
     * Method Copies EricAi tar file from filepath on MWS ieatloaner156-1 to new Installed MWS.
     * 
     * @param altEricRepoFile fullpath EricRepo Tar file to copy
     * @param installPath- location to place ERICrepo Tar file in
     * @return int
     */
    public int copyAlternateTarFile(String altEricRepoFile, String installPath) {
        logger.info("Install path is:  " + installPath);
        logger.info("Test PACKAGE IS:  " + altEricRepoFile);
        String cmd = "cp " + TEST_DROP_AREA + altEricRepoFile + " " + installPath;
        return runSingleBlockingCommandOnMwsAsRoot(cmd);
    }
    
    /**
     * Removes test package.
     * 
     * @param altEricRepoFile fullpath EricRepo Tar file to remove
     * @return int
     */
    public int removeAlternateTarFile(String altEricRepoFile) {
        logger.info("Test PACKAGE IS:  " + altEricRepoFile);
        String cmd = "rm -f " + TEST_DROP_AREA + altEricRepoFile;
        return runSingleBlockingCommandOnMwsAsRoot(cmd);
    }

    /**
     * Remove ERICrepo from cached OM Media
     * 
     * @param mwsIP
     * @return exit code of the command
     */
    public int removeOMRepo(String cachePath, String repo) {
        logger.info("Removing " + repo + " from " + cachePath + "OM now");

        String command = "rm -rf " + cachePath + "/om/"+ repo + "/ ";
        return (runSingleBlockingCommandOnMwsAsRoot(command));

    }

    /**
     * Add test ERICrepo to cached OM Media
     * 
     * @param mwsIP
     * @return exit code of the command
     */
    public int addOMRepo(String cachePath, String repo) {
        logger.info("Adding " + repo + " test Repo to " + cachePath + " OM now");
        String command = "mv " + TEST_PKG + "/" + repo + "/ " + cachePath + "/om/ ";
        return (runSingleBlockingCommandOnMwsAsRoot(command));
    }
    
    /**
     * Install additional software from mounted OM media
     * 
     * @param
     * @return exit code of the command
     */
    public int installOMpackages(String mountPath) {
        logger.info("Adding additional software from OM media now");
        String command = mountPath + "/omtools/upgrade_om.bsh -p " + mountPath + " -a mws -x ERICautoinstall";
        int exit = runSingleBlockingCommandOnMwsAsRoot(command);
        if (exit != 0) {
            logger.error("Failed to install additional software");
            logger.error(getLastOutput());
        }
        return exit;

    }
    
    /**
     * Modify list of software packages to install
     * TEMPORARY STEP - as OM Media has packages that cannot be upgraded
     * 
     * @param
     * @return exit code of the command
     */
    public int modifyOMSoftwareList(String mountPath) {
        /*
        logger.info("Removing ERICmonsrv from software to install");
        String command =  "cp " + mountPath + "/omtools/om_software /var/tmp/.om_software; grep -v selfmon:ERICmonsrv.pkg.7z:mws /var/tmp/.om_software | grep -v selfmon:ERICmonagt.pkg.7z:all | grep -v selfmon:ERICmonplugin.pkg.7z:all > " + mountPath + "/omtools/om_software";
        
        int exit = runSingleBlockingCommandOnMwsAsRoot(command);
        if (exit != 0) {
            logger.error("Failed to modify om_software");
            logger.error(getLastOutput());
        }
        return exit;*/
        logger.info("No OM Software to modfiy currently");
        return 0;

    }
    
    /**
     * Returns name of SRU to overlay, or null if not required
    * @return
     */
    public String getOverlaySRU() {

        String alternateSRU = (String) DataHandler.getAttribute("alternate_SRU");
        if (alternateSRU != null && !alternateSRU.isEmpty()) {
            logger.info("Look for alternate_SRU " + alternateSRU);
            if (listAlternateTarFile(alternateSRU)==0) {
                return alternateSRU;
            }
        }

        return null;
    }
    
    /**
     * Overlays SRU to cachePath and installs it
     * @param cachePath - OM media cache path
     * @return
     */
    public int installSRU(String cachePath) {
        String cmd = cachePath + "/om/SRU/install-repo.ksh -d /var/share/pkg/repositories/solaris -s " + cachePath + "/om/SRU -y"; 
        int exit = runSingleBlockingCommandOnMwsAsRoot(cmd);
        if (exit != 0) {
            logger.error("Failed to update SRU");
            logger.error(getLastOutput());
        }
        return exit;
    }
}
