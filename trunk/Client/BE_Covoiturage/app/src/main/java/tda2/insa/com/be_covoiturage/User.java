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
	private int _postalCode;

	User(AuthToken authToken, JSONObject userInfo) {
		_authToken = authToken;
		_routes = new ArrayList<>();

		_firstName = "";
		_lastName = "";
		_isDriver = false;
		_postalCode = 31000;

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

	public int getPostalCode() {
		return _postalCode;
	}

	public void setPostalCode(int code) {
		_postalCode = code;
	}

	public AuthToken getAuthToken() {
		return _authToken;
	}
}
