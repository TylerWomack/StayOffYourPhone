package com.tylersapps.phonechecker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Tyler on 4/20/2017.
 */

public class PhoneOnReceiver extends BroadcastReceiver {

    int timesOn = 0;
    int lastOn = 0;
    String ts = "";

    ArrayList<Long> events = new ArrayList<Long>();




    @Override
    public void onReceive(Context context, Intent intent) {


        SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", MODE_PRIVATE);
        timesOn = sharedPreferences.getInt("Turned_On", 0);

        SharedPreferences.Editor editor = context.getSharedPreferences("my_preferences", MODE_PRIVATE).edit();
        timesOn++;

        editor.putInt("Turned_On", timesOn);
        editor.apply();

        Long tsLong = System.currentTimeMillis()/1000;

        MainActivity.storeArrayList(tsLong, context);

        int events = MainActivity.findEventsInLookbackPeriod(context);
        //MainActivity.clearOldEvents(context);
        int timesAllowed = sharedPreferences.getInt("times_allowed", 100);

        if (events > timesAllowed - 1){

            int timesOver = events - timesAllowed;

            MainActivity.react(context, timesOver, timesAllowed);
        }
    }
}
