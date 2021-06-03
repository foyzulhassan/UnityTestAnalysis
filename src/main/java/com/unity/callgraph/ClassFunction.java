package com.unity.callgraph;

import java.util.ArrayList;
import java.util.List;

public class ClassFunction {
	private String classname;
	private List<FunctionCall> funcNameList;

	public ClassFunction() {
		funcNameList = new ArrayList<FunctionCall>();
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public List<FunctionCall> getFuncNameList() {
		return funcNameList;
	}

	public void addItemToFuncList(FunctionCall func) {
		funcNameList.add(func);
	}

}
