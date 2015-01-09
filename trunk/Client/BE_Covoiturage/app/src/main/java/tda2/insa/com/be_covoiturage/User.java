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
	private Place _home;

	User(AuthToken authToken, JSONObject userInfo) {
		_authToken = authToken;
		_routes = new ArrayList<>();

		_firstName = "";
		_lastName = "";
		_isDriver = false;
		_home = new Place();

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

	public void setDriver(boolean d) {
		_isDriver = d;
	}

	public String getFirstName() {
		return _firstName;
	}

	public void setFirstName(String name) {
		_firstName = name;
	}

	public String getLastName() {
		return _lastName;
	}

	public void setLastName(String name) {
		_lastName = name;
	}

	public Place getHome() {
		return _home;
	}

	public AuthToken getAuthToken() {
		return _authToken;
	}
}
