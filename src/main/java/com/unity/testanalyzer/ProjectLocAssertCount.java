package com.unity.testanalyzer;

import java.util.HashMap;
import java.util.Map;

public class ProjectLocAssertCount {
	Map<String,LineCountAssertCount> projectTestData;
	
	public ProjectLocAssertCount()
	{
		projectTestData=new HashMap<>();
	}
	
	public Map<String, LineCountAssertCount> getProjectTestData() {
		return projectTestData;
	}
	
	public void addProjectData(String projectname,LineCountAssertCount data)
	{
		projectTestData.put(projectname, data);
	}
}
