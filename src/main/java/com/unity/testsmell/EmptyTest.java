package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmptyTest {

    private boolean empty = false;

    public void getSmell(ITree root)
    {

    }

    public Map<String,Boolean> searchForEmptyTest(ITree root)
    {
        List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
        Map<String,Boolean> emptyTest=new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if(classnode==null)
            return emptyTest;

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

        String lowerclassname = classname.getLabel();


        for(ITree testfunc:testfunclist)
        {
           // List<ITree> sleepylist=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "thread");
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
            List<ITree> funccontent = TreeNodeAnalyzer.getStatementList(testfunc);
            List<ITree> funcexprcontent = TreeNodeAnalyzer.getExprStatementList(testfunc);
            String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();


//
//            if(sleepylist!=null && sleepylist.size()>0)
//            {
//                sleepFound = SleepFound(sleepylist);
//            }
            if(funccontent.isEmpty() && funcexprcontent.isEmpty())
            {

                empty = true;
            }

            emptyTest.put(classtestfunc,empty);

            empty =false;





        }


        return emptyTest;


    }

//    private boolean SleepFound(List<ITree>foundList) {
//        boolean isfound = false;
//
//        for (ITree sleep : foundList) {
//            if(sleep.getLabel().contains("Sleep")) {
//                isfound = true;
//            }
//            else{
//                isfound = false;
//            }
//        }
//        return isfound;
//    }




    public double getEmptyTestStats(Map<String,Boolean> testredundantprintTestmap)
    {

        int total=testredundantprintTestmap.keySet().size();
        if (total == 0)
            return -0.001;

        int redundantPrint=0;

        for(boolean b:testredundantprintTestmap.values()){
            if(b)
                redundantPrint++;
        }

        return  (double) redundantPrint/total;
    }
}