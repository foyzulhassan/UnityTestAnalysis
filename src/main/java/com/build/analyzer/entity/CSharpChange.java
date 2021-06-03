package com.build.analyzer.entity;

public class CSharpChange {

	int patchId;
	String className;
	String funcName;
	String stmtID;
	String typeStr;
	String LabelStr;
	String actionStr;
	String expID;

	public String getExpID() {
		return expID;
	}

	public void setExpID(String expID) {
		this.expID = expID;
	}

	public int getPatchId() {
		return patchId;
	}

	public void setPatchId(int patchId) {
		this.patchId = patchId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public String getStmtID() {
		return stmtID;
	}

	public void setStmtID(String stmtID) {
		this.stmtID = stmtID;
	}

	public String getTypeStr() {
		return typeStr;
	}

	public void setTypeStr(String typeStr) {
		this.typeStr = typeStr;
	}

	public String getLabelStr() {
		return LabelStr;
	}

	public void setLabelStr(String labelStr) {
		LabelStr = labelStr;
	}

	public String getActionStr() {
		return actionStr;
	}

	public void setActionStr(String actionStr) {
		this.actionStr = actionStr;
	}

}
