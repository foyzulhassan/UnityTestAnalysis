package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedundantPrint {

    private boolean printFound = false;

    public void getSmell(ITree root) {

    }

    public Map<String, Boolean> searchForRedundantPrint(ITree root) {
        List<ITree> testfunclist = TreeNodeAnalyzer.getTestFunctionList(root);
        Map<String, Boolean> redundantPrint = new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if (classnode == null)
            return redundantPrint;

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

        String lowerclassname = classname.getLabel();


        for (ITree testfunc : testfunclist) {
            List<ITree> Console_list = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "Console");
            System.out.println("Console_list" + Console_list);
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
            String classtestfunc = lowerclassname + Config.separatorStr + funcnamenode.getLabel();

//            if(printlist!=null && printlist.size()>0)
//            {
//                printFound = RedPrintFound(printlist);
//            }
//
//            redundantPrint.put(classtestfunc,printFound);
            boolean Console_Found = false;

            // Check if 'Thread.sleep()' is found
            if (Console_list != null && Console_list.size() > 0) {
                Console_Found = checkConsole_write(Console_list);
            }

            redundantPrint.put(classtestfunc, Console_Found);
        }
        return redundantPrint;
    }

    private boolean checkConsole_write(List<ITree> Console_list) {
        boolean isfound = false;

        for (ITree Console_Node : Console_list) {
            System.out.println("getting parents:" + Console_Node.getParents().get(2));
            ITree calls = Console_Node.getParents().get(2);
            System.out.println("calls_children:" + calls.getChildren().get(0));
            ITree calls_children = calls.getChildren().get(0);
            System.out.println("calls_children_1:" + calls_children.getChildren());
            List<Integer> res_index = getindex(calls_children, "macro");
            System.out.println("res_index:" + res_index);
            System.out.println("parent:" + calls);
            for (int i = 0; i < res_index.size(); i++) {
                System.out.println("calls:" + calls_children.getChildren().get(res_index.get(i)));
                List<ITree> siblings = Console_Node.getParent().getChildren();
                for (int j = 0; j < siblings.size(); j++) {
                    if (siblings.get(j).equals(Console_Node)) {
                        ITree sib = calls_children.getChildren().get(res_index.get(i));
                        List<ITree> sibs = sib.getChildren();
                        List<ITree> sublist = sibs.get(0).getChildren();
                        if (sublist != null && sublist.size() > 0) {
                            if (sublist.get(0).getLabel().equals(".")) {
                                List<ITree> subls = sublist.get(1).getChildren();
                                if (subls.get(0).getLabel().equals("WriteLine")) {
                                    isfound = true;
                                    break;
                                }
                            }
                        }
                        System.out.println("sibs:" + sibs.get(0).getChildren());
//                    if (i + 2 < sibs.size()) {
//                        ITree nextNode = sibs.get(i + 1);
//                        ITree sleepNode = sibs.get(i + 2);
//                        if (".".equals(nextNode.getLabel()) && "WriteLine".equals(sleepNode.getLabel())) {
//                            isfound = true;
//                            break;
//                        }
//                    }
                    }
                }
            }
        }
        return isfound;
    }

//    private boolean RedPrintFound(List<ITree>foundList) {
//        boolean isfound = false;
//
//        for (ITree print : foundList) {
//            if(print.getLabel().contains("Write") || print.getLabel().contains("WriteLine")) {
//                isfound = true;
//            }
//            else{
//                isfound = false;
//            }
//        }
//        return isfound;
//    }

        public double getRedundantPrintStats (Map < String, Boolean > testredundantprintTestmap)
        {

            int total = testredundantprintTestmap.keySet().size();
            if (total == 0)
                return -0.001;

            int redundantPrint = 0;

            for (boolean b : testredundantprintTestmap.values()) {
                if (b)
                    redundantPrint++;
            }

            return (double) redundantPrint / total;
        }


        private List<Integer> getindex (ITree callsChildren, String macro){
            int macroIndex = -1;
            List<Integer> res_index = new ArrayList<>();
            ;
            List<ITree> children = callsChildren.getChildren();
            for (int i = 0; i < children.size(); i++) {
                ITree currentNode = children.get(i);
                String x = currentNode.toString();
                if (x.startsWith(macro)) {
                    macroIndex = i;
                    res_index.add(macroIndex + 1);
                }
            }
            return res_index;
        }
    }