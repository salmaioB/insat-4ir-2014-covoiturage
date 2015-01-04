package tda2.insa.com.be_covoiturage;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;


public class ProfileViewActivity extends ActionBarActivity {
	User _user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_view);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment())
					.commit();
		}

		this.loadProfile((AuthToken) (this.getIntent().getSerializableExtra(LoginActivity.AUTH_TOKEN)));
	}

	public void loadProfile(final AuthToken authToken) {
		try {
			JSONObject obj = new JSONObject();
			obj.put("name", authToken.getEmail());

			Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("detailsAccount"), authToken, obj, new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							try {
								JSONObject data = response.getJSONObject("data");

								_user = new User(authToken, data);
							} catch (Exception e) {
								MyApplication.presentError(ProfileViewActivity.this, "Exc: " + e.getMessage());
							}
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							MyApplication.presentError(ProfileViewActivity.this, "Err: " + error.toString());
						}
					});
		}
		// Si le JSONObject.put a échoué, que faire à part pleurer ?
		catch (JSONException e) { }

		if(_user == null) {
			this.logout();
		}
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
			this.changeProfile();
			return false;
		}
		else if(id == R.id.action_logout) {
			this.logout();
			return false;
		}

		return super.onOptionsItemSelected(item);
	}

	void changeProfile() {

	}

	void logout() {
		if(_user != null) {
			Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("logout"), _user.getAuthToken(), new JSONObject(), null, null);
		}

		this.finish();

		Intent i = new Intent(this, LoginActivity.class);
		this.startActivity(i);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_profile_view, container, false);
			return rootView;
		}
	}
}
