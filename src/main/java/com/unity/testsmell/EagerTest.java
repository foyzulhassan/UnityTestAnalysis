//package com.unity.testsmell;
//
//
//import com.config.Config;
//import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
//import com.github.gumtreediff.tree.ITree;
//
//import java.io.PrintWriter;
//import java.util.*;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//public class EagerTest {
//
//    public boolean sub_tree_matcher(ITree tree1, ITree tree2, boolean objectMatch)
//    {
//        if ( tree1.getType() == tree2.getType() && Objects.equals(tree1.getLabel(), tree2.getLabel()))
//        {
//            if (tree1.isLeaf() && tree2.isLeaf()){
//                return true;
//            } else if (objectMatch){
//                return false;
//            } else{
//                if(tree1.getChildren().size() != tree2.getChildren().size())
//                {
//                    return false;
//                }
//                boolean total=true;
//                for (int counter = 0; counter<tree1.getChildren().size();counter++){
//                    total=total && sub_tree_matcher(tree1.getChild(counter),tree2.getChild(counter),false);
//                }
//                return total;
//            }
//
//        }
//        return false;
//    }
//
//
//
//
//
//    public void getSmell(ITree root)
//	{
//
//	}
//
//	public Map<String,Boolean> searchForEagerTest(ITree root)
//	{
//		List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
//        List<ITree> testfunclistCopy = new ArrayList<>();
//        for( ITree testfunc: testfunclist){
//            ITree copy = testfunc.deepCopy();
//            testfunclistCopy.add(copy);
//        }
//		Map<String,Boolcean> EagerTest=new HashMap<>();
//		ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");
//
//		if(classnode==null)
//			return EagerTest;
//
//		ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);
//
//		String lowerclassname = classname.getLabel();
//
//        Map<ITree,Integer> funcs_map = new HashMap<>();
//        for(ITree testfunc:testfunclist)
//		{
//            List<ITree> assertlist=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "assert");
//            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
////            List<AssertCall> assercalllist=new ArrayList<>();
//            String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
//            List<ITree> paramSet=new ArrayList<>();
//            List<AssertCall> assercalllist=new ArrayList<>();
//            if(assertlist!=null && assertlist.size()>0)
//            {
//                for(ITree assertitem:assertlist)
//                {
//                    AssertCall assertcall=TreeNodeAnalyzer.getAssertCall(assertitem);
//                    assercalllist.add(assertcall);
//                }
//                System.out.println("assercalllist"+assercalllist);
//            }
//            for( AssertCall assertCall:assercalllist){
//                List<ITree> paramITrees =assertCall.getParamListTrees();
//                System.out.println("param"+ paramITrees);
//                 for(ITree param:paramITrees){
//                     if(isProperty(param)){
//                         continue;
//                     }
//                     boolean alreadyInList = false;
//                     System.out.println("paramset"+paramSet);
//                     for(ITree paramFromList:paramSet){
//                         System.out.println("paramFromList"+paramFromList);
//                         if(sub_tree_matcher(paramFromList,param,false)){
//                             alreadyInList=true;
//                             break;
//                         }
//                     }
//                     if(!alreadyInList){
//                         paramSet.add(param);
//                     }
//                 }
//            }
//            if(paramSet.size() >1 ){
//                EagerTest.put(classtestfunc,true);
//            } else {
//                EagerTest.put(classtestfunc,false);
//            }
//
//        }
//
//        return EagerTest;
//
//
//	}
//
//    private boolean isProperty(ITree param) {
//        ITree local_tree= param.deepCopy();
//        for (ITree ch: local_tree.getChildren()){
//            List<ITree> argList = TreeNodeAnalyzer.getSearchTypeLabel(ch,"argument_list","");
//            if (argList.size() > 0)
//            {
//                return false;
//            }
//        }
//        return true;
//    }
//
//
//    public double getEagerTestStats(Map<String,Boolean> testfuncconditionalTestmap)
//	{
//
//        int total=testfuncconditionalTestmap.keySet().size();
//        if (total == 0)
//            return -0.001;
//
//        int sensitiveEquality=0;
//
//        for(boolean b:testfuncconditionalTestmap.values()){
//            if(b)
//                sensitiveEquality++;
//        }
//
//		return  (double) sensitiveEquality/total;
//	}
//
//
//}
package com.unity.testsmell;

import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.*;

public class EagerTest {

    public Map<String, Boolean> searchForEagerTest(ITree root, Map<String, List<String>> productCalls) {
        List<ITree> testFunctionList = TreeNodeAnalyzer.getTestFunctionList(root);
        Map<String, Boolean> eagerTests = new HashMap<>();
        ITree classNode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if (classNode == null) return eagerTests;

        ITree classNameNode = SrcmlUnityCsMetaDataGenerator.getClassName(classNode);
        String className = classNameNode.getLabel();

        for (ITree testFunc : testFunctionList) {
            ITree funcNameNode = SrcmlUnityCsMetaDataGenerator.getFuncName(testFunc);
            String testFunctionSignature = className + Config.separatorStr + funcNameNode.getLabel();

            Map<String, Set<String>> objectToMethodsMap = new HashMap<>();
            List<ITree> assertList = TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "name", "assert");
            //System.out.println("assertlist: " + assertList);

            for (ITree assertItem : assertList) {
                ITree res = assertItem.getParent().getParent();
//                System.out.println("res: " + res.getChildren());
//                System.out.println("Printing the tree structure for assert:");
                printTree(res, 0);

                List<ITree> arguments = extractArgumentsFromArgumentList(res);

                //System.out.println("Extracted Arguments for assert:");
//                for (ITree arg : arguments) {
//                    System.out.println("Argument Label: " + arg.getLabel());
//                }

                for (ITree argument : arguments) {
                    String invokedMethod = extractExpression(argument);
                    //System.out.println("Extracted Method: " + invokedMethod);

                    // Split object and method
                    String[] parts = invokedMethod.split("\\.\\.");
                    if (parts.length == 2) {
                        String object = parts[0];
                        String method = parts[1];

                        // Add method to the object's method set
                        objectToMethodsMap.putIfAbsent(object, new HashSet<>());
                        objectToMethodsMap.get(object).add(method);
                    }
                }
            }

            // Determine if the test is eager
            boolean isEager = objectToMethodsMap.values().stream()
                    .anyMatch(methods -> methods.size() > 1); // Check if any object has more than one method called
            eagerTests.put(testFunctionSignature, isEager);

            //System.out.println("Test Function: " + testFunctionSignature);
            //System.out.println("Object to Methods Map: " + objectToMethodsMap);
            //System.out.println("Is Eager Test: " + isEager);
        }

        return eagerTests;
    }

    private List<ITree> extractArgumentsFromArgumentList(ITree res) {
        List<ITree> arguments = new ArrayList<>();
        for (ITree child : res.getChildren()) {
            if ("argument_list".equalsIgnoreCase(String.valueOf(child.getType()))) {
                arguments.addAll(child.getChildren());
                //System.out.println("Children of argument_list: " + child.getChildren());
            }
        }

        if (arguments.isEmpty()) {
            //System.out.println("No arguments found in argument_list.");
        }

        return arguments;
    }

    private String extractExpression(ITree node) {
        if (node == null) return null;

        StringBuilder extractedMethods = new StringBuilder();
        if (node.getLabel() != null && !node.getLabel().isEmpty()) {
            extractedMethods.append(node.getLabel());
        }

        for (ITree child : node.getChildren()) {
            String childLabel = extractExpression(child);
            if (childLabel != null) {
                if (extractedMethods.length() > 0) extractedMethods.append(".");
                extractedMethods.append(childLabel);
            }
        }

        return extractedMethods.toString().trim();
    }

    private String normalize(String name) {
        if (name == null) return null;
        return name.trim().replaceAll("\\s+", "").toLowerCase();
    }

    private void printTree(ITree node, int level) {
        if (node == null) return;

        String indent = "  ".repeat(level);
        //System.out.println(indent + "Node: " + node.getType() + ", Label: " + node.getLabel());

        for (ITree child : node.getChildren()) {
            printTree(child, level + 1);
        }
    }

    public void logEagerTestStats(Map<String, Boolean> eagerTests) {
        long eagerTestCount = eagerTests.values().stream().filter(Boolean::booleanValue).count();
        //System.out.println("Total Tests: " + eagerTests.size());
        //System.out.println("Eager Tests: " + eagerTestCount);
        //System.out.println("Eager Test Percentage: " + (eagerTestCount * 100.0 / eagerTests.size()) + "%");
    }

    public double getEagerTestStats(Map<String, Boolean> eagerTests) {
        int totalTests = eagerTests.size();
        if (totalTests == 0) return -1;

        long eagerTestCount = eagerTests.values().stream().filter(Boolean::booleanValue).count();
        return (double) eagerTestCount / totalTests * 100;
    }
}
