package edu.augustana.osleventsandroid;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

public class OSLEventsRemoteConfig {

    public static final String KEY_UPDATE_REQUIRED = "force_update_required_android";
    public static final String KEY_CURRENT_VERSION = "force_update_version_android";
    public static final String KEY_UPDATE_URL = "force_update_store_url_android";

    public interface RemoteConfigChangeListener {
        void updateRemoteConfig();
    }

    public static void initialize(final RemoteConfigChangeListener updateActivity) {

        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder()
                .build());

        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(KEY_UPDATE_REQUIRED, true);
        remoteConfigDefaults.put(KEY_CURRENT_VERSION, "1.0.0");
        remoteConfigDefaults.put(KEY_UPDATE_URL,
                "https://play.google.com/store/apps");

        mFirebaseRemoteConfig.setDefaults(remoteConfigDefaults);
        mFirebaseRemoteConfig.fetch(3600) // fetch every hour
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();
                        }
                        updateActivity.updateRemoteConfig();
                    }
                });
    }

}
