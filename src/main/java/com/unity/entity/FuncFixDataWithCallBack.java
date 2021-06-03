package com.unity.entity;


import com.config.Config;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class FuncFixDataWithCallBack {
	@CsvBindByName(column = "funcName", required = true)
	public String funcName;

	@CsvBindByName(column = "funcCount", required = true)
	public int funcCount;

	@CsvBindByName(column = "commitList")
	public String commitList;
	
	@CsvBindByName(column = "funcType")
	public String funcType;	

	@CsvBindByName(column = "funcCaller")
	public String funcCaller;

	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public int getFuncCount() {
		return funcCount;
	}

	public void setFuncCount(int funcCount) {
		this.funcCount = funcCount;
	}

	public String getCommitList() {
		return commitList;
	}

	public void setCommitList(String commitList) {
		this.commitList = commitList;
	}
	
	public String getFuncType() {
		return funcType;
	}

	public void setFuncType(String funcType) {
		this.funcType = funcType;
	}

	public String getFuncCaller() {
		return funcCaller;
	}

	public void setFuncCaller(String funcCaller) {
		this.funcCaller = funcCaller;
	}
	
	public FuncFixDataWithCallBack()
	{
		this.funcType="callback";
	}
	
	public FuncFixDataWithCallBack(String funcname, int count, String commitlist)
	{
		this.funcName=funcname;
		this.funcCount=count;
		this.commitList=commitlist;
		this.funcType="callback";
	}
}
