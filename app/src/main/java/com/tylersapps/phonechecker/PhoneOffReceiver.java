package com.tylersapps.phonechecker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateUtils;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Tyler on 6/13/2017.
 */

public class PhoneOffReceiver extends BroadcastReceiver

    {

        //all this receiver does is record how long you have spend on your phone today, in seconds. Stores that value in sharedPreferences
        //under "SecondsToday".

        int secondsOnToday;

        @Override
        public void onReceive(Context context, Intent intent) {


            SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", MODE_PRIVATE);
            secondsOnToday = sharedPreferences.getInt("SecondsToday", 0);




            //Current Time
            Long tsLong = System.currentTimeMillis()/1000;

            //Last turned on
            Long lastOn = MainActivity.findLastTimeStamp();

            int interval = ((int) (tsLong - lastOn))/1000;

            if(DateUtils.isToday(lastOn) == true){
                secondsOnToday = secondsOnToday + interval;
            }else{
                secondsOnToday = 0;
            }

        SharedPreferences.Editor editor = context.getSharedPreferences("my_preferences", MODE_PRIVATE).edit();
        editor.putInt("SecondsToday", secondsOnToday);
        editor.apply();

    }
    }
