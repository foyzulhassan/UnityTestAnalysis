package com.unity.entity;

import com.config.Config;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
//@CsvBindByPosition(position = 2)
public class PerfFixData {
	@CsvBindByName(column = "id", required = true)	
	public int id;	

	@CsvBindByName(column = "projName", required = true)	
	public String projName;
	
	@CsvBindByName(column = "projGitUrl", required = true)	
	public String projGitUrl;	

	@CsvBindByName(column = "fixCommitID", required = true)	
	public String fixCommitID;
	
	@CsvBindByName(column = "fixCommitMsg", required = true)	
	public String fixCommitMsg;
	
	@CsvBindByName(column = "patchPath")	
	public String patchPath;
	
	@CsvBindByName(column = "srcFileChangeCount")	
	public int srcFileChangeCount;
	
	@CsvBindByName(column = "assetChangeCount")	
	public int assetChangeCount;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getProjName() {
		return projName;
	}

	public void setProjName(String projName) {
		this.projName = projName;
	}
	
	public String getProjGitUrl() {
		return projGitUrl;
	}

	public void setProjGitUrl(String projGitUrl) {
		this.projGitUrl = projGitUrl;
	}

	public String getFixCommitID() {
		return fixCommitID;
	}

	public void setFixCommitID(String fixCommitID) {
		this.fixCommitID = fixCommitID;
	}

	public String getFixCommitMsg() {
		return fixCommitMsg;
	}

	public void setFixCommitMsg(String fixCommitMsg) {
		this.fixCommitMsg = fixCommitMsg;
	}

	public String getPatchPath() {
		return patchPath;
	}

	public void setPatchPath(String patchPath) {
		this.patchPath = patchPath;
	}

	public int getSrcFileChangeCount() {
		return srcFileChangeCount;
	}

	public void setSrcFileChangeCount(int srcFileChangeCount) {
		this.srcFileChangeCount = srcFileChangeCount;
	}

	public int getAssetChangeCount() {
		return assetChangeCount;
	}

	public void setAssetChangeCount(int assetChangeCount) {
		this.assetChangeCount = assetChangeCount;
	}
	
	public PerfFixData(String projname,String commitid)
	{
		this.projName=projname;
		this.fixCommitID=commitid;
		this.id=Config.commitid++;
		
	}
	
	public PerfFixData(String projname,String giturl, String commitid)
	{
		this.projName=projname;
		this.projGitUrl=giturl;
		this.fixCommitID=commitid;	
		this.id=Config.commitid++;
	}
	
	public PerfFixData()
	{
		this.id=Config.commitid++;
	}
}
