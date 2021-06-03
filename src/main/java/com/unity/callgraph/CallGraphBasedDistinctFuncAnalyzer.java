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
import com.unity.entity.FuncFixDataWithCallBack;
import com.unity.entity.PerfFixData;
import com.utility.ClassFunctionUtil;

import edu.util.fileprocess.CSVReaderWriter;

public class CallGraphBasedDistinctFuncAnalyzer {
	private List<String> topCallBacks;

	public CallGraphBasedDistinctFuncAnalyzer() {
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

	public void generateFuncChageData(List<PerfFixData> fixlist) {

		Map<String, List<Integer>> funcmap = new HashMap<String, List<Integer>>();
		// Map<String, List<String>> funccalllist = new HashMap<String, List<String>>();

		Map<Integer, List<String>> commitfuncmap = new HashMap<Integer, List<String>>();

		List<FuncFixDataWithCallBack> funcdatalist = new ArrayList<>();

		for (PerfFixData fix : fixlist) {
			PatchXMLReader xmlreader = new PatchXMLReader();
			List<String> funclist = xmlreader.getPatchClassFuncList(fix.getId());
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
			List<String> allimpactedfuncs = new ArrayList<>();

			if (funclist != null) {
				for (String func : funclist) {
					if (!allimpactedfuncs.contains(func)) {
						allimpactedfuncs.add(func);
					}
					List<String> impactedfuncs = getClassFunctionCallSourceList(func, classfunclist);

					for (String impfunc : impactedfuncs) {
						if (!allimpactedfuncs.contains(impfunc)) {
							allimpactedfuncs.add(impfunc);
						}
					}
				}

			}

			commitfuncmap.put(fix.getId(), allimpactedfuncs);
		}

		for (Integer key : commitfuncmap.keySet()) {
			List<String> impactedfunclist = commitfuncmap.get(key);

			for (String impactedfunc : impactedfunclist) {

				String func = ClassFunctionUtil.getFunctionNameFromClsFunc(impactedfunc);
				if (funcmap.containsKey(func)) {
					//if (!funcmap.get(func).contains(key)) {
						funcmap.get(func).add(key);
					//}

				} else {
					List<Integer> projids = new ArrayList<>();
					projids.add(key);
					funcmap.put(func, projids);

				}
			}

		}

		for (String entry : funcmap.keySet()) {
			FuncFixDataWithCallBack funcdata = new FuncFixDataWithCallBack();
			// FuncFixData funcdata = new FuncFixData();
			funcdata.setFuncName(entry.toString());
			funcdata.setFuncCount(funcmap.get(entry).size());
			funcdata.setCommitList(funcListToString(funcmap.get(entry)));

			funcdata.setFuncCaller("");

			funcdatalist.add(funcdata);

		}

		CSVReaderWriter writer = new CSVReaderWriter();
		try {
			writer.writeListBean(funcdatalist, Config.rootDir + "funcchange_with_callgraph_distinct_updated_June30.csv",
					FuncFixDataWithCallBack.class);
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

	public List<String> getClassFunctionCallSourceList(String classfunc, List<ClassFunction> classfunclist) {
		String funcsrc = "xxx";
		List<String> funclist = new ArrayList<>();
		String func = ClassFunctionUtil.getFunctionNameFromClsFunc(classfunc);

		if (func.length() <= 0)
			return funclist;
		// Creating the queue, and adding the first node (step 1)
		LinkedList<String> queue = new LinkedList<>();
		queue.add(func);
		Map<String, Boolean> visited = new HashMap<>();
		visited.put(classfunc, true);

		while (!queue.isEmpty()) {
			String currentFirst = queue.removeFirst();

			// In some cases we might have added a particular node more than once before
			// actually visiting that node, so we make sure to check and skip that node if
			// we have
			// encountered it before

			// funcsrc =
			String newfuncsrc = getClassFunctionSource(currentFirst, classfunclist);//

			if (!funcsrc.equals("xxx") && newfuncsrc.equals("xxx")) {
				funcsrc = funcsrc;
			} else if (funcsrc.equals("xxx") && !newfuncsrc.equals("xxx")) {
				funcsrc = newfuncsrc;
				funclist.add(newfuncsrc);
			} else if (!funcsrc.equals("xxx") && !newfuncsrc.equals("xxx")) {
				funclist.add(newfuncsrc);
				funcsrc = newfuncsrc;
			}

			String funconly = ClassFunctionUtil.getFunctionNameFromClsFunc(funcsrc);
			if (topCallBacks.contains(funconly)) {
				// funclist.add(newfuncsrc);
				break;
			}

			else {
				if (!funcsrc.equals("xxx") && !visited.containsKey(funcsrc)) {
					funconly = ClassFunctionUtil.getFunctionNameFromClsFunc(funcsrc);
					// queue.add(funcsrc);
					queue.add(funconly);
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

	public String getClassFunctionSource(String func, List<ClassFunction> classfunclist) {
		String source = "xxx";
		boolean found = false;

		for (ClassFunction clsfunc : classfunclist) {
			List<FunctionCall> funccalllist = clsfunc.getFuncNameList();
			String classname = clsfunc.getClassname();

			for (FunctionCall funccall : funccalllist) {
				if (funccall.getFuncCallList().contains(func) && !funccall.equals(func)) {
					source = ClassFunctionUtil.getClassFuncName(classname, funccall.getFuncName());
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
