package com.csharp.diff;

import java.io.File;

import com.csharp.patch.xml.PatchXMLReader;

public class CSharpMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		File f1 = new File("F:\\Tammi_Thesis\\Repo\\sample\\c1.cs");
//		
//		File f2 = new File("F:\\Tammi_Thesis\\Repo\\sample\\c2.cs");
//		
//		CSharpDiffGenerator diffgen=new CSharpDiffGenerator();
//		diffgen.getClassFunction(f2);
		PatchXMLReader rd=new PatchXMLReader();
		rd.getPatchClassFuncList(12);
	}

}
