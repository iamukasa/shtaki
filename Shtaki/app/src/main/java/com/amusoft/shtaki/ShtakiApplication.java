package com.amusoft.shtaki;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by irving on 2/14/16.
 */
public class ShtakiApplication extends Application {
    Firebase myFirebaseRef;

    @Override
    public void onCreate(){
       super.onCreate();
       Firebase.setAndroidContext(getApplicationContext());
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
       myFirebaseRef = new Firebase("https://shtaki.firebaseio.com/");





    }
}
