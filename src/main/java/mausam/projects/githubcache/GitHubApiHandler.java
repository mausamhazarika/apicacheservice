package mausam.projects.githubcache;

import java.io.StringReader;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import mausam.projects.githubcache.utils.Constants;
import mausam.projects.githubcache.utils.Utils;

/**
 * Singleton class responsible for calling GitHub API end points as well as the leader instance of this service
 * @author Mausam Hazarika
 *
 */
public class GitHubApiHandler {
	
	private Client gitClient;
	private Client proxyClient;// for cache end points that don't need auth
	private static GitHubApiHandler instance;
	
	private GitHubApiHandler() {
		//Only one client instance since it is expensive to create
		gitClient = ClientBuilder.newClient();
		HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.basic(System.getenv(Constants.GITHUB_USER), System.getenv(Constants.GITHUB_API_TOKEN));
		gitClient.register(authFeature);
		proxyClient = ClientBuilder.newClient();
		
	}
	
	public synchronized static GitHubApiHandler getInstance(){
		if (instance==null){
			instance = new GitHubApiHandler();
		}
		return instance;
	}
	
	public boolean checkCredentials(){
		String serviceUri = Constants.GITHUB_API_URL + Constants.PATH_SEPARATOR;
		WebTarget webTarget = this.gitClient.target(serviceUri);
		Response response = getResponse(webTarget);
		return (response.getStatus()==401)?false:true;
		
	}
	
	public String getRoot(String serviceUri, boolean isGit){
		Client client = isGit?this.gitClient:proxyClient;
		WebTarget webTarget = client.target(serviceUri + Constants.PATH_SEPARATOR);
		return sendRequest(webTarget);
	}
	
	public String getOrg(String serviceUri,String orgName, boolean isGit){
		Client client = isGit?this.gitClient:proxyClient;
		WebTarget webTarget = client.target(serviceUri + Constants.PATH_SEPARATOR+
				Constants.ORGS+Constants.PATH_SEPARATOR + orgName);
		return sendRequest(webTarget);
	}
	
	public String getRepo(String serviceUri, String orgName, String repo, boolean isGit){
		Client client = isGit?this.gitClient:proxyClient;
		WebTarget webTarget = null;
		if (isGit) {
			webTarget = client.target(serviceUri + Constants.PATH_SEPARATOR+
				Constants.REPOS + Constants.PATH_SEPARATOR + orgName + Constants.PATH_SEPARATOR + repo);
		}else {
			webTarget = client.target(serviceUri + Constants.PATH_SEPARATOR+
					Constants.ORGS + Constants.PATH_SEPARATOR + orgName + Constants.PATH_SEPARATOR + 
					Constants.REPOS + Constants.PATH_SEPARATOR + repo);
		}
		return sendRequest(webTarget);
	}
	
	public String getOrgRepos(String serviceUri,String orgName,boolean isGit){
		String requestUri = serviceUri + Constants.PATH_SEPARATOR+
				Constants.ORGS+Constants.PATH_SEPARATOR + orgName + Constants.PATH_SEPARATOR + 
				Constants.REPOS;
		if (!isGit) {
			WebTarget webTarget = this.proxyClient.target(requestUri);
			return sendRequest(webTarget);
		}else{
			return sendGitHubRequestFollowLinks(requestUri);
		}
	}
	
	public String getOrgMembers(String serviceUri,String orgName, boolean isGit){
		String requestUri = serviceUri + Constants.PATH_SEPARATOR+
				Constants.ORGS+Constants.PATH_SEPARATOR + orgName + Constants.PATH_SEPARATOR + 
				Constants.MEMBERS;
		if (!isGit){
			WebTarget webTarget = this.proxyClient.target(requestUri);
			return sendRequest(webTarget);
		}else {
			return sendGitHubRequestFollowLinks(requestUri);
		}
	}
	
	public String getMemberOrgs(String serviceUri,String login, boolean isGit){
		String requestUri = serviceUri + Constants.PATH_SEPARATOR+
				Constants.USERS +Constants.PATH_SEPARATOR + login + Constants.PATH_SEPARATOR + 
				Constants.ORGS;
		if (!isGit){
			WebTarget webTarget = this.proxyClient.target(requestUri);
			return sendRequest(webTarget);
		}else {
			return sendGitHubRequestFollowLinks(requestUri);
		}
	}
	
	public String getMember(String serviceUri,String login,boolean isGit){
		Client client = isGit?this.gitClient:proxyClient;
		WebTarget webTarget = client.target(serviceUri + Constants.PATH_SEPARATOR+
				Constants.USERS +Constants.PATH_SEPARATOR + login);
		return sendRequest(webTarget);
	}
	
	public boolean getHealthCheck(String serviceUri){
		WebTarget webTarget = this.proxyClient.target(serviceUri + Constants.PATH_SEPARATOR+
				Constants.HEALTH_CHECK);
		Invocation.Builder invocationBuilder =	webTarget.request(MediaType.TEXT_PLAIN);
		Response resp = invocationBuilder.get();
		return (resp.getStatus()==200);
	}
	
	private String sendRequest(WebTarget target){
		Response response = getResponse(target);
		if (response.getStatus()==200){
			String resp = response.readEntity(String.class);
			return resp;
		}else {
			Utils.logger.error(target.getUri() + " returned http status " + response.getStatus());
			return null;
		}
	}
	
	private Response getResponse(WebTarget target){
		Invocation.Builder invocationBuilder =	target.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();
		return response;
	}
	
	private String sendGitHubRequestFollowLinks(String requestUri){
		ArrayList<String> chunkedResponses = new ArrayList<>();
		String nextRequestUri = requestUri;
		while (nextRequestUri!=null){
			WebTarget webTarget = this.gitClient.target(nextRequestUri);
			Response response = getResponse(webTarget);
			if (response.getStatus()==200){
				String linkHeader = response.getHeaderString(Constants.HTTP_RESPONSE_HEADER_LINK);
				String resp = response.readEntity(String.class);
				chunkedResponses.add(resp);
				nextRequestUri = fetchNextUriFromLinkHeader(linkHeader);
			}
		}
		return flattenChunkedResponse(chunkedResponses);
	}
	
	
	private String flattenChunkedResponse(ArrayList<String> chunkedResponses){
		
		ArrayList<JsonArray> listOfArrays = new ArrayList<>();
		int size = chunkedResponses.size();
		for(int i=0;i<size;i++){
			JsonReader jsonReader = Json.createReader(new StringReader(chunkedResponses.get(i)));
			JsonArray nextArr = jsonReader.readArray();
			jsonReader.close();
			listOfArrays.add(nextArr);
		}
		JsonArray flattedJsonArray = concatJsonArrays(listOfArrays);
		return flattedJsonArray.toString();
	
	}
	
	private JsonArray concatJsonArrays(ArrayList<JsonArray> listOfJsonArrays){
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		int n = listOfJsonArrays.size();
		for(int i=0;i<n;i++){
			JsonArray arr = listOfJsonArrays.get(i); 
			int size = arr.size();
			for(int j=0;j<size;j++){
				jsonArrayBuilder.add(arr.get(j));
			}
		}
		return jsonArrayBuilder.build();
	}
	
	private String fetchNextUriFromLinkHeader(String linkHeader){
		if (linkHeader==null || linkHeader.isEmpty()) return null;
		int idx = linkHeader.indexOf("rel=\"next\"");
		if (idx<0){
			return null;
		}else{
			String subStr = linkHeader.substring(0, idx);
			int leftBracket = subStr.indexOf('<');
			int rightBracket = subStr.indexOf('>');
			if (leftBracket>=0 && rightBracket>=0){
				return subStr.substring(leftBracket+1,rightBracket);
			}
		}
		return null;
	}
	

}
