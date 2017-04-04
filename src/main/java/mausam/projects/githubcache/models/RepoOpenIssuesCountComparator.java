package mausam.projects.githubcache.models;

import java.io.Serializable;
import java.util.Comparator;

public class RepoOpenIssuesCountComparator implements Comparator<Repo>,Serializable {

	public RepoOpenIssuesCountComparator() {
	}

	public int compare(Repo r1, Repo r2) {
		return r2.getOpenIssuesCount() - r1.getOpenIssuesCount();
	}

}
