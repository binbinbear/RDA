package com.vmware.horizontoolset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vmware.horizontoolset.util.SessionUtil;

@Controller
public class PolicyController {
	private static final String view = "policy";

    @RequestMapping(value="/policy", method=RequestMethod.GET)
    public synchronized String action( Model model, HttpSession session) {
        model.addAttribute("view", view);
        model.addAttribute("user", SessionUtil.getuser(session));
    	return Application.MAINPAGE;

    }
    
    //get  the policy on server from file e.g. c:\\temp\\test.json
    @RequestMapping(value="/data/policy",method=RequestMethod.GET)
    public @ResponseBody String get(HttpServletRequest request ) {
    	String response = "";
    	try{
    		response = readFile("c:\\temp\\test.json");
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    	return response; 
    }
    
    private String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }
    
    
}

