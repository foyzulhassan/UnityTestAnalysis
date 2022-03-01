package com.unity.testsmell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
//            if (counter > 5)
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

}
