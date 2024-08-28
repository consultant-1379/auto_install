package com.ericsson.infrastructure.test.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TafTestContext;
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.infrastructure.test.operators.CacheLatestMediaOperator;
import com.google.inject.Inject;



public class RemoveMediaOnMwsTestSteps extends TorTestCaseHelper{
	
	final static Logger logger = LoggerFactory.getLogger(RemoveMediaOnMwsTestSteps.class);
	


    
	TestContext context = TafTestContext.getContext();
    @Inject
    private CacheLatestMediaOperator cacheOperator;
    

    private static final String ISO_CACHE_SUFFIX = "ISOCachePath";
    
    
    /**
	 * Check if the latest media on Mws
	 */
    @TestStep(id="getListedMediaOnMws")
    public void getListedMediaOnMws(@Input("media") String media)
    {
    	
    	setTestStep("listMediaOnMws");
    	//Get the shipment details from the jenkins job
    	String shipment = cacheOperator.getShipment();
    	assertNotNull("Unable to find Media details", shipment);
    	
    	//Get the mode of the run
    	String mode = cacheOperator.getMode();
    	assertNotNull("Unable to find the mode of the RUN ", mode);
    	
    	// Get the product set version for given shipment
    	String psv = cacheOperator.getPsv(shipment,mode);
    	assertNotNull("Unable to get Product set version", psv);
    	DataHandler.setAttribute("psv", psv);
    	
    	// Get the shipment of the media with which they are built
    	String builtShipment = cacheOperator.getShipmentOfPsvMedia(psv, shipment, media);
    	assertNotNull("Unable to get the built shipment of Latest Media",builtShipment);
    	
    	//Get expected cache path w.r.t shipment and media
    	String mediaCachePath = cacheOperator.getMediaExpectedCachePath(media,builtShipment);
    	assertNotNull("Unable to get the expected Cache Path", mediaCachePath);
    	
    	
    	String cachePath = cacheOperator.getDestinationCachePath(media, mediaCachePath, shipment);
    	DataHandler.setAttribute(media.toLowerCase() + CacheLatestMediaOperator.CACHE_SUFFIX, cachePath);
    	
    	if (!cachePath.equals(mediaCachePath)) {
    	    // Set ISO_CACHE_PATH for when built-for and destination shipment are not the same
    	    logger.info("Setting ISO_CACHE_PATH as built-for and dest shipment don't match");
    	    DataHandler.setAttribute(media.toLowerCase() + ISO_CACHE_SUFFIX, mediaCachePath);
    	}
  
    	// Get ISO version of media
    	String isoVer = cacheOperator.getISOver(psv, media, shipment);
    	assertEquals("Unable to get ISO version of "+ media + " media from Nexus",cacheOperator.getLastExitCode(),0);
    	DataHandler.setAttribute(media.toLowerCase() +"IsoVer", isoVer);
    	
    }

	/**
	 * Remove the media on MWS
	 */
	@TestStep(id="removeMediaOnMws")
	public void removeMediaOnMws(@Input("media") String media)
	{
		setTestStep("removeMediaOnMws");
		
        //Checking the cache path on mws
        String cachePath = (String)DataHandler.getAttribute(media.toLowerCase() + CacheLatestMediaOperator.CACHE_SUFFIX);
                logger.info("Removing Media");
        assertEquals("Unable to remove " + media + " Media on MWs", cacheOperator.removeMedia(media, cachePath),0);	
	}
	/**
	 * Negative Test Remove the media on MWS
	 */
	@TestStep(id="removeNoMediaOnMws")
	public void removeNoMediaOnMws(@Input("media") String media)
	{
		setTestStep("removeNoMediaOnMws");
		
        //Checking the cache path on mws
        String cachePath = (String)DataHandler.getAttribute(media.toLowerCase() + CacheLatestMediaOperator.CACHE_SUFFIX);
                logger.info("Removing Media (negative)");
        assertEquals("Unable to remove " + media + " Media on MWs", cacheOperator.removeMedia(media, cachePath),1);	
	}
	
	/**
	 * Negative Test List the media on MWS
	 */
	@TestStep(id="listNoMediaOnMws")
	public void listNoMediaOnMws(@Input("media") String media)
	{
		setTestStep("listNoMediaOnMws");
		
        //Checking the cache path on mws
        String cachePath = (String)DataHandler.getAttribute(media.toLowerCase() + CacheLatestMediaOperator.CACHE_SUFFIX);
                logger.info("Listing Media (Negative)");
        assertEquals("Unable to list " + media + " Media on MWs", cacheOperator.listMedia(media, cachePath),1);
	
	}
	
}
