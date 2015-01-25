package tda2.insa.com.be_covoiturage;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by PhuThanh on 1/13/15.
 */
public class NotificationViewFragment extends Fragment implements DataFragment {
    private EditText _email;
    private CheckBox _notifyByMail;
    private CheckBox _notifyByPush;
    private User _user;
    public NotificationViewFragment() {}
    private Button _testButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_view, container, false);
        _user = MyApplication.getUser();

        _email = (EditText)rootView.findViewById(R.id.email);
        _email.setText(_user.getEmail());

        _notifyByMail = (CheckBox)rootView.findViewById(R.id.notifyByMail);
	    _notifyByMail.setChecked(_user.getNotifyByMail());

	    _notifyByPush = (CheckBox)rootView.findViewById(R.id.notifyByPush);
	    _notifyByPush.setChecked(_user.getNotifyByPush());

        _testButton = (Button) rootView.findViewById(R.id.Notification);

        _testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Traitement de la notification
                new PutNotification();
            }
        });
		return rootView;
    }

    @Override
    public void onExit() {
        MyJSONObject parent = new MyJSONObject();
	    MyJSONObject obj = new MyJSONObject();
	    parent.put("name", _user.getAuthToken().getEmail());

	    obj.put("notifyAddress", _email.getText().toString());
	    obj.put("notifyByMail", _notifyByMail.isChecked());
	    obj.put("notifyByPush", _notifyByPush.isChecked());

        _user.setNotifyByMail(_notifyByMail.isChecked());
	    _user.setNotifyByPush(_notifyByPush.isChecked());
	    _user.setEmail(_email.getText().toString());

	    parent.put("value", obj);
	    Network.getInstance().sendAuthenticatedPostRequest(Network.pathToRequest("modifyNotification"), _user.getAuthToken(), parent, null, null);
    }

}
