package com.jarvis_abo.notification.notification_list;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.jarvis_abo.MapViewActivity;
import com.jarvis_abo.R;
import com.jarvis_abo.notification.notification_list.adapter.NotificationListItemAdapter;

public class NotificationsList extends AppCompatActivity {

    private ListView lv_NotificationList;

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(NotificationsList.this, MapViewActivity.class);

        startActivity(intent);

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_list);


        final NotificationListItemAdapter notificationListItemAdapter = new NotificationListItemAdapter(this);
        lv_NotificationList = (ListView) findViewById(R.id.lv_notificationList);
        lv_NotificationList.setAdapter(notificationListItemAdapter);



    }
}
