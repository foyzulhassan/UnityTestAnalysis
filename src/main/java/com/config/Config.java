package com.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Config {
	// public static String rootDir="/media/AutoBuilder/UnityPerformance/";
	 public static String
	 rootDir="G:\\Research\\VR_AR_Testing\\project_repo\\";
	 //
	 //text file that contains list of projects to analyze
	 public static String gitProjList=rootDir+"Project_Source.txt";
	 //reporDir used for storing Unity Projects
	 public static String repoDir = rootDir+"GitRepo/";
	
	 public static String csvFile=rootDir+"perf_commit_data_Updated.csv";
	 public static String patchDir=rootDir+"PatchDir/";

	public static String[] perfCommitToken = { "performance", "speed up", "accelerate", "fast", "slow", "latenc",
			"contention", "optimiz", "efficient" };
	
	public static String csvFileTestStat=rootDir+"test_stat.csv";
	public static String csvFileTestStatDetails=rootDir+"test_stat_details.csv";

	// text file that contains list of projects to analyze
//	public static String rootDir = "F:\\Tammi_Thesis\\Repo\\";
//	public static String gitProjList = "F:\\Tammi_Thesis\\Repo\\Project_Source.txt";
//	// reporDir used for storing Unity Projects
//	public static String repoDir = "F:\\Tammi_Thesis\\Repo\\GitRepo\\";
//	public static String csvFile = "F:\\Tammi_Thesis\\Repo\\perf_commit_data_Updated_Test.csv";
//	public static String patchDir = rootDir + "PatchDir\\";

	public static int commitid = 1;
	public static int stmtUniqueId = 1;

}