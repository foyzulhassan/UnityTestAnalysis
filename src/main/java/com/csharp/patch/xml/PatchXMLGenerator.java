package com.csharp.patch.xml;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.build.analyzer.entity.CSharpChange;
import com.config.Config;

public class PatchXMLGenerator {

	public String getXMLPatch(List<CSharpChange> changelist, int id) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("patch");
		doc.appendChild(rootElement);

		Attr attr = doc.createAttribute("id");
		attr.setValue(Integer.toString(id));
		rootElement.setAttributeNode(attr);

		List<String> classlist = getDistinctClassList(changelist);

		for (String classname : classlist) {
			Element clsname = doc.createElement("classname");
			rootElement.appendChild(clsname);
			// set attribute to staff element
			Attr clsattr = doc.createAttribute("name");
			clsattr.setValue(classname);
			clsname.setAttributeNode(clsattr);

			List<String> funclist = getDistinctFuncList(changelist, classname);

			for (String funcname : funclist) {
				Element fnname = doc.createElement("funcname");
				clsname.appendChild(fnname);
				// set attribute to staff element
				Attr funcattr = doc.createAttribute("name");
				funcattr.setValue(funcname);
				fnname.setAttributeNode(funcattr);

				List<String> stmtlist = getStatementList(changelist, classname, funcname);

				for (String stmt : stmtlist) {
					Element stmtelement = doc.createElement("stmt");
					fnname.appendChild(stmtelement);
					// set attribute to staff element
					Attr stmtattr = doc.createAttribute("id");
					stmtattr.setValue(stmt);
					stmtelement.setAttributeNode(stmtattr);

					List<CSharpChange> explist = getExpList(changelist, classname, funcname, stmt);

					int expid = 0;

					for (CSharpChange change : explist) {
						Element exp = doc.createElement("exp");
						stmtelement.appendChild(exp);

						// set attribute to staff element
						Attr expattr = doc.createAttribute("id");
						expattr.setValue(Integer.toString(expid++));
						exp.setAttributeNode(expattr);

						Element exptype = doc.createElement("type");
						exptype.appendChild(doc.createTextNode(change.getTypeStr()));
						exp.appendChild(exptype);

						Element explabel = doc.createElement("label");
						explabel.appendChild(doc.createTextNode(change.getLabelStr()));
						exp.appendChild(explabel);

						Element expaction = doc.createElement("action");
						expaction.appendChild(doc.createTextNode(change.getActionStr()));
						exp.appendChild(expaction);
					}
				}
			}

		}

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DOMSource source = new DOMSource(doc);

		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String xmlstr = "";

		xmlstr = writer.toString();

		return xmlstr;
	}

	public List<String> getDistinctClassList(List<CSharpChange> changelist) {
		List<String> classlist = new ArrayList<>();

		for (CSharpChange chng : changelist) {
			String classname = chng.getClassName();

			if (!classlist.contains(classname))
				classlist.add(classname);
		}

		return classlist;
	}

	public List<String> getDistinctFuncList(List<CSharpChange> changelist, String classname) {
		List<String> funclist = new ArrayList<>();

		for (CSharpChange chng : changelist) {
			String clname = chng.getClassName();
			String funcname = chng.getFuncName();

			if (!funclist.contains(funcname) && clname.equals(classname))
				funclist.add(funcname);
		}

		return funclist;

	}

	private List<String> getStatementList(List<CSharpChange> changelist, String classname, String funcname) {
		List<String> stmtlist = new ArrayList<>();

		for (CSharpChange chng : changelist) {
			String clname = chng.getClassName();
			String fnname = chng.getFuncName();
			String stmtname = chng.getStmtID();

			if (clname.equals(classname) && fnname.equals(funcname) && !stmtlist.contains(stmtname))
				stmtlist.add(stmtname);
		}

		return stmtlist;
	}

	private List<CSharpChange> getExpList(List<CSharpChange> changelist, String classname, String funcname,
			String stmtid) {
		List<CSharpChange> chnglist = new ArrayList<>();

		for (CSharpChange chng : changelist) {
			String clname = chng.getClassName();
			String fnname = chng.getFuncName();
			String stmtname = chng.getStmtID();

			if (classname.equals(clname) && fnname.equals(funcname) && stmtname.equals(stmtid)) {
				chnglist.add(chng);
			}

		}

		return chnglist;
	}

	public void writeXMLPatch(List<CSharpChange> changelist, int id) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("patch");
		doc.appendChild(rootElement);

		Attr attr = doc.createAttribute("id");
		attr.setValue(Integer.toString(id));
		rootElement.setAttributeNode(attr);

		List<String> classlist = getDistinctClassList(changelist);

		for (String classname : classlist) {
			Element clsname = doc.createElement("classname");
			rootElement.appendChild(clsname);
			// set attribute to staff element
			Attr clsattr = doc.createAttribute("name");
			clsattr.setValue(classname);
			clsname.setAttributeNode(clsattr);

			List<String> funclist = getDistinctFuncList(changelist, classname);

			for (String funcname : funclist) {
				Element fnname = doc.createElement("funcname");
				clsname.appendChild(fnname);
				// set attribute to staff element
				Attr funcattr = doc.createAttribute("name");
				funcattr.setValue(funcname);
				fnname.setAttributeNode(funcattr);

				List<String> stmtlist = getStatementList(changelist, classname, funcname);

				for (String stmt : stmtlist) {
					Element stmtelement = doc.createElement("stmt");
					fnname.appendChild(stmtelement);
					// set attribute to staff element
					Attr stmtattr = doc.createAttribute("id");
					stmtattr.setValue(stmt);
					stmtelement.setAttributeNode(stmtattr);

					List<CSharpChange> explist = getExpList(changelist, classname, funcname, stmt);

					int expid = 0;

					for (CSharpChange change : explist) {
						Element exp = doc.createElement("exp");
						stmtelement.appendChild(exp);

						// set attribute to staff element
						Attr expattr = doc.createAttribute("id");
						expattr.setValue(Integer.toString(expid++));
						exp.setAttributeNode(expattr);

						Element exptype = doc.createElement("type");
						exptype.appendChild(doc.createTextNode(change.getTypeStr()));
						exp.appendChild(exptype);

						Element explabel = doc.createElement("label");
						explabel.appendChild(doc.createTextNode(change.getLabelStr()));
						exp.appendChild(explabel);

						Element expaction = doc.createElement("action");
						expaction.appendChild(doc.createTextNode(change.getActionStr()));
						exp.appendChild(expaction);
					}
				}
			}

		}
		
		File patchdir=new File(Config.patchDir);
		
		if(!patchdir.exists())
		{
			patchdir.mkdir();
		}
		
		String filename=Config.patchDir+Integer.toString(id)+".xml";

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(filename));

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
