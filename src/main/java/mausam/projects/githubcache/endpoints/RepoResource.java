package mausam.projects.githubcache.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mausam.projects.githubcache.StorageHandler;
import mausam.projects.githubcache.caching.CacheHandler;
import mausam.projects.githubcache.utils.Utils;

@Path("/orgs/{orgName}/repos/{repoName}")
public class RepoResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRepo(@PathParam("orgName") String orgName,@PathParam("repoName") String repoName){
		StorageHandler store = StorageHandler.getInstance();
		String repoKey = Utils.getRepoKey(orgName, repoName) ;
		String resp = store.getKey(repoKey);
		if ((resp==null || resp.isEmpty()) && !store.containsKey(repoKey)){
			Utils.logger.info("Cache miss for repo " + orgName + "/" + repoName);
			resp = CacheHandler.getInstance().getAndUpdateCacheNewRepo(orgName,repoName);
			if (resp==null){
				resp = Utils.getEmptyJsonResponse();
			}
		}
		return Response.ok(resp, MediaType.APPLICATION_JSON).build();
		
	}

}
