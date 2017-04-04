package mausam.projects.githubcache.models;

import java.io.Serializable;
import java.util.Comparator;

public class RepoForkCountComparator implements Comparator<Repo>,Serializable {

	public int compare(Repo r1, Repo r2) {
		return  r2.getForksCount() - r1.getForksCount();
	}
	
	

}
