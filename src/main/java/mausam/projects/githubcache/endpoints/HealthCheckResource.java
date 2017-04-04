package mausam.projects.githubcache.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import mausam.projects.githubcache.caching.CacheHandler;

@Path("/healthcheck")
public class HealthCheckResource {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getHealth(){
		CacheHandler cacheHandler = CacheHandler.getInstance();
		if (cacheHandler.isCacheReady()){
			return Response.ok().entity("OK").type("text/plain").build();
		}else {
			return Response.status(Status.SERVICE_UNAVAILABLE).entity("Service Unavailable").build();
		}
	}
}
