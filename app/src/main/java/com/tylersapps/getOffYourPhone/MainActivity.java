package com.tylersapps.getOffYourPhone;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    static ArrayList<Long> events = new ArrayList<Long>();

    static int period = 0;
    static boolean punishMode = false;
    static boolean updateStatsMode = false;
    boolean isLoaded = false;
    BillingProcessor bp;
    int itemSelected = 0;


   // final PhoneOffReceiver phoneOffReceiver  = new PhoneOffReceiver();
   // final PhoneOnReceiver phoneOnReceiver = new PhoneOnReceiver();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        final SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("my_preferences", MODE_PRIVATE).edit();
        editor.putBoolean("receiverOn", false);
        editor.apply();




        Boolean background = false;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            background = extras.getBoolean("background");
//            setContentView(R.layout.transparent_layout);
        }





        /*
        if (background == true){
            registerReceiver(phoneOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
            registerReceiver(phoneOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
            editor.putBoolean("receiverOn", true);
            editor.apply();
            moveTaskToBack(true);
        }
        */






        setContentView(R.layout.activity_main);

        boolean disabled = sharedPreferences.getBoolean("disabled", false);

        Button button2 = (Button) findViewById(R.id.button2);

        if (disabled == true){
            button2.setText("Unpause");
        }else {
            button2.setText("Pause");
        }

        int isChecked = sharedPreferences.getInt("updateMode", 1);
        if (isChecked == 1) {
            updateStatsMode = true;
        }

        itemSelected = sharedPreferences.getInt("ItemSelected", 0);



        int punish = sharedPreferences.getInt("punishMode", 0);
        if (punish == 1)
            punishMode = true;
        else punishMode = false;


// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.times_checked, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.frequency_options, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(this);

        spinner2.setSelection(itemSelected);


        final CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkBox1);

        if (punishMode == true)
            checkBox1.setChecked(true);
        else checkBox1.setChecked(false);

        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox1.isChecked()) {
                    punishMode = true;
                    editor.putInt("punishMode", 1);
                } else {
                    punishMode = false;
                    editor.putInt("punishMode", 0);
                }
                editor.apply();
            }
        });

        final CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2);

        if (isChecked == 1) {
            checkBox2.setChecked(true);
        } else checkBox2.setChecked(false);


        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox2.isChecked()) {
                    updateStatsMode = true;
                    editor.putInt("updateMode", 1);
                } else {
                    updateStatsMode = false;
                    editor.putInt("updateMode", 0);
                }
                editor.apply();
            }
        });

        if (checkBox2.isChecked())
            updateStatsMode = true;
        else updateStatsMode = false;


        if (checkBox1.isChecked())
            punishMode = true;
        else punishMode = false;

        //// TODO: 6/13/2017 possibly. On first run, check preferences. Save events. Update preferences so it never runs again.

        loadArrayListFile();
    }

    @Override
    protected void onStart() {
        super.onStart();




//crazy idea...
//        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        final SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("my_preferences", MODE_PRIVATE).edit();

        /*
        boolean hasReceiver = sharedPreferences.getBoolean("receiverOn", false);

        if (hasReceiver == false){
            registerReceiver(phoneOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
            registerReceiver(phoneOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
            editor.putBoolean("receiverOn", true);
            editor.apply();
        }
        */

        loadArrayListFile();
    }

    @Override
    protected void onStop() {
        super.onStop();
	    saveArrayListFile(getApplicationContext());
        saveArrayListFile(getApplicationContext());



    }

    @Override
    protected void onPause() {
        saveArrayListFile(getApplicationContext());
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        saveArrayListFile(getApplicationContext());

        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        final SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("my_preferences", MODE_PRIVATE).edit();
        boolean hasReceiver = sharedPreferences.getBoolean("receiverOn", false);


        if (hasReceiver == true){

     //       unregisterReceiver(phoneOffReceiver);
     //       unregisterReceiver(phoneOnReceiver);

//            editor.putBoolean("receiverOn", false);
//            editor.apply();


        }



        super.onDestroy();

    }

    //something copied and pasted for the billing processing library, don't really understand what it does.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void loadArrayListFile() {

        FileInputStream fis;
        try {
            fis = openFileInput("Events");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<Object> returnlist = (ArrayList<Object>) ois.readObject();
            events.clear();
            for (Object i : returnlist) {
                events.add((Long) i);
            }
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveArrayListFile(Context c) {
        try {

            FileOutputStream fos = c.openFileOutput("Events", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(events);
            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        Spinner spinner = (Spinner) parent;

        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("my_preferences", MODE_PRIVATE).edit();

        if (spinner.getId() == R.id.spinner2) {
            parent.getItemAtPosition(pos);

            if (isLoaded == true) {
                if (pos == 0) {
                    editor.putInt("ItemSelected", 0);
                    period = 0;
                    if (punishMode == false)
                        Toast.makeText(getApplicationContext(), "Ok, I'll warn you if check your phone today", Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(getApplicationContext(), "If you check your phone today, you'll regret it...", Toast.LENGTH_LONG).show();
                    }
                }

                if (pos == 1) {
                    period = 5;
                    editor.putInt("ItemSelected", 1);
                    if (punishMode == false)
                        Toast.makeText(getApplicationContext(), "Ok, I'll warn you if you check your phone more than 5 times today", Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(getApplicationContext(), "If you check your phone more than 5 times today, you'll regret it...", Toast.LENGTH_LONG).show();
                    }
                }

                if (pos == 2) {
                    period = 10;
                    editor.putInt("ItemSelected", 2);
                    if (punishMode == false)
                        Toast.makeText(getApplicationContext(), "Ok, I'll warn you if you check your phone more than 10 times today", Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(getApplicationContext(), "If you check your phone more than 10 times today, you'll regret it.", Toast.LENGTH_LONG).show();
                    }
                }

                if (pos == 3) {
                    period = 20;
                    editor.putInt("ItemSelected", 3);
                    if (punishMode == false)
                        Toast.makeText(getApplicationContext(), "Ok, I'll warn you if you check your phone more than 20 times today", Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(getApplicationContext(), "If you check your phone more than 20 times today, you'll regret it...", Toast.LENGTH_LONG).show();
                    }
                }

                if (pos == 4) {
                    period = 30;
                    editor.putInt("ItemSelected", 4);
                    if (punishMode == false)
                        Toast.makeText(getApplicationContext(), "Ok, I'll warn you if you check your phone more than 30 times today.", Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(getApplicationContext(), "If you check your phone more than 30 times today, you'll regret it...", Toast.LENGTH_LONG).show();
                    }
                }

                if (pos == 5) {
                    period = 50;
                    editor.putInt("ItemSelected", 5);
                    if (punishMode == false)
                        Toast.makeText(getApplicationContext(), "Ok, I'll warn you if you check your phone more than 50 times today.", Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(getApplicationContext(), "If you check your phone more than 50 times today, you'll regret it.", Toast.LENGTH_LONG).show();
                    }
                }

                if (pos == 6) {
                    period = 75;
                    editor.putInt("ItemSelected", 6);
                    if (punishMode == false)
                        Toast.makeText(getApplicationContext(), "Ok, I'll warn you if you check your phone more than 75 times today.", Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(getApplicationContext(), "If you check your phone more than 75 times today, you'll regret it.", Toast.LENGTH_LONG).show();
                    }
                }

                if (pos == 7) {
                    period = 100;
                    editor.putInt("ItemSelected", 7);
                    if (punishMode == false)
                        Toast.makeText(getApplicationContext(), "Ok, I'll warn you if you check your phone more than 100 times today.", Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(getApplicationContext(), "If you check your phone more than 100 times today, you'll regret it.", Toast.LENGTH_LONG).show();
                    }
                }

                if (pos == 8) {
                    period = 150;
                    editor.putInt("ItemSelected", 8);
                    if (punishMode == false)
                        Toast.makeText(getApplicationContext(), "Ok, I'll warn you if you check your phone more than 150 times today.", Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(getApplicationContext(), "If you check your phone more than 150 times today, you'll regret it.", Toast.LENGTH_LONG).show();
                    }
                }
                if (pos == 9) {
                    period = 200;
                    editor.putInt("ItemSelected", 9);
                    if (punishMode == false)
                        Toast.makeText(getApplicationContext(), "Ok, I'll warn you if you check your phone more than 200 times today.", Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(getApplicationContext(), "If you check your phone more than 200 times today, you'll regret it.", Toast.LENGTH_LONG).show();
                    }
                }

                if (pos == 10) {
                    if (punishMode == false)
                        Toast.makeText(getApplicationContext(), "Ok, sounds good. Unlimited phone use it is.", Toast.LENGTH_LONG).show();
                    period = 10000;
                    editor.putInt("ItemSelected", 10);
                }
                editor.putInt("times_allowed", period);
                editor.apply();
            }
            isLoaded = true;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback

    }


    public static void storeArrayList(long timeStamp, Context c) {

        events.add(timeStamp);
        saveArrayListFile(c);
    }

    public static long findLastTimeStamp() {
        //// TODO: 6/27/2017 changed -2 to -1
        //quick hack to try to fix it. If you don't have enough events to lookback and size -2 will throw an error, just return the current time
        if (events.size() < 2)
            return System.currentTimeMillis() / 1000;
        else
            return events.get(events.size() - 1);
    }

    public static int getSecondsToday(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", MODE_PRIVATE);
        return sharedPreferences.getInt("SecondsToday", 0);
    }

    public static int getMinutesToday(Context context) {
        return (getSecondsToday(context) / 60);
    }

    public static int getHoursToday(Context context) {
        return (getMinutesToday(context) / 60);
    }

    public static int getMinutesRemainder(Context context) {
        return getMinutesToday(context) % 60;
    }

    public static int findEventsInLookbackPeriod(Context c) {

        SharedPreferences sharedPreferences = c.getSharedPreferences("my_preferences", MODE_PRIVATE);
        int period = sharedPreferences.getInt("period", 60);  //lookback in minutes

        SharedPreferences.Editor editor = c.getSharedPreferences("my_preferences", MODE_PRIVATE).edit();
        int count = 0;
        for (int i = 0; i < events.size(); i++) {
            Long x = events.get(i);
            Long tsLong = System.currentTimeMillis() / 1000; //current time in seconds

            if ((tsLong - (60 * period)) < x) {
                count++;
            }
        }

        editor.putInt("Turned_On", count);
        editor.apply();
        return count;
    }

    public static void launchFlamingSkull(Context c) {

        Intent i = new Intent(c, FlamingSkull.class);
        c.startActivity(i);
    }


    public static void gentlyRemind(Context c) {

        SharedPreferences sharedPreferences = c.getSharedPreferences("my_preferences", MODE_PRIVATE);
        int turnedOn = sharedPreferences.getInt("Turned_On", 0);

        if (turnedOn == 0)
            return;

        if (turnedOn % 5 != 0 && turnedOn != 1){
            return;
        }

        Resources res = c.getResources();
        String[] myString = res.getStringArray(R.array.gentle_reminders);

        Random r = new Random();
        int rand = r.nextInt(myString.length);
        Toast.makeText(c, myString[rand], Toast.LENGTH_LONG).show();
    }

    public static void announceTimesChecked(Context c){

        SharedPreferences sharedPreferences = c.getSharedPreferences("my_preferences", MODE_PRIVATE);
        int turnedOn = sharedPreferences.getInt("Turned_On", 0);

        Toast.makeText(c, "You have checked your phone " + turnedOn + " times today.", Toast.LENGTH_SHORT).show();
    }

    public void disable(View v){
        SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("my_preferences", MODE_PRIVATE);
        final SharedPreferences.Editor editor = v.getContext().getSharedPreferences("my_preferences", MODE_PRIVATE).edit();
        boolean disabled = sharedPreferences.getBoolean("disabled", false);
        if (disabled == false){
            Toast.makeText(v.getContext(), "Ok, the app will be disabled until further notice.", Toast.LENGTH_SHORT).show();
            editor.putBoolean("disabled", true);
            editor.apply();

            Button button2 = (Button) findViewById(R.id.button2);
            button2.setText("Unpause");
        }else {
            editor.putBoolean("disabled", false);
            Toast.makeText(v.getContext(), "The app has been unpaused.", Toast.LENGTH_SHORT).show();
            editor.apply();

            Button button2 = (Button) findViewById(R.id.button2);
            button2.setText("Pause");


        }

    }




    public static void announceTimeSpent(Context c) {
        if (getHoursToday(c) == 1)
            Toast.makeText(c, "You have spent " + getHoursToday(c) + " hour and " + getMinutesRemainder(c) + " minutes on your phone today.", Toast.LENGTH_SHORT).show();
        else if (getHoursToday(c) < 1)
            Toast.makeText(c, "You have spent " + getMinutesToday(c) + " minutes on your phone today.", Toast.LENGTH_SHORT).show();
        else if (getHoursToday(c) > 1)
            Toast.makeText(c, "You have spent " + getHoursToday(c) + " hours and " + getMinutesRemainder(c) + " minutes on your phone today.", Toast.LENGTH_SHORT).show();
    }

    public static void resetTime(View v){
        Context c = v.getContext();
        final SharedPreferences.Editor editor = c.getSharedPreferences("my_preferences", MODE_PRIVATE).edit();
        editor.putInt("SecondsToday", 0);
        editor.apply();
        Toast.makeText(c, "Ok, I'll pretend you haven't spent any time on your phone today...", Toast.LENGTH_LONG).show();
    }

    public static void react(Context c, int timesOn, int timesAllowed) {



        SharedPreferences sharedPreferences = c.getSharedPreferences("my_preferences", MODE_PRIVATE);
        int isChecked = sharedPreferences.getInt("updateMode", 1);
        if (isChecked == 1) {
            updateStatsMode = true;
        } else updateStatsMode = false;


        if (updateStatsMode == true)
            announceTimesChecked(c);
//            announceTimeSpent(c);

        if (timesOn < timesAllowed) {

            return;
        }

        if (timesOn > timesAllowed) {

            if (punishMode == false) {
                gentlyRemind(c);
            } else if (punishMode == true){

                Random random = new Random();
                int r = random.nextInt(2);
                if(r == 1){
                    launchFlamingSkull(c);
                }else {
                    Toast.makeText(c, "You got lucky this time...", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

}
