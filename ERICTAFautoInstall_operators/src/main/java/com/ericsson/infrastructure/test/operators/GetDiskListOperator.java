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






//import com.ericsson.cifwk.taf.TestCase;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.infrastructure.test.operators.Auto_Install_CommonOperator;



public class GetDiskListOperator extends Auto_Install_CommonOperator {
	
	 /**
     * Copy and execute file from auto_install gitrepo and execute on SUT.      
     */
 
	 public  boolean callCopyExecute( ) {
	    	
	    	boolean cleanup = true ;
	    	Auto_Install_CommonOperator test2oper;
	    	// identify box we want to copy file to by type
	    	List<Host> sut = DataHandler.getAllHostsByType(HostType.MS);
	    	//test if box exists 
	    	if (sut.isEmpty()) {System.out.println("No Servers  defined in host.properties so test has nothing to do");	 		
	    		return cleanup;
	    	 }
	    	System.out.println("call  copy & Execute to : "+sut);
	    	test2oper = new Auto_Install_CommonOperator();
			//return test2oper.copyExecute("TC_get_disk_list.bsh","",sut);
			return test2oper.copyExecute("somefile.bsh","",sut);
	    	
		}
	 /**
	     * Call a second .bsh file     
	     */
	 public  boolean callCopyExecute2( ) {
	    	
	    	boolean cleanup = true ;
	    	Auto_Install_CommonOperator testoper;
	    	// identify box we want to copy file to by type
	    	List<Host> sut = DataHandler.getAllHostsByType(HostType.MS);
	    	//test if box exists 
	    	if (sut.isEmpty()) {System.out.println("No Servers  defined in host.properties so test has nothing to do");	 		
	    		return cleanup;
	    	 }
	    	System.out.println("call  copy & Execute to : "+sut);
	    	testoper = new Auto_Install_CommonOperator();
			return testoper.copyExecute("TC_get_disk_list.bsh","",sut);
			//return test2oper.copyExecute("somefile.bsh","",sut);
	    	
		}
	 
}
	   