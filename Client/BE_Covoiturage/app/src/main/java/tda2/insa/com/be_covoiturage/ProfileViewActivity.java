package tda2.insa.com.be_covoiturage;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;


public class ProfileViewActivity extends ActionBarActivity {
	ProfileViewFragment _profileViewFragment;
	RouteViewFragment _routeViewFragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_view);
		if (savedInstanceState == null) {
			_profileViewFragment = new ProfileViewFragment();
			_routeViewFragment = new RouteViewFragment();
			this.switchToProfile();
		}
	}

	private void switchToFragment(Fragment fragment) {
		this.getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment)
				.commit();

	}

	public void switchToIdentity() {

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

				Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("detailsAccount"), authToken, obj, new Response.Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {
								try {
									JSONObject data = response.getJSONObject("data");

									_user = new User(authToken, data);
									MyApplication.setUser(_user);
									ProfileViewFragment.this.onProfileLoaded();
								} catch (Exception e) {
									ProfileViewFragment.this.loadFailed(e.getMessage());
								}
							}
						},
						new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								ProfileViewFragment.this.loadFailed(error.toString());
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
			Log.e("edit", "Identity");
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

		public RouteViewFragment() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.route_view, container, false);
			_user = MyApplication.getUser();

			return rootView;
		}
	}
}
