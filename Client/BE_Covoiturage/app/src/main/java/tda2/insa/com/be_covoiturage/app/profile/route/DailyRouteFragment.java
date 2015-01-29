package tda2.insa.com.be_covoiturage.app.profile.route;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import tda2.insa.com.be_covoiturage.R;
import tda2.insa.com.be_covoiturage.app.DataFragment;
import tda2.insa.com.be_covoiturage.app.MyApplication;
import tda2.insa.com.be_covoiturage.app.Workplace;
import tda2.insa.com.be_covoiturage.app.profile.ProfileViewActivity;
import tda2.insa.com.be_covoiturage.network.MyJSONObject;
import tda2.insa.com.be_covoiturage.network.Network;

/**
 * Created by remi on 29/01/15.
 */
public class DailyRouteFragment extends RouteViewFragment implements DataFragment {
	private Route _route;
	private boolean _modifyRoute;

	public DailyRouteFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_route = MyApplication.getUser().getRoute(Route.Weekday.valueOf(this.getArguments().getString(WEEK_DAY)));

		View rootView = super.onCreateView(inflater, container, savedInstanceState);

		_active.setVisibility(View.VISIBLE);
		_notifyMe.setVisibility(View.VISIBLE);
		_driver.setVisibility(View.GONE);
		_city.setVisibility(View.GONE);
		_zipCode.setVisibility(View.GONE);
		_weekday.setVisibility(View.GONE);

		_active.setText("Je recherche un trajet pour " + _route.getWeekdayName());
		if(!_route.active()) {
			_map.getView().setVisibility(View.INVISIBLE);
		}

		_active.setChecked(_route.active());
		this.setActive(_route.active());
		_active.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DailyRouteFragment.this.setActive(_active.isChecked());
				if (_active.isChecked()) {
					DailyRouteFragment.this.getFragmentManager().executePendingTransactions();
					Handler handler=new Handler();
					Runnable r = new Runnable() {
						public void run() {
							DailyRouteFragment.this.updateMap();
						}
					};
					handler.postDelayed(r, 200);
				}
			}
		});

		ProfileViewActivity.setRoute(_route);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		_active.setChecked(_route.active());
		this.setActive(_route.active());
	}

	@Override
	protected String initialAddress() {
		return _route.getWorkplace().getAddress();
	}

	private void setActive(boolean active) {
		_modifyRoute = true;
		if(active) {
			_notifyMe.setEnabled(true);
			_startTime.setEnabled(true);
			_endTime.setEnabled(true);
			_worplaces.setEnabled(true);

			_notifyMe.setChecked(_route.getNotifyMe());
			_startTime.setText(_route.getStartTime());
			_endTime.setText(_route.getEndTime());
			_worplaces.setSelection(Workplace.getWorkplaces().indexOf(_route.getWorkplace()));

			this.getFragmentManager().beginTransaction().show(_map).commit();
		}
		else {
			_notifyMe.setEnabled(false);
			_startTime.setEnabled(false);
			_endTime.setEnabled(false);
			_worplaces.setEnabled(false);

			this.getFragmentManager().beginTransaction().hide(_map).commit();
		}
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
		Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest(command), _user.getAuthToken(), obj, new Network.NetworkResponseListener() {
			@Override
			public void onResponse(JSONObject data, JSONObject headers) {
				DialogFragment f = new DialogFragment() {
					private CheckBox _go, _return, _drivers;


					@Override
					public Dialog onCreateDialog(Bundle savedInstanceState) {
						final Dialog dialog = new Dialog(getActivity());

						dialog.setTitle("Notification :");
						dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
								WindowManager.LayoutParams.FLAG_FULLSCREEN);
						dialog.setContentView(R.layout.route_notification);

						TextView title = (TextView)dialog.findViewById(R.id.title);
						title.setText("Souhaitez-vous informer les personnes intéressées par votre trajet du " + _route.getWeekdayName() + " ?");
						_go = (CheckBox) dialog.findViewById(R.id.go_id);
						_return = (CheckBox) dialog.findViewById(R.id.return_id);
						_drivers = (CheckBox) dialog.findViewById(R.id.drivers_id);
						if(!_user.isDriver()) {
							_drivers.setVisibility(View.INVISIBLE);
						}
						else {
							_drivers.setVisibility(View.VISIBLE);
						}
						Button notify = (Button)dialog.findViewById(R.id.notify);
						Button dismiss = (Button)dialog.findViewById(R.id.dismiss);
						notify.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								MyJSONObject obj = new MyJSONObject();
								obj.put("go", _go.isChecked());
								obj.put("return", _return.isChecked());
								if(_user.isDriver()) {
									obj.put("drivers", _drivers.isChecked());
								}

								obj.put("name", _user.getAuthToken().getEmail());
								obj.put("weekday", _route.getWeekday().toString());
								obj.put("address", _user.getHome().getName());
								obj.put("zipCode", _user.getHome().getZipCode());
								obj.put("place", _route.getWorkplace().getID());

								Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("notifyNewRoute"), _user.getAuthToken(), obj, new Network.NetworkResponseListener() {
									@Override
									public void onResponse(JSONObject data, JSONObject headers) {
										Log.e("success", data.toString());
									}
								}, new Network.NetworkErrorListener() {
									@Override
									public void onError(String reason, VolleyError error) {
										Log.e("error", reason);
									}
								});

								dismiss();
							}
						});
						dismiss.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dismiss();
							}
						});

						return dialog;
					}
				};

				f.show(ProfileViewActivity.getActivity().getFragmentManager(), "");
			}
		}, null);
	}

	@Override
	protected void search(final boolean direction) {
		_modifyRoute = false;
		MyJSONObject obj = new MyJSONObject();
		obj.put("name", _user.getAuthToken().getEmail());
		obj.put("weekday", _route.getWeekday().toString());
		obj.put("direction", direction);

		Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("searchRoutes"), _user.getAuthToken(), obj, new Network.NetworkResponseListener() {
			@Override
			public void onResponse(JSONObject data, JSONObject headers) {
				try {
					final JSONArray arr = data.getJSONArray("value");
					((ProfileViewActivity) DailyRouteFragment.this.getActivity()).switchToSearchMatches(arr, direction);
				} catch(JSONException e) {
					Log.e("sjiojio", e.getMessage());
				}
			}
		}, new Network.NetworkErrorListener() {
			@Override
			public void onError(String reason, VolleyError error) {
				Log.e("reason", reason);
			}
		});
	}

	@Override
	public void onExit() {
		if(_modifyRoute) {
			if (_route.active()) {
				// On modifie le trajet
				if (_active.isChecked()) {
					DailyRouteFragment.this.updateRoute("modifyRoute");
					_route.invalidateMap();
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
					DailyRouteFragment.this.updateRoute("addRoute");
					_route.invalidateMap();
				}
			}
		}
	}
}
