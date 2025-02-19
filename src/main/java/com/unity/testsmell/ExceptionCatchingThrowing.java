package com.unity.testsmell;

import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExceptionCatchingThrowing {

    private boolean exceptionFound = false;
    boolean expectedexceptionFound_ = false;

    public void getSmell(ITree root) {
        // Placeholder for potential future implementation
    }

    public Map<String, Boolean> searchForExceptionTest(ITree root) {
        List<ITree> testfunclist = TreeNodeAnalyzer.getTestFunctionList(root);
        Map<ITree, String> helperfunclist = TreeNodeAnalyzer.getTestFunctionListnull(root);
        Map<String, Boolean> exceptionTest = new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if (classnode == null)
            return exceptionTest;

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);
        String lowerclassname = classname.getLabel();

        for (ITree testfunc : testfunclist) {
            List<ITree> assertlist = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "assert");
            List<ITree> catchlist = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "catch");
            //System.out.println("catchlist: "+catchlist);
            List<ITree> throwlist = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "throws");
            //System.out.println("throwlist: "+throwlist);
            List<ITree> expectedlist = TreeNodeAnalyzer.getexpectedexceptionFunctionList(testfunc);
            //System.out.println("expectedlist: "+expectedlist);
            List<ITree> functionCallss = TreeNodeAnalyzer.getSearchTypeLabel2(testfunc, "call", "");
            if(!functionCallss.isEmpty()){
                for(ITree callnodes: functionCallss) {
                    if (!callnodes.getChildren().isEmpty()) {
                        ITree callnodename = callnodes.getChildren().get(0);
                        //System.out.println("callnamenode: "+callnodename);
                        String matcher = String.valueOf(callnodename.getType());
                        if (Objects.equals(matcher, "name")) {
                            //System.out.println("entered here");
                            String expectedexception_present = callnodename.getLabel().toLowerCase();
                            //System.out.println("exceptionfound:"+expectedexception_present);
                            //String ignore = "ignore".toLowerCase();
                            if(expectedexception_present.equals("expectedexception")){
                                expectedexceptionFound_ = true;
                            }
                            //System.out.println("ignorefound: "+IgnoreFound_);
                        }
                    }
                }
            }
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);

            String classtestfunc = lowerclassname + Config.separatorStr + funcnamenode.getLabel();
            List<ITree> catchlist_subfunc = null;
            List<ITree> throwlist_subfunc = null;

            if (assertlist.isEmpty()) {
                List<ITree> functionCalls = TreeNodeAnalyzer.getSearchTypeLabel2(testfunc, "call", "");

                if (!functionCalls.isEmpty()) {
                    ITree lastFunctionCall = functionCalls.get(functionCalls.size() - 1);
                    ITree functionNameNode = lastFunctionCall.getChildren().get(0);
                    String matcher = String.valueOf(functionNameNode.getType());

                    if (Objects.equals(matcher, "name")) {
                        String functionName = functionNameNode.getLabel();

                        boolean functionExistsInHelperList = helperfunclist.containsValue(functionName);
                        if (functionExistsInHelperList) {
                            ITree helperFunc = null;
                            for (Map.Entry<ITree, String> helperEntry : helperfunclist.entrySet()) {
                                if (helperEntry.getValue().equals(functionName)) {
                                    helperFunc = helperEntry.getKey();
                                    break;
                                }
                            }

                            if (helperFunc != null) {

                                catchlist_subfunc = TreeNodeAnalyzer.getSearchTypeLabel(helperFunc, "name", "catch");
                                throwlist_subfunc = TreeNodeAnalyzer.getSearchTypeLabel(helperFunc, "name", "throws");

                                List<ITree> functionCallinloop = TreeNodeAnalyzer.getSearchTypeLabel2(helperFunc, "call", "");
                                if(!functionCallinloop.isEmpty()){
                                    for(ITree callnodes: functionCallinloop) {
                                        if (!callnodes.getChildren().isEmpty()) {
                                            ITree callnodename = callnodes.getChildren().get(0);
                                            //System.out.println("callnamenode: "+callnodename);
                                            String matchers = String.valueOf(callnodename.getType());
                                            if (Objects.equals(matchers, "name")) {
                                                //System.out.println("entered here");
                                                String expectedexception_present = callnodename.getLabel().toLowerCase();
                                                //System.out.println("exceptionfound:"+expectedexception_present);
                                                //String ignore = "ignore".toLowerCase();
                                                if(expectedexception_present.equals("expectedexception")){
                                                    expectedexceptionFound_ = true;
                                                }
                                                if (expectedexceptionFound_) {
                                                    exceptionTest.put(classtestfunc, false);  // Mark as false due to expected exception
                                                }
                                                //System.out.println("ignorefound: "+IgnoreFound_);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // If expectedexceptionFound_ is true, mark the exception test as false
            if (expectedexceptionFound_ ) {
                exceptionTest.put(classtestfunc, false);  // Mark as false due to expected exception
            } else {
                if (!catchlist.isEmpty() || !throwlist.isEmpty() ||
                        (catchlist_subfunc != null && !catchlist_subfunc.isEmpty()) ||
                        (throwlist_subfunc != null && !throwlist_subfunc.isEmpty()) && expectedlist.isEmpty()) {
                    exceptionFound = true;
                }
                exceptionTest.put(classtestfunc, exceptionFound);  // Otherwise, use exceptionFound
            }
            //exceptionTest.put(classtestfunc, exceptionFound);
            exceptionFound = false;
            expectedexceptionFound_ = false;
        }

        return exceptionTest;
    }

    public double getExceptionTestStats(Map<String, Boolean> testexceptionTestmap) {
        int total = testexceptionTestmap.keySet().size();
        if (total == 0)
            return -0.001;

        int exception = 0;

        for (boolean b : testexceptionTestmap.values()) {
            if (b)
                exception++;
        }

        return (double) exception / total;
    }
}