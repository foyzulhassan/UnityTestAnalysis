package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.unity.testanalyzer.LineCountAssertCount;

import java.util.*;

public class ConditionalTestLogic {

private int conditionCount, ifCount, switchCount, forCount, foreachCount, whileCount,doCount = 0;

	public void getSmell(ITree root)
	{

	}

	public Map<String,Map<String,Integer>> searchForConditionalTestLogic(ITree root)
	{

		List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
		Map<String,Map<String,Integer>> testfuncconditionalTestmap=new HashMap<>();

		ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

		if(classnode==null)
			return testfuncconditionalTestmap;

		ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

		String lowerclassname = classname.getLabel();


		for(ITree testfunc:testfunclist)
		{
			List<ITree> if_stmt_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "if_stmt", "");
			ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//			List<AssertCall> assercalllist=new ArrayList<>();
			String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();

			if(if_stmt_list!=null && if_stmt_list.size()>0)
			{
				ifCount++;
			}

            List<ITree> switch_stmt_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc,"switch", "");
//            System.out.println(switch_stmt_list);
            funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//			List<AssertCall> assercalllist=new ArrayList<>();
             classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();

            if(switch_stmt_list!=null && switch_stmt_list.size()>0)
            {
                switchCount++;
            }

            //condition ternary
            List<ITree> ternary_stmt_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc,"ternary","");
//            System.out.println(switch_stmt_list);
            funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//			List<AssertCall> assercalllist=new ArrayList<>();
            classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();

            if(ternary_stmt_list!=null && ternary_stmt_list.size()>0)
            {
                conditionCount++;
            }


            //dowhile
            List<ITree> do_stmt_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc,"do", "");
//            System.out.println(switch_stmt_list);
            funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//			List<AssertCall> assercalllist=new ArrayList<>();
            classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();

            if(do_stmt_list!=null && do_stmt_list.size()>0)
            {
                doCount++;
            }
            //for
            List<ITree> for_stmt_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc,"for", "");
//            System.out.println(switch_stmt_list);
            funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//			List<AssertCall> assercalllist=new ArrayList<>();
            classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();

            if(for_stmt_list!=null && for_stmt_list.size()>0)
            {
                forCount++;
            }
            //foreach
            List<ITree> for_each_stmt_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc,"foreach", "");
//            System.out.println(switch_stmt_list);
            funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//			List<AssertCall> assercalllist=new ArrayList<>();
            classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();

            if(for_each_stmt_list!=null && for_each_stmt_list.size()>0)
            {
                foreachCount++;
            }
            //while
            List<ITree> while_stmt_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc,"while", "");
//            System.out.println(switch_stmt_list);
            funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//			List<AssertCall> assercalllist=new ArrayList<>();
            classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();

            if(while_stmt_list!=null && while_stmt_list.size()>0)
            {
                whileCount++;
            }
//            if (ifCount+switchCount+doCount+conditionCount+forCount+foreachCount+whileCount >0 )
//                System.out.println(classtestfunc);
//            if(ifCount > 0) {
//                System.out.println("ifCount");
//                System.out.println(ifCount);
//            }
//            if(switchCount>0){
//                System.out.println("switchCount");
//                System.out.println(switchCount);}
//
//            if(doCount>0){
//                System.out.println("doCount");
//                System.out.println(doCount);}
//
//            if(conditionCount>0){
//                System.out.println("conditionCount");
//                System.out.println(conditionCount);}
//            if(forCount>0){
//                System.out.println("forCount");
//                System.out.println(forCount);}
//            if(foreachCount>0){
//                System.out.println("foreachCount");
//                System.out.println(foreachCount);}
//            if(whileCount>0){
//                System.out.println("whileCount");
//                System.out.println(whileCount);}

////        System.exit(0);
            Map<String,Integer> mapTemp = new HashMap<>();
            if(doCount>0)
                mapTemp.put("doCount",doCount);
            if(conditionCount>0)
            mapTemp.put("conditionCount",conditionCount);
            if(ifCount>0)
            mapTemp.put("ifCount",ifCount);
            if(switchCount>0)
            mapTemp.put("switchCount",switchCount);
            if(forCount>0)
            mapTemp.put("forCount",forCount);
            if(foreachCount>0)
            mapTemp.put("foreachCount",foreachCount);
            if(whileCount>0)
            mapTemp.put("whileCount",whileCount);
            testfuncconditionalTestmap.put(classtestfunc,mapTemp);
            doCount=0;
            conditionCount = 0;
            ifCount = 0;
            switchCount = 0;
            forCount = 0;
            foreachCount = 0;
            whileCount = 0;

		}


        return testfuncconditionalTestmap;


	}









//	public static ITree getBlock(ITree currentnode) {
//		ITree block = null;
//
//		ITree node = currentnode.getParent();
//
//		while (node != null) {
//			if (node.getType().toString() == "function") {
//				block = node;
//				break;
//			}
//
//			node = node.getParent();
//		}
//
//		return block;
//	}
//
//	public static String getFunctionName(ITree node) {
//		String label = "";
//
//		for (ITree child : node.getChildren()) {
//			if (child.getType().toString() == "name") {
//				label = child.getLabel();
//				break;
//			}
//		}
//
//		return label;
//	}
//
//	public static int getFuncParamSize(ITree node) {
//		int size = 0;
//
//		List<ITree> children = node.getChildren();
//
//		ITree param = null;
//
//		for (ITree child : children) {
//			if (child.getType().name == "parameter_list") {
//				param = child;
//				break;
//			}
//		}
//
//		if (param != null) {
//			size = param.getChildren().size();
//		}
//
//		return size;
//
//	}
//
//	public static ITree getStatement(ITree currentnode) {
//		ITree block = null;
//
//		ITree node = currentnode;
//
//		while (node != null) {
//			if (node.getType().toString().contains("stmt")) {
//				block = node;
//				break;
//			}
//
//			node = node.getParent();
//		}
//
//		return block;
//	}
//
//	public static ITree breadthFirstSearchForNode(ITree node,String type,String nodevisitedmeta) {
//
//		// Just so we handle receiving an uninitialized Node, otherwise an
//		// exception will be thrown when we try to add it to queue
//		ITree classnode = null;
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
//				classnode = currentFirst;
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
//		return classnode;
//	}
//
	public static LineCountAssertCount getLineCount(ITree node,String nodevisitedmeta) {

		// Just so we handle receiving an uninitialized Node, otherwise an
		// exception will be thrown when we try to add it to queue
		Map<Integer,Integer> mapping=new HashMap<>();
		int assertcount=0;
		LineCountAssertCount lineassert=new LineCountAssertCount();
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

			if (!currentFirst.getType().toString().equals("comment")) {
				int linepos=(int) currentFirst.getMetadata("lineno");

				if(!mapping.containsKey(linepos))
				{
					mapping.put(linepos, linepos);
				}
			}

			if(currentFirst.getType().toString().equals("call"))
			{
				if(currentFirst.getChildren().size()>0)
				{
					ITree firstchild=currentFirst.getChild(0);
					String methodname=firstchild.getLabel();
					if(methodname.toLowerCase().contains("assert"))
					{
						assertcount++;
					}
				}
			}
			else if(currentFirst.getType().toString().equals("name"))
			{
					String methodname=currentFirst.getLabel();
					//ITree parent=currentFirst.getParent();
					if(methodname.toLowerCase().contains("assert"))
					{
						assertcount++;
					}
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
		lineassert.setLineCount(mapping.size()-1);
		lineassert.setAssertCount(assertcount);
		return lineassert;
	}
//
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

	public Map<String,Double> getConditionalTestLogicStats(Map<String,Map<String,Integer>> testfuncconditionalTestmap)
	{

        Map<String,Double> results=new HashMap<>();
//		double percentage=0.0;
//		 total=0;
//		int roulecount=0;
        Map<String,Integer> totals=new HashMap<>();
		for(String key:testfuncconditionalTestmap.keySet())
		{
            Map<String,Integer> condmap=testfuncconditionalTestmap.get(key);
            condmap.forEach(
                    (key1,value1) -> {
                        if (totals.containsKey(key1))
                        totals.replace(key1,totals.get(key1)+1) ;
                        else{
                            totals.put(key1,1) ;
                        }
                    }
            );


		}
//        System.out.println("totals");
//        System.out.println(totals);

        final int total=testfuncconditionalTestmap.keySet().size();

		if(total<=0)
		{
			return results;
		}
		else
		{
            totals.forEach(
                    (key2,value2) -> results.put(key2,((double) value2 / (double) total) * 100.00)) ;
		}

		return results;
	}
}
