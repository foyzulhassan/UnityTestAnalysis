package edu.util.fileprocess;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.unity.entity.ProjAssertDensity;
import com.unity.entity.TestData;
import com.unity.testsmell.ProjectSmellEntity;

public class ApacheCSVReaderWriter {

	public void WriteAssertDensityCSVFile(List<ProjAssertDensity> fixdata, String csvfilepath) throws IOException {
		FileWriter out = new FileWriter(csvfilepath);
		String[] HEADERS = { "project", "testlinecount", "assertdensity" };

		try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS))) {

			for (ProjAssertDensity item : fixdata) {
				printer.printRecord(item.getProjName(), item.getTestlineCount(), item.getAssertDensity());
			}

		}
	}
	
	public void WriteTestDataCSVFile(List<TestData> fixdata, String csvfilepath) throws IOException {
		FileWriter out = new FileWriter(csvfilepath);
		String[] HEADERS = { "id", "projName", "classCount","testClassCount", "functionCount", "testFunctionCount" };

		try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS))) {

			for (TestData item : fixdata) {
				printer.printRecord(item.getId(),item.getProjName(),item.getClassCount(),item.getTestClassCount(),item.getFunctionCount(),item.getTestFunctionCount());
			}

		}
	}
	
	public void WriteSmellStatCSVFile(List<ProjectSmellEntity> smellstat, String csvfilepath) throws IOException {
		FileWriter out = new FileWriter(csvfilepath);
		String[] HEADERS = { "project", "smellname", "percentage" };

		try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS))) {

			for (ProjectSmellEntity item : smellstat) {
				printer.printRecord(item.getProjName(), item.getSmellName(), item.getSmellPercentage());
			}

		}
	}

}
