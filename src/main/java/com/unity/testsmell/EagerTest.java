package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class EagerTest {

    public boolean sub_tree_matcher(ITree tree1, ITree tree2, boolean objectMatch)
    {
        if ( tree1.getType() == tree2.getType() && Objects.equals(tree1.getLabel(), tree2.getLabel()))
        {
            if (tree1.isLeaf() && tree2.isLeaf()){
                return true;
            } else if (objectMatch){
                return false;
            } else{
                if(tree1.getChildren().size() != tree2.getChildren().size())
                {
                    return false;
                }
                boolean total=true;
                for (int counter = 0; counter<tree1.getChildren().size();counter++){
                    total=total && sub_tree_matcher(tree1.getChild(counter),tree2.getChild(counter),false);
                }
                return total;
            }

        }
        return false;
    }





    public void getSmell(ITree root)
	{

	}

	public Map<String,Boolean> searchForEagerTest(ITree root)
	{
		List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
        List<ITree> testfunclistCopy = new ArrayList<>();
        for( ITree testfunc: testfunclist){
            ITree copy = testfunc.deepCopy();
            testfunclistCopy.add(copy);
        }
		Map<String,Boolean> EagerTest=new HashMap<>();
		ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

		if(classnode==null)
			return EagerTest;

		ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

		String lowerclassname = classname.getLabel();

        Map<ITree,Integer> funcs_map = new HashMap<>();
        for(ITree testfunc:testfunclist)
		{
            List<ITree> assertlist=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "assert");
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//            List<AssertCall> assercalllist=new ArrayList<>();
            String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
            List<ITree> paramSet=new ArrayList<>();
            List<AssertCall> assercalllist=new ArrayList<>();
            if(assertlist!=null && assertlist.size()>0)
            {
                for(ITree assertitem:assertlist)
                {
                    AssertCall assertcall=TreeNodeAnalyzer.getAssertCall(assertitem);
                    assercalllist.add(assertcall);
                }
            }
            for( AssertCall assertCall:assercalllist){
                List<ITree> paramITrees =assertCall.getParamListTrees();
                 for(ITree param:paramITrees){
                     if(isProperty(param)){
                         continue;
                     }
                     boolean alreadyInList = false;
                     for(ITree paramFromList:paramSet){
                         if(sub_tree_matcher(paramFromList,param,false)){
                             alreadyInList=true;
                             break;
                         }
                     }
                     if(!alreadyInList){
                         paramSet.add(param);
                     }

                 }
            }
            if(paramSet.size() >1 ){
                EagerTest.put(classtestfunc,true);
            } else {
                EagerTest.put(classtestfunc,false);
            }

        }

        return EagerTest;


	}

    private boolean isProperty(ITree param) {
        ITree local_tree= param.deepCopy();
        for (ITree ch: local_tree.getChildren()){
            List<ITree> argList = TreeNodeAnalyzer.getSearchTypeLabel(ch,"argument_list","");
            if (argList.size() > 0)
            {
                return false;
            }
        }
        return true;
    }


    public double getEagerTestStats(Map<String,Boolean> testfuncconditionalTestmap)
	{

        int total=testfuncconditionalTestmap.keySet().size();
        if (total == 0)
            return -0.001;

        int sensitiveEquality=0;

        for(boolean b:testfuncconditionalTestmap.values()){
            if(b)
                sensitiveEquality++;
        }

		return  (double) sensitiveEquality/total;
	}
}
