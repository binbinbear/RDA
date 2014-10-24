package com.vmware.horizontoolset;


import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.horizontoolset.util.EventDBUtil;
import com.vmware.horizontoolset.util.LDAP;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.util.SimpleHttpClient;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.horizontoolset.viewapi.ViewApiFactory;

@Controller
public class LoginController{
	/**
	 * 
	 */
	
	public LoginController(){
		log.debug("Login Controller is created");
	}
	
	private ViewAPIService _service;
	private LDAP _ldap;
	private EventDBUtil _db;
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		//release the API
		if (this._service!=null){
			_service.disconnect();
			this._service = null;
		}
		if (this._ldap!=null){
			this._ldap.close();
			this._ldap = null;
		}
		
		if (this._db!=null){
			this._db.disConnect();
			this._db = null;
		}

	}

	public static final String LoginURL = "/Login";
	public static final String SubmitURL = "/Login/Submit";
	private static Logger log = Logger.getLogger(LoginController.class);

	protected static String server = "localhost";
	private static boolean remoteDebug = false;
	public static void setRemoteDebug(boolean newdebug){
		LoginController.remoteDebug = newdebug;
	}
	
	
	private static String brokerXMLAPI =  "https://"+server+"/broker/xml";;
	private static String message = "<?xml version=\"1.0\"?> <broker version=\"1.0\">  <get-configuration/> </broker>";
 	public static void setServer(String server) {
		LoginController.server = server;
		brokerXMLAPI = "https://"+server+"/broker/xml";
	}

	private static ArrayList<String> domains = new ArrayList<String>();
	@RequestMapping(value = LoginURL, method = RequestMethod.GET)
	public String index( Model model) {
		log.debug("Receive index request");
		if (domains.isEmpty()){
			LoginController.getDomains();
		}
		model.addAttribute("domains", domains);
		return "login";
	}
	
	
	/**
	 * This is for users to login; if login successful, show the previous report
	 * @param credential
	 * @param bindingResult
	 * @param model
	 * @return
	 */
    @RequestMapping(SubmitURL)
    public String login(Credential credential, BindingResult bindingResult, Model model, HttpSession session) {
    	if (bindingResult.hasErrors()) {
            return "redirect:/Login";
        }

    	log.debug("User login :" + credential.getUsername());
    	try{
    		_service = ViewApiFactory.createNewAPIService(server, credential.getUsername(), credential.getPassword(), credential.getDomain());
        	SessionUtil.setUser(session, credential.getUsername());
    		SessionUtil.setViewAPIService(session, _service);
    		
    		
    		
    	}catch(Exception ex){
    		log.error("login failed, return to login page,", ex);
    		return "redirect:/Login";
    	}
    	
    	try{
    		if (remoteDebug){
    			log.warn("THIS IS DEBUG MODE LDAP and domain is not used!!!!!!!!!");
    			_ldap = LDAP._get_junit_ldap(server,credential.getUsername(), credential.getPassword());
    		}else{
    			_ldap =  new LDAP(server, credential.getDomain(), credential.getUsername(), credential.getPassword()); 
    		}
    		
    		
    		SessionUtil.setLDAP(session, _ldap);
    		
    	}catch(Exception ex){
    		log.error("You can't use ldap related features!", ex);
    		
    	}
    	
    	try{
    		
    		_db = new EventDBUtil(credential.getUsername(), credential.getPassword(), credential.getDomain());
    		SessionUtil.setDB(session, _db);
    	}catch(Exception ex){
    		log.error("You can't use DB related features!", ex);
    	}
    	//the default page is snapshotsreport
    	return "redirect:/";
    }
    

	
	private static SimpleHttpClient httpClient = new SimpleHttpClient();
	private static synchronized void getDomains() {
		if (!domains.isEmpty()){
			return;
		}
		String result = httpClient.post(brokerXMLAPI, message) ;
		//TODO: this is an ugly implementation
		/**
		<name>domain</name>
        <values>
            <value>DOMAIN1</value>
        </values>
        **/
		
		if (result == null){
			log.warn("Can't get domain!");
			return;
		}
		int domainStart=result.indexOf("<name>domain</name>");
	
		if (domainStart>0){
			int domainEnd = result.indexOf("</param>", domainStart);
			int domainIndex = result.indexOf("<value>", domainStart);
			while (domainIndex<domainEnd && domainIndex>0 ){
				int domainIndexEnd = result.indexOf("</value>",domainIndex );
				String domain = result.substring(domainIndex+7, domainIndexEnd );
				if (!domains.contains(domain)){
					domains.add(domain);
					log.debug("add domain:"+domain);
				}
				domainIndex = result.indexOf("<value>", domainIndexEnd);
			}
		}
		
		
	}
	
	
	
}
