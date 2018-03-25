package de.wieczorek.rss.core;

import java.io.IOException;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

public class DeploymentMain {

	public static void main(String[] args)
			throws InvalidRemoteException, TransportException, GitAPIException, MavenInvocationException, IOException {
		Updater updater = new Updater();
		updater.start();
	}

}
