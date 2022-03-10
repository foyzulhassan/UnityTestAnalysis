package com.unity.testsmell;

import java.util.ArrayList;
import java.util.List;

public class AssertCall {
	private String assertName;	
	private List<String> paramList;
	private boolean hasMsg;
	
	
	public AssertCall()
	{
		paramList=new ArrayList<>();
		this.hasMsg=false;
	}
	
	
	public String getAssertName() {
		return assertName;
	}


	public void setAssertName(String assertName) {
		this.assertName = assertName;
	}


	public List<String> getParamList() {
		return paramList;
	}


	public void setParamList(List<String> paramList) {
		this.paramList = paramList;
	}


	public void addParam(String paramname)
	{
		this.paramList.add(paramname);
	}
	
	public boolean isHasMsg() {
		return hasMsg;
	}

	public void setHasMsg(boolean hasMsg) {
		this.hasMsg = hasMsg;
	}


    public String toString(){
        return getAssertName()+'_'+hasMsg;
    }

}
