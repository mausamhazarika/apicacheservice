package mausam.projects.githubcache.caching;

import mausam.projects.githubcache.GitHubApiHandler;
import mausam.projects.githubcache.StorageHandler;
import mausam.projects.githubcache.leaderelection.LeaderElector;
import mausam.projects.githubcache.utils.Constants;
import mausam.projects.githubcache.utils.Utils;

/**
 * Class responsible for periodically refreshing the Cache by talking to the GitHub API 
 * @author Mausam Hazarika
 *
 */
public class CacheHandler implements Runnable{

	private static CacheHandler instance;
	private boolean isInitialized = false;
	private StorageHandler store;
	private GitHubApiHandler ghApi;
	private LeaderElector leaderElector;
	private GitHubRootCacher rootCacher;
	private GitHubOrgCacher orgCacher;
	private GitHubMemberCacher memberCacher;
	private GitHubRepoCacher repoCacher;
	
	
	private CacheHandler() {
		this.store = StorageHandler.getInstance();
		this.ghApi = GitHubApiHandler.getInstance();
		this.leaderElector = LeaderElector.getInstance();
		this.rootCacher = GitHubRootCacher.getInstance();
		this.orgCacher = GitHubOrgCacher.getInstance();
		this.memberCacher = GitHubMemberCacher.getInstance();
		this.repoCacher = GitHubRepoCacher.getInstance();
	}
	
	public synchronized static CacheHandler getInstance(){
		if (instance==null){
			instance = new CacheHandler();
		}
		return instance;
	}
	
	/**
	 * Run this as a thread that continually refreshes cache at the configured interval
	 */
	public void run(){
		try{
			while (true){
				refreshCache();
				Thread.sleep(Constants.INTERVAL);
			}
		}catch(InterruptedException ie){
			Utils.logger.debug("CacheHandler interrupted!");
		}
		
	}
	
	public boolean checkGitHubCredentials(){
		return this.ghApi.checkCredentials();
	}
	
	public boolean checkStorageConnection(){
		return this.store.checkConnection();
	}
	
	private void refreshCache(){
		//Acquire lock, if success you are a leader and you can refresh cache
		if (this.leaderElector.acquireLock(Constants.SERVICE_URI)){
			Utils.logger.info(" Refreshing cache at " + Constants.SERVICE_URI);
			this.repoCacher.refreshCache();
			this.orgCacher.refreshCache();
			this.memberCacher.refreshCache();
			this.rootCacher.refreshCache();
			if (!this.isInitialized){
				this.isInitialized = true;
			}
		}else {
			//check leader's health check before declaring service readiness
			if (!this.isInitialized) {
				String leaderUri = this.leaderElector.getLeaderURI();
				boolean isLeaderReady = this.ghApi.getHealthCheck(leaderUri);
				while (!isLeaderReady){
					try {
						Thread.sleep(100);
					}catch(InterruptedException ie){
						Utils.logger.debug("Thread interrupted while waiting for leader health check");
					}
					isLeaderReady = this.ghApi.getHealthCheck(leaderUri);
				}
				this.isInitialized = true;
			}
			
		}
		
	}
	
	public boolean isCacheReady(){
		return this.isInitialized;
	}
	
	
	public String getAndUpdateCacheNewRepo(String orgName,String repoName){
		String newRepoJson = null;
		if (this.leaderElector.acquireLock(Constants.SERVICE_URI)){
			Utils.logger.info("Updating cache with new repo " + orgName + "/" + repoName);
			newRepoJson = this.ghApi.getRepo(Constants.GITHUB_API_URL,orgName, repoName, true);
			if (newRepoJson!=null){
				this.store.storeKey(Utils.getRepoKey(orgName, repoName), newRepoJson);
			}
		}else {
			//call the leader
			Utils.logger.info("Received request for new repo " + orgName + "/" + repoName + ", calling leader...");
			String leaderUri = this.leaderElector.getLeaderURI();
			newRepoJson = this.ghApi.getRepo(leaderUri, orgName, repoName, false);
		}
		return newRepoJson;
	}
	
	public String getAndUpdateCacheNewMember(String login){
		String newMemberJson = null;
		if (this.leaderElector.acquireLock(Constants.SERVICE_URI)){
			Utils.logger.info("Updating cache with new member " + login);
			newMemberJson = this.ghApi.getMember(Constants.GITHUB_API_URL,login, true);
			if (newMemberJson!=null){
				this.store.storeKey(Utils.getMemberKey(login), newMemberJson);
			}
		}else {
			//call the leader
			Utils.logger.info("Received request for new member " + login + ", calling leader...");
			String leaderUri = this.leaderElector.getLeaderURI();
			newMemberJson = this.ghApi.getMember(leaderUri, login, false);
		}
		return newMemberJson;
	}
	
	public String getAndUpdateCacheNewOrg(String orgName){
		String newOrgJson = null;
		if (this.leaderElector.acquireLock(Constants.SERVICE_URI)){
			Utils.logger.info("Updating cache with new org " + orgName);
			newOrgJson = this.ghApi.getOrg(Constants.GITHUB_API_URL, orgName, true);
			if (newOrgJson!=null){
				this.store.storeKey(Utils.getOrgKey(orgName), newOrgJson);
			}
		}else {
			//call the leader
			Utils.logger.info("Received request for new org " + orgName + ", calling leader...");
			String leaderUri = this.leaderElector.getLeaderURI();
			newOrgJson = this.ghApi.getMember(leaderUri, orgName, false);
		}
		return newOrgJson;
	}
	
	public String getAndUpdateCacheNewOrgRepos(String orgName){
		String newOrgReposJson = null;
		if (this.leaderElector.acquireLock(Constants.SERVICE_URI)){
			Utils.logger.info("Updating cache with repo list for new org " + orgName);
			newOrgReposJson = this.ghApi.getOrgRepos(Constants.GITHUB_API_URL,orgName, true);
			if (newOrgReposJson!=null){
				this.store.storeKey(Utils.getOrgReposKey(orgName), newOrgReposJson);
			}
		}else {
			//call the leader
			Utils.logger.info("Received request for repo list of new org " + orgName + ", calling leader...");
			String leaderUri = this.leaderElector.getLeaderURI();
			newOrgReposJson = this.ghApi.getOrgRepos(leaderUri, orgName,false);
			
		}
		return newOrgReposJson;
	}
	
	public String getAndUpdateCacheNewOrgMembers(String orgName){
		String newOrgMembersJson = null;
		if (this.leaderElector.acquireLock(Constants.SERVICE_URI)){
			Utils.logger.info("Updating cache with members for new org " + orgName);
			newOrgMembersJson = this.ghApi.getOrgMembers(Constants.GITHUB_API_URL,orgName, true);
			if (newOrgMembersJson!=null){
				this.store.storeKey(Utils.getOrgMembersKey(orgName), newOrgMembersJson);
			}
		}else {
			//call the leader
			Utils.logger.info("Received request for members of new org " + orgName + ", calling leader...");
			String leaderUri = this.leaderElector.getLeaderURI();
			newOrgMembersJson = this.ghApi.getOrgMembers(leaderUri, orgName, false);
		}
		return newOrgMembersJson;
	}
	
	public String getAndUpdateCacheNewMemberOrgs(String login){
		String newMemberOrgsJson = null;
		if (this.leaderElector.acquireLock(Constants.SERVICE_URI)){
			Utils.logger.info("Updating cache with orgs for new member " + login);
			newMemberOrgsJson = this.ghApi.getMemberOrgs(Constants.GITHUB_API_URL,login, true);
			if (newMemberOrgsJson!=null){
				this.store.storeKey(Utils.getMemberOrgsKey(login), newMemberOrgsJson);
			}
		}else {
			//call the leader
			Utils.logger.info("Received request for orgs for new member " + login + ", calling leader...");
			String leaderUri = this.leaderElector.getLeaderURI();
			newMemberOrgsJson = this.ghApi.getMemberOrgs(leaderUri, login, false);
		}
		return newMemberOrgsJson;
	}

}
