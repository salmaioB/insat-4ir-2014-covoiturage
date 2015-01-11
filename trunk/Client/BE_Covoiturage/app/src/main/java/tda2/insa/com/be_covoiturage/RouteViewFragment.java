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
	private User _user;
	private static Route _route;
	private Button _startTime, _endTime;
	private Spinner _worplaces;
	private ArrayAdapter<String> _workplacesAdapter;
	private CheckBox _active;
	private static MapFragment _map;

	private static RouteViewFragment _instance;

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
				newFragment.show(RouteViewFragment.this.getFragmentManager(), "timePicker");
			}
		});

		_endTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimePickerFragment newFragment = new TimePickerFragment();
				newFragment.setEnd();
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

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		_instance = this;

		_active.setText("Je recherche un trajet pour " + _route.getWeekdayName());
		if(!_route.active()) {
			_map.getView().setVisibility(View.INVISIBLE);
		}

		_active.setChecked(_route._active);
		this.setActive(_route.active());
	}

	private void setActive(boolean active) {
		if(active) {
			_startTime.setEnabled(true);
			_endTime.setEnabled(true);
			_worplaces.setEnabled(true);

			_startTime.setText(_route.getStartTime());
			_endTime.setText(_route.getEndTime());
			_worplaces.setSelection(Workplace.getWorkplaces().indexOf(_route.getWorkspace()));

			this.getActivity().getFragmentManager().beginTransaction().show(_map).commit();
		}
		else {
			_startTime.setEnabled(false);
			_endTime.setEnabled(false);
			_worplaces.setEnabled(false);

			this.getActivity().getFragmentManager().beginTransaction().hide(_map).commit();
		}
	}

	public static void updateTime() {
		_instance._startTime.setText(_route.getStartTime());
		_instance._endTime.setText(_route.getEndTime());
	}

	public static void setRoute(Route r) {
		_route = r;
	}

	public static Route getRoute() {
		return _route;
	}

	public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
		private boolean _start = true;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Route r = RouteViewFragment.getRoute();

			int hour, minute;

			if(_start) {
				hour = r.getStartHour();
				minute = r.getStartMinute();
			}
			else {
				hour = r.getEndHour();
				minute = r.getEndMinute();
			}

			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		public void setEnd() {
			_start = false;
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Route r = RouteViewFragment.getRoute();

			if(_start) {
				r.setStartTime(hourOfDay, minute);
			}
			else {
				r.setEndTime(hourOfDay, minute);
			}

			RouteViewFragment.updateTime();
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
