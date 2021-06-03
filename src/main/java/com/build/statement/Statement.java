package com.build.statement;

import java.util.ArrayList;
import java.util.List;

public class Statement {
	private int commitid;
	private boolean isMatch;
	private boolean isVisited;
	private int uniqueId;
	private String funcName;
	private List<Expression> expressionList;

	public Statement() {
		this.commitid = -1;
		this.uniqueId = -1;
		this.isMatch = false;
		this.isVisited = false;
		expressionList = new ArrayList<>();
	}

	public int getCommitid() {
		return commitid;
	}

	public void setCommitid(int commitid) {
		this.commitid = commitid;
	}

	public boolean isMatch() {
		return isMatch;
	}

	public void setMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}

	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public List<Expression> getExpressionList() {
		return expressionList;
	}

	public void setExpressionList(List<Expression> expressionList) {
		this.expressionList = expressionList;
	}

	public void addExpressionToList(Expression exp) {
		if (exp != null) {
			expressionList.add(exp);
		}
	}

	public int getExpressionSize() {
		return expressionList.size();
	}

	public int getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(int uniqueId) {
		this.uniqueId = uniqueId;
	}

}
