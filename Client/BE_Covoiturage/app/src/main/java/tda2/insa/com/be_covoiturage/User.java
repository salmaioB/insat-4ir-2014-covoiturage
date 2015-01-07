package tda2.insa.com.be_covoiturage;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by remi on 04/01/15.
 */
public class User {
	private AuthToken _authToken;
	private ArrayList<Route> _routes;
	private String _firstName, _lastName;
	private boolean _isDriver;

	User(AuthToken authToken, JSONObject userInfo) {
		_authToken = authToken;
		_routes = new ArrayList<>();

		_firstName = "";
		_lastName = "";
		_isDriver = false;

		Log.e("Creating user with info", userInfo.toString());
	}

	public ArrayList<Route> getRoutes() {
		return _routes;
	}

	public void addRoute(Route r) {
		_routes.add(r);
	}

	public void removeRoute(Route r) {
		_routes.remove(r);
	}

	public boolean isDriver() {
		return _isDriver;
	}

	public String getFirstName() {
		return _firstName;
	}

	public String getLastName() {
		return _lastName;
	}

	public AuthToken getAuthToken() {
		return _authToken;
	}
}
