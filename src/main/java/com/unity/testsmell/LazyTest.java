package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.*;

public class LazyTest {

private boolean toStringFound = false;
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

	public Map<String,Boolean> searchForLazyTest(ITree root)
	{
		List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
        List<ITree> testfunclistCopy = new ArrayList<>();
        for( ITree testfunc: testfunclist){
            ITree copy = testfunc.deepCopy();
            testfunclistCopy.add(copy);
        }
		Map<String,Boolean> LazyTest=new HashMap<>();
		ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

		if(classnode==null)
			return LazyTest;

		ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

		String lowerclassname = classname.getLabel();

        Map<ITree,Integer> funcs_map = new HashMap<>();
        for(ITree testfunc:testfunclist)
		{
			List<ITree> calls_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "call", "");
//			ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//    		String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
			if(calls_list!=null && calls_list.size()>0)
			{
				for(ITree call : calls_list){
                    call.getChildren().forEach( ch -> {
                        if(ch.getType().toString().equalsIgnoreCase("name")){
                            if (funcs_map.containsKey(ch)){
                                funcs_map.put(ch,funcs_map.get(ch)+1);
                            } else {
                                funcs_map.put(ch,1);
                            }
                        }
                    });

                }
			}
		}
        for(ITree testfunc:testfunclistCopy){
            List<ITree> calls_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "call", "");
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
            String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
            if(calls_list!=null && calls_list.size()>0)
            {
                for(ITree call : calls_list){
                    call.getChildren().forEach( ch -> {
                        if(ch.getType().toString().equalsIgnoreCase("name")){
                            for(ITree funcTree: funcs_map.keySet()){
                                if(funcs_map.get(funcTree) > 1) {
                                    if (sub_tree_matcher(ch,funcTree,false)) {
                                        LazyTest.put(classtestfunc,true);
                                    }

                                }

                            }

                        }
                    });

                }
            }

        }



        return LazyTest;


	}




	public double getSensitiveEqualityStats(Map<String,Boolean> testfuncconditionalTestmap)
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
