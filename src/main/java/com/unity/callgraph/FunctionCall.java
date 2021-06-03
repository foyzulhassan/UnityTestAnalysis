package com.unity.callgraph;

import java.util.ArrayList;
import java.util.List;

public class FunctionCall {
	private String funcName;
	private List<String> funcCallList;

	public FunctionCall() {
		funcCallList = new ArrayList<String>();
	}

	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public List<String> getFuncCallList() {
		return funcCallList;
	}

	public void addItemToCallList(String callname) {
		funcCallList.add(callname);
	}

}
