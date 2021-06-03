package com.build.statement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.config.Config;
import com.csharp.patch.xml.PatchXMLReader;
import com.github.javaparser.ast.stmt.IfStmt;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.unity.entity.FuncFixData;
import com.unity.entity.PerfFixData;

import edu.util.fileprocess.CSVReaderWriter;

public class StatementSimilarityMngr {

	private List<String> prefFuncList;
	
	public StatementSimilarityMngr(){
		
		prefFuncList=new ArrayList<>();
		
		prefFuncList.add("Update_0");
		prefFuncList.add("Start_0");
		prefFuncList.add("OnEnable_0");
		prefFuncList.add("Awake_0");
		prefFuncList.add("OnCollisionEnter_1");
		prefFuncList.add("get_0");
		prefFuncList.add("OnDisable_0");
		prefFuncList.add("LateUpdate_0");
		prefFuncList.add("OnDestroy_0");
		prefFuncList.add("OnTriggerEnter_1");

	}
	public void generateStmtSimilarity() {
		CSVReaderWriter csvrw = new CSVReaderWriter();
		List<PerfFixData> cmtlist = null;
		List<Statement> allstmtlist = new ArrayList<>();
		//List<Statement> allstmtlist = new ArrayList<>();

		try {
			cmtlist = csvrw.getListBeanFromCSV(Config.csvFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (PerfFixData fix : cmtlist) {
			StatementPatchXmlReader xmlreader = new StatementPatchXmlReader();
			List<Statement> stmtlist = xmlreader.getPatchStatementList(fix.getId());

			if (stmtlist != null)
				allstmtlist.addAll(stmtlist);
		}

		calculateStmtSimilarity(allstmtlist);
		System.out.println("Done Statement Matching\n");
	}

	public void calculateStmtSimilarity(List<Statement> stmtlist) {
		int index = 0;
		Map<Integer, List<Statement>> stmtmap = new HashMap<Integer, List<Statement>>();
		List<StatementFixData> stmtdatalist = new ArrayList<>();

		for (index = 0; index < stmtlist.size(); index++) {
			if (stmtlist.get(index).isMatch() == false) {
				List<Statement> mapstmtlist = new ArrayList<>();
				mapstmtlist.add(stmtlist.get(index));
				stmtmap.put(stmtlist.get(index).getUniqueId(), mapstmtlist);

			}
			for (int iindex = index + 1; iindex < stmtlist.size(); iindex++) {
				if (iindex < stmtlist.size()) {
					if (stmtlist.get(iindex).isMatch() == false) {
						boolean match = isSimilarStmt(stmtlist.get(index), stmtlist.get(iindex));
						if (match) {
							stmtlist.get(iindex).setMatch(true);
							stmtmap.get(stmtlist.get(index).getUniqueId()).add(stmtlist.get(iindex));
						}
					}
				}
			}

		}

		for (Integer key : stmtmap.keySet()) {
			StatementFixData stmtdata = new StatementFixData();
			List<Statement> stmts = stmtmap.get(key);
			stmtdata.setStmtChng(getFixType(stmts.get(0)));
			stmtdata.setStamtLabel(getFixLabel(stmts.get(0)));
			stmtdata.setChngCount(stmts.size());
			stmtdata.setCommitIds(getCommitIds(stmts));
			stmtdata.setFuncName(stmts.get(0).getFuncName());

			if(stmts.size()>1)
				stmtdatalist.add(stmtdata);
		}

		CSVReaderWriter writer = new CSVReaderWriter();
		try {
			writer.writeListBean(stmtdatalist, Config.rootDir + "statementchange.csv", StatementFixData.class);
		} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isSimilarStmt(Statement stmt, Statement otherstmt) {

		boolean match = true;
		
		

		if (stmt.getExpressionSize() != otherstmt.getExpressionSize())
			return false;

		// already matched with other statement; avoid duplicate counting
		if (otherstmt.isMatch())
			return false;

		if (stmt.getCommitid() == otherstmt.getCommitid())
			return false;

		if (stmt.getFuncName().length() > 0 && otherstmt.getFuncName().length() > 0
				&& stmt.getFuncName().equals(otherstmt.getFuncName())) {

			List<Expression> stmt1exp = stmt.getExpressionList();
			List<Expression> stmt2exp = otherstmt.getExpressionList();

			for (int index = 0; index < stmt1exp.size(); index++) {
				if (!stmt1exp.get(index).getTypeStr().equals(stmt2exp.get(index).getTypeStr())
						|| !stmt1exp.get(index).getActionStr().equals(stmt2exp.get(index).getActionStr()) /*|| !stmt1exp.get(index).getLabelStr().equals(stmt2exp.get(index).getLabelStr())*/) {
					
					match = false;
					break;
				}
			}

			return match;
		}
		else
		{
			return false;
		}

	}

	public String getFixType(Statement stmt) {
		StringBuilder stmttype = new StringBuilder();

		List<Expression> explist = stmt.getExpressionList();

		for (Expression exp : explist) {
			String label = "[" + exp.getActionStr() + "]=>" + exp.getTypeStr() + "~";
			stmttype.append(label);
		}

		return stmttype.toString();
	}

	public String getFixLabel(Statement stmt) {
		StringBuilder stmttype = new StringBuilder();

		List<Expression> explist = stmt.getExpressionList();

		for (Expression exp : explist) {
			String label = "[" + exp.getActionStr() + "]=>" + exp.getLabelStr() + "~";
			stmttype.append(label);
		}

		return stmttype.toString();
	}

	public String getCommitIds(List<Statement> stmtlist) {
		StringBuilder stmttype = new StringBuilder();

		for (Statement stmt : stmtlist) {
			stmttype.append(Integer.toString(stmt.getCommitid()) + ">");
		}

		return stmttype.toString();
	}
}
