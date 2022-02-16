package com.unity.testsmell;

import org.javatuples.Pair;
import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;
import com.unity.testanalyzer.LineCountAssertCount;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class GeneralFixture {


    public void getSmell(ITree root) {

    }

    public List<Pair<String, ITree>> collect_fields(List<ITree> all_funcs, ITree func) {
        ITree func_copy = func.deepCopy();
        ArrayList<Pair<String, ITree>> namesList = new ArrayList<>();
        func_copy.getChildren().forEach(
                child -> {
                    List<ITree> list_call = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNodeList(child, "call", "call1");
                    for (ITree call : list_call) {
                        call.getChildren().forEach(
                                child2 -> {
                                    if (child2.getType().toString().equals("name")) {
                                        AtomicBoolean constructor = new AtomicBoolean(false);
                                        call.getParent().getChildren().forEach(op -> {
                                            if (op.getLabel().equals("new"))
                                                constructor.set(true);
                                        });
                                        if (!constructor.get()) {
                                            String func_name = child2.getLabel();
//                                        System.out.println(func_name);
                                            all_funcs.forEach(ftemp -> {
                                                ftemp.getChildren().forEach(child3 -> {
                                                    if (child3.getType().toString().equals("name") && child3.getLabel().equals(func_name)) {
                                                        List<Pair<String, ITree>> add_list = collect_fields(all_funcs, ftemp);
                                                        namesList.addAll(add_list);
                                                    }
                                                });

                                            });
                                        }
                                    }
                                }
                        );

                    }

                }


        );
//        System.out.println(func_copy.getChildren());
        List<ITree> decls = TreeNodeAnalyzer.getSearchTypeLabel(func_copy, "operator", "=");
        decls.forEach(d -> {
            ITree ch = d.getParent().getChild(0);
            if (ch.getType().toString().equals("name")) {
                Pair<String, ITree> p = new Pair<>("property", ch);
                namesList.add(p);
            }
        });
        decls = TreeNodeAnalyzer.getSearchTypeLabel(func_copy, "init", "");
        decls.forEach(d -> d.getParent().getChildren().forEach(ch ->
                {
                    if (ch.getType().toString().equals("name")) {
                        Pair<String, ITree> p = new Pair<>("object", ch);
                        namesList.add(p);
                    }
                })
        );
//        System.out.println(decls.size());
        return namesList;
    }

    public Map<String, List<AssertCall>> searchForGeneralFixture(ITree root) {

        Map<String, List<AssertCall>> testfuncassertmap = new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");
        if (classnode == null)
            return testfuncassertmap;
        List<ITree> setupfuncslist = TreeNodeAnalyzer.getSetupFunctionsList(root);
        List<ITree> testfuncslist = TreeNodeAnalyzer.getTestFunctionList(root);
        List<ITree> allfuncs = TreeNodeAnalyzer.getFunctionList(root);
        List<Pair<String, ITree>> initialized_fields= new ArrayList<>();
//        if (testfunclist.size() > 0) {
//            System.out.println("test funcs found");
//            System.out.println(testfunclist.size());
//        }
        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);
        String lowerclassname = classname.getLabel();

//            FileWriter myWriter = new FileWriter("./labels.txt");
        for (ITree setupfunc : setupfuncslist) {
            String funcnamenode = SrcmlUnityCsMetaDataGenerator.getFunctionName(setupfunc);
            String classtestfunc = lowerclassname + Config.separatorStr + funcnamenode;
            System.out.println(classtestfunc);
            List<Pair<String, ITree>> fields = collect_fields(allfuncs, setupfunc);
            initialized_fields.addAll(fields);
        }

        for (ITree testfunc: testfuncslist){
            //TODO get fields of func, remove any fields that are touch from list of fields initialized by setupfuncs
        }

        return testfuncassertmap;
    }







//	public static List<ITree> breadthFirstSearchForNodeList(ITree node,String type,String nodevisitedmeta) {
//
//		// Just so we handle receiving an uninitialized Node, otherwise an
//		// exception will be thrown when we try to add it to queue
//		//ITree classnode = null;
//		List<ITree> nodelist=new ArrayList<>();
//		if (node == null)
//			return null;
//
//		// Creating the queue, and adding the first node (step 1)
//		LinkedList<ITree> queue = new LinkedList<>();
//		queue.add(node);
//
//		while (!queue.isEmpty()) {
//			ITree currentFirst = queue.removeFirst();
//
//			// In some cases we might have added a particular node more than once before
//			// actually visiting that node, so we make sure to check and skip that node if
//			// we have
//			// encountered it before
//
//			if (currentFirst.getType().toString().contains(type)) {
//				currentFirst.setMetadata("JUNIT", false);
//				List<ITree> attributes=breadthFirstSearchForNodeList1(currentFirst,"attribute","an1");
//				
//				if(attributes!=null && attributes.size()>0)
//				{
//					List<ITree> anotations=breadthFirstSearchForLabel(attributes.get(0),"Test","an2");
//					//System.out.println("test");
//					
//					if(anotations!=null && anotations.size()>0)
//					{
//						currentFirst.setMetadata("JUNIT", true);
//					}
//				}
//				
//				nodelist.add(currentFirst);
//
//			}
//
//			if (currentFirst.getMetadata(nodevisitedmeta) != null)
//				continue;
//
//			// Mark the node as visited
//			currentFirst.setMetadata(nodevisitedmeta, 1);
//			// System.out.print(currentFirst.name + " ");
//
//			List<ITree> allNeighbors = currentFirst.getChildren();
//
//			// We have to check whether the list of neighbors is null before proceeding,
//			// otherwise
//			// the for-each loop will throw an exception
//			if (allNeighbors == null)
//				continue;
//
//			for (ITree neighbor : allNeighbors) {
//				// We only add unvisited neighbors
//				if (neighbor.getMetadata(nodevisitedmeta) == null) {
//					queue.add(neighbor);
//				}
//			}
//		}
//		return nodelist;
//	}
//	
//	public static List<ITree> breadthFirstSearchForNodeListUnityTest(ITree node,String type,String nodevisitedmeta) {
//
//		// Just so we handle receiving an uninitialized Node, otherwise an
//		// exception will be thrown when we try to add it to queue
//		//ITree classnode = null;
//		List<ITree> nodelist=new ArrayList<>();
//		if (node == null)
//			return null;
//
//		// Creating the queue, and adding the first node (step 1)
//		LinkedList<ITree> queue = new LinkedList<>();
//		queue.add(node);
//
//		while (!queue.isEmpty()) {
//			ITree currentFirst = queue.removeFirst();
//
//			// In some cases we might have added a particular node more than once before
//			// actually visiting that node, so we make sure to check and skip that node if
//			// we have
//			// encountered it before
//
//			if (currentFirst.getType().toString().contains(type)) {
//				currentFirst.setMetadata("UNITYTEST", false);
//				List<ITree> attributes=breadthFirstSearchForNodeList1(currentFirst,"attribute","an1");
//				
//				if(attributes!=null && attributes.size()>0)
//				{
//					List<ITree> anotations=breadthFirstSearchForLabel(attributes.get(0),"UnityTest","an3");
//					//System.out.println("test");
//					
//					if(anotations!=null && anotations.size()>0)
//					{
//						currentFirst.setMetadata("UNITYTEST", true);
//					}
//				}
//				
//				nodelist.add(currentFirst);
//
//			}
//
//			if (currentFirst.getMetadata(nodevisitedmeta) != null)
//				continue;
//
//			// Mark the node as visited
//			currentFirst.setMetadata(nodevisitedmeta, 1);
//			// System.out.print(currentFirst.name + " ");
//
//			List<ITree> allNeighbors = currentFirst.getChildren();
//
//			// We have to check whether the list of neighbors is null before proceeding,
//			// otherwise
//			// the for-each loop will throw an exception
//			if (allNeighbors == null)
//				continue;
//
//			for (ITree neighbor : allNeighbors) {
//				// We only add unvisited neighbors
//				if (neighbor.getMetadata(nodevisitedmeta) == null) {
//					queue.add(neighbor);
//				}
//			}
//		}
//		return nodelist;
//	}
//	
//	
//	public static ITree getClassName(ITree classnode)
//	{
//		ITree classnamenode=null;
//		
//		for(ITree node: classnode.getChildren())
//		{
//			if(node.getType().toString().contains("name"))
//			{
//				classnamenode=node;
//				break;
//			}
//			
//		}
//		return classnamenode;		
//	}
//	
//	public static ITree getTestAnotation(ITree classnode)
//	{
//		ITree classnamenode=null;
//		
//		for(ITree node: classnode.getChildren())
//		{
//			if(node.getLabel().equals("Test"))
//			{
//				classnamenode=node;
//				break;
//			}
//			
//		}
//		return classnamenode;		
//	}
//	
//	
//	public static List<ITree> breadthFirstSearchForNodeList1(ITree node,String type,String nodevisitedmeta) {
//
//		// Just so we handle receiving an uninitialized Node, otherwise an
//		// exception will be thrown when we try to add it to queue
//		//ITree classnode = null;
//		List<ITree> nodelist=new ArrayList<>();
//		if (node == null)
//			return null;
//
//		// Creating the queue, and adding the first node (step 1)
//		LinkedList<ITree> queue = new LinkedList<>();
//		queue.add(node);
//
//		while (!queue.isEmpty()) {
//			ITree currentFirst = queue.removeFirst();
//
//			// In some cases we might have added a particular node more than once before
//			// actually visiting that node, so we make sure to check and skip that node if
//			// we have
//			// encountered it before
//
//			if (currentFirst.getType().toString().contains(type)) {
//				nodelist.add(currentFirst);
//				
//				
//				
//				
//				
//				//classnode = currentFirst;
//			}
//
//			if (currentFirst.getMetadata(nodevisitedmeta) != null)
//				continue;
//
//			// Mark the node as visited
//			currentFirst.setMetadata(nodevisitedmeta, 1);
//			// System.out.print(currentFirst.name + " ");
//
//			List<ITree> allNeighbors = currentFirst.getChildren();
//
//			// We have to check whether the list of neighbors is null before proceeding,
//			// otherwise
//			// the for-each loop will throw an exception
//			if (allNeighbors == null)
//				continue;
//
//			for (ITree neighbor : allNeighbors) {
//				// We only add unvisited neighbors
//				if (neighbor.getMetadata(nodevisitedmeta) == null) {
//					queue.add(neighbor);
//				}
//			}
//		}
//		return nodelist;
//	}
//	
//	public static List<ITree> breadthFirstSearchForLabel(ITree node,String label,String nodevisitedmeta) {
//
//		// Just so we handle receiving an uninitialized Node, otherwise an
//		// exception will be thrown when we try to add it to queue
//		//ITree classnode = null;
//		List<ITree> nodelist=new ArrayList<>();
//		if (node == null)
//			return null;
//
//		// Creating the queue, and adding the first node (step 1)
//		LinkedList<ITree> queue = new LinkedList<>();
//		queue.add(node);
//
//		while (!queue.isEmpty()) {
//			ITree currentFirst = queue.removeFirst();
//
//			// In some cases we might have added a particular node more than once before
//			// actually visiting that node, so we make sure to check and skip that node if
//			// we have
//			// encountered it before
//
//			if (currentFirst.getLabel().equals(label)) {
//				nodelist.add(currentFirst);
//				
//				//classnode = currentFirst;
//			}
//
//			if (currentFirst.getMetadata(nodevisitedmeta) != null)
//				continue;
//
//			// Mark the node as visited
//			currentFirst.setMetadata(nodevisitedmeta, 1);
//			// System.out.print(currentFirst.name + " ");
//
//			List<ITree> allNeighbors = currentFirst.getChildren();
//
//			// We have to check whether the list of neighbors is null before proceeding,
//			// otherwise
//			// the for-each loop will throw an exception
//			if (allNeighbors == null)
//				continue;
//
//			for (ITree neighbor : allNeighbors) {
//				// We only add unvisited neighbors
//				if (neighbor.getMetadata(nodevisitedmeta) == null) {
//					queue.add(neighbor);
//				}
//			}
//		}
//		return nodelist;
//	}

    public double getAssertRoulteStats(Map<String, List<AssertCall>> testfuncassertmap) {
        double percentage = 0.0;
        int total = 0;
        int roulecount = 0;

        for (String key : testfuncassertmap.keySet()) {
            List<AssertCall> assertcall = testfuncassertmap.get(key);

            if (assertcall != null && assertcall.size() > 1) {
                int index = 1;

                while (index < assertcall.size()) {
                    if (assertcall.get(index).isHasMsg() == false) {
                        roulecount++;
                        break;
                    }
                    index++;
                }

            }
        }

        total = testfuncassertmap.keySet().size();

        if (total <= 0) {
            return -0.001;
        } else {
            percentage = (((double) roulecount / (double) total) * 100.00);
        }

        return percentage;
    }
}
