package com.unity.testsmell;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import static antlr.build.ANTLR.root;
import static com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode;

public class TreeNodeAnalyzer {

    public static List<ITree> getFunctionList(ITree node) { // It will return all the functions regardless of annotations.

        ITree copynode = node.deepCopy();

        List<ITree> funclist = breadthFirstSearchForNodeList(copynode, "function", "func");
        return funclist;
    }

    public static List<ITree> getConstructorList(ITree node) {

        ITree copynode = node.deepCopy();

        List<ITree> funclist = breadthFirstSearchForNodeList(copynode, "constructor", "const");

        return funclist;
    }

    public static List<ITree> getStatementList(ITree node) {

        ITree copynode = node.deepCopy();

        List<ITree> funclist = breadthFirstSearchForNodeList(copynode, "decl_stmt", "stmt");

        return funclist;
    }

    public static List<ITree> getExprStatementList(ITree node) {

        ITree copynode = node.deepCopy();

        List<ITree> exprlist = breadthFirstSearchForNodeList(copynode, "expr_stmt", "expr");

        return exprlist;
    }

    public static List<ITree> getReturnList(ITree node) {

        ITree copynode = node.deepCopy();

        List<ITree> exprlist = breadthFirstSearchForNodeList(copynode, "return", "specifier");

        return exprlist;
    }

    public static List<ITree> getSetupFunctionsList(ITree node) {
        //System.out.println("enterered setuplist");
        AtomicBoolean in_test_fixture = new AtomicBoolean(false);
        ITree copynode = node.deepCopy();
        ITree copynode2 = node.deepCopy();
        List<ITree> testfunclist = new ArrayList<>();
        //System.out.println("Inspecting copynode2 structure:");
        //TreeNodeAnalyzer.printTree(copynode2, "  ");
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(copynode2, "class", "c1");
        if (classnode == null) {
            System.err.println("Error: Class node not found! Verify the type and label used for the search.");
            return testfunclist; // Return an empty list if the class node is not found
        }

//        System.out.println("classnode: "+classnode);
//        System.out.println("enterered setuplist..1");
        classnode.getChildren().forEach(t1 -> {
    //        System.out.println("enterered setuplist..3");
            if (t1.getType().toString().equals("attribute")) {
     //           System.out.println("enterered setuplist..5");
                t1.getChildren().forEach(t2 -> t2.getChildren().forEach(t3 -> {
                            if (t3.getType().toString().equals("name") && t3.getLabel().equals("TestFixture"))
                            {in_test_fixture.set(true);
                                System.out.println("Test Fixture");
                            }
                        }
                ));
            }
        });
        //System.out.println("enterered setuplist..5");
        List<ITree> funclist = breadthFirstSearchForNodeList(copynode, "function", "func");

        for (ITree func : funclist) {
            String func_name = SrcmlUnityCsMetaDataGenerator.getFunctionName(func);
            int nb_arfs = SrcmlUnityCsMetaDataGenerator.getFuncParamSize(func);
            if (in_test_fixture.get() && nb_arfs == 0 && (func_name.equals("Setup") )) {

                    testfunclist.add(func);

            } else {
                List<ITree> attributes = breadthFirstSearchForNodeList(func, "attribute", "an1");

                if (attributes != null && attributes.size() > 0) {
                    List<ITree> setupanotations = breadthFirstSearchForLabel(attributes.get(0), "SetUp", "an2");
//                    List<ITree> teardownanotations = breadthFirstSearchForLabel(attributes.get(0), "TearDown", "an3");
                    //System.out.println("test");

                    if (setupanotations != null && setupanotations.size() > 0) {
                        testfunclist.add(func);
                    }
//                    else if (teardownanotations != null && teardownanotations.size() > 0) {
//                        testfunclist.add(func);
//                    }
                }
            }
        }
        //System.out.println("testfunclist"+testfunclist);
        return testfunclist;
    }

    public static List<ITree> getTestFunctionList(ITree node) {

        ITree copynode = node.deepCopy();
        List<ITree> testfunclist = new ArrayList<>();
        List<ITree> funclist = breadthFirstSearchForNodeList(copynode, "function", "func");
        //System.out.println("funclist:"+funclist);
       // List<ITree> constructorlist = breadthFirstSearchForNodeList(copynode, "constructor", "func");

        for (ITree func : funclist) {
            String funcname = SrcmlUnityCsMetaDataGenerator.getFunctionName(func);

            List<ITree> attributes = breadthFirstSearchForNodeList(func, "attribute", "an1");
           // System.out.println("attributes"+attributes);

            if (attributes != null && !attributes.isEmpty()) {
                List<ITree> unitytestanotations = breadthFirstSearchForLabel(attributes.get(0), "UnityTest", "an2");
                List<ITree> testanotations = breadthFirstSearchForLabel(attributes.get(0), "Test", "an3");
                List<ITree> mtestanotations = breadthFirstSearchForLabel(attributes.get(0), "MTest", "an4");
                List<ITree> testFixture = breadthFirstSearchForLabel(attributes.get(0), "Fixture", "an5");
                List<ITree> testMethod = breadthFirstSearchForLabel(attributes.get(0),"TestMethod","ans6");
                List<ITree> testcase = breadthFirstSearchForLabel(attributes.get(0),"TestCase","ans7");
                List<ITree> TestCaseSource = breadthFirstSearchForLabel(attributes.get(0),"TestCaseSource","ans8");
                //System.out.println("test");

                if (unitytestanotations != null && !unitytestanotations.isEmpty()) {
                    testfunclist.add(func);
                } else if (testanotations != null && !testanotations.isEmpty()) {
                    testfunclist.add(func);
                }
                else if (mtestanotations != null && !mtestanotations.isEmpty()) {
                    testfunclist.add(func);
                }
                else if (testFixture != null && !testFixture.isEmpty()) {
                    testfunclist.add(func);
                }
                else if (testMethod != null && !testMethod.isEmpty()){
                    testfunclist.add(func);
                }
                else if (testcase != null && !testcase.isEmpty()){
                    testfunclist.add(func);
                }
                else if(TestCaseSource != null && !TestCaseSource.isEmpty()){
                    testfunclist.add(func);
                }
            }
        }
        return testfunclist;
    }

//    public static Map<ITree, String> getTestFunctionListnull(ITree node) {
//        ITree copynode = node.deepCopy(); // Deep copy to avoid modifying the original tree
//        Map<ITree, String> testFunctionMap = new HashMap<>(); // Map to store function nodes and their names
//
//        List<ITree> funclist = TreeNodeAnalyzer.breadthFirstSearchForNodeList(copynode, "function", "func");
//
//        for (ITree func : funclist) {
//            String funcname = SrcmlUnityCsMetaDataGenerator.getFunctionName(func);
//
//            // Get attributes to check if the function has test attributes
//            List<ITree> attributes = TreeNodeAnalyzer.breadthFirstSearchForNodeList(func, "attribute", "an1");
//
//            // Only add functions without attributes
//            if (attributes == null || attributes.isEmpty()) {
//                processFunctionRecursively(func, funcname, testFunctionMap, node); // Pass node as root
//            }
//        }
//        return testFunctionMap;
//    }
//
//    private static void processFunctionRecursively(ITree func, String funcname, Map<ITree, String> testFunctionMap, ITree node) {
//        // Avoid infinite loops by checking if the function has already been processed
//        if (testFunctionMap.containsKey(func)) {
//            return;
//        }
//
//        // Add the current function to the map
//        testFunctionMap.put(func, funcname);
//
//        // Get the last statement in the function body
//        ITree block = TreeNodeAnalyzer.breadthFirstSearchForNode(func, "block", "block");
//        if (block != null && !block.getChildren().isEmpty()) {
//            ITree lastStatement = block.getChildren().get(block.getChildren().size() - 1);
//
//            // Get all function call nodes in the last statement
//            List<ITree> callNodes = TreeNodeAnalyzer.getSearchTypeLabel(lastStatement, "name", "call");
//
//            // Process each function call recursively
//            for (ITree callNode : callNodes) {
//                String calledFunctionName = SrcmlUnityCsMetaDataGenerator.getFunctionName(callNode);
//
//                if (calledFunctionName != null) {
//                    // Ensure root is an ITree and pass it to findFunctionByName
//                    ITree calledFunction = findFunctionByName(root, calledFunctionName);
//                    if (calledFunction != null) {
//                        // Recursively process the found function
//                        processFunctionRecursively(calledFunction, calledFunctionName, testFunctionMap, root);
//                    }
//                }
//            }
//        }
//    }

//    private static ITree breadthFirstSearchForNode(ITree func, String block, String block1) {
//
//    }
//
//    private static ITree findFunctionByName(ITree root, String functionName) {
//        List<ITree> functionList = TreeNodeAnalyzer.breadthFirstSearchForNodeList(root, "function", "func");
//        for (ITree function : functionList) {
//            String funcName = SrcmlUnityCsMetaDataGenerator.getFunctionName(function);
//            if (functionName.equals(funcName)) {
//                return function; // Return the matching function node
//            }
//        }
//        return null;
//    }

// Breadth-first

//    private static boolean isLastFunctionInTest(ITree func, ITree copynode) {
//        ITree block = breadthFirstSearchForNode(func, "block", "block");
//
//        if (block != null && !block.getChildren().isEmpty()) {
//            // Check the last statement in the function body
//            ITree lastStatement = block.getChildren().get(block.getChildren().size() - 1);
//
//            // If the last statement is a function call, check if the called function exists
//            List<ITree> callNodes = TreeNodeAnalyzer.getSearchTypeLabel(lastStatement, "name", "call");
//            if (!callNodes.isEmpty()) {
//                for (ITree callNode : callNodes) {
//                    String calledFunctionName = SrcmlUnityCsMetaDataGenerator.getFunctionName(callNode);
//                    if (calledFunctionName != null) {
//                        ITree calledFunction = findFunctionByName(root, calledFunctionName);
//                        if (calledFunction != null) {
//                            return false; // Not the last function if it calls another function
//                        }
//                    }
//                }
//            }
//        }
//        return true;
//    }


    public static Map<ITree, String> getTestFunctionListnull(ITree node) {
        // Deep copy the node to avoid modifying the original tree
        ITree copynode = node.deepCopy();
        // Map to store test function nodes and their names
        Map<ITree, String> testFunctionMap = new HashMap<>();
        List<ITree> funclist = breadthFirstSearchForNodeList(copynode, "function", "func");
        //System.out.println("fucnlist"+funclist);
        for (ITree func : funclist) {
            String funcname = SrcmlUnityCsMetaDataGenerator.getFunctionName(func);
            List<ITree> attributes = breadthFirstSearchForNodeList(func, "attribute", "an1");
            // Add functions only if they have no attributes
            if (attributes == null || attributes.isEmpty()) {
                if (isLastFunctionInTest(func, copynode)) {
                    processFunctionRecursively(func, funcname, testFunctionMap, node);
                }
            }
        }
        return testFunctionMap; // Return the map
    }

    // Method to check if the given function is the last in the test flow
    private static boolean isLastFunctionInTest(ITree func, ITree root) {
        // Retrieve the block node of the function
        ITree block = breadthFirstSearchForNode1(func, "block", "block");

        // Debugging to confirm block retrieval
        //System.out.println("Block: " + block);
        //System.out.println("Block type: " + (block != null ? block.getType() : "null"));

        if (block != null && !block.getChildren().isEmpty()) {
            // Get the last statement in the block
            ITree lastStatement = block.getChildren().get(block.getChildren().size() - 1);

            // Debugging to check the last statement and function calls
            //System.out.println("Last Statement: " + lastStatement);
            System.out.println("Function calls in last statement: "
                    + TreeNodeAnalyzer.getSearchTypeLabel(lastStatement, "name", "call"));

            // Traverse the function calls to determine if this is the last function
            List<ITree> callNodes = TreeNodeAnalyzer.getSearchTypeLabel(lastStatement, "name", "call");
            for (ITree callNode : callNodes) {
                String calledFunctionName = SrcmlUnityCsMetaDataGenerator.getFunctionName(callNode);
                //System.out.println("Called function name: " + calledFunctionName);

                if (calledFunctionName != null) {
                    ITree calledFunction = findFunctionByName(root, calledFunctionName);
                    if (calledFunction != null) {
                        //System.out.println("Function found in root: " + calledFunctionName);
                        return false; // Function calls another function
                    }
                }
            }
        }
        return true; // No function calls found, this is the last function
    }

    // Recursive helper method to process functions
    private static void processFunctionRecursively(ITree func, String funcname, Map<ITree, String> testFunctionMap, ITree root) {
        if (testFunctionMap.containsKey(func)) return; // Avoid infinite recursion

        testFunctionMap.put(func, funcname); // Add current function to the map
        // Debug statements
        //System.out.println("Processing function: " + funcname);
        //System.out.println("Current function map: " + testFunctionMap);

        // Get all statements in the function body (not just the last statement)
        ITree block = breadthFirstSearchForNode1(func, "block", "block");
        if (block != null) {
            List<ITree> allStatements = block.getChildren();

            for (ITree statement : allStatements) {
                List<ITree> callNodes = TreeNodeAnalyzer.getSearchTypeLabel1(statement, "name", "call");
                for (ITree callNode : callNodes) {
                    String calledFunctionName = SrcmlUnityCsMetaDataGenerator.getFunctionName(callNode);
                    if (calledFunctionName != null) {
                        ITree calledFunction = findFunctionByName(root, calledFunctionName);
                        if (calledFunction != null) {
                            processFunctionRecursively(calledFunction, calledFunctionName, testFunctionMap, root);
                        }
                    }
                }
            }
        }
    }
    public static List<ITree> getSearchTypeLabel1(ITree node, String type, String label) {
        List<ITree> result = new ArrayList<>();
        Queue<ITree> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            ITree current = queue.poll();
            if (current.getType() != null && current.getType().toString().equals(type)
                    && current.getLabel().equals(label)) {
                result.add(current);
            }
            queue.addAll(current.getChildren()); // Ensure all children are traversed
        }
        return result;
    }

    // Method to find a function by its name in the tree
    private static ITree findFunctionByName(ITree root, String functionName) {
        List<ITree> functionList = breadthFirstSearchForNodeList1(root, "function", "func");
        for (ITree function : functionList) {
            String funcName = SrcmlUnityCsMetaDataGenerator.getFunctionName(function);
            if (functionName.equals(funcName)) {
                return function; // Return the matching function node
            }
        }
        return null; // Return null if the function is not found
    }

    // Breadth-first search for nodes matching specific type and label
    private static List<ITree> breadthFirstSearchForNodeList1(ITree node, String type, String label) {
        List<ITree> result = new ArrayList<>();
        Queue<ITree> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            ITree current = queue.poll();

            // Safely check for type and label
            if (current.getType() != null &&
                    current.getType().toString().equals(type) &&
                    current.getLabel().equals(label)) {
                result.add(current);
            }
            queue.addAll(current.getChildren());
        }
        return result;
    }


    private static ITree breadthFirstSearchForNode1(ITree node, String type, String label) {
        Queue<ITree> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            ITree current = queue.poll();

            // Match based on type and label
            if (current.getType() != null &&
                    current.getType().toString().equals(type) &&
                    current.getLabel().equals(label)) {
                return current; // Return the first matching node
            }
            queue.addAll(current.getChildren()); // Continue traversal
        }
        return null; // Return null if no match is found
    }



    public static List<ITree> getIgnoredFunctionList(ITree node) {

        ITree copynode = node.deepCopy();
        List<ITree> testfunclist = new ArrayList<>();
        List<ITree> funclist = breadthFirstSearchForNodeList(copynode, "function", "func");
        // List<ITree> constructorlist = breadthFirstSearchForNodeList(copynode, "constructor", "func");

        for (ITree func : funclist) {
            String funcname = SrcmlUnityCsMetaDataGenerator.getFunctionName(func);
            List<ITree> attributes = breadthFirstSearchForNodeList(func, "attribute", "an1");
            if (attributes != null && attributes.size() > 0) {
                List<ITree> ignoredtestanotations = breadthFirstSearchForLabel(attributes.get(0), "Ignore", "an2");
                //System.out.println("test");

                if (ignoredtestanotations  != null && ignoredtestanotations .size() > 0) {
                    testfunclist.add(func);
                }
            }
        }

        return testfunclist;
    }



    public static List<ITree> getexpectedexceptionFunctionList(ITree node) {

        ITree copynode = node.deepCopy();
        //printTree(node, " ");
        List<ITree> testfunclist = new ArrayList<>();
        List<ITree> funclist = breadthFirstSearchForNodeList(copynode, "function", "func");
        // List<ITree> constructorlist = breadthFirstSearchForNodeList(copynode, "constructor", "func");

        for (ITree func : funclist) {
            String funcname = SrcmlUnityCsMetaDataGenerator.getFunctionName(func);
            List<ITree> attributes = breadthFirstSearchForNodeList(func, "attribute", "an1");
            if (attributes != null && attributes.size() > 0) {
                List<ITree> expectedtestanotations = breadthFirstSearchForLabel(attributes.get(0), "ExpectedException", "an2");
                //System.out.println("test");

                if (expectedtestanotations  != null && expectedtestanotations .size() > 0) {
                    testfunclist.add(func);
                }
            }
        }

        return testfunclist;
    }


    public static List<ITree> getSearchTypeLabel(ITree node, String type, String label) {
        ITree copynode = node.deepCopy();
        List<ITree> nodelist = breadthFirstSearchForTypeLabel(copynode, type, label, "as1");
       // System.out.println("nodelist"+nodelist);
        return nodelist;
    }

    public static List<ITree> getSearchTypeLabel2(ITree node, String type, String label) {
        ITree copynode = node.deepCopy();
        List<ITree> nodelist = breadthFirstSearchForTypeLabel2(copynode, type, label, "as1");
        // System.out.println("nodelist"+nodelist);
        return nodelist;
    }

    public static List<ITree> getSearchTypeLabelforduplicatedassert(ITree node, String type, String label1, String label2) {
        ITree copynode = node.deepCopy();
        List<ITree> nodelist = breadthFirstSearchForTypeLabelfortwolables(copynode, type, label1, label2, "as1");
        return nodelist;
    }

    public static AssertCall getAssertCall(ITree assertnode) {
        AssertCall assertcall = null;
        ITree parent = assertnode;
        ITree callnode = null;
        boolean found = false;
        while (parent != null) {
            if (parent.getType().toString().toLowerCase().equals("call")) {
                found = true;
                break;
            }
            parent = parent.getParent();
        }

        //Called node should have two part: 1) MEthod call part and 2) Param part
        if (found) {
            assertcall = new AssertCall();
            List<ITree> childlist = parent.getChildren();
            String methodcalllabel = "";
            ITree namenode = childlist.get(0);
            for (int index = 0; index < namenode.getChildren().size(); index++) {
                methodcalllabel += namenode.getChild(index).getLabel();
            }

            assertcall.setAssertName(methodcalllabel);

            ITree param = childlist.get(childlist.size() - 1);

            List<ITree> paramlist = param.getChildren();

            for (ITree node : paramlist) {
                assertcall.addParam(node.getType().toString());
                assertcall.addParamTree(node.deepCopy());
            }
        }

        return assertcall;
    }

    private static List<ITree> breadthFirstSearchForNodeList(ITree node, String nodetype, String nodevisitedflag) {

        // Just so we handle receiving an uninitialized Node, otherwise an
        // exception will be thrown when we try to add it to queue
        // ITree classnode = null;
        List<ITree> nodelist = new ArrayList<>();
        if (node == null)
            return null;

        // Creating the queue, and adding the first node (step 1)
        LinkedList<ITree> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            ITree currentFirst = queue.removeFirst();

            // In some cases we might have added a particular node more than once before
            // actually visiting that node, so we make sure to check and skip that node if
            // we have
            // encountered it before
//            System.out.println(currentFirst.getType().toString());
//            System.out.println(currentFirst.getLabel());
            if (currentFirst.getType().toString().contains(nodetype)) {

                nodelist.add(currentFirst);

            }

//            nodelist.add(currentFirst);

            if (currentFirst.getMetadata(nodevisitedflag) != null)
                continue;

            // Mark the node as visited
            currentFirst.setMetadata(nodevisitedflag, 1);
            // System.out.print(currentFirst.name + " ");

            List<ITree> allNeighbors = currentFirst.getChildren();

            // We have to check whether the list of neighbors is null before proceeding,
            // otherwise
            // the for-each loop will throw an exception
            if (allNeighbors == null)
                continue;

            for (ITree neighbor : allNeighbors) {
                // We only add unvisited neighbors
                if (neighbor.getMetadata(nodevisitedflag) == null) {
                    queue.add(neighbor);
                }
            }
        }
        return nodelist;
    }

    public static List<ITree> breadthFirstSearchForLabel(ITree node, String label, String nodevisitedmeta) {

        // Just so we handle receiving an uninitialized Node, otherwise an
        // exception will be thrown when we try to add it to queue
        //ITree classnode = null;
        List<ITree> nodelist = new ArrayList<>();
        if (node == null)
            return null;
        // Creating the queue, and adding the first node (step 1)
        LinkedList<ITree> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            ITree currentFirst = queue.removeFirst();
            //System.out.println("currentFirst:"+currentFirst);

            // In some cases we might have added a particular node more than once before
            // actually visiting that node, so we make sure to check and skip that node if
            // we have
            // encountered it before
            if (currentFirst.getLabel().equals(label)) {
                nodelist.add(currentFirst);
                //classnode = currentFirst;
            }

            if (currentFirst.getMetadata(nodevisitedmeta) != null)
                continue;

            // Mark the node as visited
            currentFirst.setMetadata(nodevisitedmeta, 1);
            // System.out.print(currentFirst.name + " ");

            List<ITree> allNeighbors = currentFirst.getChildren();

            // We have to check whether the list of neighbors is null before proceeding,
            // otherwise
            // the for-each loop will throw an exception
            if (allNeighbors == null)
                continue;

            for (ITree neighbor : allNeighbors) {
                // We only add unvisited neighbors
                if (neighbor.getMetadata(nodevisitedmeta) == null) {
                    queue.add(neighbor);
                }
            }
        }
        return nodelist;
    }

//    private static List<ITree> breadthFirstSearchForTypeLabel(ITree node, String type, String label, String nodevisitedmeta) {
//        // Just so we handle receiving an uninitialized Node, otherwise an
//        // exception will be thrown when we try to add it to queue
//        //ITree classnode = null;
//        List<ITree> nodelist = new ArrayList<>();
//        if (node == null)
//            return null;
//
//        // Creating the queue, and adding the first node (step 1)
//        LinkedList<ITree> queue = new LinkedList<>();
//        queue.add(node);
//
//        while (!queue.isEmpty()) {
//            ITree currentFirst = queue.removeFirst();
//
//            // In some cases we might have added a particular node more than once before
//            // actually visiting that node, so we make sure to check and skip that node if
//            // we have
//            // encountered it before
//            System.out.println("Visiting node:");
//            System.out.println("Label: " + currentFirst.getLabel());
//            System.out.println("Type: " + currentFirst.getType().toString());
//
//            if (currentFirst.getType().toString().toLowerCase().equals("switch")) {
//                System.out.println(currentFirst.getLabel().toLowerCase());
//            }
//
//            if (currentFirst.getLabel().toLowerCase().equals(label) && currentFirst.getType().toString().toLowerCase().equals(type)) {
//                System.out.println("Matchfound:"+ currentFirst.getLabel());
//                nodelist.add(currentFirst);
//                //classnode = currentFirst;
//            }
//
//            if (currentFirst.getMetadata(nodevisitedmeta) != null)
//                continue;
//
//            // Mark the node as visited
//            currentFirst.setMetadata(nodevisitedmeta, 1);
//            // System.out.print(currentFirst.name + " ");
//
//            List<ITree> allNeighbors = currentFirst.getChildren();
//
//            // We have to check whether the list of neighbors is null before proceeding,
//            // otherwise
//            // the for-each loop will throw an exception
//            if (allNeighbors == null)
//                continue;
//
//            for (ITree neighbor : allNeighbors) {
//                // We only add unvisited neighbors
//                if (neighbor.getMetadata(nodevisitedmeta) == null) {
//                    queue.add(neighbor);
//                }
//            }
//        }
//        return nodelist;
//    }

    private static List<ITree> breadthFirstSearchForTypeLabel(ITree node, String type, String label, String nodevisitedmeta) {

        // Just so we handle receiving an uninitialized Node, otherwise an
        // exception will be thrown when we try to add it to queue
        //ITree classnode = null;
        List<ITree> nodelist = new ArrayList<>();
        if (node == null)
            return null;

        // Creating the queue, and adding the first node (step 1)
        LinkedList<ITree> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            ITree currentFirst = queue.removeFirst();

            // In some cases we might have added a particular node more than once before
            // actually visiting that node, so we make sure to check and skip that node if
            // we have
            // encountered it before
//            System.out.println("Visiting node:");
//            System.out.println("Label: " + currentFirst.getLabel());
//            System.out.println("Type: " + currentFirst.getType().toString());

//           System.out.println("CurrentFirstGetTpeNodeeee ===> "+ currentFirst.getType().toString());

            if (currentFirst.getType().toString().toLowerCase().equals("switch")) {
                System.out.println(currentFirst.getLabel().toLowerCase());
            }

            if (currentFirst.getLabel().toLowerCase().equals(label.toLowerCase()) && currentFirst.getType().toString().toLowerCase().equals(type.toLowerCase())) {
                nodelist.add(currentFirst);
                //classnode = currentFirst;
//            }
            }

            if (currentFirst.getMetadata(nodevisitedmeta) != null)
                continue;

            // Mark the node as visited
            currentFirst.setMetadata(nodevisitedmeta, 1);
            // System.out.print(currentFirst.name + " ");

            List<ITree> allNeighbors = currentFirst.getChildren();

            // We have to check whether the list of neighbors is null before proceeding,
            // otherwise
            // the for-each loop will throw an exception
            if (allNeighbors == null)
                continue;

            for (ITree neighbor : allNeighbors) {
                // We only add unvisited neighbors
                if (neighbor.getMetadata(nodevisitedmeta) == null) {
                    queue.add(neighbor);
                }
            }
        }
        return nodelist;
    }


    private static List<ITree> breadthFirstSearchForTypeLabelfortwolables(ITree node, String type, String label, String label2, String nodevisitedmeta) {

        // Just so we handle receiving an uninitialized Node, otherwise an
        // exception will be thrown when we try to add it to queue
        //ITree classnode = null;
        List<ITree> nodelist = new ArrayList<>();
        if (node == null)
            return null;

        // Creating the queue, and adding the first node (step 1)
        LinkedList<ITree> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            ITree currentFirst = queue.removeFirst();

            // In some cases we might have added a particular node more than once before
            // actually visiting that node, so we make sure to check and skip that node if
            // we have
            // encountered it before
//            System.out.println("Visiting node:");
//            System.out.println("Label: " + currentFirst.getLabel());
//            System.out.println("Type: " + currentFirst.getType().toString());

//           System.out.println("CurrentFirstGetTpeNodeeee ===> "+ currentFirst.getType().toString());

            if (currentFirst.getType().toString().toLowerCase().equals("switch")) {
                System.out.println(currentFirst.getLabel().toLowerCase());
            }

            if((currentFirst.getLabel().toLowerCase().equals(label.toLowerCase()) && currentFirst.getType().toString().toLowerCase().equals(type.toLowerCase())) || (currentFirst.getLabel().toLowerCase().equals(label2.toLowerCase()) && currentFirst.getType().toString().toLowerCase().equals(type.toLowerCase()))){
                nodelist.add(currentFirst);
                //classnode = currentFirst;
//            }
            }

            if (currentFirst.getMetadata(nodevisitedmeta) != null)
                continue;

            // Mark the node as visited
            currentFirst.setMetadata(nodevisitedmeta, 1);
            // System.out.print(currentFirst.name + " ");

            List<ITree> allNeighbors = currentFirst.getChildren();

            // We have to check whether the list of neighbors is null before proceeding,
            // otherwise
            // the for-each loop will throw an exception
            if (allNeighbors == null)
                continue;

            for (ITree neighbor : allNeighbors) {
                // We only add unvisited neighbors
                if (neighbor.getMetadata(nodevisitedmeta) == null) {
                    queue.add(neighbor);
                }
            }
        }
        return nodelist;
    }

    private static List<ITree> breadthFirstSearchForTypeLabel2(ITree node, String type, String label, String nodeVisitedMeta) {
        List<ITree> nodeList = new ArrayList<>();
        if (node == null) {
            //System.out.println("Node is null. Returning an empty list.");
            return nodeList;
        }

        // Queue for BFS
        LinkedList<ITree> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            ITree currentFirst = queue.removeFirst();

            // Debug: Print the current node's type and label
            //System.out.println("Visiting node:");
            //System.out.println("Type: " + currentFirst.getType());
            //System.out.println("Label: " + currentFirst.getLabel());

            // Match type and label, handling nested names
            boolean typeMatches = (type == null || currentFirst.getType().toString().equalsIgnoreCase(type));
            boolean labelMatches = (label == null || isMatchingLabel(currentFirst, label));

            if (typeMatches && labelMatches) {
                nodeList.add(currentFirst);
                //System.out.println("Match found! Node added to the list.");
            }

            // Skip if node has already been visited
            if (currentFirst.getMetadata(nodeVisitedMeta) != null) {
                continue;
            }

            // Mark as visited
            currentFirst.setMetadata(nodeVisitedMeta, 1);

            // Add children to queue
            List<ITree> children = currentFirst.getChildren();
            if (children != null) {
                queue.addAll(children);
            }
        }

        //System.out.println("Total matched nodes: " + nodeList.size());
        return nodeList;
    }


    private static boolean isMatchingLabel(ITree node, String targetLabel) {
        if (node == null) {
            return false;
        }

        //System.out.println("Checking node: Type=" + node.getType() + ", Label=" + node.getLabel());
        //System.out.println("Expected Label=" + targetLabel);

        boolean matches = targetLabel != null && node.getLabel() != null && node.getLabel().equalsIgnoreCase(targetLabel);

        if (!matches) {
            //System.out.println("Node does not match: " + node.getLabel());
        }

        return matches;
    }










    public static void printTree(ITree node, String indent) {
        if (node == null) {
            return;
        }

        System.out.println(indent + "├── Label: " + node.getLabel());
        System.out.println(indent + "│   └── Type: " + node.getType().toString());

        List<ITree> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
            if (i == children.size() - 1) {
                printTree(children.get(i), indent + "    ");
            } else {
                printTree(children.get(i), indent + "│   ");
            }
        }
    }


}
