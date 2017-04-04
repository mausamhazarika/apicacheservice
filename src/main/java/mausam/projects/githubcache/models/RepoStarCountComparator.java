package mausam.projects.githubcache.models;

import java.io.Serializable;
import java.util.Comparator;

public class RepoStarCountComparator implements Comparator<Repo>,Serializable {

	public RepoStarCountComparator() {
		// TODO Auto-generated constructor stub
	}

	public int compare(Repo r1, Repo r2) {
		return r2.getStarsCount() - r1.getStarsCount();
	}

}
