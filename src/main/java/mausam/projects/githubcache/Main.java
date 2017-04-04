package mausam.projects.githubcache;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import mausam.projects.githubcache.caching.CacheHandler;
import mausam.projects.githubcache.utils.Constants;
import mausam.projects.githubcache.utils.Utils;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

/**
 * Main class.
 *
 */
public class Main {

    public static String BASE_URI;
    public static final Optional<String> host;
    public static String port;
    private static Thread cachingThread;

    static{
      host = Optional.ofNullable(System.getenv("HOSTNAME"));
    }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer(String[] args) {
        // create a resource config that scans for JAX-RS resources and providers
        // in mausam.projects.githubcache package
        final ResourceConfig rc = new ResourceConfig().packages("mausam.projects.githubcache");

        init(args);
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    private static void init(String[] args){
    	if (args.length<2){
    		Utils.logger.fatal("Invalid number of commandline arguments");
    		System.exit(0);
    	}

		String servicePort = args[0];
		String interval = args[1];
		try{
			if (Integer.valueOf(servicePort)>0){
				port = servicePort;
			}else {
				Utils.logger.fatal("Invalid port number specified! Aborting..");
				System.exit(0);
			}
			if (Integer.valueOf(interval)>0){
				Constants.INTERVAL = Long.valueOf(interval)*60*1000;
			}else {
				Utils.logger.fatal("Invalid port number specified! Aborting..");
				System.exit(0);
			}

		}catch(NumberFormatException nfe){
			Utils.logger.fatal("Invalid port number or cache interval specified!");
			System.exit(0);
		}

      Constants.PORT = Integer.valueOf(port);
	  String redisHost = System.getenv(Constants.REDIS_HOST);

		if (redisHost==null || redisHost.isEmpty()){
			Utils.logger.fatal("Missing environment variable REDIS_HOST! This needs to be set. Aborting....");
			System.exit(0);
		}
		BASE_URI = "http://" + host.orElse("localhost") + ":" + port ;
		Constants.SERVICE_URI = BASE_URI;

		//Check for valid GitHub user and api token. If not set, abort
		String gitUser = System.getenv(Constants.GITHUB_USER);
		String gitApiToken = System.getenv(Constants.GITHUB_API_TOKEN);
		if (gitUser==null || gitUser.isEmpty() || gitApiToken==null || gitApiToken.isEmpty()){
			Utils.logger.fatal("Missing environment variable GITHUB_API_TOKEN or GIT_USER! This needs to be set. Aborting....");
			System.exit(0);
		}
		//Check GitHub Credentials
		CacheHandler cacheHandler = CacheHandler.getInstance();
		if (!cacheHandler.checkGitHubCredentials()){
			Utils.logger.fatal("Invalid GitHub credentials. Aborting....");
			System.exit(0);
		}

		//Check Redis Connection
		if (!cacheHandler.checkStorageConnection()) {
			Utils.logger.fatal("Unable to connect to Redis. Aborting....");
			System.exit(0);
		}
		cachingThread = new Thread(cacheHandler);
		cachingThread.start();
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
    
        final HttpServer server = startServer(args);
        Utils.logger.info("Git Cache Service started at " + BASE_URI + " Hit enter to stop it...");
        System.in.read();
        cachingThread.interrupt();// Tell caching thread to stop and exit
        server.shutdown();

    }
}
