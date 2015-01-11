package tda2.insa.com.be_covoiturage;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileViewFragment extends Fragment {
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
		// Liste tous les lieux de travail connus
		Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("listWorkplaces"), authToken, new JSONObject(), new Network.NetworkResponseListener() {
					@Override
					public void onResponse(JSONObject data, JSONObject headers) {
						try {
							JSONArray array = data.getJSONArray("value");
							for(int i = 0; i < array.length(); ++i) {
								JSONObject workplace = array.getJSONObject(i);
								// Ajouté automatiquement à la liste
								new Workplace(workplace.getInt("id"), workplace.getString("name"), workplace.getString("address"));
							}
						} catch (Exception e) {
							// Prout
						}

						// Charge le profil de l'utilisateur une fois les lieux de travail disponibles
						try {
							JSONObject obj = new JSONObject();
							obj.put("name", authToken.getEmail());

							// Va chercher les détails du profil
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
				},
				new Network.NetworkErrorListener() {
					@Override
					public void onError(String reason, VolleyError error) {
						// Re-prout
						ProfileViewFragment.this.loadFailed(reason + " " + error.toString());
					}
				});
	}

	private void loadFailed(String message) {
		MyApplication.presentError(this.getActivity(), "Err: " + message, new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				ProfileViewFragment.this.logout();
			}
		});
	}

	public void logout() {
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
