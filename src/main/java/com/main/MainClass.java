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
import com.unity.entity.ProjAssertDensity;
import com.unity.entity.TestData;
import com.unity.entity.TestMethodData;
import com.unity.repodownloader.ProjectLoader;
import com.unity.testanalysis.ClassFunctionTypeAnalyzer;
import com.unity.testanalysis.ProjectTestAnalyzer;
import com.unity.testanalysis.ProjectTestData;
import com.unity.testanalyzer.AssertDensityAnalyzer;
import com.unity.testanalyzer.LineCountAssertCount;
import com.unity.testanalyzer.ProjectLocAssertCount;
import com.unity.testsmell.AssertCall;
import com.unity.testsmell.AssertionRoulette;
import com.unity.testsmell.ProjectSmellEntity;
import com.unity.testsmell.SmellAnalysisMngr;
import com.unity.testsmell.TreeNodeAnalyzer;

import edu.util.fileprocess.ApacheCSVReaderWriter;
import edu.util.fileprocess.CSVReaderWriter;

public class MainClass {

	public static void main(String[] args) {

		System.out.println("Enter your action:");

		System.out.println("1->Download Projects" + "\n2->Commit Change Analysis"
				+ "\n3->Read CSV File and Generate Patch" 
				+ "\n21->Generate Test Statistics (RQ1)"
				+ "\n22->Test Assert Density Analysis(RQ2(b))"
				+ "\n23->Assert Roulette Analysis(RQ3(a)");

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
		} 
		else if (inputid == 21) {

			System.out.println("\n\n\nGenerate Test Statistics (RQ1)\n\n\n");
			
			ProjectTestAnalyzer analyzer = new ProjectTestAnalyzer();
			ProjectTestData projdata = analyzer.PerformClassFunctionType();
			List<TestData> testdatalist = analyzer.convertToTestData(projdata);
			List<TestMethodData> testmethoddatalist = analyzer.convertToTestMethodData(projdata);
			if (testdatalist.size() > 0) {
				CSVReaderWriter writer = new CSVReaderWriter();
				ApacheCSVReaderWriter apachecsv=new ApacheCSVReaderWriter();
				try {
					apachecsv.WriteTestDataCSVFile(testdatalist, Config.csvFileTestStat);
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


		} else if (inputid == 22) {

			System.out.println("Assert Density Analysis(RQ2-b)");

			AssertDensityAnalyzer analyzer = new AssertDensityAnalyzer();
			ProjectLocAssertCount projdata = analyzer.PerformAssertDensity();
			
			List<ProjAssertDensity> projassertdensity=new ArrayList<>();
			if (projdata!=null) {
				Map<String,LineCountAssertCount> projectTestData=projdata.getProjectTestData();
				try {
					for(String proj:projectTestData.keySet())	
					{
						LineCountAssertCount locassertcount=projectTestData.get(proj);
						
						ProjAssertDensity projassdensity=new ProjAssertDensity();
						projassdensity.setProjName(proj);
						projassdensity.setTestlineCount(locassertcount.getLineCount());
						
						
						if(locassertcount.getLineCount()<=0)
						{
							System.out.println(proj+"==>"+locassertcount.getLineCount());
							projassdensity.setAssertDensity(-0.01);
						}
						else
						{
							float density=(float)((float)locassertcount.getAssertCount()/(float)locassertcount.getLineCount());
							projassdensity.setAssertDensity(density);
							System.out.println(proj+"==>"+density);
						}
						
						projassertdensity.add(projassdensity);
					}
					
					ApacheCSVReaderWriter writer = new ApacheCSVReaderWriter();
					writer.WriteAssertDensityCSVFile(projassertdensity, Config.csvFileAssertDensityStat);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} 
		else if (inputid == 23) {
			System.out.println("Assert Roulette Analysis(RQ3-a)");
			
			SmellAnalysisMngr smellmgr=new SmellAnalysisMngr();
			List<ProjectSmellEntity> projsemlllist=smellmgr.analyzeAssertionRoulette();
			ApacheCSVReaderWriter writer = new ApacheCSVReaderWriter();
			try {
				writer.WriteSmellStatCSVFile(projsemlllist, Config.getSmellStatFile("AssertRoulette"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
            }

        }
        // TODO General Fixture

        else if (inputid == 98) {
            System.out.println("General Fixture Analysis(RQ3-a)");

            SmellAnalysisMngr smellmgr=new SmellAnalysisMngr();
//            List<ProjectSmellEntity> projsemlllist=
                    smellmgr.analyzeGeneralFixture();
//            ApacheCSVReaderWriter writer = new ApacheCSVReaderWriter();
//            try {
//                writer.WriteSmellStatCSVFile(projsemlllist, Config.getSmellStatFile("ConditionalTestLogic"));
//            } catch (IOException e) {
//                 TODO Auto-generated catch block
//                e.printStackTrace();
//            }

        }

        else if (inputid == 99) {
            System.out.println("Conditional Test Analysis(RQ3-a)");

            SmellAnalysisMngr smellmgr=new SmellAnalysisMngr();
            List<ProjectSmellEntity> projsemlllist=smellmgr.analyzeConditionalTest();
            ApacheCSVReaderWriter writer = new ApacheCSVReaderWriter();
            try {
                writer.WriteSmellStatCSVFile(projsemlllist, Config.getSmellStatFile("ConditionalTestLogic"));
            } catch (IOException e) {
//                 TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
		
		
		else if (inputid == 24) {
			
			ClassFunctionTypeAnalyzer typeanalyzer = new ClassFunctionTypeAnalyzer();
			File f1 = new File("D:\\Research_Works\\VR_AR_Testing\\sample_Project\\AchievementTests.cs");
			Reader reader;
			try {
				reader = new FileReader(f1.toString());
				ITree curtree = new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();
				
				//TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
				//analyzer.getTestFunctionList(curtree);
				AssertionRoulette ar=new AssertionRoulette();
				Map<String,List<AssertCall>> testfuncassertmap=ar.searchForAssertionRoulette(curtree);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
}