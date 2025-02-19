package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensitiveEquality {

	private boolean toStringFound = false;

	public void getSmell(ITree root) {

	}

	public Map<String, Boolean> searchForSensitiveEquality(ITree root) {
		List<ITree> testfunclist = TreeNodeAnalyzer.getTestFunctionList(root);
		Map<ITree, String> helperfunclist = TreeNodeAnalyzer.getTestFunctionListnull(root);

		AssertCall as = new AssertCall();
		Map<String, Boolean> sensitiveEquality = new HashMap<>();
		ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

		if (classnode == null)
			return sensitiveEquality;

		ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

		String lowerclassname = classname.getLabel();


		for (ITree testfunc : testfunclist) {
			List<ITree> assertlist = TreeNodeAnalyzer.getSearchTypeLabelforduplicatedassert(testfunc, "name", "assert", "subject");
			//System.out.println("assertlist_1:" + assertlist);
			ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
			String finalGeneratedString = "";
			List<String> statementlist = new ArrayList<>();
			String classtestfunc = lowerclassname + Config.separatorStr + funcnamenode.getLabel();

			if (assertlist != null && assertlist.size() > 0) {
				for (ITree assertitem : assertlist) {
					//System.out.println("Getting children of the assertlist: " + assertitem.getParent().getChildren());
					List<ITree> res = assertitem.getParent().getChildren();
					StringBuilder initialString = new StringBuilder();
					ITree par = assertitem.getParent().getParent();
					List<ITree> children = par.getChildren();
					ITree chill = children.get(0);
					List<ITree> children1 = chill.getChildren();
					// System.out.println("Children1: " + children1);
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
					//System.out.println("Final Generated String: " + finalGeneratedString);
					statementlist.add(finalGeneratedString);
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

//					if (!checkDuplicate.isEmpty()) {
//						if (checkDuplicate.contains(finalGeneratedString)) {
//							duplicateList.add(assertitem);
//						} else {
//							checkDuplicate.add(finalGeneratedString);
//							//System.out.println("checkdupls:" + checkDuplicate);
//						}
//					} else {
//						checkDuplicate.add(finalGeneratedString);
//						// System.out.println("checkdupls:"+ checkDuplicate);
//					}
				}
			}
//			else {
//				for (Map.Entry<ITree, String> helperEntry : helperfunclist.entrySet()) {
//					ITree helperFunc = helperEntry.getKey();
//					String helperFuncName = helperEntry.getValue();
//					//System.out.println("Helper Function Name: " + helperFuncName); // Debugging
//
//					List<ITree> unknownsublist = TreeNodeAnalyzer.getSearchTypeLabel(helperFunc, "name", "assert");
////					for (ITree assertitem : unknownsublist) {
////						//System.out.println("Getting children of the assertlist: " + assertitem.getParent().getChildren());
////						List<ITree> res = assertitem.getParent().getChildren();
////						StringBuilder initialString = new StringBuilder();
////						ITree par = assertitem.getParent().getParent();
////						List<ITree> children = par.getChildren();
////						ITree chill = children.get(0);
////						List<ITree> children1 = chill.getChildren();
////						// System.out.println("Children1: " + children1);
////						for (ITree child : children1) {
////							String x = child.getLabel();
////							initialString.append(x);
////						}
////						ITree parem = children.get(1);
////						StringBuilder finalString = new StringBuilder();
////						List<ITree> parem_list = parem.getChildren();
////						for (ITree child : parem_list) {
////							ITree child_expr = child.getChildren().get(0);
////							List<ITree> child_children = child_expr.getChildren();
////							String processed_string = processTreeAndReplace(child_children);
////							finalString.append(processed_string);  // Accumulate parameter values
////							if (parem_list.indexOf(child) != parem_list.size() - 1) {
////								finalString.append(", "); // Add comma between parameters
////							}
////						}
////						String beforeString = initialString + "(";  // Generate the final assert
////						finalGeneratedString = beforeString + finalString + ")";
////						//System.out.println("Final Generated String: " + finalGeneratedString);
////						statementlist.add(finalGeneratedString);
////					}
////				}
//			}
//			//System.out.println("statement_list:" + statementlist);
			String target = "ToString";
            toStringFound = senitivequal(statementlist, target);
			sensitiveEquality.put(classtestfunc, toStringFound);

		}
		return sensitiveEquality;
	}

	private boolean senitivequal(List<String> statementlist, String target) {
		for(String x: statementlist){
			if(x.contains(target)){
				//System.out.println("found in:"+x);
				return true;
			}
		}
        return false;
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

//            List<ITree> if_stmt_list = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "ToString");
//            List<ITree> assertlist = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "assert");
//            List<String> listofassertparams = null;
//			System.out.println("listofassertparams" + listofassertparams);
//			System.out.println("label for test function" + SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc));
//			System.out.println("assertlist" + assertlist);
//            for (ITree assertitem : assertlist) {
//                ITree par = assertitem.getParent().getParent();
//                List<ITree> children = par.getChildren();
//				System.out.println("Childeren: "+ children);
//                ITree parem = children.get(1);
//                List<ITree> parem_list = parem.getChildren();
//                List<ITree> child_children = null;
//                for (ITree child : parem_list) {
//					if (child.getChildren() != null && !child.getChildren().isEmpty()) {
//						ITree child_expr = child.getChildren().get(0);
//						System.out.println("child_expr: " + child_expr);
//						if (child_expr.getChildren() != null && !child_expr.getChildren().isEmpty()) {
//							child_children = child_expr.getChildren();
//							System.out.println("child_children: " + child_children);
//						} else {
//							System.out.println("child_expr.getChildren() is null or empty.");
//						}
//					} else {
//						System.out.println("child.getChildren() is null or empty.");
//					}
//                }
//				System.out.println("child_children_1"+child_children);
//                if(child_children != null && !child_children.isEmpty()){
//					System.out.println("entered here");
//					for(ITree res: child_children) {
//                        String s = String.valueOf(res.getType());
//                        System.out.println("s:" + s);
//                        List<ITree> resultcalllist = null;
//                        if (s.equals("call") || s.equals("argument_list")) {
//                            resultcalllist = res.getChildren();
//                            System.out.println("resultcalllist: " + resultcalllist);
//                        }
//						if(resultcalllist != null && !resultcalllist.isEmpty()&&resultcalllist.size()>1){
//							for(ITree x : resultcalllist){
//								String res_1 = String.valueOf(recursivecheck(x));
//							}
//						}
//                    }
//
//				}
//            }
//            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
////		  List<AssertCall> assercalllist=new ArrayList<>();
//            String classtestfunc = lowerclassname + Config.separatorStr + funcnamenode.getLabel();
//            toStringFound = false;
//
//            if (if_stmt_list != null && if_stmt_list.size() > 0) {
//                toStringFound = true;
//            }
//
//            sensitiveEquality.put(classtestfunc, toStringFound);
//
//        }
//        return sensitiveEquality;


//	private boolean recursivecheck(ITree x) {
//		System.out.println("getting recv");
//		if(x.equals("ToString")){
//			toStringFound = true;
//			return toStringFound;
//		} else if (x.equals("argument_list") || x.equals("argument")) {
//			System.out.println("found arguments");
//			List<ITree> res_12 = x.getChildren();
//			System.out.println("argument_list_parm"+res_12);
//			for(ITree check: res_12){
//				recursivecheck(check);
//			}
//		}
//		toStringFound = false;
//		return toStringFound;
//	}


	public double getSensitiveEqualityStats(Map<String,Boolean> testfuncconditionalTestmap)
	{

        int total=testfuncconditionalTestmap.keySet().size();
        if (total == 0)
            return -0.001;

        int sensitiveEquality=0;

        for(boolean b:testfuncconditionalTestmap.values()){
            if(b)
                sensitiveEquality++;
        }

		return  (double) sensitiveEquality/total;
	}
}
