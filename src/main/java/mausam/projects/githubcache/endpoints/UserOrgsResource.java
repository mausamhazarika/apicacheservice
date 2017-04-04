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

@Path("/users/{login}/orgs")
public class UserOrgsResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrgsForUser(@PathParam("login") String login){
		StorageHandler store = StorageHandler.getInstance();
		String userOrgsKey = Utils.getMemberOrgsKey(login);
		String resp = store.getKey(userOrgsKey);
		if ((resp==null || resp.isEmpty()) && !store.containsKey(userOrgsKey)){
			Utils.logger.info("Cache miss for orgs of member " + login);
			resp = CacheHandler.getInstance().getAndUpdateCacheNewMemberOrgs(login);
			if (resp==null){
				resp = Utils.getEmptyJsonResponse();
			}
		}
		return Response.ok(resp, MediaType.APPLICATION_JSON).build();
	}

}
