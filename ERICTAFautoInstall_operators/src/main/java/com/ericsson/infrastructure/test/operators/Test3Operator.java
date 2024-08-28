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
import java.util.List;

import com.ericsson.cifwk.taf.data.*;
import com.ericsson.infrastructure.test.operators.Auto_Install_CommonOperator;

public class Test3Operator extends Auto_Install_CommonOperator {
	 public  boolean callCopyExecute( ) {
	    	// identify box we want to copy file to 
	    	boolean cleanup = true ;
	    	Auto_Install_CommonOperator test2oper;
	    	List<Host> sut = DataHandler.getAllHostsByType(HostType.RC);
	    	//test if box exists 
	    	if (sut.isEmpty()) {System.out.println("No Servers  defined in host.properties so test has nothing to do");	 		
	    		return cleanup;
	    	 }
	    	 System.out.println("Ollie call  copyExecute2 ");
	    	 test2oper = new Auto_Install_CommonOperator();
			 test2oper.copyExecute("TC_get_disk_list.sh","",sut);
	    	 return true;
		}

	

}
