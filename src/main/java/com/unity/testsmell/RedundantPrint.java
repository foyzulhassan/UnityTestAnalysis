package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedundantPrint {

    private boolean printFound = false;

    public void getSmell(ITree root)
    {

    }

    public Map<String,Boolean> searchForRedundantPrint(ITree root)
    {
        List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
        Map<String,Boolean> redundantPrint=new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if(classnode==null)
            return redundantPrint;

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

        String lowerclassname = classname.getLabel();


        for(ITree testfunc:testfunclist)
        {
            List<ITree> printlist=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "Console");
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
            String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();


            if(printlist!=null && printlist.size()>0)
            {
                printFound = RedPrintFound(printlist);
            }

            redundantPrint.put(classtestfunc,printFound);





        }


        return redundantPrint;


    }

    private boolean RedPrintFound(List<ITree>foundList) {
        boolean isfound = false;

        for (ITree print : foundList) {
            if(print.getLabel().contains("Write") || print.getLabel().contains("WriteLine")) {
                isfound = true;
            }
            else{
                isfound = false;
            }
        }
        return isfound;
    }




    public double getRedundantPrintStats(Map<String,Boolean> testredundantprintTestmap)
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