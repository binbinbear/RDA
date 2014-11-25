package com.vmware.horizontoolset;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vmware.horizontoolset.util.SessionUtil;

@Controller
public class PolicyController {
	private static final String view = "policy";

	@RequestMapping(value = "/policy", method = RequestMethod.GET)
	public synchronized String action(Model model, HttpSession session) {
		model.addAttribute("view", view);
		model.addAttribute("user", SessionUtil.getuser(session));
		return Application.MAINPAGE;

	}

	/*
	 * Function to handle G2UI Grid related method It isn't a good practice to
	 * implement the delete action in the HTTP Get method. Just to follow the
	 * G2UI method definition <p> Get the policy on server from file e.g.
	 * c:\\temp\\test.json. I don't like to use the file to persist data. Use
	 * the method provided by Wan Nan to save the data
	 */

	@RequestMapping(value = "/data/policy", method = RequestMethod.GET)
	public @ResponseBody String getPolicies(
			@RequestParam(value = "cmd", defaultValue = "get-records") String cmd,
			@RequestParam(value="selected[]", required=false) String[] selected,
			HttpServletRequest request) {
		String response = "";

		try {
			switch (cmd) {
			case "get-records":
				//To-do: remvoe hardcode later
				response = readFile("c:\\temp\\test.json");
				break;
			case "delete-records":

				JSONParser parser = new JSONParser();
				
				try {
					//To-do: remvoe hardcode later
					Object obj = parser.parse(new FileReader("c:\\temp\\test.json"));
					JSONObject jsonObject = (JSONObject) obj;
					
					Long total =  (Long)jsonObject.get("total");
					JSONArray records = (JSONArray) jsonObject.get("records");	
					JSONArray newRecords=new JSONArray();
					
					for ( String sel : selected){						
							total--;
							
							Iterator iter = records.iterator();
							while(iter.hasNext()){
								JSONObject element = (JSONObject)iter.next();
								String name = (String)element.get("name");
								if (!name.equalsIgnoreCase(sel)){
									newRecords.add(element);
								}
							}
						
					}

					jsonObject.put("total", total);		
					jsonObject.put("records", newRecords);	
					
					//To-do: remvoe hardcode later
					FileWriter file = new FileWriter("c:\\temp\\test.json");
					file.write(jsonObject.toJSONString());
					file.flush();
					file.close();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				response = readFile("c:\\temp\\test.json");
				break;
			default:
				response = readFile("c:\\temp\\test.json");
			}
		} catch (IOException e) {
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

	public static void main(String[] args) {
		JSONParser parser = new JSONParser();
		
		try {
			//To-do: remvoe hardcode later
			Object obj = parser.parse(new FileReader("c:\\temp\\test.json"));
			JSONObject jsonObject = (JSONObject) obj;
			
			Long total =  (Long)jsonObject.get("total");
			JSONArray records = (JSONArray) jsonObject.get("records");	
			JSONArray newRecords=new JSONArray();
			String [] selected = {"John"};
			for ( String sel : selected){						
					total--;
					
					Iterator iter = records.iterator();
					while(iter.hasNext()){
						JSONObject element = (JSONObject)iter.next();
						String name = (String)element.get("name");
						if (!name.equalsIgnoreCase(sel)){
							newRecords.add(element);
						}
					}
				
			}

			jsonObject.put("total", total);		
			jsonObject.put("records", newRecords);	
			
			//To-do: remvoe hardcode later
			FileWriter file = new FileWriter("c:\\temp\\test.json");
			file.write(jsonObject.toJSONString());
			file.flush();
			file.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		

	}

}
