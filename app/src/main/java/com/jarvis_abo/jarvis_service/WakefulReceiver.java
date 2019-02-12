package com.jarvis_abo.jarvis_service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

/**
 * Created by Mxo on 22-May-16.
 */
public class WakefulReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i  = new Intent(context, JA_service.class);

        context.startService(i);
        Toast.makeText(context, "Wakeful", Toast.LENGTH_SHORT).show();

    }
}
