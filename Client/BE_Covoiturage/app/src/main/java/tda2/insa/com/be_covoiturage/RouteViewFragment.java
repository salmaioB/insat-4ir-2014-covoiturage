package tda2.insa.com.be_covoiturage;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;

/**
 * Created by remi on 11/01/15.
 */
public class RouteViewFragment extends Fragment {
	private User _user;
	private static Route _route;
	private Button _startTime, _endTime;
	private Spinner _worplaces, _weekday;
	private Button _removeRoute;
	private static RouteViewFragment _instance;

	public RouteViewFragment() {}

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

		_worplaces = (Spinner)rootView.findViewById(R.id.workplace);
		_weekday = (Spinner)rootView.findViewById(R.id.week_day);

		_removeRoute = (Button)rootView.findViewById(R.id.remove_route);


		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		_instance = this;

		_startTime.setText(_route.getStartTime());
		_endTime.setText(_route.getEndTime());

		// TODO: autres champs
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
}
