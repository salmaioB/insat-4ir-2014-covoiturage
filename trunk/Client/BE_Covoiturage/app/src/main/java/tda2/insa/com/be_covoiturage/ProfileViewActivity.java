package tda2.insa.com.be_covoiturage;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;


public class ProfileViewActivity extends ActionBarActivity {
	ProfileViewFragment _profileViewFragment;
	RouteViewFragment _routeViewFragment;
	IdentityViewFragment _identityViewFragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_view);
		if (savedInstanceState == null) {
			_profileViewFragment = new ProfileViewFragment();
			_routeViewFragment = new RouteViewFragment();
			_identityViewFragment = new IdentityViewFragment();
			this.switchToProfile();
		}
	}

	private void switchToFragment(Fragment fragment) {
		this.getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment)
				.commit();

	}

	public void switchToIdentity() {
		this.switchToFragment(_identityViewFragment);
	}

	public void switchToRoute(Route r) {
		this.switchToFragment(_routeViewFragment);
	}

	public void switchToProfile() {
		this.switchToFragment(_profileViewFragment);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_profile_view, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if(id == R.id.action_change_profile) {
			_profileViewFragment.editIdentity();
			return false;
		}
		else if(id == R.id.action_logout) {
			_profileViewFragment.logout();
			return false;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class ProfileViewFragment extends Fragment {
		private User _user;

		private boolean _canUseMaps;
		private ListView _infosList;
		private ListView _routesList;
		private ArrayAdapter<String> _infos;
		private RouteAdapter _routes;

		public ProfileViewFragment() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_profile_view, container, false);

			Button addRoute = (Button)rootView.findViewById(R.id.add_route);
			addRoute.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					ProfileViewFragment.this.addRoute();
				}
			});


			_infosList = (ListView)rootView.findViewById(R.id.profile_infos);
			_infosList.setClickable(true);

			_routesList = (ListView)rootView.findViewById(R.id.profile_routes);

			String[] values = new String[] {"Identité", "Notifications"};

			_infos = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, values);
			_infosList.setAdapter(_infos);

			_infosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
					if(position == 0) {
						ProfileViewFragment.this.editIdentity();
					}
					else if(position == 1) {
						ProfileViewFragment.this.editNotifications();
					}
				}

			});

			this.loadProfile((AuthToken) (this.getActivity().getIntent().getSerializableExtra(LoginActivity.AUTH_TOKEN)));

			return rootView;
		}

		@Override
		public void onResume() {
			super.onResume();

			int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getActivity());
			if(status != ConnectionResult.SUCCESS) {
				if(GooglePlayServicesUtil.isUserRecoverableError(status)) {
					Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this.getActivity(), 0);
					dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialogInterface) {
							_canUseMaps = false;
						}
					});
					dialog.show();
				}
				else {
					_canUseMaps = false;
				}
			}
			else {
				_canUseMaps = true;
			}

			Log.e("canUseMaps", Boolean.toString(_canUseMaps));
		}

		public void updateMaps() {
			if(_user != null) {
				for (Route r : _user.getRoutes()) {
					r.invalidateMap();
					r.updateStaticMap();
				}
			}
		}

		private void loadProfile(final AuthToken authToken) {
			try {
				JSONObject obj = new JSONObject();
				obj.put("name", authToken.getEmail());

				Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("detailsAccount"), authToken, obj, new Network.NetworkResponseListener() {
							@Override
							public void onResponse(JSONObject data, JSONObject headers) {
								try {
									_user = new User(authToken, data);
									MyApplication.setUser(_user);
									ProfileViewFragment.this.onProfileLoaded();
								} catch (Exception e) {
									ProfileViewFragment.this.loadFailed(e.getMessage());
								}
							}
						},
						new Network.NetworkErrorListener() {
							@Override
							public void onError(String reason, VolleyError error) {
								ProfileViewFragment.this.loadFailed(reason + " " + error.toString());
							}
						});
			}
			// Si le JSONObject.put a échoué, que faire à part pleurer ?
			catch (JSONException e) { }
		}

		private void loadFailed(String message) {
			MyApplication.presentError(this.getActivity(), "Err: " + message, new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					ProfileViewFragment.this.logout();
				}
			});
		}

		private void logout() {
			if(_user != null) {
				Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("logout"), _user.getAuthToken(), new JSONObject(), null, null);
			}

			this.getActivity().finish();

			Intent i = new Intent(this.getActivity(), LoginActivity.class);
			this.startActivity(i);
		}

		public void onProfileLoaded() {
			_routes = new RouteAdapter(this.getActivity(), _user.getRoutes(), this.getView().getWidth());
			_routesList.setAdapter(_routes);

			_routesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
					ProfileViewFragment.this.editRoute(position);
				}

			});

			this.updateMaps();
		}

		public void editIdentity() {
			((ProfileViewActivity)this.getActivity()).switchToIdentity();
        }

		public void editNotifications() {
			Log.e("edit", "Notifications");
		}

		public void editRoute(int index) {
			((ProfileViewActivity)this.getActivity()).switchToRoute(_user.getRoutes().get(index));
		}

		public void addRoute() {
			Route newRoute = new Route();

			_user.getRoutes().add(newRoute);
			_routes.notifyDataSetChanged();
		}
	}

	public static class RouteViewFragment extends Fragment {
		private User _user;
		private Route _route;
		private Button _startTime, _endTime;
		private Spinner _worplaces, _weekday;
		private Button _removeRoute;

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
					DialogFragment newFragment = new TimePickerFragment();
					Bundle bundle = new Bundle();
					bundle.putInt("hour", _route.getStartHour());
					bundle.putInt("minute", _route.getStartMinute());
					newFragment.show(RouteViewFragment.this.getFragmentManager(), "timePicker");
				}
			});

			_endTime.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DialogFragment newFragment = new TimePickerFragment();
					Bundle bundle = new Bundle();
					bundle.putInt("hour", _route.getEndHour());
					bundle.putInt("minute", _route.getEndMinute());
					newFragment.show(RouteViewFragment.this.getFragmentManager(), "timePicker");
				}
			});

			_worplaces = (Spinner)rootView.findViewById(R.id.workplace);
			_weekday = (Spinner)rootView.findViewById(R.id.week_day);

			_removeRoute = (Button)rootView.findViewById(R.id.remove_route);


			return rootView;
		}

		public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
			Route _route;

			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				int hour = this.getArguments().getInt("hour");
				int minute = this.getArguments().getInt("minute");

				// Create a new instance of TimePickerDialog and return it
				return new TimePickerDialog(getActivity(), this, hour, minute,
						DateFormat.is24HourFormat(getActivity()));
			}

			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

			}
		}
	}

    public static class IdentityViewFragment extends Fragment {
        private User _user;
        private EditText _text;

        public IdentityViewFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.identite_user, container, false);
            _user = MyApplication.getUser();
            _text = (EditText)rootView.findViewById(R.id.NomUser);
            _text.setText(_user.getLastName());
            /*_text = (EditText)rootView.findViewById(R.id.PrenomUser);
            _text.setText(_user.getFirstName());
            _text = (EditText)rootView.findViewById(R.id.Postal);
            _text.setText(Integer.toString(_user.getHome().getPostalCode()));*/

	        _text.addTextChangedListener(new TextWatcher(){
		        public void afterTextChanged(Editable s) {
			        _user.setFirstName("FirstName");
			        JSONObject obj = new JSONObject();
			        try {
				        obj.put("name", _user.getAuthToken().getEmail());
				        obj.put("field", "firstName");
				        obj.put("value", IdentityViewFragment.this._text.getText());
				        Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("modifyAccountField"), _user.getAuthToken(), obj, new Network.NetworkResponseListener() {
							        @Override
							        public void onResponse(JSONObject data, JSONObject headers) {
								        try {
											Log.e("ok", "ok" + data.toString());

								        } catch (Exception e) {
									        Log.e("pas ok", e.toString());
								        }
							        }
						        },
						        new Network.NetworkErrorListener() {
							        @Override
					                public void onError(String reason, VolleyError error) {
								        Log.e("pas ok", reason);
							        }
						        });
			        }
			        catch(Exception e) {}

				}
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        });
	        return rootView;
        }
    }
}
