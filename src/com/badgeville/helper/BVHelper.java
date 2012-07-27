package com.badgeville.helper;

import java.util.Iterator;
import java.util.Map;

import android.os.Handler;
import android.util.Log;

/**
 * Helper class for invoking Badgeville REST APIs.
 */
public class BVHelper {

	private final String mUrlBase;
	private final HTTPHelper mHttpHelper;

	public static final String ACTIVITIES = "activities";
	public static final String ACTIVITY_DEFINITIONS = "activity_definitions";
	public static final String LEADERBOARDS = "leaderboards";
	public static final String MISSIONS = "groups";
	public static final String PLAYERS = "players";
	public static final String REWARDS = "rewards";
	public static final String REWARD_DEFINTIONS = "reward_definitions";
	public static final String SITE_CONTENTS = "site_contents";
	public static final String SITES = "sites";
	public static final String TEAMS = "teams";
	public static final String TRACKS = "tracks";
	public static final String UNITS = "units";
	public static final String USERS = "users";

	/**
	 * Initializes helper class, in turn initializing the HTTP client.
	 * 
	 * @param hostName
	 *            Badgeville host name, e.g., "sandbox.v2.badgeville.com"
	 * @param apiKey
	 *            Private API key, e.g., "d96240d0722b8c54da091ba7cb3d3aee"
	 * @param handler
	 *            Android handler that will handle API responses
	 */
	public BVHelper(String hostName, String apiKey, Handler handler) {
		mUrlBase = "http://" + hostName + "/api/berlin/" + apiKey + "/";
		mHttpHelper = new HTTPHelper(handler);
	}

	/**
	 * Retrieves a list of the specified object.
	 * 
	 * @param objectName
	 *            Badgeville object to retrieve, e.g., BVHelper.ACTIVITIES
	 * @param params
	 *            Request filter parameters
	 */
	public void list(String objectName, Map<String, String> params) {
		String url = mUrlBase + objectName + ".json" + buildParamString(params);
		mHttpHelper.performRequest(HTTPHelper.METHOD_GET, url, null);
	}

	/**
	 * Creates the specified object based on the parameters.
	 * 
	 * @param objectName
	 *            Badgeville object to create, e.g., BVHelper.ACTIVITIES
	 * @param params
	 *            Object attributes
	 */
	public void create(String objectName, Map<String, String> params) {
		String url = mUrlBase + objectName + ".json";
		
		StringBuffer paramBuf = new StringBuffer();
		if (params != null && params.size() > 0) {
			Iterator<Map.Entry<String, String>> iter = params.entrySet()
					.iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> param = (Map.Entry<String, String>) iter
						.next();
				paramBuf.append(param.getKey() + "=" + param.getValue());
				if (iter.hasNext()) {
					paramBuf.append('&');
				}
			}
		}
		Log.i("PARAM STRING CHECK", paramBuf.toString());
		String post = paramBuf.toString();
		
		mHttpHelper.performRequest(HTTPHelper.METHOD_POST, url, post);
	}
		
//		if(objectName.equals(ACTIVITIES)) {
//			mHttpHelper.createActivities(HTTPHelper.METHOD_POST, url, params);
//		} else if(objectName.equals(ACTIVITY_DEFINITIONS)) {
//			mHttpHelper.createActivityDefinitions(HTTPHelper.METHOD_POST, url, params);
//		} else if(objectName.equals(LEADERBOARDS)) {
//			mHttpHelper.createLeaderboards(HTTPHelper.METHOD_POST, url, params);
//		} else if(objectName.equals(MISSIONS)) {
//			mHttpHelper.createMissions(HTTPHelper.METHOD_POST, url, params);
//		} else if(objectName.equals(PLAYERS)) {
//			mHttpHelper.createPlayers(HTTPHelper.METHOD_POST, url, params);
//		} else if(objectName.equals(REWARDS)) {
//			mHttpHelper.createRewards(HTTPHelper.METHOD_POST, url, params);
//		} else if(objectName.equals(REWARD_DEFINTIONS)) {
//			mHttpHelper.createRewardDefinitions(HTTPHelper.METHOD_POST, url, params);
//		} else if(objectName.equals(SITE_CONTENTS)) {
//			mHttpHelper.createSiteContents(HTTPHelper.METHOD_POST, url, params);
//		} else if(objectName.equals(SITES)) {
//			mHttpHelper.createSites(HTTPHelper.METHOD_POST, url, params);
//		} else if(objectName.equals(TEAMS)) {
//			mHttpHelper.createTeams(HTTPHelper.METHOD_POST, url, params);
//		} else if(objectName.equals(TRACKS)) {
//			mHttpHelper.createTracks(HTTPHelper.METHOD_POST, url, params);
//		} else if(objectName.equals(UNITS)) {
//			mHttpHelper.createUnits(HTTPHelper.METHOD_POST, url, params);
//		} else if(objectName.equals(USERS)) {
//			mHttpHelper.createUsers(HTTPHelper.METHOD_POST, url, params);
//		}

	/**
	 * Reads the object with the specified ID.
	 * 
	 * @param objectName
	 *            Badgeville object to read, e.g., BVHelper.ACTIVITIES
	 * @param objectId
	 *            ID of the object instance to read, e.g.,
	 *            "4e611beac47eed35550012e6"
	 * @param params
	 *            Request filter parameters
	 */
	public void read(String objectName, String objectId,
			Map<String, String> params) {
		String url = mUrlBase + objectName + "/" + objectId + ".json"
				+ buildParamString(params);
		mHttpHelper.performRequest(HTTPHelper.METHOD_GET, url, null);
	}

	/**
	 * Updates the object with the specified ID.
	 * 
	 * @param objectName
	 *            Badgeville object to update, e.g., BVHelper.ACTIVITIES
	 * @param objectId
	 *            ID of the object instance to update, e.g.,
	 *            "4e611beac47eed35550012e6"
	 * @param params
	 *            Object attributes to update
	 */
	public void update(String objectName, String objectId,
			Map<String, String> params) {
		String url = mUrlBase + objectName + "/" + objectId + ".json"
				+ buildParamString(params);
		mHttpHelper.performRequest(HTTPHelper.METHOD_PUT, url, null);
	}

	/**
	 * Deletes the object with the specified ID.
	 * 
	 * @param objectName
	 *            Badgeville object to delete, e.g.,
	 *            BVHelper.ACTIVITY_DEFINITIONS
	 * @param objectId
	 *            ID of the object instance to update, e.g.,
	 *            "4e611beac47eed35550012e6"
	 */
	public void delete(String objectName, String objectId) {
		String url = mUrlBase + objectName + "/" + objectId + ".json";
		mHttpHelper.performRequest(HTTPHelper.METHOD_DELETE, url, null);
	}

	/**
	 * Utility method for building up URL param string.
	 * 
	 * @param params
	 * @return URL parameter string
	 */
	private static String buildParamString(Map<String, String> params) {
		StringBuffer paramBuf = new StringBuffer();
		if (params != null && params.size() > 0) {
			paramBuf.append('?');
			Iterator<Map.Entry<String, String>> iter = params.entrySet()
					.iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> param = (Map.Entry<String, String>) iter
						.next();
				paramBuf.append(param.getKey() + "=" + param.getValue());
				if (iter.hasNext()) {
					paramBuf.append('&');
				}
			}
		}
		Log.i("PARAM STRING CHECK", paramBuf.toString());
		return paramBuf.toString();
	}

}
