package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IgnoredTest {

    private boolean ignoreFound = false;

    public void getSmell(ITree root)
    {

    }

    public Map<String,Boolean> searchForIgnoredTest(ITree root)
    {
        List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
        Map<String,Boolean> ignoredTest=new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if(classnode==null)
            return ignoredTest;

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

        String lowerclassname = classname.getLabel();


        for(ITree testfunc:testfunclist)
        {
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
            String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
            List<ITree> ignorelist = TreeNodeAnalyzer.getIgnoredFunctionList(testfunc);

            for(ITree ignorefunc : ignorelist) {
                if(ignorefunc.getLabel().equals("Ignore"))
                {
                    ignoreFound = true;
                }
            }

            ignoredTest.put(classtestfunc,ignoreFound);

            ignoreFound = false;





        }


        return ignoredTest;


    }





    public double getIgnoredTestStats(Map<String,Boolean> testignoredTestmap)
    {

        int total=testignoredTestmap.keySet().size();
        if (total == 0)
            return -0.001;

        int redundantPrint=0;

        for(boolean b:testignoredTestmap.values()){
            if(b)
                redundantPrint++;
        }

        return  (double) redundantPrint/total;
    }
}