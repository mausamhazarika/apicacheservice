package mausam.projects.githubcache.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mausam.projects.githubcache.StorageHandler;
import mausam.projects.githubcache.utils.Constants;

@Path("/")
public class RootResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRoot(){
		StorageHandler store = StorageHandler.getInstance();
		String resp = store.getKey(Constants.PATH_SEPARATOR);
		return  Response.ok(resp, MediaType.APPLICATION_JSON).build();
		
	}

}
