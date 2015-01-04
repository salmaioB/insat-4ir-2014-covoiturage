package tda2.insa.com.be_covoiturage;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by remi on 04/01/15.
 */
public class User {
	private AuthToken _authToken;

	User(AuthToken authToken, JSONObject userInfo) {
		_authToken = authToken;
		Log.e("Creating user with info", userInfo.toString());
	}

	public AuthToken getAuthToken() {
		return _authToken;
	}
}
