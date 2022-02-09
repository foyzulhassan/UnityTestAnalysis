package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;
import com.unity.testanalyzer.LineCountAssertCount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AssertionRoulette {
	
	
	public void getSmell(ITree root)
	{
		
	}
	
	
	public Map<String,List<AssertCall>> searchForAssertionRoulette(ITree root)
	{
				
		List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
		Map<String,List<AssertCall>> testfuncassertmap=new HashMap<>();
		ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");
		if(classnode==null)
			return testfuncassertmap;
		                  
		ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);		
		
		String lowerclassname = classname.getLabel();
		for(ITree testfunc:testfunclist)
		{
			List<ITree> assertlist=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "assert");
			ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
			List<AssertCall> assercalllist=new ArrayList<>();
			String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
			if(assertlist!=null && assertlist.size()>0)
			{
				for(ITree assertitem:assertlist)
				{					
					AssertCall assertcall=TreeNodeAnalyzer.getAssertCall(assertitem);
					boolean ismsged=IsMsgedAssert(assertcall);
					if(ismsged)
					{
						assertcall.setHasMsg(true);
					}
					assercalllist.add(assertcall);
					//System.out.println("test");
				}
			}		
			testfuncassertmap.put(classtestfunc, assercalllist);
		}
		
		return testfuncassertmap;
	}
	
	private boolean IsMsgedAssert(AssertCall assertcall)
	{
		
		boolean ismsged=false;
		
		if(IsNUnitSingleParamAssert(assertcall.getAssertName()))
		{
			if(assertcall.getParamList().size()>1)
			{
				ismsged=true;
			}
		}
		else if(IsNUnitDoubleParamAssert(assertcall.getAssertName()))
		{
			if(assertcall.getParamList().size()>2)
			{
				ismsged=true;
			}
		}
		else if(IsNUnitNoParamAssert(assertcall.getAssertName()))
		{
			if(assertcall.getParamList().size()>0)
			{
				ismsged=true;
			}
		}
		
		return ismsged;
	}
	
	private boolean IsNUnitSingleParamAssert(String assertname)
	{
		List<String> assertlist=new ArrayList<>();
		
		assertlist.add("Assert.True");
		assertlist.add("Assert.False");
		assertlist.add("Assert.Null");
		assertlist.add("Assert.NotNull");
		assertlist.add("Assert.Zero");
		assertlist.add("Assert.NotZero");
		assertlist.add("Assert.IsNaN");
		assertlist.add("Assert.IsEmpty");
		assertlist.add("Assert.IsNotEmpty");
		assertlist.add("Assert.NotNull");
		assertlist.add("Assert.Positive");
		assertlist.add("Assert.Negative");		
		assertlist.add("Assert.IsInstanceOf");
		assertlist.add("Assert.IsNotInstanceOf");
		assertlist.add("Assert.IsAssignableFrom");
		assertlist.add("Assert.IsNotAssignableFrom");		
		assertlist.add("Assert.DoesNotThrow");
		assertlist.add("Assert.DoesNotThrowAsync");
		assertlist.add("Assert.Catch");
		assertlist.add("Assert.CatchAsync");		
		assertlist.add("Assert.DoesNotThrow");
		assertlist.add("Assert.DoesNotThrowAsync");
		assertlist.add("Assert.Catch");
		assertlist.add("Assert.CatchAsync");
		
		assertlist.add("CollectionAssert.AllItemsAreInstancesOfType");
		assertlist.add("CollectionAssert.AllItemsAreNotNull");
		assertlist.add("CollectionAssert.AllItemsAreUnique");
		assertlist.add("CollectionAssert.IsEmpty");
		assertlist.add("CollectionAssert.IsNotEmpty");
		assertlist.add("CollectionAssert.IsOrdered");

		if(assertlist.contains(assertname))
			return true;
		else
			return false;
		
	}
	
	private boolean IsNUnitDoubleParamAssert(String assertname)
	{
		List<String> assertlist=new ArrayList<>();
		
		assertlist.add("Assert.AreEqual");
		assertlist.add("Assert.AreNotEqual");
		assertlist.add("Assert.AreSame");
		assertlist.add("Assert.AreNotSame");
		assertlist.add("Assert.Contains");
		assertlist.add("Assert.Greater");
		assertlist.add("Assert.GreaterOrEqual");
		assertlist.add("Assert.Less");
		assertlist.add("Assert.LessOrEqual");
		assertlist.add("Assert.NotNull");
		assertlist.add("Assert.Zero");
		assertlist.add("Assert.NotZero");		
		assertlist.add("Assert.Throws");		
		assertlist.add("StringAssert");		

		assertlist.add("DirectoryAssert");
		
		assertlist.add("CollectionAssert.AreEqual");
		assertlist.add("CollectionAssert.AreEquivalent");
		assertlist.add("CollectionAssert.AreNotEqual");
		
		assertlist.add("CollectionAssert.AreNotEquivalent");
		assertlist.add("CollectionAssert.Contains");
		assertlist.add("CollectionAssert.DoesNotContain");
		
		assertlist.add("CollectionAssert.IsSubsetOf");
		assertlist.add("CollectionAssert.IsNotSubsetOf");
		assertlist.add("CollectionAssert.AreNotEqual");

		if(assertlist.contains(assertname))
			return true;
		else
			return false;
		
	}
	
	private boolean IsNUnitNoParamAssert(String assertname)
	{
		List<String> assertlist=new ArrayList<>();
		
		assertlist.add("Assert.Pass");
		assertlist.add("Assert.Fail");
		assertlist.add("Assert.Ignore");
		assertlist.add("Assert.Inconclusive");
		
		if(assertlist.contains(assertname))
			return true;
		else
			return false;
		
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
	
	public double getAssertRoulteStats(Map<String,List<AssertCall>> testfuncassertmap)
	{
		double percentage=0.0;
		int total=0;
		int roulecount=0;
		
		for(String key:testfuncassertmap.keySet())
		{
			List<AssertCall> assertcall=testfuncassertmap.get(key);
			
			if(assertcall!=null && assertcall.size()>1)
			{
				int index=1;
				
				while(index<assertcall.size())
				{
					if(assertcall.get(index).isHasMsg()==false)
					{
						roulecount++;
						break;
					}
					index++;
				}
				
			}
		}
		
		total=testfuncassertmap.keySet().size();
		
		if(total<=0)
		{
			return -0.001;
		}
		else
		{
			percentage= (((double)roulecount/(double)total)*100.00);
		}
		
		return percentage;
	}	
}
