package tda2.insa.com.be_covoiturage;

import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class ProfileViewActivity extends ActionBarActivity {
	ProfileViewFragment _profileViewFragment;
	RouteViewFragment _routeViewFragment;
	IdentityViewFragment _identityViewFragment;
    NotificationViewFragment _notificationViewFragment;
	Fragment _currentFragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_view);
		if (savedInstanceState == null) {
			_profileViewFragment = new ProfileViewFragment();
			_routeViewFragment = new RouteViewFragment();
			_identityViewFragment = new IdentityViewFragment();
			_notificationViewFragment = new NotificationViewFragment();
			this.switchToProfile();
		}
	}

	private void switchToFragment(Fragment fragment) {
		FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
		transaction.replace(R.id.container, fragment);

		transaction.commit();

		_currentFragment = fragment;
	}

	@Override
	public void onBackPressed() {
		if(_currentFragment != _profileViewFragment) {
			this.switchToProfile();
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
