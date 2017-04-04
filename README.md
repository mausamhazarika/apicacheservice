GitHub API Read Caching Service
===============================
This is an implementation GitHub API read caching service using Redis and the Java Jersey framework. The following endpoints are cached periodically based on a configured amount of time (passed at runtime) and paginated responses from them are flattened into a single response payload.  API endpoints outside of this set are proxied through the service to GitHub and their responses are cached.

* /
* /orgs/Netflix
* /orgs/Netflix/members
* /orgs/Netflix/repos

Additionally the following custom views for Netflix organization repositories are implemented. Each of these end points returns the entire Json body of the repo which can be verified by copying the response and pasting into a JSON viewer like the one available at http://jsonviewer.stack.hu/

* Top-N repos by number of forks (/view/top/N/forks)
* Top-N repos by last updated time (/view/top/N/last_updated).
* Top-N repos by open issues (/view/top/N/open_issues).
* Top-N repos by stars (/view/top/N/stars).
* Top-N repos by watchers (/view/top/N/watchers).

A leader election process has been implemented using the Redis SET command with 'NX' and 'expiration' options. The elected leader is solely responsible for periodically refreshing the cache. Additionally the leader proxies API end points not cached to the GitHub API and caches these results. If a request for an API end point not cached in Redis is received by a follower instance, it will delegate the request to the leader.

A /healthcheck endpoint is provided that returns HTTP 200 when the service is ready to serve API responses and returns 503 during initialization when it is creating the cache.

Environment Variables Required
------------------------------
* REDIS_HOST=localhost or hostname
* GITHUB_USER=GitHub User Login
* GITHUB_API_TOKEN=Personal API Token

Runtime Arguments Required
---------------------------
PORT and CACHE-INTERVAL in minutes

Dependencies
------------
* Java 1.8
* Maven
* Redis

Running the Example
-------------------

1. Make sure Java and Maven are installed
2. Download and run Redis
3. Set the following Environment variables from command prompt

      e.g
      REDIS_HOST="localhost"

      export REDIS_HOST
      GITHUB_API_TOKEN="xyz"
      export GITHUB_API_TOKEN
      GITHUB_USER="githubuser"
      export GITHUB_USER

4. Run the service as follows:

__mvn clean compile resources:resources exec:java -Dexec.args="port cache-interval-in-minutes"__

If all requirements are satisfied, the following message should appear in the console

githubcacheservice - Git Cache Service started at http://localhost:<port> Hit enter to stop it...

To run another instance of the service, run the above mvn command with a different port number

Testing the service
--------------------
Once the service is up and running, using a browser or curl try the following URLs. These will return results in JSON format

* http://localhost:<port>/
* http://localhost:<port>/orgs/Netflix
* http://localhost:<port>/orgs/Netflix/members
* http://localhost:<port>/orgs/Netflix/members/aglover
* http://localhost:<port>/orgs/repos
* http://localhost:<port>/orgs/Netflix/repos/astyanax
* http://localhost:<port>/view/top/5/forks
* http://localhost:<port>/view/top/5/stars
* http://localhost:<port>/view/top/5/watchers
* http://localhost:<port>/view/top/5/open_issues
* http://localhost:<port>/view/top/5/last_updated

Future Optimizations
--------------------

1. The data associated with the views are stored as serialized Java objects, while the resources are stored as Json strings in Redis. Given the size of some of the resources, the volume of data transferred over the network would
be a bottleneck. This can be improved by compressing the data with a fast compression algorithm

2. The lock is built using the Redis set command with the 'NX' (set if does not exist) and 'expiration' time stamp. The key for the lock is the string 'GitHubCacheServiceLeader'. The key is set for the configured 'cache-interval' time When this key is not yet set, the first instance to call acquireLock will get the lock. Other instances will
try and fail. If a leader crashes and dies, the lock will remain on for at-least the cache-interval duration,
after which it gets released and any other instance requesting the lock will be able to acquire it. This could result in the service being unable to retrieve previously uncached data as the service relies on the leader to do that. This could be potentially improved by implementing a 'leader-health-checker' service which the follower instances could use to periodically checks for the availability of the leader and in case of unavailability tries to acquire the lock.
