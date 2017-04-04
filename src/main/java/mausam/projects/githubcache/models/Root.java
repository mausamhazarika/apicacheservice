package mausam.projects.githubcache.models;

import javax.json.JsonObject;

public class Root {
	
	private String allProperties;
	
	public Root(JsonObject rootProperties){
		this.allProperties = rootProperties.toString();
	}
	
	public String getAllProperties(){
		return this.allProperties;
	}
	
	

}
