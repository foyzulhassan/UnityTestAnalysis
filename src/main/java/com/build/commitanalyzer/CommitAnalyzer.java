package com.build.commitanalyzer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.apache.commons.compress.utils.IOUtils;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.unity.testanalysis.FunctionalCodeLOCExtractor;
import com.unity.testsmell.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import com.build.analyzer.entity.CommitChange;
import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsTreeGenerator;
import com.csharp.diff.CSharpDiffGenerator;
import com.github.gumtreediff.actions.EditScript;

//import edu.utsa.data.DataResultsHolder;
//import edu.utsa.data.DataStatsHolder;
//import edu.utsa.main.MainClass;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.tree.ITree;
import com.unity.callgraph.ClassFunction;
import com.unity.entity.PerfFixData;
import com.unity.testanalysis.ClassFunctionTypeAnalyzer;
import com.unity.testanalyzer.LineCountAssertCount;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;

import static com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNodeList;
import static java.lang.System.exit;


/**
 * @author Foyzul Hassan
 */

public class CommitAnalyzer {

    /**
     * Various methods encapsulating methods to treats Git and commits datas
     */
    private static CommitAnalyzingUtils commitAnalyzingUtils;

    /**
     * All the statistical datas (number of faulty commit, actions, etc)
     */
    private DataStatsHolder statsHolder;

    /**
     * File managing object for tables
     */
    private DataResultsHolder resultsHolder;

    /**
     * Name of the project
     */
    private String project;

    /**
     * Owner of the project (necessary for Markdown parsing)
     */
    private String projectOwner;

    /**
     * Path to the directory
     */
    private String directoryPath;

    /**
     * Repository object, representing the directory
     */
    private static Repository repository;

    /**
     * Git entity to treat with the Repository data
     */
    private Git git;

    /**
     * Revision walker from JGit
     */
    private static RevWalk rw;

    private CommitChange commitChangeTracker;

    private String gradleChanges;

    private String gitUrl;

    /**
     * Classic constructor
     */
    public CommitAnalyzer(String projectOwner, String project) throws Exception {
        this.projectOwner = projectOwner;
        this.project = project;

        directoryPath = Config.repoDir +projectOwner+"/"+ project + "/.git";

        commitAnalyzingUtils = new CommitAnalyzingUtils();
        statsHolder = new DataStatsHolder();
        repository = commitAnalyzingUtils.setRepository(directoryPath);
        git = new Git(repository);
        rw = new RevWalk(repository);
        this.commitChangeTracker = new CommitChange();
        this.gradleChanges = "";
    }

    /**
     * Classic constructor
     */
    public CommitAnalyzer(String projectOwner, String project, String giturl) throws Exception {
        this.projectOwner = projectOwner;
        this.project = project;

        directoryPath =  Config.repoDir +projectOwner+"/"+ project + "/.git";

        commitAnalyzingUtils = new CommitAnalyzingUtils();
        statsHolder = new DataStatsHolder();
        repository = commitAnalyzingUtils.setRepository(directoryPath);
        git = new Git(repository);
        rw = new RevWalk(repository);
        this.commitChangeTracker = new CommitChange();
        this.gradleChanges = "";
        this.gitUrl = giturl;
    }

    public CommitChange getCommitChangeTracker() {
        return commitChangeTracker;
    }

    public void commitSampleTry(String ID) {
        List<Action> totalactions = new ArrayList<Action>();
        List<Action> act = new ArrayList<Action>();
        List<String> debugging = new ArrayList<String>();
        String r = "";
        // File debug = new File("debug-" + ID + ".txt");

        try {
            ObjectId objectid = repository.resolve(ID);

            if (objectid == null)
                return;

            RevCommit commit = rw.parseCommit(objectid);

            // System.out.println(commit.getFullMessage());

            if (commit.getParentCount() > 0) {
                RevCommit parent = rw.parseCommit(commit.getParent(0).getId());

                DiffFormatter df = commitAnalyzingUtils.setDiffFormatter(repository, true);

                List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());

                for (DiffEntry diff : diffs) {
                    if (diff.getNewPath().contains("build.gradle")) {

                        commitChangeTracker.setBuildFileChange(commitChangeTracker.getBuildFileChange() + 1);

                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public List<PerfFixData> getAllPerformanceCommits()
            throws MissingObjectException, IncorrectObjectTypeException, IOException {
        List<PerfFixData> perffixdata = new ArrayList<>();

        Collection<Ref> allRefs = repository.getAllRefs().values();

        // a RevWalk allows to walk over commits based on some filtering that is defined
        try (RevWalk revWalk = new RevWalk(repository)) {
            for (Ref ref : allRefs) {
                revWalk.markStart(revWalk.parseCommit(ref.getObjectId()));
            }
            // System.out.println("Walking all commits starting with " + allRefs.size() + "
            // refs: " + allRefs);
            int count = 0;
            for (RevCommit commit : revWalk) {
                System.out.println("Commit: " + commit);
                count++;

                String commitmsg = commit.getFullMessage().toLowerCase();
                commitmsg = commitmsg.replaceAll(",", " cma ");
                commitmsg = commitmsg.replaceAll("\"", " quote ");

                if (isPerformanceCommit(commitmsg) && !commitmsg.contains("merge")) {
                    String commitid = commit.getName();
                    PerfFixData fixdata = new PerfFixData(this.project, this.getGitUrl(), commitid);
                    fixdata.setFixCommitMsg(commitmsg);
                    fixdata.setPatchPath("");
                    fixdata.setAssetChangeCount(0);
                    fixdata.setSrcFileChangeCount(0);
                    perffixdata.add(fixdata);
                }
            }
            // System.out.println("Had " + count + " commits");
            // System.out.println(this.project);
        }

        return perffixdata;
    }

    private boolean isPerformanceCommit(String commitmsg) {
        for (String token : Config.perfCommitToken) {
            if (commitmsg.contains(token)) {
                return true;
            }
        }

        return false;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public List<EditScript> extractCSharpFileChange(String ID) {
        // File debug = new File("debug-" + ID + ".txt");
        List<EditScript> actionslist = new ArrayList<>();

        try {
            ObjectId objectid = repository.resolve(ID);

            if (objectid == null)
                return null;

            RevCommit commit = rw.parseCommit(objectid);

            /// System.out.println(commit.getFullMessage());

            if (commit.getParentCount() > 0) {
                RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
                DiffFormatter df = commitAnalyzingUtils.setDiffFormatter(repository, true);

                List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());

                for (DiffEntry diff : diffs) {

                    if (diff.getNewPath().endsWith(".cs")) {

                        String currentContent = getFileContentAtCommit(ID, diff);
                        String previousContent = getFileContentAtCommit(parent.getName(), diff);

                        File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", currentContent);

                        File f2 = commitAnalyzingUtils.writeContentInFile("g2.cs", previousContent);

                        CSharpDiffGenerator diffgen = new CSharpDiffGenerator();
                        EditScript actions = diffgen.generateDiff(f1, f2);
                        if (actions != null) {
                            actionslist.add(actions);
                        }

                        f1.delete();
                        f2.delete();

                    }
                }

            }
            // gradleChanges = gradlechgmgr.getXMLChange();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return actionslist;
    }

    public PerfFixData extractFileChangeData(String ID, PerfFixData fixcommit) {
        // File debug = new File("debug-" + ID + ".txt");
        List<EditScript> actionslist = new ArrayList<>();
        int srcfilecount = 0;
        int otherfilecount = 0;

        try {
            ObjectId objectid = repository.resolve(ID);

            if (objectid == null)
                return null;

            RevCommit commit = rw.parseCommit(objectid);

            /// System.out.println(commit.getFullMessage());

            if (commit.getParentCount() > 0) {
                RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
                DiffFormatter df = commitAnalyzingUtils.setDiffFormatter(repository, true);

                List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());

                for (DiffEntry diff : diffs) {
                    String changepath = diff.getNewPath();
                    changepath = changepath.toLowerCase();
                    if (changepath.endsWith(".cs")) {
                        srcfilecount++;
                    } else if (!changepath.endsWith(".md") && !changepath.contains("readme")) {
                        otherfilecount++;
                    }
                }

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        fixcommit.setAssetChangeCount(otherfilecount);
        fixcommit.setSrcFileChangeCount(srcfilecount);

        return fixcommit;
    }

    public String getFileContentAtCommit(String commitid, DiffEntry diff) {
        String content = "";
        try {
            ObjectId objectid1 = repository.resolve(commitid);

            if (objectid1 == null)
                return null;

            RevCommit parent = rw.parseCommit(objectid1);

            RevTree tree = getTree(commitid);

            content = getStringFile(tree, diff.getNewPath());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return content;
    }

    public RevTree getTree(String cmtid) throws IOException {
        ObjectId lastCommitId = repository.resolve(cmtid);

        // a RevWalk allows to walk over commits based on some filtering
        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit commit = revWalk.parseCommit(lastCommitId);

            // System.out.println("Time of commit (seconds since epoch): " +
            // commit.getCommitTime());

            // and using commit's tree find the path
            RevTree tree = commit.getTree();
            // System.out.println("Having tree: " + tree);
            return tree;
        }
    }

    public String getStringFile(RevTree tree, String filter) throws IOException {
        // now try to find a specific file
        try (TreeWalk treeWalk = new TreeWalk(repository)) {

            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);

            treeWalk.setFilter(PathFilter.create(filter));
            if (!treeWalk.next()) {
                throw new IllegalStateException("Did not find expected file:" + filter);
            }

            // FileMode specifies the type of file, FileMode.REGULAR_FILE for
            // normal file, FileMode.EXECUTABLE_FILE for executable bit
            // set
            FileMode fileMode = treeWalk.getFileMode(0);
            ObjectLoader loader = repository.open(treeWalk.getObjectId(0));

            // loader.copyTo(System.out);
            byte[] butestr = loader.getBytes();

            String str = new String(butestr);

            return str;

        }
    }

    public List<ClassFunction> getClassFunctionCall(String commitid) {

        List<ClassFunction> classfunclist = new ArrayList<>();

        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());

                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {
                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);
                    CSharpDiffGenerator diffgen = new CSharpDiffGenerator();
                    ClassFunction clsfunc = diffgen.getClassFunction(f1);
                    classfunclist.add(clsfunc);

                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }

        return classfunclist;
    }

    public String getHeadCommitID() {
        System.out.println("headCheckCount ==> " + repository.getAllRefs().size());
        //System.out.println("headNullCheck ==> "+ repository.getAllRefs());
        Ref head = repository.getAllRefs().get("HEAD");
        //System.out.println("headCheck ==> " + head + "ObjectID => " + head.getObjectId() + "Name => " + head.getObjectId().getName()  );
        return head.getObjectId().getName();
    }


    public List<ClassFunction> getClassFunctionTypeList(String commitid) {

        List<ClassFunction> classfunclist = new ArrayList<>();

        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());

                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {
                    if (treeWalk.getPathString().contains("WalkingTests.cs"))
                        System.out.println("Debug");

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    //CSharpDiffGenerator diffgen = new CSharpDiffGenerator();
                    ClassFunctionTypeAnalyzer typeanalyzer = new ClassFunctionTypeAnalyzer();
                    ClassFunction clsfunc = typeanalyzer.getClassFunctionType(f1);
                    classfunclist.add(clsfunc);

                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }

        return classfunclist;
    }

    public LineCountAssertCount getTestLocAndAssertCount(String commitid) {

        List<ClassFunction> classfunclist = new ArrayList<>();
        LineCountAssertCount projtestloclinecount = new LineCountAssertCount();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());

                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {
                    if (treeWalk.getPathString().contains("WalkingTests.cs"))
                        System.out.println("Debug");

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    //CSharpDiffGenerator diffgen = new CSharpDiffGenerator();
                    ClassFunctionTypeAnalyzer typeanalyzer = new ClassFunctionTypeAnalyzer();
                    LineCountAssertCount testclasslocassert = typeanalyzer.getClassTestLocAssert(f1);
                    projtestloclinecount.addAssertCount(testclasslocassert.getAssertCount());
                    projtestloclinecount.addLineCount(testclasslocassert.getLineCount());

                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }

        return projtestloclinecount;
    }

    public LineCountAssertCount getFuncCodeLOC(String commitid) {

        List<ClassFunction> classfunclist = new ArrayList<>();
        LineCountAssertCount projtestloclinecount = new LineCountAssertCount();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());

                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {
                    if (treeWalk.getPathString().contains("WalkingTests.cs"))
                        System.out.println("Debug");

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    //CSharpDiffGenerator diffgen = new CSharpDiffGenerator();
                    FunctionalCodeLOCExtractor typeanalyzer = new FunctionalCodeLOCExtractor();
                    LineCountAssertCount testclasslocassert = typeanalyzer.getClassLoc(f1);
                    projtestloclinecount.addAssertCount(testclasslocassert.getAssertCount());
                    projtestloclinecount.addLineCount(testclasslocassert.getLineCount());

                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }

        return projtestloclinecount;
    }

    public Map<String, Map<String, Integer>> getConditionalTest(String commitid) {

        //List<ClassFunction> classfunclist=new ArrayList<>();
        Map<String, Map<String, Integer>> projtestfuncassertmap = new HashMap<>();
        Map<String, Map<String, Integer>> map = new HashMap<>();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());
//				System.out.println(treeWalk.getPathString()+"*****");
                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    Reader reader;
                    try {
//						System.out.println(treeWalk.getPathString());

//						if(treeWalk.getPathString().contains("Resources.Designer.cs"))
//						{
//							System.out.print("debug");
//						}
                        reader = new FileReader(f1.toString());
                        ITree curtree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

                        //TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
                        //analyzer.getTestFunctionList(curtree);
                        ConditionalTestLogic ct = new ConditionalTestLogic();
                        map = ct.searchForConditionalTestLogic(curtree);
//                        System.out.println(map);
                        //Copy to project map
                        for (String key : map.keySet()) {
                            if (!projtestfuncassertmap.containsKey(key)) {
                                projtestfuncassertmap.put(key, map.get(key));
                            }


                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
//        System.out.println(projtestfuncassertmap);
        return projtestfuncassertmap;
    }

    public Map<String, Boolean> getSensitiveEquality(String commitid) {

        //List<ClassFunction> classfunclist=new ArrayList<>();
        Map<String, Boolean> projtestfuncassertmap = new HashMap<>();
        Map<String, Boolean> map = new HashMap<>();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());
//				System.out.println(treeWalk.getPathString()+"*****");
                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    Reader reader;
                    try {
//						System.out.println(treeWalk.getPathString());

//						if(treeWalk.getPathString().contains("Resources.Designer.cs"))
//						{
//							System.out.print("debug");
//						}
                        reader = new FileReader(f1.toString());
                        ITree curtree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

                        //TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
                        //analyzer.getTestFunctionList(curtree);
                        SensitiveEquality se = new SensitiveEquality();
                        map = se.searchForSensitiveEquality(curtree);
                        System.out.println("map:"+map);
//                        System.out.println(map.size());
//                        System.out.println(map);
                        //Copy to project map
                        for (String key : map.keySet()) {
                            if (!projtestfuncassertmap.containsKey(key)) {
                                projtestfuncassertmap.put(key, map.get(key));
                            }


                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
//        System.out.println(projtestfuncassertmap);
        return projtestfuncassertmap;
    }

    public Map<String, Boolean> getLazyTest(String commitid, Map<String, List<String>> productCalls) {
        Map<String, Boolean> projtestfuncassertmap = new HashMap<>();
        Map<String, Boolean> map = new HashMap<>();

        if (productCalls == null || productCalls.isEmpty()) {
            //System.out.println("Error: 'productCalls' is null or empty.");
            return projtestfuncassertmap; // Return empty map to handle gracefully
        }

        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();
            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(tree);
            treeWalk.setRecursive(false);

            while (treeWalk.next()) {
                if (treeWalk.isSubtree()) {
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {
                    // Process .cs files
                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    byte[] byteContent = loader.getBytes();
                    String fileContent = new String(byteContent);
                    File tempFile = commitAnalyzingUtils.writeContentInFile("temp.cs", fileContent);

                    try (Reader reader = new FileReader(tempFile)) {
                        ITree curtree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

                        LazyTest lt = new LazyTest();
                        map = lt.searchForLazyTest(curtree, productCalls);
                        System.out.println("map:"+map);
                        //System.out.println("Lazy test map: " + map);

                        // Merge results into the project map
                        for (String key : map.keySet()) {
                            projtestfuncassertmap.putIfAbsent(key, map.get(key));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        tempFile.delete(); // Clean up temporary file
                    }
                }
            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return projtestfuncassertmap;
    }


    public Map<String, Boolean> getMysteryGuest(String commitid) {

        //List<ClassFunction> classfunclist=new ArrayList<>();
        Map<String, Boolean> projtestfuncassertmap = new HashMap<>();
        Map<String, Boolean> map = new HashMap<>();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());
//				System.out.println(treeWalk.getPathString()+"*****");
                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    Reader reader;
                    try {
//						System.out.println(treeWalk.getPathString());

//						if(treeWalk.getPathString().contains("Resources.Designer.cs"))
//						{
//							System.out.print("debug");
//						}
                        reader = new FileReader(f1.toString());
                        ITree curtree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

                        //TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
                        //analyzer.getTestFunctionList(curtree);
                        MysteryGuest se = new MysteryGuest();
                        map = se.searchForMysteryGuest(curtree);
//                        System.out.println(map.size());
                        //System.out.println("map:"+map);
                        //Copy to project map
                        for (String key : map.keySet()) {
                            if (!projtestfuncassertmap.containsKey(key)) {
                                projtestfuncassertmap.put(key, map.get(key));
                            }


                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
//        System.out.println(projtestfuncassertmap);
        return projtestfuncassertmap;
    }

    public Map<String, Double> getGeneralFixture(String commitid) {
        //List<ClassFunction> classfunclist=new ArrayList<>();
        Map<String, Double> projtestfuncassertmap = new HashMap<>();
        Map<String, Double> map = new HashMap<>();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());
//				System.out.println(treeWalk.getPathString()+"*****");
                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    Reader reader;
                    try {
//						System.out.println(treeWalk.getPathString());

//						if(treeWalk.getPathString().contains("Resources.Designer.cs"))
//						{
//							System.out.print("debug");
//						}
                        reader = new FileReader(f1.toString());
                        ITree curtree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();
//                        TreeContext tx = new SrcmlUnityCsTreeGenerator().generate(reader);
//                        System.out.println(tx.toString());
//                        System.exit(0);
                        //TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
                        //analyzer.getTestFunctionList(curtree);
                        GeneralFixture gf = new GeneralFixture();
                        //System.out.println("entered here... next time");
                        map = gf.searchForGeneralFixture(curtree);
                        //System.out.println("map"+map);
                        //System.out.println("entere here as well");
                        //Copy to project map
                        for (String key : map.keySet()) {
                            //System.out.println("entered here");
                            if (!projtestfuncassertmap.containsKey(key)) {
                                projtestfuncassertmap.put(key, map.get(key));
                            }


                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            //System.out.println("entered here");
            System.out.print(ex.getMessage());
        }
        //System.out.println("proj"+projtestfuncassertmap);
        return projtestfuncassertmap;


    }

    public static Map<String, Object> getProductCalls(String commitid) {
        Map<String, List<String>> classProductionMap = new HashMap<>(); // To group functions under classes
        Map<String, Object> resultMap = new HashMap<>();
        List<String> allProductCallsList = new ArrayList<>(); // Collect all class<>functionName pairs here

        try {
            ObjectId objectId = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectId);

            RevTree tree = commit.getTree();
            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(tree);
            treeWalk.setRecursive(false);

            while (treeWalk.next()) {
                if (treeWalk.isSubtree()) {
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {
                    // Skip files with "test" in their name (case-insensitive)
                    if (treeWalk.getPathString().toLowerCase().contains("test")) {
                        continue;
                    }

                    ObjectId fileId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(fileId);
                    byte[] byteContent = loader.getBytes();
                    String fileContent = new String(byteContent);

                    // Write the file content to a temporary file
                    File tempFile = commitAnalyzingUtils.writeContentInFile("temp.cs", fileContent);

                    try (Reader reader = new FileReader(tempFile)) {
                        // Parse the file to generate its AST
                        ITree curTree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

                        // Find the class node and its details
                        ITree classNode = SrcmlUnityCsMetaDataGenerator.breadthFirstSearchForNode(curTree, "class", "c1");
                        if (classNode == null) {
                            continue; // Skip if no class node is found
                        }

                        String className = SrcmlUnityCsMetaDataGenerator.getClassName(classNode).getLabel();
                        if (className == null || className.isEmpty()) {
                            continue; // Skip invalid class names
                        }

                        // Get all functions inside the class
                        List<ITree> functionList = breadthFirstSearchForNodeList(classNode, "function", "func");
                        for (ITree functionNode : functionList) {
                            String functionName = SrcmlUnityCsMetaDataGenerator.getFunctionName(functionNode);
                            if (functionName == null || functionName.isEmpty()) {
                                continue; // Skip invalid function names
                            }

                            // Add to class-production mapping
                            classProductionMap
                                    .computeIfAbsent(className, k -> new ArrayList<>())
                                    .add(functionName);

                            // Add to all product calls list
                            allProductCallsList.add(className + "<>" + functionName);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        // Clean up temporary file
                        tempFile.delete();
                    }
                }
            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        // Return both production call mapping and the full list of calls
        resultMap.put("productCalls", classProductionMap); // Grouped by class
        resultMap.put("allProductCalls", allProductCallsList); // Full list of calls
        return resultMap;
    }


    /**
     * Extracts class<>function pairs from the AST.
     */
    public List<String> getClassAndFunctionNames(ITree root) {
        List<String> classFunctionPairs = new ArrayList<>();
        List<ITree> classNodes = new ArrayList<>();

        // Traverse the tree to find all class nodes
        traverseTreeForClasses(root, classNodes);

        // For each class node, find its functions
        for (ITree classNode : classNodes) {
            String className = classNode.getLabel();
            if (className == null || className.trim().isEmpty()) {
                System.out.println("Skipping class node with no valid label: " + classNode);
                continue; // Skip nodes without a valid class name
            }

            List<ITree> functionNodes = getChildrenOfType(classNode, "function");
            for (ITree functionNode : functionNodes) {
                String functionName = functionNode.getLabel();
                if (functionName != null && !functionName.trim().isEmpty()) {
                    classFunctionPairs.add(className + "<>" + functionName);
                } else {
                    System.out.println("Skipping function node with no valid label: " + functionNode);
                }
            }
        }
        return classFunctionPairs;
    }

    /**
     * Traverses the AST to find all nodes of type "class".
     */
    private void traverseTreeForClasses(ITree node, List<ITree> classNodes) {
        if ("class".equalsIgnoreCase(String.valueOf(node.getType()))) {
            classNodes.add(node);
        }
        for (ITree child : node.getChildren()) {
            traverseTreeForClasses(child, classNodes);
        }
    }

    /**
     * Finds all children of a specific type under a given node.
     */
    private List<ITree> getChildrenOfType(ITree node, String type) {
        List<ITree> childrenOfType = new ArrayList<>();
        for (ITree child : node.getChildren()) {
            if (type.equalsIgnoreCase(String.valueOf(child.getType()))) {
                childrenOfType.add(child);
            }
        }
        return childrenOfType;
    }

    /**
     * Helper method to print the AST tree for debugging.
     */
    private void printTree(ITree node, int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("--");
        }
        System.out.println(node.getType() + " : " + node.getLabel());
        for (ITree child : node.getChildren()) {
            printTree(child, depth + 1);
        }
    }
    public Map<String, Boolean> getEagerTest(String commitid, Map<String, List<String>> productCalls) {
        Map<String, Boolean> projtestfuncassertmap = new HashMap<>();

        if (productCalls == null || productCalls.isEmpty()) {
            //System.out.println("Error: 'productCalls' is null or empty.");
            return projtestfuncassertmap; // Return empty map to handle gracefully
        }

        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();
            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(tree);
            treeWalk.setRecursive(false);

            while (treeWalk.next()) {
                if (treeWalk.isSubtree()) {
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {
                    // Process .cs files
                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    byte[] byteContent = loader.getBytes();
                    String fileContent = new String(byteContent);
                    File tempFile = commitAnalyzingUtils.writeContentInFile("temp.cs", fileContent);

                    try (Reader reader = new FileReader(tempFile)) {
                        ITree curtree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

                        // Analyze test functions using EagerTest
                        EagerTest et = new EagerTest();
                        Map<String, Boolean> map = et.searchForEagerTest(curtree, productCalls);
                        //System.out.println("Eager test map: " + map);

                        // Merge results into the project map
                        for (String key : map.keySet()) {
                            projtestfuncassertmap.putIfAbsent(key, map.get(key));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        tempFile.delete(); // Clean up temporary file
                    }
                }
            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return projtestfuncassertmap;
    }


    public Map<String, List<AssertCall>> getAssertRoulette(String commitid) {
        //List<ClassFunction> classfunclist=new ArrayList<>();
        Map<String, List<AssertCall>> projtestfuncassertmap = new HashMap<>();

        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());
//				System.out.println(treeWalk.getPathString()+"*****");
                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    Reader reader;
                    try {
//						System.out.println(treeWalk.getPathString());

//						if(treeWalk.getPathString().contains("Resources.Designer.cs"))
//						{
//							System.out.print("debug");
//						}
                        reader = new FileReader(f1.toString());
                        ITree curtree = new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();


                        //TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
                        //analyzer.getTestFunctionList(curtree);
                        AssertionRoulette ar = new AssertionRoulette();
                        Map<String, List<AssertCall>> testfuncassertmap = ar.searchForAssertionRoulette(curtree);
                        //System.out.println("map:"+testfuncassertmap);
                        for (Map.Entry<String, List<AssertCall>> entry : testfuncassertmap.entrySet()) {
                           String key = entry.getKey();
                           List<AssertCall> values = entry.getValue();

                           // Apply the logic: if the values' size > 1, mark as true; otherwise, false
                           boolean result = values.size() > 1;
                            // Print the transformed output
                            System.out.println(key + "= " + result);
                        }
                        //Copy to project map
                        for (String key : testfuncassertmap.keySet()) {
                            if (!projtestfuncassertmap.containsKey(key)) {
                                projtestfuncassertmap.put(key, testfuncassertmap.get(key));
                            }
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    f1.delete();
                }

            }
            treeWalk.reset();
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }

        return projtestfuncassertmap;
    }

    public Map<String, Boolean> getMagicNumber(String commitid) {

        //List<ClassFunction> classfunclist=new ArrayList<>();
        Map<String, Boolean> projtestfuncassertmap = new HashMap<>();
        Map<String, Boolean> mp = new HashMap<>();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());
//				System.out.println(treeWalk.getPathString()+"*****");
                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    Reader reader;
                    try {
//						System.out.println(treeWalk.getPathString());

//						if(treeWalk.getPathString().contains("Resources.Designer.cs"))
//						{
//							System.out.print("debug");
//						}
                        reader = new FileReader(f1.toString());
                        ITree curtree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

                        //TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
                        //analyzer.getTestFunctionList(curtree);
                        MagicNumberTest mn = new MagicNumberTest();
                        mp = mn.searchForMagicNumber(curtree);
//                        System.out.println(map.size());
                        //System.out.println("map"+ mp);
                        //Copy to project map
                        for (String key : mp.keySet()) {
                            if (!projtestfuncassertmap.containsKey(key)) {
                                projtestfuncassertmap.put(key, mp.get(key));
                            }


                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
//        System.out.println(projtestfuncassertmap);
        return projtestfuncassertmap;
    }
    public Map<String, Boolean> getDefaultTest(String commitid) {

        //List<ClassFunction> classfunclist=new ArrayList<>();
        Map<String, Boolean> projtestfuncassertmap = new HashMap<>();
        Map<String, Boolean> map = new HashMap<>();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());
//				System.out.println(treeWalk.getPathString()+"*****");
                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    Reader reader;
                    try {
//						System.out.println(treeWalk.getPathString());

//						if(treeWalk.getPathString().contains("Resources.Designer.cs"))
//						{
//							System.out.print("debug");
//						}
                        reader = new FileReader(f1.toString());
                        ITree curtree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

                        //TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
                        //analyzer.getTestFunctionList(curtree);
                        //SensitiveEquality se = new SensitiveEquality();
                        DefaultTest de = new DefaultTest();
                        map = de.searchForDefaultTest(curtree);
                        //System.out.println("map:"+map);
//                        System.out.println(map.size());
//                        System.out.println(map);
                        //Copy to project map
                        for (String key : map.keySet()) {
                            if (!projtestfuncassertmap.containsKey(key)) {
                                projtestfuncassertmap.put(key, map.get(key));
                            }


                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
//        System.out.println(projtestfuncassertmap);
        return projtestfuncassertmap;
    }

    public Map<String, Boolean> getRedundantPrint(String commitid) {

        //List<ClassFunction> classfunclist=new ArrayList<>();
        Map<String, Boolean> projtestfuncassertmap = new HashMap<>();
        Map<String, Boolean> map = new HashMap<>();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());
//				System.out.println(treeWalk.getPathString()+"*****");
                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    Reader reader;
                    try {
//						System.out.println(treeWalk.getPathString());

//						if(treeWalk.getPathString().contains("Resources.Designer.cs"))
//						{
//							System.out.print("debug");
//						}
                        reader = new FileReader(f1.toString());
                        ITree curtree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();
                        //TreeNodeAnalyzer.printTree(curtree, "");
                        //TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
                        //analyzer.getTestFunctionList(curtree);
                        RedundantPrint rp = new RedundantPrint();
                        map = rp.searchForRedundantPrint(curtree);
                        //System.out.println("map:"+map);
//                        System.out.println(map.size());
//                        System.out.println(map);
                        //Copy to project map
                        for (String key : map.keySet()) {
                            if (!projtestfuncassertmap.containsKey(key)) {
                                projtestfuncassertmap.put(key, map.get(key));
                            }


                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
//        System.out.println(projtestfuncassertmap);
        return projtestfuncassertmap;
    }

    public Map<String, Boolean> getConstructorInitialization(String commitid) {

        //List<ClassFunction> classfunclist=new ArrayList<>();
        Map<String, Boolean> projtestfuncassertmap = new HashMap<>();
        Map<String, Boolean> map = new HashMap<>();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());
//				System.out.println(treeWalk.getPathString()+"*****");
                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    Reader reader;
                    try {
//						System.out.println(treeWalk.getPathString());

//						if(treeWalk.getPathString().contains("Resources.Designer.cs"))
//						{
//							System.out.print("debug");
//						}
                        reader = new FileReader(f1.toString());
                        ITree curtree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

                        //TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
                        //analyzer.getTestFunctionList(curtree);
                        ConstructorInitialization ci = new ConstructorInitialization ();
                        map = ci.searchForConstructorInitialization(curtree);
                        //System.out.println("map:"+map);
//                        System.out.println(map);
                        //Copy to project map
                        for (String key : map.keySet()) {
                            if (!projtestfuncassertmap.containsKey(key)) {
                                projtestfuncassertmap.put(key, map.get(key));
                            }


                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
//        System.out.println(projtestfuncassertmap);
        return projtestfuncassertmap;
    }

    public Map<String, Boolean> getSleepyTest(String commitid) {

        //List<ClassFunction> classfunclist=new ArrayList<>();
        Map<String, Boolean> projtestfuncassertmap = new HashMap<>();
        Map<String, Boolean> map = new HashMap<>();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());
//				System.out.println(treeWalk.getPathString()+"*****");
                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    Reader reader;
                    try {
//						System.out.println(treeWalk.getPathString());

//						if(treeWalk.getPathString().contains("Resources.Designer.cs"))
//						{
//							System.out.print("debug");
//						}
                        reader = new FileReader(f1.toString());
                        ITree curtree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();
                        //TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
                        //analyzer.getTestFunctionList(curtree);
                        SleepyTest st = new SleepyTest ();
                        map = st.searchForSleepyTest(curtree);
                        //System.out.println("map:"+ map);
//                        System.out.println(map.size());
//                        System.out.println(map);
                        //Copy to project map
                        for (String key : map.keySet()) {
                            if (!projtestfuncassertmap.containsKey(key)) {
                                projtestfuncassertmap.put(key, map.get(key));
                            }
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    f1.delete();

                }
            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
//        System.out.println(projtestfuncassertmap);
        return projtestfuncassertmap;
    }

    public Map<String, Boolean> getEmptyTest(String commitid) {

        //List<ClassFunction> classfunclist=new ArrayList<>();
        Map<String, Boolean> projtestfuncassertmap = new HashMap<>();
        Map<String, Boolean> map = new HashMap<>();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());
//				System.out.println(treeWalk.getPathString()+"*****");
                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    Reader reader;
                    try {
//						System.out.println(treeWalk.getPathString());

//						if(treeWalk.getPathString().contains("Resources.Designer.cs"))
//						{
//							System.out.print("debug");
//						}
                        reader = new FileReader(f1.toString());
                        ITree curtree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

                        //TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
                        //analyzer.getTestFunctionList(curtree);
                        EmptyTest et = new EmptyTest ();
                        map = et.searchForEmptyTest(curtree);
                        //System.out.println("map:"+map);
//                        System.out.println(map);
                        //Copy to project map
                        for (String key : map.keySet()) {
                            if (!projtestfuncassertmap.containsKey(key)) {
                                projtestfuncassertmap.put(key, map.get(key));
                            }


                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
//        System.out.println(projtestfuncassertmap);
        return projtestfuncassertmap;
    }

    public Map<String, Boolean> getIgnoredTest(String commitid) {

        //List<ClassFunction> classfunclist=new ArrayList<>();
        Map<String, Boolean> projtestfuncassertmap = new HashMap<>();
        Map<String, Boolean> map = new HashMap<>();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());
//				System.out.println(treeWalk.getPathString()+"*****");
                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    Reader reader;
                    try {
//						System.out.println(treeWalk.getPathString());

//						if(treeWalk.getPathString().contains("Resources.Designer.cs"))
//						{
//							System.out.print("debug");
//						}
                        reader = new FileReader(f1.toString());
                        ITree curtree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

                        //TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
                        //analyzer.getTestFunctionList(curtree);
                        IgnoredTest it = new IgnoredTest ();
                        map = it.searchForIgnoredTest(curtree);
                        //System.out.println("msp:"+map);
//                        System.out.println(map.size());
//                        System.out.println(map);
                        //Copy to project map
                        for (String key : map.keySet()) {
                            if (!projtestfuncassertmap.containsKey(key)) {
                                projtestfuncassertmap.put(key, map.get(key));
                            }

                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    f1.delete();

                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
//        System.out.println(projtestfuncassertmap);
        return projtestfuncassertmap;
    }

    public Map<String, Boolean> getExceptionTest(String commitid) {

        //List<ClassFunction> classfunclist=new ArrayList<>();
        Map<String, Boolean> projtestfuncassertmap = new HashMap<>();
        Map<String, Boolean> map = new HashMap<>();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());
//				System.out.println(treeWalk.getPathString()+"*****");
                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    Reader reader;
                    try {
//						System.out.println(treeWalk.getPathString());

//						if(treeWalk.getPathString().contains("Resources.Designer.cs"))
//						{
//							System.out.print("debug");
//						}
                        reader = new FileReader(f1.toString());
                        ITree curtree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

                        //TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
                        //analyzer.getTestFunctionList(curtree);
                        ExceptionCatchingThrowing ec = new ExceptionCatchingThrowing ();
                        map = ec.searchForExceptionTest(curtree);
                        //System.out.println("map:"+map);
//                        System.out.println(map);
                        //Copy to project map
                        for (String key : map.keySet()) {
                            if (!projtestfuncassertmap.containsKey(key)) {
                                projtestfuncassertmap.put(key, map.get(key));
                            }


                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
//        System.out.println(projtestfuncassertmap);
        return projtestfuncassertmap;
    }

    public Map<String, Boolean> getUnknownTest(String commitid) {

        //List<ClassFunction> classfunclist=new ArrayList<>();
        Map<String, Boolean> projtestfuncassertmap = new HashMap<>();
        Map<String, Boolean> map = new HashMap<>();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());
//				System.out.println(treeWalk.getPathString()+"*****");
                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    Reader reader;
                    try {
//						System.out.println(treeWalk.getPathString());

//						if(treeWalk.getPathString().contains("Resources.Designer.cs"))
//						{
//							System.out.print("debug");
//						}
                        reader = new FileReader(f1.toString());
                        ITree curtree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

                        //TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
                        //analyzer.getTestFunctionList(curtree);
                        UnknownTest ut = new UnknownTest ();
                        map = ut.searchForUnknownTest(curtree);
                        //System.out.println("map"+map);
//                        System.out.println(map.size());
//                        System.out.println(map);
                        //Copy to project map
                        for (String key : map.keySet()) {
                            if (!projtestfuncassertmap.containsKey(key)) {
                                projtestfuncassertmap.put(key, map.get(key));
                            }


                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
//        System.out.println(projtestfuncassertmap);
        return projtestfuncassertmap;
    }

    public Map<String, Boolean> getRedundantAssert(String commitid) {

        //List<ClassFunction> classfunclist=new ArrayList<>();
        Map<String, Boolean> projtestfuncassertmap = new HashMap<>();
        Map<String, Boolean> mp = new HashMap<>();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());
//				System.out.println(treeWalk.getPathString()+"*****");
                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    Reader reader;
                    try {
//						System.out.println(treeWalk.getPathString());

//						if(treeWalk.getPathString().contains("Resources.Designer.cs"))
//						{
//							System.out.print("debug");
//						}
                        reader = new FileReader(f1.toString());
                        ITree curtree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

                        //TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
                        //analyzer.getTestFunctionList(curtree);
                        RedundantAssertion ra = new RedundantAssertion();
                        mp = ra.searchForRedundantAssertion(curtree);
                        //System.out.println("map:"+mp);
//                        System.out.println(map);
                        //Copy to project map
                        for (String key : mp.keySet()) {
                            if (!projtestfuncassertmap.containsKey(key)) {
                                projtestfuncassertmap.put(key, mp.get(key));
                            }


                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
//        System.out.println(projtestfuncassertmap);
        return projtestfuncassertmap;
    }

    public Map<String, Boolean> getDuplicateAssert(String commitid) {

        //List<ClassFunction> classfunclist=new ArrayList<>();
        Map<String, Boolean> projtestfuncassertmap = new HashMap<>();
        Map<String, Boolean> mp = new HashMap<>();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            // treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());
//				System.out.println(treeWalk.getPathString()+"*****");
                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    Reader reader;
                    try {
//						System.out.println(treeWalk.getPathString());

//						if(treeWalk.getPathString().contains("Resources.Designer.cs"))
//						{
//							System.out.print("debug");
//						}
                        reader = new FileReader(f1.toString());
                        ITree curtree = new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();
                        //TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
                        //analyzer.getTestFunctionList(curtree);
                        DuplicateAssert da = new DuplicateAssert();
                        mp = da.searchForDuplicateAssert(curtree);
                        //System.out.println("mp:"+mp);
//                        System.out.println(map.size());
//                        System.out.println(map);
                        //Copy to project map
                        for (String key : mp.keySet()) {
                            if (!projtestfuncassertmap.containsKey(key)) {
                                projtestfuncassertmap.put(key, mp.get(key));
                            }

                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
//        System.out.println(projtestfuncassertmap);
        return projtestfuncassertmap;
    }


    public Map<String, Boolean> getAllTestCases(String commitid) {

        Map<String, Boolean> projtestfuncassertmap = new HashMap<>();
        Map<String, Boolean> mp = new HashMap<>();
        try {
            ObjectId objectid = repository.resolve(commitid);
            RevCommit commit = rw.parseCommit(objectid);

            RevTree tree = commit.getTree();

            // TreeWalk treeWalk = new TreeWalk(repository);
            // treeWalk.addTree(tree);
            // treeWalk.setRecursive(false);
            // treeWalk.setPostOrderTraversal(false);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);
//        Map<String, String> projTestFuncMap = new HashMap<>();
//
//        try {
//            ObjectId objectId = repository.resolve(commitid);
//            RevCommit commit = rw.parseCommit(objectId);
//            RevTree tree = commit.getTree();
//
//            TreeWalk treeWalk = new TreeWalk(repository);
//            treeWalk.addTree(commit.getTree());
//            treeWalk.setRecursive(false);
//
//            while (treeWalk.next()) {
//                if (treeWalk.isSubtree()) {
//                    treeWalk.enterSubtree();
//                } else if (treeWalk.getPathString().endsWith(".cs")) {
//                    ObjectId fileObjectId = treeWalk.getObjectId(0);
//                    ObjectLoader loader = repository.open(fileObjectId);
//
//                    // Extract file name from the path string
//                    String fileName = treeWalk.getPathString().substring(treeWalk.getPathString().lastIndexOf("/") + 1);
//
//                    byte[] fileContent = loader.getBytes();
//                    String fileContentString = new String(fileContent);
//
//                    File tempFile = commitAnalyzingUtils.writeContentInFile("temp.cs", fileContentString);
//                    Reader reader = new FileReader(tempFile);
//
//                    try {
//                        ITree rootTree = new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();
//
//                        // Extract test cases with file and function name
//                        TestCaseExtractor testCaseExtractor = new TestCaseExtractor();
//                        Map<String, String> testFuncMap = testCaseExtractor.extractTestCasesWithFileName(rootTree, fileName);
//
//                        // Merge test cases into the project map
//                        projTestFuncMap.putAll(testFuncMap);
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//                        tempFile.delete();
//                        reader.close();
//                    }
//                }
//            }
//
//            treeWalk.reset();
//        } catch (Exception ex) {
//            System.out.print(ex.getMessage());
//        }
//
//        return projTestFuncMap;
//    }

            while (treeWalk.next()) {
                // System.out.println("found:" + treeWalk.getPathString());
//				System.out.println(treeWalk.getPathString()+"*****");
                if (treeWalk.isSubtree()) {
                    // System.out.println("dir: " + treeWalk.getPathString());
                    treeWalk.enterSubtree();
                } else if (treeWalk.getPathString().endsWith(".cs")) {

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);

                    // and then one can the loader to read the file
                    // loader.copyTo(System.out);

                    byte[] butestr = loader.getBytes();
                    String str = new String(butestr);
                    File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);


                    Reader reader;
                    try {
//						System.out.println(treeWalk.getPathString());

//						if(treeWalk.getPathString().contains("Resources.Designer.cs"))
//						{
//							System.out.print("debug");
//						}
                        reader = new FileReader(f1.toString());
                        ITree curtree = (ITree) new SrcmlUnityCsTreeGenerator().generate(reader).getRoot();

                        //TreeNodeAnalyzer analyzer=new TreeNodeAnalyzer();
                        //analyzer.getTestFunctionList(curtree);
                        TestCaseExtractor testCaseExtractor = new TestCaseExtractor();
                        mp = testCaseExtractor.searchFornosmells(curtree);
//                        System.out.println(map.size());
//                        System.out.println(map);
                        //Copy to project map
                        for (String key : mp.keySet()) {
                            if (!projtestfuncassertmap.containsKey(key)) {
                                projtestfuncassertmap.put(key, mp.get(key));
                            }


                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    f1.delete();


                }

            }
            treeWalk.reset();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
//        System.out.println(projtestfuncassertmap);
        return projtestfuncassertmap;
    }

}






