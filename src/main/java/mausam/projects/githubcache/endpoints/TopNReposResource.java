package mausam.projects.githubcache.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import mausam.projects.githubcache.caching.GitHubRepoCacher;
import mausam.projects.githubcache.utils.Constants;

@Path("/view/top/{number}/{sortType}")
public class TopNReposResource {
	
	private GitHubRepoCacher repoCache;
	
	 @GET
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response getTopRepositories(@PathParam("number") int n,@PathParam("sortType") String sortType) {
		 repoCache = GitHubRepoCacher.getInstance();
		 String resp = null;
		 switch(sortType){
			 case Constants.FORKS:
				 resp = repoCache.getTopNRepos(n,Constants.FORKS_VIEW);
				 break;
			 case Constants.LAST_UPDATED:
				 resp = repoCache.getTopNRepos(n,Constants.LAST_UPDATED_VIEW);
				 break;
			 case Constants.OPEN_ISSUES:
				 resp = repoCache.getTopNRepos(n,Constants.OPEN_ISSUES_VIEW);
				 break;
			 case Constants.STARS:
				 resp = repoCache.getTopNRepos(n,Constants.STARS_VIEW);
				 break;
			 case Constants.WATCHERS:
				 resp = repoCache.getTopNRepos(n,Constants.WATCHERS_VIEW);
				 break;
			default: 
				return Response.status(Status.NOT_FOUND).build();
		 }
		 return Response.ok(resp, MediaType.APPLICATION_JSON).build();
	 }

}
