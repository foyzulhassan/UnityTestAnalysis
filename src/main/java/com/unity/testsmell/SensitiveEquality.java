package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensitiveEquality {

private boolean toStringFound = false;

	public void getSmell(ITree root)
	{

	}

	public Map<String,Boolean> searchForSensitiveEquality(ITree root)
	{
		List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
		Map<String,Boolean> sensitiveEquality=new HashMap<>();
		ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

		if(classnode==null)
			return sensitiveEquality;

		ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

		String lowerclassname = classname.getLabel();


		for(ITree testfunc:testfunclist)
		{
			List<ITree> if_stmt_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "ToString");
			ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//			List<AssertCall> assercalllist=new ArrayList<>();
			String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
			toStringFound = false;

			if(if_stmt_list!=null && if_stmt_list.size()>0)
			{
				toStringFound=true;
			}

            sensitiveEquality.put(classtestfunc,toStringFound);

		}
        return sensitiveEquality;
	}




	public double getSensitiveEqualityStats(Map<String,Boolean> testfuncconditionalTestmap)
	{

        int total=testfuncconditionalTestmap.keySet().size();
        if (total == 0)
            return -0.001;

        int sensitiveEquality=0;

        for(boolean b:testfuncconditionalTestmap.values()){
            if(b)
                sensitiveEquality++;
        }

		return  (double) sensitiveEquality/total;
	}
}
