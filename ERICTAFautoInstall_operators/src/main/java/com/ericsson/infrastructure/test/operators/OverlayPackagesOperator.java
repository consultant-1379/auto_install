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
public class OverlayPackagesOperator extends Auto_Install_CommonOperator {

    /** Operator for all PCJ **/
    private Logger logger = LoggerFactory.getLogger(OverlayPackagesOperator.class);
    private final static String WGET = "/usr/local/bin/wget";
    private final static String CI_LINK = "https://cifwk-oss.lmera.ericsson.se";
    private final static String CI_PROXY_LINK = "https://arm901-eiffel004.athtem.eei.ericsson.se:8443";
    private static final String USE_TO_SHIPMENT = "testware.cacheto_to_shipment";
    private final static String TEST_PKG = "/var/tmp/testArea";
    private final static String TEST_DROP_AREA = "/net/10.45.227.26/export/TestDropArea/testLocation";
    public final static String  CACHE_SUFFIX = "CachePath";

    private TestContext context = TafTestContext.getContext();
    private String mode;
    private String mediaFile;
    private String downloadPath = (String) TafConfigurationProvider.provide().getString("downloadPath", "/JUMP/PSV/");


    /**
     *  * Method Checks if a packages list Paramter has been passed in as part of jenkins Param test_Pkg_list.
     *  *
     *  * @return String
     *  */
            public String[] alternatePkgList(String type) {
               String useAlternatepkglist[] =DataHandler.getConfiguration().getStringArray(type+"_test_pkg_list");
                if (useAlternatepkglist != null)// && !useAlternatepkglist.isEmpty())
                {
                    logger.info("Packages Test package supplied " + useAlternatepkglist);
                }
                return (useAlternatepkglist);

            }

     /**
     *   * Method Copies .pkg file from filepath on MWS ieatloaner156-1 to new Installed MWS.
     *   *
     *   * @param altMediaFile media file which needs to be copied to temporary locatoin
     *   * @param installPath- location of place to store files temporary when overlaying
     *   * @return int
     *   */
            public int copyAlternateMediaList(String altMediaFile, String installPath, String media) {
                logger.info("Install path is:  " + installPath);
                logger.info("Test Media list is:  " + altMediaFile);
                //here we need to just put the / that particular folder name as per the media file


                String cmd = "cp " + TEST_DROP_AREA +"/"+ media+"/"+altMediaFile + " " + installPath;
                   return runSingleBlockingCommandOnMwsAsRoot(cmd);
                  }


     /**
     * * Removes the .pkg format from cached OM Media
     * *
     * * @param mwsIP
     * * @return exit code of the command
     * */
            public int removeTar(String cachePath, String tar) {
                logger.info("Removing " + tar + " from " + cachePath + "OM now");

                String command = "rm -rf " + cachePath + "/om/"+ tar.split("\\.")[0];
                return (runSingleBlockingCommandOnMwsAsRoot(command));

            }





             public int removePkg(String cachePath, String pkg) {
                logger.info("Removing " + pkg + " from " + cachePath + "OM now");

                String command = "rm -rf " + cachePath + "/ossrc_base_sw/"+checkPath(pkg)+"/"+pkg.split("\\.")[0];
                return (runSingleBlockingCommandOnMwsAsRoot(command));

            }

             public void cleanTempArea() {
                logger.info("cleaning temporary Area");

                String command = "rm -rf /var/tmp/testArea";
                runSingleBlockingCommandOnMwsAsRoot(command);

            }


    /**
     ** Add test Packages one by one to cached OM Media
     **
     ** @param mwsIP
     ** @return exit code of the command
     **/
            public int addPkg(String cachePath, String pkg,String installPath) {
           // Note: The code is similar to SRU tar, so SRU gets Untar in TEST_PKG so here also it is same.
          //Once we get to know about the special handling of package then we will also do in TEST_PKG
                    logger.info("Adding " + pkg + " test Repo to " + cachePath + " OSSRC now");
            String command = "mv " + installPath + "/" + pkg + " " + cachePath + "/ossrc_base_sw/"+checkPath(pkg);
                                      return (runSingleBlockingCommandOnMwsAsRoot(command));
                                  }

public String checkPath(String pkg) {        
                String path = getPackagePath(pkg);
            logger.info("in the check path method"+path+" from the properties file");             
logger.info("this is the path "+path+" from the properties file");  
 return path;
                            }



private String getPackagePath(String attributeName) {
                Object obsPkg = DataHandler.getAttribute(attributeName);
 logger.info("fetched the object"+obsPkg+" from the properties file");
                String pkgPath;
                    pkgPath = (String)obsPkg;
                    return pkgPath;
                  }


public Object alternateTarList(String type) {
            	Object useAlternateTarList =DataHandler.getAttribute(type+"_test_tar_list");
            	if (useAlternateTarList != null)// && !useAlternateTarList.isEmpty())
            	{
            		logger.info("Packages Test package supplied " + useAlternateTarList);
            	}
            	return (useAlternateTarList);

            }
               

public int untarRepo(String installPath, String altRepoFile) {
	logger.info("Untar tar file    ");

	makeTestRepo();
	String Command = "cd " + TEST_PKG +";tar zxvf " + installPath + "/" + altRepoFile;
	return (runSingleBlockingCommandOnMwsAsRoot(Command));
}
 private int makeTestRepo() {
        logger.info("Making test location " + TEST_PKG);
        String Command = "mkdir " + TEST_PKG;
        return (runSingleBlockingCommandOnMwsAsRoot(Command));

    }
public int addTar(String cachePath, String tar) {
                            logger.info("Adding " + tar + " test Repo to " + cachePath + " OM now");
                               String command = "mv " + TEST_PKG + "/" + tar + " " + cachePath + "/om/ ";
                             return (runSingleBlockingCommandOnMwsAsRoot(command));
                           }

public String getRstateofnewPackage(String pkg){
            	  String command = "pkginfo -xd " + TEST_DROP_AREA +"/OSSRC/"+ pkg + " | grep '(' | awk '{print $2}'";
            	              runSingleBlockingCommandOnMwsAsRoot(command);
                                  return (getLastOutput());
            }

public String getRstateofExistingPackage(String pkg,String cachePath){
          	  String command = "pkginfo -xd " + cachePath+"/ossrc_base_sw/"+checkPath(pkg) +"/"+ pkg + " | grep '(' | awk '{print $2}'";
                             runSingleBlockingCommandOnMwsAsRoot(command);
                                           return (getLastOutput());
                       

}

public String getRstateofNewTar(String tar, String installPath){
        String command = "cat " + TEST_PKG+"/" +tar.split("\\.")[0]+ "/cxp_info | grep 'VERSION' | awk -F'=' '{ print $2 }'";
        runSingleBlockingCommandOnMwsAsRoot(command);
        return (getLastOutput());
  }


public String getRstateofExistingTar(String tar,String cachePath){
        String command = "cat " + cachePath + "/om/"+ tar.split("\\.")[0] + "/cxp_info | grep 'VERSION' | awk -F'=' '{ print $2 }'";
        runSingleBlockingCommandOnMwsAsRoot(command);
 return (getLastOutput());
  }
  


 public int upgradeOM(String directory) {
      logger.info("Upgrading ERICjump package on mws");
      String command = directory + "/omtools/upgrade_om.bsh  -p " + directory + " -a mws";
           int exit = runSingleBlockingCommandOnMwsAsRoot(command, false);
            logger.info("Command output: \n " + getLastOutput());
        return exit;
 }


public int installSRU(String cachePath) {
      String cmd = cachePath + "/om/SRU/install-repo.ksh -d /var/share/pkg/repositories/solaris -s " + cachePath + "/om/SRU -y"; 
      int exit = runSingleBlockingCommandOnMwsAsRoot(cmd);
      if (exit != 0) {
          logger.error("Failed to update SRU");
          logger.error(getLastOutput());
      }
      return exit;
  }
public int listAlternateTarFile(String altEricRepoFile, String mediaType) {
        logger.info("Test PACKAGE IS:  " + altEricRepoFile);
        String cmd = "ls " + TEST_DROP_AREA+"/" +mediaType+"/"+ altEricRepoFile;
        return runSingleBlockingCommandOnMwsAsRoot(cmd);
    }


 }
