package tda2.insa.com.be_covoiturage;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

/**
 *
 * Created by remi on 11/01/15.
 */
public class IdentityViewFragment extends Fragment implements DataFragment {
	private User _user;
	private EditText _firstName;
	private EditText _lastName;
	private EditText _city;
	private EditText _zipCode;
	private CheckBox _driver;

	public IdentityViewFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.identite_user, container, false);
		_user = MyApplication.getUser();

		_firstName = (EditText)rootView.findViewById(R.id.first_name);

		_lastName = (EditText)rootView.findViewById(R.id.last_name);

		_city = (EditText)rootView.findViewById(R.id.city);

		_zipCode = (EditText)rootView.findViewById(R.id.zip_code);
		_zipCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					Place p = new Place();
					p.setZipCode(Integer.parseInt(v.getText().toString()));
					v.setText(p.getPrettyZipCode());

					return true;
				}

				return false;
			}
		});

		_driver = (CheckBox)rootView.findViewById(R.id.driver);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		if(_firstName != null) {
			_firstName.setText(_user.getFirstName());
			_lastName.setText(_user.getLastName());
			_city.setText(_user.getHome().getName());
			_zipCode.setText(Integer.toString(_user.getHome().getZipCode()));
			_driver.setChecked(_user.isDriver());
		}
	}

	@Override
	public void onExit() {
		MyJSONObject parent = new MyJSONObject();
		MyJSONObject obj = new MyJSONObject();
		obj.put("name", _user.getAuthToken().getEmail());

		obj.put("firstName", _firstName.getText().toString());
		obj.put("lastName", _lastName.getText().toString());
		obj.put("driver", _driver.isChecked());
		obj.put("city", _city.getText().toString());
		obj.put("zip", _zipCode.getText().toString());

		_user.setFirstName(_firstName.getText().toString());
		_user.setLastName(_lastName.getText().toString());
		_user.setDriver(_driver.isChecked());
		_user.getHome().setName(_city.getText().toString());
		_user.getHome().setZipCode(Integer.parseInt(_zipCode.getText().toString()));

		parent.put("value", obj);
		Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("modifyAccountField"), _user.getAuthToken(), parent, null, null);

		/*InputMethodManager imm = (InputMethodManager)MyApplication.getAppContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(_firstName.getWindowToken(), 0);*/
	}
}
