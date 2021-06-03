package com.csharp.astgenerator;

import java.util.Deque;

import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.utils.Pair;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class CSharpTreeVisitor {
	private List<String> labelList;
	private List<String> typeList;
	
	public CSharpTreeVisitor()
	{
		labelList=new ArrayList<>();
		typeList=new ArrayList<>();
	}
	
	public List<String> getLabelList() {
		return labelList;
	}

	public List<String> getTypeList() {
		return typeList;
	}
	
	public void visitTree(ITree root) {
	        Deque<Pair<ITree, Iterator<ITree>>> stack = new ArrayDeque<>();
	        stack.push(new Pair<>(root, root.getChildren().iterator()));
	        //visitor.startTree(root);
	        typeList.add(root.getType().name);
	        labelList.add(root.getLabel());
	        while (!stack.isEmpty()) {
	            Pair<ITree, Iterator<ITree>> it = stack.peek();

	            if (!it.second.hasNext()) {
	                //visitor.endTree(it.first);
	                stack.pop();
	            } else {
	                ITree child = it.second.next();
	                stack.push(new Pair<>(child, child.getChildren().iterator()));
	               // visitor.startTree(child);
	    	        typeList.add(child.getType().name);
	    	        labelList.add(child.getLabel());
	            }
	        }
	   }	

}
