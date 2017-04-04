package mausam.projects.githubcache.caching;

import mausam.projects.githubcache.GitHubApiHandler;
import mausam.projects.githubcache.StorageHandler;
import mausam.projects.githubcache.utils.Constants;
import mausam.projects.githubcache.utils.Utils;

public class GitHubOrgCacher {

			
	private static GitHubOrgCacher instance;
	private StorageHandler store;
	private GitHubApiHandler ghApi;
	
	
	private GitHubOrgCacher() {
		store = StorageHandler.getInstance();
		ghApi = GitHubApiHandler.getInstance();
	}
	
	
	public static synchronized GitHubOrgCacher getInstance(){
		if (instance==null){
			instance = new GitHubOrgCacher();
		}
		return instance;
	}
	
	public synchronized void refreshCache(){
		/**
		 * Read from GitHub API
		 */
	 	 String response = ghApi.getOrg(Constants.GITHUB_API_URL, Constants.ORG_NETFLIX,true);
		 // Save org data
	 	 if (response!=null){
	 		 String orgKey = Utils.getOrgKey(Constants.ORG_NETFLIX);
	 		 store.storeKey(orgKey.toString(), response);
	 	 }
	}

	
}
