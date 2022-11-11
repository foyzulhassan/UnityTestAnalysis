package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExceptionCatchingThrowing {

    private boolean exceptionFound = false;

    public void getSmell(ITree root)
    {

    }

    public Map<String,Boolean> searchForExceptionTest(ITree root)
    {
        List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
        Map<String,Boolean> exceptionTest=new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if(classnode==null)
            return exceptionTest;

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

        String lowerclassname = classname.getLabel();


        for(ITree testfunc:testfunclist)
        {
            List<ITree> catchlist=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "catch");
            List<ITree> throwlist=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "throws");
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);

            String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();


            if(!catchlist.isEmpty() || !throwlist.isEmpty())
            {

                exceptionFound = true;
            }

            exceptionTest.put(classtestfunc,exceptionFound);

            exceptionFound =false;





        }


        return  exceptionTest;


    }




    public double getExceptionTestStats(Map<String,Boolean> testexceptionTestmap)
    {

        int total=testexceptionTestmap.keySet().size();
        if (total == 0)
            return -0.001;

        int exception=0;

        for(boolean b:testexceptionTestmap.values()){
            if(b)
                exception++;
        }

        return  (double) exception/total;
    }
}