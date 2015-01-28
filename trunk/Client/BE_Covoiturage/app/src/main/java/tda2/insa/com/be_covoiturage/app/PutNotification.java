package tda2.insa.com.be_covoiturage.app;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import tda2.insa.com.be_covoiturage.R;

/**
 * Created by PhuThanh on 1/25/15.
 */
public class PutNotification extends Activity {
    private NotificationManager _notificationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.noti_test);

        _notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        final Button NotificationButton = (Button) findViewById(R.id.Notification);

        NotificationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Traitement de la notification
                Notification notification = new Notification(R.drawable.common_signin_btn_icon_dark,
                        "Mon appli",
                        System.currentTimeMillis());

                Intent intentNotification = new Intent(Intent.ACTION_VIEW);

                PendingIntent pendingIntent = PendingIntent.getActivity(PutNotification.this, 0, intentNotification, 0);

                notification.setLatestEventInfo(PutNotification.this, "Ma notification", "Passer un appel", pendingIntent);

                _notificationManager.notify(1, notification);
            }


        });

    }
}
