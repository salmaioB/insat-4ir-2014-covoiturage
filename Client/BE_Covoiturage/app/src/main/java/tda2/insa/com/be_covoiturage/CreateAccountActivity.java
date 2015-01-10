package tda2.insa.com.be_covoiturage;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;


public class CreateAccountActivity extends ProgressActivity {
	private EditText _emailView;
	private EditText _passwordView;
	private EditText _passwordConfirmView;
	private EditText _firstNameView;
	private EditText _lastNameView;
	private CheckBox _driver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_create_account);

		// On initialise les variables se référant aux éléments de l'UI
		_emailView = (EditText)findViewById(R.id.email);
		_passwordView = (EditText)findViewById(R.id.password);
		_passwordConfirmView = (EditText)findViewById(R.id.confirm_password);
		_firstNameView = (EditText)findViewById(R.id.first_name);
		_lastNameView = (EditText)findViewById(R.id.last_name);

		_driver = (CheckBox)findViewById(R.id.driver);

		Button createAccount = (Button)findViewById(R.id.button_create_account);
		createAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				createAccount();
			}
		});

	}

	/**
	 * Validation bête et méchante des champs du formulaire
	 * @param email Adresse mail à vérifier
	 * @param pass Mot de passe à valider
	 * @param passConfirm Confirmation du mot de passe à valider
	 * @param firstName Prénom à valider
	 * @param lastName Nom à valider
	 * @return true si l'on peut tenter de créer un compte avec ces informations, false sinon
	 */
	private boolean validateForm(String email, String pass, String passConfirm, String firstName, String lastName) {
		if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			_emailView.setError(getString(R.string.error_invalid_email));
			_emailView.requestFocus();

			return false;
		}

		if(pass.length() < 6) {
			_passwordView.setError(getString(R.string.pass_too_short));
			_passwordView.requestFocus();

			return false;
		}

		if(!pass.equals(passConfirm)) {
			_passwordConfirmView.setError(getString(R.string.passwords_differ));
			_passwordConfirmView.requestFocus();

			return false;
		}

		if(firstName.isEmpty()) {
			_firstNameView.setError(getString(R.string.error_field_required));
			_firstNameView.requestFocus();

			return false;
		}

		if(lastName.isEmpty()) {
			_lastNameView.setError(getString(R.string.error_field_required));
			_lastNameView.requestFocus();

			return false;
		}

		return true;
	}

	/**
	 * Tente de créer un compte avec les informations saisies, pour peu qu'elles passent la vérification.
	 * En cas de succès, retourne à l'écran de login.
	 */
	private void createAccount() {
		_emailView.setError(null);
		_passwordView.setError(null);
		_passwordConfirmView.setError(null);
		_firstNameView.setError(null);
		_lastNameView.setError(null);

		String email = _emailView.getText().toString();
		String pass = _passwordView.getText().toString();
		String passConfirm = _passwordConfirmView.getText().toString();
		String firstName = _firstNameView.getText().toString();
		String lastName = _lastNameView.getText().toString();
		boolean driver = _driver.isChecked();

		if(validateForm(email, pass, passConfirm, firstName, lastName)) {
			// On envoie les infos au serveur
			this.showProgress(true);

			try {
				JSONObject obj = new JSONObject();

				obj.put("name", email);
				obj.put("password", pass);
				obj.put("firstName", firstName);
				obj.put("lastName", lastName);
				obj.put("driver", driver);

				Network.getInstance().sendPostRequest(Network.pathToRequest("createAccount"), obj, new Network.NetworkResponseListener() {
							@Override
							public void onResponse(JSONObject data, JSONObject headers) {
								try {
									if (!data.getString("status").equals("OK")) {
										CreateAccountActivity.this.creationFailure();
										return;
									}

									CreateAccountActivity.this.creationSuccess();
								} catch (Exception e) {
									Log.e("Creation failed", e.getMessage());
									CreateAccountActivity.this.creationFailure();
								}
							}
						},
						new Network.NetworkErrorListener() {
							@Override
							public void onError(String reason, VolleyError error) {
								Log.e("Creation failed:", reason + " " + error.toString());
								CreateAccountActivity.this.creationFailure();
							}
						});
			}
			// Si le JSONObject.put a échoué, que faire à part pleurer ?
			catch (JSONException e) { }
		}
	}

	/**
	 * Retourne à l'écran de login en lui transmettant l'adresse email du compte nouvellement créé.
	 */
	private void creationSuccess() {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(LoginActivity.LOGIN_EMAIL_IDENTIFIER, _emailView.getText().toString());
		setResult(Activity.RESULT_OK, resultIntent);

		this.finish();
	}

	/**
	 * Affiche un message d'erreur à l'utilisateur.
	 */
	private void creationFailure() {
		MyApplication.presentError(this, getString(R.string.error_create_account_unknown));
		showProgress(false);
	}
}
