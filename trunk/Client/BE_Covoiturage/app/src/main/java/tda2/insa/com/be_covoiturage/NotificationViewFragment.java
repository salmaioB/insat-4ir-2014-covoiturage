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
public class NotificationViewFragment extends Fragment {
    private EditText _email;
    private CheckBox _receiveByMail;
    private User _user;

    public NotificationViewFragment() {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_view, container, false);
        _user = MyApplication.getUser();
        _email = (EditText) rootView.findViewById(R.id.addressMail);
        _email.setText(_user.getEmail());
        _receiveByMail = (CheckBox) rootView.findViewById(R.id.email);
        _receiveByMail.setChecked(_user.isReceiveByMail());

        return rootView;
    }

}
