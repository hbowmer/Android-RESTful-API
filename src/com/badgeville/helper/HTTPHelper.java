package com.badgeville.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * HTTP helper class based on Apache HTTPHelper
 */
public class HTTPHelper {

	private static final String CLASS_NAME = HTTPHelper.class.getSimpleName();

	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_DELETE = "DELETE";

	public static final String MIME_FORM_ENCODED = "application/x-www-form-urlencoded";
	public static final String MIME_TEXT_PLAIN = "text/plain";

	private static final String CONTENT_TYPE = "Content-Type";

	private final ResponseHandler<String> mResponseHandler;

	private static final DefaultHttpClient mClient;
	static {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HTTP.UTF_8);
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
		params.setParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));

		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
				params, schemeRegistry);

		mClient = new DefaultHttpClient(cm, params);
	}

	/**
	 * Construct HTTP helper with an Android handler. HTTP responses will be
	 * handled by the specified handler.
	 * 
	 * @param handler
	 */
	public HTTPHelper(final Handler handler) {
		mResponseHandler = HTTPHelper.getResponseHandlerInstance(handler);
	}

	
	/**
	 * Constructs the Activities POST String that is passed to the 
	 * performRequest method. The following parameters can be passed
	 * to the method:
	 * 
	 * 		(String) site: REQUIRED site's url
	 * 		(String) user: REQUIRED user's email
	 * 		(String) verb: REQUIRED activity name
	 * 		(String) player_id: can be passed instead of site and user params
	 * 
	 * @param method
	 * 			HTTP method, e.g., get, put
	 * @param url
	 * 			URL to request from
	 * @param params
	 * 			Request parameters
	 */
	public void createActivities(final String method, final String url,
			final Map<String, String> params) {
		
		String post = null;
		
		if(params.get("player_id") != null) {
			post = "activity[verb]=" +params.get("verb")+ "&player_id="
					+params.get("player_id");
		} else {
		
			post = "activity[verb]=" +params.get("verb")+
					"&site=" +params.get("site")+ "&user=" +params.get("user");
		}	
		
		Log.i("POST CHECK", post);
		performRequest(method, url, post);
	}
	
	/**
	 * Constructs the ActivityDefinitions POST String that is passed to the 
	 * performRequest method. The following parameters can be passed
	 * to the method:
	 * 
	 * 		(String) site_id: REQUIRED id of the site
	 * 		(String) name: REQUIRED name of the activity definition
	 * 		(String) selector: REQUIRED selector for the activity definition
	 * 		(String) adjustment: REQUIRED adjustment for the activity definition
	 * 
	 * @param method
	 * 			HTTP method, e.g., get, put
	 * @param url
	 * 			URL to request from
	 * @param params
	 * 			Request parameters
	 */
	public void createActivityDefinitions(final String method, final String url,
			final Map<String, String> params) {
		String post = "activity_definition[site_id]=" +params.get("site_id")+
				"&activity_definition[name]=" +params.get("name")+
				"&activity_definition[selector]=" +params.get("selector")+
				"&activity_definition[adjustment]=" +params.get("adjustment");
		Log.i("POST CHECK", post);
		
		performRequest(method, url, post);
	}
	
	/**
	 * Constructs the Leaderboards POST String that is passed to the 
	 * performRequest method. The following parameters can be passed
	 * to the method:
	 * 
	 * 		(String) network_id: REQUIRED
	 * 		(String) name: REQUIRED name of the leaderboard
	 * 		(String) selector: ({}) filter for activities
	 * 		(String) field: (points) name of the field when command is "sum"
	 * 		(String) command: (sum) sum or count
	 * 		(String) label: alternate text to be displayed in widgets
	 * 
	 * @param method
	 * 			HTTP method, e.g., get, put
	 * @param url
	 * 			URL to request from
	 * @param params
	 * 			Request parameters
	 */
	public void createLeaderboards(final String method, final String url,
			final Map<String, String> params) {
		String post = null;
		String selector = "";
		String field = "";
		String command = "";
		String label = "";
		
		if(params.get("selector") != null) {
			selector = "&leaderboard[selector]=" +params.get("selector");
		}
		if(params.get("field") != null) {
			field = "&leaderboard[field]=" +params.get("field");
		}
		if(params.get("command") != null) {
			command = "&leaderboard[command]=" +params.get("command");
		}
		if(params.get("label") != null) {
			label = "&leaderboard[label]=" +params.get("label");
		}
		
		
		post = "leaderboard[network_id]=" +params.get("network_id")+
				"&leaderboard[name]=" +params.get("name") +selector+field+
				command+label;
		Log.i("POST CHECK", post);
		
		performRequest(method, url, post);
	}
	
	/**
	 * Constructs the Missions POST String that is passed to the 
	 * performRequest method. The following parameters can be passed
	 * to the method:
	 * 
	 * 		(String) site_id: REQUIRED id of the site
	 * 		(String) name: REQUIRED (unique) name of the group
	 * 		(Boolean) active: (false) activate this group for players
	 * 		(Boolean) repeatable: (false) allow a player to complete this mission more than once
	 * 		(Boolean) enable_rewards: (false) give points and reward upon completion
	 * 		(String) adjustment: points and secondary units to award when completing a mission
	 * 
	 * @param method
	 * 			HTTP method, e.g., get, put
	 * @param url
	 * 			URL to request from
	 * @param params
	 * 			Request parameters
	 */
	public void createMissions(final String method, final String url,
			final Map<String, String> params) {
		String post = null;
		String adjustment = "";
		
		if(params.get("adjustment") != null) {
			adjustment = "&group[adjustment]=" +params.get("adjustment");
		}
		
		post = "group[site_id]=" +params.get("site_id")+ "&group[name]=" +params.get("name")+
				adjustment;
		Log.i("POST CHECK", post);
		
		performRequest(method, url, post);
		
	}
	
	/**
	 * Constructs the Players POST String that is passed to the 
	 * performRequest method. The following parameters can be passed
	 * to the method:
	 * 
	 * 		(String) email: REQUIRED email of the user
	 * 		(String) site: REQUIRED url of the site
	 * 		(String) first_name
	 * 		(String) last_name
	 * 		(String) display_name
	 * 		(String) nickname
	 * 		(String) facebook_id
	 * 		(String) facebook_link
	 * 		(String) twitter_id
	 * 		(String) twitter_username
	 * 		(String) twitter_link
	 * 		(Hash) preferences
	 * 		(Boolean) email_notifications: (true)
	 * 		(String) custom_picture_url: url of the player's profile image
	 * 		(String) picture_url: default url of the player's profile image
	 * 		(Boolean) admin: (false)
	 * 
	 * @param method
	 * 			HTTP method, e.g., get, put
	 * @param url
	 * 			URL to request from
	 * @param params
	 * 			Request parameters
	 */
	public void createPlayers(final String method, final String url,
			final Map<String, String> params) {
		String post = null;
		String first_name = "";
		String last_name = "";
		String display_name = "";
		String nickname = "";
		String facebook_id = "";
		String facebook_link = "";
		String twitter_id = "";
		String twitter_username = "";
		String twitter_link = "";
		String custom_picture_url = "";
		String picture_url = "";
		
		if(params.get("first_name") != null) {
			first_name = "&player[first_name]=" +params.get("first_name");
		}
		if(params.get("last_name") != null) {
			last_name = "&player[last_name]=" +params.get("last_name");
		}
		if(params.get("display_name") != null) {
			display_name = "&player[display_name]=" +params.get("display_name");
		}
		if(params.get("nickname") != null) {
			nickname = "&player[nickname]=" +params.get("nickname");
		}
		if(params.get("facebook_id") != null) {
			facebook_id = "&player[facebook_id]=" +params.get("facebook_id");
		}
		if(params.get("facebook_link") != null) {
			facebook_link = "&player[facebook_link]=" +params.get("facebook_link");
		}
		if(params.get("twitter_id") != null) {
			twitter_id = "&player[twitter_id]=" +params.get("twitter_id");
		}
		if(params.get("twitter_username") != null) {
			twitter_username = "&player[twitter_username]=" +params.get("twitter_username");
		}
		if(params.get("twitter_link") != null) {
			twitter_link = "&player[twitter_link]=" +params.get("twitter_link");
		}
		if(params.get("custom_picture_url") != null) {
			custom_picture_url = "&player[custom_picture_url]=" +params.get("custom_picture_url");
		}
		if(params.get("picture_url") != null) {
			picture_url = "&player[picture_url]=" +params.get("picture_url");
		}
		
		post = "email=" +params.get("email")+ "&site=" +params.get("site") +first_name
				+last_name+display_name+nickname+facebook_id+facebook_link+twitter_id
				+twitter_username+twitter_link+custom_picture_url+picture_url;
		Log.i("POST CHECK", post);
		
		performRequest(method, url, post);
	}
	
	/**
	 * Constructs the Rewards POST String that is passed to the 
	 * performRequest method. The following parameters can be passed
	 * to the method:
	 * 
	 * 		(String) site_id: REQUIRED site id for the reward
	 * 		(String) player_id: REQUIRED player id for the reward
	 * 		(String) definition_id: REQUIRED definition id for the reward		
	 * 
	 * @param method
	 * 			HTTP method, e.g., get, put
	 * @param url
	 * 			URL to request from
	 * @param params
	 * 			Request parameters
	 */
	public void createRewards(final String method, final String url,
			final Map<String, String> params) {
		String post = "reward[site_id]=" +params.get("site_id")+ "&reward[player_id]="
				+params.get("player_id")+ "&reward[definition_id]="
				+params.get("definition_id");
		Log.i("POST CHECK", post);
		
		performRequest(method, url, post);
	}
	
	
	/**
	 * Constructs the RewardDefinitions POST String that is passed to the 
	 * performRequest method. The following parameters can be passed
	 * to the method:
	 * 
	 * 		(String) site_id: REQUIRED
	 * 		(String) name: REQUIRED
	 * 		(String) reward_template: REQUIRED
	 * 		(String) tags
	 * 		(String) hint
	 * 		(String) components
	 * 		(Boolean) active: (false) whether or not this reward definition is live
	 * 		(Boolean) allow_duplicates: (false) can players earn this multiple times
	 * 		(Boolean) assignable: (false) can admins manually give this to players
	 * 
	 * @param method
	 * 			HTTP method, e.g., get, put
	 * @param url
	 * 			URL to request from
	 * @param params
	 * 			Request parameters
	 */
	public void createRewardDefinitions(final String method, final String url,
			final Map<String, String> params) {
		String post = null;
		String tags = "";
		String hint = "";
		String components = "";
		
		if(params.get("tags") != null) {
			tags = "&reward_definition[tags]=" +params.get("tags");
		}
		if(params.get("hint") != null) {
			hint = "&reward_definition[hint]=" +params.get("hint");
		}
		if(params.get("components") != null) {
			components = "&reward_definition[components]" +params.get("components");
		}
		
		post = "reward_definition[site_id]=" +params.get("site_id")+
				"&reward_definition[name]=" +params.get("name")+
				"&reward_definition[reward_template]=" +params.get("reward_template") +
				tags+hint+components;
		Log.i("POST CHECK", post);
		
		performRequest(method, url, post);
	}
	
	/**
	 * Constructs the SiteContents POST String that is passed to the
	 * performRequest method. The following parameters can be passed
	 * to the method:
	 * 
	 * @param method
	 * 			HTTP method, e.g., get, put
	 * @param url
	 * 			URL to request from
	 * @param params
	 * 			Request parameters
	 */
	public void createSiteContents(final String method, final String url,
			final Map<String, String> params) {
		String post = null;
		String content_id = "";
		String content_type = "";
		String title = "";
		String image = "";
		
		if(params.get("content_id") != null) {
			content_id = "&site_content[content_id]=" +params.get("content_id");
		}
		if(params.get("content_type") != null) {
			content_type = "&site_content[content_type]=" +params.get("content_type");
		}
		if(params.get("title") != null) {
			title = "&site_content[title]=" +params.get("title");
		}
		if(params.get("image") != null) {
			image = "&site_content[image]=" +params.get("image");
		}
		
		post = "site_content[site_id]=" +params.get("site_id")+ "&site_content[content_url]="
				+params.get("content_url")+content_id+content_type+title+image;
		Log.i("POST CHECK", post);
		
		performRequest(method, url, post);
	}
	
	
	
	/**
	 * Constructs the Sites POST String that is passed to the 
	 * performRequest method. The following parameters can be passed
	 * to the method:
	 * 
	 * 		(String) name: REQUIRED name of the site
	 * 		(String) url: REQUIRED url of the site
	 * 
	 * @param method
	 * 			HTTP method, e.g., get, put
	 * @param url
	 * 			URL to request from
	 * @param params
	 * 			Request parameters
	 */
	public void createSites(final String method, final String url,
			final Map<String, String> params) {
		String post = "site[name]=" +params.get("name")+ "&site[url]=" +params.get("url");
		Log.i("POST CHECK", post);
		
		performRequest(method, url, post);
	}
	
	
	/**
	 * Constructs the Teams POST String that is passed to the 
	 * performRequest method. The following parameters can be passed
	 * to the method:
	 * 
	 * 		(String) site: REQUIRED url of the site
	 * 		(String) display_name: REQUIRED display_name of the team
	 * 		(Boolean) active: (false)
	 * 		(String) nickname
	 * 		(Hash) preferences
	 * 		(Boolean) email_notifications: (true)
	 * 
	 * @param method
	 * 			HTTP method, e.g., get, put
	 * @param url
	 * 			URL to request from
	 * @param params
	 * 			Request parameters
	 */
	public void createTeams(final String method, final String url,
			final Map<String, String> params) {
		String post = null;
		String nickname = "";
		
		if(params.get("nickname") != null) {
			nickname = "&team[nickname]=" +params.get("nickname");
		}
		
		post = "site=" +params.get("site")+ "&team[display_name]=" +params.get("display_name")
				+nickname;
		Log.i("POST CHECK", post);
		
		performRequest(method, url, post);
	}
	
	
	/**
	 * Constructs the Tracks POST String that is passed to the 
	 * performRequest method. The following parameters can be passed
	 * to the method:
	 * 
	 * 		(String) site_id: REQUIRED id of the site
	 * 		(String) label: REQUIRED label of the track
	 * 
	 * @param method
	 * 			HTTP method, e.g., get, put
	 * @param url
	 * 			URL to request from
	 * @param params
	 * 			Request parameters
	 */
	public void createTracks(final String method, final String url,
			final Map<String, String> params) {
		String post = "track[site_id]=" +params.get("site_id")+ "&track[label]="
				+params.get("label");
		Log.i("POST CHECK", post);
		
		performRequest(method, url, post);
	}
	
	/**
	 * Constructs the Units POST String that is passed to the
	 * performRequest method. The following parameters can be passed
	 * to the method:
	 * 
	 * 		(String) site: REQUIRED url of the site the unit will be for
	 * 		(String) type: REQUIRED type of the unit
	 * 		(String) name: REQUIRED name of the unit
	 * 		(String) label: REQUIRED label of the unit
	 * 		(String) abbreviation: REQUIRED abbreviation of the unit
	 * 
	 * @param method
	 * 			HTTP method, e.g., get, put
	 * @param url
	 * 			URL to request from
	 * @param params
	 * 			Request parameters
	 */
	public void createUnits(final String method, final String url,
			final Map<String, String> params) {
		String post = "site=" +params.get("site")+ "&type=" +params.get("type")+
				"&unit[name]=" +params.get("name")+ "&unit[label]=" +params.get("label")+
				"&unit[abbreviation]=" +params.get("abbreviation");
		Log.i("POST CHECK", post);
		
		performRequest(method, url, post);
	}
	
	
	/**
	 * Constructs the Users POST String that is passed to the 
	 * performRequest method. The following parameters can be passed
	 * to the method:
	 * 
	 * 		(String) email: REQUIRED email of the user
	 * 		(String) name: name of the user
	 * 
	 * 
	 * @param method
	 * 			HTTP method, e.g., get, put
	 * @param url
	 * 			URL to request from
	 * @param params
	 * 			Request parameters
	 */
	public void createUsers(final String method, final String url,
			final Map<String, String> params) {
		String post = null;
		String name = "";
		
		if(params.get("name") != null) {
		name = "&user[name]=" +params.get("name");	
		}
		
		post = "user[email]=" +params.get("email")+name;
		Log.i("POST CHECK", post);
		
		performRequest(method, url, post);
	}
	
	
	/**
	 * Execute the specified HTTP method given a URL and parameters.
	 * 
	 * @param method
	 *            HTTP method, e.g., get, put
	 * @param url
	 *            URL to request from
	 * @param params
	 *            Request parameters
	 */
	public void performRequest(final String method, final String url,
			final String post) {
		Log.d(CLASS_NAME, "HTTP " + method + " request to url: " + url);
		HttpUriRequest request = null;

		if (method.equals(HTTPHelper.METHOD_GET)) {
			request = new HttpGet(url);
		} else if (method.equals(HTTPHelper.METHOD_POST)) {
			request = new HttpPost(url);
			request.setHeader(HTTPHelper.CONTENT_TYPE,
					HTTPHelper.MIME_FORM_ENCODED);

			if (post != null) {
				try {
					((HttpPost) request).setEntity(new StringEntity(post, HTTP.UTF_8));
					Log.i("REQUEST CHECK", request.toString());
					
				} catch (UnsupportedEncodingException e) {
					Log.e(CLASS_NAME, e.getMessage());
				}
			}
		} else if (method.equals(HTTPHelper.METHOD_PUT)) {
			request = new HttpPut(url);
		} else if (method.equals(HTTPHelper.METHOD_DELETE)) {
			request = new HttpDelete(url);
		}

		if (request != null) {
			execute(request);
		}
	}	
	

	/**
	 * Execute an HTTP request.
	 * 
	 * @param request
	 */
	private void execute(HttpUriRequest request) {
		Log.d(CLASS_NAME, "Before request executed");

		try {
			mClient.execute(request, mResponseHandler);
			Log.d(CLASS_NAME, "After request executed");
		} catch (Exception e) {
			Log.e(CLASS_NAME, e.getMessage());
			BasicHttpResponse errorResponse = new BasicHttpResponse(
					new ProtocolVersion("HTTP_ERROR", 1, 1), 500, "ERROR");
			errorResponse.setReasonPhrase(e.getMessage());

			try {
				mResponseHandler.handleResponse(errorResponse);
			} catch (Exception ex) {
				Log.e(CLASS_NAME, ex.getMessage());
			}
		}
	}

	/**
	 * Construct and return HTTP response handler that will notify the specified
	 * Android handler upon HTTP response.
	 * 
	 * @param handler
	 *            Android handler
	 * @return HTTP response handler
	 */
	private static ResponseHandler<String> getResponseHandlerInstance(
			final Handler handler) {
		final ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

			public String handleResponse(final HttpResponse response) {
				Message message = handler.obtainMessage();
				Bundle bundle = new Bundle();
				StatusLine status = response.getStatusLine();
				Log.d(CLASS_NAME,
						"Response status code: " + status.getStatusCode());
				Log.d(CLASS_NAME,
						"Response reason phrase: " + status.getReasonPhrase());
				HttpEntity entity = response.getEntity();
				String result = null;
				if (entity != null) {
					try {
						result = HTTPHelper.inputStreamToString(entity
								.getContent());
						bundle.putString("RESPONSE", result);
						message.setData(bundle);
						handler.sendMessage(message);
					} catch (IOException e) {
						Log.e(CLASS_NAME, e.getMessage());
						bundle.putString("RESPONSE", "Error: " + e.getMessage());
						message.setData(bundle);
						handler.sendMessage(message);
					}
				} else {
					Log.w(CLASS_NAME, "Empty HTTP response");
					bundle.putString("RESPONSE", "Error: "
							+ response.getStatusLine().getReasonPhrase());
					message.setData(bundle);
					handler.sendMessage(message);
				}
				return result;
			}
		};
		return responseHandler;
	}

	/**
	 * Convert stream into a string.
	 * 
	 * @param stream
	 * @return string representation of stream
	 * @throws IOException
	 */
	private static String inputStreamToString(final InputStream stream)
			throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}
		br.close();
		return sb.toString();
	}
}
