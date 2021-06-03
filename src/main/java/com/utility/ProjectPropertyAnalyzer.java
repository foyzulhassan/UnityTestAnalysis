package com.utility;

public class ProjectPropertyAnalyzer {
	
	public static String getProjName(String gitrepo)
	{
		int lastslashindex=gitrepo.lastIndexOf('/');
		int indexofdot=gitrepo.lastIndexOf('.');
		
		String otherpart=gitrepo.substring(0,lastslashindex-1);
		
		int secondlastindex=otherpart.lastIndexOf('/');
		
		String repoowner=gitrepo.substring(secondlastindex+1,lastslashindex);		
		String projname=gitrepo.substring(lastslashindex+1, indexofdot);
		
		String repoprojname=repoowner+"@"+projname;
		
		return repoprojname;		
	}

}
