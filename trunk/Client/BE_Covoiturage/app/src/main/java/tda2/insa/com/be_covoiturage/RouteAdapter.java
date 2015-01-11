package tda2.insa.com.be_covoiturage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *
 * Created by remi on 07/01/15.
 */
public class RouteAdapter extends ArrayAdapter<Route> {
	int _width;

	public RouteAdapter(Context context, Route[] routes, int width) {
		super(context, 0, routes);
		_width = width;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Route route = getItem(position);

		if(convertView == null) {
			convertView = ((LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.route_item, parent, false);
		}

 		TextView weekday = (TextView)convertView.findViewById(R.id.week_day);

		ImageView img = (ImageView)convertView.findViewById(R.id.map_view);
		route.setMapView(img, _width, 150);
		if(route.active()) {
			route.updateStaticMap();
			img.setVisibility(View.VISIBLE);
			img.setMinimumHeight(300);
			weekday.setText(route.getWeekdayName() + " de " + route.getStartTime() + " à " + route.getEndTime());
		}
		else {
			weekday.setText(route.getWeekdayName() + " - pas de trajet");
			img.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}
}
