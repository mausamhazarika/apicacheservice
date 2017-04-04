package mausam.projects.githubcache.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mausam.projects.githubcache.StorageHandler;
import mausam.projects.githubcache.caching.CacheHandler;
import mausam.projects.githubcache.utils.Constants;
import mausam.projects.githubcache.utils.Utils;

@Path("/orgs/{orgName}")
public class OrgResource {

	 @GET
	 @Produces(MediaType.TEXT_PLAIN)
	public Response getOrg(@PathParam("orgName") String orgName){
		StorageHandler store = StorageHandler.getInstance();
		StringBuilder key = new StringBuilder();
		key.append(Constants.ORG_PREFIX);
		key.append(orgName);
		String resp = store.getKey(key.toString());
		if ((resp==null || resp.isEmpty()) && !store.containsKey(key.toString())){
			Utils.logger.info("Cache miss for org " + orgName);
			resp = CacheHandler.getInstance().getAndUpdateCacheNewOrg(orgName);
			if (resp==null){
				resp = Utils.getEmptyJsonResponse();
			}
		}
		return Response.ok(resp, MediaType.APPLICATION_JSON).build();
	}

}
