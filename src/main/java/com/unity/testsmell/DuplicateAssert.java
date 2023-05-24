package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;
import com.unity.testanalyzer.LineCountAssertCount;

import java.util.*;

public class DuplicateAssert {
    private boolean duplicate = false;


    public void getSmell(ITree root)
    {

    }


    public Map<String,Boolean> searchForDuplicateAssert(ITree root)
    {

        List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
        Map<String,List<AssertCall>> testfuncassertmap=new HashMap<>();
        Map<String,Boolean> duplicateAssert=new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");
        if(classnode==null)
            return duplicateAssert;

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

        String lowerclassname = classname.getLabel();
        for(ITree testfunc:testfunclist)
        {
            List<ITree> assertlist=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "assert");
            Set<ITree> checkDuplicate = new HashSet<>();
            List<ITree> duplicateList = new ArrayList<>();
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
            List<AssertCall> assercalllist=new ArrayList<>();
            String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
            if(assertlist!=null && assertlist.size()>0)
            {
                for(ITree assertitem:assertlist)
                {
                    System.out.println(assertitem);
                    if(checkDuplicate.contains(assertitem))
                    {
                        duplicateList.add(assertitem);
                    }

                }
            }
            if(duplicateList.size() >= 1)
            {
                duplicate = true;
            }
            duplicateAssert.put(classtestfunc, duplicate);
            duplicate = false;
        }

        return duplicateAssert;
    }


    public double getDuplicateAssertTestStats(Map<String,Boolean> testfunccondition)
    {

        int total=testfunccondition.keySet().size();
        if (total == 0)
            return -0.001;

        int duplicateAssert=0;

        for(boolean b:testfunccondition.values()){
            if(b)
                duplicateAssert++;
        }

        return  (double) duplicateAssert/total;
    }
}
