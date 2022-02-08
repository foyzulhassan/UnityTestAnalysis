package com.unity.testanalyzer;

public class LineCountAssertCount {
	private int lineCount;	
	private int assertCount;
	
	public LineCountAssertCount()
	{
		this.lineCount=0;
		this.assertCount=0;
	}
	
	public int getLineCount() {
		return lineCount;
	}
	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}
	public int getAssertCount() {
		return assertCount;
	}
	public void setAssertCount(int assertCount) {
		this.assertCount = assertCount;
	}
	
	public void addLineCount(int line)
	{
		this.lineCount=this.lineCount+line;
	}
	
	public void addAssertCount(int assertcount)
	{
		this.assertCount=this.assertCount+assertcount;
	}
}
