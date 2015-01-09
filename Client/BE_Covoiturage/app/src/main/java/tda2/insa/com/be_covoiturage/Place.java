package tda2.insa.com.be_covoiturage;

/**
 * Created by remi on 09/01/15.
 */
public class Place {
	private String _name;
	private int _postalCode;

	Place() {
		_name = "Toulouse";
		_postalCode = 31000;
	}

	public int getPostalCode() {
		return _postalCode;
	}

	public void setPostalCode(int code) {
		_postalCode = code;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}
}
