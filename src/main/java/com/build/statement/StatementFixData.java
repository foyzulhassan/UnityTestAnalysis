package com.build.statement;

import com.opencsv.bean.CsvBindByName;

public class StatementFixData {
	@CsvBindByName(column = "stmtChng", required = true)
	private String stmtChng;
	
	@CsvBindByName(column = "stamtLabel")
	private String stamtLabel;	
	
	@CsvBindByName(column = "chngCount", required = true)
	private int chngCount;
	
	@CsvBindByName(column = "funcName", required = true)
	private String funcName;
	
	public String getFuncName() {
		return funcName;
	}
	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	@CsvBindByName(column = "commitIds", required = true)
	private String commitIds;
	
	public String getStmtChng() {
		return stmtChng;
	}
	public void setStmtChng(String stmtChng) {
		this.stmtChng = stmtChng;
	}
	public String getStamtLabel() {
		return stamtLabel;
	}
	public void setStamtLabel(String stamtLabel) {
		this.stamtLabel = stamtLabel;
	}
	public int getChngCount() {
		return chngCount;
	}
	public void setChngCount(int chngCount) {
		this.chngCount = chngCount;
	}
	public String getCommitIds() {
		return commitIds;
	}
	public void setCommitIds(String commitIds) {
		this.commitIds = commitIds;
	}
	
	public StatementFixData()
	{
		
	}
}
