package com.unity.testsmell;

import com.github.gumtreediff.tree.ITree;

import java.util.ArrayList;
import java.util.List;

public class AssertCall {
	private String assertName;	
	private List<String> paramList;
	private List<ITree> paramListTrees;
	private boolean hasMsg;
	
	
	public AssertCall()
	{
		paramList=new ArrayList<>();
		paramListTrees=new ArrayList<>();
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


    public List<ITree> getParamListTrees() {
        return paramListTrees;
    }


    public void setParamListTrees(List<ITree> paramList) {
        this.paramListTrees = paramList;
    }



    public void addParam(String paramname)
	{
		this.paramList.add(paramname);
	}

    public void addParamTree(ITree param)
    {
        this.paramListTrees.add(param);
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
