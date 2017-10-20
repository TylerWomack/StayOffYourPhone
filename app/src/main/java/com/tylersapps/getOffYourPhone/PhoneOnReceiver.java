package com.tylersapps.getOffYourPhone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Tyler on 4/20/2017.
 */

public class PhoneOnReceiver extends BroadcastReceiver {



    int timesOn = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", MODE_PRIVATE);

        boolean disabled = sharedPreferences.getBoolean("disabled", false);
        //records the action if the app is disabled, but does not update the user or react.
        if (disabled == true){
            timesOn = sharedPreferences.getInt("Turned_On", 1);
            int timesAllowed = sharedPreferences.getInt("times_allowed", 0);
            long lastOn = sharedPreferences.getLong("LastOn", 0);

            if(DateUtils.isToday(lastOn)){
                timesOn++;
            }else{
                timesOn = 0;
            }


            SharedPreferences.Editor editor = context.getSharedPreferences("my_preferences", MODE_PRIVATE).edit();
            editor.putInt("Turned_On", timesOn);
            editor.putLong("LastOn", System.currentTimeMillis());
            editor.apply();
            return;
        }


        timesOn = sharedPreferences.getInt("Turned_On", 1);
        int timesAllowed = sharedPreferences.getInt("times_allowed", 0);
        long lastOn = sharedPreferences.getLong("LastOn", 0);

        if(DateUtils.isToday(lastOn)){
            timesOn++;
        }else{
            timesOn = 0;
        }


        SharedPreferences.Editor editor = context.getSharedPreferences("my_preferences", MODE_PRIVATE).edit();
        editor.putInt("Turned_On", timesOn);
        editor.putLong("LastOn", System.currentTimeMillis());
        editor.apply();

        MainActivity.react(context, timesOn, timesAllowed);



        /*
         //below is code from 7/13, from final model of timer version


        SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", MODE_PRIVATE);

        int secondsToday = sharedPreferences.getInt("SecondsToday", 0);
        int timeAllowed = sharedPreferences.getInt("period", 0);

        MainActivity.react(context, secondsToday/60, timeAllowed);


        SharedPreferences.Editor editor = context.getSharedPreferences("my_preferences", MODE_PRIVATE).edit();
        editor.putLong("LastOn", System.currentTimeMillis());
        editor.apply();



        /*

        SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", MODE_PRIVATE);
        timesOn = sharedPreferences.getInt("Turned_On", 0);

        SharedPreferences.Editor editor = context.getSharedPreferences("my_preferences", MODE_PRIVATE).edit();
        timesOn++;

        int secondsOnToday = sharedPreferences.getInt("SecondsToday", 0);

        long lastOn = MainActivity.findLastTimeStamp() * 1000;


        if(DateUtils.isToday(lastOn) == false){
            secondsOnToday = 0;
        }

        editor.putInt("SecondsToday", secondsOnToday);
        editor.putInt("Turned_On", timesOn);
        editor.apply();

        Long tsLong = System.currentTimeMillis()/1000;

        MainActivity.storeArrayList(tsLong, context);

        //START OF NEW CODE

        int minutesUsed = MainActivity.getMinutesToday(context);
        int timeAllowed = sharedPreferences.getInt("period", 0);

        //END OF NEW CODE



        //this is the old code that worked. It found the number of events allowed and reacted if it was over. I'm switching to a time based model.

        int events = MainActivity.findEventsInLookbackPeriod(context);
        //MainActivity.clearOldEvents(context);
        int timesAllowed = sharedPreferences.getInt("times_allowed", 100);
            //reacts everytime. Passes in number of times phone has been turned on in lookback period, the times you allowed yourself to lookback (deprecated) minutes used, and the time you allowed yourself to use
            MainActivity.react(context, events, timesAllowed, minutesUsed, timeAllowed);

            */

    }
}
