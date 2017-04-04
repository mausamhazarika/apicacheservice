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

@Path("/orgs/{orgName}/members/{memberLogin}")
public class MemberResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMember(@PathParam("memberLogin") String login){
		StorageHandler store = StorageHandler.getInstance();
		String memberKey = Utils.getMemberKey(login) ;
		String resp = store.getKey(memberKey);
		if ((resp==null || resp.isEmpty()) && !store.containsKey(memberKey)){
			Utils.logger.info("Cache miss for member " + login);
			resp = CacheHandler.getInstance().getAndUpdateCacheNewMember(login);
			if (resp==null){
				resp = Utils.getEmptyJsonResponse();
			}
		}
		return Response.ok(resp, MediaType.APPLICATION_JSON).build();
	}
}
