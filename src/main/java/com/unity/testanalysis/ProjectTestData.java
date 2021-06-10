package com.unity.testanalysis;

import java.util.HashMap;
import java.util.Map;

public class ProjectTestData {
	Map<String,TestAnalysisData> projectTestData;
	
	public ProjectTestData()
	{
		projectTestData=new HashMap<>();
	}
	
	public Map<String, TestAnalysisData> getProjectTestData() {
		return projectTestData;
	}
	
	public void addProjectData(String projectname,TestAnalysisData data)
	{
		projectTestData.put(projectname, data);
	}
}
