package tda2.insa.com.be_covoiturage;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by remi on 07/01/15.
 */
public class RouteAdapter extends ArrayAdapter<Route> {
	public RouteAdapter(Context context, ArrayList<Route> routes) {
		super(context, 0, routes);
	}

	private static View _view;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Route route = getItem(position);

		if(convertView == null) {
			convertView = ((LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.route_item, parent, false);
			//((LinearLayout)convertView).addView(new com.google.android.gms.maps.MapFragment().getView());
			//parent.
		}
        /* map is already there, just return view as it is  */
		//}
		TextView weekday = (TextView)convertView.findViewById(R.id.week_day);
		weekday.setText(route.getWeekdayName() + " de " + route.getStartHour() + ":" + route.getStartMinute() + " à " + route.getEndHour() + ":" + route.getEndMinute());

		return convertView;
	}
}
