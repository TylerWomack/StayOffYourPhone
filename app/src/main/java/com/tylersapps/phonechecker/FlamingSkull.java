package com.tylersapps.phonechecker;


import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.READ_CONTACTS;
import static java.lang.Thread.sleep;

public class FlamingSkull extends AppCompatActivity {

    boolean on = false;
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> phoneNumbers = new ArrayList<String>();
    int numberOfPunishments = 4;

    //don't change this: this is the request code for Read_Contacts
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flaming_skull);
        changeWallpaper();
        react();
    }

    public void react(){

        Random r = new Random();
        int punishmentToEnact = r.nextInt(numberOfPunishments);

        switch (punishmentToEnact){
            case 0:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    phoneCallPunishment();
                }
                break;
            case 1:
                //params: vibration in seconds
                vibrationPunishment(120);
                break;
            case 2:
                Toast.makeText(this, "Your wallpaper has been changed as punishment. Don't press your luck, and get off your phone!", Toast.LENGTH_LONG).show();
                changeWallpaper();
                break;
            case 3:
                Toast.makeText(this, "You asked for this. Enjoy the lightshow.", Toast.LENGTH_LONG).show();
                //params: times to flash, miliseconds between blinks
                flickerLights(15, 1000);
                break;
        }
    }



    public void phoneCallPunishment(){
        if (askForPermission() == 1){
            getContact();
            callMother();
        }
    }

    public void vibrationPunishment(int seconds){
        final int mili = (seconds * 1000)/5;
        final Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        for (int i = 0; i < 5; i++){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    v.vibrate(mili);
                }
            }, 50);

        }
    }


    public void flickerLights(int times, long intr) {

        for (int i = 0; i < times * 2; i++) {
            toggleFlashLight();
            try {
                sleep(intr);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void changeWallpaper() {



        WallpaperManager wm = WallpaperManager.getInstance(getApplicationContext());
        try {
            //number of images to choose from
            int size = 3;
            Random r = new Random();
            int choice = r.nextInt(size);

            switch (choice){
                case 0:
                    wm.setResource(R.raw.getoffphonenote);
                    break;
                case 1:
                    wm.setResource(R.raw.phoneaddiction2);
                    break;
                case 2:
                    wm.setResource(R.raw.getoffphone3);
                    break;

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("NewApi")
    public void turnOn() {

        CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String cameraId = null;
        try {
            cameraId = camManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        try {
            getApplicationContext().getApplicationInfo();
            if (Build.VERSION.SDK_INT > 23) {

                camManager.setTorchMode(cameraId, true);
                on = true;

            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public void turnOff() {

        CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String cameraId = null;

        try {
            cameraId = camManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        try {
            getApplicationContext().getApplicationInfo();
            if (Build.VERSION.SDK_INT > 23) {

                camManager.setTorchMode(cameraId, false);
                on = false;
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void toggleFlashLight() {
        if (!on) { // Off, turn it on
            turnOn();
        } else { // On, turn it off
            turnOff();
        }
    }

    public int askForPermission() {
        if (ContextCompat.checkSelfPermission(this,
                READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{READ_CONTACTS},
                        999);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            if(ContextCompat.checkSelfPermission(this,
                    READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED)
                return 1;
        }
        return 0;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getContact();
                    callMother();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    public void callMother() {

        boolean hasMom = false;
        boolean hasDad = false;
        int index = -1;
        String numToCall = "";

        if (names.indexOf("Mom") != -1){
            hasMom = true;
            index = names.indexOf("Mom");
            numToCall = phoneNumbers.get(index);
        }else {
            hasMom = false;
            if (names.indexOf("Dad") != -1){
                index = names.indexOf("Dad");
                numToCall = phoneNumbers.get(index);
                hasDad = true;
            }else{
                hasDad = false;
                Random r = new Random();
                index = r.nextInt(phoneNumbers.size());
            }
        }

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    //Query phone here.  Covered next
                }
            }
        }

        String posted_by = numToCall;
        String uri = "tel:" + posted_by.trim();

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);

        if (hasMom == true){
            Toast.makeText(this, "Call your Mother and apologize!", Toast.LENGTH_LONG).show();
        }else if(hasDad == true){
            Toast.makeText(this, "Call your Dad and apologize!", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Call this person and apologize!", Toast.LENGTH_LONG).show();
        }
    }

    public void getContact() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //Toast.makeText(this, "Name: " + name
                        //        + ", Phone No: " + phoneNo, Toast.LENGTH_SHORT).show();

                        names.add(name);
                        phoneNumbers.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
    }
}
