package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class LazyTest {

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
    private Map<String,Integer> ressources_paths=new HashMap<>();

    public void add_ressources(ITree tree){
        ITree tree_copy=tree.getParent().deepCopy();
        tree_copy.getChildren().forEach(ch -> {
        List<ITree> literals_list = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNodeList(tree_copy,"literal","l1");
        for (ITree lit: literals_list){
            String label = lit.getLabel();
            if(ressources_paths.containsKey(label))
            {
                ressources_paths.put(label,ressources_paths.get(label)+1);
            }
            else{
                ressources_paths.put(label,1);
            }
        }
        });

    }

    public boolean test_ressources(ITree tree){
        ITree tree_copy=tree.getParent().deepCopy();
        AtomicBoolean result = new AtomicBoolean(false);
        tree_copy.getChildren().forEach(ch -> {
            List<ITree> literals_list = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNodeList(tree_copy,"literal","l1");
            System.out.println(literals_list);
            for (ITree lit: literals_list){
                String label = lit.getLabel();
                if(ressources_paths.get(label)>1)
                {
                    result.set(true);
                }
            }
        });
        return result.get();

    }



    public void getSmell(ITree root)
	{

	}

//	public Map<String,Boolean> searchForLazyTest(ITree root)
//	{
//		List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
//        List<ITree> testfunclistCopy = new ArrayList<>();
//        for( ITree testfunc: testfunclist){
//            ITree copy = testfunc.deepCopy();
//            testfunclistCopy.add(copy);
//        }
//		Map<String,Boolean> LazyTest=new HashMap<>();
//		ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");
//
//		if(classnode==null)
//			return LazyTest;
//
//		ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);
//
//		String lowerclassname = classname.getLabel();
//
//        Map<ITree,Integer> funcs_map = new HashMap<>();
//        for(ITree testfunc:testfunclist)
//		{
//			List<ITree> calls_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "call", "");
////			ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
////    		String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
//			if(calls_list!=null && calls_list.size()>0)
//			{
//				for(ITree call : calls_list){
//                    call.getChildren().forEach( ch -> {
//                        if(ch.getType().toString().equalsIgnoreCase("name")){
//                            if (funcs_map.containsKey(ch)){
//                                funcs_map.put(ch,funcs_map.get(ch)+1);
//                            } else {
//                                funcs_map.put(ch,1);
//                            }
//                        }
//                    });
//
//                }
//			}
//            List<ITree> resources_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "Resources");
//            resources_list.forEach(this::add_ressources);
//		}
//        for(ITree testfunc:testfunclistCopy){
//            List<ITree> calls_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "call", "");
//            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//            String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
//            if(calls_list!=null && calls_list.size()>0)
//            {
//                for(ITree call : calls_list){
//                    call.getChildren().forEach( ch -> {
//                        if(ch.getType().toString().equalsIgnoreCase("name")){
//                            for(ITree funcTree: funcs_map.keySet()){
//                                if(funcs_map.get(funcTree) > 1) {
//                                    if (sub_tree_matcher(ch,funcTree,false)) {
//                                        LazyTest.put(classtestfunc,true);
//                                    }
//
//                                }
//
//                            }
//
//                        }
//                    });
//
//                }
//                List<ITree> resources_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "Resources");
//                for (ITree resource:resources_list){
//                    if(test_ressources(resource)){
//                        LazyTest.put(classtestfunc,true);
//                        break;
//                    }
//                }
//            }
//
//            if (!LazyTest.containsKey(classtestfunc)){
//                LazyTest.put(classtestfunc,false);
//            }
//
//        }
//        if(ressources_paths.size() > 0)
//            System.out.println(ressources_paths);
//        return LazyTest;
//
//
//	}

    public Map<String, Boolean> searchForLazyTest(ITree root) {

        // Get the list of test functions
        List<ITree> testfunclist = TreeNodeAnalyzer.getTestFunctionList(root);

        // Map to store test functions and their lazy status
        Map<String, Boolean> LazyTest = new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if (classnode == null) return LazyTest;

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);
        String lowerclassname = classname.getLabel();

        // Map to store which test methods call each production method
        Map<String, Set<String>> funcCallTestFuncMap = new HashMap<>();

        // Loop through test functions to track function calls
        for (ITree testfunc : testfunclist) {
            String testFuncName = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc).getLabel();

            // Get the list of function calls within the test function
            List<ITree> calls_list = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "call", "");

            // Keep track of which functions are called in each test method
            Set<String> calledFunctionsInTest = new HashSet<>();

            for (ITree call : calls_list) {
                ITree funcnameNode = SrcmlUnityCsMetaDataGenerator.getFuncName(call);
                if (funcnameNode != null) {
                    String funcname = funcnameNode.getLabel();

                    // Track which test functions call this function
                    funcCallTestFuncMap.computeIfAbsent(funcname, k -> new HashSet<>()).add(testFuncName);

                    // Track the functions called within this particular test function
                    calledFunctionsInTest.add(funcname);
                }
            }
            List<ITree> resources_list = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "Resources");
            resources_list.forEach(this::add_ressources);
        }

        // Mark lazy tests: check if the same function is called by more than one test function
        for (ITree testfunc : testfunclist) {
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
            String classtestfunc = lowerclassname + Config.separatorStr + funcnamenode.getLabel();

            boolean isLazy = false;
            List<ITree> calls_list = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "call", "");

            // Iterate through the calls in the current test function
            for (ITree call : calls_list) {
                ITree funcnameNode = SrcmlUnityCsMetaDataGenerator.getFuncName(call);
                if (funcnameNode != null) {
                    String funcname = funcnameNode.getLabel();

                    // Check if the same production function is called across multiple test functions
                    Set<String> callingTestFuncs = funcCallTestFuncMap.get(funcname);
                    if (callingTestFuncs != null && callingTestFuncs.size() > 1) {
                        isLazy = true;  // Mark as lazy if the function is called across multiple test functions
                        break;
                    }
                }
            }

            LazyTest.put(classtestfunc, isLazy);  // Update map with lazy status for the test
        }

        if (ressources_paths.size() > 0)
            System.out.println(ressources_paths);

        return LazyTest;
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
