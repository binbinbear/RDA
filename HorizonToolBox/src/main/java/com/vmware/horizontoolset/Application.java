package com.vmware.horizontoolset;


import org.apache.log4j.Logger;


public class Application {
	private static Logger log = Logger.getLogger(Application.class);
    private String server;
    
    public static final String MAINPAGE = "toolset";
    
    public Application(){
    	log.info("Horizon Tool Set Application is being loaded!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }


	public String getServer() {
		return server;
	}


	public void setServer(String server) {
		this.server = server;
		LoginController.setServer(server);
		log.info("Server is set to be:"+server);
	}

    
}
