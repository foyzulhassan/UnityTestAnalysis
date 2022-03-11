package com.unity.testsmell;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

public class TreeNodeAnalyzer {

    public static List<ITree> getFunctionList(ITree node) {

        ITree copynode = node.deepCopy();

        List<ITree> funclist = breadthFirstSearchForNodeList(copynode, "function", "func");

        return funclist;
    }

    public static List<ITree> getSetupFunctionsList(ITree node) {
        AtomicBoolean in_test_fixture = new AtomicBoolean(false);
        ITree copynode = node.deepCopy();
        ITree copynode2 = node.deepCopy();
        List<ITree> testfunclist = new ArrayList<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(copynode2, "class", "c1");
        classnode.getChildren().forEach(t1 -> {
            if (t1.getType().toString().equals("attribute")) {
                t1.getChildren().forEach(t2 -> t2.getChildren().forEach(t3 -> {
                            if (t3.getType().toString().equals("name") && t3.getLabel().equals("TestFixture"))
                            {in_test_fixture.set(true);
//                                System.out.println("Test Fixture");
                            }
                        }
                ));
            }
        });

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

        return testfunclist;
    }

    public static List<ITree> getTestFunctionList(ITree node) {

        ITree copynode = node.deepCopy();
        List<ITree> testfunclist = new ArrayList<>();
        List<ITree> funclist = breadthFirstSearchForNodeList(copynode, "function", "func");

        for (ITree func : funclist) {
            List<ITree> attributes = breadthFirstSearchForNodeList(func, "attribute", "an1");

            if (attributes != null && attributes.size() > 0) {
                List<ITree> unitytestanotations = breadthFirstSearchForLabel(attributes.get(0), "UnityTest", "an2");
                List<ITree> testanotations = breadthFirstSearchForLabel(attributes.get(0), "Test", "an3");
                //System.out.println("test");

                if (unitytestanotations != null && unitytestanotations.size() > 0) {
                    testfunclist.add(func);
                } else if (testanotations != null && testanotations.size() > 0) {
                    testfunclist.add(func);
                }
            }
        }

        return testfunclist;
    }

    public static List<ITree> getSearchTypeLabel(ITree node, String type, String label) {
        ITree copynode = node.deepCopy();
        List<ITree> nodelist = breadthFirstSearchForTypeLabel(copynode, type, label, "as1");
        return nodelist;
    }

    public static AssertCall getAssertCall(ITree assertnode) {
        AssertCall assertcall = null;

        ITree parent = assertnode;
        ITree callnode = null;
        boolean found = false;
        /*Get the Called node of Assert Statement*/
        while (parent != null) {
            if (parent.getType().toString().toLowerCase().equals("call")) {
                //found the called node. so break
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

            if (currentFirst.getType().toString().toLowerCase().equals("switch")) {
                System.out.println(currentFirst.getLabel().toLowerCase());
            }

            if (currentFirst.getLabel().toLowerCase().equals(label) && currentFirst.getType().toString().toLowerCase().equals(type)) {
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


}
