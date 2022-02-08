package com.unity.entity;

import com.opencsv.bean.CsvBindByName;

public class ProjAssertDensity {
	@CsvBindByName(column = "projName", required = true)
	public String projName;	

	@CsvBindByName(column = "testlineCount", required = true)
	public int testlineCount;

	@CsvBindByName(column = "assertDensity")
	public double assertDensity;
	
	public ProjAssertDensity()
	{
		
	}
	
	public String getProjName() {
		return projName;
	}

	public void setProjName(String projName) {
		this.projName = projName;
	}

	public int getTestlineCount() {
		return testlineCount;
	}

	public void setTestlineCount(int testlineCount) {
		this.testlineCount = testlineCount;
	}

	public double getAssertDensity() {
		return assertDensity;
	}

	public void setAssertDensity(double assertDensity) {
		this.assertDensity = assertDensity;
	}

}
