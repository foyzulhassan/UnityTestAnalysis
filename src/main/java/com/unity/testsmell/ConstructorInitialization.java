package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConstructorInitialization {

    private boolean constructorFound = false;
    List<Integer>foo;


    public void getSmell(ITree root)
    {

    }

    public Map<String,Boolean> searchForConstructorInitialization(ITree root)
    {
        List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
        List<ITree> funclist= TreeNodeAnalyzer.getFunctionList(root);
        List<ITree> mainlist=null;
        List<ITree> actuallist=null;
        //List<ITree> testfunclist=TreeNodeAnalyzer.getFunctionList(root);
        Map<String,Boolean> constructorInitialization=new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if(classnode==null)
            return constructorInitialization;

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

        String lowerclassname = classname.getLabel();

        String[] extensions = {"Tests", "Test"};
//        for(String val : extensions)
//        {
//
//            if(lowerclassname.endsWith(val))
//            {
//                foo.add(1);
//            }
//            else
//                continue;
//            System.out.println(foo.size());
//        }
//        for(String entry : extensions)
//        {
//            if(classname.getLabel().endsWith(entry))
//            {
//               // x++;
//                mainlist=TreeNodeAnalyzer.getConstructorList(root);
//            }
//        }
//
//        if(testfunclist!=null &&testfunclist.size()>0)
//        {
//            mainlist=TreeNodeAnalyzer.getConstructorList(root);
//        }

//        if(testfunclist!=null && testfunclist.size()>0 && funclist!=null && funclist.size()>0)
//        {
//            for(ITree node:funclist)
//            {
//                for(ITree node2: node.getChildren())
//                {
//                    for (ITree node3:testfunclist){
//                        for (ITree node4: node3.getChildren()){
//                            if(node4.getLabel()!=node2.getLabel())
//                            {
//                                mainlist.add(node);
//                            }
//
//                        }
//                    }
//
//                }
//
//            }
//        }
//        if(mainlist!=null &&mainlist.size()>0)
//        {
//            actuallist=TreeNodeAnalyzer.getConstructorList(root);
//        }
        if(testfunclist!=null && testfunclist.size()>0)
        {
            for(ITree search : testfunclist)
            {
               // ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(search);
                //String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
                mainlist = TreeNodeAnalyzer.getConstructorList(root);
                if(mainlist!=null && mainlist.size()>0)
                {
                    for(ITree search1 : mainlist) {
                        ITree funcname = SrcmlUnityCsMetaDataGenerator.getFuncName(search1);
                       // String classtestfunc=lowerclassname+Config.separatorStr+funcname.getLabel();

                        if(funcname.getLabel().equals(lowerclassname)) {
                            constructorFound = true;
                        }

                    }
                }
                constructorInitialization.put(lowerclassname, constructorFound);


            }
        }


        return constructorInitialization;
    }

    public double getConstructorInitializationStats(Map<String,Boolean> testconstructorTestmap)
    {

        int total=testconstructorTestmap.size();
        if (total == 0)
            return -0.001;

        int constructorInitialization=0;

        for(boolean b:testconstructorTestmap.values()){
            if(b)
                constructorInitialization++;
        }

       return  (double) constructorInitialization/total;

    }
}