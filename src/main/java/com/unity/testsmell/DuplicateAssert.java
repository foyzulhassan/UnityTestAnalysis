package com.unity.testsmell;

import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.*;

public class DuplicateAssert {
    private boolean duplicate = false;

    public void getSmell(ITree root) {
        // Your method
    }

    public Map<String, Boolean> searchForDuplicateAssert(ITree root) {

        List<ITree> testfunclist = TreeNodeAnalyzer.getTestFunctionList(root);
        Map<String, Boolean> duplicateAssert = new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");
        if (classnode == null)
            return duplicateAssert;

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

        String lowerclassname = classname.getLabel();
        for (ITree testfunc : testfunclist) {
            List<ITree> assertlist = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "assert");
            Set<String> checkDuplicate = new HashSet<>();
            List<ITree> duplicateList = new ArrayList<>();
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
            String finalGeneratedString = "";
            String classtestfunc = lowerclassname + Config.separatorStr + funcnamenode.getLabel();

            if (assertlist != null && assertlist.size() > 0) {
                for (ITree assertitem : assertlist) {
                    System.out.println("Getting children of the assertlist: " + assertitem.getParent().getChildren());
                    List<ITree> res = assertitem.getParent().getChildren();
                    StringBuilder initialString = new StringBuilder();
                    ITree par = assertitem.getParent().getParent();
                    List<ITree> children = par.getChildren();
                    ITree chill = children.get(0);
                    List<ITree> children1 = chill.getChildren();
                    System.out.println("Children1: " + children1);
                    for (ITree child : children1) {
                        String x = child.getLabel();
                        initialString.append(x);
                    }
                    ITree parem = children.get(1);
                    StringBuilder finalString = new StringBuilder();
                    List<ITree> parem_list = parem.getChildren();
                    for (ITree child : parem_list) {
                        ITree child_expr = child.getChildren().get(0);
                        List<ITree> child_children = child_expr.getChildren();
                        String processed_string = processTreeAndReplace(child_children);
                        finalString.append(processed_string);  // Accumulate parameter values
                        if (parem_list.indexOf(child) != parem_list.size() - 1) {
                            finalString.append(", "); // Add comma between parameters
                        }
                    }
                    String beforeString = initialString + "(";  // Generate the final assert
                    finalGeneratedString = beforeString + finalString + ")";
                    System.out.println("Final Generated String: " + finalGeneratedString);
                    if (!checkDuplicate.isEmpty()) {
                        if (checkDuplicate.contains(finalGeneratedString)) {
                            duplicateList.add(assertitem);
                        }
                    } else {
                        checkDuplicate.add(finalGeneratedString);
                    }
                }
            }

            if (duplicateList.size() >= 1) {
                duplicate = true;
            }
            duplicateAssert.put(classtestfunc, duplicate);
            duplicate = false;
        }

        return duplicateAssert;
    }

    public String processTreeAndReplace(List<ITree> nodes) {
        StringBuilder finalResult = new StringBuilder();
        for (ITree node : nodes) {
            finalResult.append(findAndReplaceLiterals(node));
        }

        return finalResult.toString();
    }

    private String findAndReplaceLiterals(ITree node) {
        StringBuilder finalString = new StringBuilder();

        if (node.getChildren().isEmpty()) {
            if (node.getType().name.equals("literal")) {
                String literalValue = node.getLabel();

                try {
                    if (literalValue.matches("-?\\d+")) {
                        int value = Integer.parseInt(literalValue);
                        if (value < 0) {
                            finalString.append("lessthanzero");
                        } else if (value > 0) {
                            finalString.append("greaterthanzero");
                        } else {
                            finalString.append("zero");
                        }
                    } else if (literalValue.matches("-?\\d+\\.\\d+f")) {
                        float value = Float.parseFloat(literalValue);
                        if (value < 0) {
                            finalString.append("lessthanzero");
                        } else if (value > 0) {
                            finalString.append("greaterthanzero");
                        } else {
                            finalString.append("zero");
                        }
                    } else if (literalValue.matches("-?\\d+\\.\\d+")) {
                        double value = Double.parseDouble(literalValue);
                        if (value < 0) {
                            finalString.append("lessthanzero");
                        } else if (value > 0) {
                            finalString.append("greaterthanzero");
                        } else {
                            finalString.append("zero");
                        }
                    } else {
                        finalString.append(literalValue); // Handle non-numeric literals
                    }
                } catch (NumberFormatException e) {
                    finalString.append(literalValue);
                }
            } else {
                finalString.append(node.getLabel()); // Handle non-literal nodes
            }
        }
        for (ITree child : node.getChildren()) {
            finalString.append(findAndReplaceLiterals(child));
        }

        return finalString.toString();
    }

    public double getDuplicateAssertTestStats(Map<String, Boolean> testfunccondition) {

        int total = testfunccondition.keySet().size();
        if (total == 0)
            return -0.001;

        int duplicateAssert = 0;

        for (boolean b : testfunccondition.values()) {
            if (b)
                duplicateAssert++;
        }

        return (double) duplicateAssert / total;
    }
}
