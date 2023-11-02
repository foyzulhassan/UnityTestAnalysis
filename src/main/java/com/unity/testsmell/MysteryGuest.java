package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.io.PrintWriter;
import java.util.*;

public class MysteryGuest {

    public void getSmell(ITree root) {

    }



    private final List<String> mysteryGuestTypes = new ArrayList<>(
            Arrays.asList(
                    // file/db/webservice types classes in c#
                    "File",
                    "FileSource",
                    "FileInfo",
                    "Directory",
                    "DirectoryInfo",
                    "Path",
                    "SqlConnection",
                    "RestClient",
                    "HttpWebRequest",
                    "asyncResult",
                    // file importers unity
                    "AssemblyDefinitionImporter",
                    "AssemblyDefinitionReferenceImporter",
                    "AudioImporter",
                    "ComputeShaderImporter",
                    "DefaultImporter",
                    "FBXImporter",
                    "IHVImageFormatImporter",
                    "LocalizationImporter",
                    "Mesh3DSImporter",
                    "NativeFormatImporter",
                    "PackageManifestImporter",
                    "PluginImporter",
                    "PrefabImporter",
                    "RayTracingShaderImporter",
                    "ShaderImporter",
                    "SketchUpImporter",
                    "SpeedTreeImporter",
                    "SubstanceImporter",
                    "TextScriptImporter",
                    "TextureImporter",
                    "TrueTypeFontImporter",
                    "VideoClipImporter",
                    "VisualEffectImporter",
                    "StyleSheetImporter",
                    "UIElementsViewImporter",
                    "AssetDatabase"
            )); // add asset and prefab =, Monobehavior.instantiate


    public Map<String, Boolean> searchForMysteryGuest(ITree root) {
        List<ITree> testfunclist = TreeNodeAnalyzer.getTestFunctionList(root);
        Map<String, Boolean> mysteryGuest = new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if (classnode == null)
            return mysteryGuest;

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

        String lowerclassname = classname.getLabel();
        List<ITree> setupfuncslist = TreeNodeAnalyzer.getSetupFunctionsList(root);
        List<ITree> classProps=SrcmlUnityCsMetaDataGenerator.getClassProperties(classnode);

        List<String> mysteryGuestsList = new ArrayList<>();

        for (ITree prop : classProps) {


            // find possible mystery guests
            List<ITree> type_list = TreeNodeAnalyzer.getSearchTypeLabel(prop, "type", "");
            if (type_list != null && type_list.size() > 0) {
                for (ITree type : type_list) {
                    type.getChildren().forEach(ch -> {
                        if (ch.getType().toString().equalsIgnoreCase("name")) {
//                            System.out.println(ch.getLabel().toString());
                            if (mysteryGuestTypes.contains(ch.getLabel().toString()) || ch.getLabel().toLowerCase().startsWith("sql")) {
                                String name = get_var_name(type);
                                mysteryGuestsList.add(name);
                            }
                        }
                    });
                }
            }
            List<ITree> init_list = TreeNodeAnalyzer.getSearchTypeLabel(prop, "init", "");
            if (init_list != null && init_list.size() > 0) {
                for (ITree init : init_list) {
                    init.getChildren().forEach(ch -> {
                        if (ch.getType().toString().equalsIgnoreCase("call")) {
                            ch.getChildren().forEach(gch -> {
                                if (gch.getType().toString().equalsIgnoreCase("name")) {
                                    gch.getChildren().forEach(ggch -> {
                                        if (ggch.getType().toString().equals("name") && ggch.getLabel().toLowerCase().contains("mock")) {
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

        }


        for (ITree setupfunc : setupfuncslist) {
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(setupfunc);

            String classtestfunc = lowerclassname + Config.separatorStr + funcnamenode.getLabel();

            // find possible mystery guests
            List<ITree> type_list = TreeNodeAnalyzer.getSearchTypeLabel(setupfunc, "type", "");
            if (type_list != null && type_list.size() > 0) {
                for (ITree type : type_list) {
                    type.getChildren().forEach(ch -> {
                        if (ch.getType().toString().equalsIgnoreCase("name")) {
                            if (mysteryGuestTypes.contains(ch.getLabel().toString()) || ch.getLabel().toLowerCase().startsWith("sql")) {
                                String name = get_var_name(type);
                                mysteryGuestsList.add(name);
                            }
                        }
                    });
                }
            }
            List<ITree> init_list = TreeNodeAnalyzer.getSearchTypeLabel(setupfunc, "init", "");
            if (init_list != null && init_list.size() > 0) {
                for (ITree init : init_list) {
                    init.getChildren().forEach(ch -> {
                        if (ch.getType().toString().equalsIgnoreCase("call")) {
                            ch.getChildren().forEach(gch -> {
                                if (gch.getType().toString().equalsIgnoreCase("name")) {
                                    gch.getChildren().forEach(ggch -> {
                                        if (ggch.getType().toString().equals("name") && ggch.getLabel().toLowerCase().contains("mock")) {
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

        }

        for (ITree testfunc : testfunclist) {
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);

            String classtestfunc = lowerclassname + Config.separatorStr + funcnamenode.getLabel();

            // find possible mystery guests
            List<ITree> type_list = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "type", "");
//            List<String> mysteryGuestsList = new ArrayList<>();
            if (type_list != null && type_list.size() > 0) {
                for (ITree type : type_list) {
                    type.getChildren().forEach(ch -> {
                        if (ch.getType().toString().equalsIgnoreCase("name")) {
                            if (mysteryGuestTypes.contains(ch.getLabel().toString())) {
                                String name = get_var_name(type);
                                mysteryGuestsList.add(name);

                            }
                        }
                    });
                }
            }

            //remove mock objects
            List<ITree> init_list = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "init", "");
            if (init_list != null && init_list.size() > 0) {
                for (ITree init : init_list) {
                    init.getChildren().forEach(ch -> {
                        if (ch.getType().toString().equalsIgnoreCase("call")) {
                            ch.getChildren().forEach(gch -> {
                                if (gch.getType().toString().equalsIgnoreCase("name")) {
                                    gch.getChildren().forEach(ggch -> {
                                        if (ggch.getType().toString().equals("name") && ggch.getLabel().toLowerCase().contains("mock")) {
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
            // find other loader instantiates : Resources.Load() ,
            List<ITree> calls_list = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "call", "");
//             funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//             classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();
            if (calls_list != null && calls_list.size() > 0) {
                for (ITree call : calls_list) {

                    call.getChildren().forEach(ch -> {
                        int found = 0;
                        if (ch.getType().toString().equalsIgnoreCase("name")) {
                            boolean opFound = false;
                            boolean resFound = false;
                            boolean loadFound = false;
//                            int i = 0;
                            for (ITree gch : ch.getChildren()) {
                                if (gch.getLabel().equals("Resources")) {

//                                    System.out.println(ch.getChildren());
                                    resFound = true;
                                }
                                if (gch.getLabel().equals(".")) {
//                                    System.out.println(ch.getChildren());
                                    opFound = true;
                                }
                                if ((gch.getLabel().equals("Load") || gch.getLabel().equals("LoadAll"))) {
//                                    System.out.println(ch.getChildren());
                                    loadFound = true;
                                }
                                if(resFound && gch.getLabel().equals("") ) {
                                    for (ITree ggch:gch.getChildren())
                                        {
                                            if(ggch.getLabel().equals("Load") || ggch.getLabel().equals("LoadAll"))
                                            {
                                                loadFound=true;
                                            }

                                                                                }
                                }


                            }
                            if (opFound&&resFound&&loadFound) {
                                mysteryGuestsList.add("ResLoad");
                            }

                        }

                    });

                }

            }
            if (mysteryGuestsList.size() > 0) {
                mysteryGuest.put(classtestfunc, true);
            } else {
                mysteryGuest.put(classtestfunc, false);
            }

        }
        return mysteryGuest;
    }

    private String get_var_name(ITree iTree) {
        ITree iTreeCopy;

        if (iTree.getType().toString().equalsIgnoreCase("init")) {
            iTreeCopy = iTree.getParent().deepCopy();
            for (ITree ch : iTreeCopy.getChildren()) {
                if (ch.getType().toString().equalsIgnoreCase("type")) {
                    return get_var_name(ch);
                }
            }
        } else if ((iTree.getType().toString().equalsIgnoreCase("type"))) {
            iTreeCopy = iTree.getParent().deepCopy();
            for (ITree ch : iTreeCopy.getChildren()) {
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


    public double getMysteryGuestStats(Map<String, Boolean> testfuncconditionalTestmap) {

        int total = testfuncconditionalTestmap.keySet().size();
        if (total == 0)
            return -0.001;

        int mysteryGuest = 0;

        for (boolean b : testfuncconditionalTestmap.values()) {
            if (b)
                mysteryGuest++;
        }

        return (double) mysteryGuest / total;

    }
}
