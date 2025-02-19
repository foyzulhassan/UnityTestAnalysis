package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.*;

public class IgnoredTest {

    private boolean ignoreFound = false;
    boolean IgnoreFound_ = false;

    public void getSmell(ITree root)
    {

    }

    public Map<String,Boolean> searchForIgnoredTest(ITree root) {
        List<ITree> testfunclist = TreeNodeAnalyzer.getTestFunctionList(root);
        Map<String, Boolean> ignoredTest = new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if (classnode == null)
            return ignoredTest;

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

        String lowerclassname = classname.getLabel();
        //String classtestfunc = lowerclassname + Config.separatorStr + classname.getLabel();
       // System.out.println("classtestfunc"+classtestfunc);


        for (ITree testfunc : testfunclist) {

            // Search for the 'Thread' class
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
            String classtestfunc = lowerclassname + Config.separatorStr + funcnamenode.getLabel();
            List<ITree> Ignorelist = TreeNodeAnalyzer.getIgnoredFunctionList(testfunc);
            //System.out.println("ignorelist:"+Ignorelist);
            List<ITree> functionCalls = TreeNodeAnalyzer.getSearchTypeLabel2(testfunc, "call", "");
            if(!functionCalls.isEmpty()){
                for(ITree callnodes: functionCalls) {
                    if (!callnodes.getChildren().isEmpty()) {
                        ITree callnodename = callnodes.getChildren().get(0);
                        //System.out.println("callnamenode: "+callnodename);
                        String matcher = String.valueOf(callnodename.getType());
                        if (Objects.equals(matcher, "name")) {
                            //System.out.println("entered here");
                            String ignore_present = callnodename.getLabel().toLowerCase();
                            //System.out.println("ignorefound:"+ignore_present);
                            //String ignore = "ignore".toLowerCase();
                            if(ignore_present.equals("ignore")){
                                IgnoreFound_ = true;
                            }
                            //System.out.println("ignorefound: "+IgnoreFound_);
                        }
                    }
                }
            }
            //System.out.println("ignore list:"+ Ignorelist);

            if (Ignorelist != null && Ignorelist.size() > 0 || IgnoreFound_) {
                ignoreFound = true;
            }
            else{
                ignoreFound = false;
            }


//        for(ITree testfunc:testfunclist)
//        {
//            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//            String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
//            List<ITree> ignorelist = TreeNodeAnalyzer.getIgnoredFunctionList(testfunc);

//            for(ITree ignorefunc : ignorelist) {
//                if(ignorefunc.getLabel().equals("Ignore"))
//                {
//                    ignoreFound = true;
//                }
//            }
//
//            ignoredTest.put(classtestfunc,ignoreFound);
//
//            ignoreFound = false;
//
//
//
//
//
//        }
            ignoredTest.put(classtestfunc, ignoreFound);
            IgnoreFound_ = false;
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