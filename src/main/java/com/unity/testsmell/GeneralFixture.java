package com.unity.testsmell;

import com.github.gumtreediff.tree.Type;
import org.javatuples.Pair;
import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;

import java.security.Key;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class GeneralFixture {


    public void getSmell(ITree root) {

    }

    public List<Pair<String, ITree>> collect_fields(List<ITree> all_funcs, ITree func) {
        try {
            ITree func_copy = func.deepCopy();
            ArrayList<Pair<String, ITree>> namesList = new ArrayList<>();
            func_copy.getChildren().forEach(
                    child -> {
                        List<ITree> list_call = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNodeList(child, "call", "call1");
                        for (ITree call : list_call) {
                            call.getChildren().forEach(
                                    child2 -> {
                                        if (child2.getType().toString().equals("name")) {
                                            AtomicBoolean constructor = new AtomicBoolean(false);
                                            call.getParent().getChildren().forEach(op -> {
                                                if (op.getLabel().equals("new"))
                                                    constructor.set(true);
                                            });
                                            if (!constructor.get()) {
                                                String func_name = child2.getLabel();
//                                        System.out.println(func_name);
                                                all_funcs.forEach(ftemp -> {
                                                    ftemp.getChildren().forEach(child3 -> {
                                                        if (child3.getType().toString().equals("name") && child3.getLabel().equals(func_name)) {
                                                            List<Pair<String, ITree>> add_list = collect_fields(all_funcs, ftemp);
                                                            namesList.addAll(add_list);
                                                        }
                                                    });

                                                });
                                            }
                                        }
                                    }
                            );

                        }

                    }


            );
//        System.out.println(func_copy.getChildren());
            List<ITree> decls = TreeNodeAnalyzer.getSearchTypeLabel(func_copy, "operator", "=");
            decls.forEach(d -> {
                ITree ch = d.getParent().getChild(0);
                if (ch.getType().toString().equals("name")) {
                    Pair<String, ITree> p = new Pair<>("property", ch);
                    namesList.add(p);
                }
            });
            decls = TreeNodeAnalyzer.getSearchTypeLabel(func_copy, "init", "");
            decls.forEach(d -> d.getParent().getChildren().forEach(ch ->
                    {
                        if (ch.getType().toString().equals("name")) {
                            Pair<String, ITree> p = new Pair<>("object", ch);
                            namesList.add(p);
                        }
                    })
            );
//        System.out.println(decls.size());
            return namesList;
        }
        catch (Exception e){
            return new ArrayList<Pair<String, ITree>>() ;
        }
    }

    public boolean sub_tree_matcher(ITree tree1, ITree tree2, boolean objectMatch)
    {
        if ( tree1.getType() == tree2.getType() && Objects.equals(tree1.getLabel(), tree2.getLabel()))
        {
            if (tree1.isLeaf() && tree2.isLeaf()){
                return true;
            } else if (objectMatch){
                return false;
            } else{
                if(tree1.getChildren().size() != tree2.getChildren().size())
                {
                    return false;
                }
                boolean total=true;
                for (int counter = 0; counter<tree1.getChildren().size();counter++){
                   total=total && sub_tree_matcher(tree1.getChild(counter),tree2.getChild(counter),false);
                }
                return total;
            }

        }
        return false;
    }

    public double fields_in_test_func_matcher(ITree testfunc, List<Pair<String, ITree>> fields){
        List<Pair<String, ITree>> fields_copy = new ArrayList<>();
        int fields_number=fields.size();
        if(fields_number==0)
        {
            return 0.0;
        }
        fields.forEach( p -> fields_copy.add(new Pair<>(p.getValue0(), p.getValue1())));
        fields.forEach(p -> {
            if(Objects.equals(p.getValue0(), "object"))
            {
                if ( sub_tree_matcher(p.getValue1(),testfunc,true))
                {
                    fields_copy.remove(p);
                }
            }
            else if(Objects.equals(p.getValue0(), "property")){
                if (sub_tree_matcher(p.getValue1(),testfunc,false))
                {
                        fields_copy.remove(p);
                }
                } else{
                System.out.println("Unsupported field type");
            }
        });
       int  new_number=fields_copy.size() ;

       return (double) (new_number/fields_number)*100;
    }


    public Map<String,Double> searchForGeneralFixture(ITree root) {

        Map<String,Double> generalFixtureMap = new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");
        if (classnode == null)
            return generalFixtureMap;
        List<ITree> setupfuncslist = TreeNodeAnalyzer.getSetupFunctionsList(root);
        List<ITree> testfuncslist = TreeNodeAnalyzer.getTestFunctionList(root);
        List<ITree> allfuncs = TreeNodeAnalyzer.getFunctionList(root);
        List<Pair<String, ITree>> initialized_fields= new ArrayList<>();
//        if (testfunclist.size() > 0) {
//            System.out.println("test funcs found");
//            System.out.println(testfunclist.size());
//        }
        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);
        String lowerclassname = classname.getLabel();

//            FileWriter myWriter = new FileWriter("./labels.txt");
        for (ITree setupfunc : setupfuncslist) {
            String funcnamenode = SrcmlUnityCsMetaDataGenerator.getFunctionName(setupfunc);
            String classtestfunc = lowerclassname + Config.separatorStr + funcnamenode;
//            System.out.println(classtestfunc);
            List<Pair<String, ITree>> fields = collect_fields(allfuncs, setupfunc);
            initialized_fields.addAll(fields);
        }

        for (ITree testfunc: testfuncslist){
            String testfunc_name=SrcmlUnityCsMetaDataGenerator.getFunctionName(testfunc.deepCopy());
            Double d;
            if(initialized_fields.size()>0)
            {d= fields_in_test_func_matcher(testfunc,initialized_fields);}
            else{
                d = 0.0;}
            generalFixtureMap.put(testfunc_name,d);
        }
//        System.out.println(generalFixtureMap);
        return generalFixtureMap;

    }





    public double getGeneralFixtureStats(Map<String, Double> testfuncassertmap) {
        double percentage = 0.0;
        int total = 0;
        double percent_total=0.0;
        for( Double s: testfuncassertmap.values())
        {
            percent_total+=s;
        }
        total = testfuncassertmap.keySet().size();

        if (total <= 0) {
            return -0.001;
        } else {
            percentage = percent_total/total;
//            System.out.println(percent_total);

        }

        return percentage;
    }

}
