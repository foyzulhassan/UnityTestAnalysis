package com.csharp.changesize;

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

import com.build.statement.Expression;
import com.build.statement.Statement;
import com.build.statement.StatementFixData;
import com.build.statement.StatementPatchXmlReader;
import com.config.Config;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.unity.entity.PerfFixData;

import edu.util.fileprocess.CSVReaderWriter;

public class ChangeSizeAnalyzer {
	public void generateChangeSizeStat() {
		CSVReaderWriter csvrw = new CSVReaderWriter();
		List<PerfFixData> cmtlist = null;
		List<ChangeSize> changesizelist = new ArrayList<>();
		// List<Statement> allstmtlist = new ArrayList<>();

		try {
			cmtlist = csvrw.getListBeanFromCSV(Config.csvFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (PerfFixData fix : cmtlist) {
			ChangeSize change = new ChangeSize();
			StatementPatchXmlReader xmlreader = new StatementPatchXmlReader();
			List<Statement> stmtlist = xmlreader.getPatchStatementList(fix.getId());
			int classcount = getClassChangeSize(fix.getId());
			int funccount = getFuncChangeSize(fix.getId());
			change.setId(fix.getId());
			if(stmtlist!=null)
				change.setStatementChangeSize(stmtlist.size());
			else
				change.setStatementChangeSize(0);
			change.setClassChangeSize(classcount);
			change.setMethodChangeSize(funccount);

			changesizelist.add(change);
		}

		CSVReaderWriter writer = new CSVReaderWriter();
		try {
			writer.writeListBean(changesizelist, Config.rootDir + "changesize_July23.csv", ChangeSize.class);
		} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done Change Size Calculation\n");	
		
	}

	public int getClassChangeSize(int id) {

		String filename = Config.patchDir + Integer.toString(id) + ".xml";

		System.out.println(filename);

		File fXmlFile = new File(filename);

		if (!fXmlFile.exists())
			return -1;

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

		NodeList nList = doc.getElementsByTagName("classname");

		return nList.getLength();
	}

	public int getFuncChangeSize(int id) {

		String filename = Config.patchDir + Integer.toString(id) + ".xml";

		System.out.println(filename);

		File fXmlFile = new File(filename);

		if (!fXmlFile.exists())
			return -1;

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

		NodeList nList = doc.getElementsByTagName("funcname");

		return nList.getLength();
	}

}
