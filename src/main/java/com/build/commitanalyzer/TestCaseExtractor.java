package com.build.commitanalyzer;

import com.github.gumtreediff.tree.ITree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;
import com.unity.testsmell.TreeNodeAnalyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static groovyjarjarantlr.build.ANTLR.root;

public class TestCaseExtractor {
    public Map<String, String> extractTestCasesWithFileName(ITree rootTree, String fileName) {
        Map<String, String> testCases = new HashMap<>();

        // Traverse the tree and extract test function names (based on attribute or method naming convention)
        for (ITree node : rootTree.getChildren()) {
            if (isTestFunction(node)) {
                String functionName = getFunctionName(node);
                String fileAndFunctionName = fileName + "::" + functionName; // FileName::FunctionName
                testCases.put(functionName, fileName);
            }
        }

        return testCases;
    }

    private boolean isTestFunction(ITree node) {
        // Logic to detect test functions based on annotations or naming convention like [Test]
        return node.getLabel().contains("[Test]") || node.getLabel().contains("Test");
    }

    private String getFunctionName(ITree node) {
        // Logic to extract the function name from the node
        return node.getLabel(); // Adjust this based on how function names are represented in your AST
    }

    public Map<String, Boolean> searchFornosmells(ITree root) {
        // Get all the functions marked as test functions
        List<ITree> testfunclist = TreeNodeAnalyzer.getTestFunctionList(root);

        // A map to store the result where the key is the class + function name, and the value is true if it is a test case
        Map<String, Boolean> testCaseMap = new HashMap<>();

        // Get the class node (from SrcML)
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if (classnode == null)
            return testCaseMap;  // Return an empty map if no class is found

        // Get the class name
        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);
        String lowerclassname = classname.getLabel();  // Store the class name

        // Iterate over each function in the class
        for (ITree testfunc : testfunclist) {
            // Get the function name
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
            String classtestfunc = lowerclassname + Config.separatorStr + funcnamenode.getLabel();  // Combine class name and function name

            // Assuming that if it is in testfunclist, it is a test case, so we mark it as true
            testCaseMap.put(classtestfunc, true);
        }

        return testCaseMap;
    }

}
