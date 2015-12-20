package com.nifino.login;


import io.fabric.sdk.android.Fabric;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthConfig;
import com.digits.sdk.android.DigitsAuthConfig.Builder;
import com.digits.sdk.android.DigitsOAuthSigning;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;

public class TwitterDigits extends CordovaPlugin {

	private static final String LOG_TAG = "twitter-digits";

	private TwitterCore twitter = null;

	private Digits digits = null;
	
	private AuthCallback authCallback= null;
	
	private TwitterAuthConfig authConfig = null;

	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		authConfig = new TwitterAuthConfig(getTwitterKey(),
				getTwitterSecret());

		this.twitter = new TwitterCore(authConfig);
		this.digits = new Digits();

		Fabric.with(cordova.getActivity(), twitter, this.digits);
		
		this.digits.getFabric().setCurrentActivity(cordova.getActivity());
		Log.v(LOG_TAG, "Initialize TwitterDigits");
	}

	private String getTwitterKey() {
		return preferences.getString("TwitterConsumerKey", "");
	}

	private String getTwitterSecret() {
		return preferences.getString("TwitterConsumerSecret", "");
	}

	private String getCustomTheme() {
		return preferences.getString("CustomTheme", "");
	}

	public boolean execute(final String action, JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		Log.v(LOG_TAG, "Received: " + action);
		final Activity activity = this.cordova.getActivity();

		cordova.setActivityResultCallback(this);
		
		this.authCallback = new AuthCallback() {
			@Override
			public void success(DigitsSession session, String phoneNumber) {
				Log.v(LOG_TAG, "Callback returned successfully");
				callbackContext.success(handleResult(session, phoneNumber));
			}

			@Override
			public void failure(DigitsException exception) {
				Log.v(LOG_TAG,
						"Callback returned with exception:"
								+ exception.getMessage());
				callbackContext.error(handleFault(exception));
			}
		};

		if (action.equals("login")) {
			login(activity.getApplication());
			Log.v(LOG_TAG, "Successfully executed login method");
			return true;
		}
		return false;
	}

	private void login(final Application app) {
		Log.v(LOG_TAG, "Create AuthCallback");

		Builder configBuilder = null;

		if (getCustomTheme().isEmpty()) {
			Log.v(LOG_TAG, "Call Digits without a custom theme");
			configBuilder = new DigitsAuthConfig.Builder()
			.withAuthCallBack(authCallback);
		} else {
			Log.v(LOG_TAG, "Call Digits with the custom theme:'"
					+ getCustomTheme() + "'");
			configBuilder = new DigitsAuthConfig.Builder()
			.withAuthCallBack(authCallback).withThemeResId(app.getResources().getIdentifier(getCustomTheme(),
					"style", app.getPackageName()));
		}

		final Builder digitsAuthConfigBuilder = configBuilder;
		
		cordova.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Digits.authenticate(digitsAuthConfigBuilder.build());
			}
		});
	}

	private JSONObject handleResult(DigitsSession result, String phoneNumber) {
		JSONObject response = new JSONObject();
		try {
			TwitterAuthToken authToken = (TwitterAuthToken) result
					.getAuthToken();
			
			DigitsOAuthSigning oauthSigning = new DigitsOAuthSigning(authConfig, authToken);
			
			response.put("userId", result.getId());
			response.put("secret", authToken.secret);
			response.put("token", authToken.token);
			response.put("phoneNumber", phoneNumber);
			
			response.put("verifyCredentialsAuthHeader", oauthSigning.getOAuthEchoHeadersForVerifyCredentials().get("X-Verify-Credentials-Authorization"));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return response;
	}

	private JSONObject handleFault(DigitsException fault) {
		JSONObject response = new JSONObject();
		try {
			response.put("errorCode", fault.getErrorCode());
			response.put("message", fault.getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Log.v(LOG_TAG, "activity result: " + requestCode + ", code: "
				+ resultCode);
	}
}