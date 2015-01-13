package tda2.insa.com.be_covoiturage;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.DialogFragment;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TimePicker;
import com.google.android.gms.maps.*;

import java.util.ArrayList;

/**
 *
 * Created by remi on 11/01/15.
 */
public class RouteViewFragment extends Fragment {
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
	private Button _save;
	private static MapFragment _map;

	public RouteViewFragment() {}

	public static void setMapFragement(MapFragment frag) {
		_map = frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.route_view, container, false);
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

		_worplaces = (Spinner)rootView.findViewById(R.id.workplace);
		_workplacesAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, workplacesNames);
		_worplaces.setAdapter(_workplacesAdapter);

		_active = (CheckBox)rootView.findViewById(R.id.route_active);
		_active.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RouteViewFragment.this.setActive(_active.isChecked());
			}
		});

		_notifyMe = (CheckBox)rootView.findViewById(R.id.notify_me);

		_route = _user.getRoute(Route.Weekday.valueOf(this.getArguments().getString(WEEK_DAY)));

		_active.setText("Je recherche un trajet pour " + _route.getWeekdayName());
		if(!_route.active()) {
			_map.getView().setVisibility(View.INVISIBLE);
		}

		_active.setChecked(_route._active);
		this.setActive(_route.active());

		_save = (Button)rootView.findViewById(R.id.save_button);
		_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (_route.active()) {
					// On modifie le trajet
					if (_active.isChecked()) {
						RouteViewFragment.this.updateRoute("modifyRoute");
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
					}
				}
			}
		});

		return rootView;
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
	}

	private void setActive(boolean active) {
		if(active) {
			_notifyMe.setEnabled(true);
			_startTime.setEnabled(true);
			_endTime.setEnabled(true);
			_worplaces.setEnabled(true);

			_notifyMe.setChecked(_route.getNotifyMe());
			_startTime.setText(_route.getStartTime());
			_endTime.setText(_route.getEndTime());
			_worplaces.setSelection(Workplace.getWorkplaces().indexOf(_route.getWorkplace()));

			this.getActivity().getFragmentManager().beginTransaction().show(_map).commit();
		}
		else {
			_notifyMe.setEnabled(false);
			_startTime.setEnabled(false);
			_endTime.setEnabled(false);
			_worplaces.setEnabled(false);

			this.getActivity().getFragmentManager().beginTransaction().hide(_map).commit();
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

		//@Override
		//public void onMap
	}
}
