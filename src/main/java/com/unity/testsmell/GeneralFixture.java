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

    public boolean sub_tree_matcher(ITree property, ITree testFuncTree, boolean objectMatch)
    {
        if ( property.getType() == testFuncTree.getType() && Objects.equals(property.getLabel(), testFuncTree.getLabel()))
        {
            if (property.isLeaf() && testFuncTree.isLeaf()){
                return true;
            } else if (objectMatch){
                return false;
            } else{
                if(property.getChildren().size() != testFuncTree.getChildren().size())
                {
                    return false;
                }
                boolean total=true;
                for (int counter = 0; counter<property.getChildren().size();counter++){
                   total=total && sub_tree_matcher(property.getChild(counter),testFuncTree.getChild(counter),false);
                }
                return total;
            }

        }
        boolean result=false;
        if(testFuncTree.getChildren().size() == 0){
            return false;
        } else{
            for(ITree ch : testFuncTree.getChildren()){
                result=  sub_tree_matcher(property,ch,objectMatch);
                if(result){
                    break;
                }
            }
        }
        return result;
    }

    public double fields_in_test_func_matcher(ITree testfunc, List<Pair<String, ITree>> init_fields){
        List<Pair<String, ITree>> fields_copy = new ArrayList<>();
        int fields_number=init_fields.size();
        if(fields_number==0)
        {
            return 0.0;
        }
        init_fields.forEach( p -> fields_copy.add(new Pair<>(p.getValue0(), p.getValue1())));
        init_fields.forEach(p -> {
            if(Objects.equals(p.getValue0(), "object"))
            {
                if ( sub_tree_matcher(p.getValue1(),testfunc,true))
                {
                    System.out.println("Found Object");
                    System.out.println(p);
                    fields_copy.remove(p);
                }
            }
            else if(Objects.equals(p.getValue0(), "property")){
                if (sub_tree_matcher(p.getValue1(),testfunc,false))
                {
                    System.out.println("Found Property");
                        System.out.println(p);
                        fields_copy.remove(p);
                }
                } else{
                System.out.println("Unsupported field type");
            }
        });
       int  new_number=fields_copy.size() ;
        System.out.println("Fields copy");
        System.out.println(fields_copy);
       return ( (double) new_number/fields_number)*100;
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
            {
                System.out.println("Init Fields");
                System.out.println(initialized_fields);
                d= fields_in_test_func_matcher(testfunc,initialized_fields);
                System.out.println(d);
            }
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
