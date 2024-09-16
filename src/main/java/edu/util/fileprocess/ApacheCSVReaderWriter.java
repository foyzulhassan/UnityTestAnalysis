package edu.util.fileprocess;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.unity.entity.ProjAssertDensity;
import com.unity.entity.TestData;
import com.unity.testsmell.ProjectSmellEntity;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ApacheCSVReaderWriter {

    public void WriteFuncLOCCSVFile(List<ProjAssertDensity> fixdata, String csvfilepath) throws IOException {
        FileWriter out = new FileWriter(csvfilepath);
        String[] HEADERS = { "project", "funclinecount" };

        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS))) {

            for (ProjAssertDensity item : fixdata) {
                printer.printRecord(item.getProjName(), item.getTestlineCount());
            }

        }
    }

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
