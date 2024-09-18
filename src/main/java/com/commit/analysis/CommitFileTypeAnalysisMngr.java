package com.commit.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.build.analyzer.entity.CSharpChange;
import com.build.commitanalyzer.CommitAnalyzer;
import com.build.statement.Statement;
import com.build.statement.StatementPatchXmlReader;
import com.config.Config;
import com.csharp.changesize.ChangeSize;
import com.csharp.diff.CSharpDiffGenerator;
import com.csharp.patch.xml.PatchXMLGenerator;
//import com.github.gumtreediff.actions.EditScript;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.unity.entity.PerfFixData;
import com.utility.ProjectPropertyAnalyzer;

import edu.util.fileprocess.CSVReaderWriter;

public class CommitFileTypeAnalysisMngr {

	public void generateCommitAnalysis() {
		CSVReaderWriter csvrw = new CSVReaderWriter();
		List<PerfFixData> cmtlist = null;
		List<PerfFixData> cmtlistwithchange = new ArrayList<>();

		// List<Statement> allstmtlist = new ArrayList<>();

		try {
			cmtlist = csvrw.getListBeanFromCSV(Config.csvFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (PerfFixData fix : cmtlist) {
			String projname_and_owner = fix.getProjName();
			String owner = projname_and_owner.split("@")[0];
			String projname = projname_and_owner.split("@")[1];
			CommitAnalyzer cmtanalyzer = null;

			try {
				cmtanalyzer = new CommitAnalyzer(owner, projname);
				PerfFixData fixwithchange=cmtanalyzer.extractFileChangeData(fix.getFixCommitID(),fix);
				cmtlistwithchange.add(fixwithchange);		

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		CSVReaderWriter writer = new CSVReaderWriter();
		try {
			writer.writeListBean(cmtlistwithchange, Config.rootDir + "filechangestat_Jun23.csv", PerfFixData.class);
		} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done Change Size Calculation\n");
	}

}
