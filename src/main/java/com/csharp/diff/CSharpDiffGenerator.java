package com.csharp.diff;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.build.analyzer.entity.CSharpChange;
import com.csharp.astgenerator.CSharpTreeVisitor;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.csharp.astgenerator.SrcmlUnityCsTreeGenerator;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.EditScriptGenerator;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Delete;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.actions.model.TreeDelete;
import com.github.gumtreediff.actions.model.TreeInsert;
import com.github.gumtreediff.actions.model.Update;
import com.github.gumtreediff.gen.srcml.SrcmlCsTreeGenerator;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.matchers.heuristic.gt.CompleteBottomUpMatcher;
import com.github.gumtreediff.tree.ITree;
import com.unity.callgraph.ClassFunction;
import com.unity.callgraph.FunctionCall;

public class CSharpDiffGenerator {

	public EditScript generateDiff(File cursrc, File prevsrc) {

		EditScript actions = null;
		if (cursrc != null && prevsrc != null) {
			try {

				Reader reader = new FileReader(cursrc.toString());
				ITree curtree = new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

				reader = new FileReader(prevsrc.toString());
				ITree prevtree = new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

				// System.out.println(curtree.toTreeString());
				// System.out.println("\n\n\n\\n\n*************************\n\n\n\n");
				// System.out.println(prevtree.toTreeString());

				// ClassicGumtree m=new ClassicGumtree(src,dst, new MappingStore());

				ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(curtree, "class", "c");
				ITree classname = null;
				if (classnode != null) {
					classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);
				}

				//Matcher m = new CompleteBottomUpMatcher();
				Matcher m = Matchers.getInstance().getMatcher();
				MappingStore mappings = m.match(prevtree, curtree);

				EditScriptGenerator editScriptGenerator = new SimplifiedChawatheScriptGenerator();
				actions = editScriptGenerator.computeActions(mappings);

				for (Action a : actions) {
					ITree node = a.getNode();
					ITree function = SrcmlUnityCsMetaDataGenerator.getBlock(node);
					if (function != null) {
						String funcname = SrcmlUnityCsMetaDataGenerator.getFunctionName(function);
						int paramcnt = SrcmlUnityCsMetaDataGenerator.getFuncParamSize(function);
						String funcnameparm = funcname + "_" + Integer.toString(paramcnt);
						System.out.println(funcnameparm);
						node.setMetadata("func", funcnameparm);
					} else {
						node.setMetadata("func", "");
					}

					ITree statement = SrcmlUnityCsMetaDataGenerator.getStatement(node);

					if (statement != null) {
						int stmtid = (int) statement.getMetadata("id");
						node.setMetadata("stmtid", stmtid);
					} else {
						node.setMetadata("stmtid", -1);
					}

					if (classname != null) {
						node.setMetadata("class", classname.getLabel());
					}

				}
			}

			catch (Exception e) {
				System.out.println(e.getMessage());

			}

		}

		return actions;
	}

	public EditScript newGenerateDiff(File cursrc, File prevsrc) {

		EditScript actions = null;
		if (cursrc != null && prevsrc != null) {
			try {

				Reader reader = new FileReader(cursrc.toString());
				ITree curtree = new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

				reader = new FileReader(prevsrc.toString());
				ITree prevtree = new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

				// System.out.println(curtree.toTreeString());
				// System.out.println("\n\n\n\\n\n*************************\n\n\n\n");
				// System.out.println(prevtree.toTreeString());

				// ClassicGumtree m=new ClassicGumtree(src,dst, new MappingStore());

				ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(curtree, "class", "c");
				ITree classname = null;
				if (classnode != null) {
					classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);
				}

				Matcher m = new CompleteBottomUpMatcher();
				// Matcher m = Matchers.getInstance().getMatcher();
				MappingStore mappings = m.match(prevtree, curtree);

				EditScriptGenerator editScriptGenerator = new SimplifiedChawatheScriptGenerator();
				actions = editScriptGenerator.computeActions(mappings);

				for (Action a : actions) {
					ITree node = a.getNode();
					ITree function = SrcmlUnityCsMetaDataGenerator.getBlock(node);
					if (function != null) {
						String funcname = SrcmlUnityCsMetaDataGenerator.getFunctionName(function);
						int paramcnt = SrcmlUnityCsMetaDataGenerator.getFuncParamSize(function);
						String funcnameparm = funcname + "_" + Integer.toString(paramcnt);
						System.out.println(funcnameparm);
						node.setMetadata("func", funcnameparm);
					} else {
						node.setMetadata("func", "");
					}

					ITree statement = SrcmlUnityCsMetaDataGenerator.getStatement(node);

					if (statement != null) {
						int stmtid = (int) statement.getMetadata("id");
						node.setMetadata("stmtid", stmtid);
					} else {
						node.setMetadata("stmtid", -1);
					}

					if (classname != null) {
						node.setMetadata("class", classname.getLabel());
					}

				}
			}

			catch (Exception e) {
				System.out.println(e.getMessage());

			}

		}

		return actions;
	}

	public List<CSharpChange> getCSharpChangeList(List<EditScript> actionlist, int patchid) {
		List<CSharpChange> changelist = new ArrayList<>();

		for (EditScript actions : actionlist) {
			for (Action a : actions) {
				ITree node = a.getNode();
				CSharpChange change = new CSharpChange();

				if (a instanceof Delete) {
					change.setActionStr("delete");
					change.setClassName(node.getMetadata("class").toString());
					change.setFuncName(node.getMetadata("func").toString());
					change.setLabelStr(node.getLabel().toString());
					change.setPatchId(patchid);
					change.setStmtID(node.getMetadata("stmtid").toString());
					change.setTypeStr(node.getType().name);

				} else if (a instanceof TreeDelete) {
					CSharpTreeVisitor treevisitor = new CSharpTreeVisitor();
					treevisitor.visitTree(a.getNode());
					List<String> label = treevisitor.getLabelList();
					List<String> type = treevisitor.getTypeList();
					String strlabel = listToString(label, " ");
					String strtype = listToString(type, " ");

					change.setActionStr("delete");
					change.setClassName(node.getMetadata("class").toString());
					change.setFuncName(node.getMetadata("func").toString());
					change.setLabelStr(strlabel);
					change.setPatchId(patchid);
					change.setStmtID(node.getMetadata("stmtid").toString());
					change.setTypeStr(strtype);

				} else if (a instanceof Insert) {
					change.setActionStr("insert");
					change.setClassName(node.getMetadata("class").toString());
					change.setFuncName(node.getMetadata("func").toString());
					change.setLabelStr(node.getLabel().toString());
					change.setPatchId(patchid);
					change.setStmtID(node.getMetadata("stmtid").toString());
					change.setTypeStr(node.getType().name);

				} else if (a instanceof TreeInsert) {
					CSharpTreeVisitor treevisitor = new CSharpTreeVisitor();
					treevisitor.visitTree(a.getNode());
					List<String> label = treevisitor.getLabelList();
					List<String> type = treevisitor.getTypeList();
					String strlabel = listToString(label, " ");
					String strtype = listToString(type, " ");

					change.setActionStr("insert");
					change.setClassName(node.getMetadata("class").toString());
					change.setFuncName(node.getMetadata("func").toString());
					change.setLabelStr(strlabel);
					change.setPatchId(patchid);
					change.setStmtID(node.getMetadata("stmtid").toString());
					change.setTypeStr(strtype);
				}

				else if (a instanceof Update) {
					change.setActionStr("update");
					change.setClassName(node.getMetadata("class").toString());
					change.setFuncName(node.getMetadata("func").toString());
					change.setLabelStr(node.getLabel().toString());
					change.setPatchId(patchid);
					change.setStmtID(node.getMetadata("stmtid").toString());
					change.setTypeStr(node.getType().name);

				} else if (a instanceof Move) {
					change.setActionStr("move");
					change.setClassName(node.getMetadata("class").toString());
					change.setFuncName(node.getMetadata("func").toString());
					change.setLabelStr(node.getLabel().toString());
					change.setPatchId(patchid);
					change.setStmtID(node.getMetadata("stmtid").toString());
					change.setTypeStr(node.getType().name);
				}

				changelist.add(change);
			}
		}

		return changelist;
	}

	private String listToString(List<String> strList, String sperator) {
		StringBuilder str = new StringBuilder();

		for (String s : strList) {
			str.append(s);
			str.append(sperator);
		}

		return str.toString();
	}

	public ClassFunction getClassFunction(File cursrc) {

		ClassFunction clsfunc = null;
		if (cursrc != null) {
			try {

				clsfunc = new ClassFunction();
				Reader reader = new FileReader(cursrc.toString());
				ITree curtree = new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

				ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(curtree, "class", "c1");

				List<ITree> funcnodelist = null;
				ITree classname = null;
				if (classnode != null) {
					
					classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);
					clsfunc.setClassname(classname.getLabel());
					funcnodelist = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNodeList(classnode, "function",
							"f1");
				}

				if (classnode != null) {
					for (ITree funcnode : funcnodelist) {
						FunctionCall funccall = new FunctionCall();
						String funcname = SrcmlUnityCsMetaDataGenerator.getFunctionName(funcnode);
						int paramcnt = SrcmlUnityCsMetaDataGenerator.getFuncParamSize(funcnode);
						String funcnameparm = funcname + "_" + Integer.toString(paramcnt);

						funccall.setFuncName(funcnameparm);

						List<ITree> calllist = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNodeList(funcnode,
								"call", "cl1");

						for (ITree call : calllist) {
							List<ITree> callchildren = call.getChildren();
							String strcallparam = null;

							if (callchildren.size() > 0) {
								List<ITree> callname = callchildren.get(0).getChildren();

								if (callname.size() > 0) {
									String strcallname = callname.get(callname.size() - 1).getLabel();
									strcallparam = strcallname;
									// System.out.println(strcallname);
								} else {
									String strcallname = callchildren.get(0).getLabel();
									strcallparam = strcallname;
								}

							}

							if (callchildren.size() > 1) {
								List<ITree> paramname = callchildren.get(1).getChildren();
								int size = paramname.size();
								strcallparam = strcallparam + "_" + Integer.toString(size);
							}
							//System.out.println(strcallparam);
							funccall.addItemToCallList(strcallparam);
							clsfunc.addItemToFuncList(funccall);
						}
					}
				}

			}

			catch (Exception e) {
				System.out.println(e.getMessage());

			}

		}

		return clsfunc;
	}

}
