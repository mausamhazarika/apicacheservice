package mausam.projects.githubcache.caching;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.Base64;
import java.util.PriorityQueue;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;

import mausam.projects.githubcache.GitHubApiHandler;
import mausam.projects.githubcache.StorageHandler;
import mausam.projects.githubcache.models.Repo;
import mausam.projects.githubcache.models.RepoForkCountComparator;
import mausam.projects.githubcache.models.RepoOpenIssuesCountComparator;
import mausam.projects.githubcache.models.RepoStarCountComparator;
import mausam.projects.githubcache.models.RepoUpdatedTimeComparator;
import mausam.projects.githubcache.models.RepoWatchersCountComparator;
import mausam.projects.githubcache.models.SerializationException;
import mausam.projects.githubcache.utils.Constants;
import mausam.projects.githubcache.utils.Utils;

public class GitHubRepoCacher {
	
	
	
	private PriorityQueue<Repo> forksView;
	private PriorityQueue<Repo> lastUpdatedView;
	private PriorityQueue<Repo> openIssuesView;
	private PriorityQueue<Repo> starsView;
	private PriorityQueue<Repo> watchersView;
	private StorageHandler store;
	private static GitHubRepoCacher instance;
	private GitHubApiHandler ghApi;
	
	private GitHubRepoCacher() {
		forksView = new PriorityQueue<Repo>(new RepoForkCountComparator());
		lastUpdatedView = new PriorityQueue<Repo>(new RepoUpdatedTimeComparator());
		openIssuesView = new PriorityQueue<Repo>(new RepoOpenIssuesCountComparator());
		starsView = new PriorityQueue<Repo>(new RepoStarCountComparator());
		watchersView = new PriorityQueue<Repo>(new RepoWatchersCountComparator());
		store = StorageHandler.getInstance();
		ghApi = GitHubApiHandler.getInstance();
	}
	
	public static synchronized GitHubRepoCacher getInstance(){
		if (instance==null){
			instance = new GitHubRepoCacher();
		}
		return instance;
	}
	
	public synchronized void refreshCache(){
		try{
		/**
		 * Read from GitHub API
		 */
		 String response = ghApi.getOrgRepos(Constants.GITHUB_API_URL, Constants.ORG_NETFLIX, true);
		 // Save list of repos for default retrieval of all repos
		 if (response!=null){
			 String orgReposKey = Utils.getOrgReposKey(Constants.ORG_NETFLIX);
			 store.storeKey(orgReposKey, response);
			 JsonReader jsonReader = Json.createReader(new StringReader(response));
			 JsonArray repoList = jsonReader.readArray();
			 jsonReader.close();
		 
			 if (repoList!=null && repoList.size()>0){
				 int size = repoList.size();
				 for(int i=0;i<size;i++){
					 JsonObject jsonRepo = repoList.getJsonObject(i);
					 Repo repo = new Repo(jsonRepo);
					 String repoKey = Utils.getRepoKey(Constants.ORG_NETFLIX, repo.getName());
					 // Store each Repo's JSON in storage
					 store.storeKey(repoKey, jsonRepo.toString());
					 // Store the serialized priority queues for fast retrieval of views
					 forksView.add(repo);
					 lastUpdatedView.add(repo);
					 openIssuesView.add(repo);
					 starsView.add(repo);
					 watchersView.add(repo);
				 }
				 String forksViewSer = serializePriorityQueue(forksView);
				 String lastUpdatedViewSer = serializePriorityQueue(lastUpdatedView);
				 String openIssuesViewSer = serializePriorityQueue(openIssuesView);
				 String starsViewSer = serializePriorityQueue(starsView);
				 String watchersViewSer = serializePriorityQueue(watchersView);
				 
				 /**
				  * Save to Storage
				  */
				 
				 this.store.storeKey(Constants.FORKS_VIEW, forksViewSer);
				 this.store.storeKey(Constants.LAST_UPDATED_VIEW, lastUpdatedViewSer);
				 this.store.storeKey(Constants.OPEN_ISSUES_VIEW, openIssuesViewSer);
				 this.store.storeKey(Constants.STARS_VIEW, starsViewSer);
				 this.store.storeKey(Constants.WATCHERS_VIEW, watchersViewSer);
			 }
		 }
		}catch(SerializationException se){
			Utils.logger.error("Repo object serialization error");
		}
		
	}
	
	public String serializePriorityQueue(PriorityQueue<Repo> queue) throws SerializationException{
		try{
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
	        ObjectOutputStream objOut = new ObjectOutputStream( byteStream );
	        objOut.writeObject(queue);
	        objOut.close();
	        String ser =  Base64.getEncoder().encodeToString(byteStream.toByteArray()); 
			return ser;
		}catch(IOException ioe){
			ioe.printStackTrace();
			throw new SerializationException("Unable to serialize list of repos");
		}
	}
	
	public String getTopNRepos(int n,String view){
		String ser = this.store.getKey(view);
		PriorityQueue<Repo> pqr = deserializePriorityQueue(ser);
		if (pqr==null) return "";
		int count = 0;
	    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
	    while(!pqr.isEmpty() && count<n){
		   Repo repo = pqr.poll();
		   JsonReader jsonReader = Json.createReader(new StringReader(repo.getAllProperties()));
		   arrayBuilder.add(jsonReader.readObject());
		   count++;
		   
	    }
	    JsonArray topNRepos = arrayBuilder.build();
	    return topNRepos.toString();
	}
	
	private PriorityQueue<Repo> deserializePriorityQueue(String contents){
		try{
			byte [] data = Base64.getDecoder().decode(contents);
	        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
	        PriorityQueue<Repo> pqr = (PriorityQueue<Repo>)ois.readObject();
	        ois.close();
		    return pqr;
		}catch(IOException ioe){
			return null;
		}catch(ClassNotFoundException cnfe){
			return null;
		}
		   
		  
		  
	}
	

}
