package com.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Config {
	// public static String rootDir="/media/AutoBuilder/UnityPerformance/";
	 public static String
//	 rootDir="/home/umd-002677/VR_AR_Testing/project_repos/";
//	 rootDir="/media/umd-002677/HDD/Project sets/VR_AR_Testing/project_repos/";
	 rootDir="C:\\Users\\dhiarzig\\Documents\\GitHub\\Selected\\";
//	 rootDir="D:\\VR_AR_Testing\\project_repos\\";
	 //
	 //text file that contains list of projects to analyze
	 public static String gitProjList=rootDir+"Project_Source.txt";
	 //reporDir used for storing Unity Projects
	 public static String repoDir = rootDir+"GitRepo/";
	
	 public static String csvFile=rootDir+"perf_commit_data_Updated.csv";
	 public static String patchDir=rootDir+"PatchDir/";

	public static String[] perfCommitToken = { "performance", "speed up", "accelerate", "fast", "slow", "latenc",
			"contention", "optimiz", "efficient" };
	
	public static String csvFileTestStat=rootDir+"unity_test_stat.csv";
	public static String csvFileTestStatDetails=rootDir+"unity_test_stat_details.csv";
	public static String csvFileAssertDensityStat=rootDir+"unity_assert_density_stat.csv";
	public static String csvFileLOCStat=rootDir+"unity_FuncCode_LOC_stat.csv";
	private static String csvFileSmellStat=rootDir+"unity_Smell_";


	// text file that contains list of projects to analyze
//	public static String rootDir = "F:\\Tammi_Thesis\\Repo\\";
//	public static String gitProjList = "F:\\Tammi_Thesis\\Repo\\Project_Source.txt";
//	// reporDir used for storing Unity Projects
//	public static String repoDir = "F:\\Tammi_Thesis\\Repo\\GitRepo\\";
//	public static String csvFile = "F:\\Tammi_Thesis\\Repo\\perf_commit_data_Updated_Test.csv";
//	public static String patchDir = rootDir + "PatchDir\\";

	public static int commitid = 1;
	public static int stmtUniqueId = 1;
	public static String separatorStr="<>";
	
	public static String getSmellStatFile(String smellname)
	{
		return csvFileSmellStat+smellname+".csv";
	}

}