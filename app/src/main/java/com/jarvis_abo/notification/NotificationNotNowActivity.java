package com.jarvis_abo.notification;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jarvis_abo.JA_application;
import com.jarvis_abo.R;
import com.jarvis_abo.entities.Notification;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mxo on 27-May-16.
 */
public class NotificationNotNowActivity extends AppCompatActivity {

    private TextView tv_snoozeTime;
    private Button bt_okNotNow;
    private IconTextView itv_NotNowNotificationType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_not_now);

        Long notificationID = getIntent().getExtras().getLong("notification_id");
        final Notification notification = JA_application.getInstance().getDaoSession().getNotificationDao().load(notificationID);


        tv_snoozeTime = (TextView) findViewById(R.id.tv_snoozeTime);

        itv_NotNowNotificationType = (IconTextView) findViewById(R.id.itv_notNowNotificationType);
        itv_NotNowNotificationType.setText("{" + notification.getType() + "}");

        bt_okNotNow = (Button) findViewById(R.id.bt_okNotNow);

        bt_okNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Long twoHoursAfterNowMilisec = Calendar.getInstance().getTime().getTime() + 2 * 60 * 60 * 1000;

                Date twoHoursAfterNow = new Date(twoHoursAfterNowMilisec);


                notification.setInactiveUntil(twoHoursAfterNow);
                JA_application.getInstance().getDaoSession().getNotificationDao().insertOrReplace(notification);

                finish();


            }
        });


    }
}
