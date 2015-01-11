package tda2.insa.com.be_covoiturage;

/**
 * Created by remi on 09/01/15.
 */
public class Place {
	private String _name;
	private int _zipCode;

	Place() {
		_name = "Toulouse";
		_zipCode = 31000;

	}

	public int getZipCode() {
		return _zipCode;
	}

	public String getPrettyZipCode() {
		String rep = Integer.toString(this.getZipCode());

		return "00000".substring(rep.length()) + rep;
	}

	public void setZipCode(int code) {
		_zipCode = code;
		if(_zipCode > 99999)
			_zipCode = 31000;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}
}
