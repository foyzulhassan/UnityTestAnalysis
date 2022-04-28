package com.unity.testanalysis;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.csharp.astgenerator.SrcmlUnityCsTreeGenerator;
import com.github.gumtreediff.tree.ITree;
import com.unity.callgraph.ClassFunction;
import com.unity.callgraph.FunctionCall;
import com.unity.testanalyzer.LineCountAssertCount;

import static com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNodeList;
import static com.unity.testsmell.TreeNodeAnalyzer.breadthFirstSearchForLabel;
import static com.unity.testsmell.TreeNodeAnalyzer.getTestFunctionList;

public class ClassFunctionTypeAnalyzer {
	public ClassFunction getClassFunctionType(File cursrc) {

		ClassFunction clsfunc = null;
		boolean hastest = false;
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

//					if(classname.getLabel().equals("CoreTests"))
//						System.out.println("sdafadf");

					String lowerclassname = classname.getLabel();

//					if (lowerclassname.startsWith("test") || lowerclassname.endsWith("test")
//							|| lowerclassname.startsWith("tests") || lowerclassname.endsWith("tests")) {
//						clsfunc.setTestClass(true);
//					}

                    List<ITree> testFuncList= getTestFunctionList(classnode);
                    if (testFuncList.size()>0){
                        clsfunc.setTestClass(true);
                    }
                    clsfunc.setClassname(classname.getLabel());
					funcnodelist = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNodeListUnityTest(classnode,
							"function", "f1");
				}

				if (classnode != null) {
					for (ITree funcnode : funcnodelist) {
						FunctionCall funccall = new FunctionCall();
						String funcname = SrcmlUnityCsMetaDataGenerator.getFunctionName(funcnode);
						int paramcnt = SrcmlUnityCsMetaDataGenerator.getFuncParamSize(funcnode);
						String classstrname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode).getLabel();
						String funcnameparm = classstrname + "_" + funcname + "_" + Integer.toString(paramcnt);

						String lowerfuncname = funcname.toLowerCase();

//						if (funcnode.getMetadata("UNITYTEST").equals(true) || funcnode.getMetadata("TEST").equals(true) || funcnode.getMetadata("UnityTest").equals(true) || funcnode.getMetadata("Test").equals(true) ) {

                        List<ITree> attributes = breadthFirstSearchForNodeList(funcnode, "attribute", "an11");

                        if (attributes != null && attributes.size() > 0) {
                            List<ITree> unitytestanotations = breadthFirstSearchForLabel(attributes.get(0), "UnityTest", "an22");
                            List<ITree> testanotations = breadthFirstSearchForLabel(attributes.get(0), "Test", "an33");
                            //System.out.println("test");

                            if (unitytestanotations != null && unitytestanotations.size() > 0) {
                                funccall.setTestFunction(true);
							    hastest = true;
                            } else if (testanotations != null && testanotations.size() > 0) {
                                funccall.setTestFunction(true);
                                hastest = true;
                            }
                        }
						funccall.setFuncName(funcnameparm);

						clsfunc.addItemToFuncList(funccall);
					}
				}
			}

			catch (Exception e) {
				System.out.println(e.getMessage());

			}

		}

		if (hastest) {
			clsfunc.setTestClass(true);
		}

		return clsfunc;
	}

	public LineCountAssertCount getClassTestLocAssert(File cursrc) {

		LineCountAssertCount lineassert=new LineCountAssertCount();
		if (cursrc != null) {
			try {
				
				Reader reader = new FileReader(cursrc.toString());
				ITree curtree = new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

				ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(curtree, "class", "c1");

				List<ITree> funcnodelist = null;
				ITree classname = null;
				boolean istestclass=false;
				if (classnode != null) {

					classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

//					if(classname.getLabel().equals("CoreTests"))
//						System.out.println("sdafadf");

					String lowerclassname = classname.getLabel().toLowerCase();

                    List<ITree> testFuncList= getTestFunctionList(classnode);
                    if (testFuncList.size()>0){
                        istestclass=true;
						}
					
					
					funcnodelist = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNodeListUnityTest(classnode,
							"function", "f1");
					
				}

				if (classnode != null) {
					for (ITree funcnode : funcnodelist) {
						FunctionCall funccall = new FunctionCall();
						String funcname = SrcmlUnityCsMetaDataGenerator.getFunctionName(funcnode);
						int paramcnt = SrcmlUnityCsMetaDataGenerator.getFuncParamSize(funcnode);
						String classstrname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode).getLabel();
						String funcnameparm = classstrname + "_" + funcname + "_" + Integer.toString(paramcnt);

						String lowerfuncname = funcname.toLowerCase();
                        List<ITree> attributes = breadthFirstSearchForNodeList(funcnode, "attribute", "an1");
                        if (attributes != null && attributes.size() > 0) {
                            List<ITree> unitytestanotations = breadthFirstSearchForLabel(attributes.get(0), "UnityTest", "an2");
                            List<ITree> testanotations = breadthFirstSearchForLabel(attributes.get(0), "Test", "an3");
                            //System.out.println("test");
                            if (unitytestanotations != null && unitytestanotations.size() > 0) {
                                funccall.setTestFunction(true);
                                istestclass=true;
                            } else if (testanotations != null && testanotations.size() > 0) {
                                funccall.setTestFunction(true);
                                istestclass=true;
                            }
                        }

						funccall.setFuncName(funcnameparm);

						
					}
				}
				if (classnode != null && istestclass) {
					lineassert= SrcmlUnityCsMetaDataGenerator.getLineCount(curtree, "locassert");
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());

			}

		}		

		return lineassert;
	}

}
