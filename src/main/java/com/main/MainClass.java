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
import com.unity.testanalyzer.FuncCodeLOCAnalyzer;
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

        System.out.println("0->Download Projects" +
//                "\n2->Commit Change Analysis"
//				+ "\n3->Read CSV File and Generate Patch"
                        "\n11->Generate Test and Functional code Method and Class Count (RQ1)"
                        + "\n12->Generate Functional code LOC (RQ1)"
                        + "\n2->Test Assert Density(RQ2 + LOC(RQ1) analysis)"
                        + "\n31->Assert Roulette Analysis(RQ3)"
                        + "\n32->Eager Test Analysis(RQ3)"
                        + "\n33->Lazy Test Analysis(RQ3)"
                        + "\n34->Mystery Guest  Analysis(RQ3)"
                        + "\n35->Sensitive Equality Analysis(RQ3)"
                        + "\n35->General Fixture Analysis(RQ3)"
                        + "\n37->Magic Number Test Analysis"
                        + "\n38->Default Test Analysis"
                        + "\n39->Redundant Print Analysis"
                        + "\n40->Constructor initialization"
                        + "\n41->Sleepy Test"
                        + "\n42->Empty Test"


        );

        Scanner cin = new Scanner(System.in);

        System.out.println("Enter an integer: ");
        int inputid = cin.nextInt();

        if (inputid == 1) {
            ProjectLoader projloader = new ProjectLoader();
            projloader.LoadDownloadProjects();
            System.out.println("Download Projects->Completed");

        }
//        else if (inputid == 2) {
//			CommitAnalysisMngr commitmngr = new CommitAnalysisMngr();
//			commitmngr.PerformCommitAnalysis();
//		} else if (inputid == 3) {
//			CSVReaderWriter csvrw = new CSVReaderWriter();
//			try {
//				List<PerfFixData> cmtlist = csvrw.getListBeanFromCSV(Config.csvFile);
//				CSharpDiffGenMngr diffgen = new CSharpDiffGenMngr();
//				diffgen.generateCSharpDiff(cmtlist);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
        else if (inputid == 11) {

            System.out.println("\n\n\nGenerate Test and Functional code Method and Class Count (RQ1)\n\n\n");

            ProjectTestAnalyzer analyzer = new ProjectTestAnalyzer();
            ProjectTestData projdata = analyzer.PerformClassFunctionType();
            List<TestData> testdatalist = analyzer.convertToTestData(projdata);
            List<TestMethodData> testmethoddatalist = analyzer.convertToTestMethodData(projdata);
            if (testdatalist.size() > 0) {
                CSVReaderWriter writer = new CSVReaderWriter();
                ApacheCSVReaderWriter apachecsv = new ApacheCSVReaderWriter();
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


        } else if (inputid == 2) {

            System.out.println("Assert Density  + LOC  Analysis(RQ2-b)");

            AssertDensityAnalyzer analyzer = new AssertDensityAnalyzer();
            ProjectLocAssertCount projdata = analyzer.PerformAssertDensity();

            List<ProjAssertDensity> projassertdensity = new ArrayList<>();
            if (projdata != null) {
                Map<String, LineCountAssertCount> projectTestData = projdata.getProjectTestData();
                try {
                    for (String proj : projectTestData.keySet()) {
                        LineCountAssertCount locassertcount = projectTestData.get(proj);
                        ProjAssertDensity projassdensity = new ProjAssertDensity();
                        projassdensity.setProjName(proj);
                        projassdensity.setTestlineCount(locassertcount.getLineCount());
                        if (locassertcount.getLineCount() <= 0) {
                            System.out.println(proj + "==>" + locassertcount.getLineCount());
                            projassdensity.setAssertDensity(-0.01);
                        } else {
                            float density = (float) ((float) locassertcount.getAssertCount() / (float) locassertcount.getLineCount());
                            projassdensity.setAssertDensity(density);
                            System.out.println(proj + "==>" + density);
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

        } else if (inputid == 12) {

            System.out.println("Generate Functional code LOC (RQ1)");

            FuncCodeLOCAnalyzer analyzer = new FuncCodeLOCAnalyzer();
            ProjectLocAssertCount projdata = analyzer.PerformLOC();

            List<ProjAssertDensity> projassertdensity = new ArrayList<>();
            if (projdata != null) {
                Map<String, LineCountAssertCount> projectTestData = projdata.getProjectTestData();
                try {
                    for (String proj : projectTestData.keySet()) {

                        LineCountAssertCount locassertcount = projectTestData.get(proj);

                        ProjAssertDensity projassdensity = new ProjAssertDensity();
                        projassdensity.setProjName(proj);
                        projassdensity.setTestlineCount(locassertcount.getLineCount());


                        if (locassertcount.getLineCount() <= 0) {
                            System.out.println(proj + "==>" + locassertcount.getLineCount());
                            projassdensity.setAssertDensity(-0.01);
                        } else {
                            float density = (float) ((float) locassertcount.getAssertCount() / (float) locassertcount.getLineCount());
                            projassdensity.setAssertDensity(density);
                            System.out.println(proj + "==>" + density);
                        }

                        projassertdensity.add(projassdensity);
                    }

                    ApacheCSVReaderWriter writer = new ApacheCSVReaderWriter();
                    writer.WriteFuncLOCCSVFile(projassertdensity, Config.csvFileLOCStat);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        } else if (inputid == 31) {
            System.out.println("Assert Roulette Analysis(RQ3)");

            SmellAnalysisMngr smellmgr = new SmellAnalysisMngr();
            List<ProjectSmellEntity> projsemlllist = smellmgr.analyzeAssertionRoulette();
            ApacheCSVReaderWriter writer = new ApacheCSVReaderWriter();
            try {
                writer.WriteSmellStatCSVFile(projsemlllist, Config.getSmellStatFile("AssertRoulette"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (inputid == 32) {  //
            System.out.println("Eager Test Analysis(RQ3)");

            SmellAnalysisMngr smellmgr = new SmellAnalysisMngr();
            List<ProjectSmellEntity> projsemlllist = smellmgr.analyzeEagerTest();
            ApacheCSVReaderWriter writer = new ApacheCSVReaderWriter();
            try {
                writer.WriteSmellStatCSVFile(projsemlllist, Config.getSmellStatFile("EagerTest"));
            } catch (IOException e) {
//                 TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (inputid == 33) {  //
            System.out.println("Lazy Test Analysis(RQ3)");

            SmellAnalysisMngr smellmgr = new SmellAnalysisMngr();
            List<ProjectSmellEntity> projsemlllist = smellmgr.analyzeLazyTest();
            ApacheCSVReaderWriter writer = new ApacheCSVReaderWriter();
            try {
                writer.WriteSmellStatCSVFile(projsemlllist, Config.getSmellStatFile("LazyTest"));
            } catch (IOException e) {
//                 TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (inputid == 34) {
            System.out.println("Mystery Guest Analysis(RQ3)");

            SmellAnalysisMngr smellmgr = new SmellAnalysisMngr();
            List<ProjectSmellEntity> projsemlllist = smellmgr.analyzeMysteryGuest();
            ApacheCSVReaderWriter writer = new ApacheCSVReaderWriter();
            try {
                writer.WriteSmellStatCSVFile(projsemlllist, Config.getSmellStatFile("MysteryGuest"));
            } catch (IOException e) {
//                 TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (inputid == 35) {
            System.out.println("Sensitive Equality Analysis(RQ3)");

            SmellAnalysisMngr
                    smellmgr = new SmellAnalysisMngr();
            List<ProjectSmellEntity>
                    projsemlllist = smellmgr.analyzeSensitiveEquality();
            ApacheCSVReaderWriter
                    writer = new ApacheCSVReaderWriter();
            try {
                writer.WriteSmellStatCSVFile(projsemlllist, Config.getSmellStatFile("SensitiveEqaulity"));
            } catch (IOException e) {
//                 TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (inputid == 36) {
            System.out.println("General Fixture Analysis(RQ3)");

            SmellAnalysisMngr
                    smellmgr = new SmellAnalysisMngr();
            List<ProjectSmellEntity>
                    projsemlllist = smellmgr.analyzeGeneralFixture();
            ApacheCSVReaderWriter
                    writer = new ApacheCSVReaderWriter();
            try {
                writer.WriteSmellStatCSVFile(projsemlllist, Config.getSmellStatFile("GeneralAnalysis"));
            } catch (IOException e) {
//                 TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (inputid == 37) {
            System.out.println("Magic Number Test Analysis");

            SmellAnalysisMngr
                    smellmgr = new SmellAnalysisMngr();
            List<ProjectSmellEntity>
                    projsemlllist = smellmgr.analyzeMagicNumberTest();
            ApacheCSVReaderWriter
                    writer = new ApacheCSVReaderWriter();
            try {
                writer.WriteSmellStatCSVFile(projsemlllist, Config.getSmellStatFile("MagicNumberTest"));
            } catch (IOException e) {
//                 TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
        else if (inputid == 38) {
            System.out.println("Default Test Analysis");

            SmellAnalysisMngr
                    smellmgr=new SmellAnalysisMngr();
            List<ProjectSmellEntity>
                    projsemlllist=smellmgr.analyzeDefaultTest();
            ApacheCSVReaderWriter
                    writer = new ApacheCSVReaderWriter();
            try {
                writer.WriteSmellStatCSVFile(projsemlllist, Config.getSmellStatFile("DefaultTest"));
            } catch (IOException e) {
//                 TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        else if (inputid == 39) {
            System.out.println("Redundant Print");

            SmellAnalysisMngr
                    smellmgr=new SmellAnalysisMngr();
            List<ProjectSmellEntity>
                    projsemlllist=smellmgr.analyzeRedundantPrint();
            ApacheCSVReaderWriter
                    writer = new ApacheCSVReaderWriter();
            try {
                writer.WriteSmellStatCSVFile(projsemlllist, Config.getSmellStatFile("RedundantPrint"));
            } catch (IOException e) {
//                 TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        else if (inputid == 40) {
            System.out.println("Constructor Initialization");

            SmellAnalysisMngr
                    smellmgr=new SmellAnalysisMngr();
            List<ProjectSmellEntity>
                    projsemlllist=smellmgr.analyzeConstructorInitialization();
            ApacheCSVReaderWriter
                    writer = new ApacheCSVReaderWriter();
            try {
                writer.WriteSmellStatCSVFile(projsemlllist, Config.getSmellStatFile("ConstructorInitialization"));
            } catch (IOException e) {
//                 TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        else if (inputid == 41) {
            System.out.println("Sleepy Test");

            SmellAnalysisMngr
                    smellmgr=new SmellAnalysisMngr();
            List<ProjectSmellEntity>
                    projsemlllist=smellmgr.analyzeSleepyTest();
            ApacheCSVReaderWriter
                    writer = new ApacheCSVReaderWriter();
            try {
                writer.WriteSmellStatCSVFile(projsemlllist, Config.getSmellStatFile("SleepyTest"));
            } catch (IOException e) {
//                 TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        else if (inputid == 42) {
            System.out.println("Empty Test");

            SmellAnalysisMngr
                    smellmgr=new SmellAnalysisMngr();
            List<ProjectSmellEntity>
                    projsemlllist=smellmgr.analyzeEmptyTest();
            ApacheCSVReaderWriter
                    writer = new ApacheCSVReaderWriter();
            try {
                writer.WriteSmellStatCSVFile(projsemlllist, Config.getSmellStatFile("EmptyTest"));
            } catch (IOException e) {
//                 TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}