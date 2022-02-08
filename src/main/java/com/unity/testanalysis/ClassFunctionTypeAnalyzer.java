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

					if (lowerclassname.startsWith("test") || lowerclassname.endsWith("test")
							|| lowerclassname.startsWith("tests") || lowerclassname.endsWith("tests")) {
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

						if (lowerfuncname.startsWith("test") || lowerfuncname.endsWith("test") || funcnode
								.getMetadata("UNITYTEST").equals(true)) {
							funccall.setTestFunction(true);
							hastest = true;
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

					if (lowerclassname.startsWith("test") || lowerclassname.endsWith("test")
							|| lowerclassname.startsWith("tests") || lowerclassname.endsWith("tests")) {
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

						if (lowerfuncname.startsWith("test") || lowerfuncname.endsWith("test") || funcnode
								.getMetadata("UNITYTEST").equals(true)) {
							funccall.setTestFunction(true);
							istestclass=true;
						}

						funccall.setFuncName(funcnameparm);

						
					}
				}
				if (classnode != null && istestclass==true) {
					lineassert= SrcmlUnityCsMetaDataGenerator.getLineCount(curtree, "locassert");
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());

			}

		}		

		return lineassert;
	}

}
