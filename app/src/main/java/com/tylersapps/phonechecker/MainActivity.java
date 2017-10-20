package com.tylersapps.phonechecker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    static ArrayList<Long> events = new ArrayList<Long>();

    static int timesAllowed = 0;
    static int period = 0;
    static boolean hurlAbuse = false;
    static boolean beNice = false;
    static boolean punishMode = false;
    int secondsOnToday;



    BroadcastReceiver phoneOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", MODE_PRIVATE);
            secondsOnToday = sharedPreferences.getInt("SecondsToday", 0);




            //Current Time
            long tsLong = System.currentTimeMillis();

            //Last turned on
            long lastOn = MainActivity.findLastTimeStamp() * 1000;

            int interval = ((int) (tsLong - lastOn))/1000;

            if(DateUtils.isToday(lastOn)){
                secondsOnToday = secondsOnToday + interval;
            }else{
                secondsOnToday = 0;
            }

            SharedPreferences.Editor editor = context.getSharedPreferences("my_preferences", MODE_PRIVATE).edit();
            editor.putInt("SecondsToday", secondsOnToday);
            editor.apply();

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //a test. I need to register a receiver to detect if screen is turned off.
        registerReceiver(phoneOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));


        Spinner spinner = (Spinner) findViewById(R.id.spinner);

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.times_checked, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.frequency_options, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner2.setAdapter(adapter2);


        spinner.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);



        final CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkBox1);

        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox1.isChecked()){
                    punishMode = true;
                }else punishMode = false;

            }
        });


        if(checkBox1.isChecked())
            punishMode = true;
        else punishMode = false;

        //// TODO: 6/13/2017 possibly. On first run, check preferences. Save events. Update preferences so it never runs again.

        loadArrayListFile();
    }

    @Override
    protected void onStart(){
        super.onStart();
        loadArrayListFile();
    }

    @Override protected void onStop(){
        saveArrayListFile(getApplicationContext());
        super.onStop();

    }

    @Override protected void onPause(){
        saveArrayListFile(getApplicationContext());
        super.onPause();

    }

    @Override protected void onDestroy(){
        saveArrayListFile(getApplicationContext());
        super.onDestroy();

    }



    public void loadArrayListFile(){

        FileInputStream fis;
        try {
            fis = openFileInput("Events");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<Object> returnlist = (ArrayList<Object>) ois.readObject();
            events.clear();
            for (Object i : returnlist){
                events.add((Long) i);
            }


            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void saveArrayListFile(Context c){
        //Toast.makeText(getApplicationContext(), "testing" , Toast.LENGTH_LONG).show();
        try{

            FileOutputStream fos = c.openFileOutput("Events", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(events);
            oos.close();

        }catch (IOException e){
            e.printStackTrace();
        }


    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {



        Spinner spinner = (Spinner) parent;
        if (spinner.getId() == R.id.spinner){
            String times = (String) parent.getItemAtPosition(pos);
            timesAllowed = Integer.valueOf(times);
            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("my_preferences", MODE_PRIVATE).edit();
            editor.putInt("times_allowed", timesAllowed);
            editor.apply();


        }else if(spinner.getId() == R.id.spinner2){
            parent.getItemAtPosition(pos);

            if (pos == 0){
                period = 30;

            }

            if (pos == 1){
                period = 60;

            }

            if (pos == 2){
                period = 120;

            }


            if (pos == 3){
                period = 300;

            }


            if (pos == 4){
                period = 1440;

            }

            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("my_preferences", MODE_PRIVATE).edit();
            editor.putInt("period", period);
            editor.apply();

        }

        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback

    }



    public static void storeArrayList(long timeStamp, Context c){

        events.add(timeStamp);
        saveArrayListFile(c);
    }

    public static long findLastTimeStamp(){
        //// TODO: 6/13/2017 this threw an index out of bounds error. Not good. Try to fix it somehow.
        //Caused by: java.lang.ArrayIndexOutOfBoundsException: length=10; index=-1

        //quick hack to try to fix it. If you don't have enough events to lookback and size -2 will throw an error, just return the current time
        if (events.size() < 2)
            return System.currentTimeMillis()/1000;
        else
        return events.get(events.size() - 2);
    }

    public static int getSecondsToday(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", MODE_PRIVATE);
        return sharedPreferences.getInt("SecondsToday", 0);
    }

    public static int getMinutesToday(Context context){
        return (getSecondsToday(context)/60);
    }

    public static int getHoursToday(Context context){
        return (getMinutesToday(context)/60);
    }

    public static int getMinutesRemainder(Context context){
        return getMinutesToday(context)%60;
    }


    public static void clearOldEvents(Context c){
        for (int i = 0; i < events.size(); i++){
            Long x = events.get(i);
            Long tsLong = System.currentTimeMillis()/1000; //current time in seconds

            if ((tsLong - ((60 * 60) * 168) < x )){
                events.remove(i);
                i++;
            }
        }
    }

    public static int findEventsInLookbackPeriod(Context c){

        SharedPreferences sharedPreferences = c.getSharedPreferences("my_preferences", MODE_PRIVATE);
        int period = sharedPreferences.getInt("period", 60);  //lookback in minutes
        int timesAllowed = sharedPreferences.getInt("times_allowed", 100);

        SharedPreferences.Editor editor = c.getSharedPreferences("my_preferences", MODE_PRIVATE).edit();


        int count = 0;
        for (int i = 0; i < events.size(); i++){
            Long x = events.get(i);
            Long tsLong = System.currentTimeMillis()/1000; //current time in seconds

            if ((tsLong - (60 * period)) < x ){
                count++;
            }
        }

        editor.putInt("Turned_On", count);
        editor.apply();
        return count;
    }

    public static void vibrate(Context c){
        Vibrator v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);

    }


    public static void launchFlamingSkull(Context c){

        Intent i = new Intent(c, FlamingSkull.class);
        c.startActivity(i);
    }

    //make camera flash like a strobe light. Pop open a screen that is a flaming skull or something. Make the phone vibrate
    //incessantly. Pop open text that utterly berates them. Basically, just go way over the top and fuck up their phone, it'll
    //be hilarious. In fact, add a level of variety to it, so it isn't the same skull everytime. Introduce like, 10 or 20 pictures.

    /*
    public static void turnOnFlashlight(Context c){
        if (c.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
            Camera camera;
            camera = Camera.open();
        }


    }
    */
    public static void hurlAbuse(Context c){
        Resources res  = c.getResources();
        String[] myString = res.getStringArray(R.array.angry_phrases);

        Random r = new Random();
        int rand = r.nextInt(myString.length);

        Toast.makeText(c, myString[rand], Toast.LENGTH_LONG).show();

    }

    public static void gentlyRemind(Context c){
        Resources res  = c.getResources();
        String[] myString = res.getStringArray(R.array.gentle_reminders);

        Random r = new Random();
        int rand = r.nextInt(myString.length);

        Toast.makeText(c, myString[rand], Toast.LENGTH_LONG).show();
    }



    public static void defaultReminder(Context c){

        SharedPreferences sharedPreferences = c.getSharedPreferences("my_preferences", MODE_PRIVATE);
        int timesOn = sharedPreferences.getInt("Turned_On", 0);
        int periodMins = sharedPreferences.getInt("period", 100);
        int period = periodMins/60;
        boolean inMinutes = false;

        if(period < 1 ) {
            period = periodMins;
            inMinutes = true;
        }

        String allowed = Integer.toString(timesAllowed/60);

        if (inMinutes == false && period == 1)
            Toast.makeText(c, "You've turned your phone on " + timesOn + " times in the last hour", Toast.LENGTH_LONG).show();

        if(inMinutes == false && period > 1)
            Toast.makeText(c, "You've turned your phone on " + timesOn + " times in the last " + period + " hours", Toast.LENGTH_LONG).show();


        if (inMinutes == true)
            Toast.makeText(c, "You've turned your phone on " + timesOn + " times in the last " + periodMins + " minutes", Toast.LENGTH_LONG).show();
    }

    public static void announceTimeSpent(Context c){
        if (getHoursToday(c) > 0)
            Toast.makeText(c, "You have spent " + getHoursToday(c) + " hours and " + getMinutesToday(c) + " minutes on your phone today.", Toast.LENGTH_LONG).show();
        else if (getHoursToday(c) < 1)
            Toast.makeText(c, "You have spent " + getMinutesToday(c) + " minutes on your phone today.", Toast.LENGTH_LONG).show();
    }

    public static void react(Context c, int timesOver, int timesAllowed){

        int secondLevel = (int) (0.5 * timesAllowed);

        int thirdLevel = timesAllowed;

        if (timesOver < secondLevel){
            gentlyRemind(c);
            announceTimeSpent(c);
        }

        if (punishMode == true) {

            if (timesOver >= secondLevel && timesOver < thirdLevel) {
                hurlAbuse(c);
                announceTimeSpent(c);

                //                Toast.makeText(c, "If you check your phone " + Integer.toString((int) (timesAllowed - timesOver)) + " times, you're going to regret it.", Toast.LENGTH_LONG);
            }
        }else {
            if (timesOver >= secondLevel) {
                hurlAbuse(c);
                announceTimeSpent(c);
            }
        }

        if (timesOver >= thirdLevel && punishMode == true){
            launchFlamingSkull(c);
        }
    }
}
