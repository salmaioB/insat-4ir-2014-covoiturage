/******************************************************************************/
/* server/src/covoit/Route.java                                     2015-01-07 */
/* Covoiturage Sopra - INSA Toulouse                             		Julie */
/******************************************************************************/

package covoit;

import javax.json.*;

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
	
	public Route (JsonObject o){
		try {
			_start = o.getString("start");
			_end = o.getString("end");
			_startHour = o.getInt("startHour");
			_startMinute = o.getInt("startMinute");
			_endHour = o.getInt("endHour");
			_endMinute = o.getInt("endMinute");
		} catch (JSONException e) {};
		
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

	public void setWeekday(Weekday weekday) {
		weekday = weekday;
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
