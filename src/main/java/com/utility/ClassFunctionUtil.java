package com.utility;

import java.util.Arrays;
import java.util.List;

public class ClassFunctionUtil {
	
	public static String getFunctionNameFromClsFunc(String clsfuncname)
	{
		String func="";
		
		String[] tokens=clsfuncname.split("@@");
		List<String> tokenlist = Arrays.asList(tokens); 
		
		if(tokenlist.size()>1)
		{
			func=tokenlist.get(tokenlist.size()-1);
		}
		
		return func;
	}
	
	public static String getClassNameFromClsFunc(String clsfuncname)
	{
		String classname="";
		
		String[] tokens=clsfuncname.split("@@");
		List<String> tokenlist = Arrays.asList(tokens); 
		
		if(tokenlist.size()>0)
		{
			classname=tokenlist.get(0);
		}
		
		return classname;
	}
	
	public static String getClassFuncName(String classname,String funcname)
	{
		String name="";
		
		name=classname+"@@"+funcname;
		
		return name;
	}

}
