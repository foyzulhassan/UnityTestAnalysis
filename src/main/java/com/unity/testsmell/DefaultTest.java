package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultTest {

    private boolean toStringFound = false;

    public void getSmell(ITree root)
    {

    }

    public Map<String,Boolean> searchForDefaultTest(ITree root)
    {
        List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
        Map<String,Boolean> defaultTest=new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if(classnode==null)
            return defaultTest;

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

        String lowerclassname = classname.getLabel();
        if(lowerclassname.equals("ExampleUnitTest") || lowerclassname.equals("ExampleInstrumentedTest"))
        {
            toStringFound = true;
        }
        defaultTest.put(lowerclassname,toStringFound);
        toStringFound = false;


//        for(ITree testfunc:testfunclist)
//        {
//            List<ITree> if_stmt_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "ToString");
//            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
////			List<AssertCall> assercalllist=new ArrayList<>();
//            String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
//
//            if(if_stmt_list!=null && if_stmt_list.size()>0)
//            {
//                toStringFound=true;
//            }
//
//            defaultTest.put(classtestfunc,toStringFound);
//
//            toStringFound = false;
//
//
//
//        }


        return defaultTest;


    }




    public double getDefaultTestStats(Map<String,Boolean> testfuncconditionalTestmap)
    {

        int total=testfuncconditionalTestmap.keySet().size();
        if (total == 0)
            return -0.001;

        int defaultTest=0;

        for(boolean b:testfuncconditionalTestmap.values()){
            if(b)
                defaultTest++;
        }

        return  (double) defaultTest/total;
    }
}
