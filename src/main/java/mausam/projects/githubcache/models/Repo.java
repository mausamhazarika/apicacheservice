package mausam.projects.githubcache.models;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import javax.json.JsonObject;

public class Repo implements Serializable{
	
	private int id;
	private String name;
	private String fullName;
	private String description;
	private String htmlUrl;
	private int forksCount;
	private Date updatedTime;
	private int openIssuesCount;
	private int starsCount;
	private int watchersCount;
	private Member owner;
	private String allProperties;
	
	
	public Repo(JsonObject repo) {
		//Populate properties from JSON object
		this.id = Integer.valueOf(repo.get("id").toString());
		this.name = repo.getString("name");
		this.fullName = repo.getString("full_name",null);
		this.description = repo.getString("description",null);
		this.htmlUrl = repo.getString("html_url");
		this.forksCount = repo.getInt("forks_count");
		Instant inst = Instant.parse(repo.getString("updated_at"));
		this.updatedTime = Date.from(inst);
		this.openIssuesCount = repo.getInt("open_issues");
		this.starsCount = repo.getInt("stargazers_count");
		this.watchersCount = repo.getInt("watchers_count");
		this.owner = new Member(repo.getJsonObject("owner"));
		this.allProperties = repo.toString();
	}
	
	public void setOpenIssuesCount(int n){
		this.openIssuesCount = n;
	}
	
	public void setForksCount(int n){
		this.forksCount = n;
	}
	
	public void setOpenStarsCount(int n){
		this.starsCount = n;
	}
	
	public void setWatchersCount(int n){
		this.watchersCount = n;
	}
	
	public int getOpenIssuesCount(){
		return this.openIssuesCount;
	}
	
	public int getForksCount(){
		return this.forksCount;
	}
	
	public int getStarsCount(){
		return this.starsCount;
	}
	
	public int getWatchersCount(){
		return this.watchersCount;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getFullName(){
		return this.fullName;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public String getURL(){
		return this.htmlUrl;
	}
	
	public Date getUpdatedTime(){
		return this.updatedTime;
	}
	
	public Member getOwner(){
		return this.owner;
	}
	
	public String getAllProperties(){
		return this.allProperties;
	}

}
