package com.unity.testsmell;

import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.build.commitanalyzer.CommitAnalyzer;
import com.config.Config;
import com.unity.testanalysis.TestAnalysisData;
import com.utility.ProjectPropertyAnalyzer;

import edu.util.fileprocess.TextFileReaderWriter;

public class SmellAnalysisMngr {

	public List<ProjectSmellEntity> analyzeAssertionRoulette() {
		String filepath = Config.gitProjList;

		List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
		// List<PerfFixData> fixdata = new ArrayList<>();
		List<ProjectSmellEntity> smellpercentage = new ArrayList<>();

		int counter = 0;
		for (String proj : projlist) {
			String projname = ProjectPropertyAnalyzer.getProjName(proj);
			TestAnalysisData analysisdata = new TestAnalysisData(projname);

			CommitAnalyzer cmtanalyzer = null;
			System.out.println(counter + "-->" + projname);
			;
			counter++;


			try {
				cmtanalyzer = new CommitAnalyzer("test", projname, proj);

				String commitid = cmtanalyzer.getHeadCommitID();
				Map<String, List<AssertCall>> projtestfuncassertmap = cmtanalyzer.getAssertRoulette(commitid);
				AssertionRoulette assertroulette = new AssertionRoulette();
				double percentage = assertroulette.getAssertRoulteStats(projtestfuncassertmap);

				ProjectSmellEntity projsmell = new ProjectSmellEntity("AssertionRoulette");
				projsmell.setProjName(projname);
				projsmell.setSmellPercentage(percentage);

				smellpercentage.add(projsmell);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return smellpercentage;

	}

    public List<ProjectSmellEntity> analyzeSensitiveEquality() {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();

        int counter = 0;
        for (String proj : projlist) {
            String projname = ProjectPropertyAnalyzer.getProjName(proj);
            TestAnalysisData analysisdata = new TestAnalysisData(projname);

            CommitAnalyzer cmtanalyzer = null;
            System.out.println(counter + "-->" + projname);

            counter++;
//            if (counter > 5)
//                return smellpercentage;

            try {
                cmtanalyzer = new CommitAnalyzer("test", projname, proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testfuncconditionalTestmap = cmtanalyzer.getSensitiveEquality(commitid);
                SensitiveEquality sensitiveEquality = new SensitiveEquality();
                double percentage = sensitiveEquality.getSensitiveEqualityStats(testfuncconditionalTestmap);

                ProjectSmellEntity projsmell = new ProjectSmellEntity("SensitiveEquality");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);



            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return smellpercentage;

    }

    public List<ProjectSmellEntity> analyzeLazyTest() {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();

        int counter = 0;
        for (String proj : projlist) {
            String projname = ProjectPropertyAnalyzer.getProjName(proj);
            TestAnalysisData analysisdata = new TestAnalysisData(projname);

            CommitAnalyzer cmtanalyzer = null;
            System.out.println(counter + "-->" + projname);

            counter++;
//            if (counter > 5)
//                return smellpercentage;

            try {
                cmtanalyzer = new CommitAnalyzer("test", projname, proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testfuncconditionalTestmap = cmtanalyzer.getLazyTest(commitid);
                LazyTest lazyTest = new LazyTest();
                double percentage = lazyTest.getLazyTestStats(testfuncconditionalTestmap);

                ProjectSmellEntity projsmell = new ProjectSmellEntity("LazyTest");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);



            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return smellpercentage;

    }

    public List<ProjectSmellEntity> analyzeEagerTest() {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();

        int counter = 0;
        for (String proj : projlist) {
            String projname = ProjectPropertyAnalyzer.getProjName(proj);
            TestAnalysisData analysisdata = new TestAnalysisData(projname);

            CommitAnalyzer cmtanalyzer = null;
            System.out.println(counter + "-->" + projname);
            counter++;
//            if(!projname.equals("iamtomhewitt@jet-dash-vr")) {
//                continue;
//            }
//            if (counter > 5)
//                return smellpercentage;

            try {
                cmtanalyzer = new CommitAnalyzer("test", projname, proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testfuncconditionalTestmap = cmtanalyzer.getEagerTest(commitid);
                EagerTest eagerTest = new EagerTest();
                double percentage = eagerTest.getEagerTestStats(testfuncconditionalTestmap);

                ProjectSmellEntity projsmell = new ProjectSmellEntity("EagerTest");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);



            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return smellpercentage;

    }

    public List<ProjectSmellEntity> analyzeMysteryGuest() {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();

        int counter = 0;
        for (String proj : projlist) {
            String projname = ProjectPropertyAnalyzer.getProjName(proj);
            TestAnalysisData analysisdata = new TestAnalysisData(projname);

            CommitAnalyzer cmtanalyzer = null;
            System.out.println(counter + "-->" + projname);

            counter++;
//            if (counter < 50)
//                continue;
//            if (counter > 55)
//                return smellpercentage;
            try {
                cmtanalyzer = new CommitAnalyzer("test", projname, proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testfuncconditionalTestmap = cmtanalyzer.getMysteryGuest(commitid);
                MysteryGuest mysteryGuest = new MysteryGuest();
                double percentage = mysteryGuest.getMysteryGuestStats(testfuncconditionalTestmap);

                ProjectSmellEntity projsmell = new ProjectSmellEntity("MysteryGuest");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);



            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return smellpercentage;

    }

    public List<ProjectSmellEntity> analyzeConditionalTest() {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();

        int counter = 0;
        for (String proj : projlist) {
            String projname = ProjectPropertyAnalyzer.getProjName(proj);
            TestAnalysisData analysisdata = new TestAnalysisData(projname);

            CommitAnalyzer cmtanalyzer = null;
            System.out.println(counter + "-->" + projname);

            counter++;
//            if (counter > 5)
//                return smellpercentage;

            try {
                cmtanalyzer = new CommitAnalyzer("test", projname, proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String, Map<String, Integer>> testfuncconditionalTestmap = cmtanalyzer.getConditionalTest(commitid);
                ConditionalTestLogic conditionalTestLogic = new ConditionalTestLogic();
//                double percentage = assertroulette.getAssertRoulteStats(projtestfuncassertmap);
                Map<String,Double> percetnage_map = conditionalTestLogic.getConditionalTestLogicStats(testfuncconditionalTestmap);
//                System.out.println("percentage_map");
//                System.out.println(percetnage_map);
                percetnage_map.forEach(
                        (k,v) -> {
                            ProjectSmellEntity projsmell = new ProjectSmellEntity("ConditionalTestLogic_"+k);
                            projsmell.setProjName(projname);
                            projsmell.setSmellPercentage(v);
                            smellpercentage.add(projsmell);
                        }

                );



            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return smellpercentage;

    }
    public  List<ProjectSmellEntity> analyzeGeneralFixture(){
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();

        int counter = 0;
        for (String proj : projlist) {
            String projname = ProjectPropertyAnalyzer.getProjName(proj);
            TestAnalysisData analysisdata = new TestAnalysisData(projname);

            CommitAnalyzer cmtanalyzer = null;
            System.out.println(counter + "-->" + projname);

            counter++;

//            if (counter > 5)
//                return smellpercentage ;
            if (projname.equals("Unity-Technologies@InputSystem"))
            {
                continue;
            }
//
//            if (projname.equals("iamtomhewitt@vr-pacman"))
//            {
//                continue;
//            }
            try {
                cmtanalyzer = new CommitAnalyzer("test", projname, proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String, Double> testgeneralfixtureTestmap = cmtanalyzer.getGeneralFixture(commitid);
                                GeneralFixture generalFixture = new GeneralFixture();
                double percentage = generalFixture.getGeneralFixtureStats(testgeneralfixtureTestmap);


//                Map<String,Double> percetnage_map = conditionalTestLogic.getConditionalTestLogicStats(testfuncconditionalTestmap);
////                System.out.println("percentage_map");
////                System.out.println(percetnage_map);
//                percetnage_map.forEach(
//                        (k,v) -> {
                            ProjectSmellEntity projsmell = new ProjectSmellEntity("GeneralTestFixture");
                            projsmell.setProjName(projname);
                            projsmell.setSmellPercentage(percentage);
                            smellpercentage.add(projsmell);
//                        }
//
//                );



            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return smellpercentage;
    }
    public List<ProjectSmellEntity> analyzeMagicNumberTest() {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();

        int counter = 0;
        for (String proj : projlist) {
            String projname = ProjectPropertyAnalyzer.getProjName(proj);
            TestAnalysisData analysisdata = new TestAnalysisData(projname);

            CommitAnalyzer cmtanalyzer = null;
            System.out.println(counter + "-->" + projname);

            counter++;

            try {
                cmtanalyzer = new CommitAnalyzer("test", projname, proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testfunccondition = cmtanalyzer.getMagicNumber(commitid);
                MagicNumberTest magicnumber = new MagicNumberTest();
                double percentage = magicnumber.getMagicNumberStats(testfunccondition);

                ProjectSmellEntity projsmell = new ProjectSmellEntity("MagicNumber");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);



            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return smellpercentage;

    }



    public static void main(String[] args) {
            String filepath = Config.gitProjList;

            List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
            // List<PerfFixData> fixdata = new ArrayList<>();

            List<Map<String, Double>> generalFixtureSmells= new ArrayList<>();
//            List<Map<String, Map<String, Integer>>> conditionalTestSmells= new ArrayList<>();
            List< Map<String,Boolean> > mysteryGuestSmells= new ArrayList<>();
            List<Map<String, Boolean>> eagerTestSmells= new ArrayList<>();
            List<Map<String, Boolean>> lazyTestSmells= new ArrayList<>();
            List<Map<String, Boolean>> sensitiveEqualitySmells= new ArrayList<>();
            List<Map<String, List<AssertCall>>> assertionRouletteSmells= new ArrayList<>();

            int counter = 0;
            for (String proj : projlist) {
                String projname = ProjectPropertyAnalyzer.getProjName(proj);
                TestAnalysisData analysisdata = new TestAnalysisData(projname);

                CommitAnalyzer cmtanalyzer = null;
                System.out.println(counter + "-->" + projname);

                counter++;
                if (projname.equals("Unity-Technologies@InputSystem"))
                {
                    continue;
                }
//
//                if(!projname.equals("iamtomhewitt@jet-dash-vr")) {
//                    continue;
//                }
//            if (counter > 5)
//                return smellpercentage ;
//                if (!(projname.equals("willychang21@MapboxARGame") || projname.equals("iamtomhewitt@vr-pacman") ))
//                {
//                    continue;
//                }
                try {
                    cmtanalyzer = new CommitAnalyzer("test", projname, proj);

                    String commitid = cmtanalyzer.getHeadCommitID();
                    Map<String, Double> tempMap = cmtanalyzer.getGeneralFixture(commitid);

                    Map<String, Double> newMap1 = new HashMap<>();
                    Iterator<Map.Entry<String, Double>> iterator1 = tempMap.entrySet().iterator();
                    while (iterator1.hasNext()) {
                        Map.Entry<String,  Double> entry = iterator1.next();
                        iterator1.remove();
                        newMap1.put(projname+'_'+entry.getKey(),entry.getValue());  // Whatever logic to compose new key/value pair.
                    }
                    tempMap.clear();
                    tempMap.putAll(newMap1);
                    generalFixtureSmells.add(tempMap);


//                    Map<String, Map<String, Integer>> tempMap_2 = cmtanalyzer.getConditionalTest(commitid);
//                    conditionalTestSmells.add(tempMap_2);
//                    Map<String,Boolean> tempMap_2 = cmtanalyzer.getMysteryGuest(commitid);
//                    Map<String, Boolean> newMap2 = new HashMap<>();
//                    Iterator<Map.Entry<String, Boolean>> iterator2 = tempMap_2.entrySet().iterator();
//                    while (iterator2.hasNext()) {
//                        Map.Entry<String, Boolean> entry = iterator2.next();
//                        iterator2.remove();
//                        newMap2.put(projname+'_'+entry.getKey(),entry.getValue());  // Whatever logic to compose new key/value pair.
//                    }
//                    tempMap_2.clear();
//                    tempMap_2.putAll(newMap2);
//                    mysteryGuestSmells.add(tempMap_2);


//                    Map<String,Boolean> tempMap_3 = cmtanalyzer.getEagerTest(commitid);
//                    Map<String, Boolean> newMap3 = new HashMap<>();
//                    Iterator<Map.Entry<String, Boolean>> iterator3 = tempMap_3.entrySet().iterator();
//                    while (iterator3.hasNext()) {
//                        Map.Entry<String, Boolean> entry = iterator3.next();
//                        iterator3.remove();
//                        newMap3.put(projname+'_'+entry.getKey(),entry.getValue());  // Whatever logic to compose new key/value pair.
//                    }
//                    tempMap_3.clear();
//                    tempMap_3.putAll(newMap3);
//                    eagerTestSmells.add(tempMap_3);
////
//                    Map<String,Boolean> tempMap_4 = cmtanalyzer.getLazyTest(commitid);
//                    Map<String, Boolean> newMap4 = new HashMap<>();
//                    Iterator<Map.Entry<String, Boolean>> iterator4 = tempMap_4.entrySet().iterator();
//                    while (iterator4.hasNext()) {
//                        Map.Entry<String, Boolean> entry = iterator4.next();
//                        iterator4.remove();
//                        newMap4.put(projname+'_'+entry.getKey(),entry.getValue());  // Whatever logic to compose new key/value pair.
//                    }
//                    tempMap_4.clear();
//                    tempMap_4.putAll(newMap4);
//                    lazyTestSmells.add(tempMap_4);
//
//
//                    Map<String, List<AssertCall>> tempMap_5 = cmtanalyzer.getAssertRoulette(commitid);
//                    Map<String, List<AssertCall>> newMap = new HashMap<>();
//                    Iterator<Map.Entry<String, List<AssertCall>>> iterator = tempMap_5.entrySet().iterator();
//                    while (iterator.hasNext()) {
//                        Map.Entry<String,  List<AssertCall>> entry = iterator.next();
//                        iterator.remove();
//                        newMap.put(projname+'_'+entry.getKey(),entry.getValue());  // Whatever logic to compose new key/value pair.
//                    }
//                    tempMap_5.clear();
//                    tempMap_5.putAll(newMap);
//
//                    assertionRouletteSmells.add(tempMap_5);
//
//
//
//                    Map<String,Boolean> tempMap_6 = cmtanalyzer.getSensitiveEquality(commitid);
//
//                    Map<String, Boolean> newMap6 = new HashMap<>();
//                    Iterator<Map.Entry<String, Boolean>> iterator6 = tempMap_6.entrySet().iterator();
//                    while (iterator6.hasNext()) {
//                        Map.Entry<String, Boolean> entry = iterator6.next();
//                        iterator6.remove();
//                        newMap6.put(projname+'_'+entry.getKey(),entry.getValue());  // Whatever logic to compose new key/value pair.
//                    }
//                    tempMap_6.clear();
//                    tempMap_6.putAll(newMap6);
//                    sensitiveEqualitySmells.add(tempMap_6);








                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        final Optional<Map<String, Double>> generalFixtureSmellsMap = generalFixtureSmells.stream().reduce((firstMap, secondMap) -> {
            return Stream.concat(firstMap.entrySet().stream(), secondMap.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (countInFirstMap, countInSecondMap) -> countInFirstMap + countInSecondMap));
        });
//
//            List<Map<String, Map<String, Integer>>> conditionalTestSmells= new ArrayList<>();
//
//        final Optional<Map<String, Boolean>> mysteryGuestSmellsMap = mysteryGuestSmells.stream().reduce((firstMap, secondMap) -> {
//            return Stream.concat(firstMap.entrySet().stream(), secondMap.entrySet().stream())
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
//                            (countInFirstMap, countInSecondMap) -> countInFirstMap && countInSecondMap));
//        });
//        final Optional<Map<String, Boolean>> eagerTestSmellsMap = eagerTestSmells.stream().reduce((firstMap, secondMap) -> {
//            return Stream.concat(firstMap.entrySet().stream(), secondMap.entrySet().stream())
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
//                            (countInFirstMap, countInSecondMap) -> countInFirstMap && countInSecondMap));
//        });
//        final Optional<Map<String, Boolean>> lazyTestSmellsMap = lazyTestSmells.stream().reduce((firstMap, secondMap) -> {
//            return Stream.concat(firstMap.entrySet().stream(), secondMap.entrySet().stream())
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
//                            (countInFirstMap, countInSecondMap) -> countInFirstMap && countInSecondMap));
//        });
//        final Optional<Map<String, Boolean>> sensitiveEqualitySmellsMap = sensitiveEqualitySmells.stream().reduce((firstMap, secondMap) -> {
//            return Stream.concat(firstMap.entrySet().stream(), secondMap.entrySet().stream())
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
//                            (countInFirstMap, countInSecondMap) -> countInFirstMap && countInSecondMap));
//        });
//        final Optional<Map<String, List<AssertCall>>> assertionRouletteSmellsMap = assertionRouletteSmells.stream().reduce((firstMap, secondMap) -> {
//            return Stream.concat(firstMap.entrySet().stream(), secondMap.entrySet().stream())
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
//                            (countInFirstMap, countInSecondMap) -> countInFirstMap ));
//        });

        Random rand = new Random();

//        List<String> keyList = new ArrayList<>(assertionRouletteSmellsMap.get().keySet());
//        List<String> tempList = new ArrayList<>();
//        for (int i = 0; i < Math.min(20,keyList.size()); i++) {
//            int randomIndex = rand.nextInt(keyList.size());
//            // the loop check on repetition of elements
//            String key =keyList.get(randomIndex);
//            while(tempList.contains(key)){
//                randomIndex = rand.nextInt(keyList.size());
//            }
//            tempList.add(keyList.get(randomIndex));
//        }
//        try{
//            FileWriter fWriter  = new FileWriter("assert_roulette_smells.csv");
//            for(String key:assertionRouletteSmellsMap.get().keySet()){
//            List<AssertCall> val = assertionRouletteSmellsMap.get().get(key);
//                for( AssertCall assertCall:val){
//                    String temp = key+','+assertCall.toString()+'\n';
//                    fWriter.write(temp);
//                }
//            }
//            fWriter.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////
//        rand = new Random();
//        keyList = new ArrayList<>(sensitiveEqualitySmellsMap.get().keySet());
//        tempList = new ArrayList<>();
//        for (int i = 0; i < Math.min(20,keyList.size()); i++) {
//            int randomIndex = rand.nextInt(keyList.size());
//            // the loop check on repetition of elements
//            String key =keyList.get(randomIndex);
//            while(tempList.contains(key)){
//                randomIndex = rand.nextInt(keyList.size());
//            }
//            tempList.add(keyList.get(randomIndex));
//        }

//        try{
//            FileWriter fWriter  = new FileWriter("sens_equality_smells.csv");
//            for(String key:sensitiveEqualitySmellsMap.get().keySet()){
//                boolean val = sensitiveEqualitySmellsMap.get().get(key);
//
//
//                    String temp = key+','+val+'\n';
//                    fWriter.write(temp);
//
//            }
//            fWriter.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }




//        rand = new Random();
//        keyList = new ArrayList<>(mysteryGuestSmellsMap.get().keySet());
//        tempList = new ArrayList<>();
//        for (int i = 0; i < Math.min(20,keyList.size()); i++) {
//            int randomIndex = rand.nextInt(keyList.size());
//             the loop check on repetition of elements
//            String key =keyList.get(randomIndex);
//            while(tempList.contains(key)){
//                randomIndex = rand.nextInt(keyList.size());
//            }
//            tempList.add(keyList.get(randomIndex));
//        }
//        try{
//            FileWriter fWriter  = new FileWriter("mystery_guest_smells.csv");
//            for(String key:mysteryGuestSmellsMap.get().keySet()){
//                boolean val = mysteryGuestSmellsMap.get().get(key);
//
//
//                String temp = key+','+val+'\n';
//                fWriter.write(temp);
//
//            }
//            fWriter.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        rand = new Random();
//        keyList = new ArrayList<>(eagerTestSmellsMap.get().keySet());
//        tempList = new ArrayList<>();
//        for (int i = 0; i < Math.min(20,keyList.size()); i++) {
//            int randomIndex = rand.nextInt(keyList.size());
//            // the loop check on repetition of elements
//            String key =keyList.get(randomIndex);
//            while(tempList.contains(key)){
//                randomIndex = rand.nextInt(keyList.size());
//            }
//            tempList.add(keyList.get(randomIndex));
//        }
//        try{
//            FileWriter fWriter  = new FileWriter("eager_test_smells.csv");
//            for(String key:eagerTestSmellsMap.get().keySet()){
//                boolean val = eagerTestSmellsMap.get().get(key);
//
//                String temp = key+','+val+'\n';
//                fWriter.write(temp);
//
//            }
//            fWriter.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        rand = new Random();
//        keyList = new ArrayList<>(lazyTestSmellsMap.get().keySet());
//        tempList = new ArrayList<>();
//        for (int i = 0; i < Math.min(20,keyList.size()); i++) {
//            int randomIndex = rand.nextInt(keyList.size());
//            // the loop check on repetition of elements
//            String key =keyList.get(randomIndex);
//            while(tempList.contains(key)){
//                randomIndex = rand.nextInt(keyList.size());
//            }
//            tempList.add(keyList.get(randomIndex));
//        }
//        try{
//            FileWriter fWriter  = new FileWriter("lazy_test_smells.csv");
//            for(String key:lazyTestSmellsMap.get().keySet()){
//                boolean val = lazyTestSmellsMap.get().get(key);
//                String temp = key+','+val+'\n';
//                fWriter.write(temp);
//
//            }
//            fWriter.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        rand = new Random();
//        keyList = new ArrayList<>(generalFixtureSmellsMap.get().keySet());
//        tempList = new ArrayList<>();
//        for (int i = 0; i < Math.min(20,keyList.size()); i++) {
//            int randomIndex = rand.nextInt(keyList.size());
//            // the loop check on repetition of elements
//            String key =keyList.get(randomIndex);
//            while(tempList.contains(key)){
//                randomIndex = rand.nextInt(keyList.size());
//            }
//            tempList.add(keyList.get(randomIndex));
//        }
        try{
            FileWriter fWriter  = new FileWriter("gen_fix_smells.csv");
            for(String key:generalFixtureSmellsMap.get().keySet()){
                Double val = generalFixtureSmellsMap.get().get(key);
//                System.out.println(val);
                String temp = key+','+val.toString()+'\n';
                fWriter.write(temp);

            }
            fWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }




    }



}
