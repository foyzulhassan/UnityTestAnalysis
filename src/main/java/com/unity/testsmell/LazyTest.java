//package com.unity.testsmell;
//
//
//import com.config.Config;
//import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
//import com.github.gumtreediff.tree.ITree;
//
//import java.util.*;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//public class LazyTest {
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
//    private Map<String,Integer> ressources_paths=new HashMap<>();
//
//    public void add_ressources(ITree tree){
//        ITree tree_copy=tree.getParent().deepCopy();
//        tree_copy.getChildren().forEach(ch -> {
//        List<ITree> literals_list = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNodeList(tree_copy,"literal","l1");
//        for (ITree lit: literals_list){
//            String label = lit.getLabel();
//            if(ressources_paths.containsKey(label))
//            {
//                ressources_paths.put(label,ressources_paths.get(label)+1);
//            }
//            else{
//                ressources_paths.put(label,1);
//            }
//        }
//        });
//
//    }
//
//    public boolean test_ressources(ITree tree){
//        ITree tree_copy=tree.getParent().deepCopy();
//        AtomicBoolean result = new AtomicBoolean(false);
//        tree_copy.getChildren().forEach(ch -> {
//            List<ITree> literals_list = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNodeList(tree_copy,"literal","l1");
//            System.out.println(literals_list);
//            for (ITree lit: literals_list){
//                String label = lit.getLabel();
//                if(ressources_paths.get(label)>1)
//                {
//                    result.set(true);
//                }
//            }
//        });
//        return result.get();
//
//    }
//
//
//
//    public void getSmell(ITree root)
//	{
//
//	}
//
////	public Map<String,Boolean> searchForLazyTest(ITree root)
////	{
////		List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
////        List<ITree> testfunclistCopy = new ArrayList<>();
////        for( ITree testfunc: testfunclist){
////            ITree copy = testfunc.deepCopy();
////            testfunclistCopy.add(copy);
////        }
////		Map<String,Boolean> LazyTest=new HashMap<>();
////		ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");
////
////		if(classnode==null)
////			return LazyTest;
////
////		ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);
////
////		String lowerclassname = classname.getLabel();
////
////        Map<ITree,Integer> funcs_map = new HashMap<>();
////        for(ITree testfunc:testfunclist)
////		{
////			List<ITree> calls_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "call", "");
//////			ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//////    		String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
////			if(calls_list!=null && calls_list.size()>0)
////			{
////				for(ITree call : calls_list){
////                    call.getChildren().forEach( ch -> {
////                        if(ch.getType().toString().equalsIgnoreCase("name")){
////                            if (funcs_map.containsKey(ch)){
////                                funcs_map.put(ch,funcs_map.get(ch)+1);
////                            } else {
////                                funcs_map.put(ch,1);
////                            }
////                        }
////                    });
////
////                }
////			}
////            List<ITree> resources_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "Resources");
////            resources_list.forEach(this::add_ressources);
////		}
////        for(ITree testfunc:testfunclistCopy){
////            List<ITree> calls_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "call", "");
////            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
////            String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
////            if(calls_list!=null && calls_list.size()>0)
////            {
////                for(ITree call : calls_list){
////                    call.getChildren().forEach( ch -> {
////                        if(ch.getType().toString().equalsIgnoreCase("name")){
////                            for(ITree funcTree: funcs_map.keySet()){
////                                if(funcs_map.get(funcTree) > 1) {
////                                    if (sub_tree_matcher(ch,funcTree,false)) {
////                                        LazyTest.put(classtestfunc,true);
////                                    }
////
////                                }
////
////                            }
////
////                        }
////                    });
////
////                }
////                List<ITree> resources_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "Resources");
////                for (ITree resource:resources_list){
////                    if(test_ressources(resource)){
////                        LazyTest.put(classtestfunc,true);
////                        break;
////                    }
////                }
////            }
////
////            if (!LazyTest.containsKey(classtestfunc)){
////                LazyTest.put(classtestfunc,false);
////            }
////
////        }
////        if(ressources_paths.size() > 0)
////            System.out.println(ressources_paths);
////        return LazyTest;
////
////
////	}
//
//    public Map<String, Boolean> searchForLazyTest(ITree root) {
//
//        // Get the list of test functions
//        List<ITree> testfunclist = TreeNodeAnalyzer.getTestFunctionList(root);
//
//        // Map to store test functions and their lazy status
//        Map<String, Boolean> LazyTest = new HashMap<>();
//        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");
//
//        if (classnode == null) return LazyTest;
//
//        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);
//        String lowerclassname = classname.getLabel();
//
//        // Map to store which test methods call each production method
//        Map<String, Set<String>> funcCallTestFuncMap = new HashMap<>();
//
//        // Loop through test functions to track function calls
//        for (ITree testfunc : testfunclist) {
//            String testFuncName = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc).getLabel();
//
//            // Get the list of function calls within the test function
//            List<ITree> calls_list = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "call", "");
//
//            // Keep track of which functions are called in each test method
//            Set<String> calledFunctionsInTest = new HashSet<>();
//
//            for (ITree call : calls_list) {
//                ITree funcnameNode = SrcmlUnityCsMetaDataGenerator.getFuncName(call);
//                if (funcnameNode != null) {
//                    String funcname = funcnameNode.getLabel();
//
//                    // Track which test functions call this function
//                    funcCallTestFuncMap.computeIfAbsent(funcname, k -> new HashSet<>()).add(testFuncName);
//
//                    // Track the functions called within this particular test function
//                    calledFunctionsInTest.add(funcname);
//                }
//            }
//            List<ITree> resources_list = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "Resources");
//            resources_list.forEach(this::add_ressources);
//        }
//
//        // Mark lazy tests: check if the same function is called by more than one test function
//        for (ITree testfunc : testfunclist) {
//            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//            String classtestfunc = lowerclassname + Config.separatorStr + funcnamenode.getLabel();
//
//            boolean isLazy = false;
//            List<ITree> calls_list = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "call", "");
//
//            // Iterate through the calls in the current test function
//            for (ITree call : calls_list) {
//                ITree funcnameNode = SrcmlUnityCsMetaDataGenerator.getFuncName(call);
//                if (funcnameNode != null) {
//                    String funcname = funcnameNode.getLabel();
//
//                    // Check if the same production function is called across multiple test functions
//                    Set<String> callingTestFuncs = funcCallTestFuncMap.get(funcname);
//                    if (callingTestFuncs != null && callingTestFuncs.size() > 1) {
//                        isLazy = true;  // Mark as lazy if the function is called across multiple test functions
//                        break;
//                    }
//                }
//            }
//
//            LazyTest.put(classtestfunc, isLazy);  // Update map with lazy status for the test
//        }
//
//        if (ressources_paths.size() > 0)
//            System.out.println(ressources_paths);
//
//        return LazyTest;
//    }
//
//
//
//
//	public double getLazyTestStats(Map<String,Boolean> testfuncconditionalTestmap)
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
//}
//
//package com.unity.testsmell;
//
//import com.config.Config;
//import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
//import com.github.gumtreediff.tree.ITree;
//
//import java.util.*;
//
//public class LazyTest {
//
//    private final Map<String, Integer> resourcePaths = new HashMap<>();
//
//    /**
//     * Adds resource paths to track usage across tests.
//     */
//    public void addResource(ITree tree) {
//        List<ITree> literalsList = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNodeList(tree, "literal", "l1");
//        for (ITree literal : literalsList) {
//            String label = literal.getLabel();
//            resourcePaths.put(label, resourcePaths.getOrDefault(label, 0) + 1);
//        }
//    }
//
//
//    /**
//     * Identifies lazy tests based on tree structure.
//     */
////    public Map<String, Boolean> searchForLazyTest(ITree root) {
////        List<ITree> testFuncList = TreeNodeAnalyzer.getTestFunctionList(root);
////        Map<String, Boolean> lazyTests = new HashMap<>();
////        ITree classNode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");
////
////        if (classNode == null) return lazyTests;
////
////        String className = SrcmlUnityCsMetaDataGenerator.getClassName(classNode).getLabel();
////
////        Map<String, Set<String>> funcCallTestFuncMap = new HashMap<>();
////
////        for (ITree testFunc : testFuncList) {
////            String testFuncName = SrcmlUnityCsMetaDataGenerator.getFuncName(testFunc).getLabel();
////            List<ITree> callsList = TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "call", "");
////            List<ITree> resourcesList = TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "name", "Resources");
////
////            Set<String> calledFunctionsInTest = new HashSet<>();
////            boolean usesMockObjects = usesMockObjects(testFunc);
////
////            for (ITree call : callsList) {
////                if (isMockCall(call)) {
////                    System.out.println("Skipping mock call: " + SrcmlUnityCsMetaDataGenerator.getFuncName(call).getLabel());
////                    continue; // Skip mock calls
////                }
////
////                ITree funcNameNode = SrcmlUnityCsMetaDataGenerator.getFuncName(call);
////                if (funcNameNode != null) {
////                    String funcName = funcNameNode.getLabel();
////                    if (isTestSetupCall(funcName)) {
////                        System.out.println("Skipping test setup call: " + funcName);
////                        continue; // Skip test setup calls like vector3, etc.
////                    }
////                    funcCallTestFuncMap.computeIfAbsent(funcName, k -> new HashSet<>()).add(testFuncName);
////                    calledFunctionsInTest.add(funcName);
////                }
////            }
////
////            resourcesList.forEach(this::addResource);
////
////            String classTestFunc = className + Config.separatorStr + testFuncName;
////            boolean isLazy = !usesMockObjects &&
////                    (calledFunctionsInTest.stream().anyMatch(func -> funcCallTestFuncMap.get(func).size() > 1) || testResource(testFunc));
////
////            System.out.println("Test: " + classTestFunc + ", Lazy: " + isLazy);
////            lazyTests.put(classTestFunc, isLazy);
////        }
////
////        return lazyTests;
////    }
//    public boolean testResource(ITree tree) {
//        // List of generic setup resources to exclude
//        Set<String> excludedResources = Set.of("vector3", "vector3equalitycomparer", "using");
//
//        List<ITree> literalsList = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNodeList(tree, "literal", "l1");
//        for (ITree literal : literalsList) {
//            String label = literal.getLabel();
//
//            // Skip excluded setup resources
//            if (excludedResources.contains(label.toLowerCase())) {
//                continue;
//            }
//
//            // Check for shared usage
//            if (resourcePaths.getOrDefault(label, 0) > 1) {
//                System.out.println("Shared resource detected: " + label);
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private static final Set<String> NON_PRODUCTION_CALLS = new HashSet<>(Arrays.asList(
//            "Using", "Vector3EqualityComparer", "Vector3", ""
//    ));
//
//    public Map<String, Boolean> searchForLazyTest(ITree root) {
//        List<ITree> testFuncList = TreeNodeAnalyzer.getTestFunctionList(root);
//        Map<String, Boolean> lazyTests = new HashMap<>();
//        ITree classNode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");
//
//        if (classNode == null) return lazyTests;
//
//        String className = SrcmlUnityCsMetaDataGenerator.getClassName(classNode).getLabel();
//
//        Map<String, Set<String>> funcCallTestFuncMap = new HashMap<>();
//
//        // Analyze each test function
//        for (ITree testFunc : testFuncList) {
//            String testFuncName = SrcmlUnityCsMetaDataGenerator.getFuncName(testFunc).getLabel();
//            List<ITree> callsList = TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "call", "");
//            List<ITree> resourcesList = TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "name", "Resources");
//
//            Set<String> calledFunctionsInTest = new HashSet<>();
//            boolean usesMockObjects = this.usesMockObjects(testFunc);
//
//            // Process function calls
//            for (ITree call : callsList) {
//                ITree funcNameNode = SrcmlUnityCsMetaDataGenerator.getFuncName(call);
//                if (funcNameNode != null) {
//                    String funcName = funcNameNode.getLabel();
//
//                    // Filter out non-production calls
//                    if (!NON_PRODUCTION_CALLS.contains(funcName)) {
//                        funcCallTestFuncMap.computeIfAbsent(funcName, k -> new HashSet<>()).add(testFuncName);
//                        calledFunctionsInTest.add(funcName);
//                    }
//                }
//            }
//
//            // Add resources for tracking
//            for (ITree resource : resourcesList) {
//                this.addResource(resource);
//            }
//
//            // Evaluate laziness
//            String classTestFunc = className + Config.separatorStr + testFuncName;
//            boolean isLazy = !usesMockObjects &&
//                    (calledFunctionsInTest.stream().anyMatch(func -> funcCallTestFuncMap.get(func).size() > 1)
//                            || this.testResource(testFunc)); // Check only valid resources
//            lazyTests.put(classTestFunc, isLazy);
//
//            // Debugging output for clarity
//            if (isLazy) {
//                System.out.println("Test marked as lazy: " + classTestFunc);
//                System.out.println("- Does not use mock objects.");
//                System.out.println("- Shared function calls: " + calledFunctionsInTest);
//            }
//        }
//
//        return lazyTests;
//    }
//
//    /**
//     * Determines if a function call is a mock-related call.
//     */
//    private boolean isMockCall(ITree call) {
//        ITree funcNameNode = SrcmlUnityCsMetaDataGenerator.getFuncName(call);
//        if (funcNameNode == null) return false;
//
//        String funcName = funcNameNode.getLabel().toLowerCase();
//        boolean isMock = funcName.contains("mock") || funcName.contains("fake") || funcName.contains("stub");
//        System.out.println("Analyzing call: " + funcName + ", Is Mock: " + isMock);
//        return isMock;
//    }
//
//    /**
//     * Checks if a function is part of test setup logic.
//     */
//    private boolean isTestSetupCall(String funcName) {
//        List<String> setupKeywords = Arrays.asList("vector3", "vector3equalitycomparer", "using");
//        return setupKeywords.contains(funcName.toLowerCase());
//    }
//
//    /**
//     * Checks if a test function uses mock objects.
//     */
//    private boolean usesMockObjects(ITree testFunc) {
//        List<ITree> mockNodes = TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "mock", "");
//        List<ITree> newNodes = TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "new", "");
//
//        return !mockNodes.isEmpty() || newNodes.stream().anyMatch(node -> node.getLabel().toLowerCase().contains("mock"));
//    }
//
//    public double getLazyTestStats(Map<String, Boolean> testFuncConditionalTestMap) {
//        int total = testFuncConditionalTestMap.size();
//        if (total == 0) return -0.001;
//
//        long lazyCount = testFuncConditionalTestMap.values().stream().filter(Boolean::booleanValue).count();
//        return (double) lazyCount / total;
//    }
//}
//
//package com.unity.testsmell;
//import com.config.Config;
//import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
//import com.github.gumtreediff.tree.ITree;
//
//import java.util.*;
//
//public class LazyTest {
//
//    private final Map<String, Integer> resourcePaths = new HashMap<>();
//
//    // Adds resources to the tracking map
//    public void addResource(ITree tree) {
//        List<ITree> literalsList = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNodeList(tree, "literal", "l1");
//        for (ITree literal : literalsList) {
//            String label = literal.getLabel();
//            resourcePaths.put(label, resourcePaths.getOrDefault(label, 0) + 1);
//        }
//    }
//
//    // Main method to detect lazy tests
//    public Map<String, Boolean> searchForLazyTest(ITree root) {
//        List<ITree> testFuncList = TreeNodeAnalyzer.getTestFunctionList(root);
//        Map<String, Boolean> lazyTests = new HashMap<>();
//        ITree classNode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");
//
//        if (classNode == null) return lazyTests;
//
//        String className = SrcmlUnityCsMetaDataGenerator.getClassName(classNode).getLabel();
//        Map<String, Set<String>> funcCallTestFuncMap = new HashMap<>();
//
//        for (ITree testFunc : testFuncList) {
//            String testFuncName = SrcmlUnityCsMetaDataGenerator.getFuncName(testFunc).getLabel();
//            List<ITree> callsList = TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "call", "");
//            List<ITree> assertList = TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "assert", "");
//
//            Set<String> calledFunctionsInTest = new HashSet<>();
//            boolean usesMockObjects = usesMockObjects(testFunc);
//            boolean usesLibraryCalls = usesLibraryFunctions(testFunc);
//
//            for (ITree call : callsList) {
//                ITree funcNameNode = SrcmlUnityCsMetaDataGenerator.getFuncName(call);
//                if (funcNameNode != null) {
//                    String funcName = funcNameNode.getLabel();
//                    funcCallTestFuncMap.computeIfAbsent(funcName, k -> new HashSet<>()).add(testFuncName);
//                    calledFunctionsInTest.add(funcName);
//                }
//            }
//
//            String classTestFunc = className + Config.separatorStr + testFuncName;
//
//            // Mark as lazy if no assertions, no mocks, no library calls, and shared production calls
//            boolean isLazy = assertList.isEmpty() &&
//                    !usesMockObjects &&
//                    !usesLibraryCalls &&
//                    calledFunctionsInTest.stream().anyMatch(func -> funcCallTestFuncMap.get(func).size() > 1);
//
//            lazyTests.put(classTestFunc, isLazy);
//
//            // Debugging information
//            System.out.println("Test: " + classTestFunc);
//            System.out.println(" - Lazy: " + isLazy);
//            System.out.println(" - Uses Mock Objects: " + usesMockObjects);
//            System.out.println(" - Uses Library Calls: " + usesLibraryCalls);
//            System.out.println(" - Assertions: " + !assertList.isEmpty());
//            System.out.println(" - Shared Function Calls: " + calledFunctionsInTest);
//        }
//
//        return lazyTests;
//    }
//
//    private boolean usesMockObjects(ITree testFunc) {
//        List<ITree> mockNodes = TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "mock", "");
//        List<ITree> newNodes = TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "new", "");
//        return !mockNodes.isEmpty() || newNodes.stream().anyMatch(node -> node.getLabel().toLowerCase().contains("mock"));
//    }
//
//    private boolean usesLibraryFunctions(ITree testFunc) {
//        List<ITree> callNodes = TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "call", "");
//        for (ITree call : callNodes) {
//            String fullPath = getFullPath(call);
//            if (fullPath.startsWith("UnityEngine") || fullPath.startsWith("System")) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private String getFullPath(ITree node) {
//        StringBuilder fullPath = new StringBuilder();
//        ITree current = node;
//
//        while (current != null) {
//            if (!current.getLabel().isEmpty()) {
//                fullPath.insert(0, current.getLabel() + ".");
//            }
//            current = current.getParent();
//        }
//
//        if (fullPath.length() > 0 && fullPath.charAt(fullPath.length() - 1) == '.') {
//            fullPath.setLength(fullPath.length() - 1);
//        }
//
//        return fullPath.toString();
//    }
//
//    // Calculates lazy test statistics
//    public double getLazyTestStats(Map<String, Boolean> testFuncLazyMap) {
//        int totalTests = testFuncLazyMap.size();
//        if (totalTests == 0) return -1;
//
//        long lazyTestCount = testFuncLazyMap.values().stream().filter(Boolean::booleanValue).count();
//        return (double) lazyTestCount / totalTests;
//    }
//
//    public void logLazyTestStats(Map<String, Boolean> testFuncLazyMap) {
//        double lazyPercentage = getLazyTestStats(testFuncLazyMap) * 100;
//        System.out.println("Total Tests: " + testFuncLazyMap.size());
//        System.out.println("Lazy Tests: " + testFuncLazyMap.values().stream().filter(Boolean::booleanValue).count());
//        System.out.println("Lazy Test Percentage: " + lazyPercentage + "%");
//    }
//}

package com.unity.testsmell;

import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.*;

public class LazyTestRefined {

    // Main method to detect lazy tests
    public Map<String, Boolean> detectLazyTests(ITree root) {
        List<ITree> testFunctions = TreeNodeAnalyzer.getTestFunctionList(root);
        Map<String, Boolean> lazyTests = new HashMap<>();

        for (ITree testFunc : testFunctions) {
            String testFuncName = SrcmlUnityCsMetaDataGenerator.getFuncName(testFunc).getLabel();

            // Check for key characteristics of a lazy test
            boolean hasAssertions = !TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "assert", "").isEmpty();
            boolean usesMocks = !TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "mock", "").isEmpty();
            boolean usesSharedFunctions = !TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "call", "").isEmpty();

            // Lazy if no assertions, no mocks, and only shared function calls
            boolean isLazy = !hasAssertions && !usesMocks && usesSharedFunctions;

            lazyTests.put(testFuncName, isLazy);

            // Debugging information
            System.out.println("Test Function: " + testFuncName);
            System.out.println(" - Has Assertions: " + hasAssertions);
            System.out.println(" - Uses Mocks: " + usesMocks);
            System.out.println(" - Uses Shared Functions: " + usesSharedFunctions);
            System.out.println(" - Lazy Test: " + isLazy);
        }

        return lazyTests;
    }

    // Get lazy test statistics
    public double calculateLazyTestPercentage(Map<String, Boolean> testResults) {
        if (testResults.isEmpty()) return 0.0;

        long lazyTestCount = testResults.values().stream().filter(Boolean::booleanValue).count();
        return (double) lazyTestCount / testResults.size() * 100;
    }

    // Log lazy test results
    public void logLazyTestResults(Map<String, Boolean> testResults) {
        double lazyTestPercentage = calculateLazyTestPercentage(testResults);

        System.out.println("Total Tests: " + testResults.size());
        System.out.println("Lazy Tests: " + testResults.values().stream().filter(Boolean::booleanValue).count());
        System.out.println("Lazy Test Percentage: " + lazyTestPercentage + "%");
    }
}
