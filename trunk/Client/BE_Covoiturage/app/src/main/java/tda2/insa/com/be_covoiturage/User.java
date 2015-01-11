package tda2.insa.com.be_covoiturage;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
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

		try {
			_firstName = userInfo.getString("firstName");
			_lastName = userInfo.getString("lastName");
			_isDriver = userInfo.getBoolean("driver");
			_home = new Place();
			_home.setName(userInfo.getString("city"));
			_home.setZipCode(userInfo.getInt("zipCode"));

			JSONArray routes = userInfo.getJSONArray("routes");
			for(int i = 0; i < routes.length(); ++i) {
				JSONObject object = routes.getJSONObject(i);

				Route route = new Route();

				route.setWorkspace(Workplace.getWorkplaces().get(new Integer(object.getInt("placeID"))));
				route.setStartTime(object.getInt("startHour"), object.getInt("startMinute"));
				route.setEndTime(object.getInt("endHour"), object.getInt("endMinute"));
				route.setWeekday(Route.Weekday.valueOf(object.getString("weekday")));
			}
		}
		catch (JSONException e) {
			Log.e("User creation error:", e.getMessage());
		}
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
