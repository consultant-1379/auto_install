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

import java.io.*;
import java.util.*;

import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.ericsson.cifwk.taf.tools.cli.Shell;

public class ManageDhcpClientsListOperator extends Auto_Install_CommonOperator{

	protected Shell shell;
	private static String AUTO_INSTALL_DIR = "/ericsson/autoinstall/"; 
	private int returnExitValue = -1;
	private final File folder = new File("../ERICTAFautoInstall_CXP9032762/src/main/resources/test_scripts/env/testenv");
	private final File[] listOfFiles = folder.listFiles();
	
	
	public String getListofClientsIntoString(String hostName){
		int exitCode = -1;
		String tempList = "/tmp/templist";
		String command = "";
		if (hostName==null){
			command="rm "+tempList+";"+AUTO_INSTALL_DIR+"/bin/manage_dhcp_clients.bsh -a list >> "+tempList;
		}
		else{
			command="rm "+tempList+";"+AUTO_INSTALL_DIR+"/bin/manage_dhcp_clients.bsh -a list -c "+hostName+" >> "+tempList;
		}
		Host sut = getMsHost();    
        final CLICommandHelper cmdHelper = new CLICommandHelper(sut); 
        cmdHelper.execute(command);
        cmdHelper.execute("cat /tmp/templist");
        String tempClientlist = cmdHelper.getStdOut();
        logger.debug("Temp Client List is " +tempClientlist);
        return tempClientlist;
		
	}
	
	public String[] getIndividualInfoOfHosts(String hostList){
		
		return hostList.split("\\[");
		
	}
	
	public boolean compareListedInfo(String hostList, ArrayList<String> individualHostInfo){
		
		boolean listResult = false;
		
		logger.info("Number of Clients to look for is "+individualHostInfo.size());
		for(String a : individualHostInfo ){
			String[] tmp=a.split("@");
			logger.info("looking for " +tmp[1]);
			listResult = hostList.contains(tmp[1]);
			if (listResult){
				logger.info("PASS : Found client "+tmp[1]);
			}
			else{
				logger.info("FAIL : Client "+tmp[1]+" not found");
			}
		}
		
		return listResult;
		
	}
	
    public ArrayList<String> readHostnamesFromFile(File path, ArrayList<String> fileContent) {
	//ArrayList<String> fileContentsList = new ArrayList<String>();
	try (InputStream in = new FileInputStream(path);
	    BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
	    String line = null;
	    while ((line = reader.readLine()) != null ) {
	    
	    	if(line.contains("CLIENT_HOSTNAME")){
	    		fileContent.add(line);  
	    		logger.info("Adding line from file"+line);
	    	}
	    }
	} catch (IOException e) {
	    System.err.println(e);
	}
	return fileContent;
    }
    
    public Map<String,String> readParamsIntoMap(ArrayList<String> param_details,String clientDetails, String splitChar){
    	Map<String,String> paramDetails = new HashMap<String, String>();
    	String temp =null;
    	if(param_details.size()!=0){
    	for (String param : param_details){
    		String[] param_part=param.split(splitChar);
    		paramDetails.put(param_part[0], param_part[1]);
    		logger.debug("Adding Parameter information to Map as"+param_part[0] + " " + param_part[1]);
    	}
    	}
    	if(clientDetails!=null){
    		String a =clientDetails.replaceAll("(\\r|\\n|\\r\\n)+"," \\:");
    		//a.replaceAll(" \\:", ",");
    		String[] tmp= a.split(splitChar);
    		//remove whitespaces
    		for (int k=0 ;k<tmp.length;k++){
    			tmp[k]=tmp[k].replaceAll("\\s","");
    		}
    		for (int i=0;i<tmp.length;i++){
    			int j = 1;
    			//int b = a+1;
    			if (i != 0){
    				i+=1;
    				j+=i;
        			if (i>=tmp.length){
        				break;
        			}
        			if(tmp[i].equalsIgnoreCase("InstallParams")){
        				for(int l=0;l<tmp.length;l++){
        					if(l==0){
        						l=i+1;
        					}
        					if(temp == null){
        						temp =tmp[l];
        						
        					}
        					else{
        						temp = temp+tmp[l];
        					}
        					
        					paramDetails.put(tmp[i], temp);
        					
        				}
        				break;
        			}
    			}
    			paramDetails.put(tmp[i], tmp[j]);
    			logger.debug("Adding Parameter information to Map as"+tmp[i] + " " + tmp[j]);

    		}
    	}
    	return paramDetails;
    }

    /**
	 * @param detailsInFIle
	 * @param file
	 * @return
	 */
	public String getClientInfo(Map<String, String> detailsInFile) {
		// TODO Auto-generated method stub
		String hostname = detailsInFile.get("CLIENT_HOSTNAME");
		/*String command = AUTO_INSTALL_DIR+"/bin/manage_dhcp_clients.bsh -a list -c "+hostname+" >>/tmp/clientinfo";
		Host sut = getMsHost();    
		final CLICommandHelper cmdHelper = new CLICommandHelper(sut); 
		cmdHelper.execute(command);*/
		String clientInfo = getListofClientsIntoString(hostname);
		return clientInfo;
		
	}

	/**
	 * @param file
	 * @return
	 */
	public ArrayList<String> readParamsIntoList(File file) {
		// TODO Auto-generated method stub
		ArrayList<String> fileContentsList = new ArrayList<String>();
		try (InputStream in = new FileInputStream(file);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
		    String line = null;
		    while ((line = reader.readLine()) != null ) {
		    		fileContentsList.add(line);  
		    		logger.info("Adding line from file"+line);
		    }
		} catch (IOException e) {
		    System.err.println(e);
		}
		return fileContentsList;
	    }

	/**
	 * @param detailsInFIle
	 * @param clientInfoInMap
	 * @return
	 */
	public boolean compareIndividualHostInfo(Map<String, String> detailsInFIle,
			Map<String, String> clientInfoInMap) {
		// TODO Auto-generated method stub
//		int size = 0;
		boolean result = false;
			
			logger.info("Comparing client details" +clientInfoInMap+ " and " +detailsInFIle);
			result =  (clientInfoInMap.get("Hostname")).contains(detailsInFIle.get("CLIENT_HOSTNAME"));
			logger.info("Comparing "+clientInfoInMap.get("Hostname")+" with "+ detailsInFIle.get("CLIENT_HOSTNAME"));
			result =  (clientInfoInMap.get("IPAddress")).contains(detailsInFIle.get("CLIENT_IP_ADDR"));
			logger.info("Comparing "+clientInfoInMap.get("IPAddress")+" with "+ detailsInFIle.get("CLIENT_IP_ADDR"));
			result =  (clientInfoInMap.get("Netmask")).contains(detailsInFIle.get("CLIENT_NETMASK"));
			logger.info("Comparing "+clientInfoInMap.get("Netmask")+" with "+ detailsInFIle.get("CLIENT_NETMASK"));
			result =  (clientInfoInMap.get("MACAddress")).contains(detailsInFIle.get("CLIENT_MAC_ADDR"));
			logger.info("Comparing "+clientInfoInMap.get("MACAddress")+" with "+ detailsInFIle.get("CLIENT_MAC_ADDR"));
			result = (clientInfoInMap.get("ApplicationSWPath")).contains(detailsInFIle.get("CLIENT_APPL_MEDIA_LOC"));
			logger.info("Comparing "+clientInfoInMap.get("ApplicationSWPath")+" with "+ detailsInFIle.get("CLIENT_APPL_MEDIA_LOC"));
			result =  (clientInfoInMap.get("DisplayType")).contains(detailsInFIle.get("CLIENT_DISP_TYPE"));
			logger.info("Comparing "+clientInfoInMap.get("DisplayType")+" with "+ detailsInFIle.get("CLIENT_DISP_TYPE"));
			result = (clientInfoInMap.get("O&MSWPath")).contains(detailsInFIle.get("CLIENT_OM_LOC"));
			logger.info("Comparing "+clientInfoInMap.get("O&MSWPath")+" with "+ detailsInFIle.get("CLIENT_OM_LOC"));
			result =  clientInfoInMap.get("InstallParams").contains(detailsInFIle.get("CLIENT_INSTALL_PARAMS").replaceAll("\\s",""));
			logger.info("Comparing "+clientInfoInMap.get("InstallParams")+" with "+ detailsInFIle.get("CLIENT_INSTALL_PARAMS").replaceAll("\\s",""));
	
		return result;
	}
	
}
