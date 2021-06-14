package com.unity.entity;

import com.config.Config;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

//@CsvBindByPosition(position = 2)
public class TestData {
	@CsvBindByName(column = "id", required = true)
	@CsvBindByPosition(position = 0)
	public int id;

	@CsvBindByName(column = "projName", required = true)
	 @CsvBindByPosition(position = 1)
	public String projName;

	@CsvBindByName(column = "classCount", required = true)
	@CsvBindByPosition(position = 2)
	public int classCount;

	@CsvBindByName(column = "testClassCount", required = true)
	@CsvBindByPosition(position = 3)
	public int testClassCount;

	@CsvBindByName(column = "functionCount", required = true)
	@CsvBindByPosition(position = 4)
	public int functionCount;

	@CsvBindByName(column = "testFunctionCount", required = true)
	@CsvBindByPosition(position = 5)
	public int testFunctionCount;

	public TestData(String projname) {
		this.projName = projname;
		this.id = Config.commitid++;
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

	public int getClassCount() {
		return classCount;
	}

	public void setClassCount(int classCount) {
		this.classCount = classCount;
	}

	public int getTestClassCount() {
		return testClassCount;
	}

	public void setTestClassCount(int testClassCount) {
		this.testClassCount = testClassCount;
	}

	public int getFunctionCount() {
		return functionCount;
	}

	public void setFunctionCount(int functionCount) {
		this.functionCount = functionCount;
	}

	public int getTestFunctionCount() {
		return testFunctionCount;
	}

	public void setTestFunctionCount(int testFunctionCount) {
		this.testFunctionCount = testFunctionCount;
	}

}