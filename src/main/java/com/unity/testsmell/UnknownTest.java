package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnknownTest {

    private boolean unknownFound = false;

    public void getSmell(ITree root)
    {

    }

    public Map<String,Boolean> searchForUnknownTest(ITree root)
    {
        List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
        Map<String,Boolean> unknownTest=new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if(classnode==null)
            return unknownTest;

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

        String lowerclassname = classname.getLabel();


        for(ITree testfunc:testfunclist)
        {
            List<ITree> unknownlist=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "assert");
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
            String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();


            if(unknownlist.isEmpty())
            {
                unknownFound = true;
            }

            unknownTest.put(classtestfunc,unknownFound);

            unknownFound = false;


        }


        return unknownTest;


    }

    public double getUnknownTestStats(Map<String,Boolean> testunknownTestmap)
    {

        int total=testunknownTestmap.keySet().size();
        if (total == 0)
            return -0.001;

        int unknown=0;

        for(boolean b:testunknownTestmap.values()){
            if(b)
                unknown++;
        }

        return  (double) unknown/total;
    }
}