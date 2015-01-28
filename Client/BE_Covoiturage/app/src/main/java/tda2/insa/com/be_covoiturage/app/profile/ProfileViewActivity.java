package tda2.insa.com.be_covoiturage.app.profile;

import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;

import tda2.insa.com.be_covoiturage.R;
import tda2.insa.com.be_covoiturage.app.DataFragment;
import tda2.insa.com.be_covoiturage.app.profile.route.*;
import tda2.insa.com.be_covoiturage.app.profile.search.SearchMatchesFragment;


public class ProfileViewActivity extends ActionBarActivity {
	ProfileViewFragment _profileViewFragment;
	RouteViewFragment _routeViewFragment;
	IdentityViewFragment _identityViewFragment;
    NotificationViewFragment _notificationViewFragment;
	SearchMatchesFragment _searchMatchesFragment;
	DataFragment _currentFragment;
	private static Route _lastRoute;

	public static void setRoute(Route r) {
		_lastRoute = r;
	}

	public static Route getRoute() {
		return _lastRoute;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_view);
		if (savedInstanceState == null) {
			_profileViewFragment = new ProfileViewFragment();
			_routeViewFragment = new RouteViewFragment();
			_identityViewFragment = new IdentityViewFragment();
			_notificationViewFragment = new NotificationViewFragment();
			_searchMatchesFragment = new SearchMatchesFragment();
			this.switchToProfile();
		}
	}

	private void switchToFragment(DataFragment fragment) {
		if(_currentFragment != null) {
			_currentFragment.onExit();
		}

		FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
		transaction.replace(R.id.container, (Fragment)fragment);

		transaction.commit();

		setTitle(fragment.fragmentTitle());

		_currentFragment = fragment;
	}

	@Override
	public void onBackPressed() {
		if(_currentFragment != _profileViewFragment) {
			if(_currentFragment == _searchMatchesFragment) {
				this.switchToRoute(_lastRoute);
			}
			else {
				this.switchToProfile();
			}
		}
		else {
			this.moveTaskToBack(true);
		}
	}

	public void switchToIdentity() {
		this.switchToFragment(_identityViewFragment);
	}

    public void switchToNotification() { this.switchToFragment(_notificationViewFragment);}

    public void switchToRoute(Route r) {
		Bundle args = new Bundle();
		args.putString(RouteViewFragment.WEEK_DAY, r.getWeekday().toString());
		_routeViewFragment.setArguments(args);
		this.switchToFragment(_routeViewFragment);
	}

	public void switchToProfile() {
		this.switchToFragment(_profileViewFragment);
	}

	public void switchToSearchMatches(JSONArray arr, boolean direction) {
		Bundle args = new Bundle();
		args.putString(SearchMatchesFragment.MATCHES, arr.toString());
		args.putBoolean(SearchMatchesFragment.DIRECTION, direction);
		_searchMatchesFragment.setArguments(args);
		this.switchToFragment(_searchMatchesFragment);
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

		if(id == R.id.action_logout) {
			_profileViewFragment.logout();
			return false;
		}

		return super.onOptionsItemSelected(item);
	}
}
