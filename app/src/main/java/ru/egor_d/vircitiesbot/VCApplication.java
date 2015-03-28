package ru.egor_d.vircitiesbot;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Egor Danilin on 29.03.2015.
 */
public class VCApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "fOVHMrCpIhG8o4qd9JXXFvka5BgCJbMi6Q5KZRa0", "plsRCm4OoCiBzVV7I2Wq5sxgTF9uYo2M3oaGFy2w");
    }
}
