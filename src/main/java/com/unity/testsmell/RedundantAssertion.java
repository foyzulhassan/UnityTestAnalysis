package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class RedundantAssertion {

    private boolean redundantassertion = false;

    public void getSmell(ITree root)
    {

    }

    public Map<String,Boolean> searchForRedundantAssertion(ITree root)
    {
        List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
        Map<String,List<AssertCall>> testfuncassertmap=new HashMap<>();
        Map<String,Boolean> redundantAssertion=new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if(classnode==null)
            return redundantAssertion;

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

        String lowerclassname = classname.getLabel();


        for(ITree testfunc:testfunclist)
        {
            List<ITree> redundantlist=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "assert");
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//			List<AssertCall> assercalllist=new ArrayList<>();
            String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();

            if(redundantlist!=null && redundantlist.size()>0)
            {

                redundantassertion = IsRedundantAssertionFound(redundantlist);

            }

            //sensitiveEquality.put(classtestfunc,toStringFound);
            redundantAssertion.put(classtestfunc, redundantassertion);

            redundantassertion = false;



        }


        return redundantAssertion;


    }

    private boolean IsRedundantAssertionFound(List<ITree>foundList) {
        boolean isfound = false;

            for (ITree num : foundList) {
                AssertCall assertCall = TreeNodeAnalyzer.getAssertCall(num);

                if (assertCall.getParamList().size() > 0) {
                    int count =0, count2=0;
                    for (int i = 0; i < assertCall.getParamList().size(); i++) {

                        for(ITree iTree: assertCall.getParamListTrees()){
                            for(ITree tc : iTree.getChildren()){
                                if(tc.getLabel().equals("true"))
                                {
                                    //isfound = true;
                                    count++;
                                }
                                else if(tc.getLabel().equals("false"))
                                {
                                    count2++;
                                }
                                for(ITree tcc : tc.getChildren()){
                                    if(tcc.getLabel().equals("true"))
                                    {
                                        //isfound = true;
                                        count++;

                                    }
                                    else if(tcc.getLabel().equals("false"))
                                    {
                                        count2++;
                                    }

                                    for(ITree tch : tcc.getChildren()){
                                        if(tch.getLabel().equals("true"))
                                        {
                                            //isfound = true;
                                            count++;
                                        }
                                        else if(tch.getLabel().equals("false"))
                                        {
                                            count2++;
                                        }

                                        }

                                    }

                                }
                            }
                        break;
                        }
                    if(count == assertCall.getParamList().size() || count2 == assertCall.getParamList().size())
                    {
                        isfound = true;
                    }

                    }
                }
        return isfound;
    }



    public double getRedundantAssertionStats(Map<String,Boolean> testredundantassertion)
    {

        int total=testredundantassertion.keySet().size();
        if (total == 0)
            return -0.001;

        int redundantassertion=0;

        for(boolean b:testredundantassertion.values()){
            if(b)
                redundantassertion++;
        }

        return  (double) redundantassertion/total;
    }

}