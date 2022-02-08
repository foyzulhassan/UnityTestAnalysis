package com.unity.testsmell;

public class ProjectSmellEntity {
	private String smellName;
	private String projName;
	private double smellPercentage;
	
	@SuppressWarnings("unused")
	private ProjectSmellEntity()
	{
		
	}
	
	public ProjectSmellEntity(String smellname)
	{
		this.smellName=smellname;
	}
	
	public String getSmellName() {
		return smellName;
	}

	public void setSmellName(String smellName) {
		this.smellName = smellName;
	}

	public String getProjName() {
		return projName;
	}

	public void setProjName(String projName) {
		this.projName = projName;
	}

	public double getSmellPercentage() {
		return smellPercentage;
	}

	public void setSmellPercentage(double smellPercentage) {
		this.smellPercentage = smellPercentage;
	}

	

}
