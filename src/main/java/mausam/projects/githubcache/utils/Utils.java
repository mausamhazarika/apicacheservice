package mausam.projects.githubcache.utils;

import javax.json.Json;
import javax.json.JsonObject;

import org.apache.log4j.Logger;

public final class Utils {
	
	public static final Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

	public static String getRepoKey(String orgName, String repoName){
		StringBuilder key = new StringBuilder();
		 key.append(Constants.REPO_PREFIX);
		 key.append(orgName);
		 key.append("-");
		 key.append(repoName);
		 return key.toString();
	}
	
	public static String getOrgKey(String orgName){
		StringBuilder key = new StringBuilder();
		key.append(Constants.ORG_PREFIX);
		key.append(orgName);
		return key.toString();
	}
	
	public static String getOrgReposKey(String orgName){
		StringBuilder key = new StringBuilder();
		key.append(Constants.ORG_REPOS_PREFIX);
		key.append(orgName);
		return key.toString();
	}
	
	public static String getOrgMembersKey(String orgName){
		StringBuilder key = new StringBuilder();
		key.append(Constants.ORG_MEMBERS_PREFIX);
		key.append(orgName);
		return key.toString();
	}
	
	public static String getMemberOrgsKey(String login){
		StringBuilder key = new StringBuilder();
		key.append(Constants.MEMBER_ORGS_PREFIX);
		key.append(login);
		return key.toString();
	}
	
	public static String getMemberKey(String login){
		StringBuilder key = new StringBuilder();
		key.append(Constants.MEMBER_PREFIX);
		key.append(login);
		return key.toString();
	}
	
	public static String getEmptyJsonResponse(){
		JsonObject obj = Json.createObjectBuilder().add("error", "Unable to fetch data from GitHub").build();
		return obj.toString();
	}

}
