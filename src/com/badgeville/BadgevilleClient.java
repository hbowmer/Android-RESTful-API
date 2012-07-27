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
			String responseString = 
					msg.getData().getString("RESPONSE");
			try {
				// Do something!
				JSONObject responseJson = new JSONObject(responseString);
				Toast.makeText(BadgevilleClient.this, responseJson.toString(), Toast.LENGTH_LONG).show();
				Log.i("RESPONSE JSON", responseJson.toString());
			} catch (JSONException je) {
				// Handle!
				Toast.makeText(BadgevilleClient.this, 
						responseString, Toast.LENGTH_LONG).show();
			}
		}
	};
	
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main); 

		BVHelper helper = new BVHelper("staging.badgeville.com",
				"e020e02a4c076a06eb6a2786a0700fbb", mHandler);
		
//		String deleteId = "50089a89a768650cb300012b";
//		helper.delete(BVHelper.ACTIVITY_DEFINITIONS, deleteId);
//		data.put("site_id", "<site_id>");
//		data.put("name", "Reporter");
//		data.put("selector", "{\"verb\":\"vote\"}");
//		data.put("adjustment", ":5");
		
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("reward_definition[name]", "Troubleshoot");
		data.put("reward_definition[reward_template]", 
				"%7B%22message%22%3A%22Awesome%22%7D");
		
		String objectId = "50107480a76865045b00000f";
		helper.update(BVHelper.REWARD_DEFINTIONS, objectId, data);
		
		Log.e("HASHMAP CHECK", data.toString());
	}
}