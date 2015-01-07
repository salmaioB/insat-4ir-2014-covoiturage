package tda2.insa.com.be_covoiturage;

/**
 * Created by remi on 07/01/15.
 */
public class Route {
	private String _start;
	private String _end;
	private Weekday _weekday;
	private int _startHour, _startMinute;
	private int _endHour, _endMinute;

	public enum Weekday {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday};

	public Route() {
		_start = "";
		_end = "";
		_startHour = 8;
		_startMinute = 0;
		_endHour = 18;
		_endMinute = 0;

		_weekday = Weekday.Monday;
	}

	public String getStart() {
		return _start;
	}

	public void setStart(String start) {
		_start = start;
	}

	public String getEnd() {
		return _end;
	}

	public void setEnd(String end) {
		_end = end;
	}

	public Weekday getWeekday() {
		return _weekday;
	}

	public void setStart(Weekday weekday) {
		weekday = weekday;
	}

	public String getWeekdayName() {
		return Route.getWeekdayName(_weekday);
	}

	public static String getWeekdayName(Weekday day) {
		switch(day) {
			case Monday:
				return MyApplication.getAppContext().getString(R.string.monday);
			case Tuesday:
				return MyApplication.getAppContext().getString(R.string.tuesday);
			case Wednesday:
				return MyApplication.getAppContext().getString(R.string.wednesday);
			case Thursday:
				return MyApplication.getAppContext().getString(R.string.thursday);
			case Friday:
				return MyApplication.getAppContext().getString(R.string.friday);
			case Saturday:
				return MyApplication.getAppContext().getString(R.string.saturday);
		}

		return null;
	}

	public void setStartTime(int hour, int minute) {
		_startHour = hour;
		_startMinute = minute;
	}


	public void setEndTime(int hour, int minute) {
		_endHour = hour;
		_endMinute = minute;
	}

	public int getStartHour() {
		return _startHour;
	}

	public int getStartMinute() {
		return _startMinute;
	}

	public int getEndHour() {
		return _endHour;
	}

	public int getEndMinute() {
		return _endMinute;
	}

}
