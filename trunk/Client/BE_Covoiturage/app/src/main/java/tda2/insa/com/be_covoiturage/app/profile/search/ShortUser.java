package tda2.insa.com.be_covoiturage.app.profile.search;

import org.json.JSONObject;

/**
 * Created by remi on 14/01/15.
 */
public class ShortUser {

	private String _name; // l'adresse email
	private String _firstName;
	private String _lastName;
	private int _hour;
	private int _minute;
	private boolean _driver;

	public ShortUser(JSONObject obj) {
		try {
			_name = obj.getString("name");
			_firstName = obj.getString("firstName");
			_lastName = obj.getString("lastName");
			_hour = obj.getInt("hour");
			_minute = obj.getInt("minute");
			_driver = obj.getBoolean("driver");
		} catch(Exception e) {}
	}

	public String getName() {
		return _name;
	}

	public String getFirstName() {
		return _firstName;
	}

	public String getLastName() {
		return _lastName;
	}

	public int getHour() {
		return _hour;
	}

	public int getMinute() {
		return _minute;
	}

	public boolean getDriver() {
		return _driver;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setFirstName(String firstName) {
		_firstName = firstName;
	}

	public void setLastName(String lastName) {
		_lastName = lastName;
	}

	public void setHour(int hour) {
		_hour = hour;
	}

	public void setMinute(int minute) {
		_minute = minute;
	}

	public void setDriver (boolean driver) {
		_driver = driver;
	}
}