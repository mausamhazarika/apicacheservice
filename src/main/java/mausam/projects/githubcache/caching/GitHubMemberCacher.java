package mausam.projects.githubcache.caching;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import mausam.projects.githubcache.GitHubApiHandler;
import mausam.projects.githubcache.StorageHandler;
import mausam.projects.githubcache.models.Member;
import mausam.projects.githubcache.utils.Constants;
import mausam.projects.githubcache.utils.Utils;

public class GitHubMemberCacher {

	
	private static GitHubMemberCacher instance;
	private StorageHandler store;
	private GitHubApiHandler ghApi;
	
	
	private GitHubMemberCacher() {
		this.store = StorageHandler.getInstance();
		this.ghApi = GitHubApiHandler.getInstance();
	}
	
	
	public static synchronized GitHubMemberCacher getInstance(){
		if (instance==null){
			instance = new GitHubMemberCacher();
		}
		return instance;
	}
	
	public synchronized void refreshCache(){
		/**
		 * Read from GitHub API
		 */
		 // Save list of members for default retrieval of all members
		 String response = this.ghApi.getOrgMembers(Constants.GITHUB_API_URL, Constants.ORG_NETFLIX, true);
		 if (response!=null){
			 String orgMembersKey = Utils.getOrgMembersKey(Constants.ORG_NETFLIX);
			 store.storeKey(orgMembersKey, response);
			 
			 JsonReader jsonReader = Json.createReader(new StringReader(response));
			 JsonArray memberList = jsonReader.readArray();
			 jsonReader.close();
			 
			 if (memberList!=null && memberList.size()>0){
				 int size = memberList.size();
				 for(int i=0;i<size;i++){
					 JsonObject jsonMember = memberList.getJsonObject(i);
					 Member member = new Member(jsonMember);
					 String memberKey = Utils.getMemberKey(member.getLogin());
					 // Store each Member's JSON in storage
					 store.storeKey(memberKey, jsonMember.toString());
				 }
				 
			 }
		 }
		
	}

}

