package mausam.projects.githubcache.caching;

import mausam.projects.githubcache.GitHubApiHandler;
import mausam.projects.githubcache.StorageHandler;
import mausam.projects.githubcache.utils.Constants;

public class GitHubRootCacher {

	private StorageHandler store;
	private static GitHubRootCacher instance;
	private GitHubApiHandler ghApi;
	
	
	private GitHubRootCacher() {
		store = StorageHandler.getInstance();
		ghApi = GitHubApiHandler.getInstance();
	}
	
	
	public static synchronized GitHubRootCacher getInstance(){
		if (instance==null){
			instance = new GitHubRootCacher();
		}
		return instance;
	}
	
	public synchronized void refreshCache(){
		/**
		 * Read from GitHub API
		 */
		String response = ghApi.getRoot(Constants.GITHUB_API_URL, true);
		 // Save the root / level data for default retrieval
		if (response!=null){
			store.storeKey(Constants.PATH_SEPARATOR, response);
		}
	}
}
