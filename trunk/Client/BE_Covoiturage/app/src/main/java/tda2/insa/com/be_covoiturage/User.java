package tda2.insa.com.be_covoiturage;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Created by remi on 04/01/15.
 */
public class User {
	private AuthToken _authToken;
	private Route[] _routes;
	private String _firstName, _lastName;
	private boolean _isDriver;
	private Place _home;
    private String _email;
    private boolean _notifyByMail;
    private boolean _notifyByPush;

	User(AuthToken authToken, JSONObject userInfo) {
		_authToken = authToken;
		_routes = new Route[Route.Weekday.values().length];

		for(Route.Weekday day : Route.Weekday.values()) {
			_routes[day.ordinal()] = new Route(day);
		}

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

				Log.e(object.getString("weekday"), object.toString());

				Route.Weekday wd = Route.Weekday.valueOf(object.getString("weekday"));
				int index = wd.ordinal();

				Route route = _routes[index];

				route.setActive(true);
				route.setWorkplace(Workplace.getWithID(object.getInt("placeID")));
				route.setStartTime(object.getInt("startHour"), object.getInt("startMinute"));
				route.setEndTime(object.getInt("endHour"), object.getInt("endMinute"));
				route.setNotifyMe(object.getBoolean("notify"));
			}
		}
		catch (JSONException e) {
			Log.e("User creation error:", e.getMessage());
		}
	}

	public String getAddress() {
		return _home.getPrettyZipCode() + " " + _home.getName() + ", " + "France";
	}

	public Route getRoute(Route.Weekday day) {
		return _routes[day.ordinal()];
	}

	public Route[] getRoutes() {
		return _routes;
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

    public String getEmail() { return _email; }

    public void setEmail(String email) {_email = email; }

    public boolean getNotifyByMail() { return _notifyByMail; }

    public void setNotifyByMail(boolean d) {
        _notifyByMail = d;
    }

    public boolean getNotifyByPush() { return _notifyByPush; }

    public void setNotifyByPush(boolean d) { _notifyByPush = d; }

}
