package com.unity.testsmell;

import java.io.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.build.commitanalyzer.CommitAnalyzer;
import com.config.Config;
import com.unity.testanalysis.TestAnalysisData;
import com.utility.ProjectPropertyAnalyzer;

import edu.util.fileprocess.ApacheCSVReaderWriter;
import edu.util.fileprocess.TextFileReaderWriter;

import static com.config.Config.rootDir;

public class SmellAnalysisMngr {

    public List<ProjectSmellEntity> analyzeAssertionRoulette() throws IOException {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();
        List<String[]> csvData = new ArrayList<>();

        //System.out.println("Assert Roulette ProjectList ==> " + projlist.size()); 312

        int counter = 0;
        for (String proj : projlist) {
            System.out.println("project: "+proj);
            String projname = ProjectPropertyAnalyzer.getProjName(proj);
            System.out.println("ProjectName ==> " + projname);
            TestAnalysisData analysisdata = new TestAnalysisData(projname);

            CommitAnalyzer cmtanalyzer = null;
            System.out.println("ProjectName==>" + counter + "-->" + projname);

            counter++;


            try {
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String, List<AssertCall>> projtestfuncassertmap = cmtanalyzer.getAssertRoulette(commitid);
                Set<String> allKeys = projtestfuncassertmap.keySet();
                ProjectSmellEntity projsmell = null;

                AssertionRoulette assertroulette = new AssertionRoulette();
                double percentage = assertroulette.getAssertRoulteStats(projtestfuncassertmap);

                projsmell = new ProjectSmellEntity("AssertionRoulette");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);

                System.out.println("Assert Roulette ProjectSmellName ==> " + projsmell);
                smellpercentage.add(projsmell);

                for (String key : allKeys) {
                    List<AssertCall> assertCalls = projtestfuncassertmap.get(key);
                    // Only add keys where the associated list has more than 1 element
                    if (assertCalls != null && assertCalls.size() > 1) {
                        csvData.add(new String[]{projsmell.getProjName(), key, "AssertionRoulette"});
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("Assert Roulette Exception ==> " + e.getMessage());
                e.printStackTrace();
            }
            String csvFilePath = rootDir+"Assertion_Roulette_Smells.csv";
            createFileIfNotExists(csvFilePath);
            System.out.println("CsvPath====>>>" + csvFilePath);
            appendDataToCSV(csvData, csvFilePath);
        }
        String inputFilePath = rootDir+"Assertion_Roulette_Smells.csv";
        String outputFilePath = rootDir+"Final_Assertion_Roulette_Smells.csv";
        String smelltype = "Assertion_Roulette_Smells";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);
        return smellpercentage;

    }

    public static Map<String,String> getProjectStructureName(String projname){
        String owner = projname.split("@")[0];
        projname = projname.split("@")[1];
        return Map.of(owner, projname);
    }

    private void createFileIfNotExists(String filePath) {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                // Create directories if they don't exist
                file.getParentFile().mkdirs();
                // Create the file
                file.createNewFile();
                System.out.println("CSV file created: " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void appendDataToCSV(List<String[]> data, String fileName) {
        boolean fileExists = Files.exists(Paths.get(fileName));

        try (FileWriter out = new FileWriter(fileName, true); // 'true' for append mode
             CSVPrinter printer = new CSVPrinter(out,
                     fileExists ? CSVFormat.DEFAULT : CSVFormat.DEFAULT.withHeader("Project Name", "Test Function", "Smell Type"))) {

            for (String[] row : data) {
                printer.printRecord((Object[]) row);  // Write each row to the CSV file
            }

            System.out.println("Data appended successfully to CSV file: " + fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public List<ProjectSmellEntity> analyzeAssertionRoulette() {
//        String filepath = Config.gitProjList;
//
//        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
//        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();
//
//        int counter = 0;
//
//        // Open the PrintWriter outside the loop
//        try (PrintWriter writer = new PrintWriter("AssertRoulette.csv", "UTF-8")) {
//
//            for (String proj : projlist) {
//                String projname = ProjectPropertyAnalyzer.getProjName(proj);
//                TestAnalysisData analysisdata = new TestAnalysisData(projname);
//                CommitAnalyzer cmtanalyzer = null;
//
//                System.out.println(counter + "-->" + projname);
//                counter++;
//
//                try {
//                    Map <String,String> ownerProject = getProjectStructureName(projname);
//                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);
//
//                    String commitid = cmtanalyzer.getHeadCommitID();
//                    Map<String, List<AssertCall>> projtestfuncassertmap = cmtanalyzer.getAssertRoulette(commitid);
//                    AssertionRoulette assertroulette = new AssertionRoulette();
//
//                    // Pass writer to the getAssertRoulteStats method
//                    double percentage = assertroulette.getAssertRoulteStats(projtestfuncassertmap, writer, projname);
//
//                    ProjectSmellEntity projsmell = new ProjectSmellEntity("AssertionRoulette");
//                    projsmell.setProjName(projname);
//                    projsmell.setSmellPercentage(percentage);
//
//                    smellpercentage.add(projsmell);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return smellpercentage;
//    }


    public List<ProjectSmellEntity> analyzeSensitiveEquality() throws IOException {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();
        List<String[]> csvData = new ArrayList<>();

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

                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testfuncconditionalTestmap = cmtanalyzer.getSensitiveEquality(commitid);
                SensitiveEquality sensitiveEquality = new SensitiveEquality();
                Set<String> allKeys = testfuncconditionalTestmap.keySet();

                double percentage = sensitiveEquality.getSensitiveEqualityStats(testfuncconditionalTestmap);

                ProjectSmellEntity projsmell = new ProjectSmellEntity("SensitiveEquality");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);

                for (String key : allKeys) {
                    if (testfuncconditionalTestmap.get(key)) {
                        // Add project name, key, and smell type to the csvData list only if value is true
                        csvData.add(new String[]{projsmell.getProjName(), key, "SensitiveEquality"});
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String csvFilePath = rootDir+"SensitiveEquality.csv";
            createFileIfNotExists(csvFilePath);
            appendDataToCSV(csvData, csvFilePath);

        }
        String inputFilePath = rootDir+"SensitiveEquality.csv";
        String outputFilePath = rootDir+"Final_SensitiveEquality.csv";
        String smelltype = "SensitiveEquality";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);
        return smellpercentage;

    }


    public List<ProjectSmellEntity> analyzeLazyTest() throws IOException {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();
        List<String[]> csvData = new ArrayList<>();

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
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testfuncconditionalTestmap = cmtanalyzer.getLazyTest(commitid);
                Set<String> allKeys = testfuncconditionalTestmap.keySet();
                LazyTest lazyTest = new LazyTest();
                double percentage = lazyTest.getLazyTestStats(testfuncconditionalTestmap);
                ProjectSmellEntity projsmell = new ProjectSmellEntity("LazyTest");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);
                for (String key : allKeys) {
                    if (testfuncconditionalTestmap.get(key)) {
                        // Add project name, key, and smell type to the csvData list only if value is true
                        csvData.add(new String[]{projsmell.getProjName(), key, "LazyTest"});
                    }
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String csvFilePath = rootDir+"LazyTest_.csv";
            createFileIfNotExists(csvFilePath);
            appendDataToCSV(csvData, csvFilePath);

        }
        String inputFilePath = rootDir+"LazyTest_.csv";
        String outputFilePath = rootDir+"Final_LazyTest_.csv";
        String smelltype = "LazyTest";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);

        return smellpercentage;

    }

    public List<ProjectSmellEntity> analyzeEagerTest() throws IOException {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();
        List<String[]> csvData = new ArrayList<>();

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
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testfuncconditionalTestmap = cmtanalyzer.getEagerTest(commitid);
                Set<String> allKeys = testfuncconditionalTestmap.keySet();
                EagerTest eagerTest = new EagerTest();

                double percentage = eagerTest.getEagerTestStats(testfuncconditionalTestmap);
                ProjectSmellEntity projsmell = new ProjectSmellEntity("EagerTest");

                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);

                for (String key : allKeys) {
                    if (testfuncconditionalTestmap.get(key)) {
                        // Add project name, key, and smell type to the csvData list only if value is true
                        csvData.add(new String[]{projsmell.getProjName(), key, "EagerTest"});
                    }
                }



            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String csvFilePath = rootDir+"EagerTest.csv";
            createFileIfNotExists(csvFilePath);
            appendDataToCSV(csvData, csvFilePath);

        }
        String inputFilePath = rootDir+"EagerTest.csv";
        String outputFilePath = rootDir+"Final_EagerTest.csv";
        String smelltype = "EagerTest";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);
        return smellpercentage;

    }

    public List<ProjectSmellEntity> analyzeMysteryGuest() throws IOException {
        String filepath = Config.gitProjList;
        List<String[]> csvData = new ArrayList<>();
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
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testfuncconditionalTestmap = cmtanalyzer.getMysteryGuest(commitid);
                Set<String> allKeys = testfuncconditionalTestmap.keySet();
                MysteryGuest mysteryGuest = new MysteryGuest();
                double percentage = mysteryGuest.getMysteryGuestStats(testfuncconditionalTestmap);

                ProjectSmellEntity projsmell = new ProjectSmellEntity("MysteryGuest");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);

                for (String key : allKeys) {
                    if (testfuncconditionalTestmap.get(key)) {
                        // Add project name, key, and smell type to the csvData list only if value is true
                        csvData.add(new String[]{projsmell.getProjName(), key, "MysteryGuest"});
                    }

                }



            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String csvFilePath = rootDir+"MysteryGuest.csv";
            createFileIfNotExists(csvFilePath);
            appendDataToCSV(csvData, csvFilePath);

        }
        String inputFilePath = rootDir+"MysteryGuest.csv";
        String outputFilePath = rootDir+"Final_MysteryGuest.csv";
        String smelltype = "MysteryGuest";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);

        return smellpercentage;

    }

    public List<ProjectSmellEntity> analyzeConditionalTest() throws IOException {
        String filepath = Config.gitProjList;
        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        List<String[]> csvData = new ArrayList<>();
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
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String, Map<String, Integer>> testfuncconditionalTestmap = cmtanalyzer.getConditionalTest(commitid);
                System.out.println("testfuncconditionalTestmap"+testfuncconditionalTestmap);
                ConditionalTestLogic conditionalTestLogic = new ConditionalTestLogic();
//                double percentage = assertroulette.getAssertRoulteStats(projtestfuncassertmap);
                Map<String,Double> percetnage_map = conditionalTestLogic.getConditionalTestLogicStats(testfuncconditionalTestmap);
                Set<String> allKeys = testfuncconditionalTestmap.keySet();
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
                ProjectSmellEntity projsmells = new ProjectSmellEntity("ConditionalTestLogic_");
                projsmells.setProjName(projname);

                for (String key : allKeys) {
                    // Add project name, key, and smell type to the csvData list
                    csvData.add(new String[]{projsmells.getProjName(), key, "ConditionalTestLogic_"});
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String csvFilePath = rootDir+"ConditionalTestLogic_.csv";
            createFileIfNotExists(csvFilePath);
            appendDataToCSV(csvData, csvFilePath);

        }
        String inputFilePath = rootDir+"ConditionalTestLogic_.csv";
        String outputFilePath = rootDir+"Final_ConditionalTestLogic_.csv";
        String smelltype = "ConditionalTestLogic";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);

        return smellpercentage;

    }
    public  List<ProjectSmellEntity> analyzeGeneralFixture() throws IOException {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();
        List<String[]> csvData = new ArrayList<>();


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
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String, Double> testgeneralfixtureTestmap = cmtanalyzer.getGeneralFixture(commitid);
                GeneralFixture generalFixture = new GeneralFixture();
                Set<String> allKeys = testgeneralfixtureTestmap.keySet();
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

                ProjectSmellEntity projsmells = new ProjectSmellEntity("GeneralFixture");
                projsmells.setProjName(projname);
                for (String key : allKeys) {
                    Double value = testgeneralfixtureTestmap.get(key);
                    if (value > 0.00) {  // Only add keys where value is greater than 0.00
                        csvData.add(new String[]{projsmell.getProjName(), key, "GeneralFixture"});
                    }
                }



            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String csvFilePath = rootDir+"GeneralFixture.csv";
            createFileIfNotExists(csvFilePath);
            appendDataToCSV(csvData, csvFilePath);

        }
        String inputFilePath = rootDir+"GeneralFixture.csv";
        String outputFilePath = rootDir+"Final_GeneralFixture.csv";
        String smelltype = "GeneralFixture";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);
        return smellpercentage;
    }
    public List<ProjectSmellEntity> analyzeMagicNumberTest() throws IOException {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();
        List<String[]> csvData = new ArrayList<>();

        int counter = 0;
        for (String proj : projlist) {
            String projname = ProjectPropertyAnalyzer.getProjName(proj);
            TestAnalysisData analysisdata = new TestAnalysisData(projname);

            CommitAnalyzer cmtanalyzer = null;
            System.out.println(counter + "-->" + projname);

            counter++;

            try {
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testfunccondition = cmtanalyzer.getMagicNumber(commitid);
                Set<String> allKeys = testfunccondition.keySet();
                MagicNumberTest magicnumber = new MagicNumberTest();
                double percentage = magicnumber.getMagicNumberStats(testfunccondition);

                ProjectSmellEntity projsmell = new ProjectSmellEntity("MagicNumber");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);
                ProjectSmellEntity projsmells = new ProjectSmellEntity("MagicNumber");
                for (String key : allKeys) {
                    if (testfunccondition.get(key)) {
                        // Add project name, key, and smell type to the csvData list only if value is true
                        csvData.add(new String[]{projsmell.getProjName(), key, "MagicNumber"});
                    }
                }



            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String csvFilePath = rootDir+"MagicNumber.csv";
            createFileIfNotExists(csvFilePath);
            appendDataToCSV(csvData, csvFilePath);


        }
        String inputFilePath = rootDir+"MagicNumber.csv";
        String outputFilePath = rootDir+"Final_MagicNumber.csv";
        String smelltype = "MagicNumber";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);

        return smellpercentage;

    }
    public List<ProjectSmellEntity> analyzeDefaultTest() throws IOException {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();
        List<String[]> csvData = new ArrayList<>();

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
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testdefaultTestmap = cmtanalyzer.getDefaultTest(commitid);
                Set<String> allKeys = testdefaultTestmap.keySet();
                DefaultTest defaultTest = new DefaultTest();
                double percentage = defaultTest.getDefaultTestStats(testdefaultTestmap);

                ProjectSmellEntity projsmell = new ProjectSmellEntity("DefaultTest");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);

                for (String key : allKeys) {
                    if (testdefaultTestmap.get(key)) {
                        // Add project name, key, and smell type to the csvData list only if value is true
                        csvData.add(new String[]{projsmell.getProjName(), key, "DefaultTest"});
                    }

                }




            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String csvFilePath = rootDir+"DefaultTest.csv";
            createFileIfNotExists(csvFilePath);
            appendDataToCSV(csvData, csvFilePath);

        }

        String inputFilePath = rootDir+"DefaultTest.csv";
        String outputFilePath = rootDir+"Final_DefaultTest.csv";
        String smelltype = "DefaultTest";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);


        return smellpercentage;

    }

    public List<ProjectSmellEntity> analyzeRedundantPrint() throws IOException {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        List<String[]> csvData = new ArrayList<>();
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
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testredundantprintTestmap = cmtanalyzer.getRedundantPrint(commitid);
                System.out.println("Map of RedundantPrint: "+ testredundantprintTestmap);
                Set<String> allKeys = testredundantprintTestmap.keySet();
                RedundantPrint redundantPrint = new RedundantPrint();
                double percentage = redundantPrint.getRedundantPrintStats(testredundantprintTestmap);

                ProjectSmellEntity projsmell = new ProjectSmellEntity("RedundantPrint");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);

                for (String key : allKeys) {
                    if (testredundantprintTestmap.get(key)) {
                        // Add project name, key, and smell type to the csvData list only if value is true
                        csvData.add(new String[]{projsmell.getProjName(), key, "RedundantPrint"});
                    }

                }




            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String csvFilePath = rootDir+"RedundantPrint.csv";
            createFileIfNotExists(csvFilePath);
            appendDataToCSV(csvData, csvFilePath);

        }


        String inputFilePath = rootDir+"RedundantPrint.csv";
        String outputFilePath = rootDir+"Final_RedundantPrint.csv";
        String smelltype = "RedundantPrint";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);

        return smellpercentage;

    }


    public List<ProjectSmellEntity> analyzeConstructorInitialization() throws IOException {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();
        List<String[]> csvData = new ArrayList<>();

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
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testconstructorTestmap = cmtanalyzer.getConstructorInitialization(commitid);
                Set<String> allKeys = testconstructorTestmap.keySet();
                ConstructorInitialization constructorInitialization = new ConstructorInitialization();
                double percentage = constructorInitialization.getConstructorInitializationStats(testconstructorTestmap);

                ProjectSmellEntity projsmell = new ProjectSmellEntity("ConstructorInitialization");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);

                for (String key : allKeys) {
                    if (testconstructorTestmap.get(key)) {
                        // Add project name, key, and smell type to the csvData list only if value is true
                        csvData.add(new String[]{projsmell.getProjName(), key, "ConstructorInitialization"});
                    }
                }



            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String csvFilePath = rootDir+"ConstructorInitialization.csv";
            createFileIfNotExists(csvFilePath);
            appendDataToCSV(csvData, csvFilePath);

        }

        String inputFilePath = rootDir+"ConstructorInitialization.csv";
        String outputFilePath = rootDir+"Final_ConstructorInitialization.csv";
        String smelltype = "ConstructorInitialization";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);

        return smellpercentage;

    }

    public List<ProjectSmellEntity> analyzeSleepyTest() throws IOException {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();
        List<String[]> csvData = new ArrayList<>();
        int counter = 0;
        System.out.println("TreeCheckkkk"+ projlist.size());
        for (String proj : projlist) {
            String projname = ProjectPropertyAnalyzer.getProjName(proj);
            TestAnalysisData analysisdata = new TestAnalysisData(projname);

            CommitAnalyzer cmtanalyzer = null;
            System.out.println(counter + "-->" + projname);

            counter++;
//            if (counter > 5)
//                return smellpercentage;

            try {
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                System.out.println("TreeCheckkkk"+ commitid);
                Map<String,Boolean> testsleepyTestmap = cmtanalyzer.getSleepyTest(commitid);
//                System.out.println("sleepy test:"+ testsleepyTestmap);
                Set<String> allKeys = testsleepyTestmap.keySet();
                SleepyTest sleepyTest = new SleepyTest();
                double percentage = sleepyTest.getSleepyTestStats(testsleepyTestmap);

                ProjectSmellEntity projsmell = new ProjectSmellEntity("SleepyTest");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);

                for (String key : allKeys) {
                    if (testsleepyTestmap.get(key)) {
                        // Add project name, key, and smell type to the csvData list only if value is true
                        csvData.add(new String[]{projsmell.getProjName(), key, "SleepyTest"});
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String csvFilePath = rootDir+"SleepyTest.csv";
            createFileIfNotExists(csvFilePath);
            appendDataToCSV(csvData, csvFilePath);

        }

        String inputFilePath = rootDir+"SleepyTest.csv";
        String outputFilePath = rootDir+"Final_SleepyTest.csv";
        String smelltype = "SleepyTest";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);

        return smellpercentage;

    }

    public List<ProjectSmellEntity> analyzeEmptyTest() throws IOException {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();
        List<String[]> csvData = new ArrayList<>();

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
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testemptyTestmap = cmtanalyzer.getEmptyTest(commitid);
                Set<String> allKeys = testemptyTestmap.keySet();
                EmptyTest emptyTest = new EmptyTest();
                double percentage = emptyTest.getEmptyTestStats(testemptyTestmap);

                ProjectSmellEntity projsmell = new ProjectSmellEntity("EmptyTest");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);


                for (String key : allKeys) {
                    if (testemptyTestmap.get(key)) {
                        // Add project name, key, and smell type to the csvData list only if value is true
                        csvData.add(new String[]{projsmell.getProjName(), key, "EmptyTest"});
                    }
                }



            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String csvFilePath = rootDir+"EmptyTest.csv";
            createFileIfNotExists(csvFilePath);
            appendDataToCSV(csvData, csvFilePath);

        }

        String inputFilePath = rootDir+"EmptyTest.csv";
        String outputFilePath = rootDir+"Final_EmptyTest.csv";
        String smelltype = "EmptyTest";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);

        return smellpercentage;

    }

    public List<ProjectSmellEntity> analyzeIgnoredTest() throws IOException {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();
        List<String[]> csvData = new ArrayList<>();

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
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testignoredTestmap = cmtanalyzer.getIgnoredTest(commitid);
                Set<String> allKeys = testignoredTestmap.keySet();
                IgnoredTest ignoredTest = new IgnoredTest();
                double percentage = ignoredTest.getIgnoredTestStats(testignoredTestmap);

                ProjectSmellEntity projsmell = new ProjectSmellEntity("IgnoredTest");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);

                for (String key : allKeys) {
                    if (testignoredTestmap.get(key)) {
                        // Add project name, key, and smell type to the csvData list only if value is true
                        csvData.add(new String[]{projsmell.getProjName(), key, "IgnoredTest"});
                    }
                }



            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String csvFilePath = rootDir+"IgnoredTest.csv";
            createFileIfNotExists(csvFilePath);
            appendDataToCSV(csvData, csvFilePath);

        }


        String inputFilePath = rootDir+"IgnoredTest.csv";
        String outputFilePath = rootDir+"Final_IgnoredTest.csv";
        String smelltype = "IgnoredTest";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);

        return smellpercentage;

    }

    public List<ProjectSmellEntity> analyzeExceptionCatchingThrowingTest() throws IOException {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        // List<PerfFixData> fixdata = new ArrayList<>();
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();
        List<String[]> csvData = new ArrayList<>();

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
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testexceptionTestmap = cmtanalyzer.getExceptionTest(commitid);
                Set<String> allKeys = testexceptionTestmap.keySet();
                ExceptionCatchingThrowing exceptionCatchingThrowing = new ExceptionCatchingThrowing();
                double percentage = exceptionCatchingThrowing .getExceptionTestStats(testexceptionTestmap );

                ProjectSmellEntity projsmell = new ProjectSmellEntity("ExceptionThrowingTest");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);

                for (String key : allKeys) {
                    if (testexceptionTestmap.get(key)) {
                        // Add project name, key, and smell type to the csvData list only if value is true
                        csvData.add(new String[]{projsmell.getProjName(), key, "ExceptionThrowingTest"});
                    }
                }



            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String csvFilePath = rootDir+"ExceptionThrowingTest.csv";
            createFileIfNotExists(csvFilePath);
            appendDataToCSV(csvData, csvFilePath);

        }


        String inputFilePath = rootDir+"ExceptionThrowingTest.csv";
        String outputFilePath = rootDir+"Final_ExceptionThrowingTest.csv";
        String smelltype = "ExceptionThrowingTest";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);

        return smellpercentage;

    }

    public List<ProjectSmellEntity> analyzeUnknownTest() throws IOException {
        String filepath = Config.gitProjList;
        List<String[]> csvData = new ArrayList<>();
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
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testunknownTestmap = cmtanalyzer.getUnknownTest(commitid);
                Set<String> allKeys = testunknownTestmap.keySet();
                UnknownTest unknownTest = new UnknownTest();
                double percentage = unknownTest.getUnknownTestStats(testunknownTestmap);

                ProjectSmellEntity projsmell = new ProjectSmellEntity("UnknownTest");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);

                ProjectSmellEntity projsmells = new ProjectSmellEntity("UnknownTest");
                for (String key : allKeys) {
                    if (testunknownTestmap.get(key)) {
                        // Add project name, key, and smell type to the csvData list only if value is true
                        csvData.add(new String[]{projsmell.getProjName(), key, "UnknownTest"});
                    }
                }



            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String csvFilePath = rootDir+"UnknownTest.csv";
            createFileIfNotExists(csvFilePath);
            appendDataToCSV(csvData, csvFilePath);

        }

        String inputFilePath = rootDir+"UnknownTest.csv";
        String outputFilePath = rootDir+"Final_UnknownTest.csv";
        String smelltype = "UnknownTest";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);

        return smellpercentage;

    }

    public List<ProjectSmellEntity> analyzeRedundantAssertTest() throws IOException {
        String filepath = Config.gitProjList;
        List<String[]> csvData = new ArrayList<>();
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
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testredundantassert = cmtanalyzer.getRedundantAssert(commitid);
                System.out.println("Map of testredundantassert: "+ testredundantassert);
                RedundantAssertion redundantassertion = new RedundantAssertion();
                Set<String> allKeys = testredundantassert.keySet();
                double percentage = redundantassertion.getRedundantAssertionStats(testredundantassert);

                ProjectSmellEntity projsmell = new ProjectSmellEntity("RedundantAssertion");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);

                ProjectSmellEntity projsmells = new ProjectSmellEntity("RedundantAssertion");
                for (String key : allKeys) {
                    if (testredundantassert.get(key)) {
                        // Add project name, key, and smell type to the csvData list only if value is true
                        csvData.add(new String[]{projsmell.getProjName(), key, "RedundantAssertion"});
                    }
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String csvFilePath = rootDir+"RedundantAssertion_.csv";
            createFileIfNotExists(csvFilePath);
            appendDataToCSV(csvData, csvFilePath);

        }
        String inputFilePath = rootDir+"RedundantAssertion_.csv";
        String outputFilePath = rootDir+"Final_RedundantAssertion_.csv";
        String smelltype = "RedundantAssertion";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);

        return smellpercentage;

    }

    public List<ProjectSmellEntity> analyzeDuplicateAssertTest() throws IOException {
        String filepath = Config.gitProjList;
        List<String[]> csvData = new ArrayList<>();
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
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                String commitid = cmtanalyzer.getHeadCommitID();
                Map<String,Boolean> testduplicateassert = cmtanalyzer.getDuplicateAssert(commitid);
                Set<String> allKeys = testduplicateassert.keySet();
                DuplicateAssert duplicateassert = new DuplicateAssert();
                double percentage = duplicateassert.getDuplicateAssertTestStats(testduplicateassert);
                ProjectSmellEntity projsmell = new ProjectSmellEntity("DuplicateAssert");
                projsmell.setProjName(projname);
                projsmell.setSmellPercentage(percentage);
                smellpercentage.add(projsmell);

                ProjectSmellEntity projsmells = new ProjectSmellEntity("DuplicateAssert");
                for (String key : allKeys) {
                    if (testduplicateassert.get(key)) {
                        // Add project name, key, and smell type to the csvData list only if value is true
                        csvData.add(new String[]{projsmell.getProjName(), key, "DuplicateAssert"});
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String csvFilePath = rootDir+"DuplicateAssert_.csv";
            createFileIfNotExists(csvFilePath);
            appendDataToCSV(csvData, csvFilePath);

        }

        String inputFilePath = rootDir+"DuplicateAssert_.csv";
        String outputFilePath = rootDir+"Final_DuplicateAssert_.csv";
        String smelltype = "DuplicateAssert";
        removeDuplicatesFromCSV(inputFilePath,outputFilePath,smelltype);


        return smellpercentage;

    }

    public List<ProjectSmellEntity> analyzeAllTestCases() throws IOException {
        String filepath = Config.gitProjList;

        List<String> projlist = TextFileReaderWriter.GetFileContentByLine(filepath);
        List<ProjectSmellEntity> smellpercentage = new ArrayList<>();
        List<String[]> csvData = new ArrayList<>();

        Set<String> existingTestCases_nosmell = new HashSet<>();
        // Read test cases from other CSV files
        Set<String> existingTestCases = new HashSet<>();
        existingTestCases.addAll(getTestCasesFromCSV(rootDir+"Final_Assertion_Roulette_Smells.csv"));
        existingTestCases.addAll(getTestCasesFromCSV(rootDir+"Final_EagerTest.csv"));
        existingTestCases.addAll(getTestCasesFromCSV(rootDir+"Final_ConstructorInitialization.csv"));
        existingTestCases.addAll(getTestCasesFromCSV(rootDir+"Final_DefaultTest.csv"));
        existingTestCases.addAll(getTestCasesFromCSV(rootDir+"Final_EmptyTest.csv"));
        existingTestCases.addAll(getTestCasesFromCSV(rootDir+"Final_ExceptionThrowingTest.csv"));
        existingTestCases.addAll(getTestCasesFromCSV(rootDir+"Final_GeneralFixture.csv"));
        existingTestCases.addAll(getTestCasesFromCSV(rootDir+"Final_IgnoredTest.csv"));
        existingTestCases.addAll(getTestCasesFromCSV(rootDir+"Final_LazyTest.csv"));
        existingTestCases.addAll(getTestCasesFromCSV(rootDir+"Final_MagicNumber.csv"));
        existingTestCases.addAll(getTestCasesFromCSV(rootDir+"Final_MysteryGuest.csv"));
        existingTestCases.addAll(getTestCasesFromCSV(rootDir+"Final_RedundantAssertion.csv"));
        existingTestCases.addAll(getTestCasesFromCSV(rootDir+"Final_RedundantPrint.csv"));
        existingTestCases.addAll(getTestCasesFromCSV(rootDir+"Final_SensitiveEquality.csv"));
        existingTestCases.addAll(getTestCasesFromCSV(rootDir+"Final_SleepyTest.csv"));


        int counter = 0;
        for (String proj : projlist) {
            String projname = ProjectPropertyAnalyzer.getProjName(proj);
            TestAnalysisData analysisdata = new TestAnalysisData(projname);

            CommitAnalyzer cmtanalyzer = null;
            System.out.println(counter + "-->" + projname);
            counter++;

            try {
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

                // Get the commit ID for the project
                String commitid = cmtanalyzer.getHeadCommitID();
                // Get all test cases from the commit, with file names
                Map<String, Boolean> projTestFuncMap = cmtanalyzer.getAllTestCases(commitid);
                Set<String> allKeys = projTestFuncMap.keySet();

                System.out.println("proj_result" + projTestFuncMap);
                ProjectSmellEntity projsmell = new ProjectSmellEntity("TestCases");
                projsmell.setProjName(projname);

                // Add the number of test cases to the project smell percentage
                projsmell.setSmellPercentage((double) allKeys.size());
                smellpercentage.add(projsmell);

                // Add test case data (formatted as "TestFile<>Function") to CSV if it does not exist in x.csv, y.csv, z.csv
                for (String key : allKeys) {
                    String fileName = String.valueOf(projTestFuncMap.get(key));
                    // Combine Test File Name and Function Name into "TestFileName<>TestFunction"
                    String testCaseFormatted1 = fileName.replace(".cs", "") + "<>" + key;
                    String testcaseFormatted = testCaseFormatted1.replace("true<>", "").trim();

                    // Check if the test case already exists in x.csv, y.csv, or z.csv
                    if (!existingTestCases.contains(projname + "::" + testcaseFormatted)) {
                        csvData.add(new String[]{projname, testcaseFormatted, "NoSmell"});
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Append the captured test cases to the no_smell.csv file if they do not already exist in x.csv, y.csv, z.csv
            String csvFilePath = rootDir + "no_smell.csv";
            appendDataToCSV(csvData, csvFilePath);
            String inputFilePath = rootDir+"no_smell.csv";
            String outputFilePath= rootDir+"Final_no_smell.csv";
            String smellType = "no smell";
            File outputFile = new File(outputFilePath);
            if (!outputFile.exists()) {
                outputFile.createNewFile();  // Create the file if it doesn't exist
                System.out.println("Created file: " + outputFilePath);
            }
            removeDuplicatesFromCSV(inputFilePath,outputFilePath,smellType);
        }

        return smellpercentage;
    }

    private void removeDuplicatesFromCSV(String inputFilePath, String outputFilePath, String smellType) throws IOException {
        // Ensure that the output file exists, create it if not

        Set<String> uniqueRows = new HashSet<>();  // Set to store unique rows

        // Read the CSV and store only unique rows based on columns
        try (FileReader reader = new FileReader(inputFilePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            for (CSVRecord record : csvParser) {
                // Construct a unique row based on the specific columns
                String projectName = record.get(0).trim();
                String testFunction = record.get(1).trim();

                // Create a unique row string (you can adjust the delimiter as needed)
                String row = projectName + "," + testFunction + "," + smellType;

                uniqueRows.add(row);  // Add the row to the set (will automatically avoid duplicates)
            }
        }

        // Write the unique rows back to the output CSV file
        try (FileWriter writer = new FileWriter(outputFilePath);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Project Name", "Test Function", "Smell Type"))) {

            for (String row : uniqueRows) {
                // Split the row back into columns
                String[] columns = row.split(",");
                csvPrinter.printRecord((Object[]) columns);  // Write each row
            }
        }

        System.out.println("Duplicate rows removed, unique data written to: " + outputFilePath);
    }

    public Set<String> getTestCasesFromCSV(String csvFilePath) throws IOException {
        Set<String> testCases = new HashSet<>();

        try (FileReader reader = new FileReader(csvFilePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            for (CSVRecord record : csvParser) {
                String projName = record.get(0).trim();;  // Get project name
                String testCase = record.get(1).trim();;     // Get test case name
                // Combine project name and test case as a unique string
                String combinedTestCase = projName + "::" + testCase;
                testCases.add(combinedTestCase);
            }
        }

        return testCases;
    }

    public static List<String[]> readCSV(String filePath) {
        List<String[]> data = new ArrayList<>();
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                data.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static List<String[]> getRandomRows(List<String[]> data, int numRows) {
        List<String[]> selectedRows = new ArrayList<>();
        Random random = new Random();
        Set<Integer> selectedIndices = new HashSet<>();

        // Ensure we don't pick more rows than available
        if (numRows > data.size()) {
            System.out.println("Number of rows to select exceeds the number of rows available in the CSV file.");
            return selectedRows;
        }

        while (selectedIndices.size() < numRows) {
            int randomIndex = random.nextInt(data.size() - 1) + 1;
            if (!selectedIndices.contains(randomIndex)) {
                selectedRows.add(data.get(randomIndex));
                selectedIndices.add(randomIndex);
            }
        }

        return selectedRows;
    }

    public static void writeCSV(String filePath, List<String[]> data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] row : data) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        List<Map<String, Boolean>> defaultTestSmells= new ArrayList<>();
        List< Map<String,Boolean> > redundantPrint= new ArrayList<>();
        List< Map<String,Boolean> > constructorInitialization= new ArrayList<>();

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
                Map <String,String> ownerProject = getProjectStructureName(projname);
                cmtanalyzer = new CommitAnalyzer(ownerProject.keySet().iterator().next(), ownerProject.values().iterator().next(), proj);

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
