package tda2.insa.com.be_covoiturage;

import android.util.Log;
import android.widget.ImageView;

import java.io.Serializable;

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

	private Workplace _workplace;

	public enum Weekday {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday}

	public Route(Weekday day) {
		_startHour = 8;
		_startMinute = 0;
		_endHour = 18;
		_endMinute = 0;

		_weekday = day;
		_workplace = Workplace.getWorkplaces().iterator().next();
	}

	public boolean active() {
		return _active;
	}

	public void setActive(boolean active) {
		_active = active;
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
		String res = "";
		if(this.getStartHour() < 10) {
			res = res + "0";
		}

		res = res + Integer.toString(this.getStartHour()) + ":";

		if(this.getStartMinute() < 10) {
			res = res + "0";
		}

		res = res + Integer.toString(this.getStartMinute());

		return res;
	}

	public int getEndHour() {
		return _endHour;
	}

	public int getEndMinute() {
		return _endMinute;
	}

	public String getEndTime() {
		String res = "";
		if(this.getEndHour() < 10) {
			res = res + "0";
		}

		res = res + Integer.toString(this.getEndHour()) + ":";

		if(this.getEndMinute() < 10) {
			res = res + "0";
		}

		res = res + Integer.toString(this.getEndMinute());

		return res;
	}

	public Workplace getWorkspace() {
		return _workplace;
	}
	public void setWorkspace(Workplace wp) {
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
		return 	"https://maps.googleapis.com/maps/api/staticmap?size=" + Integer.toString(_imageWidth) + "x" + Integer.toString(_imageHeight);
	}

	public void updateStaticMap() {
		if(!_mapUpToDate) {
			new ImageDownloader().execute(this.getStaticMapURL(), _imageView);
			Log.e("update", this.getStaticMapURL());
			_mapUpToDate = true;
		}
	}
}
