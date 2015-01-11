package tda2.insa.com.be_covoiturage;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * Created by remi on 11/01/15.
 */
public class IdentityViewFragment extends Fragment {
	private User _user;
	private EditText _firstName;
	private EditText _lastName;
	private EditText _city;
	private EditText _zipCode;
	private Button _cancel, _save;
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

		_save = (Button)rootView.findViewById(R.id.save);
		_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyJSONObject obj = new MyJSONObject();
				obj.put("name", _user.getAuthToken().getEmail());

				obj.put("field", "firstName");
				obj.put("value", _firstName.getText().toString());
				Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("modifyAccountField"), _user.getAuthToken(), obj, null, null);

				obj.put("field", "lastName");
				obj.put("value", _lastName.getText().toString());
				Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("modifyAccountField"), _user.getAuthToken(), obj, null, null);

				// TODO: password

				obj.put("field", "driver");
				obj.put("value", _driver.isChecked());
				Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("modifyAccountField"), _user.getAuthToken(), obj, null, null);

				MyJSONObject city = new MyJSONObject();
				city.put("city", _city.getText().toString());
				city.put("zip", _zipCode.getText().toString());
				obj.put("field", "city");
				obj.put("value", city);
				Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("modifyAccountField"), _user.getAuthToken(), obj, null, null);

				InputMethodManager imm = (InputMethodManager)MyApplication.getAppContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(_cancel.getWindowToken(), 0);

				((ProfileViewActivity)IdentityViewFragment.this.getActivity()).switchToProfile();
			}
		});

		_cancel = (Button)rootView.findViewById(R.id.cancel);
		_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager)MyApplication.getAppContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(_cancel.getWindowToken(), 0);

				((ProfileViewActivity)IdentityViewFragment.this.getActivity()).switchToProfile();
			}
		});

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		_firstName.setText(_user.getFirstName());
		_lastName.setText(_user.getLastName());
		_city.setText(_user.getHome().getName());
		_zipCode.setText(Integer.toString(_user.getHome().getZipCode()));
		_driver.setChecked(_user.isDriver());
	}
}
