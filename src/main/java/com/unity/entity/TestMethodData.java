package com.unity.entity;

import com.config.Config;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class TestMethodData {
	
	@CsvBindByName(column = "id", required = true)
	@CsvBindByPosition(position = 0)
	public int id;


	@CsvBindByName(column = "projName", required = true)
	@CsvBindByPosition(position = 1)
	public String projName;

	@CsvBindByName(column = "testMethodName", required = true)
	@CsvBindByPosition(position = 2)
	public String testMethodName;

	public TestMethodData(String projname, int projid) {
		this.projName = projname;
		this.id = projid;
	}
	
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



	public String getTestMethodName() {
		return testMethodName;
	}



	public void setTestMethodName(String testMethodName) {
		this.testMethodName = testMethodName;
	}

}
