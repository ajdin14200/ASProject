package com.example.project_master;

import android.app.Application;

import com.google.android.libraries.places.api.Places;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        String apiKey = getString(R.string.api_key);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

    }
}
