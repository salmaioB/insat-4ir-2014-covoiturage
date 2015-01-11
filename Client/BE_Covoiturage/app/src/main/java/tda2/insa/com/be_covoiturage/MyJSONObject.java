package tda2.insa.com.be_covoiturage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by remi on 10/01/15.
 */
public class MyJSONObject extends JSONObject {
	@Override
	public JSONObject put(String name, int value) {
		try {
			super.put(name, value);
		} catch(JSONException e) {}

		return this;
	}

	@Override
	public JSONObject put(String name, boolean value) {
		try {
			super.put(name, value);
		} catch(JSONException e) {}

		return this;
	}

	@Override
	public JSONObject put(String name, double value) {
		try {
			super.put(name, value);
		} catch(JSONException e) {}

		return this;
	}

	@Override
	public JSONObject put(String name, long value) {
		try {
			super.put(name, value);
		} catch(JSONException e) {}

		return this;
	}

	@Override
	public JSONObject put(String name, Object value) {
		try {
			super.put(name, value);
		} catch(JSONException e) {}

		return this;
	}
}
