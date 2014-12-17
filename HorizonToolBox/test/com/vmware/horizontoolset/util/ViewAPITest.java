package com.vmware.horizontoolset.util;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.vmware.horizontoolset.viewapi.LinkedClonePool;
import com.vmware.horizontoolset.viewapi.SnapShotViewPool;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.horizontoolset.viewapi.ViewApiFactory;
import com.vmware.horizontoolset.viewapi.ViewType;

public class ViewAPITest {

	@Test
	public void test() {
		ViewAPIService _service = ViewApiFactory.createNewAPIService("10.112.118.27", "administrator", "ca$hc0w", "stengdomain");
		List<SnapShotViewPool> pools=_service.getDetailedAutoPools();
		try{
			for (SnapShotViewPool pool: pools){
				if (pool.getViewType().equals(ViewType.LinkedClone)){
					LinkedClonePool linkedPool = (LinkedClonePool)pool;
					System.out.println("pool name:" + linkedPool.getName() + " rdn:"+linkedPool.getADContainer().getRDN());
					
				}
			}
		}catch(Exception ex){
			
			ex.printStackTrace();
			assertTrue(false);
		}

    	
	}

}
