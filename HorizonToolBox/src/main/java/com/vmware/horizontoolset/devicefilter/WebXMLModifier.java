package com.vmware.horizontoolset.devicefilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.vmware.horizontoolset.Application;

public class WebXMLModifier implements IWebXMLModifier {

	private static String filterparent = "filter";
	private static String filterName = "filter-name";
	private static String filterNameValue = "ToolboxFilter";
	private static String filterClass = "filter-class";
	private static String filterClassValue = "com.vmware.vdi.broker.filters.ToolboxFilter";

	private static String filtermappingParent = "filter-mapping";
	private static String filtermapping = "url-pattern";
	private static String filtermappingValue = "/broker/xml";


	private String getTextValue(Node ele){
		NodeList children = ele.getChildNodes();
		int clength = children.getLength();
		for (int j = 0; j < clength; j++) {
			Node child = children.item(j);
			if (child.getNodeType() == Node.TEXT_NODE) {
				return child.getNodeValue();
			}
		}
		return "";
	}


	private static Logger log = Logger.getLogger(WebXMLModifier.class);

	private List<Node> getToolboxFilterNames(Document doc){
		NodeList filters = doc.getElementsByTagName(filterName);
		List<Node> nodes = new ArrayList<Node>();
		for (int i=0;i<filters.getLength();i++){
			Node filter = filters.item(i);
			if (getTextValue(filter).equalsIgnoreCase(filterNameValue)){
				nodes.add(filter);
			}
		}
		return nodes;

	}
	@Override
	public boolean hasToolboxFilter() {
		Document doc = getDocument();
		if (doc==null){
			return false;
		}
	/**check the following
		  <filter>
		    <filter-name>ToolboxFilter</filter-name>
		    <filter-class>com.vmware.vdi.broker.filters.ToolboxFilter</filter-class>
		  </filter>

		  <filter-mapping>
		    <filter-name>ToolboxFilter</filter-name>
		    <url-pattern>/broker/xml</url-pattern>
		  </filter-mapping>

		  **/
		List<Node> filters = this.getToolboxFilterNames(doc);
		if (filters.size()!=2){
			return false;
		}
		boolean foundClass = false;
		boolean foundMapping = false;;
		for (Node filter: filters){
			Node next = filter.getNextSibling();
			while(next!=null && next.getNodeType()!=Node.ELEMENT_NODE){
				next =next.getNextSibling();
			}
			if (next ==null ){
				return false;
			}
			if (next.getNodeName().equalsIgnoreCase(filterClass)
					&& this.getTextValue(next).equalsIgnoreCase(filterClassValue)){
				foundClass = true;
			}else if (next.getNodeName().equalsIgnoreCase(filtermapping)
					&& this.getTextValue(next).equalsIgnoreCase(filtermappingValue)){
				foundMapping = true;
			}else{
				return false;
			}
		}

		return foundMapping && foundClass;

	}


	private File _getXMLFile(){
		return new File(this.getWebXMLPath());
	}
	private Document getDocument(){
		File webXML = _getXMLFile();
		if (!webXML.exists()) {
			log.info("Can't find the xml:"+webXML.getPath());
			return null;
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			InputStream in = new FileInputStream(webXML);
			Document doc = builder.parse(in);
			in.close();
			return doc;
		}catch(Exception ex){
			log.error("Can't parse the xml file", ex);
			return null;
		}
	}


	private void writeDocument (Document doc) throws IOException, TransformerException{
		TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transFormer = transFactory.newTransformer();
        transFormer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource domSource = new DOMSource(doc);
        File file = this._getXMLFile();
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        StreamResult xmlResult = new StreamResult(out);
        transFormer.transform(domSource, xmlResult);
        out.close();
	}

	private void addChild(Document doc, Element ele, String tagname, String value){
		 Element tag = doc.createElement(tagname);
		 Text text = doc.createTextNode(value);
		 tag.appendChild(text);

		 ele.appendChild(tag);
	}

	@Override
	public void insertToolboxFilter() {
		System.out.println("start to add filter");

		if (this.hasToolboxFilter()){
			System.out.println("Toolbox filter already there, please remove first!");
			log.error("Toolbox filter already there, please remove first!");
			return;
		}


//add before the first <filter>
		Document doc = this.getDocument();
	    Element toolboxfilter = doc.createElement(filterparent);
	    this.addChild(doc, toolboxfilter, filterName, filterNameValue);
	    this.addChild(doc, toolboxfilter, filterClass, filterClassValue);

	    Element toolboxfiltermapping = doc.createElement(filtermappingParent);
	    this.addChild(doc, toolboxfiltermapping, filterName, filterNameValue);
	    this.addChild(doc, toolboxfiltermapping, filtermapping, filtermappingValue);


	    NodeList filters = doc.getElementsByTagName(filterparent);
	    Node firstFilter  = filters.item(0);
	    Node root = firstFilter.getParentNode();
	    root.insertBefore(toolboxfilter,firstFilter );
	    root.insertBefore(toolboxfiltermapping, firstFilter);


	    try{
	    	// Copy web.xml to be web.xml.backup and then modify
	    	File file = this._getXMLFile();
	    	file.renameTo(new File(getWebXMLBackupPath()));
	    	writeDocument(doc);
	    }catch(Exception ex){
	    	log.error("Can't write document",ex);
	    	ex.printStackTrace();
	    }

	}

	@Override
	public void removeToolboxFilter() {
		// TODO Auto-generated method stub
		System.out.println("start to remove filter");
		// Copy web.xml to be web.xml.backup and then modify
		if (!this.hasToolboxFilter()){
			System.out.println("Can't remove since Toolbox filter not found!");
			log.warn("Can't remove since Toolbox filter not found!");
			return;
		}
//remove parent of <filter-name>ToolboxFilter</filter-name>
		Document doc = this.getDocument();



	    List<Node> filternames = this.getToolboxFilterNames(doc);

	    for (Node filtername: filternames){
	    	Node grandparent = filtername.getParentNode().getParentNode();
	    	Node filter = filtername.getParentNode();
	    	grandparent.removeChild(filter);
	    }

	    try{
	    	writeDocument(doc);
	    }catch(Exception ex){
	    	log.error("Can't write document",ex);
	    	ex.printStackTrace();
	    }


	}

	private String getWebXMLPath() {
		String serverPath = Application.getViewServerPath();

		return serverPath + "\\broker\\webapps\\ROOT\\WEB-INF\\web.xml";
	}

	private String getWebXMLBackupPath() {
		String serverPath = Application.getViewServerPath();

		return serverPath + "\\broker\\webapps\\ROOT\\WEB-INF\\web.xml.backup";
	}

	public static void main(String args[]) {
		WebXMLModifier modifier = new WebXMLModifier();
		System.out.println(modifier.hasToolboxFilter());
		if (modifier.hasToolboxFilter()){
			modifier.removeToolboxFilter();
		}else{
			modifier.insertToolboxFilter();
		}
		//modifier.insertToolboxFilter();

	}
}
