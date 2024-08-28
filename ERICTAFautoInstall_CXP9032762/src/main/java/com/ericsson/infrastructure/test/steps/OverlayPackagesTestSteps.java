package com.ericsson.infrastructure.test.steps;

import java.util.List;
import java.util.Arrays;

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
import com.ericsson.infrastructure.test.operators.OverlayPackagesOperator;
import com.google.inject.Inject;

public class OverlayPackagesTestSteps extends TorTestCaseHelper {

    final static Logger logger = LoggerFactory.getLogger(OverlayPackagesTestSteps.class);

    TestContext context = TafTestContext.getContext();
    @Inject
    private OverlayPackagesOperator overlayPackagesOperator;

    public static String revision = "";
    public static final String INSTALL_PATH = "/var/tmp/";

    
    /**
     *      *  This step updates the cached OM media with Packages Test package
     *           */
        @TestStep(id = "overlayPackages")
        public void overlayPackages(@Input("media") String media) {
            setTestStep("overlayPackages");
            String cachePath = (String) DataHandler.getAttribute(media.toLowerCase() + OverlayPackagesOperator.CACHE_SUFFIX);

         List<String> listOfOSSRCPackages = Arrays.asList(overlayPackagesOperator.alternatePkgList("ossrc"));
                     

     if (media.equals("OSSRC")) {
                for (int i=0;i < listOfOSSRCPackages.size();i++)
                {

                if (listOfOSSRCPackages.get(i) != null) {
int fileExists = overlayPackagesOperator.listAlternateTarFile(listOfOSSRCPackages.get(i),"OSSRC");
     if (fileExists != 0) {
logger.info(listOfOSSRCPackages.get(i)+" Package is not present in the TestDropArea");
         continue;
     }
 if (checkForRstateOfPackage(listOfOSSRCPackages.get(i),cachePath)) {                

assertEquals(listOfOSSRCPackages.get(i)+"file has not copied properly to /var/tmp",
                          overlayPackagesOperator.copyAlternateMediaList(listOfOSSRCPackages.get(i), INSTALL_PATH, "OSSRC"), 0);

                  assertEquals("Unable to remove old "+ listOfOSSRCPackages.get(i) +" from OSSRC MEDIA", overlayPackagesOperator.removePkg(cachePath,listOfOSSRCPackages.get(i) ), 0);
                                    assertEquals("Unable to Add this pkg to OSSRC MEDIA", overlayPackagesOperator.addPkg(cachePath, listOfOSSRCPackages.get(i),INSTALL_PATH), 0);
                    //                               // assertEquals("Failed to Overlay SRU", cacheOperator.installSRU(cachePath), 0);
checkForUpgrade(listOfOSSRCPackages.get(i),cachePath);        
}
 else {
                    logger.info("The package in the media area is the latest available");
                }

}
     else {
                    logger.info("No parameter set for this package");
                }
    }
    
}
else 
logger.info("The media is not OSSRC but "+media);
    }

  





/**
 *          *      *  This step updates the cached OM media with All the tar files 
 *                   *           */
        @TestStep(id = "overlayTarFiles")
        public void overlayTarFiles(@Input("media") String media) {
        	setTestStep("overlayTarFiles");
        	String cachePath = (String) DataHandler.getAttribute(media.toLowerCase() + overlayPackagesOperator.CACHE_SUFFIX);
// Later this statement will be out of the if clause as now we are overlaying for all media typ
List<String> listOfOMTars = Arrays.asList(overlayPackagesOperator.alternatePkgList("om"));        
       int forLoopExecuted = 0; 
	if (media.equals("OM")) {
for (int i=0;i < listOfOMTars.size();i++)
        		{
        			if (listOfOMTars.get(i) != null) {
 int fileExists = overlayPackagesOperator.listAlternateTarFile(listOfOMTars.get(i),"OM");
     if (fileExists != 0) {
logger.info(listOfOMTars.get(i)+"Tar is not present in the TestDropArea");    	 
    	 continue;
     }
assertEquals(listOfOMTars.get(i)+"file has not copied properly to /var/tmp",
        		overlayPackagesOperator.copyAlternateMediaList(listOfOMTars.get(i), INSTALL_PATH, "OM"), 0);
 assertEquals("Unable to untar"+listOfOMTars.get(i),
 overlayPackagesOperator.untarRepo(INSTALL_PATH,listOfOMTars.get(i)), 0);
if(checkForRstateOfTar(listOfOMTars.get(i),cachePath)){
                         logger.info("Getting the latest tar from the test drop area");
                  }
                  else 
                  {
                	  logger.info("Latest tar is present in the media area");
overlayPackagesOperator.cleanTempArea();
                	  continue;
                  }

  	assertEquals("Unable to remove old "+ listOfOMTars.get(i) +" from OM MEDIA", overlayPackagesOperator.removeTar(cachePath,listOfOMTars.get(i) ), 0);
    assertEquals("Unable to Add this pkg to OM MEDIA", overlayPackagesOperator.addTar(cachePath,(listOfOMTars.get(i)).replace(".tar.gz", "")), 0);

if(listOfOMTars.get(i).equalsIgnoreCase("omtools.tar.gz")){logger.info("upgrade_om.bsh will run after all the tars are overlayed");}
            else{ 
 checkForUpgrade(listOfOMTars.get(i),cachePath);
   }     				
forLoopExecuted++;

logger.info("cleaning temporary Area");


overlayPackagesOperator.cleanTempArea();

}     else {
        				logger.info("No alternate OM package list parameter is set in Jenkins");
        			}
        		}
if(forLoopExecuted > 0){checkForUpgrade("omtools.tar.gz",cachePath);}
        	}
else
{logger.info("Nothing to do media is not OM but"+media);}
        }    				

//Need to modularize the code as both the methods have most of the code similar....
private boolean checkForRstateOfPackage(String pkg, String cachePath)
        {
        	String rstateOfNewPackage = overlayPackagesOperator.getRstateofnewPackage(pkg);
        	String rstateofExistingPackage = overlayPackagesOperator.getRstateofExistingPackage(pkg,cachePath);
                  return(checkForRstate(rstateOfNewPackage,rstateofExistingPackage));
        }



    

private boolean checkForRstateOfTar(String tar, String cachePath)
        {
        	String rstateOfNewTar = overlayPackagesOperator.getRstateofNewTar(tar,INSTALL_PATH);
        	String rstateofExistingTar = overlayPackagesOperator.getRstateofExistingTar(tar,cachePath);
                return( checkForRstate(rstateOfNewTar,rstateofExistingTar));
}






private boolean checkForRstate(String newRstate, String existingRstate){
	int lengthofNewRstate = newRstate.length( );
	int lengthofExistingRstate = existingRstate.length( );
	if(lengthofNewRstate<6){
		newRstate = "0"+newRstate.substring(1);
	}
        else
        {
        	newRstate = newRstate.substring(1);
        }
	if(lengthofExistingRstate<6){
		existingRstate = "0"+existingRstate.substring(1);
	}
        else
        { existingRstate = existingRstate.substring(1);
        }

logger.info("Existing Rstate in Media Area"+existingRstate);
logger.info("New Rstate in Media Area"+newRstate);



	if(Integer.parseInt(newRstate.substring(0,2))>Integer.parseInt(existingRstate.substring(0,2)))
			return true;
	else if(Integer.parseInt(newRstate.substring(0,2))<Integer.parseInt(existingRstate.substring(0,2))){
return false;

}	else
		{
if(newRstate.charAt(2)>existingRstate.charAt(2)){
return true;}

else if(newRstate.charAt(2)<existingRstate.charAt(2)){
return false;}
else{
	if(Integer.parseInt(newRstate.substring(3,5))>Integer.parseInt(existingRstate.substring(3,5)))
				return true;
			else 
				return false;	
		}
	}

}



private void checkForUpgrade(String pkg, String cachePath)
{
        	
        	 if(pkg.equalsIgnoreCase("omtools.tar.gz")){
        	  	  upgradeOM(cachePath);  
        	    }
        	 else if(pkg.equalsIgnoreCase("SRU.tar.gz")){
        		 overlayPackagesOperator.installSRU(cachePath);
        	 }
else{}
        	
        }

private void upgradeOM(String cachePath) {
    assertEquals("Unable to upgrade ERICjump package", overlayPackagesOperator.upgradeOM(cachePath + "/om"), 0);
}






}
