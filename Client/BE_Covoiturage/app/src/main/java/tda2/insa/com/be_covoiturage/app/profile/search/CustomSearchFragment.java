package tda2.insa.com.be_covoiturage.app.profile.search;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tda2.insa.com.be_covoiturage.R;
import tda2.insa.com.be_covoiturage.app.DataFragment;
import tda2.insa.com.be_covoiturage.app.MyApplication;
import tda2.insa.com.be_covoiturage.app.User;
import tda2.insa.com.be_covoiturage.app.Workplace;
import tda2.insa.com.be_covoiturage.app.profile.ProfileViewActivity;
import tda2.insa.com.be_covoiturage.app.profile.route.Route;
import tda2.insa.com.be_covoiturage.app.profile.route.RouteViewFragment;
import tda2.insa.com.be_covoiturage.network.MyJSONObject;
import tda2.insa.com.be_covoiturage.network.Network;

/**
 *
 * Created by remi on 27/01/15.
 */
/*public class CustomSearchFragment extends Fragment implements OnMapReadyCallback, DataFragment {
	public static String WEEK_DAY = "weekday";
	private static String HOUR = "hour";
	private static String MINUTE = "minute";

	private User _user;
	private Button _startTime, _endTime;
	private Spinner _worplaces;
	private ArrayAdapter<String> _workplacesAdapter;
	private CheckBox _active;
	private CheckBox _notifyMe;
	private Route _route;
	private boolean _modifyRoute;
	private String _workplaceAddress;
	private Button _searchGo, _searchReturn;

	private static MapFragment _map;
	private static CustomSearchFragment _instance;

	public CustomSearchFragment() {}

	public static void setMapFragement(MapFragment frag) {
		_map = frag;
		_map.getMapAsync(getInstance());
	}

	public static CustomSearchFragment getInstance() {
		return _instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_instance = this;

		View rootView = inflater.inflate(R.layout.fragment_custom_search, container, false);
		_user = MyApplication.getUser();

		_startTime = (Button)rootView.findViewById(R.id.start_time);
		_endTime = (Button)rootView.findViewById(R.id.end_time);

		_startTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimePickerFragment newFragment = new TimePickerFragment();

				Bundle bundle = new Bundle();

				String[] startTime = _startTime.getText().toString().split(":");
				bundle.putInt(HOUR, Integer.parseInt(startTime[0]));
				bundle.putInt(MINUTE, Integer.parseInt(startTime[1]));
				newFragment.setArguments(bundle);

				newFragment.setParent(CustomSearchFragment.this);
				newFragment.show(CustomSearchFragment.this.getFragmentManager(), "timePicker");
			}
		});

		_endTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimePickerFragment newFragment = new TimePickerFragment();

				Bundle bundle = new Bundle();

				String[] endTime = _endTime.getText().toString().split(":");
				bundle.putInt(HOUR, Integer.parseInt(endTime[0]));
				bundle.putInt(MINUTE, Integer.parseInt(endTime[1]));
				newFragment.setArguments(bundle);

				newFragment.setEnd();
				newFragment.setParent(CustomSearchFragment.this);
				newFragment.show(CustomSearchFragment.this.getFragmentManager(), "timePicker");
			}
		});

		ArrayList<String> workplacesNames = new ArrayList<>();

		for (Workplace wp : Workplace.getWorkplaces()) {
			workplacesNames.add(wp.getName() + " " + wp.getAddress());
		}

		_worplaces = (Spinner)rootView.findViewById(R.id.workplace);
		_worplaces.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				_workplaceAddress = Workplace.getWorkplaces().get(position).getAddress();
				RouteViewFragment.this.updateMap();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		_workplacesAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, workplacesNames);
		_worplaces.setAdapter(_workplacesAdapter);

		_active = (CheckBox)rootView.findViewById(R.id.route_active);

		_notifyMe = (CheckBox)rootView.findViewById(R.id.notify_me);

		_searchGo = (Button)rootView.findViewById(R.id.search_go);
		_searchGo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RouteViewFragment.this.search(false);
			}
		});
		_searchReturn = (Button)rootView.findViewById(R.id.search_return);
		_searchReturn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RouteViewFragment.this.search(true);
			}
		});

		_route = _user.getRoute(Route.Weekday.valueOf(this.getArguments().getString(WEEK_DAY)));
		_workplaceAddress = _route.getWorkplace().getAddress();

		_active.setText("Je recherche un trajet pour " + _route.getWeekdayName());
		if(!_route.active()) {
			_map.getView().setVisibility(View.INVISIBLE);
		}

		_active.setChecked(_route.active());
		this.setActive(_route.active());
		_active.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RouteViewFragment.this.setActive(_active.isChecked());
				if (_active.isChecked()) {
					RouteViewFragment.this.getFragmentManager().executePendingTransactions();
					Handler handler=new Handler();
					Runnable r=new Runnable() {
						public void run() {
							RouteViewFragment.this.updateMap();
						}
					};
					handler.postDelayed(r, 200);
				}
			}
		});

		ProfileViewActivity.setRoute(_route);

		return rootView;
	}

	private void search(final boolean direction) {
		_modifyRoute = false;
		MyJSONObject obj = new MyJSONObject();
		obj.put("name", _user.getAuthToken().getEmail());
		obj.put("weekday", _route.getWeekday().toString());
		obj.put("direction", direction);

		Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("searchRoutes"), _user.getAuthToken(), obj, new Network.NetworkResponseListener() {
			@Override
			public void onResponse(JSONObject data, JSONObject headers) {
				try {
					final JSONArray arr = data.getJSONArray("value");
					((ProfileViewActivity) RouteViewFragment.this.getActivity()).switchToSearchMatches(arr, direction);
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

	private void updateRoute(String command) {
		MyJSONObject obj = new MyJSONObject();
		obj.put("name", _user.getAuthToken().getEmail());

		_route.setActive(true);
		String[] startTime = _startTime.getText().toString().split(":");
		_route.setStartTime(Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1]));

		String[] endTime = _endTime.getText().toString().split(":");
		_route.setEndTime(Integer.parseInt(endTime[0]), Integer.parseInt(endTime[1]));

		_route.setWorkplace(Workplace.getWorkplaces().get(_worplaces.getSelectedItemPosition()));
		_route.invalidateMap();

		_route.setNotifyMe(_notifyMe.isChecked());

		obj.put("route", _route.getJSON());
		Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest(command), _user.getAuthToken(), obj, null, null);
	}

	@Override
	public void onResume() {
		super.onResume();
		_active.setChecked(_route.active());
		this.setActive(_route.active());
	}

	private void setActive(boolean active) {
		_modifyRoute = true;
		if(active) {
			_notifyMe.setEnabled(true);
			_startTime.setEnabled(true);
			_endTime.setEnabled(true);
			_worplaces.setEnabled(true);

			_notifyMe.setChecked(_route.getNotifyMe());
			_startTime.setText(_route.getStartTime());
			_endTime.setText(_route.getEndTime());
			_worplaces.setSelection(Workplace.getWorkplaces().indexOf(_route.getWorkplace()));

			this.getFragmentManager().beginTransaction().show(_map).commit();
		}
		else {
			_notifyMe.setEnabled(false);
			_startTime.setEnabled(false);
			_endTime.setEnabled(false);
			_worplaces.setEnabled(false);

			this.getFragmentManager().beginTransaction().hide(_map).commit();
		}
	}

	public void updateTime(int hour, int minute, boolean start) {
		if(start) {
			_startTime.setText(Route.getPrettyHour(hour, minute));
		}
		else {
			_endTime.setText(Route.getPrettyHour(hour, minute));
		}
	}

	@Override
	public void onMapReady(GoogleMap map) {
		this.updateMap();
	}

	private void updateMap() {
		if(_active.isChecked() && _map.getView().getWidth() > 0) {
			_map.getMap().clear();

			Marker workplaceMarker = null, homeMarker = null;
			LatLng workplace = Route.getLocationFromAddress(_workplaceAddress);
			if (workplace != null) {
				workplaceMarker = _map.getMap().addMarker(new MarkerOptions()
						.position(workplace)
						.title("Lieu de travail"));
			}

			LatLng home = Route.getLocationFromAddress(_user.getAddress());
			if (home != null) {
				homeMarker = _map.getMap().addMarker(new MarkerOptions()
						.position(home)
						.title("Domicile")
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			}

			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			int count = 0;
			LatLng latLng = null;
			if (workplaceMarker != null) {
				latLng = workplaceMarker.getPosition();
				builder.include(latLng);
				++count;
			}
			if (homeMarker != null) {
				latLng = homeMarker.getPosition();
				builder.include(latLng);
				++count;
			}

			if (count < 2) {
				if (latLng == null) {
					// Toulouse
					latLng = new LatLng(43.604482, 1.443962);
				}
				CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 8.0f);
				_map.getMap().animateCamera(cu);
			} else {
				LatLngBounds bounds = builder.build();

				int padding = _map.getView().getWidth() / 6; // offset from edges of the map in pixels
				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
				_map.getMap().animateCamera(cu);
			}
		}
	}

	@Override
	public void onExit() {
		if(_modifyRoute) {
			if (_route.active()) {
				// On modifie le trajet
				if (_active.isChecked()) {
					RouteViewFragment.this.updateRoute("modifyRoute");
					_route.invalidateMap();
				}
				// On supprime le trajet
				else {
					_route.setActive(false);

					MyJSONObject obj = new MyJSONObject();
					obj.put("name", _user.getAuthToken().getEmail());
					obj.put("weekday", _route.getWeekday().toString());
					Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("removeRoute"), _user.getAuthToken(), obj, null, null);
				}
			} else {
				// On créé le trajet
				if (_active.isChecked()) {
					RouteViewFragment.this.updateRoute("addRoute");
					_route.invalidateMap();
				}
			}
		}
	}

	public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
		private boolean _start = true;
		CustomSearchFragment _parent;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int hour = this.getArguments().getInt(HOUR);
			int minute = this.getArguments().getInt(MINUTE);

			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		}

		public void setEnd() {
			_start = false;
		}

		public void setParent(CustomSearchFragment parent) {
			_parent = parent;
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			_parent.updateTime(hourOfDay, minute, _start);
		}
	}

	public static class MyMapFragment extends MapFragment {
		@Override
		public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			CustomSearchFragment.setMapFragement(this);
			return super.onCreateView(inflater, container, savedInstanceState);
		}
	}
}*/
