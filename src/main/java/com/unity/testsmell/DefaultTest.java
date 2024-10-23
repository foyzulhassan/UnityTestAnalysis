package com.unity.testsmell;
import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.github.gumtreediff.tree.ITree;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultTest {

    private boolean toStringFound = false;

    public void getSmell(ITree root)
    {

    }

    public Map<String,Boolean> searchForDefaultTest(ITree root)
    {
        List<ITree> testfunclist=TreeNodeAnalyzer.getTestFunctionList(root);
        Map<String,Boolean> defaultTest=new HashMap<>();
        Map<String,Boolean> defaultTest_class=new HashMap<>();
        ITree classnode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(root, "class", "c1");

        if(classnode==null)
            return defaultTest;

        ITree classname = SrcmlUnityCsMetaDataGenerator.getClassName(classnode);

        String lowerclassname = classname.getLabel();
        toStringFound = false;
        if(lowerclassname.startsWith("Example"))
        {
            toStringFound = true;
        }
        defaultTest_class.put(lowerclassname,toStringFound);

//        for (ITree testfunc : testfunclist) {
//            // Search for the 'Thread' class
//            List<ITree> defaulttestlist = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "name", "Assert");
//            System.out.println("defaulttest list"+defaulttestlist);
//            List<ITree> defaulttestylist = TreeNodeAnalyzer.getSearchTypeLabel(testfunc, "specifier", "yield");
//            System.out.println("defaulttest list"+defaulttestylist);
//
//            ITree funcnamenode = SrcmlUnityCsMetaDataGenerator.getFuncName(testfunc);
//            String classtestfunc = lowerclassname + Config.separatorStr + funcnamenode.getLabel();
//            System.out.println("classtestfunc"+classtestfunc);
//
//            boolean defaulttestFound = false;
//            boolean defaultTestyealdFound = false;
//
//            // Check if 'Thread.sleep()' is found
//            if (defaulttestlist != null && defaulttestlist.size() > 0) {
//                defaulttestFound = checkdefaulttest(defaulttestlist);
//            }
//
//            if (defaulttestylist != null && defaulttestylist.size() > 0) {
//                defaultTestyealdFound = checkdefaulttest_1(defaulttestylist);
//            }
//
//
//
//
//
//            // Combine both results, if either is found, mark as true
//            defaultTest.put(classtestfunc, defaulttestFound||defaultTestyealdFound);
//        }

        return defaultTest_class;
    }

    private boolean checkdefaulttest(List<ITree> defaulttestlist) {
        boolean isfound = false;

        for (ITree defaultNode : defaulttestlist) {
            List<ITree> siblings = defaultNode.getParent().getChildren();
            for (int i = 0; i < siblings.size(); i++) {
                if (siblings.get(i).equals(defaultNode)) {
                    // Check if next sibling is '.' and the one after that is 'sleep'
                    if (i + 2 < siblings.size()) {
                        ITree nextNode = siblings.get(i + 1);
                        ITree sleepNode = siblings.get(i + 2);

                        if (".".equals(nextNode.getLabel()) && "Pass".equals(sleepNode.getLabel())) {
                            isfound = true;
                            break;
                        }
                    }
                }
            }
        }

        return isfound;
    }


    private boolean checkdefaulttest_1(List<ITree> defaulttestylist) {
        boolean isfound = false;

        for (ITree defaultNode : defaulttestylist) {
            System.out.println("getting label"+defaultNode.getLabel());
            List<ITree> siblings = defaultNode.getParent().getChildren();
            System.out.println("getting parents"+ defaultNode.getParent());
            ITree x = defaultNode.getParent();
            if(x.toString().startsWith("return")){
                System.out.println("siblings "+siblings);
                ITree res = siblings.get(1);
                List<ITree> nulllist = res.getChildren();
                String result = nulllist.get(0).getLabel();
                if(result.equals("null")){
                    isfound = true;
                    break;
                }
            }

        }

        return isfound;
    }



    public double getDefaultTestStats(Map<String,Boolean> testdefaultTestmap)
    {

        int total=testdefaultTestmap.keySet().size();
        if (total == 0)
            return -0.001;

        int defaultTest=0;

        for(boolean b:testdefaultTestmap.values()){
            if(b)
                defaultTest++;
        }

        return  (double) defaultTest/total;
    }
}