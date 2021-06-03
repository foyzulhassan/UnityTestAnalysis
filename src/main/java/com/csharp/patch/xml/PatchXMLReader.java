package com.csharp.patch.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.config.Config;
import com.utility.ClassFunctionUtil;

import org.w3c.dom.Node;
import org.w3c.dom.Element;

//Resources:
//https://mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
//https://www.javatpoint.com/how-to-read-xml-file-in-java

public class PatchXMLReader {

	public List<String> getPatchFuncList(int id) {
		List<String> funclist = new ArrayList<>();
		String filename = Config.patchDir + Integer.toString(id) + ".xml";

		File fXmlFile = new File(filename);

		if (!fXmlFile.exists())
			return null;

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc = null;
		try {
			doc = dBuilder.parse(fXmlFile);
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// optional, but recommended
		// read this -
		// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();

		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());

		NodeList nList = doc.getElementsByTagName("funcname");

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			/// System.out.println("\nCurrent Element :" + nNode.getNodeName());

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				// System.out.println("Function Name : " + eElement.getAttribute("name"));

				funclist.add(eElement.getAttribute("name"));
			}
		}

		return funclist;
	}

	public List<String> getPatchClassFuncList(int id) {
		List<String> funclist = new ArrayList<>();
		String filename = Config.patchDir + Integer.toString(id) + ".xml";

		File fXmlFile = new File(filename);

		if (!fXmlFile.exists())
			return null;

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc = null;
		try {
			doc = dBuilder.parse(fXmlFile);
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// optional, but recommended
		// read this -
		// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();

		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());

		NodeList nList = doc.getElementsByTagName("classname");

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			/// System.out.println("\nCurrent Element :" + nNode.getNodeName());

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				// System.out.println("Function Name : " + eElement.getAttribute("name"));

				//funclist.add(eElement.getAttribute("name"));

				NodeList nFuncList = eElement.getElementsByTagName("funcname");

				for (int fcount = 0; fcount < nFuncList.getLength(); fcount++) {

					Node nfuncNode = nFuncList.item(fcount);

					/// System.out.println("\nCurrent Element :" + nNode.getNodeName());

					if (nfuncNode.getNodeType() == Node.ELEMENT_NODE) {

						Element efuncElement = (Element) nfuncNode;

						// System.out.println("Function Name : " + eElement.getAttribute("name"));

						funclist.add(ClassFunctionUtil.getClassFuncName(eElement.getAttribute("name"),efuncElement.getAttribute("name")));
						/// NodeList nFuncList=eElement.getElementsByTagName("funcname");

					}
				}

			}
		}

		return funclist;
	}

}
