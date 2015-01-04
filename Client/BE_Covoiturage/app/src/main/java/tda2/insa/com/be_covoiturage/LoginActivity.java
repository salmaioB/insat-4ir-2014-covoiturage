package tda2.insa.com.be_covoiturage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import com.android.volley.*;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity/* implements LoaderCallbacks<Cursor>*/ {

	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[]{
			"foo@example.com:hello", "bar@example.com:world"
	};
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	//private UserLoginTask mAuthTask = null;

	// UI references.
	private AutoCompleteTextView _emailView;
	private EditText _passwordView;
	private View _progressView;
	private View _loginFormView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Set up the login form.
		_emailView = (AutoCompleteTextView) findViewById(R.id.email);
		populateAutoComplete();

		_passwordView = (EditText) findViewById(R.id.password);
		_passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
		mEmailSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

		_loginFormView = findViewById(R.id.login_form);
		_progressView = findViewById(R.id.login_progress);

		// TODO: supprimer ça pour le déploiement :D
		_emailView.setText("testuser@testmb.net");
		_passwordView.setText("password");
	}

	private void populateAutoComplete() {
		//getLoaderManager().initLoader(0, null, this);
	}


	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		// Reset errors.
		_emailView.setError(null);
		_passwordView.setError(null);

		// Store values at the time of the login attempt.
		String email = _emailView.getText().toString();
		String password = _passwordView.getText().toString();

		View focusView = null;

		((TextView)findViewById(R.id.error)).setText("");

		// Show a progress spinner
		showProgress(true);

		try {
			JSONObject obj = new JSONObject();
			obj.put("name", email);
			obj.put("password", password);

			Network.getInstance().sendPostRequest("http://" + Network.getHost() + "/android/login", obj, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					try {
						JSONObject data = response.getJSONObject("data");
						if(!data.getString("status").equals("OK")) {
							LoginActivity.this.wrongCredentials();
							return;
						}
						
						String cookie = response.getJSONObject("headers").getString("Set-Cookie");
						cookie = cookie.substring(0, cookie.indexOf(';'));
						Log.w("Got cookie", cookie);
						LoginActivity.this.loginSuccess();
					}
					catch(Exception e) {
						LoginActivity.this.loginFailure(e.getMessage());
					}
				}
			},
			new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					LoginActivity.this.loginFailure(error.toString());
				}
			});
		}
		// Si le JSONObject.put a échoué, que faire à part pleurer ?
		catch(JSONException e) {

		}
	}

	public void loginSuccess() {
		Intent intent = new Intent(this, Settings.class);
		startActivity(intent);
		this.showProgress(false);
	}

	public void wrongCredentials() {
		this.showProgress(false);
		_passwordView.setError("Identifiants invalides");
	}

	public void loginFailure(String message) {
		MyApplication.presentError(this, "Une erreur inattendue est survenue :" + message);
		this.showProgress(false);
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

		_loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		_loginFormView.animate().setDuration(shortAnimTime).alpha(
				show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				_loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			}
		});

		_progressView.setVisibility(show ? View.VISIBLE : View.GONE);
		_progressView.animate().setDuration(shortAnimTime).alpha(
				show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				_progressView.setVisibility(show ? View.VISIBLE : View.GONE);
			}
		});
	}

	/*@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(this,
				// Retrieve data rows for the device user's 'profile' contact.
				Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
						ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

				// Select only email addresses.
				ContactsContract.Contacts.Data.MIMETYPE +
						" = ?", new String[]{ContactsContract.CommonDataKinds.Email
				.CONTENT_ITEM_TYPE},

				// Show primary email addresses first. Note that there won't be
				// a primary email address if the user hasn't specified one.
				ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		List<String> emails = new ArrayList<String>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			emails.add(cursor.getString(ProfileQuery.ADDRESS));
			cursor.moveToNext();
		}

		addEmailsToAutoComplete(emails);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {

	}

	private interface ProfileQuery {
		String[] PROJECTION = {
				ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
		};

		int ADDRESS = 0;
		int IS_PRIMARY = 1;
	}


	private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
		//Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
		ArrayAdapter<String> adapter =
				new ArrayAdapter<String>(LoginActivity.this,
						android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

		mEmailView.setAdapter(adapter);
	}*/

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	/*public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;

		UserLoginTask(String email, String password) {
			mEmail = email;
			mPassword = password;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}

			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(mEmail)) {
					// Account exists, return true if the password matches.
					return pieces[1].equals(mPassword);
				}
			}

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
			} else {
				mPasswordView.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}*/
}



