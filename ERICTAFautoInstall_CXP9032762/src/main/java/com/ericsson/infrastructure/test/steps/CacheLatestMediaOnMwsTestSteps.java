package com.ericsson.infrastructure.test.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TafTestContext;
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.configuration.TafConfiguration;
import com.ericsson.cifwk.taf.configuration.TafConfigurationProvider;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.infrastructure.test.operators.CacheLatestMediaOperator;
import com.google.inject.Inject;

public class CacheLatestMediaOnMwsTestSteps extends TorTestCaseHelper {

    final static Logger logger = LoggerFactory.getLogger(CacheLatestMediaOnMwsTestSteps.class);

    TestContext context = TafTestContext.getContext();
    @Inject
    private CacheLatestMediaOperator cacheOperator;

    public static String revision = "";
    public static final String INSTALL_PATH = "/var/tmp/";
    public static final String ALREADY_CACHED = "Already_cached";
    public static String altEricRepoFile = "";

    private static final String ISO_CACHE_SUFFIX = "ISOCachePath";

    /**
     * Check if the latest media on Mws
     */
    @TestStep(id = "checkCachedMediaOnMws")
    public void checkCachedMediaOnMws(@Input("media") String media) {

        setTestStep("checkCachedMediaOnMws");
        //Get the shipment details from the jenkins job
        String shipment = cacheOperator.getShipment();
        assertNotNull("Unable to find Shipment details", shipment);
        DataHandler.setAttribute("shipment", shipment);

        //Get the mode of the run
        String mode = cacheOperator.getMode();
        assertNotNull("Unable to find the mode of the RUN ", mode);

        // Get the product set version for given shipment
        String psv = cacheOperator.getPsv(shipment, mode);
        assertNotNull("Unable to get Product set version", psv);
        DataHandler.setAttribute("psv", psv);

        // Get the shipment of the media with which they are built
        String builtShipment = cacheOperator.getShipmentOfPsvMedia(psv, shipment, media);
        assertNotNull("Unable to get the built shipment of Latest Media", builtShipment);
        DataHandler.setAttribute("builtShipment", builtShipment);
        
        // Get cache path
        
        //Get expected cache path w.r.t shipment and media
        String mediaCachePath = cacheOperator.getMediaExpectedCachePath(media, builtShipment);
        assertNotNull("Unable to get the expected Cache Path", mediaCachePath);

        String cachePath = cacheOperator.getDestinationCachePath(media, mediaCachePath, shipment);
        DataHandler.setAttribute(media.toLowerCase() + CacheLatestMediaOperator.CACHE_SUFFIX, cachePath);

        if (!cachePath.equals(mediaCachePath)) {
            // Set ISO_CACHE_PATH for when built-for and dest shipment are not the same
            logger.info("Setting ISO_CACHE_PATH as built-for and dest shipment don't match");
            DataHandler.setAttribute(media.toLowerCase() + ISO_CACHE_SUFFIX, mediaCachePath);
        }

        // Get ISO version of media
        String isoVer = cacheOperator.getISOver(psv, media, shipment);
        assertEquals("Unable to get ISO version of " + media + " media from Nexus", cacheOperator.getLastExitCode(), 0);
        DataHandler.setAttribute(media.toLowerCase() + "IsoVer", isoVer);

        //Check cache path available with latest media on mws
        logger.info("MEDIA CACHE PATH IS.............." + mediaCachePath);
        boolean already_cached = cacheOperator.checkMediaToDownload(mediaCachePath, media, isoVer); //does that
        assertNotNull("Unable to Check media to Download or not", already_cached);
        context.setAttribute(media.toLowerCase() + ALREADY_CACHED, already_cached);

        // Remove the existing cache path if it has lower r-state
        int exitcode = cacheOperator.checkAndRemoveCachePathOnMws(mediaCachePath, media, isoVer);
        assertEquals("Unable to remove the path " + cachePath + " from mws", exitcode, 0);

    }

    /**
     * Check Cache Path
     */
    
    @TestStep(id = "checkCachePath")
    public void checkCachePath(@Input("media") String media) {

        String builtShipment = (String) DataHandler.getAttribute("builtShipment");
        String shipment = (String) DataHandler.getAttribute("shipment");
        String mediaCachePath = cacheOperator.getMediaExpectedCachePath(media, builtShipment);
        DataHandler.setAttribute("mediaCachePath", mediaCachePath);
        String cachePath = cacheOperator.getDestinationCachePath(media, mediaCachePath, shipment);
        DataHandler.setAttribute(media.toLowerCase() + CacheLatestMediaOperator.CACHE_SUFFIX, cachePath);
        assertNotNull("Unable to get the expected Cache Path", mediaCachePath);

    }

    /**
     * Download Latest Media from NEXUS
     */
    @TestStep(id = "downloadLatestMedia")
    public void downloadLatestMedia(@Input("media") String media) {
        setTestStep("downloadLatestMedia");
        boolean already_cached = context.getAttribute(media.toLowerCase() + ALREADY_CACHED);
        String psv = (String) DataHandler.getAttribute("psv");
        String isoVer = (String) DataHandler.getAttribute(media.toLowerCase() + "IsoVer");
        String shipment = cacheOperator.getShipment();

        if (!already_cached) {
            //Check the media already existed in download Path
            boolean already_downloaded = cacheOperator.checkMediaAlreadyExists(media, isoVer, shipment, psv);
            if (!already_downloaded) {
                assertTrue("Unable to Download " + media + " media to MWS as downloadPath has low storage", cacheOperator.storageCheckOnMws());
                //Downloading the latest media from nexus	
                assertEquals("Unable to Downloaded media in download path of mws", cacheOperator.downloadMediaFromNexus(shipment, psv, media), 0);
                assertEquals("Unable to find the Downloaded media in download path of mws", cacheOperator.existenceOfMedia(media, isoVer), 0);
                String mediaFile = context.getAttribute("mediaFile");
                //Sha1 checksum of download media			
                assertEquals("Unable to Download " + media + " media", cacheOperator.getMediaSha1OnMws(mediaFile),
                        cacheOperator.getMediaSha1FromNexus(shipment, psv, media));
            }

        }

    }

    /**
     * Mount the Downloaded media
     */
    @TestStep(id = "mountMedia")
    public void mountMedia(@Input("media") String media) {
        setTestStep("mountMedia");
        if (media.equals("SOLARIS")) {
            logger.info("Not required for Solaris Media");
        } else {
            String mountPath = "/tmp/temp_" + media.toLowerCase() + "_iso";
            DataHandler.setAttribute("mountPath", mountPath);
            boolean already_cached = context.getAttribute(media.toLowerCase() + ALREADY_CACHED);
            if (!already_cached) {
                String mediaFile = context.getAttribute("mediaFile");
                assertNotNull(mediaFile);
                String isoVer = (String) DataHandler.getAttribute(media.toLowerCase() + "IsoVer");
                assertNotNull(isoVer);

                //Getting lofi device
                String lofiDevice = cacheOperator.getLofiadmDevice(mediaFile, isoVer);
                DataHandler.setAttribute("lofiDevice", lofiDevice);
                assertEquals("Unable to get lofi device", cacheOperator.getLastExitCode(), 0);

                //Mounting OM media to /tmp/temp_om_iso directory
                assertEquals("Unable to mount the media", cacheOperator.mountMedia(lofiDevice, mountPath), 0);
            } else {
                logger.info("SKIPPED mounting of " + media + "MEDIA");
            }
        }

    }

    /**
     * Upgrading ERICjump Package from the latest mount media
     */
    /*
    @TestStep(id = "upgradeERICjump")
    public void upgradeERICjump(@Input("media") String media) {

        setTestStep("upgradeERICjump");
        String mountPath = (String) DataHandler.getAttribute("mountPath");
        if (media.equals("OM")) {
            boolean already_cached = context.getAttribute(media.toLowerCase() + ALREADY_CACHED);
            
            String actualVer;
            if (!already_cached) {
                //Upgrade the package from mount path
                assertEquals("Unable to upgrade ERICjump package", cacheOperator.upgradeERICjump(mountPath), 0);
                actualVer = cacheOperator.getERICjumpVer(mountPath);

            } else {
                //Upgrade the package from cached path
                String cachePath = (String) DataHandler.getAttribute(CacheLatestMediaOperator.OM_CACHE);
                assertEquals("Unable to upgrade ERICjump package", cacheOperator.upgradeERICjump(cachePath + "/om"), 0);
                actualVer = cacheOperator.getERICjumpVer(cachePath + "/om");
            }
            int cacheNum = Integer.parseInt(cacheOperator.convertRstateToVersion(actualVer).replace(".", ""));
            int installNum = Integer.parseInt(cacheOperator.convertRstateToVersion(cacheOperator.getERICjumpVerOnMws()).replace(".", ""));
            if (installNum > cacheNum) {
                logger.info("Upgrade of ERICJump Skipped as mws has latest Version");
            } else {
                assertEquals("Version Mismatch of ERICjump package", cacheOperator.getERICjumpVerOnMws(), actualVer);
            }

        }
    }*/

    /**
     * Cache the media on MWS
     */
    @TestStep(id = "cacheMediaOnMws")
    public void cacheMediaOnMws(@Input("media") String media) {
        setTestStep("cacheMediaOnMws");
        String mountPath = (String) DataHandler.getAttribute("mountPath");
        String mediaFile = context.getAttribute("mediaFile");

        //Checking the cache path on mws mediaCachePath
        String cachePath = (String) DataHandler.getAttribute(media.toLowerCase() + CacheLatestMediaOperator.CACHE_SUFFIX);
        String mediaCachePath = (String) DataHandler.getAttribute(media.toLowerCase() + ISO_CACHE_SUFFIX);
        logger.info("MediaCachePath is: " + mediaCachePath);

        //Caching media on Mws
        boolean already_cached = context.getAttribute(media.toLowerCase() + ALREADY_CACHED);
        if (!already_cached) {
            assertEquals("Unable to cache " + media + " Media on MWS", cacheOperator.cacheMedia(media, mountPath, mediaFile), 0);

            if (mediaCachePath == null) {
                logger.info("Media cache and dest shipment cache path are the same");
                assertEquals("Unable to cache " + media + " Media on MWs", cacheOperator.verifyPathAvailabilityOnMws(cachePath), 0);
            } else {
                logger.info("Media cache and dest shipment cache path differ");
                assertEquals("Unable to cache " + media + " Media on MWs", cacheOperator.verifyPathAvailabilityOnMws(mediaCachePath), 0);
            }
        } else {
            logger.info("SKIPPED caching of " + media + "MEDIA");
            
        }
        // Add a link if needed from cachePath to mediaCachePath
        if (mediaCachePath != null) {
            assertEquals("Unable to delete target link cached media", cacheOperator.removePathOnMws(cachePath), 0);
            assertEquals("Unable to link cached media to shipment media", cacheOperator.addMediaLink(cachePath, mediaCachePath), 0);
            assertEquals("Unable to cache " + media + " Media on MWs", cacheOperator.verifyPathAvailabilityOnMws(cachePath), 0);
        }

    }

    /**
     * Umount the media on MWS
     * 
     */
    @TestStep(id = "umountMediaOnMws")
    public void umountMediaOnMws(@Input("media") String media) {
        setTestStep("umountMediaOnMws");
        String mountPath = (String) DataHandler.getAttribute("mountPath");
        if (media.equals("SOLARIS")) {
            logger.info("umountMediaOnMWS is not required for: " +media );
        }
        else {
        //unmounting the media on mws
        boolean already_cached = context.getAttribute(media.toLowerCase() + ALREADY_CACHED);

        if (!already_cached) {
            assertEquals("Unable to unmount the media path " + mountPath + "on mws", cacheOperator.umountMedia(mountPath), 0);
            assertEquals("Unable to remove the media path " + mountPath + "on mws", cacheOperator.removePathOnMws(mountPath), 0);

            //Removing lofiadm from mws
            String lofiDevice = (String) DataHandler.getAttribute("lofiDevice");
            assertEquals("Unable to delete the lofidevice on mws", cacheOperator.deleteLofiDevice(lofiDevice), 0);
            //Removing media which is downloaded 
            assertEquals("Unable to remove download Path from MWS", cacheOperator.removeDownloadPath(), 0);
        }
        }
    }

    /**
     * Install ERICautoinstall Package 
     * if Jenkins param altEricRepoFile is set - Install from Test ERICRepo
     * if no Jenkins param set - Install from ERICrepo on OM temp mount
     */
    @TestStep(id = "installERICautoinstall")
    public void installERICautoinstall(@Input("media") String media) {
        setTestStep("installERICautoinstall");
        String mountPath = (String) DataHandler.getAttribute("mountPath");

        if (media.equals("OM")) {

            if (altEricRepoFile != null && !altEricRepoFile.isEmpty() ) {
                //Install ERICautoinstall from ERICrepo test package using installEricAutoinstall method
                logger.info("Copying " + altEricRepoFile + " to " + INSTALL_PATH);
                assertEquals("ERICRepo Tar file has not copied properly to /var/tmp",
                        cacheOperator.copyAlternateTarFile(altEricRepoFile, INSTALL_PATH), 0);
                assertEquals("Unable to untar ERICRepo", cacheOperator.untarRepo(INSTALL_PATH, altEricRepoFile), 0);
                
                assertEquals("Unable to install ERICautoinstall test Packge to MWS", cacheOperator.installEricAutoinstall(), 0);
                assertEquals("ERICRepo Tar file has not copied properly to /var/tmp",
                        cacheOperator.removeAlternateTarFile(altEricRepoFile), 0);
              
            } else {
                // DataHandler.setAttribute("mountPath", mountPath);
                boolean already_cached = context.getAttribute(media.toLowerCase() + ALREADY_CACHED);
                if (!already_cached) {
                    //Install the package from mount path of OM media using installERICautoinstall method
                    logger.info("InstallERICauointstall from the mount path");
                    assertEquals("Unable to Install ERICautoinstall package", cacheOperator.installERICautoinstall(mountPath), 0);
                } else {
                    logger.info("Not required as " + media + "is already cached");
                }
            }
        } else {
            logger.info("This step is not required for " + media + "MEDIA");
        }
    }

    /**
     * Set ericsson and HP publishers on mws using ERICrepo and HPrepo from cached OM
     */
    @TestStep(id = "setPublishers")
    public void setPublishers(@Input("media") String media) {
        setTestStep("setPublishers");
        String cachePath = (String) DataHandler.getAttribute(media.toLowerCase() + CacheLatestMediaOperator.CACHE_SUFFIX);
        logger.info("cachePath is this:::" + cachePath);
        if (media.equals("OM")) {
            String mwsIP = cacheOperator.getMWSip();
            DataHandler.setAttribute("mwsIP", mwsIP);
            logger.info("MWS IP IS: " + mwsIP);
            assertNotNull(mwsIP);
            assertEquals("Unable to set publishers", cacheOperator.setPublishers(cachePath, mwsIP), 0);
        } else
            logger.info("Publishers have been set for OM Media. Not required for " + media);
    }

    /**
     *  This Step Checks for ERICrepo Test package parameter set in Jenkins
     *  Parameter in Jenkins: alternate_ERICrepo_tar_file 
     */   
    @TestStep(id = "CheckForAlternativeEricRepo")
    public void checkForAlternativeEricRepo() {
        setTestStep("CheckForAlternativeEricRepo");
        TafConfiguration configuration = TafConfigurationProvider.provide();
        revision = (String) configuration.getProperty("testware.revision");
        altEricRepoFile = cacheOperator.checkForAlternateEricRepo();

        if (altEricRepoFile != null && !altEricRepoFile.isEmpty()) {
            int repoExists = cacheOperator.listAlternateTarFile(altEricRepoFile);
            if (repoExists == 0) {
                logger.info("Using alternate ERICrepo file" + altEricRepoFile);
            } else {
                assertEquals("Failed to check if alternate tar is present", repoExists, 2);
                logger.info("No alternate ERICrepo file present, so use standard");
                altEricRepoFile = null;
            }
            logger.info("Using alternate ERICrepo file" + altEricRepoFile);
        } else {
            logger.info("No altEricRepoFile param set in Jenkins");
        }
    }

    /**
     *  This step updates the cached OM media with ERICrepo Test package 
     */
    @TestStep(id = "UpdateERICrepoOnOM")
    public void UpdateERICrepoOnOM(@Input("media") String media) {
        setTestStep("UpdateERICrepoOnOM");
        String cachePath = (String) DataHandler.getAttribute(media.toLowerCase() + CacheLatestMediaOperator.CACHE_SUFFIX);

        if (altEricRepoFile != null && !altEricRepoFile.isEmpty()) {
            if (media.equals("OM")) {
                //remove ERICai from cached om media
                assertEquals("Unable to remove ERICautoinstall from OM MEDIA", cacheOperator.removeOMRepo(cachePath, "ERICrepo"), 0);
                //Add test pkg ERICai to cached om media
                assertEquals("Unable to Add ERICautoinstall to OM MEDIA", cacheOperator.addOMRepo(cachePath, "ERICrepo"), 0);
            } else
                logger.info("Test Step only required for Test Package on OM Media");
        } else
            logger.info("No altEricRepoFile param set in Jenkins");
    }
    
    /**
     *  This step updates the cached OM media with ERICrepo Test package 
     */
    @TestStep(id = "overlaySRU")
    public void overlaySRU(@Input("media") String media) {
        setTestStep("overlaySRU");
        String cachePath = (String) DataHandler.getAttribute(media.toLowerCase() + CacheLatestMediaOperator.CACHE_SUFFIX);

        if (media.equals("OM")) {
            String alternateSRU = cacheOperator.getOverlaySRU();
            if (alternateSRU != null) {
                assertEquals("SRU Tar file has not copied properly to /var/tmp",
                        cacheOperator.copyAlternateTarFile(alternateSRU, INSTALL_PATH), 0);
                assertEquals("Unable to untar SRU", cacheOperator.untarRepo(INSTALL_PATH, alternateSRU), 0);
                
                assertEquals("Unable to remove old SRU from OM MEDIA", cacheOperator.removeOMRepo(cachePath, "SRU"), 0);
                //Add test pkg ERICai to cached om media
                assertEquals("Unable to Add SRU to OM MEDIA", cacheOperator.addOMRepo(cachePath, "SRU"), 0);
                assertEquals("Failed to Overlay SRU", cacheOperator.installSRU(cachePath), 0);  

            } else {
                logger.info("No alt SRU param set in Jenkins");
            }
        } else {
            logger.info("Test Step only required for Test Package on OM Media");
        }
    }
    
    /**
     * This step installs additional software from the mounted OM media
     */
    @TestStep(id = "installAdditionalSoftware")
    public void installAdditionalSoftware(@Input("media") String media) {
        setTestStep("installAdditionalSoftware");
        String cachePath = (String) DataHandler.getAttribute(media.toLowerCase() + CacheLatestMediaOperator.CACHE_SUFFIX);
        String mountPath = (String) DataHandler.getAttribute("mountPath");

        if (media.equals("OM")) {
            //Install additional packages onto MWS from om media
            cachePath = cachePath + "/om";
            assertEquals("Unable to install additional packages from OM MEDIA", cacheOperator.installOMpackages(cachePath), 0);

        } else {
            logger.info("Test Step only required for OM Media");
        }

    }
    
    @TestStep(id = "modifyOMSoftwareList")
    public void modifyOMSoftwareList(@Input("media") String media) {
        setTestStep("modifyOMSoftwareList");
        String cachePath = (String) DataHandler.getAttribute(media.toLowerCase() + CacheLatestMediaOperator.CACHE_SUFFIX);
       
        if (media.equals("OM")) {
            cachePath = cachePath + "/om";
            //Install additional packages onto MWS from om media
            assertEquals("Unable to modify OM SoftwareList from OM MEDIA", cacheOperator.modifyOMSoftwareList(cachePath), 0);

        } else {
            logger.info("Test Step only required for OM Media");
        }

    }
    

}
