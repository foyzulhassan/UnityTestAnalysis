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
            Set<String> methodsSet=new HashSet<>();
            if(assertlist!=null && assertlist.size()>0)
            {
                List<AssertCall> assercalllist=new ArrayList<>();
                for(ITree assertitem:assertlist)
                {
                    AssertCall assertcall=TreeNodeAnalyzer.getAssertCall(assertitem);
                    assercalllist.add(assertcall);
                    String method_name = assertcall.getAssertName();
                    methodsSet.add(method_name);
                    //System.out.println("test");
                }
            }
            if(methodsSet.size() >1 ){
                EagerTest.put(classtestfunc,true);
            } else {
                EagerTest.put(classtestfunc,false);
            }

        }

        return EagerTest;


	}




	public double getLazyTestStats(Map<String,Boolean> testfuncconditionalTestmap)
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
