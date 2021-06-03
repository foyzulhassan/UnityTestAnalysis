package com.csharp.diff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.build.analyzer.entity.CSharpChange;
import com.build.commitanalyzer.CommitAnalyzer;
import com.config.Config;
import com.csharp.patch.xml.PatchXMLGenerator;
import com.csharp.patch.xml.PatchXMLReader;
import com.github.gumtreediff.actions.EditScript;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.unity.entity.FuncFixData;
import com.unity.entity.PerfFixData;
import com.utility.ProjectPropertyAnalyzer;

import edu.util.fileprocess.CSVReaderWriter;

public class CSharpDiffGenMngr {

	public void generateCSharpDiff(List<PerfFixData> fixlist) {

		for (PerfFixData fix : fixlist) {
			String projname = fix.getProjName();

			CommitAnalyzer cmtanalyzer = null;

			try {
				cmtanalyzer = new CommitAnalyzer("test", projname);
				List<EditScript> actionlist = cmtanalyzer.extractCSharpFileChange(fix.getFixCommitID());
				CSharpDiffGenerator diffgen = new CSharpDiffGenerator();

				List<CSharpChange> changelist = diffgen.getCSharpChangeList(actionlist, fix.getId());
				PatchXMLGenerator xmlgen = new PatchXMLGenerator();
				// String strxml=xmlgen.getXMLPatch(changelist, 1);
				xmlgen.writeXMLPatch(changelist, fix.getId());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void generateFuncChageData(List<PerfFixData> fixlist) {

		Map<String, List<Integer>> funcmap = new HashMap<String, List<Integer>>();
		List<FuncFixData> funcdatalist = new ArrayList<>();

		for (PerfFixData fix : fixlist) {
			PatchXMLReader xmlreader = new PatchXMLReader();
			List<String> funclist = xmlreader.getPatchFuncList(fix.getId());

			if (funclist != null) {
				for (String func : funclist) {
					if (funcmap.containsKey(func)) {
						if(!funcmap.get(func).contains(fix.getId()))
						{
							funcmap.get(func).add(fix.getId());
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
			funcdata.setFuncName(entry.toString());
			funcdata.setFuncCount(funcmap.get(entry).size());
			funcdata.setCommitList(funcListToString(funcmap.get(entry)));
			funcdatalist.add(funcdata);
		}

		CSVReaderWriter writer = new CSVReaderWriter();
		try {
			writer.writeListBean(funcdatalist, Config.rootDir + "funcchange_updated_July23.csv", FuncFixData.class);
		} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private String funcListToString(List<Integer> list)
	{
		StringBuilder strbl=new StringBuilder();
		
		for(Integer val:list)
		{
			strbl.append(Integer.toString(val));
			strbl.append(">");
		}
		
		return strbl.toString();
	}
	
	

}
