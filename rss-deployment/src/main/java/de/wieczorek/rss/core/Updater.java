package de.wieczorek.rss.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;

public class Updater {

    private ScheduledExecutorService executor;

    public void start() {
	if (executor == null || executor.isShutdown()) {
	    executor = Executors.newScheduledThreadPool(1);
	}
	executor.execute(() -> updateLoop());

    }

    private void updateLoop() {
	try {
	    updateApps();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	executor.schedule(() -> updateLoop(), 60, TimeUnit.MINUTES);
    }

    private void updateApps() throws IOException, GitAPIException, WrongRepositoryStateException,
	    InvalidConfigurationException, InvalidRemoteException, CanceledException, RefNotFoundException,
	    RefNotAdvertisedException, NoHeadException, TransportException, MavenInvocationException {
	File repoDir = new File("repo");
	File targetDir = new File("repoTgt");
	File newAppTempDir = new File("repoTmp");
	Files.createDirectories(repoDir.toPath());
	Files.createDirectories(targetDir.toPath());
	boolean hasToBeBuilt = updateRepository(repoDir);

	if (hasToBeBuilt || !newAppTempDir.exists()) {
	    buildNewApps();
	    copyAppsToTempDir(repoDir, newAppTempDir);
	}
	Map<String, AppMetadata> newAppVersions = getVersionsOfApps(newAppTempDir);
	System.out.println("new Apps: " + newAppVersions.keySet());
	Map<String, AppMetadata> oldAppVersions = getVersionsOfApps(targetDir);
	System.out.println("old Apps: " + oldAppVersions.keySet());
	List<File> appsToInstall = newAppVersions.values().stream()
		.filter(app -> !oldAppVersions.containsKey(app.appName)).map(app -> app.jarFile)
		.collect(Collectors.toList());

	System.out.println("new apps to install: " + appsToInstall);

	List<File> appsToUpdate = newAppVersions.values().stream()
		.filter(app -> oldAppVersions.containsKey(app.appName)
			&& app.version.compareTo(oldAppVersions.get(app.appName).version) > 0)
		.map(app -> app.jarFile).collect(Collectors.toList());

	System.out.println("apps to update: " + appsToUpdate);

	List<File> appsToCopy = new ArrayList<>(appsToInstall);
	appsToCopy.addAll(appsToUpdate);
	System.out.println("Copying " + appsToCopy);

	killRunningProcesses(targetDir, appsToCopy);

	copyToDir(targetDir, appsToCopy);

	startApps(targetDir, appsToCopy);

	deleteFile(newAppTempDir);
    }

    void deleteFile(File f) throws IOException {
	if (f.isDirectory()) {
	    for (File c : f.listFiles())
		deleteFile(c);
	}
	if (!f.delete())
	    throw new FileNotFoundException("Failed to delete file: " + f);
    }

    private void startApps(File targetDir, List<File> appsToCopy) {
	appsToCopy.forEach(app -> {
	    PrintWriter writer;
	    try {
		System.out.println("Starting app " + app.getName());
		Process myProcess = new ProcessBuilder("java", "-Xmx32m", "-Xms1m", "-XX:ParallelGCThreads=1", "-jar",
			app.getName()).directory(targetDir).start();
		writer = new PrintWriter(new File(targetDir, app.getName()) + ".pid", "UTF-8");
		writer.println("" + myProcess.pid());
		writer.close();
	    } catch (FileNotFoundException | UnsupportedEncodingException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	});
    }

    private void copyToDir(File targetDir, List<File> appsToCopy) {
	appsToCopy.stream().forEach(file -> {
	    try {
		Files.copy(file.toPath(), new File(targetDir, file.getName()).toPath(),
			StandardCopyOption.REPLACE_EXISTING);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	});
    }

    private void killRunningProcesses(File targetDir, List<File> appsToCopy) {
	appsToCopy.forEach(app -> {
	    try {
		if (new File(app.getName() + ".pid").exists()) {
		    int pid = Integer.parseInt(Files.readAllLines(new File(app.getName() + ".pid").toPath()).get(0));
		    Process myProcess = new ProcessBuilder("kill", pid + "").directory(targetDir).start();
		    myProcess.waitFor();
		    Files.delete(new File(app.getName() + ".pid").toPath());
		}
	    } catch (FileNotFoundException | UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	});
    }

    private void copyAppsToTempDir(File repoDir, File newAppTempDir) throws IOException {
	Files.createDirectories(newAppTempDir.toPath());
	File[] filenames = repoDir.listFiles(new FilenameFilter() {
	    @Override
	    public boolean accept(File dir, String name) {
		return new File(dir, name).isDirectory() && name.startsWith("rss-") && !name.equals("rss-core")
			&& !name.equals("rss-deployment") && !name.endsWith("presentation");
	    }
	});

	FilenameFilter filter = new FilenameFilter() {
	    @Override
	    public boolean accept(File dir, String name) {
		return !name.startsWith("original") && name.endsWith(".jar");
	    }
	};

	List<File> jars = new ArrayList<>();
	Arrays.asList(filenames).stream().map(file -> new File(file, "target")).forEach(file -> {
	    jars.addAll(Arrays.asList(file.listFiles(filter) != null ? file.listFiles(filter) : new File[0]));
	});
	jars.forEach(file -> {
	    try {
		Files.copy(file.toPath(), new File(newAppTempDir, file.getName()).toPath(),
			StandardCopyOption.REPLACE_EXISTING);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	});
    }

    private boolean updateRepository(File repoDir) throws GitAPIException, WrongRepositoryStateException,
	    InvalidConfigurationException, InvalidRemoteException, CanceledException, RefNotFoundException,
	    RefNotAdvertisedException, NoHeadException, TransportException, IOException {
	boolean hasToBeBuilt = false;

	if (RepositoryCache.FileKey.isGitRepository(repoDir.toPath().resolve(".git").toFile(), FS.DETECTED)) {
	    System.out.println("found existing git repository");
	    PullResult fetchResult = Git.open(new File("repo")).pull().call();
	    if (fetchResult.getMergeResult().getMergeStatus() != MergeStatus.ALREADY_UP_TO_DATE) {
		hasToBeBuilt = true;
		System.out.println("Found changes ... re-building");
	    } else {
		System.out.println("Found no changes ... not building");
	    }

	} else {
	    System.out.println("directory is not a git repository... cloning");
	    Git.cloneRepository().setURI("https://github.com/DanielWieczorek/RSS-Grabber.git").setDirectory(repoDir)
		    .call();
	    hasToBeBuilt = true;
	}
	return hasToBeBuilt;
    }

    private void buildNewApps() throws MavenInvocationException {
	InvocationRequest request = new DefaultInvocationRequest();

	request.setPomFile(new File("repo/pom.xml"));
	request.setGoals(Arrays.asList("clean", "package"));

	Invoker invoker = new DefaultInvoker();
	invoker.execute(request);
    }

    private Map<String, AppMetadata> getVersionsOfApps(File repoDir) {
	Map<String, AppMetadata> newApps = new HashMap<>();

	File[] subDirs = repoDir.listFiles();
	if (subDirs != null) {
	    Arrays.asList(subDirs).stream().filter(file -> file.getName().endsWith(".jar")).forEach(file -> {
		try (JarFile jarFile = new JarFile(file)) {
		    Manifest m = jarFile.getManifest();
		    System.out.println(m.getMainAttributes().getValue("Implementation-Title") + ": "
			    + m.getMainAttributes().getValue("Implementation-Version"));

		    AppMetadata data = new AppMetadata();
		    data.jarFile = file;
		    data.appName = m.getMainAttributes().getValue("Implementation-Title");
		    data.version = m.getMainAttributes().getValue("Implementation-Version");
		    if (data.appName != null) {
			newApps.put(m.getMainAttributes().getValue("Implementation-Title"), data);
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    });
	}
	return newApps;
    }

    private class AppMetadata {
	String appName;
	File jarFile;
	String version;
    }

}
