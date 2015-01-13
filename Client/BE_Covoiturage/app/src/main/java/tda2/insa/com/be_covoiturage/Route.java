package tda2.insa.com.be_covoiturage;

import android.util.Log;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.Serializable;
import java.net.URLEncoder;

/**
 *
 * Created by remi on 07/01/15.
 */
public class Route implements Serializable {
	private Weekday _weekday;
	private int _startHour, _startMinute;
	private int _endHour, _endMinute;
	boolean _active = false;
	private boolean _mapUpToDate = false;
	private ImageView _imageView;
	private int _imageWidth, _imageHeight;
	private boolean _notifyMe;

	private Workplace _workplace;

	public enum Weekday {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday}

	public Route(Weekday day) {
		_startHour = 8;
		_startMinute = 0;
		_endHour = 18;
		_endMinute = 0;

		_notifyMe = true;

		_weekday = day;
		_workplace = Workplace.getWorkplaces().iterator().next();
	}

	public JSONObject getJSON() {
		MyJSONObject route = new MyJSONObject();

		route.put("placeID", this.getWorkplace().getID());
		route.put("startHour", this.getStartHour());
		route.put("startMinute", this.getStartMinute());
		route.put("endHour", this.getEndHour());
		route.put("endMinute", this.getEndMinute());
		route.put("weekday", this.getWeekday().toString());
		route.put("notify", this.getNotifyMe());

		Log.e("route !!", route.toString());

		return route;
	}

	public boolean active() {
		return _active;
	}

	public void setActive(boolean active) {
		_active = active;
	}

	public boolean getNotifyMe() {
		return _notifyMe;
	}

	public void setNotifyMe(boolean notifyMe) {
		_notifyMe = notifyMe;
	}

	public Weekday getWeekday() {
		return _weekday;
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

	public String getStartTime() {
		return Route.getPrettyHour(this.getStartHour(), this.getStartMinute());
	}

	public int getEndHour() {
		return _endHour;
	}

	public int getEndMinute() {
		return _endMinute;
	}

	public String getEndTime() {
		return Route.getPrettyHour(this.getEndHour(), this.getEndMinute());
	}

	public static String getPrettyHour(int hour, int minute) {
		String res = "";

		if(hour < 10) {
			res = res + "0";
		}

		res = res + Integer.toString(hour) + ":";

		if(minute < 10) {
			res = res + "0";
		}

		res = res + Integer.toString(minute);

		return res;
	}

	public Workplace getWorkplace() {
		return _workplace;
	}
	public void setWorkplace(Workplace wp) {
		_workplace = wp;
	}

	public void setMapView(ImageView view, int width, int height) {
		_imageView = view;
		_imageWidth = width;
		_imageHeight = height;
	}

	public void invalidateMap() {
		_mapUpToDate = false;
	}

	public String getStaticMapURL() {
		String url = "https://maps.googleapis.com/maps/api/staticmap?size=" + Integer.toString(_imageWidth) + "x" + Integer.toString(_imageHeight);

		String workplace = _workplace.getAddress();
		String home = MyApplication.getUser().getAddress();

		String[] markers = {"color:green|label:H|" + home, "color:red|label:S|" + workplace};

		for(String m : markers) {
			try {
				url += "&markers=" + URLEncoder.encode(m, "utf-8");
			}
			catch(Exception e){}
		}

		return url;
	}

	public void updateStaticMap() {
		if(!_mapUpToDate) {
			new ImageDownloader().execute(this.getStaticMapURL(), _imageView);
			_mapUpToDate = true;
		}
	}
}
