package com.vmware.horizontoolset;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.horizon.auditing.EventsAuditing;
import com.vmware.horizon.auditing.EventsAuditingImpl;
import com.vmware.horizon.auditing.db.EventDBUtil;
import com.vmware.horizontoolset.check.VersionChecker;
import com.vmware.horizontoolset.util.TaskModuleUtil;
import com.vmware.horizontoolset.util.LDAP;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.util.SimpleHttpClient;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.horizontoolset.viewapi.ViewApiFactory;
import com.vmware.vdi.common.winauth.WinAuthUtils;

@Controller
public class LoginController{
	/**
	 * 
	 */
	
	public LoginController(){
		log.debug("Login Controller is created");
	}
	
	public static final String LoginURL = "/Login";
	public static final String SubmitURL = "/submitlogin";
	private static Logger log = Logger.getLogger(LoginController.class);

	private String server = "localhost";
	private boolean remoteDebug = false;
	public void setRemoteDebug(boolean newdebug){
		this.remoteDebug = newdebug;
	}
	public void setMatchedVersions(String[] versions){
		VersionChecker.setMatchedVersions(versions);
	}
	
	
	private String brokerXMLAPI =  "https://"+this.server+"/broker/xml";;
	private static String message = "<?xml version=\"1.0\"?> <broker version=\"1.0\">  <get-configuration/> </broker>";
 	public void setServer(String server) {
		this.server = server;
		brokerXMLAPI = "https://"+server+"/broker/xml";
	}

	private static List<String> domains = new ArrayList<String>();
	@RequestMapping(value = LoginURL, method = RequestMethod.GET)
	public String index( Model model) {
		log.debug("Receive index request");
		return loginPageWithMessage(model, null);
	}
	private String loginPageWithMessage(Model model,String message){
		if (domains.isEmpty()){
			this.getDomains();
		}
		model.addAttribute("domains", domains);
		model.addAttribute("message", message);
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
    	ViewAPIService _service;
    	try{
    		_service = ViewApiFactory.createNewAPIService(server, credential.getUsername(), credential.getPassword(), credential.getDomain());
        	SessionUtil.setUser(session, credential.getUsername());
    		SessionUtil.setSessionObj(session, _service);
    		
    	}catch(Exception ex){
    		log.error("login failed, return to login page,", ex);
    		return loginPageWithMessage(model, ex.getMessage());
    	}

    	boolean anyError = false;

    	try{
    		VersionChecker.isServerMatched(_service);
    	}catch(Exception ex){
    		log.error("Version Incompatiable,",ex);
    		SessionUtil.releaseSession(session);
    		return loginPageWithMessage(model, ex.getMessage());
    	}
    	LDAP _ldap = null;
    	try{
    		if (remoteDebug){
    			log.warn("THIS IS DEBUG MODE LDAP and domain is not used!!!!!!!!!");
    			_ldap = LDAP._get_junit_ldap(server,credential.getUsername(), credential.getPassword(), credential.getDomain());
    		}else{
    			_ldap =  new LDAP(credential.getUsername(), credential.getPassword(),  credential.getDomain()); 
    		}
    		
    		
    		SessionUtil.setSessionObj(session, _ldap);
    		
    	}catch(Exception ex){
    		log.error("You can't use ldap related features!", ex);
        	anyError = true;
    	}
    	
    	try{
    		if (_ldap !=null){
    			EventsAuditing _db = new EventsAuditingImpl(_ldap.getVDIContext());
    		SessionUtil.setSessionObj(session, _db);
    		}
    	}catch(Exception ex){
    		log.error("You can't use DB related features!", ex);
    		anyError = true;
    	}
    	
    	TaskModuleUtil.onLogin(server, credential, anyError);
    	
    	//the default page is snapshotsreport
    	return "redirect:/";
    }
    

	
	private static SimpleHttpClient httpClient = new SimpleHttpClient();
	
	private void getDomainsFromXMLAPI(){
		String result = httpClient.post(brokerXMLAPI, message);
		/**
		<name>domain</name>
        <values>
            <value>DOMAIN1</value>
        </values>
        **/
		
		if (result == null){
			log.info("Can't get domain from XML API, may be using RADIUS or other auth method!");
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
	private synchronized void getDomains() {
		if (!domains.isEmpty()){
			return;
		}
		
		getDomainsFromXMLAPI();
		
		if (domains.isEmpty()){
			log.debug("try to get domain from WinAuth");
			List<String> tempdomains = WinAuthUtils.getDomains();
			if (tempdomains!=null){
				domains.addAll(tempdomains);
			}

		}
		log.debug("Get domains:"+domains.size());
	}
	
	
	
}
