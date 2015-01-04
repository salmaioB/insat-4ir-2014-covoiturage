package tda2.insa.com.be_covoiturage;

import java.io.Serializable;

/**
 * Created by remi on 04/01/15.
 */
public class AuthToken implements Serializable {
	private String _token;
	private String _email;

	public AuthToken(String email, String token) {
		_email = email;
		_token = token;
	}

	public String getEmail() {
		return _email;
	}
	public String getToken() {
		return _token;
	}
}
