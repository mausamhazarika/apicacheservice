package mausam.projects.githubcache.leaderelection;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import mausam.projects.githubcache.StorageHandler;
import mausam.projects.githubcache.utils.Constants;
import mausam.projects.githubcache.utils.Utils;

/**
 * Singleton class providing leader election mechanism by means of a lock. The lock is built using the
 * Redis set command with the 'NX' (set if does not exist) and 'expiration' time stamp. The key for the
 * lock is the string 'GitHubCacheServiceLeader'. The key is set for the configured 'cache-interval' time.
 * When this key is not yet set, the first instance to call acquireLock will get the lock. Other instances will
 * try and fail. If a leader crashes and dies, the lock will remain on for at-least the cache-interval duration, 
 * after which it gets released and any other instance requesting the lock will be able to acquire it. 
 * @author Mausam Hazarika
 *
 */

public class LeaderElector {

	private StorageHandler store;
	private static LeaderElector instance;
	
	private LeaderElector() {
		this.store = StorageHandler.getInstance();
	}
	
	public synchronized static LeaderElector getInstance(){
		if (instance==null){
			instance = new LeaderElector();
		}
		return instance;
	}
	
	public synchronized boolean acquireLock(String baseUri){
		String leaderUri = getLeaderURI();
		if (leaderUri==null){ // if null, Redis has removed the key after TTL. Now anyone can acquire the lock
			JsonObject lockObj = Json.createObjectBuilder()
					.add(Constants.EXPIRES, String.valueOf(System.currentTimeMillis() + Constants.INTERVAL - 1000))
					.add(Constants.LEADER_URI,baseUri).build();
			boolean acquired = store.setLock(lockObj);
			if (acquired){
				Utils.logger.info("Lock acquired by " + baseUri);
			}
			return acquired;
		}else {
			// Current lock hasn't expired. Check if current instance is the leader
			return leaderUri.equals(baseUri); //already the leader
		}
		
	}
	
	public synchronized String getLeaderURI(){
		String lockStr = store.getKey(Constants.LEADER_KEY);
		if (lockStr!=null){
			 JsonReader jsonReader = Json.createReader(new StringReader(lockStr));
			 JsonObject lockObj = jsonReader.readObject();
			 jsonReader.close();
			 return lockObj.getString(Constants.LEADER_URI);
		}
		return null;
	}

}
