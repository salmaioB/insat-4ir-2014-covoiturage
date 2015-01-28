/**
 * ***************************************************************************
 */
/* server/src/covoit/Route.java                                    2015-01-07 */
/* Covoiturage Sopra - INSA Toulouse                                    Julie */
/**
 * ***************************************************************************
 */
package covoit;

import java.math.BigDecimal;
import javax.json.*;
import java.util.ArrayList;

public class Route {

    private int _idPlace;
    private Weekday _weekday;
    private int _startHour, _startMinute;
    private int _endHour, _endMinute;
    private boolean _notifyUser;

    public enum Weekday {

        Monday, Tuesday, Wednesday, Thursday, Friday, Saturday
    };

    public Route() {
        _idPlace = -1;
        _startHour = 8;
        _startMinute = 0;
        _endHour = 18;
        _endMinute = 0;
        _weekday = Weekday.Monday;
        _notifyUser = false;
    }

    public Route(JsonObject o) {
        try {
            _idPlace = o.getInt("placeID");
            _startHour = o.getInt("startHour");
            _startMinute = o.getInt("startMinute");
            _endHour = o.getInt("endHour");
            _endMinute = o.getInt("endMinute");
            _weekday = Weekday.valueOf(o.getString("weekday"));
            _notifyUser = o.getBoolean("notify");
        } catch (Exception e) {
        };

    }

	public static String getWeekdayName(Weekday day) {
		switch(day) {
			case Monday:
				return "lundi";
			case Tuesday:
				return "mardi";
			case Wednesday:
				return "mercredi";
			case Thursday:
				return "jeudi";
			case Friday:
				return "vendredi";
			case Saturday:
				return "samedi";
		}

		return null;
	}

	public boolean getNotifyUser() {
        return _notifyUser;
    }

    public int getPlaceID() {
        return _idPlace;
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

    public Weekday getWeekday() {
        return _weekday;
    }

    public void setNotifyUser(boolean notify) {
        _notifyUser = notify;
    }

    public void setPlaceID(int id) {
        _idPlace = id;
    }

    public void setWeekday(Weekday weekday) {
        _weekday = weekday;
    }

    public void setStartTime(int hour, int minute) {
        _startHour = hour;
        _startMinute = minute;
    }

    public void setEndTime(int hour, int minute) {
        _endHour = hour;
        _endMinute = minute;
    }

    public JsonObject getJsonObjectRoute() {
        JsonObjectBuilder pl = Json.createObjectBuilder();
        pl.add("placeID", this.getPlaceID())
                .add("startHour", this.getStartHour())
                .add("startMinute", this.getStartMinute())
                .add("endHour", this.getEndHour())
                .add("endMinute", this.getEndMinute())
                .add("weekday", this.getWeekday().toString())
                .add("notify", this.getNotifyUser());
        return pl.build();
    }

    public static JsonArray getJsonObjectRoutes(ArrayList<Route> routes) {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        if (routes != null) {
            for (int i = 0; i < routes.size(); i++) {
                jab.add(routes.get(i).getJsonObjectRoute());
            }
            return jab.build();
        } else {
            return null;
        }
    }
}
