package com.unity.testanalysis;

import java.util.ArrayList;
import java.util.List;

public class TestAnalysisData {
	String projectName;
	List<String> classList;
	List<String> testClassList;
	List<String> methodList;
	List<String> testMethodList;
	
	
	private TestAnalysisData()
	{
		
	}
	
	public TestAnalysisData(String projectname)
	{
		this.projectName=projectname;
		classList=new ArrayList<String>();
		testClassList=new ArrayList<String>();
		methodList=new ArrayList<String>();
		testMethodList=new ArrayList<String>();		
	}
	
	public List<String> getClassList() {
		return classList;
	}


	public List<String> getTestClassList() {
		return testClassList;
	}


	public List<String> getMethodList() {
		return methodList;
	}


	public List<String> getTestMethodList() {
		return testMethodList;
	}
	
	public void addToClassList(String classname)
	{
		if(!classList.contains(classname))
			classList.add(classname);
	}
	
	public void addToTestClassList(String testclassname)
	{
		if(!testClassList.contains(testclassname))
			testClassList.add(testclassname);
	}
	
	public void addToMethodList(String methodname)
	{
		if(!methodList.contains(methodname))
			methodList.add(methodname);
	}
	
	public void addToTestMethodList(String testmethodname)
	{
		if(!testMethodList.contains(testmethodname))
			testMethodList.add(testmethodname);
	}
	
	
	public int getClassListSize()
	{
		return classList.size();
	}
	
	public int getTestClassListSize()
	{
		return testClassList.size();
	}
	
	public int getMethodListSize()
	{
		return methodList.size();
	}
	
	public int getTestMethodListSize()
	{
		return testMethodList.size();
	}

}
