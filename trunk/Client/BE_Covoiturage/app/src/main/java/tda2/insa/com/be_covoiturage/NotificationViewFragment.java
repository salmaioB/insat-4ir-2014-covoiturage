package tda2.insa.com.be_covoiturage;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by PhuThanh on 1/13/15.
 */
public class NotificationViewFragment extends Fragment implements DataFragment {
    private EditText _email;
    private CheckBox _receiveByMail;
    private CheckBox _receiveNotiInSmp;
    private User _user;
    public NotificationViewFragment() {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_view, container, false);
        _user = MyApplication.getUser();

        _email = (EditText)rootView.findViewById(R.id.email);
        _email.setText(_user.getEmail());

        _receiveByMail = (CheckBox)rootView.findViewById(R.id.receiveByMail);
        _receiveByMail.setChecked(_user.isReceiveByMail());

        _receiveNotiInSmp = (CheckBox)rootView.findViewById(R.id.receiveNotiInSmp);
        _receiveNotiInSmp.setChecked(_user.is_receiveNotiInSmp());

		return rootView;
    }

    @Override
    public void onExit() {
        MyJSONObject obj = new MyJSONObject();

        obj.put("field", "email");
        obj.put("value", _email.getText().toString());
        _user.setEmail(_email.getText().toString());
        Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("modifyAccountField"), _user.getAuthToken(), obj, null, null);

        obj.put("field", "isReceiveByMail");
        obj.put("value", _receiveByMail.isChecked());
        _user.setReceiveByMail(_receiveByMail.isChecked());
        Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("modifyAccountField"), _user.getAuthToken(), obj, null, null);

        obj.put("field", "isReceiveNotiInSmp");
        obj.put("value", _receiveNotiInSmp.isChecked());
        _user.set_receiveNotiInSmp(_receiveNotiInSmp.isChecked());
        Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("modifyAccountField"), _user.getAuthToken(), obj, null, null);
    }
}
