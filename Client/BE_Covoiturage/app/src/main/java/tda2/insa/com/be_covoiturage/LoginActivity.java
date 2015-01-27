package tda2.insa.com.be_covoiturage;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.*;

import com.android.volley.*;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends ProgressActivity {
	public final static int LOGIN_EMAIL = 0;
	public final static String LOGIN_EMAIL_IDENTIFIER = "LOGIN_EMAIL";
	public final static String AUTH_TOKEN = "AUTH_TOKEN";

	private EditText _emailView;
	private EditText _passwordView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_login);

		// On initialise les variables se référant aux éléments de l'UI
		_emailView = (EditText)findViewById(R.id.email);
		_passwordView = (EditText)findViewById(R.id.password);

		// Association de la validation du formulaire à la fonction attemptLogin()
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

		// Association du clic sur le bouton valider à la fonction attemptLogin()
		Button signInButton = (Button)findViewById(R.id.sign_in_button);
		signInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

		// Association du clic sur le bouton créer compte à la fonction createAccount()
		Button createAccountButton = (Button)findViewById(R.id.create_account_button);
		createAccountButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				createAccount();
			}
		});

		// TODO: supprimer ça pour le déploiement :D
		_emailView.setText("testuser@testmb.net");
		_passwordView.setText("password");
	}

	/**
	 * Appelé quand on appuie sur le bouton de création de compte.
	 * Demande à l'utilisateur de remplir le formulaire, et si la démarche parvient à son terme,
	 * l'utilisateut revient ici et n'a plus qu'à rentrer son nouveau mot de passe et se connecter.
	 */
	private void createAccount() {
		Intent intent = new Intent(this, CreateAccountActivity.class);
		this.startActivityForResult(intent, LOGIN_EMAIL);
	}

	/**
	 * Vérifie si les identifiants fournis par l'utilisateur sont valides (adresse email valide et mot de passe pas vide),
	 * et si c'est le cas envoie la demande d'authentification au serveur. Récupère le cookie de connexion et affiche le profil si réussi,
	 * ou affiche une éventuelle erreur.
	 */
	private void attemptLogin() {
		_emailView.setError(null);
		_passwordView.setError(null);

		final String email = _emailView.getText().toString();
		final String password = _passwordView.getText().toString();

		// Vérification des identifiants
		if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			_emailView.setError(getString(R.string.error_invalid_email));
			_emailView.requestFocus();
		}
		else {
			if (password.isEmpty()) {
				_passwordView.setError(getString(R.string.error_field_required));
				_passwordView.requestFocus();
			} else {
				// On envoie les infos au serveur
				this.showProgress(true);

				MyJSONObject obj = new MyJSONObject();
				obj.put("name", email);
				obj.put("password", password);

				Network.getInstance().sendPostRequest(Network.pathToRequest("login"), obj, new Network.NetworkResponseListener() {
							@Override
							public void onResponse(JSONObject data, JSONObject headers) {
								try {
									if (!data.getString("status").equals("OK")) {
										Log.e("result: ", data.getString("status"));
										LoginActivity.this.wrongCredentials();
										return;
									}

									String cookie = headers.getString("Set-Cookie");
									cookie = cookie.substring(0, cookie.indexOf(';'));

									LoginActivity.this.loginSuccess(new AuthToken(email, cookie));
								} catch (Exception e) {
									LoginActivity.this.loginError(e.getMessage());
								}
							}
						},
						new Network.NetworkErrorListener() {
							@Override
							public void onError(String reason, VolleyError error) {
								LoginActivity.this.loginError(reason + " " + error.toString());
							}
						});
			}
		}
	}

	/**
	 * Affiche le profil de l'utilisateur.
	 */
	private void loginSuccess(AuthToken authToken) {
		Intent intent = new Intent(this, ProfileViewActivity.class);
		intent.putExtra(AUTH_TOKEN, authToken);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		this.showProgress(false);
	}

	/**
	 * Affiche à l'utilisateur que le mot de passe est invalide.
	 */
	private void wrongCredentials() {
		this.showProgress(false);
		_passwordView.setError("Identifiants invalides");
	}

	/**
	 * Une erreur inatendue est survenue, on l'affiche à l'utilisateur.
	 * @param message Le message d'erreur
	 */
	private void loginError(String message) {
		MyApplication.presentError(this, "Une erreur inattendue est survenue :" + message);
		this.showProgress(false);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
			case(LOGIN_EMAIL) : {
				if(resultCode == Activity.RESULT_OK) {
					String text = data.getStringExtra(LOGIN_EMAIL_IDENTIFIER);
					_emailView.setError(null);
					_passwordView.setError(null);
					_passwordView.requestFocus();
					_emailView.setText(text);
					_passwordView.setText("");
				}
				break;
			}
		}
	}
}



