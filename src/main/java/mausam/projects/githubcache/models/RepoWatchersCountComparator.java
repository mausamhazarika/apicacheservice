package mausam.projects.githubcache.models;

import java.io.Serializable;
import java.util.Comparator;

public class RepoWatchersCountComparator implements Comparator<Repo>,Serializable{

	public RepoWatchersCountComparator() {
		// TODO Auto-generated constructor stub
	}

	public int compare(Repo r1, Repo r2) {
		return r2.getWatchersCount() - r1.getWatchersCount();
	}

}
