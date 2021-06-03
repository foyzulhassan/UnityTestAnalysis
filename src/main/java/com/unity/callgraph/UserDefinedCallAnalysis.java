package com.unity.callgraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.config.Config;
import com.csharp.changesize.ChangeSize;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.unity.entity.FuncFixDataWithCallBack;
import com.unity.entity.PerfFixData;

import edu.util.fileprocess.CSVReaderWriter;

public class UserDefinedCallAnalysis {
	
	public void generateCallAnalysis()
	{
		CSVReaderWriter csvrw = new CSVReaderWriter();
		List<FuncFixDataWithCallBack> callbacklist=null;
		List<FuncFixDataWithCallBack> callbacklistfilterd=new ArrayList<>();
		try {
			callbacklist = csvrw.getListBeanFromCSV(Config.rootDir + "callgraph.csv",FuncFixDataWithCallBack.class);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(FuncFixDataWithCallBack clback:callbacklist)
		{
			if(clback.getFuncType().equals("userdefined"))
			{
				callbacklistfilterd.add(clback);
			}
		}	
		
		List<CallerFrequency> callfreqlist=calcuateCallerFrequency(callbacklistfilterd,callbacklist);
		
		CSVReaderWriter writer = new CSVReaderWriter();
		try {
			writer.writeListBean(callfreqlist, Config.rootDir + "usefunc_call_freq_July24_1.csv", CallerFrequency.class);
		} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done\n");	
		
	}
	
	public List<CallerFrequency> calcuateCallerFrequency(List<FuncFixDataWithCallBack> filteredcalllist, List<FuncFixDataWithCallBack> calllist)
	{
		Map<String,Integer> callercounter=new HashMap<>();
		List<CallerFrequency> callfrequencylist=new ArrayList<>();
		
		for(FuncFixDataWithCallBack call:filteredcalllist)
		{
			String callerlist=call.getFuncCaller();			
		    
		    List<String> callers = Arrays.asList(callerlist.split(">")); 
		    
		    for(String caller:callers)
		    {
		    	int count = callercounter.containsKey(caller) ? callercounter.get(caller) : 0;
		    	callercounter.put(caller, count + 1);
		    }
		}	
		
		
		for(FuncFixDataWithCallBack func:calllist)
		{
			String funcname=func.getFuncName();
			int count=func.getFuncCount();
			
			if(callercounter.containsKey(funcname))
			{
				callercounter.put(funcname, callercounter.get(funcname) + count);
			}
			else
			{
				callercounter.put(funcname, count);
			}
			
		}
		for(String key:callercounter.keySet())
		{
			int count=callercounter.get(key);			
			CallerFrequency callfreq=new CallerFrequency(key,count);
			callfrequencylist.add(callfreq);			
		}
		
		return callfrequencylist;
		
	}

}
