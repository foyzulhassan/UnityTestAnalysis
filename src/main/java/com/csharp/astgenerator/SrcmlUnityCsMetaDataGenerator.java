package com.csharp.astgenerator;

import com.github.gumtreediff.tree.ITree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SrcmlUnityCsMetaDataGenerator {
	public static ITree getBlock(ITree currentnode) {
		ITree block = null;

		ITree node = currentnode.getParent();

		while (node != null) {
			if (node.getType().toString() == "function") {
				block = node;
				break;
			}

			node = node.getParent();
		}

		return block;
	}

	public static String getFunctionName(ITree node) {
		String label = "";

		for (ITree child : node.getChildren()) {
			if (child.getType().toString() == "name") {
				label = child.getLabel();
				break;
			}
		}

		return label;
	}

	public static int getFuncParamSize(ITree node) {
		int size = 0;

		List<ITree> children = node.getChildren();

		ITree param = null;

		for (ITree child : children) {
			if (child.getType().name == "parameter_list") {
				param = child;
				break;
			}
		}

		if (param != null) {
			size = param.getChildren().size();
		}

		return size;

	}

	public static ITree getStatement(ITree currentnode) {
		ITree block = null;

		ITree node = currentnode;

		while (node != null) {
			if (node.getType().toString().contains("stmt")) {
				block = node;
				break;
			}

			node = node.getParent();
		}

		return block;
	}

	public static ITree breadthFirstSearchForNode(ITree node,String type,String nodevisitedmeta) {

		// Just so we handle receiving an uninitialized Node, otherwise an
		// exception will be thrown when we try to add it to queue
		ITree classnode = null;
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

			if (currentFirst.getType().toString().contains(type)) {
				classnode = currentFirst;
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
		return classnode;
	}
	
	public static List<ITree> breadthFirstSearchForNodeList(ITree node,String type,String nodevisitedmeta) {

		// Just so we handle receiving an uninitialized Node, otherwise an
		// exception will be thrown when we try to add it to queue
		//ITree classnode = null;
		List<ITree> nodelist=new ArrayList<>();
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

			if (currentFirst.getType().toString().contains(type)) {
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
	
	
	public static ITree getClassName(ITree classnode)
	{
		ITree classnamenode=null;
		
		for(ITree node: classnode.getChildren())
		{
			if(node.getType().toString().contains("name"))
			{
				classnamenode=node;
				break;
			}
			
		}
		return classnamenode;		
	}

}
