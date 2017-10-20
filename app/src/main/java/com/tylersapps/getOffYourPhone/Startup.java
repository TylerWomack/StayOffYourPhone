package com.tylersapps.getOffYourPhone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Tyler on 4/26/2017.
 */

public class Startup extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            Intent i = new Intent(context, MainActivity.class);
           // context.startService(i);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("background", true);
            context.startActivity(i);

        }
    }
}