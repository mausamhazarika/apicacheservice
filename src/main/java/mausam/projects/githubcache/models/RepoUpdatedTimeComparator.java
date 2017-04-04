package mausam.projects.githubcache.models;

import java.io.Serializable;
import java.util.Comparator;

public class RepoUpdatedTimeComparator implements Comparator<Repo>,Serializable {

	public int compare(Repo r1, Repo r2) {
		long diff =  r2.getUpdatedTime().getTime() - r1.getUpdatedTime().getTime();
		if (diff>0) return 1;
		else if (diff<0) return -1;
		return 0;
	}

}
