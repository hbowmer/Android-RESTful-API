package com.badgeville;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.badgeville.helper.BVHelper;

/**
 * Example of an Android activity leveraging the BVHelper class to invoke
 * Badgeville's REST APIs.
 */
public class BadgevilleClient extends Activity {

	private final Handler mHandler = new Handler() {
		public void handleMessage(final Message msg) {
			String responseString = msg.getData().getString("RESPONSE");
			try {
				JSONObject responseJson = new JSONObject(responseString);
				// Do something!
				Toast.makeText(BadgevilleClient.this, responseJson.toString(), Toast.LENGTH_LONG).show();
				Log.i("RESPONSE JSON", responseJson.toString());
			} catch (JSONException je) {
				// Handle!
				Toast.makeText(BadgevilleClient.this, responseString, Toast.LENGTH_LONG).show();
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main); 

		BVHelper helper = new BVHelper("staging.badgeville.com",
				"e020e02a4c076a06eb6a2786a0700fbb", mHandler);
		
		
//		String deleteId = "500491f5a768650207000001";
//		helper.delete(BVHelper.USERS, deleteId);
		
		HashMap<String, HashMap> params = new HashMap<String, HashMap>();
		HashMap<String, String> inside = new HashMap<String, String>();
		inside.put("email", "jimmy@badgeville.com");
		inside.put("site", "holtbow.com");
		inside.put("first_name", "Jimmy");
		inside.put("last_name", "Zhang");
		inside.put("display_name", "jayjzee23");
		inside.put("nickname", "Jingshen");
		params.put("player", inside);
		
		Log.e("HASHMAP CHECK", inside.toString());
		
		helper.create(BVHelper.PLAYERS, inside);
	}
}