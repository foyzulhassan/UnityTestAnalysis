package com.unity.callgraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.build.commitanalyzer.CommitAnalyzer;
import com.config.Config;
import com.csharp.patch.xml.PatchXMLReader;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.unity.entity.FuncFixData;
import com.unity.entity.FuncFixDataWithCallBack;
import com.unity.entity.PerfFixData;

import edu.util.fileprocess.CSVReaderWriter;

public class CallGraphBasedFuncFixCommit {
	private List<String> topCallBacks;

	public CallGraphBasedFuncFixCommit() {
		topCallBacks = new ArrayList<>();
		topCallBacks.add("Update_0");
		topCallBacks.add("Start_0");
		topCallBacks.add("OnEnable_0");
		topCallBacks.add("Awake_0");
		topCallBacks.add("OnCollisionEnter_1");
		topCallBacks.add("OnDestroy_0");
		topCallBacks.add("OnTriggerStay_1");
		topCallBacks.add("OnDisable_0");
		topCallBacks.add("LateUpdate_0");
		topCallBacks.add("OnTriggerEnter_1");
	}

	public void generateFuncChageCountAnalsysis(List<PerfFixData> fixlist) {

		Map<String, List<Integer>> funcmap = new HashMap<String, List<Integer>>();
		// Map<String, List<String>> funccalllist = new HashMap<String, List<String>>();

		List<FuncFixData> funcdatalist = new ArrayList<>();

		for (PerfFixData fix : fixlist) {
			PatchXMLReader xmlreader = new PatchXMLReader();
			List<String> funclist = xmlreader.getPatchFuncList(fix.getId());
			String projname = fix.getProjName();

			System.out.println(fix.getId());

			CommitAnalyzer cmtanalyzer = null;
			try {
				cmtanalyzer = new CommitAnalyzer("test", projname);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			List<ClassFunction> classfunclist = cmtanalyzer.getClassFunctionCall(fix.getFixCommitID());

			List<String> funcwithallimpacted = new ArrayList<>();

			if (funclist != null && funclist.size() > 0)
				funcwithallimpacted.addAll(funclist);

			// funclist
			if (funclist != null && funclist.size() > 0) {
				for (String func : funclist) {
					List<String> impactedfunc = getFunctionCallSourceList(func, classfunclist);

					if (impactedfunc != null) {
						for (String impmethod : impactedfunc) {
							if (!funcwithallimpacted.contains(impmethod)) {
								funcwithallimpacted.add(impmethod);
							}
						}
					}

				}
			}

			
			if (funcwithallimpacted != null) {
				for (String func : funcwithallimpacted) {
					if (funcmap.containsKey(func)) {
						if (!funcmap.get(func).contains(fix.getId())) {
							funcmap.get(func).add(fix.getId());
							// funccalllist.get(func).add(callsrc);
						}

					} else {
						List<Integer> projids = new ArrayList<>();
						projids.add(fix.getId());
						funcmap.put(func, projids);
					}
				}
			}
		}
		
		

		for (String entry : funcmap.keySet()) {
			FuncFixData funcdata = new FuncFixData();
			// FuncFixData funcdata = new FuncFixData();
			funcdata.setFuncName(entry.toString());
			funcdata.setFuncCount(funcmap.get(entry).size());
			funcdata.setCommitList(funcListToString(funcmap.get(entry)));

			// List<String> caller = funccalllist.get(entry);
			// boolean userdefined = isUserDefined(caller);
			// if (userdefined)
			// funcdata.setFuncType("userdefined");
			//
			// funcdata.setFuncCaller(methodListToString(caller));

			funcdatalist.add(funcdata);

		}

		CSVReaderWriter writer = new CSVReaderWriter();
		try {
			writer.writeListBean(funcdatalist, Config.rootDir + "funcchange_with_callgraph_commitlist_July29.csv",
					FuncFixData.class);
		} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Done");
	}

	private String funcListToString(List<Integer> list) {
		StringBuilder strbl = new StringBuilder();

		for (Integer val : list) {
			strbl.append(Integer.toString(val));
			strbl.append(">");
		}

		return strbl.toString();
	}

	public String getFunctionCallSource(String func, List<ClassFunction> classfunclist) {
		String funcsrc = "xxx";

		if (func.length() <= 0)
			return funcsrc;
		// Creating the queue, and adding the first node (step 1)
		LinkedList<String> queue = new LinkedList<>();
		queue.add(func);
		Map<String, Boolean> visited = new HashMap<>();
		visited.put(func, true);

		while (!queue.isEmpty()) {
			String currentFirst = queue.removeFirst();

			// In some cases we might have added a particular node more than once before
			// actually visiting that node, so we make sure to check and skip that node if
			// we have
			// encountered it before

			// funcsrc =
			String newfuncsrc = getFunctionSource(currentFirst, classfunclist);//

			if (!funcsrc.equals("xxx") && newfuncsrc.equals("xxx")) {
				funcsrc = funcsrc;
			} else if (funcsrc.equals("xxx") && !newfuncsrc.equals("xxx")) {
				funcsrc = newfuncsrc;
			} else if (!funcsrc.equals("xxx") && !newfuncsrc.equals("xxx")) {
				funcsrc = newfuncsrc;
			}

			if (topCallBacks.contains(funcsrc)) {
				break;
			}

			else {
				if (!funcsrc.equals("xxx") && !visited.containsKey(funcsrc)) {
					queue.add(funcsrc);
					visited.put(funcsrc, true);

				}
			}
		}

		return funcsrc;
	}

	public List<String> getFunctionCallSourceList(String func, List<ClassFunction> classfunclist) {
		String funcsrc = "xxx";
		List<String> funclist = new ArrayList<>();

		if (func.length() <= 0)
			return null;
		// Creating the queue, and adding the first node (step 1)
		LinkedList<String> queue = new LinkedList<>();
		queue.add(func);
		Map<String, Boolean> visited = new HashMap<>();
		visited.put(func, true);

		while (!queue.isEmpty()) {
			String currentFirst = queue.removeFirst();

			// In some cases we might have added a particular node more than once before
			// actually visiting that node, so we make sure to check and skip that node if
			// we have
			// encountered it before

			// funcsrc =
			String newfuncsrc = getFunctionSource(currentFirst, classfunclist);//

			if (!funcsrc.equals("xxx") && newfuncsrc.equals("xxx")) {
				funcsrc = funcsrc;
			} else if (funcsrc.equals("xxx") && !newfuncsrc.equals("xxx")) {
				funcsrc = newfuncsrc;
				funclist.add(newfuncsrc);
			} else if (!funcsrc.equals("xxx") && !newfuncsrc.equals("xxx")) {
				funclist.add(newfuncsrc);
				funcsrc = newfuncsrc;
			}

			if (topCallBacks.contains(funcsrc)) {
				// funclist.add(newfuncsrc);
				break;
			}

			else {
				if (!funcsrc.equals("xxx") && !visited.containsKey(funcsrc)) {
					queue.add(funcsrc);
					visited.put(funcsrc, true);

				}
			}
		}

		return funclist;
	}

	public String getFunctionSource(String func, List<ClassFunction> classfunclist) {
		String source = "xxx";
		boolean found = false;

		for (ClassFunction clsfunc : classfunclist) {
			List<FunctionCall> funccalllist = clsfunc.getFuncNameList();

			if (clsfunc.getClassname() != null && clsfunc.getClassname().equals("GazeStabilizer")) {
				System.out.print("test");
			}

			for (FunctionCall funccall : funccalllist) {
				if (funccall.getFuncCallList().contains(func) && !funccall.equals(func)) {
					source = funccall.getFuncName();
					found = true;
					break;
				}
			}

			if (found == true) {
				break;
			}
		}

		return source;

	}

	public boolean isUserDefined(List<String> calllist) {
		boolean userdefined = false;

		for (String call : calllist) {
			if (!call.equals("xxx")) {
				userdefined = true;
				break;
			}
		}

		return userdefined;
	}

	private String methodListToString(List<String> list) {
		StringBuilder strbl = new StringBuilder();

		for (String val : list) {
			strbl.append(val);
			strbl.append(">");
		}

		return strbl.toString();
	}

}
