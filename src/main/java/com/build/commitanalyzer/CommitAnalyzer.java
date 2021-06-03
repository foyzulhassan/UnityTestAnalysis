package com.build.commitanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

//import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import com.build.analyzer.entity.CommitChange;
import com.config.Config;
import com.csharp.astgenerator.SrcmlUnityCsMetaDataGenerator;
import com.csharp.astgenerator.SrcmlUnityCsTreeGenerator;
import com.csharp.diff.CSharpDiffGenerator;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.EditScriptGenerator;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;

//import edu.utsa.data.DataResultsHolder;
//import edu.utsa.data.DataStatsHolder;
//import edu.utsa.main.MainClass;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.gen.srcml.SrcmlCsTreeGenerator;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.matchers.heuristic.gt.CompleteBottomUpMatcher;
import com.github.gumtreediff.tree.ITree;
import com.unity.callgraph.ClassFunction;
import com.unity.entity.PerfFixData;

import org.apache.commons.io.IOUtils;

/**
 * 
 * @author Foyzul Hassan
 *
 * 
 */

public class CommitAnalyzer {

	/** Various methods encapsulating methods to treats Git and commits datas */
	private CommitAnalyzingUtils commitAnalyzingUtils;

	/** All the statistical datas (number of faulty commit, actions, etc) */
	private DataStatsHolder statsHolder;

	/** File managing object for tables */
	private DataResultsHolder resultsHolder;

	/** Name of the project */
	private String project;

	/** Owner of the project (necessary for Markdown parsing) */
	private String projectOwner;

	/** Path to the directory */
	private String directoryPath;

	/** Repository object, representing the directory */
	private Repository repository;

	/** Git entity to treat with the Repository data */
	private Git git;

	/** Revision walker from JGit */
	private RevWalk rw;

	private CommitChange commitChangeTracker;

	private String gradleChanges;

	private String gitUrl;

	/** Classic constructor */
	public CommitAnalyzer(String projectOwner, String project) throws Exception {
		this.projectOwner = projectOwner;
		this.project = project;

		directoryPath = Config.repoDir + project + "/.git";

		commitAnalyzingUtils = new CommitAnalyzingUtils();
		statsHolder = new DataStatsHolder();
		repository = commitAnalyzingUtils.setRepository(directoryPath);
		git = new Git(repository);
		rw = new RevWalk(repository);
		this.commitChangeTracker = new CommitChange();
		this.gradleChanges = "";
	}

	/** Classic constructor */
	public CommitAnalyzer(String projectOwner, String project, String giturl) throws Exception {
		this.projectOwner = projectOwner;
		this.project = project;

		directoryPath = Config.repoDir + project + "/.git";

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

		List<ClassFunction> classfunclist=new ArrayList<>();
		
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
				}

				else if (treeWalk.getPathString().endsWith(".cs")) {
					ObjectId objectId = treeWalk.getObjectId(0);
					ObjectLoader loader = repository.open(objectId);

					// and then one can the loader to read the file
					// loader.copyTo(System.out);

					byte[] butestr = loader.getBytes();
					String str = new String(butestr);
					File f1 = commitAnalyzingUtils.writeContentInFile("g1.cs", str);
					CSharpDiffGenerator diffgen = new CSharpDiffGenerator();
					ClassFunction clsfunc=diffgen.getClassFunction(f1);
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

}
