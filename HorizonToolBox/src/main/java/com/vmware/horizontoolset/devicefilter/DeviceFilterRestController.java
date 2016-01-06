package com.vmware.horizontoolset.devicefilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeviceFilterRestController {

	  @RequestMapping("/devicefilter/all")
	    public List<DeviceFilterPolicy> getAllPolicies(HttpSession session) {
		  //TODO: get from DB
		  List<DeviceFilterPolicy> all = new ArrayList<DeviceFilterPolicy>();
		  DeviceFilterPolicy p1 = new DeviceFilterPolicy("pool1");
		  p1.setBlack(true);

		  List<DeviceFilterItem> items = new ArrayList<DeviceFilterItem>();
		  items.add(new DeviceFilterItem(DeviceFilterEnum.IP,"192.168.1.*"));

		  p1.setItems(items);

		  all.add(p1);
		  return all;
		}


	  @RequestMapping("/devicefilter/result")
	    public List<DeviceFilterResult> getAllResults(HttpSession session) {
		  //TODO: read from DB
		  List<DeviceFilterResult> all = new ArrayList<DeviceFilterResult>();
		  DeviceFilterResult r1 = new DeviceFilterResult();
		  r1.setIp("192.168.1.2");
		  r1.setMac("a000-a000-a000-a000");
		  r1.setPoolName("p1");
		  r1.setOs("Windows 7");
		  r1.setTime(new Date());
		  r1.setVersion("3.2");
		  all.add(r1);

		  	return all;
		}


}
