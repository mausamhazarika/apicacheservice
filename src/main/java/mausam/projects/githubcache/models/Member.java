package mausam.projects.githubcache.models;

import java.io.Serializable;
import javax.json.JsonObject;

public class Member implements Serializable{
	
	private String login;
	private int id;
	private String type;
	private boolean isSiteAdmin;
	private String url;
	private String allProperties;
	
	
	public Member(JsonObject member){
		this.login = member.getString("login");
		this.id = member.getInt("id");
		this.type = member.getString("type");
		this.isSiteAdmin = member.getBoolean("site_admin");
		this.url = member.getString("html_url");
		this.allProperties = member.toString();
	}
	
	public String getLogin(){
		return this.login;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getType(){
		return this.type;
	}
	
	public String getURL(){
		return this.url;
	}
	
	public boolean isSiteAdmin(){
		return this.isSiteAdmin;
	}
	
	public String getAllProperties(){
		return this.allProperties;
	}

}
