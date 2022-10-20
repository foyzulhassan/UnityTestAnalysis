package com.unity.testsmell;


import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class MagicNumberTest {

   private boolean numberFound = false;

    public void getSmell(ITree root)
    {

    }

    public Map<String,Boolean> searchForMagicNumber(ITree root)
    {
        List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
        Map<String,List<AssertCall>> testfuncassertmap=new HashMap<>();
        Map<String,Boolean> magicNumber=new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if(classnode==null)
            return magicNumber;

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

        String lowerclassname = classname.getLabel();


        for(ITree testfunc:testfunclist)
        {
            List<ITree> magiclist=TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "assert");
            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//			List<AssertCall> assercalllist=new ArrayList<>();
            String classtestfunc=lowerclassname+Config.separatorStr+funcnamenode.getLabel();

            if(magiclist!=null && magiclist.size()>0)
            {

                numberFound = IsNumFound(magiclist);

            }

            //sensitiveEquality.put(classtestfunc,toStringFound);
            magicNumber.put(classtestfunc, numberFound);

            //toStringFound = false;



        }


        return magicNumber;


    }

    private boolean IsNumFound(List<ITree>foundList) {
        boolean isfound = false;
        try {
            for (ITree num : foundList) {
                AssertCall assertCall = TreeNodeAnalyzer.getAssertCall(num);
                //double v = Double.parseDouble(num.toTreeString());
                //System.out.println(v);
                // double d = Double.parseDouble(assertCall.getParamList().);
                if (assertCall.getParamList() != null) {
                    for (int i = 0; i < assertCall.getParamList().size(); i++) {
                       // System.out.println(assertCall.getParamList().get(0));
                        //String str = assertCall.getParamList().get(i);
                        for(ITree iTree: assertCall.getParamListTrees()){
                            for(ITree tc : iTree.getChildren()){
                                System.out.println(tc.getLabel());
                                try{
                                    Double.parseDouble(tc.getLabel());
                                    isfound = true;
                                   // System.out.println("Parsed");
                                    break;
                                }catch (NumberFormatException ec){
                                   // System.out.println("Couldn't Parse");
                                }
                                for(ITree tcc : tc.getChildren()){
                                    System.out.println(tcc.getLabel());
                                    try{
                                        Double.parseDouble(tcc.getLabel());
                                        isfound = true;
                                        //System.out.println("Parsed");
                                    }catch (NumberFormatException ep){
                                        //System.out.println("Couldn't Parse");

                                    }
                                    for(ITree tch : tcc.getChildren()){
                                        //System.out.println(tch.getLabel());
                                        try{
                                               Double.parseDouble(tch.getLabel());
                                                isfound = true;
                                           // System.out.println("Parsed");
                                            }catch (NumberFormatException ez){
                                               // System.out.println("Couldn't Parse");
                                           }
                                    }
                                    break;
                                }
                                break;
                            }
                            break;
                        }

           //             assertCall.getParamListTrees().forEach(t ->{
//                                t.getChildren().forEach(tc->{
//                                    tc.getChildren().forEach(tcc->{
//                                        tcc.getChildren().forEach(tch->
//                                        {
//                                           // System.out.println(tch.getLabel());
//                                             ;
//                                             if(Double.parseDouble(tch.getLabel())>=0.0){
//                                                 return
//                                             }
//                                             try{
//                                                 Double.parseDouble(tch.getLabel());
//                                                 isfound = true;
//                                             }catch (NumberFormatException){
//
//                                             }
//
//                                             //d = Double.parseDouble(tch.toString());
////                                            if(d>=0.0) {
////                                                ok++;
////                                            }
//
//                                        });
//
//                                    });
//                                });
//                        });
                       // StringBuilder sb = new StringBuilder();
//                        for (char c : chars) {
//                            if (Character.isDigit(c)) {
//                                isfound = true;
//                            }
//                        }
//                        if ( d>= 0.0){
//                            isfound =true;
//                        }
                    }
                }
            }
        } catch (NumberFormatException er) {
            //System.out.println("Number error");
            isfound = false;
        }
        return isfound;
    }



    public double getMagicNumberStats(Map<String,Boolean> testfunccondition)
    {

        int total=testfunccondition.keySet().size();
        if (total == 0)
            return -0.001;

        int magicnumber=0;

        for(boolean b:testfunccondition.values()){
            if(b)
                magicnumber++;
        }

        return  (double) magicnumber/total;
    }




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
