package com.unity.commitanalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.build.commitanalyzer.CommitAnalyzer;
import com.config.Config;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.unity.entity.PerfFixData;
import com.unity.repodownloader.RepoDownloadManager;
import com.utility.ProjectPropertyAnalyzer;

import edu.util.fileprocess.CSVReaderWriter;
import edu.util.fileprocess.TextFileReaderWriter;

public class CommitAnalysisMngr {
	
	public CommitAnalysisMngr()
	{
		
	}
	
	
	public void PerformCommitAnalysis() {
		String filepath = Config.gitProjList;

		List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
		List<PerfFixData> fixdata=new ArrayList<>();

		for(String proj:projlist)
		{
			String projname=ProjectPropertyAnalyzer.getProjName(proj);
			
			CommitAnalyzer cmtanalyzer = null;

			try {
				cmtanalyzer = new CommitAnalyzer("test", projname,proj);
				List<PerfFixData> projfixdata=cmtanalyzer.getAllPerformanceCommits();
				
				if(projfixdata.size()>0)
				{
					fixdata.addAll(projfixdata);
				}				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(fixdata.size()>0)
		{
			CSVReaderWriter writer=new CSVReaderWriter();
			try {
				writer.writeListBean(fixdata, Config.csvFile);
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

	}

}
