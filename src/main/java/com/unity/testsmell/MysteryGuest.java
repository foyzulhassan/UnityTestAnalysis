package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.*;

public class MysteryGuest {

	public void getSmell(ITree root)
	{

	}

    private final List<String> mysteryGuestTypes = new ArrayList<>(
            Arrays.asList(
                    "File",
                    "FileInfo",
                    "Directory",
                    "DirectoryInfo",
                    "Path",
                    "SqlConnection",
                    "RestClient",
                    "HttpWebRequest",
                    "asyncResult"
            ));


	public Map<String,Boolean> searchForMysteryGuest(ITree root)
	{
		List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
		Map<String,Boolean> mysteryGuest=new HashMap<>();
		ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

		if(classnode==null)
			return mysteryGuest;

		ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

		String lowerclassname = classname.getLabel();



        for(ITree testfunc:testfunclist)
		{
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);

            String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
            // find possible mystery guests
			List<ITree> type_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "type", "");


            List<String> mysteryGuestsList= new ArrayList<>();
			if(type_list!=null && type_list.size()>0)
			{
				for (ITree type:type_list){
                    type.getChildren().forEach( ch -> {
                        if(ch.getType().toString().equalsIgnoreCase("name"))
                        {
                            for(String mysteryType:mysteryGuestTypes){
                                if(ch.getLabel().toString().equalsIgnoreCase(mysteryType))
                                {
                                    String name = get_var_name(type);
                                    mysteryGuestsList.add(name);

                                }
                            }
                        }
                    });
                }
			}

            //remove mock objects
            List<ITree> init_list=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "init", "");
            if(init_list!=null && init_list.size()>0)
            {
                for (ITree init:init_list){
                    init.getChildren().forEach( ch -> {
                        if(ch.getType().toString().equalsIgnoreCase("call"))
                        {
                            ch.getChildren().forEach( gch -> {
                                if(gch.getType().toString().equalsIgnoreCase("name")){
                                    gch.getChildren().forEach(ggch -> {
                                        if (ggch.getType().toString().equals("name") && ggch.getLabel().toLowerCase().contains("mock") ) {
                                            String name = get_var_name(init);
                                            mysteryGuestsList.remove(name);
                                        }
                                    });
                                }

                            });

                        }
                    });
                }
            }
            if(mysteryGuestsList.size() > 0)
            {
                mysteryGuest.put(classtestfunc,true);
            } else {
                mysteryGuest.put(classtestfunc,false);
            }


		}


        return mysteryGuest;


	}

    private String get_var_name(ITree iTree) {
        ITree iTreeCopy ;

        if(iTree.getType().toString().equalsIgnoreCase("init")){
            iTreeCopy=iTree.getParent().deepCopy();
            for( ITree ch : iTreeCopy.getChildren()) {
                if (ch.getType().toString().equalsIgnoreCase("type")) {
                    return get_var_name(ch);
                }
            }
        } else if ((iTree.getType().toString().equalsIgnoreCase("type"))) {
            iTreeCopy=iTree.getParent().deepCopy();
            for( ITree ch : iTreeCopy.getChildren()) {
                if (ch.getType().toString().equalsIgnoreCase("name")) {
                    return ch.getLabel();
                }
            }
        } else {
            System.out.println("Unsupported node type");
            return "";
        }
        return "";
    }


    public double getMysteryGuestStats(Map<String,Boolean> testfuncconditionalTestmap)
	{

        int total=testfuncconditionalTestmap.keySet().size();
        if (total == 0)
            return -0.001;

        int mysteryGuest=0;

        for(boolean b:testfuncconditionalTestmap.values()){
            if(b)
                mysteryGuest++;
        }

		return  (double) mysteryGuest/total;
	}
}
