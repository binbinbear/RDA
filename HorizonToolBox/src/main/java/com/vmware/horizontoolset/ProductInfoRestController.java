package com.vmware.horizontoolset;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductInfoRestController {
	private static Logger log = Logger.getLogger(ProductInfoRestController.class);
	private ProductInfo _info;
	private static final String PROPERTY_FILE = "build-info.properties";
	private static final String BUILD_MAJOR= "build.major";
	private static final String BUILD_MINOR= "build.minor";
	private static final String UNKNOWN= "unknonwn";
	@RequestMapping("/about")
	public synchronized ProductInfo getProductInfo(){
		if (_info == null){
			_info = new ProductInfo();

            Properties p = new Properties();
            try {
                p.load(ProductInfo.class.getClassLoader()
                        .getResourceAsStream(PROPERTY_FILE));
            }catch(Exception e){
            	log.warn("Can't get properties", e);
            }
            
            _info.setVersion(p.getProperty(BUILD_MAJOR,UNKNOWN) + "-" + p.getProperty(BUILD_MINOR, UNKNOWN));
		}
		return _info;
		
	}
}
