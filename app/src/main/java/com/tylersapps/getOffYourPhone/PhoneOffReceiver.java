package com.tylersapps.getOffYourPhone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Tyler on 6/13/2017.
 */

public class PhoneOffReceiver extends BroadcastReceiver

    {



        //all this receiver does is record how long you have spend on your phone today, in seconds. Stores that value in sharedPreferences
        //under "SecondsToday".




        @Override
        public void onReceive(Context context, Intent intent) {

            SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = context.getSharedPreferences("my_preferences", MODE_PRIVATE).edit();

            long lastOn = sharedPreferences.getLong("LastOn", 0);
            long interval = System.currentTimeMillis() - lastOn;
            int secondsOnToday = sharedPreferences.getInt("SecondsToday", 0);

            interval = interval/1000; //mili to seconds
            int timeSpent = (int) interval;

            if(DateUtils.isToday(lastOn)){
                //secondsOnToday = 0;
                secondsOnToday = secondsOnToday + timeSpent;
            }else{
                secondsOnToday = 0;
            }

            editor.putInt("SecondsToday", secondsOnToday);
            editor.apply();



             /*


            SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", MODE_PRIVATE);
            secondsOnToday = sharedPreferences.getInt("SecondsToday", 0);

            //Current Time
            long tsLong = System.currentTimeMillis();

            //Last turned on
            long lastOn = MainActivity.findLastTimeStamp() * 1000;

            //inspect this...
            int interval = ((int) (tsLong - lastOn))/1000;

            if(DateUtils.isToday(lastOn)){
                //secondsOnToday = 0;
                secondsOnToday = secondsOnToday + interval;
            }else{
                secondsOnToday = 0;
            }

            SharedPreferences.Editor editor = context.getSharedPreferences("my_preferences", MODE_PRIVATE).edit();
            editor.putInt("SecondsToday", secondsOnToday);
            editor.apply();

            Toast.makeText(context, String.valueOf(secondsOnToday), Toast.LENGTH_SHORT).show();

            */
     }
}
