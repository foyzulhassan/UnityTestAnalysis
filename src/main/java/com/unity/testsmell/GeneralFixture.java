package com.unity.testsmell;//package com.unity.testsmell;
//
//import com.github.gumtreediff.tree.Type;
//import org.javatuples.Pair;
//import com.config.Config;
//import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
//import com.github.gumtreediff.tree.ITree;
//
//import java.security.Key;
//import java.util.*;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//public class GeneralFixture {
//
//
//    public void getSmell(ITree root) {
//
//    }
//
//    public List<Pair<String, ITree>> collect_fields(List<ITree> all_funcs, ITree func) {
//        try {
//            ITree func_copy = func.deepCopy();
//            ArrayList<Pair<String, ITree>> namesList = new ArrayList<>();
//            func_copy.getChildren().forEach(
//                    child -> {
//                        List<ITree> list_call = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNodeList(child, "call", "call1");
//                        for (ITree call : list_call) {
//                            call.getChildren().forEach(
//                                    child2 -> {
//                                        if (child2.getType().toString().equals("name")) {
//                                            AtomicBoolean constructor = new AtomicBoolean(false);
//                                            call.getParent().getChildren().forEach(op -> {
//                                                if (op.getLabel().equals("new"))
//                                                    constructor.set(true);
//                                            });
//                                            if (!constructor.get()) {
//                                                String func_name = child2.getLabel();
////                                        System.out.println(func_name);
//                                                all_funcs.forEach(ftemp -> {
//                                                    ftemp.getChildren().forEach(child3 -> {
//                                                        if (child3.getType().toString().equals("name") && child3.getLabel().equals(func_name)) {
//                                                            List<Pair<String, ITree>> add_list = collect_fields(all_funcs, ftemp);
//                                                            namesList.addAll(add_list);
//                                                        }
//                                                    });
//
//                                                });
//                                            }
//                                        }
//                                    }
//                            );
//
//                        }
//
//                    }
//
//
//            );
////        System.out.println(func_copy.getChildren());
//            List<ITree> decls = TreeNodeAnalyzer.getSearchTypeLabel(func_copy, "operator", "=");
//            decls.forEach(d -> {
//                ITree ch = d.getParent().getChild(0);
//                if (ch.getType().toString().equals("name")) {
//                    Pair<String, ITree> p = new Pair<>("property", ch);
//                    namesList.add(p);
//                }
//            });
//            decls = TreeNodeAnalyzer.getSearchTypeLabel(func_copy, "init", "");
//            decls.forEach(d -> d.getParent().getChildren().forEach(ch ->
//                    {
//                        if (ch.getType().toString().equals("name")) {
//                            Pair<String, ITree> p = new Pair<>("object", ch);
//                            namesList.add(p);
//                        }
//                    })
//            );
////        System.out.println(decls.size());
//            return namesList;
//        }
//        catch (Exception e){
//            return new ArrayList<Pair<String, ITree>>() ;
//        }
//    }
//
//    public boolean sub_tree_matcher(ITree property, ITree testFuncTree, boolean objectMatch)
//    {
//        if ( property.getType() == testFuncTree.getType() && Objects.equals(property.getLabel(), testFuncTree.getLabel()))
//        {
//            if (property.isLeaf() && testFuncTree.isLeaf()){
//                return true;
//            } else if (objectMatch){
//                return false;
//            } else{
//                if(property.getChildren().size() != testFuncTree.getChildren().size())
//                {
//                    return false;
//                }
//                boolean total=true;
//                for (int counter = 0; counter<property.getChildren().size();counter++){
//                   total=total && sub_tree_matcher(property.getChild(counter),testFuncTree.getChild(counter),false);
//                }
//                return total;
//            }
//
//        }
//        boolean result=false;
//        if(testFuncTree.getChildren().size() == 0){
//            return false;
//        } else{
//            for(ITree ch : testFuncTree.getChildren()){
//                result=  sub_tree_matcher(property,ch,objectMatch);
//                if(result){
//                    break;
//                }
//            }
//        }
//        return result;
//    }
//
//    public double fields_in_test_func_matcher(ITree testfunc, List<Pair<String, ITree>> init_fields) {
//        List<Pair<String, ITree>> fields_copy = new ArrayList<>(init_fields);
//        int fields_number = init_fields.size();
//        if (fields_number == 0) {
//            return 0.0;
//        }
//
//        init_fields.forEach(p -> {
//            System.out.println("Matching field: " + p);
//            if (Objects.equals(p.getValue0(), "object")) {
//                if (sub_tree_matcher(p.getValue1(), testfunc, true)) {
//                    System.out.println("Found Object Match: " + p);
//                    fields_copy.remove(p);
//                }
//            } else if (Objects.equals(p.getValue0(), "property")) {
//                if (sub_tree_matcher(p.getValue1(), testfunc, false)) {
//                    System.out.println("Found Property Match: " + p);
//                    fields_copy.remove(p);
//                }
//            } else {
//                System.out.println("Unsupported field type: " + p.getValue0());
//            }
//        });
//
//        int new_number = fields_copy.size();
//        System.out.println("Fields remaining: " + new_number + "/" + fields_number);
//        return ((double) new_number / fields_number) * 100;
//    }
//
//    public Map<String, Double> searchForGeneralFixture(ITree root) {
//        Map<String, Double> generalFixtureMap = new HashMap<>();
//        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");
//        if (classnode == null) {
//            return generalFixtureMap;
//        }
//
//        List<ITree> setupfuncslist = TreeNodeAnalyzer.getSetupFunctionsList(root);
//        List<ITree> testfuncslist = TreeNodeAnalyzer.getTestFunctionList(root);
//        List<ITree> allfuncs = TreeNodeAnalyzer.getFunctionList(root);
//        List<Pair<String, ITree>> initialized_fields = new ArrayList<>();
//
//        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);
//        String lowerclassname = classname.getLabel();
//        String result = null;
//
//        for (ITree setupfunc : setupfuncslist) {
//            String funcnamenode = SrcmlUnityCsMetaDataGenerator.getFunctionName(setupfunc);
//            List<ITree> subjectlist = TreeNodeAnalyzer.getSearchTypeLabel(setupfunc, "name", "subject");
//            //System.out.println("subjectlist: " + subjectlist);
//
//            // Safely retrieve `result` only if valid
//            if (!subjectlist.isEmpty() && setupfunc.getParent() != null) {
//                List<ITree> parentChildren = setupfunc.getParent().getChildren();
//                if (parentChildren.size() > 2) {
//                    List<ITree> grandChildren = parentChildren.get(2).getChildren();
//                    if (!grandChildren.isEmpty() && !grandChildren.get(0).getChildren().isEmpty()) {
//                        result = grandChildren.get(0).getChildren().get(0).getLabel();
//                    }
//                }
//            }
//
//            //System.out.println("result: " + result);
//            String classtestfunc = lowerclassname + Config.separatorStr + funcnamenode;
//            //System.out.println(classtestfunc);
//
//            List<Pair<String, ITree>> fields = collect_fields(allfuncs, setupfunc);
//            initialized_fields.addAll(fields);
//        }
//
//        //System.out.println("fields-----" + initialized_fields);
//
//        // Safely filter `initialized_fields` without modifying during iteration
//        List<Pair<String, ITree>> filteredFields = new ArrayList<>(initialized_fields);
//        for (Pair<String, ITree> x : initialized_fields) {
//            //System.out.println("x: " + x);
//            String res = String.valueOf(x);
//            //System.out.println("res: " + res);
//
//            if (result != null && res.contains(result)) {
//                filteredFields.remove(x);
//            }
//            System.out.println("filteredFields"+filteredFields);
//        }
//
//        System.out.println("filteredFields-----" + filteredFields);
//
//        for (ITree testfunc : testfuncslist) {
//            String funcnamenode = SrcmlUnityCsMetaDataGenerator.getFunctionName(testfunc);
//            String classtestfunc = lowerclassname + Config.separatorStr + funcnamenode;
//            Double d;
//            System.out.println("filteredFields----- in for loop" + filteredFields);
//            if (!filteredFields.isEmpty()) {
//                System.out.println("entered here...");
//                d = fields_in_test_func_matcher(testfunc, filteredFields);
//            } else {
//                d = 0.0;
//            }
//
//            generalFixtureMap.put(classtestfunc, d);
//        }
//
//        return generalFixtureMap;
//    }
//
//
//    public double getGeneralFixtureStats(Map<String, Double> testfuncassertmap) {
//        double percentage = 0.0;
//        int total = 0;
//        double percent_total=0.0;
//        for( Double s: testfuncassertmap.values())
//        {
//            percent_total+=s;
//        }
//        total = testfuncassertmap.keySet().size();
//
//        if (total <= 0) {
//            return -0.001;
//        } else {
//            percentage = percent_total/total;
////            System.out.println(percent_total);
//
//        }
//
//        return percentage;
//    }
//
//}

//package com.unity.testsmell;
import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;
import org.javatuples.Pair;

import java.util.*;

public class GeneralFixture {

    private Map<String, String> dependencyMap = new HashMap<>();

    private final Set<String> setupSpecificFields = new HashSet<>(Collections.singletonList("containingObject"));

    public List<Pair<String, ITree>> collect_fields(List<ITree> all_funcs, ITree func) {
        List<Pair<String, ITree>> namesList = new ArrayList<>();
        try {
            func.getChildren().forEach(child -> {
                List<ITree> decls = TreeNodeAnalyzer.getSearchTypeLabel(func, "operator", "=");
                decls.forEach(d -> {
                    ITree parent = d.getParent();
                    if (parent != null && parent.getChildren().size() > 1) {
                        ITree nameNode = parent.getChild(0);
                        ITree valueNode = parent.getChild(1);

                        if ("name".equals(nameNode.getType().toString())) {
                            namesList.add(new Pair<>("property", nameNode));
                        }

                        if (valueNode != null && "name".equals(valueNode.getType().toString())) {
                            dependencyMap.put(nameNode.getLabel(), valueNode.getLabel());
                        }
                    } else {
                        //System.err.println("Parent node is null or doesn't have enough children.");
                    }
                });
            });
        } catch (Exception e) {
            //System.err.println("Error collecting fields: " + e.getMessage());
        }
        return namesList;
    }

    public double fields_in_test_func_matcher(ITree testFunc, List<Pair<String, ITree>> initFields) {
        List<Pair<String, ITree>> unusedFields = new ArrayList<>(initFields);
        int totalFields = initFields.size();

        for (Pair<String, ITree> field : initFields) {
            String fieldName = field.getValue1().getLabel();
            if (setupSpecificFields.contains(fieldName)) {
                unusedFields.remove(field);
                continue;
            }

            boolean isMatched = sub_tree_matcher(field.getValue1(), testFunc, false) || isFieldIndirectlyUsed(field, testFunc, initFields);
            if (isMatched) {
                unusedFields.remove(field);
            }
        }

        int unusedCount = unusedFields.size();
        return totalFields > 0 ? ((double) unusedCount / totalFields) * 100 : 0;
    }

    private boolean isFieldIndirectlyUsed(Pair<String, ITree> field, ITree testFunc, List<Pair<String, ITree>> initFields) {
        String fieldName = field.getValue1().getLabel();
        if (dependencyMap.containsKey(fieldName)) {
            String dependentFieldName = dependencyMap.get(fieldName);
            for (Pair<String, ITree> initField : initFields) {
                if (initField.getValue1().getLabel().equals(dependentFieldName)) {
                    return sub_tree_matcher(initField.getValue1(), testFunc, false);
                }
            }
        }
        return false;
    }

    public boolean sub_tree_matcher(ITree property, ITree testFuncTree, boolean objectMatch) {
        if (property.getType() == testFuncTree.getType() && Objects.equals(property.getLabel(), testFuncTree.getLabel())) {
            if (property.isLeaf() && testFuncTree.isLeaf()) {
                return true;
            } else if (objectMatch) {
                return false;
            } else {
                boolean matches = true;

                // Check minimum size of children to prevent index out of bounds
                int childrenCount = Math.min(property.getChildren().size(), testFuncTree.getChildren().size());
                for (int i = 0; i < childrenCount; i++) {
                    matches &= sub_tree_matcher(property.getChild(i), testFuncTree.getChild(i), false);
                }
                return matches;
            }
        }

        return testFuncTree.getChildren().stream().anyMatch(child -> sub_tree_matcher(property, child, objectMatch));
    }


    public Map<String, Double> searchForGeneralFixture(ITree root) {
        Map<String, Double> generalFixtureMap = new HashMap<>();
        ITree classNode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if (classNode == null) {
            //System.err.println("Class node not found.");
            return generalFixtureMap;
        }

        ITree classNameNode = SrcmlUnityCsMetaDataGenerator.getClassName(classNode);
        String className = classNameNode.getLabel();

        List<ITree> setupFunctions = TreeNodeAnalyzer.getSetupFunctionsList(root);
        if (setupFunctions == null || setupFunctions.isEmpty()) {
            //System.err.println("No setup functions found.");
            return generalFixtureMap;
        }

        List<ITree> testFunctions = TreeNodeAnalyzer.getTestFunctionList(root);
        if (testFunctions == null || testFunctions.isEmpty()) {
            //System.err.println("No test functions found.");
            return generalFixtureMap;
        }

        List<Pair<String, ITree>> initializedFields = new ArrayList<>();
        for (ITree setupFunc : setupFunctions) {
            List<Pair<String, ITree>> fields = collect_fields(TreeNodeAnalyzer.getFunctionList(root), setupFunc);
            initializedFields.addAll(fields);
        }

        if (initializedFields.isEmpty()) {
            //System.err.println("No initialized fields found.");
            return generalFixtureMap;
        }

        for (ITree testFunc : testFunctions) {
            String testFuncName = SrcmlUnityCsMetaDataGenerator.getFunctionName(testFunc);
            double unusedPercentage = fields_in_test_func_matcher(testFunc, initializedFields);
            String classtestfunc = className + Config.separatorStr + testFuncName;
            generalFixtureMap.put(classtestfunc, unusedPercentage);
        }

        return generalFixtureMap;
    }

    public double getGeneralFixtureStats(Map<String, Double> testfuncassertmap) {
        double percentTotal = testfuncassertmap.values().stream().mapToDouble(Double::doubleValue).sum();
        int total = testfuncassertmap.size();
        return total > 0 ? percentTotal / total : 0.0;
    }
}
