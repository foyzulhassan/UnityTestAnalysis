package com.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.build.statement.StatementPatchXmlReader;
import com.build.statement.StatementSimilarityMngr;
import com.commit.analysis.CommitFileTypeAnalysisMngr;
import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.csharp.astgenerator.SrcmlUnityCsTreeGenerator;
import com.csharp.changesize.ChangeSizeAnalyzer;
import com.csharp.diff.CSharpDiffGenMngr;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.tree.ITree;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.unity.callgraph.CallGraphBasedDistinctFuncAnalyzer;
import com.unity.callgraph.CallGraphBasedFuncAnalyzer;
import com.unity.callgraph.CallGraphBasedFuncFixCommit;
import com.unity.callgraph.ClassFunction;
import com.unity.callgraph.UserDefinedCallAnalysis;
import com.unity.commitanalyzer.CommitAnalysisMngr;
import com.unity.entity.PerfFixData;
import com.unity.entity.TestData;
import com.unity.entity.TestMethodData;
import com.unity.repodownloader.ProjectLoader;
import com.unity.testanalysis.ClassFunctionTypeAnalyzer;
import com.unity.testanalysis.ProjectTestAnalyzer;
import com.unity.testanalysis.ProjectTestData;
import com.unity.testanalyzer.AssertDensityAnalyzer;
import com.unity.testanalyzer.LineCountAssertCount;
import com.unity.testanalyzer.ProjectLocAssertCount;
import com.unity.testsmell.AssertionRoulette;
import com.unity.testsmell.TreeNodeAnalyzer;

import edu.util.fileprocess.CSVReaderWriter;

public class MainClassOld {

	public static void main(String[] args) {

		System.out.println("Enter your action:");

		System.out.println("1->Download Projects" + "\n2->Commit Change Analysis"
				+ "\n3->Read CSV File and Generate Patch" + "\n4->Generate Function Change Statistics"
				+ "\n5->Generate Statement Change Statistics" + "\n6->Change Size Calculation"
				+ "\n7->Change File Calculation" + "\n8->Callback based Function Analysis"
				+ "\n9->User Defined Statistics Generation" + "\n10-> Calcuate Fix with Distinct Method Change"
				+ "\n11->Generate Function Change Statistics with Callback Analysis" + "\n12->Generate Test Statistics"
				+ "\n13->Test Assert Density Analysis");

		Scanner cin = new Scanner(System.in);

		System.out.println("Enter an integer: ");
		int inputid = cin.nextInt();

		if (inputid == 1) {
			ProjectLoader projloader = new ProjectLoader();
			projloader.LoadDownloadProjects();
			System.out.println("Download Projects->Completed");

		} else if (inputid == 2) {
			CommitAnalysisMngr commitmngr = new CommitAnalysisMngr();
			commitmngr.PerformCommitAnalysis();
		} else if (inputid == 3) {
			CSVReaderWriter csvrw = new CSVReaderWriter();
			try {
				List<PerfFixData> cmtlist = csvrw.getListBeanFromCSV(Config.csvFile);
				CSharpDiffGenMngr diffgen = new CSharpDiffGenMngr();
				diffgen.generateCSharpDiff(cmtlist);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (inputid == 4) {
			CSVReaderWriter csvrw = new CSVReaderWriter();
			try {
				List<PerfFixData> cmtlist = csvrw.getListBeanFromCSV(Config.csvFile);
				CSharpDiffGenMngr diffgen = new CSharpDiffGenMngr();
				diffgen.generateFuncChageData(cmtlist);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (inputid == 5) {

			StatementSimilarityMngr stmtsimmngr = new StatementSimilarityMngr();
			stmtsimmngr.generateStmtSimilarity();
		} else if (inputid == 6) {

			ChangeSizeAnalyzer chaneanalyzer = new ChangeSizeAnalyzer();
			chaneanalyzer.generateChangeSizeStat();

		} else if (inputid == 7) {

			CommitFileTypeAnalysisMngr analyzer = new CommitFileTypeAnalysisMngr();
			analyzer.generateCommitAnalysis();

		} else if (inputid == 8) {
			CallGraphBasedFuncAnalyzer callgrpanalyzer = new CallGraphBasedFuncAnalyzer();

			CSVReaderWriter csvrw = new CSVReaderWriter();
			try {
				List<PerfFixData> cmtlist = csvrw.getListBeanFromCSV(Config.csvFile);
				callgrpanalyzer.generateFuncChageData(cmtlist);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (inputid == 9) {
			UserDefinedCallAnalysis usercall = new UserDefinedCallAnalysis();
			usercall.generateCallAnalysis();

		} else if (inputid == 10) {

			CallGraphBasedFuncFixCommit callgraphcommit = new CallGraphBasedFuncFixCommit();

			CSVReaderWriter csvrw = new CSVReaderWriter();
			try {
				List<PerfFixData> cmtlist = csvrw.getListBeanFromCSV(Config.csvFile);
				callgraphcommit.generateFuncChageCountAnalsysis(cmtlist);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (inputid == 11) {

			CallGraphBasedDistinctFuncAnalyzer callgraphcommit = new CallGraphBasedDistinctFuncAnalyzer();

			CSVReaderWriter csvrw = new CSVReaderWriter();
			try {
				List<PerfFixData> cmtlist = csvrw.getListBeanFromCSV(Config.csvFile);
				callgraphcommit.generateFuncChageData(cmtlist);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		else if (inputid == 12) {

			ProjectTestAnalyzer analyzer = new ProjectTestAnalyzer();
			ProjectTestData projdata = analyzer.PerformClassFunctionType();
			List<TestData> testdatalist = analyzer.convertToTestData(projdata);
			List<TestMethodData> testmethoddatalist = analyzer.convertToTestMethodData(projdata);
			if (testdatalist.size() > 0) {
				CSVReaderWriter writer = new CSVReaderWriter();
				try {
					writer.newwriteListBeanToFile(testdatalist, Config.csvFileTestStat);
					writer.writeBeanToFile(testmethoddatalist, Config.csvFileTestStatDetails);
				} catch (CsvDataTypeMismatchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CsvRequiredFieldEmptyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			System.out.print("*********Done************");

//			  CallGraphBasedDistinctFuncAnalyzer callgraphcommit = new CallGraphBasedDistinctFuncAnalyzer();
//
//				CSVReaderWriter csvrw = new CSVReaderWriter();
//				try {
//					List<PerfFixData> cmtlist = csvrw.getListBeanFromCSV(Config.csvFile);
//					callgraphcommit.generateFuncChageData(cmtlist);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

		} else if (inputid == 13) {

			System.out.println("Assert Density Analysis");

			AssertDensityAnalyzer analyzer = new AssertDensityAnalyzer();
			ProjectLocAssertCount projdata = analyzer.PerformAssertDensity();
			
			if (projdata!=null) {
				Map<String,LineCountAssertCount> projectTestData=projdata.getProjectTestData();
				try {
					for(String proj:projectTestData.keySet())	
					{
						LineCountAssertCount locassertcount=projectTestData.get(proj);
						
						if(locassertcount.getLineCount()<=0)
						{
							System.out.println(proj+"==>"+locassertcount.getLineCount());
						}
						else
						{
							float density=(float)((float)locassertcount.getAssertCount()/(float)locassertcount.getLineCount());
							System.out.println(proj+"==>"+density);
						}
						
					}				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else if (inputid == 14) {
			
			ClassFunctionTypeAnalyzer typeanalyzer = new ClassFunctionTypeAnalyzer();
			File f1 = new File("D:\\Research_Works\\VR_AR_Testing\\sample_Project\\AchievementTests.cs");
			Reader reader;
			try {
				reader = new FileReader(f1.toString());
				ITree curtree = new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();
				
				//TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
				//analyzer.getTestFunctionList(curtree);
				AssertionRoulette ar=new AssertionRoulette();
				ar.searchForAssertionRoulette(curtree);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
}