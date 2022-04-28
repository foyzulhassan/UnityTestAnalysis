package com.unity.testanalysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.build.commitanalyzer.CommitAnalyzer;
import com.config.Config;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.unity.callgraph.ClassFunction;
import com.unity.callgraph.FunctionCall;
import com.unity.entity.PerfFixData;
import com.unity.entity.TestData;
import com.unity.entity.TestMethodData;
import com.utility.ProjectPropertyAnalyzer;

import edu.util.fileprocess.CSVReaderWriter;
import edu.util.fileprocess.TextFileReaderWriter;

public class ProjectTestAnalyzer {

	public ProjectTestAnalyzer() {

	}

	public ProjectTestData PerformClassFunctionType() {
		String filepath = Config.gitProjList;

		List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
		//List<PerfFixData> fixdata = new ArrayList<>();
		ProjectTestData projtestdata=new ProjectTestData();

		int counter=0;
		for (String proj : projlist) {
			String projname = ProjectPropertyAnalyzer.getProjName(proj);
			TestAnalysisData analysisdata = new TestAnalysisData(projname);

			CommitAnalyzer cmtanalyzer = null;
			System.out.println(counter+"-->"+projname);;
			counter++;
//            if(counter >5){
//                break;
//            }
			

			try {
				cmtanalyzer = new CommitAnalyzer("test", projname, proj);

				String commitid = cmtanalyzer.getHeadCommitID();
				List<ClassFunction> classfunclist = cmtanalyzer.getClassFunctionTypeList(commitid);

				for (ClassFunction item : classfunclist) {
					if (item.isTestClass()) {
						analysisdata.addToTestClassList(item.getClassname());
					} else {
						analysisdata.addToClassList(item.getClassname());
					}

					List<FunctionCall> funclist = item.getFuncNameList();

					for (FunctionCall func : funclist) {
						if (func.isTestFunction()) {
							analysisdata.addToTestMethodList(func.getFuncName());
						} else {
							analysisdata.addToMethodList(func.getFuncName());
						}
					}

				}

				// Should work here to analyze the data
				
				//System.out.print("test");
				projtestdata.addProjectData(projname, analysisdata);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}

//		if(fixdata.size()>0)
//		{
//			CSVReaderWriter writer=new CSVReaderWriter();
//			try {
//				writer.writeListBean(fixdata, Config.csvFile);
//			} catch (CsvDataTypeMismatchException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (CsvRequiredFieldEmptyException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		return projtestdata;

	}
	
	
	public List<TestData> convertToTestData(ProjectTestData data)
	{
		List<TestData> testdatalist=new ArrayList<TestData>();
		
		Map<String,TestAnalysisData> projectTestData=data.getProjectTestData();
		
		 for (String proj : projectTestData.keySet())
		 {
			 TestData testdata=new TestData(proj);
			 testdata.setClassCount(projectTestData.get(proj).getClassListSize());
			 testdata.setTestClassCount(projectTestData.get(proj).getTestClassListSize());
			 testdata.setFunctionCount(projectTestData.get(proj).getMethodListSize());
			 testdata.setTestFunctionCount(projectTestData.get(proj).getTestMethodListSize());
			 
			 testdatalist.add(testdata);
		 }	
		
		
		return testdatalist;
	}
	
	public List<TestMethodData> convertToTestMethodData(ProjectTestData data)
	{
		List<TestMethodData> testdatalist=new ArrayList<TestMethodData>();
		
		Map<String,TestAnalysisData> projectTestData=data.getProjectTestData();
		
		int id=0;
		 for (String proj : projectTestData.keySet())
		 {			 
			 List<String> methodlist=projectTestData.get(proj).getTestMethodList();
			 
			 if(methodlist!=null && methodlist.size()>0)
			 {
				 for(String method:methodlist)
				 {
					 TestMethodData testmethoddata=new TestMethodData(proj,id);
					 testmethoddata.setTestMethodName(method);
					 testdatalist.add(testmethoddata);
				 }
			 }
			 
			 id++;
			
		 }	
		
		
		return testdatalist;
	}

}
