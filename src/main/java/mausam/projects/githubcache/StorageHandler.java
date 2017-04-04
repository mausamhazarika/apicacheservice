package mausam.projects.githubcache;

import javax.json.JsonObject;

import mausam.projects.githubcache.utils.Constants;
import mausam.projects.githubcache.utils.Utils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Singleton class responsible for connecting with Redis using the Jedis API and provides
 * wrapper functions for storage and retrieval of keys from Redis
 * @author Mausam Hazarika
 *
 */

public class StorageHandler {

	private static StorageHandler instance;
	private String redisHost;
	private static JedisPool redisPool;
	
	private StorageHandler() {
		redisHost = System.getenv("REDIS_HOST");
		redisPool = new JedisPool(new JedisPoolConfig(), redisHost);
	}
	
	public synchronized static StorageHandler getInstance(){
		if (instance==null){
			instance = new StorageHandler();
		}
		return instance;
	}
	
	public boolean checkConnection(){
		try {
		    Jedis jedis = redisPool.getResource();
		    // Is connected
		    jedis.close();
		    return true;
		} catch (JedisConnectionException e) {
		    Utils.logger.error("Redis connection failure");
		    return false;
		}
	}
	
	public String storeKey(String key, String value){
		try{
			Jedis jedis = redisPool.getResource();
			String response =  jedis.set(key, value);
			jedis.close();
			return response;
		}catch (JedisConnectionException e) {
		    Utils.logger.error("Redis connection failure");
		    return null;
		}
	}
	
	public String getKey(String key){
		try {
			Jedis jedis = redisPool.getResource();
			String response =  jedis.get(key);
			jedis.close();
			return response;
		}catch (JedisConnectionException e) {
		    Utils.logger.error("Redis connection failure");
		    return null;
		}
		
		
	}
	
	public boolean containsKey(String key){
		try{
			Jedis jedis = redisPool.getResource();
			boolean response =  jedis.exists(key);
			jedis.close();
			return response;
		}catch (JedisConnectionException e) {
		    Utils.logger.error("Redis connection failure");
		    return false;
		}
	}
	
	public boolean setLock(JsonObject lockObj){
		try {
			Jedis jedis = redisPool.getResource();
			String response =  jedis.set(Constants.LEADER_KEY,lockObj.toString(),Constants.JEDIS_OPT_SET_IF_NOT_SET,
					Constants.JEDIS_OPT_MILLISECS,Long.valueOf(lockObj.getString(Constants.EXPIRES)));
			jedis.close();
			return response.equals(Constants.JEDIS_KEY_SET_OK)?true:false;
		}catch (JedisConnectionException e) {
		    Utils.logger.error("Redis connection failure");
		    return false;
		}
	}

}
