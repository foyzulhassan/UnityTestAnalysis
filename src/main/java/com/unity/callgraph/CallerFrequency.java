package com.unity.callgraph;

import com.opencsv.bean.CsvBindByName;

public class CallerFrequency {
	@CsvBindByName(column = "funcName", required = true)
	public String funcName;	

	@CsvBindByName(column = "funcCount", required = true)
	public int funcCount;
	
	public CallerFrequency()
	{
		this.funcCount=0;
	}
	
	public CallerFrequency(String func, int count)
	{
		this.funcName=func;
		this.funcCount=count;
	}
	
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
}
