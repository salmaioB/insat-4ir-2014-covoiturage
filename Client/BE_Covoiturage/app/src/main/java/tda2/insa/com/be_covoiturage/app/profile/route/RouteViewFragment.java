package tda2.insa.com.be_covoiturage.app.profile.route;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.*;
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
import tda2.insa.com.be_covoiturage.network.MyJSONObject;
import tda2.insa.com.be_covoiturage.network.Network;

/**
 *
 * Created by remi on 11/01/15.
 */
public abstract class RouteViewFragment extends Fragment implements OnMapReadyCallback {
	public static String WEEK_DAY = "weekday";
	protected static String HOUR = "hour";
	protected static String MINUTE = "minute";

	protected User _user;
	protected Button _startTime, _endTime;
	protected Spinner _worplaces, _weekday;
	protected ArrayAdapter<String> _workplacesAdapter;
	protected String _workplaceAddress;
	protected Button _searchGo, _searchReturn;

	protected CheckBox _notifyMe;
	protected CheckBox _active;

	protected CheckBox _driver;
	protected EditText _city;
	protected EditText _zipCode;

	protected static MapFragment _map;
	protected static RouteViewFragment _instance;

	public RouteViewFragment() {}

	public static void setMapFragement(MapFragment frag) {
		_map = frag;
		_map.getMapAsync(getInstance());
	}

	public static RouteViewFragment getInstance() {
		return _instance;
	}

	public String fragmentTitle() {
		return "Trajet";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_instance = this;

		View rootView = inflater.inflate(tda2.insa.com.be_covoiturage.R.layout.route_view, container, false);
		_user = MyApplication.getUser();

		_active = (CheckBox)rootView.findViewById(R.id.route_active);
		_notifyMe = (CheckBox)rootView.findViewById(R.id.notify_me);

		_driver = (CheckBox)rootView.findViewById(R.id.driver);
		_city = (EditText)rootView.findViewById(R.id.city);
		_zipCode = (EditText)rootView.findViewById(R.id.zip_code);

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

				newFragment.setParent(RouteViewFragment.this);
				newFragment.show(RouteViewFragment.this.getFragmentManager(), "timePicker");
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
				newFragment.setParent(RouteViewFragment.this);
				newFragment.show(RouteViewFragment.this.getFragmentManager(), "timePicker");
			}
		});

		ArrayList<String> workplacesNames = new ArrayList<>();

		for (Workplace wp : Workplace.getWorkplaces()) {
			workplacesNames.add(wp.getName() + " " + wp.getAddress());
		}

		_weekday = (Spinner)rootView.findViewById(R.id.weekday);
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

		_workplaceAddress = initialAddress();

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

		return rootView;
	}

	protected abstract void search(final boolean direction);

	@Override
	public void onResume() {
		super.onResume();
	}

	protected abstract String initialAddress();

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

	protected void updateMap() {
		if(_map.getView().getWidth() > 0) {
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

	public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
		private boolean _start = true;
		RouteViewFragment _parent;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int hour = this.getArguments().getInt(HOUR);
			int minute = this.getArguments().getInt(MINUTE);

			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		}

		public void setEnd() {
			_start = false;
		}

		public void setParent(RouteViewFragment parent) {
			_parent = parent;
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			_parent.updateTime(hourOfDay, minute, _start);
		}
	}

	public static class MyMapFragment extends MapFragment {
		@Override
		public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			RouteViewFragment.setMapFragement(this);
			return super.onCreateView(inflater, container, savedInstanceState);
		}
	}
}
