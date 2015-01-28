package tda2.insa.com.be_covoiturage.app.profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import tda2.insa.com.be_covoiturage.R;
import tda2.insa.com.be_covoiturage.app.DataFragment;
import tda2.insa.com.be_covoiturage.app.LoginActivity;
import tda2.insa.com.be_covoiturage.app.MyApplication;
import tda2.insa.com.be_covoiturage.app.User;
import tda2.insa.com.be_covoiturage.app.Workplace;
import tda2.insa.com.be_covoiturage.app.profile.route.Route;
import tda2.insa.com.be_covoiturage.app.profile.route.RouteAdapter;
import tda2.insa.com.be_covoiturage.network.AuthToken;
import tda2.insa.com.be_covoiturage.network.MyJSONObject;
import tda2.insa.com.be_covoiturage.network.Network;

public class ProfileViewFragment extends Fragment implements DataFragment {
	private User _user;

	private boolean _canUseMaps;
	private ListView _infosList;
	private ListView _routesList;
	private ArrayAdapter<String> _infos;
	private RouteAdapter _routes;
	private Activity _activity;

	public ProfileViewFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_profile_view, container, false);

		_activity = this.getActivity();

		_infosList = (ListView)rootView.findViewById(R.id.profile_infos);
		_infosList.setClickable(true);

		_routesList = (ListView)rootView.findViewById(R.id.profile_routes);

		String[] values = new String[] {"Identité", "Notifications", "Trajet personnalisé…"};

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
				else if(position == 2) {
					ProfileViewFragment.this.customRoute();
				}
			}
		});

		if(MyApplication.getUser() == null) {
			this.loadProfile((AuthToken) (this.getActivity().getIntent().getSerializableExtra(LoginActivity.AUTH_TOKEN)));
		}
		else {
			Handler handler=new Handler();
			Runnable r=new Runnable() {
				public void run() {
					ProfileViewFragment.this.onProfileLoaded();
				}
			};
			handler.postDelayed(r, 100);
		}

		return rootView;
	}

	public String fragmentTitle() {
		return "Profil";
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
	}

	private void loadProfile(final AuthToken authToken) {
		// Liste tous les lieux de travail connus, charge le profil si réussi
		Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("listWorkplaces"), authToken, new JSONObject(), new Network.NetworkResponseListener() {
					@Override
					public void onResponse(JSONObject data, JSONObject headers) {
						try {
							Workplace.getWorkplaces().clear();

							JSONArray array = data.getJSONArray("value");
							if(array.length() == 0) {
								throw new IllegalArgumentException(getActivity().getString(R.string.error_no_worplace));
							}
							for (int i = 0; i < array.length(); ++i) {
								JSONObject workplace = array.getJSONObject(i);
								// Ajouté automatiquement à la liste
								new Workplace(workplace.getInt("id"), workplace.getString("name"), workplace.getString("address"));
							}

							// Charge le profil de l'utilisateur une fois les lieux de travail disponibles
							MyJSONObject obj = new MyJSONObject();
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
						} catch (Exception e) {
							ProfileViewFragment.this.loadFailed(e.getMessage());
						}
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

		MyApplication.setUser(null);

		_activity.finish();

		Intent i = new Intent(_activity, LoginActivity.class);
		_activity.startActivity(i);
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
	}

	public void editIdentity() {
		((ProfileViewActivity)this.getActivity()).switchToIdentity();
	}

	public void editNotifications() {
        ((ProfileViewActivity)this.getActivity()).switchToNotification();
	}

	public void editRoute(int index) {
		((ProfileViewActivity)this.getActivity()).switchToRoute(_user.getRoute(Route.Weekday.values()[index]));
	}

	public void customRoute() {
		((ProfileViewActivity)this.getActivity()).switchToNotification();
	}

	@Override
	public void onExit() {

	}
}
