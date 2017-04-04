package mausam.projects.githubcache.models;

import java.io.Serializable;

import javax.json.JsonObject;

public class Org implements Serializable{
	
	private String login;
	private int id;
	private String url;
	private String name;
	private String company;
	private String allProperties;
	
	public Org(JsonObject org){
		this.login = org.getString("login");
		this.id = org.getInt("id");
		this.company = org.getString("company");
		this.name = org.getString("name");
		this.url = org.getString("html_url");
		this.allProperties = org.toString();
	}
	
	public String getLogin(){
		return this.login;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getURL(){
		return this.url;
	}
	
	public String getCompany(){
		return this.company;
	}
	
	public String getAllProperties(){
		return this.allProperties;
	}

}
