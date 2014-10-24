package com.vmware.horizontoolset;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.util.SessionMsg;
import com.vmware.horizontoolset.util.SessionMsg.Msg;
import com.vmware.horizontoolset.util.SessionMsg.Type;

@RestController
public class SessionMsgRestController {
	
	private static final String JSON_OK = "{\"ret\":\"OK\"}";
	
	public static class Msgs {
		List<Msg> msgs;
	}
	
	@RequestMapping("/sessionMsg.list")
	public Msgs list(HttpSession session){
		Msgs ret = new Msgs();
		ret.msgs = SessionMsg.list(session);
		return ret;
	}
	
	@RequestMapping("/sessionMsg.clear")
	public String clear(HttpSession session){
		SessionMsg.clear(session);
		return JSON_OK;
	}
	
	@RequestMapping("/sessionMsg.add")
	public String add(HttpSession session,
			@RequestParam(value="m", required=true) String msg,
			@RequestParam(value="t", required=false, defaultValue="INFO") String type
			){
		SessionMsg.add(session, msg, Type.valueOf(type));
		return JSON_OK;
	}
}

