package mausam.projects.githubcache.utils;

public final class Constants {

	public static final String FORKS_VIEW = "top-N-view-forks";
	public static final String LAST_UPDATED_VIEW = "top-N-view-last-updated";
	public static final String OPEN_ISSUES_VIEW = "top-N-view-open-issues";
	public static final String STARS_VIEW = "top-N-view-stars";
	public static final String WATCHERS_VIEW = "top-N-view-watchers";
	public static final String REPO_PREFIX = "repo-";
	public static final String MEMBER_PREFIX = "member-";
	public static final String ORG_PREFIX = "org-";
	public static final String ORG_REPOS_PREFIX = "org-repos-";
	public static final String ORG_MEMBERS_PREFIX = "org-members-";
	public static final String MEMBER_ORGS_PREFIX = "member-orgs-";
	public static final String ORG_NETFLIX = "Netflix";
	public static final String PATH_SEPARATOR = "/";
	
	public static final String FORKS = "forks";
	public static final String LAST_UPDATED = "last_updated";
	public static final String OPEN_ISSUES = "open_issues";
	public static final String STARS = "stars";
	public static final String WATCHERS = "watchers";
	
	public static final String GITHUB_API_URL = "https://api.github.com";
	public static final String GITHUB_API_TOKEN = "GITHUB_API_TOKEN";
	public static final String GITHUB_USER = "GITHUB_USER";
	public static final String ORGS = "orgs";
	public static final String REPOS = "repos";
	public static final String USERS = "users";
	public static final String MEMBERS = "members";
	
	public static final String REDIS_HOST = "REDIS_HOST";
	
	public static final String LEADER_KEY = "GitHubCacheServiceLeader";
	public static final String EXPIRES = "expires";
	public static final String LEADER_URI = "leaderuri";
	
	public static final String JEDIS_OPT_SET_IF_NOT_SET = "NX";
	public static final String JEDIS_OPT_MILLISECS = "PX";
	public static final String JEDIS_KEY_SET_OK = "OK";
	
	public static final String HTTP_RESPONSE_HEADER_LINK = "Link";
	public static final String HEALTH_CHECK = "healthcheck";
	
	public static final String LOGGER_NAME = "githubcacheservice";
	
	public static int PORT = 8080;
	public static long INTERVAL = 30*60*1000; // Cache interval in milliseconds
	public static String SERVICE_URI = "";
	
	
	


}
