package com.badgeville.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
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
