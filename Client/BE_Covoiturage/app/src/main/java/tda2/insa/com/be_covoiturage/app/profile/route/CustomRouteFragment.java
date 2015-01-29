package tda2.insa.com.be_covoiturage.app.profile.route;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tda2.insa.com.be_covoiturage.app.DataFragment;
import tda2.insa.com.be_covoiturage.app.Workplace;
import tda2.insa.com.be_covoiturage.app.profile.ProfileViewActivity;
import tda2.insa.com.be_covoiturage.network.MyJSONObject;
import tda2.insa.com.be_covoiturage.network.Network;

/**
 * Created by remi on 29/01/15.
 */
public class CustomRouteFragment extends RouteViewFragment implements DataFragment {
	ArrayAdapter<String> _weekdayAdapter;
	Route.Weekday _currentDay;

	public CustomRouteFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = super.onCreateView(inflater, container, savedInstanceState);

		_active.setVisibility(View.INVISIBLE);
		_notifyMe.setVisibility(View.INVISIBLE);
		_driver.setVisibility(View.VISIBLE);
		_city.setVisibility(View.VISIBLE);
		_zipCode.setVisibility(View.VISIBLE);
		_weekday.setVisibility(View.VISIBLE);

		_currentDay = Route.Weekday.Monday;

		_weekday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				_currentDay = Route.Weekday.values()[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		String[] weekdays = new String[]{"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};
		_weekdayAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, weekdays);
		_weekday.setAdapter(_weekdayAdapter);

		_active.setText("Je recherche un trajet personnalisé :");

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected String initialAddress() {
		return Workplace.getWorkplaces().get(0).getAddress();
	}

	@Override
	protected void search(final boolean direction) {
		MyJSONObject obj = new MyJSONObject();
		obj.put("name", _user.getAuthToken().getEmail());
		obj.put("weekday", _currentDay.toString());
		obj.put("direction", direction);
		obj.put("place", Workplace.getWorkplaces().get(_worplaces.getSelectedItemPosition()).getID());
		obj.put("city", _city.getText().toString());
		obj.put("zip", _zipCode.getText().toString());
		String[] time;
		if(direction == false) {
			time = _startTime.getText().toString().split(":");
		}
		else {
			time = _endTime.getText().toString().split(":");
		}

		obj.put("hour", Integer.parseInt(time[0]));
		obj.put("minute", Integer.parseInt(time[0]));
		obj.put("driver", _driver.isChecked());

		Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("searchRoutes"), _user.getAuthToken(), obj, new Network.NetworkResponseListener() {
			@Override
			public void onResponse(JSONObject data, JSONObject headers) {
				try {
					final JSONArray arr = data.getJSONArray("value");
					((ProfileViewActivity) CustomRouteFragment.this.getActivity()).switchToSearchMatches(arr, direction);
				} catch(JSONException e) {
					Log.e("sjiojio", e.getMessage());
				}
			}
		}, new Network.NetworkErrorListener() {
			@Override
			public void onError(String reason, VolleyError error) {
				Log.e("reason", reason);
			}
		});
	}

	@Override
	public void onExit() {

	}
}