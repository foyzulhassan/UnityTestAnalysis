//package com.unity.testsmell;
//
//
//import com.config.Config;
//import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
//import com.github.gumtreediff.tree.ITree;
//import org.apache.commons.collections.functors.IfClosure;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class UnknownTest {
//
//    private boolean unknownFound = false;
//
//    public void getSmell(ITree root)
//    {
//
//    }
//
//    public Map<String,Boolean> searchForUnknownTest(ITree root)
//    {
//        List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
//        System.out.println("testfunclist"+testfunclist);
//        Map<ITree, String> helperfunclist = TreeNodeAnalyzer.getTestFunctionListnull(root);
//        System.out.println("Helper function list: " + helperfunclist);
//        Map<String,Boolean> unknownTest=new HashMap<>();
//        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");
//
//        if(classnode==null)
//            return unknownTest;
//
//        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);
//
//        String lowerclassname = classname.getLabel();
//
//
//        for(ITree testfunc:testfunclist)
//        {
//            List<ITree> unknownlist=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "assert");
//            List<ITree> ExpectedExceptionlist = TreeNodeAnalyzer.getSearchTypeLabel(testfunc,"name","ExpectedException");
//            List<ITree> TestUtility = TreeNodeAnalyzer.getSearchTypeLabel(testfunc,"name","TestUtility");
//            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//            String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
//
//
//            if(unknownlist.isEmpty() && ExpectedExceptionlist.isEmpty() && TestUtility.isEmpty() && !helperfunclist.isEmpty())
//            {
//                Map<ITree, String> helperfunclist_1 = TreeNodeAnalyzer.getTestFunctionListnull(testfunc);
//                System.out.println("getfunclist"+ helperfunclist_1);
//                for (Map.Entry<ITree, String> helperEntry : helperfunclist.entrySet()) {
//                    ITree helperFunc = helperEntry.getKey();
//                    String helperFuncName = helperEntry.getValue();
//                    System.out.println("Helper Function Name: " + helperFuncName); // Debugging
//
//                    List<ITree> unknownsublist = TreeNodeAnalyzer.getSearchTypeLabel(helperFunc, "name", "assert");
//                    System.out.println("unknowsublist"+unknownsublist);
//                    if (unknownsublist.isEmpty()) {
//                        unknownFound = true;
//                        break;
//                    }
//                    else{
//                        unknownFound = false;
//                    }
//                }
//            }
//            else{
//                unknownFound = true;
//            }
//            unknownTest.put(classtestfunc,unknownFound);
//        }
//        return unknownTest;
//    }
//
//    public double getUnknownTestStats(Map<String,Boolean> testunknownTestmap)
//    {
//
//        int total=testunknownTestmap.keySet().size();
//        if (total == 0)
//            return -0.001;
//
//        int unknown=0;
//
//        for(boolean b:testunknownTestmap.values()){
//            if(b)
//                unknown++;
//        }
//
//       return  (double) unknown/total;
//
//    }
//}
//
package com.unity.testsmell;

import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UnknownTest {

    private boolean unknownFound = false;

    public Map<String, Boolean> searchForUnknownTest(ITree root) {
        // Print the tree structure for debugging
        //System.out.println("Printing the tree structure:");
        //analyzeTree(root);

        List<ITree> testfunclist = TreeNodeAnalyzer.getTestFunctionList(root);
        //System.out.println("Test function list: " + testfunclist);
        Map<ITree, String> helperfunclist = TreeNodeAnalyzer.getTestFunctionListnull(root);
        //System.out.println("Helper function list: " + helperfunclist);

        Map<String, Boolean> unknownTest = new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if (classnode == null) {
            //System.out.println("No class node found.");
            return unknownTest;
        }

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);
        if (classname == null) {
            //System.out.println("No class name node found.");
            return unknownTest;
        }

        String lowerclassname = classname.getLabel();

        String classtestfunc = null;
        for (ITree testfunc : testfunclist) {
            List<ITree> unknownlist = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "assert");
            //System.out.println("unknownlist"+unknownlist);
            List<ITree> expectedExceptionList = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "ExpectedException");
            List<ITree> testUtility = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "TestUtility");

            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
            if (funcnamenode == null) {
                //System.out.println("No function name found for test function: " + testfunc);
                continue;
            }
            classtestfunc = lowerclassname + Config.separatorStr + funcnamenode.getLabel();
            if (!unknownlist.isEmpty() || !expectedExceptionList.isEmpty() || !testUtility.isEmpty()) {
                //System.out.println("entered here..");
                unknownFound = false;
                unknownTest.put(classtestfunc, unknownFound);
                continue;
            }

            if (unknownlist.isEmpty() && expectedExceptionList.isEmpty() && testUtility.isEmpty() && !helperfunclist.isEmpty()) {
                Map<ITree, String> helperfunclist_1 = TreeNodeAnalyzer.getTestFunctionListnull(testfunc);
                //System.out.println("Helper function list within function: " + helperfunclist_1);

                // Debug: Print all "call" nodes in the test function
                List<ITree> functionCalls = TreeNodeAnalyzer.getSearchTypeLabel2(testfunc, "call", "");
//                for (ITree x : functionCalls) {
//                    System.out.println("res:" + x.getChildren().get(0).getType());
//                }
                //System.out.println("functionCalls" + functionCalls);

                // Check if there are any function calls
                if (!functionCalls.isEmpty()) {
                    // Get the last function call
                    ITree lastFunctionCall = functionCalls.get(functionCalls.size() - 1);
                    //System.out.println("Last function call: " + lastFunctionCall);

                    // Retrieve the name of the last function call
                    ITree functionNameNode = lastFunctionCall.getChildren().get(0);
                    String matcher = String.valueOf(functionNameNode.getType());
                    if (Objects.equals(matcher, "name")) {
                        //System.out.println("entered here");
                        String functionName = functionNameNode.getLabel();
                        //System.out.println("Function name: " + functionName);

                        // Check if the function name exists in the helper function list
                        boolean functionExistsInHelperList = helperfunclist.containsValue(functionName);
                        if (functionExistsInHelperList) {
                            //System.out.println("Function '" + functionName + "' exists in the helper function list.");

                            // Find the corresponding helper function node
                            ITree helperFunc = null;
                            for (Map.Entry<ITree, String> helperEntry : helperfunclist.entrySet()) {
                                if (helperEntry.getValue().equals(functionName)) {
                                    helperFunc = helperEntry.getKey();
                                    break;
                                }
                            }

                            if (helperFunc != null) {
                                // Extract assertion calls within the helper function
                                List<ITree> unknownsublist = TreeNodeAnalyzer.getSearchTypeLabel(helperFunc, "name", "assert");
                                //List<ITree> assertlist=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "assert");
                                //System.out.println("Assertion calls in helper function: " + unknownsublist);

                                if (unknownsublist.isEmpty()) {
                                    unknownFound = true; // No assertions found in the helper function
                                } else {
                                    unknownFound = false; // Assertions found in the helper function
                                }
                            } else {
                                //System.out.println("Helper function node not found for function name: " + functionName);
                                unknownFound = true;
                            }
                        } else {
                            //System.out.println("Function '" + functionName + "' does not exist in the helper function list.");
                            unknownFound = true;
                        }
                    } else {
                        //System.out.println("Function call node does not have a 'name' child.");
                        unknownFound = true;
                    }
                } else {
                    //System.out.println("No function calls found in the test function.");
                    unknownFound = true;
                }
            } else {
                unknownFound = true;
            }
            // Add the result to the unknownTest map
            unknownTest.put(classtestfunc, unknownFound);
            unknownFound = false;
        }
        return unknownTest;
    }

    public double getUnknownTestStats(Map<String, Boolean> testunknownTestmap) {
        int total = testunknownTestmap.keySet().size();
        if (total == 0) {
            //System.out.println("No test cases found to calculate stats.");
            return -0.001; // Return a special value if there are no test cases
        }

        int unknown = 0;

        // Count the number of unknown test cases
        for (boolean isUnknown : testunknownTestmap.values()) {
            if (isUnknown) {
                unknown++;
            }
        }

        // Calculate the percentage of unknown test cases
        double percentage = (double) unknown / total * 100;
        //System.out.println("Total test cases: " + total + ", Unknown test cases: " + unknown + ", Percentage: " + percentage + "%");
        return percentage;
    }
}