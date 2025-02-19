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
            //System.out.println("testfunc"+testfunc);
            List<ITree> assertlist = TreeNodeAnalyzer.getSearchTypeLabelforduplicatedassert(testfunc, "name", "assert", "subject");
            //System.out.println("assertlist_1:" + assertlist);
            List<String> checkDuplicate = new ArrayList<>();
            List<ITree> duplicateList = new ArrayList<>();
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
            String finalGeneratedString = "";
            List<String> statementlist = new ArrayList<>();
            String classtestfunc = lowerclassname + Config.separatorStr + funcnamenode.getLabel();

            boolean duplicat1 = false;
            boolean duplicat2 = false;

            if (assertlist != null && assertlist.size() > 0) {
                for (ITree assertitem : assertlist) {
                    //System.out.println("Getting children of the assertlist: " + assertitem.getParent().getChildren());
                    List<ITree> res = assertitem.getParent().getChildren();
                    StringBuilder initialString = new StringBuilder();
                    ITree par = assertitem.getParent().getParent();
                    List<ITree> children = par.getChildren();

                    if (!children.isEmpty()) {
                        ITree chill = children.get(0);
                        List<ITree> children1 = chill.getChildren();
                        //System.out.println("Children1: " + children1);
                        for (ITree child : children1) {
                            String x = child.getLabel();
                            initialString.append(x);
                        }
                    }

                    if (children.size() > 1) {
                        ITree parem = children.get(1);
                        StringBuilder finalString = new StringBuilder();
                        List<ITree> parem_list = parem.getChildren();
                        for (ITree child : parem_list) {
                            if (!child.getChildren().isEmpty()) { // Ensure child has children
                                ITree child_expr = child.getChildren().get(0);
                                List<ITree> child_children = child_expr.getChildren();
                                String processed_string = processTreeAndReplace(child_children);
                                finalString.append(processed_string); // Accumulate parameter values
                                if (parem_list.indexOf(child) != parem_list.size() - 1) {
                                    finalString.append(", "); // Add comma between parameters
                                }
                            }
                        }
                        String beforeString = initialString + "("; // Generate the final assert
                        finalGeneratedString = beforeString + finalString + ")";
                        statementlist.add(finalGeneratedString);
                    } else {
                         continue;
                    }
//                    String beforeString = initialString + "(";  // Generate the final assert
//                    finalGeneratedString = beforeString + finalString + ")";
//                    //System.out.println("Final Generated String: " + finalGeneratedString);
//                    statementlist.add(finalGeneratedString);
                    //System.out.println("statementlist:" + statementlist);

//                    for (int i = searchind; i < statementlist.size() - 1; i++) {
//                        int j = i + 1;
//                        if (statementlist.get(i).equals(statementlist.get(j))) {
//                            duplicate = true;
//                            break;
//                        } else if (statementlist.get(j).startsWith("subject")) {
//                            duplicate = false;
//                            break;
//                        }
//                    }

                    if (!checkDuplicate.isEmpty()) {
                        if (checkDuplicate.contains(finalGeneratedString)) {
                            duplicateList.add(assertitem);
                        } else {
                            checkDuplicate.add(finalGeneratedString);
                            //System.out.println("checkdupls:" + checkDuplicate);
                        }
                    } else {
                        checkDuplicate.add(finalGeneratedString);
                        // System.out.println("checkdupls:"+ checkDuplicate);
                    }
                }
            }

            int ind = 0;
            String target = "Assert";
            int searchind = searchineex(ind, target, statementlist);
            //System.out.println("searchid"+searchind);
            String target_subject = "subject";
            int searchsecondsuject = searchineex1(searchind+1, target_subject, statementlist);
            //System.out.println("searchid_!"+searchsecondsuject);
            duplicat1 = searchduplicateassert(searchind, searchsecondsuject, statementlist);
            //System.out.println("duplicate_1 is done");
            int size_of_statementlist = statementlist.size();
            if(searchsecondsuject == searchind+1){
               duplicat2 = false;
            }
            else{duplicat2 = searchduplicateassert_1(searchsecondsuject+1, size_of_statementlist, statementlist);}
            if (duplicat1 || duplicat2) {
                duplicate = true;
            }
            duplicateAssert.put(classtestfunc, duplicate);
            duplicate = false;
        }
        return duplicateAssert;
    }

    private boolean searchduplicateassert_1(int searchsecondsuject, int sizeOfStatementlist, List<String> statementlist) {
        // Validate the range to avoid IndexOutOfBoundsException
        //System.out.println("Seraching int he second block of the assert stamesnts");
        for(int i = searchsecondsuject;i<sizeOfStatementlist-1;i++){
            for(int j= i+1;j<sizeOfStatementlist;j++){
                //System.out.println("statementlist.get(i):"+ statementlist.get(i));
               // System.out.println("statementlist.get(j):"+statementlist.get(j));
                if(statementlist.get(i).equals(statementlist.get(j))){
                    return true;
                }
            }
        }
        // No duplicates found in the specified range
        return false;
    }

    private boolean searchduplicateassert(int searchind, int searchsecondsuject, List<String> statementlist) {
       // Validate the range to avoid IndexOutOfBoundsException
       for(int i = searchind;i<searchsecondsuject-1;i++){
           for(int j= i+1;j<searchsecondsuject;j++){
               //System.out.println("statementlist.get(i):"+ statementlist.get(i));
               //System.out.println("statementlist.get(j):"+statementlist.get(j));
               if(statementlist.get(i).equals(statementlist.get(j))){
                  return true;
               }
           }
        }
        // No duplicates found in the specified range
        return false;
    }


    private int searchineex(int ind, String target, List<String> statementlist) {
        for(String x: statementlist){
            if(x.startsWith(target)){
              return ind;
            }
            else{
                ind = ind+1;
            }
        }
        return ind;
    }

    private int searchineex1(int ind, String target, List<String> statementlist) {
        for(int i = ind; i<statementlist.size();i++){
            String x =statementlist.get(i);
            if(x.startsWith(target)){
                return i;
            }
        }
        return ind;
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
            if (node.getType().name.equals("")) {
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
                        finalString.append(literalValue);
                    }
                } catch (NumberFormatException e) {
                    finalString.append(literalValue);
                }
            } else {
                finalString.append(node.getLabel());
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
