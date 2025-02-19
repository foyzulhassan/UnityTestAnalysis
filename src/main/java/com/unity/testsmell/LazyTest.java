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
//
//package com.unity.testsmell;
//import com.config.Config;
//import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
//import com.github.gumtreediff.tree.ITree;
//
//import java.util.*;
//
//public class LazyTest {
//    private static final List<String> ASSERT_CALLS = Arrays.asList(
//            "Assert", "That", "AreEqual", "AreNotEqual", "IsTrue", "IsFalse", "IsNull", "IsNotNull", "IsNotEmpty"
//    );
//
//    private static final List<String> EXCLUDED_CALLS = Arrays.asList(
//            "SetUp", "TearDown", "DummyObject", "GetVelocity", "GameObject", "AddComponent"
//    );
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
//
//        for (ITree testFunc : testFuncList) {
//            String testFuncName = SrcmlUnityCsMetaDataGenerator.getFuncName(testFunc).getLabel();
//            List<ITree> callsList = TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "call", "");
//            List<ITree> mockObjectList = TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "mock", "");
//
//            boolean hasAssertions = checkAssertions(callsList);
//            boolean usesMockObjects = !mockObjectList.isEmpty();
//            boolean hasMeaningfulCalls = checkMeaningfulProductionCalls(callsList);
//
//            String classTestFunc = className + Config.separatorStr + testFuncName;
//
//            // Lazy test detection logic
//            boolean isLazy = !hasAssertions && !usesMockObjects && !hasMeaningfulCalls;
//
//            lazyTests.put(classTestFunc, isLazy);
//
//            // Debugging output
//            System.out.println("Test: " + classTestFunc);
//            System.out.println(" - Lazy: " + isLazy);
//            System.out.println(" - Has Assertions: " + hasAssertions);
//            System.out.println(" - Uses Mock Objects: " + usesMockObjects);
//            System.out.println(" - Has Meaningful Calls: " + hasMeaningfulCalls);
//        }
//
//        return lazyTests;
//    }
//
//    // Check if assertions are present
//    private boolean checkAssertions(List<ITree> callsList) {
//        for (ITree call : callsList) {
//            ITree funcNameNode = SrcmlUnityCsMetaDataGenerator.getFuncName(call);
//            if (funcNameNode != null) {
//                String callName = funcNameNode.getLabel();
//                System.out.println("Checking assertion call: " + callName); // Debugging
//                for (String assertCall : ASSERT_CALLS) {
//                    if (callName.contains(assertCall)) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    // Check if there are meaningful production calls
//    private boolean checkMeaningfulProductionCalls(List<ITree> callsList) {
//        for (ITree call : callsList) {
//            ITree funcNameNode = SrcmlUnityCsMetaDataGenerator.getFuncName(call);
//            if (funcNameNode != null) {
//                String callName = funcNameNode.getLabel();
//                // Debugging: Print the call name being analyzed
//                System.out.println("Checking production call: " + callName);
//
//                // Skip calls that are utilities or test setup
//                if (!EXCLUDED_CALLS.contains(callName) && !callName.isEmpty()) {
//                    return true; // Found a meaningful production call
//                }
//            }
//        }
//        return false; // No meaningful calls found
//    }
//
//    // Get lazy test statistics
//    public double getLazyTestStats(Map<String, Boolean> testFuncLazyMap) {
//        int totalTests = testFuncLazyMap.size();
//        if (totalTests == 0) return -1;
//
//        long lazyTestCount = testFuncLazyMap.values().stream().filter(Boolean::booleanValue).count();
//        return (double) lazyTestCount / totalTests;
//    }
//
//    // Log lazy test statistics
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

public class LazyTest {

    /**
     * Main method to detect lazy tests in C# code.
     * Lazy tests occur when multiple test methods invoke the same method of the production object.
     *
     * @param root            The root of the AST.
     * @param productCallData The production calls data.
     * @return A map of test function names to whether they are lazy.
     */
    public Map<String, Boolean> searchForLazyTest(ITree root, Map<String, List<String>> productCallData) {
        Map<String, Boolean> lazyTests = new HashMap<>();
        Map<String, List<String>> normalizedProductCalls = normalizeProductCallData(productCallData);

        // Debug: Print normalized production calls
        //System.out.println("Normalized Production Calls: " + normalizedProductCalls);

        List<ITree> testFuncList = TreeNodeAnalyzer.getTestFunctionList(root);
        Map<String, Set<String>> testFunctionToProductionCalls = new HashMap<>();
        Map<String, Set<String>> productionMethodToTestFunctions = new HashMap<>();

        ITree classNode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if (classNode == null) return lazyTests;

        ITree classNameNode = SrcmlUnityCsMetaDataGenerator.getClassName(classNode);
        String className = classNameNode.getLabel();

        for (ITree testFunc : testFuncList) {
            String testFuncName = SrcmlUnityCsMetaDataGenerator.getFuncName(testFunc).getLabel();

            if (testFuncName == null || testFuncName.isEmpty()) {
                //System.out.println("Skipping unnamed test function.");
                continue;
            }

            List<ITree> callsList = TreeNodeAnalyzer.getSearchTypeLabel(testFunc, "call", "");
           // System.out.println("callsList: " + callsList);
            if (callsList == null || callsList.isEmpty()) {
                //System.out.println("No calls found for test function: " + testFuncName);
                continue;
            }

            Set<String> invokedProductionMethods = new HashSet<>();

            for (ITree call : callsList) {
                //Log raw node details
                //System.out.println("Raw call node: " + call.toString());
                //System.out.println("Subtree for call node:\n" + call.toTreeString());
                for (ITree child : call.getChildren()) {
                    //System.out.println("Child node: " + child.toString() + ", Label: " + child.getLabel() + ", Type: " + child.getType());
                }

                // Check for excluded production objects
                if (isExcludedProductionObject(call)) {
                    //System.out.println("Excluded call: " + extractObjectNameFromCall(call));
                    continue;
                }

                // Extract function name
                ITree funcNameNode = SrcmlUnityCsMetaDataGenerator.getFuncName(call);
                String callName = null;

                if (funcNameNode != null && funcNameNode.getLabel() != null && !funcNameNode.getLabel().isEmpty()) {
                    callName = funcNameNode.getLabel();
                    //System.out.println("Extracted call name: " + callName);
                } else {
                    //System.out.println("Function name node is null or has an empty label for call: " + call.toString());
                    callName = extractFallbackFunctionName(call);
                    //System.out.println("Fallback extracted call name: " + callName);
                }

                if (callName != null) {
                     // Match with normalized production calls
                    String matchedMethod = fuzzyMatchCallName(callName, normalizedProductCalls);
                    if (matchedMethod != null) {
                        //System.out.println("Matched production method: " + matchedMethod);
                        invokedProductionMethods.add(matchedMethod);

                        // Track which test functions call this production method
                        productionMethodToTestFunctions
                                .computeIfAbsent(matchedMethod, k -> new HashSet<>())
                                .add(testFuncName);
                    } else {
                        //System.out.println("No match found for call name: " + callName);
                    }
                }
            }

            testFunctionToProductionCalls.put(testFuncName, invokedProductionMethods);

            // Debug: Log invoked production methods for the test function
            //System.out.println("Test function: " + testFuncName + ", Invoked production methods: " + invokedProductionMethods);
        }

        // Analyze lazy test determination
        for (Map.Entry<String, Set<String>> entry : testFunctionToProductionCalls.entrySet()) {
            String testFuncName = entry.getKey();
            Set<String> productionMethods = entry.getValue();

            // A test is considered lazy if it invokes a production method that is called by other test functions
            boolean isLazy = productionMethods.stream()
                    .anyMatch(method -> productionMethodToTestFunctions.getOrDefault(method, Collections.emptySet()).size() > 1);

            // Debug: Log the lazy test determination
            //System.out.println("Test function: " + testFuncName + ", Is Lazy: " + isLazy);

            lazyTests.put(className + Config.separatorStr + testFuncName, isLazy);
        }

        return lazyTests;
    }

    private Map<String, List<String>> normalizeProductCallData(Map<String, List<String>> productCalls) {
        Map<String, List<String>> normalizedProductCalls = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : productCalls.entrySet()) {
            String normalizedKey = normalizeCallName(entry.getKey());
            List<String> normalizedValues = new ArrayList<>();
            for (String value : entry.getValue()) {
                normalizedValues.add(normalizeCallName(value));
            }
            normalizedProductCalls.put(normalizedKey, normalizedValues);
        }
        return normalizedProductCalls;
    }

    private String normalizeCallName(String callName) {
        return callName.trim().toLowerCase();
    }

    private String fuzzyMatchCallName(String callName, Map<String, List<String>> normalizedProductCalls) {
        String normalizedCallName = normalizeCallName(callName);
        //System.out.println("Normalized call name: " + normalizedCallName);

        // Split the call name into potential class and method names
        String[] parts = normalizedCallName.split("\\.");
        if (parts.length == 2) {
            String className = parts[0];  // Top-level class or object name
            String methodName = parts[1]; // Method being called

            //System.out.println("Checking for class: " + className + ", method: " + methodName);

            // Check if the class exists in normalized product calls
            if (normalizedProductCalls.containsKey(className)) {
                List<String> methods = normalizedProductCalls.get(className);

                // Match the method name
                if (methods.contains(methodName)) {
                    return className + "<>" + methodName;
                } else {
                    //System.out.println("No match for method: " + methodName + " in class: " + className);
                }
            } else {
                //System.out.println("Class not found in production calls: " + className);
            }
        } else {
           // System.out.println("Invalid call name format: " + normalizedCallName);
        }

        return null;
    }

    private boolean isExcludedProductionObject(ITree call) {
        String objectName = extractObjectNameFromCall(call);
        if (objectName == null || objectName.isEmpty()) {
            return false;
        }

        // Exclude mocks and Unity objects
        return objectName.toLowerCase().contains("mock") || objectName.contains("Unity");
    }

    private String extractObjectNameFromCall(ITree call) {
        for (ITree child : call.getChildren()) {
            if ("name".equalsIgnoreCase(String.valueOf(child.getType()))) {
                return child.getLabel();
            }

            String extractedName = extractObjectNameFromCall(child);
            if (extractedName != null) {
                return extractedName;
            }
        }
        return null;
    }

    private String extractFallbackFunctionName(ITree call) {
        StringBuilder functionNameBuilder = new StringBuilder();

        for (ITree child : call.getChildren()) {
            if ("name".equalsIgnoreCase(String.valueOf(child.getType()))) {
                // Check if the name node has a non-empty label
                if (child.getLabel() != null && !child.getLabel().isEmpty()) {
                    functionNameBuilder.append(child.getLabel());
                } else {
                    // Recursively extract names from child nodes
                    String extractedName = extractFallbackFunctionName(child);
                    if (extractedName != null) {
                        functionNameBuilder.append(extractedName);
                    }
                }
            } else if ("operator".equalsIgnoreCase(String.valueOf(child.getType()))) {
                // Add operator (e.g., ".") to the function name
                functionNameBuilder.append(child.getLabel());
            }
        }

        return functionNameBuilder.length() > 0 ? functionNameBuilder.toString() : null;
    }

    public double getLazyTestStats(Map<String, Boolean> testFuncLazyMap) {
        if (testFuncLazyMap == null || testFuncLazyMap.isEmpty()) {
            return -1; // Return -1 if no tests are present.
        }

        long totalTests = testFuncLazyMap.size();
        long lazyTestCount = testFuncLazyMap.values().stream().filter(Boolean::booleanValue).count();

        return ((double) lazyTestCount / totalTests) * 100;
    }
}
