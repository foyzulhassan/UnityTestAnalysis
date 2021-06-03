package com.csharp.changesize;

import com.opencsv.bean.CsvBindByName;

public class ChangeSize {
	@CsvBindByName(column = "id", required = true)
	int id;	
	@CsvBindByName(column = "classChangeSize", required = true)
	int classChangeSize;
	@CsvBindByName(column = "statementChangeSize", required = true)
	int statementChangeSize;
	@CsvBindByName(column = "methodChangeSize", required = true)
	int methodChangeSize;
	
	public ChangeSize()
	{
		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getClassChangeSize() {
		return classChangeSize;
	}
	public void setClassChangeSize(int classChangeSize) {
		this.classChangeSize = classChangeSize;
	}
	public int getStatementChangeSize() {
		return statementChangeSize;
	}
	public void setStatementChangeSize(int statementChangeSize) {
		this.statementChangeSize = statementChangeSize;
	}
	public int getMethodChangeSize() {
		return methodChangeSize;
	}
	public void setMethodChangeSize(int methodChangeSize) {
		this.methodChangeSize = methodChangeSize;
	}

}
