package com.build.statement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.config.Config;

public class StatementPatchXmlReader {
	public List<Statement> getPatchStatementList(int id) {
		List<Statement> stamentlist = new ArrayList<>();
		String filename = Config.patchDir + Integer.toString(id) + ".xml";

		System.out.println(filename);

		File fXmlFile = new File(filename);

		if (!fXmlFile.exists())
			return null;

		System.out.println(filename);

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

		NodeList nList = doc.getElementsByTagName("stmt");

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			/// System.out.println("\nCurrent Element :" + nNode.getNodeName());

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				Node parent=eElement.getParentNode();
				Element pElement = (Element) parent;
				String funcname=pElement.getAttribute("name");
				
				// System.out.println("Function Name : " + eElement.getAttribute("name"));

				String strid = eElement.getAttribute("id");
				Statement stmt = new Statement();
				stmt.setCommitid(id);
				stmt.setUniqueId(Config.stmtUniqueId++);
				stmt.setFuncName(funcname);

				NodeList expList = eElement.getElementsByTagName("exp");

				for (int count = 0; count < expList.getLength(); count++) {
					Node node1 = expList.item(count);
					if (node1.getNodeType() == node1.ELEMENT_NODE) {
						Element exp = (Element) node1;
						Expression expression = new Expression();
						expression.setId(exp.getAttribute("id"));

						if (exp.getElementsByTagName("type").getLength() > 0
								&& exp.getElementsByTagName("type").item(0).getFirstChild() != null) {
							expression.setTypeStr(
									exp.getElementsByTagName("type").item(0).getFirstChild().getNodeValue());
						} else {
							expression.setTypeStr("");
						}
						if (exp.getElementsByTagName("label").getLength() > 0
								&& exp.getElementsByTagName("label").item(0).getFirstChild() != null) {
							expression.setLabelStr(
									exp.getElementsByTagName("label").item(0).getFirstChild().getNodeValue());
						}
						else
						{
							expression.setLabelStr("");
						}
						if (exp.getElementsByTagName("action").getLength() > 0
								&& exp.getElementsByTagName("action").item(0).getFirstChild() != null) {
							expression.setActionStr(
									exp.getElementsByTagName("action").item(0).getFirstChild().getNodeValue());
						}
						//should not add comment as not to avoid matching issue
						if (!expression.getTypeStr().toLowerCase().contains("comment")) {
							stmt.addExpressionToList(expression);
						}
					}
				}
				stamentlist.add(stmt);
			}
		}

		return stamentlist;
	}

}
